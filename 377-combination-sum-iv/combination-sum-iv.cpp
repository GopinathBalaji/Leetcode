// Method 1: 1D DP + Unbounded Knapsack
/*
This is a Dynamic Programming + Unbounded Knapsack (Counting Permutations) problem.

The key idea is that you're counting the number of different ordered sequences of numbers that add up to `target`.

Unlike 322. Coin Change, where you minimize the number of coins, here you count how many valid ways exist.

The most important detail is that order matters.

### Hint 1: Understand what counts as a different combination

Suppose:

```
nums = {1, 2, 3};
target = 4;
```

These are all valid:

```
1 + 1 + 1 + 1
1 + 1 + 2
1 + 2 + 1
2 + 1 + 1
2 + 2
1 + 3
3 + 1
```

So the answer is:

```
7
```

Notice:

```
1 + 3
3 + 1
```

count as two different ways.

That's the key difference between this problem and traditional combination-counting problems.

### Hint 2: Define your DP state

Let:

```
dp[x]
```

mean:

```
the number of different ordered sequences
that add up to amount x
```

Your goal is:

```
dp[target]
```

For example:

```
dp[4] = 7
```

means there are 7 different ordered sequences that sum to `4`.

### Hint 3: What is the base case?

To form a sum of:

```
0
```

there is exactly one way:

```
choose no numbers
```

So:

```
dp[0] = 1;
```

Why `1` instead of `0`?

Suppose:

```
nums = {3};
target = 3;
```

You want:

```
dp[3] = dp[0];
```

because using `3` completes one valid sequence.

If `dp[0]` were zero, you would never count that sequence.

So initialize:

```
vector<int> dp(target + 1, 0);

dp[0] = 1;
```

### Hint 4: Think about the last number you choose

Suppose you're calculating:

```
dp[x]
```

Imagine the last number in your sequence is:

```
num
```

Then everything before that last number must add up to:

```
x - num
```

For example:

```
target = 4
last number = 2
```

Then:

```
previous sum = 4 - 2 = 2
```

Every valid sequence that makes `2` can be extended by appending `2`.

So the number of new sequences is:

```
dp[x - num]
```

### Hint 5: Build the recurrence

For each amount `x`, try every number in `nums`.

If:

```
num <= x
```

then you can append `num` to every sequence that forms `x - num`.

So:

```
dp[x] += dp[x - num];
```

Why `+=` instead of `min()`?

Because this problem asks for the number of valid sequences, not the minimum number of elements.

Mental model:

```
ways to make x
=
sum of ways to make (x - num)
for every usable num
```

More formally:

\\[ dp[x] = \sum\_{\substack{num \in nums \\\ num \le x}} dp[x-num] \\]

### Hint 6: Example with `nums = {1, 2, 3}`

Let's calculate:

```
target = 4;
```

Start:

```
dp[0] = 1
```

For amount `1`:

```
use 1:
dp[1] += dp[0] = 1
```

So:

```
dp[1] = 1
```

For amount `2`:

```
use 1:
dp[2] += dp[1] = 1

use 2:
dp[2] += dp[0] = 1
```

So:

```
dp[2] = 2
```

For amount `3`:

```
use 1:
dp[3] += dp[2] = 2

use 2:
dp[3] += dp[1] = 1

use 3:
dp[3] += dp[0] = 1
```

So:

```
dp[3] = 4
```

For amount `4`:

```
use 1:
dp[4] += dp[3] = 4

use 2:
dp[4] += dp[2] = 2

use 3:
dp[4] += dp[1] = 1
```

Therefore:

```
dp[4] = 4 + 2 + 1 = 7
```

### Hint 7: The loop order is extremely important

This is probably the trickiest part of the problem.

You want:

```
for (int x = 1; x <= target; x++) {
    for (int num : nums) {
        // update dp[x]
    }
}
```

Notice:

Amounts are in the outer loop, and numbers are in the inner loop.

Why?

Because you want to count different orderings.

For example:

```
1 + 2
2 + 1
```

When calculating `dp[3]`:

```
last number = 1
→ extend sequences summing to 2

last number = 2
→ extend sequences summing to 1
```

These represent different ordered sequences.

Your DP counts both.

### Hint 8: Compare this with Coin Change II

This is an important distinction.

| Problem                 | What matters?         | Loop order                  |
| ----------------------- | --------------------- | --------------------------- |
| 377. Combination Sum IV | Order matters         | Amount outer, numbers inner |
| 518. Coin Change II     | Order does not matter | Coins outer, amount inner   |

For example:

```
1 + 2
2 + 1
```

In Combination Sum IV:

```
2 different sequences
```

In Coin Change II:

```
1 combination
```

The reason is that the order of your loops changes what your DP counts.

### Hint 9: You can reuse numbers as many times as necessary

Suppose:

```
nums = {2};
target = 6;
```

You can choose:

```
2 + 2 + 2
```

So the answer is:

```
1
```

You do not remove a number after using it.

This is why the problem is related to Unbounded Knapsack.

Each number can be used repeatedly.

### Hint 10: Why does this DP count each sequence exactly once?

Every nonempty valid sequence has exactly one last number.

For example:

```
1 + 2 + 1
```

Its last number is:

```
1
```

So it gets counted when you consider:

```
num = 1
```

while calculating:

```
dp[4]
```

The remaining prefix:

```
1 + 2
```

is already counted in:

```
dp[3]
```

Because each sequence has a unique last element and prefix, every possible ordered sequence gets counted exactly once.

### Hint 11: Complexity

Let:

```
n = nums.size()
```

For every amount from `1` to `target`, you inspect every number in `nums`.

Therefore:

```
Time:  O(n × target)
Space: O(target)
```

You only need a one-dimensional DP array.

### Skeleton

```
class Solution {
public:
    int combinationSum4(vector<int>& nums, int target) {
        int n = nums.size();

        vector<int> dp(target + 1, 0);

        // Base case:
        // one way to make sum 0

        for (int x = 1; x <= target; x++) {

            for (int num : nums) {

                if (num <= x) {

                    // Every sequence that makes
                    // x - num can be extended
                    // by appending num
                    //
                    // dp[x] += ?
                }
            }
        }

        return dp[target];
    }
};
```

The core recurrence is:

```
dp[0] = 1

For each amount x:

    For each num in nums:

        if num <= x:

            dp[x] += dp[x - num]
```

Or mentally:

```
current amount x
        ↓
try every possible last number
        ↓
look up the number of ways
to form x - num
        ↓
add those ways to dp[x]
        ↓
return dp[target]
```

The trickiest conceptual part is understanding that order matters, which is why the outer loop iterates through amounts and the inner loop iterates through numbers.

Remember these three problems:

- 322\. Coin Change: Minimum number of coins → use `min()`.
- 416\. Partition Equal Subset Sum: Can we form the target? → use boolean `OR`, iterate backward.
- 377\. Combination Sum IV: How many ordered ways can we form the target? → use `+=`, iterate amounts first.

They all use 1D dynamic programming, but the meaning of `dp[x]` determines the recurrence.
*/
class Solution {
public:
    int combinationSum4(vector<int>& nums, int target) {
        const long long CAP = (long long)INT_MAX + 1;

        vector<long long> dp(target + 1, 0); 
        dp[0] = 1;

        for(int x=1; x <= target; x++){
            for(int num: nums){
                if(num <= x){
                    dp[x] = std::min(CAP, dp[x] + dp[x - num]);
                }
            }
        }   

        return (int)dp[target];    
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna