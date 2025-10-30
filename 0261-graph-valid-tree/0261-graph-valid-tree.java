// Method 1: Using BFS
/*
# Where was I going wrong:

For an undirected graph to be a tree, **both** of these must be true:

1. The graph has exactly `n - 1` edges

   * Trees on `n` nodes always have `n-1` edges.
   * If you have fewer, it's disconnected. If you have more, there’s at least one cycle.

2. The graph is fully connected

   * Every node must be reachable from any other node (i.e. 1 connected component).

Those two conditions together are equivalent to "acyclic + connected" in an undirected graph.

---

## Let's look at your code

```java
class Solution {
    public boolean validTree(int n, int[][] edges) {
        int[] indegree = new int[n];

        for(int[] edge: edges){
            int toNode = edge[1];
            indegree[toNode] += 1;

            if(indegree[toNode] > 1){
                return false;
            }
        }

        return true;
    }
}
```

### Problem 1. You're treating this like a directed graph with “indegree”

You do:

```java
int toNode = edge[1];
indegree[toNode] += 1;
if (indegree[toNode] > 1) return false;
```

That logic says:

* “If any node has more than one incoming edge, it's not a tree.”

That is the *directed* tree / arborescence idea: a rooted directed tree should have exactly one parent (indegree ≤ 1 except root).
But this problem is about an **undirected** graph. In an undirected tree, a node (that's not the root of any orientation at all) can have multiple neighbors. So a node can easily have “degree” 3, 4, etc., and that's totally valid. For example:

```
   0
  /|\
 1 2 3
```

Node 0 connects to 1, 2, 3. In your code, node 1 gets indegree 1, node 2 gets indegree 1, node 3 gets indegree 1. Fine. But if we had chain 0-1-2, node 1 ends up with indegree 1 (from edge [0,1]) and then indegree 2 (from edge [1,2]), and you'd incorrectly reject it — even though 0-1-2 is a valid tree.

So: checking “indegree > 1” is not the right way to detect cycles in an undirected graph.

---

### Problem 2. You never check connectivity

Your code returns `true` as long as no node's indegree exceeds 1.
So consider this input:

```text
n = 4
edges = [[0,1],[2,3]]
```

This is two separate components:

* Component A: 0--1
* Component B: 2--3

This is **not** one tree. It's disconnected.

Your code:

* indegree[1] = 1
* indegree[3] = 1
* nobody > 1
  → returns true ❌

But a valid tree must be exactly one connected component.

---

### Problem 3. You don't check number of edges

Suppose:

```text
n = 4
edges = [[0,1],[1,2],[2,0],[2,3]]
```

That has 4 edges for 4 nodes. A tree on 4 nodes must have exactly 3 edges. So this graph has a cycle (0-1-2-0). It's not a tree.

Your code:

* indegree[1] = 1
* indegree[2] = 2 → you'd reject this one, *but for the wrong reason*.
  Now try a different cyclic graph:

```text
n = 4
edges = [[0,1],[1,2],[2,0]]
```

This one is a simple 3-cycle on nodes 0-1-2, node 3 isolated.

* indegree[1] = 1
* indegree[2] = 1
* indegree[0] = 1
  Everybody has indegree 1, nobody > 1 → you'd return true ❌

But that's absolutely not a tree:

* It has a cycle.
* It's disconnected (node 3 is floating).

So just looking at indegree doesn't catch cycles in undirected graphs.

---

## So what SHOULD we do?

For an undirected graph to be a tree:

1. **Check edge count:** if `edges.length != n - 1`, return false immediately.

   * Too many edges = must have a cycle.
   * Too few edges = must be disconnected.

2. **Check connectivity:** do a BFS/DFS/Union-Find from one node and make sure you can visit all `n` nodes.

If both pass, it's a valid tree.

---

### Option A: BFS/DFS approach (clear in interviews)

Why this works:

* If there are exactly `n-1` edges and the graph is connected (we saw all nodes), then it cannot have a cycle. (In an undirected graph, any connected component with `k` nodes and `k-1` edges is a tree. If it had a cycle, it would need ≥ k edges.)
* If it's disconnected, `seen < n`, and we return false.

---

### Option B: Union-Find (Disjoint Set Union)

Also standard in interviews:

* Initially each node is its own parent.
* For each edge (u, v):

  * If `find(u) == find(v)`, there's a cycle → not a tree.
  * Otherwise union(u, v).
* At the end, check that you end up with exactly 1 connected component.
* Also check `edges.length == n - 1` first.

Union-Find is great for “is this undirected graph acyclic and connected?” but BFS is usually easier to explain live.

---

## TL;DR on what's wrong in your submission

1. You're using `indegree` like it's a directed tree.
   This is an undirected graph. A node can have multiple neighbors and that's totally fine.

2. You never verify connectivity.
   Disconnected graphs should be false but you'd return true.

3. You don't check `edges.length == n - 1`, which is a must-have property.

4. You don't actually detect undirected cycles; indegree doesn't do that.

If you add:

* edge-count check,
* connectedness check (via BFS/DFS),
  you'll have the correct solution.
*/
class Solution {
    public boolean validTree(int n, int[][] edges) {
        // Rule 1: must have exactly n-1 edges
        if (edges.length != n - 1) {
            return false;
        }

        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            int u = e[0];
            int v = e[1];
            graph.get(u).add(v);
            graph.get(v).add(u);
        }

        // BFS (or DFS) from node 0 to see if it's fully connected
        boolean[] visited = new boolean[n];
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(0);
        visited[0] = true;
        int seen = 1;

        while (!q.isEmpty()) {
            int node = q.poll();
            for (int nei : graph.get(node)) {
                if (!visited[nei]) {
                    visited[nei] = true;
                    seen++;
                    q.offer(nei);
                }
            }
        }

        // Graph is a tree iff we saw all nodes
        return seen == n;
    }
}




// Method 2: Using Recursive DFS
/*
You only need to check two facts for an undirected graph to be a valid tree:
1) It has exactly n − 1 edges, and
2) It is connected (all nodes reachable from any one node).

With (1) true, “connected” ⇔ “acyclic” for undirected graphs, so a single reachability DFS is enough.
*/

// class Solution {
//     public boolean validTree(int n, int[][] edges) {
//         // 1) A tree on n nodes must have n-1 edges
//         if (edges.length != n - 1) return false;

//         // Build undirected adjacency list
//         List<List<Integer>> g = new ArrayList<>();
//         for (int i = 0; i < n; i++) g.add(new ArrayList<>());
//         for (int[] e : edges) {
//             g.get(e[0]).add(e[1]);
//             g.get(e[1]).add(e[0]);
//         }

//         // 2) Check connectivity with a single DFS from node 0
//         boolean[] vis = new boolean[n];
//         int seen = dfs(0, -1, g, vis);  // parent is not needed for correctness here; kept for clarity

//         return seen == n;
//     }

//     private int dfs(int u, int parent, List<List<Integer>> g, boolean[] vis) {
//         vis[u] = true;
//         int count = 1; // count this node
//         for (int v : g.get(u)) {
//             if (!vis[v]) {
//                 count += dfs(v, u, g, vis);
//             }
//         }
//         return count;
//     }
// }





// Method 3: Iterative DFS
/*
Optional: explicit cycle check (not required if you keep the n−1 check)

If you omit the edges.length == n - 1 early-check, you must 
detect cycles. For DFS that means tracking a parent and flagging
when you revisit a node that is not the parent. With the n−1 check, you 
don’t need this; connectivity implies acyclicity.
*/
// class SolutionIterative {
//     public boolean validTree(int n, int[][] edges) {
//         // 1) Edge count
//         if (edges.length != n - 1) return false;

//         // Build graph
//         List<List<Integer>> g = new ArrayList<>();
//         for (int i = 0; i < n; i++) g.add(new ArrayList<>());
//         for (int[] e : edges) {
//             g.get(e[0]).add(e[1]);
//             g.get(e[1]).add(e[0]);
//         }

//         // 2) Connectivity via iterative DFS
//         boolean[] vis = new boolean[n];
//         Deque<Integer> st = new ArrayDeque<>();
//         st.push(0);
//         vis[0] = true;
//         int seen = 1;

//         while (!st.isEmpty()) {
//             int u = st.pop();
//             for (int v : g.get(u)) {
//                 if (!vis[v]) {
//                     vis[v] = true;
//                     seen++;
//                     st.push(v);
//                 }
//             }
//         }

//         return seen == n;
//     }
// }





// Method 4: Disjoint Set Union (DSU) / Union Find with edge count check + cycle detection
/*
Why this works:

If edges.length != n - 1, you can stop:
fewer ⇒ disconnected; more ⇒ at least one cycle.

With exactly n−1 edges, “no cycles” ⇔ connected for an undirected graph.
DSU detects a cycle when union fails (both endpoints already in the same set).

Therefore: edge-count OK + no cycle ⇒ it’s a tree.

Time: O(n + m α(n)) (α is inverse Ackermann, tiny)
Space: O(n)


## Walkthroughs

### Example 1 — Valid tree

```
n = 5
edges = [[0,1],[0,2],[0,3],[1,4]]
```

**Version A:**

* Check edges: `4 == n−1 (5−1)` ✅
* DSU unions:

  * union(0,1): merge {0},{1} → {0,1}
  * union(0,2): merge {0,1},{2} → {0,1,2}
  * union(0,3): → {0,1,2,3}
  * union(1,4): merge {0,1,2,3},{4} → {0,1,2,3,4}
* No union failed ⇒ no cycle. With `n−1` edges, connected ⇒ **true**.

### Example 2 — Cycle present

```
n = 4
edges = [[0,1],[1,2],[2,0],[1,3]]
```

**Version A:**

* `edges.length = 4 != 3` ⇒ **false** immediately.

**Version B (for intuition):**

* union(0,1) OK
* union(1,2) OK
* union(2,0) fails (same root) ⇒ cycle ⇒ **false**

### Example 3 — Disconnected

```
n = 4
edges = [[0,1],[2,3]]
```

**Version A:**

* `edges.length = 2 != 3` ⇒ **false**

**Version B:**

* union(0,1) OK → components from 4 to 3
* union(2,3) OK → components from 3 to 2
* No cycle, but `components != 1` ⇒ **false**

---

## Common pitfalls to avoid

* **Indegree/degree checks (directed logic) on an undirected graph.**
  A node in an undirected tree can have degree > 1; that’s fine.

* **Skipping connectivity.**
  Acyclic alone isn’t enough; must be a single component.

* **Not short-circuiting on edge count.**
  The `n−1` check is a fast and elegant filter (Version A).

---

## Cheat-sheet takeaways

* **Fastest interview route:**
  Check `edges.length == n−1`, then DSU to ensure no cycle.

* **DSU essentials:** path compression in `find`, union by rank/size, `union` returns false if cycle.

* **Proof sketch:** Undirected, `n−1` edges and no cycles ⇒ connected ⇒ tree.

*/

// class Solution {
//     public boolean validTree(int n, int[][] edges) {
//         // A tree on n nodes must have exactly n-1 edges
//         if (edges.length != n - 1) return false;

//         DSU dsu = new DSU(n);
//         // If any edge connects two nodes already in the same set -> cycle
//         for (int[] e : edges) {
//             if (!dsu.union(e[0], e[1])) {
//                 return false; // cycle found
//             }
//         }
//         // With n-1 edges and no cycles, graph is necessarily connected -> a valid tree
//         return true;
//     }

//     static class DSU {
//         int[] parent;
//         int[] rank; // or size; rank is fine

//         DSU(int n) {
//             parent = new int[n];
//             rank = new int[n];
//             for (int i = 0; i < n; i++) parent[i] = i;
//         }

//         int find(int x) {
//             if (parent[x] != x) parent[x] = find(parent[x]); // path compression
//             return parent[x];
//         }

//         // returns true if union merged two different sets; false if already same set (cycle)
//         boolean union(int a, int b) {
//             int ra = find(a), rb = find(b);
//             if (ra == rb) return false; // would form a cycle

//             // union by rank
//             if (rank[ra] < rank[rb]) {
//                 parent[ra] = rb;
//             } else if (rank[ra] > rank[rb]) {
//                 parent[rb] = ra;
//             } else {
//                 parent[rb] = ra;
//                 rank[ra]++;
//             }
//             return true;
//         }
//     }
// }





// Method 5: DSU by tracking components + cycle detection
/*
If you prefer not to rely on the n−1 check, you can track the number of connected components and ensure:

no union fails (no cycles), and

components == 1 at the end.


## Walkthroughs

### Example 1 — Valid tree

```
n = 5
edges = [[0,1],[0,2],[0,3],[1,4]]
```

**Version A:**

* Check edges: `4 == n−1 (5−1)` ✅
* DSU unions:

  * union(0,1): merge {0},{1} → {0,1}
  * union(0,2): merge {0,1},{2} → {0,1,2}
  * union(0,3): → {0,1,2,3}
  * union(1,4): merge {0,1,2,3},{4} → {0,1,2,3,4}
* No union failed ⇒ no cycle. With `n−1` edges, connected ⇒ **true**.

### Example 2 — Cycle present

```
n = 4
edges = [[0,1],[1,2],[2,0],[1,3]]
```

**Version A:**

* `edges.length = 4 != 3` ⇒ **false** immediately.

**Version B (for intuition):**

* union(0,1) OK
* union(1,2) OK
* union(2,0) fails (same root) ⇒ cycle ⇒ **false**

### Example 3 — Disconnected

```
n = 4
edges = [[0,1],[2,3]]
```

**Version A:**

* `edges.length = 2 != 3` ⇒ **false**

**Version B:**

* union(0,1) OK → components from 4 to 3
* union(2,3) OK → components from 3 to 2
* No cycle, but `components != 1` ⇒ **false**

---

## Common pitfalls to avoid

* **Indegree/degree checks (directed logic) on an undirected graph.**
  A node in an undirected tree can have degree > 1; that’s fine.

* **Skipping connectivity.**
  Acyclic alone isn’t enough; must be a single component.

* **Not short-circuiting on edge count.**
  The `n−1` check is a fast and elegant filter (Version A).

---

## Cheat-sheet takeaways

* **Fastest interview route:**
  Check `edges.length == n−1`, then DSU to ensure no cycle.

* **DSU essentials:** path compression in `find`, union by rank/size, `union` returns false if cycle.

* **Proof sketch:** Undirected, `n−1` edges and no cycles ⇒ connected ⇒ tree.
*/

// class SolutionNoEdgeCheck {
//     public boolean validTree(int n, int[][] edges) {
//         DSU dsu = new DSU(n);
//         boolean hasCycle = false;

//         for (int[] e : edges) {
//             if (!dsu.union(e[0], e[1])) {
//                 hasCycle = true; // found a cycle
//                 break;
//             }
//         }
//         // valid tree iff one component and no cycles
//         return !hasCycle && dsu.components == 1;
//     }

//     static class DSU {
//         int[] parent, rank;
//         int components;

//         DSU(int n) {
//             parent = new int[n];
//             rank = new int[n];
//             components = n;
//             for (int i = 0; i < n; i++) parent[i] = i;
//         }

//         int find(int x) {
//             if (parent[x] != x) parent[x] = find(parent[x]);
//             return parent[x];
//         }

//         boolean union(int a, int b) {
//             int ra = find(a), rb = find(b);
//             if (ra == rb) return false; // cycle
//             if (rank[ra] < rank[rb]) parent[ra] = rb;
//             else if (rank[ra] > rank[rb]) parent[rb] = ra;
//             else { parent[rb] = ra; rank[ra]++; }
//             components--;
//             return true;
//         }
//     }
// }



