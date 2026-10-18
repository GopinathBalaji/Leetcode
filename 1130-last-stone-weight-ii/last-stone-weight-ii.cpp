// Method 1: 1D DP
/*
This is a Dynamic Programming + 0/1 Knapsack (Subset Sum) problem.

The key idea is that instead of simulating stones smashing into each other, you can think of the stones as being divided into two groups.

Your goal is to make the sums of these two groups as close as possible.

This is very similar to 416. Partition Equal Subset Sum, except you don't need the two groups to have exactly equal sums. You want to minimize the difference between their sums.

### Hint 1: Reframe the problem as partitioning stones

Suppose:

```
stones = {2, 7, 4, 1, 8, 1};
```

The total sum is:

```
2 + 7 + 4 + 1 + 8 + 1 = 23
```

Imagine dividing the stones into two groups:

```
Group A: {7, 4}       → sum = 11
Group B: {2, 1, 8, 1} → sum = 12
```

The difference is:

```
12 - 11 = 1
```

That means the minimum possible final stone weight is `1`.

The important observation is that the stone-smashing process can be viewed as assigning each stone either a positive or negative sign.

For example:

```
(2 + 1 + 8 + 1) - (7 + 4) = 1
```

So instead of simulating the collisions, focus on finding two groups with the smallest possible sum difference.

### Hint 2: Reduce the problem to a target sum

Let:

```
int total = accumulate(
    stones.begin(),
    stones.end(),
    0
);
```

Suppose you choose one group of stones whose sum is:

```
s
```

The remaining group must have sum:

```
total - s
```

So their difference is:

```
(total - s) - s
```

which simplifies to:

```
total - 2 * s
```

Your goal is to minimize this difference.

But there's a useful trick.

You only need to consider:

```
s <= total / 2
```

because you can always call the smaller-sum group `s`.

### Hint 3: Find the largest achievable sum that doesn't exceed half

Suppose:

```
total = 23;
```

Ideally, you want one group to have sum:

```
11
```

and the other to have:

```
12
```

because those are as close as possible.

So define:

```
int target = total / 2;
```

Your new problem becomes:

```
Find the largest subset sum s
such that:

s <= target
```

Once you find that sum, the answer is:

```
total - 2 * s
```

Mental model:

```
minimum leftover stone
=
total sum
-
2 × largest achievable subset sum <= total/2
```

### Hint 4: This is almost exactly Partition Equal Subset Sum

Remember LeetCode 416?

You used:

```
dp[s]
```

to represent:

```
whether it is possible to form sum s
using the stones processed so far
```

You can use the same DP state here.

Define:

```
vector<bool> dp(target + 1, false);
```

where:

```
dp[s] = true

means some subset of stones
can add up to exactly s
```

The difference from problem 416 is:

```
416. Partition Equal Subset Sum:
Can we reach exactly total / 2?

1049. Last Stone Weight II:
What is the largest sum we can reach
without exceeding total / 2?
```

### Hint 5: Initialize the base case

You can always form a sum of zero by selecting no stones.

So:

```
dp[0] = true;
```

Initially, every other sum is unreachable:

```
vector<bool> dp(target + 1, false);

dp[0] = true;
```

This gives you the foundation for building larger achievable sums.

### Hint 6: For every stone, choose whether to take it or skip it

Suppose you're processing:

```
int stone = stones[i];
```

For each possible sum `s`, you have two choices.

Option 1: Skip the stone

If sum `s` was already achievable, it remains achievable.

```
dp[s]
```

Option 2: Include the stone

If you could previously make:

```
s - stone
```

then adding the current stone lets you make:

```
s
```

So:

```
dp[s - stone]
```

tells you whether that choice is possible.

Combine both choices:

```
dp[s] = dp[s] || dp[s - stone];
```

This is the exact same recurrence as Partition Equal Subset Sum.

### Hint 7: Iterate backward to avoid reusing stones

This is the most important implementation detail.

Each stone can only be used once.

So you must iterate:

```
for (int s = target; s >= stone; s--) {
    dp[s] = dp[s] || dp[s - stone];
}
```

Why backward?

Suppose you have:

```
stones = {2};
```

If you iterate forward:

```
dp[2] becomes true

then dp[4] might become true
using the same 2 again
```

But you only have one stone weighing `2`.

Backward iteration prevents a stone from being reused during the same iteration.

Remember the rule:

```
0/1 Knapsack:
Each item can be used once
→ iterate backward
```

### Hint 8: Walk through an example

Suppose:

```
stones = {2, 7, 4, 1, 8, 1};
```

Calculate:

```
total = 23;
target = 11;
```

Initially:

```
Reachable sums:
{0}
```

Now process the stones one by one.

Subset Sum DP Walkthrough

Step 5 of 7

sum

0

sum

1

sum

2

sum

3

sum

4

sum

5

sum

6

sum

7

sum

8

sum

9

sum

10

sum

11

After processing stone 4: 1

All sums from 0 through 11 are now achievable.

&#x20;PreviousNext&#x20;

After processing all the stones, notice:

```
dp[11] == true;
```

That means you found a subset with sum `11`.

The other group has sum:

```
23 - 11 = 12
```

So your final answer is:

```
23 - 2 * 11
```

which gives:

```
1
```

### Hint 9: What if you cannot reach exactly `target`?

This is the main difference from problem 416.

Suppose:

```
stones = {2, 7, 4};
```

Then:

```
total = 13;
target = 6;
```

You might want to form sum `6`, but you cannot.

The reachable sums not exceeding `6` are:

```
0, 2, 4, 6
```

Actually, `6` is reachable because:

```
2 + 4 = 6
```

So the answer is:

```
13 - 2 × 6 = 1
```

Now consider another example:

```
stones = {2, 4, 9};
```

Then:

```
total = 15;
target = 7;
```

The reachable sums up to `7` are:

```
0, 2, 4, 6
```

You cannot make `7`, but you can make `6`.

Therefore:

```
15 - 2 × 6 = 3
```

The important point: You don't need to reach `target` exactly.

You need the largest achievable sum at or below `target`.

### Hint 10: Search backward for the best subset sum

After filling your DP array, start from:

```
target
```

and search downward:

```
for (int s = target; s >= 0; s--) {
    if (dp[s]) {
        // This is the largest achievable sum
    }
}
```

Why search backward?

Because you want the subset sum closest to half of the total.

The first reachable sum you find is optimal.

Then calculate:

```
total - 2 * s
```

and return it.

### Hint 11: Why not directly simulate the stone smashing?

You might consider using a max heap and repeatedly smashing the two heaviest stones.

But that's not guaranteed to produce the minimum final weight, because the problem lets you choose which two stones to smash.

For example:

```
stones = {2, 7, 4, 1, 8, 1};
```

Choosing the largest two stones first gives one particular sequence of operations.

But the goal is to find the best possible sequence of operations, not just simulate a fixed strategy.

The subset-sum transformation allows you to explore all meaningful partitions efficiently.

### Hint 12: Complexity

Let:

```
n = stones.size();
S = sum(stones);
```

Your target is:

```
S / 2
```

For each stone, you iterate through sums up to that target.

Therefore:

```
Time:  O(n × S)
Space: O(S)
```

More precisely, both complexities can be expressed in terms of `S / 2`, but the Big-O bounds are unchanged.

### Skeleton — 1D DP

```
class Solution {
public:
    int lastStoneWeightII(vector<int>& stones) {
        int total = accumulate(
            stones.begin(),
            stones.end(),
            0
        );

        int target = total / 2;

        vector<bool> dp(target + 1, false);

        // Base case:
        // Sum 0 is always achievable

        for (int stone : stones) {

            // Iterate BACKWARD
            // from target down to stone

            for (int s = target; s >= stone; s--) {

                // Option 1: skip this stone
                // Option 2: include this stone
                //
                // dp[s] = ?
            }
        }

        // Find the largest achievable sum
        // that does not exceed target

        for (int s = target; s >= 0; s--) {

            if (dp[s]) {

                // Return the difference between
                // the two groups
            }
        }

        return 0;
    }
};
```

### Core recurrence

```
dp[0] = true

For each stone:

    For s from target down to stone:

        dp[s] = dp[s] || dp[s - stone]
```

Then:

```
Find largest s such that:

dp[s] == true
and
s <= total / 2
```

Return:

```
total - 2 * s
```

Or mentally:

```
stones
    ↓
calculate total sum
    ↓
target = total / 2
    ↓
0/1 Knapsack:
find reachable subset sums
    ↓
find largest reachable sum <= target
    ↓
divide stones into two groups
with sums s and total - s
    ↓
return total - 2 * s
```

### Connection to previous DP problems

| Problem                         | DP meaning                      | Goal                                    |
| ------------------------------- | ------------------------------- | --------------------------------------- |
| 416. Partition Equal Subset Sum | Can we form sum `s`?            | Reach exactly `total / 2`               |
| 1049. Last Stone Weight II      | Can we form sum `s`?            | Get as close as possible to `total / 2` |
| 322. Coin Change                | Minimum coins to form sum `s`   | Minimize number of coins                |
| 279. Perfect Squares            | Minimum squares to form sum `s` | Minimize number of squares              |

The trickiest conceptual part is recognizing that smashing stones is equivalent to partitioning them into two groups and minimizing the difference between their sums.

Once you make that connection, you can reuse almost the exact same 1D DP approach from 416. Partition Equal Subset Sum, including the backward iteration to prevent using a stone more than once.
*/
class Solution {
public:
    int lastStoneWeightII(vector<int>& stones) {
        int total = std::accumulate(stones.begin(), stones.end(), 0);

        int target = total / 2;

        vector<bool> dp(target + 1, false);
        dp[0] = true;

        for(int stone : stones){
            for(int s = target; s >= stone; s--){
                dp[s] = dp[s] || dp[s - stone];
            }
        }
        
        for(int s = target; s >= 0; s--){
            if(dp[s]){
                int ans = total - 2 * s;
                return ans;
            }
        }

        return 0;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna