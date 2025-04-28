class Solution {
    public int maximalSquare(char[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] memo = new int[rows][cols];

        int best = 0;
        for(int i=0;i<cols;i++){
            memo[0][i] = matrix[0][i] == '1' ? 1 : 0;
            best = Math.max(best, memo[0][i]);
        }

        for(int j=0;j<rows;j++){
            memo[j][0] = matrix[j][0] == '1' ? 1 : 0;
            best = Math.max(best, memo[j][0]);
        }

        for(int i=1;i<rows;i++){
            for(int j=1;j<cols;j++){
                if(matrix[i][j] == '1'){
                memo[i][j] = Math.min(memo[i-1][j], Math.min(memo[i][j-1], memo[i-1][j-1])) + 1;
                best = Math.max(best, memo[i][j]);
                } else{
                    memo[i][j] = 0;
                }
            }
        }

        return best * best;
    }
}