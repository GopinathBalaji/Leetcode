class Solution {
public:
//     Method 1: Follow template of Lynn Zheng
//  Why we need return in the mentioned place in the search function?
//         you have a referenced vector<vector> board, so any changes made to it will persist, now you are 
    //    doing recursion with this board, in your method, there is no return after correct state of board
    // is found, your method will keep making changes to the board,
  
    void solveSudoku(vector<vector<char>>& board) {
        search(board,0,0);    //(board,row,column)
    }
    
    bool isSafeCandidate(vector<vector<char>>& board,int row,int col,char c){
        
        // row check
       for(int i=0;i<9;i++){
           if(board[i][col]==c){
               return false;
           }
       }
        
        // col check
       for(int i=0;i<9;i++){
           if(board[row][i]==c){
               return false;
           }
       }
       
        // each grid check
       int x0 = (row/3)*3;
       int y0 = (col/3)*3;
        
       for(int i=0;i<3;i++){
           for(int j=0;j<3;j++){
               if(board[x0 + i][y0 + j] == c){
                   return false;
               }
           }
       }
        return true;
    }
    
    bool search(vector<vector<char>>& board,int row,int col){
         // done
         if(row==9){                        //
             return true;                   //
         }                                  //
        // time for next row                //
        if(col==9){                         //   isValidState  part from Lynn Zheng BackTracking Template
            return search(board,row+1,0);   //
        }                                   //
        // already marked                   //
        if(board[row][col] != '.'){         //
           return search(board,row,col+1);  //
        }                                   //
        
        for(char c='1';c<='9';c++){
            if(isSafeCandidate(board,row,col,c)){
                board[row][col] = c;
                
        // without return here, the board reverts to initial state
                if(search(board,row,col+1)){
                    return true;
                }
                
                board[row][col] = '.';
            }
        }
        return false;
    }
};


