// Method 1: Top-Down 2D DP
/*
## 1. Problem restated in DP terms

You get an array `prices`, where:

* `prices[i]` = stock price on day `i`
* You can do as many transactions as you like (buy → sell), but:

  * You must **sell before you can buy again**.
  * After you **sell**, you **cannot buy on the next day** (cooldown for 1 day).

Goal: **maximize total profit**.

So this isn’t just “buy once, sell once”; it’s “buy/sell many times” with a cooldown constraint.

---

## 2. Core DP idea: think in terms of *states*

At any day `day`, there are two logical states you can be in:

1. You are **allowed to buy** (you do *not* currently hold a stock).
2. You are **not allowed to buy** because you’re currently **holding** a stock (so you can sell or hold).

Let’s encode this as:

* `canBuy = 1` → you **do not** hold a stock, and you are allowed to buy.
* `canBuy = 0` → you **do** hold a stock (so you can sell or hold).

We define:

> `dp(day, canBuy)` = **maximum profit** you can achieve from `day` onward, given the current state (whether you can buy or are holding).

We’ll solve this recursively with memoization (top-down).

We start at:

```java
day = 0
canBuy = 1   // at the beginning, we own nothing and are allowed to buy
```

---

## 3. Base case

If `day` is past the last index:

```java
if (day >= prices.length) return 0;
```

There are no more days left → no more profit can be made.

This also automatically handles cooldown jumps that go beyond the end (e.g., `day + 2`).

---

## 4. Transitions (the heart of it)

### Case A: `canBuy == 1` (we **do not** hold a stock)

We have two choices:

1. **Buy** on this day:

   * We pay `prices[day]` → subtract from profit.
   * We move to the next day with `canBuy = 0` (because now we’re holding a stock):

     ```text
     buyProfit = -prices[day] + dp(day + 1, 0)
     ```

2. **Skip** (do nothing today):

   * Stay in the “can buy” state on the next day:

     ```text
     skipProfit = dp(day + 1, 1)
     ```

So:

```text
dp(day, 1) = max(buyProfit, skipProfit)
```

---

### Case B: `canBuy == 0` (we are **holding** a stock)

We also have two choices:

1. **Sell** on this day:

   * We receive `prices[day]` → add to profit.
   * Because of cooldown, we **cannot buy on the next day**.
   * So we jump to `day + 2` with `canBuy = 1` (we no longer hold a stock, and that day is past the cooldown):

     ```text
     sellProfit = prices[day] + dp(day + 2, 1)
     ```

2. **Hold** (do nothing):

   * Keep holding the stock and move to next day, still holding:

     ```text
     holdProfit = dp(day + 1, 0)
     ```

So:

```text
dp(day, 0) = max(sellProfit, holdProfit)
```

---

## 5. Memoization structure

We have two parameters: `day` and `canBuy`.

* `day` can be from `0` to `n-1`
* `canBuy` is `0` or `1`

So we create a 2D memo:

```java
Integer[][] memo = new Integer[n][2];
// memo[day][canBuy] stores the best profit from that state
```

We use `null` to indicate “not computed yet”.

The pattern:

```java
if (memo[day][canBuy] != null) return memo[day][canBuy];
// compute profit ...
memo[day][canBuy] = profit;
return profit;
```

Time complexity: **O(n · 2) = O(n)**
Space: **O(n · 2)** for memo + recursion stack.

---

## 7. Thorough example walkthrough: `[1,2,3,0,2]`

This is the classic example. The correct answer is **3**.

One optimal sequence:

* Day 0: price 1 → **buy**
* Day 1: price 2 → **sell** → profit = 1
* Day 2: cooldown
* Day 3: price 0 → **buy**
* Day 4: price 2 → **sell** → profit = 2 more
  Total = 1 + 2 = 3

Now let’s see how `dp(0,1)` ends up as 3.

I’ll write `f(day, canBuy)` as shorthand.

Remember:

* `canBuy = 1` → not holding, allowed to buy.
* `canBuy = 0` → holding.

### Step 1: Start at `f(0, 1)`

Day 0, price = 1, we can buy.

Two options:

1. **Buy**:

   ```text
   buy = -1 + f(1, 0)
   ```

2. **Skip**:

   ```text
   skip = f(1, 1)
   ```

So:

```text
f(0,1) = max(-1 + f(1,0), f(1,1))
```

We need `f(1,0)` and `f(1,1)`.

---

### Step 2: Compute `f(4,1)` and `f(4,0)` first (we'll need them later)

Let’s jump to the end because recursion will eventually ask those.

#### `f(4,1)` – Day 4, can buy

Day 4, price = 2, last day.

Options:

* Buy: `-2 + f(5,0) = -2 + 0 = -2` (no time to sell afterwards, so bad)
* Skip: `f(5,1) = 0`

So:

```text
f(4,1) = max(-2, 0) = 0
```

#### `f(4,0)` – Day 4, holding

Options:

* Sell: `2 + f(6,1) = 2 + 0 = 2`
* Hold: `f(5,0) = 0`

So:

```text
f(4,0) = max(2, 0) = 2
```

---

### Step 3: `f(3,1)` – Day 3, can buy

Day 3, price = 0:

* Buy: `0 + f(4,0) = 0 + 2 = 2`
* Skip: `f(4,1) = 0`

So:

```text
f(3,1) = max(2, 0) = 2
```

This corresponds to: buy at 0 on day 3, then later sell on day 4 for profit 2.

---

### Step 4: `f(3,0)` – Day 3, holding

Day 3, price = 0, we’re already holding:

* Sell: `0 + f(5,1) = 0 + 0 = 0`
* Hold: `f(4,0) = 2`

So:

```text
f(3,0) = max(0, 2) = 2
```

If we’re holding at day 3, best is not to sell at 0, but to hold and sell at price 2 on day 4.

---

### Step 5: `f(2,1)` – Day 2, can buy

Day 2, price = 3:

* Buy: `-3 + f(3,0)`
  We computed `f(3,0) = 2`, so:

  ```text
  buy = -3 + 2 = -1
  ```

* Skip: `f(3,1) = 2`

So:

```text
f(2,1) = max(-1, 2) = 2
```

It’s better to skip buying at price 3 and instead use the later opportunity (buy at 0 on day 3 → profit 2).

---

### Step 6: `f(2,0)` – Day 2, holding

Day 2, price = 3:

* Sell: `3 + f(4,1) = 3 + 0 = 3`
* Hold: `f(3,0) = 2`

So:

```text
f(2,0) = max(3, 2) = 3
```

Meaning: if we are holding a stock at day 2, best is to sell it now for profit 3 and then we’re done (no good trades after due to cooldown and prices).

---

### Step 7: `f(1,1)` – Day 1, can buy

Day 1, price = 2:

* Buy: `-2 + f(2,0)`
  We know `f(2,0) = 3`:

  ```text
  buy = -2 + 3 = 1
  ```

* Skip: `f(2,1) = 2`

So:

```text
f(1,1) = max(1, 2) = 2
```

Interpretation: from day1 in “can buy” state, best is to **skip** buying at 2, and later buy at 0 (day3) and sell at 2 (day4) for profit 2.

---

### Step 8: `f(1,0)` – Day 1, holding

This is the state we enter if we bought on day0.

Day 1, price = 2:

* Sell: `2 + f(3,1)`
  We know `f(3,1) = 2`, so:

  ```text
  sell = 2 + 2 = 4
  ```

* Hold: `f(2,0) = 3`

So:

```text
f(1,0) = max(4, 3) = 4
```

If we’re holding at day1, best plan:

* sell on day1 for profit 2
* cooldown on day2
* buy at day3 for 0
* sell at day4 for 2
  Total **4** future profit from the perspective of already holding.

(This 4 is *relative* to already having paid for the stock earlier.)

---

### Step 9: Finally `f(0,1)` – our start state

Recall:

```text
f(0,1) = max( -1 + f(1,0), f(1,1) )
```

We know:

* `f(1,0) = 4`
* `f(1,1) = 2`

So:

* Buy path:

  ```text
  -1 + f(1,0) = -1 + 4 = 3
  ```

* Skip path: `2`

So:

```text
f(0,1) = max(3, 2) = 3
```

That’s the correct answer.

The interpretation of the chosen path (buy then sell) is:

* Day 0: buy at 1
* Day 1: sell at 2 → profit +1
* Day 2: cooldown
* Day 3: buy at 0
* Day 4: sell at 2 → profit +2
  Total profit: 3.

---

## 8. Recap

Top-down DP for 309 looks like this:

* **State:** `(day, canBuy)`

  * `day` = which index in `prices` you are at.
  * `canBuy = 1` → you don’t hold, you may buy.
  * `canBuy = 0` → you do hold, you may sell or hold.
* **Base:** `day >= n` → 0.
* **Transitions:**

  * If `canBuy == 1`:

    * Buy: `-prices[day] + dp(day+1, 0)`
    * Skip: `dp(day+1, 1)`
  * If `canBuy == 0`:

    * Sell: `prices[day] + dp(day+2, 1)` (cooldown!)
    * Hold: `dp(day+1, 0)`
* **Memoize** every `(day, canBuy)` so we don’t recompute states.
* Answer is `dp(0, 1)`.
*/
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        Integer[][] memo = new Integer[n][2];
        
        // Start at day 0 with "canBuy = 1" (we don't own any stock yet)
        return dp(prices, memo, 0, 1);
    }

    private int dp(int[] prices, Integer[][] memo, int day, int canBuy) {
        // Base case: beyond last day
        if (day >= prices.length) {
            return 0;
        }

        // Memoization check
        if (memo[day][canBuy] != null) {
            return memo[day][canBuy];
        }

        int profit;
        if (canBuy == 1) {
            // Option 1: buy today
            int buy = -prices[day] + dp(prices, memo, day + 1, 0);
            // Option 2: skip today
            int skip = dp(prices, memo, day + 1, 1);
            profit = Math.max(buy, skip);
        } else {
            // We're holding a stock
            // Option 1: sell today (and cooldown next day)
            int sell = prices[day] + dp(prices, memo, day + 2, 1);
            // Option 2: hold
            int hold = dp(prices, memo, day + 1, 0);
            profit = Math.max(sell, hold);
        }

        memo[day][canBuy] = profit;
        return profit;
    }
}






// Method 2: Bottom-Up 2D DP
/*
## 1. Recall the top-down state

From the top-down version we had:

> `dp(day, canBuy)` = **maximum profit** you can earn starting from `day`
> where:
>
> * `canBuy = 1` → you **do not** hold a stock (you’re allowed to buy),
> * `canBuy = 0` → you **do** hold a stock (you’re allowed to sell or hold).

Transitions:

* **Base:**

  ```text
  if day >= n: return 0
  ```

* **If canBuy == 1** (no stock):

  ```text
  buy  = -prices[day] + dp(day+1, 0)
  skip =  dp(day+1, 1)
  dp(day,1) = max(buy, skip)
  ```

* **If canBuy == 0** (holding stock):

  ```text
  sell =  prices[day] + dp(day+2, 1)   // cooldown
  hold =  dp(day+1, 0)
  dp(day,0) = max(sell, hold)
  ```

Answer: `dp(0, 1)`.

Bottom-up will use **exactly the same DP state**, we’ll just fill it iteratively.

---

## 2. Bottom-up DP definition

We define a 2D array:

```text
dp[i][canBuy]
```

Where:

* `i` = day index (`0..n` or a bit beyond),
* `canBuy` = 0 or 1 with the same meaning as above.

Interpretation:

> `dp[i][1]` = max profit from **day i** onward if you **can buy** (no stock).
>
> `dp[i][0]` = max profit from **day i** onward if you’re **holding** a stock.

We want `dp[0][1]`.

### Base case in bottom-up form

From top-down we had:

```text
if day >= n: return 0
```

So in bottom-up we set:

```text
dp[n][0] = 0
dp[n][1] = 0
```

And also any day > n is effectively 0. Easiest trick: allocate a bit more space so `dp[i+2]` is always valid.

We can allocate:

```java
int[][] dp = new int[n + 2][2];
```

Then `dp[n][*]` and `dp[n+1][*]` are 0 by default (Java initializes arrays with 0).

---

## 3. Bottom-up transitions

We will fill `dp` **backwards** in time: from day `n-1` down to `0`.

At each day `i`:

### 3.1. When `canBuy = 1` (no stock)

Same logic as top-down:

```text
buy  = -prices[i] + dp[i+1][0];
skip =  dp[i+1][1];
dp[i][1] = max(buy, skip);
```

### 3.2. When `canBuy = 0` (holding stock)

Same logic:

```text
sell = prices[i] + dp[i+2][1];   // cooldown
hold = dp[i+1][0];
dp[i][0] = max(sell, hold);
```

Finally, answer is `dp[0][1]`.

* Time: **O(n)**
* Space: **O(n)** (you can compress further to O(1) with a few variables, but this is clean for understanding).

---

## 5. Thorough example walkthrough: `[1, 2, 3, 0, 2]`

Let’s walk day by day.

```text
prices = [1, 2, 3, 0, 2]
index    0  1  2  3  4
n = 5
```

We allocate:

```text
dp[0..6][0..1]   // n+2 = 7
```

All zeros initially:

```text
for all i: dp[i][0] = 0, dp[i][1] = 0
```

Remember:

* `dp[i][1]` = best profit from day i if we can buy.
* `dp[i][0]` = best profit from day i if we are holding stock.

We will fill `i = 4 → 0`.

---

### Day 5 and 6: implicit base

We don’t explicitly touch them; they remain 0:

```text
dp[5][0] = 0, dp[5][1] = 0
dp[6][0] = 0, dp[6][1] = 0
```

These correspond to “past the end → no more profit”.

---

### i = 4 (last real day), price = 2

We compute `dp[4][1]` and `dp[4][0]`.

#### State: canBuy = 1 (not holding)

Options:

* Buy at price 2:

  ```text
  buy  = -prices[4] + dp[5][0]
       = -2 + 0
       = -2
  ```

* Skip:

  ```text
  skip = dp[5][1] = 0
  ```

So:

```text
dp[4][1] = max(-2, 0) = 0
```

Interpretation: from the last day if you’re not holding, it’s better to do nothing than to buy (you have no time to sell).

#### State: canBuy = 0 (holding)

Options:

* Sell at price 2:

  ```text
  sell = prices[4] + dp[6][1]
       = 2 + 0
       = 2
  ```

* Hold:

  ```text
  hold = dp[5][0] = 0
  ```

So:

```text
dp[4][0] = max(2, 0) = 2
```

Interpretation: if you’re holding a stock on day 4, best is to sell it for profit 2.

Current dp:

```text
i   dp[i][0]   dp[i][1]
4     2          0
5     0          0
6     0          0
```

---

### i = 3, price = 0

#### canBuy = 1 (not holding)

* Buy:

  ```text
  buy = -0 + dp[4][0]
      = 0 + 2
      = 2
  ```

* Skip:

  ```text
  skip = dp[4][1] = 0
  ```

So:

```text
dp[3][1] = max(2, 0) = 2
```

Interpretation: from day 3, if you’re not holding, best is to **buy at 0** and then later sell at day 4 for profit 2.

#### canBuy = 0 (holding)

* Sell:

  ```text
  sell = prices[3] + dp[5][1]
       = 0 + 0
       = 0
  ```

* Hold:

  ```text
  hold = dp[4][0] = 2
  ```

So:

```text
dp[3][0] = max(0, 2) = 2
```

Interpretation: if you’re already holding at day 3, don’t sell at 0; just hold and sell at 2 on day 4.

Current dp:

```text
i   dp[i][0]   dp[i][1]
3     2          2
4     2          0
5     0          0
6     0          0
```

---

### i = 2, price = 3

#### canBuy = 1 (not holding)

* Buy:

  ```text
  buy = -3 + dp[3][0]
      = -3 + 2
      = -1
  ```

* Skip:

  ```text
  skip = dp[3][1] = 2
  ```

So:

```text
dp[2][1] = max(-1, 2) = 2
```

Interpretation: from day 2, if you’re not holding, it’s better to **not** buy at price 3; instead wait and use the day 3–4 opportunity (buy at 0, sell at 2) for profit 2.

#### canBuy = 0 (holding)

* Sell:

  ```text
  sell = prices[2] + dp[4][1]
       = 3 + 0
       = 3
  ```

* Hold:

  ```text
  hold = dp[3][0] = 2
  ```

So:

```text
dp[2][0] = max(3, 2) = 3
```

Interpretation: if you’re already holding at day 2, best is to sell at 3 now (profit 3 from here).

Current dp:

```text
i   dp[i][0]   dp[i][1]
2     3          2
3     2          2
4     2          0
5     0          0
6     0          0
```

---

### i = 1, price = 2

#### canBuy = 1 (not holding)

* Buy:

  ```text
  buy = -2 + dp[2][0]
      = -2 + 3
      = 1
  ```

* Skip:

  ```text
  skip = dp[2][1] = 2
  ```

So:

```text
dp[1][1] = max(1, 2) = 2
```

Interpretation: from day 1, if you’re not holding, it’s actually better to **skip** buying at 2 and instead take the day 3–4 trade (profit 2).

#### canBuy = 0 (holding)

This corresponds to: you bought earlier (e.g., at day 0) and are holding at day 1.

* Sell:

  ```text
  sell = prices[1] + dp[3][1]
       = 2 + 2
       = 4
  ```

* Hold:

  ```text
  hold = dp[2][0] = 3
  ```

So:

```text
dp[1][0] = max(4, 3) = 4
```

Interpretation: if you’re holding at day 1, the best from here is:

* sell at 2 now (+2),
* cooldown on day 2,
* then from day 3 onward, take the 0→2 trade (+2),
  for total +4 (from this “holding” perspective).

Current dp:

```text
i   dp[i][0]   dp[i][1]
1     4          2
2     3          2
3     2          2
4     2          0
5     0          0
6     0          0
```

---

### i = 0, price = 1

This is our starting day.

#### canBuy = 1 (starting state: not holding)

* Buy:

  ```text
  buy = -1 + dp[1][0]
      = -1 + 4
      = 3
  ```

* Skip:

  ```text
  skip = dp[1][1] = 2
  ```

So:

```text
dp[0][1] = max(3, 2) = 3
```

This is the final answer.

Interpretation:

* If we **buy at 1** on day 0 and then optimally act afterward:

  * Day 1: sell at 2 → +1
  * Day 2: cooldown
  * Day 3: buy at 0
  * Day 4: sell at 2 → +2
    Total = 3.

* If we **skip** day 0 and start optimally at day 1, we get only 2 (using just the day 3–4 trade).

Thus max profit = **3**.

For completeness we could also compute `dp[0][0]` (holding on day0, which isn’t actually a valid start state unless we assume we bought before the timeline), but we don’t need it. Our entry state is `dp[0][1]`.

Final DP snapshot:

```text
i   dp[i][0]   dp[i][1]
0     ?          3  <-- answer (starting here with canBuy)
1     4          2
2     3          2
3     2          2
4     2          0
5     0          0
6     0          0
```

---

## 6. Summary

Bottom-up DP for 309 using the same state as top-down:

* **State:** `dp[i][canBuy]`

  * `i` = day (0..n)
  * `canBuy = 1` → not holding a stock, can buy.
  * `canBuy = 0` → holding a stock.

* **Base:** `dp[n][*] = 0`, and we allocate `n+2` to safely use `i+2`.

* **Transition (fill i from n-1 downto 0):**

  ```text
  // canBuy = 1
  dp[i][1] = max(-prices[i] + dp[i+1][0], dp[i+1][1]);

  // canBuy = 0
  dp[i][0] = max(prices[i] + dp[i+2][1], dp[i+1][0]);
  ```

* **Answer:** `dp[0][1]`.

If you’d like next, I can show how to compress this DP to **O(1)** space using just a few variables (`aheadBuy`, `aheadSell`, etc.) and relate them back to this table.

*/

// class Solution {
//     public int maxProfit(int[] prices) {
//         int n = prices.length;
//         // dp[i][0] = max profit from day i if you are holding a stock
//         // dp[i][1] = max profit from day i if you can buy (not holding)
//         int[][] dp = new int[n + 2][2]; // extra 2 to safely use i+2

//         // Base: dp[n][*] and dp[n+1][*] are 0 by default

//         // Fill from last day down to day 0
//         for (int i = n - 1; i >= 0; i--) {
//             // canBuy = 1: we don't hold a stock now
//             int buy  = -prices[i] + dp[i + 1][0];
//             int skip =  dp[i + 1][1];
//             dp[i][1] = Math.max(buy, skip);

//             // canBuy = 0: we are holding a stock now
//             int sell = prices[i] + dp[i + 2][1];  // cooldown goes to i+2
//             int hold = dp[i + 1][0];
//             dp[i][0] = Math.max(sell, hold);
//         }

//         // Start at day 0 with permission to buy (not holding anything)
//         return dp[0][1];
//     }
// }
