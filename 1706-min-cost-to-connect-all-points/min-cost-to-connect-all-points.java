// Method 1: Minimum Spanning Tree (Prim's Algorithm wihtout PriorityQueue)
/*
# What is a Minimum Spanning Tree?

* You have a **connected, undirected, weighted graph** (G = (V, E)).
* A **spanning tree** connects **all** vertices with exactly **( |V|-1 )** edges (so no cycles).
* A **minimum** spanning tree (MST) is a spanning tree with **minimum total weight** among all spanning trees.

## Two fundamental properties (how algorithms know their choices are safe)

1. **Cut property**
   Consider any cut (partition of vertices into two sets). The **minimum-weight edge crossing that cut** is *safe* to include in an MST.

2. **Cycle property**
   In any cycle, the **maximum-weight edge** is *never* part of *some* MST (you can drop it and not increase the total).

Prim’s algorithm uses the **cut** property; Kruskal’s uses the **cycle** property (via a Union-Find to avoid cycles).

---

# Two classic MST algorithms

## A) Prim’s algorithm (grow a tree from one node)

* Start from any node.
* Maintain the cheapest edge that connects the **grown set** of nodes to any **outside** node.
* Repeatedly pick the smallest such edge and add the new node.
* Stop when all nodes are in; the sum of chosen edges is the MST cost.

**Complexities**

* Dense graph with an array of best costs: **(O(V^2))** (great for (V \le 2000)).
* With a binary heap + adjacency lists: **(O(E \log V))**.

## B) Kruskal’s algorithm (sort edges, add if no cycle)

* Sort **all edges** by weight ascending.
* Scan in order; add an edge if it connects two **different** components (detected by Disjoint Set Union / Union-Find).
* Stop after taking ( |V|-1 ) edges.

**Complexities**

* Sorting dominates: **(O(E \log E))**; Union-Find ops are effectively ( \alpha(V) ) (near constant).

---

# Why 1584 is an MST problem

**Problem recap**: You’re given (n) points in the plane. The **cost to connect two points** is their **Manhattan distance**
[
\text{cost}((x_i,y_i),(x_j,y_j)) = |x_i-x_j| + |y_i-y_j|.
]
You want the **minimum total cost** to connect all points (no need to return the edges). That is exactly the **MST cost** of the complete graph on these points with the above edge weights.

* Graph is **complete**: (E \approx n(n-1)/2).
* Building + sorting all edges costs (O(n^2 \log n^2)) and uses (O(n^2)) memory—works up to ~(n=1000) but is heavy.
* **Prim without materializing edges** shines: compute distances **on the fly** and track the minimum cost to connect each unvisited vertex → **(O(n^2))** time and **(O(n))** extra space.

**Bottom line for 1584**: Use **Prim’s (O(n^2))** (array version). It’s simpler and efficient for (n \le 1000).

---

# Prim for 1584 — how it looks conceptually

We’ll keep two arrays/sets:

* `inMST[i]` — whether point (i) is already included.
* `minCost[i]` — the **current best** cost to connect point (i) to **any** point already in the MST.
  Initialize `minCost[src]=0`, `minCost[others]=+∞`.

Loop (n) times:

1. Pick the **unvisited** node `u` with minimum `minCost[u]`.
   Add `minCost[u]` to `answer`. Mark `u` visited.
2. For every **unvisited** node `v`, update:
   [
   \text{minCost}[v] = \min(\text{minCost}[v], \text{Manhattan}(u, v)).
   ]
3. Repeat until all nodes are visited. The sum of `minCost[u]` picked each round is the MST cost.

No priority queue is necessary; a simple linear scan for the minimum each round gives total (O(n^2)).

---

# Full, thorough walkthrough (sample from the problem)

**Points** (index → ((x,y)))
0 → (0,0)
1 → (2,2)
2 → (3,10)
3 → (5,2)
4 → (7,0)

We’ll run **Prim** starting at node 0.

Initialize:

* `inMST = [F, F, F, F, F]`
* `minCost = [0, +∞, +∞, +∞, +∞]`
* `answer = 0`

We also need Manhattan distances (we’ll compute on the fly when updating).

### Iteration 1

Pick unvisited min:

* Among {0..4}, `minCost` is smallest at node **0 (0)**.
* Add to MST: `answer += 0 → 0`; `inMST[0]=T`.

Update neighbors’ best connection via node 0:

* dist(0,1) = |0-2| + |0-2| = 4 → `minCost[1]=4`
* dist(0,2) = |0-3| + |0-10| = 13 → `minCost[2]=13`
* dist(0,3) = |0-5| + |0-2| = 7 → `minCost[3]=7`
* dist(0,4) = |0-7| + |0-0| = 7 → `minCost[4]=7`

Now:

* `minCost = [0, 4, 13, 7, 7]`

### Iteration 2

Pick unvisited min:

* Candidates: 1(4), 2(13), 3(7), 4(7) → pick **1 (4)**.
* `answer += 4 → 4`; `inMST[1]=T`.

Update via node 1:

* dist(1,2) = |2-3| + |2-10| = 1 + 8 = 9 → `minCost[2] = min(13, 9) = 9`
* dist(1,3) = |2-5| + |2-2| = 3 + 0 = 3 → `minCost[3] = min(7, 3) = 3`
* dist(1,4) = |2-7| + |2-0| = 5 + 2 = 7 → `minCost[4] = min(7, 7) = 7`

Now:

* `minCost = [0, 4, 9, 3, 7]`

### Iteration 3

Pick unvisited min:

* Candidates: 2(9), 3(3), 4(7) → pick **3 (3)**.
* `answer += 3 → 7`; `inMST[3]=T`.

Update via node 3:

* dist(3,2) = |5-3| + |2-10| = 2 + 8 = 10 → `minCost[2] = min(9,10) = 9` (no change)
* dist(3,4) = |5-7| + |2-0| = 2 + 2 = 4 → `minCost[4] = min(7,4) = 4`

Now:

* `minCost = [0, 4, 9, 3, 4]`

### Iteration 4

Pick unvisited min:

* Candidates: 2(9), 4(4) → pick **4 (4)**.
* `answer += 4 → 11`; `inMST[4]=T`.

Update via node 4:

* dist(4,2) = |7-3| + |0-10| = 4 + 10 = 14 → `minCost[2] = min(9,14) = 9` (no change)

### Iteration 5

Pick remaining:

* Only node **2 (9)** left.
* `answer += 9 → 20`; `inMST[2]=T`.

All nodes taken → **MST cost = 20**.
(This matches the official sample answer.)

---

# Kruskal’s for 1584 (when & how)

* You’d generate **all pairs** ((i,j)), compute their Manhattan distance, push into a list of edges, sort by weight, then run Union-Find and take the first (n-1) edges that connect different components.
* Works fine for (n \le 1000) (about 500k edges), but is heavier in memory and sorting time than Prim’s (O(n^2)) without materializing edges.

Use Kruskal if:

* The graph is **sparse** (few edges vs (n^2)).
* You already have explicit edges and weights, and sorting them is natural.

Use Prim if:

* The graph is **dense** or implicit (like 1584).
* You can cheaply compute “distance to the current tree” on the fly.

---

# Common pitfalls & pro tips

* **1584 uses Manhattan distance** ( |x_1-x_2| + |y_1-y_2| ), **not** Euclidean.
* In Prim, the **starting node doesn’t matter**—you’ll get the same total cost (the MST is unique if all edge weights are distinct; otherwise total cost is still minimal either way).
* Your `minCost` value that you add each round is exactly the weight of the edge that connects the new node to the MST—**sum them up**.
* Don’t double-count: Prim adds exactly (n) picks, but the first pick is `0` for the start; the remaining (n-1) picks are the actual edges.
* For Kruskal, **Union-Find (DSU)** with path compression + union-by-rank/size is key for performance.
* If you ever get multiple identical MSTs (ties), **1584 only wants the cost**, so you’re safe—no tie-breaking needed.

---

# Quick interview checklist

* “This is an MST over a complete graph with Manhattan edge weights.”
* “I’ll use **Prim (O(n^2))** with an array of best edges; no need to store all edges.”
* “Initialize `minCost[0]=0`, others `∞`; repeat `n` times: pick min unvisited, add its cost, update neighbors via Manhattan.”
* “Total is the MST cost.”
* Optionally: “Kruskal works but needs (O(n^2)) edges & sort—heavier here.”



### Why this works (Prim in one breath)

* Maintain a set **S** of points already in the tree and an array `minCost[v]` = cheapest edge from **S** to `v ∉ S`.
* Each round, choose `u` with the smallest `minCost[u]`, add it to **S**, and update neighbors.
* The sum of chosen `minCost[u]` values is exactly the MST cost (cut property guarantees optimality).

**Time:** (O(n^2)) (n selections × n relaxations).
**Space:** (O(n)).

---

## Thorough walkthrough on the classic sample

**points** (index → (x,y)):

```
0 → (0,0)
1 → (2,2)
2 → (3,10)
3 → (5,2)
4 → (7,0)
```

Initialize:

```
inMST = [F, F, F, F, F]
minCost = [0, ∞, ∞, ∞, ∞]
total = 0
```

### Iteration 1

Pick smallest unvisited `minCost`: node **0** (0).
Add it: `total += 0 = 0`, `inMST[0]=T`.

Relax via node 0:

* cost(0,1)=|0-2|+|0-2|=4  → minCost[1]=4
* cost(0,2)=|0-3|+|0-10|=13 → minCost[2]=13
* cost(0,3)=|0-5|+|0-2|=7  → minCost[3]=7
* cost(0,4)=|0-7|+|0-0|=7  → minCost[4]=7

State:

```
minCost = [0, 4, 13, 7, 7]
```

### Iteration 2

Pick smallest among unvisited: **1** (4).
`total += 4 = 4`, `inMST[1]=T`.

Relax via node 1:

* cost(1,2)=|2-3|+|2-10|=1+8=9 → minCost[2]=min(13,9)=9
* cost(1,3)=|2-5|+|2-2|=3+0=3 → minCost[3]=min(7,3)=3
* cost(1,4)=|2-7|+|2-0|=5+2=7 → minCost[4]=min(7,7)=7

State:

```
minCost = [0, 4, 9, 3, 7]
```

### Iteration 3

Pick smallest: **3** (3).
`total += 3 = 7`, `inMST[3]=T`.

Relax via node 3:

* cost(3,2)=|5-3|+|2-10|=2+8=10 → minCost[2]=min(9,10)=9 (no change)
* cost(3,4)=|5-7|+|2-0|=2+2=4  → minCost[4]=min(7,4)=4

State:

```
minCost = [0, 4, 9, 3, 4]
```

### Iteration 4

Pick smallest: **4** (4).
`total += 4 = 11`, `inMST[4]=T`.

Relax via node 4:

* cost(4,2)=|7-3|+|0-10|=4+10=14 → minCost[2]=min(9,14)=9 (no change)

### Iteration 5

Only **2** remains with `minCost[2]=9`.
`total += 9 = 20`.

All nodes included ⇒ **answer = 20**.

---

## Common pitfalls & quick fixes

* **Materializing all edges** (Kruskal) is (O(n^2)) memory + sort; Prim (O(n^2)) without storing edges is simpler/faster here.
* **Forgetting Manhattan**: use `|x1-x2| + |y1-y2|` (not Euclidean).
* **Overflow worries**: intermediate total might approach ~2e9; using `long total` is safe, cast at return.
*/
class Solution {
    public int minCostConnectPoints(int[][] points) {
        int n = points.length;
        if (n <= 1) return 0;

        // inMST[i] = whether point i is already included in the growing tree
        boolean[] inMST = new boolean[n];

        // minCost[i] = current cheapest cost to connect point i to the tree
        int[] minCost = new int[n];
        final int INF = Integer.MAX_VALUE / 4;
        for (int i = 0; i < n; i++) minCost[i] = INF;

        // Start from any node (0). Its connection cost is 0 by convention.
        minCost[0] = 0;

        long total = 0; // use long internally for safety; will fit in int at the end

        // Repeat n times: each time, pick the unvisited node with smallest minCost.
        for (int iter = 0; iter < n; iter++) {
            int u = -1, best = INF;
            for (int i = 0; i < n; i++) {
                if (!inMST[i] && minCost[i] < best) {
                    best = minCost[i];
                    u = i;
                }
            }

            // Add that node to MST and pay its connection cost.
            inMST[u] = true;
            total += best;

            // Relax: update the best edge to every remaining node via u.
            for (int v = 0; v < n; v++) {
                if (!inMST[v]) {
                    int w = Math.abs(points[u][0] - points[v][0]) + Math.abs(points[u][1] - points[v][1]);
                    if (w < minCost[v]) minCost[v] = w;
                }
            }
        }
        return (int) total;
    }
}





// Method 2: Minimum Spanning Tree (Prim's Algorithm with PriorityQueue)
/*
## Prim with a priority queue (lazy decrease-key)

**Idea:** Grow the MST from any start (index 0).
Keep a min-heap of the *current best known edge* to connect each **outside** vertex to the MST.
When we pop a vertex, if it’s already in MST we **skip** (stale entry); otherwise we finalize it and relax neighbors.


### Why this works

* **Cut property:** At each step we add the outside vertex with the smallest edge from the current tree—this edge is safe.
* **Lazy “decrease-key”:** Java’s `PriorityQueue` can’t update keys in place. We push the improved pair `(newCost, v)` and, when an older `(oldCost, v)` surfaces later, we skip it because either `inMST[v]` is already true or `oldCost` no longer matches the best known value.
* **No explicit edges:** For 1584 the graph is complete; computing Manhattan distances on the fly in the relax step avoids building/storing (O(n^2)) edges.

**Time:** Up to (O(n^2 \log n)) pushes/relaxations (dense).
**Space:** (O(n)) plus heap entries (can be (O(n^2)) in the worst case for dense graphs).

> Note: For **1584**, an (O(n^2)) array-based Prim (no heap) is often a bit faster, but you specifically asked for the heap version—this is the standard template you’ll reuse for **sparse** graphs too.

---

## Detailed example walkthrough

Points (index → (x, y)):

```
0 → (0,0)
1 → (2,2)
2 → (3,10)
3 → (5,2)
4 → (7,0)
```

We’ll start from vertex **0**.

**Init**

```
inMST = [F, F, F, F, F]
best  = [∞, ∞, ∞, ∞, ∞]
pq    = [(0,0)]
total = 0, taken = 0
```

### Step 1: pop (0, 0)

* `u = 0` not in MST → add it.
* `total += 0 = 0`, `taken = 1`, `inMST[0] = T`.

Relax all v ∉ MST from u = 0 (compute Manhattan):

* w(0,1) = |0-2| + |0-2| = 4     → best[1]=4;  push (4,1)
* w(0,2) = |0-3| + |0-10| = 13   → best[2]=13; push (13,2)
* w(0,3) = |0-5| + |0-2|  = 7    → best[3]=7;  push (7,3)
* w(0,4) = |0-7| + |0-0|  = 7    → best[4]=7;  push (7,4)

Heap now: `(4,1), (7,3), (7,4), (13,2)` (min at top).

### Step 2: pop (4, 1)

* `u = 1` not in MST → add it.
* `total += 4 = 4`, `taken = 2`, `inMST[1] = T`.

Relax via u = 1:

* w(1,2) = |2-3| + |2-10| = 1 + 8 = 9  → best[2] improves 13→9;  push (9,2)
* w(1,3) = |2-5| + |2-2|  = 3 + 0 = 3  → best[3] improves 7→3;   push (3,3)
* w(1,4) = |2-7| + |2-0|  = 5 + 2 = 7  → best[4] stays 7 (no push)

Heap (unordered view): `(3,3), (7,3), (7,4), (13,2), (9,2)`

> Note the “stale” entries like `(7,3)` and `(13,2)`—they remain in the heap but won’t be used because we’ve already pushed better keys `(3,3)` and `(9,2)`.

### Step 3: pop (3, 3)

* `u = 3` not in MST → add it.
* `total += 3 = 7`, `taken = 3`, `inMST[3] = T`.

Relax via u = 3:

* w(3,2) = |5-3| + |2-10| = 2 + 8 = 10 → best[2] remains 9 (no push)
* w(3,4) = |5-7| + |2-0|  = 2 + 2 = 4  → best[4] improves 7→4; push (4,4)

Heap contains: `(4,4), (7,4), (7,3), (13,2), (9,2)` (top is `(4,4)`)

### Step 4: pop (4, 4)

* `u = 4` not in MST → add it.
* `total += 4 = 11`, `taken = 4`, `inMST[4] = T`.

Relax via u = 4:

* w(4,2) = |7-3| + |0-10| = 4 + 10 = 14 → best[2] stays 9 (no push)

Heap: `(7,4), (7,3), (13,2), (9,2)`.

### Step 5: pop until we get the next real vertex

* Pop `(7,4)`: `inMST[4]` is already true → **stale**, skip.
* Pop `(7,3)`: `inMST[3]` is true → **stale**, skip.
* Pop `(9,2)`: `u = 2` not in MST → add it.

  * `total += 9 = 20`, `taken = 5`, `inMST[2] = T`.

All 5 vertices taken → **answer = 20**.

That matches the expected MST cost for this set of points.

---

## Tips, pitfalls, and when to use which Prim

* **Manhattan distance** (|x1−x2| + |y1−y2|), not Euclidean.
* Lazy PQ means duplicated entries are fine—always guard with `if (inMST[u]) continue;`.
* The `best[v]` check ensures we only push improved keys, keeping the heap leaner.
* For **dense** graphs like this complete one, the **array-based O(n²)** Prim (no heap) is usually faster/simpler.
  The **heap Prim** shines on **sparse** graphs where each vertex has few neighbors and you have a real adjacency list.

If you’d like, I can also show the adjacency-list flavor (for sparse graphs) or compare Kruskal vs. Prim on a different example.
*/

// class Solution {
//     public int minCostConnectPoints(int[][] points) {
//         int n = points.length;
//         if (n <= 1) return 0;

//         boolean[] inMST = new boolean[n];                // whether node is already in MST
//         int[] best = new int[n];                         // best[v] = current cheapest connection cost to MST
//         Arrays.fill(best, Integer.MAX_VALUE / 4);

//         // (cost, vertex). We start by offering (0, 0) so the first picked vertex costs 0.
//         PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
//         pq.offer(new int[]{0, 0});

//         long total = 0;
//         int taken = 0;

//         while (taken < n) {
//             int[] top = pq.poll();
//             int cost = top[0], u = top[1];

//             // Lazy deletion: skip stale entries or vertices already in MST
//             if (inMST[u]) continue;

//             // Finalize u
//             inMST[u] = true;
//             total += cost;
//             taken++;

//             // Relax all outside vertices using u (no explicit adjacency: compute on the fly)
//             for (int v = 0; v < n; v++) {
//                 if (!inMST[v]) {
//                     int w = Math.abs(points[u][0] - points[v][0]) + Math.abs(points[u][1] - points[v][1]);
//                     if (w < best[v]) {
//                         best[v] = w;
//                         pq.offer(new int[]{w, v});       // push improved key; older (worse) entries for v will be skipped later
//                     }
//                 }
//             }
//         }
//         return (int) total;
//     }
// }






// Method 3: Minimum Spanning Tree (Prim's Algorithm with Adjacency List and Priority Queue)
/*
# Prim (adjacency list, edge-PQ flavor)

### When to use this

* You have a **weighted, undirected** graph given as edges.
* The graph is **sparse** (E ≪ V²).
* You want the **MST total cost** (and optionally the edges).

### Core idea

Maintain a set **S** of vertices already in the MST. A **min-heap** of candidate edges stores all edges that **cross the cut** (one endpoint in S, the other outside). Repeatedly:

1. Pop the smallest edge (w, u→v) from the heap.
2. If v is already in S, skip (stale).
3. Otherwise, add v to S, add w to the answer, and push all edges (v→x) where x ∉ S.

By the **cut property**, the minimum edge crossing the cut is always safe for the MST.


### Why this works

* The heap always holds edges that **cross** the cut (S, V \ S).
* The **smallest** such edge is always safe to add (cut property).
* We skip **stale** edges (those whose `to` endpoint is already in S).

**Time:** (O((E + V)\log E) \approx O(E \log E)).
**Space:** (O(E + V)).

---

# Detailed example walkthrough

Let’s run the template on a small sparse graph (undirected):

Vertices: `0..5`
Edges (u, v, w):

```
0-1(4), 0-2(3),
1-2(1), 1-3(2),
2-3(4), 2-4(5),
3-4(1), 3-5(7),
4-5(2)
```

We’ll start Prim at node **0**.

**Adjacency lists** (neighbor, weight):

```
0: (1,4) (2,3)
1: (0,4) (2,1) (3,2)
2: (0,3) (1,1) (3,4) (4,5)
3: (1,2) (2,4) (4,1) (5,7)
4: (2,5) (3,1) (5,2)
5: (3,7) (4,2)
```

Initialize:

```
inMST = [T, F, F, F, F, F]    (we chose 0)
pq = edges from 0 = {(3,0,2), (4,0,1)}    // (w,u,v)
total = 0, taken = 1
parent = [-1,-1,-1,-1,-1,-1]
```

### Step 1: pop (3,0,2)

* v=2 not in MST → take it.
* total += 3 → 3; inMST[2]=T; parent[2]=0; taken=2
* push edges from 2 to outside: (0,3) (1,1) (3,4) (4,5)

  * 0 already in MST → we can still push; it will be stale when popped
  * push (1,2,1), (4,2,3), (5,2,4), (3,2,0)
* pq now (unordered view): {(1,2,1), (4,0,1), (4,2,3), (5,2,4), (3,2,0)}

### Step 2: pop (1,2,1)

* v=1 not in MST → take it.
* total += 1 → 4; inMST[1]=T; parent[1]=2; taken=3
* push from 1: (0,4), (2,1), (3,2)

  * push (4,1,0), (1,1,2), (2,1,3)

pq roughly holds:

```
(2,1,3), (4,2,3), (5,2,4), (4,0,1), (3,2,0), (4,1,0), (1,1,2)
```

(many will be stale when popped)

### Step 3: pop (1,1,2)

* v=2 already in MST → **stale**, skip.

Next pop (2,1,3):

* v=3 not in MST → take it.
* total += 2 → 6; inMST[3]=T; parent[3]=1; taken=4
* push from 3: (1,3,4), (4,3,2), (2,3,1), (7,3,5)

pq now includes a new **(1,3,4)** edge—note it’s tiny.

### Step 4: pop (1,3,4)

* v=4 not in MST → take it.
* total += 1 → 7; inMST[4]=T; parent[4]=3; taken=5
* push from 4: (5,4,2), (1,4,3), (2,4,5)

### Step 5: next useful pop is (2,4,5)  (others like (1,4,3) are stale)

* v=5 not in MST → take it.
* total += 2 → 9; inMST[5]=T; parent[5]=4; taken=6 (= n)

We’re done: **MST cost = 9**.
Chosen edges via `parent`:

```
2-0 (3), 1-2 (1), 3-1 (2), 4-3 (1), 5-4 (2)
```

Total `3+1+2+1+2 = 9`.

### What just happened (intuition)

We always added the **cheapest** edge that connects the current tree to a new vertex:

* 0→2 (3), 2→1 (1), 1→3 (2), 3→4 (1), 4→5 (2).
  Every time we added a vertex, we threw all its edges into the heap. Entries that later referred to a vertex already in MST were **stale** and skipped—this is the typical “lazy” pattern (no decrease-key needed).

---

# Tips & pitfalls

* **Undirected graph:** add edges both ways in the adjacency list.
* **Disconnected graph:** Prim will stop early; check `taken == n` (or return `-1`) if not connected.
* **Lazy vs. decrease-key:** Java doesn’t have decrease-key on `PriorityQueue`, so we push improved edges and **skip stale** ones on pop.
* **Reconstructing edges:** keep `parent[v] = u` when you accept `(w,u,v)`. Your MST is all `parent[v]--v` for `v≠start`.
* **Weights:** Any nonnegative weights are fine. (MST is well-defined with nonnegative; negative also works for MST in general, but your data usually isn’t.)
* **Complexity:** (O(E \log E)). For dense graphs, the **array-based O(n²) Prim** is usually better.

---

## Where this pattern shines

* Road networks, sparse connectivity, “connect components at minimum cost” when edges are given explicitly.
* If your input is a **complete graph** with an implicit metric (like LC 1584), prefer the **O(n²) Prim** (compute weights on the fly) to avoid materializing (O(n^2)) edges.

If you want, I can adapt this to:

* return the **edge list** of the MST,
* handle **1-based** vertex labels,
* or show how to plug this straight into problems like “connect cities with min total cost.”
*/

// class Solution {
//     // n = number of vertices (0..n-1)
//     // edges: each as {u, v, w} for undirected edge u--v with weight w
//     public int primMSTCost(int n, int[][] edges) {
//         // Build adjacency list: g[u] = list of (neighbor, weight)
//         List<int[]>[] g = new ArrayList[n];
//         for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
//         for (int[] e : edges) {
//             int u = e[0], v = e[1], w = e[2];
//             g[u].add(new int[]{v, w});
//             g[v].add(new int[]{u, w});
//         }

//         boolean[] inMST = new boolean[n];
//         int taken = 0;
//         long total = 0;

//         // (w, u, v): edge from u (in MST) to v (outside). We only need v+w to grow the tree,
//         // but carrying u lets you reconstruct the MST edges if you want.
//         PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

//         // Start the MST from an arbitrary node, say 0.
//         inMST[0] = true;
//         for (int[] e : g[0]) {
//             pq.offer(new int[]{e[1], 0, e[0]}); // (w, from=0, to=e[0])
//         }
//         taken = 1;

//         // Optional: parent array if you want the chosen edges
//         int[] parent = new int[n];
//         Arrays.fill(parent, -1);

//         while (taken < n && !pq.isEmpty()) {
//             int[] top = pq.poll();
//             int w = top[0], u = top[1], v = top[2];

//             if (inMST[v]) continue;      // stale edge crossing to an already-added vertex

//             // Take this edge into MST
//             inMST[v] = true;
//             total += w;
//             parent[v] = u;
//             taken++;

//             // Push all new crossing edges from v
//             for (int[] e : g[v]) {
//                 int nxt = e[0], ww = e[1];
//                 if (!inMST[nxt]) {
//                     pq.offer(new int[]{ww, v, nxt});
//                 }
//             }
//         }

//         // If the graph isn't connected, we can't span all vertices
//         if (taken < n) return -1; // or throw, depending on your use-case

//         // If you want the actual MST edge list, parent[v] (v != 0) gives the chosen edge parent[v]--v.
//         return (int) total;
//     }
// }






// Method 4: Minimum Spanning Tree (Kruskal's Algorithm (Uses DSU))
/*
## How Kruskal’s works here:

* The problem asks for the **minimum total cost** to connect all points where edge cost is **Manhattan distance**. That’s exactly the **MST** cost of the complete graph on the points.
* **Kruskal’s algorithm**:

  1. Sort all edges by weight ascending.
  2. Scan edges; for each edge, if its endpoints are in different components, **union** them and add the weight to the answer.
  3. Stop after taking **n−1** edges.

**Complexity**

* Building edges: (O(n^2)) edges.
* Sorting: (O(n^2 \log n)).
* DSU operations: (O(n^2 \cdot \alpha(n))) (almost linear).
* Space: (O(n^2)) for the edge list.

> For (n \le 1000), this is fine (≈ 500k edges). If you want better memory/time for dense graphs, use **Prim’s (O(n^2))** (no edge list), which you already have.

---

## Detailed example walkthrough

Points (index → (x, y)):

```
0 → (0,0)
1 → (2,2)
2 → (3,10)
3 → (5,2)
4 → (7,0)
```

### 1) Build all edges with Manhattan weights

Compute |xi−xj| + |yi−yj| for each pair i<j:

* (0,1) = 4     (|0-2| + |0-2|)
* (0,2) = 13    (|0-3| + |0-10|)
* (0,3) = 7     (|0-5| + |0-2|)
* (0,4) = 7     (|0-7| + |0-0|)
* (1,2) = 9     (|2-3| + |2-10|)
* (1,3) = 3     (|2-5| + |2-2|)
* (1,4) = 7     (|2-7| + |2-0|)
* (2,3) = 10    (|3-5| + |10-2|)
* (2,4) = 14    (|3-7| + |10-0|)
* (3,4) = 4     (|5-7| + |2-0|)

### 2) Sort edges by weight

Weights in ascending order:

```
3 : (1,3)
4 : (0,1), (3,4)
7 : (0,3), (0,4), (1,4)
9 : (1,2)
10: (2,3)
13: (0,2)
14: (2,4)
```

### 3) Kruskal scan with DSU

Initially each vertex is its own set.

* Take **(1,3)** cost 3 → merge {1} & {3}. Total = 3
* Take **(0,1)** cost 4 → merge {0} & {1,3}. Total = 7
* Take **(3,4)** cost 4 → merge {4} into {0,1,3}? Wait: (3,4) connects set {1,3,0} to {4} → allowed. Total = 11
* Next **(0,3)** cost 7 → endpoints already in same set {0,1,3,4} → **skip** (would make a cycle)
* **(0,4)** cost 7 → same set → **skip**
* **(1,4)** cost 7 → same set → **skip**
* **(1,2)** cost 9 → connects {0,1,3,4} to {2} → take it. Total = 20
* We’ve taken **n−1 = 4** edges → **stop**. MST cost = **20**.

This matches the expected answer.

---

## Pitfalls & tips

* **Manhattan distance** (|x1−x2| + |y1−y2|), not Euclidean.
* Ensure DSU is correct (path compression + union by size/rank).
* **Early stop** once you’ve taken **n−1** edges.
* Memory: building all edges uses ~(3 \times) ints per edge; for (n=1000), ~500k edges is OK in Java. If tight, prefer Prim’s (O(n^2)) without storing edges.
*/

// class Solution {
//     public int minCostConnectPoints(int[][] points) {
//         int n = points.length;
//         if (n <= 1) return 0;

//         // 1) Build all edges of the complete graph: O(n^2)
//         int m = n * (n - 1) / 2;
//         int[][] edges = new int[m][3]; // each: {weight, u, v}
//         int idx = 0;
//         for (int i = 0; i < n; i++) {
//             for (int j = i + 1; j < n; j++) {
//                 int w = Math.abs(points[i][0] - points[j][0]) + Math.abs(points[i][1] - points[j][1]);
//                 edges[idx++] = new int[]{w, i, j};
//             }
//         }

//         // 2) Sort edges by weight: O(m log m)
//         Arrays.sort(edges, Comparator.comparingInt(a -> a[0]));

//         // 3) Kruskal with DSU: take smallest edges that connect different components
//         DSU dsu = new DSU(n);
//         long total = 0;
//         int taken = 0;

//         for (int[] e : edges) {
//             int w = e[0], u = e[1], v = e[2];
//             if (dsu.union(u, v)) {     // merges two components
//                 total += w;
//                 taken++;
//                 if (taken == n - 1) break; // MST complete
//             }
//         }
//         return (int) total;
//     }

//     // Disjoint Set Union with path compression + union by size
//     static class DSU {
//         int[] parent;
//         int[] size;

//         DSU(int n) {
//             parent = new int[n];
//             size = new int[n];
//             for (int i = 0; i < n; i++) {
//                 parent[i] = i;
//                 size[i] = 1;
//             }
//         }
//         int find(int x) {
//             if (parent[x] != x) parent[x] = find(parent[x]);
//             return parent[x];
//         }
//         boolean union(int a, int b) {
//             int ra = find(a), rb = find(b);
//             if (ra == rb) return false;
//             if (size[ra] < size[rb]) { int t = ra; ra = rb; rb = t; }
//             parent[rb] = ra;
//             size[ra] += size[rb];
//             return true;
//         }
//     }
// }
