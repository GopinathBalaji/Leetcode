// Method 1: Minimax Shorted Path (Dijkstra's Algorithm with modified relaxation)
/*
Here’s a crisp way to think about both ideas, then code you can drop in, and a careful walkthrough.

# What’s Dijkstra’s algorithm?

* It solves **single-source shortest paths** on a graph with **non-negative** edge weights.
* You keep a distance array `dist[v] = best known sum of weights from source s to v`.
* Repeatedly pick the node with **smallest `dist`** from a min-heap, then **relax** its outgoing edges:

  ```
  if (dist[u] + w(u→v) < dist[v]) {
      dist[v] = dist[u] + w(u→v)
      push (dist[v], v) into heap
  }
  ```
* The first time you pop a node with its current best distance, that distance is final.

# What’s the “minimax shortest path” variant?

* Some problems don’t minimize the **sum** of edge weights; they minimize the **maximum edge cost along the path**.
* For a path s → … → v, define its cost as `max(edge weights on that path)`. Among all s→v paths, choose the one with **smallest possible maximum**.
* You can solve this with Dijkstra’s machinery by changing the relax step to:

  ```
  newCost = max(dist[u], w(u→v))
  if (newCost < dist[v]) update and push
  ```
* This is exactly what **LeetCode 778. Swim in Rising Water** needs, where `w(u→v)` is the elevation of the cell you’re entering, and your path cost is the **highest elevation** you must “wait” to reach.

---

# A. Classic Dijkstra (sum of weights) — adjacency list template (Java)

Use this when you truly minimize **sum** of weights.

```java
import java.util.*;

class DijkstraSumTemplate {
    // n: number of nodes (0..n-1)
    // graph[u] holds (v, w) directed edges; weights w >= 0
    public long[] dijkstraSum(int n, List<int[]>[] graph, int src) {
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        // (dist, node)
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        pq.offer(new long[]{0L, src});

        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            long d = cur[0];
            int u = (int) cur[1];
            if (d != dist[u]) continue; // lazy deletion / stale entry

            for (int[] e : graph[u]) {
                int v = e[0], w = e[1];
                long nd = d + w;
                if (nd < dist[v]) {
                    dist[v] = nd;
                    pq.offer(new long[]{nd, v});
                }
            }
        }
        return dist; // dist[v] = +∞ (INF) if unreachable
    }
}
```

# B. Explanation about Minimax

## Why this is correct

* With non-negative “edge costs” (elevations), the first time you pop a cell `(r,c)` with time `t`, no other path can reach `(r,c)` with a strictly smaller maximal elevation; hence `t` is final.
* The relax rule `newTime = max(currTime, elev(neighbor))` exactly captures the **bottleneck** (maximum) objective.

## Complexity

* Each cell may be pushed multiple times, but only the best one “sticks.”
  Time `O(n^2 log n^2)` = `O(n^2 log n)`. Space `O(n^2)`.

---

# C. General “minimax Dijkstra” on a graph (not just grids)

Sometimes edges themselves have costs, and you want the path that minimizes the **maximum edge on the path**. Change only the relax line:

```java
import java.util.*;

class DijkstraMinimaxTemplate {
    // graph[u] holds (v, wEdge). Path cost is max edge on path.
    public int[] dijkstraMinimax(int n, List<int[]>[] graph, int src) {
        int INF = Integer.MAX_VALUE;
        int[] best = new int[n]; // best[v] = minimal possible bottleneck to v
        Arrays.fill(best, INF);
        best[src] = 0;

        // (bottleneck, node)
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, src});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int b = cur[0], u = cur[1];
            if (b != best[u]) continue; // stale

            for (int[] e : graph[u]) {
                int v = e[0], w = e[1];
                int nb = Math.max(b, w);      // minimax relax
                if (nb < best[v]) {
                    best[v] = nb;
                    pq.offer(new int[]{nb, v});
                }
            }
        }
        return best;
    }
}
```

---

# Walkthrough (tiny 2×2 example from 778)

Grid:

```
0 2
1 3
```

We model moves 4-directionally.

Initialize:

```
dist[0][0] = 0        // actually grid[0][0] = 0
pq = (0,0,0)
others = ∞
```

Process:

1. Pop (0,0,0).

   * Neigh (0,1): newTime = max(0,2) = 2 → dist[0][1]=2, push (2,0,1)
   * Neigh (1,0): newTime = max(0,1) = 1 → dist[1][0]=1, push (1,1,0)

2. Pop (1,1,0).

   * Neigh (1,1): newTime = max(1,3)=3 → dist[1][1]=3, push (3,1,1)

3. Pop (2,0,1).

   * Neigh (1,1): newTime = max(2,3)=3; dist[1][1] is already 3 → no improve

4. Pop (3,1,1) = target → **answer = 3**.

Interpretation: the earliest time you can have a fully submerged path to the bottom-right is `3`, matching the highest tile you must step on along the best route (0→1→3).

---

# Common pitfalls (and easy fixes)

* **Marking visited too early:** In weighted settings, don’t mark visited when you push; finalize when you pop the **current best** entry (`if (t != dist[r][c]) continue`).
* **Wrong objective:** 778 is **minimize the maximum** elevation, not sum. Use `max(...)` in the relax step, not `+`.
* **Unordered PQ on arrays:** In Java, `PriorityQueue<int[]>` **needs a comparator**; arrays aren’t comparable by default.
* **Overflow:** For classic Dijkstra on sums, use `long` for distances if sums can exceed `int`. Minimax 778 stays within `int`.

---

## When to use which

* **Classic Dijkstra (sum):** shortest path by total weight (e.g., road travel times).
* **Minimax Dijkstra:** minimize the worst edge on the path (778; “path with minimum effort”; “minimize maximum risk”).
* **Alternatives for 778:**

  * **Binary search + BFS/DFS** on time threshold T (monotone feasibility).
  * **Union-Find (activation/Kruskal)**: increase water level or sort edges by max(elevations), union until start/end connect.
*/


class Solution {
    public int swimInWater(int[][] grid) {
        int n = grid.length;
        // dist[r][c] = minimal max elevation needed to reach (r,c)
        int[][] dist = new int[n][n];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);

        // (time, r, c) with time = minimal max elevation so far
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        dist[0][0] = grid[0][0];
        pq.offer(new int[]{grid[0][0], 0, 0});

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int t = cur[0], r = cur[1], c = cur[2];

            // Lazy deletion: skip if this is not the current best
            if (t != dist[r][c]) continue;

            // Reached goal: the time we pop it is the answer
            if (r == n - 1 && c == n - 1) return t;

            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= n || nc < 0 || nc >= n) continue;

                // Minimax relax: cost to enter neighbor is max of current time and neighbor elevation
                int newTime = Math.max(t, grid[nr][nc]);
                if (newTime < dist[nr][nc]) {
                    dist[nr][nc] = newTime;
                    pq.offer(new int[]{newTime, nr, nc});
                }
            }
        }
        // Problem guarantees connectivity, but return dist if you want:
        return dist[n-1][n-1];
    }
}





// Method 2: Binary Search + BFS (feasibility check)
/*
Below is a tight Java implementation, followed by the intuition, complexity, and a step-by-step walkthrough on a classic example.

## Why this works

### 1) Monotonic predicate

Let `P(T)` = “there exists a path from (0,0) to (n−1,n−1) using only cells with elevation ≤ T.”
If `P(T)` is true, then `P(T')` is also true for all `T' ≥ T` (you can only add more allowed cells).
So `P(T)` is **monotone** → perfect for **binary search**.

### 2) Feasibility via BFS

For a fixed `T`, we run a BFS from (0,0), but we’re only allowed to step into cells with `grid[r][c] ≤ T`.
If we reach (n−1,n−1), then `P(T)` is true; otherwise false.

Binary search narrows to the **smallest T** for which `P(T)` holds — which is exactly the minimum time when a fully submerged path exists.

---

## Complexity

* **Binary search** over `T` in `[lo, hi]` → `O(log (maxElevation))` iterations. Under constraints, `maxElevation ≤ n^2−1`.
* Each feasibility check is a **BFS** over at most `n^2` cells → `O(n^2)`.
* **Total:** `O(n^2 log n)` (since `log(n^2) = 2 log n`).
* **Space:** `O(n^2)` for `visited`.

---

## Thorough example walkthrough

Use the classic 5×5 example:

```
0   1   2   3   4
24 23  22  21   5
12 13  14  15  16
11 17  18  19  20
10  9   8   7   6
```

The known answer is **16**.

* `lo = max(grid[0][0]=0, grid[4][4]=6) = 6`
* `hi = 24` (or `n*n - 1`)

We’ll binary search T and run BFS each time.

### Try `T = (6 + 24) // 2 = 15`

* Allowed cells: elevations ≤ 15.
* BFS from (0,0):

  * First row (0..4) is fully ≤ 4 — we can move right to (0,4).
  * From (0,4)=4, neighbors include (1,4)=5 (ok).
  * From (1,4)=5 → (2,4)=16 (blocked since 16 > 15).
  * From the first row, trying to go downward earlier:

    * (1,0)=24 (blocked), (1,1)=23 (blocked), (1,2)=22 (blocked), (1,3)=21 (blocked).
  * From (1,4)=5, try left/up: those are small but don’t lead down past column 4 due to (2,4)=16 block.
* Can’t reach (4,4). **Infeasible** → increase `lo` to `16`.

### Now `lo=16, hi=24` → `T = (16 + 24)//2 = 20`

* Allowed: ≤ 20.
* BFS:

  * Top row → (0,4).
  * Down the last column: (1,4)=5 ok, (2,4)=16 ok (now allowed!), (3,4)=20 ok, (4,4)=6 ok.
* We reached (4,4). **Feasible** → shrink `hi` to `20`.

### Now `lo=16, hi=20` → `T = 18`

* Allowed: ≤ 18 → last column includes 16 but (3,4)=20 blocked, so that specific route is blocked.
* But we can route differently:

  * Move across the top to (0,4), down to (2,4)=16 (ok), then left into the “middle band”:

    * (2,3)=15 ok, (2,2)=14 ok, (2,1)=13 ok, (2,0)=12 ok
    * From there snake downward: (3,0)=11 ok, (4,0)=10 ok, (4,1)=9 ok, (4,2)=8 ok, (4,3)=7 ok, (4,4)=6 ok
  * Indeed with `T=18`, we **can** reach the end via this lower detour.
* **Feasible** → `hi = 18`.

### Now `lo=16, hi=18` → `T = 17`

* Try the same logic: the “middle band” route still works (all those are ≤ 17 except we never need >17 on that detour).
* **Feasible** → `hi = 17`.

### Now `lo=16, hi=17` → `T = 16`

* Check `T = 16`:

  * Top to (0,4), down to (2,4)=16 (ok).
  * Then the same lower detour is still all ≤ 16: yes, all numbers along that snaking path are ≤ 16.
* **Feasible** → `hi = 16`.

Now `lo == hi == 16` → **answer = 16**. ✅

This exactly matches the minimal water level needed to make *some* path fully submerged.

---

## When to prefer this approach

* When you can state a **monotonic feasibility test** (here: “reachable using cells ≤ T”).
* When implementing Dijkstra feels heavier or you want to demonstrate binary search on answer.
* It’s simple, robust, and very easy to reason about correctness.
*/

// class Solution {
//     public int swimInWater(int[][] grid) {
//         int n = grid.length;

//         // Lower bound: at least the start cell's elevation (you must stand on it),
//         // often people also use max(grid[0][0], grid[n-1][n-1]) — both are valid LBs.
//         int lo = Math.max(grid[0][0], grid[n - 1][n - 1]);
//         int hi = n * n - 1; // max possible elevation per constraints

//         while (lo < hi) {
//             int mid = (lo + hi) >>> 1;  // candidate time T
//             if (canReach(grid, mid)) {
//                 hi = mid;               // feasible → try smaller T
//             } else {
//                 lo = mid + 1;           // infeasible → need more time
//             }
//         }
//         return lo; // == hi
//     }

//     // Feasibility check: can we reach (n-1,n-1) using only cells with elevation <= T?
//     private boolean canReach(int[][] grid, int T) {
//         int n = grid.length;
//         if (grid[0][0] > T) return false;

//         boolean[][] vis = new boolean[n][n];
//         Deque<int[]> q = new ArrayDeque<>();
//         q.offer(new int[]{0, 0});
//         vis[0][0] = true;

//         int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
//         while (!q.isEmpty()) {
//             int[] cur = q.poll();
//             int r = cur[0], c = cur[1];
//             if (r == n - 1 && c == n - 1) return true;

//             for (int[] d : dirs) {
//                 int nr = r + d[0], nc = c + d[1];
//                 if (nr < 0 || nr >= n || nc < 0 || nc >= n || vis[nr][nc]) continue;
//                 if (grid[nr][nc] <= T) {
//                     vis[nr][nc] = true;
//                     q.offer(new int[]{nr, nc});
//                 }
//             }
//         }
//         return false;
//     }
// }








// Method 3: Binary Search + DFS (feasibility check)
/*
## Why this works

### 1) Monotone predicate → binary search

Define (P(T)): “There exists a path from (0,0) to (n−1,n−1) using only cells with elevation ≤ T.”
If (P(T)) is true, then (P(T')) is also true for all (T' \ge T) (adding more allowed cells never breaks reachability). That monotonicity lets us binary search the **smallest** feasible (T).

### 2) Feasibility via DFS

For a fixed (T), run a flood-fill (here, recursive DFS) from (0,0), but only step into cells with `grid[r][c] ≤ T`. If we can reach (n−1,n−1), then (P(T)) holds.

---

## Complexity

* Binary search does (O(\log M)) iterations where (M) is the max elevation (≤ (n^2-1)).
* Each DFS visits each cell at most once → (O(n^2)).
* **Total:** (O(n^2 \log n)).
* **Space:** (O(n^2)) for `seen` + recursion depth up to (n^2) (OK for LC constraints; convert to an explicit stack if you want to avoid recursion).

---

## Thorough example walkthrough

Consider the classic 5×5 grid (answer = **16**):

```
0   1   2   3   4
24 23  22  21   5
12 13  14  15  16
11 17  18  19  20
10  9   8   7   6
```

Bounds:

* `lo = max(grid[0][0], grid[4][4]) = max(0,6) = 6`
* `hi = max cell = 24`

We binary search `T` and run DFS each time (only moving onto cells ≤ `T`):

1. **T = 15**

   * Start at (0,0). Top row (0..4) is all ≤ 4, so DFS reaches (0,4)=4.
   * From (0,4), going down hits (2,4)=16 → **blocked** (16>15).
   * All other downward entrances along the top are 21,22,23,24 → **blocked**.
     → (P(15)) is **false** ⇒ raise `lo` to 16.

2. **T = 20** (mid of [16,24])

   * Top row to (0,4); down the last column: 5 → 16 → 20 → 6 → goal.
     → (P(20)) **true** ⇒ lower `hi` to 20.

3. **T = 18** (mid of [16,20])

   * Last column has 20 at (3,4) → blocked now, but DFS can detour:
     top row → (0,4)=4 → (2,4)=16 ok → left across the “middle band”: 15→14→13→12→(3,0)=11→(4,0)=10→9→8→7→(4,4)=6.
     → (P(18)) **true** ⇒ `hi = 18`.

4. **T = 17** → the same detour still works.
   → (P(17)) **true** ⇒ `hi = 17`.

5. **T = 16** → still works (all cells on that detour ≤ 16).
   → (P(16)) **true** ⇒ `hi = 16`.

Now `lo == hi == 16` ⇒ **answer = 16**.

---

## Pitfalls & pro tips

* Use `≤ T` (not `< T`) when checking cells.
* Reset `seen` for each feasibility check (each `mid`).
* A safe lower bound is `max(grid[0][0], grid[n-1][n-1])`; an even tighter upper bound is the max cell value (we computed it).
* Worried about recursion depth? Switch DFS to an explicit stack (iterative) with the same predicate.

This approach is easy to explain: “binary search the time, and for a fixed time, check reachability by DFS over cells not exceeding that time.” It pairs nicely with the minimax-Dijkstra solution you already saw.
*/

// class Solution {
//     public int swimInWater(int[][] grid) {
//         int n = grid.length;

//         // Lower bound: must at least match start/end elevations.
//         int lo = Math.max(grid[0][0], grid[n - 1][n - 1]);

//         // Upper bound: maximum elevation present anywhere.
//         int hi = 0;
//         for (int[] row : grid) for (int x : row) hi = Math.max(hi, x);

//         while (lo < hi) {
//             int mid = lo + (hi - lo) / 2;      // candidate water level T
//             boolean[][] seen = new boolean[n][n];

//             // Feasible if we can reach target by stepping only on cells <= T
//             if (grid[0][0] <= mid && dfs(grid, 0, 0, mid, seen)) {
//                 hi = mid;                      // try smaller T
//             } else {
//                 lo = mid + 1;                  // need larger T
//             }
//         }
//         return lo; // == hi: minimal feasible T
//     }

//     // DFS that only walks into cells with elevation <= T
//     private boolean dfs(int[][] g, int r, int c, int T, boolean[][] seen) {
//         int n = g.length;
//         if (r == n - 1 && c == n - 1) return true;

//         seen[r][c] = true;
//         // 4-dir moves
//         if (r + 1 < n && !seen[r + 1][c] && g[r + 1][c] <= T && dfs(g, r + 1, c, T, seen)) return true;
//         if (r - 1 >= 0 && !seen[r - 1][c] && g[r - 1][c] <= T && dfs(g, r - 1, c, T, seen)) return true;
//         if (c + 1 < n && !seen[r][c + 1] && g[r][c + 1] <= T && dfs(g, r, c + 1, T, seen)) return true;
//         if (c - 1 >= 0 && !seen[r][c - 1] && g[r][c - 1] <= T && dfs(g, r, c - 1, T, seen)) return true;

//         return false;
//     }
// }






// Method 4: Union–Find (Kruskal/activation)
/*
* **Activation by height (most common & simplest to implement)**
* **Edge-sorting Kruskal (conceptually identical; sometimes taught in MST units)**


# Intuition (why Union–Find works)

Think of the water level (T) rising from 0 upward. A cell ((r,c)) becomes *swimmable* once (T \ge \text{grid}[r][c]). The moment ((0,0)) and ((n-1,n-1)) become connected through already-submerged cells is the **earliest time you can traverse** — that’s the answer.

Union–Find (Disjoint Set Union, DSU) is perfect for **maintaining connectivity** as we gradually “activate” cells in ascending height order.

---

# Approach A: **Activation by height** (recommended)

## Steps

1. Make a list of all cells ((h, r, c)) where (h=\text{grid}[r][c]).
2. Sort the cells by (h) (ascending).
3. Maintain a boolean `active[r][c]` (initially false) and a DSU over all (n^2) cells (`id = r*n + c`).
4. Sweep the sorted list in order:

   * Mark the current cell active.
   * Union it with its **already active** 4-neighbors.
   * After each activation, if `find(id(0,0)) == find(id(n-1,n-1))`, return the current height (h).
     (That’s the earliest level at which start and end become connected.)

## Complexity

* Sorting (n^2) cells: (O(n^2 \log n)).
* Each union/find is effectively (O(\alpha(n^2))) (inverse Ackermann ~ constant).
* Total: **(O(n^2 \log n))** time, **(O(n^2))** space.


# Approach B: **Edge-sorting Kruskal** (equivalent)

Interpret the grid as a graph with edges only between **4-neighbors**. Define each edge weight as:
[
w((r,c) \leftrightarrow (nr,nc)) ;=; \max(\text{grid}[r][c], \text{grid}[nr][nc]).
]
Sort all such edges ascending, and union endpoints. The **first time** start and end become connected, the current edge weight is the answer.

> This is the **minimum bottleneck path** viewpoint: among all paths, minimize the largest edge; Kruskal finds the minimal threshold that connects start and end.

**Complexity:** there are (\approx 2n(n-1)) edges → sorting is (O(n^2 \log n)), unions near linear.

(Activation and Kruskal are two sides of the same coin: both raise a threshold until connectivity holds.)

---

# Detailed walkthrough (classic 5×5 example)

Grid:

```
 0   1   2   3   4
24  23  22  21   5
12  13  14  15  16
11  17  18  19  20
10   9   8   7   6
```

Answer = **16**

## Activation perspective

* Sort all cells by height and “turn them on” in that order.
* Early on, heights `0..4` activate the entire top row; start `(0,0)` can move across the top but cannot drop to row 1 because entries there are `21,22,23,24` (still inactive).
* At height **5**, cell `(1,4)` activates, connecting top-right `(0,4)` downward one step — but the end `(4,4)` is still isolated by larger numbers in between.
* At height **6**, `(4,4)` (the goal) is active but not connected to start yet.
* Heights `7,8,9,10,11,12,13,14,15` progressively activate the **bottom snake** and the **middle band** from the left side: `(4,3) → (4,2) → (4,1) → (4,0) → (3,0) → (2,0) → (2,1) → (2,2) → (2,3)=15`.
* Finally at height **16**, `(2,4)` activates. Now unions along the right column/top row + middle band create a full connected chain from `(0,0)` to `(4,4)`:

  * `(0,0)` across top → `(0,4)` (≤4)
  * down to `(1,4)=5` (active since 5)
  * down to `(2,4)=16` (now active at 16)
  * left via `(2,3)=15` → `(2,2)=14` → `(2,1)=13` → `(2,0)=12`
  * down `(3,0)=11` → `(4,0)=10` → `(4,1)=9` → `(4,2)=8` → `(4,3)=7` → `(4,4)=6`
* This is the **first** (T) at which start and end are in the same DSU set → **16**.

## Kruskal perspective (edges between neighbors)

* Build all neighbor edges with weight = max(elevations of its two endpoints).
* Sort edges ascending. Very small edges connect across the top and along the bottom snake, but they **don’t** connect the top and bottom regions until we include an edge with weight **16** (the step `(2,3)↔(2,4)` or `(1,4)↔(2,4)` of weight max(15,16)=16).
* The first moment start and end become connected is when an edge of weight **16** is added → answer = **16**.

---

# Pitfalls & pro tips

* **Activation:** only **union with already active neighbors**. Don’t pre-union everything.
* **Indexing:** map `(r,c)` to `id = r*n + c` consistently.
* **Early exit:** check connectivity of start/end **after each activation** (or after each Kruskal union). That gives the earliest threshold.
* **Two equivalent views:**

  * Activation: process nodes by height.
  * Kruskal: process edges by bottleneck weight.
* **Complexity parity:** both are (O(n^2 \log n)) due to sorting; DSU dominates connectivity maintenance.
*/

// class Solution {
//     public int swimInWater(int[][] grid) {
//         int n = grid.length;
//         int total = n * n;

//         // Pack cells as (height, r, c)
//         int[][] cells = new int[total][3];
//         int k = 0;
//         int maxH = 0;
//         for (int r = 0; r < n; r++) {
//             for (int c = 0; c < n; c++) {
//                 int h = grid[r][c];
//                 cells[k++] = new int[]{h, r, c};
//                 maxH = Math.max(maxH, h);
//             }
//         }

//         // Sort by elevation ascending
//         Arrays.sort(cells, Comparator.comparingInt(a -> a[0]));

//         DSU dsu = new DSU(total);
//         boolean[][] active = new boolean[n][n];
//         int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

//         // Helper to map (r,c) -> DSU id
//         final java.util.function.BiFunction<Integer,Integer,Integer> id = (r, c) -> r * n + c;

//         int start = 0;
//         int target = total - 1;

//         for (int[] cell : cells) {
//             int h = cell[0], r = cell[1], c = cell[2];
//             active[r][c] = true;

//             int u = id.apply(r, c);
//             // Union with already active neighbors
//             for (int[] d : dirs) {
//                 int nr = r + d[0], nc = c + d[1];
//                 if (nr < 0 || nr >= n || nc < 0 || nc >= n) continue;
//                 if (active[nr][nc]) {
//                     int v = id.apply(nr, nc);
//                     dsu.union(u, v);
//                 }
//             }

//             // Earliest moment start and end connect
//             if (dsu.find(start) == dsu.find(target)) {
//                 return h;
//             }
//         }

//         // Should never happen for valid inputs
//         return maxH;
//     }

//     static class DSU {
//         int[] parent, size;
//         DSU(int n) {
//             parent = new int[n];
//             size = new int[n];
//             for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
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
