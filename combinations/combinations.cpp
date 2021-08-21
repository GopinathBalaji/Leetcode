class Solution {
public:
//     Method 1: Lynn Zheng's Backtracking template
    vector<vector<int>> combine(int n, int k) {
        vector<vector<int>> res;
        vector<int> v;
        search(1,n,k,res,v);
        return res;
    }
    
    bool isValidState(vector<int> &v,int k){
         if(v.size()==k){
             return true;
         }
        return false;
    }
    
    bool isSafeCandidate(int b,int n){
        if(b<=n){
            return true;
        }
        return false;
    }
    
    void search(int start,int n,int k,vector<vector<int>> &res,vector<int> &v){
        if(isValidState(v,k)){
            res.push_back(v);
            return;
        }
        
        for(int i=start;i<=n;i++){
            if(isSafeCandidate(i,n)){
                v.push_back(i);
                search(i+1,n,k,res,v);
                v.pop_back();
            }
        }
    }
    
};