class Solution {
    public int climbStairs(int n) {
        int[] memo = new int[n+1];
        Arrays.fill(memo, -1);

        return dp(n, memo);
    }

    public int dp(int n, int[] memo){
        if(n <= 1){
            return 1;
        }

        if(memo[n] != -1){
            return memo[n];
        }

        int res = dp(n-1, memo) + dp(n-2, memo);
        memo[n] = res;

        return res;
    }
}