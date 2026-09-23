// Method 1: Shortest path with constraints (Bellman-Ford algorithm)
/*
This is a **shortest path with a constraint** problem.

The important twist is:

> You may use at most `k` stops, which means at most `k + 1` flights/edges.

A clean approach here is **Bellman-Ford with a limited number of relaxations**.

### Hint 1: Track cheapest cost to each city

Start with:

```cpp
vector<int> dist(n, INT_MAX);
dist[src] = 0;
```

`dist[i]` means the cheapest known cost to reach city `i`.

### Hint 2: Why `k + 1` rounds?

If:

```text
src -> A -> B -> dst
```

there are:

```text
2 stops: A, B
3 flights
```

So with at most `k` stops, you may take at most:

```text
k + 1 edges
```

Therefore, perform exactly `k + 1` rounds of edge relaxation.

### Hint 3: Don't update `dist` in-place during a round

This is the most important part.

For each round, first copy:

```cpp
vector<int> temp = dist;
```

Then use the **old `dist`** to relax edges into `temp`.

Why?

Because if you update `dist` immediately, one iteration could chain through multiple flights, accidentally using more than one new edge during the same round.

### Hint 4: Relax every flight

For:

```cpp
[u, v, price]
```

if `u` is reachable:

```cpp
if (dist[u] != INT_MAX) {
    temp[v] = min(temp[v], dist[u] + price);
}
```

After processing all flights:

```cpp
dist = temp;
```

### Hint 5: Skeleton

```cpp
int findCheapestPrice(int n,
                      vector<vector<int>>& flights,
                      int src,
                      int dst,
                      int k) {

    vector<int> dist(n, INT_MAX);
    dist[src] = 0;

    for (int i = 0; i <= k; i++) {

        vector<int> temp = dist;

        for (auto& flight : flights) {
            int u = flight[0];
            int v = flight[1];
            int price = flight[2];

            // if u is reachable:
            // try relaxing v into temp
        }

        dist = temp;
    }

    // return -1 if dst unreachable
}
```

The mental model is:

```text
round 1 → paths using at most 1 flight
round 2 → paths using at most 2 flights
...
round k+1 → paths using at most k+1 flights
```

The big trap is updating `dist` directly instead of using a temporary copy. That would break the stop constraint.
*/
class Solution {
public:
    int findCheapestPrice(int n, vector<vector<int>>& flights, int src, int dst, int k) {
        vector<int> dist(n, INT_MAX);
        dist[src] = 0;

        for(int i=0; i <= k; i++){
            vector<int> temp = dist;

            for(auto& flight : flights){
                int u = flight[0];
                int v = flight[1];
                int price = flight[2];

                if(dist[u] != INT_MAX){
                    temp[v] = std::min(temp[v], dist[u] + price);
                }
            }

            dist = temp;
        }   

        return dist[dst] == INT_MAX ? -1 : dist[dst];     
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna