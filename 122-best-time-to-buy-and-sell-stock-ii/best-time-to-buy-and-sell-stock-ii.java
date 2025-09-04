// Greedy Appraoch
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
    public int maxProfit(int[] prices) {
        int totalProfit = 0;
        int prevPrice = prices[0];

        for(int i=1; i<prices.length; i++){
            if(prices[i] > prevPrice){
                totalProfit += prices[i] - prevPrice;
            }

            prevPrice = prices[i];
        }

        return totalProfit;
    }
}


// Bottom-Up DP maintaining states and transitions
/*
Use two states per day:
    cash = max profit when not holding after day i
    hold = max profit when holding after day i

Transitions:
    cash = max(cash, hold + price) (sell today or not)
    hold = max(hold, cash - price) (buy today or not)
*/
// class Solution {
//     public int maxProfit(int[] prices) {
//         if (prices == null || prices.length == 0) return 0;

//         int cash = 0;            // profit with no stock
//         int hold = -prices[0];   // profit after buying first day

//         for (int i = 1; i < prices.length; i++) {
//             int price = prices[i];
//             int newCash = Math.max(cash, hold + price);
//             int newHold = Math.max(hold, cash - price);
//             cash = newCash;
//             hold = newHold;
//         }
//         return cash; // best profit ends not holding
//     }
// }




// Top-Down Memoized DP 
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
//     public int maxProfit(int[] prices) {
//         if (prices == null || prices.length == 0) return 0;
//         int n = prices.length;
//         Integer[][] memo = new Integer[n][2]; // memo[i][hold]
//         return dfs(0, 0, prices, memo);       // start day 0, not holding
//     }

//     private int dfs(int i, int hold, int[] p, Integer[][] memo) {
//         if (i == p.length) return 0;
//         if (memo[i][hold] != null) return memo[i][hold];

//         int best;
//         if (hold == 0) {
//             // skip OR buy today
//             best = Math.max(dfs(i + 1, 0, p, memo), -p[i] + dfs(i + 1, 1, p, memo));
//         } else {
//             // hold OR sell today
//             best = Math.max(dfs(i + 1, 1, p, memo), p[i] + dfs(i + 1, 0, p, memo));
//         }
//         return memo[i][hold] = best;
//     }
// }
