// Method 1: 1D DP
/*
This is a Dynamic Programming / Integer Partitioning problem.

The key idea is to break an integer `n` into at least two positive integers such that their product is maximized.

For each integer `x`, try every possible first split, then decide whether it's better to stop splitting or continue breaking the remaining number.

### Hint 1: Understand what you're maximizing

Suppose:

```
n = 10;
```

You could break it into:

```
10 = 5 + 5
Product = 5 × 5 = 25
```

Or:

```
10 = 3 + 3 + 4
Product = 3 × 3 × 4 = 36
```

Or:

```
10 = 2 + 2 + 3 + 3
Product = 2 × 2 × 3 × 3 = 36
```

The maximum product is:

```
36
```

Notice that you're maximizing the product, not the number of integers or their sum.

### Hint 2: Define your DP state

Let:

```
dp[x]
```

mean:

```
maximum product obtainable by breaking
integer x into at least two positive integers
```

Your goal is:

```
dp[n]
```

For example:

```
dp[2] = 1    // 1 × 1
dp[3] = 2    // 1 × 2
dp[4] = 4    // 2 × 2
dp[5] = 6    // 2 × 3
```

### Hint 3: Try every possible first split

Suppose you're calculating:

```
dp[x]
```

You can split `x` into:

```
1 + (x - 1)
2 + (x - 2)
3 + (x - 3)
...
(x - 1) + 1
```

In general:

```
x = j + (x - j)
```

where:

```
1 <= j && j < x
```

So your inner loop can be:

```
for (int j = 1; j < x; j++) {
    // Try splitting x into j and x - j
}
```

### Hint 4: You have two choices after splitting

This is the most important part of the problem.

Suppose:

```
x = j + (x - j)
```

You have two possibilities.

Option 1: Stop splitting

Keep both integers as they are.

Your product is:

```
j * (x - j)
```

Option 2: Continue splitting

Keep `j`, but break the remaining integer `x - j` optimally.

Your product becomes:

```
j * dp[x - j]
```

You want whichever gives the larger product.

### Hint 5: Build the recurrence

Combine those two choices:

```
dp[x] = max({
    dp[x],
    j * (x - j),
    j * dp[x - j]
});
```

Why do we include `dp[x]`?

Because you're trying every possible `j`, and you want to remember the best product found so far.

Mental model:

```
maximum product for x
=
max(
    best answer found so far,
    split into exactly two numbers,
    split remaining part further
)
```

### Hint 6: Why can't you just use `j * dp[x - j]`?

This is the trickiest conceptual detail.

Consider:

```
x = 4;
```

Suppose you split:

```
4 = 2 + 2
```

The product should be:

```
2 × 2 = 4
```

But:

```
dp[2] = 1;
```

because breaking `2` into at least two integers gives:

```
1 × 1 = 1
```

So if you only calculate:

```
2 * dp[2]
```

you get:

```
2 × 1 = 2
```

which is worse than simply keeping the second `2` intact.

That's why your recurrence must consider both:

```
j * (x - j)     // don't break remainder
j * dp[x - j]   // break remainder
```

### Hint 7: What are the base cases?

You can initialize:

```
vector<int> dp(n + 1, 0);
```

And optionally:

```
dp[1] = 1;
```

For the first meaningful integer:

```
n = 2;
```

you must break it into:

```
1 + 1
```

giving:

```
dp[2] = 1;
```

For:

```
n = 3;
```

the best is:

```
1 + 2
```

giving:

```
dp[3] = 2;
```

You can let your recurrence compute these automatically by starting at `x = 2`.

The value `dp[1] = 1` is only a convenient initialization; the actual problem requires breaking `n` into at least two integers.

### Hint 8: Walk through an example

Suppose:

```
n = 5;
```

You already calculated:

```
dp[2] = 1
dp[3] = 2
dp[4] = 4
```

Now calculate:

```
dp[5]
```

Try every first split:

| j | Stop splitting: `j * (5-j)` | Continue: `j * dp[5-j]` |
| - | --------------------------- | ----------------------- |
| 1 | 4                           | 4                       |
| 2 | 6                           | 4                       |
| 3 | 6                           | 3                       |
| 4 | 4                           | 4                       |

The maximum is:

```
dp[5] = 6;
```

which corresponds to:

```
5 = 2 + 3

2 × 3 = 6
```

### Hint 9: Build the DP array bottom-up

Start from:

```
x = 2
```

and work toward:

```
x = n
```

For every `x`, try all splits:

```
for (int x = 2; x <= n; x++) {

    for (int j = 1; j < x; j++) {

        // Option 1: don't break x - j

        // Option 2: break x - j further

        // Update dp[x] with the maximum
    }
}
```

Why does this work?

Because:

```
x - j < x
```

So whenever you calculate:

```
dp[x]
```

the smaller DP states you need have already been computed.

### Hint 10: Why greedy isn't necessary

You might notice that splitting integers into `3`s often produces the best product.

For example:

```
10 = 3 + 3 + 4

Product = 36
```

There is a mathematical greedy solution based on repeatedly taking `3`, with a special case for a remainder of `1`.

However, the DP approach is more useful for practicing recurrence construction, because it explicitly explores every possible split.

I'd recommend implementing the DP solution first.

### Hint 11: Complexity

You have two nested loops:

```
for (int x = 2; x <= n; x++) {
    for (int j = 1; j < x; j++) {
        // constant-time DP transition
    }
}
```

Therefore:

```
Time:  O(n²)
Space: O(n)
```

### Skeleton

```
class Solution {
public:
    int integerBreak(int n) {

        vector<int> dp(n + 1, 0);

        // Optional base case
        dp[1] = 1;

        for (int x = 2; x <= n; x++) {

            for (int j = 1; j < x; j++) {

                // Split x into:
                // j + (x - j)

                // Option 1:
                // Don't break (x - j) further

                // Option 2:
                // Break (x - j) optimally

                dp[x] = max({
                    dp[x],

                    // product if we stop splitting

                    // product if we continue splitting
                });
            }
        }

        return dp[n];
    }
};
```

The core recurrence is:

```
For every integer x:

    Try every split j + (x - j)

    dp[x] = max(
        dp[x],
        j * (x - j),
        j * dp[x - j]
    )
```

Or mentally:

```
current integer x
        ↓
try every possible first split
        ↓
j + (x - j)
        ↓
compare two choices:

stop splitting
→ j * (x - j)

continue splitting
→ j * dp[x - j]
        ↓
take the maximum product
        ↓
return dp[n]
```

The trickiest part is realizing that you don't always want to break the remaining integer further.

That's why `j * (x - j)` and `j * dp[x - j]` are both necessary in the recurrence.
*/
class Solution {
public:
    int integerBreak(int n) {
        vector<int> dp(n + 1, 0);
        dp[1] = 1;

        for(int x=2; x <= n; x++){
            for(int j=1; j < x; j++){
                dp[x] = std::max({dp[x], j * (x - j), j * dp[x - j]});
            }
        }

        return dp[n];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna