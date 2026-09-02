// Method 1: Multi-source BFS
/*
This is a classic **multi-source BFS** problem.

You have three kinds of cells:

```text
-1   = water / wall
0    = treasure
INF  = empty land
```

Your goal is to replace each empty land cell with its distance to the **nearest treasure**.

### Hint 1: Don’t BFS from every empty cell

A tempting approach is:

```text
for every empty cell:
    search for nearest treasure
```

That repeats a lot of work.

Instead, reverse the perspective:

> Start BFS from **all treasure cells at the same time**.

That way, the first time BFS reaches an empty cell, it must be through the nearest treasure.

### Hint 2: Put all treasure cells into the queue first

Scan the grid:

```cpp
queue<pair<int, int>> q;

for (...) {
    for (...) {
        if (grid[r][c] == 0) {
            q.push({r, c});
        }
    }
}
```

This is what makes it **multi-source BFS**.

### Hint 3: Expand outward level by level

For each cell popped from the queue, explore:

```text
up
down
left
right
```

For a neighbor, only visit it if it is still empty land.

Something like:

```cpp
if (grid[nr][nc] == INF) {
    // update its distance
    // push it into queue
}
```

Do not walk into:

```text
-1  walls/water
0   treasure
```

or cells you've already updated.

### Hint 4: You don’t need a separate `visited`

The grid itself can tell you whether a cell has been visited.

Initially empty cells contain `INF`.

Once you reach one:

```cpp
grid[nr][nc] = grid[r][c] + 1;
```

Now it is no longer `INF`, so you won't process it again.

### Hint 5: Why `grid[r][c] + 1`?

Treasure cells start with:

```text
0
```

Their neighbors become:

```text
1
```

Then the next layer becomes:

```text
2
```

and so on.

So BFS naturally writes the shortest distance directly into the grid.

### Skeleton

```cpp
void islandsAndTreasure(vector<vector<int>>& grid) {
    int rows = grid.size();
    int cols = grid[0].size();

    queue<pair<int, int>> q;

    // 1. Add every treasure cell to q

    int directions[4][2] = {
        {1, 0},
        {-1, 0},
        {0, 1},
        {0, -1}
    };

    // 2. BFS
    while (!q.empty()) {
        auto [r, c] = q.front();
        q.pop();

        for (auto& dir : directions) {
            int nr = r + dir[0];
            int nc = c + dir[1];

            // bounds check

            // only process INF cells

            // set distance = current distance + 1

            // push neighbor
        }
    }
}
```

The key mental model is:

```text
all treasures enter queue first
→ BFS expands outward from all of them simultaneously
→ first time an empty cell is reached = shortest distance to a treasure
```

This is the same pattern used in problems like **Rotting Oranges** and other “distance from nearest source” grid problems.
*/
class Solution {
public:
    void islandsAndTreasure(vector<vector<int>>& grid) {
        const int INF = 2147483647;
        queue<pair<int, int>> q;

        for(int i=0; i<grid.size(); i++){
            for(int j=0; j<grid[0].size(); j++){
                if(grid[i][j] == 0){
                    q.push({i, j});
                }
            }
        }

        int directions[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while(!q.empty()){
            auto [r, c] = q.front();
            q.pop();

            for(auto& dir : directions){
                int nr = r + dir[0];
                int nc = c + dir[1];

                if(nr < 0 || nr >= grid.size() || nc < 0 || nc >= grid[0].size()){
                    continue;
                }

                if(grid[nr][nc] == INF){
                    grid[nr][nc] = grid[r][c] + 1;
                    q.push({nr, nc});
                }
            }
        }
    }
};
