// Method 1: DFS Cycle Detection in Undirected Graphs
/*
This is a classic **undirected graph + cycle detection + connectivity** problem.

A graph is a valid tree if **both** are true:

* There are no cycles.
* Every node is connected.

### Hint 1: Build an undirected adjacency list

Each edge:

```cpp
[a, b]
```

goes both ways:

```cpp
graph[a].push_back(b);
graph[b].push_back(a);
```

So:

```cpp
vector<vector<int>> graph(n);
```

### Hint 2: Use DFS with a `visited` set

You want to traverse the graph starting from one node, for example:

```cpp
dfs(0, ...)
```

Mark nodes as visited as you encounter them.

But there's an important complication with **undirected** graphs.

Suppose:

```text
0 --- 1
```

DFS goes:

```text
0 → 1
```

From `1`, you'll see `0` as a neighbor again.

That's **not** a cycle—it's just the edge you came from.

### Hint 3: Track the parent

Pass the previous node into DFS:

```cpp
bool dfs(int node, int parent)
```

Then while examining neighbors:

```cpp
for (int neighbor : graph[node]) {
    if (neighbor == parent) {
        continue;
    }

    // ...
}
```

This avoids incorrectly treating the edge back to the parent as a cycle.

### Hint 4: How do you detect a real cycle?

After skipping the parent, if a neighbor has already been visited:

```cpp
if (visited.count(neighbor)) {
    return false;
}
```

you've found another way back to an existing node, which means there's a cycle.

For example:

```text
0 --- 1
|     |
└--2--┘
```

DFS could eventually encounter an already-visited node that isn't its parent.

### Hint 5: DFS skeleton

```cpp
bool dfs(int node,
         int parent,
         vector<vector<int>>& graph,
         unordered_set<int>& visited) {

    if (visited.count(node)) {
        // think about what this means
    }

    visited.insert(node);

    for (int neighbor : graph[node]) {

        if (neighbor == parent) {
            continue;
        }

        // detect cycle / recurse
    }

    return true;
}
```

### Hint 6: No cycle is not enough

Consider:

```text
0 --- 1

2 --- 3
```

There are no cycles, but this is **not** one tree because the graph is disconnected.

After DFS from `0`, check:

```cpp
visited.size() == n
```

If not all nodes were visited, return `false`.

### Hint 7: There's a useful early check

A tree with `n` nodes always has exactly:

```text
n - 1 edges
```

So you can immediately reject:

```cpp
if (edges.size() != n - 1) {
    return false;
}
```

Then run DFS to make sure everything is connected.

### Overall skeleton

```cpp
bool validTree(int n, vector<vector<int>>& edges) {

    if (edges.size() != n - 1) {
        return false;
    }

    vector<vector<int>> graph(n);

    // build undirected graph

    unordered_set<int> visited;

    // DFS from node 0
    // detect cycle using parent

    // make sure all n nodes were reached
}
```

The key mental model is:

```text
Valid Tree
    = connected
    + no cycles
```

And for undirected DFS, remember the important distinction:

```text
visited neighbor == parent
    → normal

visited neighbor != parent
    → cycle
```
*/
class Solution {
private:
    bool dfs(int node, int parent, vector<vector<int>>& graph, unordered_set<int>& visited){
        
        if(visited.count(node)){
            return false;
        }

        visited.insert(node);

        for(int neighbor : graph[node]){
            if(neighbor == parent){
                continue;
            }

            if(!dfs(neighbor, node, graph, visited)){
                return false;
            }
        }

        return true;
    }


public:
    bool validTree(int n, vector<vector<int>>& edges) {
        vector<vector<int>> graph(n);

        for(auto& edge : edges){
            graph[edge[0]].push_back(edge[1]);
            graph[edge[1]].push_back(edge[0]);
        }

        unordered_set<int> visited;

        if(!dfs(0, -1, graph, visited)){
            return false;
        }

        return visited.size() == n ? true : false;
    }
};
