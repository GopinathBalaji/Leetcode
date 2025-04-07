class Solution {
    private int rows;
    private int cols;
    private char[][] gameboard;

    public boolean dfs(int row, int col, int index, String word){
        if(index == word.length() - 1){
            return gameboard[row][col] == word.charAt(index);
        }

        if(gameboard[row][col] != word.charAt(index)){
            return false;
        }

        char temp = gameboard[row][col];
        gameboard[row][col] = '0';

        int[] directions = {-1,0,1,0,-1};

        for(int d=0;d<4;d++){
            int newrow = row + directions[d];
            int newcol = col + directions[d+1];

            if(newrow >=0 && newrow < rows && newcol >=0 && newcol < cols && gameboard[newrow][newcol] != '0'){
                if(dfs(newrow,newcol,index+1,word)){
                    return true;
                }
            }
        }
        gameboard[row][col] = temp;

        return false;

    }


    public boolean exist(char[][] board, String word) {
        gameboard = board;
        rows = board.length;
        cols = board[0].length;

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(dfs(i,j,0,word)){
                    return true;
                }
            }
        }

        return false;
    }
}