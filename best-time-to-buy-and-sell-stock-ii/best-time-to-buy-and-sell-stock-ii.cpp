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

// Method 6: Dynamic Programming Bottom Up Approach
   /*
    int maxProfit(vector<int>& prices) {
        int sell[prices.size()];
        int buy[prices.size()];
        memset(sell,0,sizeof(sell));
        memset(buy,0,sizeof(buy));
        buy[0] = -prices[0];
        for(int i=1; i<prices.size();i++) {
            buy[i] = max(buy[i-1], sell[i-1] - prices[i]);
            sell[i] = max(sell[i-1],prices[i] + buy[i-1]);
        }
        return max(buy[prices.size()-1],sell[prices.size()-1]);
    }
   */


// Method 7: Using Recusion and DP
   /*
   class Solution {
public:
    
    int dp[30001][2];
    
    int recurs(int i,bool hold,vector<int>& prices)
    {
        if(i>=prices.size())
            return 0;
        
        if(dp[i][hold]!=-1)
            return dp[i][hold];
        
        // 1 indicates we have stock currently
        // 0 indicates no stock is possesed currently
        
        if(hold==1)//sell and get +prices[i] because we are gaining money or do nothing
        {
            return dp[i][hold]=max(recurs(i+1,0,prices)+prices[i],recurs(i+1,1,prices));
        }
        if(hold==0)//buy and lose -prices[i] because we are losing money or do nothing
        {
            return dp[i][hold]=max(recurs(i+1,1,prices)-prices[i],recurs(i+1,0,prices));
        }
        
        return 0;
    }
    
    int maxProfit(vector<int>& prices) {
        memset(dp,-1,sizeof(dp));
        return recurs(0,0,prices);
    }
   */