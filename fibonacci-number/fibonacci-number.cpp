class Solution {
public:
//         Method 1: Top Down (Tabulation) Dynamic Programming
        int memoization(int *dp, int n)
    {
        if (dp[n] != -1) {
            return dp[n];
        }
        int result = -1;
        if (n == 0 || n == 1) {
            result = n;
        }
        else {
            result = memoization(dp, n-1) + memoization(dp, n-2);
        }
        dp[n] = result;
        return result;
    }
    
    int fib(int n) 
    {
        int dp[31];
        for (int i = 0; i < 31; i++) {
            dp[i] = -1;
        }
        return memoization(dp, n);
    }
};

// Method 2: Recursive
   /*
       int fib(int n) 
    {
        if (n == 0 || n == 1)
            return n;
        return fib(n-1) + fib(n-2);
    }
   */

// Method 3: Bottom Up (Tabulation) Dynamic Programming
   /*
    int tabulation(int *dp, int n)
    {
        dp[0] = 0;
        dp[1] = 1;
        for (int i = 2; i <= n; i++)
            dp[i] = dp[i-1] + dp[i-2];
        return dp[n];    
    }
        
    int fib(int n) 
    {
        if (n == 0) {
            return 0;
        }
        int dp[n+1];
        for (int i = 0; i < n + 1; i++) {
            dp[i] = -1;
        }
        return tabulation(dp, n);
    }
   */