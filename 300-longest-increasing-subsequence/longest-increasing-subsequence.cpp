// Method 1: 1D DP approach
/*
This is a Dynamic Programming problem, with an optional Binary Search + Greedy optimization.

The key idea is to find the longest subsequence where every number is strictly greater than the previous number.

Unlike a substring, a subsequence does not need to contain consecutive elements.

We'll start with the O(n²) DP solution, then look at how to optimize it to O(n log n).

### Hint 1: Understand what a subsequence means

Suppose:

```
nums = {10, 9, 2, 5, 3, 7, 101, 18};
```

One valid increasing subsequence is:

```
2 → 3 → 7 → 101
```

Its length is:

```
4
```

Notice that you can skip elements, but you cannot change their original order.

Also, the numbers must be strictly increasing:

```
nums[j] < nums[i]
```

Not:

```
nums[j] <= nums[i]
```

### Hint 2: Define your DP state

Let:

```
dp[i]
```

mean:

```
the length of the longest increasing
subsequence that ENDS at index i
```

That last part is important.

You're not calculating the longest subsequence anywhere in the first `i` elements.

You're calculating the longest subsequence that must include `nums[i]` as its last element.

### Hint 3: Every element can form a subsequence of length 1

For example:

```
nums = {5, 3, 8};
```

Even without combining numbers, each element forms a valid subsequence:

```
{5}
{3}
{8}
```

So initialize:

```
vector<int> dp(n, 1);
```

Every `dp[i]` starts at `1`.

### Hint 4: Look at all previous elements

Suppose you're calculating:

```
dp[i]
```

To extend an increasing subsequence ending at index `j`, you need:

```
j < i
```

and:

```
nums[j] < nums[i]
```

Why?

Because the previous number must be smaller than the current one.

So for every index `i`, inspect all earlier indices:

```
for (int j = 0; j < i; j++) {
    if (nums[j] < nums[i]) {
        // Can extend the subsequence ending at j
    }
}
```

### Hint 5: Build the recurrence

If:

```
nums[j] < nums[i]
```

then you can append `nums[i]` to the increasing subsequence ending at `j`.

That gives a new subsequence of length:

```
dp[j] + 1
```

So update:

```
dp[i] = max(
    dp[i],
    dp[j] + 1
);
```

Mental model:

```
best subsequence ending at i
=
1 + best compatible subsequence
    ending before i
```

More formally:

\\[ dp[i] = \max\_{\substack{0 \le j < i \\\ nums[j] < nums[i]}}(dp[j]+1) \\]

If no previous element is smaller, then `dp[i]` stays `1`.

### Hint 6: Example

Suppose:

```
nums = {3, 1, 2, 4};
```

Initially:

```
nums:  3  1  2  4
dp:    1  1  1  1
```

DP walkthrough

Step 4 of 4

i = 0

## 3

dp[i]

### 1

i = 1

## 1

dp[i]

### 1

i = 2

## 2

dp[i]

### 2

i = 3

## 4

dp[i]

### 3

Process 4

Extend the subsequence {1, 2} with 4. dp[3] = 3.

&#x20;PreviousNext&#x20;

At the end:

```
dp = {1, 1, 2, 3};
```

So the longest increasing subsequence has length `3`:

```
1 → 2 → 4
```

### Hint 7: The answer is not necessarily `dp[n - 1]`

This is an important detail.

Remember:

```
dp[i]
```

means the longest increasing subsequence ending at index `i`.

But the longest subsequence might end somewhere earlier.

For example:

```
nums = {1, 2, 3, 0};
```

The best subsequence is:

```
1 → 2 → 3
```

It does not include the last element.

So you need:

```
int ans = 1;
```

And after computing each `dp[i]`:

```
ans = max(ans, dp[i]);
```

Alternatively, return the maximum element in the finished DP array.

### Hint 8: The loop structure

You need two loops.

The outer loop determines which index you're currently processing:

```
for (int i = 0; i < n; i++) {
```

The inner loop checks all earlier elements:

```
for (int j = 0; j < i; j++) {
```

Inside, ask:

```
Can nums[i] extend a subsequence ending at j?
```

If yes, update `dp[i]`.

### Hint 9: Why greedy doesn't work directly

Suppose:

```
nums = {1, 10, 2, 3, 4};
```

If you greedily choose the next greater number, you might take:

```
1 → 10
```

giving length `2`.

But the optimal subsequence is:

```
1 → 2 → 3 → 4
```

giving length `4`.

The problem is that choosing a large number too early may prevent you from extending the subsequence later.

DP avoids this issue by considering all valid previous endpoints.

### Hint 10: Complexity

You have:

```
for (int i = 0; i < n; i++) {
    for (int j = 0; j < i; j++) {
        // transition
    }
}
```

So the time complexity is:

```
O(n²)
```

You also store:

```
vector<int> dp(n);
```

So the space complexity is:

```
O(n)
```

This is sufficient for the problem's constraints.

### Hint 11: Bonus — Can you optimize to O(n log n)?

There's also a more advanced approach using Binary Search + Greedy.

Instead of a normal DP array, maintain:

```
vector<int> tails;
```

where:

```
tails[len - 1]
=
smallest possible ending value
of an increasing subsequence of length len
```

For each number `x`:

- If `x` is greater than every value in `tails`, append it.
- Otherwise, find the first element in `tails` that is greater than or equal to `x`, and replace it with `x`.

You can find that position using:

```
lower_bound(tails.begin(), tails.end(), x);
```

The length of `tails` at the end is the answer.

One subtlety: `tails` itself does not necessarily represent an actual subsequence. It stores the best possible ending values for different subsequence lengths.

This achieves:

```
Time:  O(n log n)
Space: O(n)
```

I recommend understanding the O(n²) DP solution first, because the state transition is useful in many other DP problems.

### Skeleton — O(n²) DP

```
class Solution {
public:
    int lengthOfLIS(vector<int>& nums) {
        int n = nums.size();

        vector<int> dp(n, 1);

        int ans = 1;

        for (int i = 0; i < n; i++) {

            for (int j = 0; j < i; j++) {

                // If nums[j] < nums[i]:
                //
                // We can extend the subsequence
                // ending at j with nums[i]
                //
                // Update dp[i]
            }

            // Update global answer
        }

        return ans;
    }
};
```

The core recurrence is:

```
dp[i] = 1 initially

For every j < i:

    if nums[j] < nums[i]:

        dp[i] = max(
            dp[i],
            dp[j] + 1
        )

answer = max(dp[i]) over all i
```

The trickiest conceptual part is understanding that `dp[i]` represents the longest increasing subsequence ending exactly at index `i`, rather than the longest increasing subsequence found anywhere so far.
*/
class Solution {
public:
    int lengthOfLIS(vector<int>& nums) {
        int n = nums.size();

        vector<int> dp(n, 1);

        int ans = 1;

        for(int i=0; i<n; i++){
            for(int j=0; j<i; j++){
                if(nums[j] < nums[i]){
                    dp[i] = std::max(dp[i], dp[j] + 1);
                }
            }

            ans = std::max(ans, dp[i]);
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna