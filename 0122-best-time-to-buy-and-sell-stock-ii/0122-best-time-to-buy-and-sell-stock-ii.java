class Solution {
    public int maxProfit(int[] prices) {
        int dp0 = 0;
        int dp1 = -prices[0];

        for(int i=1;i<prices.length;i++){
            dp0 = Math.max(dp0, prices[i] + dp1);
            dp1 = Math.max(dp1, dp0 - prices[i]);
        }

        return dp0;
    }
}
// Example Code walkthrough
// | Day | Price | dp0 (no stock)     | dp1 (holding stock) |
// | --- | ----- | ------------------ | ------------------- |
// | 0   | 7     | 0                  | max(-∞, 0 - 7) = -7 |
// | 1   | 1     | max(0, -7 + 1) = 0 | max(-7, 0 - 1) = -1 |
// | 2   | 5     | max(0, -1 + 5) = 4 | max(-1, 0 - 5) = -1 |
// | 3   | 3     | max(4, -1 + 3) = 4 | max(-1, 4 - 3) = 1  |
// | 4   | 6     | max(4, 1 + 6) = 7  | max(1, 4 - 6) = 1   |
// | 5   | 4     | max(7, 1 + 4) = 7  | max(1, 7 - 4) = 3   |



// DP Solution a similar question : Best Time to Buy and Sell Stock I
// class Solution {
//     public int maxProfit(int[] prices) {
//         int n = prices.length;
//         if (n == 0) return 0;

//         int dp0 = 0;              // day 0, not holding (selling today)
//         int dp1 = -prices[0];     // day 0, if buying today

//         for (int j = 1; j < n; j++) {
//             dp0 = Math.max(dp0, dp1 + prices[j]);
//             dp1 = Math.max(dp1, -prices[j]);
//         }

//         return dp0; // max profit ends with not holding any stock
//     }
// }
