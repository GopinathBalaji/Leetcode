// Top Down DP
/*
The recursion tree for the first example:
dp(0,0)
└─ calls dp(1,0)
   └─ calls dp(2,0)
      ├─ calls dp(3,0)       → ∞ (oob)
      └─ calls dp(2,1)
         ├─ calls dp(3,1)    → ∞ (oob)
         └─ calls dp(2,2)    → 1 (base; memo[2][2]=1)
         └─ dp(2,1)=2+1=3    (memo[2][1]=3)
      └─ dp(2,0)=4+3=7       (memo[2][0]=7)
   └─ calls dp(1,1)
      ├─ calls dp(2,1)       → 3 (memo hit)
      └─ calls dp(1,2)
         ├─ calls dp(2,2)    → 1 (memo hit)
         └─ calls dp(1,3)    → ∞ (oob)
         └─ dp(1,2)=1+1=2    (memo[1][2]=2)
      └─ dp(1,1)=5+2=7       (memo[1][1]=7)
   └─ dp(1,0)=1+7=8          (memo[1][0]=8)

└─ calls dp(0,1)
   ├─ calls dp(1,1)          → 7 (memo hit)
   └─ calls dp(0,2)
      ├─ calls dp(1,2)       → 2 (memo hit)
      └─ calls dp(0,3)       → ∞ (oob)
      └─ dp(0,2)=1+2=3        (memo[0][2]=3)
   └─ dp(0,1)=3+3=6           (memo[0][1]=6)

└─ dp(0,0)=1+6=7              (memo[0][0]=7)

*/
class Solution {
    public int minPathSum(int[][] grid) {
        int[][] memo = new int[grid.length][grid[0].length];
        for(int[] row : memo){
            Arrays.fill(row, -1);
        }
        int m = grid.length;
        int n = grid[0].length;
        return dp(grid, memo, m, n, 0, 0);
    }

    public int dp(int[][] grid, int[][] memo, int m, int n, int row, int col){
        if(row > m-1 || col > n-1){
            return Integer.MAX_VALUE;
        }
        if(memo[row][col] != -1){
            return memo[row][col];
        }
        if(row == m-1 && col == n-1){
            return grid[row][col];
        }

        int val = grid[row][col];
        int bestVal = Math.min(dp(grid, memo, m, n, row + 1, col), dp(grid, memo, m, n, row, col + 1));

        if(bestVal == Integer.MAX_VALUE){
            memo[row][col] = Integer.MAX_VALUE;
        }else{
            memo[row][col] = bestVal + val;
        }

        return memo[row][col];
    }
}

// Bottom Up DP
/*
## Detailed Explanation

1. **State definition**

   * We build a 2D table `dp` of the same dimensions as `grid`.
   * **`dp[i][j]`** represents the minimum sum of any path from the **top-left** `(0,0)` to **cell** `(i,j)`, moving only **down** or **right**.

2. **Initialization**

   * **`dp[0][0] = grid[0][0]`**: the cost to “reach” the starting cell is its own value.
   * **First row** (`i = 0`): you can only move right from the previous cell, so

     ```
     dp[0][j] = dp[0][j-1] + grid[0][j];
     ```
   * **First column** (`j = 0`): you can only move down from above, so

     ```
     dp[i][0] = dp[i-1][0] + grid[i][0];
     ```

3. **Recurrence**
   For every other cell `(i,j)` (with `i>0` and `j>0`), you have two ways to arrive:

   * From **above**: the best cost to `(i-1,j)` is `dp[i-1][j]`.
   * From **left**: the best cost to `(i,j-1)` is `dp[i][j-1]`.

   You choose the cheaper of those two entry‐paths, then add the cost of the current cell:

   ```
   dp[i][j] = grid[i][j] + min( dp[i-1][j], dp[i][j-1] )
   ```

4. **Filling order**

   * You must compute row 0 fully, then column 0, before you can compute any `(i,j)`.
   * After initializing the first row and column, you iterate `i = 1…m-1` and inside that `j = 1…n-1`.
   * By the time you reach `(i,j)`, both `dp[i-1][j]` and `dp[i][j-1]` are already computed.

5. **Result**

   * Once the table is filled, `dp[m-1][n-1]` holds the minimum sum of an allowed path from the top-left to the bottom-right.

---

### Complexity

* **Time:** You visit each of the `m×n` cells exactly once, doing O(1) work per cell → **O(m·n)**.
* **Space:** You allocate an `m×n` DP table → **O(m·n)** extra space.

---

### Space‐Optimized Variant (O(n) space)

You can notice that each `dp[i][j]` depends only on the **same row’s** left neighbor and the **previous row’s** same column. You can therefore collapse to a single 1D array of length `n`:

```java
public int minPathSum(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    int[] dp = new int[n];
    dp[0] = grid[0][0];
    for (int j = 1; j < n; j++) {
        dp[j] = dp[j - 1] + grid[0][j];
    }
    for (int i = 1; i < m; i++) {
        dp[0] += grid[i][0];  // first column update
        for (int j = 1; j < n; j++) {
            dp[j] = grid[i][j] + Math.min(dp[j], dp[j - 1]);
        }
    }
    return dp[n - 1];
}
```

* Here **`dp[j]`** always holds the min‐sum for the **current row** at column `j`, after updating from left (`dp[j-1]`) and (old) up (`dp[j]`).
* This reduces extra space from O(m·n) to **O(n)**, while still running in **O(m·n)** time.

*/
// class Solution {
//     public int minPathSum(int[][] grid) {
//         int m = grid.length, n = grid[0].length;
//         // dp[i][j] = minimum path‐sum from (0,0) to (i,j)
//         int[][] dp = new int[m][n];
        
//         // 1) Base case: start at the top‐left corner
//         dp[0][0] = grid[0][0];
        
//         // 2) Fill in the first row (can only come from the left)
//         for (int j = 1; j < n; j++) {
//             dp[0][j] = dp[0][j - 1] + grid[0][j];
//         }
        
//         // 3) Fill in the first column (can only come from above)
//         for (int i = 1; i < m; i++) {
//             dp[i][0] = dp[i - 1][0] + grid[i][0];
//         }
        
//         // 4) Fill in the rest of the table
//         //    At each cell (i,j), you can arrive either from above (i-1,j)
//         //    or from the left (i,j-1); pick the cheaper path and add grid[i][j].
//         for (int i = 1; i < m; i++) {
//             for (int j = 1; j < n; j++) {
//                 dp[i][j] = grid[i][j] + Math.min(dp[i - 1][j], dp[i][j - 1]);
//             }
//         }
        
//         // 5) The answer is the min‐sum to reach the bottom‐right corner
//         return dp[m - 1][n - 1];
//     }
// }
