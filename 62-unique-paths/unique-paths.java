// Method 1: Top-Down 2D DP
/*
## 2. Top-down DP idea (what your recursion means)

Let’s formalize what your `dp` function is doing:

### State definition

> `dp(i, j)` = number of unique paths from cell `(i, j)` to the destination `(m-1, n-1)`
> given you can only move **down** (`i+1, j`) or **right** (`i, j+1`).

We want `dp(0, 0)`.

### Base cases

1. **Out of bounds**:

   ```java
   if (i < 0 || i >= m || j < 0 || j >= n) return 0;
   ```

   If you step outside the grid, that’s not a valid path, so there are 0 ways.

2. **Destination cell**:

   ```java
   if (i == m-1 && j == n-1) return 1;
   ```

   If you’re already at the bottom-right, there is exactly **one** path from here:
   → "do nothing, we’re done".

### Recurrence

From any **valid, non-destination** cell `(i, j)`, you have two choices:

* Move **down** → go to `(i+1, j)` → contributes `dp(i+1, j)` paths.
* Move **right** → go to `(i, j+1)` → contributes `dp(i, j+1)` paths.

Since all paths are disjoint in terms of their sequences, total paths:

```text
dp(i, j) = dp(i+1, j) + dp(i, j+1)
```

That’s exactly what your code computes:

```java
int down = dp(memo, m, n, i+1, j);
int right = dp(memo, m, n, i, j+1);
memo[i][j] = down + right;
```

### Memoization

Without memoization, this recursion is exponential, because the same states `(i,j)` are recomputed many times via different paths.

You avoid that by:

```java
if (memo[i][j] != null) {
    return memo[i][j];
}
```

So each cell `(i,j)` is computed only once, then cached.

Overall:

* Time: O(m·n)
* Space: O(m·n) for memo + O(m+n) recursion stack

---

## 3. Thorough example walkthrough (3×3 grid)

Let’s take a small grid:

```text
m = 3, n = 3

Cells (i,j):

(0,0) (0,1) (0,2)
(1,0) (1,1) (1,2)
(2,0) (2,1) (2,2)  <-- destination
```

We want `uniquePaths(3,3)` → `dp(0,0)`.

We’ll see how values get computed and stored in `memo`.

### Step 0: Setup

`memo` is a 3×3 array, all `null` initially:

```text
memo:
[ [null, null, null],
  [null, null, null],
  [null, null, null] ]
```

Call: `dp(0,0)`.

---

### Step 1: dp(0,0)

* `(0,0)` is inside bounds and not destination.
* `memo[0][0]` is null → compute:

```java
down  = dp(1,0)
right = dp(0,1)
memo[0][0] = down + right
```

We need `dp(1,0)` and `dp(0,1)`.

---

### Step 2: dp(1,0)

* `(1,0)` is valid, not destination.
* `memo[1][0]` is null.

Compute:

```java
down  = dp(2,0)
right = dp(1,1)
memo[1][0] = down + right
```

Need `dp(2,0)` and `dp(1,1)`.

---

#### Step 2a: dp(2,0)

* `(2,0)` is valid, not destination.
* `memo[2][0]` null.

Compute:

```java
down  = dp(3,0)
right = dp(2,1)
```

* `dp(3,0)` → out of bounds (`i >= m`), so returns 0.
* Need `dp(2,1)`.

##### Step 2a-i: dp(2,1)

* `(2,1)` is valid, not destination.
* `memo[2][1]` null.

Compute:

```java
down  = dp(3,1)  // out of bounds → 0
right = dp(2,2)
```

Call `dp(2,2)`.

###### Step 2a-i-x: dp(2,2)

* `(2,2)` == `(m-1, n-1)` → destination.

So:

```java
dp(2,2) = 1
```

Return 1.

Back to `dp(2,1)`:

* `down = 0`
* `right = 1`
* `memo[2][1] = 0 + 1 = 1`

So:

```text
dp(2,1) = 1
memo[2][1] = 1
```

Back to `dp(2,0)`:

* `down = dp(3,0) = 0`
* `right = dp(2,1) = 1`
* `memo[2][0] = 0 + 1 = 1`

So:

```text
dp(2,0) = 1
memo[2][0] = 1
```

Interpretation: from `(2,0)`, there is exactly **one** way to get to `(2,2)`:
→ go right twice: `(2,0) → (2,1) → (2,2)`.

---

#### Step 2b: dp(1,1)

Now back in `dp(1,0)`, we still need `dp(1,1)`.

* `(1,1)` valid, not destination.
* `memo[1][1]` null.

Compute:

```java
down  = dp(2,1)   // already computed as 1
right = dp(1,2)
```

We know `dp(2,1) = 1`. Now compute `dp(1,2)`.

##### Step 2b-i: dp(1,2)

* `(1,2)` valid, not destination.
* `memo[1][2]` null.

Compute:

```java
down  = dp(2,2)
right = dp(1,3)  // out of bounds → 0
```

* `dp(2,2)` = 1 (destination)
* `dp(1,3)` = 0 (out of bounds)

So:

```text
dp(1,2) = 1 + 0 = 1
memo[1][2] = 1
```

Back to `dp(1,1)`:

* `down = dp(2,1) = 1`
* `right = dp(1,2) = 1`
* `memo[1][1] = 1 + 1 = 2`

So:

```text
dp(1,1) = 2
memo[1][1] = 2
```

Interpretation: from `(1,1)`, there are 2 ways to get to `(2,2)`:

* `(1,1) → (2,1) → (2,2)`
* `(1,1) → (1,2) → (2,2)`

Back to `dp(1,0)`:

* `down  = dp(2,0) = 1`
* `right = dp(1,1) = 2`
* `memo[1][0] = 1 + 2 = 3`

So:

```text
dp(1,0) = 3
memo[1][0] = 3
```

Interpretation: from `(1,0)` there are 3 ways to reach `(2,2)`.

---

### Step 3: dp(0,1)

Back to our original `dp(0,0)`, we also need `dp(0,1)`.

* `(0,1)` valid, not destination.
* `memo[0][1]` null.

Compute:

```java
down  = dp(1,1)  // already computed = 2
right = dp(0,2)
```

Need `dp(0,2)`.

#### Step 3a: dp(0,2)

* `(0,2)` valid, not destination.
* `memo[0][2]` null.

Compute:

```java
down  = dp(1,2)  // computed = 1
right = dp(0,3)  // out of bounds → 0
```

So:

```text
dp(0,2) = 1 + 0 = 1
memo[0][2] = 1
```

Back to `dp(0,1)`:

* `down  = dp(1,1) = 2`
* `right = dp(0,2) = 1`
* `memo[0][1] = 2 + 1 = 3`

So:

```text
dp(0,1) = 3
memo[0][1] = 3
```

Interpretation: from `(0,1)` there are 3 paths to `(2,2)`.

---

### Final step: back to dp(0,0)

We now have:

* `dp(1,0) = 3`
* `dp(0,1) = 3`

So:

```java
memo[0][0] = dp(1,0) + dp(0,1) = 3 + 3 = 6
dp(0,0) = 6
```

**Answer:** `uniquePaths(3,3) = 6`.

This matches the well-known combinatorial result:

* To go from top-left to bottom-right in a 3×3 grid:

  * You need 2 rights (R) and 2 downs (D) in some order (total 4 moves).
  * Number of distinct sequences: `C(4, 2) = 6`.

---

### Final memo table

At the end, `memo` looks like:

```text
[ [6, 3, 1],
  [3, 2, 1],
  [1, 1, 1] ]
```

Each `memo[i][j]` is “number of paths from (i,j) to (2,2)”.
*/
class Solution {
    public int uniquePaths(int m, int n) {
        Integer[][] memo = new Integer[m][n];

        return dp(memo, m, n, 0, 0);        
    }

    private int dp(Integer[][] memo, int m, int n, int i, int j){
        if(i < 0 || i >= m || j < 0 || j >= n){
            return 0;
        }
        if(i == m-1 && j == n-1){
            return 1;
        }

        if(memo[i][j] != null){
            return memo[i][j];
        }

        
        int down = dp(memo, m, n, i+1, j);
        int right = dp(memo, m, n, i, j+1);

        memo[i][j] = down + right;

        return memo[i][j];
    }
}





// Method 2: Bottom-Up Approach
/*
Nice, this is a perfect one to translate from your top-down recursion into a clean bottom-up table.

I’ll walk through:

1. The bottom-up DP idea
2. The Java implementation
3. A step-by-step walkthrough on a 3×3 grid

---

## 1. Bottom-up DP idea

We’re on an `m x n` grid:

* Start: top-left cell `(0,0)`
* End: bottom-right cell `(m-1, n-1)`
* Moves allowed: **only right** or **only down**

We want to count **how many unique paths** exist from start to end.

### DP state

Define:

> `dp[i][j]` = number of unique paths to reach cell `(i, j)` **from** `(0, 0)`.

(You can also define it as “from `(i, j)` to the end”; it’s symmetric. Here we’ll use “to reach `(i,j)`”.)

### Base cases – first row and first column

Think about the first row `(0, j)`:

* To reach any cell in the first row, you can only come from the **left**.
* There’s exactly **one** way to reach any `(0, j)`:

  * Move right `j` times from `(0,0)`.

So:

```text
dp[0][j] = 1 for all j
```

Similarly, for the first column `(i, 0)`:

* To reach any cell in the first column, you can only come from **above**.
* Exactly one way to reach `(i,0)`:

  * Move down `i` times from `(0,0)`.

So:

```text
dp[i][0] = 1 for all i
```

### Recurrence for the rest of the cells

For any other cell `(i, j)` with `i > 0` and `j > 0`:

To arrive at `(i, j)`, you must have come from either:

* The cell **above**: `(i-1, j)`
* The cell **to the left**: `(i, j-1)`

So the number of ways to reach `(i, j)` is:

```text
dp[i][j] = dp[i-1][j] + dp[i][j-1]
```

This is exactly the same recurrence you had in the top-down version, just “built up” iteratively.

### Answer

We fill the whole `dp` table row by row, column by column, using the above rules.

The final answer is:

```text
dp[m-1][n-1]
```

---

## 2. Java bottom-up implementation

```java
class Solution {
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];

        // 1) Initialize first column: only one way to go straight down
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }

        // 2) Initialize first row: only one way to go straight right
        for (int j = 0; j < n; j++) {
            dp[0][j] = 1;
        }

        // 3) Fill the rest of the table
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }

        // 4) The answer is number of ways to reach bottom-right
        return dp[m - 1][n - 1];
    }
}
```

* Time: `O(m * n)`
* Space: `O(m * n)`
  (You can optimize to `O(n)` using a 1D array, but this 2D version is conceptually clearer.)

---

## 3. Thorough example walkthrough – m=3, n=3

Let’s use the same 3×3 example as before:

Grid coordinates:

```text
(0,0) (0,1) (0,2)
(1,0) (1,1) (1,2)
(2,0) (2,1) (2,2)
```

We’ll construct `dp[i][j]` step by step.

### Step 1: Initialize dp with zeros

We start with a 3×3 array of zeros:

```text
dp:
[ [0, 0, 0],
  [0, 0, 0],
  [0, 0, 0] ]
```

### Step 2: First column = 1

There’s exactly one way to get to any cell in the first column: go straight down.

We run:

```java
for (int i = 0; i < m; i++) {
    dp[i][0] = 1;
}
```

Now `dp`:

```text
[ [1, 0, 0],
  [1, 0, 0],
  [1, 0, 0] ]
```

This matches the idea:

* `(0,0)`: start → 1 way.
* `(1,0)`: one way: D (down).
* `(2,0)`: one way: D, D.

### Step 3: First row = 1

Similarly, only one way to reach any cell in the first row: go straight right.

```java
for (int j = 0; j < n; j++) {
    dp[0][j] = 1;
}
```

Now `dp`:

```text
[ [1, 1, 1],
  [1, 0, 0],
  [1, 0, 0] ]
```

Interpretation:

* `(0,1)`: only path is R from `(0,0)`.
* `(0,2)`: only path is R, R from `(0,0)`.

### Step 4: Fill the rest using the recurrence

Now for each `i >= 1` and `j >= 1`:

```java
dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
```

We go row by row, left to right.

#### Cell (1,1)

```text
dp[1][1] = dp[0][1] + dp[1][0]
         = 1       + 1
         = 2
```

Update:

```text
[ [1, 1, 1],
  [1, 2, 0],
  [1, 0, 0] ]
```

Interpretation: ways to reach `(1,1)`:

1. Right then down: `(0,0) → (0,1) → (1,1)` (R, D)
2. Down then right: `(0,0) → (1,0) → (1,1)` (D, R)

So 2 paths.

#### Cell (1,2)

```text
dp[1][2] = dp[0][2] + dp[1][1]
         = 1       + 2
         = 3
```

Update:

```text
[ [1, 1, 1],
  [1, 2, 3],
  [1, 0, 0] ]
```

Interpretation: ways to get to `(1,2)`:

* From above `(0,2)` (1 way) and then down.
* From left `(1,1)` (2 ways to get there) then right.

So total = 1 + 2 = 3 paths.

#### Cell (2,1)

Now next row, `i = 2`.

```text
dp[2][1] = dp[1][1] + dp[2][0]
         = 2       + 1
         = 3
```

Update:

```text
[ [1, 1, 1],
  [1, 2, 3],
  [1, 3, 0] ]
```

Interpretation: to reach `(2,1)`:

* From `(1,1)` down: there are 2 ways to reach `(1,1)`.
* From `(2,0)` right: there’s 1 way to reach `(2,0)`.

Total = 3 paths.

#### Cell (2,2) – the destination

Finally:

```text
dp[2][2] = dp[1][2] + dp[2][1]
         = 3       + 3
         = 6
```

Update:

```text
[ [1, 1, 1],
  [1, 2, 3],
  [1, 3, 6] ]
```

So:

```text
dp[2][2] = 6
```

That’s our answer: `uniquePaths(3,3) = 6`.

### Sanity check: list all 6 paths (optional but intuitive)

For a 3×3 grid, to get from `(0,0)` to `(2,2)` you must move:

* 2 times down (D)
* 2 times right (R)

So each path is a permutation of `D, D, R, R`.

All distinct sequences:

1. `D D R R`
2. `D R D R`
3. `D R R D`
4. `R D D R`
5. `R D R D`
6. `R R D D`

There are `C(4,2) = 6` such permutations, matching `dp[2][2]`.

---

## Summary

Bottom-up DP for **Unique Paths (62)**:

* State: `dp[i][j]` = number of paths from `(0,0)` to `(i,j)`.
* Base:

  * First row: `dp[0][j] = 1`
  * First column: `dp[i][0] = 1`
* Recurrence for all `i>0, j>0`:

  * `dp[i][j] = dp[i-1][j] + dp[i][j-1]`
* Answer: `dp[m-1][n-1]`.
*/

// class Solution {
//     public int uniquePaths(int m, int n) {
//         int[][] dp = new int[m][n];

//         // 1) Initialize first column: only one way to go straight down
//         for (int i = 0; i < m; i++) {
//             dp[i][0] = 1;
//         }

//         // 2) Initialize first row: only one way to go straight right
//         for (int j = 0; j < n; j++) {
//             dp[0][j] = 1;
//         }

//         // 3) Fill the rest of the table
//         for (int i = 1; i < m; i++) {
//             for (int j = 1; j < n; j++) {
//                 dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
//             }
//         }

//         // 4) The answer is number of ways to reach bottom-right
//         return dp[m - 1][n - 1];
//     }
// }