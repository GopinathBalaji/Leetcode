class Solution {
    public List<Integer> spiralOrder(int[][] matrix) {
       
        int firstRow = 0;
        int lastCol = matrix[0].length - 1;
        int lastRow = matrix.length - 1;
        int firstCol = 0;

        List<Integer> ans = new ArrayList<>();

        while(firstRow <= lastRow && firstCol <= lastCol){

            for(int j=firstCol;j<=lastCol;j++){
                ans.add(matrix[firstRow][j]);
            }
            
            firstRow += 1;

            for(int i=firstRow;i<=lastRow;i++){
                ans.add(matrix[i][lastCol]);
            }

            lastCol -= 1;

            if(firstRow <= lastRow){
                for(int j=lastCol;j>=firstCol;j--){
                    ans.add(matrix[lastRow][j]);
                }

                lastRow -= 1;
            }

            if(firstCol <= lastCol){
                for(int i=lastRow;i>=firstRow;i--){
                    ans.add(matrix[i][firstCol]);
                }
                
                firstCol += 1;
            }


        }

        return ans;
    }
}