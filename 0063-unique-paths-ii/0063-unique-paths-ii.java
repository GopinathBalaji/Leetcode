// My Tow Down Approach
/*
The recursion tree for the first example:
dp(0,0)
├─ dp(0,1)
│  ├─ dp(0,2)
│  │  ├─ dp(0,3) → 0 (oob)
│  │  └─ dp(1,2)
│  │     ├─ dp(1,3) → 0 (oob)
│  │     └─ dp(2,2) → 1 (goal; memo[2][2]=1)
│  │     → memo[1][2] = 1
│  │  → memo[0][2] = 1
│  └─ dp(1,1) → 0 (obstacle)
│  → memo[0][1] = 1 + 0 = 1
│
└─ dp(1,0)
   ├─ dp(1,1) → 0 (obstacle)
   └─ dp(2,0)
      ├─ dp(2,1)
      │  ├─ dp(2,2) → 1 (memo hit)
      │  └─ dp(3,1) → 0 (oob)
      │  → memo[2][1] = 1
      └─ dp(3,0) → 0 (oob)
      → memo[2][0] = 1
   → memo[1][0] = 0 + 1 = 1

→ memo[0][0] = dp(0,1=1) + dp(1,0=1) = 2

Think of “how many ways are there from this cell to the finish?” as your central question, and cache the answers so you never recompute the same sub‐problem twice.

---

### 1) Define the DP state

```
dp(r,c) = number of unique obstacle-free paths from grid cell (r,c) to the bottom‐right corner  
```

where `(r,c)` runs from `0 ≤ r < m`, `0 ≤ c < n`.

---

### 2) Identify your base cases

1. **Out of bounds**
   If `r ≥ m` or `c ≥ n`, you’ve stepped off the grid—there are **0** ways from an invalid cell.

2. **Obstacle cell**
   If `obstacleGrid[r][c] == 1`, you can’t stand here—so **0** ways.

3. **Goal cell**
   If `(r,c)` is exactly `(m-1,n-1)` and it’s not blocked, you’ve arrived—there’s exactly **1** “empty” path (you’re already there).

---

### 3) Write the recurrence

From any free, in-bounds, non-goal cell, you have two moves available (down or right), so

```
dp(r,c) = dp(r+1, c)   +   dp(r, c+1)  
```

—sum the number of ways from the cell below and the cell to the right.

---

### 4) Use memoization to avoid exponential blow-up

A naïve recursion from `(0,0)` would branch into two calls at each step, revisiting the same `(r,c)` over and over (exponential time). Instead:

* Allocate a 2D array `memo[m][n]`, initialized to an “uncomputed” sentinel (e.g. `-1`).
* In `dp(r,c)`, **first** check `memo[r][c]`; if it isn’t `-1`, return it immediately.
* Otherwise compute `dp(r,c)` by applying the base cases or recurrence, store it in `memo[r][c]`, and return.

This guarantees **each** distinct `(r,c)` is computed at most once. Every subsequent request for that cell’s answer is a constant-time lookup.

---

### 5) Top-down flow

1. Call `dp(0,0)`.
2. If `(0,0)` is blocked, you immediately return 0. Otherwise you recurse:

   * into `dp(1,0)` (downward)
   * and into `dp(0,1)` (rightward)
3. Each of those recurses similarly, hitting base cases at obstacles, out-of-bounds, or the goal.
4. Whenever you compute a non-base result, you store it in `memo[r][c]`.
5. As the recursion unwinds, memo hits cut off entire sub-trees, and your final return from `dp(0,0)` is the total number of valid paths.

---

### 6) Complexity

* **Time:** You have at most `m×n` states, each doing O(1) work once memoized → **O(m·n)**.
* **Space:** The memo table is O(m·n), and the recursion stack is O(m+n) deep in the worst case.

---

### Why this perspective helps

* You never need to think about the order in which you fill rows or columns—you simply ask “how many from here?” and let recursion handle the dependencies.
* Memoization transforms what would be an exponential tree of calls into a directed acyclic graph of at most `m·n` nodes.
* It’s especially natural when a problem’s rule (“you can move only right or down”) gives a small, fixed set of next-states from each cell.

*/

class Solution {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length;
        int n = obstacleGrid[0].length;
        int[][] memo = new int[m][n];

        for(int[] row : memo){
            Arrays.fill(row, -1);
        }
       
        return dp(obstacleGrid, memo, m, n, 0, 0);
    }

    public int dp(int[][] obstacleGrid, int[][] memo, int m, int n, int row, int col){
        if(row > m-1 || col > n-1){
            return 0;
        }
        if(obstacleGrid[row][col] == 1){
            return 0;
        }
        if(memo[row][col] != -1){
            return memo[row][col];
        }

        if(row == m-1 && col == n-1 && obstacleGrid[row][col] != 1){
            return 1; 
        }

        memo[row][col] = dp(obstacleGrid, memo, m, n, row, col + 1) + dp(obstacleGrid, memo, m, n, row + 1, col);

        return memo[row][col];
    }
}

// Bottom Up DP
/*
Step-by-Step Explanation
DP State
Define a 2D array dp of the same size as obstacleGrid, where


dp[i][j] = number of unique paths from start (0,0) to cell (i,j),
           moving only down or right, without stepping on any obstacles.
Initialization

dp[0][0]

If the start cell (0,0) is free (obstacleGrid[0][0] == 0), there is exactly 1 way to “start” there.

If it’s blocked, there are 0 ways, and the entire result will be 0.

First Row and First Column

First row (i = 0):
You can only reach (0,j) from (0,j-1). If either (0,j) has an obstacle or (0,j-1) was unreachable (dp[0][j-1] == 0), then dp[0][j] = 0; otherwise

dp[0][j] = dp[0][j-1];
First column (j = 0):
Similarly, you can only come down from (i-1,0):

dp[i][0] = (obstacleGrid[i][0] == 0) ? dp[i-1][0] : 0;
General Recurrence
For each cell (i,j) with i≥1, j≥1:

If it’s an obstacle (obstacleGrid[i][j] == 1), set dp[i][j] = 0—no paths go through here.

Otherwise, you can arrive via two moves:

From above (i-1,j) → dp[i-1][j] ways

From left (i,j-1) → dp[i][j-1] ways

Sum them:

dp[i][j] = dp[i-1][j] + dp[i][j-1];
Result
After filling all cells, dp[m-1][n-1] contains the total number of obstacle-free paths from the top-left to the bottom-right.

Complexity
Time: We visit each of the m × n cells exactly once, doing O(1) work per cell → O(m·n).

Space: We allocate an m × n DP table → O(m·n) extra space.

Optional Optimization to O(n) Space
Because dp[i][j] depends only on the same row’s left neighbor and the previous row’s same column, you can collapse down to a single 1D array of length n. At each new row, you update:


int[] dp = new int[n];
dp[0] = (obstacleGrid[0][0] == 0) ? 1 : 0;
for (int j = 1; j < n; j++) {
    dp[j] = obstacleGrid[0][j] == 0 ? dp[j-1] : 0;
}

for (int i = 1; i < m; i++) {
    // First column update
    dp[0] = (obstacleGrid[i][0] == 0) ? dp[0] : 0;
    for (int j = 1; j < n; j++) {
        dp[j] = obstacleGrid[i][j] == 0
                ? dp[j] + dp[j-1]    // dp[j] is “up”, dp[j-1] is “left”
                : 0;
    }
}
return dp[n-1];

This still runs in O(m·n) time but uses only O(n) additional space.
*/
// class Solution {
//     public int uniquePathsWithObstacles(int[][] obstacleGrid) {
//         int m = obstacleGrid.length;
//         int n = obstacleGrid[0].length;
//         // dp[i][j] = # of ways to reach cell (i,j) from (0,0)
//         int[][] dp = new int[m][n];
        
//         // 1) Base case: starting cell
//         // If there's no obstacle at (0,0), there's exactly 1 way to be there.
//         dp[0][0] = (obstacleGrid[0][0] == 0) ? 1 : 0;
        
//         // 2) First row: you can only come from the left
//         for (int j = 1; j < n; j++) {
//             // If this cell is free and the cell to the left is reachable,
//             // you inherit that many ways; otherwise 0.
//             if (obstacleGrid[0][j] == 0 && dp[0][j - 1] > 0) {
//                 dp[0][j] = dp[0][j - 1];
//             } else {
//                 dp[0][j] = 0;
//             }
//         }
        
//         // 3) First column: you can only come from above
//         for (int i = 1; i < m; i++) {
//             if (obstacleGrid[i][0] == 0 && dp[i - 1][0] > 0) {
//                 dp[i][0] = dp[i - 1][0];
//             } else {
//                 dp[i][0] = 0;
//             }
//         }
        
//         // 4) Fill in the rest of the grid
//         //    For each free cell, sum the ways from above and from the left.
//         //    If there's an obstacle, leave dp[i][j] = 0.
//         for (int i = 1; i < m; i++) {
//             for (int j = 1; j < n; j++) {
//                 if (obstacleGrid[i][j] == 0) {
//                     dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
//                 } else {
//                     dp[i][j] = 0;
//                 }
//             }
//         }
        
//         // 5) The bottom-right corner holds the total number of unique paths
//         return dp[m - 1][n - 1];
//     }
// }
