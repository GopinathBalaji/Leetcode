// Method 1: Using BFS to trim the leaves
/*
This is a **“trim the leaves” BFS** problem.

The key idea is:

> The roots of minimum-height trees are the **center(s)** of the tree.

A tree has either **1 center** or **2 centers**.

### Hint 1: Think from the outside inward

Leaves are nodes with:

```cpp
degree == 1
```

If you remove all leaves at once, the next layer becomes the new leaves.

Keep doing that until only `1` or `2` nodes remain.

Those remaining nodes are the answer.

### Hint 2: Build an undirected graph + degree array

For every edge:

```cpp
[a, b]
```

add both directions:

```cpp
graph[a].push_back(b);
graph[b].push_back(a);

degree[a]++;
degree[b]++;
```

Use:

```cpp
vector<vector<int>> graph(n);
vector<int> degree(n, 0);
```

### Hint 3: Start with all current leaves

Push every node with:

```cpp
degree[i] == 1
```

into a queue.

```cpp
queue<int> q;
```

### Hint 4: Remove leaves level by level

Keep track of how many nodes remain:

```cpp
int remaining = n;
```

While:

```cpp
remaining > 2
```

remove the entire current leaf layer.

Something like:

```cpp
int size = q.size();
remaining -= size;

for (int i = 0; i < size; i++) {
    int leaf = q.front();
    q.pop();

    // update leaf's neighbor(s)
}
```

### Hint 5: Removing a leaf changes its neighbor's degree

For each neighbor of the removed leaf:

```cpp
degree[neighbor]--;
```

If that neighbor now becomes a leaf:

```cpp
if (degree[neighbor] == 1) {
    q.push(neighbor);
}
```

This is very similar to Kahn's algorithm, except you're peeling an **undirected tree** from the outside inward.

### Hint 6: Important edge case

If:

```cpp
n == 1
```

there are no edges and node `0` is the only possible root:

```cpp
return {0};
```

### Skeleton

```cpp
vector<int> findMinHeightTrees(int n, vector<vector<int>>& edges) {
    if (n == 1) {
        return {0};
    }

    vector<vector<int>> graph(n);
    vector<int> degree(n, 0);

    // build graph and degrees

    queue<int> q;

    // push all initial leaves

    int remaining = n;

    while (remaining > 2) {
        int size = q.size();
        remaining -= size;

        for (int i = 0; i < size; i++) {
            int leaf = q.front();
            q.pop();

            // reduce neighbors' degrees
            // enqueue new leaves
        }
    }

    // whatever remains in q is the answer
}
```

The mental model is:

```text
leaves
→ remove them
→ new leaves appear
→ remove them
→ continue until the center(s) remain
```

The biggest insight is that you do **not** need to compute the height for every possible root. Peel the tree inward instead.
*/
class Solution {
public:
    vector<int> findMinHeightTrees(int n, vector<vector<int>>& edges) {
        if(n == 1){
            return {0};
        }

        vector<vector<int>> graph(n);
        vector<int> degree(n, 0);

        for(auto& edge : edges){
            graph[edge[0]].push_back(edge[1]);
            graph[edge[1]].push_back(edge[0]);

            degree[edge[0]]++;
            degree[edge[1]]++;
        }

        queue<int> q;

        for(int node=0; node<degree.size(); node++){
            if(degree[node] == 1){
                q.push(node);
            }
        }

        int remaining = n;

        while(remaining > 2){
            int size = q.size();
            remaining -= size;

            for(int i=0; i<size; i++){
                int leaf = q.front();
                q.pop();

                for(auto& neighbor : graph[leaf]){
                    degree[neighbor]--;

                    if(degree[neighbor] == 1){
                        q.push(neighbor);
                    }
                }
            }
        }

        vector<int> ans;

        while(!q.empty()){
            int center = q.front();
            q.pop();

            ans.push_back(center);
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna