// Top Down DP
// Top-Down (Memoized) DP Intuition

// “Ask smaller questions”: To know the number of ways to climb n steps (dp(n)), observe that your first move is either 1 step or 2 steps—so

// dp(n)=dp(n−1)+dp(n−2).
// Base cases anchor the recursion:

// dp(0)=1 (one way to stand still),

// dp(1)=1 (one single-step move).

// Avoid repeat work by remembering answers: whenever you compute dp(k), store it in a table. If you ever need dp(k) again, just look it up in O(1) instead of recursing.

// Recursive flow: start by “asking” dp(n), which in turn asks dp(n-1) and dp(n-2), each of which further asks smaller values, until you hit the base cases and bubble answers back up.

class Solution {
    public int climbStairs(int n) {
        int[] memo = new int[n+1];
        return dp(n, memo);
    }

    public int dp(int n, int[] memo){
        if(n == 0 || n== 1){
            return 1;
        }
        if(memo[n] != 0){
            return memo[n];
        }
        
        memo[n] = dp(n-1, memo) + dp(n-2, memo);

        return memo[n];
    }
}


// Bottom Up DP
// When you switch to a bottom-up DP for “climbing stairs,” you’re simply inverting the recursion and building your answers from the ground up:

// Define your DP state

// dp[i] = number of distinct ways to reach stair i  
// (where “reaching stair 0” counts as 1 way—i.e. standing at the ground).

// Establish your base cases

// dp[0] = 1: there’s exactly one way to be at the start (do nothing).

// dp[1] = 1: one step of size 1.

// Write the recurrence
// From stair i, your last move was either

// a 1-step from i-1, or

// a 2-step from i-2.

// So,
// dp[i] = dp[i-1] + dp[i-2]
// /////////
// class Solution {
//     public int climbStairs(int n) {
//       int[] dp = new int[n];
//       dp[0] = 1;
//       dp[1] = 1;

//       for(int i=2;i<n;i++){
//         dp[i] = dp[i-1] + dp[i-2];
//       } 

//         return dp[n];
//     }
// }
