// Method 1: Top-Down DP
/*
### Why this works

* **0 for impossible states**: sums stay correct because paths are counts.
* **Base cases**:

  * out-of-bounds/obstacle ⇒ 0 paths;
  * at target ⇒ 1 path.
* **Memoization**: each cell’s count computed once ⇒ O(m·n) time, O(m·n) space.

### Quick walkthrough

Grid:

```
[ [0,0,0],
  [0,1,0],
  [0,0,0] ]
```

* `dfs(2,2)=1` (goal)
* `dfs(2,1)= dfs(2,2)+dfs(3,1)=1+0=1`
* `dfs(2,0)= dfs(2,1)+dfs(3,0)=1+0=1`
* `dfs(1,2)= dfs(1,3)+dfs(2,2)=0+1=1`
* `dfs(1,1)= obstacle ⇒ 0`
* `dfs(1,0)= dfs(1,1)+dfs(2,0)=0+1=1`
* `dfs(0,2)= dfs(0,3)+dfs(1,2)=0+1=1`
* `dfs(0,1)= dfs(0,2)+dfs(1,1)=1+0=1`
* `dfs(0,0)= dfs(0,1)+dfs(1,0)=1+1=2` 
*/
class Solution {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int rows = obstacleGrid.length;
        int cols = obstacleGrid[0].length;

        Integer[][] memo = new Integer[rows][cols];

        return dp(obstacleGrid, memo, rows, cols, 0, 0);
    }

    private int dp(int[][] obstacleGrid, Integer[][] memo, int rows, int cols, int i, int j){
        if(i < 0 || i >= rows || j < 0 || j >= cols){
            return 0;
        }
        if(obstacleGrid[i][j] == 1){
            return 0;
        }

        if(i == rows - 1 && j == cols - 1){
            return 1;
        }

        if(memo[i][j] != null){
            return memo[i][j];
        }

        return memo[i][j] = dp(obstacleGrid, memo, rows, cols, i, j+1) + dp(obstacleGrid, memo, rows, cols, i+1, j);
    }
}




// Method 2: 1-D Bottom-Up DP (O(n) Space)
/*
# Idea (tabulation with 1D DP)

We scan the grid row by row, keeping a 1D array `dp[j]` where:

* `dp[j]` = number of ways to reach the cell in the **current row** at column `j`.
* When we move across a row, `dp[j]` already holds “ways from **above**” (same column, previous row), and `dp[j-1]` holds “ways from the **left**” (already updated in this row).
* If a cell is an obstacle, ways to it are `0` (we “block” the path).

**Transition for a free cell `(i, j)`**:

```
dp[j] = dp[j] (from above) + dp[j-1] (from left)
```

**Obstacle cell**:

```
dp[j] = 0
```

We initialize the start `dp[0]` to `1` **only** if the start is not blocked.

## Why this is correct

* We process left→right within each row:

  * `dp[j]` (before update) is the number of ways from **above**.
  * `dp[j-1]` (already updated) is the number of ways from the **left** in the current row.
* If a cell is an obstacle, we zero `dp[j]` so it won’t contribute to later cells in that row or below.
* Space is `O(n)` for `n = number of columns`.

**Time:** `O(m·n)`
**Space:** `O(n)`

---

# Thorough example walkthrough

Grid:

```
[
  [0, 0, 0],
  [0, 1, 0],
  [0, 0, 0]
]
```

`0` = free, `1` = obstacle.
Answer should be `2`.

Initialize:

```
dp = [1, 0, 0]   // start cell is free → dp[0]=1
```

### Row 0 (i = 0): [0, 0, 0]

* j = 0, free:

  * j==0 → only from above (which is dp[0] itself) → stays 1
  * dp = [1, 0, 0]

* j = 1, free:

  * dp[1] += dp[0] → 0 + 1 = 1
  * dp = [1, 1, 0]

* j = 2, free:

  * dp[2] += dp[1] → 0 + 1 = 1
  * dp = [1, 1, 1]

Interpretation after row 0: 1 way to each cell in the first row (only moving right).

### Row 1 (i = 1): [0, 1, 0]

* j = 0, free:

  * first column → dp[0] stays as “from above”: 1
  * dp = [1, 1, 1]

* j = 1, **obstacle**:

  * dp[1] = 0   // block paths through this cell
  * dp = [1, 0, 1]

* j = 2, free:

  * dp[2] += dp[1] → 1 + 0 = 1
  * dp = [1, 0, 1]

Interpretation after row 1: there’s **no** path through the middle; only the upper route survives to column 2.

### Row 2 (i = 2): [0, 0, 0]

* j = 0, free:

  * first column → dp[0] stays as “from above”: 1
  * dp = [1, 0, 1]

* j = 1, free:

  * dp[1] += dp[0] → 0 + 1 = 1
  * dp = [1, 1, 1]

* j = 2, free:

  * dp[2] += dp[1] → 1 + 1 = 2
  * dp = [1, 1, 2]

Final answer = `dp[last] = 2`.

**Intuition:** There are exactly 2 valid routes that avoid the obstacle:

* Right → Right → Down → Down
* Right → Down → Down → Right

---

## Common edge cases

* **Start blocked (`obstacleGrid[0][0] == 1`)** → return 0 (we handle via `dp[0]=0`).
* **End blocked (`obstacleGrid[m-1][n-1] == 1`)** → result becomes 0 naturally.
* **First row/column obstacles:** once `dp[j]` (or `dp[0]`) becomes 0 due to an obstacle, cells to its right in that row (or below in that column) can only be reached from the other direction, which the recurrence handles.

That’s the full O(n)-space solution with the reasoning and a concrete trace.

*/

// class Solution {
//     public int uniquePathsWithObstacles(int[][] obstacleGrid) {
//         int m = obstacleGrid.length, n = obstacleGrid[0].length;

//         int[] dp = new int[n];
//         // Start cell
//         dp[0] = (obstacleGrid[0][0] == 1) ? 0 : 1;

//         for (int i = 0; i < m; i++) {
//             for (int j = 0; j < n; j++) {

//                 if (obstacleGrid[i][j] == 1) {
//                     // Blocked cell: no paths end here
//                     dp[j] = 0;
//                 } else if (j > 0) {
//                     // Free cell: ways from above (dp[j]) + from left (dp[j-1])
//                     dp[j] += dp[j - 1];
//                 }
//                 // Note: if j == 0 and cell is free, dp[0] already equals
//                 // "ways from above"; there's no left neighbor in first column.
//             }
//         }
//         return dp[n - 1];
//     }
// }
