// Method 1: MST + Kruskal + DSU
/*
This is an **MST + Kruskal + DSU** problem.

The key idea is to first compute the normal MST cost, then test every edge in two different ways.

### Hint 1: Preserve each edge’s original index

You’ll sort edges by weight, but the answer wants the original indices.

Convert:

```cpp
[u, v, weight]
```

into:

```cpp
[u, v, weight, originalIndex]
```

Then sort by `weight`.

---

### Hint 2: Write a reusable Kruskal helper

You want a helper that can compute an MST while optionally:

* **skipping** one edge
* **forcing** one edge to be included first

Think of something like:

```cpp
int kruskal(
    int n,
    vector<vector<int>>& edges,
    int skipEdge,
    int forceEdge
)
```

It should return the MST cost, or something like `INT_MAX` if you cannot connect all `n` nodes.

---

### Hint 3: First compute the normal MST cost

Run ordinary Kruskal:

```cpp
int baseCost = kruskal(n, edges, -1, -1);
```

This gives you the minimum possible spanning-tree cost.

You’ll compare every edge against this value.

---

### Hint 4: How do you identify a **critical** edge?

Take edge `i` and pretend it does not exist.

Run Kruskal while skipping it:

```cpp
int costWithout = kruskal(n, edges, i, -1);
```

If either:

```text
the graph can no longer form an MST
```

or:

```text
costWithout > baseCost
```

then that edge was necessary for every minimum spanning tree.

So it is **critical**.

Mental model:

```text
Remove edge
→ MST gets worse or becomes impossible
→ edge is critical
```

---

### Hint 5: How do you identify a **pseudo-critical** edge?

Now force edge `i` into the MST before running normal Kruskal.

Conceptually:

```cpp
dsu.unite(u, v);
cost += weight;
edgesUsed++;
```

Then continue Kruskal with the remaining edges.

If the final cost is still:

```cpp
baseCost
```

then there exists at least one MST containing this edge.

So the edge is **pseudo-critical**.

Mental model:

```text
Force edge into tree
→ can still achieve minimum MST cost
→ pseudo-critical
```

---

### Hint 6: Test critical first

For each edge:

```cpp
if (removing edge makes MST worse) {
    // critical
}
else if (forcing edge still gives base MST cost) {
    // pseudo-critical
}
```

An edge classified as critical should not also be added to pseudo-critical.

---

### Hint 7: Your Kruskal helper needs to know whether the graph is fully connected

Track:

```cpp
int edgesUsed = 0;
```

Every successful union:

```cpp
if (dsu.unite(u, v)) {
    cost += weight;
    edgesUsed++;
}
```

At the end, a spanning tree requires:

```cpp
edgesUsed == n - 1
```

Otherwise return something like:

```cpp
INT_MAX
```

---

### Skeleton

```cpp
class Solution {
private:
    class DSU {
        // parent
        // rank / size
        // find
        // unite
    };

    int kruskal(
        int n,
        vector<vector<int>>& edges,
        int skip,
        int force
    ) {
        DSU dsu(n);

        int cost = 0;
        int edgesUsed = 0;

        // If force != -1:
        //     add that edge first

        for (int i = 0; i < edges.size(); i++) {

            if (i == skip || i == force) {
                continue;
            }

            // try union
            // if successful:
            //     cost += weight
            //     edgesUsed++
        }

        // if edgesUsed != n - 1 → impossible

        return cost;
    }

public:
    vector<vector<int>> findCriticalAndPseudoCriticalEdges(
        int n,
        vector<vector<int>>& edges
    ) {
        // attach original indices

        // sort by weight

        // compute base MST cost

        // for every edge:
        //   test exclusion
        //   test forced inclusion

        // return {critical, pseudoCritical}
    }
};
```

The core classification rule is:

```text
Critical:
remove it → MST cost increases / impossible

Pseudo-critical:
force it → MST can still have the original minimum cost
```

The trickiest implementation detail is usually keeping the **original edge index** after sorting.
*/
class Solution {
private:
    class DSU {
    private:
        vector<int> parent;
        vector<int> rank;

    public:
        DSU(int n){
            parent.resize(n);
            rank.resize(n, 1);

            for(int i=0; i<n; i++){
                parent[i] = i;
            }
        }

        int find(int x){
            if(parent[x] == x){
                return x;
            }

            parent[x] = find(parent[x]);

            return parent[x];
        }

        bool unite(int a, int b){
            int rootA = find(a);
            int rootB = find(b);

            if(rootA == rootB){
                return false;
            }

            if(rank[rootA] < rank[rootB]){
                parent[rootA] = rootB;
            }else if(rank[rootB] < rank[rootA]){
                parent[rootB] = rootA;
            }else{
                parent[rootA] = rootB;
                rank[rootB]++;
            }

            return true;
        }
    };


    int kruskal(int n, vector<vector<int>>& edges, int skip, int force){
        DSU dsu(n);

        int cost = 0;
        int edgesUsed = 0;

        // Force this edge into the MST first
        if (force != -1) {
            int u = edges[force][0];
            int v = edges[force][1];
            int w = edges[force][2];

            if (dsu.unite(u, v)) {
                cost += w;
                edgesUsed++;
            }
        }

        // Normal Kruskal
        for (int i = 0; i < edges.size(); i++) {

            // Skip the excluded edge
            // Also skip the forced edge because we already added it
            if (i == skip || i == force) {
                continue;
            }

            int u = edges[i][0];
            int v = edges[i][1];
            int w = edges[i][2];

            if (dsu.unite(u, v)) {
                cost += w;
                edgesUsed++;
            }
        }

        // A spanning tree must contain exactly n - 1 edges
        if (edgesUsed != n - 1) {
            return INT_MAX;
        }

        return cost;
    }


public:
    vector<vector<int>> findCriticalAndPseudoCriticalEdges(int n, vector<vector<int>>& edges) {
         // Attach original indices
        for (int i = 0; i < edges.size(); i++) {
            edges[i].push_back(i);
        }

        // Sort by weight
        sort(edges.begin(), edges.end(),
            [](const vector<int>& a, const vector<int>& b) {
                return a[2] < b[2];
            });

        // Normal MST cost
        int baseCost = kruskal(n, edges, -1, -1);

        vector<int> critical;
        vector<int> pseudoCritical;

        for (int i = 0; i < edges.size(); i++) {

            // Test removing edge i
            int withoutEdge = kruskal(n, edges, i, -1);

            if (withoutEdge > baseCost) {
                critical.push_back(edges[i][3]);
            }
            else {
                // Test forcing edge i
                int withEdge = kruskal(n, edges, -1, i);

                if (withEdge == baseCost) {
                    pseudoCritical.push_back(edges[i][3]);
                }
            }
        }

        return {critical, pseudoCritical};
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna