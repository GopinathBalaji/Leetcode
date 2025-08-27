/*
// Definition for a QuadTree node.
class Node {
    public boolean val;
    public boolean isLeaf;
    public Node topLeft;
    public Node topRight;
    public Node bottomLeft;
    public Node bottomRight;

    
    public Node() {
        this.val = false;
        this.isLeaf = false;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }
    
    public Node(boolean val, boolean isLeaf) {
        this.val = val;
        this.isLeaf = isLeaf;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }
    
    public Node(boolean val, boolean isLeaf, Node topLeft, Node topRight, Node bottomLeft, Node bottomRight) {
        this.val = val;
        this.isLeaf = isLeaf;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }
}
*/

// Divide and conquer by recursion approach
/*
### 1) Think in terms of a helper on a sub-square

* Pass `(row, col, size)` describing the **top-left** of the current square and its side length.
* The helper returns a **node** for that square.

### 2) Decide “leaf or split” with a **uniformity test**

* You need a quick way to tell if **all cells** in `[row..row+size), [col..col+size)` are the same.
* Naive: scan the square and **early-exit** on the first mismatch.
* (Optional optimization) If grid is large, consider a **2D prefix-sum** so uniformity is O(1): sum==0 ⇒ all zeros, sum==size\*size ⇒ all ones.

### 3) Base cases

* If `size == 1`, it’s trivially a **leaf** with that cell’s value.
* If the uniformity test passes, also return a **leaf** (don’t split further).

### 4) When not uniform, split in **four quadrants** (careful with indices)

Let `half = size / 2` and build children in this fixed order (LeetCode expects it):

1. **topLeft**:     `(row,        col,        half)`
2. **topRight**:    `(row,        col + half, half)`
3. **bottomLeft**:  `(row + half, col,        half)`
4. **bottomRight**: `(row + half, col + half, half)`

### 5) Node fields checklist (so you don’t mix them up)

* `isLeaf`: `true` only when uniform; otherwise `false`.
* `val`: for leaves, set to the cell’s boolean value; for internal nodes, it’s ignored by the judge but many implementations set it arbitrarily (often `true`).
* `topLeft`, `topRight`, `bottomLeft`, `bottomRight`: assign children only for non-leaf nodes.

### 6) Common pitfalls

* **Off-by-one** in indexing when slicing quadrants.
* Forgetting to **stop** (return) on uniform regions (causes needless recursion).
* Creating children in the **wrong order** (AC fails even if the tree is logically correct).
* Re-scanning the same region multiple times—if you do naive scans, at least **early-exit** on mismatch.

### 7) Complexity intuition (to sanity-check)

* Worst-case (checkerboard), you’ll touch many cells at many levels: about **O(n² log n)** with naive checks.
* With prefix sums, each level’s uniformity test is O(1), so overall about **O(n²)** to build.

### 8) Optional micro-optimizations

* Cache `half` to avoid recomputing.
* If you wrote a uniformity scan, compare the first cell and **break** on mismatch immediately.

NOTE: "outer:" is a Java lable here, nothing special
*/
class Solution {
    public Node construct(int[][] grid) {
        return recurse(0, 0, grid, grid.length);
    }

    private Node recurse(int row, int col, int[][] grid, int n) {
        // Base case 1: single cell
        if (n == 1) {
            return new Node(grid[row][col] == 1, true, null, null, null, null);
        }

        // Uniformity check: if all cells are the same, make a leaf
        int first = grid[row][col];
        boolean uniform = true;
        outer:
        for (int i = row; i < row + n; i++) {
            for (int j = col; j < col + n; j++) {
                if (grid[i][j] != first) { uniform = false; break outer; }
            }
        }
        if (uniform) {
            return new Node(first == 1, true, null, null, null, null);
        }

        // Not uniform → split into quadrants
        int half = n / 2;
        Node topLeft     = recurse(row,         col,         grid, half);
        Node topRight    = recurse(row,         col + half,  grid, half);
        Node bottomLeft  = recurse(row + half,  col,         grid, half);
        Node bottomRight = recurse(row + half,  col + half,  grid, half);

        // Internal node (val can be anything for non-leaf)
        return new Node(true, false, topLeft, topRight, bottomLeft, bottomRight);
    }
}


// Optimized code using Prefix Sum for uniformity checking, and compression to avoid 
// unnecessary internal node computation
/*
Great question — let’s carefully derive where this **2D prefix sum recurrence** comes from:

---

## 1. What is a prefix sum in 1D?

For a 1D array:

```
arr = [a0, a1, a2, a3 ...]
```

We define prefix sum:

```
ps[i] = arr[0] + arr[1] + ... + arr[i-1]
```

So `ps[i]` = sum of first `i` elements.
That means to get the sum of `[L..R]`, we do:

```
sum(L..R) = ps[R+1] - ps[L]
```

---

## 2. Extending the idea to 2D

Now we want `ps[r][c]` to represent the **sum of the rectangle** from `(0,0)` to `(r-1,c-1)` (inclusive).

So visually:

```
grid:
(0,0) ........ (0,c-1)
   .             .
   .   region    .
   .             .
(r-1,0) .... (r-1,c-1)

ps[r][c] = sum of all values in this region
```

---

## 3. How to compute `ps[r][c]`?

We can build it incrementally:

* Take the **rectangle above** `(0..r-2, 0..c-1)` → `ps[r-1][c]`
* Take the **rectangle left** `(0..r-1, 0..c-2)` → `ps[r][c-1]`
* But the **top-left overlap** `(0..r-2, 0..c-2)` has been counted twice, so subtract it once → `- ps[r-1][c-1]`
* Finally, add the **current cell** itself: `grid[r-1][c-1]`

So the formula is:

```
ps[r][c] = ps[r-1][c] + ps[r][c-1] - ps[r-1][c-1] + grid[r-1][c-1]
```

---

## 4. Visual intuition with a diagram

Suppose we’re filling `ps[r][c]`.

```
 +---------+---+
 |         |   |
 |   A     | B |
 |         |   |
 +---------+---+
 |    C    | X |
 +---------+---+
```

* `A` = `ps[r-1][c-1]` (everything above-left)
* `B` = `ps[r-1][c] - ps[r-1][c-1]` (the column above the new cell)
* `C` = `ps[r][c-1] - ps[r-1][c-1]` (the row left of the new cell)
* `X` = current cell `grid[r-1][c-1]`

So when you add `ps[r-1][c]` (A+B) and `ps[r][c-1]` (A+C), you counted region `A` twice. Subtract once. Then add X.

---

## 5. How it enables fast sub-rectangle queries

Once `ps` is built, any sum in `[r1..r2][c1..c2]` is obtained by inclusion–exclusion:

```
sum = ps[r2+1][c2+1]
    - ps[r1][c2+1]
    - ps[r2+1][c1]
    + ps[r1][c1]
```

This is just applying the same “avoid double counting” logic at query time.

---

✅ So the recurrence is nothing but an extension of the 1D prefix idea with inclusion–exclusion to avoid double-counting overlapping rectangles.

Perfect, let’s work through a **small example step by step** so you can see exactly how the 2D prefix sum table `ps` is computed and then used.

---

## Example Grid

Say we have a 3×3 grid:

```
grid =
1 0 1
0 1 0
1 0 1
```

---

## Step 1. Define the prefix-sum table

We create `ps` of size `(n+1) × (n+1)` = `4 × 4`.
Why `+1`? → To make the math simpler at the borders (so we don’t go negative when subtracting).

* `ps[r][c]` will mean: **number of 1s in the rectangle `[0..r-1][0..c-1]`** (top-left origin).

Initially:

```
ps =
0 0 0 0
0 0 0 0
0 0 0 0
0 0 0 0
```

---

## Step 2. Fill prefix sums row by row

Formula:

```
ps[r][c] = ps[r-1][c] + ps[r][c-1] - ps[r-1][c-1] + grid[r-1][c-1]
```

### Row 1 (`grid` row index = 0)

* (r=1,c=1): ps\[1]\[1] = ps\[0]\[1]+ps\[1]\[0]-ps\[0]\[0]+grid\[0]\[0]
  \= 0+0-0+1 = 1
* (r=1,c=2): ps\[1]\[2] = ps\[0]\[2]+ps\[1]\[1]-ps\[0]\[1]+grid\[0]\[1]
  \= 0+1-0+0 = 1
* (r=1,c=3): ps\[1]\[3] = ps\[0]\[3]+ps\[1]\[2]-ps\[0]\[2]+grid\[0]\[2]
  \= 0+1-0+1 = 2

Row 1 of `ps`: \[0, 1, 1, 2]

### Row 2 (`grid` row index = 1)

* (2,1): ps\[2]\[1] = ps\[1]\[1]+ps\[2]\[0]-ps\[1]\[0]+grid\[1]\[0]
  \= 1+0-0+0 = 1
* (2,2): ps\[2]\[2] = ps\[1]\[2]+ps\[2]\[1]-ps\[1]\[1]+grid\[1]\[1]
  \= 1+1-1+1 = 2
* (2,3): ps\[2]\[3] = ps\[1]\[3]+ps\[2]\[2]-ps\[1]\[2]+grid\[1]\[2]
  \= 2+2-1+0 = 3

Row 2 of `ps`: \[0, 1, 2, 3]

### Row 3 (`grid` row index = 2)

* (3,1): ps\[3]\[1] = ps\[2]\[1]+ps\[3]\[0]-ps\[2]\[0]+grid\[2]\[0]
  \= 1+0-0+1 = 2
* (3,2): ps\[3]\[2] = ps\[2]\[2]+ps\[3]\[1]-ps\[2]\[1]+grid\[2]\[1]
  \= 2+2-1+0 = 3
* (3,3): ps\[3]\[3] = ps\[2]\[3]+ps\[3]\[2]-ps\[2]\[2]+grid\[2]\[2]
  \= 3+3-2+1 = 5

Row 3 of `ps`: \[0, 2, 3, 5]

---

## Final Prefix Sum Table `ps`

```
ps =
0 0 0 0
0 1 1 2
0 1 2 3
0 2 3 5
```

---

## Step 3. Using prefix sums to query a sub-square

Formula for # of ones in `[row..row+size-1][col..col+size-1]`:

```
ones = ps[row+size][col+size] 
     - ps[row][col+size] 
     - ps[row+size][col] 
     + ps[row][col]
```

### Example Query: square (row=1,col=1,size=2)

This means the 2×2 block:

```
grid[1..2][1..2] =
1 0
0 1
```

→ Total = 2 ones.

Now check via `ps`:

```
ones = ps[3][3] - ps[1][3] - ps[3][1] + ps[1][1]
     = 5        - 2        - 2        + 1
     = 2
```

✅ Matches exactly.

---

### Summary

* `ps` lets you compute sums of **any square** in O(1).
* No need to rescan the block.
* Uniform test:

  * `ones == 0` → all 0s.
  * `ones == size*size` → all 1s.
  * else → mixed.


Code Explanation:
The main bottleneck is the **uniformity test**: scanning a k×k square over and over costs O(k²) each time, which balloons to \~O(n² log n) in the worst case. A simple, powerful optimization is a **2D prefix-sum (integral image)** so you can check “all-zeros / all-ones” in **O(1)** for any square. Then you only recurse when needed.

# How the optimized approach works

1. **Precompute 2D prefix sums**
   Let `ps[r+1][c+1]` store the number of 1s in the rectangle `[0..r][0..c]` (inclusive).
   Then the number of 1s in a square with top-left `(row,col)` and side `size` is:

```
ones = ps[row+size][col+size] - ps[row][col+size] - ps[row+size][col] + ps[row][col]
```

* If `ones == 0` → all zeros → **leaf(false)**
* If `ones == size*size` → all ones → **leaf(true)**
* Else → **split into 4 quadrants** and recurse

2. **Post-merge compression (micro-optimization)**
   After building four children, if they’re all leaves and all have the same `val`, **collapse** them into a single leaf. This avoids unnecessary internal nodes in uniform regions.

3. **Caching?**
   There’s usually **no overlapping sub-squares**, so memoization by `(row,col,size)` doesn’t help. (You can reuse the same prebuilt leaf instances for `true/false` to save a few objects, but LeetCode doesn’t require that and identity-sharing is optional.)

4. **Complexity**

* Building `ps`: O(n²)
* Each node’s uniformity check: O(1)
* Total: O(n²) time in practice; recursion/tree construction overhead remains, but you’ve removed the repeated O(k²) scans.
* Space: O(n²) for `ps` + O(H) recursion stack (H = log₂ n; input n is a power of two per problem).
*/

// class Solution {
//     private int n;
//     private int[][] ps; // prefix sums of 1s, size (n+1) x (n+1)

//     public Node construct(int[][] grid) {
//         n = grid.length;
//         // 1) Build 2D prefix sum: ps[r+1][c+1] = # of 1s in rectangle [0..r][0..c]
//         ps = new int[n + 1][n + 1];
//         for (int r = 1; r <= n; r++) {
//             int rowSum = 0;
//             for (int c = 1; c <= n; c++) {
//                 rowSum += grid[r - 1][c - 1];               // current row running sum
//                 ps[r][c] = ps[r - 1][c] + rowSum;           // add sum above
//             }
//         }

//         // 2) Build the quad-tree from the whole square
//         return build(grid, 0, 0, n);
//     }

//     // Build a quad-tree node for the square with top-left (row,col) and side length 'size'
//     private Node build(int[][] grid, int row, int col, int size) {
//         // O(1) uniformity check using prefix sums
//         int ones = sumOnes(row, col, size);
//         if (ones == 0) {
//             // All zeros → leaf(false)
//             return new Node(false, true, null, null, null, null);
//         }
//         if (ones == size * size) {
//             // All ones → leaf(true)
//             return new Node(true, true, null, null, null, null);
//         }

//         // Not uniform → split into 4 quadrants
//         int half = size / 2;

//         Node topLeft     = build(grid, row,          col,          half);
//         Node topRight    = build(grid, row,          col + half,   half);
//         Node bottomLeft  = build(grid, row + half,   col,          half);
//         Node bottomRight = build(grid, row + half,   col + half,   half);

//         // Post-merge compression:
//         // If all 4 children are leaves with the same value, collapse into a single leaf.
//         if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf) {
//             boolean v = topLeft.val;
//             if (v == topRight.val && v == bottomLeft.val && v == bottomRight.val) {
//                 return new Node(v, true, null, null, null, null);
//             }
//         }

//         // Otherwise return a proper internal node (val can be arbitrary for internal nodes)
//         return new Node(true, false, topLeft, topRight, bottomLeft, bottomRight);
//     }

//     // Number of 1s in the square [row .. row+size-1][col .. col+size-1]
//     private int sumOnes(int row, int col, int size) {
//         int r2 = row + size;
//         int c2 = col + size;
//         // Inclusion–exclusion on the 2D prefix sums
//         return ps[r2][c2] - ps[row][c2] - ps[r2][col] + ps[row][col];
//     }
// }