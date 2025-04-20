class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -2);  // use -2 to denote "not computed"
        int result = dp(coins, amount, memo);
        return result == Integer.MAX_VALUE ? -1 : result;
    }

    private int dp(int[] coins, int rem, int[] memo) {
        if (rem < 0) return Integer.MAX_VALUE;
        if (rem == 0) return 0;
        if (memo[rem] != -2) return memo[rem];

        int min = Integer.MAX_VALUE;
        for (int coin : coins) {
            int res = dp(coins, rem - coin, memo);
            if (res != Integer.MAX_VALUE) {
                min = Math.min(min, res + 1);
            }
        }

        memo[rem] = min;
        return min;
    }
}
