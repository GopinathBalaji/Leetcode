// Method 1: 1D DP / 0-1 Knapsack problem
/*
This is a Dynamic Programming + 0/1 Knapsack (Subset Sum) problem.

The key idea is that instead of trying to divide the array into two groups directly, you only need to determine whether some subset of numbers adds up to half of the total sum.

Since each number can only be used once, this is a classic 0/1 Knapsack problem.

### Hint 1: Reduce the problem to a target sum

Suppose:

```
nums = {1, 5, 11, 5};
```

The total sum is:

```
1 + 5 + 11 + 5 = 22
```

To divide the array into two equal subsets, each subset must have a sum of:

```
22 / 2 = 11
```

So your problem becomes:

```
Can I choose some numbers from nums
that add up to exactly 11?
```

For example:

```
Subset 1: {11}       → sum = 11
Subset 2: {1, 5, 5}  → sum = 11
```

Therefore, the answer is `true`.

### Hint 2: Immediately reject odd total sums

If the total sum is odd, it's impossible to divide it into two equal integer sums.

For example:

```
nums = {1, 2, 4};
```

Total:

```
1 + 2 + 4 = 7
```

You cannot split `7` into two equal integer sums.

So first compute:

```
int total = accumulate(nums.begin(), nums.end(), 0);
```

Then:

```
if (total % 2 != 0) {
    return false;
}
```

Otherwise, your target is:

```
int target = total / 2;
```

### Hint 3: Define your DP state

Let:

```
dp[s]
```

mean:

```
whether it is possible to form sum s
using the numbers processed so far
```

For example:

```
dp[5] = true
```

means:

```
some subset of the numbers processed
can add up to 5
```

You want:

```
dp[target]
```

### Hint 4: What is the base case?

You can always form a sum of:

```
0
```

by choosing no numbers.

So:

```
dp[0] = true;
```

Initially, every other sum is impossible:

```
vector<bool> dp(target + 1, false);

dp[0] = true;
```

Mental model:

```
sum 0 → possible
all other sums → not possible yet
```

### Hint 5: At each number, you have two choices

Suppose you're processing:

```
int num = nums[i];
```

For any target sum `s`, you can either:

Option 1: Skip the current number

If you could already form `s` without this number, you can still form it.

```
dp[s]
```

Option 2: Take the current number

If you could previously form:

```
s - num
```

then adding `num` lets you form:

```
s
```

So that possibility comes from:

```
dp[s - num]
```

### Hint 6: Build the recurrence

Combine the two choices using logical OR:

```
dp[s] = dp[s] || dp[s - num];
```

Why OR?

Because you only need one valid subset.

Mental model:

```
Can I make sum s?

YES, if:

I could already make s

OR

I could make (s - num)
and now include num
```

Only perform this transition when:

```
s >= num
```

because you cannot subtract a number larger than the desired sum.

### Hint 7: The most important detail — iterate backward

This is the trickiest part of the problem.

You might be tempted to write:

```
for (int s = num; s <= target; s++) {
    dp[s] = dp[s] || dp[s - num];
}
```

But this is wrong for 0/1 Knapsack.

Why?

Because moving forward allows the same number to be used multiple times during one iteration.

Consider:

```
nums = {2};
target = 4;
```

Initially:

```
dp[0] = true
```

When processing `2`:

```
dp[2] = dp[2] || dp[0]
      = true
```

Then, if you continue forward:

```
dp[4] = dp[4] || dp[2]
      = true
```

But you've effectively used the number `2` twice!

That's invalid because each array element can only be used once.

So instead, iterate from right to left:

```
for (int s = target; s >= num; s--) {
    dp[s] = dp[s] || dp[s - num];
}
```

This ensures the current number is used at most once.

### Hint 8: Walk through an example

Suppose:

```
nums = {1, 5, 11, 5};
```

Your target is:

```
target = 11;
```

Initially:

```
Reachable sums:
{0}
```

Process number `1`:

```
Reachable sums:
{0, 1}
```

Process number `5`:

```
Reachable sums:
{0, 1, 5, 6}
```

Why `6`?

Because:

```
1 + 5 = 6
```

Process number `11`:

```
Reachable sums:
{0, 1, 5, 6, 11}
```

Now:

```
dp[11] == true
```

So you already know there's a subset whose sum is `11`.

You don't even need to process the final `5` to know the answer.

### Hint 9: You don't need to track the actual subsets

The problem only asks:

```
Is an equal partition possible?
```

It doesn't ask you to return the elements in each subset.

Therefore, you don't need to store:

```
vector<vector<int>> subsets;
```

or track exactly which numbers formed each sum.

You only need:

```
vector<bool> dp(target + 1, false);
```

This is why a boolean DP is sufficient.

### Hint 10: Why is this called 0/1 Knapsack?

In knapsack problems, you typically decide whether to:

```
take an item
or
skip an item
```

Here, each number is an item.

You can use each number:

```
0 times → skip
1 time  → take
```

But not more than once.

That's why it is called 0/1 Knapsack.

Compare the loop directions:

| Problem type                      | Inner loop direction |
| --------------------------------- | -------------------- |
| 0/1 Knapsack (use each item once) | Backward             |
| Unbounded Knapsack (reuse items)  | Forward              |

For this problem:

```
for (int s = target; s >= num; s--)
```

is the correct approach.

This is different from 322. Coin Change, where you can use the same coin denomination multiple times.

### Hint 11: Can you return early?

After processing a number, if:

```
dp[target] == true
```

then you already found a valid subset.

Since the total sum is exactly:

```
2 * target
```

the remaining elements automatically sum to the other half.

So you can immediately return:

```
true;
```

This is an optional optimization.

### Hint 12: Complexity

Let:

```
n = nums.size()
target = total / 2
```

You process every number and potentially every sum up to `target`.

So:

```
Time:  O(n × target)
Space: O(target)
```

This is much better than enumerating all possible subsets, which could take exponential time.

### Skeleton

```
class Solution {
public:
    bool canPartition(vector<int>& nums) {
        int n = nums.size();

        int total = accumulate(
            nums.begin(),
            nums.end(),
            0
        );

        // If total is odd:
        // impossible to split equally

        int target = total / 2;

        vector<bool> dp(target + 1, false);

        // Base case:
        // sum 0 is always possible

        for (int num : nums) {

            // Iterate BACKWARD
            // from target down to num

            for (int s = target; s >= num; s--) {

                // Option 1: skip num
                // Option 2: include num
                //
                // dp[s] = ?
            }
        }

        // Return whether target is reachable

        return dp[target];
    }
};
```

The core recurrence is:

```
dp[s] = dp[s] || dp[s - num]
```

Or mentally:

```
calculate total sum
        ↓
if odd → false
        ↓
target = total / 2
        ↓
initialize dp[0] = true
        ↓
for every number:
    update reachable sums BACKWARD
        ↓
is dp[target] true?
```

The trickiest implementation detail is iterating backward through the DP array. This is what prevents you from accidentally using the same element multiple times.

The most important conceptual connection is that Partition Equal Subset Sum is really a 0/1 Knapsack decision problem, rather than a problem about explicitly constructing two subsets.
*/
class Solution {
public:
    bool canPartition(vector<int>& nums) {
        int total = std::accumulate(nums.begin(), nums.end(), 0);

        if(total % 2 != 0){
            return false;
        }

        int target = total / 2;
        vector<bool> dp(target + 1, false);

        dp[0] = true;

        for(int num : nums){
            for(int s = target; s >= num; s--){
                dp[s] = dp[s] || dp[s - num];
            }
        }
        
        return dp[target];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna