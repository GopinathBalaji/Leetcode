// Checking cycle using DFS (Topological sort)
/*
Using a recStack to check if we visited the node in the currect DFS attempt.

### ✅ Example Directed Graph (with cycle):

```java
graph = {
    1 → [2],
    2 → [3],
    3 → [4, 5],
    4 → [],
    5 → [2],   // back edge to 2 creates a cycle: 2 → 3 → 5 → 2
    6 → []
}
```

### \U0001f9e0 Cycle:

* **2 → 3 → 5 → 2**
  Back edge from 5 to 2 → this is the cycle.

---

## \U0001fa9c Step-by-step Trace

### Initial state:

```java
visited = {}
recStack = {}
Start from node = 1
```

---

### Call: `dfs(1)`

* `visited = {1}`
* `recStack = {1}`
* neighbors of 1 → \[2]

---

### Call: `dfs(2)`

* `visited = {1, 2}`
* `recStack = {1, 2}`
* neighbors of 2 → \[3]

---

### Call: `dfs(3)`

* `visited = {1, 2, 3}`
* `recStack = {1, 2, 3}`
* neighbors of 3 → \[4, 5]

---

### Call: `dfs(4)`

* `visited = {1, 2, 3, 4}`
* `recStack = {1, 2, 3, 4}`
* neighbors of 4 → \[]

➡️ backtrack:

* `recStack = {1, 2, 3}`

---

### Back in `dfs(3)`, now visit `5`

### Call: `dfs(5)`

* `visited = {1, 2, 3, 4, 5}`
* `recStack = {1, 2, 3, 5}`
* neighbors of 5 → \[2]

Now we check:

```java
if (recStack.contains(2)) → true
```

### ✅ CYCLE FOUND: return true

---

### \U0001f9ed Recursion Tree

```
dfs(1)
  └── dfs(2)
        └── dfs(3)
              ├── dfs(4) ✅ returns false
              └── dfs(5)
                    └── dfs(2) ❌ already in recStack → CYCLE
```

---

### ✅ Key Insight:

`visited` tracks all nodes ever visited — it never shrinks.

`recStack` tracks nodes *on the current DFS path*. Once you backtrack (return from the call), you `recStack.remove(node)`.

So, for example:

* `4` is in `visited` but not in `recStack` anymore when we return to `dfs(3)`.
* Only `2 → 3 → 5` are in the **recStack** when the back edge is found.

---

### \U0001f501 After cycle:

Once cycle is detected, all recursive calls immediately return `true`.
*/
class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        
        HashMap<Integer, List<Integer>> graph = new HashMap<>();

        for(int i=0;i<prerequisites.length;i++){
            int from = prerequisites[i][1];
            int to = prerequisites[i][0];

            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(to);
        }

        // If cycle exists return false
        return !hasCycle(graph);
    }

    public boolean hasCycle(HashMap<Integer, List<Integer>> graph){
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recStack = new HashSet<>();

        for(int node: graph.keySet()){
            if(!visited.contains(node)){
                if(dfs(graph, node, visited, recStack)){
                    return true;
                }
            }
        }

        return false;
    }

    public boolean dfs(HashMap<Integer, List<Integer>> graph, int node, Set<Integer> visited, Set<Integer> recStack){
        visited.add(node);
        recStack.add(node);

        for(int neighbor: graph.getOrDefault(node, new ArrayList<>())){
            if(!visited.contains(neighbor)){
                if(dfs(graph, neighbor, visited, recStack)){
                    return true;
                }
            }else if(recStack.contains(neighbor)){
                return true; 
            }
        }

        // backtrack to remove the node from the current DFS recStack
        recStack.remove(node); 
        return false;
    }
}

// Using BFS to check if a cycle exists (Khan's Toplogical Sort Algorithm)
/*

---

### \U0001f9e0 **Kahn’s Algorithm: Intuition**

Kahn’s Algorithm is based on **topological sorting**. It only works on **Directed Acyclic Graphs (DAGs)**.

> If we can do a complete topological sort (i.e., visit all nodes), then the graph has **no cycle**.
> If some nodes remain unvisited, it means there's a **cycle** blocking us from continuing.

---

### ✅ Algorithm Steps:

1. Compute **in-degree** of all nodes.
2. Add all **nodes with in-degree 0** to a queue.
3. While the queue is not empty:

   * Pop a node.
   * Reduce the in-degree of its neighbors.
   * If a neighbor's in-degree becomes 0, add it to the queue.
4. Keep a **count** of how many nodes we visited.
5. If count == number of nodes → **no cycle**
   Else → **cycle exists**

---

### ✅ Java Code

```

---

### \U0001f9ea Example

Let’s reuse this graph:

```
graph = {
    1 → [2],
    2 → [3],
    3 → [4, 5],
    4 → [],
    5 → [2],   // cycle here: 2 → 3 → 5 → 2
    6 → []
}
```

### \U0001f4ca In-Degree Calculation:

| Node | In-degree     |
| ---- | ------------- |
| 1    | 0             |
| 2    | 2 (from 1, 5) |
| 3    | 1 (from 2)    |
| 4    | 1 (from 3)    |
| 5    | 1 (from 3)    |
| 6    | 0             |

* Start queue: `[1, 6]`
* After processing:

  * 1 → reduces in-degree of 2 → now 1
  * 6 → nothing
* **No new nodes added to queue**
* Loop ends after visiting 1 and 6 → `visitedCount = 2`

But total nodes = 6 → `2 != 6` → **Cycle exists**

---

### \U0001f9e0 Summary

| Method         | Technique         | Detects Cycle? | Can Show Cycle? | Use When                                                           |
| -------------- | ----------------- | -------------- | --------------- | ------------------------------------------------------------------ |
| **DFS**        | Recursive stack   | ✅              | Yes (back edge) | When you want to **trace** cycle path                              |
| **Kahn's BFS** | In-degree + queue | ✅              | No              | When checking if graph is a **DAG** or want a **topological sort** |
*/
// public class Solution {
//     public boolean hasCycle(HashMap<Integer, List<Integer>> graph) {
//         Map<Integer, Integer> inDegree = new HashMap<>();
//         Queue<Integer> queue = new LinkedList<>();

//         // Step 1: Initialize in-degree for all nodes
//         for (int node : graph.keySet()) {
//             inDegree.putIfAbsent(node, 0); // in case node has no incoming edges
//             for (int neighbor : graph.get(node)) {
//                 inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
//             }
//         }

//         // Step 2: Add nodes with in-degree 0 to the queue
//         for (int node : inDegree.keySet()) {
//             if (inDegree.get(node) == 0) {
//                 queue.offer(node);
//             }
//         }

//         // Step 3: BFS and count processed nodes
//         int visitedCount = 0;
//         while (!queue.isEmpty()) {
//             int curr = queue.poll();
//             visitedCount++;

//             for (int neighbor : graph.getOrDefault(curr, new ArrayList<>())) {
//                 inDegree.put(neighbor, inDegree.get(neighbor) - 1);
//                 if (inDegree.get(neighbor) == 0) {
//                     queue.offer(neighbor);
//                 }
//             }
//         }

//         // Step 4: If all nodes were processed, no cycle
//         return visitedCount != inDegree.size();
//     }
// }


