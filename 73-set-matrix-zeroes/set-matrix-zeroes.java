// GPT solution using O(1) space
class Solution {
public void setZeroes(int[][] matrix) {
        boolean rowZero = false;
        boolean colZero = false;

        int m = matrix.length;
        int n = matrix[0].length;

        for(int i=0;i<m;i++){
            if(matrix[i][0] == 0){
                colZero = true;
            }
        }

        for(int j=0;j<n;j++){
            if(matrix[0][j] == 0){
                rowZero = true;
            }
        }

        for(int i=1;i<m;i++){
            for(int j=1;j<n;j++){
                if(matrix[i][j] == 0){
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
            }
        }

        for(int i=1;i<m;i++){
            for(int j=1;j<n;j++){
                if(matrix[i][0] == 0 || matrix[0][j] == 0){
                    matrix[i][j] = 0;
                }
            }
        }

        if(colZero){
            for(int i=0;i<m;i++){
                matrix[i][0] = 0;
            }
        }

        if(rowZero){
            for(int j=0;j<n;j++){
                matrix[0][j] = 0;
            }
        }

    }
}



// My O(m + n) space solution using Sets

// class Solution {
//     public void setZeroes(int[][] matrix) {
//         Set<Integer> setRow = new HashSet<>();
//         Set<Integer> setCol = new HashSet<>();


//         for(int i=0;i<matrix.length;i++){
//             for(int j=0;j<matrix[0].length;j++){
//                 if(matrix[i][j] == 0){
//                     setRow.add(i);
//                     setCol.add(j);
//                 }
//             }
//         }

//         for(int i=0;i<matrix.length;i++){
//             if(setRow.contains(i)){
//                 for(int j=0;j<matrix[0].length;j++){
//                     matrix[i][j] = 0;
//                 }
//             }
//         }

//         for(int i=0;i<matrix.length;i++){
//             for(int j=0;j<matrix[0].length;j++){
//                 if(setCol.contains(j)){
//                     matrix[i][j] = 0;
//                 }
//             }
//         }


//     }
// }