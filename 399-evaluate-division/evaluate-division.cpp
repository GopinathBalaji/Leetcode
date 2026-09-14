// Method 1: Directed graph DFS
/*
This is a **weighted graph + DFS/BFS** problem.

The key idea is to treat each variable as a node, and each equation as a directed weighted edge.

### Hint 1: Convert equations into graph edges

If:

```text
a / b = 2.0
```

then create:

```text
a -> b   weight 2.0
b -> a   weight 1 / 2.0
```

So you might use:

```cpp
unordered_map<string, vector<pair<string, double>>> graph;
```

and add:

```cpp
graph["a"].push_back({"b", 2.0});
graph["b"].push_back({"a", 0.5});
```

### Hint 2: A query becomes a path search

Suppose:

```text
a / b = 2
b / c = 3
```

and the query is:

```text
a / c
```

Following the graph:

```text
a --2--> b --3--> c
```

Multiply the edge weights:

```text
2 * 3 = 6
```

So the answer is `6`.

That means during DFS, carry a running product.

### Hint 3: DFS state

A useful helper could look like:

```cpp
double dfs(
    string current,
    string target,
    double product,
    ...
)
```

If:

```cpp
current == target
```

then you've found the answer:

```cpp
return product;
```

### Hint 4: Avoid cycles

The graph can contain paths like:

```text
a -> b -> a
```

so keep a visited set:

```cpp
unordered_set<string> visited;
```

Before exploring:

```cpp
visited.insert(current);
```

and don't revisit already visited nodes.

### Hint 5: Multiply as you recurse

For every neighbor:

```cpp
for (auto& [next, weight] : graph[current]) {
```

recurse with:

```cpp
product * weight
```

So conceptually:

```cpp
double result = dfs(
    next,
    target,
    product * weight,
    ...
);
```

If that recursive call succeeds, return it.

### Hint 6: What indicates failure?

If no path exists, return:

```cpp
-1.0
```

Then during recursion:

```cpp
double result = dfs(...);

if (result != -1.0) {
    return result;
}
```

### Hint 7: Handle missing variables

For a query:

```text
x / y
```

if either `"x"` or `"y"` does not appear in the graph:

```cpp
return -1.0;
```

Also, if:

```text
a / a
```

and `a` exists, the answer is:

```text
1.0
```

### Skeleton

```cpp
double dfs(string current,
           string target,
           double product,
           unordered_map<string, vector<pair<string, double>>>& graph,
           unordered_set<string>& visited) {

    if (current == target) {
        return product;
    }

    visited.insert(current);

    for (auto& [next, weight] : graph[current]) {

        if (visited.count(next)) {
            continue;
        }

        // recurse with product * weight
        // if successful, return result
    }

    return -1.0;
}
```

Then for every query `[a, b]`:

```text
if a or b doesn't exist:
    -1

otherwise:
    DFS from a to b starting with product = 1.0
```

The main mental model is:

```text
equation = weighted edge
query = find a path
answer = multiply weights along that path
```

For example:

```text
a / b = 2
b / c = 3
c / d = 4
```

then:

```text
a / d = 2 × 3 × 4 = 24
```

That's the core of the problem.
*/
class Solution {
private:
    double dfs(string current, string target, double product, unordered_map<string, vector<pair<string, double>>>& graph, unordered_set<string>& visited){
        if(current == target){
            return product;
        }

        visited.insert(current);

        for(auto& [next, weight] : graph[current]){
            if(visited.count(next)){
                continue;
            }

            double result = dfs(next, target, product * weight, graph, visited);

            if(result != -1.0){
                return result;
            }
        }

        return -1.0;
    }


public:
    vector<double> calcEquation(vector<vector<string>>& equations, vector<double>& values, vector<vector<string>>& queries) {
        unordered_map<string, vector<pair<string, double>>> graph;

        for(int i=0; i<equations.size(); ++i){
            graph[equations[i][0]].push_back({equations[i][1], values[i]});
            graph[equations[i][1]].push_back({equations[i][0], 1 / values[i]});
        }

        vector<double> ans;

        for(auto& query : queries){
            string start = query[0];
            string target = query[1];

            if(!graph.count(start) || !graph.count(target)){
                ans.push_back(-1.0);
                continue;
            }


            unordered_set<string> visited;
            ans.push_back(dfs(start, target, 1, graph, visited));
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna