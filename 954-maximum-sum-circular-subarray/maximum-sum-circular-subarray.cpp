// Method 1: Minimum Kadane's Algorithm
/*
# LeetCode 918. Maximum Sum Circular Subarray — Hints (C++)

This is a **Dynamic Programming + Greedy + Kadane's Algorithm + Circular Array** problem.

The key idea is that instead of checking every possible circular subarray, you can divide the problem into **two cases: the maximum subarray does not wrap around the array, or it wraps from the end to the beginning**.

This is closely related to:

- **53. Maximum Subarray:** Find the maximum sum of a contiguous subarray using Kadane's Algorithm.
- **152. Maximum Product Subarray:** Track both maximum and minimum values because negative numbers affect optimal decisions.
- **121. Best Time to Buy and Sell Stock:** Maintain the best result while scanning an array.
- **213. House Robber II:** Handle circular constraints by considering separate cases.
- **198. House Robber:** Use 1D DP with constant-space optimization.

The major difference from LeetCode 53 is that **the array is circular, so a valid subarray may contain elements from both the end and the beginning of the array**.

We'll derive the two cases, understand why finding the minimum subarray helps maximize the circular sum, and handle the important all-negative edge case.

---

## Hint 1: Understand what a circular subarray means

You're given:

```cpp
vector<int>& nums;
```

Your goal is to find the maximum sum of a **nonempty contiguous subarray**.

However, the array is circular.

This means the last element is considered adjacent to the first element.

For example:

```cpp
nums = {5, -3, 5};
```

In a normal array, the maximum subarray is:

```text
{5, -3, 5}
```

Its sum is:

```text
5 - 3 + 5 = 7
```

But because the array is circular, we can select:

```text
Last element:  5

First element: 5
```

These elements form a valid circular subarray.

Their sum is:

```text
5 + 5 = 10
```

Therefore:

```cpp
answer = 10;
```

### Important observation

A circular subarray can either:

1. Stay entirely within the normal array.
2. Wrap around from the end of the array to the beginning.

These are the only two possibilities.

Also, the subarray must not use any index more than once.

---

## Hint 2: Case 1 — The maximum subarray does NOT wrap around

Consider:

```cpp
nums = {-2, 3, -1, 4, -5};
```

The maximum subarray is:

```text
{3, -1, 4}
```

Its sum is:

```text
3 - 1 + 4 = 6
```

This subarray does not wrap around the array.

Therefore, we can solve this case exactly like **LeetCode 53. Maximum Subarray**.

Recall Kadane's Algorithm.

Define:

```cpp
maxEnding[i]
```

as:

```text
Maximum sum of a nonempty
subarray ending exactly at index i.
```

At every index, we have two options.

### Option 1: Start a new subarray

```cpp
nums[i]
```

### Option 2: Extend the previous subarray

```cpp
maxEnding[i - 1] + nums[i]
```

Choose the better option.

### Recurrence

\[
\boxed{maxEnding[i]=\max(nums[i],maxEnding[i-1]+nums[i])}
\]

Track the global maximum:

```cpp
maxSum = max(maxSum, maxEnding[i]);
```

This gives the maximum sum among all non-wrapping subarrays.

**Important:** This case is identical to LeetCode 53.

---

## Hint 3: Case 2 — The maximum subarray WRAPS around

Now consider:

```cpp
nums = {8, -1, -3, 8};
```

The total array sum is:

```text
8 + (-1) + (-3) + 8 = 12
```

The best non-wrapping subarray is:

```text
{8, -1, -3, 8}
```

with sum:

```text
12
```

But the circular subarray can select:

```text
Last element:  8

First element: 8
```

Its sum is:

```text
8 + 8 = 16
```

Therefore:

```cpp
answer = 16;
```

### How can we calculate this without explicitly wrapping around?

Notice that the optimal circular subarray selects:

```text
8 ... 8
```

while excluding the middle portion:

```text
{-1, -3}
```

The excluded portion has sum:

```text
-1 + (-3) = -4
```

Now consider:

```text
Total array sum - Excluded middle sum
```

That gives:

```text
12 - (-4) = 16
```

Exactly the circular maximum.

### Key observation

**A wrapping subarray can be viewed as the entire array minus one contiguous middle subarray.**

Therefore, instead of directly finding the maximum wrapping subarray, we can find the minimum-sum subarray to exclude.

---

## Hint 4: Why do we need the MINIMUM subarray?

Suppose:

```cpp
nums = {5, -3, 5};
```

The total sum is:

```text
5 - 3 + 5 = 7
```

The best circular subarray includes:

```text
5 + 5 = 10
```

What did we exclude?

```text
{-3}
```

This is the minimum-sum subarray.

Its sum is:

```text
-3
```

Therefore:

```text
Maximum circular sum
    = Total sum - Minimum subarray sum
```

Substitute:

```text
7 - (-3) = 10
```

### Mathematical reasoning

Let:

\[
S=\text{Total array sum}
\]

Let:

\[
m=\text{Minimum contiguous subarray sum}
\]

Then the best wrapping sum is:

\[
\boxed{S-m}
\]

Why does this work?

Because subtracting the minimum-sum middle portion leaves the largest possible sum formed by the remaining suffix and prefix.

### Visual representation

```text
Array:

[ 5 | -3 | 5 ]
  ↑    ↑   ↑
  |    |   |
Keep  Skip Keep
```

```text
Total sum = 7

Minimum subarray sum = -3

Wrapping sum = 7 - (-3)

Wrapping sum = 10
```

**This is the most important insight in LeetCode 918.**

---

## Hint 5: How do we find the minimum subarray sum?

Recall Kadane's Algorithm for the maximum subarray.

We use:

```cpp
maxEnding = max(
    nums[i],
    maxEnding + nums[i]
);
```

Now ask:

> What if we want the minimum sum of a subarray ending at the current index?

The same two choices exist.

### Option 1: Start a new subarray

```cpp
nums[i]
```

### Option 2: Extend the previous minimum subarray

```cpp
minEnding + nums[i]
```

However, instead of taking the maximum, take the minimum.

### Minimum Kadane recurrence

\[
\boxed{minEnding[i]=\min(nums[i],minEnding[i-1]+nums[i])}
\]

In C++:

```cpp
minEnding = min(
    nums[i],
    minEnding + nums[i]
);
```

Track the global minimum:

```cpp
minSum = min(minSum, minEnding);
```

### Compare the two recurrences

| Goal | Recurrence |
|---|---|
| Maximum subarray ending at `i` | `max(nums[i], maxEnding + nums[i])` |
| Minimum subarray ending at `i` | `min(nums[i], minEnding + nums[i])` |

The structure is identical.

Only the optimization direction changes.

---

## Hint 6: Combine the two cases

We now have two possible answers.

### Case 1: Non-wrapping subarray

Use normal Kadane's Algorithm:

```cpp
maxSum
```

### Case 2: Wrapping subarray

Use:

```cpp
totalSum - minSum
```

Since either case might be optimal, take the maximum.

### Core formula

\[
\boxed{\text{Answer}=\max(maxSum,totalSum-minSum)}
\]

In C++:

```cpp
return max(
    maxSum,
    totalSum - minSum
);
```

However, there is one important edge case where this formula is invalid.

**What happens when every element is negative?**

---

## Hint 7: The all-negative edge case

Consider:

```cpp
nums = {-3, -2, -5};
```

The maximum subarray sum is:

```text
-2
```

because the subarray must contain at least one element.

Let's calculate both cases.

### Case 1: Maximum Kadane

```text
maxSum = -2
```

This is correct.

### Case 2: Total sum minus minimum subarray sum

The total sum is:

```text
-3 + (-2) + (-5) = -10
```

The minimum-sum subarray is the entire array:

```text
{-3, -2, -5}
```

Therefore:

```text
minSum = -10
```

Now calculate:

```text
totalSum - minSum
```

We get:

```text
-10 - (-10) = 0
```

But:

```cpp
answer = 0;
```

would be incorrect.

Why?

Because subtracting the entire array leaves an **empty subarray**.

The problem requires a nonempty subarray.

### Important observation

When all elements are negative:

```text
Maximum Kadane returns
the largest individual element.
```

And:

```text
Minimum Kadane selects
the entire array.
```

Therefore:

```cpp
totalSum - minSum
```

becomes zero, representing an invalid empty selection.

### How can we detect this?

If:

```cpp
maxSum < 0
```

then every element is negative.

Why?

Because Kadane's Algorithm can always select a single element.

If even the maximum subarray sum is negative, no element can be zero or positive.

Therefore:

```cpp
if (maxSum < 0) {

    return maxSum;
}
```

This handles the all-negative case.

### Final formula

\[
\boxed{
\text{Answer}=
\begin{cases}
maxSum, & maxSum<0\\
\max(maxSum,totalSum-minSum), & \text{otherwise}
\end{cases}
}
\]

This is the complete solution logic.

---

## Hint 8: Walk through a complete example

Consider:

```cpp
nums = {5, -3, 5};
```

Initially:

```cpp
totalSum = 0;

maxEnding = nums[0];
minEnding = nums[0];

maxSum = nums[0];
minSum = nums[0];
```

Now process each element.

### Maximum Kadane

```cpp
maxEnding = max(
    nums[i],
    maxEnding + nums[i]
);
```

### Minimum Kadane

```cpp
minEnding = min(
    nums[i],
    minEnding + nums[i]
);
```

### Simulation table

| Index | `nums[i]` | `maxEnding` | `maxSum` | `minEnding` | `minSum` | Running total |
|---|---:|---:|---:|---:|---:|---:|
| 0 | 5 | 5 | 5 | 5 | 5 | 5 |
| 1 | -3 | 2 | 5 | -3 | -3 | 2 |
| 2 | 5 | 7 | 7 | 2 | -3 | 7 |

After processing all elements:

```cpp
totalSum = 7;

maxSum = 7;

minSum = -3;
```

### Non-wrapping case

```text
maxSum = 7
```

### Wrapping case

```text
totalSum - minSum

= 7 - (-3)

= 10
```

Compare:

```cpp
max(7, 10)
```

Therefore:

```cpp
answer = 10;
```

The optimal circular subarray contains:

```text
Last element: 5

First element: 5
```

---

## Hint 9: Why does the circular formula work?

Suppose the array is divided into three contiguous sections:

```text
[ PREFIX | MIDDLE | SUFFIX ]
```

A wrapping subarray contains:

```text
SUFFIX + PREFIX
```

It excludes:

```text
MIDDLE
```

Therefore:

\[
\text{Wrapping Sum}
=
\text{Total Sum}-\text{Middle Sum}
\]

To maximize the wrapping sum, we want to minimize the excluded middle sum.

Hence:

\[
\boxed{
\text{Maximum Wrapping Sum}
=
\text{Total Sum}-\text{Minimum Subarray Sum}
}
\]

There is one subtle detail.

For a valid wrapping subarray, the excluded middle must not be the entire array.

Otherwise, the selected subarray would be empty.

The all-negative check prevents this invalid case from affecting the answer.

When at least one element is nonnegative, the standard formula remains safe for computing the final maximum: if the minimum subarray is the whole array, the wrapping candidate is zero, and the non-wrapping maximum is at least zero.

### Mental model

```text
           Maximum Circular Subarray
                       |
                       v
              Two possibilities
                       |
             ┌─────────┴─────────┐
             |                   |
             v                   v
        NO WRAPPING           WRAPPING
             |                   |
             v                   v
       Normal Kadane      Total array sum
             |                   |
             v                   v
           maxSum          Remove minimum
                           middle subarray
                                 |
                                 v
                          totalSum - minSum
                                 |
                ┌────────────────┘
                |
                v
        Handle all-negative case
                |
                v
       Otherwise take the MAX
```

---

## Hint 10: Can we solve everything in one traversal?

Yes.

Both Kadane recurrences depend only on the previous ending sums.

We need four running variables:

```cpp
int maxEnding;
int maxSum;

int minEnding;
int minSum;
```

We also need:

```cpp
int totalSum;
```

At each index:

1. Update the maximum subarray ending sum.
2. Update the minimum subarray ending sum.
3. Update the global maximum.
4. Update the global minimum.
5. Add the current element to the total sum.

All these operations take constant time.

### Why don't we need DP arrays?

The recurrences are:

\[
maxEnding[i]=\max(nums[i],maxEnding[i-1]+nums[i])
\]

\[
minEnding[i]=\min(nums[i],minEnding[i-1]+nums[i])
\]

Each state depends only on the previous state.

Therefore, we can replace the DP arrays with variables.

This gives:

\[
\boxed{\text{Time: }O(n)}
\]

\[
\boxed{\text{Space: }O(1)}
\]

---

# Skeleton — Kadane's Algorithm (C++)

Try filling in the missing expressions yourself.

```cpp
class Solution {
public:
    int maxSubarraySumCircular(vector<int>& nums) {

        int n = nums.size();

        // Total sum of the array.

        int totalSum = 0;

        // Maximum subarray sum
        // ending at the current index.

        int maxEnding =  ? ;

        // Minimum subarray sum
        // ending at the current index.

        int minEnding =  ? ;

        // Global maximum subarray sum.

        int maxSum =  ? ;

        // Global minimum subarray sum.

        int minSum =  ? ;

        // Process every element.

        for (int i = 0; i < n; i++) {

            // Update total sum.

            totalSum +=  ? ;

            // At index 0, the ending sums
            // are already initialized.

            if (i == 0) {
                continue;
            }

            // Maximum Kadane:
            // Start new or extend previous.

            maxEnding = max(
                 ? ,
                 ? 
            );

            // Minimum Kadane:
            // Start new or extend previous.

            minEnding = min(
                 ? ,
                 ? 
            );

            // Update global maximum.

            maxSum = ? ;

            // Update global minimum.

            minSum =  ? ;
        }

        // Edge case:
        // Every element is negative.

        if ( ? ) {

            return  ? ;
        }

        // Case 1: Non-wrapping maximum.
        // Case 2: Wrapping maximum.

        int nonWrap =  ? ;

        int wrap =  ? ;

        return max(nonWrap, wrap);
    }
};
```

The most important expressions to derive are:

```cpp
maxEnding = max(
    nums[i],
    maxEnding + nums[i]
);
```

and:

```cpp
minEnding = min(
    nums[i],
    minEnding + nums[i]
);
```

Then understand why:

```cpp
totalSum - minSum
```

represents the best wrapping candidate.

---

# Core Recurrence

Define:

```cpp
maxEnding[i]
```

as:

```text
Maximum sum of a nonempty
subarray ending at index i.
```

Define:

```cpp
minEnding[i]
```

as:

```text
Minimum sum of a nonempty
subarray ending at index i.
```

## Base cases

\[
\boxed{maxEnding[0]=nums[0]}
\]

\[
\boxed{minEnding[0]=nums[0]}
\]

## Maximum Kadane

\[
\boxed{
maxEnding[i]=\max(nums[i],maxEnding[i-1]+nums[i])
}
\]

## Minimum Kadane

\[
\boxed{
minEnding[i]=\min(nums[i],minEnding[i-1]+nums[i])
}
\]

## Global maximum and minimum

\[
\boxed{
maxSum=\max_{0\leq i<n}maxEnding[i]
}
\]

\[
\boxed{
minSum=\min_{0\leq i<n}minEnding[i]
}
\]

## Total sum

\[
\boxed{
totalSum=\sum_{i=0}^{n-1}nums[i]
}
\]

## Final answer

\[
\boxed{
\text{Answer}=
\begin{cases}
maxSum, & maxSum<0\\
\max(maxSum,totalSum-minSum), & maxSum\geq0
\end{cases}
}
\]

---

# Connection to LeetCode 53. Maximum Subarray

The strongest connection is with **LeetCode 53**.

In LeetCode 53, you find:

```cpp
maxSum
```

using Kadane's Algorithm.

In LeetCode 918, you still use Kadane's Algorithm, but you additionally find:

```cpp
minSum
```

and:

```cpp
totalSum
```

### Comparison

| Feature | 53. Maximum Subarray | 918. Maximum Sum Circular Subarray |
|---|---|---|
| Array type | Normal | Circular |
| Can subarray wrap around? | No | Yes |
| Maximum Kadane | Yes | Yes |
| Minimum Kadane | No | Yes |
| Total sum needed | No | Yes |
| Main formula | `maxSum` | `max(maxSum, totalSum - minSum)` |
| Important edge case | All-negative array | All-negative array and invalid empty wrap |
| Time complexity | O(n) | O(n) |
| Space complexity | O(1) | O(1) |

The main conceptual jump is:

**Instead of directly calculating the wrapping maximum, calculate the minimum contiguous portion to remove.**

---

# Common Mistakes to Avoid

### Mistake 1: Using only normal Kadane's Algorithm

Consider:

```cpp
nums = {5, -3, 5};
```

Normal Kadane returns:

```cpp
7
```

But the circular maximum is:

```cpp
10
```

You must consider the wrapping case.

---

### Mistake 2: Forgetting the all-negative edge case

Consider:

```cpp
nums = {-3, -2, -5};
```

The expression:

```cpp
totalSum - minSum
```

produces:

```cpp
0
```

because the minimum subarray is the entire array.

But an empty subarray is invalid.

The correct answer is:

```cpp
-2
```

Therefore, handle:

```cpp
if (maxSum < 0)
```

before using the circular formula.

---

### Mistake 3: Using maximum Kadane to calculate the wrapping sum

This is incorrect:

```cpp
totalSum - maxSum
```

Why?

Because you want to remove the **minimum-sum portion**, not the maximum-sum portion.

The correct expression is:

```cpp
totalSum - minSum
```

---

### Mistake 4: Assuming the wrapping case is always better

Consider:

```cpp
nums = {-2, 3, -1, 4, -5};
```

The normal maximum subarray is:

```text
{3, -1, 4}
```

with sum:

```cpp
6
```

The minimum subarray sum is:

```cpp
-5
```

The total sum is:

```cpp
-1
```

Therefore:

```text
Wrapping candidate:

-1 - (-5) = 4
```

But:

```cpp
max(6, 4) = 6;
```

The non-wrapping case is better.

Always compare both candidates.

---

### Mistake 5: Initializing Kadane's variables to zero

Consider:

```cpp
nums = {-5};
```

If you initialize:

```cpp
maxSum = 0;
```

you may incorrectly allow an empty subarray.

Instead, initialize the Kadane variables using:

```cpp
nums[0]
```

because the selected subarray must be nonempty.

---

# Edge Cases to Test

| Input | Expected output | Reason |
|---|---:|---|
| `{5}` | 5 | Single element |
| `{-5}` | -5 | Nonempty subarray required |
| `{-3, -2, -5}` | -2 | All negative |
| `{5, -3, 5}` | 10 | Wrapping is optimal |
| `{1, -2, 3, -2}` | 3 | Non-wrapping is optimal |
| `{3, -1, 2, -1}` | 4 | Wrapping is optimal |
| `{8, -1, -3, 8}` | 16 | Exclude negative middle |
| `{1, 2, 3}` | 6 | Entire array |
| `{0, 0, 0}` | 0 | All zeros |
| `{5, -10, 5}` | 10 | Circular subarray uses both ends |

---

# Three Important Rules to Remember

### Rule 1: Find the maximum non-wrapping subarray

Use normal Kadane:

```cpp
maxEnding = max(
    nums[i],
    maxEnding + nums[i]
);
```

Track:

```cpp
maxSum = max(maxSum, maxEnding);
```

### Rule 2: Find the minimum subarray to exclude

Use minimum Kadane:

```cpp
minEnding = min(
    nums[i],
    minEnding + nums[i]
);
```

Track:

```cpp
minSum = min(minSum, minEnding);
```

Then calculate:

```cpp
wrap = totalSum - minSum;
```

### Rule 3: Handle the invalid empty-subarray case

If:

```cpp
maxSum < 0
```

return:

```cpp
maxSum
```

Otherwise:

```cpp
return max(
    maxSum,
    totalSum - minSum
);
```

---

# One Final Mental Trick

Whenever you see a circular maximum subarray problem, ask:

> Can I turn the wrapping case into the total sum minus something I want to exclude?

For this problem, the answer is yes.

```text
                  Circular Array
                        |
                        v
              Maximum Subarray Sum
                        |
              ┌─────────┴─────────┐
              |                   |
              v                   v
         NON-WRAPPING          WRAPPING
              |                   |
              v                   v
         Maximum Kadane      Total Sum
              |                   |
              v                   v
            maxSum          Subtract Minimum
                            Subarray Sum
                                  |
                                  v
                           totalSum - minSum
                                  |
                    ┌─────────────┘
                    |
                    v
            Is maxSum < 0?
                    |
              ┌─────┴─────┐
              |           |
             YES          NO
              |           |
              v           v
         Return maxSum   Return MAX
                        of both cases
```

---

## Final Takeaway

**LeetCode 918. Maximum Sum Circular Subarray is an extension of Kadane's Algorithm that handles circular subarrays by combining maximum and minimum subarray sums.**

The crucial observations are:

1. **Non-wrapping subarrays:** Use standard maximum Kadane.
2. **Wrapping subarrays:** Subtract the minimum contiguous subarray sum from the total array sum.
3. **All-negative arrays:** Return the normal maximum because the wrapping formula would select an invalid empty subarray.
4. **Space optimization:** Both Kadane recurrences require only the previous state.

The essential formula is:

\[
\boxed{
\text{Answer}=
\begin{cases}
maxSum, & maxSum<0\\
\max(maxSum,totalSum-minSum), & \text{otherwise}
\end{cases}
}
\]

**Time:** \(O(n)\)  
**Space:** \(O(1)\)

**The most important conceptual skill is recognizing that maximizing a wrapping subarray is equivalent to minimizing the contiguous portion excluded from the array.**
*/ 
class Solution {
public:
    int maxSubarraySumCircular(vector<int>& nums) {
        int n = nums.size();

        int totalSum = nums[0];

        int maxEnding = nums[0];

        int minEnding = nums[0];

        int maxSum = nums[0];

        int minSum = nums[0];

        for(int i=1; i<n; i++){
            totalSum += nums[i];

            if(i == 0){
                continue;
            }

            maxEnding = std::max(nums[i], maxEnding + nums[i]);

            minEnding = std::min(nums[i], minEnding + nums[i]);

            maxSum = std::max(maxSum, maxEnding);

            minSum = std::min(minSum, minEnding);
        }

        return maxSum < 0 ? maxSum : std::max(maxSum, totalSum - minSum);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna