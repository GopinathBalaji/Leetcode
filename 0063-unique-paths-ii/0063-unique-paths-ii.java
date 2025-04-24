class Solution {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {

        int rows = obstacleGrid.length;
        int cols = obstacleGrid[0].length;

        int[][] memo = new int[rows][cols];

        if (obstacleGrid[0][0] == 1){ 
            return 0;
        }
        memo[0][0] = 1;

        boolean flag = false;
        for(int i=1;i<cols;++i){
            if(obstacleGrid[0][i] == 1){
                memo[0][i] = 0;
                flag = true;
            } else if(flag){
                memo[0][i] = 0;
            } else{
                memo[0][i] = 1;
            }
        }

        flag = false;
        for(int i=1;i<rows;++i){
            if(obstacleGrid[i][0] == 1){
                memo[i][0] = 0;
                flag = true;
            } else if(flag){
                memo[i][0] = 0;
            } else {
                memo[i][0] = 1;
            }
        }

        for(int row=1;row<rows;++row){
            for(int col=1;col<cols;++col){
                if(obstacleGrid[row][col] != 1){
                    memo[row][col] = memo[row][col-1] + memo[row-1][col];
                } else{
                    memo[row][col] = 0;
                }
            }
        }

        return memo[rows-1][cols-1];
    }
        
}
