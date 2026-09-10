// Method 1: Reachability in Directed Graph using DFS
// (This problem is also a classic transitive closure problem, so Floyd–Warshall works nicely)
/*
This one is about **reachability in a directed graph**.

For each query `[u, v]`, you need to answer:

> Is `u` a prerequisite of `v` directly or indirectly?

So if:

```text
0 -> 1 -> 2
```

then `0` is also a prerequisite of `2`.

### Hint 1: Build a directed graph

For each prerequisite:

```cpp
[a, b]
```

add:

```cpp
graph[a].push_back(b);
```

because course `a` must come before course `b`.

### Hint 2: Think “can I reach v from u?”

Each query becomes a graph reachability question:

```text
[u, v]
```

means:

```text
Can DFS starting at u reach v?
```

A brute-force approach would run DFS for every query, but that can repeat a lot of work.

### Hint 3: Precompute reachability

A convenient structure is:

```cpp
vector<vector<bool>> reachable(
    numCourses,
    vector<bool>(numCourses, false)
);
```

Interpret:

```cpp
reachable[a][b] == true
```

as:

```text
a is a prerequisite of b
```

Then every query becomes `O(1)`:

```cpp
answer.push_back(reachable[u][v]);
```

### Hint 4: One simple approach is DFS from every course

For each course `start`:

```cpp
dfs(start, start)
```

and mark everything reachable from it:

```cpp
reachable[start][neighbor] = true;
```

Your DFS state can look like:

```cpp
void dfs(int start,
         int node,
         vector<vector<int>>& graph,
         vector<vector<bool>>& reachable)
```

Here:

* `start` stays fixed
* `node` changes as you traverse

### Hint 5: Avoid revisiting the same relation

Before recursing:

```cpp
if (reachable[start][neighbor]) {
    continue;
}
```

Then:

```cpp
reachable[start][neighbor] = true;
dfs(start, neighbor, graph, reachable);
```

That prevents repeated exploration.

### Skeleton

```cpp
void dfs(int start,
         int node,
         vector<vector<int>>& graph,
         vector<vector<bool>>& reachable) {

    for (int next : graph[node]) {

        if (reachable[start][next]) {
            continue;
        }

        // mark start -> next

        // recurse from next
    }
}
```

Then:

```cpp
for (int course = 0; course < numCourses; course++) {
    dfs(course, course, graph, reachable);
}
```

Finally answer each query using the matrix.

### Another approach to recognize

This problem is also a classic **transitive closure** problem, so Floyd–Warshall works nicely:

```text
if i can reach k
and k can reach j
then i can reach j
```

That gives an `O(n^3)` solution, which is simple and often clean for this problem.

The main mental model is:

```text
Course Schedule IV
= precompute which nodes can reach which other nodes
= answer queries from that reachability table
```

Given the graph problems you've been doing, I'd try the **DFS-from-each-node + reachable matrix** approach first.
*/
class Solution {
private:
    void dfs(int start, int node, vector<vector<int>>& graph, vector<vector<bool>>& reachable){
        for(int next: graph[node]){
            if(reachable[start][next]){
                continue;
            }

            reachable[start][next] = true;

            dfs(start, next, graph, reachable);
        }
    }

public:
    vector<bool> checkIfPrerequisite(int numCourses, vector<vector<int>>& prerequisites, vector<vector<int>>& queries) {
        vector<vector<int>> graph(numCourses);

        vector<bool> ans;

        for(auto& prereq : prerequisites){
            graph[prereq[0]].push_back(prereq[1]);
        }

        vector<vector<bool>> reachable(numCourses, vector<bool>(numCourses, false));

        for(int course = 0; course < numCourses; course++){
            dfs(course, course, graph, reachable);
        }

        for(auto& query : queries){
            if(reachable[query[0]][query[1]]){
                ans.push_back(true);
            }else{
                ans.push_back(false);
            }
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna