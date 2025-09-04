// Bottom-UP Dynamic Programming (with O(1) state compression)
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
    public int maxProfit(int[] prices) {
        if (prices == null || prices.length == 0) return 0;

        int minPrice = prices[0];
        int best = 0;  // max profit so far

        for (int i = 1; i < prices.length; i++) {
            // sell today vs. best so far
            best = Math.max(best, prices[i] - minPrice);
            // update min seen so far
            minPrice = Math.min(minPrice, prices[i]);
        }
        return best;
    }
}



// Top-Down Dynamic Programming
/*
* `i`: day index (0…n-1)
* `t`: how many **transactions (sells)** you still may perform (here `t ∈ {0,1}`)
* `hold`: 0 = you’re **not** holding a stock, 1 = you **are** holding

Transitions (sell consumes one transaction):

* If `hold == 0`:

  * **Skip** today → `dfs(i+1, t, 0)`
  * **Buy** (only if you’ll be allowed to sell later, i.e., `t > 0`) → `-prices[i] + dfs(i+1, t, 1)`
* If `hold == 1`:

  * **Hold** → `dfs(i+1, t, 1)`
  * **Sell** (if `t > 0`) → `prices[i] + dfs(i+1, t-1, 0)`

Base case:

* If `i == n` (past the last day):

  * If `hold == 0`: profit `0` (we’re done)
  * If `hold == 1`: invalid (you ended while still holding and can’t sell) → return a very negative sentinel to discourage this path.


**Complexity:**
States = `n * 2 (t) * 2 (hold)` → **O(n)** states, each solved once → **O(n)** time.
Memo table size O(n). Extra stack depth O(n) in worst case (tail-ish recursion).

---

## Why this is correct (intuition)

* We’re exploring all valid sequences of (buy, sell) with at most one sell.
* `t` counts **how many sells** you’re still allowed. Buying doesn’t consume `t`, selling does.
* The “invalid when holding at end” base case prevents “phantom” profit from an unsold position.
* Memoization ensures each `(i, t, hold)` is computed once.

---

## Thorough walkthrough on `[7, 1, 5, 3, 6, 4]`

Goal: max profit with ≤1 transaction (answer should be **5** by buying at 1, selling at 6).

We start at `(i=0, t=1, hold=0)`:

1. **Day 0, price 7, not holding**

   * Skip → `dfs(1,1,0)`
   * Buy → profit `-7 + dfs(1,1,1)`
     We’ll keep both; memo will decide which is better.

2. **Day 1, price 1**

   * From `(1,1,0)` (skip path):

     * Skip → `dfs(2,1,0)`
     * Buy → `-1 + dfs(2,1,1)`  ← buying cheap here is promising
   * From `(1,1,1)` (if we had bought 7):

     * Hold → `dfs(2,1,1)`
     * Sell → `+1 + dfs(2,0,0)` → profit `-7 + 1 = -6` so far (worse than holding)

3. **Day 2, price 5**

   * If **holding** from buying at 1:

     * Hold → `dfs(3,1,1)`
     * Sell → `+5 + dfs(3,0,0)` → net so far `-1 + 5 = 4`
   * If **not holding**:

     * Skip → `dfs(3,1,0)`
     * Buy → `-5 + dfs(3,1,1)` (worse than buying at 1)

4. **Day 3, price 3**

   * Holding from 1:

     * Hold → `dfs(4,1,1)`
     * Sell → `-1 + 3 = 2` (worse than selling at 5)
   * Not holding:

     * Skip / Buy (buying at 3 is still inferior to buying at 1)

5. **Day 4, price 6**

   * Holding from 1:

     * **Sell** now: `-1 + 6 = 5` and `dfs(5,0,0)` returns 0 → total **5**
     * Holding further gains no advantage (we have only one sell)
   * Not holding: skip/buy considered but won’t beat profit 5 already found.

6. **Day 5, price 4**

   * Any new buy here cannot be paired with a sell later (no days left), so those branches die at base case.

Memoized maxima bubble up: best is **5** (buy at 1, sell at 6).

---

### Notes

* This top-down DP is the recursive mirror of the classic iterative DP and compresses to the common greedy one-pass (`minPrice` / `best`).
* DP is useful for generalizing to **k transactions**: just keep `t ∈ {0..k}` and the same transitions.
*/

// class Solution {
//     public int maxProfit(int[] prices) {
//         int n = prices.length;
//         if (n == 0) return 0;

//         // memo[i][t][hold] stores the best profit from day i with t sells left and hold state
//         Integer[][][] memo = new Integer[n][2][2];
//         return dfs(0, 1, 0, prices, memo); // start: day 0, 1 transaction allowed, not holding
//     }

//     // dfs returns max profit from day i with t sells remaining and hold state
//     private int dfs(int i, int t, int hold, int[] prices, Integer[][][] memo) {
//         // End of array: if still holding, this path is invalid (can't sell anymore)
//         if (i == prices.length) return (hold == 1) ? (Integer.MIN_VALUE / 4) : 0;

//         if (memo[i][t][hold] != null) return memo[i][t][hold];

//         int best;
//         if (hold == 0) {
//             // Option 1: do nothing today
//             best = dfs(i + 1, t, 0, prices, memo);

//             // Option 2: buy today (only useful if we still have at least one sell left later)
//             if (t > 0) {
//                 best = Math.max(best, -prices[i] + dfs(i + 1, t, 1, prices, memo));
//             }
//         } else { // hold == 1
//             // Option 1: keep holding
//             best = dfs(i + 1, t, 1, prices, memo);

//             // Option 2: sell today (consumes one transaction)
//             if (t > 0) {
//                 best = Math.max(best, prices[i] + dfs(i + 1, t - 1, 0, prices, memo));
//             }
//         }

//         memo[i][t][hold] = best;
//         return best;
//     }
// }


// Same Top-Down Approach with better explanation
/*
Got it — let’s make the **top-down recursive DP** for LeetCode 121 super simple.

We’ll use only two easy states:

* **`buy(i)`** = best profit you can get starting at day `i` **when you’re allowed to buy** (haven’t bought yet).
* **`sell(i)`** = best profit you can get starting at day `i` **when you’re holding** a stock (you already bought, you can sell once).

Because the problem allows **at most one transaction (one buy + one sell)**, once we sell, we’re done — no more profit afterward.

---

# The idea in plain words

At each day `i`:

* If you’re in **buy** mode:

  * **Skip** today → try buying later → `buy(i+1)`
  * **Buy** today → pay `-prices[i]` now, then you’re in **sell** mode tomorrow → `-prices[i] + sell(i+1)`
  * Take the better of the two.

* If you’re in **sell** mode (you’re holding):

  * **Hold** → try selling later → `sell(i+1)`
  * **Sell** today → earn `+prices[i]`, and you’re done (no more profit after) → `prices[i]`
  * Take the better of the two.

**Base case:** if `i == n` (past last day), there’s no more profit to make → both `buy(n)` and `sell(n)` are `0`.

We memoize results so each state is computed once.

**Why no negative “sentinel” values?**
Because if selling isn’t profitable later, `sell(i)` can always choose to **not sell** and return 0. Then `buy(i)` will choose to **skip buying** rather than take a loss. That naturally avoids bad paths.

**Complexity:** Each day computes at most one `buy(i)` and one `sell(i)` → **O(n)** time, **O(n)** memory for memo; recursion depth up to `n`.

---

# Thorough walkthrough

Example: `prices = [7, 1, 5, 3, 6, 4]`
Answer should be **5** (buy at 1, sell at 6).

We’ll show the values that the recursion/memoization ends up computing (from the rightmost day back):

Let `n = 6`. Conceptually:

* `sell(i) = max(sell(i+1), prices[i])` (either hold, or sell now and stop)
* `buy(i)  = max(buy(i+1), -prices[i] + sell(i+1))` (either skip, or buy now then later sell optimally)

Add a row for `i = 6` as the base case.

| i | price | sell(i) explanation | sell(i) | buy(i) explanation                                 | buy(i) |
| - | ----- | ------------------- | ------- | -------------------------------------------------- | ------ |
| 6 | —     | base case           | 0       | base case                                          | 0      |
| 5 | 4     | max(sell(6)=0, 4)   | 4       | max(buy(6)=0, -4 + sell(6)=0) = max(0, -4)         | 0      |
| 4 | 6     | max(sell(5)=4, 6)   | 6       | max(buy(5)=0, -6 + sell(5)=4) = max(0, -2)         | 0      |
| 3 | 3     | max(sell(4)=6, 3)   | 6       | max(buy(4)=0, -3 + sell(4)=6) = max(0, 3)          | 3      |
| 2 | 5     | max(sell(3)=6, 5)   | 6       | max(buy(3)=3, -5 + sell(3)=6 = 1) = max(3,1)       | 3      |
| 1 | 1     | max(sell(2)=6, 1)   | 6       | max(buy(2)=3, -1 + sell(2)=6 = 5) = max(3,5)       | **5**  |
| 0 | 7     | max(sell(1)=6, 7)   | 7       | max(buy(1)=**5**, -7 + sell(1)=6 = -1) = max(5,-1) | **5**  |

We return `buy(0) = 5`.

**Interpretation:**

* When we’re allowed to buy at day 1 (price 1), the best move is to buy then later sell at day 4 (price 6): `-1 + 6 = 5`.
* All other choices (e.g., buying at 7, or selling at 3) are worse, and the DP/memo naturally avoids them.

---

## Key takeaways

* Two small functions (`buy` / `sell`) and memoization make the top-down DP easy.
* After a **sell**, the game ends (profit after that is 0). That’s why the recurrences stay simple.
* This DP produces exactly the same answer as the classic one-pass greedy — it’s just a recursive way to see it, and it generalizes nicely when you later allow more than one transaction.
*/

// class Solution {
//     public int maxProfit(int[] prices) {
//         int n = prices.length;
//         // Edge case: empty input → no profit
//         if (n == 0) return 0;

//         // Memo tables: null means “not computed yet”
//         Integer[] memoBuy = new Integer[n + 1];
//         Integer[] memoSell = new Integer[n + 1];

//         // Start on day 0, allowed to buy
//         return buy(0, prices, memoBuy, memoSell);
//     }

//     // Max profit from day i when we are allowed to BUY (haven't bought yet)
//     private int buy(int i, int[] p, Integer[] memoBuy, Integer[] memoSell) {
//         if (i == p.length) return 0;                  // no days left → no profit
//         if (memoBuy[i] != null) return memoBuy[i];

//         // Option 1: skip buying today
//         int skip = buy(i + 1, p, memoBuy, memoSell);

//         // Option 2: buy today, then we switch to "sell" mode tomorrow
//         int take = -p[i] + sell(i + 1, p, memoBuy, memoSell);

//         return memoBuy[i] = Math.max(skip, take);
//     }

//     // Max profit from day i when we are HOLDING (must decide when to SELL)
//     private int sell(int i, int[] p, Integer[] memoBuy, Integer[] memoSell) {
//         if (i == p.length) return 0;                  // no days left → if we never sold, profit 0
//         if (memoSell[i] != null) return memoSell[i];

//         // Option 1: hold another day
//         int hold = sell(i + 1, p, memoBuy, memoSell);

//         // Option 2: sell today; after selling, we're done (no more profit)
//         int take = p[i];

//         return memoSell[i] = Math.max(hold, take);
//     }
// }