// Answer to the Neetcode question: Number of Connected Components in an Undirected Graph / LeetCode question: 323. Number of Connected Components in an Undirected Graph

// Method 1: DSU / Union Find by rank
/*
### What I was doing wrong:

* You name the array `rank` (which should represent tree height) and compare it to decide which root becomes parent—**that’s union-by-rank**.
* But in the `<` / `>` branches you then do `rank[pb] += rank[pa]` (and vice versa) — **that’s union-by-size**.
* In union-by-rank you **never sum** the ranks; you **only** increment the rank by **1** when two equal-rank trees are merged.
* In union-by-size you **do** sum sizes, but then you must **initialize sizes to 1** (not 0), and you **never** do the `++` on ties—just attach smaller to larger and add sizes.

This mixup doesn’t usually break correctness (you still join sets), but it defeats the heuristic and can degrade performance; it’s also easy to get wrong comparisons later because your “rank” numbers no longer mean height **or** size consistently.

Also, this line at the top is redundant (not wrong):

```java
if (edges.length == 0) return n;
```

Your DSU already returns `n` in that case.


## Quick definitions (what `rank` means)

* We keep two arrays:

  * `parent[x]` — representative parent of the set containing `x`.
  * `rank[x]` — an **upper bound on the height** of the tree whose root is `x`.

    * Single-node set has height 0 ⇒ `rank = 0`.
    * **Only** when we merge two roots **of equal rank** do we increment the new root’s rank by 1.
    * If ranks are unequal, we attach the shorter tree under the taller tree and **do not** change rank (height doesn’t increase).

* **Path compression** flattens trees during `find`. Ranks don’t decrease afterward; they remain an **upper bound**.

---

## Example: step-by-step unions on 0..6

Start with `n = 7` nodes: `{0},{1},{2},{3},{4},{5},{6}`

```
parent = [0,1,2,3,4,5,6]
rank   = [0,0,0,0,0,0,0]
```

We'll perform these operations:

1. union(0,1)
2. union(2,3)
3. union(0,2)
4. union(4,5)
5. union(5,6)
6. union(3,6)   (this will showcase path compression + rank logic)

---

### 1) union(0,1)

* Roots: `find(0)=0` (rank 0), `find(1)=1` (rank 0) → **equal ranks**
* Make, say, `0` the parent and **increment** its rank.

```
parent = [0,0,2,3,4,5,6]
rank   = [1,0,0,0,0,0,0]
```

Tree:

```
   0 (rank 1)
  /
 1
```

Height increased from 0 to 1 because we merged two height-0 trees.

---

### 2) union(2,3)

* Roots: `2` (rank 0), `3` (rank 0) → **equal**
* Attach `3` under `2`, increment rank of `2`.

```
parent = [0,0,2,2,4,5,6]
rank   = [1,0,1,0,0,0,0]
```

Tree:

```
   2 (rank 1)
  /
 3
```

---

### 3) union(0,2)

* Roots: `0` (rank 1), `2` (rank 1) → **equal**
* Attach, say, `2` under `0`, and **increment** `rank[0]` to 2.

```
parent = [0,0,0,2,4,5,6]   // 2's parent becomes 0
rank   = [2,0,1,0,0,0,0]
```

Structure (conceptually):

```
      0 (rank 2)
     / \
    1   2
        /
       3
```

Why rank increased: we just combined **two height-1 trees**, so the new height becomes 2.

---

### 4) union(4,5)

* Roots: `4` (rank 0), `5` (rank 0) → **equal**
* Attach `5` under `4`, increment `rank[4]` to 1.

```
parent = [0,0,0,2,4,4,6]
rank   = [2,0,1,0,1,0,0]
```

Tree:

```
   4 (rank 1)
  /
 5
```

---

### 5) union(5,6)

* `find(5)` → root is `4` (since parent[5]=4).
* Roots: `4` (rank 1), `6` (rank 0) → **unequal ranks**
* Attach **shorter under taller**: `parent[6] = 4`, **no rank change**.

```
parent = [0,0,0,2,4,4,4]
rank   = [2,0,1,0,1,0,0]
```

Tree:

```
     4 (rank 1)
    / \
   5   6
```

Why no rank change: attaching a shorter tree (height 0) under a taller tree (height 1) doesn’t increase overall height; max height stays 1.

---

### 6) union(3,6)  ← shows path compression + rank choice

First get roots:

* `find(3)`:

  * parent[3] = 2, parent[2] = 0 → root is `0`
  * **Path compression** sets `parent[3] = 0` (and often `parent[2]=0` already)
* `find(6)`:

  * parent[6] = 4 → root is `4`

Now compare roots:

* RootA = `0` (rank **2**)
* RootB = `4` (rank **1**)
* **Unequal ranks** → attach **smaller under larger**: `parent[4] = 0` (no rank change)

Final arrays:

```
parent = [0,0,0,0,0,4,4]   // after path compression, future finds flatten more
rank   = [2,0,1,0,1,0,0]
```

Conceptual final structure:

```
        0 (rank 2)
      /  |   \
     1   2    4
         |   / \
         3  5   6
```

* Notice `rank[0]` stayed 2 because the other root (`4`) had smaller rank (1). Height doesn’t increase.
* Path compression will later make many nodes point directly (or almost directly) to `0`, flattening the tree for near-O(1) finds.

---

## The core rank rule (why we increment only sometimes)

Let `h(A)` be height of root A’s tree; `rank[A]` tracks an **upper bound** of `h(A)`.

* **Equal ranks:** `rank[A] == rank[B] == h`
  Merging two height-`h` trees can create a tree of height `h+1`.
  We attach one under the other and set the new root’s rank to `h+1` (i.e., `rank++`).

* **Unequal ranks:** suppose `rank[A] = h` and `rank[B] = k` with `h > k`.
  The new height is still `h` (the taller tree dominates).
  Attach `B` under `A`, and **do not** change `rank[A]`.

This keeps trees shallow and gives the near-constant amortized time.

---

## Path compression + rank = subtle point

* During `find`, we often do `parent[x] = root` for all nodes on the path (compression).
* That **reduces actual height** dramatically, but we **do not** reduce `rank`.
  This is fine because rank is only an *upper bound*, and correctness/performance don’t rely on it being tight—only monotone non-decreasing at roots.

---

## What goes wrong if you mix rank and size

* If you sometimes **sum** ranks (`rank[root] += rank[child]`) but also use rank comparisons, you’re no longer comparing heights. The heuristic becomes inconsistent.
* It still finds correct components, but can be slower because you may attach in suboptimal directions.

Stick to one:

* **Union-by-rank**: rank starts at 0, compare ranks, `rank++` **only** on equal ranks.
* **Union-by-size**: size starts at 1, compare sizes, attach smaller to larger, **add sizes**, never `++` on ties.

---

## Why all this matters (complexity)

* With **union-by-rank** + **path compression**, the amortized time per operation is `α(n)` (inverse Ackermann), which is < 5 for any realistic `n`.
* Practically: almost O(1) per `find`/`union`.

---

## Tiny sanity check

If you accidentally did:

```java
if (rank[ra] < rank[rb]) parent[ra] = rb;
else if (rank[ra] > rank[rb]) parent[rb] = ra;
else { parent[rb] = ra; /* rank[ra] stays same }  // (WRONG: forgot rank++)


Then when equal ranks merge, you **didn’t** bump the rank. Later you might attach another equal-rank tree under it, still thinking it’s not tall, which can grow the height more than necessary. That’s the key bug union-by-rank avoids.

---

### TL;DR

* **Rank = upper bound on height (at roots)**.
* **Increment rank only when merging equal ranks**; otherwise keep rank unchanged and attach the shorter under the taller.
* **Path compression** flattens trees during `find`, ranks don’t decrease.
* This combo gives you near-O(1) performance and keeps trees shallow.
*/
class Solution {
    public int countComponents(int n, int[][] edges) {
        DSU dsu = new DSU(n);
        for (int[] e : edges) dsu.union(e[0], e[1]);
        return dsu.components;
    }

    static class DSU {
        int[] parent, rank;  // rank = tree height upper bound
        int components;

        DSU(int n) {
            parent = new int[n];
            rank = new int[n];         // all zeros
            components = n;
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return;

            if (rank[ra] < rank[rb]) {
                parent[ra] = rb;
            } else if (rank[ra] > rank[rb]) {
                parent[rb] = ra;
            } else {
                parent[rb] = ra;
                rank[ra]++;            // only increment on equal ranks
            }
            components--;
        }
    }
}





// Method 2: DSU / Union Find by size
/*
In union-by-size you do sum sizes, but then you must 
initialize sizes to 1 (not 0), and you never do the ++ on 
ties—just attach smaller to larger and add sizes.
*/
// class Solution {
//     public int countComponents(int n, int[][] edges) {
//         DSU dsu = new DSU(n);
//         for (int[] e : edges) dsu.union(e[0], e[1]);
//         return dsu.components;
//     }

//     static class DSU {
//         int[] parent, size;  // size = subtree size
//         int components;

//         DSU(int n) {
//             parent = new int[n];
//             size = new int[n];
//             components = n;
//             for (int i = 0; i < n; i++) {
//                 parent[i] = i;
//                 size[i] = 1;          // IMPORTANT: start at 1
//             }
//         }

//         int find(int x) {
//             if (parent[x] != x) parent[x] = find(parent[x]);
//             return parent[x];
//         }

//         void union(int a, int b) {
//             int ra = find(a), rb = find(b);
//             if (ra == rb) return;

//             if (size[ra] < size[rb]) {
//                 parent[ra] = rb;
//                 size[rb] += size[ra];
//             } else {
//                 parent[rb] = ra;
//                 size[ra] += size[rb];
//             }
//             components--;
//         }
//     }
// }






// Method 3: BFS Approach
/*
## Core idea

In an undirected graph, a **connected component** is a group of nodes where every node can reach every other node in the group.

Algorithm:

1. Build an adjacency list from edges.
2. Keep a `visited[]` array.
3. Loop through all nodes `0..n-1`:

   * If a node is not visited, it starts a **new component**:

     * increment `components`
     * run BFS from that node to mark its entire component as visited
4. Return `components`.



# Why this works (detailed explanation)

### Why increment `components` when we see an unvisited node?

If `i` is unvisited, that means:

* No previous BFS/DFS reached it
* So it must belong to a **new connected component**

When we run BFS from `i`, we visit **every node reachable from i**, i.e., the entire component.
So we won’t count that component again.

---

## What BFS is doing here

BFS is simply a way to “flood fill” the component:

* Start from a node
* Visit its neighbors
* Then neighbors of neighbors
* Continue until no more new nodes can be reached

All visited nodes in that BFS run belong to the same component.

---

# Thorough example walkthrough

## Example

```text
n = 7
edges = [[0,1],[1,2],[3,4],[5,6]]
```

Let’s see the graph structure:

* Component 1: `0 - 1 - 2`
* Component 2: `3 - 4`
* Component 3: `5 - 6`

So answer should be **3 components**.

---

## Step 1: Build adjacency list

Adjacency list looks like:

* `0: [1]`
* `1: [0, 2]`
* `2: [1]`
* `3: [4]`
* `4: [3]`
* `5: [6]`
* `6: [5]`

---

## Step 2: visited array

Initially:

```text
visited = [F, F, F, F, F, F, F]
components = 0
```

---

## Step 3: Loop i = 0..6

### i = 0

`visited[0] == false` → new component found

* `components = 1`
* BFS from 0

#### BFS starting at 0

Queue: `[0]`
Mark visited[0] = true

* pop 0 → neighbors: 1

  * 1 not visited → mark visited[1]=true, push 1
    Queue: `[1]`

* pop 1 → neighbors: 0, 2

  * 0 already visited
  * 2 not visited → mark visited[2]=true, push 2
    Queue: `[2]`

* pop 2 → neighbors: 1 (already visited)

Queue empty → BFS ends.

Now:

```text
visited = [T, T, T, F, F, F, F]
components = 1
```

---

### i = 1, i = 2

Both visited already → skip

---

### i = 3

`visited[3] == false` → new component

* `components = 2`
* BFS from 3

#### BFS starting at 3

Queue: `[3]`, visited[3]=true

* pop 3 → neighbor: 4

  * 4 not visited → visited[4]=true, push 4
    Queue: `[4]`

* pop 4 → neighbor: 3 already visited

Done.

Now:

```text
visited = [T, T, T, T, T, F, F]
components = 2
```

---

### i = 4

visited → skip

---

### i = 5

`visited[5] == false` → new component

* `components = 3`
* BFS from 5

#### BFS starting at 5

Queue: `[5]`, visited[5]=true

* pop 5 → neighbor: 6

  * 6 not visited → visited[6]=true, push 6
* pop 6 → neighbor: 5 already visited

Done.

Now:

```text
visited = [T, T, T, T, T, T, T]
components = 3
```

---

### i = 6

visited → skip

---

## Final answer

`components = 3` ✅

---

# Complexity

Let `V = n`, `E = edges.length`

* Building adjacency: `O(V + E)`
* BFS across all components visits each node once and each edge twice:

  * `O(V + E)`
* Space: `O(V + E)` for adjacency + `O(V)` visited + BFS queue
*/

// class Solution {
//     public int countComponents(int n, int[][] edges) {
//         List<Integer>[] adjList = new ArrayList[n];
//         for (int i = 0; i < n; i++) {
//             adjList[i] = new ArrayList<>();
//         }

//         for (int[] e : edges) {
//             int u = e[0];
//             int v = e[1];
//             adjList[u].add(v);
//             adjList[v].add(u);
//         }

//         boolean[] visited = new boolean[n];
//         int components = 0;

//         for (int i = 0; i < n; i++) {
//             if (!visited[i]) {
//                 components++;
//                 bfs(i, adjList, visited);
//             }
//         }

//         return components;
//     }

//     private void bfs(int start, List<Integer>[] adjList, boolean[] visited) {
//         Deque<Integer> queue = new ArrayDeque<>();
//         queue.offerLast(start);
//         visited[start] = true;

//         while (!queue.isEmpty()) {
//             int node = queue.pollFirst();

//             for (int neighbor : adjList[node]) {
//                 if (!visited[neighbor]) {
//                     visited[neighbor] = true;
//                     queue.offerLast(neighbor);
//                 }
//             }
//         }
//     }
// }








// Method 4: DFS Approach
/*
## Core idea

Same concept as BFS:

* A **connected component** is a set of nodes reachable from one another.
* If you pick any unvisited node and run DFS, DFS will visit **every node in that component**.
* So:

  * each time you start DFS from an unvisited node, you found a **new component**.


# Why this works (detailed explanation)

### Step 1: Build adjacency list

Because the graph is undirected, every edge `[u, v]` means:

* `u` is connected to `v`
* `v` is connected to `u`

So we add both directions.

---

### Step 2: Maintain `visited[]`

`visited[x]` means:

* have we already visited node `x` as part of some DFS exploration?

This prevents:

* infinite loops in cycles
* repeated work

---

### Step 3: Count components by starting DFS

Loop from `0` to `n-1`:

* If node `i` is already visited → it belongs to a previously discovered component.
* If node `i` is not visited → this is the first node we’ve seen in a **new component**:

  * increment `components`
  * run DFS from `i` to mark the entire component visited

Each component increases the answer exactly once.

---

# Thorough example walkthrough

## Example

```text
n = 7
edges = [[0,1],[1,2],[3,4],[5,6]]
```

Graph structure:

* Component 1: `0 - 1 - 2`
* Component 2: `3 - 4`
* Component 3: `5 - 6`

Expected answer: **3**

---

## Step A: Build adjacency list

Adjacency list becomes:

* `0: [1]`
* `1: [0, 2]`
* `2: [1]`
* `3: [4]`
* `4: [3]`
* `5: [6]`
* `6: [5]`

---

## Step B: Initialize tracking

```text
visited = [F, F, F, F, F, F, F]
components = 0
```

---

## Step C: Loop over nodes

### i = 0

`visited[0] == false` → new component found

* `components = 1`
* Call `dfs(0)`

#### DFS(0)

* mark `visited[0]=true`
* neighbors: 1 → not visited → dfs(1)

#### DFS(1)

* mark `visited[1]=true`
* neighbors: 0 (visited), 2 (not visited) → dfs(2)

#### DFS(2)

* mark `visited[2]=true`
* neighbor: 1 (visited) → return

Return chain completes. Now:

```text
visited = [T, T, T, F, F, F, F]
components = 1
```

---

### i = 1, 2

Already visited → skip

---

### i = 3

`visited[3] == false` → new component

* `components = 2`
* `dfs(3)`

#### DFS(3)

* mark visited[3]=true
* neighbor 4 not visited → dfs(4)

#### DFS(4)

* mark visited[4]=true
* neighbor 3 visited → return

Now:

```text
visited = [T, T, T, T, T, F, F]
components = 2
```

---

### i = 4

Visited → skip

---

### i = 5

`visited[5] == false` → new component

* `components = 3`
* `dfs(5)`

#### DFS(5)

* mark visited[5]=true
* neighbor 6 not visited → dfs(6)

#### DFS(6)

* mark visited[6]=true
* neighbor 5 visited → return

Now:

```text
visited = [T, T, T, T, T, T, T]
components = 3
```

---

### i = 6

Visited → skip

---

## Final answer

`components = 3` ✅

---

# Complexity

Let `V = n`, `E = edges.length`

* Time: `O(V + E)` (each node visited once, each edge processed twice)
* Space: `O(V + E)` for adjacency list + `O(V)` recursion stack worst-case

---

## Note about recursion depth

If `n` can be very large (like 200k) and the graph is a long chain, recursion can overflow the stack in Java.

In that case, use:

* iterative DFS (stack), or
* BFS (queue)
*/

// class Solution {
//     public int countComponents(int n, int[][] edges) {
//         List<Integer>[] adjList = new ArrayList[n];
//         for (int i = 0; i < n; i++) {
//             adjList[i] = new ArrayList<>();
//         }

//         for (int[] e : edges) {
//             int u = e[0];
//             int v = e[1];
//             adjList[u].add(v);
//             adjList[v].add(u);
//         }

//         boolean[] visited = new boolean[n];
//         int components = 0;

//         for (int i = 0; i < n; i++) {
//             if (!visited[i]) {
//                 components++;
//                 dfs(i, adjList, visited);
//             }
//         }

//         return components;
//     }

//     private void dfs(int node, List<Integer>[] adjList, boolean[] visited) {
//         visited[node] = true;

//         for (int neighbor : adjList[node]) {
//             if (!visited[neighbor]) {
//                 dfs(neighbor, adjList, visited);
//             }
//         }
//     }
// }
