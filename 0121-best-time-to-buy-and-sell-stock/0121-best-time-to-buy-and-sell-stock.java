// My two pointer / sliding window solution

class Solution {
    public int maxProfit(int[] prices) {
        int max = 0;

        int pointer1 = 0;
        int pointer2 = 1;

        while(pointer2 < prices.length){
            if(prices[pointer2] > prices[pointer1]){
                max = Math.max(max, prices[pointer2] - prices[pointer1]);
                pointer2 ++;
            }else{
                pointer1 = pointer2;
                pointer2 ++;
            }
        }

        return max;
    }
}


// GPT DP Solution
// class Solution {
//     public int maxProfit(int[] prices) {
//         int n = prices.length;
//         if (n == 0) return 0;

//         int dp0 = 0;              // day 0, not holding (selling today)
//         int dp1 = -prices[0];     // day 0, if buy todya

//         for (int j = 1; j < n; j++) {
//             dp0 = Math.max(dp0, dp1 + prices[j]);
//             dp1 = Math.max(dp1, -prices[j]);
//         }

//         return dp0; // max profit ends with not holding any stock
//     }
// }

