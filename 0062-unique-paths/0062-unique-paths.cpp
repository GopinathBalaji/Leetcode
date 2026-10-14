// Method 1: 2D DP
/*
This is a Dynamic Programming / Grid DP problem.

The key idea is that the robot can only move right or down.

Therefore, to reach any cell `(i, j)`, the robot must have come from either:

```
the cell directly above
```

or:

```
the cell directly to the left
```

So the number of unique paths to a cell is the sum of the paths to those two neighboring cells.

### Hint 1: Define your DP state

Let:

```
dp[i][j]
```

mean:

```
the number of unique paths from
the top-left corner to cell (i, j)
```

Your goal is:

```
dp[m - 1][n - 1]
```

because the robot starts at `(0, 0)` and needs to reach the bottom-right corner.

### Hint 2: Think about the last move

Suppose you're trying to reach cell:

```
(i, j)
```

Since the robot can only move right or down, it must have arrived from one of two places:

Two ways to enter a cell

Every path to the target ends with a move from above or from the left.

So the number of ways to reach `(i, j)` depends on:

```
dp[i - 1][j]  // from above
dp[i][j - 1]  // from left
```

### Hint 3: Build the recurrence

Since every valid path must come from one of those two cells:

```
dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
```

Why addition?

Because the paths from above and the paths from the left are two different groups of valid paths.

Mental model:

```
ways to reach current cell
=
ways to reach cell above
+
ways to reach cell on left
```

### Hint 4: What is the base case?

The robot begins at:

```
(0, 0)
```

There's exactly one way to be at the starting cell: start there.

So:

```
dp[0][0] = 1;
```

But what about the rest of the first row and first column?

Think about a grid like:

```
S → → →
↓
↓
↓
```

To reach any cell in the first row, the robot can only move right.

To reach any cell in the first column, it can only move down.

So every cell along those edges has exactly one unique path.

### Hint 5: Initialize the first row and first column

You can initialize the entire DP grid with zeros:

```
vector<vector<int>> dp(
    m,
    vector<int>(n, 0)
);
```

Then set the first row:

```
for (int j = 0; j < n; j++) {
    dp[0][j] = 1;
}
```

And the first column:

```
for (int i = 0; i < m; i++) {
    dp[i][0] = 1;
}
```

Now you've established the base cases.

### Hint 6: Fill the rest of the grid

Once the first row and first column are initialized, you can start at:

```
(1, 1)
```

and fill the remaining cells.

Conceptually:

```
for (int i = 1; i < m; i++) {
    for (int j = 1; j < n; j++) {

        // paths from above
        // +
        // paths from left
    }
}
```

Notice that you don't start at `(0, 0)` inside these loops because the edges are already initialized.

### Hint 7: Walk through an example

Suppose:

```
m = 3;
n = 3;
```

DP walkthrough

Step 1 of 5

## 1

## 1

## 1

## 1

## –

## –

## 1

## –

## –

Initialize the base cases

First row and first column have exactly one path to every cell.

&#x20;PreviousNext&#x20;

At the end, the DP grid becomes:

```
1  1  1
1  2  3
1  3  6
```

So:

```
dp[2][2] = 6;
```

There are 6 unique paths.

### Hint 8: Why does filling the grid from top-left to bottom-right work?

Your recurrence uses:

```
dp[i - 1][j]
dp[i][j - 1]
```

These values must already be calculated before processing `dp[i][j]`.

When you traverse row by row, left to right:

```
→ → →
→ → →
→ → →
```

both required neighboring cells have already been computed.

That's why bottom-up DP works naturally here.

### Hint 9: You can optimize to O(n) space

Notice that to calculate:

```
dp[i][j]
```

you only need:

```
the value above
and
the value to the left
```

So you don't actually need to store the entire 2D grid.

You can use:

```
vector<int> dp(n, 1);
```

where `dp[j]` represents the number of paths to column `j` in the current row as you update it.

When processing the next row:

```
dp[j] = dp[j] + dp[j - 1];
```

Here:

```
dp[j]     → paths from above (old value)
dp[j - 1] → paths from left (already updated)
```

This reduces the space complexity from:

```
O(m × n)
```

to:

```
O(n)
```

### Hint 10: Complexity

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

Both solutions work for the problem's constraints.

### Skeleton — 2D DP

```
class Solution {
public:
    int uniquePaths(int m, int n) {

        vector<vector<int>> dp(
            m,
            vector<int>(n, 0)
        );

        // Initialize first row to 1

        // Initialize first column to 1

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {

                // dp[i][j] =
                // paths from above
                // +
                // paths from left
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
    int uniquePaths(int m, int n) {

        vector<int> dp(n, 1);

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {

                // dp[j] = ?
                //
                // dp[j]     = paths from above
                // dp[j - 1] = paths from left
            }
        }

        return dp[n - 1];
    }
};
```

The core recurrence is:

```
dp[i][j]
=
dp[i - 1][j]
+
dp[i][j - 1]
```

Or mentally:

```
start at top-left
        ↓
initialize first row and column
        ↓
for every other cell:
    paths = above + left
        ↓
return bottom-right cell
```

The trickiest conceptual part is recognizing that every path to a cell must end with either a move from above or a move from the left, which means you can count paths without enumerating them individually.

This is also a great introduction to 2D DP, since the same idea appears in many other grid problems.
*/
class Solution {
public:
    int uniquePaths(int m, int n) {
        vector<vector<int>> dp(m, vector<int>(n, 0));
        dp[0][0] = 1;

        for(int j=0; j<n; j++){
            dp[0][j] = 1;
        }

        for(int i=0; i<m; i++){
            dp[i][0] = 1;
        }   

        for(int i=1; i<m; i++){
            for(int j=1; j<n; j++){
                dp[i][j] = dp[i-1][j] + dp[i][j-1];
            }
        }

        return dp[m-1][n-1];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna