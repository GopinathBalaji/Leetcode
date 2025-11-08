// Method 1: Dijkstra's algo approach
/*
# Why maintaining only the shortest distance to a node is not enough for this problem:
if (cost != minCost[u]) continue;
This is classic “lazy deletion” for normal Dijkstra: if we’ve found a better cost to u, ignore this entry.

But here the state is (u, stopsUsed). Two paths to the same city u:
One cheap but uses many stops.
One a bit more expensive but uses fewer stops.

With a K limit, a path that is slightly more expensive to an intermediate node but with fewer stops can lead to a cheaper final route that fits within K, while the “cheaper-to-u but too many stops” one can’t.
By only keeping minCost[u] (single value per city), you kill valid states.
You need the state to depend on both cost and stops.

#################


You’re really close with that attempt, but a couple of subtle Dijkstra+K-stops gotchas are biting you. Let’s:

1. Point out **exactly** what’s wrong in your code.
2. Give a **correct PQ/Dijkstra-style solution**.
3. Walk through a classic example step-by-step so it really clicks.

---

## What was wrong with my code:

Your idea:

* `minCost[v]` tracks cheapest cost to each city.
* PQ state = `(cost, u, stops)`.
* Only relax if `stops + 1 <= k` and `cost + w < minCost[v]`.

Main issues:

### 1. You’re treating it like classic Dijkstra (no K constraint)

You’re pruning based on:

```java
if (cost != minCost[u]) continue;
```

This is classic “lazy deletion” for normal Dijkstra: if we’ve found a better cost to `u`, ignore this entry.

But here the state is `(u, stopsUsed)`. Two paths to the same city `u`:

* One cheap but uses many stops.
* One a bit more expensive but uses fewer stops.

With a **K limit**, a path that is *slightly more expensive to an intermediate node but with fewer stops* can lead to a **cheaper final route** that fits within `K`, while the “cheaper-to-u but too many stops” one can’t.

By only keeping `minCost[u]` (single value per city), you kill valid states.

You need the state to depend on both **cost** and **stops**.

---

### 2. Wrong interpretation of `k` vs `stops`

The problem:

> At most **K stops** = at most **K+1 edges**.

You start `stops = 0` at `src`, then for a neighbor you check:

```java
if (stops + 1 <= k)
```

This allows at most `k` edges, i.e. at most `k-1` stops between, which is off by one.

Example: `k = 0` (no stops allowed) but direct flight is allowed (1 edge).

Your condition `stops + 1 <= k` blocks even 1 edge when `k=0` → broken.

---

### 3. No unreachable handling

If `dst` is unreachable under constraints, you return `minCost[dst]` which is still `INF`. You must return `-1` in that case.

---

## Correct Dijkstra-style approach (state = (node, steps))

We do **Dijkstra on an expanded state space**: `(city, stepsUsed)`.

Key ideas:

* Each state: `(costSoFar, node, stepsUsed)`.
* `stepsUsed` = number of edges used so far.
* Only move on if `stepsUsed <= K` (because next edge → `stepsUsed+1`).
* We allow multiple entries for the same city with different `stepsUsed`.
* We prune using a `best[city][steps]` table.


A slightly simpler variant: instead of tracking all `best[v][steps]`, many people:

* Let PQ handle `(cost, node, steps)` in ascending `cost`.
* Maintain `int[] minStops` or `int[] dist`, but always be careful not to prune valid states incorrectly.
* The above 2D `best` is the safest, clearest.

And yes: you can early-`return cost` the first time `u == dst` (because PQ is by cost and all edge weights are non-negative and we enforce the edge bound). I left the `ans` variable to be explicit.

---

## Step-by-step example walkthrough

Use the classic example:

```text
n = 4
flights = [
  [0, 1, 100],
  [1, 2, 100],
  [0, 2, 500]
]
src = 0, dst = 2, K = 1   // at most 1 stop => at most 2 edges
```

We expect answer = `200` via `0 -> 1 -> 2`.

### Setup

* maxEdges = K + 1 = 2
* best is size [4][3]; init to INF, except `best[0][0] = 0`.

PQ: `(0, 0, 0)`.

---

### Pop `(0,0,0)`

* `u=0`, `edges=0`, `cost=0`.
* Not dst.
* `edges < maxEdges`, so relax neighbors of 0:

Neighbors:

* `0 -> 1 (100)`

  * nextEdges = 1
  * newCost = 100
  * `best[1][1]` = 100 → push `(100,1,1)`
* `0 -> 2 (500)`

  * nextEdges = 1
  * newCost = 500
  * `best[2][1]` = 500 → push `(500,2,1)`

PQ now: `(100,1,1), (500,2,1)`.

---

### Pop `(100,1,1)`

* `u=1`, `edges=1`, `cost=100`.
* Not dst.
* `edges < maxEdges (2)`, so relax neighbors:

Neighbor:

* `1 -> 2 (100)`

  * nextEdges = 2
  * newCost = 200
  * `best[2][2]` = 200 → push `(200,2,2)`

PQ now: `(200,2,2), (500,2,1)`.

---

### Pop `(200,2,2)`

* `u=2 == dst`, `edges=2` (2 edges, 1 stop → valid).
* First time reaching dst from PQ → this `200` is the **minimum cost** respecting the edge/stops constraint.
* We return 200.

We **never incorrectly prune**:

* We allowed `2` to have entries with different edges used: one with cost 500 at edges=1, another with cost 200 at edges=2.
* Because PQ is sorted by cost, `(200,2,2)` pops before `(500,2,1)`, so we naturally get the optimal.

If we had used your original `minCost[v]` single array:

* After processing `(100,1,1)` we’d set `minCost[2]` to 200.
* When `(500,2,1)` comes, we’d discard it (good).
* But in other graphs, a more complex situation with different stop counts can flip which path is allowed by K, and the 1D `minCost` breaks.

---

## Summary of fixes

* **Bug 1:** You only tracked `minCost[u]` per city; must incorporate `stops` into state or use constrained DP.
* **Bug 2:** You enforced `stops + 1 <= k` instead of allowing up to `K+1` edges.
* **Bug 3:** You didn’t return `-1` when unreachable.

The provided solution uses a clean “Dijkstra over `(node, edgesUsed)`” pattern that’s correct and reusable. If you’d like, next I can also show you the Bellman-Ford/DP version side-by-side so you can see how similar the logic is.
*/

class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
        // Build adjacency list: u -> (v, w)
        List<int[]>[] g = new ArrayList[n];
        for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
        for (int[] e : flights) {
            g[e[0]].add(new int[]{e[1], e[2]});
        }

        int maxEdges = K + 1; // at most K stops -> at most K+1 edges

        // best[v][e] = minimum cost to reach v using exactly e edges
        // We'll use e in [0..maxEdges].
        int INF = Integer.MAX_VALUE / 4;
        int[][] best = new int[n][maxEdges + 1];
        for (int i = 0; i < n; i++) Arrays.fill(best[i], INF);
        best[src][0] = 0;

        // (cost, node, edgesUsed)
        PriorityQueue<int[]> pq =
            new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, src, 0});

        int ans = INF;

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int cost = cur[0];
            int u = cur[1];
            int edges = cur[2];

            // If we've already found a better cost for this exact (u, edges) state, skip
            if (cost > best[u][edges]) continue;

            // If we reached dst within allowed edges, we can update answer
            if (u == dst) {
                ans = Math.min(ans, cost);
                // We don't break immediately: there *might* be another path
                // with same or fewer edges but even cheaper, but because PQ is
                // cost-ordered, the first time we hit dst is already minimal.
                // So: we COULD safely return here. Using ans is just explicit.
                return ans;
            }

            // If we've already used maxEdges edges, can't go further
            if (edges == maxEdges) continue;

            // Relax neighbors
            for (int[] edge : g[u]) {
                int v = edge[0], w = edge[1];
                int nextEdges = edges + 1;
                int newCost = cost + w;

                // If using nextEdges to reach v is cheaper than before, push
                if (newCost < best[v][nextEdges]) {
                    best[v][nextEdges] = newCost;
                    pq.offer(new int[]{newCost, v, nextEdges});
                }
            }
        }

        return ans == INF ? -1 : ans;
    }
}





// Method 1: DP/Bellman-Ford algo approach
/*
Bellman-Ford is exactly the right hammer for this nail, so let’s go all in.

I’ll do this in two parts:

1. Explain **Bellman-Ford** itself: what, why, how, where it beats Dijkstra.
2. Show how to adapt it to **LeetCode 787. Cheapest Flights Within K Stops** with full code + step-by-step walkthrough.

---

## 1. Bellman-Ford: what it is and how it works

### Problem it solves

Single-source shortest paths on a directed graph that may have:

* **Positive weights**
* **Zero weights**
* **Negative weights** (as long as there’s no negative cycle reachable from the source)

Where Dijkstra fails with negative edges, Bellman-Ford is safe.

---

### Core idea

Think in terms of **paths with limited edges**:

> Let `dist[i][v]` = shortest distance from `src` to `v` using **at most `i` edges**.

Key facts:

* Any simple shortest path has at most `V-1` edges (otherwise it repeats a node → cycle).
* So if you compute best distances using up to 1 edge, then 2 edges, … up to `V-1` edges, you will have the true shortest paths.

Dynamic programming recurrence:

For each `i` (number of edges allowed):

* Start from previous layer (`dist[i-1][*]`).
* Try to relax each directed edge `u → v (w)`:
  [
  dist[i][v] = \min(dist[i][v], dist[i-1][u] + w)
  ]
  Important: we use **only dist from `i-1`** to compute `i` (no in-place updates), to ensure we only use up to `i` edges.

Classic Bellman-Ford just implements this idea in a slightly compressed form.

---

### Standard implementation

Let:

* `dist[v]` track best-known distance.
* Initialize: `dist[src] = 0`, others `= +∞`.

Algorithm:

1. Repeat **V-1 times**:

   * For every edge `(u, v, w)`:

     * If `dist[u] + w < dist[v]`, update `dist[v]`.
2. (Optional) To detect **negative cycles**:

   * Do one more full relaxation pass:
   * If any `dist[v]` improves, there is a negative cycle reachable from `src`.

Why `V-1` times?

* Any shortest path can use at most `V-1` edges. Each pass allows one more edge in the path. After `V-1` passes, all shortest paths are accounted for.

Complexity:

* Time: `O(V * E)`
* Space: `O(V)`

Strength:

* Handles negative weights (no negative cycles).
* Simple, robust.

---

## 2. Using Bellman-Ford style DP for 787. Cheapest Flights Within K Stops

Now let’s plug this idea into:

**Problem 787 recap:**

* `n` cities: `0..n-1`
* `flights[i] = [u, v, w]` directed, cost `w >= 0`
* Want cheapest price from `src` to `dst` with **at most K stops**
* “At most K stops” = **at most K+1 edges**.

This is literally:

> Shortest path from `src` to `dst` using at most `K+1` edges.

That’s Bellman-Ford’s DP with a cap on iterations.

Instead of `V-1` iterations, we only allow up to `K+1` relaxations.

---

### DP/Bellman-Ford pattern for this problem

Define:

* `dp[i][v]` = minimum cost to reach `v` using **at most `i` edges**.

But we don’t need full 2D array; we can roll it:

* `prev[v]` = dp for ≤ `i-1` edges
* `curr[v]` = dp for ≤ `i` edges

Initialization:

* `prev[src] = 0`
* `prev[other] = INF`

Transition for each edge `(u, v, w)`:

* If `prev[u] != INF`, then:
  [
  curr[v] = \min(curr[v], prev[u] + w)
  ]

Process:

* Repeat this relaxation exactly `K+1` times (because that’s max edges).
* On each iteration:

  * Start `curr` as a copy of `prev` (because you can always choose to not add another edge).
  * Relax all edges from `prev` into `curr`.
  * Then `prev = curr`.

Answer:

* After `K+1` iterations, `prev[dst]` is the cost of the cheapest route using at most `K+1` edges (i.e. at most `K` stops).
* If it’s still `INF`, return `-1`.

**CRITICAL:**
Do **NOT** relax into `prev` in-place. That would allow paths with more than `i` edges to influence dp for round `i`.


This is literally Bellman-Ford, but:

* Instead of `V-1` iterations, we do `K+1`.
* We don’t care about negative cycles (weights are non-negative).
* We’re only interested in bounded-length paths.

---

## Walkthrough: classic example

Example:

```text
n = 3
flights = [[0,1,100],
           [1,2,100],
           [0,2,500]]
src = 0, dst = 2, K = 1
```

At most `K=1` stop → at most `K+1 = 2` edges.

We expect: `0 -> 1 -> 2` = 200.

### Initialization

`prev` after setup:

* prev[0] = 0
* prev[1] = INF
* prev[2] = INF

`maxEdges = 2`

---

### Iteration 1 (paths using ≤1 edge)

Start:

* `curr = copy(prev) = [0, INF, INF]`

Relax each flight:

1. Edge `0 -> 1 (100)`:

   * prev[0] = 0 → `curr[1] = min(INF, 0 + 100) = 100`
2. Edge `1 -> 2 (100)`:

   * prev[1] = INF → ignore
3. Edge `0 -> 2 (500)`:

   * prev[0] = 0 → `curr[2] = min(INF, 0 + 500) = 500`

Now:

* `curr = [0, 100, 500]`
  Set `prev = curr`.

Interpretation:

* With at most 1 edge:

  * To 0: cost 0
  * To 1: cost 100 (0→1)
  * To 2: cost 500 (0→2 direct)

---

### Iteration 2 (paths using ≤2 edges)

Start:

* `curr = copy(prev) = [0, 100, 500]`

Relax edges again, using **prev**:

1. Edge `0 -> 1 (100)`:

   * prev[0] = 0 → `curr[1] = min(100, 0 + 100) = 100` (no change)
2. Edge `1 -> 2 (100)`:

   * prev[1] = 100 → `curr[2] = min(500, 100 + 100 = 200) = 200` ✅
3. Edge `0 -> 2 (500)`:

   * prev[0] = 0 → `curr[2] = min(200, 0 + 500 = 500) = 200` (no change)

Now:

* `curr = [0, 100, 200]`
  Set `prev = curr`.

We did `maxEdges = 2` iterations, so we stop.

Final:

* `prev[2] = 200` → return 200.

We’ve correctly found the cheapest price from 0 to 2 using at most 2 edges (1 stop).

---

## Another quick edge-case check: K = 0 (no stops)

Same inputs, but `K = 0` → `maxEdges = 1`.

* Iteration 1:

  * We only allow paths with ≤1 edge.
  * We find:

    * 0→1 = 100
    * 0→2 = 500
* Stop.

Answer:

* `prev[2] = 500` (only direct flight allowed).
* This matches “no stops” requirement (you can’t use 0→1→2).

---

## Common pitfalls (watch these)

1. **Off-by-one on K**

   * “At most K stops” = at most **K+1 edges**.
   * So run the relaxation loop exactly `K+1` times.

2. **In-place relaxation**

   * If you update into `prev` directly, a path of length `i` can incorrectly be used again in the same iteration, effectively counting `i+1` or more edges.
   * Always relax from `prev` into `curr`, then assign `prev = curr`.

3. **Initialization**

   * Only `src` is 0 at start, others INF.
   * Don’t forget `src`.

4. **Unreachable**

   * If `prev[dst]` is still INF at the end, return `-1`.

---

* Conceptually: “shortest path with limited edges” → textbook Bellman-Ford DP.
*/

// class Solution {
//     public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
//         final int INF = Integer.MAX_VALUE / 4;

//         // prev[v] = best cost to reach v with <= current number of edges
//         int[] prev = new int[n];
//         Arrays.fill(prev, INF);
//         prev[src] = 0;

//         int maxEdges = K + 1; // at most K stops -> at most K+1 edges

//         // We run relaxation up to maxEdges times
//         for (int i = 1; i <= maxEdges; i++) {
//             // Start with a copy of prev (so "do nothing" is always allowed)
//             int[] curr = Arrays.copyOf(prev, n);

//             // Relax all edges once, based on prev
//             for (int[] e : flights) {
//                 int u = e[0];
//                 int v = e[1];
//                 int w = e[2];

//                 if (prev[u] != INF) {
//                     int newCost = prev[u] + w;
//                     if (newCost < curr[v]) {
//                         curr[v] = newCost;
//                     }
//                 }
//             }

//             prev = curr;
//         }

//         return prev[dst] == INF ? -1 : prev[dst];
//     }
// }
