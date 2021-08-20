class Solution {
public:
    int totalNQueens(int n) {
        vector<vector<string>> res;
        vector<string> v(n, string(n, '.'));
        search(0,n,res,v);
        return res.size();
    }
    
    void search(int col,int n,vector<vector<string>> &res,vector<string> &v){
        if(isValidState(col,n)){
            res.push_back(v);
            return;
        }
        
        for(int row=0;row<n;row++){
            if(isSafeCandidate(row,col,n,v) == true){
                v[row][col] = 'Q';
                search(col+1,n,res,v);
                v[row][col] = '.';
            }
        }
    }
    
    bool isValidState(int col,int n){
        return col==n;
    }
    
    bool isSafeCandidate(int row,int col,int n,vector<string> &v){
        for(int i=0;i<col;i++){
            if(v[row][i] == 'Q'){
                return false;
            }
        }
        
        for(int i=row,j=col;i>=0 and j>=0; i--,j--){
            if(v[i][j]=='Q'){
                return false;
            }
        }
        
        for(int i=row,j=col;i<n and j>=0; i++,j--){
            if(v[i][j]=='Q'){
                return false;
            }
        }
        
        return true;
    }
};