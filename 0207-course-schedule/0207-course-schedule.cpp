// Method 1: Directed Graph Cycle-Detection
/*
This is a **directed graph cycle-detection** problem.

The key question is:

> Is there a cycle in the prerequisite graph?

If there is a cycle, you cannot finish all courses.

### Hint 1: Build a directed graph

For:

```cpp
prerequisites = [[1,0]]
```

this means:

```text
to take course 1, you must first take course 0
```

So create an edge:

```text
0 → 1
```

You can build:

```cpp
vector<vector<int>> graph(numCourses);
```

and for each:

```cpp
[a, b]
```

do:

```cpp
graph[b].push_back(a);
```

### Hint 2: Detect cycles with DFS

Each course can be in one of 3 states:

```text
0 = unvisited
1 = currently visiting
2 = fully processed
```

Use something like:

```cpp
vector<int> state(numCourses, 0);
```

The important idea:

> If DFS reaches a node that is already `1`, you found a cycle.

### Hint 3: Why does state `1` mean cycle?

Suppose:

```text
0 → 1 → 2 → 0
```

Your recursion looks like:

```text
visit 0
  visit 1
    visit 2
      visit 0 again
```

But `0` is still in the current recursion path.

So:

```cpp
state[0] == 1
```

means you've looped back into the current path → cycle.

### Hint 4: DFS structure

Think about:

```cpp
bool dfs(int course) {

    if (state[course] == 1) {
        return false; // cycle
    }

    if (state[course] == 2) {
        return true; // already checked
    }

    state[course] = 1;

    for (int next : graph[course]) {
        // dfs(next)
    }

    state[course] = 2;

    return true;
}
```

### Hint 5: Run DFS from every course

The graph may have disconnected components.

So don't just start from course `0`.

Do:

```cpp
for (int course = 0; course < numCourses; course++) {
    // run dfs if needed
}
```

If any DFS detects a cycle:

```cpp
return false;
```

Otherwise:

```cpp
return true;
```

### Skeleton

```cpp
class Solution {
private:
    bool dfs(int course,
             vector<vector<int>>& graph,
             vector<int>& state) {

        if (state[course] == 1) {
            return false;
        }

        if (state[course] == 2) {
            return true;
        }

        state[course] = 1;

        for (int next : graph[course]) {
            // if dfs fails, return false
        }

        state[course] = 2;

        return true;
    }

public:
    bool canFinish(int numCourses,
                   vector<vector<int>>& prerequisites) {

        vector<vector<int>> graph(numCourses);

        // build graph

        vector<int> state(numCourses, 0);

        // DFS every course

        return true;
    }
};
```

The core mental model is:

```text
Course Schedule = "Does this directed graph contain a cycle?"

cycle     → impossible → false
no cycle  → possible   → true
```

There’s also a very common **BFS / indegree / Kahn’s algorithm** solution, which is worth learning right after this DFS version.
*/
class Solution {
private:
    bool dfs(int course, vector<vector<int>>& graph, vector<int>& state){
        if(state[course] == 1){
            return false;
        }

        if(state[course] == 2){
            return true;
        }

        state[course] = 1;

        for(int next : graph[course]){
            if(!dfs(next, graph, state)){
                return false;
            }
        }

        state[course] = 2;

        return true;
    }

public:
    bool canFinish(int numCourses, vector<vector<int>>& prerequisites) {
        vector<vector<int>> graph(numCourses);

        for(auto& prereq : prerequisites){
            graph[prereq[1]].push_back(prereq[0]);
        }

        vector<int> state(numCourses, 0);

        for(int i=0; i<numCourses; i++){
            if(!dfs(i, graph, state)){
                return false;
            }
        }

        return true;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna