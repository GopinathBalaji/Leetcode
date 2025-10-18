// Method 1: Top-Down DP
/*
# What the problem asks

Given coin denominations and an amount, find the **minimum number of coins** to reach that amount (unbounded supply). If impossible, return “no solution.”

# Core DP idea

Define a function over sub-amounts and build the answer from smaller targets.

## 1) Top-down (memoized recursion)

* **State:** `f(t)` = minimum coins to make total `t`.
* **Base cases:**

  * `f(0) = 0` (zero coins to make zero)
  * `f(t) = impossible` for `t < 0`
* **Transition:**
  Try each coin `c`, and combine:
  `f(t) = 1 + min over c ( f(t − c) )` (skip any `t−c` that’s impossible)
* **Memoization:** cache each `t` once (use a sentinel for “unknown”).
* **Answer:** `f(amount)`; if “impossible,” map it to `-1`.

**Pitfalls to avoid**

* Confusing “0 = unknown.” Use `-1`/`null` as the memo sentinel.
* Letting negative totals recurse; cut them off immediately.
* Not handling “no coin fits at any step” → must return “impossible.”


# Example walkthrough

**coins = [1, 2, 5], amount = 11**

Sentinel: `memo[t] = -1` means “unknown.”
Goal: `dp(11)` = min coins to make 11.

## Phase 1: First time we see each sub-amount

I’ll show the essential branches only (calls that return `-1` for negatives are pruned).

1. `dp(11)`
   try `1 → dp(10)`, `2 → dp(9)`, `5 → dp(6)`

2. `dp(10)`
   `1 → dp(9)`, `2 → dp(8)`, `5 → dp(5)`

3. `dp(9)`
   `1 → dp(8)`, `2 → dp(7)`, `5 → dp(4)`

4. `dp(6)`
   `1 → dp(5)`, `2 → dp(4)`, `5 → dp(1)`

5. `dp(5)`
   `1 → dp(4)`, `2 → dp(3)`, `5 → dp(0)`

   * `dp(0)=0` ⇒ using coin 5 gives `1 + dp(0) = 1`.
     So `dp(5)=1`. Memoize: `memo[5]=1`.

6. Now resolve parents using `dp(5)=1`:

   * In `dp(10)`, branch `5 → dp(5)=1` ⇒ candidate `1 + 1 = 2`.
     We still must check other branches to ensure minimum.
   * In `dp(6)`, branch `1 → dp(5)=1` ⇒ candidate `1 + 1 = 2`.

7. Continue first-time evaluations (only where needed):

   * `dp(4)` → best is `2` (e.g., 2+2): `memo[4]=2`.
   * `dp(3)` → best is `2` (1+2): `memo[3]=2`.
   * `dp(8)` can use `dp(3)` via coin 5 → `1 + 2 = 3`, or `dp(6)` via coin 2 once it’s known, etc.
   * `dp(7)` can use `dp(2)` via coin 5, etc.
   * `dp(1)` → `1` (coin 1): `memo[1]=1`.
   * `dp(2)` → `1` (coin 2): `memo[2]=1`.

With these:

* `dp(6)` tries:

  * `1 + dp(5)=1 + 1 = 2`
  * `1 + dp(4)=1 + 2 = 3`
  * `1 + dp(1)=1 + 1 = 2`
    ⇒ `dp(6)=2` (memo).
* `dp(9)` tries:

  * `1 + dp(8)` (pending)
  * `1 + dp(7)` (pending)
  * `1 + dp(4)=1 + 2 = 3`
    Keep 3 as current best, but continue.
* `dp(8)`:

  * `1 + dp(7)` (pending)
  * `1 + dp(6)=1 + 2 = 3`
  * `1 + dp(3)=1 + 2 = 3`
    ⇒ `dp(8)=3` (memo).
* `dp(7)`:

  * `1 + dp(6)=1 + 2 = 3`
  * `1 + dp(5)=1 + 1 = 2`
  * `1 + dp(2)=1 + 1 = 2`
    ⇒ `dp(7)=2` (memo).
* Now go back to `dp(9)`:

  * We already had `1 + dp(4)=3`
  * `1 + dp(8)=1 + 3 = 4` (worse)
  * `1 + dp(7)=1 + 2 = 3`
    ⇒ `dp(9)=3` (memo).
* `dp(10)`:

  * `1 + dp(9)=1 + 3 = 4`
  * `1 + dp(8)=1 + 3 = 4`
  * `1 + dp(5)=1 + 1 = 2`
    ⇒ `dp(10)=2` (memo).
* Finally `dp(11)`:

  * `1 + dp(10)=1 + 2 = 3`
  * `1 + dp(9)=1 + 3 = 4`
  * `1 + dp(6)=1 + 2 = 3`
    ⇒ `dp(11)=3` (memo), which the function returns.

Throughout, each `dp(t)` is computed once and memoized.

---

# How memoization helps (concretely)

Without memoization, the recursion tree **recomputes the same sub-amounts many times**. For example:

* `dp(10)` calls `dp(9)`, and `dp(11)` also calls `dp(9)`.
* `dp(6)` is reached via `dp(11)` (coin 5), and again from `dp(8)` (coin 2), etc.

With memoization:

* The **first** time we compute, say, `dp(6)`, we explore its children and store `memo[6]=2`.
* The **next** time any path needs `dp(6)`, it returns **immediately** from the `if (memo[amount] != -1)` check.
* This collapses the exponential recursion tree into **O(amount × #coins)** total work.

You can even observe it at runtime by adding a counter for how many times `dp(t)` actually enters its “compute” section—each `t` should be computed at most once.

---

# A few correctness notes about your code

* **Base cases** are spot on: `amount==0 → 0`, `amount<0 → -1`.
* **Sentinel choice** is safe: `-1` means “unknown/impossible,” distinct from valid counts.
* **Overflow safety:** you never do `Integer.MAX_VALUE + 1` because you only add `1` after checking `val != -1`.
* **Order of coins** doesn’t matter for correctness (it can affect which valid minimum you find first, but not the final min).
*/
class Solution {
    public int coinChange(int[] coins, int amount) {
        if (amount == 0) return 0;

        // memo[t] = minimum coins for t
        //   -2 => unknown, -1 => impossible, >=0 => computed answer
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -2);
        memo[0] = 0;

        return dp(coins, amount, memo);
    }

    private int dp(int[] coins, int amount, int[] memo) {
        if (amount < 0) return -1;
        if (memo[amount] != -2) return memo[amount]; // already computed (could be -1 or >=0)

        int best = Integer.MAX_VALUE;
        for (int c : coins) {
            int sub = dp(coins, amount - c, memo);
            if (sub != -1) {
                // safe: sub < Integer.MAX_VALUE by construction
                best = Math.min(best, sub + 1);
            }
        }

        memo[amount] = (best == Integer.MAX_VALUE) ? -1 : best;
        return memo[amount];
    }
}



// Method 2: Bottom-Up Approach
/*
### Why this is correct

* **State:** `dp[t]` = min coins to form total `t`.
* **Base:** `dp[0] = 0`.
* **Transition:** try taking coin `c` last → need `dp[t - c]` first, so candidate is `dp[t - c] + 1`.
* **Sentinel:** `amount+1` stands for “impossible”; if it survives at `dp[amount]`, return `-1`.
* **No overflow:** adding `1` to at most `amount` yields ≤ `amount+1`.

### Time & space

* Time: `O(amount × #coins)`
* Space: `O(amount)`

---

## Thorough example walkthrough

**coins = [1, 2, 5], amount = 11**

Initialize:

```
dp[0] = 0
dp[1..11] = 12   // since amount+1 = 12
```

We fill `dp` from `t=1` to `11`.

### t = 1

* Try coin 1: `dp[1] = min(12, dp[0] + 1) = min(12, 0+1) = 1`
* Coin 2, 5 don’t fit.
  → `dp[1] = 1`

### t = 2

* Coin 1: `dp[2] = min(12, dp[1]+1) = min(12, 1+1)=2`
* Coin 2: `dp[2] = min(2, dp[0]+1) = min(2, 0+1)=1`  ✅ better
* Coin 5: skip
  → `dp[2] = 1`

### t = 3

* c=1: `dp[3] = min(12, dp[2]+1) = min(12, 1+1)=2`
* c=2: `dp[3] = min(2, dp[1]+1) = min(2, 1+1)=2`
* c=5: skip
  → `dp[3] = 2`

### t = 4

* c=1: `dp[4] = min(12, dp[3]+1) = min(12, 2+1)=3`
* c=2: `dp[4] = min(3, dp[2]+1) = min(3, 1+1)=2` ✅
* c=5: skip
  → `dp[4] = 2`

### t = 5

* c=1: `dp[5] = min(12, dp[4]+1) = min(12, 2+1)=3`
* c=2: `dp[5] = min(3, dp[3]+1) = min(3, 2+1)=3`
* c=5: `dp[5] = min(3, dp[0]+1) = min(3, 0+1)=1` ✅ big improvement
  → `dp[5] = 1`

You can see the pattern: each `dp[t]` looks one coin back and adds 1.

Fast-forward key steps:

### t = 6

* via c=1 → `dp[5]+1 = 1+1 = 2` (best)
  → `dp[6] = 2`

### t = 7

* via c=5 → `dp[2]+1 = 1+1 = 2` (best)
  → `dp[7] = 2`

### t = 8

* via c=5 → `dp[3]+1 = 2+1 = 3`
* via c=2 → `dp[6]+1 = 2+1 = 3`
  → `dp[8] = 3`

### t = 9

* via c=5 → `dp[4]+1 = 2+1 = 3`
* via c=2 → `dp[7]+1 = 2+1 = 3`
  → `dp[9] = 3`

### t = 10

* via c=5 → `dp[5]+1 = 1+1 = 2` ✅ best
  → `dp[10] = 2`

### t = 11

* via c=1 → `dp[10]+1 = 2+1 = 3` ✅
* via c=2 → `dp[9]+1 = 3+1 = 4` (worse)
* via c=5 → `dp[6]+1 = 2+1 = 3`
  → `dp[11] = 3`

**Result:** `3` coins (e.g., `5 + 5 + 1`).
*/
// class Solution {
//     public int coinChange(int[] coins, int amount) {
//         if (amount == 0) return 0;

//         // dp[t] = min coins to make sum t; sentinel = amount+1 (impossible)
//         int[] dp = new int[amount + 1];
//         Arrays.fill(dp, amount + 1);
//         dp[0] = 0;

//         for (int t = 1; t <= amount; t++) {
//             for (int c : coins) {
//                 if (t - c >= 0) {
//                     dp[t] = Math.min(dp[t], dp[t - c] + 1);
//                 }
//             }
//         }
//         return dp[amount] > amount ? -1 : dp[amount];
//     }
// }



// Method 3: BFS Approach
/*
# Intuition: why BFS?

Think of each total as a **node** and adding a coin value as an **edge**:

* Start at total **0**.
* From total **x**, you can go to **x + c** for each coin `c` (if `≤ amount`).
* Each BFS “layer” = using one more coin.
  So the **first time** you reach `amount`, you’ve used the **minimum number of coins**.

---

# Algorithm (BFS over sums)

1. If `amount == 0`, return `0`.
2. Use a queue initialized with `0` (the starting sum).
3. Use a `visited[0..amount]` to avoid revisiting the same sum.
4. Maintain `steps` = number of coins used so far (BFS level).
5. While queue not empty:

   * For all items currently in the queue (one level):

     * Pop `curr`.
     * For each coin `c`:

       * `next = curr + c`.
       * If `next == amount` → return `steps + 1` (you just used one more coin).
       * If `next < amount` and not visited → mark visited and push.
   * After processing the whole level, `steps++`.
6. If the queue empties without hitting `amount`, return `-1` (impossible).


### Notes / micro-optimizations

* Sorting `coins` descending can sometimes find the answer earlier (same complexity).
* You can prune coins `c > amount` up front.
* BFS is great when `amount` is moderate and coin set is small; DP is typically more predictable for very large amounts.

---

# Thorough example walkthrough

**coins = [1, 2, 5], amount = 11**

We’ll keep track of the queue (frontier) and the `steps` (coins used).

* **Initialize**
  `visited[0] = true`, `q = [0]`, `steps = 0`

---

### Level 0  (steps = 0)

Process: `q = [0]`

* From `0`:

  * `0 + 1 = 1` → enqueue 1, mark visited
  * `0 + 2 = 2` → enqueue 2, mark visited
  * `0 + 5 = 5` → enqueue 5, mark visited
    End of level → `steps = 1`, `q = [1, 2, 5]`

Meaning: with **1 coin**, you can reach sums `{1, 2, 5}`.

---

### Level 1  (steps = 1)

Process `q = [1, 2, 5]`:

1. From `1`:

   * `1 + 1 = 2` (already visited)
   * `1 + 2 = 3` → enqueue 3
   * `1 + 5 = 6` → enqueue 6

2. From `2`:

   * `2 + 1 = 3` (just enqueued/visited)
   * `2 + 2 = 4` → enqueue 4
   * `2 + 5 = 7` → enqueue 7

3. From `5`:

   * `5 + 1 = 6` (already enqueued)
   * `5 + 2 = 7` (already enqueued)
   * `5 + 5 = 10` → enqueue 10

End of level → `steps = 2`, `q = [3, 6, 4, 7, 10]`

Meaning: with **2 coins**, you can reach `{3, 4, 6, 7, 10}`.

---

### Level 2  (steps = 2)

Process `q = [3, 6, 4, 7, 10]`:

1. From `3`:

   * `3 + 1 = 4` (visited)
   * `3 + 2 = 5` (visited)
   * `3 + 5 = 8` → enqueue 8

2. From `6`:

   * `6 + 1 = 7` (visited)
   * `6 + 2 = 8` (just enqueued)
   * `6 + 5 = 11` → **TARGET REACHED**
     Return `steps + 1 = 3`.

We found `11` at **level 3**, i.e., using **3 coins**—which is minimal (e.g., `5 + 5 + 1` or `2 + 2 + 2 + 5` is 4 coins; BFS finds the true minimum).

---

# Why BFS guarantees the minimum

* Each level corresponds to adding **one more coin**.
* BFS explores all sums with `k` coins before touching any with `k+1` coins.
* The first time you reach `amount`, you must have used the **fewest** coins.

---

# Complexity

Let `A = amount`, `N = #coins`.

* **Time:** Each sum `0..A` is enqueued at most once, and we try up to `N` edges per sum ⇒ `O(N * A)`.
* **Space:** `O(A)` for `visited` and the queue (in the worst case).

---

## Common pitfalls (and how the code avoids them)

* **Revisiting sums** causes exponential blow-up → we mark `visited[next] = true` as soon as we enqueue.
* **Overshooting** sums > amount → we discard those.
* **Not counting levels correctly** → we increment `steps` once per BFS level (using `size` loop).
*/

// class Solution {
//     public int coinChange(int[] coins, int amount) {
//         if (amount == 0) return 0;

//         boolean[] visited = new boolean[amount + 1];
//         Queue<Integer> q = new ArrayDeque<>();
//         q.offer(0);
//         visited[0] = true;

//         int steps = 0; // number of coins used so far

//         while (!q.isEmpty()) {
//             int size = q.size();
//             // All nodes at the current "coin count" layer
//             for (int s = 0; s < size; s++) {
//                 int curr = q.poll();

//                 for (int c : coins) {
//                     int next = curr + c;

//                     if (next == amount) {
//                         return steps + 1; // reached target with one more coin
//                     }
//                     if (next < amount && !visited[next]) {
//                         visited[next] = true;
//                         q.offer(next);
//                     }
//                 }
//             }
//             steps++; // move to the next layer => one more coin used
//         }

//         return -1; // not reachable
//     }
// }
