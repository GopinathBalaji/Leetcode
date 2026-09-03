// Method 1: Multisource BFS
/*
This is another **multi-source BFS** problem, very similar to **Islands and Treasure**.

### Hint 1: Start from all rotten oranges

Every rotten orange can spread at the same time, so first scan the grid and add every rotten orange to a queue:

```cpp
queue<pair<int, int>> q;
```

Also count how many fresh oranges exist:

```cpp
int fresh = 0;
```

### Hint 2: Why multi-source BFS?

If you start BFS from only one rotten orange, you won’t correctly model all oranges rotting simultaneously.

Instead:

```text
minute 0: all initially rotten oranges
minute 1: their fresh neighbors rot
minute 2: next layer rots
...
```

So put **all cells with value `2`** into the queue before BFS starts.

### Hint 3: Process BFS by levels

Each BFS level represents **one minute**.

A common pattern:

```cpp
while (!q.empty() && fresh > 0) {
    int size = q.size();

    for (int i = 0; i < size; i++) {
        // process one rotten orange
        // rot its fresh neighbors
    }

    minutes++;
}
```

The important part is using:

```cpp
int size = q.size();
```

before processing the level.

### Hint 4: Only spread to fresh oranges

For each rotten orange, check:

```text
up
down
left
right
```

If a neighbor contains:

```cpp
grid[nr][nc] == 1
```

then:

```cpp
grid[nr][nc] = 2;
fresh--;
q.push({nr, nc});
```

Changing it to `2` immediately also acts as your `visited` marking.

### Hint 5: Final condition

After BFS finishes, ask:

```text
Are there any fresh oranges left?
```

If:

```cpp
fresh > 0
```

then some fresh orange was unreachable, so return:

```cpp
-1
```

Otherwise return the number of minutes.

### Hint 6: Important edge case

Suppose the grid contains no fresh oranges initially.

Example:

```text
[0, 2]
```

The answer should be:

```text
0
```

So initialize:

```cpp
int minutes = 0;
```

and only increment it when you actually process another spreading round.

### Skeleton

```cpp
int orangesRotting(vector<vector<int>>& grid) {
    int rows = grid.size();
    int cols = grid[0].size();

    queue<pair<int, int>> q;
    int fresh = 0;

    // Scan grid:
    // 2 -> push into queue
    // 1 -> fresh++

    int minutes = 0;

    while (!q.empty() && fresh > 0) {
        int size = q.size();

        for (int i = 0; i < size; i++) {
            auto [r, c] = q.front();
            q.pop();

            // explore 4 directions

            // if neighbor is fresh:
            //     make rotten
            //     fresh--
            //     push into queue
        }

        minutes++;
    }

    // if fresh remains -> impossible
    // otherwise return minutes
}
```

The main mental model is:

```text
All rotten oranges = BFS sources
One BFS layer = one minute
Fresh orange reached = rot it and enqueue it
Fresh oranges left at the end = return -1
```
*/
class Solution {
public:
    int orangesRotting(vector<vector<int>>& grid) {
        int rows = grid.size();
        int cols = grid[0].size();

        queue<pair<int, int>> q;

        int fresh = 0;

        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(grid[i][j] == 2){
                    q.push({i, j});
                }else if(grid[i][j] == 1){
                    fresh++;
                }
            }
        }

        int dirs[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        int minutes = 0;

        while(!q.empty() && fresh > 0){
            int size = q.size();

            for(int i=0; i<size; i++){
                auto [r, c] = q.front();
                q.pop();

                for(auto& dir : dirs){
                    int nr = r + dir[0];
                    int nc = c + dir[1];

                    if(nr < 0 || nr >= rows || nc < 0 || nc >= cols){
                        continue;
                    }

                    if(grid[nr][nc] == 1){
                        grid[nr][nc] = 2;
                        fresh--;
                        q.push({nr, nc});
                    }
                }
            }

            minutes++;
        }


        return fresh > 0 ? -1 : minutes;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna