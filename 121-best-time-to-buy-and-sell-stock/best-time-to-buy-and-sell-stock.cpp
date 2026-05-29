// Method 1: Bottom-UP Dynamic Programming (with O(1) state compression)
/*
## Why this works (quick intuition)

* Track the **cheapest buy so far** (`minPrice`).
* At each day `i`, the best sell-today profit is `prices[i] - minPrice`.
* Keep the max of those over the scan.

## Tiny walkthrough

`[7,1,5,3,6,4]`

* start: `min=7`, `best=0`
* 1 → `best=max(0,1-7= -6)=0`, `min=1`
* 5 → `best=max(0,5-1=4)=4`, `min=1`
* 3 → `best=max(4,3-1=2)=4`, `min=1`
* 6 → `best=max(4,6-1=5)=5`, `min=1`
* 4 → `best=max(5,4-1=3)=5`, `min=1`
  Return `5`.

If you want the **DP formulation**, it’s:
`dpMin[i] = min(dpMin[i-1], prices[i])`,
`dpProfit[i] = max(dpProfit[i-1], prices[i] - dpMin[i])`,
and we keep only the last values (`minPrice`, `best`).
*/

class Solution {
public:
    int maxProfit(vector<int>& prices) {
        int n = prices.size();

        if(n <= 1){
            return 0;
        }

        int minPrice = prices[0];
        int maxProfit = 0;

        for(int i=1; i<n; i++){
            maxProfit = std::max(maxProfit, prices[i] - minPrice);
            minPrice = std::min(minPrice, prices[i]);
        }

        return maxProfit;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna