// Method 1: Top-Down DP
/*

 **Risk of overflow when adding a sentinel.**
   Even away from the destination, if you ever add `grid[i][j] + Integer.MAX_VALUE`, it will overflow to a negative number. Use a smaller “infinity” (e.g., `1_000_000_000`) or ensure you never add the sentinel by handling the base case.


### Mini walkthrough (memo in action)

Grid:

```
[
  [1, 3, 1],
  [1, 5, 1],
  [4, 2, 1]
]
```

* `dp(2,2) = 1` (destination base)
* `dp(2,1) = 2 + min(dp(2,2)=1, dp(3,1)=INF) = 3`
* `dp(2,0) = 4 + min(dp(2,1)=3, dp(3,0)=INF) = 7`
* `dp(1,2) = 1 + min(dp(1,3)=INF, dp(2,2)=1) = 2`
* `dp(1,1) = 5 + min(dp(1,2)=2, dp(2,1)=3) = 7`
* `dp(1,0) = 1 + min(dp(1,1)=7, dp(2,0)=7) = 8`
* `dp(0,2) = 1 + min(dp(0,3)=INF, dp(1,2)=2) = 3`
* `dp(0,1) = 3 + min(dp(0,2)=3, dp(1,1)=7) = 6`
* `dp(0,0) = 1 + min(dp(0,1)=6, dp(1,0)=8) = 7` ← answer

Memo ensures each `(i,j)` is computed once, turning the recursion into O(rows×cols).
*/
class Solution {
    public int minPathSum(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Integer[][] memo = new Integer[rows][cols];
        return dp(grid, 0, 0, memo);
    }

    // dp(i,j) = min path sum from (i,j) to bottom-right
    private int dp(int[][] grid, int i, int j, Integer[][] memo) {
        int rows = grid.length, cols = grid[0].length;

        // Out of bounds → impossible (use large finite INF to avoid overflow)
        final int INF = 1_000_000_000;
        if (i >= rows || j >= cols) return INF;

        // Destination cell: just its value
        if (i == rows - 1 && j == cols - 1) return grid[i][j];

        if (memo[i][j] != null) return memo[i][j];

        int right = dp(grid, i, j + 1, memo);
        int down  = dp(grid, i + 1, j, memo);

        // At least one of right/down is finite here (thanks to the base case)
        return memo[i][j] = grid[i][j] + Math.min(right, down);
    }
}




// Method 2: Bottom-Up DP Approach
/*
Here’s a clean **bottom-up DP** for **LeetCode 64 — Minimum Path Sum**, plus why it works and a step-by-step walkthrough.

---

# Idea (tabulation)

Let `dp[i][j]` be the **minimum path sum to reach** cell `(i, j)` **from** `(0, 0)` when you can only move **right** or **down**.

Recurrence:

```
dp[i][j] = grid[i][j] + min(
  dp[i-1][j]   // come from above (if i > 0)
  dp[i][j-1]   // come from left  (if j > 0)
)
```

Base row/col:

* First cell: `dp[0][0] = grid[0][0]`
* First row: can only come from the left → prefix sums
* First col: can only come from above → prefix sums

You can implement this with a **1D array** (space O(cols)) by overwriting in place.


### Why this is correct

* When scanning row by row, `dp[j]` holds the min path sum **to the cell above** `(i-1, j)` before update; `dp[j-1]` holds the min path sum **to the left** `(i, j-1)` (already updated in this row).
* We pick the smaller, add `grid[i][j]`, and store back into `dp[j]`.

### Complexity

* **Time:** `O(rows × cols)` (every cell touched once)
* **Space:** `O(cols)` (can do `O(rows)` if you prefer column-wise sweep)

---

# Thorough example walkthrough

Grid:

```
[
  [1, 3, 1],
  [1, 5, 1],
  [4, 2, 1]
]
```

We’ll track `dp` after each cell update.

Initial: `dp = [0, 0, 0]`

**Row 0**

* (0,0): `dp[0] = 1` → `dp = [1, 0, 0]`
* (0,1): first row → `dp[1] = dp[0] + 3 = 1 + 3 = 4` → `dp = [1, 4, 0]`
* (0,2): first row → `dp[2] = dp[1] + 1 = 4 + 1 = 5` → `dp = [1, 4, 5]`

**Row 1**

* (1,0): first col → `dp[0] = dp[0] + 1 = 1 + 1 = 2` → `dp = [2, 4, 5]`
* (1,1): general → `dp[1] = 5 + min(dp[1]=4, dp[0]=2) = 5 + 2 = 7` → `dp = [2, 7, 5]`
* (1,2): general → `dp[2] = 1 + min(dp[2]=5, dp[1]=7) = 1 + 5 = 6` → `dp = [2, 7, 6]`

**Row 2**

* (2,0): first col → `dp[0] = dp[0] + 4 = 2 + 4 = 6` → `dp = [6, 7, 6]`
* (2,1): general → `dp[1] = 2 + min(dp[1]=7, dp[0]=6) = 2 + 6 = 8` → `dp = [6, 8, 6]`
* (2,2): general → `dp[2] = 1 + min(dp[2]=6, dp[1]=8) = 1 + 6 = 7` → `dp = [6, 8, 7]`

Final answer: `dp[2] = 7`.

**Path achieving 7**: `1 → 3 → 1 → 1 → 1` is **not** minimal; the minimal is
`1 → 1 → 5 → 1 → 1`? Let’s check:

* Optimal path is actually `1 → 3 → 1 → 1 → 1` (sum 7) **or** `1 → 1 → 2 → 1 → 1` (sum 6)?
  Careful: valid moves are only right/down. The DP shows minimum is **7**. One achieving path is:
* Right from (0,0) to (0,1): 1 + 3 = 4
* Right to (0,2): 4 + 1 = 5
* Down to (1,2): 5 + 1 = 6
* Down to (2,2): 6 + 1 = **7**
  Another path with 7: (0,0)→(1,0)→(2,0)→(2,1)→(2,2) = 1+1+4+2+1 = 9 (worse), so the right-right-down-down path above is optimal.

> Key takeaway: each `dp[j]` combines “best from above” and “best from left” as you sweep, so by the time you finish, `dp[last]` is the global minimum.

---

## If you prefer a 2D DP (clear but uses O(rows×cols) space)

```java
class Solution2D {
    public int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];

        for (int j = 1; j < n; j++) dp[0][j] = dp[0][j-1] + grid[0][j];
        for (int i = 1; i < m; i++) dp[i][0] = dp[i-1][0] + grid[i][0];

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = grid[i][j] + Math.min(dp[i-1][j], dp[i][j-1]);
            }
        }
        return dp[m-1][n-1];
    }
}
```

Both versions compute the same result; the 1D version just does it with **O(n)** extra space.
*/

// class Solution {
//     public int minPathSum(int[][] grid) {
//         int rows = grid.length, cols = grid[0].length;

//         // dp[j] = min path sum to reach current row's column j
//         int[] dp = new int[cols];

//         for (int i = 0; i < rows; i++) {
//             for (int j = 0; j < cols; j++) {
//                 if (i == 0 && j == 0) {
//                     dp[j] = grid[0][0];
//                 } else if (i == 0) {
//                     // first row: only from left
//                     dp[j] = dp[j - 1] + grid[i][j];
//                 } else if (j == 0) {
//                     // first column: only from above
//                     dp[j] = dp[j] + grid[i][j];
//                 } else {
//                     // general case: min(from above=dp[j], from left=dp[j-1])
//                     dp[j] = grid[i][j] + Math.min(dp[j], dp[j - 1]);
//                 }
//             }
//         }
//         return dp[cols - 1];
//     }
// }
