// Method 1: Kadane's Algorithn (DP)
/*
# LeetCode 53. Maximum Subarray — Hints (C++)

This is a **Dynamic Programming + Greedy + Kadane's Algorithm** problem.

The key idea is that instead of checking every possible subarray, you can determine the maximum subarray sum **ending at each index** using the result from the previous index.

This is closely related to:

- **121. Best Time to Buy and Sell Stock:** Track the best result while scanning an array.
- **152. Maximum Product Subarray:** Track maximum and minimum products ending at each index.
- **918. Maximum Sum Circular Subarray:** Extend Kadane's Algorithm to circular arrays.
- **198. House Robber:** Use 1D DP to make optimal decisions based on previous states.
- **674. Longest Continuous Increasing Subsequence:** Track the best contiguous sequence ending at each index.

The major difference is that **the selected subarray must be contiguous, and it must contain at least one element**.

We'll derive the DP recurrence, optimize it to constant space, and understand why Kadane's Algorithm works.

---

## Hint 1: Understand the problem

You're given:

```cpp
vector<int>& nums;
```

Your goal is to find the maximum sum of any **nonempty contiguous subarray**.

For example:

```cpp
nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
```

The maximum-sum subarray is:

```text
{4, -1, 2, 1}
```

Its sum is:

```text
4 + (-1) + 2 + 1 = 6
```

Therefore:

```cpp
answer = 6;
```

**Important:** You cannot arbitrarily skip elements inside the selected subarray.

For example:

```cpp
nums = {4, -1, 2};
```

You cannot select:

```text
{4, 2}
```

because those elements are not contiguous.

However, you can select:

```text
{4, -1, 2}
```

whose sum is:

```text
5
```

---

## Hint 2: Start with the brute-force approach

A simple approach is to consider every possible subarray.

A subarray is determined by:

```cpp
int left;
int right;
```

where:

```text
0 <= left <= right < n
```

For each starting index, extend the subarray toward the right while maintaining its running sum.

For example:

```cpp
nums = {2, -1, 3};
```

Possible subarrays include:

```text
{2}          → 2
{2, -1}      → 1
{2, -1, 3}   → 4

{-1}         → -1
{-1, 3}      → 2

{3}          → 3
```

The maximum sum is:

```cpp
answer = 4;
```

There are:

\[
\frac{n(n+1)}{2}
\]

nonempty subarrays.

Using a running sum, brute force takes:

\[
\boxed{O(n^2)}
\]

Can we avoid checking every starting index?

**Key observation:** When processing a new element, we only need to know whether extending the previous subarray is beneficial.

---

## Hint 3: Think about subarrays ending at index `i`

Instead of asking:

> What is the maximum sum among all possible subarrays?

Ask a smaller question:

> What is the maximum sum of a nonempty subarray that must end at index `i`?

For example:

```cpp
nums = {4, -1, 2};
```

Consider subarrays ending at index `2`.

They are:

```text
{2}          → 2

{-1, 2}      → 1

{4, -1, 2}   → 5
```

The maximum sum is:

```text
5
```

Now consider:

```cpp
nums = {-5, 2};
```

Subarrays ending at index `1`:

```text
{2}       → 2

{-5, 2}   → -3
```

The maximum sum is:

```text
2
```

Notice the difference.

Sometimes extending a previous subarray helps.

Sometimes starting a new subarray is better.

This leads to our DP state.

---

## Hint 4: Define the DP state

Let:

```cpp
dp[i]
```

represent:

```text
Maximum sum of a nonempty
contiguous subarray that
MUST end at index i.
```

For example:

```cpp
nums = {4, -1, 2};
```

Then:

```text
dp[0] = 4
```

because the only subarray ending at index `0` is:

```text
{4}
```

Next:

```text
dp[1] = 3
```

because:

```text
{4, -1} → 3

{-1}    → -1
```

Finally:

```text
dp[2] = 5
```

because:

```text
{4, -1, 2} → 5
```

### Important distinction

`dp[i]` does **not** represent the maximum subarray sum anywhere within the first `i + 1` elements.

It represents the maximum subarray sum **ending exactly at index `i`**.

This distinction is essential for deriving the recurrence.

---

## Hint 5: Derive the DP transition

Suppose you're calculating:

```cpp
dp[i]
```

Every subarray ending at index `i` has two possibilities.

### Option 1: Start a new subarray at index `i`

Select only:

```cpp
nums[i]
```

The sum is:

```cpp
nums[i]
```

### Option 2: Extend the best subarray ending at index `i - 1`

Take the previous maximum ending sum:

```cpp
dp[i - 1]
```

and append:

```cpp
nums[i]
```

The resulting sum is:

```cpp
dp[i - 1] + nums[i]
```

Since we want the maximum sum, choose the better option.

### Core recurrence

\[
\boxed{dp[i]=\max(nums[i],dp[i-1]+nums[i])}
\]

In C++:

```cpp
dp[i] = max(
    nums[i],
    dp[i - 1] + nums[i]
);
```

### Mental model

```text
             dp[i]
               |
               v
     Maximum subarray sum
       ending at index i
               |
       ┌───────┴────────┐
       |                |
       v                v
   Start new        Extend previous
   subarray         best subarray
       |                |
       v                v
    nums[i]       dp[i-1] + nums[i]
       |                |
       └───────┬────────┘
               |
               v
           Take MAX
               |
               v
            dp[i]
```

**This is the most important recurrence in the problem.**

---

## Hint 6: Understand when to restart the subarray

Look at the recurrence:

```cpp
dp[i] = max(
    nums[i],
    dp[i - 1] + nums[i]
);
```

We can rewrite it as:

```cpp
dp[i] = nums[i] + max(0, dp[i - 1]);
```

Why?

Because we have two choices:

```text
Start fresh:

nums[i] + 0
```

or:

```text
Extend:

nums[i] + dp[i-1]
```

Therefore:

```cpp
nums[i] + max(0, dp[i - 1])
```

selects whichever is better.

### Case 1: Previous sum is positive

Suppose:

```text
dp[i-1] = 5

nums[i] = -2
```

Starting fresh gives:

```text
-2
```

Extending gives:

```text
5 + (-2) = 3
```

Therefore:

```text
dp[i] = 3
```

Even though the current number is negative, extending is better.

### Case 2: Previous sum is negative

Suppose:

```text
dp[i-1] = -5

nums[i] = 3
```

Starting fresh gives:

```text
3
```

Extending gives:

```text
-5 + 3 = -2
```

Therefore:

```text
dp[i] = 3
```

Starting a new subarray is better.

### Greedy insight

**A negative previous subarray sum cannot improve the sum of a future subarray.**

Therefore, discard it and start fresh.

If the previous sum is positive, extending is better than starting fresh.

If it is zero, both choices have the same sum.

This is the central idea behind **Kadane's Algorithm**.

---

## Hint 7: Understand the base case

Since:

```cpp
dp[i]
```

represents the maximum subarray sum ending at index `i`, consider:

```cpp
dp[0]
```

There is only one nonempty subarray ending at index `0`:

```text
{nums[0]}
```

Therefore:

```cpp
dp[0] = nums[0];
```

In C++:

```cpp
vector<int> dp(n);

dp[0] = nums[0];
```

### Why not initialize with zero?

Consider:

```cpp
nums = {-5};
```

The only valid nonempty subarray is:

```text
{-5}
```

Therefore:

```cpp
answer = -5;
```

If you allow an empty subarray with sum `0`, you might incorrectly return:

```cpp
0
```

But the problem requires a **nonempty subarray**.

This is why initialization matters.

---

## Hint 8: Track the global maximum

Remember:

```cpp
dp[i]
```

represents the maximum sum of a subarray ending at index `i`.

But the overall best subarray can end at any index.

Therefore, the answer is:

\[
\boxed{\max_{0\leq i<n}dp[i]}
\]

You can track it using:

```cpp
int maxSum = nums[0];
```

After calculating each DP state:

```cpp
maxSum = max(maxSum, dp[i]);
```

### Why not simply return `dp[n - 1]`?

Consider:

```cpp
nums = {5, -10, 1};
```

The DP values are:

```text
dp[0] = 5

dp[1] = -5

dp[2] = 1
```

The maximum subarray sum is:

```text
5
```

But:

```text
dp[2] = 1
```

Therefore, returning the last DP state would be incorrect.

**Important distinction:**

```text
dp[i]  → Best subarray ending at i.

maxSum → Best subarray seen anywhere so far.
```

---

## Hint 9: Walk through a complete example

Consider:

```cpp
nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
```

Initialize:

```cpp
dp[0] = -2;

maxSum = -2;
```

Now apply:

```cpp
dp[i] = max(
    nums[i],
    dp[i - 1] + nums[i]
);
```

### DP simulation

| Index `i` | `nums[i]` | Start new | Extend previous | `dp[i]` | `maxSum` |
|---|---:|---:|---:|---:|---:|
| 0 | -2 | -2 | — | -2 | -2 |
| 1 | 1 | 1 | -1 | 1 | 1 |
| 2 | -3 | -3 | -2 | -2 | 1 |
| 3 | 4 | 4 | 2 | 4 | 4 |
| 4 | -1 | -1 | 3 | 3 | 4 |
| 5 | 2 | 2 | 5 | 5 | 5 |
| 6 | 1 | 1 | 6 | 6 | 6 |
| 7 | -5 | -5 | 1 | 1 | 6 |
| 8 | 4 | 4 | 5 | 5 | 6 |

Notice the important steps.

### At index `3`

```cpp
nums[3] = 4;
```

The previous ending sum is:

```text
dp[2] = -2
```

Extending gives:

```text
-2 + 4 = 2
```

Starting fresh gives:

```text
4
```

Therefore:

```text
dp[3] = 4
```

We discard the previous negative sum.

### At index `4`

```cpp
nums[4] = -1;
```

The previous ending sum is:

```text
dp[3] = 4
```

Extending gives:

```text
4 + (-1) = 3
```

Starting fresh gives:

```text
-1
```

Therefore:

```text
dp[4] = 3
```

Even though the current element is negative, we keep it because extending the previous subarray is better.

### At index `6`

```cpp
nums[6] = 1;
```

We get:

```text
dp[6] = 6
```

The corresponding subarray is:

```text
{4, -1, 2, 1}
```

Therefore:

```cpp
maxSum = 6;
```

No later subarray exceeds this sum.

Final answer:

```cpp
return 6;
```

---

## Hint 10: Can you optimize the space?

Look at the recurrence:

```cpp
dp[i] = max(
    nums[i],
    dp[i - 1] + nums[i]
);
```

To calculate `dp[i]`, you only need:

```cpp
dp[i - 1]
```

You don't need the entire DP array.

Therefore, replace:

```cpp
vector<int> dp(n);
```

with one variable:

```cpp
int currentSum = nums[0];
```

Here:

```cpp
currentSum
```

represents the maximum subarray sum ending at the current index.

Update it using:

```cpp
currentSum = max(
    nums[i],
    currentSum + nums[i]
);
```

Maintain a separate global maximum:

```cpp
maxSum = max(maxSum, currentSum);
```

### Why does this work?

Every new state depends only on the previous state.

```text
dp[0] → dp[1] → dp[2] → ... → dp[n-1]
```

There is no need to retain older states.

This reduces the space complexity from:

\[
O(n)
\]

to:

\[
\boxed{O(1)}
\]

This space-optimized DP solution is commonly called **Kadane's Algorithm**.

---

## Hint 11: Analyze the complexity

Let:

```cpp
int n = nums.size();
```

### Time complexity

We process every element exactly once.

At each index, we perform:

```cpp
max(nums[i], currentSum + nums[i]);
```

and:

```cpp
max(maxSum, currentSum);
```

Each operation takes constant time.

Therefore:

\[
\boxed{\text{Time: }O(n)}
\]

### Space complexity

The standard DP solution uses:

```cpp
vector<int> dp(n);
```

Therefore:

\[
\boxed{\text{DP Space: }O(n)}
\]

Kadane's Algorithm uses only:

```cpp
int currentSum;
int maxSum;
```

Therefore:

\[
\boxed{\text{Optimized Space: }O(1)}
\]

---

# Skeleton — 1D Dynamic Programming (C++)

Try filling in the missing expressions.

```cpp
class Solution {
public:
    int maxSubArray(vector<int>& nums) {

        int n = nums.size();

        // dp[i] = Maximum subarray sum
        // ending exactly at index i.

        vector<int> dp(n);

        // Base case:
        // Only one subarray ends at index 0.

        dp[0] =  ? ;

        // Track the global maximum.

        int maxSum =  ? ;

        for (int i = 1; i < n; i++) {

            // Option 1:
            // Start a new subarray at i.

            int startNew =  ? ;

            // Option 2:
            // Extend the best subarray
            // ending at index i - 1.

            int extend =  ? ;

            // Choose the better option.

            dp[i] =  ? ;

            // Update the global maximum.

            maxSum =  ? ;
        }

        return maxSum;
    }
};
```

The most important expression to derive is:

```cpp
dp[i] =  ? ;
```

Think about:

```text
Should I start a new subarray
at the current index?

OR

Should I extend the previous
best ending subarray?
```

---

# Skeleton — Kadane's Algorithm (O(1) Space)

Once you understand the DP solution, try implementing its space-optimized version.

```cpp
class Solution {
public:
    int maxSubArray(vector<int>& nums) {

        int n = nums.size();

        // Maximum subarray sum
        // ending at the current index.

        int currentSum =  ? ;

        // Maximum subarray sum
        // found anywhere so far.

        int maxSum =  ? ;

        for (int i = 1; i < n; i++) {

            // Either start a new subarray
            // or extend the previous one.

            currentSum = max(
                 ? ,
                 ? 
            );

            // Update the global answer.

            maxSum = max(
                 ? ,
                 ? 
            );
        }

        return maxSum;
    }
};
```

The two questions to answer are:

1. **When is starting a new subarray better than extending the previous one?**
2. **Why must `maxSum` be updated independently of `currentSum`?**

Once you can answer both, you've understood Kadane's Algorithm.

---

# Core Recurrence

Define:

```cpp
dp[i]
```

as:

```text
Maximum sum of a nonempty
contiguous subarray ending at index i.
```

### Base case

\[
\boxed{dp[0]=nums[0]}
\]

### Transition

At every index, choose between:

```text
Start new:

nums[i]
```

and:

```text
Extend previous:

dp[i-1] + nums[i]
```

Therefore:

\[
\boxed{dp[i]=\max(nums[i],dp[i-1]+nums[i])}
\]

### Equivalent recurrence

\[
\boxed{dp[i]=nums[i]+\max(0,dp[i-1])}
\]

### Final answer

\[
\boxed{\max_{0\leq i<n}dp[i]}
\]

### Space optimization

Replace:

```cpp
dp[i - 1]
```

with:

```cpp
currentSum
```

to obtain:

```cpp
currentSum = max(
    nums[i],
    currentSum + nums[i]
);
```

Track the global answer separately:

```cpp
maxSum = max(maxSum, currentSum);
```

---

# Common Mistakes to Avoid

### Mistake 1: Initializing the answer to `0`

Consider:

```cpp
nums = {-3, -2, -5};
```

Every element is negative.

The maximum subarray sum is:

```cpp
-2
```

not:

```cpp
0
```

Because the subarray must contain at least one element.

Initialize using:

```cpp
nums[0]
```

rather than zero.

---

### Mistake 2: Resetting the current sum whenever the current element is negative

Consider:

```cpp
nums = {5, -2, 4};
```

The optimal subarray is:

```text
{5, -2, 4}
```

Its sum is:

```text
7
```

If you discard the subarray immediately upon encountering `-2`, you lose the optimal answer.

**A negative element does not necessarily mean you should restart.**

The decision depends on the accumulated ending sum.

---

### Mistake 3: Returning the last `currentSum`

Consider:

```cpp
nums = {5, -10, 1};
```

The final ending sum is:

```cpp
currentSum = 1;
```

But the maximum subarray sum is:

```cpp
maxSum = 5;
```

Therefore:

```cpp
return currentSum;
```

is incorrect.

You must return the global maximum.

---

### Mistake 4: Confusing subarray with subsequence

Consider:

```cpp
nums = {5, -100, 6};
```

You cannot select:

```text
{5, 6}
```

because the elements are not contiguous.

The maximum subarray sum is:

```cpp
6
```

not:

```cpp
11
```

---

# Edge Cases to Test

| Input | Expected output | Reason |
|---|---:|---|
| `{5}` | 5 | Single element |
| `{-5}` | -5 | Nonempty subarray required |
| `{-3, -2, -5}` | -2 | All elements negative |
| `{1, 2, 3}` | 6 | Entire array |
| `{5, -2, 4}` | 7 | Include a negative element |
| `{5, -10, 1}` | 5 | Best subarray ends early |
| `{-2, 1}` | 1 | Restart at second element |
| `{0, 0, 0}` | 0 | All zeros |
| `{4, -1, 2, 1}` | 6 | Entire array is optimal |

---

# Three Important Rules to Remember

### Rule 1: Define the DP state by where the subarray ends

```text
dp[i]
  |
  v
Maximum subarray sum
ending EXACTLY at i
```

### Rule 2: Start new or extend

```text
              nums[i]
                 |
                 v
       Should we start fresh
       or extend the previous
            subarray?
                 |
         ┌───────┴───────┐
         |               |
         v               v
     Start new        Extend
         |               |
         v               v
      nums[i]      dp[i-1]+nums[i]
         |               |
         └───────┬───────┘
                 |
                 v
              Take MAX
                 |
                 v
               dp[i]
```

### Rule 3: Track the overall maximum separately

```text
          currentSum
               |
               v
       Best subarray ending
         at current index
               |
               v
         Update maxSum
               |
               v
     Best subarray seen so far
```

---

# One Final Mental Trick

Whenever you process:

```cpp
nums[i]
```

ask:

> Is the maximum subarray sum ending at the previous index helping me or hurting me?

If the previous ending sum is positive, extending helps.

If it is negative, starting fresh is better.

If it is zero, either choice gives the same sum.

```text
              Previous sum
                    |
                    v
              Is it positive?
                    |
             ┌──────┴──────┐
             |             |
            YES            NO
             |             |
             v             v
         EXTEND          START NEW
             |             |
             v             v
     Add nums[i]       Use nums[i]
             |             |
             └──────┬──────┘
                    |
                    v
              Update maxSum
                    |
                    v
              Next element
```

## Final Takeaway

**LeetCode 53. Maximum Subarray is a classic example of 1D Dynamic Programming optimized into a greedy linear scan using Kadane's Algorithm.**

The central recurrence is:

\[
\boxed{dp[i]=\max(nums[i],dp[i-1]+nums[i])}
\]

The three most important ideas are:

1. **Define the state as the maximum subarray sum ending at the current index.**
2. **At every index, choose between starting a new subarray and extending the previous one.**
3. **Maintain a separate global maximum because the optimal subarray can end anywhere.**

**Time:** \(O(n)\)  
**Space:** \(O(1)\) with Kadane's Algorithm.

**The biggest conceptual skill is recognizing that a negative accumulated sum cannot help a future subarray, so you can discard it and start fresh without losing the optimal answer.**
*/
class Solution {
public:
    int maxSubArray(vector<int>& nums) {
        int n = nums.size();

        int currentSum = nums[0];

        int maxSum = nums[0];

        for(int i=1; i<n; i++){
            currentSum = std::max(nums[i], currentSum + nums[i]);

            maxSum = std::max(maxSum, currentSum);
        }

        return maxSum;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna