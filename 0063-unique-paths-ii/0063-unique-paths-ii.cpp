// Method 1: 2D DP
/*
This is a Dynamic Programming / Grid DP problem.

The key idea is almost identical to 62. Unique Paths, except now some cells contain obstacles.

You still calculate the number of ways to reach each cell by adding:

```
paths from above
+
paths from the left
```

But if a cell contains an obstacle, the number of paths to that cell must be 0.

### Hint 1: Reuse the Unique Paths recurrence

In problem 62, you used:

```
dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
```

That recurrence still works here.

The difference is that some cells are blocked.

In `obstacleGrid`:

```
0 → empty cell
1 → obstacle
```

If:

```
obstacleGrid[i][j] == 1
```

then the robot cannot enter that cell.

Therefore:

```
dp[i][j] = 0;
```

### Hint 2: Define your DP state

Let:

```
dp[i][j]
```

mean:

```
the number of unique paths from
(0, 0) to (i, j)
without passing through obstacles
```

Your goal is:

```
dp[m - 1][n - 1]
```

where:

```
int m = obstacleGrid.size();
int n = obstacleGrid[0].size();
```

### Hint 3: Build the recurrence

For each cell `(i, j)`, there are two situations.

Case 1: The cell is blocked

```
if (obstacleGrid[i][j] == 1) {
    dp[i][j] = 0;
}
```

Case 2: The cell is empty

The robot can arrive from above or from the left:

```
else {
    dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
}
```

This is the same recurrence as Unique Paths.

However, you need to handle the first row and first column carefully to avoid accessing invalid indices.

### Hint 4: What if the starting cell is blocked?

Suppose:

```
obstacleGrid = {
    {1, 0},
    {0, 0}
};
```

The starting cell `(0, 0)` contains an obstacle.

The robot cannot even begin moving.

So immediately return:

```
if (obstacleGrid[0][0] == 1) {
    return 0;
}
```

Otherwise, initialize:

```
dp[0][0] = 1;
```

because there's exactly one way to be at the starting position.

### Hint 5: Be careful initializing the first row

In problem 62, you initialized the entire first row to `1`.

But that no longer works!

Consider:

```
S → X → →
```

where `X` is an obstacle.

After encountering the obstacle, the robot cannot reach any cells to its right while staying in the first row.

So the number of paths becomes:

```
1  1  0  0  0
```

You should initialize the first row using:

```
for (int j = 1; j < n; j++) {
    if (obstacleGrid[0][j] == 0) {
        dp[0][j] = dp[0][j - 1];
    }
}
```

Notice that when there's an obstacle, `dp[0][j]` stays `0`.

And every subsequent cell also remains unreachable.

### Hint 6: The first column works the same way

Similarly, in the first column, the robot can only move downward.

Consider:

```
S   1
↓   1
X   0
↓   0
↓   0
```

Once an obstacle appears, all cells below it in that column become unreachable from above.

So:

```
for (int i = 1; i < m; i++) {
    if (obstacleGrid[i][0] == 0) {
        dp[i][0] = dp[i - 1][0];
    }
}
```

This handles obstacles along the left edge.

### Hint 7: Fill the remaining cells

After initializing the first row and column, process the rest:

```
for (int i = 1; i < m; i++) {
    for (int j = 1; j < n; j++) {

        if (obstacleGrid[i][j] == 1) {
            // Blocked cell
        }
        else {
            // Paths from above + paths from left
        }
    }
}
```

Remember:

```
Obstacle → dp[i][j] = 0

Empty cell → dp[i-1][j] + dp[i][j-1]
```

### Hint 8: Walk through an example

Suppose:

```
obstacleGrid = {
    {0, 0, 0},
    {0, 1, 0},
    {0, 0, 0}
};
```

DP walkthrough

Step 1 of 5

## 1

## 1

## 1

## 1

Blocked

## –

## 1

## –

## –

Initialize boundaries

The first row and column are reachable. The center is an obstacle.

&#x20;PreviousNext&#x20;

At the end:

```
1  1  1
1  0  1
1  1  2
```

So:

```
dp[2][2] = 2;
```

There are exactly two unique paths that avoid the obstacle.

### Hint 9: You don't need to explicitly check neighboring obstacles

This is a useful observation.

You might think you need to check:

```
obstacleGrid[i - 1][j]
obstacleGrid[i][j - 1]
```

before adding their DP values.

But you don't!

Why?

Because whenever a cell is blocked, you've already set:

```
dp[i][j] = 0;
```

So blocked neighbors naturally contribute zero paths.

Your recurrence remains:

```
dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
```

for every empty interior cell.

### Hint 10: You can optimize to O(n) space

Just like Unique Paths I, you don't need the entire 2D DP array.

You can use:

```
vector<int> dp(n, 0);
dp[0] = 1;
```

Then process each cell.

For an obstacle:

```
dp[j] = 0;
```

For an empty cell with `j > 0`:

```
dp[j] += dp[j - 1];
```

Why does this work?

```
dp[j]     → paths from above
dp[j - 1] → paths from left
```

The important difference from problem 62 is that you must reset `dp[j]` to zero whenever you encounter an obstacle.

Otherwise, paths would incorrectly continue through blocked cells.

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

Both are efficient enough for the problem.

### Skeleton — 2D DP

```
class Solution {
public:
    int uniquePathsWithObstacles(
        vector<vector<int>>& obstacleGrid
    ) {
        int m = obstacleGrid.size();
        int n = obstacleGrid[0].size();

        // If starting cell is blocked:
        // return 0

        vector<vector<int>> dp(
            m,
            vector<int>(n, 0)
        );

        // Base case:
        // dp[0][0] = ?

        // Initialize first row,
        // accounting for obstacles

        // Initialize first column,
        // accounting for obstacles

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {

                if (obstacleGrid[i][j] == 1) {
                    // Blocked cell
                }
                else {
                    // Paths from above
                    // +
                    // Paths from left
                }
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
    int uniquePathsWithObstacles(
        vector<vector<int>>& obstacleGrid
    ) {
        int m = obstacleGrid.size();
        int n = obstacleGrid[0].size();

        if (obstacleGrid[0][0] == 1) {
            return 0;
        }

        vector<int> dp(n, 0);

        dp[0] = 1;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {

                if (obstacleGrid[i][j] == 1) {
                    // Reset paths to 0
                }
                else if (j > 0) {
                    // Add paths from left
                    // to paths from above
                }
            }
        }

        return dp[n - 1];
    }
};
```

The core recurrence is:

```
if obstacleGrid[i][j] == 1:

    dp[i][j] = 0

else:

    dp[i][j] =
        dp[i - 1][j] +
        dp[i][j - 1]
```

with appropriate handling for boundary cells.

Or mentally:

```
start at top-left
        ↓
if starting cell blocked → 0
        ↓
initialize first row and column
while respecting obstacles
        ↓
for every other cell:
    if obstacle → 0 paths
    otherwise → above + left
        ↓
return bottom-right cell
```

### Connection to Unique Paths I

| Concept          | 62. Unique Paths            | 63. Unique Paths II                  |
| ---------------- | --------------------------- | ------------------------------------ |
| DP state         | Number of paths to `(i, j)` | Same                                 |
| Recurrence       | Above + Left                | Above + Left                         |
| Starting cell    | Always reachable            | May be blocked                       |
| First row/column | All `1`s                    | Obstacles can make cells unreachable |
| Blocked cells    | None                        | `dp[i][j] = 0`                       |
| Time complexity  | O(m × n)                    | O(m × n)                             |

The trickiest implementation detail is initializing the first row and first column correctly when obstacles are present.

Unlike Unique Paths I, you cannot automatically fill both edges with `1`s. Once an obstacle appears along an edge, every cell beyond it on that edge becomes unreachable.
*/
class Solution {
public:
    int uniquePathsWithObstacles(vector<vector<int>>& obstacleGrid) {
        if(obstacleGrid[0][0] == 1){
            return 0;
        }

        int rows = obstacleGrid.size();
        int cols = obstacleGrid[0].size();

        vector<vector<int>> dp(rows, vector<int>(cols, 0));
        dp[0][0] = 1;

        for(int j=1; j<cols; j++){
            if(obstacleGrid[0][j] == 0){
                dp[0][j] = dp[0][j - 1];
            }
        }

        for(int i=1; i<rows; i++){
            if(obstacleGrid[i][0] == 0){
                dp[i][0] = dp[i-1][0];
            }
        }

        for(int i=1; i<rows; i++){
            for(int j=1; j<cols; j++){
                if(obstacleGrid[i][j] == 1){
                    dp[i][j] = 0;
                }else{
                    dp[i][j] = dp[i-1][j] + dp[i][j-1];
                }
            }
        }

        return dp[rows-1][cols-1];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna