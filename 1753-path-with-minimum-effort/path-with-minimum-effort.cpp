// Method 1: Dijkstra’s algorithm on a grid
/*
This is a **Dijkstra’s algorithm on a grid**, but the path cost is unusual.

Normally Dijkstra adds edge weights. Here, the effort of a path is the **maximum absolute height difference** seen along that path.

So if a path has edge differences:

```text
2, 5, 1
```

its effort is:

```text
max(2, 5, 1) = 5
```

### Hint 1: Treat each cell like a graph node

From each cell, you can move:

```text
up
down
left
right
```

The cost of moving from `(r,c)` to `(nr,nc)` is:

```cpp
abs(heights[r][c] - heights[nr][nc])
```

### Hint 2: Track the best effort to reach each cell

Use:

```cpp
vector<vector<int>> effort(
    rows,
    vector<int>(cols, INT_MAX)
);
```

Set:

```cpp
effort[0][0] = 0;
```

Meaning it takes zero effort to start at the first cell.

### Hint 3: Use a min-heap

Store:

```text
{currentEffort, row, col}
```

with the smallest effort on top.

In C++, one option is:

```cpp
priority_queue<
    tuple<int,int,int>,
    vector<tuple<int,int,int>>,
    greater<tuple<int,int,int>>
> pq;
```

Start with:

```cpp
pq.push({0, 0, 0});
```

### Hint 4: The relaxation formula is the key

Suppose your current path effort is:

```cpp
currentEffort
```

and the next edge has difference:

```cpp
diff
```

Then the effort to reach the neighbor through this path is:

```cpp
int newEffort = max(currentEffort, diff);
```

Not:

```cpp
currentEffort + diff
```

That is the biggest difference from normal shortest-path Dijkstra.

### Hint 5: Relax the neighbor

If:

```cpp
newEffort < effort[nr][nc]
```

then you've found a better path:

```cpp
effort[nr][nc] = newEffort;
pq.push({newEffort, nr, nc});
```

### Hint 6: Early return

Because Dijkstra always pops the currently smallest possible effort, when you pop:

```text
(rows - 1, cols - 1)
```

you can immediately return its effort.

### Skeleton

```cpp
int minimumEffortPath(vector<vector<int>>& heights) {
    int rows = heights.size();
    int cols = heights[0].size();

    vector<vector<int>> effort(
        rows,
        vector<int>(cols, INT_MAX)
    );

    // min heap

    effort[0][0] = 0;
    // push {0, 0, 0}

    int directions[4][2] = {
        {1, 0},
        {-1, 0},
        {0, 1},
        {0, -1}
    };

    while (!pq.empty()) {
        // pop smallest effort

        // optional: skip stale entry

        // if destination -> return effort

        // explore 4 neighbors

        // diff = abs(...)
        // newEffort = max(currentEffort, diff)

        // relax if better
    }

    return 0;
}
```

The key mental model is:

```text
normal Dijkstra:
newCost = currentCost + edgeCost

this problem:
newEffort = max(currentEffort, edgeDifference)
```

That one change turns standard Dijkstra into the solution for this problem.
*/
class Solution {
public:
    int minimumEffortPath(vector<vector<int>>& heights) {
        int rows = heights.size();
        int cols = heights[0].size();

        vector<vector<int>> effort(rows, vector<int>(cols, INT_MAX));
        effort[0][0] = 0;

        // {currentEffort, row, col}
        priority_queue<
            tuple<int, int, int>,
            vector<tuple<int, int, int>>,
            greater<tuple<int, int, int>>
        > pq;
        pq.push({0, 0, 0});

        int directions[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while(!pq.empty()){
            auto [currentEffort, row, col] = pq.top(); 
            pq.pop();

            if(row == rows-1 && col == cols-1){
                return currentEffort;
            }

            for(auto& dir : directions){
                int nr = row + dir[0];
                int nc = col + dir[1];

                if(nr < 0 || nr >= rows || nc < 0 || nc >= cols){
                    continue;
                }

                int diff = std::abs(heights[row][col] - heights[nr][nc]);
                int newEffort = std::max(currentEffort, diff);

                if(newEffort < effort[nr][nc]){
                    effort[nr][nc] = newEffort;
                    pq.push({newEffort, nr, nc});
                }
            }
        }

        return 0;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna