// Method 1: DSU by rank to find the number of connected components
/*
This problem is a perfect use case for **Disjoint Set Union (Union-Find)**.

The main idea is:

> Start with every node as its own connected component. Every time an edge successfully joins two different components, reduce the component count by 1.

### Hint 1: Initially, there are `n` components

With nodes:

```text
0  1  2  3  4
```

before processing any edges, each node is separate:

```text
{0} {1} {2} {3} {4}
```

So initialize:

```cpp
int components = n;
```

### Hint 2: DSU needs a `parent` array

Initially, every node is its own parent:

```cpp
vector<int> parent(n);

for (int i = 0; i < n; i++) {
    parent[i] = i;
}
```

Your `find(x)` operation should return the root/representative of the component containing `x`.

Conceptually:

```cpp
int find(int x) {
    if (parent[x] == x) {
        return x;
    }

    // recursively find root
}
```

Use **path compression**:

```cpp
parent[x] = find(parent[x]);
```

### Hint 3: `union(a, b)` should tell you whether a merge happened

For an edge:

```text
[a, b]
```

first find:

```cpp
rootA = find(a)
rootB = find(b)
```

If:

```cpp
rootA == rootB
```

they are already in the same component, so the edge changes nothing.

Otherwise, merge them.

A useful function signature is:

```cpp
bool unite(int a, int b)
```

where:

```text
true  = two components were merged
false = they were already connected
```

### Hint 4: Decrement only on a successful union

Suppose:

```text
n = 5
edges = [[0,1], [1,2], [3,4]]
```

Start:

```text
components = 5
```

Process:

```text
0-1  → different components → components = 4
1-2  → different components → components = 3
3-4  → different components → components = 2
```

Answer:

```text
2
```

But if another edge were:

```text
0-2
```

then `0` and `2` are already connected, so you **must not** decrement again.

### Hint 5: Use rank or size to keep the tree shallow

Maintain something like:

```cpp
vector<int> rank(n, 1);
```

When merging two roots, attach the smaller-rank tree under the larger-rank tree.

Conceptually:

```cpp
if (rank[rootA] < rank[rootB]) {
    parent[rootA] = rootB;
} else if (...) {
    ...
} else {
    ...
}
```

Path compression + union by rank makes DSU operations extremely fast.

### Skeleton

```cpp
class Solution {
private:
    vector<int> parent;
    vector<int> rank;

    int find(int x) {
        // find root
        // path compression
    }

    bool unite(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        // merge by rank

        return true;
    }

public:
    int countComponents(int n, vector<vector<int>>& edges) {
        parent.resize(n);
        rank.resize(n, 1);

        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        int components = n;

        for (auto& edge : edges) {
            if (unite(edge[0], edge[1])) {
                // what happens to components?
            }
        }

        return components;
    }
};
```

The key DSU pattern to remember is:

```text
start with n components

for every edge:
    if roots are different:
        union them
        components--

return components
```

That `components--` only after a **successful union** is the core trick for this problem.
*/
class Solution {
private:
    class DSU{
    private:
        int components;
        vector<int> parent;
        vector<int> rank;
    
    public:
        DSU(int n){
            components = n;
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

            components--;

            return true;
        }

        int getComponents(){
            return components;
        }
    };

public:
    int countComponents(int n, vector<vector<int>>& edges) {
        DSU dsu(n);

        for(auto& edge : edges){
            dsu.unite(edge[0], edge[1]);     
        }

        return dsu.getComponents();
    }
};
