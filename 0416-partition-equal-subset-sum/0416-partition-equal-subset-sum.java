// Method 1: Top-Down 2D DP
/*
### Issue 1: Base-case order / logic

Right now:

```java
if (i == nums.length) {
    return false;
}
if (remaining < 0) {
    return false;
}
if (remaining == 0) {
    return true;
}
```

This is wrong when you hit a state like `i == nums.length` **and** `remaining == 0`.

That state means:

> “We have used some subset of all elements and exactly hit the target.”

You should return **true**, but your code returns false because it hits `i == nums.length` first and exits.

This can definitely happen:

* Suppose `target = 11`, `nums = [1,5,11,5]`.
* At some point you do a `take` on the last element that makes `remaining` become 0, and `i` becomes `nums.length`. The child call sees `i == nums.length, remaining == 0` and incorrectly returns false.

**Fix:** check `remaining == 0` **before** `i == nums.length`, or handle `i == nums.length` as `return remaining == 0;`.

Key changes:

* Base cases reordered: `remaining == 0` first; `i == n || remaining < 0` treated as failure.
* Added memo read: `if (memo[i][remaining] != null) return memo[i][remaining];`.

---

## 3. Explanation of the top-down DP idea

### Step 1: Problem → subset sum

We want to split `nums` into two subsets with equal sum.

Let `sum` be the total:

* If `sum` is odd → impossible → `false`.
* Otherwise, each subset must sum to `target = sum / 2`.

So problem becomes:

> “Is there a subset of `nums` that sums to `target`?”

Classic **subset-sum** question.

---

### Step 2: Define DP state

We define:

> `dp(i, remaining)` = **true** if we can pick some elements from `nums[i..n-1]` that sum to `remaining`.

We are asked: `dp(0, target)`.

---

### Step 3: Base cases

1. If `remaining == 0`:

   We found a subset whose sum is exactly the target → success.

   ```java
   if (remaining == 0) return true;
   ```

2. If we run out of elements or overshoot:

   * `i == nums.length`: no more elements to use
   * `remaining < 0`: assuming non-negative numbers, we overshot

   Both mean we **cannot** successfully hit exactly zero from here:

   ```java
   if (i == nums.length || remaining < 0) return false;
   ```

So base cases:

```java
if (remaining == 0) return true;
if (i == nums.length || remaining < 0) return false;
```

---

### Step 4: Choices at index `i`

At position `i`, with value `nums[i]`, we have two choices:

1. **Skip** `nums[i]`:

   Do not include it in the subset.

   ```java
   boolean skip = dp(i + 1, remaining);
   ```

2. **Take** `nums[i]`:

   Include it in the subset.

   ```java
   boolean take = dp(i + 1, remaining - nums[i]);
   ```

If **either** gives `true`, then `dp(i, remaining)` is true:

```java
return skip || take;
```

---

### Step 5: Memoization

Different paths in the recursion can reach the same `(i, remaining)` state.

Without memoization, the same subproblem is recomputed many times → exponential time.

We use `memo[i][remaining]`:

* `null` → uncomputed
* `true/false` → stored result

Pattern:

```java
if (memo[i][remaining] != null) return memo[i][remaining];

boolean ans = skip || take;
memo[i][remaining] = ans;
return ans;
```

Time complexity with memo: **O(n * target)**.
Space: **O(n * target)** for the memo table.

---

## 4. Thorough walkthrough: `nums = [1, 5, 11, 5]`

This is the classic example where answer is `true`.

### Step 0: Preprocessing

`nums = [1, 5, 11, 5]`:

```text
sum = 1 + 5 + 11 + 5 = 22
sum % 2 == 0 → okay
target = sum / 2 = 11
```

We want to know if some subset totals to 11.

Create:

```text
n = 4
memo[4][0..11] all null initially
call dp(0, 11)
```

I’ll denote `dp(i, rem)`.

---

### Call 1: `dp(0, 11)`

At index 0, value = 1.

Base checks:

* `remaining == 0`? → 11? no.
* `i == n` or remaining < 0? → 0 == 4? no; 11 < 0? no.
* `memo[0][11]` == null → compute.

Choices:

1. Skip 1: `skip = dp(1, 11)`
2. Take 1: `take = dp(1, 10)` (11 - 1)

`dp(0,11) = dp(1,11) || dp(1,10)`

We’ll explore both branches.

---

### Call 2: `dp(1, 11)` (skip 1)

At index 1, value = 5.

Base checks:

* rem == 0? → no.
* i == n or rem < 0? → 1 == 4? no; 11 < 0? no.
* memo[1][11] is null.

Choices:

* Skip 5: `dp(2, 11)`
* Take 5: `dp(2, 6)` (11 - 5)

So:

```text
dp(1,11) = dp(2,11) || dp(2,6)
```

---

#### Call 3: `dp(2, 11)` (skip 1, skip 5)

Index 2, value = 11.

Base checks:

* rem == 0? → no.
* i == n or rem < 0? → 2 == 4? no; 11 < 0? no.
* memo[2][11] null.

Choices:

* Skip 11: `dp(3, 11)`
* Take 11: `dp(3, 0)` (11 - 11)

So:

```text
dp(2,11) = dp(3,11) || dp(3,0)
```

---

##### Call 4: `dp(3, 11)` (skip 1, skip 5, skip 11)

Index 3, value = 5.

Base checks:

* rem == 0? → no.
* i == n or rem < 0? → 3 == 4? no; 11 < 0? no.
* memo[3][11] null.

Choices:

* Skip 5: `dp(4, 11)`
* Take 5: `dp(4, 6)`

So:

```text
dp(3,11) = dp(4,11) || dp(4,6)
```

Call 5: `dp(4, 11)`:

* rem == 0? → no.
* `i == n`? → 4 == 4 → yes → return **false**.

Call 6: `dp(4, 6)`:

* rem == 0? → no.
* `i == n` → yes → false.

So:

```text
dp(3,11) = false || false = false
memo[3][11] = false
```

Back to `dp(2,11)`:

We still need `dp(3,0)`.

---

##### Call 7: `dp(3, 0)` (skip 1, skip 5, take 11)

Now:

* `remaining == 0` → base case, return **true**.

This is key: we found a subset that sums to 11: `{11}` alone.

So:

```text
dp(2,11) = dp(3,11) || dp(3,0) = false || true = true
memo[2][11] = true
```

Back to `dp(1,11)`, we now know `dp(2,11)` is true. The `||` short-circuit means we don’t *need* `dp(2,6)` to be true:

```text
dp(1,11) = dp(2,11) || dp(2,6) = true || (doesn't matter) = true
memo[1][11] = true
```

Back to `dp(0,11)`, we now know `dp(1,11)` is true, so again:

```text
dp(0,11) = dp(1,11) || dp(1,10) = true || (doesn't matter) = true
memo[0][11] = true
```

So the final result is **true**: there *is* a subset summing to 11.

One valid subset here is `{11}`, and the complement `{1,5,5}` also sums to 11, so we can partition `[1,5,11,5]` into `[11]` and `[1,5,5]`.

Note:

* We never needed to explore all subsets.
* Many states like `dp(2,11)` get memoized; if some other branch asks for `dp(2,11)` again, we just read `memo[2][11]` instead of recomputing.

---

## Complexity

With memo:

* Number of states: `n * (target + 1)`
* Each state does O(1) work (just two recursive calls + memo lookup)
* **Time:** `O(n * target)`
* **Space:** `O(n * target)` for the memo table (plus recursion stack up to `O(n)`).
*/
class Solution {
    public boolean canPartition(int[] nums) {
        int n = nums.length;
        int sum = 0;

        for (int x : nums) {
            sum += x;
        }

        // Total sum must be even to split into two equal subsets
        if (sum % 2 != 0) {
            return false;
        }

        int target = sum / 2;
        Boolean[][] memo = new Boolean[n][target + 1];

        return dp(nums, memo, target, 0);
    }

    // Can we get 'remaining' using elements from index i..end ?
    private boolean dp(int[] nums, Boolean[][] memo, int remaining, int i) {
        // Success: we've hit the target sum exactly
        if (remaining == 0) {
            return true;
        }

        // Out of bounds or overshoot
        if (i == nums.length || remaining < 0) {
            return false;
        }

        // Memo check
        if (memo[i][remaining] != null) {
            return memo[i][remaining];
        }

        // Choice 1: skip current element
        boolean skip = dp(nums, memo, remaining, i + 1);

        // Choice 2: take current element
        boolean take = dp(nums, memo, remaining - nums[i], i + 1);

        boolean ans = skip || take;
        memo[i][remaining] = ans;

        return ans;
    }
}






// Method 2: Bottom-Up 1D DP (0/1 Knapsack Concept)
/*
## 1. Problem → subset sum

We want to split `nums` into **two subsets with equal sum**.

Let:

```text
sum = nums[0] + nums[1] + ... + nums[n-1]
```

* If `sum` is **odd** → cannot split into two equal integers → return `false`.
* Otherwise, each subset must have sum:

```text
target = sum / 2
```

So the problem becomes:

> Is there a subset of `nums` whose sum is exactly `target`?

That’s a classic **0/1 subset sum** (each element can be used at most once).

---

## 2. 2D → 1D DP: what the array means

Instead of a 2D DP `dp[i][s]` (can we get sum `s` using elements up to index `i`?), we compress it to 1D:

> `dp[s]` = **true** if we can form sum `s` using **some subset** of the elements we’ve processed so far.

* Size: `dp[0..target]`.
* We always maintain: after processing the first `k` elements, `dp[s]` tells us if some subset of those `k` elements can sum to `s`.

### Initialization

* We can **always** make sum `0` by taking an empty subset:

  ```text
  dp[0] = true
  ```

* For all other sums initially:

  ```text
  dp[s] = false for s > 0
  ```

### Transition (very important)

For each number `num` in `nums`:

We want to update `dp[s]` to reflect using `num` **once**.

**Key formula**:

```text
If dp[s - num] was true before,
then dp[s] can become true (by adding `num` to that subset).
```

So for each `num`, we do:

```text
for s from target down to num:
    if dp[s - num] is true:
        dp[s] = true
```

Why go **backwards** (from target down to num)?

* Because this is a **0/1** problem (each element at most once).
* If we go forwards (`for s from num to target`), when we set `dp[s] = true`, it would affect `dp[s + num]` in the **same iteration**, effectively using the same number multiple times (like unbounded knapsack).
* Going backwards guarantees that each `num` is only used once per subset.

At the end:

* If `dp[target]` is true → there exists a subset with sum `target` → can partition.
* Else → cannot.

Time: `O(n * target)`
Space: `O(target)`

---

## 4. Detailed walkthrough: `nums = [1, 5, 11, 5]`

We already know from before that this case should return **true**.

### Step 0: Compute sum and target

```text
nums = [1, 5, 11, 5]

sum = 1 + 5 + 11 + 5 = 22
sum % 2 == 0 → OK

target = sum / 2 = 11
```

We want to see if we can form `11` from some subset.

### Step 1: Initialize dp

`dp` is size `target + 1 = 12`:

Indices: `0  1  2  3  4  5  6  7  8  9 10 11`

Initial:

```text
dp[0] = true
dp[1..11] = false

So:

s:    0    1    2    3    4    5    6    7    8    9   10   11
dp:  [T,   F,   F,   F,   F,   F,   F,   F,   F,   F,   F,   F]
```

---

### Step 2: Process num = 1

Loop:

```java
for (int s = target; s >= num; s--)
    for s from 11 down to 1
```

We look for `dp[s - 1]` being true.

Starting:

* `s = 11`: check `dp[10]` (false) → `dp[11]` stays false
* `s = 10`: check `dp[9]` (false)  → `dp[10]` stays false
* ...
* `s = 1`:  check `dp[0]` (true!)  → set `dp[1] = true`

After processing `1`:

```text
dp[0] = true
dp[1] = true  (subset {1})
others remain false

s:    0    1    2    3    4    5    6    7    8    9   10   11
dp:  [T,   T,   F,   F,   F,   F,   F,   F,   F,   F,   F,   F]
```

Interpretation: with subset `{1}`, we can form sums 0 and 1.

---

### Step 3: Process num = 5

Now we process the next element, `5`.

Loop:

```java
for (s = 11 down to 5)
    if (dp[s-5]) dp[s] = true;
```

Check each `s`:

* `s = 11`: check `dp[6]` (currently false) → `dp[11]` stays false.
* `s = 10`: check `dp[5]` (false) → `dp[10]` stays false.
* `s = 9`: check `dp[4]` (false) → `dp[9]` stays false.
* `s = 8`: check `dp[3]` (false) → `dp[8]` stays false.
* `s = 7`: check `dp[2]` (false) → `dp[7]` stays false.
* `s = 6`: check `dp[1]` (true!) → set `dp[6] = true`.
* `s = 5`: check `dp[0]` (true!) → set `dp[5] = true`.

Now `dp`:

```text
dp[0] = true
dp[1] = true           // {1}
dp[5] = true           // {5}
dp[6] = true           // {1, 5}
others false

s:    0    1    2    3    4    5    6    7    8    9   10   11
dp:  [T,   T,   F,   F,   F,   T,   T,   F,   F,   F,   F,   F]
```

So with `{1,5}`, we can form sums:

* 0 (empty subset)
* 1 (`{1}`)
* 5 (`{5}`)
* 6 (`{1,5}`)

---

### Step 4: Process num = 11

Now process `11`.

Loop:

```java
for (s = 11 down to 11)
    if (dp[s-11]) dp[s] = true;
```

At `s = 11`: check `dp[0]`:

* `dp[0]` is true → set `dp[11] = true`.

Now `dp`:

```text
dp[11] = true   // subset {11}

s:    0    1    2    3    4    5    6    7    8    9   10   11
dp:  [T,   T,   F,   F,   F,   T,   T,   F,   F,   F,   F,   T]
```

At this moment, `dp[target] = dp[11]` is **true**.
That already tells us we can form sum 11 from `{11}`.

The algorithm will still finish processing the last element, but logically we already know the answer will be true.

---

### Step 5: Process last num = 5

For completeness, we can follow one more step with the last `5`:

Now `dp` currently has `true` at positions 0, 1, 5, 6, 11.

Loop for `num = 5`:

```java
for (s = 11 down to 5) {
    if (dp[s-5]) dp[s] = true;
}
```

Step by step:

* `s = 11`: check `dp[6]` which is true → `dp[11]` stays true.

  * That corresponds to subset `{1,5,5}` → sum 11.
* `s = 10`: check `dp[5]` (true) → `dp[10] = true` (subset `{5,5}`)
* `s = 9`: check `dp[4]` (false) → `dp[9]` stays false.
* `s = 8`: check `dp[3]` (false) → `dp[8]` stays false.
* `s = 7`: check `dp[2]` (false) → `dp[7]` stays false.
* `s = 6`: check `dp[1]` (true) → `dp[6]` stays true (already true).

Final `dp` state:

```text
s:    0    1    2    3    4    5    6    7    8    9   10   11
dp:  [T,   T,   F,   F,   F,   T,   T,   F,   F,   F,   T,   T]
```

Key point: `dp[11] == true`.

So we can definitely form a subset summing to 11, e.g.:

* `{11}`, or
* `{1,5,5}`

Hence the array can be partitioned:

* `{11}` and `{1,5,5}` both sum to 11.

So `canPartition` returns **true**.

---

## Summary

1D DP for **Partition Equal Subset Sum**:

* Reduce to: “Does there exist a subset that sums to `target = totalSum / 2`?”
* Use `boolean dp[target+1]`:

  * `dp[0] = true`
  * For each `num`:

    * For `s` from `target` down to `num`:

      * If `dp[s - num]` is true, mark `dp[s] = true`.
* Answer is `dp[target]`.
*/

// class Solution {
//     public boolean canPartition(int[] nums) {
//         int n = nums.length;
//         int sum = 0;

//         for (int x : nums) {
//             sum += x;
//         }

//         // If total sum is odd, can't split into two equal subsets
//         if (sum % 2 != 0) {
//             return false;
//         }

//         int target = sum / 2;

//         // dp[s] = true if we can form sum s using some subset of nums
//         boolean[] dp = new boolean[target + 1];
//         dp[0] = true;  // we can always have sum 0 with empty subset

//         for (int num : nums) {
//             // Traverse backwards to avoid reusing this num multiple times
//             for (int s = target; s >= num; s--) {
//                 if (dp[s - num]) {
//                     dp[s] = true;
//                 }
//             }
//         }

//         return dp[target];
//     }
// }
