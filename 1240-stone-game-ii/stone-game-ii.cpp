// Method 1: 2D DP 
/*
# LeetCode 1140. Stone Game II — Hints

This is a Dynamic Programming + Game Theory (Minimax) + Suffix Sum + Memoization problem.

The key idea is that instead of trying to maximize the number of stones Alice takes on her current turn, you need to consider how her decision affects the number of stones her opponent can collect.

This is closely related to:

- 877\. Stone Game: Use Interval DP to calculate the maximum score advantage the current player can achieve.
- 486\. Predict the Winner: Determine whether the first player can win when both players play optimally.
- 1690\. Stone Game VII: Use optimal decisions from both players to maximize the score difference.

The major difference is that in Stone Game II, players can take multiple piles, and the maximum number of piles they can take changes throughout the game.

We'll derive the recursive approach first, then use memoization to optimize it.

### Hint 1: Understand how the game works

Suppose:

```
piles = {2, 7, 9, 4, 4};
```

Initially:

```
M = 1;
```

On each turn, the current player may take the first `X` remaining piles, where:

\\[ 1 \le X \le 2M \\]

After taking those piles, update:

```
M = max(M, X);
```

Alice plays first, and both players play optimally.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://leetcode.com\&sz=32)

LeetCode



Since initially `M = 1`, Alice can choose:

```
X = 1
or
X = 2
```

Initial piles

Index 0

## 2

Index 1

## 7

Index 2

## 9

Index 3

## 4

Index 4

## 4

Alice can take only the first pile or the first two piles. She cannot skip a pile.

Option 1: Alice takes one pile.

```
Alice takes: 2

Remaining: [7, 9, 4, 4]

New M = max(1, 1) = 1
```

Now Bob can take either `1` or `2` piles.

Option 2: Alice takes two piles.

```
Alice takes: 2 + 7 = 9

Remaining: [9, 4, 4]

New M = max(1, 2) = 2
```

Now Bob can take up to:

```
2 * M = 4 piles
```

Since only three piles remain, Bob can take all of them.

This is the first important observation:

Taking more piles now can allow your opponent to take more piles on the next turn.

### Hint 2: Why doesn't a greedy approach work?

For the same example:

```
piles = {2, 7, 9, 4, 4};
```

Alice might think:

```
I can take either:

X = 1 → 2 stones

X = 2 → 9 stones

9 > 2

So I should take 2 piles.
```

But if she chooses `X = 2`:

```
Alice takes: 2 + 7 = 9

Remaining: [9, 4, 4]

M = 2
```

Bob can take all three remaining piles:

```
Bob takes: 9 + 4 + 4 = 17
```

Alice finishes with only:

```
9
```

However, if Alice initially takes only the first pile, optimal play can proceed as:

```
Alice takes: 2

Bob takes: 7 + 9 = 16

Alice takes: 4 + 4 = 8
```

Final scores:

```
Alice = 2 + 8 = 10

Bob = 7 + 9 = 16
```

Alice gets:

```
10
```

That's better than the greedy choice of taking two piles immediately.

Notice that Alice does not need to win the game. The question asks for the maximum number of stones Alice can obtain, even if Bob finishes with more.

### Hint 3: Identify the information needed to represent a game state

In Stone Game 877, you used:

```
dp[i][j]
```

because players could remove piles from either end.

But in Stone Game II, you can only remove piles from the front of the remaining array.

So once some piles have been removed, the remaining piles always form a suffix.

For example:

```
Original:

[2, 7, 9, 4, 4]
```

If three piles have been removed:

```
Remaining:

[4, 4]
```

You can represent these remaining piles using a single index:

```
i = 3;
```

But is `i` enough to describe the state?

No.

You also need `M`, because it determines how many piles the current player may take.

Therefore, a game state needs two variables:

```
(i, M)
```

Where:

```
i = Index of the first remaining pile.

M = Current limit parameter that
    controls how many piles may be taken.
```

The next step is to decide what value your DP should store for each state.

### Hint 4: Define your DP state

Define:

```
dp[i][M]
```

to represent:

```
The maximum number of stones
the CURRENT player can collect
from piles[i ... n-1]

when the current limit parameter is M,
assuming both players play optimally.
```

This is very important.

`dp[i][M]` does not always represent Alice's stones.

It represents the maximum stones that whoever is about to play can collect.

For example:

```
dp[2][2]
```

means:

```
Remaining piles:

piles[2 ... n-1]

Current M = 2

How many stones can the player
whose turn it is collect optimally?
```

Why is this useful?

Because Alice and Bob follow the same rules.

You don't need to write separate DP transitions for Alice and Bob.

After each move, the current player changes, but the meaning of the DP state remains the same.

This is the central game-theory insight.

### Hint 5: Understand the choices from a state `(i, M)`

Suppose you're at:

```
dp[i][M]
```

You can take between `1` and `2 * M` piles, as long as enough piles remain.

So consider every possible `X`:

```
for (int X = 1; X <= 2 * M && i + X <= n; X++) {
    // Try taking X piles
}
```

If you take `X` piles, the next state becomes:

```
nextIndex = i + X;
```

And the new value of `M` becomes:

```
nextM = max(M, X);
```

Therefore, the opponent's next state is:

```
dp[i + X][max(M, X)]
```

The important observation is:

```
Current state: (i, M)
          ↓
Choose X piles
          ↓
Take piles[i ... i+X-1]
          ↓
Remaining state:
(i + X, max(M, X))
          ↓
Opponent's turn
```

Now think about how to calculate the current player's maximum stones using the opponent's DP result.

### Hint 6: Use suffix sums to calculate remaining stones

Suppose:

```
piles = {2, 7, 9, 4, 4};
```

You want to know how many stones remain starting from index `i`.

For example:

```
i = 2

Remaining piles: [9, 4, 4]

Total remaining stones = 17
```

You could calculate this by looping through the array each time.

But that would be inefficient.

Instead, use a suffix sum array.

Define:

```
suffix[i]
```

to represent:

```
The total number of stones
from piles[i] to piles[n-1].
```

The recurrence is:

```
suffix[i] = piles[i] + suffix[i + 1];
```

Initialize:

```
suffix[n] = 0;
```

And build the array backward:

```
int n = piles.size();

vector<int> suffix(n + 1, 0);

for (int i = n - 1; i >= 0; i--) {
    suffix[i] = piles[i] + suffix[i + 1];
}
```

For our example:

| Index | `piles[i]` | `suffix[i]` |
| ----- | ---------- | ----------- |
| 0     | 2          | 26          |
| 1     | 7          | 24          |
| 2     | 9          | 17          |
| 3     | 4          | 8           |
| 4     | 4          | 4           |
| 5     | —          | 0           |

So:

```
suffix[0] = 26;
suffix[1] = 24;
suffix[2] = 17;
```

This lets you calculate the remaining number of stones in `O(1)` time.

Why is this useful?

Because the current player and their opponent will eventually divide all the remaining stones between them.

That gives us a very useful relationship.

### Hint 7: Derive the Minimax recurrence

Suppose you're calculating:

```
dp[i][M]
```

And the current player chooses to take `X` piles.

After that move, the opponent starts playing from:

```
dp[i + X][max(M, X)]
```

Remember what this DP value represents:

```
The maximum number of stones
the opponent can collect
from the remaining piles.
```

Now consider the total number of stones available before the current move:

```
suffix[i]
```

All these stones will eventually belong to either the current player or the opponent.

Therefore:

\\[ \text{Current player's total} = \text{Remaining stones} - \text{Opponent's total} \\]

In code:

```
currentPlayerStones =
    suffix[i] -
    dp[i + X][max(M, X)];
```

Why subtract the opponent's result?

Because the opponent is trying to maximize their own stones, which reduces how many stones the current player can get.

The current player wants to choose whichever `X` maximizes their final total.

Therefore:

```
dp[i][M] = max(
    dp[i][M],
    suffix[i] - dp[i + X][max(M, X)]
);
```

You need to try every valid `X`.

Putting this together:

```
int best = 0;

for (int X = 1; X <= 2 * M && i + X <= n; X++) {

    int opponent = dp[i + X][max(M, X)];

    int current = suffix[i] - opponent;

    best = max(best, current);
}
```

Then:

```
dp[i][M] = best;
```

This is the core Minimax recurrence.

Notice that you don't need to separately calculate the stones taken immediately.

They are already included in `suffix[i] - opponent`.

### Hint 8: Understand the base cases

There are two important base cases.

Base case 1: No piles remain

Suppose:

```
i == n
```

There are no piles left to collect.

So:

```
return 0;
```

Base case 2: The current player can take all remaining piles

Suppose:

```
2 * M >= n - i
```

This means the current player is allowed to take all remaining piles in one turn.

For example:

```
piles = {2, 7, 9, 4, 4};

i = 2;
M = 2;
```

Remaining piles:

```
[9, 4, 4]
```

There are:

```
n - i = 3 piles
```

The current player can take up to:

```
2 * M = 4 piles
```

So they can take all three remaining piles.

Their maximum stones are:

```
suffix[2] = 17;
```

Therefore:

```
if (2 * M >= n - i) {
    return suffix[i];
}
```

This base case is an important optimization because it avoids exploring unnecessary moves.

### Hint 9: Use recursion to implement the state transitions

At this point, you have everything necessary to define a recursive function.

Think of:

```
dfs(i, M)
```

as:

```
Return the maximum number of stones
the current player can collect
starting from index i with parameter M.
```

The recursive logic is:

```
dfs(i, M)
     ↓
Can the current player take all piles?
     ↓
Yes → Return suffix[i]
     ↓
No → Try every valid X
     ↓
Opponent plays from:
dfs(i + X, max(M, X))
     ↓
Current player's stones:
suffix[i] - opponent's stones
     ↓
Choose maximum
```

The recurrence becomes:

\\[ \boxed{ dp(i,M)=\max\_{1\le X\le 2M} \left(suffix[i]-dp(i+X,\max(M,X))\right) } \\]

For states where the player can take all remaining piles, use the base case from Hint 8.

The initial state is:

```
dfs(0, 1)
```

Why?

Because:

```
i = 0 → All piles are available.

M = 1 → Initial game parameter.

Current player = Alice.
```

So:

```
return dfs(0, 1);
```

gives Alice's maximum possible number of stones.

### Hint 10: Add memoization to avoid repeated calculations

A purely recursive solution can repeatedly evaluate the same states.

For example, different sequences of moves may eventually reach:

```
dfs(3, 2)
```

Without memoization, you calculate that state again each time.

Instead, store previously computed answers.

Create:

```
vector<vector<int>> memo(
    n,
    vector<int>(n + 1, -1)
);
```

Why use `-1`?

Because all piles contain positive numbers, so any state with at least one remaining pile has a positive optimal result.

Thus, `-1` can safely indicate an uncomputed state.

Before calculating a state:

```
if (memo[i][M] != -1) {
    return memo[i][M];
}
```

After calculating the best answer:

```
memo[i][M] = best;
```

Then return it.

This converts your exponential recursive exploration into a polynomial-time dynamic programming algorithm.

### Hint 11: Walk through the example

Let's revisit:

```
piles = {2, 7, 9, 4, 4};
```

The suffix sums are:

```
suffix = {26, 24, 17, 8, 4, 0};
```

We want:

```
dfs(0, 1);
```

Alice has two choices.

Option 1: Take one pile

```
X = 1

Alice takes: 2

Next state: dfs(1, 1)
```

Bob can optimally collect:

```
dfs(1, 1) = 16;
```

Therefore:

```
Alice's total = suffix[0] - 16;
              = 26 - 16;
              = 10;
```

Option 2: Take two piles

```
X = 2

Alice takes: 2 + 7 = 9

Next state: dfs(2, 2)
```

Bob can take all remaining piles:

```
dfs(2, 2) = 17;
```

Therefore:

```
Alice's total = suffix[0] - 17;
              = 26 - 17;
              = 9;
```

Choose the maximum:

```
dfs(0, 1) = max(10, 9);
          = 10;
```

So:

```
return 10;
```

#### Interactive DP walkthrough

Initialize suffix sums

Step 1 of 5

DP calculation

suffix = [26, 24, 17, 8, 4, 0]

Result

## 26 stones in total

Precompute the stones remaining from every index. These totals allow us to subtract the opponent's optimal score.

&#x20;PreviousNext step&#x20;

The important pattern is that each state uses the optimal answers of smaller remaining-game states.

### Hint 12: Why don't you need to track whose turn it is?

You might initially consider:

```
dp[i][M][turn]
```

Where:

```
turn = 0 → Alice

turn = 1 → Bob
```

This is a valid approach.

But it isn't necessary.

Remember our definition:

```
dp[i][M]
```

means:

```
Maximum stones the CURRENT player
can collect from this state.
```

Since both players are trying to maximize their own total, the recurrence is symmetric.

After the current player chooses `X`, the recursive call automatically evaluates the opponent's best possible outcome.

You then subtract it:

```
suffix[i] - dfs(i + X, max(M, X))
```

This automatically accounts for the opponent's optimal strategy.

So the player identity doesn't need to be part of the DP state.

This technique is useful in many two-player zero-sum games.

### Hint 13: Analyze the complexity

Let:

```
n = piles.size();
```

Your memoized state is:

```
(i, M)
```

There are at most `O(n)` possibilities for `i` and `O(n)` possibilities for `M`.

Therefore, you have at most:

```
O(n²) DP states
```

For each state, you may try:

```
X = 1, 2, 3, ..., 2 * M
```

In the worst case, there can be `O(n)` possible moves.

So:

```
Time per state: O(n)

Number of states: O(n²)
```

Therefore:

\\[ \boxed{\text{Time: } O(n^3)} \\]

For space, you store the memoization table:

```
vector<vector<int>> memo(n, vector<int>(n + 1, -1));
```

This requires:

\\[ \boxed{\text{Space: } O(n^2)} \\]

The recursion stack uses an additional `O(n)` space, which doesn't change the overall bound.

### Hint 14: Compare Stone Game II with Stone Game I

Recall the solution to LeetCode 877.

You defined:

```
dp[i][j]
```

as the maximum score difference the current player could achieve over their opponent.

The recurrence was:

```
dp[i][j] = max(
    piles[i] - dp[i + 1][j],
    piles[j] - dp[i][j - 1]
);
```

But in Stone Game II, the player can remove several piles from the front, so the recurrence changes.

Instead of choosing between two endpoints, you try all valid `X` values.

Instead of tracking the score difference, you can track the maximum stones the current player collects.

| Property              | 877. Stone Game            | 1140. Stone Game II                   |
| --------------------- | -------------------------- | ------------------------------------- |
| Available moves       | Leftmost or rightmost pile | First `X` remaining piles             |
| DP state              | `dp[i][j]`                 | `dp[i][M]`                            |
| DP meaning            | Maximum score difference   | Maximum stones current player can get |
| Next state            | Shorter interval           | Remaining suffix with updated `M`     |
| Choices per state     | 2                          | Up to `2M`                            |
| Typical DP complexity | O(n²)                      | O(n³)                                 |

The important connection is that both problems involve optimal play from the opponent.

In Stone Game I:

```
piles[i] - opponentAdvantage
```

In Stone Game II:

```
remainingStones - opponentMaximum
```

Both use subtraction to account for the opponent playing optimally.

### Skeleton — Top-Down DP + Memoization (C++)

Try filling in the missing expressions yourself.

```
class Solution {
public:
    int n;

    vector<int> suffix;
    vector<vector<int>> memo;

    int dfs(int i, int M) {

        // Base case 1:
        // No piles remain
        if ( ? ) {
            return 0;
        }

        // Base case 2:
        // Current player can take all remaining piles
        if ( ? ) {
            return suffix[i];
        }

        // Check memoization
        if (memo[i][M] != -1) {
            return memo[i][M];
        }

        int best = 0;

        // Try all possible numbers of piles
        for (int X = 1; X <=  ? ; X++) {

            // Next remaining pile index
            int nextIndex =  ? ;

            // Update the M parameter
            int nextM =  ? ;

            // Opponent plays optimally
            int opponent = dfs(nextIndex, nextM);

            // Current player gets all remaining
            // stones minus opponent's stones
            int current =  ? ;

            best = max(best, current);
        }

        // Store and return
        return memo[i][M] = best;
    }

    int stoneGameII(vector<int>& piles) {

        n = piles.size();

        // Build suffix sum array
        suffix.assign(n + 1, 0);

        for (int i = n - 1; i >= 0; i--) {
            suffix[i] =  ? ;
        }

        // Initialize memoization table
        memo.assign(
            n,
            vector<int>(n + 1, -1)
        );

        // Alice starts from index 0 with M = 1
        return dfs( ? ,  ? );
    }
};
```

Try implementing the solution using this skeleton before looking at the recurrence summary below.

### Core recurrence

Define:

```
dp[i][M] = Maximum stones the current player
           can collect optimally
           starting at pile i with parameter M
```

Precompute suffix sums:

```
suffix[i] = piles[i] + suffix[i + 1];
```

Base cases:

```
if (i == n) {
    return 0;
}

if (2 * M >= n - i) {
    return suffix[i];
}
```

Transition:

```
int best = 0;

for (int X = 1; X <= 2 * M; X++) {

    int nextM = max(M, X);

    int opponent = dfs(i + X, nextM);

    int current = suffix[i] - opponent;

    best = max(best, current);
}
```

This transition assumes the base case has already handled situations where the current player can take every remaining pile.

The final answer is:

```
dfs(0, 1)
```

Or mentally:

```
Given piles
      ↓
Build suffix sums
      ↓
Define state:
dp[i][M]
      ↓
Maximum stones CURRENT player
can collect from index i
      ↓
Can take all remaining piles?
      ↓
YES → Return suffix[i]
      ↓
NO → Try X = 1 ... 2M
      ↓
Update M = max(M, X)
      ↓
Opponent receives:
dfs(i + X, max(M, X))
      ↓
Current player receives:
suffix[i] - opponent
      ↓
Maximize over all X
      ↓
Memoize result
      ↓
Return dfs(0, 1)
```

### Connection to previous DP problems

| Problem                 | DP meaning                                              | Goal                                       |
| ----------------------- | ------------------------------------------------------- | ------------------------------------------ |
| 486. Predict the Winner | Best score difference from remaining interval           | Determine if first player can avoid losing |
| 877. Stone Game         | Best score difference from remaining interval           | Determine if Alice wins                    |
| 1140. Stone Game II     | Maximum stones current player can collect from `(i, M)` | Maximize Alice's total                     |
| 1690. Stone Game VII    | Best score difference from remaining interval           | Maximize score advantage                   |
| 1406. Stone Game III    | Best score difference from current index                | Determine winner                           |

The most useful connection is with LeetCode 877. Stone Game.

Both problems use the same general game-theory idea:

```
My optimal outcome
depends on
my opponent's optimal outcome.
```

But Stone Game II introduces a dynamically changing parameter `M`, making the state more complex.

The three most important rules to remember are:

```
Rule 1:
Players take piles from the front.
→ Track the starting index i.

Rule 2:
The number of piles you can take changes.
→ Include M in the DP state.

Rule 3:
Both players play optimally.
→ Subtract the opponent's optimal result
  from the total remaining stones.
```

The biggest takeaway: LeetCode 1140 teaches you how to model a two-player optimal game using DP from the current player's perspective. By defining `dp[i][M]` as the maximum stones the current player can collect, you avoid tracking Alice and Bob separately. Suffix sums make the recurrence simple, and memoization avoids solving the same game state repeatedly.
*/
class Solution {
private:
    vector<int> suffix;
    vector<vector<int>> memo;
    int n;

    int dfs(int i, int M){
        if(i == n){
            return 0;
        }

        if(2 * M >= n-i){
            return suffix[i];
        }

        if(memo[i][M] != -1){
            return memo[i][M];
        }

        int best = 0;

        for(int X=1; X <= 2 * M; X++){
            int nextIndex = i + X;

            int nextM = std::max(M, X);

            int opponent = dfs(nextIndex, nextM);

            int current = suffix[i] - opponent;

            best = std::max(best, current);
        }

        memo[i][M] = best;

        return memo[i][M];
    }

public:
    int stoneGameII(vector<int>& piles) {
        n = piles.size();

        suffix.assign(n + 1, 0);

        for(int i=n-1; i >= 0; i--){
            suffix[i] = piles[i] + suffix[i + 1];
        }

        memo.assign(n, vector<int>(n + 1, -1));

        return dfs(0 , 1);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna