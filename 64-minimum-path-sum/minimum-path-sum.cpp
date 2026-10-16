// Method 1: 2D DP
/*
This is a Dynamic Programming / Grid DP (Minimization) problem.

The key idea is almost identical to 62. Unique Paths and 63. Unique Paths II, except instead of counting how many paths exist, you need to find the minimum sum of values along a path.

You can only move right or down, so every cell can only be reached from:

```
the cell directly above
or
the cell directly to the left
```

Instead of adding the number of paths from those two cells, you take the minimum path sum and add the current cell's value.

### Hint 1: Define your DP state

Let:

```
dp[i][j]
```

mean:

```
the minimum path sum required to travel
from (0, 0) to cell (i, j)
```

Your goal is:

```
dp[m - 1][n - 1]
```

where:

```
int m = grid.size();
int n = grid[0].size();
```

### Hint 2: Think about the last move

Suppose you're trying to reach:

```
(i, j)
```

Since you can only move right or down, you must have arrived from either:

```
dp[i - 1][j]  // from above
dp[i][j - 1]  // from left
```

But unlike Unique Paths, you don't want to add these two values together.

You want whichever path is cheaper.

### Hint 3: Build the recurrence

If you arrive from above, your total cost is:

```
dp[i - 1][j] + grid[i][j]
```

If you arrive from the left:

```
dp[i][j - 1] + grid[i][j]
```

Choose the smaller cost:

```
dp[i][j] = grid[i][j] + min(
    dp[i - 1][j],
    dp[i][j - 1]
);
```

Mental model:

```
minimum cost to reach current cell
=
current cell's value
+
minimum(
    cost from above,
    cost from left
)
```

### Hint 4: What is the base case?

The robot starts at:

```
(0, 0)
```

You must include the value of the starting cell in your path sum.

Therefore:

```
dp[0][0] = grid[0][0];
```

Unlike Unique Paths, where:

```
dp[0][0] = 1;
```

here you're tracking cost, not number of paths.

### Hint 5: Initialize the first row

In the first row, the robot can only move right.

For example:

```
grid = {
    {1, 3, 1, 5}
};
```

The minimum path sums are:

```
grid:  1   3   1   5
dp:    1   4   5   10
```

Why?

```
dp[0][0] = 1

dp[0][1] = 1 + 3 = 4

dp[0][2] = 4 + 1 = 5

dp[0][3] = 5 + 5 = 10
```

So initialize the first row using:

```
for (int j = 1; j < n; j++) {
    dp[0][j] = dp[0][j - 1] + grid[0][j];
}
```

### Hint 6: Initialize the first column

Similarly, in the first column, the robot can only move down.

Suppose:

```
grid = {
    {1},
    {2},
    {4},
    {3}
};
```

The minimum path sums are:

```
grid    dp

 1       1
 2       3
 4       7
 3      10
```

So:

```
for (int i = 1; i < m; i++) {
    dp[i][0] = dp[i - 1][0] + grid[i][0];
}
```

Now your boundary cells are initialized.

### Hint 7: Fill the remaining cells

Once the first row and first column are initialized, start from:

```
(1, 1)
```

For every remaining cell:

```
for (int i = 1; i < m; i++) {
    for (int j = 1; j < n; j++) {

        // Minimum cost from above or left

        // Add current cell's value
    }
}
```

Because you're processing the grid from top-left to bottom-right, both neighboring DP states are already calculated.

### Hint 8: Walk through an example

Suppose:

```
grid = {
    {1, 3, 1},
    {1, 5, 1},
    {4, 2, 1}
};
```

DP walkthrough

Step 1 of 6

Original grid

1

3

1

1

5

1

4

2

1

Minimum cost DP

1

–

–

–

–

–

–

–

–

Initialize starting cell

dp[0][0] = grid[0][0] = 1.

&#x20;PreviousNext&#x20;

At the end, your DP grid becomes:

```
1  4  5
2  7  6
6  8  7
```

So:

```
dp[2][2] = 7;
```

One optimal path is:

```
1 → 3 → 1 → 1 → 1
```

Its sum is:

```
1 + 3 + 1 + 1 + 1 = 7
```

### Hint 9: Why can't you just greedily choose the smaller neighboring cell?

You might think:

```
At every step, move to whichever neighboring
cell has the smaller value.
```

But that can fail.

Consider:

```
grid = {
    {1, 1, 100},
    {2, 1, 100},
    {3, 1,   1}
};
```

A greedy decision based only on the next cell doesn't necessarily account for the cost of completing the entire path.

DP works because it stores the minimum total cost to reach each position, rather than making decisions based only on individual cell values.

### Hint 10: You can optimize to O(n) space

Just like Unique Paths I and II, you don't actually need the entire 2D DP array.

You can use:

```
vector<int> dp(n);
```

Each `dp[j]` will store the minimum path sum to the current row's column `j`.

When processing a cell:

```
dp[j] = grid[i][j] + min(
    dp[j],       // from above
    dp[j - 1]    // from left
);
```

Why?

Before updating:

```
dp[j] = minimum cost from above
```

After updating `dp[j - 1]` in the current row:

```
dp[j - 1] = minimum cost from left
```

You just need to initialize the first row and handle the first column separately.

This reduces the space complexity.

### Hint 11: Complexity

For the 2D DP solution:

```
Time:  O(m × n)
Space: O(m × n)
```

For the optimized 1D DP solution:

```
Time:  O(m × n)
Space: O(n)
```

Both work for the problem.

You can also modify `grid` directly to achieve `O(1)` auxiliary space, but the separate DP version is easier to understand.

### Skeleton — 2D DP

```
class Solution {
public:
    int minPathSum(vector<vector<int>>& grid) {
        int m = grid.size();
        int n = grid[0].size();

        vector<vector<int>> dp(
            m,
            vector<int>(n, 0)
        );

        // Base case:
        // dp[0][0] = ?

        // Initialize first row
        for (int j = 1; j < n; j++) {
            // Current cell value
            // + previous cell's minimum cost
        }

        // Initialize first column
        for (int i = 1; i < m; i++) {
            // Current cell value
            // + previous cell's minimum cost
        }

        // Fill remaining cells
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {

                // dp[i][j] =
                // current cell's value
                // +
                // min(
                //     cost from above,
                //     cost from left
                // )
            }
        }

        return dp[m - 1][n - 1];
    }
};
```

### Skeleton — 1D DP (Optional Optimization)

```
class Solution {
public:
    int minPathSum(vector<vector<int>>& grid) {
        int m = grid.size();
        int n = grid[0].size();

        vector<int> dp(n, 0);

        // Initialize starting cell

        // Initialize first row

        for (int i = 1; i < m; i++) {

            // Update first column

            for (int j = 1; j < n; j++) {

                // dp[j] represents cost from above
                // dp[j - 1] represents cost from left

                // Take the minimum
                // and add grid[i][j]
            }
        }

        return dp[n - 1];
    }
};
```

The core recurrence is:

```
dp[0][0] = grid[0][0]

First row:
dp[0][j] = grid[0][j] + dp[0][j - 1]

First column:
dp[i][0] = grid[i][0] + dp[i - 1][0]

Other cells:
dp[i][j] = grid[i][j] + min(
    dp[i - 1][j],
    dp[i][j - 1]
)
```

Or mentally:

```
start at top-left
        ↓
initialize starting cell
        ↓
accumulate costs in first row and column
        ↓
for every remaining cell:
    choose minimum cost from above or left
    add current cell's value
        ↓
return bottom-right cell
```

### Connection to previous Grid DP problems

| Concept    | 62. Unique Paths | 63. Unique Paths II           | 64. Minimum Path Sum    |
| ---------- | ---------------- | ----------------------------- | ----------------------- |
| DP state   | Number of paths  | Number of valid paths         | Minimum path cost       |
| Transition | Above + Left     | Above + Left (unless blocked) | Min(Above, Left) + Cost |
| Base case  | `dp[0][0] = 1`   | `1` if not blocked            | `grid[0][0]`            |
| Time       | O(m × n)         | O(m × n)                      | O(m × n)                |

The trickiest conceptual part is realizing that you're no longer counting paths; you're minimizing the total cost of a path.

That changes the transition from:

```
dp[i - 1][j] + dp[i][j - 1]
```

to:

```
grid[i][j] + min(dp[i - 1][j], dp[i][j - 1])
```

Once you recognize that distinction, this problem becomes almost identical to Unique Paths.
*/
class Solution {
public:
    int minPathSum(vector<vector<int>>& grid) {
        int m = grid.size();
        int n = grid[0].size();

        vector<vector<int>> dp(m, vector<int>(n, 0));
        dp[0][0] = grid[0][0];

        for(int j=1; j<n; j++){
            dp[0][j] = dp[0][j-1] + grid[0][j];
        }

        for(int i=1; i<m; i++){
            dp[i][0] = dp[i-1][0] + grid[i][0];
        }

        for(int i=1; i<m; i++){
            for(int j=1; j<n; j++){
                dp[i][j] = grid[i][j] + std::min(dp[i-1][j], dp[i][j-1]);
            }
        }

        return dp[m-1][n-1];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna