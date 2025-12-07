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
class Solution {
    public int change(int amount, int[] coins) {
        int n = coins.length;
        Integer[][] memo = new Integer[n][amount + 1];
        return dfs(coins, 0, amount, memo);
    }

    // dfs(idx, remaining): number of ways to form 'remaining' using coins[idx..end]
    private int dfs(int[] coins, int idx, int remaining, Integer[][] memo) {
        // Base: exact amount formed
        if (remaining == 0) {
            return 1;
        }

        // Base: no coins left or overshoot
        if (idx == coins.length || remaining < 0) {
            return 0;
        }

        if (memo[idx][remaining] != null) {
            return memo[idx][remaining];
        }

        // Option 1: skip this coin
        int waysSkip = dfs(coins, idx + 1, remaining, memo);

        // Option 2: take this coin (stay at same idx)
        int waysTake = dfs(coins, idx, remaining - coins[idx], memo);

        int totalWays = waysSkip + waysTake;
        memo[idx][remaining] = totalWays;
        return totalWays;
    }
}





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
