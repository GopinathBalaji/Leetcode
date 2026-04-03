// Method 1: Top-Down 2D DP
/*
# WHY NO FOR-LOOP FOR THESE TYPES OF PROBLEMS

### Problem 1: The `for` loop + two recursive calls **double counts** (and worse)

In Coin Change II, we want:

> Number of **combinations** (order doesn’t matter) to make `amount`.

Standard pattern:

* At each `idx`, you decide:

  * **take** `coins[idx]` (stay at same `idx` to reuse it), or
  * **skip** `coins[idx]` (move to `idx+1`).

Your code instead does:

```java
for (int i = idx; i < coins.length; i++) {
    ways += dp(... remaining - coins[i], i);     // include coin i
    ways += dp(... remaining,          i + 1);   // skip coin i
}
```

This means **inside a single `dp` call**, you:

* Loop over all coins `i = idx..end`,
* For each coin `i`, you:

  * Branch “take”
  * And branch “skip to i+1”

But then the loop increments `i` and you again “skip” to `i+1`, etc. This creates **many overlapping skip branches** and counts the same combination multiple times.

For example, with `amount = 5`, `coins = [1,2]`:

At state `(remaining=5, idx=0)`:

* `i = 0`:

  * `dp(5 - 1, idx=0)`  // use coin 1
  * `dp(5, idx=1)`      // skip coin 1
* `i = 1`:

  * `dp(5 - 2, idx=1)`  // use coin 2
  * `dp(5, idx=2)`      // skip coin 2

But `dp(5, idx=1)` is also itself looping and calling `dp(5, idx=2)` etc. You end up exploring “skip coin 1 then skip coin 2” multiple times in different paths, and similarly for combinations like `1+2+2`, etc. That leads to **overcounting**.

The correct structure is **not**:

* “Loop over i from `idx` and inside that do take/skip”

It should be:

* At each `idx`, just one decision:

  * **take** `coins[idx]` (stay on `idx`)
  * **skip** `coins[idx]` (go to `idx+1`)

No loop over all coins inside the `dp`.

---

### Problem 2: No memoization

Your DP is **top-down** in spirit, but there is no memo table. For this problem, the recursion tree without memoization is exponential and will time out on large inputs.

You want to memoize states keyed by:

> `(idx, remaining)`

I.e., `dp[idx][remaining]`.


## 2. Correct top-down state & recurrence

We want to count **combinations** using coins with unlimited supply.

A standard top-down DP definition:

> `dp(idx, remaining)` = number of ways to form `remaining` using coins from index `idx..end` (i.e., `coins[idx]`, `coins[idx+1]`, …).

We will allow **unlimited copies** of `coins[idx]` by staying at `idx` when we “take” it.

### Base cases

1. If `remaining == 0`:

   We’ve formed the target amount exactly:

   ```java
   if (remaining == 0) return 1;
   ```

2. If `idx == coins.length` (no coins left) **and** `remaining > 0`:

   We can’t make positive `remaining` with no coins:

   ```java
   if (idx == coins.length) return 0;
   ```

3. If `remaining < 0`:

   Overshoot → no valid way:

   ```java
   if (remaining < 0) return 0;
   ```

(You can combine conditions as suits you; the key is these three “stop points.”)

### Recurrence: two choices at each `idx`

At coin index `idx`:

1. **Skip** this coin:

   ```text
   waysSkip = dp(idx + 1, remaining)
   ```

2. **Take** this coin at least once:

   * We subtract `coins[idx]` from `remaining`.
   * Stay at `idx` because we can use the same coin again:

   ```text
   waysTake = dp(idx, remaining - coins[idx])
   ```

Total ways:

```text
dp(idx, remaining) = waysSkip + waysTake
```

This structure **avoids permutations** because once you move `idx` forward, you never go back to earlier coins.

---

### Memoization

We store results for `(idx, remaining)` in a 2D table:

```java
Integer[][] memo = new Integer[coins.length][amount + 1];
```

Pattern:

```java
if (memo[idx][remaining] != null) return memo[idx][remaining];

int ans = ...; // compute with recursion
memo[idx][remaining] = ans;
return ans;
```

## 4. Thorough example walkthrough: `amount = 5`, `coins = [1, 2, 5]`

All combinations (order doesn’t matter) are:

1. `1 + 1 + 1 + 1 + 1`
2. `1 + 1 + 1 + 2`
3. `1 + 2 + 2`
4. `5`

So the answer should be `4`.

Let’s see how `dfs(0, 5)` gets there.

We’ll use notation: `dfs(idx, remaining)`.

### Top call

```text
dfs(0, 5)   // can use coins[0..2] = [1,2,5]
```

At `idx = 0`, coin = 1.

Two choices:

* Skip coin 1 → `dfs(1, 5)`
* Take coin 1 → `dfs(0, 4)`

So:

```text
dfs(0, 5) = dfs(1, 5) + dfs(0, 4)
```

---

### 4.1. Evaluate `dfs(1, 5)` (skip coin 1)

Now we’re at `idx = 1`, coin = 2, remaining = 5.

Choices:

* Skip coin 2 → `dfs(2, 5)`
* Take coin 2 → `dfs(1, 3)` (since 5 - 2 = 3)

So:

```text
dfs(1, 5) = dfs(2, 5) + dfs(1, 3)
```

#### 4.1.a. `dfs(2, 5)` (only coin 5 available)

`idx = 2`, coin = 5.

Choices:

* Skip coin 5 → `dfs(3, 5)`
* Take coin 5 → `dfs(2, 0)`

So:

```text
dfs(2, 5) = dfs(3, 5) + dfs(2, 0)
```

* `dfs(3, 5)` → `idx == coins.length` and `remaining > 0` → 0 ways.
* `dfs(2, 0)` → `remaining == 0` → 1 way (using one 5).

Thus:

```text
dfs(2, 5) = 0 + 1 = 1
```

This corresponds to combination: `[5]`.

#### 4.1.b. `dfs(1, 3)` (only 2 and 5 allowed; remaining = 3)

At `idx = 1`, coin = 2, remaining = 3.

Choices:

* Skip 2 → `dfs(2, 3)`
* Take 2 → `dfs(1, 1)`

So:

```text
dfs(1, 3) = dfs(2, 3) + dfs(1, 1)
```

* `dfs(2, 3)`: with coin 5 only, remaining 3:

  * Skip 5 → `dfs(3, 3)` → 0
  * Take 5 → `dfs(2, -2)` → remaining < 0 → 0

  So `dfs(2, 3) = 0`.

* `dfs(1, 1)`:

  At `idx = 1`, coin = 2, remaining = 1.

  * Skip 2 → `dfs(2, 1)`
  * Take 2 → `dfs(1, -1)` → 0

  `dfs(2, 1)` (coin 5 only, remaining = 1):

  * Skip 5 → `dfs(3, 1)` → 0
  * Take 5 → `dfs(2, -4)` → 0

  So `dfs(2, 1) = 0`, and `dfs(1,1) = 0 + 0 = 0`.

Thus:

```text
dfs(1, 3) = 0 + 0 = 0
```

So from `dfs(1, 5)`:

```text
dfs(1, 5) = dfs(2,5) + dfs(1,3) = 1 + 0 = 1
```

This 1 corresponds to the combination `[5]` (using coin 5 only, since we skipped coin 1 and then coin 2).

---

### 4.2. Evaluate `dfs(0, 4)` (we took one coin 1)

Now `idx = 0`, coin = 1, remaining = 4.

Choices:

* Skip 1 → `dfs(1, 4)`
* Take 1 → `dfs(0, 3)`

So:

```text
dfs(0, 4) = dfs(1, 4) + dfs(0, 3)
```

#### 4.2.a. `dfs(1, 4)` (skip coin 1)

At `idx=1`, coin 2, remaining 4.

Choices:

* Skip 2 → `dfs(2, 4)`
* Take 2 → `dfs(1, 2)`

So:

```text
dfs(1, 4) = dfs(2,4) + dfs(1,2)
```

* `dfs(2, 4)` (coin 5 only, remaining 4):

  * Skip 5 → `dfs(3,4)` → 0
  * Take 5 → `dfs(2,-1)` → 0

  So `dfs(2,4) = 0`.

* `dfs(1,2)`:

  At `idx=1`, coin 2, remaining 2:

  * Skip 2 → `dfs(2,2)`
  * Take 2 → `dfs(1,0)`

  `dfs(2,2)`:

  * Skip 5 → `dfs(3,2)` → 0
  * Take 5 → `dfs(2,-3)` → 0
    ⇒ `dfs(2,2) = 0`.

  `dfs(1,0)`:

  * Remaining = 0 → return 1 (we formed amount 2 with coin 2 once).

  So:

  ```text
  dfs(1,2) = 0 + 1 = 1
  ```

Thus:

```text
dfs(1,4) = 0 + 1 = 1
```

This corresponds to combination `[2,2]` (using two 2’s, since we skipped 1).

#### 4.2.b. `dfs(0,3)` (we already used one 1, remaining 3)

At `idx=0`, coin 1, remaining 3.

Choices:

* Skip 1 → `dfs(1,3)`
* Take 1 → `dfs(0,2)`

We already computed `dfs(1,3) = 0` earlier.

So:

```text
dfs(0,3) = 0 + dfs(0,2)
```

Now compute `dfs(0,2)`:

At `idx=0`, remaining 2.

* Skip 1 → `dfs(1,2)`
* Take 1 → `dfs(0,1)`

We know `dfs(1,2) = 1` (that was `[2]`).

Now `dfs(0,1)`:

At `idx=0`, remaining 1.

* Skip 1 → `dfs(1,1)` (we computed earlier = 0)
* Take 1 → `dfs(0,0)` → 1 (exact amount formed with one 1)

So:

```text
dfs(0,1) = 0 + 1 = 1
```

Therefore:

```text
dfs(0,2) = dfs(1,2) + dfs(0,1) = 1 + 1 = 2
```

Interpretation:

* `dfs(1,2) = 1` → combination `[2]`
* `dfs(0,1) = 1` → combination `[1,1]` (but inside this branch we already used some 1s)

So from `dfs(0,3)`:

```text
dfs(0,3) = 0 + dfs(0,2) = 2
```

The 2 ways at `remaining=3` using coins starting at 1 are:

* `[1,1,1]`
* `[1,2]`

And from `dfs(0,4)`:

```text
dfs(0,4) = dfs(1,4) + dfs(0,3) = 1 + 2 = 3
```

The 3 ways for remaining 4 using coins starting at 1:

* `[2,2]`        (from dfs(1,4))
* `[1,1,1,1]`    (from dfs(0,3) and then dfs(0,2)/dfs(0,1))
* `[1,1,2]`      (also from those branches)

---

### 4.3. Back to top: `dfs(0,5)`

Recall:

```text
dfs(0,5) = dfs(1,5) + dfs(0,4)
```

We computed:

* `dfs(1,5) = 1`  → combination `[5]`
* `dfs(0,4) = 3`  → combinations:

  * `[1,1,1,1]`
  * `[1,1,2]`
  * `[2,2]`

So:

```text
dfs(0,5) = 1 + 3 = 4
```

Exactly the 4 combinations we expected:

1. `[5]`
2. `[2,2,1]`
3. `[2,1,1,1]`
4. `[1,1,1,1,1]`

(order doesn’t matter; we’re counting combinations, not permutations).

---

## Wrap-up

### What was wrong with your version?

* The inner `for (int i = idx; ...)` plus **two recursive calls** per iteration:

  * `dp(... remaining - coins[i], i)` and
  * `dp(... remaining, i + 1)`

  causes overlapping “skip” paths and multiple counting of the same combinations.

* No memoization → exponential time.

### What’s the right top-down structure?

* State: `dp(idx, remaining)` = ways to make `remaining` using `coins[idx..]`.

* Base:

  * `remaining == 0` → 1
  * `idx == n` or `remaining < 0` → 0

* Recurrence:

  ```text
  dp(idx, remaining) = dp(idx + 1, remaining)         // skip coin[idx]
                     + dp(idx, remaining - coins[idx]) // take coin[idx]
  ```

* Memoize over `(idx, remaining)`.
*/
// class Solution {
//     public int change(int amount, int[] coins) {
//         int n = coins.length;
//         Integer[][] memo = new Integer[n][amount + 1];
//         return dfs(coins, 0, amount, memo);
//     }

//     // dfs(idx, remaining): number of ways to form 'remaining' using coins[idx..end]
//     private int dfs(int[] coins, int idx, int remaining, Integer[][] memo) {
//         // Base: exact amount formed
//         if (remaining == 0) {
//             return 1;
//         }

//         // Base: no coins left or overshoot
//         if (idx == coins.length || remaining < 0) {
//             return 0;
//         }

//         if (memo[idx][remaining] != null) {
//             return memo[idx][remaining];
//         }

//         // Option 1: skip this coin
//         int waysSkip = dfs(coins, idx + 1, remaining, memo);

//         // Option 2: take this coin (stay at same idx)
//         int waysTake = dfs(coins, idx, remaining - coins[idx], memo);

//         int totalWays = waysSkip + waysTake;
//         memo[idx][remaining] = totalWays;
//         return totalWays;
//     }
// }





// Method 2: Bottom-Up 2D DP
/*
## 1. DP idea – what are we counting?

We want the **number of combinations** (order doesn’t matter) to form a given `amount` using unlimited copies of the given coin denominations.

Classic bottom-up way:

> Let `dp[i][a]` = number of ways to make amount `a`
> using the **first `i` coins** (i.e., `coins[0..i-1]`).

* `i` goes from `0..n`
* `a` goes from `0..amount`
* `n = coins.length`

Our final answer will be:

> `dp[n][amount]` (using all `n` coins to form `amount`)

---

## 2. Base cases and recurrence

### Base case: 0 coins

`dp[0][a]` means: using **0** coin types to form amount `a`.

* If `a == 0`:

  * There’s exactly **1** way: choose nothing.
* If `a > 0`:

  * There is **0** way to form a positive amount without any coins.

So:

```text
dp[0][0] = 1
dp[0][a>0] = 0
```

### Transition

When we are at coin index `i` (1-based in the DP), the coin value is `coins[i-1]`.

To compute `dp[i][a]`, we have two choices:

1. **Skip** the i-th coin (don’t use this coin at all):

   We then rely only on the first `i-1` coins to form `a`:

   ```text
   waysWithout = dp[i-1][a]
   ```

2. **Use** the i-th coin at least once:

   If we use one copy of `coins[i-1]`, then we still want to make up the remaining amount `a - coins[i-1]`, but we are still allowed to use the i-th coin again (infinite supply). So we stay in the same row:

   ```text
   waysWith = dp[i][a - coins[i-1]]   (if a - coins[i-1] >= 0)
   ```

Then:

```text
dp[i][a] = waysWithout + waysWith
```

This recurrence **avoids permutations** because:

* Coins are processed in a fixed order (0..i-1),
* Once we move to `i+1`, we never go back.


Time: `O(n * amount)`
Space: `O(n * amount)`

(We could optimize to 1D, but first let’s understand this clearly.)

---

## 4. Thorough example walkthrough: `amount = 5`, `coins = [1, 2, 5]`

We know the correct output is 4:

Combinations (order doesn’t matter):

1. `1 + 1 + 1 + 1 + 1`
2. `1 + 1 + 1 + 2`
3. `1 + 2 + 2`
4. `5`

Let’s see how DP finds these.

### Setup

* `n = 3` (coins: 1, 2, 5)
* We have a table `dp[0..3][0..5]`

Interpretation:

* Row `i` → can use the first `i` coins:

  * `i = 0`: no coins
  * `i = 1`: coin {1}
  * `i = 2`: coins {1,2}
  * `i = 3`: coins {1,2,5}

* Column `a` → target amount `a`.

---

### Row 0 (i = 0): no coins

We initialize:

* `dp[0][0] = 1` (one way to make 0 with no coins: choose nothing)
* `dp[0][1..5] = 0` (cannot form positive amounts with no coins)

So:

```text
      a:  0  1  2  3  4  5
dp[0]    1  0  0  0  0  0
```

---

### Row 1 (i = 1): using coin {1}

Coin = `coins[0] = 1`.

We fill `dp[1][a]` for `a = 0..5`.

**a = 0**

* Without coin 1: `dp[0][0] = 1`
* With coin 1: `a - 1 = -1` < 0 → cannot
* So: `dp[1][0] = 1`

**a = 1**

* Without coin 1: `dp[0][1] = 0`
* With coin 1: `dp[1][1 - 1] = dp[1][0] = 1`
* So: `dp[1][1] = 0 + 1 = 1`

Interpretation: one way to make 1 using {1} → `[1]`.

**a = 2**

* Without 1: `dp[0][2] = 0`
* With 1: `dp[1][2 - 1] = dp[1][1] = 1`
* So: `dp[1][2] = 1`

Interpretation: one way to make 2 using {1} → `[1,1]`.

**a = 3**

* Without 1: `dp[0][3] = 0`
* With 1: `dp[1][3 - 1] = dp[1][2] = 1`
* So: `dp[1][3] = 1` (that’s `[1,1,1]`)

**a = 4**

* Without 1: `dp[0][4] = 0`
* With 1: `dp[1][4 - 1] = dp[1][3] = 1`
* So: `dp[1][4] = 1` (`[1,1,1,1]`)

**a = 5**

* Without 1: `dp[0][5] = 0`
* With 1: `dp[1][5 - 1] = dp[1][4] = 1`
* So: `dp[1][5] = 1` (`[1,1,1,1,1]`)

Row 1:

```text
      a:  0  1  2  3  4  5
dp[0]    1  0  0  0  0  0
dp[1]    1  1  1  1  1  1
```

So far: using only coin 1, there’s exactly **1** way to make any amount `a` (just `a` copies of 1).

---

### Row 2 (i = 2): using coins {1, 2}

Now coin = `coins[1] = 2`.

We fill `dp[2][a]`.

**a = 0**

* Without 2: `dp[1][0] = 1`
* With 2: `a - 2 < 0` → no
* `dp[2][0] = 1`

**a = 1**

* Without 2: `dp[1][1] = 1` (just `[1]`)
* With 2: `a - 2 = -1` → no
* `dp[2][1] = 1`

So using {1,2}, there is still only 1 way to make 1: `[1]`.

**a = 2**

* Without 2: `dp[1][2] = 1` (`[1,1]`)
* With 2: `dp[2][2 - 2] = dp[2][0] = 1` (taking one 2; remaining 0)
* So: `dp[2][2] = 1 + 1 = 2`

Interpretation:
Ways to make 2 using {1,2}:

1. `[1,1]`
2. `[2]`

**a = 3**

* Without 2: `dp[1][3] = 1` (`[1,1,1]`)
* With 2: `dp[2][3 - 2] = dp[2][1] = 1` (take one 2, then 1 → `[1,2]`)
* So: `dp[2][3] = 1 + 1 = 2`

Ways to make 3 using {1,2}:

1. `[1,1,1]`
2. `[1,2]`

**a = 4**

* Without 2: `dp[1][4] = 1` (`[1,1,1,1]`)
* With 2: `dp[2][4 - 2] = dp[2][2] = 2`

  * Because `dp[2][2]` already counts:

    * `[1,1]`
    * `[2]`
  * So if we add another 2 in front, we get:

    * `[1,1,2]`
    * `[2,2]`
* Thus `dp[2][4] = 1 + 2 = 3`

So ways to make 4 using {1,2}:

1. `[1,1,1,1]`
2. `[1,1,2]`
3. `[2,2]`

**a = 5**

* Without 2: `dp[1][5] = 1` (`[1,1,1,1,1]`)
* With 2: `dp[2][5 - 2] = dp[2][3] = 2`

  * `dp[2][3]` counted:

    * `[1,1,1]`
    * `[1,2]`
  * If we add another 2, we get:

    * `[1,1,1,2]`
    * `[1,2,2]`
* So:

```text
dp[2][5] = 1 + 2 = 3
```

Ways to make 5 using {1,2} so far:

1. `[1,1,1,1,1]`
2. `[1,1,1,2]`
3. `[1,2,2]`

Row 2:

```text
      a:  0  1  2  3  4  5
dp[0]    1  0  0  0  0  0
dp[1]    1  1  1  1  1  1
dp[2]    1  1  2  2  3  3
```

---

### Row 3 (i = 3): using coins {1, 2, 5}

Now coin = `coins[2] = 5`.

We fill `dp[3][a]`.

**a = 0**

* Without 5: `dp[2][0] = 1`
* With 5: `a - 5 < 0` → no
* `dp[3][0] = 1`

**a = 1**

* Without 5: `dp[2][1] = 1`
* With 5: `a - 5 < 0` → no
* `dp[3][1] = 1`

**a = 2**

* Without 5: `dp[2][2] = 2`
* With 5: `a - 5 < 0` → no
* `dp[3][2] = 2`

**a = 3**

* Without 5: `dp[2][3] = 2`
* With 5: `a - 5 < 0` → no
* `dp[3][3] = 2`

**a = 4**

* Without 5: `dp[2][4] = 3`
* With 5: `a - 5 < 0` → no
* `dp[3][4] = 3`

**a = 5**

* Without 5: `dp[2][5] = 3` (these were the {1,2}-only combinations)
* With 5: `dp[3][5 - 5] = dp[3][0] = 1` (one combination: `[5]`)
* So:

```text
dp[3][5] = 3 + 1 = 4
```

Row 3:

```text
      a:  0  1  2  3  4  5
dp[0]    1  0  0  0  0  0
dp[1]    1  1  1  1  1  1
dp[2]    1  1  2  2  3  3
dp[3]    1  1  2  2  3  4
```

Final answer:

```text
dp[3][5] = 4
```

Which matches the expected number of combinations.

---

## 5. 1D DP optimization (quick note)

You can compress the 2D DP into 1D, because when computing `dp[i][a]`, you only need:

* `dp[i-1][a]` (previous row, same column)
* `dp[i][a - coin]` (current row, left column)

The classic 1D version (for completeness) looks like:

```java
public int change(int amount, int[] coins) {
    int[] dp = new int[amount + 1];
    dp[0] = 1;

    for (int coin : coins) {
        for (int a = coin; a <= amount; a++) {
            dp[a] += dp[a - coin];
        }
    }

    return dp[amount];
}
```

But conceptually, it’s doing the exact same recurrence we just walked through with the 2D table.
*/

// class Solution {
//     public int change(int amount, int[] coins) {
//         int n = coins.length;

//         // dp[i][a] = # of ways to make amount 'a' using first i coins (coins[0..i-1])
//         int[][] dp = new int[n + 1][amount + 1];

//         // Base: using 0 coins, we can make amount 0 in exactly 1 way (choose nothing)
//         dp[0][0] = 1;

//         // Fill the table
//         for (int i = 1; i <= n; i++) {
//             int coin = coins[i - 1];
//             for (int a = 0; a <= amount; a++) {
//                 // 1) Ways without using this coin
//                 dp[i][a] = dp[i - 1][a];

//                 // 2) Ways using this coin (at least once), if it's not too large
//                 if (a - coin >= 0) {
//                     dp[i][a] += dp[i][a - coin];
//                 }
//             }
//         }

//         return dp[n][amount];
//     }
// }







// Method 2.5: My Bottom-Up Approach
/*
*/
class Solution {
    public int change(int amount, int[] coins) {
        int n = coins.length;

        int[][] memo = new int[n][amount+1];

        for(int i=0; i<coins.length; i++){
            for(int a=0; a<=amount; a++){
                if(i == 0){
                    memo[0][a] = (a % coins[0] == 0) ? 1 : 0;
                }else{
                    int notTake = memo[i-1][a];
                    int take = 0;
                    if(a >= coins[i]){
                        take = memo[i][a - coins[i]];
                    }

                    memo[i][a] = notTake + take;
                }
            }
        }     

        return memo[n-1][amount];
    }
}








// Method 3: 1D Bottom-Up Approach
/*
# Core idea

We want to count the number of **combinations** to make `amount`.

For example, if:

* `amount = 5`
* `coins = [1, 2, 5]`

then these are the valid combinations:

* `5`
* `2 + 2 + 1`
* `2 + 1 + 1 + 1`
* `1 + 1 + 1 + 1 + 1`

So the answer is `4`.

Notice:

* `2 + 1 + 1 + 1` and `1 + 2 + 1 + 1` are **not different**
* order does **not** matter
* so this is a **combinations** problem, not a permutations problem

---

# DP meaning

We use a 1D array:

```java id="1f4ylh"
dp[a]
```

which means:

> number of ways to make amount `a`

So if `dp[4] = 3`, that means there are 3 different combinations that make amount 4.

---

# Base case

```java id="qj0jry"
dp[0] = 1
```

Why?

Because there is exactly **one** way to make amount `0`:

* choose no coins

This base case is extremely important because it helps build all other answers.

---

# Transition

For each coin, we try to use it to contribute to larger amounts.

If the current coin is `coin`, then for every amount `a >= coin`:

```java id="04f5u9"
dp[a] += dp[a - coin]
```

---

# Why does this work?

Suppose current coin is `2`, and we are computing `dp[5]`.

Then:

```java id="bs6vdb"
dp[5] += dp[3]
```

Why?

Because every way to make `3` can become a way to make `5` by adding one `2`.

So if there are 2 ways to make `3`, then those 2 ways produce 2 more ways to make `5`.

---

# Why coins must be the outer loop

The loops should be:

```java id="exkxrm"
for each coin
    for amount from coin to target
```

This ensures combinations are counted only once.

We process coin types one by one, so once we move past a coin, we do not go back and create different orderings of the same combination.

That is exactly what prevents counting:

* `1 + 2`
* `2 + 1`

as two separate answers.

---

# Why amount goes from small to large

For each coin, we do:

```java id="obpsy8"
for (int a = coin; a <= amount; a++)
```

This is because the same coin can be used **unlimited times**.

When we compute `dp[a]`, we want `dp[a - coin]` to already include ways that may have used this same coin earlier in the same iteration.

That is why we go left to right.


# Very detailed walkthrough

Let us take:

```java id="op6c0n"
amount = 5
coins = [1, 2, 5]
```

We create:

```java id="0notr3"
dp = [0, 0, 0, 0, 0, 0]
```

Size is `amount + 1 = 6`, so indices are `0` through `5`.

Then set:

```java id="ywn95m"
dp[0] = 1
```

So now:

```java id="7m5kav"
dp = [1, 0, 0, 0, 0, 0]
```

This means:

* amount 0 → 1 way
* everything else → 0 ways so far

---

## Process coin = 1

We loop:

```java id="yjx1c8"
for (a = 1; a <= 5; a++)
```

### a = 1

```java id="xfcu5r"
dp[1] += dp[1 - 1]
dp[1] += dp[0]
dp[1] += 1
```

So:

```java id="9b2a4j"
dp = [1, 1, 0, 0, 0, 0]
```

Interpretation:

* amount 1 can be made in 1 way: `[1]`

---

### a = 2

```java id="ydjlwm"
dp[2] += dp[2 - 1]
dp[2] += dp[1]
dp[2] += 1
```

So:

```java id="wqozx4"
dp = [1, 1, 1, 0, 0, 0]
```

Interpretation:

* amount 2 can be made in 1 way: `[1,1]`

---

### a = 3

```java id="of0v4h"
dp[3] += dp[2]
dp[3] += 1
```

So:

```java id="7c60eu"
dp = [1, 1, 1, 1, 0, 0]
```

Interpretation:

* amount 3 can be made in 1 way: `[1,1,1]`

---

### a = 4

```java id="uklo99"
dp[4] += dp[3]
dp[4] += 1
```

So:

```java id="jp6rr7"
dp = [1, 1, 1, 1, 1, 0]
```

Interpretation:

* amount 4 can be made in 1 way: `[1,1,1,1]`

---

### a = 5

```java id="h9zoru"
dp[5] += dp[4]
dp[5] += 1
```

So:

```java id="zw2hmy"
dp = [1, 1, 1, 1, 1, 1]
```

Interpretation:

* amount 5 can be made in 1 way: `[1,1,1,1,1]`

---

After processing coin `1`, the array is:

```java id="9jod9m"
dp = [1, 1, 1, 1, 1, 1]
```

This makes sense, because using only coin `1`, every amount has exactly 1 way.

---

## Process coin = 2

Now loop:

```java id="jlwmw2"
for (a = 2; a <= 5; a++)
```

Current array before starting:

```java id="7834f3"
dp = [1, 1, 1, 1, 1, 1]
```

---

### a = 2

```java id="czwvrf"
dp[2] += dp[2 - 2]
dp[2] += dp[0]
dp[2] += 1
```

So:

```java id="x3lcb4"
dp[2] = 2
dp = [1, 1, 2, 1, 1, 1]
```

Interpretation:

Ways to make 2:

* `[1,1]`
* `[2]`

---

### a = 3

```java id="4jp7dc"
dp[3] += dp[3 - 2]
dp[3] += dp[1]
dp[3] += 1
```

So:

```java id="m90epl"
dp[3] = 2
dp = [1, 1, 2, 2, 1, 1]
```

Interpretation:

Ways to make 3:

* `[1,1,1]`
* `[1,2]`

---

### a = 4

```java id="8lw6zf"
dp[4] += dp[4 - 2]
dp[4] += dp[2]
```

Now be careful:

`dp[2]` is already updated in this same coin iteration, and currently `dp[2] = 2`.

So:

```java id="mgt0tw"
dp[4] = 1 + 2 = 3
dp = [1, 1, 2, 2, 3, 1]
```

Interpretation:

Ways to make 4:

* `[1,1,1,1]`
* `[1,1,2]`
* `[2,2]`

This is exactly why we loop left to right.
We want to allow reusing coin `2`.

---

### a = 5

```java id="dlgd1s"
dp[5] += dp[5 - 2]
dp[5] += dp[3]
```

Currently `dp[3] = 2`, so:

```java id="bmx3l2"
dp[5] = 1 + 2 = 3
dp = [1, 1, 2, 2, 3, 3]
```

Interpretation:

Ways to make 5 so far:

* `[1,1,1,1,1]`
* `[1,1,1,2]`
* `[1,2,2]`

---

After processing coin `2`, we have:

```java id="d4oumx"
dp = [1, 1, 2, 2, 3, 3]
```

---

## Process coin = 5

Now loop:

```java id="jyu6hf"
for (a = 5; a <= 5; a++)
```

Only one value.

### a = 5

```java id="fxfn9q"
dp[5] += dp[5 - 5]
dp[5] += dp[0]
dp[5] += 1
```

So:

```java id="55y0yh"
dp[5] = 4
dp = [1, 1, 2, 2, 3, 4]
```

Interpretation:

Ways to make 5:

* `[1,1,1,1,1]`
* `[1,1,1,2]`
* `[1,2,2]`
* `[5]`

Final answer:

```java id="qomc2p"
dp[5] = 4
```

---

# Why this counts combinations correctly

Let us focus on `amount = 3`, `coins = [1, 2]`.

The valid combinations are:

* `[1,1,1]`
* `[1,2]`

We should count `2`, not `3`.

We do **not** want:

* `[1,2]`
* `[2,1]`

to be treated separately.

Because we process:

1. all combinations using coin `1`
2. then extend them using coin `2`

we naturally create combinations in a fixed order of coin types, which prevents duplicates due to reordering.

---

# Intuition in one sentence

For each coin, we ask:

> how many old ways to make `a - coin` can be extended by adding this coin once more?

That is exactly:

```java id="jlwmh7"
dp[a] += dp[a - coin]
```

---

# Time and space complexity

## Time

There are:

* `coins.length` coins
* `amount + 1` states

So time is:

```java id="zudl2n"
O(coins.length * amount)
```

## Space

We only use one array of length `amount + 1`:

```java id="c1ci2j"
O(amount)
```

---

# Common mistake

A very common mistake is reversing the loops:

```java id="anxuxu"
for (int a = 0; a <= amount; a++) {
    for (int coin : coins) {
        ...
    }
}
```

This tends to count **permutations** because the same combination can be formed in different orders during the same amount step.

For Coin Change II, that is wrong.

The correct order is:

```java id="lap7jk"
for (int coin : coins) {
    for (int a = coin; a <= amount; a++) {
        dp[a] += dp[a - coin];
    }
}
```

---

# Small second example

Take:

```java id="r62vn1"
amount = 3
coins = [2]
```

Start:

```java id="l4ofx3"
dp = [1, 0, 0, 0]
```

Process coin `2`:

* `a = 2`: `dp[2] += dp[0]` → `dp[2] = 1`
* `a = 3`: `dp[3] += dp[1]` → `dp[3] = 0`

Final:

```java id="t24g0y"
dp = [1, 0, 1, 0]
```

So answer is `0`, which is correct because amount `3` cannot be made using only coin `2`.


# Simple mental model

Think of `dp[a]` as:

> number of combinations currently known for amount `a`

When you process a new coin, you allow that coin to participate in building new combinations.

So each coin gradually expands the set of valid combinations.

---

# Final takeaway

The 1D version works because:

* `dp[a]` stores ways to make amount `a`
* `dp[0] = 1` seeds the process
* `dp[a] += dp[a - coin]` means: add this coin to every way of making `a - coin`
* coins must be the outer loop so combinations are counted once
* amounts go from left to right so the same coin can be reused
*/

// class Solution {
//     public int change(int amount, int[] coins) {
//         int[] dp = new int[amount + 1];

//         // One way to make amount 0: choose nothing
//         dp[0] = 1;

//         // Process one coin at a time
//         for (int coin : coins) {
//             // Build all amounts that can include this coin
//             for (int a = coin; a <= amount; a++) {
//                 dp[a] += dp[a - coin];
//             }
//         }

//         return dp[amount];
//     }
// }