class Solution {
    public boolean checkValid(int[][] matrix) {
        HashSet<Integer> set = new HashSet<>();
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length;j++){
                if(matrix[i][j] >= 1 && matrix[i][j] <= matrix.length){
                    set.add(matrix[i][j]);
                }
            }
            if(set.size() != matrix.length){
                    return false;
            }
            set.clear();
        }
        

        for(int j=0;j<matrix[0].length;j++){
            for(int i=0;i<matrix.length;i++){
                if(matrix[i][j] >= 1 && matrix[i][j] <= matrix.length){
                    set.add(matrix[i][j]);
                }                
            }
            if(set.size() != matrix.length){
                    return false;
            }
            set.clear();            
        }

        return true;
    }
}