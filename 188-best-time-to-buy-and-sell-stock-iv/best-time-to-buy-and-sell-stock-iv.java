// Method 1: Top-Down DP
/*
### What I was doing “wrong”

1. **You don’t cap `k`.**
   You can’t complete more than `⌊n/2⌋` transactions (each needs at least 2 days).
   If `k > n/2`, your DP allocates and explores tons of useless states.

   * **Fix:** `k = Math.min(k, n / 2);`

2. **No “unlimited transactions” fast path.**
   When `k ≥ n/2`, the problem is equivalent to unlimited transactions; you should return the greedy sum of all positive deltas. Without this, you’ll still do O(n·k) work unnecessarily.

   * **Fix:** if `k >= n/2`, return `∑ max(0, prices[i]-prices[i-1])`.

3. **Autoboxing with `Integer[][][]` is expensive.**
   Every memo cell is a heap object; with large `n`/`k` this is a memory/time killer.

   * **Fix:** use primitive `int[][][]` + a sentinel (and/or a `seen` boolean array).

4. **Deep recursion can overflow the stack.**
   For large `n`, a pure top-down may blow the stack even if memoized. (Bottom-up or iterative “phase DP” avoids this.) If you want to keep top-down, it’ll still pass **if** you clamp `k` and use primitives—but bottom-up is safer.

> Algorithmically, your transitions are fine: you count a transaction on **sell**, and you advance the day for every choice. The issues are performance/scalability and Java allocation.


### Why this fixes your pain points

* Capping `K` shrinks the state space to at most `O(n·K)` with `K ≤ n/2`.
* The “unlimited” early return turns worst cases into linear time.
* Primitive arrays avoid autoboxing overhead.
* Logic stays exactly the same: transaction counted on **sell**, one action per day.
*/
class Solution {
    public int maxProfit(int k, int[] prices) {
        int n = prices.length;
        if(n <= 1 || k == 0){
            return 0;
        }
        
        if(k >= n/2){
            int ans = 0;
            for(int i=1; i<n; i++){
                if(prices[i] > prices[i-1]){
                    ans += prices[i] - prices[i-1];
                }
            }

            return ans;
        }

        final int UNSET = Integer.MIN_VALUE / 4;
        int[][][] memo = new int[n+1][2][k+1];
        for(int i=0; i<=n; i++){
            Arrays.fill(memo[i][0], UNSET);
            Arrays.fill(memo[i][1], UNSET);
        }

        return dp(prices, memo, 0, 0, 0, k);
    }

    private int dp(int[] prices, int[][][] memo, int i, int hold, int sold, int k){
        if(i == prices.length || sold == k){
            return 0;
        }

        if(memo[i][hold][sold] != Integer.MIN_VALUE / 4){
            return memo[i][hold][sold];
        }        

        int skip = dp(prices, memo, i+1, hold, sold, k);

        int take;
        if(hold == 0){
            take = -prices[i] + dp(prices, memo, i+1, 1, sold, k);
        }else{
            take = prices[i] + dp(prices, memo, i+1, 0, sold + 1, k);
        }

        return memo[i][hold][sold] = Math.max(skip, take);
    }
}



// Method 2: Bottom-Up DP
/*
## Let’s unpack the DP meaning

### State meaning

For each day `day` and each `t` from `0` to `K`:

* `dpNotHold[day][t]`

  * “Best profit achievable after looking at prices[0..day], ending today with:

    * I do NOT currently own a stock
    * I have done at most `t` completed transactions (i.e. sells).”

* `dpHold[day][t]`

  * “Best profit achievable after looking at prices[0..day], ending today with:

    * I DO currently own a stock
    * I have done at most `t` completed transactions.”

Why “at most t sells,” not “exactly t sells”?
Because you might not use all your transactions. If you're allowed 2 sells max, you're also allowed 1 sell or 0 sells. We want flexibility.

---

### Transitions

We fill day by day from left to right.

For each `day` and `t`:

#### Case 1: End the day NOT holding (`dpNotHold[day][t]`)

Two ways to end not holding today:

1. **Do nothing / stay out of the market today**
   You were already not holding yesterday with up to `t` sells:

   ```text
   stayNotHolding = dpNotHold[day-1][t]
   ```

2. **Sell today**
   You *were* holding yesterday with up to `t-1` sells.
   You sell at today’s price, which completes a new sell, bumping your completed sells up to `t`.

   ```text
   sellToday = dpHold[day-1][t-1] + prices[day]
   ```

Then:

```text
dpNotHold[day][t] = max(stayNotHolding, sellToday)
```

Note: when `t == 0`, you *cannot* have sold today (because that would create your first completed transaction). So for `t == 0`, `sellToday` is invalid.

---

#### Case 2: End the day HOLDING (`dpHold[day][t]`)

Two ways to end holding today:

1. **Keep holding through today**
   You were holding yesterday with up to `t` sells:

   ```text
   keepHolding = dpHold[day-1][t]
   ```

2. **Buy today**
   You were not holding yesterday, still with up to `t` sells done.
   You buy today (spend today’s price). Buying does NOT increase the number of completed sells:

   ```text
   buyToday = dpNotHold[day-1][t] - prices[day]
   ```

Then:

```text
dpHold[day][t] = max(keepHolding, buyToday)
```

---

### Base case (day 0)

At day 0:

* If we **don’t buy**, we’re not holding and we’ve done 0 sells:

  ```text
  dpNotHold[0][0] = 0
  ```

* If we **do buy** on day 0, we’re holding a stock we paid `prices[0]` for, still 0 sells:

  ```text
  dpHold[0][0] = -prices[0]
  ```

* Anything that assumes we already completed sells on day 0 (like `dpNotHold[0][1]`) is impossible, so we keep that as a very negative number (`NEG`).

That “NEG” is how we ban illegal states from accidentally looking attractive.

---

### Final answer

On the last day (`n-1`) you want to NOT be holding (because holding means you've spent money but haven't realized profit). So we take the best over all allowed sell counts:

```text
answer = max(dpNotHold[n-1][t]) for t in 0..K
```

---

## Full example walkthrough

Let’s walk an example to see numbers change.
We'll use:

```text
prices = [3, 2, 6, 5, 0, 3]
K = 2 (at most 2 transactions allowed)
```

We expect final answer = 7:

* Buy at 2 → sell at 6 = +4
* Buy at 0 → sell at 3 = +3
  Total = 7

We'll track small tables:

* Row = day
* Col = t (0,1,2)
* We'll keep two tables: `dpNotHold` and `dpHold`

We'll use NEG = "very negative" for impossible.

### Day 0 (price = 3)

Initialize:

* `dpNotHold[0][0] = 0`
  We did nothing, we’re not holding, 0 sells done.

* `dpHold[0][0] = -3`
  We bought at price 3, so profit is `-3`.

* All other entries are NEG because we can't have finished 1 or 2 sells on day 0.

So:

`dpNotHold[0] = [ 0,  NEG,  NEG ]`
`dpHold[0]    = [-3,  NEG,  NEG ]`

### Day 1 (price = 2)

Now we fill `day = 1`.

We'll compute each `t` separately.

#### For t = 0

`dpNotHold[1][0]`:

* stayNotHolding = dpNotHold[0][0] = 0
* sellToday: invalid because t=0 can't come from t-1
  → dpNotHold[1][0] = 0

`dpHold[1][0]`:

* keepHolding = dpHold[0][0] = -3
* buyToday = dpNotHold[0][0] - 2 = 0 - 2 = -2
  → dpHold[1][0] = max(-3, -2) = -2
  (“If I buy, better to think of it as having bought at 2 instead of 3.”)

#### For t = 1

`dpNotHold[1][1]`:

* stayNotHolding = dpNotHold[0][1] = NEG
* sellToday = dpHold[0][0] + 2 = (-3) + 2 = -1
  → dpNotHold[1][1] = -1
  (This is: buy at 3, sell at 2 → lose 1. It's allowed, just dumb.)

`dpHold[1][1]`:

* keepHolding = dpHold[0][1] = NEG
* buyToday = dpNotHold[0][1] - 2 = NEG - 2 = NEG
  → dpHold[1][1] = NEG
  (We can't end day 1 holding a new stock *after* already completing 1 sell, because that would require a full completed sale earlier, which we don't really have profitably yet.)

#### For t = 2

Same story, both are NEG for now.

Summary after Day 1:
`dpNotHold[1] = [ 0,  -1, NEG ]`
`dpHold[1]    = [-2,  NEG, NEG ]`

### Day 2 (price = 6)

#### t = 0

`dpNotHold[2][0]`:

* stayNotHolding = dpNotHold[1][0] = 0
* sellToday = invalid for t=0
  → dpNotHold[2][0] = 0

`dpHold[2][0]`:

* keepHolding = dpHold[1][0] = -2
* buyToday = dpNotHold[1][0] - 6 = 0 - 6 = -6
  → dpHold[2][0] = max(-2, -6) = -2
  (Still best to think we "bought at 2".)

#### t = 1

`dpNotHold[2][1]`:

* stayNotHolding = dpNotHold[1][1] = -1
* sellToday = dpHold[1][0] + 6 = (-2) + 6 = 4
  → dpNotHold[2][1] = max(-1, 4) = 4
  (This is our first GOOD completed transaction:
  buy at 2 (profit -2 when holding), sell at 6 = +4 total.)

`dpHold[2][1]`:

* keepHolding = dpHold[1][1] = NEG
* buyToday = dpNotHold[1][1] - 6 = (-1) - 6 = -7
  → dpHold[2][1] = -7
  (This means: after doing 1 sell but losing money so far (-1), we then buy at 6, leaving us effectively at -7. It's allowed but bad.)

#### t = 2

`dpNotHold[2][2]`:

* stayNotHolding = dpNotHold[1][2] = NEG
* sellToday = dpHold[1][1] + 6 = NEG + 6 = NEG
  → dpNotHold[2][2] = NEG

`dpHold[2][2]`:

* keepHolding = dpHold[1][2] = NEG
* buyToday = dpNotHold[1][2] - 6 = NEG - 6 = NEG
  → dpHold[2][2] = NEG

Summary after Day 2:
`dpNotHold[2] = [ 0,  4,  NEG ]`
`dpHold[2]    = [-2, -7,  NEG ]`

Interpretation:

* `dpNotHold[2][1] = 4` means: after day 2, not holding, and with at most 1 sell done, best profit = 4.
  That's exactly “buy at 2, sell at 6.” ✔️

### Day 3 (price = 5)

#### t = 0

`dpNotHold[3][0]`:

* stayNotHolding = dpNotHold[2][0] = 0
* sellToday = invalid
  → 0

`dpHold[3][0]`:

* keepHolding = dpHold[2][0] = -2
* buyToday = dpNotHold[2][0] - 5 = 0 - 5 = -5
  → max(-2, -5) = -2

#### t = 1

`dpNotHold[3][1]`:

* stayNotHolding = dpNotHold[2][1] = 4
* sellToday = dpHold[2][0] + 5 = (-2) + 5 = 3
  → max(4, 3) = 4
  (So it's actually better to have sold yesterday at 6 than to sell today at 5. Makes sense.)

`dpHold[3][1]`:

* keepHolding = dpHold[2][1] = -7
* buyToday = dpNotHold[2][1] - 5 = 4 - 5 = -1
  → max(-7, -1) = -1
  This is important:
* We already made profit 4 from first transaction.
* Now we "buy" again at price 5.
* After that buy, our net is 4 - 5 = -1.
  So `dpHold[3][1] = -1` means:

> after finishing 1 good sell, we have re-entered the market with effective running profit -1.

#### t = 2

`dpNotHold[3][2]`:

* stayNotHolding = dpNotHold[2][2] = NEG
* sellToday = dpHold[2][1] + 5 = (-7) + 5 = -2
  → max(NEG, -2) = -2
  So, one way to get 2 sells by now is:
* We did one terrible transaction that left us at -7 (buy high, sell low),
* Then sell again at 5, getting to -2. It's legal but ugly.

`dpHold[3][2]`:

* keepHolding = dpHold[2][2] = NEG
* buyToday = dpNotHold[2][2] - 5 = NEG - 5 = NEG
  → NEG

Summary after Day 3:
`dpNotHold[3] = [ 0,  4, -2 ]`
`dpHold[3]    = [-2, -1,  NEG ]`

Now watch what happens on the huge dip Day 4.

### Day 4 (price = 0)

#### t = 0

`dpNotHold[4][0]`:

* stayNotHolding = dpNotHold[3][0] = 0
* sellToday = invalid
  → 0

`dpHold[4][0]`:

* keepHolding = dpHold[3][0] = -2
* buyToday = dpNotHold[3][0] - 0 = 0 - 0 = 0
  → max(-2, 0) = 0
  This means: if we haven't sold yet, we can just "buy now at 0", so our effective running profit after that buy is 0. That's strictly better than "I bought at 2" (-2).

#### t = 1

`dpNotHold[4][1]`:

* stayNotHolding = dpNotHold[3][1] = 4
* sellToday = dpHold[3][0] + 0 = (-2) + 0 = -2
  → max(4, -2) = 4
  (So holding from that earlier buy at 2 and selling at 0 would be dumb vs the 4 we already locked in.)

`dpHold[4][1]`:

* keepHolding = dpHold[3][1] = -1
* buyToday = dpNotHold[3][1] - 0 = 4 - 0 = 4
  → max(-1, 4) = 4
  Huge jump:
* We had profit 4 from first completed transaction (buy 2, sell 6).
* Now we buy again at 0.
* Our effective profit while holding is now 4 (not -1 anymore), because buying at 0 costs basically nothing.
  This is setting us up for the second transaction.

#### t = 2

`dpNotHold[4][2]`:

* stayNotHolding = dpNotHold[3][2] = -2
* sellToday = dpHold[3][1] + 0 = (-1) + 0 = -1
  → max(-2, -1) = -1

`dpHold[4][2]`:

* keepHolding = dpHold[3][2] = NEG
* buyToday = dpNotHold[3][2] - 0 = (-2) - 0 = -2
  → -2

Summary after Day 4:
`dpNotHold[4] = [ 0,  4, -1 ]`
`dpHold[4]    = [ 0,  4, -2 ]`

Most important thing here:

* `dpHold[4][1] = 4` says:

  > After finishing exactly one great sell (profit 4),
  > we then bought at price 0,
  > so we're holding again with an effective running profit of 4.
  > This is the perfect position to cash out a second time.

Now the last day.

### Day 5 (price = 3)

#### t = 0

`dpNotHold[5][0]`:

* stayNotHolding = dpNotHold[4][0] = 0
* sellToday = invalid
  → 0

`dpHold[5][0]`:

* keepHolding = dpHold[4][0] = 0
* buyToday = dpNotHold[4][0] - 3 = 0 - 3 = -3
  → max(0, -3) = 0
  (This isn't meaningful long-term, but it’s consistent.)

#### t = 1

`dpNotHold[5][1]`:

* stayNotHolding = dpNotHold[4][1] = 4
* sellToday = dpHold[4][0] + 3 = 0 + 3 = 3
  → max(4, 3) = 4
  So best with ≤1 sell is still 4 (buy at 2, sell at 6).

`dpHold[5][1]`:

* keepHolding = dpHold[4][1] = 4
* buyToday = dpNotHold[4][1] - 3 = 4 - 3 = 1
  → max(4, 1) = 4
  (“After 1 sell, still holding after maybe buying again at 0 and not selling yet.”)

#### t = 2

`dpNotHold[5][2]`:

* stayNotHolding = dpNotHold[4][2] = -1
* sellToday = dpHold[4][1] + 3 = 4 + 3 = 7
  → max(-1, 7) = 7
  BOOM.
  This is:
* First transaction: buy at 2, sell at 6 = +4
* Second transaction: buy at 0, sell at 3 = +3
  Total = 7

`dpHold[5][2]`:

* keepHolding = dpHold[4][2] = -2
* buyToday = dpNotHold[4][2] - 3 = (-1) - 3 = -4
  → max(-2, -4) = -2  (doesn't matter now)

Final after last day:
`dpNotHold[5] = [ 0, 4, 7 ]`
`dpHold[5]    = [ 0, 4, -2]`

Now we take the best "not holding" value after the last day across all `t ≤ K`:

* max(0, 4, 7) = **7**

That’s the final answer.

---

## Why this DP is clean for interviews

* The dimensions are easy to explain:

  * `day` goes forward in time.
  * `t` = how many sells (transactions) you’ve completed so far.
  * `hold` / `notHold` = whether you’re currently in or out of the market.

* The recurrence lines are intuitive:

  * “To end not holding, either I was not holding, or I sold today.”
  * “To end holding, either I was holding, or I bought today.”

* You never double-sell or double-buy in one day because transitions always look at `day-1`.

* At the end, you only consider the `notHold` states, because un-sold stock is just unrealized profit.
*/

// class Solution {
//     public int maxProfit(int K, int[] prices) {
//         int n = prices.length;
//         if (n == 0 || K == 0) return 0;

//         // If we are allowed at least n/2 transactions, it's effectively unlimited.
//         // For unlimited transactions, best profit is just sum of all upward moves.
//         if (K >= n / 2) {
//             int greedy = 0;
//             for (int i = 1; i < n; i++) {
//                 if (prices[i] > prices[i - 1]) {
//                     greedy += prices[i] - prices[i - 1];
//                 }
//             }
//             return greedy;
//         }

//         // We'll use a large negative number as "impossible state" (instead of -∞).
//         final int NEG = Integer.MIN_VALUE / 4;

//         // dpNotHold[day][t] = max profit after processing days [0..day],
//         //                     ending day 'day' with AT MOST t completed sells,
//         //                     and currently NOT holding a stock.
//         //
//         // dpHold[day][t]    = max profit after processing days [0..day],
//         //                     ending day 'day' with AT MOST t completed sells,
//         //                     and currently HOLDING a stock.
//         //
//         // Here t ranges from 0..K (how many completed transactions we've done).
//         int[][] dpNotHold = new int[n][K + 1];
//         int[][] dpHold    = new int[n][K + 1];

//         // Initialize all states to NEG (impossible) first.
//         for (int i = 0; i < n; i++) {
//             Arrays.fill(dpNotHold[i], NEG);
//             Arrays.fill(dpHold[i], NEG);
//         }

//         // Day 0 (base case):
//         // - We can choose to do nothing: not holding stock, 0 completed sells → profit 0.
//         dpNotHold[0][0] = 0;

//         // - We can buy on day 0:
//         //   This puts us in HOLDING state, still 0 completed sells, with profit = -prices[0]
//         dpHold[0][0] = -prices[0];

//         // We cannot have completed any sells on day 0, so dpNotHold[0][t>0] and dpHold[0][t>0] stay NEG.

//         // Fill DP for each subsequent day
//         for (int day = 1; day < n; day++) {
//             int price = prices[day];

//             for (int t = 0; t <= K; t++) {

//                 // 1. Transition for dpNotHold[day][t]
//                 //
//                 // Option A: we were already NOT holding yesterday with at most t sells
//                 //           and we keep not holding today.
//                 //           -> dpNotHold[day-1][t]
//                 int stayNotHolding = dpNotHold[day - 1][t];

//                 // Option B: we SELL today.
//                 //   Yesterday we had to be HOLDING with at most (t-1) sells,
//                 //   and by selling today we finish one more transaction,
//                 //   so now we have at most t sells.
//                 //   -> dpHold[day-1][t-1] + price
//                 int sellToday = NEG;
//                 if (t > 0 && dpHold[day - 1][t - 1] != NEG) {
//                     sellToday = dpHold[day - 1][t - 1] + price;
//                 }

//                 dpNotHold[day][t] = Math.max(stayNotHolding, sellToday);

//                 // 2. Transition for dpHold[day][t]
//                 //
//                 // Option A: we were already HOLDING yesterday with at most t sells
//                 //           and we just continue holding today.
//                 //           -> dpHold[day-1][t]
//                 int keepHolding = dpHold[day - 1][t];

//                 // Option B: we BUY today.
//                 //   Yesterday we were NOT holding with at most t sells,
//                 //   and today we buy (spend 'price'), still t sells completed.
//                 //   -> dpNotHold[day-1][t] - price
//                 int buyToday = NEG;
//                 if (dpNotHold[day - 1][t] != NEG) {
//                     buyToday = dpNotHold[day - 1][t] - price;
//                 }

//                 dpHold[day][t] = Math.max(keepHolding, buyToday);
//             }
//         }

//         // Final answer:
//         // At the end (day = n-1), we want to NOT be holding stock.
//         // We can have completed anywhere from 0..K sells.
//         int ans = 0;
//         for (int t = 0; t <= K; t++) {
//             ans = Math.max(ans, dpNotHold[n - 1][t]);
//         }
//         return ans;
//     }
// }





// Method 3: Better O(n*k) bottom-up solution
/*
## Let’s unpack the DP meaning

### State meaning

For each day `day` and each `t` from `0` to `K`:

* `dpNotHold[day][t]`

  * “Best profit achievable after looking at prices[0..day], ending today with:

    * I do NOT currently own a stock
    * I have done at most `t` completed transactions (i.e. sells).”

* `dpHold[day][t]`

  * “Best profit achievable after looking at prices[0..day], ending today with:

    * I DO currently own a stock
    * I have done at most `t` completed transactions.”

Why “at most t sells,” not “exactly t sells”?
Because you might not use all your transactions. If you're allowed 2 sells max, you're also allowed 1 sell or 0 sells. We want flexibility.

---

### Transitions

We fill day by day from left to right.

For each `day` and `t`:

#### Case 1: End the day NOT holding (`dpNotHold[day][t]`)

Two ways to end not holding today:

1. **Do nothing / stay out of the market today**
   You were already not holding yesterday with up to `t` sells:

   ```text
   stayNotHolding = dpNotHold[day-1][t]
   ```

2. **Sell today**
   You *were* holding yesterday with up to `t-1` sells.
   You sell at today’s price, which completes a new sell, bumping your completed sells up to `t`.

   ```text
   sellToday = dpHold[day-1][t-1] + prices[day]
   ```

Then:

```text
dpNotHold[day][t] = max(stayNotHolding, sellToday)
```

Note: when `t == 0`, you *cannot* have sold today (because that would create your first completed transaction). So for `t == 0`, `sellToday` is invalid.

---

#### Case 2: End the day HOLDING (`dpHold[day][t]`)

Two ways to end holding today:

1. **Keep holding through today**
   You were holding yesterday with up to `t` sells:

   ```text
   keepHolding = dpHold[day-1][t]
   ```

2. **Buy today**
   You were not holding yesterday, still with up to `t` sells done.
   You buy today (spend today’s price). Buying does NOT increase the number of completed sells:

   ```text
   buyToday = dpNotHold[day-1][t] - prices[day]
   ```

Then:

```text
dpHold[day][t] = max(keepHolding, buyToday)
```

---

### Base case (day 0)

At day 0:

* If we **don’t buy**, we’re not holding and we’ve done 0 sells:

  ```text
  dpNotHold[0][0] = 0
  ```

* If we **do buy** on day 0, we’re holding a stock we paid `prices[0]` for, still 0 sells:

  ```text
  dpHold[0][0] = -prices[0]
  ```

* Anything that assumes we already completed sells on day 0 (like `dpNotHold[0][1]`) is impossible, so we keep that as a very negative number (`NEG`).

That “NEG” is how we ban illegal states from accidentally looking attractive.

---

### Final answer

On the last day (`n-1`) you want to NOT be holding (because holding means you've spent money but haven't realized profit). So we take the best over all allowed sell counts:

```text
answer = max(dpNotHold[n-1][t]) for t in 0..K
```

---

## Full example walkthrough

Let’s walk an example to see numbers change.
We'll use:

```text
prices = [3, 2, 6, 5, 0, 3]
K = 2 (at most 2 transactions allowed)
```

We expect final answer = 7:

* Buy at 2 → sell at 6 = +4
* Buy at 0 → sell at 3 = +3
  Total = 7

We'll track small tables:

* Row = day
* Col = t (0,1,2)
* We'll keep two tables: `dpNotHold` and `dpHold`

We'll use NEG = "very negative" for impossible.

### Day 0 (price = 3)

Initialize:

* `dpNotHold[0][0] = 0`
  We did nothing, we’re not holding, 0 sells done.

* `dpHold[0][0] = -3`
  We bought at price 3, so profit is `-3`.

* All other entries are NEG because we can't have finished 1 or 2 sells on day 0.

So:

`dpNotHold[0] = [ 0,  NEG,  NEG ]`
`dpHold[0]    = [-3,  NEG,  NEG ]`

### Day 1 (price = 2)

Now we fill `day = 1`.

We'll compute each `t` separately.

#### For t = 0

`dpNotHold[1][0]`:

* stayNotHolding = dpNotHold[0][0] = 0
* sellToday: invalid because t=0 can't come from t-1
  → dpNotHold[1][0] = 0

`dpHold[1][0]`:

* keepHolding = dpHold[0][0] = -3
* buyToday = dpNotHold[0][0] - 2 = 0 - 2 = -2
  → dpHold[1][0] = max(-3, -2) = -2
  (“If I buy, better to think of it as having bought at 2 instead of 3.”)

#### For t = 1

`dpNotHold[1][1]`:

* stayNotHolding = dpNotHold[0][1] = NEG
* sellToday = dpHold[0][0] + 2 = (-3) + 2 = -1
  → dpNotHold[1][1] = -1
  (This is: buy at 3, sell at 2 → lose 1. It's allowed, just dumb.)

`dpHold[1][1]`:

* keepHolding = dpHold[0][1] = NEG
* buyToday = dpNotHold[0][1] - 2 = NEG - 2 = NEG
  → dpHold[1][1] = NEG
  (We can't end day 1 holding a new stock *after* already completing 1 sell, because that would require a full completed sale earlier, which we don't really have profitably yet.)

#### For t = 2

Same story, both are NEG for now.

Summary after Day 1:
`dpNotHold[1] = [ 0,  -1, NEG ]`
`dpHold[1]    = [-2,  NEG, NEG ]`

### Day 2 (price = 6)

#### t = 0

`dpNotHold[2][0]`:

* stayNotHolding = dpNotHold[1][0] = 0
* sellToday = invalid for t=0
  → dpNotHold[2][0] = 0

`dpHold[2][0]`:

* keepHolding = dpHold[1][0] = -2
* buyToday = dpNotHold[1][0] - 6 = 0 - 6 = -6
  → dpHold[2][0] = max(-2, -6) = -2
  (Still best to think we "bought at 2".)

#### t = 1

`dpNotHold[2][1]`:

* stayNotHolding = dpNotHold[1][1] = -1
* sellToday = dpHold[1][0] + 6 = (-2) + 6 = 4
  → dpNotHold[2][1] = max(-1, 4) = 4
  (This is our first GOOD completed transaction:
  buy at 2 (profit -2 when holding), sell at 6 = +4 total.)

`dpHold[2][1]`:

* keepHolding = dpHold[1][1] = NEG
* buyToday = dpNotHold[1][1] - 6 = (-1) - 6 = -7
  → dpHold[2][1] = -7
  (This means: after doing 1 sell but losing money so far (-1), we then buy at 6, leaving us effectively at -7. It's allowed but bad.)

#### t = 2

`dpNotHold[2][2]`:

* stayNotHolding = dpNotHold[1][2] = NEG
* sellToday = dpHold[1][1] + 6 = NEG + 6 = NEG
  → dpNotHold[2][2] = NEG

`dpHold[2][2]`:

* keepHolding = dpHold[1][2] = NEG
* buyToday = dpNotHold[1][2] - 6 = NEG - 6 = NEG
  → dpHold[2][2] = NEG

Summary after Day 2:
`dpNotHold[2] = [ 0,  4,  NEG ]`
`dpHold[2]    = [-2, -7,  NEG ]`

Interpretation:

* `dpNotHold[2][1] = 4` means: after day 2, not holding, and with at most 1 sell done, best profit = 4.
  That's exactly “buy at 2, sell at 6.” ✔️

### Day 3 (price = 5)

#### t = 0

`dpNotHold[3][0]`:

* stayNotHolding = dpNotHold[2][0] = 0
* sellToday = invalid
  → 0

`dpHold[3][0]`:

* keepHolding = dpHold[2][0] = -2
* buyToday = dpNotHold[2][0] - 5 = 0 - 5 = -5
  → max(-2, -5) = -2

#### t = 1

`dpNotHold[3][1]`:

* stayNotHolding = dpNotHold[2][1] = 4
* sellToday = dpHold[2][0] + 5 = (-2) + 5 = 3
  → max(4, 3) = 4
  (So it's actually better to have sold yesterday at 6 than to sell today at 5. Makes sense.)

`dpHold[3][1]`:

* keepHolding = dpHold[2][1] = -7
* buyToday = dpNotHold[2][1] - 5 = 4 - 5 = -1
  → max(-7, -1) = -1
  This is important:
* We already made profit 4 from first transaction.
* Now we "buy" again at price 5.
* After that buy, our net is 4 - 5 = -1.
  So `dpHold[3][1] = -1` means:

> after finishing 1 good sell, we have re-entered the market with effective running profit -1.

#### t = 2

`dpNotHold[3][2]`:

* stayNotHolding = dpNotHold[2][2] = NEG
* sellToday = dpHold[2][1] + 5 = (-7) + 5 = -2
  → max(NEG, -2) = -2
  So, one way to get 2 sells by now is:
* We did one terrible transaction that left us at -7 (buy high, sell low),
* Then sell again at 5, getting to -2. It's legal but ugly.

`dpHold[3][2]`:

* keepHolding = dpHold[2][2] = NEG
* buyToday = dpNotHold[2][2] - 5 = NEG - 5 = NEG
  → NEG

Summary after Day 3:
`dpNotHold[3] = [ 0,  4, -2 ]`
`dpHold[3]    = [-2, -1,  NEG ]`

Now watch what happens on the huge dip Day 4.

### Day 4 (price = 0)

#### t = 0

`dpNotHold[4][0]`:

* stayNotHolding = dpNotHold[3][0] = 0
* sellToday = invalid
  → 0

`dpHold[4][0]`:

* keepHolding = dpHold[3][0] = -2
* buyToday = dpNotHold[3][0] - 0 = 0 - 0 = 0
  → max(-2, 0) = 0
  This means: if we haven't sold yet, we can just "buy now at 0", so our effective running profit after that buy is 0. That's strictly better than "I bought at 2" (-2).

#### t = 1

`dpNotHold[4][1]`:

* stayNotHolding = dpNotHold[3][1] = 4
* sellToday = dpHold[3][0] + 0 = (-2) + 0 = -2
  → max(4, -2) = 4
  (So holding from that earlier buy at 2 and selling at 0 would be dumb vs the 4 we already locked in.)

`dpHold[4][1]`:

* keepHolding = dpHold[3][1] = -1
* buyToday = dpNotHold[3][1] - 0 = 4 - 0 = 4
  → max(-1, 4) = 4
  Huge jump:
* We had profit 4 from first completed transaction (buy 2, sell 6).
* Now we buy again at 0.
* Our effective profit while holding is now 4 (not -1 anymore), because buying at 0 costs basically nothing.
  This is setting us up for the second transaction.

#### t = 2

`dpNotHold[4][2]`:

* stayNotHolding = dpNotHold[3][2] = -2
* sellToday = dpHold[3][1] + 0 = (-1) + 0 = -1
  → max(-2, -1) = -1

`dpHold[4][2]`:

* keepHolding = dpHold[3][2] = NEG
* buyToday = dpNotHold[3][2] - 0 = (-2) - 0 = -2
  → -2

Summary after Day 4:
`dpNotHold[4] = [ 0,  4, -1 ]`
`dpHold[4]    = [ 0,  4, -2 ]`

Most important thing here:

* `dpHold[4][1] = 4` says:

  > After finishing exactly one great sell (profit 4),
  > we then bought at price 0,
  > so we're holding again with an effective running profit of 4.
  > This is the perfect position to cash out a second time.

Now the last day.

### Day 5 (price = 3)

#### t = 0

`dpNotHold[5][0]`:

* stayNotHolding = dpNotHold[4][0] = 0
* sellToday = invalid
  → 0

`dpHold[5][0]`:

* keepHolding = dpHold[4][0] = 0
* buyToday = dpNotHold[4][0] - 3 = 0 - 3 = -3
  → max(0, -3) = 0
  (This isn't meaningful long-term, but it’s consistent.)

#### t = 1

`dpNotHold[5][1]`:

* stayNotHolding = dpNotHold[4][1] = 4
* sellToday = dpHold[4][0] + 3 = 0 + 3 = 3
  → max(4, 3) = 4
  So best with ≤1 sell is still 4 (buy at 2, sell at 6).

`dpHold[5][1]`:

* keepHolding = dpHold[4][1] = 4
* buyToday = dpNotHold[4][1] - 3 = 4 - 3 = 1
  → max(4, 1) = 4
  (“After 1 sell, still holding after maybe buying again at 0 and not selling yet.”)

#### t = 2

`dpNotHold[5][2]`:

* stayNotHolding = dpNotHold[4][2] = -1
* sellToday = dpHold[4][1] + 3 = 4 + 3 = 7
  → max(-1, 7) = 7
  BOOM.
  This is:
* First transaction: buy at 2, sell at 6 = +4
* Second transaction: buy at 0, sell at 3 = +3
  Total = 7

`dpHold[5][2]`:

* keepHolding = dpHold[4][2] = -2
* buyToday = dpNotHold[4][2] - 3 = (-1) - 3 = -4
  → max(-2, -4) = -2  (doesn't matter now)

Final after last day:
`dpNotHold[5] = [ 0, 4, 7 ]`
`dpHold[5]    = [ 0, 4, -2]`

Now we take the best "not holding" value after the last day across all `t ≤ K`:

* max(0, 4, 7) = **7**

That’s the final answer.

---

## Why this DP is clean for interviews

* The dimensions are easy to explain:

  * `day` goes forward in time.
  * `t` = how many sells (transactions) you’ve completed so far.
  * `hold` / `notHold` = whether you’re currently in or out of the market.

* The recurrence lines are intuitive:

  * “To end not holding, either I was not holding, or I sold today.”
  * “To end holding, either I was holding, or I bought today.”

* You never double-sell or double-buy in one day because transitions always look at `day-1`.

* At the end, you only consider the `notHold` states, because un-sold stock is just unrealized profit.

This version is super interview-friendly because you can literally draw two tables on a whiteboard (`dpNotHold` and `dpHold`), fill them row by row, and point to the transitions.


##################

```java
for (int price : prices) {

    // SELL step
    for (int t = k - 1; t >= 0; --t) {
        if (hold[t] != NEG) {
            notHold[t + 1] = Math.max(notHold[t + 1], hold[t] + price);
        }
    }

    // BUY step
    for (int t = 0; t <= k; ++t) {
        hold[t] = Math.max(hold[t], notHold[t] - price);
    }
}
```

Where:

* `hold[t]`  = best profit so far if we are CURRENTLY holding a stock and have completed **t sells**
* `notHold[t]` = best profit so far if we are CURRENTLY not holding a stock and have completed **t sells**
* `t` goes from `0` to `k`
* `NEG` is a big negative number used to mean “impossible state”

Now, I’ll answer each of your questions directly:

---

## 1. “Explain the sell step by updating notHold?”

When we sell a stock today, what happens to our state?

Before selling:

* we were in state `hold[t]`

  * that means: we are holding a stock
  * we’ve already completed `t` sells in the past (so `t` entire transactions are done)
  * we haven’t sold this currently-held stock yet

After selling today:

* we will NOT be holding a stock anymore
* and we’ve just completed one more transaction (the sell finishes a buy→sell pair)
* so our completed sell count goes from `t` to `t+1`
* our new profit is `hold[t] + price` (we cash out at today’s `price`)

So selling “moves you” from:

```text
hold[t]  --->  notHold[t+1]
```

That’s exactly why the update is:

```java
notHold[t + 1] = Math.max(notHold[t + 1], hold[t] + price);
```

We’re saying:

> “If I sell today out of a state where I was holding and had done `t` sells before, what’s the best not-holding profit I can now have with `t+1` sells completed?”

So:

* **Right-hand side** (`hold[t] + price`) = profit after selling today
* **Left-hand side** (`notHold[t+1]`) = best known profit for “not holding with `t+1` sells done”
* `Math.max` = keep the best version

That is literally “perform a sell transaction.”

---

## 2. “Why complicated indexing with `t + 1`?”

Because selling completes a transaction.

Think:

* A “transaction” is completed only when you **sell**.
* You might have bought earlier, but that doesn’t count as a completed transaction yet.
* When you sell, that buy+sell pair is now done. So `t` (the number of completed sells so far) increases by 1.

That’s why “after selling” we end up in `notHold[t+1]`, not `notHold[t]`.

If we wrote to `notHold[t]` instead of `notHold[t+1]`, we'd be lying about how many transactions we’ve finished. We’d be pretending we can make unlimited sells while claiming we still haven't used up a transaction slot.

So `t+1` is not “complicated” for its own sake — it's doing bookkeeping: “I’ve closed one more full transaction.”

---

## 3. “Why start the SELL loop from `t = k - 1` and go down to 0?”

This is the subtle correctness / no-cheating rule.

Let’s say we iterated upward instead:

```java
for (int t = 0; t < k; ++t) {
    notHold[t + 1] = max(notHold[t + 1], hold[t] + price);
}
```

Why is that bad?

Because on the same day, an earlier `t` could update a later `notHold[t+1]`, and then when we reach that later index in the same loop, we might reuse that freshly-updated `notHold` value to effectively “sell twice today” or chain artificial gains.

Concretely, if we loop upward:

1. At `t = 0`, we compute a new `notHold[1]` from `hold[0] + price`.
2. Then at `t = 1`, if we (wrongly) used that new `notHold[1]` to derive something else (e.g. via buy→sell in the same iteration), we’ve just simulated multiple sequential actions happening all on the **same day**.

That is illegal: in the stock problem, each day you can choose one action (buy, sell, or do nothing). You cannot sell twice in a single day and count it as two different completed transactions.

Walking `t` **downward** prevents this contamination.

Here’s why walking downward is safe:

* When we’re at `t = k-1`, we update `notHold[k]` from the **previous** day’s `hold[k-1]`.
* Then we go to `t = k-2`, updating `notHold[k-1]` from the previous day’s `hold[k-2]`.
* Notice: we never touch `notHold[k-1]` *after* we’ve used it for any other computation in this SELL loop. So there's no chance that “today’s sell” result sneaks into another sell again later in the same iteration.

In other words:

* **Reverse iteration makes sure every sell transition for this price uses only states from before today’s sell updates — i.e. from the previous day.**
* This enforces “you can sell at most once per held share per day,” which is exactly what we want.

If we went forward, we’d risk letting today's new `notHold[t+1]` leak into another sell or buy inside the same price step, which would simulate multiple transactions instantaneously.

Short version:

* **Reverse iteration = freeze old world, update into new world without reusing the new stuff right away.**

This is a very common trick in DP whenever your transition updates `dp[next_state]` from `dp[current_state]` and `next_state` has a higher index. You loop high→low to avoid reusing freshly-updated values inappropriately.

---

## 4. “Why do we even have two loops: SELL first, then BUY?”

Two reasons:

### Reason A: Logical ordering

* Selling uses `hold[t]` and writes into `notHold[t+1]`.
* Buying uses `notHold[t]` and writes into `hold[t]`.

Those are different directions of flow:

* SELL: `hold[t]` → `notHold[t+1]`
* BUY:  `notHold[t]` → `hold[t]`

If we mixed them naively in one loop, we might do something nonsensical like:

* Buy and sell on the same day in a way that acts like two separate full transactions with zero time in between.

By separating SELL pass and BUY pass, we conceptually say:

1. “Given what I could have been holding coming into today, what's the best not-holding state if I cash out today?”
2. “Given what I could have been not-holding coming into today, what's the best holding state if I buy today?”

It keeps the semantics clean: each transition is from yesterday → today, not chaining multiple flips within the same “today.”

### Reason B: Dependency safety

* SELL writes `notHold[t+1]`, BUY writes `hold[t]`.
  Doing SELL first then BUY helps us align with the interpretation “after considering sales today, also consider that I could be buying today for going forward,” while respecting the reverse/forward iteration rules and not cross-pollinating states in illegal ways.

(You could also justify BUY first then SELL with careful loop ordering and indexing, but the important thing is: you must be consistent and avoid reusing the freshly updated states in the same day to simulate multiple sequential trades on the same price tick.)

---

## 5. Tiny sanity example to see why reverse matters

Suppose:

* `k = 2`
* Current arrays before processing today’s price:

  * `hold[0] = 10`  (we’re holding, with 0 completed sells, profit 10)
  * `hold[1] = 20`  (we’re holding, with 1 completed sell already, profit 20)
  * `notHold[0] = 0`
  * `notHold[1] = 5`
  * `notHold[2] = 9`

Say today’s price = 3.

**Correct behavior:**

* From `hold[1]` we can sell and become `notHold[2] = max(old notHold[2], hold[1] + price) = max(9, 20+3) = 23`
* From `hold[0]` we can sell and become `notHold[1] = max(old notHold[1], hold[0] + price) = max(5, 10+3) = 13`

In other words:

```text
hold[1] (profit 20) -> sell -> notHold[2] = 23
hold[0] (profit 10) -> sell -> notHold[1] = 13
```

If we iterate `t` downward:

* First t=1 updates notHold[2].
* Then t=0 updates notHold[1].
* We never accidentally let that new 13 get “resold” again, because we’re going downward.

If we went upward:

* t=0 would update notHold[1] = 13.
* Then when we get to t=1, if our code wasn’t careful, it might be tempted to treat that new notHold[1]=13 as “cash after 1 sell,” buy again, then sell again *in the same day*, effectively chaining two sells using just today’s price. That’s illegal in the real problem.

Reverse order blocks that cheat.

---

## TL;DR answers to your exact questions

**Q: Explain the sell step by updating `notHold`?**
When you sell, you are no longer holding, and you have completed one more full transaction. So a sell moves you from `hold[t]` to `notHold[t+1]` with profit `hold[t] + price`.

**Q: Why use `t+1`?**
Because selling finishes a buy→sell pair and increases the number of completed transactions by 1. After selling, you’ve used up one of your allowed transactions.

**Q: Why start from `k - 1` and go down to `0`?**
To avoid reusing the just-updated `notHold[t+1]` in the same iteration to “sell again” or “buy and immediately resell” on the same day. Going downward guarantees each update is based only on states from *before* today’s operations.

**Q: Why reverse iteration at all?**
Reverse iteration enforces the rule: you get at most one real sell per share per day, and prevents illegal chain reactions inside one day’s update that would effectively simulate doing multiple sequential trades at the same single price point.
*/

// class Solution {
//     public int maxProfit(int k, int[] prices) {
//         int n = prices.length;
//         if (n <= 1 || k == 0) return 0;

//         // Optimization: if k >= n/2, it's equivalent to unlimited transactions.
//         // Just grab every upward move.
//         if (k >= n / 2) {
//             int greedy = 0;
//             for (int i = 1; i < n; i++) {
//                 if (prices[i] > prices[i - 1]) {
//                     greedy += prices[i] - prices[i - 1];
//                 }
//             }
//             return greedy;
//         }

//         // We only need k transactions (buys+ sells), track up to k sells completed.
//         // notHold[t] = best profit so far with t completed sells, not holding stock
//         // hold[t]    = best profit so far with t completed sells, holding stock
//         // t ranges 0..k
//         int[] notHold = new int[k + 1];
//         int[] hold    = new int[k + 1];

//         // Initialize:
//         // notHold[t] starts at 0 profit for all t (you can always "do nothing")
//         // hold[t] should start at very negative (like -infinity) because
//         // initially you are NOT holding a stock, so "best state where I *am* holding"
//         // is impossible until you buy.
//         Arrays.fill(hold, Integer.MIN_VALUE / 4);

//         // Process each day's price
//         for (int price : prices) {

//             // Important:
//             // We'll update sell transitions first (t from k-1 down to 0) so we don't
//             // reuse updated values in the same iteration in a way that creates
//             // illegal multiple sells on the same day.

//             // SELL step:
//             // selling turns a state (hold[t]) into notHold[t+1]
//             for (int t = k - 1; t >= 0; --t) {
//                 if (hold[t] != Integer.MIN_VALUE / 4) {
//                     notHold[t + 1] = Math.max(notHold[t + 1], hold[t] + price);
//                 }
//             }

//             // BUY step:
//             // buying turns a state (notHold[t]) into hold[t]
//             for (int t = 0; t <= k; ++t) {
//                 // we can either keep holding, or buy new today from notHold[t]
//                 hold[t] = Math.max(hold[t], notHold[t] - price);
//             }
//         }

//         // Answer: best profit after at most k completed sells, not holding stock
//         // i.e. max over notHold[0..k], but it's always nondecreasing in t, so just notHold[k]
//         return notHold[k];
//     }
// }