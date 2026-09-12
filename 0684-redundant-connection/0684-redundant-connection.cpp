// Method 1: DSU by rank
/*
This is another great **DSU / Union-Find** problem.

The key idea is:

> As you process edges, the first edge connecting two nodes that are **already in the same component** is the redundant edge.

### Hint 1: Start with every node separate

Create a DSU for nodes `1...n`.

Since LeetCode node labels start at `1`, make your arrays size:

```cpp
n + 1
```

so you can use the node values directly.

### Hint 2: For each edge `[a, b]`

Find their roots:

```cpp
int rootA = find(a);
int rootB = find(b);
```

If:

```cpp
rootA == rootB
```

then `a` and `b` are already connected by some existing path.

Adding `[a, b]` would create a cycle.

That edge is your answer.

### Hint 3: Otherwise, union them

If the roots are different:

```text
different components
→ merge them
→ keep going
```

So your loop is conceptually:

```cpp
for (auto& edge : edges) {
    if (!unite(edge[0], edge[1])) {
        // this is the redundant edge
    }
}
```

This is why it’s convenient for `unite()` to return:

```text
true  = merge happened
false = already connected
```

### Hint 4: Reuse almost the exact DSU you just wrote

You already have:

```cpp
find(x)
unite(a, b)
```

with path compression and union by rank.

You do **not** need a `components` counter for this problem.

### Skeleton

```cpp
class Solution {
private:
    vector<int> parent;
    vector<int> rank;

    int find(int x) {
        // path compression
    }

    bool unite(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        // union by rank

        return true;
    }

public:
    vector<int> findRedundantConnection(vector<vector<int>>& edges) {
        int n = edges.size();

        parent.resize(n + 1);
        rank.resize(n + 1, 1);

        // initialize parent[i] = i

        for (auto& edge : edges) {
            // if union fails, return this edge
        }

        return {};
    }
};
```

The mental model is:

```text
If two nodes already share the same root,
then an edge between them closes a cycle.
```

So for:

```text
1 - 2
|   |
3---
```

when you process the last edge and both endpoints already have the same root, that edge is redundant.
*/
class Solution {
private:
    class DSU{
        private:
            vector<int> parent;
            vector<int> rank;

        public:
            DSU(int n){
                parent.resize(n+1);
                rank.resize(n+1, 1);

                for(int i=1; i<parent.size(); i++){
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


public:
    vector<int> findRedundantConnection(vector<vector<int>>& edges) {
        DSU dsu(edges.size());

        for(auto& edge : edges){
            if(!dsu.unite(edge[0], edge[1])){
                return edge;
            }
        }

        return {};
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna