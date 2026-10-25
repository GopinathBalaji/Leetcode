// Method 1: 2D DP + DFS
/*
# LeetCode 329. Longest Increasing Path in a Matrix — Hints

This is a Dynamic Programming + DFS (Depth-First Search) + Memoization + Graph Theory problem.

The key idea is that instead of trying to find every possible increasing path in the matrix, you can calculate the longest increasing path starting from each cell and reuse those results.

This is closely related to:

- 79\. Word Search: Explore neighboring cells using DFS.
- 62\. Unique Paths: Use DP to solve a matrix path problem.
- 300\. Longest Increasing Subsequence: Find the longest strictly increasing sequence.
- 1140\. Stone Game II: Use top-down DP with memoization to avoid recomputing states.

The major difference is that you can start from any cell and move in four directions, but every next cell must have a strictly greater value.

We'll derive the DFS solution first, then add memoization to optimize it.

### Hint 1: Understand what makes a path valid

Suppose:

```
matrix = {
    {9, 9, 4},
    {6, 6, 8},
    {2, 1, 1}
};
```

You want to find the length of the longest strictly increasing path.

One valid path is:

```
1 → 2 → 6 → 9
```

Visualize the path:

## 9

Step 4

## 9

—

## 4

—

## 6

Step 3

## 6

—

## 8

—

## 2

Step 2

## 1

Step 1

## 1

—

The highlighted cells form the path 1 → 2 → 6 → 9, moving up or left each time.

The path has four cells, so:

```
answer = 4;
```

There are three important rules:

1. You can move up, down, left, or right.
2. You cannot move diagonally or outside the matrix.
3. The next cell must be strictly greater than the current cell.&#x20;

   [image](https://www.google.com/s2/favicons?domain=https://leetcode.com\&sz=32)

   LeetCode

   +1



For example:

```
1 → 2 → 6 → 9   Valid

1 → 6 → 6       Invalid (equal values)

2 → 1 → 8       Invalid (decreasing step)
```

Important observation: You're maximizing the number of cells in a valid path, not the sum of their values.

### Hint 2: Why might a simple DFS be inefficient?

A natural first approach is to start DFS from every cell.

For example:

```
for (int i = 0; i < rows; i++) {
    for (int j = 0; j < cols; j++) {

        // Find the longest increasing
        // path starting at (i, j)
    }
}
```

From each cell, explore all four directions and continue only when the next cell is larger.

This can find the correct answer.

But consider:

```
matrix = {
    {1, 2, 3},
    {2, 3, 4},
    {3, 4, 5}
};
```

Many paths eventually reach the same cells.

For example:

```
Path A: 1 → 2 → 3 → 4 → 5

Path B: 2 → 3 → 4 → 5

Path C: 3 → 4 → 5
```

When exploring these paths separately, your DFS may repeatedly calculate the longest increasing path starting from a cell with value `4`.

That's unnecessary.

Once you've calculated the longest increasing path starting at a particular cell, the answer will always be the same.

This suggests memoization.

### Hint 3: Define your DP state

Instead of using a DP state based on the entire path you've already taken, define:

```
dp[i][j]
```

to represent:

```
The LENGTH of the longest
strictly increasing path

STARTING at cell (i, j).
```

For example:

```
dp[2][1]
```

means:

```
What is the longest increasing path
that starts from matrix[2][1]?
```

In our example:

```
matrix[2][1] = 1;
```

The longest path starting there is:

```
1 → 2 → 6 → 9
```

Therefore:

```
dp[2][1] = 4;
```

Another example:

```
matrix[0][0] = 9;
```

There is no adjacent cell with a value greater than `9`.

Therefore:

```
dp[0][0] = 1;
```

Why `1`?

Because a path containing only the starting cell is still a valid path.

Remember:

```
dp[i][j] = maximum number of cells
           in an increasing path
           starting at (i, j)
```

This is the most important state definition.

### Hint 4: Think about the choices from each cell

Suppose you're currently at:

```
matrix[i][j]
```

You can potentially move in four directions:

```
          UP
           ↑
           |
LEFT ← (i, j) → RIGHT
           |
           ↓
          DOWN
```

Represent these directions using:

```
vector<pair<int, int>> directions = {
    {-1, 0},  // Up
    {1, 0},   // Down
    {0, -1},  // Left
    {0, 1}    // Right
};
```

For each direction:

```
int ni = i + di;
int nj = j + dj;
```

But before moving, ask two questions:

Question 1: Is the next cell inside the matrix?

```
ni >= 0 && ni < rows &&
nj >= 0 && nj < cols
```

Question 2: Is the next cell strictly greater?

```
matrix[ni][nj] > matrix[i][j]
```

Only if both conditions are true can you continue exploring that neighbor.

For example:

```
matrix = {
    {9, 9, 4},
    {6, 6, 8},
    {2, 1, 1}
};
```

From:

```
matrix[1][1] = 6;
```

The neighbors are:

```
Up:    9 → Valid

Down:  1 → Invalid

Left:  6 → Invalid (equal)

Right: 8 → Valid
```

Therefore, from this cell you can explore the paths beginning at `9` and `8`.

Which one should you choose?

The one that produces the longest total path.

### Hint 5: Derive the DP recurrence

Suppose you want to calculate:

```
dp[i][j]
```

You are currently standing on cell `(i, j)`.

You have two possibilities.

Option 1: Don't move anywhere

If no neighboring cell has a strictly greater value, your path contains only the current cell.

So:

```
dp[i][j] = 1;
```

Option 2: Move to a greater neighboring cell

Suppose a valid neighbor is:

```
(ni, nj)
```

And you already know:

```
dp[ni][nj]
```

This represents the longest increasing path starting at that neighboring cell.

If you move from `(i, j)` to `(ni, nj)`, your path length becomes:

```
1 + dp[ni][nj]
```

Why add `1`?

Because you're including the current cell in the path.

You want to maximize the path length over all valid neighbors.

Therefore:

```
dp[i][j] = max(
    dp[i][j],
    1 + dp[ni][nj]
);
```

This is the core recurrence.

Mathematically:

\\[ dp[i][j]=1+\max\_{\text{valid greater neighbors }(x,y)}dp[x][y] \\]

If no greater neighbor exists, the answer is simply `1`.

Mental model:

```
         dp[i][j]
             |
      Explore neighbors
             |
      Is neighbor greater?
          /       \
         No       Yes
         |         |
       Skip     dfs(ni,nj)
                   |
             1 + result
                   |
              Take MAX
```

### Hint 6: Why should you use DFS?

You might consider filling the DP table using nested loops:

```
for (int i = 0; i < rows; i++) {
    for (int j = 0; j < cols; j++) {
        // Calculate dp[i][j]
    }
}
```

But there's a problem.

Suppose:

```
matrix = {
    {9, 1},
    {3, 5}
};
```

When calculating:

```
dp[0][1]
```

you need information about its greater neighbors:

```
9 and 5
```

But have their DP values been calculated yet?

Not necessarily.

Unlike problems such as Unique Paths, where dependencies always come from specific directions, here the next cell could be above, below, left, or right.

So a simple row-by-row DP traversal doesn't guarantee that the states you need are already computed.

Instead, use DFS with memoization.

Define:

```
dfs(i, j)
```

to mean:

```
Return the longest increasing
path starting from cell (i, j).
```

When computing a cell, recursively calculate the answers for valid greater neighbors first.

Then use their results to compute the current cell.

This is top-down dynamic programming.

### Hint 7: Add memoization to your DFS

Create a DP table:

```
int rows = matrix.size();
int cols = matrix[0].size();

vector<vector<int>> dp(
    rows,
    vector<int>(cols, 0)
);
```

Why initialize with `0`?

Because every valid path has a length of at least `1`.

Therefore:

```
dp[i][j] == 0
```

means:

```
We haven't calculated this cell yet.
```

Before exploring a cell, check:

```
if (dp[i][j] != 0) {
    return dp[i][j];
}
```

If the answer is already stored, return it immediately.

Otherwise:

1. Initialize the path length to `1`.
2. Explore all four directions.
3. Recursively calculate valid greater neighbors.
4. Store the maximum path length in `dp[i][j]`.
5. Return the stored result.

The important observation is that each cell's DFS result only needs to be computed once.

This avoids exploring the same increasing paths repeatedly.

### Hint 8: Do you need a `visited` array?

This is a subtle but important detail.

In many DFS problems, such as LeetCode 79. Word Search, you need a `visited` array to prevent revisiting cells.

But is it necessary here?

Consider the rule:

```
matrix[ni][nj] > matrix[i][j]
```

Every move must go to a strictly greater value.

Suppose you had a cycle:

```
A → B → C → A
```

Because the path is strictly increasing, you would need:

\\[ A\<B\<C\<A \\]

But that's impossible.

A number cannot be strictly greater than itself.

Therefore, a strictly increasing path cannot contain a cycle.

This means the matrix can be viewed as a Directed Acyclic Graph (DAG), where an edge exists from a cell to an adjacent cell with a greater value.

Example of increasing transitions

Transitions only go toward larger values. You can never follow an increasing path back to an earlier cell.

So you don't need a separate `visited` array for cycle detection.

Your memoization table is enough.

Remember: The absence of cycles comes from the strict `>` condition. If the problem allowed moves to equal values, this reasoning would no longer hold.

### Hint 9: Walk through an example

Let's return to:

```
matrix = {
    {9, 9, 4},
    {6, 6, 8},
    {2, 1, 1}
};
```

Recall that:

```
dp[i][j]
```

represents the longest increasing path starting at `(i, j)`.

Start with cells that have no greater neighbors.

For example:

```
matrix[0][0] = 9;
```

No neighboring value is greater than `9`.

Therefore:

```
dp[0][0] = 1;
```

Similarly:

```
dp[0][1] = 1;
dp[1][2] = 1;
```

Now consider:

```
matrix[1][0] = 6;
```

You can move to:

```
matrix[0][0] = 9;
```

So:

```
dp[1][0] = 1 + dp[0][0];
         = 1 + 1;
         = 2;
```

Now consider:

```
matrix[2][0] = 2;
```

You can move to `6`, whose longest increasing path has length `2`.

So:

```
dp[2][0] = 1 + dp[1][0];
         = 1 + 2;
         = 3;
```

Finally:

```
matrix[2][1] = 1;
```

One possible move is to `2`, whose DP value is `3`.

So:

```
dp[2][1] = 1 + dp[2][0];
         = 1 + 3;
         = 4;
```

This corresponds to:

```
1 → 2 → 6 → 9
```

#### Interactive DP walkthrough

Initialize memoization

Step 1 of 5

matrix[0][0] = 9

## —

matrix[0][1] = 9

## —

matrix[0][2] = 4

## —

matrix[1][0] = 6

## —

matrix[1][1] = 6

## —

matrix[1][2] = 8

## —

matrix[2][0] = 2

## —

matrix[2][1] = 1

## —

matrix[2][2] = 1

## —

Each box shows the longest increasing path length starting from that cell.

All DP states are initially 0 (uncomputed). The visualization uses dashes for these cells.

&#x20;PreviousNext step&#x20;

The final DP table is:

|     | j=0 | j=1 | j=2 |
| --- | --- | --- | --- |
| i=0 | 1   | 1   | 2   |
| i=1 | 2   | 2   | 1   |
| i=2 | 3   | 4   | 2   |

Notice:

```
dp[2][1] = 4;
```

This is the maximum value in the table.

Therefore:

```
answer = 4;
```

### Hint 10: Why must you start DFS from every cell?

You might think that the longest increasing path must always start at the smallest number in the matrix.

But that isn't necessarily true.

Consider:

```
matrix = {
    {1, 9, 8},
    {7, 6, 5},
    {2, 3, 4}
};
```

The smallest value is `1`, but its adjacent cells are `9` and `7`.

Neither gives you access to the long path:

```
2 → 3 → 4 → 5 → 6 → 7
```

That path has length `6`, and it begins at `2`.

So you cannot simply find the minimum element and start there.

Instead, calculate:

```
dfs(i, j)
```

for every cell and take the maximum.

Conceptually:

```
int answer = 0;

for (int i = 0; i < rows; i++) {
    for (int j = 0; j < cols; j++) {

        answer = max(
            answer,
            dfs(i, j)
        );
    }
}
```

Because DFS is memoized, calling it from every cell doesn't mean recalculating every path from scratch.

Previously computed results are reused.

### Hint 11: What is the base case?

In many recursive problems, you write an explicit base case such as:

```
if (i == rows || j == cols) {
    return 0;
}
```

But this problem can be handled differently.

Suppose you are at a cell where no neighbor has a larger value.

For example:

```
matrix = {
    {3, 5},
    {2, 1}
};
```

Starting from `5`:

```
No valid greater neighbor exists.
```

Your path still has length `1`.

So initialize:

```
int best = 1;
```

Then explore all valid greater neighbors.

If none exist, `best` stays `1`.

Therefore, you don't need a separate termination condition for a local maximum.

The DFS naturally stops when no valid moves remain.

Mental model:

```
Start at cell (i, j)
         ↓
Set best = 1
         ↓
Explore all four directions
         ↓
Any greater neighbor?
      /          \
     No          Yes
     |            |
Return 1     Recurse and
             update best
```

### Hint 12: Understand why memoization changes the complexity

Let:

```
m = matrix.size();
n = matrix[0].size();
```

Without memoization, DFS can repeatedly explore many of the same increasing paths.

The number of explored paths can become very large.

But with memoization, each cell's result is fully computed only once.

For each cell, you examine at most four neighbors:

```
Up
Down
Left
Right
```

So the total work is proportional to:

```
Number of cells × 4
```

Which is:

\\[ 4mn \\]

Since `4` is constant:

\\[ \boxed{O(mn)} \\]

Your time complexity is:

```
Time: O(m × n)
```

You store one memoized result per cell:

```
vector<vector<int>> dp(m, vector<int>(n));
```

So:

```
Memoization space: O(m × n)
```

The recursion stack can reach `O(m × n)` depth in the worst case, when there is a long increasing path.

Therefore:

```
Total auxiliary space: O(m × n)
```

This is an optimal asymptotic time complexity because you may need to examine every cell in the matrix.

### Hint 13: What about Bottom-Up DP?

You might ask:

> Can I solve this using iterative DP instead of DFS?

Yes.

But there's an important difference from ordinary grid DP.

In a typical problem such as Unique Paths, the dependencies have a fixed direction.

For example:

```
dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
```

You can process the grid row by row.

In Longest Increasing Path, dependencies depend on cell values.

For example:

```
A cell may depend on:
- The cell above it
- The cell below it
- The cell to its left
- The cell to its right
```

So a simple row-by-row order is insufficient.

One alternative is to view the matrix as a Directed Acyclic Graph and use topological sorting.

Because each valid move goes toward a strictly larger value, the dependency graph has no cycles.

You can process the cells in an appropriate topological order.

However, for this problem, DFS + Memoization is generally the more intuitive approach.

It handles dependency order automatically through recursion.

### Hint 14: Compare it with Longest Increasing Subsequence

Recall LeetCode 300. Longest Increasing Subsequence.

In LIS, a common DP state is:

```
dp[i]
```

meaning:

```
Length of the longest increasing
subsequence ending at index i.
```

The transition compares the current element with other elements that can precede it.

In Longest Increasing Path, you instead use:

```
dp[i][j]
```

meaning:

```
Length of the longest increasing
path starting at cell (i, j).
```

The transition compares the current cell with its valid greater neighbors.

| Property             | 300. LIS                     | 329. Longest Increasing Path |
| -------------------- | ---------------------------- | ---------------------------- |
| Data structure       | 1D array                     | 2D matrix                    |
| Movement restriction | Increasing indices           | Four adjacent directions     |
| Increasing condition | Strictly greater value       | Strictly greater value       |
| DP state             | `dp[i]`                      | `dp[i][j]`                   |
| Typical approach     | Bottom-up DP / binary search | DFS + memoization            |
| DP complexity        | O(n²)                        | O(m × n)                     |

The important similarity is that both problems reuse answers to smaller subproblems to construct longer increasing sequences.

### Skeleton — DFS + Memoization (C++)

Try filling in the missing expressions yourself.

```
class Solution {
public:
    int rows, cols;

    vector<vector<int>> dp;

    vector<pair<int, int>> directions = {
        {-1, 0},
        {1, 0},
        {0, -1},
        {0, 1}
    };

    int dfs(int i, int j, vector<vector<int>>& matrix) {

        // If this cell has already been calculated,
        // return the stored answer.
        if ( ? ) {
            return dp[i][j];
        }

        // Every path contains at least one cell.
        int best =  ? ;

        // Explore four directions.
        for (auto [di, dj] : directions) {

            int ni = i + di;
            int nj = j + dj;

            // Check whether the neighbor is valid.
            if ( ? ) {
                continue;
            }

            // Only move to strictly greater values.
            if ( ? ) {

                // Extend the current path by
                // the longest path from the neighbor.
                best = max(
                    best,
                     ? 
                );
            }
        }

        // Save the result for this cell.
        dp[i][j] =  ? ;

        return dp[i][j];
    }

    int longestIncreasingPath(vector<vector<int>>& matrix) {

        rows = matrix.size();
        cols = matrix[0].size();

        // Initialize memoization table.
        dp.assign(
            rows,
            vector<int>(cols, 0)
        );

        int answer = 0;

        // Every cell is a possible starting point.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                answer = max(
                    answer,
                     ? 
                );
            }
        }

        return answer;
    }
};
```

### Core recurrence

Define:

```
dp[i][j] = Length of the longest
           strictly increasing path
           starting at (i, j)
```

Initialize the memoization table:

```
vector<vector<int>> dp(
    rows,
    vector<int>(cols, 0)
);
```

If the answer is already computed:

```
if (dp[i][j] != 0) {
    return dp[i][j];
}
```

For each valid greater neighbor:

```
if (matrix[ni][nj] > matrix[i][j]) {
    best = max(
        best,
        1 + dfs(ni, nj)
    );
}
```

Base case:

```
int best = 1;
```

This handles cells with no greater neighbor.

Memoize the result:

```
dp[i][j] = best;
```

Final answer:

```
max(dfs(i, j))
```

over every cell `(i, j)` in the matrix.

Or mentally:

```
Given an m × n matrix
          ↓
Define:
dp[i][j] = Longest increasing path
           starting at cell (i, j)
          ↓
Create memoization table
initialized to 0
          ↓
Start DFS from every cell
          ↓
Has this state been calculated?
          ↓
YES → Return cached value
          ↓
NO → Initialize best = 1
          ↓
Explore four directions
          ↓
Is the neighbor inside the matrix?
          ↓
Is its value strictly greater?
          ↓
YES → Recursively calculate
      its longest path
          ↓
best = max(best, 1 + neighborPath)
          ↓
Store dp[i][j] = best
          ↓
Return best
          ↓
Take maximum across all cells
```

### Connection to previous problems

| Problem                                    | DP / DFS meaning                           | Goal                            |
| ------------------------------------------ | ------------------------------------------ | ------------------------------- |
| 79. Word Search                            | Explore valid neighboring cells            | Determine whether a word exists |
| 300. Longest Increasing Subsequence        | Longest increasing subsequence at an index | Maximize sequence length        |
| 62. Unique Paths                           | Number of paths to a grid position         | Count paths                     |
| 1140. Stone Game II                        | Best outcome from a game state             | Maximize stones                 |
| 329. Longest Increasing Path               | Longest increasing path from a cell        | Maximize path length            |
| 2328. Number of Increasing Paths in a Grid | Number of increasing paths from a cell     | Count paths                     |

The most useful connection is with DFS + Memoization.

Like Stone Game II, you define a recursive function whose result depends on smaller states, and you store those results to avoid recalculating them.

The difference is that Stone Game II uses states representing game positions, while Longest Increasing Path uses states representing matrix coordinates.

The four most important rules to remember are:

```
Rule 1:
You can start from any cell.
→ Run DFS from EVERY cell.

Rule 2:
You can only move to a greater value.
→ Check matrix[ni][nj] > matrix[i][j].

Rule 3:
The longest path from a cell
can be reused.
→ Use MEMOIZATION.

Rule 4:
Every move strictly increases the value.
→ No cycles, so no visited array is needed.
```

The biggest takeaway: LeetCode 329 is a classic example of DFS + Memoization on a Directed Acyclic Graph. Instead of exploring every possible path independently, define the longest increasing path starting from each cell, recursively calculate it from greater neighbors, and cache the result. This turns an expensive search into an `O(m × n)` dynamic programming solution.
*/
class Solution {
private:
    int rows;
    int cols;
    vector<vector<int>> dp;

    vector<pair<int, int>> directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    int dfs(int i, int j, vector<vector<int>>& matrix){
        if(dp[i][j] != 0){
            return dp[i][j];
        }

        int best = 1;

        for(auto [di, dj] : directions){
            int ni = i + di;
            int nj = j + dj;

            if(ni < 0 || ni >= rows || nj < 0 || nj >= cols){
                continue;
            }

            if(matrix[ni][nj] > matrix[i][j]){
                best = std::max(best, 1 + dfs(ni, nj, matrix));
            }
        }

        dp[i][j] = best;

        return dp[i][j];
    }

public:
    int longestIncreasingPath(vector<vector<int>>& matrix) {
        int answer = 0;

        rows = matrix.size();
        cols = matrix[0].size();

        
        dp.assign(rows, vector<int>(cols, 0));



        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                answer = std::max(answer, dfs(i, j, matrix));
            }
        }

        return answer;  
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna