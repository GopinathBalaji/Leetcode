// Method 1: 1D DP + Minimax problem
/*
This is a Dynamic Programming + Game Theory (Minimax) problem.

The key idea is that Alice and Bob both play optimally, and on every turn, a player can take 1, 2, or 3 stones from the beginning of the remaining array.

Instead of tracking Alice's score and Bob's score separately, track the maximum score difference the current player can achieve over their opponent.

That makes the DP much simpler.

### Hint 1: Understand the choices at each turn

Suppose:

```
stoneValue = {1, 2, 3, 7};
```

Alice can choose:

```
Take 1 stone:
Alice gets 1
Remaining: {2, 3, 7}

Take 2 stones:
Alice gets 1 + 2 = 3
Remaining: {3, 7}

Take 3 stones:
Alice gets 1 + 2 + 3 = 6
Remaining: {7}
```

After Alice's move, Bob gets to choose from the remaining stones.

The problem is that Bob is also trying to maximize his own score.

So you cannot simply choose the move that gives Alice the most points immediately.

### Hint 2: Define your DP state using score difference

This is the most important conceptual step.

Let:

```
dp[i]
```

mean:

```
the maximum score difference
(current player - opponent)

that the current player can achieve
starting from stone i
```

Notice that `dp[i]` does not specifically represent Alice's score.

It represents the advantage of whichever player's turn it is at index `i`.

For example:

```
dp[i] = 5
```

means the current player can finish the remaining game with 5 more points than their opponent.

And:

```
dp[i] = -3
```

means the current player will finish 3 points behind, assuming both play optimally.

Your goal is:

```
dp[0]
```

because Alice plays first.

### Hint 3: Think about what happens when you take one stone

Suppose you're calculating:

```
dp[i]
```

If the current player takes one stone:

```
stoneValue[i]
```

then the opponent gets to play starting at:

```
i + 1
```

The opponent's optimal advantage from there is:

```
dp[i + 1]
```

Since that advantage belongs to your opponent, you need to subtract it.

Therefore:

```
stoneValue[i] - dp[i + 1]
```

is the score difference you can achieve by taking one stone.

Mental model:

```
my points this turn
-
opponent's future advantage
=
my final advantage
```

### Hint 4: What if you take two stones?

If you take two stones, you gain:

```
stoneValue[i] + stoneValue[i + 1]
```

Then your opponent starts at:

```
i + 2
```

So your score difference becomes:

```
stoneValue[i]
+ stoneValue[i + 1]
- dp[i + 2]
```

Similarly, taking three stones gives:

```
stoneValue[i]
+ stoneValue[i + 1]
+ stoneValue[i + 2]
- dp[i + 3]
```

Of course, you can only take two or three stones if enough stones remain.

### Hint 5: Build the recurrence

At every index `i`, try all three possible choices:

```
Option 1: Take 1 stone

stoneValue[i] - dp[i + 1]
```

```
Option 2: Take 2 stones

stoneValue[i] + stoneValue[i + 1] - dp[i + 2]
```

```
Option 3: Take 3 stones

stoneValue[i] + stoneValue[i + 1] + stoneValue[i + 2] - dp[i + 3]
```

Since the current player plays optimally, choose the maximum.

Conceptually:

```
dp[i] = max({
    take1,
    take2,
    take3
});
```

You should only consider moves that stay within the array.

### Hint 6: Use a running sum to avoid repeating code

Instead of writing separate logic for taking one, two, and three stones, you can use a loop:

```
int sum = 0;

for (int k = 1; k <= 3; k++) {

    // Take k stones

}
```

Each time, add the next stone:

```
sum += stoneValue[i + k - 1];
```

Now:

```
k = 1 → sum of first 1 stone
k = 2 → sum of first 2 stones
k = 3 → sum of first 3 stones
```

The corresponding opponent starts at:

```
i + k
```

So your candidate is:

```
sum - dp[i + k]
```

Update:

```
dp[i] = max(dp[i], sum - dp[i + k]);
```

This gives you all three options with one loop.

### Hint 7: What is the base case?

When no stones remain, neither player can gain additional points.

So:

```
dp[n] = 0;
```

where:

```
int n = stoneValue.size();
```

A convenient initialization is:

```
vector<int> dp(n + 1, 0);
```

But there's an important detail: when calculating each nonempty state, initialize:

```
dp[i] = INT_MIN;
```

Why?

Because stone values can be negative.

For example:

```
stoneValue = {-5};
```

The only player must take the stone and gets `-5`.

So:

```
dp[0] = -5;
```

If you initialize `dp[0] = 0` and only take the maximum, you'll incorrectly get `0`.

### Hint 8: Build the DP array backward

This is different from many of the DP problems you've recently solved.

Your recurrence uses:

```
dp[i + 1]
dp[i + 2]
dp[i + 3]
```

So you need to calculate the states from right to left.

Start from:

```
i = n - 1
```

and move toward:

```
i = 0
```

Conceptually:

```
for (int i = n - 1; i >= 0; i--) {
    // calculate dp[i]
}
```

Why?

Because when you calculate:

```
dp[i]
```

all the future states are already known.

### Hint 9: Walk through an example

Suppose:

```
stoneValue = {1, 2, 3, 7};
```

We calculate from right to left.

DP walkthrough

Step 5 of 5

i=0

1

dp

### -1

i=1

2

dp

### 12

i=2

3

dp

### 10

i=3

7

dp

### 7

i=4

End

dp

### 0

Take 1: −11. Take 2: −7. Take 3: −1. The best possible difference is −1.

&#x20;PreviousNext&#x20;

At the end:

```
dp = {-1, 12, 10, 7, 0};
```

So:

```
dp[0] = -1;
```

This means Alice loses by one point if both players play optimally.

The answer is:

```
"Bob"
```

### Hint 10: How do you decide who wins?

Once you've calculated:

```
dp[0]
```

the problem becomes very simple.

If:

```
dp[0] > 0
```

Alice can finish with more points than Bob.

Return:

```
"Alice"
```

If:

```
dp[0] < 0
```

Bob will finish with more points.

Return:

```
"Bob"
```

Otherwise:

```
dp[0] == 0
```

The game ends in a tie.

Return:

```
"Tie"
```

You don't need to calculate Alice's and Bob's actual scores separately.

### Hint 11: Why greedy doesn't work

Consider:

```
stoneValue = {1, 2, 3, -9};
```

A greedy player might avoid thinking about the opponent's future choices.

But Alice can take the first three stones:

```
1 + 2 + 3 = 6
```

leaving Bob:

```
-9
```

So Alice wins.

Notice that the important decision isn't just how many points Alice gains immediately.

It's also what remains for Bob.

That's why your DP recurrence subtracts the opponent's best future advantage.

### Hint 12: Complexity

For each index `i`, you consider at most three choices.

So:

```
Time: O(3n) = O(n)
```

You store a DP array of size:

```
n + 1
```

Therefore:

```
Space: O(n)
```

You can eventually optimize the space to `O(1)` because each state only depends on the next three states, but I recommend implementing the array version first.

### Skeleton

```
class Solution {
public:
    string stoneGameIII(vector<int>& stoneValue) {
        int n = stoneValue.size();

        vector<int> dp(n + 1, 0);

        // Base case:
        // dp[n] = 0

        for (int i = n - 1; i >= 0; i--) {

            dp[i] = INT_MIN;

            int sum = 0;

            // Try taking 1, 2, or 3 stones
            for (int k = 1; k <= 3 && i + k <= n; k++) {

                // Add the next stone to sum

                // Opponent starts at i + k

                // Candidate:
                // stones taken - opponent's advantage

                // Update dp[i] with the maximum
            }
        }

        // dp[0] > 0 → Alice
        // dp[0] < 0 → Bob
        // dp[0] == 0 → Tie
    }
};
```

The core recurrence is:

\\[ dp[i] = \max\_{1\le k\le 3,\ i+k\le n} \left( \sum\_{j=i}^{i+k-1} stoneValue[j] - dp[i+k] \right) \\]

Or mentally:

```
current position i
        ↓
try taking 1, 2, or 3 stones
        ↓
calculate sum of stones taken
        ↓
opponent plays optimally
from position i + k
        ↓
my advantage =
sum - dp[i + k]
        ↓
take the maximum advantage
        ↓
use dp[0] to determine winner
```

### Connection to previous DP problems

| Problem              | DP meaning                            | Transition                          |
| -------------------- | ------------------------------------- | ----------------------------------- |
| 198. House Robber    | Maximum money obtainable              | `max(skip, take)`                   |
| 343. Integer Break   | Maximum product after splitting       | `max(stop, split further)`          |
| 1406. Stone Game III | Maximum score advantage over opponent | `max(takenSum - opponentAdvantage)` |

The trickiest conceptual part is understanding why you subtract `dp[i + k]`.

The DP value represents the advantage of the player whose turn it is. After your move, that player becomes your opponent, so their advantage is a disadvantage for you.

Once that clicks, the entire solution becomes a straightforward right-to-left DP with three choices per position.
*/
class Solution {
public:
    string stoneGameIII(vector<int>& stoneValue) {
        int n = stoneValue.size();

        vector<int> dp(n + 1, 0);
        dp[n] = 0;

        for(int i = n-1; i >= 0; i--){
            dp[i] = INT_MIN;

            int sum = 0;

            for(int k=1; k <= 3 && i + k <= n; k++){
                sum += stoneValue[i + k - 1];

                dp[i] = std::max(dp[i], sum - dp[i + k]);
            }
        }

        if(dp[0] > 0){
            return "Alice";
        }else if(dp[0] < 0){
            return "Bob";
        }

        return "Tie";
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna