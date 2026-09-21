// Method 1: Dijkstra on a grid
/*
This is very similar to **1631. Path With Minimum Effort**. Think **Dijkstra on a grid**, but your path cost is the maximum elevation seen so far.

### Hint 1: What does a path “cost”?

If you follow cells with heights:

```text
0 -> 2 -> 5 -> 3
```

you cannot finish that path until the water level reaches:

```text
5
```

So the cost of a path is:

```text
maximum grid value on that path
```

### Hint 2: Use Dijkstra

For each cell, track the minimum water level needed to reach it:

```cpp
vector<vector<int>> dist(
    n,
    vector<int>(n, INT_MAX)
);
```

At the start:

```cpp
dist[0][0] = grid[0][0];
```

Use a min-heap storing:

```text
{requiredWaterLevel, row, col}
```

### Hint 3: The relaxation formula is the key

Suppose you're at a cell with:

```cpp
currentTime
```

and the neighbor has height:

```cpp
grid[nr][nc]
```

To reach that neighbor, the required water level becomes:

```cpp
int newTime = max(currentTime, grid[nr][nc]);
```

Not addition.

That is the central idea.

### Hint 4: Relax if this path is better

If:

```cpp
newTime < dist[nr][nc]
```

then:

```cpp
dist[nr][nc] = newTime;
pq.push({newTime, nr, nc});
```

### Hint 5: Early exit

Because Dijkstra always pops the smallest currently possible water level, once you pop:

```text
(n - 1, n - 1)
```

you can return that cost immediately.

### Skeleton

```cpp
int swimInWater(vector<vector<int>>& grid) {
    int n = grid.size();

    vector<vector<int>> dist(
        n,
        vector<int>(n, INT_MAX)
    );

    priority_queue<
        tuple<int,int,int>,
        vector<tuple<int,int,int>>,
        greater<tuple<int,int,int>>
    > pq;

    dist[0][0] = grid[0][0];
    pq.push({grid[0][0], 0, 0});

    while (!pq.empty()) {
        auto [time, r, c] = pq.top();
        pq.pop();

        // skip stale entries

        // if destination -> return time

        // explore 4 neighbors

        // newTime = max(time, grid[nr][nc])

        // relax if better
    }

    return -1;
}
```

The key comparison to remember is:

```text
1631 Minimum Effort:
newCost = max(currentCost, abs(height difference))

778 Swim in Rising Water:
newCost = max(currentCost, neighbor height)
```

Same Dijkstra pattern, different path-cost formula.
*/
class Solution {
public:
    int swimInWater(vector<vector<int>>& grid) {
        int n = grid.size();

        vector<vector<int>> dist(n, vector<int>(n, INT_MAX));
        dist[0][0] = grid[0][0];

        int dirs[4][2] = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

        priority_queue<tuple<int, int, int>, vector<tuple<int, int, int>>, greater<tuple<int, int, int>>> pq;

        pq.push({grid[0][0], 0, 0});

        while(!pq.empty()){
            auto [time, r, c] = pq.top();
            pq.pop();

            if(time > dist[r][c]){
                continue;
            }

            if(r == n-1 && c == n-1){
                return time;
            }

            for(auto dir : dirs){
                int nr = r + dir[0];
                int nc = c + dir[1];

                if(nr < 0 || nr >= n || nc < 0 || nc >= n){
                    continue;
                }

                int newTime = std::max(time, grid[nr][nc]);

                if(newTime < dist[nr][nc]){
                    dist[nr][nc] = newTime;
                    pq.push({newTime, nr, nc});
                }
            }
        }

        return -1;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna