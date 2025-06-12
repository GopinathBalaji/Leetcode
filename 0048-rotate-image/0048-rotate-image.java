class Solution {
    public void rotate(int[][] matrix) {
        int temp = 0;
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length;j++){
                if(j > i){
                    temp = matrix[i][j];
                    matrix[i][j] = matrix[j][i];
                    matrix[j][i] = temp;
                }
            }
        }

        Explanation:


        for(int i=0;i<matrix.length;i++){
            int left = 0;
            int right = matrix[i].length - 1;

            while(left < right){
                int temp2 = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp2;

                left++;
                right--;
            }
        }

    }
}