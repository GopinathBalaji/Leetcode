// Method 1: Top-Down DP Approach
/*
# What I was doing wrong:

## 1. You’re not memoizing cells that are '0'

In your code:

```java
if(matrix[i][j] == '0'){
    return 0;
}
```

You return 0 but you never write `memo[i][j] = 0`.
That means if multiple neighbors call `dp(i,j)`, you’ll recompute that branch again and again.

This is not *logically* wrong, but it’s wasteful and can push you toward TLE on bigger inputs. Memoization should cover every state, including states that evaluate to 0.

We'll fix that by storing `0` in `memo[i][j]` before returning.

---

## 2. Your bounds check can be simplified

You wrote:

```java
if(i < 0 || i >= matrix.length || j < 0 || j >= matrix[0].length){
    return 0;
}
```

This works because you’re guarding both underflow and overflow.
But notice: in recursion you only ever go **up** (`i-1`) and **left** (`j-1`), never down or right past the matrix. So you’ll never hit `i >= matrix.length` or `j >= matrix[0].length` from recursion. You only risk going negative.

We can simplify to `if (i < 0 || j < 0) return 0;`
This also slightly reduces branching.

(Not required for correctness, but cleaner.)

---

## 3. The memo check should happen before we recurse

You actually already do this:

```java
if(memo[i][j] != -1){
    return memo[i][j];
}
```

That’s good. We’ll keep it.

But we’ll just reorder the logic a tiny bit to make sure we *also* cache zeros.

---

## 4. Style: `best` and imports

Two polish things:

* You should `import java.util.Arrays;`, or this won't compile.
* `int best = Integer.MIN_VALUE;` works, but `int best = 0;` is simpler (the largest square side length can’t be negative anyway).


## How this works (conceptually)

### Meaning of `dfs(i, j)`

`dfs(i, j)` = “What is the side length of the biggest all-1 square whose bottom-right corner is at `(i, j)`?”

That means:

* If `matrix[i][j] == '0'`, answer is `0` immediately. You can't end a square of 1s on a 0.
* If `matrix[i][j] == '1'`, then:

  * You can always form at least a `1x1` square (just itself).
  * But you might be able to form bigger, like `2x2`, `3x3`, etc.
  * For you to form a `(k+1) x (k+1)` square ending at `(i, j)`, you need:

    * a `k x k` square above `(i-1, j)`
    * a `k x k` square to the left `(i, j-1)`
    * a `k x k` square diagonally up-left `(i-1, j-1)`

  So the size you're allowed here is limited by the **smallest** of those three.
  That’s why we do:

  ```java
  memo[i][j] = 1 + min(up, left, diag)
  ```

If one of those neighbors can only support size 2, you can't magically create a 4x4 here. The weakest side limits you.

---

## Why do we try every cell in the outer loop?

Because the biggest square might end anywhere.

For example:

* The top-left corner may be noisy / full of 0s,
* The bottom-right region may quietly contain a 3x3 block of 1s,
* The max answer comes from the cell that is the bottom-right corner of that block.

So we do:

```java
for (i,j):
    maxSide = max(maxSide, dfs(i,j))
```

Then return `maxSide * maxSide` because the problem wants area.

---

## Tiny example walkthrough

Say the matrix is:

[
['1','0','1','0','0'],
['1','0','1','1','1'],
['1','1','1','1','1'],
['1','0','0','1','0']
]

We'll describe what happens for `(2,2)` (0-indexed, value = '1') — the center of the big block.

* We call `dfs(2,2)`:

  * It's '1', so we look at:

    * `up = dfs(1,2)`  (square ending above)
    * `left = dfs(2,1)` (square ending left)
    * `diag = dfs(1,1)` (square ending diagonally up-left)

* Suppose:

  * `dfs(1,2)` returns 1 (there's at least a `1x1` ending at (1,2))
  * `dfs(2,1)` returns 1
  * `dfs(1,1)` returns 1
    Then:

  ```text
  dfs(2,2) = 1 + min(1,1,1) = 2
  ```

  Which means: there is a `2x2` square of '1's whose bottom-right corner is (2,2).

Later, maxSide will become at least 2. Area = 2*2 = 4.
That's the correct LeetCode output for this matrix.

---

## Recap of what was "wrong" in yours

* You didn't store `0` in memo for '0' cells (hurts performance).
* Bounds check could be cleaner.
* You need to import `Arrays`.
* Everything else (recurrence, scanning all cells, squaring max) is solid.
*/

class Solution {
    public int maximalSquare(char[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        // memo[i][j] = side length of largest all-1 square
        // with bottom-right corner at (i, j)
        // -1 means "not computed yet"
        int[][] memo = new int[m][n];
        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }

        int maxSide = 0;

        // Try every cell as the bottom-right corner of the square
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                maxSide = Math.max(maxSide, dfs(matrix, memo, i, j));
            }
        }

        // Problem asks for AREA, not side length
        return maxSide * maxSide;
    }

    private int dfs(char[][] matrix, int[][] memo, int i, int j) {
        // Out of bounds => no square
        if (i < 0 || j < 0) {
            return 0;
        }

        // If we've already computed this cell, reuse it
        if (memo[i][j] != -1) {
            return memo[i][j];
        }

        // If this cell is '0', it cannot be the bottom-right of any all-1 square.
        if (matrix[i][j] == '0') {
            memo[i][j] = 0;       // <- IMPORTANT: cache 0 as well
            return 0;
        }

        // Otherwise this cell is '1'
        // Recurrence:
        // size here = 1 + min( up, left, diag )
        int up    = dfs(matrix, memo, i - 1, j);
        int left  = dfs(matrix, memo, i, j - 1);
        int diag  = dfs(matrix, memo, i - 1, j - 1);

        memo[i][j] = 1 + Math.min(diag, Math.min(up, left));
        return memo[i][j];
    }
}




// Method 2: Bottom-Up DP
/*
## Explanation of the DP idea

### What `dp[i][j]` means

We define:

> `dp[i][j]` = the **side length** of the largest square made only of '1's such that the square’s **bottom-right corner** is exactly at `matrix[i-1][j-1]`.

Why `i-1`, `j-1` and not just `i`, `j`?

We build `dp` with one extra padding row and column (index 0).
That way:

* `dp[0][*] = 0` and `dp[*][0] = 0`
* When we write the recurrence for `dp[i][j]`, and we read `dp[i-1][j]`, `dp[i][j-1]`, `dp[i-1][j-1]`, we never fall off the array.

This avoids ugly `if (i > 0 && j > 0)` checks.

Example mappings:

* `dp[1][1]` corresponds to `matrix[0][0]`
* `dp[1][2]` corresponds to `matrix[0][1]`
* `dp[3][5]` corresponds to `matrix[2][4]`

This is a very common trick.

---

### The recurrence

Let’s zoom in on some cell `(i, j)` in `dp`, which corresponds to `(i-1, j-1)` in `matrix`.

If `matrix[i-1][j-1] == '0'`:

* Then that cell cannot be the bottom-right of an all-1 square.
* So `dp[i][j] = 0`.

If `matrix[i-1][j-1] == '1'`:

* Then at minimum you have a 1×1 square of '1's ending here.
* But maybe you can do bigger than 1×1. For it to be bigger, say side = s ≥ 2, you need:

  * A square of side ≥ s-1 directly **above** you
  * A square of side ≥ s-1 directly **to the left** of you
  * A square of side ≥ s-1 on the **diagonal up-left** of you

Visually, to extend to a bigger square ending at (i,j), you want:

```
[ diag-square ][ up extension ]
[ left ext    ][ current cell ]
```

All of those regions must be filled with '1'.

That logic becomes:

```java
dp[i][j] = 1 + min(
    dp[i-1][j],    // up
    dp[i][j-1],    // left
    dp[i-1][j-1]   // diagonal up-left
)
```

Why `min`?
Because the size of the largest square you can form here is limited by the SMALLEST of the three support squares. If any direction can only support, say, a 2×2, then you can’t suddenly make a 4×4 here — you're bottlenecked by that weakest direction.

Then we keep a global `maxSide` while we scan.
In the end we return `maxSide * maxSide` because we want area.

---

## Complexity

* Time: O(m * n), where m = #rows, n = #cols.
* Space: O(m * n) for the DP table.
  (You can optimize to O(n) space using one rolling row, but this 2D DP is the clearest version.)

---

## Full example walkthrough

Let’s walk through a classic test matrix (from the LeetCode prompt):

```text
matrix =
[
  ['1','0','1','0','0'],
  ['1','0','1','1','1'],
  ['1','1','1','1','1'],
  ['1','0','0','1','0']
]
```

Dimensions: m = 4 rows, n = 5 cols.

We'll build `dp` of size (m+1) x (n+1) = 5 x 6, all starting at 0:

We'll fill it row by row, col by col.

I'll show:

* `i` / `j` in dp
* the corresponding matrix cell `(i-1, j-1)`
* computed `dp[i][j]`
* and current `maxSide`

We'll write the dp table as we go (only nonzero entries are interesting).

---

### Initialization

Before we start:

`dp` (5x6) is all zeros:

Row 0 and Col 0 are all zeros by construction:

```
   j→  0 1 2 3 4 5
i
0     0 0 0 0 0 0
1     0 . . . . .
2     0 . . . . .
3     0 . . . . .
4     0 . . . . .
```

We'll fill `.` as we go.

`maxSide = 0`

---

### i = 1 (matrix row 0)

We'll go across j = 1..5, which maps to matrix row 0, columns 0..4.

#### j = 1 → matrix[0][0] = '1'

* up = dp[0][1] = 0
* left = dp[1][0] = 0
* diag = dp[0][0] = 0
* dp[1][1] = 1 + min(0,0,0) = 1
* maxSide = max(0,1) = 1

#### j = 2 → matrix[0][1] = '0'

* dp[1][2] = 0
* maxSide still 1

#### j = 3 → matrix[0][2] = '1'

* up = dp[0][3] = 0
* left = dp[1][2] = 0
* diag = dp[0][2] = 0
* dp[1][3] = 1 + min(0,0,0) = 1
* maxSide = 1

#### j = 4 → matrix[0][3] = '0'

* dp[1][4] = 0

#### j = 5 → matrix[0][4] = '0'

* dp[1][5] = 0

After i = 1, dp row 1 is:

```
i=1: 0 1 0 1 0 0
```

dp so far:

```
   j→   0 1 2 3 4 5
i
0      0 0 0 0 0 0
1      0 1 0 1 0 0
2      0 . . . . .
3      0 . . . . .
4      0 . . . . .
```

---

### i = 2 (matrix row 1)

This row in the original matrix is:
`['1','0','1','1','1']`

We'll calculate dp[2][1]..dp[2][5].

#### j = 1 → matrix[1][0] = '1'

* up = dp[1][1] = 1
* left = dp[2][0] = 0
* diag = dp[1][0] = 0
* dp[2][1] = 1 + min(1,0,0) = 1
* maxSide stays 1

Why min(...) has 0:

* On the left of this cell (dp[2][0]) we can't go bigger than 0 because col 0 is padded 0.
* That means you can't form a 2x2 ending here yet, only 1x1.

#### j = 2 → matrix[1][1] = '0'

* dp[2][2] = 0

#### j = 3 → matrix[1][2] = '1'

* up   = dp[1][3] = 1
* left = dp[2][2] = 0
* diag = dp[1][2] = 0
* dp[2][3] = 1 + min(1,0,0) = 1
* maxSide = 1

#### j = 4 → matrix[1][3] = '1'

* up   = dp[1][4] = 0
* left = dp[2][3] = 1
* diag = dp[1][3] = 1
* dp[2][4] = 1 + min(0,1,1) = 1
* maxSide = 1

#### j = 5 → matrix[1][4] = '1'

* up   = dp[1][5] = 0
* left = dp[2][4] = 1
* diag = dp[1][4] = 0
* dp[2][5] = 1 + min(0,1,0) = 1
* maxSide = 1

After i = 2, row 2 is:

```
i=2: 0 1 0 1 1 1
```

Now dp looks like:

```
   j→   0 1 2 3 4 5
i
0      0 0 0 0 0 0
1      0 1 0 1 0 0
2      0 1 0 1 1 1
3      0 . . . . .
4      0 . . . . .
```

Still no square bigger than 1x1 yet. Wait for row 3.

---

### i = 3 (matrix row 2)

Row 2 of matrix is:
`['1','1','1','1','1']`

Now we start getting bigger than size 1.

We compute dp[3][1]..dp[3][5]:

#### j = 1 → matrix[2][0] = '1'

* up   = dp[2][1] = 1
* left = dp[3][0] = 0
* diag = dp[2][0] = 0
* dp[3][1] = 1 + min(1,0,0) = 1
* maxSide = 1

#### j = 2 → matrix[2][1] = '1'

* up   = dp[2][2] = 0
* left = dp[3][1] = 1
* diag = dp[2][1] = 1
* dp[3][2] = 1 + min(0,1,1) = 1
* maxSide = 1

#### j = 3 → matrix[2][2] = '1'

Now watch this one. This is where a 2x2 square appears.

* up   = dp[2][3] = 1
* left = dp[3][2] = 1
* diag = dp[2][2] = 0
* dp[3][3] = 1 + min(1,1,0) = 1

Still just 1, because dp[2][2] was 0 (matrix[1][1] was '0'), so the up-left corner blocks forming a 2x2 here.

#### j = 4 → matrix[2][3] = '1'

This is the interesting one.

* up   = dp[2][4] = 1
* left = dp[3][3] = 1
* diag = dp[2][3] = 1
* dp[3][4] = 1 + min(1,1,1) = 2
* maxSide = max(1,2) = 2

Interpretation:

* We just found a 2×2 square of all '1's whose bottom-right corner is at matrix[2][3] (which is row 2, col 3 in original 0-based indexing).
* That 2×2 block in the original matrix is:

  rows 1..2, cols 2..3 (0-based):

  ```
  1 1
  1 1
  ```

#### j = 5 → matrix[2][4] = '1'

* up   = dp[2][5] = 1
* left = dp[3][4] = 2
* diag = dp[2][4] = 1
* dp[3][5] = 1 + min(1,2,1) = 2
* maxSide = 2

After i = 3, row 3 is:

```
i=3: 0 1 1 1 2 2
```

Our dp table now:

```
   j→   0 1 2 3 4 5
i
0      0 0 0 0 0 0
1      0 1 0 1 0 0
2      0 1 0 1 1 1
3      0 1 1 1 2 2
4      0 . . . . .
```

And we have `maxSide = 2` so far, meaning the best square we’ve seen is 2x2 (area 4).

---

### i = 4 (matrix row 3)

Row 3 of matrix is:
`['1','0','0','1','0']`

Let’s finish the table:

#### j = 1 → matrix[3][0] = '1'

* up   = dp[3][1] = 1
* left = dp[4][0] = 0
* diag = dp[3][0] = 0
* dp[4][1] = 1 + min(1,0,0) = 1
* maxSide = 2

#### j = 2 → matrix[3][1] = '0'

* dp[4][2] = 0

#### j = 3 → matrix[3][2] = '0'

* dp[4][3] = 0

#### j = 4 → matrix[3][3] = '1'

* up   = dp[3][4] = 2
* left = dp[4][3] = 0
* diag = dp[3][3] = 1
* dp[4][4] = 1 + min(2,0,1) = 1
  (Can't grow >1 here because the left neighbor dp[4][3] is 0.)
* maxSide = 2

#### j = 5 → matrix[3][4] = '0'

* dp[4][5] = 0

After i = 4:

```
i=4: 0 1 0 0 1 0
```

Final dp table:

```
   j→   0 1 2 3 4 5
i
0      0 0 0 0 0 0
1      0 1 0 1 0 0
2      0 1 0 1 1 1
3      0 1 1 1 2 2
4      0 1 0 0 1 0
```

And our recorded `maxSide = 2`.

Return `maxSide * maxSide = 2 * 2 = 4`.

That matches the expected answer for this matrix: the largest square of 1s has side 2, area 4.

---

## Why this works intuitively

* Each `dp[i][j]` is saying:

  > "How big a perfect filled square can I claim that ends exactly here?"

* The recurrence enforces the idea that to grow a (k+1)×(k+1) square ending here, I need:

  * a k×k ending above-left,
  * and the row above continued to support it,
  * and the column to the left continued to support it.

* Taking the `min` of those three neighbors enforces the weakest boundary. If any side fails earlier (like there's a '0' that breaks continuity), you can't get a bigger square.

* Keeping `maxSide` while we fill the table means we don’t need a second pass.

---

## Summary

* We pad the dp array with an extra row and column of zeros so we can use a clean recurrence without bounds checks.
* `dp[i][j]` stores side length, not area.
* If a cell in `matrix` is '1', we extend from up / left / diag.
* If it's '0', `dp[i][j] = 0`.
* Track the max side length, square it at the end for area.
*/
// class Solution {
//     public int maximalSquare(char[][] matrix) {
//         int m = matrix.length;
//         int n = matrix[0].length;

//         // dp[i][j] = side length of the largest all-1 square
//         //            whose bottom-right corner is at (i-1, j-1)
//         //
//         // WHY i-1 and j-1??
//         // We add an extra row and col of zeros at the top/left (dp[0][*], dp[*][0])
//         // so we never go out of bounds when we look "up", "left", or "diag".
//         //
//         // So dp has dimensions (m+1) x (n+1)
//         int[][] dp = new int[m + 1][n + 1];

//         int maxSide = 0; // we'll track the largest square SIDE we ever see

//         for (int i = 1; i <= m; i++) {        // note: starts at 1
//             for (int j = 1; j <= n; j++) {    // note: starts at 1
//                 if (matrix[i - 1][j - 1] == '1') {
//                     // Recurrence:
//                     // dp[i][j] = 1 + min(
//                     //      dp[i-1][j],    // up
//                     //      dp[i][j-1],    // left
//                     //      dp[i-1][j-1]   // diag up-left
//                     // )
//                     int up    = dp[i - 1][j];
//                     int left  = dp[i][j - 1];
//                     int diag  = dp[i - 1][j - 1];

//                     dp[i][j] = 1 + Math.min(diag, Math.min(up, left));

//                     // update global best
//                     if (dp[i][j] > maxSide) {
//                         maxSide = dp[i][j];
//                     }
//                 } else {
//                     // If matrix cell is '0', you cannot end a square here.
//                     dp[i][j] = 0;
//                 }
//             }
//         }

//         // Problem asks for AREA, not side length
//         return maxSide * maxSide;
//     }
// }
