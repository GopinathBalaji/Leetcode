// Method 1: Bottom-Up DP
/*
### \U0001f9e9 Step 1: What does the problem ask?

You can climb **1 or 2 steps** at a time.
Question: *In how many distinct ways can you reach step `n`?*

---

### \U0001fa9c Step 2: What pattern did we find before?

From the top-down idea:

* To reach step `i`, you either came from step `i−1` (a 1-step jump)
  or from step `i−2` (a 2-step jump).

So,
[
f(i) = f(i-1) + f(i-2)
]

This is the same recurrence we’ll use here.

---

### \U0001f501 Step 3: What does “bottom-up” mean?

Instead of recursion + memoization, we **build the table iteratively** from the smallest cases upward.

We start with known base values and use the recurrence to fill in all higher values.

---

### \U0001f9f1 Step 4: Building the DP table

1. Create an array `dp` of size `n+1`.
2. Set base cases:

   * `dp[0] = 1` (1 way to stand on the ground)
   * `dp[1] = 1` (only one 1-step move to reach step 1)
3. For each `i` from 2 to `n`, use the rule:

   * `dp[i] = dp[i−1] + dp[i−2]`
4. Return `dp[n]`.

---

### \U0001f9e0 Step 5: Why this works

Each value `dp[i]` stores the *total number of ways to reach step i*.
Since every higher step depends only on the two previous values, we can build up the solution progressively — no recursion, no repeated work.

---

### ⚙️ Step 6: Time and space

* **Time:** `O(n)` — each step computed once.
* **Space:** `O(n)` for the array.
  (Can reduce to `O(1)` using two variables if you only keep `dp[i−1]` and `dp[i−2]`.)
*/
class Solution {
    public int climbStairs(int n) {
        int[] dp = new int[n+1];
        dp[0] = 1;
        dp[1] = 1;

        for(int i=2; i<=n; i++){
            dp[i] = dp[i-1] + dp[i-2];
        }

        return dp[n];
    }
}



// Method 2: Top-Down Recursive DP
/*
**Big idea:** define a function that answers “How many ways to reach step *i*?” and cache results so we don’t recompute them.

### Plan

1. **State (what we memoize).**
   Let `f(i)` = number of distinct ways to climb to step `i`.

2. **Base cases (stopping points).**
   Decide `f(0)` and `f(1)` so the recurrence works. (Think: from ground to step 0, and to step 1.)

3. **Recurrence (transition).**
   To get to step `i`, your last move was either from `i-1` (a 1-step) or from `i-2` (a 2-step).
   So `f(i)` should combine `f(i-1)` and `f(i-2)` appropriately.

4. **Memoization.**
   Use an array/map `memo` with “unknown” initialized (e.g., `-1`/`null`).
   On each call:

   * If `i` is a base case → return it.
   * If `memo[i]` is known → return it.
   * Otherwise compute from the recurrence, store in `memo[i]`, return.

5. **Answer call.**
   Return `f(n)`.

6. **Complexity.**
   With memoization: **O(n)** time, **O(n)** space (due to memo + recursion depth).

7. **Common pitfalls.**

   * Off-by-one mistakes in base cases (especially `f(0)`).
   * Forgetting to memoize before returning.
   * Handling small `n` (like `n=0` or `n=1`).

### Fill-in template (language-agnostic pseudocode)

```text
function climbStairs(n):
    memo = array/map sized n+1, filled with UNKNOWN
    return ways(n, memo)

function ways(i, memo):
    if i <= ???: return ???            // base cases
    if memo[i] != UNKNOWN: return memo[i]
    memo[i] = ways(i-1, memo) ??? ways(i-2, memo)   // combine correctly
    return memo[i]
```*/
// class Solution {
//     public int climbStairs(int n) {
//         int[] memo = new int[n+1];
//         return dp(n, memo);
//     }

//     public int dp(int i, int[] memo){
//         if(i <= 1){
//             return 1;
//         }

//         if(memo[i] != 0){
//             return memo[i];
//         }

//         memo[i] = dp(i-1, memo) + dp(i-2, memo);

//         return memo[i];
//     }
// }