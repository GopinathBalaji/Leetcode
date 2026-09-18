// Method 1: Dijkstra’s shortest path problem on a directed weighted graph
/*
This is a classic **Dijkstra’s shortest path** problem on a directed weighted graph.

The key question is:

> Starting from node `k`, how long does it take for the signal to reach every node?

### Hint 1: Build a directed weighted graph

Each entry:

```cpp
[u, v, w]
```

means:

```text
u -> v
cost = w
```

A useful adjacency list:

```cpp
vector<vector<pair<int, int>>> graph(n + 1);
```

where each pair stores:

```text
{neighbor, weight}
```

So:

```cpp
graph[u].push_back({v, w});
```

### Hint 2: Track the shortest known distance

Create:

```cpp
vector<int> dist(n + 1, INT_MAX);
```

Since the signal starts at `k`:

```cpp
dist[k] = 0;
```

### Hint 3: Use a min-heap

Dijkstra needs the node with the smallest current distance first.

Store:

```text
{distance, node}
```

with:

```cpp
priority_queue<
    pair<int,int>,
    vector<pair<int,int>>,
    greater<pair<int,int>>
> pq;
```

Start with:

```cpp
pq.push({0, k});
```

### Hint 4: Relax every outgoing edge

When you pop:

```cpp
auto [currentDist, node] = pq.top();
pq.pop();
```

for every:

```cpp
[next, weight]
```

calculate:

```cpp
int newDist = currentDist + weight;
```

If:

```cpp
newDist < dist[next]
```

then you've found a better path:

```cpp
dist[next] = newDist;
pq.push({newDist, next});
```

### Hint 5: Skip stale heap entries

A node may be pushed into the heap multiple times as you discover better paths.

So after popping:

```cpp
if (currentDist > dist[node]) {
    continue;
}
```

This means that heap entry is outdated.

### Hint 6: How do you get the final answer?

After Dijkstra finishes, `dist[i]` tells you when node `i` receives the signal.

The signal reaches **everyone** only when the slowest node receives it.

So think:

```cpp
answer = max(dist[1], dist[2], ..., dist[n]);
```

But if any node still has:

```cpp
INT_MAX
```

then that node was unreachable, so return:

```cpp
-1
```

### Skeleton

```cpp
int networkDelayTime(vector<vector<int>>& times, int n, int k) {
    vector<vector<pair<int,int>>> graph(n + 1);

    // build graph

    vector<int> dist(n + 1, INT_MAX);
    dist[k] = 0;

    priority_queue<
        pair<int,int>,
        vector<pair<int,int>>,
        greater<pair<int,int>>
    > pq;

    pq.push({0, k});

    while (!pq.empty()) {
        auto [currentDist, node] = pq.top();
        pq.pop();

        // skip stale entries

        for (auto& [next, weight] : graph[node]) {

            // calculate new distance

            // relax if better
        }
    }

    // find maximum shortest distance
    // if any node unreachable -> -1
}
```

The key difference from **1631 Path With Minimum Effort** is the relaxation formula:

```text
1631:
newCost = max(currentCost, edgeCost)

743:
newCost = currentCost + edgeCost
```

Everything else is essentially standard Dijkstra.
*/
class Solution {
public:
    int networkDelayTime(vector<vector<int>>& times, int n, int k) {
        vector<vector<pair<int, int>>> graph(n + 1);

        for(auto& time : times){
            int u = time[0];
            int v = time[1];
            int w = time[2];

            graph[u].push_back({v, w});
        }

        vector<int> dist(n + 1, INT_MAX);
        dist[k] = 0;

        priority_queue<pair<int, int>, vector<pair<int, int>>, greater<pair<int, int>>> pq;
        pq.push({0, k});

        while(!pq.empty()){
            auto [currentDist, node] = pq.top();
            pq.pop();

            if(currentDist > dist[node]){
                continue;
            }

            for(auto& [next, weight] : graph[node]){
                int newDist = currentDist + weight;

                if(newDist < dist[next]){
                    dist[next] = newDist;
                    pq.push({newDist, next});
                }
            }
        }

        int ans = 0;

        for(int i=1; i<=n; i++){
            ans = std::max(ans, dist[i]);
        }

        return ans == INT_MAX ? -1 : ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna