// Method 1: 1D DP + Unbounded Knapsack
/*
This is a Dynamic Programming + Unbounded Knapsack problem.

The key idea is very similar to 322. Coin Change, except instead of choosing from a given list of coins, you're choosing from perfect squares:

```
1, 4, 9, 16, 25, 36, ...
```

Your goal is to find the minimum number of perfect squares that sum to `n`.

### Hint 1: Think of perfect squares as coins

Suppose:

```
n = 12;
```

You can use these perfect squares:

```
1, 4, 9
```

One possible representation is:

```
12 = 4 + 4 + 4
```

which uses `3` perfect squares.

Another is:

```
12 = 9 + 1 + 1 + 1
```

which uses `4`.

You want the minimum, so the answer is:

```
3
```

Notice that you're allowed to reuse a perfect square multiple times.

This is exactly like an unbounded knapsack problem.

### Hint 2: Define your DP state

Let:

```
dp[x]
```

mean:

```
the minimum number of perfect squares
needed to make sum x
```

Your goal is:

```
dp[n]
```

For example:

```
dp[1] = 1   // 1
dp[2] = 2   // 1 + 1
dp[3] = 3   // 1 + 1 + 1
dp[4] = 1   // 4
dp[5] = 2   // 4 + 1
```

### Hint 3: What is the base case?

To make a sum of:

```
0
```

you need:

```
0 perfect squares
```

So:

```
dp[0] = 0;
```

This gives your recurrence a starting point.

For example, if you're trying to calculate:

```
dp[9]
```

and you use the perfect square `9`, then:

```
dp[9] = dp[0] + 1;
```

which gives `1`.

### Hint 4: Think about the last perfect square you use

Suppose you're calculating:

```
dp[x]
```

Imagine the last perfect square you choose is:

```
j * j
```

Then before choosing that square, you must have already formed:

```
x - j * j
```

So your candidate answer becomes:

```
dp[x - j * j] + 1
```

The `+1` represents the current perfect square.

Since you want the fewest squares, take the minimum:

```
dp[x] = min(
    dp[x],
    dp[x - j * j] + 1
);
```

### Hint 5: Try every possible perfect square

For each amount:

```
x
```

try all perfect squares that are less than or equal to `x`.

For example, when:

```
x = 13;
```

the possible squares are:

```
1, 4, 9
```

So:

```
dp[13] = min({
    dp[12] + 1,  // use 1
    dp[9] + 1,   // use 4
    dp[4] + 1    // use 9
});
```

Since:

```
13 = 9 + 4
```

the answer is:

```
dp[13] = 2;
```

### Hint 6: You don't need to generate perfect squares separately

You could precompute:

```
vector<int> squares;
```

containing:

```
1, 4, 9, 16, ...
```

But you can also generate them directly in your inner loop.

Something like:

```
for (int j = 1; j * j <= x; j++) {
    int square = j * j;

    // try using this square
}
```

This automatically considers every possible perfect square for the current amount.

### Hint 7: Initialize DP values to something large

Initially, you don't know the minimum number of squares needed for every amount.

So initialize:

```
vector<int> dp(n + 1, n + 1);
```

Why `n + 1`?

Because you can always represent any positive integer `x` using `x` copies of `1`:

```
5 = 1 + 1 + 1 + 1 + 1
```

Therefore, the optimal answer can never exceed `n`.

So:

```
n + 1
```

is a safe infinity value.

Then:

```
dp[0] = 0;
```

### Hint 8: Build the DP array bottom-up

Start from the smallest sum:

```
for (int x = 1; x <= n; x++) {
```

For every `x`, try every square:

```
for (int j = 1; j * j <= x; j++) {
```

Then update:

```
dp[x] = min(
    dp[x],
    dp[x - j * j] + 1
);
```

Why does bottom-up work?

Because:

```
x - j * j < x
```

So whenever you're calculating `dp[x]`, all the smaller DP states you need have already been computed.

### Hint 9: Example with `n = 12`

Let's build the answers:

| x  | Best representation | dp[x] |
| -- | ------------------- | ----- |
| 1  | 1                   | 1     |
| 2  | 1 + 1               | 2     |
| 3  | 1 + 1 + 1           | 3     |
| 4  | 4                   | 1     |
| 5  | 4 + 1               | 2     |
| 6  | 4 + 1 + 1           | 3     |
| 7  | 4 + 1 + 1 + 1       | 4     |
| 8  | 4 + 4               | 2     |
| 9  | 9                   | 1     |
| 10 | 9 + 1               | 2     |
| 11 | 9 + 1 + 1           | 3     |
| 12 | 4 + 4 + 4           | 3     |

So:

```
dp[12] = 3;
```

### Hint 10: Why greedy doesn't work

You might think:

```
always choose the largest perfect square
that fits in the remaining amount
```

But consider:

```
n = 12;
```

Greedy would choose:

```
9 + 1 + 1 + 1
```

giving:

```
4 squares
```

But the optimal representation is:

```
4 + 4 + 4
```

giving:

```
3 squares
```

That's why you need DP rather than simply choosing the largest available square.

### Hint 11: Complexity

For each amount from `1` to `n`, you try every perfect square up to that amount.

There are approximately:

```
√n
```

perfect squares less than or equal to `n`.

Therefore:

```
Time:  O(n × √n)
Space: O(n)
```

### Skeleton

```
class Solution {
public:
    int numSquares(int n) {

        vector<int> dp(n + 1, n + 1);

        // Base case:
        // Minimum squares needed for sum 0

        for (int x = 1; x <= n; x++) {

            for (int j = 1; j * j <= x; j++) {

                int square = j * j;

                // Option:
                // Use square as the last number
                //
                // dp[x] = min(
                //     dp[x],
                //     ?
                // );
            }
        }

        return dp[n];
    }
};
```

The core recurrence is:

```
dp[0] = 0

For each amount x:

    For every perfect square j*j <= x:

        dp[x] = min(
            dp[x],
            dp[x - j*j] + 1
        )
```

Or mentally:

```
current amount x
        ↓
try every perfect square ≤ x
        ↓
look up minimum squares needed
for x - square
        ↓
add 1 for the current square
        ↓
take the minimum
        ↓
return dp[n]
```

### Connection to previous problems

| Problem                 | DP meaning                         | Transition                       |
| ----------------------- | ---------------------------------- | -------------------------------- |
| 322. Coin Change        | Minimum coins to make `x`          | `min(dp[x], dp[x - coin] + 1)`   |
| 377. Combination Sum IV | Number of ordered ways to make `x` | `dp[x] += dp[x - num]`           |
| 279. Perfect Squares    | Minimum squares to make `x`        | `min(dp[x], dp[x - square] + 1)` |

The trickiest part is realizing that Perfect Squares is essentially Coin Change where the available coins are `1, 4, 9, 16, ...`.

Once you recognize that connection, you can reuse almost the exact same DP approach from problem 322.
*/
class Solution {
public:
    int numSquares(int n) {
        vector<int> dp(n + 1, n + 1);
        dp[0] = 0;

        for(int x=1; x <= n; x++){
            for(int j=1; j * j<=x; j++){
                int square = j * j;
                dp[x] = std::min(dp[x], dp[x - square] + 1);
            }
        }
        
        return dp[n];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna