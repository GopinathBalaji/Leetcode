class Solution {
public:
//   Method 1: Mark visited letters then change them back while backtracking
    
    bool exist(vector<vector<char>>& board, string word) {
        int row = board.size();
        int col = board[0].size();
        int index = 0;
        
        for(int i=0;i<board.size();i++){
            for(int j=0;j<board[i].size();j++){
                if(board[i][j]==word[0] && solve(i,j,row,col,board,word,index)){
                    return true;         // First condition is to just speed up the process
                }
            }
        }
        return false;
    }
    
    bool solve(int i,int j,int row,int col,vector<vector<char>>& board,string &word,int index){
        if(i<0 || j<0 || i>=row || j>=col || word[index] != board[i][j]){
            return false;
        }
        
        if(index == word.length()-1){
            return true;
        }
        
        board[i][j] = '0'; // to avoid using the character in this position again
        if( solve(i+1,j,row,col,board,word,index+1) || solve(i,j+1,row,col,board,word,index+1) || solve(i-1,j,row,col,board,word,index+1) || solve(i,j-1,row,col,board,word,index+1) ){
            return true;      // checking (bottom,right,top,left)
        }
        board[i][j] = word[index];
        
        return false;
    }
    
};                            

// Method 2: Similar Method but using a visited vector to mark visited letters
   /*
     int n, m;
    vector<vector<bool>> vis;
    bool dfs(vector<vector<char>> &board, string word, int i, int j, int index)
    {
        if(board[i][j] == word[index])
        {
            vis[i][j] = true;
            if(index == word.length() - 1)
                return true;
            if(i-1 >= 0 && vis[i-1][j] == false)
                if(dfs(board, word, i-1, j, index+1))
                    return true;
            if(i+1 < n && vis[i+1][j] == false)
                if(dfs(board, word, i+1, j, index+1))
                    return true;
            if(j-1 >= 0 && vis[i][j-1] == false)
                if(dfs(board, word, i, j-1, index+1))
                    return true;
            if(j+1 < m && vis[i][j+1] == false)
                if(dfs(board, word, i, j+1, index+1))
                    return true;
            vis[i][j] = false;
            return false;
        }
        return false;
    }
public:
    bool exist(vector<vector<char>>& board, string word) {
        n = board.size();
        m = board[0].size();
        vis = vector<vector<bool>>(n, vector<bool>(m, false));
        for(int i=0; i<n; i++)
            for(int j=0; j<m; j++)
            {
                if(dfs(board, word, i, j, 0))
                    return true;
            }
        return false;
    }
   */