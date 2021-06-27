class Solution {
public:
    int climbStairs(int n) {
//  Method 1: Using DYNAMIC PROGRAMMING (BOTTOM UP APPROACH)
//         Base case: No. of ways to take 0 steps is 1, because there is only one 
//         way to not take any step. And no. of ways to take 1 step is 1.
        vector<int> v1;
        v1.push_back(1);
        v1.push_back(1);
        for(int i=2;i<=n;i++){
            v1.push_back(v1[i-1] + v1[i-2]);
        }
        return v1[n];
    }
};

// Method 2: Top Down Without Memoization
    /*
     int takeSteps(int n,vector<int>& dp){
    
    if(n == 0)  return 1;
    if(n < 0)   return 0;
    if(dp[n] != -1) return dp[n];
    return dp[n] = takeSteps(n-1,dp) + takeSteps(n-2,dp);
    
}

class Solution {
public:
    int climbStairs(int n) {
        vector<int> dp(n+1,-1);
        return takeSteps(n,dp);
    }
};
    */

// Method 3: Top Down With Memoization
   /*
       public int climbStairs(int n) {
        int memo[] = new int[n + 1];
        return climb_Stairs(0, n, memo);
    }
    public int climb_Stairs(int i, int n, int memo[]) {
        if (i > n) {
            return 0;
        }
        if (i == n) {
            return 1;
        }
        if (memo[i] > 0) {
            return memo[i];
        }
        memo[i] = climb_Stairs(i + 1, n, memo) + climb_Stairs(i + 2, n, memo);
        return memo[i];
    }
   */