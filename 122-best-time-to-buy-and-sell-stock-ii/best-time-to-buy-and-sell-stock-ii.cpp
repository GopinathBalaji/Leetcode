// Method 1: Greedy Approach
/*
Why this works:
With unlimited transactions and no fees, the optimal strategy is to sum all positive day-to-day increases. That equals buying at each local valley and selling at the next peak.

Walkthrough [7,1,5,3,6,4]
Differences: -6, +4, -2, +3, -2 → add positives 4 + 3 = 7.

Hint from comments:
"I'll tell you the most basic thing to understand before doing any "Buy and Sell Stock" problem :-

Take ex :- [1,4,7,8,6,4]
if you take (1, 8) , diff = 7
or if you take (1, 4), (4, 7), (7, 8), diff = 3 + 3 + 1 = 7

Going directly to 8, or going to 8 by adding all differences in between is same in result, so rather than thinking to jump, think it in this way."
*/
class Solution {
public:
    int maxProfit(vector<int>& prices) {
        int profit = 0;

        for(int i=1; i<prices.size(); i++){
            if(prices[i] > prices[i-1]){
                profit += prices[i] - prices[i-1];
            }
        }

        return profit;
    }
};




// Method 2: Bottom-Up Approach with tabulation
/*
For **LeetCode 122. Best Time to Buy and Sell Stock II**, the bottom-up DP approach uses two states for each day.

At any day `i`, you can be in one of two states:

```cpp
hold[i]     // max profit at end of day i if you are holding one stock
notHold[i]  // max profit at end of day i if you are not holding any stock
```

---

## State transitions

### 1. If you are holding a stock on day `i`

Either:

```text
You were already holding from yesterday
```

or:

```text
You buy today
```

So:

```cpp
hold[i] = max(hold[i - 1], notHold[i - 1] - prices[i]);
```

---

### 2. If you are not holding a stock on day `i`

Either:

```text
You were already not holding from yesterday
```

or:

```text
You sell today
```

So:

```cpp
notHold[i] = max(notHold[i - 1], hold[i - 1] + prices[i]);
```

---

## Base case

On day `0`:

```cpp
hold[0] = -prices[0];  // buy stock on day 0
notHold[0] = 0;        // do nothing
```
*/
// class Solution {
// public:
//     int maxProfit(vector<int>& prices) {
//         int n = prices.size();

//         vector<int> hold(n, 0);
//         vector<int> notHold(n, 0);

//         hold[0] = -prices[0];
//         notHold[0] = 0;

//         for (int i = 1; i < n; i++) {
//             hold[i] = max(hold[i - 1], notHold[i - 1] - prices[i]);
//             notHold[i] = max(notHold[i - 1], hold[i - 1] + prices[i]);
//         }

//         return notHold[n - 1];
//     }
// };





// Method 2.5: Bottom-Up DP maintaining states and transitions
/*
Use two states per day:
    cash = max profit when not holding after day i
    hold = max profit when holding after day i

Transitions:
    cash = max(cash, hold + price) (sell today or not)
    hold = max(hold, cash - price) (buy today or not)
*/
// class Solution {
// public:
//     int maxProfit(vector<int>& prices) {
//         int hold = -prices[0];
//         int notHold = 0;

//         for (int i = 1; i < prices.size(); i++) {
//             int prevHold = hold;
//             int prevNotHold = notHold;

//             hold = max(prevHold, prevNotHold - prices[i]);
//             notHold = max(prevNotHold, prevHold + prices[i]);
//         }

//         return notHold;
//     }
// };





// Method 3: Top-Down Memoized DP 
/*
top-down DP (memoized recursion) using two states:
    i: day index
    hold: 0 = not holding, 1 = holding

At each day you either do nothing or trade (buy/sell). With unlimited transactions, after selling you can buy again later.

Recurrence:
    If hold == 0 (not holding):
        f(i,0) = max( f(i+1,0), -prices[i] + f(i+1,1) )

    If hold == 1 (holding):
        f(i,1) = max( f(i+1,1), prices[i] + f(i+1,0) )

    Base: i == n → 0
*/
// class Solution {
// private:
//     vector<vector<int>> dp;

// public:
//     int maxProfit(vector<int>& prices) {
//         int n = prices.size();

//         dp.resize(n, vector<int>(2, -1));

//         return dfs(0, 0, prices);
//     }

// private:
//     int dfs(int index, int holding, vector<int>& prices) {
//         if (index == prices.size()) {
//             return 0;
//         }

//         if (dp[index][holding] != -1) {
//             return dp[index][holding];
//         }

//         int profit = 0;

//         if (holding == 0) {
//             int skip = dfs(index + 1, 0, prices);
//             int buy = -prices[index] + dfs(index + 1, 1, prices);

//             profit = max(skip, buy);
//         } else {
//             int skip = dfs(index + 1, 1, prices);
//             int sell = prices[index] + dfs(index + 1, 0, prices);

//             profit = max(skip, sell);
//         }

//         dp[index][holding] = profit;
//         return profit;
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna