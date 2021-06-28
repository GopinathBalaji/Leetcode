class Solution {
public:
    int maxProfit(vector<int>& prices) {
//         Method 1: Peak Valley Approach
        int i=0;
        int valley = prices[0];
        int peak = prices[0];
        int maxprofit = 0;
       
        while(i<prices.size()-1){
            while(i<prices.size()-1 && prices[i]>=prices[i+1]){
                i++;
            }
            valley = prices[i];
            while(i<prices.size()-1 && prices[i]<=prices[i+1]){
                i++;
            }
            peak = prices[i];
            maxprofit += peak - valley;
        }
        return maxprofit;
    }
};

// Method 2: Alternate Peak Valley Approach
   /*
       int maxProfit(vector<int>& prices) {
       
        int bd = 0;
        int sd = 0;
        int profit = 0;
        for(int i=1;i<prices.size();i++){
            if(prices[i]>=prices[i-1]){
                sd++;
            }else{
                profit += prices[sd]-prices[bd];
                bd = sd = i;
            }
        }
        profit += prices[sd] - prices[bd];
        return profit;
    }
   */


//         Method 3: Brute Force Approach(Optimized using DP)
/*
 int calculate (vector<int> prices, vector<int>& dp, int s, int& count ) {
        if (s >= prices.size())
            return 0;
    
       if (dp[s] != -1)
          return dp[s];
    
        int maxProfit = 0;
        for (int start = s; start < prices.size(); start++) {
            for (int i = start + 1; i < prices.size(); i++) {
                count++;
                maxProfit = max(maxProfit, 
                            calculate(prices, dp, i+1, count) + prices[i] - prices[start]);
            }
        }
    
        dp[s] = maxProfit;
        return maxProfit;
    }
    
    int maxProfit(vector<int>& prices) {

        vector<int> dp(prices.size(), -1);
        int count = 0;
        return (calculate(prices, dp, 0, count));
    }
*/

// Method 4: Brute Force
   /*
    public int maxProfit(int[] prices) {
        return calculate(prices, 0);
    }

    public int calculate(int prices[], int s) {
        if (s >= prices.length)
            return 0;
        int max = 0;
        for (int start = s; start < prices.length; start++) {
            int maxprofit = 0;
            for (int i = start + 1; i < prices.length; i++) {
                if (prices[start] < prices[i]) {
                    int profit = calculate(prices, i + 1) + prices[i] - prices[start];
                    if (profit > maxprofit)
                        maxprofit = profit;
                }
            }
            if (maxprofit > max)
                max = maxprofit;
        }
        return max;
    }
   */

// Method 5: One Pass 
   /*
   public int maxProfit(int[] prices) {
        int maxprofit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1])
                maxprofit += prices[i] - prices[i - 1];
        }
        return maxprofit;
    }
   */