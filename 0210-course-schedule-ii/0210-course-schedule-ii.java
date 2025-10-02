// Method 1: DFS Cycle Detection and Postorder + Reversal for topological order
/*
## What was wrong with my implementation:

1. **You add nodes on DFS entry (preorder), not on exit (postorder).**
   In DFS topological sort with edges `prereq → course`, you must place a node **after** all of its outgoing neighbors have been fully processed. That means: **append on exit** (when `state[u]` goes to `2`).
   Adding on entry (`ans.add(u)` before exploring neighbors) produces an order that can put a course **before** its prerequisites.

2. **You don’t reverse the result (or push to a stack).**
   Classic DFS topo: push each node onto a stack on exit, then pop all → this is equivalent to “append on exit then reverse at the end”. Since you’re appending to a list, you must reverse it once all DFS calls finish.

3. **`toArray` misuse for primitives.**
   `ans.toArray(ret)` doesn’t work for `int[]`; it’s for object arrays (`Integer[]`). You need to manually copy to an `int[]`.

4. **(Minor) `ans` as a field can retain state if the same `Solution` instance is reused.**
   LeetCode usually instantiates per test, but it’s safer to keep `ans` local or clear it.


### Why this works

* Edges `prereq → course` mean “course depends on prereq”.
* Appending a node **after** all its neighbors are done guarantees **prereqs appear before courses** once you reverse the list.
* Cycle detection uses the 3-state coloring: hitting a `visiting` node again means a back-edge → cycle.

---

## Tiny walkthrough

`numCourses = 4`, `prerequisites = [[1,0],[2,0],[3,1],[3,2]]`

* Graph: `0→1`, `0→2`, `1→3`, `2→3`.
* DFS from `0`:

  * visit `1` → visit `3` → exit `3` (add `3`)
  * exit `1` (add `1`)
  * visit `2` → neighbor `3` already done → exit `2` (add `2`)
  * exit `0` (add `0`)
* `order` (postorder): `[3,1,2,0]`.
* Reverse → `[0,2,1,3]` (or `[0,1,2,3]` depending on traversal order).
  All valid topological orders have each course after its prereqs.

---

## Alternative: Kahn’s Algorithm (BFS)

For completeness, BFS topo (indegree queue) is also great and avoids recursion. Your original direction `prereq → course` works perfectly with Kahn too.

---

### Quick checklist for DFS topo

* Build edges `prereq → course`.
* 3-state color (0/1/2) to detect cycles.
* **Append on exit**, then **reverse** at the end (or push to stack on exit and pop).
* Convert `List<Integer>` to `int[]` manually.
*/
class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // Build graph: prereq -> course
        List<Integer>[] g = new ArrayList[numCourses];
        for (int i = 0; i < numCourses; i++) g[i] = new ArrayList<>();
        for (int[] p : prerequisites) {
            int course = p[0], prereq = p[1];
            g[prereq].add(course);
        }

        int[] state = new int[numCourses]; // 0=unvisited, 1=visiting, 2=done
        List<Integer> order = new ArrayList<>(numCourses);

        // DFS every component
        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0) {
                if (hasCycle(i, g, state, order)) {
                    return new int[0]; // cycle found: no valid ordering
                }
            }
        }

        // order currently holds nodes in reverse topological order (postorder append)
        Collections.reverse(order);

        // copy to int[]
        int[] res = new int[numCourses];
        for (int i = 0; i < numCourses; i++) res[i] = order.get(i);
        return res;
    }

    // returns true if a cycle is found
    private boolean hasCycle(int u, List<Integer>[] g, int[] state, List<Integer> order) {
        if (state[u] == 1) return true;   // back-edge: cycle
        if (state[u] == 2) return false;  // already processed

        state[u] = 1; // visiting
        for (int v : g[u]) {
            if (hasCycle(v, g, state, order)) return true;
        }
        state[u] = 2;       // done
        order.add(u);       // append on exit (postorder)
        return false;
    }
}





// Method 2: BFS (Kahn’s Algorithm)
/*
Here’s a clean **BFS (Kahn’s Algorithm)** solution for **LeetCode 210 — Course Schedule II** that returns a valid ordering (or `[]` if impossible).

### Key idea

* Build a graph with edges **prereq → course**.
* Track **indegree\[course]** = number of unmet prerequisites.
* Start with all courses with **indegree = 0** (ready to take).
* Repeatedly take a 0-indegree course, append it to the order, and decrement indegrees of its neighbors. Newly 0-indegree courses join the queue.
* If we output exactly `numCourses` courses → valid order; otherwise a cycle exists → return `[]`.


### Why this works

* Courses with **indegree 0** have all prerequisites satisfied.
* Taking such a course can only reduce indegrees of its dependents.
* If a cycle exists, some courses’ indegrees never drop to 0, so we output fewer than `numCourses` items.

### Complexity

* **Time:** `O(V + E)` where `V = numCourses`, `E = prerequisites.length`.
* **Space:** `O(V + E)` for the graph, indegree array, and queue.

### Quick example

`numCourses=4`, `prereqs=[[1,0],[2,0],[3,1],[3,2]]`
Graph: `0→1, 0→2, 1→3, 2→3`; indegrees: `[0,1,1,2]`
Queue starts with `[0]` → take `0`, indegrees → `[0,0,0,1]` → add `1,2`
Take `1` → indegrees `[0,0,0,0]` → add `3`
Take `2`, then `3` → order could be `[0,1,2,3]` (any valid topo order is accepted).
*/

// class Solution {
//     public int[] findOrder(int numCourses, int[][] prerequisites) {
//         // Build adjacency list: prereq -> list of courses that depend on it
//         List<Integer>[] graph = new ArrayList[numCourses];
//         for (int i = 0; i < numCourses; i++) graph[i] = new ArrayList<>();

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

//         int[] order = new int[numCourses];
//         int idx = 0;

//         while (!q.isEmpty()) {
//             int u = q.poll();
//             order[idx++] = u; // take course u

//             for (int v : graph[u]) {
//                 if (--indegree[v] == 0) q.offer(v);
//             }
//         }

//         // If we couldn't take all courses, there's a cycle → no valid order
//         if (idx < numCourses) return new int[0];
//         return order;
//     }
// }

