// Method 1: Topological Sort (Cycle Detection) using Recursive DFS
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
class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // Build adjacency list: prereq -> course
        List<Integer>[] g = new ArrayList[numCourses];
        for (int i = 0; i < numCourses; i++) g[i] = new ArrayList<>();

        for (int[] p : prerequisites) {
            int course = p[0], prereq = p[1];
            g[prereq].add(course);
        }

        // 0 = unvisited, 1 = visiting, 2 = done
        int[] state = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0 && hasCycle(i, g, state)) {
                return false; // cycle found → cannot finish all courses
            }
        }
        return true; // no cycles
    }

    private boolean hasCycle(int u, List<Integer>[] g, int[] state) {
        if (state[u] == 1) return true;   // back-edge to node on current path → cycle
        if (state[u] == 2) return false;  // already fully processed

        state[u] = 1; // mark as visiting
        for (int v : g[u]) {
            if (hasCycle(v, g, state)) return true;
        }
        state[u] = 2; // mark as done
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
