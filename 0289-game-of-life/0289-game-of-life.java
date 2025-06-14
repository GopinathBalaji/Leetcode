// Constant space GPT solution
// | Old Value | New Value | Temporary Marker |
// | --------- | --------- | ---------------- |
// | 0         | 0         | 0                |
// | 1         | 1         | 1                |
// | 1         | 0         | 3                |
// | 0         | 1         | 2                |
// 3 means "was alive, now dead"
// 2 means "was dead, now alive"


class Solution {
    public void gameOfLife(int[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        // Directions for 8 neighbors
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
        };

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                int liveNeighbors = 0;

                for(int[] dir : directions){
                    int ni = i + dir[0];
                    int nj = j + dir[1];

                    if(ni >= 0 && ni < rows && nj >= 0 && nj < cols){
                        if(board[ni][nj] == 1 || board[ni][nj] == 3){
                            liveNeighbors++;
                        }
                    }
                }

                if(board[i][j] == 1){
                    if(liveNeighbors < 2 || liveNeighbors > 3){
                        board[i][j] = 3;
                    }
                }else{
                    if(liveNeighbors == 3){
                        board[i][j] = 2;
                    }
                }
            }
        }

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(board[i][j] == 2){
                    board[i][j] = 1;
                }else if(board[i][j] == 3){
                    board[i][j] = 0;
                }
            }
        }
    }
}







// My solution with extra O(m*n) space
// class Solution {
//     public void gameOfLife(int[][] board) {

//         int[][] copy = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);

//         int vertLimit = board.length;
//         int horiLimit = board[0].length;

//         for (int i = 0; i < vertLimit; i++) {
//             for (int j = 0; j < horiLimit; j++) {
//                 int liveCount = 0;

//                 // Iterate over all 8 directions
//                 for (int x = -1; x <= 1; x++) {
//                     for (int y = -1; y <= 1; y++) {
//                         if (x == 0 && y == 0) continue; // skip the current cell itself

//                         int ni = i + x;
//                         int nj = j + y;

//                         if (ni >= 0 && ni < vertLimit && nj >= 0 && nj < horiLimit) {
//                             if (copy[ni][nj] == 1) {
//                                 liveCount++;
//                             }
//                         }
//                     }
//                 }

//                 // Apply Game of Life rules
//                 if (copy[i][j] == 1) {
//                     if (liveCount < 2 || liveCount > 3) {
//                         board[i][j] = 0;
//                     } else {
//                         board[i][j] = 1;
//                     }
//                 } else {
//                     if (liveCount == 3) {
//                         board[i][j] = 1;
//                     }
//                 }
//             }
//         }
//     }
// }
