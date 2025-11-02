// Method 1: Using Dijkstra's Algorithm (Single-source shortest paths)
/*
# What Dijkstra’s solves

> **Single-source shortest paths on a graph with non-negative edge weights.**

* **Input:** directed/undirected graph, non-negative weights, a start node `s`.
* **Output:** the minimum distance `dist[v]` from `s` to every node `v` (or `∞` if unreachable).

If edge weights are all `1` → use **BFS** (faster/simpler).
If any weights are negative → **Dijkstra is invalid**; use **Bellman-Ford** (or SPFA).
If weights are only `0` or `1` → use **0-1 BFS** (deque).

---

# Intuition (why the “min-heap” works)

Think of dropping ink at the source: it spreads along edges, but **moving along heavier edges takes longer**. Dijkstra uses a **min-heap** (priority queue) to always expand the **closest not-yet-finalized** node next. When we pop a node `u` from the heap and its distance matches our current best `dist[u]`, that distance is final (cannot be improved later), because there’s no way to reach `u` through some other path that uses only non-negative extra cost and still beat it.

---

# Core algorithm (adjacency list + min-heap)

1. Build adjacency list `g[u] = list of (v, w)`.
2. Initialize distances: `dist[*] = INF`, `dist[s] = 0`.
3. Push `(0, s)` into a min-heap keyed by distance.
4. While heap not empty:

   * Pop `(d, u)`. If `d != dist[u]`, skip (stale heap entry).
   * For every edge `(u → v, w)`:

     * If `dist[v] > dist[u] + w`, relax:

       * `dist[v] = dist[u] + w`
       * Push `(dist[v], v)` into heap.
5. `dist` now has all shortest distances (or `INF` if unreachable).

**Time:** `O((E + V) log V)` with a binary heap.
**Space:** `O(E + V)`.

---

# Apply to LeetCode 743. Network Delay Time

**Problem recap:**
You’re given `times[i] = [u, v, w]` for directed edge `u → v` taking time `w` (`w ≥ 1`). There are `n` nodes labeled `1..n`, and a start `k`. Return the time for a signal from `k` to reach **all** nodes; if some node is unreachable, return `-1`.
This is exactly **single-source shortest paths** with non-negative weights → **Dijkstra**.


### Why this returns the correct answer

* Dijkstra finds the shortest arrival time to each node from `k`.
* The time for **all** nodes to receive the signal is the **maximum** shortest distance.
* If any node is unreachable (`INF`), we return `-1`.

### Step-by-step on the sample

`times = [[2,1,1],[2,3,1],[3,4,1]]`, `n = 4`, `k = 2`

Graph:

* `2 → 1 (1)`, `2 → 3 (1)`, `3 → 4 (1)`

Initialize: `dist[2]=0`, others `INF`. Heap = `(0,2)`.

1. Pop `(0,2)`

   * Relax `2→1`: `dist[1]=1` push `(1,1)`
   * Relax `2→3`: `dist[3]=1` push `(1,3)`
     Heap: `(1,1),(1,3)`

2. Pop `(1,1)`

   * No outgoing edges → nothing changes.

3. Pop `(1,3)`

   * Relax `3→4`: `dist[4]=2` push `(2,4)`
     Heap: `(2,4)`

4. Pop `(2,4)`

   * No outgoing edges.

Max dist among nodes 1..4 = `max(1,0,1,2) = 2` → **answer = 2**.

---

# Reusable Dijkstra template (generic)

When you’re solving other weighted shortest path problems, keep this pattern handy:

```java
static long[] dijkstra(int n, List<int[]>[] g, int src) {
    long INF = Long.MAX_VALUE / 4;
    long[] dist = new long[n];
    Arrays.fill(dist, INF);
    dist[src] = 0;

    PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
    pq.offer(new long[]{0, src});

    while (!pq.isEmpty()) {
        long[] cur = pq.poll();
        long d = cur[0]; int u = (int) cur[1];
        if (d != dist[u]) continue;
        for (int[] e : g[u]) {
            int v = e[0], w = e[1];
            long nd = d + w;
            if (nd < dist[v]) {
                dist[v] = nd;
                pq.offer(new long[]{nd, v});
            }
        }
    }
    return dist;
}
```

* Build `g` as `new ArrayList[n]`, each edge `u→(v,w)`.
* For 1-based labels, either shift to 0-based or size to `n+1`.

---

# When to use Dijkstra (and close cousins)

**Classic Dijkstra (sum of weights):**

* 743. **Network Delay Time** ✅
* 787. **Cheapest Flights Within K Stops** ❗️(Dijkstra doesn’t handle the **stop cap** directly; use BFS/Bellman-Ford with `K+1` relaxations, or a state `(node, stops)` in a PQ.)
* 1631. **Path With Minimum Effort** (use **minimax** variant: path cost = max edge on path; push state with `max(currEffort, edgeCost)`).
* 1514. **Path with Maximum Probability** (either transform with `-log` to sum, or run a **max-heap** where you relax by *maximizing* probability).
* 1976. **Number of Ways to Arrive at Destination** (run Dijkstra for distances and simultaneously count shortest-path ways modulo 1e9+7).

**0-1 BFS (weights 0 or 1 only):**

* 1368. **Minimum Cost to Make at Least One Valid Path in a Grid** (deque; push front on cost 0, push back on cost 1).

**BFS (unweighted):**

* 1091. **Shortest Path in Binary Matrix**, classic shortest steps on grids with unit cost.

**Bellman-Ford (negative edges) / Floyd-Warshall (all-pairs):**

* Use when edges can be negative (no negative cycles for shortest paths).
* Floyd-Warshall is `O(n^3)` for dense, small graphs, or when you need all-pairs.

---

# Common pitfalls (and how to avoid them)

* **Negative weights:** Dijkstra is invalid; switch algorithms.
* **Wrong graph build:** 743 is **directed**; don’t add reverse edges.
* **Stale heap entries:** always check `if (d != dist[u]) continue;`
* **Visited arrays:** you can use a `visited[]` to finalize nodes once popped, but the “stale-check” pattern is simpler and equivalent.
* **Overflow:** sums of weights can exceed `int` in some problems—store `dist` in `long`.
* **Disconnected graphs:** after Dijkstra, check for `INF`; if any → unreachable case (e.g., return `-1` in 743).
* **1-based labels:** either convert to 0-based or size arrays to `n+1`.

---

# Quick variations you’ll actually use

1. **Minimax Dijkstra (Path With Minimum Effort, 1631)**
   Replace “sum of weights” with “effort = max edge on path so far” and order the PQ by current effort.

2. **Max-probability path (1514)**
   Either:

   * Max-heap on probability, relax by `prob[v] < prob[u] * p`, or
   * Transform to weights `-log(p)` and run standard Dijkstra (sum of logs).

3. **Count shortest paths (1976)**
   Keep an array `ways[v]`.

   * On a better distance: `ways[v] = ways[u]`.
   * On equal distance: `ways[v] = (ways[v] + ways[u]) % MOD`.

4. **K-stops constrained (787)**
   Dijkstra by distance alone doesn’t enforce stop limits. Use **layered DP/Bellman-Ford** with at most `K+1` relaxations, or push states `(node, stopsUsed)` in PQ and prune when `stopsUsed > K`.

---
Dijkstra template can be used for any of those variants (minimax, counting paths, max probability, 0-1 BFS).


######################
# Why do we need lazy deletion

“**Lazy deletion**” (also called **skipping stale entries**) is a super common trick in Dijkstra’s when you use a normal priority queue (like Java’s `PriorityQueue`) that **doesn’t** support decrease-key.

## What’s a “stale entry”?

During Dijkstra, you might discover a **better (shorter)** path to a node **after** you already pushed a worse distance for that same node into the heap. That old heap item is now **stale**—it no longer matches the best known `dist[u]`.

Example:

* You push `(10, A)` (distance 10 to A).
* Later you find a better path and push `(5, A)`.
* The heap now contains **two entries** for A: `(5, A)` (good) and `(10, A)` (stale).

Because Java’s PQ can’t efficiently “decrease key” or delete the old `(10, A)` in place, we just leave it there. That’s the “lazy” part: don’t remove it now; **skip it later** when it pops.

## How do we skip it?

When you pop `(d, u)` from the heap, compare `d` to your best known `dist[u]`. If they don’t match, this entry is stale:

```java
long[] cur = pq.poll();
long d = cur[0];
int  u = (int) cur[1];

if (d != dist[u]) continue;  // stale: ignore and pop next
```

(You’ll also see `if (d > dist[u]) continue;`—same idea, a bit more permissive.)

Only if the popped distance equals the current best do you relax outgoing edges from `u`.

## Why do we need it?

* **No decrease-key in Java PQ:** You can’t update a key already inside the heap. Pushing a new pair is easy; removing the old one is hard. Lazy deletion avoids tricky heap surgery.
* **Correctness:** The first time you pop a node with `d == dist[u]`, that distance is final in Dijkstra (non-negative weights). Any later (bigger) `d` for the same node is guaranteed worse → safe to skip.
* **Simplicity:** The check is one line and avoids bookkeeping.

## Does this hurt complexity?

Not really. You may push multiple entries per node—worst-case **O(E)** pushes—and each push/pop costs `O(log V)`. That’s the standard `O((E + V) log V)` bound for heap-based Dijkstra. Memory is also `O(E)` for the heap in the worst case, which is fine for typical constraints.

## Relationship to `visited[]`

An alternative pattern is:

```java
boolean[] vis = new boolean[n+1];

while (!pq.isEmpty()) {
  var cur = pq.poll();
  int u = cur.node;
  long d = cur.dist;

  if (vis[u]) continue;    // already finalized, skip
  vis[u] = true;           // finalize u on first pop

  // relax edges from u ...
}
```

* With **visited**: you finalize a node the **first** time you pop it, and skip later pops for that node.
* With **lazy deletion**: you don’t need `visited`; the `d != dist[u]` check ensures you only act on the current best.
* You can use **either**. Don’t need both. I prefer the `d != dist[u]` check because it avoids an extra array and plays nicely with re-pushing improved distances.

## Tiny step-by-step example

Graph (directed, weights ≥ 0). Start at `S`.

Edges:

* `S → A (10)`
* `S → B (3)`
* `B → A (2)`

Initialization:

* `dist[S]=0`, `dist[A]=∞`, `dist[B]=∞`
* Heap: `(0,S)`

Process:

1. Pop `(0,S)` (matches `dist[S]`):

   * Relax `S→A`: `dist[A]=10`, push `(10,A)`
   * Relax `S→B`: `dist[B]=3`,  push `(3,B)`
     Heap: `(3,B), (10,A)`

2. Pop `(3,B)` (matches `dist[B]`):

   * Relax `B→A`: `dist[A]=min(10, 3+2=5)=5`, push `(5,A)`
     Heap: `(5,A), (10,A)`

3. Pop `(5,A)` (matches `dist[A]`): relax A’s edges if any…
   Heap: `(10,A)`

4. Pop `(10,A)`:

   * Check: `10 != dist[A](=5)` → **stale** → `continue` (skip)

We never had to delete `(10,A)` earlier; lazy deletion handled it when it surfaced.

## TL;DR

* **Stale entry** = an outdated `(distance, node)` left in the heap after you found a better distance.
* **Lazy deletion** = allow stale entries to remain in the heap; when popped, **skip** them via:

  ```java
  if (d != dist[u]) continue;
  ```
* It’s necessary because common PQs (like Java’s) don’t support decrease-key; it keeps code simple, correct, and efficient.
*/

class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        List<int[]>[] g = new ArrayList[n + 1];
        for (int i = 0; i <= n; i++) g[i] = new ArrayList<>();
        for (int[] e : times) {
            int u = e[0], v = e[1], w = e[2];
            g[u].add(new int[]{v, w});
        }

        // dist as long to avoid overflow in other settings; cast at the end
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n + 1];
        Arrays.fill(dist, INF);
        dist[k] = 0;

        // (distance, node) min-heap
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        pq.offer(new long[]{0, k});

        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            long d = cur[0];
            int u = (int) cur[1];
            if (d != dist[u]) continue; // lazy deletion

            for (int[] edge : g[u]) {
                int v = edge[0], w = edge[1];
                long nd = d + w;
                if (nd < dist[v]) {
                    dist[v] = nd;
                    pq.offer(new long[]{nd, v});
                }
            }
        }

        long ans = 0;
        for (int node = 1; node <= n; node++) {
            if (dist[node] == INF) return -1; // unreachable
            ans = Math.max(ans, dist[node]);
        }
        return (int) ans; // fits in int under LC constraints
    }
}
