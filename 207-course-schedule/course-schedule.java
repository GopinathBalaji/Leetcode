// Method 1: Topological Sort (Cycle Detection) using Recursive DFS (uses graph coloring)
/*
## What’s wrong in my previous attempt:

1. **Wrong visitation bookkeeping.**
   You use a single `visited` set to mean both “seen before” and “on the current recursion path.” That conflates two different states. You need **three states** (or two sets):

   * `0 = unvisited`
   * `1 = visiting` (on current DFS stack / path)
   * `2 = done` (fully explored)
     A cycle exists iff you reach a node that’s already **visiting**.

2. **You ignore DFS return values.**
   Inside the loop you call `dfs(...)` but you don’t check its result. If a child finds a cycle, you must **propagate `false` up** immediately.

3. **You only start DFS from one node.**
   Doing `dfs(..., prerequisites[0][0])` explores a single component. The graph can be **disconnected**. You must run DFS from **every course 0..numCourses-1** that’s unvisited.

4. **Edge direction & null checks.**
   Typical modeling is `prereq -> course` (i.e., to take `course`, you must take `prereq` first). Your code uses `u -> v` where `u=course, v=prereq`. Either direction works **if consistent**, but be careful when reasoning about cycles. Also, `graph.get(curr)` can be `null`.

---

## Correct recursive DFS = cycle detection with 3-state coloring


### Why this works

* **Cycle detection:** During DFS, encountering a **visiting** node means a back-edge → cycle.
* **Termination:** Nodes marked **done** won’t be re-explored.
* **Coverage:** We start DFS from every unvisited course, so disconnected components are handled.

### If you prefer two sets instead of colors

Use `onPath` (recursion stack) and `seen`:

* When you enter a node: add to `onPath`, add to `seen`.
* If you reach a neighbor already in `onPath` → cycle.
* When you exit a node: remove from `onPath`.

---

## Quick walkthrough

### Example 1 (cycle)

```
numCourses = 2
prerequisites = [[0,1],[1,0]]
Edges: 1 -> 0, 0 -> 1
```

* Start at 0: visiting(0) → go to 1: visiting(1) → neighbor 0 is visiting → **cycle** → return false.

### Example 2 (no cycle)

```
numCourses = 4
prerequisites = [[1,0],[2,0],[3,1],[3,2]]
Edges: 0->1, 0->2, 1->3, 2->3
```

* DFS(0): visit 1 (then 3), back, visit 2 (then 3 already done), mark 0 done → no cycles. Other nodes done/unvisited → return true.

---

## Common pitfalls to avoid

* Using one `visited` set for both “seen” and “on the current recursion stack.”
* Not checking/propagating the child DFS boolean result.
* Only exploring from one node.
* Forgetting to handle nodes with **no outgoing edges** (ensure adjacency lists exist for all courses).
* Building edges in the opposite direction and then reasoning as if it were prereq → course.

---

### Bonus: Kahn’s algorithm (BFS topological sort)

Another standard solution: compute indegrees and repeatedly pop 0-indegree nodes. If you process all `numCourses`, no cycle. If you can’t, there’s a cycle. (You asked for recursive DFS, so the code above sticks to that.)
*/
// class Solution {
//     public boolean canFinish(int numCourses, int[][] prerequisites) {
//         // Build adjacency list: prereq -> course
//         List<Integer>[] g = new ArrayList[numCourses];
//         for (int i = 0; i < numCourses; i++) g[i] = new ArrayList<>();

//         for (int[] p : prerequisites) {
//             int course = p[0], prereq = p[1];
//             g[prereq].add(course);
//         }

//         // 0 = unvisited, 1 = visiting, 2 = done
//         int[] state = new int[numCourses];

//         for (int i = 0; i < numCourses; i++) {
//             if (state[i] == 0 && hasCycle(i, g, state)) {
//                 return false; // cycle found → cannot finish all courses
//             }
//         }
//         return true; // no cycles
//     }

//     private boolean hasCycle(int u, List<Integer>[] g, int[] state) {
//         if (state[u] == 1) return true;   // back-edge to node on current path → cycle
//         if (state[u] == 2) return false;  // already fully processed

//         state[u] = 1; // mark as visiting
//         for (int v : g[u]) {
//             if (hasCycle(v, g, state)) return true;
//         }
//         state[u] = 2; // mark as done
//         return false;
//     }
// }








// Method 1.5: Similar approach as above but with 2 arrays instead of graph coloring
/*
################# WHAT WAS I DOING WRONG #####################
## 1) `visited[]` alone cannot detect cycles in a directed graph ❌

In Course Schedule, you must detect a **directed cycle**.

Using only one `visited[]` means:

* once a node is visited, you never explore it again
* but in directed graphs, this does **not** tell you whether you hit a cycle or just reached a node that was already fully processed via another path

What you need is a way to distinguish:

* **currently in the recursion path** (back edge ⇒ cycle)
  vs
* **already finished/processed** (safe)

That requires either:

* `onPath[]` / `recStack[]`, or
* 3-state coloring: `0=unvisited, 1=visiting, 2=done`

---

## 2) Your “else return ans” is wrong (it returns false for normal revisits) ❌

Inside DFS you do:

```java
boolean ans = false;

for (int neighbor : adjList[node]) {
    if (!visited[neighbor]) {
        ans = dfs(neighbor, adjList, visited);
    } else {
        return ans;   // ans is often false here
    }
}

ans = true;
return ans;
```

### Why this is wrong

* If a neighbor is already visited, that does **not** automatically mean “cycle” (could be a completed node from another branch).
* And you return `ans` which is initially `false`, so you can return `false` even when there’s **no cycle**.

### Concrete failure case (acyclic)

`0 -> 1`, `2 -> 1` (no cycle)

Start DFS(0):

* visit 0, DFS(1), finish 1, return true
  Start DFS(2):
* visit 2, neighbor 1 is already visited
* you hit the `else` and return `ans` (still false) ⇒ returns false incorrectly

So your code can fail even on DAGs.

---

## 3) Another bug: you overwrite `ans` and don’t propagate failures correctly ⚠️

You do:

```java
ans = dfs(neighbor, ...)
```

If one neighbor returns `false`, you should return `false` immediately.
Also if one neighbor returns `true`, you still need to check other neighbors.

Right now the logic doesn’t handle “one bad neighbor” correctly.

---

# What you should do instead (conceptual fix)

Use **two arrays**:

* `visited[]` = node fully processed
* `onPath[]` = node currently in recursion stack

Cycle rule:

* if you ever go to a neighbor that is `onPath[neighbor] == true`, you found a cycle

Or use color array:

* `0` unvisited
* `1` visiting (on recursion stack)
* `2` done

---

## Minimal correct DFS pattern (for reference)

(Showing the key idea; not changing your whole structure too much.)

```java
boolean dfs(int node) {
    if (state[node] == 1) return false; // back edge => cycle
    if (state[node] == 2) return true;  // already checked

    state[node] = 1; // visiting
    for (int nei : adj[node]) {
        if (!dfs(nei)) return false;
    }
    state[node] = 2; // done
    return true;
}
```

---

## Summary of what’s wrong

* ❌ `visited[]` alone can’t detect directed cycles
* ❌ You treat “visited neighbor” as failure and return `false` in DAGs
* ⚠️ You don’t properly propagate results across multiple neighbors
################################################################################

## Core idea

A course dependency graph is a **directed graph**.

* Each course = a node
* `prereq -> course` is a directed edge (because you must take `prereq` before `course`)

You can finish all courses **iff** this directed graph has **no directed cycle**.

Why?
A cycle like `0 -> 1 -> 2 -> 0` means “to take 0 you need 1, to take 1 you need 2, to take 2 you need 0” — impossible.

---

## Why we need TWO boolean arrays

### `visited[]` (a.k.a. “done/processed”)

* `visited[x] = true` means: **we have fully explored x and all its descendants**, and confirmed it does **not** lead to a cycle.
* If we reach a node that is already `visited`, we can skip it safely.

### `onPath[]` (a.k.a. recursion stack)

* `onPath[x] = true` means: **x is currently in the recursion call stack** (we are exploring a path that includes x right now).
* If during DFS we ever see an edge to a node that is `onPath == true`, that is a **back edge** → **cycle**.

**Cycle rule:**
If DFS explores edge `u -> v` and `onPath[v] == true`, then there is a cycle.


### What each state means

* `onPath[node] = true` when we enter DFS(node)
* `onPath[node] = false` when we leave DFS(node)
* `visited[node] = true` when DFS(node) is completely finished and confirmed safe

---

# Walkthrough 1: Graph WITH a cycle

### Example

`numCourses = 3`
`prerequisites = [[1,0],[2,1],[0,2]]`

Edges (prereq -> course):

* `0 -> 1`
* `1 -> 2`
* `2 -> 0`

This is a cycle: `0 -> 1 -> 2 -> 0`

### DFS trace

Start loop `i=0` (not visited):

#### Call `hasCycle(0)`

* `onPath[0]=true`

Neighbors of 0: `[1]`

##### Call `hasCycle(1)`

* `onPath[1]=true`

Neighbors of 1: `[2]`

###### Call `hasCycle(2)`

* `onPath[2]=true`

Neighbors of 2: `[0]`

####### Call `hasCycle(0)` again
Now check base cases:

* `onPath[0] == true` ✅
  That means 0 is already in the current recursion stack → **cycle found** → return `true`.

This `true` bubbles up:

* `hasCycle(2)` returns true
* `hasCycle(1)` returns true
* `hasCycle(0)` returns true
  So `canFinish` returns `false`.

✅ Correct.

---

# Walkthrough 2: Graph WITHOUT a cycle

### Example

`numCourses = 4`
`prerequisites = [[1,0],[2,0],[3,1],[3,2]]`

Edges:

* `0 -> 1`
* `0 -> 2`
* `1 -> 3`
* `2 -> 3`

No cycle.

### DFS trace (key points)

Start `i=0`:

#### `hasCycle(0)`

* `onPath[0]=true`
  Neighbors: `1, 2`

##### Explore `1`

`hasCycle(1)`

* `onPath[1]=true`
  Neighbors: `3`

###### Explore `3`

`hasCycle(3)`

* `onPath[3]=true`
  Neighbors: none

* finish 3: `onPath[3]=false`, `visited[3]=true`
  Return false (no cycle)

* finish 1: `onPath[1]=false`, `visited[1]=true`
  Return false

##### Explore `2`

`hasCycle(2)`

* `onPath[2]=true`
  Neighbors: `3`

###### Explore `3`

`hasCycle(3)`:

* `onPath[3]` is false

* `visited[3]` is true ✅
  So return false immediately (already known safe)

* finish 2: `onPath[2]=false`, `visited[2]=true`
  Return false

* finish 0: `onPath[0]=false`, `visited[0]=true`
  Return false

Loop continues `i=1,2,3` but all are visited, so done.

✅ `canFinish` returns true.

---

## Why this works (intuition)

* `onPath` detects **back edges** (which exactly represent directed cycles in DFS).
* `visited` prevents reprocessing nodes and keeps runtime efficient.

---

## Complexity

Let `V = numCourses`, `E = prerequisites.length`

* Time: `O(V + E)` (each node/edge is processed a constant number of times)
* Space: `O(V + E)` for adjacency list + recursion stack arrays
*/
class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // Build adjacency list: prereq -> course
        List<Integer>[] adj = new List[numCourses];
        for (int i = 0; i < numCourses; i++) adj[i] = new ArrayList<>();

        for (int[] p : prerequisites) {
            int course = p[0];
            int prereq = p[1];
            adj[prereq].add(course);
        }

        boolean[] visited = new boolean[numCourses]; // fully processed
        boolean[] onPath  = new boolean[numCourses]; // in current recursion stack

        for (int i = 0; i < numCourses; i++) {
            if (!visited[i]) {
                if (hasCycle(i, adj, visited, onPath)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasCycle(int node, List<Integer>[] adj,
                             boolean[] visited, boolean[] onPath) {

        // If we're revisiting a node already on the current DFS path => cycle
        if (onPath[node]) return true;

        // If already fully processed, no cycle from here
        if (visited[node]) return false;

        // Mark as being explored in current path
        onPath[node] = true;

        // Explore neighbors
        for (int nei : adj[node]) {
            if (hasCycle(nei, adj, visited, onPath)) {
                return true;
            }
        }

        // Done exploring this node
        onPath[node] = false;
        visited[node] = true;

        return false;
    }
}








// Method 2: Using Kahn’s Algorithm (BFS Topological Sort)
/*
### How it works

* Count indegrees (how many prerequisites each course still needs).
* Start with all courses whose indegree is 0 (ready to take).
* Repeatedly “take” them (pop from queue), and decrement indegrees of dependents.
* Any dependent hitting indegree 0 becomes ready and enters the queue.
* If you process all `numCourses`, there’s **no cycle** → `true`; otherwise a cycle blocked progress → `false`.

**Time:** `O(V + E)`
**Space:** `O(V + E)`

If you also want the **actual order** (for LC 210), you can push `u` into a list where we increment `taken`, and return that list if its size equals `numCourses`.
*/

// class Solution {
//     public boolean canFinish(int numCourses, int[][] prerequisites) {
//         // Build adjacency list: prereq -> list of courses that depend on it
//         List<Integer>[] graph = new ArrayList[numCourses];
//         for (int i = 0; i < numCourses; i++) graph[i] = new ArrayList<>();

//         // indegree[c] = number of prerequisites needed before taking course c
//         int[] indegree = new int[numCourses];

//         for (int[] p : prerequisites) {
//             int course = p[0], prereq = p[1];
//             graph[prereq].add(course);
//             indegree[course]++;
//         }

//         // Queue of courses with no remaining prerequisites
//         Deque<Integer> q = new ArrayDeque<>();
//         for (int c = 0; c < numCourses; c++) {
//             if (indegree[c] == 0) q.offer(c);
//         }

//         int taken = 0;

//         while (!q.isEmpty()) {
//             int u = q.poll();
//             taken++; // we can take this course now

//             for (int v : graph[u]) {
//                 if (--indegree[v] == 0) q.offer(v);
//             }
//         }

//         // If we managed to "take" all courses, there's no cycle
//         return taken == numCourses;
//     }
// }
