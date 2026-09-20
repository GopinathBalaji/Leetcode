// Method 1: Prim's Algorithm for Minimum Spanning Tree (MST)
/*
This is a **Minimum Spanning Tree (MST)** problem.

You have points as graph nodes, and the cost between any two points is their Manhattan distance:

```cpp
abs(x1 - x2) + abs(y1 - y2)
```

A very natural approach is **Prim’s algorithm**.

### Hint 1: Think “connect one new point at a time”

Start from any point, say point `0`.

Keep a set of points already included in the MST.

At every step:

> choose the cheapest edge that connects the current MST to some unvisited point.

That is exactly Prim’s algorithm.

### Hint 2: Use a min-heap

Store:

```text
{costToReachPoint, pointIndex}
```

For C++:

```cpp
priority_queue<
    pair<int,int>,
    vector<pair<int,int>>,
    greater<pair<int,int>>
> pq;
```

Start with:

```cpp
pq.push({0, 0});
```

because it costs `0` to include the starting point.

### Hint 3: Track visited points

Use:

```cpp
vector<bool> visited(n, false);
```

When you pop:

```cpp
auto [cost, node] = pq.top();
pq.pop();
```

if `node` is already visited:

```cpp
continue;
```

Otherwise:

```cpp
visited[node] = true;
totalCost += cost;
```

### Hint 4: From the newly added point, consider every other point

Because this graph is complete, every point connects to every other point.

So once you add point `node`, loop through:

```cpp
for (int next = 0; next < n; next++) {
```

If `next` is not visited, compute:

```cpp
int distance =
    abs(points[node][0] - points[next][0]) +
    abs(points[node][1] - points[next][1]);
```

and push:

```cpp
pq.push({distance, next});
```

### Hint 5: Stop after adding all `n` points

Track:

```cpp
int count = 0;
```

Each time you successfully visit a new node:

```cpp
count++;
```

Once:

```cpp
count == n
```

you can stop.

### Skeleton

```cpp
int minCostConnectPoints(vector<vector<int>>& points) {
    int n = points.size();

    vector<bool> visited(n, false);

    priority_queue<
        pair<int,int>,
        vector<pair<int,int>>,
        greater<pair<int,int>>
    > pq;

    pq.push({0, 0});

    int totalCost = 0;
    int count = 0;

    while (!pq.empty() && count < n) {
        auto [cost, node] = pq.top();
        pq.pop();

        if (visited[node]) {
            continue;
        }

        // mark visited
        // add cost
        // increment count

        for (int next = 0; next < n; next++) {
            if (!visited[next]) {
                // compute Manhattan distance
                // push into heap
            }
        }
    }

    return totalCost;
}
```

The mental model is:

```text
Prim's MST:
start anywhere
→ repeatedly take cheapest edge to an unvisited node
→ add that edge's cost
→ continue until all nodes are connected
```

This problem is especially convenient because you don’t need to build the full graph ahead of time; you can compute Manhattan distances on the fly.
*/
class Solution {
public:
    int minCostConnectPoints(vector<vector<int>>& points) {
        int n = points.size();

        priority_queue<pair<int, int>, vector<pair<int, int>>, greater<pair<int, int>>> pq;
        pq.push({0, 0});

        vector<bool> visited(n, false);

        int totalCost = 0;
        int count = 0;

        while(!pq.empty() && count < n){
            auto [cost, node] = pq.top();
            pq.pop();

            if(visited[node]){
                continue;
            }

            visited[node] = true;
            totalCost += cost;
            count++;


            for(int next=0; next<n; next++){
                if(!visited[next]){
                    int distance = std::abs(points[node][0] - points[next][0]) + std::abs(points[node][1] - points[next][1]);
                    pq.push({distance, next});
                }
            }
        }

        return totalCost;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna