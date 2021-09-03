class Solution {
public:
//     Method 1: Backtracking
// Base Case: Every problem of backtracking has some base case which tells us at which point we have
// to stop with the recursion process. In our case, when the length of our string has reached the 
// maximum length(n*2), we stop with the recursion for that case and that is our base case.

// Conditions: On observing carefully we find that there are two conditions present:

// For adding (: If number of opening brackets(open) is less than the the given length(n) i.e.
// if max<n, then we can add (,else not.
// For adding ): If number of close brackets(close) is less than the opening brackets(open), i.e.
// if open<close, we can add ), else not
    
    vector<string> generateParenthesis(int n) {
        vector<string> res;
        string s;
        int open = 0;
        int close = 0;
        search(open,close,s,res,n);
        return res;
    }
    
    void search(int open,int close,string s,vector<string> &res,int n){
        if(s.length() == 2*n){
            res.push_back(s);
            return;
        }
        
        if(open < n){
            search(open+1,close,s+'(',res,n);
        }
        if(close < open){
            search(open,close+1,s+')',res,n);
        }
    }
};