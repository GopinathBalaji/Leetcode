// Backtracking
/*
Note that any position in the board can be the starting position. So, this 
gives the hint that only starting from 0,0 is not enough, we need to loop over
the entire board and all four directions for each attempt to cover all starting
positions and possiblities.

Also, another hint is that to prevent using the same cell multiple times in the 
same backtracking attempt, mark cells as visited if we use it and later while
backtracking mark them as unvisited so that other backtracking calls can use it.
*/
class Solution {
    public boolean exist(char[][] board, String word) {
        int rows = board.length;
        int cols = board[0].length;
        boolean[][] visited = new boolean[rows][cols];

        // Try every cell as a starting point
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                // Start DFS if the first character matches
                if(board[i][j] == word.charAt(0)){
                    if(backtrack(board, word, 0, i, j, visited)){
                        return true;
                    }
                }
            }
        }

        return false;  // no path found
    }

    public boolean backtrack(char[][] board, String word, int index, int i, int j, boolean[][] visited){
        // Base case: all characters matched
        if(index == word.length()){
            return true;
        }

        // Out of bounds
        if(i<0 || j<0 || i>=board.length || j>=board[0].length){
            return false;
        }

        // Already visited or wrong character
        if(visited[i][j] || board[i][j] != word.charAt(index)){
            return false;
        }              

        // Choose current cell
        visited[i][j] = true;  
        
        // Explore 4 directions (DFS)
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        for(int[] dir: directions){
            int newI = i + dir[0];
            int newJ = j + dir[1];

            if(backtrack(board, word, index+1, newI, newJ, visited)){
                return true;
            }
        }

        // Backtrack (undo choice)
        visited[i][j] = false;

        return false; // no valid path from this cell
    }
}