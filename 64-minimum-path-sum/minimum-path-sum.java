// Bottom-Up
// class Solution {
//     public int minPathSum(int[][] grid) {
//         int m = grid.length;
//         int n = grid[0].length;

//         int[][] dp = new int[m][n];
//         dp[0][0] = grid[0][0];

//         for(int c=1;c<n;++c){
//             dp[0][c] = dp[0][c-1] + grid[0][c];
//         }

//         for(int r=1;r<m;++r){
//             dp[r][0] = dp[r-1][0] + grid[r][0];
//         }

//         for(int r=1;r<m;++r){
//             for(int c=1;c<n;++c){
//                 dp[r][c] = Math.min(dp[r-1][c], dp[r][c-1]) + grid[r][c];
//             }
//         }

//         return dp[m-1][n-1];

//     }
// }

// Top-Down Recursive
class Solution {
    public int minPathSum(int[][] grid) {
        int m = grid.length; 
        int n = grid[0].length; 
        int[][] memo = new int[m][n];

        for (int[] row : memo){
        Arrays.fill(row, -1);
        }

        return dfs(m - 1, n - 1, grid, memo);   
    }

    public int dfs(int i, int j, int[][] grid, int[][] memo){
        if(i==0 && j==0){
            return grid[0][0];
        }

        if(i<0 || j<0){
            return Integer.MAX_VALUE;
        }

        if(memo[i][j] != -1){
            return memo[i][j];
        }

        int up = dfs(i-1,j,grid,memo);
        int left = dfs(i,j-1,grid,memo);

        memo[i][j] = grid[i][j] + Math.min(up,left);

        return memo[i][j];
    }
}