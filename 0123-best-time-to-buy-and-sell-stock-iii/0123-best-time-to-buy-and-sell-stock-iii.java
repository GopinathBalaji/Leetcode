// Method 1: Top-Down DP
/*
## Why this works (and what changed from your attempt)

1. **State is explicit and unambiguous**
   We encode everything we need to decide legal actions:

   * `i` = day
   * `hold` (0/1): whether we currently hold a stock
   * `sold` (0/1/2): how many completed **sell** operations we’ve done
     This avoids packing or extra parameters.

2. **Count transactions on SELL**
   A “transaction” = buy→sell pair. We increment `sold` **only when we sell**, which avoids half-transaction bugs and off-by-one errors.

3. **Always advance the day**
   Every recursive call moves to `i + 1` (skip/buy/sell all consume exactly one day), preventing infinite recursion.

4. **No `profit` parameter**
   We return “best profit from here” and add/subtract the day’s price **in the return expression** for buy/sell. This keeps memo keys small and correct.

5. **Prune when `sold == 2`**
   If we’ve already completed two sells, no further profit is possible—return `0`.

6. **Memoization is direct**
   `memo[i][hold][sold]` caches each state. Using `Integer` and `null` avoids sentinel collisions.

7. **Base case with holding**
   Ending the recursion at `i == n` returns `0`. The DP naturally avoids paths that end while holding if selling earlier would help (because selling adds `+prices[i]`).

---

## Step-by-step mini walkthrough

Let’s use a classic example:
`prices = [3, 2, 6, 5, 0, 3]`
Optimal: buy at 2 → sell at 6 (profit 4), buy at 0 → sell at 3 (profit 3). Total **7**.

We’ll peek at a few key states to see how decisions emerge.

### Work backward on the last day (i = 5, price = 3)

* `dfs(5, hold=0, sold=1)`

  * `skip = dfs(6,0,1) = 0`
  * `take (buy) = -3 + dfs(6,1,1) = -3 + 0 = -3`
    → best = `0` (don’t buy on the last day)

* `dfs(5, hold=1, sold=1)`

  * `skip = dfs(6,1,1) = 0`
  * `take (sell) = +3 + dfs(6,0,2) = 3 + 0 = 3`
    → best = `3` (sell now)

This establishes that **if you’re holding on day 5 and still have a sell left, you sell**.

### The day before (i = 4, price = 0), with one sell already done (`sold=1`)

* `dfs(4, 0, 1)`

  * `skip = dfs(5,0,1) = 0`
  * `buy  = -0 + dfs(5,1,1) = 0 + 3 = 3` (from above)
    → best = `3` → **buy** at price 0 to capture the day-5 sell.

So from (4,0,1) the DP chooses to **buy at 0**, planning to sell at 3 next day.

### Earlier around the first transaction (i = 2, price = 6), still `sold=0`

* `dfs(2, 1, 0)` (we’re holding—say we bought earlier at 2)

  * `skip = dfs(3,1,0)` → will consider selling later at 5 (worse)
  * `sell = +6 + dfs(3,0,1)`
    Now, look at `dfs(3,0,1)`:

    * It will later choose to buy at day 4 for 0 and sell at day 5 for 3 (from above), giving `3`.
  * So selling at day 2 yields `6 + 3 = 9` minus the earlier buy cost (that cost was accounted at the earlier buy step).
    This supports the best path: **buy at 2 (day 1), sell at 6 (day 2)**, then later **buy at 0 (day 4), sell at 3 (day 5)**.

### Putting it together

* From the start: `dfs(0,0,0)` considers BUY at 3 vs SKIP.
  It quickly learns skipping to day 1 and buying at `2` dominates, because:

  * Buy at 3 then sell at 6 → immediate gain `+3`, later buy at 0 → +3 more = 6 total.
  * Skip day 0, buy at 2 then sell at 6 → immediate gain `+4`, later buy at 0 → +3 more = **7 total**.

The memo ensures each `(i, hold, sold)` is computed just once; the recursion naturally discovers the optimal pairing of buys and sells under the “at most two transactions” rule.

---

## Complexity

* **States**: `n × 2 × 3 = O(n)`
* **Per state**: O(1) work
* **Total**: **O(n)** time, **O(n)** space

---

If you want, I can also show the equivalent **phase-based** 2D memo (buy1/sell1/buy2/sell2) version—interviewers tend to love both because they’re concise and hard to get wrong.
*/
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        // memo[i][hold][sold] stores best profit from day i with 'hold' and 'sold' completed sells
        Integer[][][] memo = new Integer[n + 1][2][3];
        return dfs(prices, 0, 0, 0, memo);
    }

    // i = day index, hold ∈ {0,1}, sold ∈ {0,1,2} (# of completed sells)
    private int dfs(int[] prices, int i, int hold, int sold, Integer[][][] memo) {
        // Base cases:
        // - out of days
        // - already completed two sells (no more profit possible)
        if (i == prices.length || sold == 2) return 0;

        if (memo[i][hold][sold] != null) return memo[i][hold][sold];

        // Option 1: skip today
        int skip = dfs(prices, i + 1, hold, sold, memo);

        // Option 2: take action depending on holding state
        int take;
        if (hold == 0) {
            // Buy today: pay prices[i], advance day, now holding
            take = -prices[i] + dfs(prices, i + 1, 1, sold, memo);
        } else {
            // Sell today: gain prices[i], advance day, drop holding, increment completed sells
            take = prices[i] + dfs(prices, i + 1, 0, sold + 1, memo);
        }

        return memo[i][hold][sold] = Math.max(skip, take);
    }
}






// Method 2: Bottom-Up DP
/*
### State

`dp[i][hold][sold]` = max profit starting at **day i** if:

* `hold ∈ {0,1}` whether you currently hold a stock
* `sold ∈ {0,1,2}` how many **completed** sells you’ve done

Answer is `dp[0][0][0]`.

### Transitions (filled from the end toward the start)

For each day `i` from `n-1` down to `0`, for each `sold`:

* If `hold == 0`:

  * **skip**: `dp[i+1][0][sold]`
  * **buy** (only makes sense if `sold < 2`): `-prices[i] + dp[i+1][1][sold]`
* If `hold == 1`:

  * **skip**: `dp[i+1][1][sold]`
  * **sell** (only if `sold < 2`): `+prices[i] + dp[i+1][0][sold+1]`

When `sold == 2`, no more sells are possible; only “skip” transitions apply (buying would be pointless).


**Complexity:** `O(n * 2 * 3) = O(n)` time and `O(n * 2 * 3) = O(n)` space.
(You can also roll arrays to `O(1)` space if you want, but let’s do that next.)

*/

// class Solution {
//     public int maxProfit(int[] prices) {
//         int n = prices.length;
//         // dp[i][hold][sold]
//         int[][][] dp = new int[n + 1][2][3];

//         // Base: dp[n][*][*] = 0 (no days left → no profit)

//         for (int i = n - 1; i >= 0; --i) {
//             int p = prices[i];

//             for (int sold = 0; sold <= 2; ++sold) {
//                 // hold == 0
//                 int bestNoHold = dp[i + 1][0][sold]; // skip
//                 if (sold < 2) {
//                     bestNoHold = Math.max(bestNoHold, -p + dp[i + 1][1][sold]); // buy
//                 }
//                 dp[i][0][sold] = bestNoHold;

//                 // hold == 1
//                 int bestHold = dp[i + 1][1][sold]; // skip
//                 if (sold < 2) {
//                     bestHold = Math.max(bestHold, p + dp[i + 1][0][sold + 1]); // sell
//                 }
//                 dp[i][1][sold] = bestHold;
//             }
//         }

//         return dp[0][0][0];
//     }
// }





// Method 3: O(1) space bottom-up
/*
Interpret “at most 2 transactions” as four ordered phases:

* `buy1` → `sell1` → `buy2` → `sell2`

Keep the **best** value of each phase so far as you sweep days left-to-right.

* `buy1` = max profit after the **first buy** (so it’s usually negative)
* `sell1` = max profit after the **first sell**
* `buy2` = max profit after the **second buy** (i.e., after you’ve already sold once)
* `sell2` = max profit after the **second sell**

### Updates per price `p`

```
buy1  = max(buy1,  -p)
sell1 = max(sell1, buy1 + p)

buy2  = max(buy2,  sell1 - p)
sell2 = max(sell2, buy2 + p)
```

Initialize `buy1` and `buy2` to a very negative number (acts like −∞), and `sell1 = sell2 = 0`.


**Why this works:**
It’s exactly the tabulation compressed into four running “columns”.
Each line uses the best from the **previous phase** as of the **current day**, so the dependencies match the 3D DP’s transitions.

---

# Thorough Example Walkthrough (O(1) version)

Let’s use `prices = [3, 2, 6, 5, 0, 3]` (optimal answer = **7**).

Initialize:

```
buy1=-∞, sell1=0, buy2=-∞, sell2=0
(use -1e9 as -∞ in mind)
```

### Day 0, p=3

* buy1  = max(-∞, -3)    = -3
* sell1 = max(0, -3+3)   = 0
* buy2  = max(-∞, 0-3)   = -3
* sell2 = max(0, -3+3)   = 0
  State: (buy1=-3, sell1=0, buy2=-3, sell2=0)

### Day 1, p=2

* buy1  = max(-3, -2)    = -2
* sell1 = max(0, -2+2)   = 0
* buy2  = max(-3, 0-2)   = -2
* sell2 = max(0, -2+2)   = 0
  State: (buy1=-2, sell1=0, buy2=-2, sell2=0)

### Day 2, p=6

* buy1  = max(-2, -6)    = -2
* sell1 = max(0, -2+6)   = 4   ← first transaction complete (buy at 2, sell at 6)
* buy2  = max(-2, 4-6)   = -2
* sell2 = max(0, -2+6)   = 4
  State: (buy1=-2, sell1=4, buy2=-2, sell2=4)

### Day 3, p=5

* buy1  = max(-2, -5)    = -2
* sell1 = max(4, -2+5)   = 4
* buy2  = max(-2, 4-5)   = -1  ← start 2nd buy effectively at total cost 1
* sell2 = max(4, -1+5)   = 4
  State: (buy1=-2, sell1=4, buy2=-1, sell2=4)

### Day 4, p=0

* buy1  = max(-2,  0)    = 0
* sell1 = max(4,  0+0)   = 4
* buy2  = max(-1, 4-0)   = 4   ← perfect time to (re)buy for 2nd trade
* sell2 = max(4,  4+0)   = 4
  State: (buy1=0, sell1=4, buy2=4, sell2=4)

### Day 5, p=3

* buy1  = max(0, -3)     = 0
* sell1 = max(4, 0+3)    = 4
* buy2  = max(4, 4-3)    = 4
* sell2 = max(4, 4+3)    = 7   ← second transaction complete (buy at 0, sell at 3)
  Final `sell2 = 7` → **answer = 7**.

**Intuition:**

* `buy1` tracks the best way to end today holding your *first* stock (most negative cost).
* `sell1` tracks the best finished first transaction.
* `buy2` tracks best state of holding a *second* stock (using profit from `sell1`).
* `sell2` tracks best finished second transaction.

Because we always take the **max** of “keep old state” vs “take new action today”, the variables monotonically improve (or stay the same) and capture the best feasible plan up to each day.
*/

// class Solution {
//     public int maxProfit(int[] prices) {
//         int buy1  = Integer.MIN_VALUE / 4;
//         int sell1 = 0;
//         int buy2  = Integer.MIN_VALUE / 4;
//         int sell2 = 0;

//         for (int p : prices) {
//             buy1  = Math.max(buy1,  -p);        // start or improve 1st buy
//             sell1 = Math.max(sell1, buy1 + p);  // finish/improve 1st sell
//             buy2  = Math.max(buy2,  sell1 - p); // start or improve 2nd buy (after sell1)
//             sell2 = Math.max(sell2, buy2 + p);  // finish/improve 2nd sell
//         }
//         return sell2; // best after up to 2 transactions
//     }
// }
