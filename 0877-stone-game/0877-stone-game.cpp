// Method 1: 2D DP
/*
# LeetCode 877. Stone Game — Hints

This is a Dynamic Programming + Interval DP + Game Theory (Minimax) problem.

The key idea is that instead of greedily choosing the pile with the most stones, you need to consider how your decision affects the choices available to your opponent.

This problem is closely related to:

- 486\. Predict the Winner: Determine whether the first player can win when both players choose numbers from either end.
- 1140\. Stone Game II: Find the maximum stones Alice can collect when both players play optimally.
- 1690\. Stone Game VII: Maximize the score difference when removing stones from the ends.

However, LeetCode 877 has a special mathematical property that allows an even simpler solution than DP.

We'll first derive the general Interval DP solution, because it's useful for many two-player game problems, and then explore the mathematical shortcut.

### Hint 1: Understand why a greedy approach doesn't work

Suppose:

```
piles = {8, 15, 3, 7};
```

Alice and Bob take turns choosing either the leftmost or rightmost pile.

Alice goes first.

If Alice greedily chooses the larger endpoint, she picks:

```
Left = 8
Right = 7

Choose 8
```

But consider the resulting game:

```
Initial: [8, 15, 3, 7]

Alice takes 8
Remaining: [15, 3, 7]

Bob takes 15
Remaining: [3, 7]

Alice takes 7
Bob takes 3
```

Final scores:

```
Alice = 8 + 7  = 15
Bob   = 15 + 3 = 18
```

Alice loses.

But what if Alice chooses `7` first?

She can guarantee obtaining both `7` and `15`:

```
Alice = 7 + 15 = 22
Bob   = 8 + 3  = 11
```

Alice wins.

Important observation: Choosing the larger pile right now doesn't necessarily maximize your final score.

You must consider what your opponent can do afterward.

### Hint 2: Think about what both players are optimizing

Alice wants to maximize the number of stones she collects.

Bob also wants to maximize the number of stones he collects.

But there's a useful way to simplify the problem.

Instead of tracking both scores independently, track their difference.

Suppose:

```
Alice collects 22 stones
Bob collects 11 stones
```

Then Alice's score advantage is:

```
22 - 11 = 11
```

If this difference is positive, Alice wins.

If it's negative, Bob wins.

Now think about any moment in the game.

Rather than labeling the players as Alice and Bob, consider:

```
Current player
vs.
Other player
```

Define the score difference as:

```
Score of current player
-
Score of other player
```

This viewpoint is useful because after the current player chooses a pile, the roles switch.

The opponent becomes the current player in the next subproblem.

That's the foundation of the DP recurrence.

### Hint 3: Define your DP state

Instead of storing the maximum number of stones Alice can collect, define:

```
dp[i][j]
```

to represent:

```
Maximum score difference that the
CURRENT player can achieve over the
OTHER player

when the remaining piles are
piles[i ... j].
```

For example:

```
dp[1][3]
```

represents the optimal score difference for the player whose turn it is when the remaining piles are:

```
piles[1], piles[2], piles[3]
```

The important distinction is that `dp[i][j]` doesn't always represent Alice's advantage.

It represents the advantage of whoever is about to play.

This lets you use the same recurrence for both Alice and Bob.

### Hint 4: Derive the first transition — Take the left pile

Suppose you're calculating:

```
dp[i][j]
```

The current player can choose the leftmost pile:

```
piles[i]
```

After taking it, the remaining interval is:

```
piles[i+1 ... j]
```

Now it's the opponent's turn.

By definition:

```
dp[i + 1][j]
```

is the maximum advantage the opponent can achieve over the current player on that remaining interval.

Since you gained `piles[i]` stones, but the opponent can achieve that advantage afterward, your net advantage is:

```
piles[i] - dp[i + 1][j]
```

This subtraction is crucial.

Why subtract?

Because after your move, the roles reverse. What is an advantage for your opponent is a disadvantage for you.

So the first choice is:

```
takeLeft = piles[i] - dp[i + 1][j];
```

### Hint 5: Derive the second transition — Take the right pile

Alternatively, the current player can take:

```
piles[j]
```

After taking the rightmost pile, the remaining interval becomes:

```
piles[i ... j-1]
```

The opponent then plays optimally.

Their maximum advantage is:

```
dp[i][j - 1]
```

Therefore, the current player's net advantage is:

```
takeRight = piles[j] - dp[i][j - 1];
```

The current player wants to maximize their score difference.

So choose whichever option gives a larger advantage.

The core recurrence is:

```
dp[i][j] = max(
    piles[i] - dp[i + 1][j],
    piles[j] - dp[i][j - 1]
);
```

This is the most important DP recurrence in the problem.

Mental model:

```
             dp[i][j]
                 |
          Choose a pile
          /           \
     Take LEFT     Take RIGHT
         |              |
     piles[i]        piles[j]
         |              |
      Opponent       Opponent
      plays on       plays on
      [i+1, j]       [i, j-1]
         |              |
  piles[i] -      piles[j] -
  dp[i+1][j]      dp[i][j-1]
          \          /
           Take MAX
```

### Hint 6: Initialize the base case

Consider the simplest possible interval:

```
dp[i][i]
```

This means only one pile remains.

For example:

```
piles = {5};
```

The current player has no choice.

They take the only remaining pile and collect `5` stones.

The opponent receives `0` stones.

So the score difference is:

```
5 - 0 = 5
```

Therefore:

```
dp[i][i] = piles[i];
```

This is the base case.

Initialize your DP table:

```
int n = piles.size();

vector<vector<int>> dp(
    n,
    vector<int>(n, 0)
);

for (int i = 0; i < n; i++) {
    dp[i][i] = piles[i];
}
```

Now you can start calculating answers for longer intervals.

### Hint 7: Fill the DP table by increasing interval length

Look at the recurrence:

```
dp[i][j] = max(
    piles[i] - dp[i + 1][j],
    piles[j] - dp[i][j - 1]
);
```

Notice that:

```
dp[i + 1][j]
```

represents an interval with one fewer pile.

Similarly:

```
dp[i][j - 1]
```

also represents an interval with one fewer pile.

Therefore, you must solve smaller intervals before larger ones.

A natural order is:

```
Length 1 → Single piles

Length 2 → Two adjacent piles

Length 3 → Three adjacent piles

...

Length n → Entire array
```

You can iterate like this:

```
for (int len = 2; len <= n; len++) {
    for (int i = 0; i + len <= n; i++) {

        int j = i + len - 1;

        // Calculate dp[i][j]
    }
}
```

Why:

```
j = i + len - 1;
```

Because an interval starting at `i` with `len` elements ends at index `i + len - 1`.

### Hint 8: Walk through an example

Consider:

```
piles = {5, 3, 4, 5};
```

The expected answer is:

```
true
```

Let's calculate the optimal score differences.

Initialize the diagonal:

```
dp[0][0] = 5
dp[1][1] = 3
dp[2][2] = 4
dp[3][3] = 5
```

Now consider an interval of length `2`:

```
dp[0][1]
```

The two piles are:

```
[5, 3]
```

You can take the left pile:

```
5 - dp[1][1]
= 5 - 3
= 2
```

Or take the right pile:

```
3 - dp[0][0]
= 3 - 5
= -2
```

Choose the maximum:

```
dp[0][1] = max(2, -2);
             // = 2
```

Continue calculating the intervals.

Final DP table for `piles = {5, 3, 4, 5}`

| `dp[i][j]` | j=0 | j=1 | j=2 | j=3 |
| ---------- | --- | --- | --- | --- |
| i=0        | 5   | 2   | 4   | 1   |
| i=1        | —   | 3   | 1   | 4   |
| i=2        | —   | —   | 4   | 1   |
| i=3        | —   | —   | —   | 5   |

Let's examine how the table fills:

Initialize base cases

Step 1 of 4

i / j

0

1

2

3

0

5

—

—

—

1

—

3

—

—

2

—

—

4

—

3

—

—

—

5

Only one pile remains in each interval. The current player receives its entire value.

&#x20;PreviousNext step&#x20;

For the entire game:

```
dp[0][3] = 1;
```

That means the first player can guarantee a score difference of `1`.

So Alice wins.

### Hint 9: Determine the final answer

After calculating all intervals, look at:

```
dp[0][n - 1]
```

This represents:

```
Maximum score difference
the first player can guarantee
over the second player

when all piles are available.
```

Because Alice plays first, a positive difference means she wins.

Therefore:

```
return dp[0][n - 1] > 0;
```

Notice we're checking:

```
> 0
```

rather than:

```
>= 0
```

because Alice must collect strictly more stones than Bob to win.

Under LeetCode 877's constraints, the total number of stones is odd, so a tie cannot occur.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://leetcode.com\&sz=32)

LeetCode



### Hint 10: Can you optimize the DP space?

Look at the recurrence again:

```
dp[i][j] = max(
    piles[i] - dp[i + 1][j],
    piles[j] - dp[i][j - 1]
);
```

You need two previously calculated states:

```
dp[i + 1][j]

dp[i][j - 1]
```

You don't necessarily need to store the entire 2D table.

Try imagining a 1D array:

```
vector<int> dp(n);
```

If you process:

```
i from n - 1 down to 0
```

and:

```
j from i + 1 up to n - 1
```

then before updating `dp[j]`:

```
dp[j]     represents dp[i + 1][j]

dp[j - 1] represents dp[i][j - 1]
```

That allows you to reduce space from:

```
O(n²)
```

to:

```
O(n)
```

while keeping the same time complexity.

This is useful practice for 2D Interval DP problems.

### Hint 11: Notice something special about the constraints

Now comes the most interesting observation.

Read the problem constraints carefully.

LeetCode guarantees that:

```
1. The number of piles is EVEN.

2. Each pile contains a positive number of stones.

3. The total number of stones is ODD.
```

[image](https://www.google.com/s2/favicons?domain=https://leetcode.com\&sz=32)

LeetCode

Why might the number of piles being even matter?

Suppose:

```
piles = {5, 3, 4, 5};
```

Label the original indices:

```
Index:    0   1   2   3

Piles:    5   3   4   5
```

Divide the piles into two groups based on their original indices.

Index 0

## 5

Even

Index 1

## 3

Odd

Index 2

## 4

Even

Index 3

## 5

Odd

Even indices (0, 2)

# 5 + 4 = 9

Odd indices (1, 3)

# 3 + 5 = 8

The sum of piles at even indices is:

```
9
```

The sum of piles at odd indices is:

```
8
```

What if Alice could guarantee taking every pile from whichever group has the larger total?

She would win.

The key question is:

Can Alice force herself to take either all even-indexed piles or all odd-indexed piles?

Think about the endpoints of a row containing an even number of piles.

### Hint 12: The parity strategy

Suppose there are an even number of remaining piles.

For example:

```
Index:  0  1  2  3  4  5

Piles:  A  B  C  D  E  F
```

Notice the two endpoints:

```
Left endpoint:  Index 0 → Even

Right endpoint: Index 5 → Odd
```

The endpoints have opposite index parity.

This means Alice can always choose either an even-indexed pile or an odd-indexed pile on her first turn.

Now Bob makes a move.

After Alice and Bob have both taken one pile, an even number of piles remains.

Once again, the two endpoints have opposite original index parity.

So on Alice's next turn, she can again choose whichever parity she wants.

This continues throughout the game.

Therefore:

Alice can guarantee collecting every pile from either the even-indexed group or the odd-indexed group.

She simply chooses the group with the larger total.

### Hint 13: Why does Alice always win?

Let:

```
E = sum of even-indexed piles

O = sum of odd-indexed piles
```

All piles belong to exactly one of these groups:

\\[ E+O=total \\]

The total is guaranteed to be odd.

Therefore:

\\[ E\ne O \\]

One group must have a strictly larger total.

So:

\\[ \max(E,O)>\frac{total}{2} \\]

Because Alice can force herself to collect whichever parity group has the larger sum, she can guarantee more than half of all stones.

That means Alice always wins, regardless of how Bob plays.

This is the mathematical shortcut specific to LeetCode 877.

You don't actually need dynamic programming to solve this particular problem.

### Hint 14: Complexity comparison

| Approach                      | Time        | Space           |
| ----------------------------- | ----------- | --------------- |
| Recursion without memoization | Exponential | O(n) call stack |
| Recursion + memoization       | O(n²)       | O(n²)           |
| 2D Interval DP                | O(n²)       | O(n²)           |
| 1D Interval DP                | O(n²)       | O(n)            |
| Mathematical observation      | O(1)        | O(1)            |

The mathematical solution works because of the specific constraints. If you removed the even-number-of-piles guarantee, the outcome would no longer always be a win for Alice.

For learning game theory and Interval DP, it's still worth understanding the general recurrence.

### Skeleton — 2D Interval DP (C++)

Try filling in the missing expressions yourself.

```
class Solution {
public:
    bool stoneGame(vector<int>& piles) {

        int n = piles.size();

        // dp[i][j] = Maximum score difference
        // current player can achieve over opponent
        // using piles[i ... j]

        vector<vector<int>> dp(
            n,
            vector<int>(n, 0)
        );

        // Base case:
        // Only one pile remains
        for (int i = 0; i < n; i++) {
            dp[i][i] =  ? ;
        }

        // Process intervals in increasing length
        for (int len = 2; len <= n; len++) {

            for (int i = 0; i + len <= n; i++) {

                int j = i + len - 1;

                // Option 1: Take leftmost pile
                int takeLeft =  ? ;

                // Option 2: Take rightmost pile
                int takeRight =  ? ;

                // Current player chooses
                // the larger score advantage
                dp[i][j] =  ? ;
            }
        }

        // Can Alice guarantee a positive
        // score difference?

        return  ? ;
    }
};
```

### Core recurrence

Define:

```
dp[i][j] = maximum score advantage
           the current player can achieve
           over the opponent
           using piles[i ... j]
```

Base case:

```
dp[i][i] = piles[i];
```

Transition:

```
dp[i][j] = max(
    piles[i] - dp[i + 1][j],
    piles[j] - dp[i][j - 1]
);
```

Final answer:

```
return dp[0][n - 1] > 0;
```

Or mentally:

```
Given piles
     ↓
Define Interval DP
     ↓
dp[i][j] = Maximum score difference
     ↓
Initialize single-pile intervals
     ↓
Process intervals of increasing length
     ↓
For each interval:
     ↓
Option 1: Take LEFT pile
Option 2: Take RIGHT pile
     ↓
Subtract opponent's optimal advantage
     ↓
Choose maximum score difference
     ↓
Return dp[0][n-1] > 0
```

### Connection to previous DP problems

| Problem                 | DP meaning                                                       | Goal                                       |
| ----------------------- | ---------------------------------------------------------------- | ------------------------------------------ |
| 486. Predict the Winner | Maximum score difference on an interval                          | Determine if first player can avoid losing |
| 877. Stone Game         | Maximum score difference on an interval                          | Determine if Alice wins                    |
| 1140. Stone Game II     | Maximum stones achievable from a position and allowed move range | Maximize stones                            |
| 1690. Stone Game VII    | Maximum score difference on an interval                          | Maximize score advantage                   |
| 312. Burst Balloons     | Maximum coins achievable for an interval                         | Maximize total reward                      |

The most useful connection is with LeetCode 486. Predict the Winner.

Both problems can use exactly the same Interval DP recurrence:

```
dp[i][j] = max(
    nums[i] - dp[i + 1][j],
    nums[j] - dp[i][j - 1]
);
```

But LeetCode 877 has additional constraints that guarantee the first player wins.

The three most important rules to remember are:

```
Rule 1:
Two players are competing optimally.
→ Track SCORE DIFFERENCE.

Rule 2:
Players choose from either end.
→ Use INTERVAL DP.

Rule 3:
There are an even number of piles,
and the total number of stones is odd.
→ The first player can force a win
  using INDEX PARITY.
```

The biggest takeaway: LeetCode 877 teaches two valuable ideas. The generalizable one is how to represent an optimal two-player game using the current player's score advantage, with the opponent's advantage subtracted at each transition. The special insight is recognizing that the even number of piles allows Alice to control index parity, making a full DP solution unnecessary.
*/
class Solution {
public:
    bool stoneGame(vector<int>& piles) {
        int n = piles.size();

        vector<vector<int>> dp(n, vector<int>(n, 0));

        for(int i=0; i<n; i++){
            dp[i][i] = piles[i];
        }

        for(int len=2; len <= n; len++){
            for(int i=0; i + len <= n; i++){
                int j = i + len - 1;

                int takeLeft = piles[i] - dp[i + 1][j];

                int takeRight = piles[j] - dp[i][j - 1];

                dp[i][j] = std::max(takeLeft, takeRight);
            }
        }

        return dp[0][n - 1] > 0 ? true : false;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna