// Method 1: Bottom-Up DP
/*
Okay, bottom-up with hints, coming right up 😄

I’ll give you **layered hints** so you can stop reading when you have enough.

---

### Hint 1: Recall House Robber I (linear)

For the **linear** version (no circle), the key idea is:

* At each house `i`, you choose:

  * **Skip it** → keep `dp[i-1]`
  * **Rob it** → money from this house + `dp[i-2]`
* So the recurrence is:

  ```text
  dp[i] = max(dp[i-1], dp[i-2] + nums[i])
  ```

Try to make sure you’re fully comfortable with that first.

---

### Hint 2: What does “circular” really change?

In House Robber II, first and last house are **neighbors**.

That means you **cannot** choose **both**:

* house `0`
* and house `n-1`

Ask yourself:

> How can I transform this into one or more **linear** problems where this conflict doesn’t exist?

---

### Hint 3: Break into two scenarios

Think in terms of **two separate cases**:

1. **You do NOT rob the first house**

   * Then you’re free to choose from houses `1 .. n-1` (last is allowed).

2. **You do NOT rob the last house**

   * Then you’re free to choose from houses `0 .. n-2` (first is allowed).

In each case, the houses form a **simple line** → you can apply the **House Robber I** DP.

So:

* Case A: run linear DP on subarray `nums[0 .. n-2]`
* Case B: run linear DP on subarray `nums[1 .. n-1]`

Then take the **maximum** of those two answers.

---

### Hint 4: Bottom-up helper for the linear case

Write a **helper** function that solves the **linear** robber problem on a subarray `[start, end]` (inclusive):

* Use **two variables** (or an array) bottom-up:

  * Something like `prev2` (dp[i-2]) and `prev1` (dp[i-1])
* For each index `i` from `start` to `end`:

  * Compute:

    ```text
    pick    = prev2 + nums[i]
    skip    = prev1
    current = max(pick, skip)
    ```
  * Shift:

    ```text
    prev2 = prev1
    prev1 = current
    ```

At the end, `prev1` is the answer for that linear range.

---

### Hint 5: Don’t forget edge cases

Careful with small `n`:

* If `n == 1` → only one house, just return `nums[0]`
* If `n == 2` → circle but you can only pick one → `max(nums[0], nums[1])`

After handling these, you can safely apply the “two ranges” idea.
*/
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        
        // Case A: rob from 0 to n-2
        int caseA = robLinear(nums, 0, n - 2);
        // Case B: rob from 1 to n-1
        int caseB = robLinear(nums, 1, n - 1);
        
        return Math.max(caseA, caseB);
    }
    
    // Standard House Robber I on nums[start..end]
    private int robLinear(int[] nums, int start, int end) {
        int prev2 = 0; // dp[i-2]
        int prev1 = 0; // dp[i-1]
        
        for (int i = start; i <= end; i++) {
            int pick = prev2 + nums[i];  // rob this house + best up to i-2
            int skip = prev1;            // skip this house, keep best up to i-1
            int cur = Math.max(pick, skip);
            
            prev2 = prev1;
            prev1 = cur;
        }
        
        return prev1; // dp[end]
    }
}






// Method 2: Top-Down DP
/*
## 1. Key idea: break the circle into two lines

In House Robber II, houses are in a circle:

* House `0` is adjacent to house `1` **and** house `n-1`.
* So you **cannot** rob both `0` and `n-1`.

This gives two **mutually exclusive** scenarios:

1. You **do not rob house 0**
   → You are free to rob from houses `1 .. n-1` (a linear line).

2. You **do not rob house n-1**
   → You are free to rob from houses `0 .. n-2` (also linear).

So:

* Compute the best you can do on **range `[0 .. n-2]`**.
* Compute the best you can do on **range `[1 .. n-1]`**.
* Answer = `max(those two)`.

We’ll solve each **linear range** using a top-down recursive DP with memoization.

---

## 2. Top-down DP for a linear range

For a given **linear** range `[start .. end]`, define:

> `dfs(i)` = **maximum money you can rob from houses i..end** (inclusive).

At each house `i` (within `[start..end]`), you have two choices:

* **Rob house i**

  * You get `nums[i]`
  * But you must skip `i+1`, so you jump to `i+2`
  * Total = `nums[i] + dfs(i+2)`

* **Skip house i**

  * You get whatever you can from `i+1..end`
  * Total = `dfs(i+1)`

So recurrence:

```text
dfs(i) = max(
    nums[i] + dfs(i + 2),
    dfs(i + 1)
)
```

Base case:

* If `i > end`, there are no houses left → return `0`.

Memoization:

* Create `memo[i]`, initialized to `-1`.
* If `memo[i] != -1`, return it instead of recomputing.

We wrap this in a helper:

```java
robRange(nums, start, end) → dfs(start)
```

## 4. Detailed walkthrough on `[2, 3, 2]`

### Input

```text
nums = [2, 3, 2]
index:  0  1  2
n = 3
```

First, handle trivial case:

* `n != 1`, so we skip the early return.

We compute:

1. `case1 = robRange(nums, 0, 1)` → don’t rob house 2
2. `case2 = robRange(nums, 1, 2)` → don’t rob house 0

Then answer = `max(case1, case2)`.

---

### Case 1: range [0..1] → houses [2,3]

Call:

```java
robRange(nums, 0, 1)
```

Inside `robRange`:

* `memo = [-1, -1, -1]`
* We call `dfs(0, 1, nums, memo)`

#### dfs(0, 1, nums, memo)

`i = 0`, `end = 1`

* `i <= end`, not base case.
* `memo[0] == -1`, so compute.

Compute:

1. **Rob house 0**:

   ```text
   rob = nums[0] + dfs(2, 1, nums, memo)
       = 2       + dfs(2, 1, ...)
   ```

   * Now compute `dfs(2, 1, ...)`:

     * `i = 2`, `end = 1` → `i > end` → base case → return 0.

   So:

   ```text
   rob = 2 + 0 = 2
   ```

2. **Skip house 0**:

   ```text
   skip = dfs(1, 1, nums, memo)
   ```

   Need `dfs(1, 1, ...)`.

#### dfs(1, 1, nums, memo)

`i = 1`, `end = 1`

* `i <= end`, not base case.
* `memo[1] == -1`, so compute.

Again two options:

1. **Rob house 1**:

   ```text
   rob = nums[1] + dfs(3, 1, nums, memo)
       = 3       + dfs(3, 1, ...)
   ```

   * `dfs(3, 1, ...)`: `i = 3 > end = 1` → base case → 0.

   So:

   ```text
   rob = 3 + 0 = 3
   ```

2. **Skip house 1**:

   ```text
   skip = dfs(2, 1, nums, memo)
   ```

   * `dfs(2, 1, ...)`: `i = 2 > 1` → base case → 0.

So:

```text
memo[1] = max(3, 0) = 3
dfs(1)  = 3
```

Back to `dfs(0)`:

* We had:

  ```text
  rob  = 2
  skip = dfs(1) = 3
  ```

So:

```text
memo[0] = max(2, 3) = 3
dfs(0)  = 3
```

Thus:

```text
case1 = robRange(nums, 0, 1) = 3
```

Interpretation:
In [2,3], the optimal is to rob house 1 (value 3) and skip house 0 (value 2).

---

### Case 2: range [1..2] → houses [3,2]

Now we compute:

```java
case2 = robRange(nums, 1, 2)
```

Inside `robRange` again:

* `memo = [-1, -1, -1]` (fresh array)
* Call `dfs(1, 2, nums, memo)`

#### dfs(1, 2, nums, memo)

`i = 1`, `end = 2`

* `memo[1] == -1`

Compute:

1. **Rob house 1**:

   ```text
   rob = nums[1] + dfs(3, 2, nums, memo)
       = 3       + dfs(3, 2, ...)
   ```

   * `dfs(3, 2, ...)`: `i = 3 > 2` → base case → 0

   So:

   ```text
   rob = 3
   ```

2. **Skip house 1**:

   ```text
   skip = dfs(2, 2, nums, memo)
   ```

   Need `dfs(2, 2, ...)`.

#### dfs(2, 2, nums, memo)

`i = 2`, `end = 2`

* `memo[2] == -1`

Options:

1. **Rob house 2**:

   ```text
   rob = nums[2] + dfs(4, 2, nums, memo)
       = 2       + dfs(4, 2, ...)
   ```

   * `dfs(4, 2, ...)`: `i = 4 > 2` → base case → 0

   So:

   ```text
   rob = 2
   ```

2. **Skip house 2**:

   ```text
   skip = dfs(3, 2, nums, memo)
   ```

   * `dfs(3, 2, ...)`: base case → 0

So:

```text
memo[2] = max(2, 0) = 2
dfs(2)  = 2
```

Back to `dfs(1)`:

* `rob  = 3`
* `skip = dfs(2) = 2`

So:

```text
memo[1] = max(3, 2) = 3
dfs(1)  = 3
```

Thus:

```text
case2 = 3
```

Interpretation:
In [3,2], the optimal is to rob house 1 (value 3), skip house 2 (value 2).

---

### Final result

```text
case1 = 3
case2 = 3

answer = max(3, 3) = 3
```

Optimal strategy:

* Rob either house 1 (value 3) and skip houses 0 and 2.
* That respects the circular adjacency constraint (0 and 2 are neighbors, and both neighbor 1).

---

## 5. Quick second example: `[1, 2, 3, 1]`

Just to see the two ranges:

```text
nums = [1, 2, 3, 1]
index:  0  1  2  3
n = 4
```

* Case 1: range [0..2] → [1, 2, 3]

  * Linear best = rob houses 0 and 2 → 1 + 3 = 4

* Case 2: range [1..3] → [2, 3, 1]

  * Linear best = rob houses 1 and 3 → 2 + 1 = 3

So answer = `max(4, 3) = 4`.
Your top-down recursion + memo does exactly this, just via `dfs()` calls.
*/

// class Solution {
//     public int rob(int[] nums) {
//         int n = nums.length;
//         if (n == 1) {
//             // Only one house, no circle problem
//             return nums[0];
//         }
        
//         // Case 1: do NOT rob the last house → consider houses [0 .. n-2]
//         int case1 = robRange(nums, 0, n - 2);
        
//         // Case 2: do NOT rob the first house → consider houses [1 .. n-1]
//         int case2 = robRange(nums, 1, n - 1);
        
//         return Math.max(case1, case2);
//     }
    
//     // Solve standard House Robber I on nums[start..end] using top-down DP
//     private int robRange(int[] nums, int start, int end) {
//         int n = nums.length;
//         int[] memo = new int[n];
//         Arrays.fill(memo, -1);
//         return dfs(start, end, nums, memo);
//     }
    
//     // dfs(i) = max money from houses i..end (inclusive)
//     private int dfs(int i, int end, int[] nums, int[] memo) {
//         // Base case: we've gone past the last house in this range
//         if (i > end) {
//             return 0;
//         }
        
//         if (memo[i] != -1) {
//             return memo[i];
//         }
        
//         // Option 1: rob this house and skip the next one
//         int rob = nums[i] + dfs(i + 2, end, nums, memo);
        
//         // Option 2: skip this house
//         int skip = dfs(i + 1, end, nums, memo);
        
//         memo[i] = Math.max(rob, skip);
//         return memo[i];
//     }
// }
