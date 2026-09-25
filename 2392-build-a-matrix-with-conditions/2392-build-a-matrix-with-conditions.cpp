// Method 1: Topological sort using Kahn's Algorithm
/*
This is a **Topological Sort + Graph Ordering** problem.

The key idea is that the **row constraints** and **column constraints** are completely independent.

You can find:

```text
a valid ordering of numbers from top → bottom
```

using `rowConditions`, and separately find:

```text
a valid ordering of numbers from left → right
```

using `colConditions`.

Then combine those two orderings to decide exactly where each number goes. The problem asks for any valid `k x k` matrix containing every number `1...k` exactly once, while respecting all row and column constraints. ([LeetCode][1])

### Hint 1: Think of each condition as a directed edge

For a row condition:

```cpp
[a, b]
```

you need:

```text
a above b
```

So create a directed edge:

```text
a → b
```

Similarly, for a column condition:

```cpp
[a, b]
```

meaning:

```text
a left of b
```

also create:

```text
a → b
```

The interpretation changes, but the graph problem is identical.

---

### Hint 2: What graph problem gives an ordering that respects all directed edges?

You want an ordering where:

```text
u → v
```

always means:

```text
u appears before v
```

That is exactly a **topological ordering**.

So write a reusable helper:

```cpp
vector<int> topoSort(
    int k,
    vector<vector<int>>& conditions
)
```

It should return an ordering of all numbers:

```text
1, 2, ..., k
```

that satisfies the given conditions.

---

### Hint 3: Kahn's algorithm works nicely here

Build:

```cpp
vector<vector<int>> adj(k + 1);
vector<int> indegree(k + 1);
```

For every condition:

```cpp
[u, v]
```

do:

```cpp
adj[u].push_back(v);
indegree[v]++;
```

Then put every node with:

```cpp
indegree[node] == 0
```

into a queue.

Repeatedly:

```text
remove node
→ add it to ordering
→ decrease indegree of its neighbors
→ if neighbor becomes 0, push it
```

---

### Hint 4: Don't forget numbers with no conditions

Suppose:

```text
k = 5
```

but number `4` never appears in `conditions`.

It still needs to appear in the matrix.

So your graph contains **all nodes from `1` to `k`**, not just nodes appearing in the condition list.

Initialize your Kahn's algorithm with:

```cpp
for (int x = 1; x <= k; x++) {
    if (indegree[x] == 0) {
        q.push(x);
    }
}
```

---

### Hint 5: How do you detect impossible conditions?

A topological sort cannot include every node if the graph contains a directed cycle.

For example:

```text
1 → 2
2 → 3
3 → 1
```

No ordering can satisfy all three.

After Kahn's algorithm:

```cpp
if (order.size() != k) {
    // cycle exists
}
```

You can return:

```cpp
{}
```

from your helper.

This is exactly why contradictory constraints can make the matrix impossible. ([LeetCode][1])

---

### Hint 6: Topologically sort the rows and columns separately

Do:

```cpp
vector<int> rowOrder = topoSort(k, rowConditions);
vector<int> colOrder = topoSort(k, colConditions);
```

If either one is empty:

```cpp
if (rowOrder.empty() || colOrder.empty()) {
    return {};
}
```

Why?

Because a cycle in the row constraints means there is no possible top-to-bottom arrangement.

A cycle in the column constraints means there is no possible left-to-right arrangement.

---

### Hint 7: What does `rowOrder` actually tell you?

Suppose:

```cpp
rowOrder = {3, 1, 2};
```

That means you can assign:

```text
row 0 → number 3
row 1 → number 1
row 2 → number 2
```

So build a position array:

```cpp
vector<int> rowPos(k + 1);

for (int i = 0; i < k; i++) {
    rowPos[rowOrder[i]] = i;
}
```

Now:

```cpp
rowPos[x]
```

tells you which row number `x` belongs in.

---

### Hint 8: Do the same thing for columns

Suppose:

```cpp
colOrder = {2, 3, 1};
```

Build:

```cpp
vector<int> colPos(k + 1);

for (int i = 0; i < k; i++) {
    colPos[colOrder[i]] = i;
}
```

Now:

```cpp
colPos[x]
```

tells you which column number `x` belongs in.

---

### Hint 9: Each number now has exactly one coordinate

Once you know:

```cpp
rowPos[x]
```

and:

```cpp
colPos[x]
```

the location of `x` is simply:

```cpp
matrix[rowPos[x]][colPos[x]] = x;
```

So initialize:

```cpp
vector<vector<int>> ans(
    k,
    vector<int>(k, 0)
);
```

and place every number:

```cpp
for (int x = 1; x <= k; x++) {
    ans[rowPos[x]][colPos[x]] = x;
}
```

This is the key trick.

You **don't** need to search over matrix cells.

The two topological sorts already determine one valid row and one valid column for every number.

---

### Hint 10: Why can't two numbers overwrite each other?

Suppose two different numbers `a` and `b` ended up in the same cell.

Then both would need:

```text
rowPos[a] == rowPos[b]
```

and:

```text
colPos[a] == colPos[b]
```

But `rowOrder` is a permutation of `1...k`, so every number gets a unique row position.

Likewise, `colOrder` gives every number a unique column position.

Therefore, different numbers cannot occupy the same coordinate.

---

### Skeleton

```cpp
class Solution {
private:
    vector<int> topoSort(
        int k,
        vector<vector<int>>& conditions
    ) {
        vector<vector<int>> adj(k + 1);
        vector<int> indegree(k + 1, 0);

        // Build directed graph:
        // u -> v

        queue<int> q;

        // Push every node with indegree 0

        vector<int> order;

        while (!q.empty()) {
            int node = q.front();
            q.pop();

            // add node to order

            // process neighbors
            // decrease indegree
            // push neighbor if indegree becomes 0
        }

        // If order does not contain all k nodes:
        // cycle -> impossible

        return order;
    }

public:
    vector<vector<int>> buildMatrix(
        int k,
        vector<vector<int>>& rowConditions,
        vector<vector<int>>& colConditions
    ) {
        // Topological order for rows

        // Topological order for columns

        // If either is impossible -> return {}

        // Build:
        // rowPos[number] = row
        // colPos[number] = column

        vector<vector<int>> ans(
            k,
            vector<int>(k, 0)
        );

        // For number = 1...k:
        // ans[rowPos[number]][colPos[number]] = number

        return ans;
    }
};
```

The core idea is:

```text
rowConditions
    ↓
topological sort
    ↓
row position of every number

colConditions
    ↓
topological sort
    ↓
column position of every number

combine:
number x → (rowPos[x], colPos[x])
```

The trickiest conceptual part is realizing that you **do not need to construct the matrix while processing the constraints**. Solve the two one-dimensional ordering problems independently, then combine their positions.
*/
class Solution {
private:    
    vector<int> topoSort(int k, vector<vector<int>>& conditions){
        vector<vector<int>> adj(k + 1);
        vector<int> indegree(k + 1);

        for(auto& condition : conditions){
            int u = condition[0];
            int v = condition[1];

            adj[u].push_back(v);
            indegree[v]++;
        }

        queue<int> q;

        for(int node=1; node<=k; node++){
            if(indegree[node] == 0){
                q.push(node);                
            }
        }

        vector<int> order;

        while(!q.empty()){
            int node = q.front();
            q.pop();

            order.push_back(node);

            for(int neighbor : adj[node]){
                indegree[neighbor]--;

                if(indegree[neighbor] == 0){
                    q.push(neighbor);
                }
            }
        }

        if(order.size() != k){
            return {};
        }

        return order;
    }

public:
    vector<vector<int>> buildMatrix(int k, vector<vector<int>>& rowConditions, vector<vector<int>>& colConditions) {
        vector<int> rowOrder = topoSort(k, rowConditions);
        vector<int> colOrder = topoSort(k, colConditions);

        if(rowOrder.empty() || colOrder.empty()){
            return {};
        }

        vector<int> rowPos(k + 1);
        for(int i=0; i<k; i++){
            rowPos[rowOrder[i]] = i;
        }

        vector<int> colPos(k + 1);
        for(int i=0; i<k; i++){
            colPos[colOrder[i]] = i;
        }

        vector<vector<int>> ans(k, vector<int>(k, 0));

        for(int x=1; x <= k; x++){
            ans[rowPos[x]][colPos[x]] = x;
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna