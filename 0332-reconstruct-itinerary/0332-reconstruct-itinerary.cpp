// Method 1: graph traversal + Eulerian path (Hierholzer’s algorithm)
/*
This problem is a **graph traversal + Eulerian path** problem.

The key requirement is:

> Use every ticket exactly once, starting from `"JFK"`.

That’s a strong hint toward **Hierholzer’s algorithm**.

### Hint 1: Build a directed graph

Each ticket:

```cpp
[from, to]
```

is a directed edge:

```text
from -> to
```

You need to preserve lexical order among possible destinations.

A convenient structure is:

```cpp
unordered_map<string, priority_queue<
    string,
    vector<string>,
    greater<string>
>> graph;
```

So for each ticket:

```cpp
graph[from].push(to);
```

The min-heap ensures you try the lexicographically smallest destination first.

### Hint 2: Don’t add airports to the result immediately

This is the tricky part.

Suppose you are at `"JFK"`. You keep following unused tickets until you reach an airport with **no outgoing tickets left**.

Only then should you add that airport to your itinerary.

Conceptually:

```cpp
void dfs(string airport) {
    while (graph[airport] is not empty) {
        string next = smallest destination;

        remove that ticket;

        dfs(next);
    }

    result.push_back(airport);
}
```

Notice that `push_back()` happens **after** exploring all outgoing edges.

### Hint 3: Why remove the edge?

Every ticket can only be used once.

So when you take:

```text
JFK -> ATL
```

remove `"ATL"` from JFK's heap before recursing.

That ticket is now consumed.

### Hint 4: Why is the result backwards?

Consider:

```text
JFK -> A
A   -> B
```

DFS works like:

```text
JFK
  A
    B
```

At `B`, there are no outgoing edges:

```text
result = [B]
```

Return to `A`:

```text
result = [B, A]
```

Return to `JFK`:

```text
result = [B, A, JFK]
```

So after DFS:

```cpp
reverse(result.begin(), result.end());
```

gives:

```text
JFK, A, B
```

### Hint 5: Skeleton

```cpp
class Solution {
private:
    unordered_map<
        string,
        priority_queue<string, vector<string>, greater<string>>
    > graph;

    vector<string> itinerary;

    void dfs(string airport) {

        while (!graph[airport].empty()) {

            // get lexicographically smallest destination

            // remove the ticket

            // dfs(next)
        }

        // add airport AFTER using all outgoing tickets
    }

public:
    vector<string> findItinerary(vector<vector<string>>& tickets) {

        // build graph

        dfs("JFK");

        // reverse itinerary

        return itinerary;
    }
};
```

The main mental model is:

```text
Use every edge exactly once
→ keep consuming tickets
→ when stuck, add airport to answer
→ reverse at the end
```

The subtle part is **postorder traversal**: adding the airport after exploring its outgoing flights, not before.
*/
class Solution {
private:
    void dfs(unordered_map<string, priority_queue<string, vector<string>, greater<string>>>& graph, vector<string>& result, string airport){
        while(!graph[airport].empty()){
            string next = graph[airport].top();
            graph[airport].pop();

            dfs(graph, result, next);
        }

        result.push_back(airport);
    }

public:
    vector<string> findItinerary(vector<vector<string>>& tickets) {
        unordered_map<string, priority_queue<string, vector<string>, greater<string>>> graph;

        for(auto& ticket : tickets){
            string from = ticket[0];
            string to = ticket[1];

            graph[from].push(to);
        }

        vector<string> result;

        dfs(graph, result, "JFK");

        std::reverse(result.begin(), result.end());

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna