// Method 1: Top-Down 2D DP
/*
# NOTE: USE OFFSET IN MEMO TO KEEP INDEX POSITIVE

### Bug 1: Wrong base case ordering / logic

You have:

```java
if (val == target) {
    return 1;
}
if (idx >= nums.length) {
    return 0;
}
```

This says:

> “As soon as the *current* sum equals the target, count that as a valid way, even if I haven’t processed all numbers yet.”

That’s incorrect. For Target Sum, you must assign **a `+` or `-` sign to every element**. So you only want to check `val == target` **after you’ve used all numbers**.

Correct logic:

```java
if (idx == nums.length) {
    return val == target ? 1 : 0;
}
```

That is: at the end (idx == n), if the sum you’ve built equals the target, count 1 way; otherwise 0.

---

### Bug 2: Using `val` as an array index directly

You declare:

```java
Integer[][] memo = new Integer[n][target + 1];
```

and then use `memo[idx][val]`.

Problems:

1. `val` can be **negative** (you do `val - nums[idx]`), so `memo[idx][val]` can be an invalid index.
2. `val` can be **greater than `target`** in absolute value. For example:

   * `nums = [1000,1000,...]`, `target = 3`, but partial sums can be `±1000, ±2000, ...`.
   * Your second dimension is `target + 1`, which may be small and doesn’t cover all reachable sums.
3. `target` itself can be **negative** in the problem constraints, so `new Integer[n][target + 1]` can even throw `NegativeArraySizeException`.

The sum `val` ranges roughly in:

```text
[-sum(nums), +sum(nums)]
```

You cannot use it directly as an index without either:

* adding an **offset** (`val + offset`), or
* using a **HashMap** to memoize `(idx, val)`.

---

### Bug 3: Unused `amount`-like parameter

You pass `amount` (named `target`) and `remaining`-like logic, but in the `dp` function you only really need:

* `idx`
* `currentSum` (your `val`)
* `target` (constant)

That’s more of a style thing; not a correctness bug, but it’s extra clutter.

---

## 2. Correct top-down version

### Key idea

State:

> `dp(idx, curSum)` = number of ways to assign `+/-` signs to `nums[idx..end]`
> such that the total sum becomes `target`, given that current sum is `curSum`.

We’ll implement that with:

* Base case at `idx == nums.length`
* Two choices at each index: `+ nums[idx]` and `- nums[idx]`
* Memo over `(idx, curSum)` using an **offset** so we can store `curSum` in an array.

Because `|curSum|` can be as large as `sum(nums)`, let:

```text
S = sum(nums)
curSum ∈ [-S, S]
map curSum → (curSum + S) ∈ [0, 2S]
```

## 3. Explanation of the DP state and recurrence

### State definition

> `dfs(idx, curSum)` = number of ways to assign signs to `nums[idx..]`
> such that if we start with `curSum` at this point, the final sum equals `target`.

Initially:

```text
idx = 0
curSum = 0
```

We want `dfs(0, 0)`.

### Base case

When we’ve assigned signs to all elements (we’re at `idx == nums.length`):

* If `curSum == target` → exactly 1 way (this sign assignment works).
* Else → 0 ways.

So:

```java
if (idx == nums.length) {
    return curSum == target ? 1 : 0;
}
```

### Transition

At position `idx`, we have two choices with `nums[idx]`:

1. Assign `+nums[idx]`:

   * New sum: `curSum + nums[idx]`.
   * Recurse: `dfs(idx + 1, curSum + nums[idx])`.

2. Assign `-nums[idx]`:

   * New sum: `curSum - nums[idx]`.
   * Recurse: `dfs(idx + 1, curSum - nums[idx])`.

Total ways:

```text
dfs(idx, curSum) = dfs(idx + 1, curSum + nums[idx])
                 + dfs(idx + 1, curSum - nums[idx])
```

### Memoization

The same `(idx, curSum)` pair can be reached by different paths, so we memoize:

* Use a 2D array `memo[idx][curSum + offset]`.
* `offset` is `sum(nums)` to handle negative sums.

If `memo[idx][key]` is already filled, just return it.

This reduces complexity from exponential to `O(n * sum(nums))`.

---

## 4. Example walkthrough: `nums = [1,1,1,1,1]`, `target = 3`

We know the answer is **5**.

Valid sign assignments that sum to 3 (with 5 ones):

* `+ + + - -` (in various positions). There are exactly 5 of them.

Let’s see conceptually how `dfs` counts them.

### Setup

```text
nums   = [1, 1, 1, 1, 1]
target = 3
sum(nums) = 5
offset = 5
memo size: [5][11]  // sums from -5..+5
```

We call:

```text
dfs(0, 0)
```

At each step, we branch into `+` and `-`.

### Level 0 (idx = 0)

`curSum = 0`

Two choices with nums[0] = 1:

* `+1` → `dfs(1, 1)`
* `-1` → `dfs(1, -1)`

So:

```text
dfs(0, 0) = dfs(1, 1) + dfs(1, -1)
```

---

### Level 1 (idx = 1)

#### `dfs(1, 1)`:

Choices:

* `+1` → `dfs(2, 2)`
* `-1` → `dfs(2, 0)`

So:

```text
dfs(1, 1) = dfs(2, 2) + dfs(2, 0)
```

#### `dfs(1, -1)`:

Choices:

* `+1` → `dfs(2, 0)`
* `-1` → `dfs(2, -2)`

So:

```text
dfs(1, -1) = dfs(2, 0) + dfs(2, -2)
```

(Here you can already see how memoization helps: `dfs(2, 0)` is reused.)

---

### Level 2 and beyond

We keep branching until `idx == 5`. At that point we check:

```text
if (curSum == target) return 1; else 0;
```

I won’t enumerate *all* 2⁵ = 32 paths, but here’s the pattern:

* Every path corresponds to a sequence of `+1` / `-1` over 5 positions.
* Each path produces a final sum `curSum`.
* Exactly 5 of those paths produce `curSum == 3`.

For instance:

1. `+ + + - -` → sum = 1+1+1-1-1 = 1 (oops, miscalc? Let’s be precise)

Let’s properly compute some:

* `+ + + - -` → 1 + 1 + 1 - 1 - 1 = 1
  (not 3)
* `+ + - + -` → 1 + 1 - 1 + 1 - 1 = 1
  not 3
  We need exactly three `+` and two `-`, but signs matter in their positions on a sequence of all 1’s. Actually, sum over 5 ones:

```text
Let #plus = p, #minus = q, p + q = 5
Total sum = p*1 + q*(-1) = p - q
We want p - q = 3 and p + q = 5 → solve:
p - q = 3
p + q = 5
-----------
2p = 8 → p = 4, q = 1
```

So we need **4 pluses and 1 minus** in the sequence of 5 positions.

Number of such sequences: choose 1 position to be minus:

```text
C(5,1) = 5
```

That’s exactly the 5 ways the DP will count and return.

Example valid sequences:

1. `- + + + +` → -1 + 1 + 1 + 1 + 1 = 3
2. `+ - + + +`
3. `+ + - + +`
4. `+ + + - +`
5. `+ + + + -`

For each such sequence, the recursion travels down to `idx == 5` with `curSum == 3`, and returns 1 for that leaf.

All other sign patterns eventually reach `idx == 5` with `curSum != 3` and return 0.

The recursion adds these leaf results up, and with memoization it avoids recomputing repeated `(idx, curSum)` states.
*/
class Solution {
    public int findTargetSumWays(int[] nums, int target) {
        int n = nums.length;
        int sum = 0;
        for (int x : nums) sum += x;

        // If |target| > sum, it's impossible
        if (Math.abs(target) > sum) return 0;

        int offset = sum;
        // memo[idx][curSum + offset]
        Integer[][] memo = new Integer[n][2 * sum + 1];

        return dp(nums, target, 0, 0, memo, offset);
    }

    // dfs(idx, curSum): #ways using nums[idx..] to reach target
    private int dp(int[] nums, int target,
                    int idx, int curSum,
                    Integer[][] memo, int offset) {

        if (idx == nums.length) {
            return curSum == target ? 1 : 0;
        }

        int key = curSum + offset;  // shift sum into [0..2*sum]
        if (memo[idx][key] != null) {
            return memo[idx][key];
        }

        // Choose '+'
        int plus = dp(nums, target, idx + 1, curSum + nums[idx], memo, offset);

        // Choose '-'
        int minus = dp(nums, target, idx + 1, curSum - nums[idx], memo, offset);

        memo[idx][key] = plus + minus;
        return memo[idx][key];
    }
}







// Method 2: Bottom-Up 2D DP (0/1 Knapsack Approach)
/*
## 1. Key idea: reduce to a subset-sum counting problem

Each number `nums[i]` gets a `+` or `-` sign. Let:

* `P` = sum of numbers you chose `+` for
* `N` = sum of numbers you chose `-` for

Then:

```text
P - N = target         (1)
P + N = totalSum       (2)  where totalSum = sum(nums)
```

Add (1) and (2):

```text
2P = target + totalSum
⇒ P = (target + totalSum) / 2
```

So:

> Counting ways to assign `+` and `-` signs is equivalent to counting the number of subsets whose sum is **P**, where
> `P = (target + totalSum) / 2`.

But this only makes sense if:

* `target + totalSum` is **even**, and
* `|target| ≤ totalSum`

Otherwise there is no solution → return 0.

So the problem becomes:

> How many subsets of `nums` have sum `P`?

This is a classic **counting subset-sum** DP.

---

## 2. Bottom-up DP definition

Let:

> `dp[s]` = number of ways to make sum `s` using some prefix of `nums`.

We’ll build this iteratively:

* Start with `dp[0] = 1` (one way to make sum 0: pick nothing).
* For each number `num` in `nums`, we update `dp` from **right to left**:

  ```text
  for s from P down to num:
      dp[s] += dp[s - num]
  ```

Why from right to left?

* We’re using each number at most once (each `nums[i]` must be either `+` or `-`, so a given value is used at most in one side of the subset).
* Going backward ensures each number is only counted once per `dp` row (classic 0/1 knapsack trick).

At the end, `dp[P]` is the answer.

Time: `O(n * P)` where `P ≤ totalSum`
Space: `O(P)`

---

## 4. Thorough example walkthrough

Example:

```text
nums   = [1, 1, 1, 1, 1]
target = 3
```

### 4.1. Compute P

First:

```text
totalSum = 1 + 1 + 1 + 1 + 1 = 5
sumPlusTarget = totalSum + target = 5 + 3 = 8
P = sumPlusTarget / 2 = 8 / 2 = 4
```

So we need to count the number of subsets of `[1,1,1,1,1]` that sum to `4`.

We know the answer should be 5 (there are 5 ways to choose 4 of the 5 ones).

### 4.2. Initialize dp

We have `P = 4`, so `dp` has indices `0..4`.

Initially:

```text
dp[0] = 1
dp[1] = 0
dp[2] = 0
dp[3] = 0
dp[4] = 0
```

Interpretation:

* There is 1 way to make sum 0: choose nothing.
* No ways yet to make positive sums.

---

### 4.3. Process each number one by one

All `nums[i]` are `1`, so we’ll update with `num = 1` five times.

#### After first `1`

We iterate `s` from `P = 4` down to `num = 1`:

* `s = 4`: `dp[4] += dp[3]` → `dp[4] = 0 + 0 = 0`
* `s = 3`: `dp[3] += dp[2]` → `dp[3] = 0 + 0 = 0`
* `s = 2`: `dp[2] += dp[1]` → `dp[2] = 0 + 0 = 0`
* `s = 1`: `dp[1] += dp[0]` → `dp[1] = 0 + 1 = 1`

Now:

```text
dp = [1, 1, 0, 0, 0]
```

Interpretation:
Using the first `1`, we can make:

* sum `0` in 1 way (no pick),
* sum `1` in 1 way (`[1]`),
* others: 0 ways.

---

#### After second `1`

Again `num = 1`:

* `s = 4`: `dp[4] += dp[3]` → `dp[4] = 0 + 0 = 0`
* `s = 3`: `dp[3] += dp[2]` → `dp[3] = 0 + 0 = 0`
* `s = 2`: `dp[2] += dp[1]` → `dp[2] = 0 + 1 = 1`
* `s = 1`: `dp[1] += dp[0]` → `dp[1] = 1 + 1 = 2`

Now:

```text
dp = [1, 2, 1, 0, 0]
```

Interpretation, using two ones:

* sum 0: `[]`
* sum 1: choose exactly one of the two → 2 ways
* sum 2: `[1,1]` → 1 way
* sums 3,4: 0 ways

---

#### After third `1`

`num = 1`:

* `s = 4`: `dp[4] += dp[3]` → `0 + 0 = 0`
* `s = 3`: `dp[3] += dp[2]` → `0 + 1 = 1`
* `s = 2`: `dp[2] += dp[1]` → `1 + 2 = 3`
* `s = 1`: `dp[1] += dp[0]` → `2 + 1 = 3`

Now:

```text
dp = [1, 3, 3, 1, 0]
```

Interpretation, using three ones:

* sum 0: 1 way
* sum 1: 3 ways (pick any 1 of 3)
* sum 2: 3 ways (pick any 2 of 3)
* sum 3: 1 way (`[1,1,1]`)
* sum 4: 0 ways

---

#### After fourth `1`

`num = 1`:

* `s = 4`: `dp[4] += dp[3]` → `0 + 1 = 1`
* `s = 3`: `dp[3] += dp[2]` → `1 + 3 = 4`
* `s = 2`: `dp[2] += dp[1]` → `3 + 3 = 6`
* `s = 1`: `dp[1] += dp[0]` → `3 + 1 = 4`

Now:

```text
dp = [1, 4, 6, 4, 1]
```

Using four ones:

* sum 0: 1 way
* sum 1: C(4,1) = 4
* sum 2: C(4,2) = 6
* sum 3: C(4,3) = 4
* sum 4: C(4,4) = 1

These are exactly the binomial coefficients, as expected when all numbers are identical.

---

#### After fifth `1`

`num = 1` again:

* `s = 4`: `dp[4] += dp[3]` → `1 + 4 = 5`
* `s = 3`: `dp[3] += dp[2]` → `4 + 6 = 10`
* `s = 2`: `dp[2] += dp[1]` → `6 + 4 = 10`
* `s = 1`: `dp[1] += dp[0]` → `4 + 1 = 5`

Final `dp`:

```text
dp = [1, 5, 10, 10, 5]
```

So:

```text
dp[4] = 5
```

That means:

> There are 5 subsets of `[1,1,1,1,1]` that sum to `4`.

Those subsets correspond exactly to picking any 4 out of the 5 ones.

Each such subset corresponds to assigning `+` to those 4 positions and `-` to the remaining 1 position:

* That’s the 4 plus / 1 minus pattern we derived earlier.

Therefore, the number of ways to assign `+/-` to reach target 3 is **5**, which matches `dp[P]`.

---

## 5. Connecting back to the original Target Sum

* We turned the sign assignment problem into subset-sum counting with target `P = (totalSum + target) / 2`.
* The bottom-up DP counts how many subsets reach sum `P`.
* That count is exactly the answer for Target Sum.

So the bottom-up DP solution is:

* Conceptually simple,
* Uses 1D array,
* Runs in `O(n * P)` time and `O(P)` space.

If you’d like, I can also show the **direct** bottom-up DP on `(index, currentSum)` using an offset (similar to the top-down `(idx, curSum)` but tabulated as 2D), and then show how it’s equivalent to this subset-sum version.
*/
// class Solution {
//     public int findTargetSumWays(int[] nums, int target) {
//         int totalSum = 0;
//         for (int x : nums) {
//             totalSum += x;
//         }

//         // If target is out of achievable range, no ways
//         if (Math.abs(target) > totalSum) {
//             return 0;
//         }

//         int sumPlusTarget = totalSum + target;
//         // Must be even to get integer P
//         if (sumPlusTarget % 2 != 0) {
//             return 0;
//         }

//         int P = sumPlusTarget / 2; // subset sum target

//         // dp[s] = number of ways to get sum s
//         int[] dp = new int[P + 1];
//         dp[0] = 1;  // one way to make sum 0: pick nothing

//         // For each number, update dp from right to left
//         for (int num : nums) {
//             for (int s = P; s >= num; s--) {
//                 dp[s] += dp[s - num];
//             }
//         }

//         return dp[P];
//     }
// }






// 