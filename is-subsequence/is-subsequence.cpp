class Solution {
public:
    bool isSubsequence(string s, string t) {
//         Method 1: Check from last letter using tail recursion
        int m = s.length(); 
        int n = t.length();
       return recursubseq(s,t,m,n);
    }
    
    bool recursubseq(string s,string t,int m,int n){
        if(m==0){
            return true;
        }
        if(n==0){
            return false;
        }
        
        if(s[m-1]==t[n-1]){
            return recursubseq(s,t,m-1,n-1);
        }
        return recursubseq(s,t,m,n-1);
    }
};  

// Method 2: Two Pointer
   /*
     bool isSubsequence(string s, string t){
        int m = s.length(); 
        int n = t.length();
       return subseq(s,t,m,n);
     }
     
     bool subseq(string s,string t,int m,int n){
          int j = 0;
          for(int i=0;i<n && j<m;i++){
             if(s[j]==t[i]){
                j++;
             }
          }
          return (j==m);
     }
   */

// Method 3: Dynamic Programming
   /*
      bool isSubsequence(string s, string t) {
        int n = s.length(), m = t.length();
        int dp[n+1][m+1];
        memset(dp, 0, sizeof(dp));
        for(int i=1;i<=n;i++)
        {
            for(int j=1;j<=m;j++)
            {
                if(s[i-1]==t[j-1])
                {
                    dp[i][j] = 1+dp[i-1][j-1];
                }
                else
                {
                    dp[i][j] = max(dp[i-1][j], dp[i][j-1]);
                }
            }
        }
        if(dp[n][m]==n)
        {
            return true;
        }
        return false;
    }
   */


