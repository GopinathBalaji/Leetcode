// Method 1: Using DSU / Union Find
/*
# What I was doing wrong:

1. **Wrong DSU size / indexing**

* In this problem the nodes are labeled **1..N**, and **N = edges.length**.
* You construct `new DSU(edges.length - 1)`, so your DSU arrays are too small.
  When an edge contains node `N`, `find(N)` will access `parent[N]` **out of bounds** (or at best leave it uninitialized).
* Also, you initialize parents for `i = 1..n`, but since you passed `n = edges.length - 1`, node `N` never gets initialized.

2. **`components = n+1` is incorrect**

* Not used here, but it should be `components = n` if you keep it.

Everything else (union-by-rank, path compression, “keep the last failing edge”) is fine.


## Quick walkthrough

Example: `[[1,2],[1,3],[2,3]]`

* Union(1,2) → OK
* Union(1,3) → OK
* Union(2,3) → `find(2)==find(3)` ⇒ **fails** ⇒ redundant = `[2,3]` (returned)

Your original code would fail when encountering node label `3` with DSU sized for `2`.
*/

class Solution {
    public int[] findRedundantConnection(int[][] edges) {
        int[] ans = null;
        DSU dsu = new DSU(edges.length);

        for(int[] e: edges){
            if(!dsu.union(e[0], e[1])){
                ans = e;
            }
        }

        return ans;
    }

    static class DSU{
        int[] parent;
        int[] rank;

        DSU(int n){
            parent = new int[n+1];
            rank = new int[n+1];
            for(int i=1; i<=n; i++){
                parent[i] = i;
            }
        }

        int find(int x){
            if(parent[x] != x){
                parent[x] = find(parent[x]);
            }

            return parent[x];
        }

        boolean union(int a, int b){
            int pa = find(a);
            int pb = find(b);

            if(pa == pb){
                return false;
            }

            if(rank[pa] < rank[pb]){
                parent[pa] = pb;
            }else if(rank[pb] < rank[pa]){
                parent[pb] = pa;
            }else{
                parent[pb] = pa;
                rank[pa]++;
            }

            return true;
        }

    }
}



// Method 2: Using Recursive DFS
/*
## Idea (path-existence check)

Process edges one by one on an (initially empty) undirected graph:

* For each edge `(u, v)`:

  * **Before** adding it, run a DFS from `u` to see if `v` is already reachable in the current graph.

    * If reachable, then adding `(u, v)` would close a cycle → this edge is **redundant** → return it.
    * If not reachable, add `(u, v)` to the graph and continue.

Why this works: if there is **already a path** between `u` and `v`, adding `(u, v)` creates a cycle; the problem asks you to return the **last** such edge, and iterating in input order naturally gives that.

### Notes

* We **check reachability before adding** the edge. That’s the key detail.
* `visited` is **reinitialized for each edge** because we’re answering a new reachability question each time.
* We don’t strictly need a `parent` argument since we maintain `visited`, and the current graph (before adding the redundant edge) remains acyclic.

### Complexity

* Let `n = edges.length`. Each DFS is `O(n)` on a sparse graph; doing it for up to `n` edges yields **O(n²)** time.
* Space is **O(n)** for the adjacency list + visited array.

---

## Walkthrough 1 (simple)

**edges = [[1,2],[1,3],[2,3]]**

* Start with an empty graph.
* Edge **(1,2)**:

  * DFS(1 → 2)? No path (graph empty). Add (1,2).
* Edge **(1,3)**:

  * DFS(1 → 3)? No path yet. Add (1,3).
* Edge **(2,3)**:

  * DFS(2 → 3)? Path exists: 2 → 1 → 3 (using previously added edges).
  * Therefore, (2,3) is the redundant edge. **Return [2,3]**.

---

## Walkthrough 2 (classic)

**edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]**
Expected redundant edge: **[1,4]**

* Start empty.
* (1,2): no path → add.
* (2,3): DFS 2→3? no → add.
* (3,4): DFS 3→4? no → add.
* (1,4): DFS 1→4?

  * 1 → 2 → 3 → 4 is reachable → **redundant** → return **[1,4]**.
* (1,5) never gets processed (we already returned).

---

## Common pitfalls to avoid

* **Adding the edge before checking reachability.** That will always find a path (the edge you just added) and falsely mark the first edge as redundant.
* **Not resetting `visited` for each DFS**—you’ll incorrectly block paths across edges.
* **Off-by-one with 1-based labels.** We sized graph to `n + 1` and use indices directly.

This recursive DFS approach is interview-friendly, easy to reason about, and passes comfortably within constraints.
*/

// class Solution {
//     public int[] findRedundantConnection(int[][] edges) {
//         int n = edges.length;                 // nodes are labeled 1..n
//         List<List<Integer>> g = new ArrayList<>(n + 1);
//         for (int i = 0; i <= n; i++) g.add(new ArrayList<>());

//         // Try to add edges one-by-one
//         for (int[] e : edges) {
//             int u = e[0], v = e[1];

//             // visited resets for each reachability check
//             boolean[] visited = new boolean[n + 1];

//             // If v is already reachable from u using existing edges, (u,v) is redundant
//             if (dfsReachable(g, u, v, visited)) {
//                 return e;
//             }

//             // Otherwise, add the edge to the undirected graph
//             g.get(u).add(v);
//             g.get(v).add(u);
//         }

//         // Per problem guarantee, we will have returned before here.
//         return new int[0];
//     }

//     // Returns true if target is reachable from curr using current graph
//     private boolean dfsReachable(List<List<Integer>> g, int curr, int target, boolean[] visited) {
//         if (curr == target) return true;
//         visited[curr] = true;

//         for (int nei : g.get(curr)) {
//             if (!visited[nei]) {
//                 if (dfsReachable(g, nei, target, visited)) {
//                     return true;
//                 }
//             }
//         }
//         return false;
//     }
// }




// Method 3: Iterative DFS
/*
# Idea (path-existence check)

Process edges one by one on an (initially empty) **undirected** graph.

For each edge `(u, v)`:

1. **Before adding it**, check whether `v` is already **reachable** from `u` in the current graph via **iterative DFS** (stack).
2. If reachable, adding `(u, v)` would create a cycle ⇒ this is the **redundant** edge → return it.
3. Otherwise, add `(u, v)` to the graph and continue.

Because we scan edges in input order, the **last** edge that closes a cycle is exactly what we return.

### Notes

* **Do not** add the edge before testing connectivity; otherwise you trivially “find” a path using the edge itself.
* We re-create a fresh `visited` array for each reachability check (each edge).
* Marking `visited` **when pushing** avoids pushing the same node many times.

### Complexity

* Let `n = edges.length` (and nodes are labeled `1..n`).
* Each connectivity check is `O(n)` on a sparse graph; we may do up to `n` checks → **O(n²)** time worst-case.
* Space: `O(n)` for the adjacency list + `visited` + stack.

---

# Walkthrough 1 (simple triangle)

**edges = [[1,2],[1,3],[2,3]]**

Start with empty graph.

1. Edge (1,2):

   * DFS from 1 to 2?
     Stack: [1] → pop 1 → neighbors (none yet) → not found.
     Not connected → add (1,2).
   * Graph: 1—2

2. Edge (1,3):

   * DFS from 1 to 3?
     Stack: [1] → pop 1 → neighbor 2 → push 2 → pop 2 → neighbors [1] (visited) → not found.
     Not connected → add (1,3).
   * Graph: 1—2 and 1—3

3. Edge (2,3):

   * DFS from 2 to 3?
     Stack: [2] → pop 2 → neighbor 1 → push 1
     Pop 1 → neighbors [2,3] → 3 is unvisited → push 3
     Pop 3 → found target.
   * Already connected ⇒ (2,3) closes a cycle ⇒ **return [2,3]**.

---

# Walkthrough 2 (classic example)

**edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]**
Expected answer: **[1,4]**

Start empty.

1. (1,2):

   * DFS 1→2? (no edges yet) → not connected → add
     Graph: 1—2

2. (2,3):

   * DFS 2→3? stack [2] → pop 2 → neighbor 1 → push 1 → pop 1 → neighbors [2] (visited) → not found
     Not connected → add
     Graph: 1—2—3

3. (3,4):

   * DFS 3→4? walk 3→2→1 → still no 4 → not connected → add
     Graph: 1—2—3—4

4. (1,4):

   * DFS 1→4?
     Stack [1] → pop 1 → neighbor 2 → push 2
     Pop 2 → neighbor 3 → push 3
     Pop 3 → neighbor 4 → push 4
     Pop 4 → found target
   * Already connected → adding (1,4) closes a cycle ⇒ **return [1,4]**.

(We never process (1,5), because we’ve already found the redundant edge.)

---

## Common pitfalls (and how this code avoids them)

* **Adding edge before checking:** would always find a trivial path using itself → we check *first*, then add.
* **1-based indexing:** adjacency list sized to `n+1`.
* **Reusing `visited` across edges:** can leak state → we reinitialize `visited` on each check.
* **Using BFS by accident:** BFS works too, but here we explicitly use a **stack** for iterative DFS. (Either is acceptable for reachability.)

This iterative DFS version is interview-friendly, avoids recursion depth issues, and cleanly returns the last edge that introduces a cycle.
*/

// class Solution {
//     public int[] findRedundantConnection(int[][] edges) {
//         int n = edges.length;                     // nodes are 1..n
//         List<List<Integer>> g = new ArrayList<>(n + 1);
//         for (int i = 0; i <= n; i++) g.add(new ArrayList<>());

//         for (int[] e : edges) {
//             int u = e[0], v = e[1];

//             // If u and v are already connected in current graph, e closes a cycle
//             if (connectedIterativeDFS(g, u, v)) {
//                 return e;
//             }

//             // Otherwise add the edge and move on
//             g.get(u).add(v);
//             g.get(v).add(u);
//         }

//         // Per problem guarantee there is always one answer; this is just fallback
//         return new int[0];
//     }

//     private boolean connectedIterativeDFS(List<List<Integer>> g, int start, int target) {
//         if (start == target) return true;

//         boolean[] visited = new boolean[g.size()];
//         Deque<Integer> stack = new ArrayDeque<>();
//         stack.push(start);
//         visited[start] = true; // mark on push to avoid duplicate pushes

//         while (!stack.isEmpty()) {
//             int u = stack.pop();
//             if (u == target) return true;

//             for (int nei : g.get(u)) {
//                 if (!visited[nei]) {
//                     visited[nei] = true;
//                     stack.push(nei);
//                 }
//             }
//         }
//         return false;
//     }
// }




// Method 4: BFS version
/*
# Core idea (reachability before insertion)

Process edges one by one on an initially empty **undirected** graph:

* For each edge `(u, v)`:

  1. **Before adding it**, run a **BFS** from `u` to check if `v` is already reachable in the current graph.
  2. If reachable, adding `(u, v)` would create a cycle ⇒ this edge is **redundant** ⇒ return it.
  3. Otherwise, add `(u, v)` and continue.

Because we scan edges in input order, the one we return is automatically the **last** edge that closes a cycle (as required).

### Why this works

* If there’s already a path between `u` and `v`, adding `(u, v)` closes a cycle—exactly what “redundant connection” means.
* We **must** check reachability **before** inserting the edge; otherwise the edge itself would create a trivial path and falsely mark the first edge as redundant.
* Using BFS guarantees we examine nodes level by level; for mere reachability, BFS and DFS are equivalent.

### Complexity

* Let `n = edges.length`. Each BFS is `O(n)` on a sparse graph; we do it up to `n` times → **O(n²)** worst case.
* Space is **O(n)** for adjacency + visited + queue.

---

## Walkthrough 1 — simple triangle

**edges = [[1,2],[1,3],[2,3]]**

Graph starts empty.

1. (1,2)

   * BFS 1→2 on empty graph: not reachable → add (1,2).
   * Graph: 1—2

2. (1,3)

   * BFS 1→3: queue=[1] → pop 1 → neighbor 2 → enqueue 2 → pop 2 → neighbor 1 (visited) → not found.
   * Not reachable → add (1,3).
   * Graph: 1—2 and 1—3

3. (2,3)

   * BFS 2→3: queue=[2] → pop 2 → neighbor 1 → enqueue 1 → pop 1 → neighbors [2,3].
     3 is unvisited → enqueue 3 → pop 3 → found target.
   * Already connected → (2,3) creates a cycle ⇒ **return [2,3]**.

---

## Walkthrough 2 — classic example

**edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]**
Expected redundant edge: **[1,4]**

Start empty.

1. (1,2)

   * BFS 1→2: not reachable → add (1,2).
     Graph: 1—2

2. (2,3)

   * BFS 2→3: queue=[2] → pop 2 → neighbor 1 → enqueue 1 → pop 1 → neighbor 2 (visited) → not found.
     Not reachable → add (2,3).
     Graph: 1—2—3

3. (3,4)

   * BFS 3→4: walk 3→2→1, still no 4 → not reachable → add (3,4).
     Graph: 1—2—3—4

4. (1,4)

   * BFS 1→4: queue=[1] → pop 1 → neighbor 2 → enqueue 2 → pop 2 → neighbor 3 → enqueue 3 → pop 3 → neighbor 4 → enqueue 4 → pop 4 → found.
     Already connected → adding (1,4) closes the cycle ⇒ **return [1,4]**.

(Edge (1,5) is never processed; we’ve already returned.)

---

## Common pitfalls (and how this code avoids them)

* **Adding the edge before checking** → always finds a trivial path through itself.
  *We check first, then add.*
* **Forgetting 1-based labels** → off-by-one errors.
  *We size the adjacency list to `n + 1` and index directly by labels.*
* **Reusing `visited` across edges** → stale state contaminates searches.
  *We create a fresh `visited` per BFS call.*
* **Marking visited on dequeue** → can enqueue duplicates.
  *We mark visited on enqueue to keep the queue small.*

This BFS approach is clean, interview-friendly, and avoids recursion limits while matching the intended logic for detecting the last edge that forms a cycle.
*/

// class Solution {
//     public int[] findRedundantConnection(int[][] edges) {
//         int n = edges.length; // nodes are labeled 1..n
//         List<List<Integer>> g = new ArrayList<>(n + 1);
//         for (int i = 0; i <= n; i++) g.add(new ArrayList<>());

//         for (int[] e : edges) {
//             int u = e[0], v = e[1];

//             // Check if u and v are already connected via BFS
//             if (connectedBFS(g, u, v)) {
//                 return e; // adding this would form a cycle
//             }

//             // Otherwise, add the edge to the graph
//             g.get(u).add(v);
//             g.get(v).add(u);
//         }

//         // Per problem guarantee, an answer exists before this.
//         return new int[0];
//     }

//     private boolean connectedBFS(List<List<Integer>> g, int start, int target) {
//         if (start == target) return true; // trivial

//         boolean[] visited = new boolean[g.size()];
//         Queue<Integer> q = new ArrayDeque<>();
//         q.offer(start);
//         visited[start] = true; // mark on enqueue to avoid duplicates

//         while (!q.isEmpty()) {
//             int u = q.poll();
//             if (u == target) return true;

//             for (int nei : g.get(u)) {
//                 if (!visited[nei]) {
//                     visited[nei] = true;
//                     q.offer(nei);
//                 }
//             }
//         }
//         return false;
//     }
// }
