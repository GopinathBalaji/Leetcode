// Method 1: DP + Unbounded Knapsack (Combination Counting) problem
/*
# LeetCode 518. Coin Change II — Hints

This is a Dynamic Programming + Unbounded Knapsack (Combination Counting) problem.

The key idea is that instead of trying every possible combination of coins using brute force, you can use DP to count how many different ways you can form a target amount.

This is closely related to LeetCode 322. Coin Change, but there is an important difference:

- 322\. Coin Change: Find the minimum number of coins needed to make an amount.
- 518\. Coin Change II: Find the number of different combinations of coins that make an amount.

The important observation is that you can use each coin denomination an unlimited number of times, but the order of coins does not matter.

### Hint 1: Understand what you're counting

Suppose:

```
amount = 5;
coins = {1, 2, 5};
```

You want to count the number of different combinations that sum to `5`.

The valid combinations are:

```
1 + 1 + 1 + 1 + 1 = 5

1 + 1 + 1 + 2 = 5

1 + 2 + 2 = 5

5 = 5
```

So the answer is:

```
4
```

But notice something important.

These two sequences:

```
1 + 2 + 2

2 + 1 + 2
```

represent the same combination.

They should only be counted once.

This is the first major challenge of the problem.

You must count combinations, not permutations.

### Hint 2: Recognize the Unbounded Knapsack pattern

Think about the properties of this problem.

You have:

```
vector<int> coins;
int amount;
```

For each coin denomination, you can:

1. Skip the coin.
2. Use the coin once.
3. Use the coin twice.
4. Use the coin as many times as needed.

This makes the problem similar to the Unbounded Knapsack pattern.

Compare it to 0/1 Knapsack:

| Property      | 0/1 Knapsack                                     | Coin Change II     |
| ------------- | ------------------------------------------------ | ------------------ |
| Use each item | At most once                                     | Unlimited times    |
| Target        | Capacity or sum                                  | Amount             |
| DP objective  | Usually maximize value or determine reachability | Count combinations |
| 1D iteration  | Backward                                         | Forward            |

The most important distinction is:

```
0/1 Knapsack:
Each item can be used once.

Unbounded Knapsack:
Each item can be used unlimited times.
```

Keep this in mind when deciding which direction to iterate through your DP array.

### Hint 3: Define your DP state

Instead of trying to construct every combination explicitly, consider:

```
dp[s]
```

Let it represent:

```
The number of different combinations
that can form amount s

using the coin denominations
processed so far.
```

For example:

```
dp[5] = 4;
```

means:

```
There are 4 different combinations
that can form amount 5.
```

Notice the difference from other DP problems.

In LeetCode 416, you might use:

```
dp[s] = true;
```

to indicate that a sum is achievable.

In LeetCode 322, you might use:

```
dp[s] = minimum number of coins;
```

But here:

```
dp[s] = number of combinations;
```

Your DP array stores counts, rather than booleans or minimum values.

### Hint 4: Initialize the base case

Suppose:

```
amount = 5;
```

You can create:

```
vector<int> dp(amount + 1, 0);
```

Initially:

```
Amount:  0  1  2  3  4  5

dp:      0  0  0  0  0  0
```

But consider this question:

How many ways can you form an amount of `0`?

There is exactly one way:

```
Choose no coins.
```

Therefore:

```
dp[0] = 1;
```

Your initial DP array becomes:

```
Amount:  0  1  2  3  4  5

dp:      1  0  0  0  0  0
```

This is extremely important.

If you initialized `dp[0] = 0`, your recurrence would never generate any combinations.

Mental model:

```
dp[0] = 1

One way to make zero:
Choose nothing.
```

This empty combination is the foundation for constructing all other combinations.

### Hint 5: For each coin, decide whether to include it

Suppose you're processing:

```
int coin = coins[i];
```

And you want to calculate:

```
dp[s]
```

Think about how many ways you can make amount `s`.

There are two types of combinations.

Option 1: Don't use the current coin

These combinations have already been counted.

Their count is:

```
dp[s]
```

Option 2: Use the current coin

If you include one coin of value `coin`, how much amount remains?

```
s - coin
```

So if you already know the number of ways to form:

```
dp[s - coin]
```

you can add the current coin to each of those combinations.

For example:

```
Current coin = 2
Target amount = 5
```

If you use one coin worth `2`, you need to form:

```
5 - 2 = 3
```

Suppose:

```
dp[3] = 2;
```

Then those two combinations can each be extended with a coin worth `2` to form amount `5`.

Therefore, you add those two possibilities to `dp[5]`.

This gives the key recurrence:

```
dp[s] = dp[s] + dp[s - coin];
```

Or more simply:

```
dp[s] += dp[s - coin];
```

Why addition?

Because you're counting the total number of combinations, not finding a minimum or maximum.

### Hint 6: Why should the amount loop go forward?

This is one of the most important implementation details.

Since you can use each coin an unlimited number of times, you want the current coin to be reusable.

Consider:

```
coins = {2};
amount = 6;
```

You should be able to form:

```
2 + 2 + 2 = 6
```

Let's examine how the DP updates.

Initially:

```
dp[0] = 1
```

Now process coin `2`.

First:

```
dp[2] += dp[0];
```

So:

```
dp[2] = 1
```

Now:

```
dp[4] += dp[2];
```

Since `dp[2] = 1`:

```
dp[4] = 1
```

This represents:

```
2 + 2
```

Next:

```
dp[6] += dp[4];
```

So:

```
dp[6] = 1
```

This represents:

```
2 + 2 + 2
```

Notice that the same coin was reused multiple times.

This works because you iterate forward:

```
for (int s = coin; s <= amount; s++) {
    dp[s] += dp[s - coin];
}
```

If you iterated backward, you wouldn't be able to reuse the current coin during the same iteration.

Remember:

```
0/1 Knapsack:
Each item can be used once.
→ Iterate backward.

Unbounded Knapsack:
Each item can be used unlimited times.
→ Iterate forward.
```

### Hint 7: Why must the outer loop iterate over coins?

This is the trickiest conceptual part of Coin Change II.

You might consider writing:

```
for (int s = 1; s <= amount; s++) {
    for (int coin : coins) {
        // Update dp[s]
    }
}
```

But there is a problem.

This approach can count different arrangements of the same coins separately.

For example:

```
amount = 3;
coins = {1, 2};
```

There are only two combinations:

```
1 + 1 + 1

1 + 2
```

But an amount-first DP can count:

```
1 + 2

2 + 1
```

as different ways.

So you might incorrectly obtain:

```
3
```

instead of:

```
2
```

Why does this happen?

Because when you iterate over amounts first, each transition can append any coin as the last coin, so different orders can be counted separately.

Instead, process coins in the outer loop:

```
for (int coin : coins) {

    for (int s = coin; s <= amount; s++) {

        // Update dp[s]

    }
}
```

This ensures that combinations are built using the coin denominations in a fixed processing order.

For example:

```
Process coin 1 first.

Then process coin 2.

Then process coin 5.
```

You never go back to process coin `1` after processing coin `2`.

Therefore, the same combination won't be counted multiple times in different orders.

Remember:

```
Coins outer loop:
Count combinations.

Amounts outer loop:
Count permutations (with this recurrence).
```

This distinction appears frequently in dynamic programming problems.

### Hint 8: Walk through an example

Consider:

```
amount = 5;
coins = {1, 2, 5};
```

Initialize:

```
vector<int> dp(6, 0);
dp[0] = 1;
```

Number of combinations for each amount

| Coins processed | dp[0] | dp[1] | dp[2] | dp[3] | dp[4] | dp[5] |
| --------------- | ----- | ----- | ----- | ----- | ----- | ----- |
| None            | 1     | 0     | 0     | 0     | 0     | 0     |
| `{1}`           | 1     | 1     | 1     | 1     | 1     | 1     |
| `{1, 2}`        | 1     | 1     | 2     | 2     | 3     | 3     |
| `{1, 2, 5}`     | 1     | 1     | 2     | 2     | 3     | 4     |

Explore how the DP changes as each coin is processed:

Initialize DP

Step 1 of 4

0

## 1

1

## 0

2

## 0

3

## 0

4

## 0

5

## 0

Top: amountBox: number of combinations

Only amount 0 is achievable, using the empty combination.

&#x20;PreviousNext step&#x20;

Notice how the value of:

```
dp[5]
```

changes:

```
Initially:       0

After coin 1:    1

After coin 2:    3

After coin 5:    4
```

Each new denomination contributes additional combinations without duplicating the combinations already counted.

### Hint 9: Understand the recurrence more deeply

The recurrence is:

```
dp[s] += dp[s - coin];
```

Let's examine exactly what happens when:

```
coin = 2;
s = 4;
```

Before processing coin `2`, we have:

```
dp[4] = 1
```

because the only combination is:

```
1 + 1 + 1 + 1
```

Now we want to include the coin `2`.

We calculate:

```
dp[4] += dp[2];
```

But `dp[2]` has already been updated during the current coin's iteration.

It represents two possibilities:

```
1 + 1

2
```

Add another coin worth `2` to each:

```
1 + 1 + 2

2 + 2
```

These are two new combinations for amount `4`.

So:

```
Previous combinations = 1

New combinations = 2

Total combinations = 3
```

Therefore:

```
dp[4] = 3;
```

This explains why forward iteration is necessary.

It allows `dp[s - coin]` to include combinations that already use the current coin, enabling unlimited reuse.

### Hint 10: What happens if an amount cannot be formed?

Suppose:

```
amount = 3;
coins = {2};
```

You can use coin `2` any number of times:

```
2
2 + 2
2 + 2 + 2
...
```

But none of these combinations sum to `3`.

Initially:

```
Amount: 0  1  2  3

dp:     1  0  0  0
```

After processing coin `2`:

```
Amount: 0  1  2  3

dp:     1  0  1  0
```

Notice:

```
dp[3] = 0;
```

This means no valid combination can form amount `3`.

You don't need a special check.

The DP naturally handles impossible amounts by leaving their counts at zero.

### Hint 11: Why is this different from Coin Change I?

Compare LeetCode 322 and LeetCode 518.

In both problems, you can reuse each denomination indefinitely.

But what does your DP state mean?

LeetCode 322 — Coin Change

```
dp[s] = minimum coins needed
        to form amount s
```

The transition focuses on minimizing:

```
dp[s] = min(
    dp[s],
    dp[s - coin] + 1
);
```

Why `+1`?

Because you're using one additional coin.

LeetCode 518 — Coin Change II

```
dp[s] = number of combinations
        that form amount s
```

The transition focuses on counting:

```
dp[s] += dp[s - coin];
```

Why addition?

Because you're adding the number of new combinations that include the current denomination.

The distinction is:

```
322. Coin Change

Optimization DP
→ Find minimum number of coins
```

```
518. Coin Change II

Counting DP
→ Find number of combinations
```

They share an unbounded-coin structure, but the DP operations are different.

### Hint 12: Analyze the complexity

Let:

```
n = coins.size();
A = amount;
```

You process every coin denomination.

For each coin, you iterate through amounts up to `A`.

So your time complexity is:

```
Time: O(n × A)
```

Your DP array contains:

```
amount + 1
```

elements.

Therefore:

```
Space: O(A)
```

This is already the space-optimized DP solution.

You don't need a two-dimensional array.

### Skeleton — 1D DP (C++)

Try filling in the missing parts yourself.

```
class Solution {
public:
    int change(int amount, vector<int>& coins) {

        // dp[s] = number of combinations
        // that form amount s

        vector<int> dp(amount + 1, 0);

        // Base case:
        // One way to form amount 0
        dp[0] =  ? ;

        // Process each coin denomination
        for (int coin : coins) {

            // Iterate FORWARD because
            // coins can be reused

            for (int s =  ? ; s <= amount; s++) {

                // Option 1:
                // Don't use the current coin

                // Option 2:
                // Include the current coin

                // dp[s] = ?
            }
        }

        // Return number of combinations
        // to form the target amount

        return  ? ;
    }
};
```

### Core recurrence

The DP state is:

```
dp[s] = number of combinations
        that form amount s
```

The base case is:

```
dp[0] = 1;
```

For each coin:

```
for (int s = coin; s <= amount; s++) {
    dp[s] += dp[s - coin];
}
```

The final answer is:

```
dp[amount]
```

Or mentally:

```
Given coins and target amount
             ↓
Create dp[0 ... amount]
             ↓
Initialize dp[0] = 1
             ↓
Process coins one by one
             ↓
For each coin:
             ↓
Iterate amounts FORWARD
from coin to target
             ↓
For every amount s:
             ↓
dp[s] += dp[s - coin]
             ↓
Finish processing all coins
             ↓
Return dp[amount]
```

### Connection to previous DP problems

| Problem                         | DP meaning                          | Goal                     |
| ------------------------------- | ----------------------------------- | ------------------------ |
| 416. Partition Equal Subset Sum | Can we form sum `s`?                | Determine reachability   |
| 1049. Last Stone Weight II      | Can we form sum `s`?                | Minimize difference      |
| 322. Coin Change                | Minimum coins to form `s`           | Minimize number of coins |
| 518. Coin Change II             | Number of combinations for `s`      | Count combinations       |
| 377. Combination Sum IV         | Number of ordered sequences for `s` | Count permutations       |

An especially useful comparison is with LeetCode 377. Combination Sum IV.

Both problems use a similar counting recurrence, but the loop order changes what you're counting.

|                | 518. Coin Change II | 377. Combination Sum IV |
| -------------- | ------------------- | ----------------------- |
| Order matters? | No                  | Yes                     |
| Outer loop     | Coins               | Amounts                 |
| Inner loop     | Amounts             | Numbers                 |
| Counts         | Combinations        | Ordered sequences       |

The trickiest conceptual part of Coin Change II is understanding why the coin loop goes outside and why the amount loop goes forward.

Once you understand those two details, you can recognize a large class of Unbounded Knapsack and counting DP problems.

Two rules worth remembering:

```
Rule 1: Can reuse the same coin
        → Iterate amounts FORWARD.

Rule 2: Order of coins doesn't matter
        → Iterate COINS in the outer loop.
```

These are the two most important takeaways from LeetCode 518.
*/
class Solution {
public:
    int change(int amount, vector<int>& coins) {
        vector<long long> dp(amount + 1, 0);
        dp[0] = 1;

        for(int coin : coins){
            for(int s=coin; s <= amount; s++){
                dp[s] = std::min((long long)INT_MAX, dp[s] + dp[s-coin]);
            }
        }  

        return (int)dp[amount]; 
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna