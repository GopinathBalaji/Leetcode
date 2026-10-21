// Method 1: DP + 0/1 Knapsack (Subset Sum Counting) problem
/*
# LeetCode 494. Target Sum — Hints

This is a Dynamic Programming + 0/1 Knapsack (Subset Sum Counting) problem.

The key idea is that instead of trying every possible combination of `+` and `-` signs, you can transform the problem into counting the number of subsets that add up to a particular sum.

This is closely related to:

- 416\. Partition Equal Subset Sum: Determine whether a subset with a particular sum exists.
- 1049\. Last Stone Weight II: Find a subset sum as close as possible to half the total.
- 518\. Coin Change II: Count the number of combinations that produce a target amount.

The major difference is that each number can only be used once, and you're counting the number of valid assignments rather than checking whether one exists.

### Hint 1: Understand what you're counting

Suppose:

```
nums = {1, 1, 1, 1, 1};
target = 3;
```

You need to assign either `+` or `-` to each number so that the resulting expression equals `3`.

Here are the valid assignments:

```
-1 + 1 + 1 + 1 + 1 = 3

+1 - 1 + 1 + 1 + 1 = 3

+1 + 1 - 1 + 1 + 1 = 3

+1 + 1 + 1 - 1 + 1 = 3

+1 + 1 + 1 + 1 - 1 = 3
```

There are `5` valid ways.

So:

```
answer = 5;
```

Notice that the numbers are identical, but choosing a different position for the negative sign counts as a different assignment.

Your goal is to count all valid sign assignments.

### Hint 2: Think about dividing numbers into two groups

Instead of thinking about adding `+` and `-` signs to individual numbers, imagine dividing all the numbers into two groups:

- Group `P`: Numbers assigned a positive sign.
- Group `N`: Numbers assigned a negative sign.

Suppose:

```
nums = {1, 1, 1, 1, 1};
target = 3;
```

One possible partition is:

```
Positive group: {1, 1, 1, 1}

Negative group: {1}
```

Then:

```
Positive sum = 4

Negative sum = 1
```

And:

```
4 - 1 = 3
```

This satisfies the target.

Important observation: Every valid sign assignment corresponds to dividing the numbers into a positive group and a negative group.

This means you can reframe the problem as a subset sum problem.

### Hint 3: Derive the target subset sum

Let:

```
P = sum of numbers assigned +

N = sum of numbers assigned -

S = sum of all numbers
```

You know two things.

First:

\\[ P-N=target \\]

Second, because every number belongs to exactly one group:

\\[ P+N=S \\]

Add these two equations:

\\[ (P-N)+(P+N)=target+S \\]

The negative terms cancel:

\\[ 2P=S+target \\]

Therefore:

\\[ \boxed{P=\frac{S+target}{2}} \\]

This is the most important mathematical transformation in the problem.

Instead of counting different assignments of positive and negative signs, your new goal becomes:

Count the number of subsets whose sum equals `(S + target) / 2`.

Every chosen subset represents the numbers assigned a positive sign. All remaining numbers receive a negative sign.

### Hint 4: Apply the transformation to an example

Consider:

```
nums = {1, 1, 1, 1, 1};
target = 3;
```

Calculate the total sum:

```
S = 1 + 1 + 1 + 1 + 1;

// S = 5
```

Now calculate:

```
subsetTarget = (S + target) / 2;
```

So:

```
subsetTarget = (5 + 3) / 2
             = 4
```

Your new problem becomes:

```
How many different subsets
have a sum equal to 4?
```

Since all five numbers are `1`, you need to choose exactly four of them.

There are five ways to do this:

```
Choose positions: 0, 1, 2, 3
Choose positions: 0, 1, 2, 4
Choose positions: 0, 1, 3, 4
Choose positions: 0, 2, 3, 4
Choose positions: 1, 2, 3, 4
```

Therefore:

```
answer = 5;
```

The sign-assignment problem has now become a subset-counting problem.

### Hint 5: Check whether a valid answer is even possible

Before building your DP, consider whether `subsetTarget` is valid.

Recall:

```
subsetTarget = (S + target) / 2;
```

There are two important cases.

Case 1: The target is outside the achievable range.

Suppose:

```
nums = {1, 2, 3};
target = 10;
```

The total sum is:

```
S = 6;
```

Even assigning `+` to every number only gives:

```
1 + 2 + 3 = 6
```

You can never reach `10`.

Similarly, you cannot reach a target smaller than `-6`.

So:

```
if (abs(target) > S) {
    return 0;
}
```

Case 2: The required subset sum is not an integer.

Suppose:

```
nums = {1, 2, 3};
target = 1;
```

Then:

```
S = 6

S + target = 7

subsetTarget = 7 / 2 = 3.5
```

You cannot form a subset with sum `3.5` using integers.

So if:

```
(S + target) % 2 != 0
```

the answer must be `0`.

These checks are important because the transformed subset sum must be a valid nonnegative integer.

### Hint 6: Define your DP state

Now that you've transformed the problem, think back to LeetCode 518.

You can use:

```
dp[s]
```

to represent:

```
The number of subsets
that have sum exactly s

using the numbers processed so far.
```

For example:

```
dp[4] = 5;
```

means:

```
There are 5 different subsets
that add up to 4.
```

Compare this with previous problems:

| Problem                         | Meaning of `dp[s]`                      |
| ------------------------------- | --------------------------------------- |
| 416. Partition Equal Subset Sum | Whether sum `s` is possible             |
| 1049. Last Stone Weight II      | Whether sum `s` is possible             |
| 518. Coin Change II             | Number of coin combinations forming `s` |
| 494. Target Sum                 | Number of subsets forming `s`           |

Notice that LeetCode 494 and LeetCode 518 both use counting DP.

But there's one very important difference in how they update their states.

### Hint 7: Initialize the base case

Suppose:

```
subsetTarget = 4;
```

Create:

```
vector<int> dp(subsetTarget + 1, 0);
```

Initially:

```
Sum:  0  1  2  3  4

dp:   0  0  0  0  0
```

Now ask:

How many subsets can form a sum of zero before processing any numbers?

Exactly one:

```
The empty subset.
```

Therefore:

```
dp[0] = 1;
```

Your DP array becomes:

```
Sum:  0  1  2  3  4

dp:   1  0  0  0  0
```

This is the same base case you used in Coin Change II.

Mental model:

```
dp[0] = 1

There is one way to form sum 0:
Choose no numbers.
```

### Hint 8: For every number, choose whether to include it

Suppose you're processing:

```
int num = nums[i];
```

For each possible sum `s`, you have two choices.

Option 1: Skip the current number

If you could already form sum `s`, those subsets still exist.

Their count is:

```
dp[s]
```

Option 2: Include the current number

If you include `num`, the remaining sum must be:

```
s - num
```

So every subset that previously formed `s - num` can now form `s` by including the current number.

The number of these new subsets is:

```
dp[s - num]
```

Therefore:

```
dp[s] = dp[s] + dp[s - num];
```

Or:

```
dp[s] += dp[s - num];
```

This is exactly the same counting recurrence as Coin Change II.

But the loop direction is different.

### Hint 9: Why must you iterate backward?

This is the most important implementation detail.

In Coin Change II, you used:

```
for (int s = coin; s <= amount; s++) {
    dp[s] += dp[s - coin];
}
```

That loop iterates forward.

Why?

Because you can use each coin an unlimited number of times.

But in Target Sum, each number appears exactly once and must receive exactly one sign.

When selecting a positive subset, you can only include each array element once.

This makes it a 0/1 Knapsack problem.

Consider:

```
nums = {2};
subsetTarget = 4;
```

Initially:

```
Sum:  0  1  2  3  4

dp:   1  0  0  0  0
```

If you iterate forward:

```
dp[2] += dp[0];
```

Now:

```
dp[2] = 1
```

Later in the same iteration:

```
dp[4] += dp[2];
```

Now:

```
dp[4] = 1
```

But this is incorrect.

You only have one number `2`.

You cannot use it twice to form `4`.

To prevent this, iterate backward:

```
for (int s = subsetTarget; s >= num; s--) {
    dp[s] += dp[s - num];
}
```

Now `dp[s - num]` still refers to combinations from before the current number was processed.

Remember:

```
0/1 Knapsack:
Each number can be used once.
→ Iterate BACKWARD.

Unbounded Knapsack:
Each denomination can be reused.
→ Iterate FORWARD.
```

### Hint 10: Walk through an example

Consider:

```
nums = {1, 1, 1, 1, 1};
target = 3;
```

We already calculated:

```
S = 5;
subsetTarget = 4;
```

Initialize:

```
vector<int> dp(5, 0);
dp[0] = 1;
```

Number of subsets forming each sum

| Numbers processed | `dp[0]` | `dp[1]` | `dp[2]` | `dp[3]` | `dp[4]` |
| ----------------- | ------- | ------- | ------- | ------- | ------- |
| None              | 1       | 0       | 0       | 0       | 0       |
| First `1`         | 1       | 1       | 0       | 0       | 0       |
| Second `1`        | 1       | 2       | 1       | 0       | 0       |
| Third `1`         | 1       | 3       | 3       | 1       | 0       |
| Fourth `1`        | 1       | 4       | 6       | 4       | 1       |
| Fifth `1`         | 1       | 5       | 10      | 10      | 5       |

Explore how the DP changes after each number:

Initialize

Step 1 of 6

Sum 0

## 1

Sum 1

## 0

Sum 2

## 0

Sum 3

## 0

Sum 4

## 0

Only the empty subset forms sum 0.

&#x20;PreviousNext step&#x20;

Notice how the value of:

```
dp[4]
```

changes:

```
Initially:       0

After first 1:   0

After second 1:  0

After third 1:   0

After fourth 1:  1

After fifth 1:   5
```

Therefore:

```
return dp[subsetTarget];
```

gives:

```
5
```

This matches the expected answer.

### Hint 11: Be careful with zeros

This is an important edge case in Target Sum.

Suppose:

```
nums = {0, 0, 1};
target = 1;
```

The valid expressions are:

```
+0 +0 +1 = 1

+0 -0 +1 = 1

-0 +0 +1 = 1

-0 -0 +1 = 1
```

Even though:

```
+0 = -0
```

numerically, the sign assignments are different.

So the answer is:

```
4
```

What happens in your DP when:

```
num = 0;
```

Your recurrence becomes:

```
dp[s] += dp[s - 0];
```

Which simplifies to:

```
dp[s] += dp[s];
```

So every count doubles.

Why?

Because for every existing subset, you can either:

1. Include the zero.
2. Exclude the zero.

Both choices produce the same sum, but represent different sign assignments.

Important: You do not need special handling for zeros. The counting recurrence already handles them correctly.

Just make sure your backward loop includes `s = 0` when `num = 0`.

### Hint 12: Compare Target Sum with Coin Change II

At first glance, the DP code looks almost identical.

Both problems use:

```
dp[s] += dp[s - num];
```

But their interpretations are different.

| Property              | 494. Target Sum   | 518. Coin Change II         |
| --------------------- | ----------------- | --------------------------- |
| DP meaning            | Number of subsets | Number of coin combinations |
| Can reuse an element? | No                | Yes                         |
| Outer loop            | Numbers           | Coins                       |
| Inner loop direction  | Backward          | Forward                     |
| Knapsack type         | 0/1 Knapsack      | Unbounded Knapsack          |
| DP objective          | Count ways        | Count ways                  |

The key difference:

```
// 494. Target Sum

for (int num : nums) {
    for (int s = subsetTarget; s >= num; s--) {
        dp[s] += dp[s - num];
    }
}
```

Compared with:

```
// 518. Coin Change II

for (int coin : coins) {
    for (int s = coin; s <= amount; s++) {
        dp[s] += dp[s - coin];
    }
}
```

Notice that the recurrence is identical.

Only the iteration direction changes.

This is one of the most useful patterns to recognize in Knapsack DP.

### Hint 13: Analyze the complexity

Let:

```
n = nums.size();
S = sum(nums);
```

Your transformed target is:

```
subsetTarget = (S + target) / 2;
```

You process every number exactly once, and for each number, you iterate through subset sums up to `subsetTarget`.

Therefore:

```
Time: O(n × subsetTarget)
```

Since `subsetTarget` is at most `S` for valid inputs:

```
Time: O(n × S)
```

Your DP array has:

```
subsetTarget + 1
```

elements.

So:

```
Space: O(subsetTarget)
```

Or more generally:

```
Space: O(S)
```

### Skeleton — 1D DP (C++)

Try filling in the missing expressions yourself.

```
class Solution {
public:
    int findTargetSumWays(vector<int>& nums, int target) {

        // Calculate the sum of all numbers
        int total = accumulate(
            nums.begin(),
            nums.end(),
            0
        );

        // Check if target is outside
        // the achievable range
        if ( ? ) {
            return 0;
        }

        // Check if transformed target
        // is an integer
        if ( ? ) {
            return 0;
        }

        // Calculate target subset sum
        int subsetTarget =  ? ;

        // dp[s] = number of subsets
        // that form sum s
        vector<int> dp(subsetTarget + 1, 0);

        // Base case:
        // One way to form sum 0
        dp[0] =  ? ;

        // Process each number
        for (int num : nums) {

            // Iterate BACKWARD
            // Each number can be used once

            for (int s =  ? ; s >= num; s--) {

                // Option 1: Skip current number
                // Option 2: Include current number

                // dp[s] = ?
            }
        }

        // Return number of subsets
        // that form subsetTarget

        return  ? ;
    }
};
```

### Core recurrence

First, transform the problem.

Given:

\\[ P-N=target \\]

and:

\\[ P+N=S \\]

derive:

\\[ \boxed{P=\frac{S+target}{2}} \\]

Then define:

```
dp[s] = number of subsets
        that form sum s
```

Base case:

```
dp[0] = 1;
```

Transition:

```
for (int num : nums) {
    for (int s = subsetTarget; s >= num; s--) {
        dp[s] += dp[s - num];
    }
}
```

Final answer:

```
dp[subsetTarget]
```

Or mentally:

```
Given nums and target
          ↓
Calculate total sum S
          ↓
Divide numbers into:
Positive group P
Negative group N
          ↓
P - N = target
P + N = S
          ↓
Derive:
P = (S + target) / 2
          ↓
Check whether P is valid
          ↓
0/1 Knapsack counting DP
          ↓
dp[s] = number of subsets
        with sum s
          ↓
Initialize dp[0] = 1
          ↓
Process each number once
          ↓
Iterate sums BACKWARD
          ↓
dp[s] += dp[s - num]
          ↓
Return dp[P]
```

### Connection to previous DP problems

| Problem                         | DP meaning                         | Goal                             | Loop direction           |
| ------------------------------- | ---------------------------------- | -------------------------------- | ------------------------ |
| 416. Partition Equal Subset Sum | Can we form sum `s`?               | Reach exactly half               | Backward                 |
| 1049. Last Stone Weight II      | Can we form sum `s`?               | Get as close as possible to half | Backward                 |
| 322. Coin Change                | Minimum coins to form `s`          | Minimize coin count              | Forward in coin-first DP |
| 518. Coin Change II             | Number of combinations forming `s` | Count combinations               | Forward                  |
| 494. Target Sum                 | Number of subsets forming `s`      | Count sign assignments           | Backward                 |

The trickiest conceptual part of Target Sum is recognizing that assigning positive and negative signs is equivalent to selecting a subset with a particular sum.

Once you make that connection, the problem becomes a standard 0/1 Knapsack counting problem.

The three most important things to remember are:

```
Rule 1:
+/- sign assignments
→ Transform into subset sum using algebra.

Rule 2:
Count the number of valid subsets
→ Use dp[s] += dp[s - num].

Rule 3:
Each array element can be used once
→ Iterate BACKWARD.
```

The biggest takeaway: LeetCode 494 combines the subset-sum transformation from LeetCode 416/1049 with the counting recurrence from LeetCode 518, while using the backward iteration required by 0/1 Knapsack.
*/
class Solution {
public:
    int findTargetSumWays(vector<int>& nums, int target) {
        int sum = std::accumulate(nums.begin(), nums.end(), 0);

        if(std::abs(target) > sum){
            return 0;
        }

        int subsetTarget = 0;  

        if((sum + target) % 2 != 0){
            return 0;
        }else{
            subsetTarget = (sum + target) / 2;
        }


        vector<int> dp(subsetTarget + 1, 0);
        dp[0] = 1;
        
        for(int num : nums){
            for(int s = subsetTarget; s >= num; s--){
                dp[s] = dp[s] + dp[s - num];
            }
        }
        
        return dp[subsetTarget];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna