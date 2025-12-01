// Method 1: Bottom-Up 1D DP (Similar approach to Kadane's Algorithm)
/*
## 1. What your code was doing

Your code:

```java
class Solution {
    public int maxProduct(int[] nums) {
        int n = nums.length;
        Integer[][] memo = new Integer[n][n];
        int best = Integer.MIN_VALUE / 4;

        for (int l = 0; l < n; l++) {
            for (int r = l; r < n; r++) {
                best = Math.max(best, dp(nums, memo, l, r));
            }
        }

        return best;
    }

    private int dp(int[] nums, Integer[][] memo, int l, int r) {
        if (l > r) {
            return 1;
        }

        if (memo[l][r] != null) {
            return memo[l][r];
        }

        int product = nums[l] * dp(nums, memo, l + 1, r);
        memo[l][r] = product;

        return memo[l][r];
    }
}
```

### Interpretation

* The outer double loop:

  ```java
  for (int l = 0; l < n; l++) {
      for (int r = l; r < n; r++) {
          best = Math.max(best, dp(nums, memo, l, r));
      }
  }
  ```

  For **every subarray** `nums[l..r]`, you compute its product via `dp(l, r)` and update the global `best`.

* The recursive `dp(l, r)`:

  ```java
  if (l > r) return 1; // empty product = 1

  dp(l, r) = nums[l] * dp(l+1, r)
  ```

  So:

  ```text
  dp(l, r) = nums[l] * nums[l+1] * ... * nums[r]
  ```

  with the base case for `l > r` acting as “multiplicative identity” (product of empty set is 1).

So you’re literally enumerating **all contiguous subarrays** and taking their product. That *will* find the correct max product (ignoring overflow), because by definition the answer is just the maximum over products of all contiguous subarrays.

---

## 2. Why this is not acceptable for LeetCode

### 2.1. Time complexity

Number of subarrays in an array of length `n` is:

[
\frac{n(n+1)}{2} = O(n^2).
]

You have:

* Outer loops: `O(n^2)` pairs `(l, r)`.
* For each `(l, r)`, you call `dp(l, r)`, but with memoization each `(l, r)` is computed only once.
* Total distinct `(l, r)` states: also O(n²).

Thus total time complexity is **O(n²)**.

For this problem, `n` can be up to around `2 * 10^4`.

* `n²` ≈ 4 × 10^8 (400 million) states.
* That’s way too big:

  * You’ll do hundreds of millions of operations.
  * Plus recursion overhead.

You will almost certainly **TLE** (time limit exceeded) on LeetCode.

### 2.2. Space complexity

You have:

```java
Integer[][] memo = new Integer[n][n];
```

* That’s `n²` `Integer` references.
* For `n ~ 2 * 10^4`, that’s ~4 × 10^8 entries.
* Each `Integer` object also has overhead (not just 4 bytes).

This explodes memory — you’re looking at several *gigabytes* of usage in practice → **MLE** (memory limit exceeded).

### 2.3. Recursion depth

`dp(l, r)` recursively calls `dp(l+1, r)` until `l > r`, so depth can be up to ~n (worst case when r = n-1, l goes from 0 to n).

* With `n ~ 2 * 10^4`, you may also hit **stack overflow** in Java.

---

### Summary of “wrongness”

* **Logically** (ignoring overflow), your approach *can* find the correct answer.
* But **algorithmically**, it’s way too slow and memory-hungry for the problem constraints.

LeetCode 152 expects an **O(n)** time, **O(1)** extra space solution.

---

## 3. The intended O(n) DP approach (max + min tracking)

This problem has a classic trick: because we’re dealing with **products** and **negative numbers**, we need to keep track of:

* The **maximum** product ending at this index
* The **minimum** product ending at this index

Why the minimum as well? Because:

* A negative number times another negative number becomes **positive**.
* So the smallest (most negative) product so far might become the **largest** if we multiply it by a negative.

### 3.1. Define the DP states

For each index `i`:

* `maxHere` = the **maximum product** of a subarray that **ends at index i**.
* `minHere` = the **minimum product** of a subarray that **ends at index i**.

We also keep a global `ans` = maximum over all `maxHere` values.

### 3.2. Transition

At position `i`, with value `x = nums[i]`, we have three choices (conceptually):

1. Start a new subarray at `i` → product is just `x`.
2. Extend the previous maximum subarray: `x * prevMaxHere`.
3. Extend the previous minimum subarray: `x * prevMinHere`.

So:

```text
maxHere = max(x, x * prevMaxHere, x * prevMinHere)
minHere = min(x, x * prevMaxHere, x * prevMinHere)
```

Then:

```text
ans = max(ans, maxHere)
```

Base case at `i = 0`:

```text
maxHere = nums[0]
minHere = nums[0]
ans = nums[0]
```

We iterate `i` from 1 to n-1.

Time: O(n). Space: O(1).

---

## 4. Detailed example walkthroughs

Let’s walk through two examples step by step.

### Example 1: nums = [2, 3, -2, 4]

All subarrays and their products (for sanity):

* [2] → 2
* [2,3] → 6
* [2,3,-2] → -12
* [2,3,-2,4] → -48
* [3] → 3
* [3,-2] → -6
* [3,-2,4] → -24
* [-2] → -2
* [-2,4] → -8
* [4] → 4

Maximum product is **6**, from subarray [2,3].

Now step through the DP:

#### Initial (i = 0)

`nums[0] = 2`.

```text
maxHere = 2
minHere = 2
ans     = 2
```

#### i = 1, x = 3

We have previous:

```text
prevMax = 2
prevMin = 2
x = 3
```

Candidates:

* `x` = 3               (start new subarray [3])
* `x * prevMax` = 3 * 2 = 6   (extend [2] to [2,3])
* `x * prevMin` = 3 * 2 = 6   (same here)

So:

```text
maxHere = max(3, 6, 6) = 6
minHere = min(3, 6, 6) = 3
ans     = max(2, 6) = 6
```

Interpretation:

* Best product ending at index 1 is 6 (subarray [2,3]).
* Worst (most negative) is 3 (in this case still positive).

#### i = 2, x = -2

Now:

```text
prevMax = 6
prevMin = 3
x = -2
```

Candidates for max and min:

* `x` = -2              (start new at [-2])
* `x * prevMax` = -2 * 6 = -12
* `x * prevMin` = -2 * 3 = -6

So:

```text
maxHere = max(-2, -12, -6) = -2
minHere = min(-2, -12, -6) = -12
ans     = max(6, -2) = 6
```

Interpretation:

* Any subarray ending at index 2 has a non-positive product; best is just [-2] with product -2.
* But we keep ans = 6 from earlier.

#### i = 3, x = 4

Now:

```text
prevMax = -2
prevMin = -12
x = 4
```

Candidates:

* `x` = 4               (start [4])
* `x * prevMax` = 4 * (-2) = -8
* `x * prevMin` = 4 * (-12) = -48

So:

```text
maxHere = max(4, -8, -48) = 4
minHere = min(4, -8, -48) = -48
ans     = max(6, 4) = 6
```

So final answer for [2,3,-2,4] is **6**, as expected.

---

### Example 2: nums = [-2, 3, -4]

All subarrays products:

* [-2] → -2
* [-2,3] → -6
* [-2,3,-4] → 24
* [3] → 3
* [3,-4] → -12
* [-4] → -4

Maximum product is **24** from subarray [-2,3,-4].

Now step by step:

#### i = 0, x = -2

```text
maxHere = -2
minHere = -2
ans     = -2
```

#### i = 1, x = 3

```text
prevMax = -2
prevMin = -2
x = 3
```

Candidates:

* `x` = 3
* `x * prevMax` = 3 * (-2) = -6
* `x * prevMin` = 3 * (-2) = -6

So:

```text
maxHere = max(3, -6, -6) = 3
minHere = min(3, -6, -6) = -6
ans     = max(-2, 3) = 3
```

Now best product ending at index 1 is 3 ([3]), worst is -6 ([-2,3]).

#### i = 2, x = -4

```text
prevMax = 3
prevMin = -6
x = -4
```

Candidates:

* `x` = -4           (start new [-4])
* `x * prevMax` = -4 * 3 = -12
* `x * prevMin` = -4 * (-6) = 24

So:

```text
maxHere = max(-4, -12, 24) = 24
minHere = min(-4, -12, 24) = -12
ans     = max(3, 24) = 24
```

We correctly find max product = **24**, corresponding to subarray [-2,3,-4].

Notice how the **minimum** product (-6) at index 1 became the **maximum** (24) at index 2 when multiplied by another negative (-4). That’s exactly why we must track both `maxHere` and `minHere`. A simple one-dimensional DP (like Kadane) that tracks only max would miss this.

---

## 5. Takeaways for your solution

What’s “wrong” with your approach?

1. **Complexity**:

   * O(n²) time and O(n²) space with recursion: not usable for n up to 2×10⁴.
   * LeetCode expects O(n) time, O(1) extra space.

2. **Stack & memory**:

   * 2D `Integer` memo → huge memory usage, likely MLE.
   * Deep recursion → potential stack overflow.

3. **Conceptual**:

   * You’re brute-forcing all subarrays via DP.
   * The trick of this problem is to exploit the structure (negative sign flips and zero) to solve it in one pass, tracking max & min suffix products.
*/
class Solution {
    public int maxProduct(int[] nums) {
        int n = nums.length;
        
        int maxHere = nums[0]; // max product ending at i
        int minHere = nums[0]; // min product ending at i
        int ans = nums[0];     // global maximum
        
        for (int i = 1; i < n; i++) {
            int x = nums[i];
            
            // We need prev values, so store them
            int prevMax = maxHere;
            int prevMin = minHere;
            
            // Candidates:
            // 1) start new at x
            // 2) extend previous max
            // 3) extend previous min
            maxHere = Math.max(x, Math.max(x * prevMax, x * prevMin));
            minHere = Math.min(x, Math.min(x * prevMax, x * prevMin));
            
            ans = Math.max(ans, maxHere);
        }
        
        return ans;
    }
}






// Method 2: Top-Down 1D DP
/*
## 1. Core idea: why this problem is tricky

We want:

> The maximum product of any **contiguous subarray** in `nums`.

Problems:

* We have **negative numbers**:

  * A large positive can become negative when multiplied by a negative.
  * A large negative can become a large positive if multiplied by another negative.
* We also have **zeros**:

  * Multiplying by zero resets the product.

So just tracking a single “max so far” like Kadane’s algorithm (for sum) is not enough.
At each index `i`, we must know:

* The **maximum product** of a subarray that **ends at i**
* The **minimum product** of a subarray that **ends at i**

Because the minimum might become the maximum if we multiply by a negative.

---

## 2. Top-down DP state and recurrence

We’ll define a recursive function that works from **left to right**.

### DP state

For each index `i`, define:

* `maxEnd[i]` = maximum product of any subarray that **ends exactly at index `i`**.
* `minEnd[i]` = minimum product of any subarray that **ends exactly at index `i`**.

We’ll compute `(maxEnd[i], minEnd[i])` with recursion and memoization.

We also keep a global variable:

* `globalMax` = maximum of all `maxEnd[i]` over all i.

At the end, the answer is `globalMax`.

### Base case

At `i = 0`, the only subarray ending at index 0 is `[nums[0]]`:

```text
maxEnd[0] = nums[0]
minEnd[0] = nums[0]
globalMax = nums[0]
```

### Recursive case

To compute `(maxEnd[i], minEnd[i])` for `i > 0`, we first compute `(maxEnd[i-1], minEnd[i-1])`:

Let:

* `x = nums[i]`
* `prevMax = maxEnd[i-1]`
* `prevMin = minEnd[i-1]`

Three ways to form a subarray ending at `i`:

1. **Start a new subarray at i**: `[x]`
   → product = `x`
2. **Extend the previous max subarray**: some best subarray ending at `i-1`, then include `x`
   → product = `x * prevMax`
3. **Extend the previous min subarray**: some worst subarray ending at `i-1`, then include `x`
   → product = `x * prevMin`

Because `x` might be negative, `x * prevMin` could become the new maximum.

So we set:

```text
maxEnd[i] = max( x, x * prevMax, x * prevMin )
minEnd[i] = min( x, x * prevMax, x * prevMin )
```

Then:

```text
globalMax = max(globalMax, maxEnd[i])
```

We do this with a recursive helper `dfs(i)` that:

* Returns after filling `maxEnd[i]` and `minEnd[i]` (using memo to avoid recomputation)
* Recursively calls `dfs(i-1)` when needed.


* Time: **O(n)** — each `i` is processed once.
* Space: **O(n)** for the two memo arrays (you could optimize to O(1) if you convert it to bottom-up).

---

## 4. Walkthrough on `[2, 3, -2, 4]`

Array:

```text
nums = [2, 3, -2, 4]
 index: 0  1   2  3
```

We call `maxProduct(nums)`:

* `n = 4`
* `maxEnd` = [null, null, null, null]
* `minEnd` = [null, null, null, null]
* Call `dfs(3)`

### Step 1: dfs(3)

`dfs(3)` calls `dfs(2)`
`dfs(2)` calls `dfs(1)`
`dfs(1)` calls `dfs(0)`

---

### Step 2: dfs(0) — base case

At `i = 0`, `nums[0] = 2`:

```text
maxEnd[0] = 2
minEnd[0] = 2
globalMax = 2
```

Return to `dfs(1)`.

---

### Step 3: dfs(1)

Now `i = 1`, `nums[1] = 3`:

We already have:

```text
maxEnd[0] = 2
minEnd[0] = 2
globalMax = 2
```

Compute:

* `x = 3`
* `prevMax = maxEnd[0] = 2`
* `prevMin = minEnd[0] = 2`

Candidates:

```text
cand1 = x           = 3         // start [3]
cand2 = x * prevMax = 3 * 2 = 6 // extend [2] to [2,3]
cand3 = x * prevMin = 3 * 2 = 6 // same as above
```

So:

```text
curMax = max(3, 6, 6) = 6
curMin = min(3, 6, 6) = 3
```

Set:

```text
maxEnd[1] = 6
minEnd[1] = 3
globalMax = max(2, 6) = 6
```

Interpretation:

* Best subarray ending at index 1 is `[2, 3]` with product 6.
* Worst (minimum) is `[3]` with product 3.

Return to `dfs(2)`.

---

### Step 4: dfs(2)

Now `i = 2`, `nums[2] = -2`:

Current info:

```text
maxEnd[1] = 6
minEnd[1] = 3
globalMax = 6
```

Compute:

* `x = -2`
* `prevMax = 6`
* `prevMin = 3`

Candidates:

```text
cand1 = x            = -2             // start new subarray [-2]
cand2 = x * prevMax  = -2 * 6  = -12  // extend the best [2,3] → [2,3,-2]
cand3 = x * prevMin  = -2 * 3  = -6   // extend [3] → [3,-2]
```

So:

```text
curMax = max(-2, -12, -6) = -2
curMin = min(-2, -12, -6) = -12
```

Set:

```text
maxEnd[2] = -2
minEnd[2] = -12
globalMax = max(6, -2) = 6
```

Interpretation:

* Any subarray ending at index 2 has a non-positive product; the best is just `[-2]` with product -2, which is still worse than the global max 6.

Return to `dfs(3)`.

---

### Step 5: dfs(3)

Now `i = 3`, `nums[3] = 4`:

Current:

```text
maxEnd[2] = -2
minEnd[2] = -12
globalMax = 6
```

Compute:

* `x = 4`
* `prevMax = -2`
* `prevMin = -12`

Candidates:

```text
cand1 = x            = 4           // start [4]
cand2 = x * prevMax  = 4 * (-2)  = -8
cand3 = x * prevMin  = 4 * (-12) = -48
```

So:

```text
curMax = max(4, -8, -48) = 4
curMin = min(4, -8, -48) = -48
```

Set:

```text
maxEnd[3] = 4
minEnd[3] = -48
globalMax = max(6, 4) = 6
```

So finally:

```text
maxEnd = [ 2,  6,  -2,  4 ]
minEnd = [ 2,  3, -12, -48 ]
globalMax = 6
```

Answer = 6, which corresponds to subarray `[2, 3]`.

---

## 5. Second example: `[-2, 3, -4]` (showing negative × negative)

This one is great for seeing why we track minimum as well:

```text
nums = [-2, 3, -4]
index:  0   1   2
```

All contiguous products:

* [-2] → -2
* [-2,3] → -6
* [-2,3,-4] → 24
* [3] → 3
* [3,-4] → -12
* [-4] → -4

Max product = **24** from `[-2,3,-4]`.

Let’s see what the DP does.

### dfs(0)

`nums[0] = -2`:

```text
maxEnd[0] = -2
minEnd[0] = -2
globalMax = -2
```

### dfs(1)

`nums[1] = 3`:

* `prevMax = -2`
* `prevMin = -2`
* `x = 3`

Candidates:

```text
cand1 = 3
cand2 = 3 * (-2) = -6
cand3 = 3 * (-2) = -6
```

So:

```text
maxEnd[1] = max(3, -6, -6) = 3
minEnd[1] = min(3, -6, -6) = -6
globalMax = max(-2, 3) = 3
```

### dfs(2)

`nums[2] = -4`:

* `prevMax = maxEnd[1] = 3`
* `prevMin = minEnd[1] = -6`
* `x = -4`

Candidates:

```text
cand1 = -4
cand2 = -4 * 3   = -12
cand3 = -4 * (-6)= 24
```

So:

```text
maxEnd[2] = max(-4, -12, 24) = 24
minEnd[2] = min(-4, -12, 24) = -12
globalMax = max(3, 24) = 24
```

That negative minimum at index 1 (`-6`) turned into a positive maximum (`24`) when multiplied by `-4`. That’s exactly the phenomenon this DP is designed to capture.

---

## Wrap-up

Top-down approach for **152. Maximum Product Subarray**:

* **State:** for each index `i`, track `(maxEnd[i], minEnd[i])`, the max and min product of subarrays that **end at i**.
* **Transition:** use previous `(maxEnd[i-1], minEnd[i-1])` and `nums[i]` to compute three candidates:

  * start new at `i`
  * extend previous max
  * extend previous min
* **Recursion:** `dfs(i)` calls `dfs(i-1)` if needed (memoized), then computes `maxEnd[i]`, `minEnd[i]`, and updates `globalMax`.
* **Answer:** `globalMax`.
*/

// class Solution {
//     private int[] nums;
//     private Integer[] maxEnd;  // max product of subarray ending at i
//     private Integer[] minEnd;  // min product of subarray ending at i
//     private int globalMax;

//     public int maxProduct(int[] nums) {
//         int n = nums.length;
//         this.nums = nums;
//         this.maxEnd = new Integer[n];
//         this.minEnd = new Integer[n];

//         // Base will be handled inside dfs(0)
//         dfs(n - 1);  // ensure all states up to last index are computed

//         return globalMax;
//     }

//     // Compute maxEnd[i], minEnd[i] recursively (top-down)
//     private void dfs(int i) {
//         if (maxEnd[i] != null && minEnd[i] != null) {
//             // Already computed
//             return;
//         }

//         if (i == 0) {
//             // Base case: only one element subarray [nums[0]]
//             maxEnd[0] = nums[0];
//             minEnd[0] = nums[0];
//             globalMax = nums[0];  // initialize globalMax
//             return;
//         }

//         // Ensure previous state is computed
//         dfs(i - 1);

//         int x = nums[i];
//         int prevMax = maxEnd[i - 1];
//         int prevMin = minEnd[i - 1];

//         int cand1 = x;                 // start new at i
//         int cand2 = x * prevMax;       // extend previous max
//         int cand3 = x * prevMin;       // extend previous min

//         int curMax = Math.max(cand1, Math.max(cand2, cand3));
//         int curMin = Math.min(cand1, Math.min(cand2, cand3));

//         maxEnd[i] = curMax;
//         minEnd[i] = curMin;

//         // Update global maximum
//         globalMax = Math.max(globalMax, curMax);
//     }
// }
