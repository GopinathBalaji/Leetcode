class Solution {
public:
//     Method 1: Kadane's Algorithm (Everyday is a potential selling day, keep tarck of  // least value until now)
    int maxProfit(vector<int>& prices) {
        int minsofar = prices[0];
        int profitsofar = 0;
        for(int i=0;i<prices.size();i++){
            if(prices[i]<minsofar){
                minsofar = prices[i];
            }
            if(profitsofar<prices[i]-minsofar){
                profitsofar = prices[i] - minsofar;
            }
        }
        return profitsofar;
    }
};

// Method 2: Brute force
   /*
   public int maxProfit(int prices[]) {
        int maxprofit = 0;
        for (int i = 0; i < prices.length - 1; i++) {
            for (int j = i + 1; j < prices.length; j++) {
                int profit = prices[j] - prices[i];
                if (profit > maxprofit)
                    maxprofit = profit;
            }
        }
        return maxprofit;
    }
   */

// Method 3: Bottom Up Dynamic Programming
// Start with having the dp array of the length of the prices array
// At each index store the max of the difference with the prices[i] with min that was computed earlier
// Update the min everytime comparing with the current price
   /*
   
   public int maxProfit(int[] prices) {
        if (prices.length == 0)
            return 0;
        int[] dp = new int[prices.length];
        int min = prices[0];
    
        for (int i=1;i<prices.length;i++){
            dp[i] = Math.max(dp[i-1], prices[i] - min);
            min = Math.min(min, prices[i]);
        }
        
        return dp[prices.length-1];
    }
   */

// Method 4: Recursive Approach
// The base case is that if the index passed to the recursive function has reached the prices array length, if so return 0
// compute the minimum of the current item, prices[index] so that when we backtrack after the recursive call we can compute maxProfit by comparing with the current element difference with the prices[index]
// recursively call the function by incrementing the index, the value that will be returned from recursive call is the maxProfit
// after coming from the recursive call, we cmpute the maxProfit with the current element prices[index] - min element found before recursing.   
   /*
   
    protected int dfs(int[] prices, int index, int min){
         if (index == prices.length)
             return 0;
        
         int minimumEndingHere = Math.min(min, prices[index]);
         int maxProfit = dfs(prices, index + 1, minimumEndingHere);
         maxProfit = Math.max(maxProfit, prices[index] - min);
         return maxProfit;
     }
     public int maxProfit(int[] prices) {
         return dfs(prices, 0, Integer.MAX_VALUE);
   }
   */