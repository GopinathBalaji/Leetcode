// Answer to the LeetCode question Walls And Gates / Neetcode question Islands and Treasure

// Graph BFS once
/*
# Where I was going wrong:
Great question. Your code is close in spirit (you're using BFS), but there are several fundamental problems that cause it to give wrong answers and/or blow up in complexity. I'll walk you through:

1. what's wrong,
2. how this problem is meant to be solved efficiently, and
3. the corrected code with a full walkthrough.

---

## First, a quick restatement of the problem (just to align terms)

In “Islands and Treasure” (also known as Walls and Gates / LeetCode 286 variant with different names):

* `-1` = water / wall / cannot pass
* `0`  = treasure
* `2147483647` (INF) = land we need to fill

Goal: For every land cell (INF), fill it with the minimum number of steps to reach the **nearest treasure (0)** by moving up/down/left/right.
Water stays `-1`. Treasures stay `0`.

So we're computing the shortest distance to the nearest treasure for every reachable land cell.

---

## What’s wrong with your approach

Here’s your approach in words:

> “For every cell that isn’t -1 or INF, run BFS from that cell to the nearest treasure and write that distance.”

Let's look at the specific issues.

### 1. You're doing a BFS from **each** land cell

```java
for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
        if (grid[i][j] != -1 && grid[i][j] != 2147483647) {
            int dist = bfs(grid, i, j);
            grid[i][j] = dist;
        }
    }
}
```

Problem:

* This makes you run BFS potentially `rows * cols` times.
* Each BFS can touch the whole grid.
* Worst case: O((mn) * (mn)) = O(m²n²), which is too slow for large grids.

Even more importantly:

* You are also overwriting `grid[i][j]` during this loop, which can affect later BFS calls. So later BFS calls are no longer seeing the original grid — they see a mutated mix of distances and INF. That means correctness breaks, not just performance.

👉 The intended solution is **one BFS total**, not one BFS per cell.

We’ll come back to that.

---

### 2. Your BFS distance tracking is incorrect

Inside `bfs`:

```java
int dist = 0;
queue.offer(new int[]{i, j});

while (!queue.isEmpty()) {
    int[] coord = queue.poll();
    ...
    if (grid[cr][cc] == 0) {
        return dist;
    }
    ...
    dist++;
    ...
}
```

Issues:

* You’re using a single `dist` counter and incrementing it once per popped cell. That does **not** represent graph distance in BFS.
* BFS shortest path distance depends on levels (layers). You should only increase the "steps from source" after you've finished exploring a whole layer, not after every node.

In other words:

* `dist` here is “how many cells I’ve popped so far,” not “how far I am from the start.”

That returns wrong distances.

---

### 3. You never record the distance per cell in the queue

In a correct BFS-from-source approach you’d either:

* push `(row, col, distanceSoFar)` into the queue, or
* process in layers using `for (size = queue.size(); size-- > 0)`.

Your `bfs` does neither, so it can't know how far the current cell is from the starting cell.

---

### 4. You enqueue neighbors incorrectly and the directions array is wrong

You wrote:

```java
int[][] dirs = {{-1, 0}, {0, 1}, {1, 0}, {-1, 0}};
```

Two problems here:

* You duplicated `{-1, 0}` (up) and forgot `{0, -1}` (left).
* So you're never exploring left neighbors. That alone can miss valid shortest paths.

Should be:

```java
int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
```

---

### 5. You don't mark the current cell `visited` early enough

You only mark neighbors visited:

```java
if(grid[nr][nc] == 2147483647 || grid[nr][nc] == 0){
    visited[nr][nc] = true;
    queue.offer(new int[]{nr, nc});
}
```

But you don't mark `(cr, cc)` before exploring, so you can requeue the same nodes over and over in some patterns.

Better: mark a cell visited the moment you add it to the queue.

---

### 6. Conceptual mismatch: you're searching “from land → nearest treasure”

This works in theory but is inefficient. The standard trick is the opposite:

Instead of:

> "For each land cell, BFS to find its nearest treasure."

Do:

> "From all treasures at once, BFS outward and fill distances into land."

This is called a **multi-source BFS**, and it's the key insight.

Why it works:

* The distance from a land cell to the *nearest* treasure is the same as
  the distance from the *nearest treasure* to that land cell.
* So we can throw all treasures (all `0`s) into the queue initially with distance 0.
  Then expand outward in rings (distance 1, distance 2, ...).
  The first time we reach any land cell, we've found its minimum distance.

This visits each cell at most once → O(mn) total.

---

## Correct approach (multi-source BFS)

Algorithm:

1. Create a queue.
2. Add **all cells that are treasure (0)** to the queue first.
3. Do a BFS from all of them in parallel.
4. Whenever you reach a land cell (`2147483647`), update it with `currentDistance + 1` and push it.
5. Skip water (`-1`).

That’s it. Only one BFS run.

### Why this code works

* All treasures start in the queue with distance = 0.
* When you pop a treasure at distance 0, its neighbors that are still INF become distance 1.
* When you pop a distance-1 cell, its INF neighbors become distance 2.
* This is exactly shortest-path layering in BFS on an unweighted grid.
* Each cell is assigned once, the first time it's reached. That assignment is guaranteed to be the shortest distance, because BFS explores in increasing distance order.

No need for a separate `visited[][]`, because once we assign a distance to an `INF` cell, it's no longer `INF`, so we won't assign it again.

---

## Mini walkthrough example

Say we start with this grid (`INF` = 2147483647 to save space I'll write `INF` here just conceptually):

Row 0: [INF,  -1,   0, INF]
Row 1: [INF, INF, INF,  -1]
Row 2: [INF,  -1, INF,  -1]
Row 3: [  0,  -1, INF, INF]

Interpretation:

* `0` = treasure
* `-1` = water
* `INF` = land needing distance

### Step 1. Push all treasures

Queue initially:

* (0,2) with value 0
* (3,0) with value 0

grid unchanged so far.

### Step 2. BFS loop

Pop (0,2) [distance 0]

* Check neighbors:

  * (1,2): was INF → set to 1, push (1,2)
  * (-1,2): OOB → skip
  * (0,3): was INF → set to 1, push (0,3)
  * (0,1): was -1 → skip

Pop (3,0) [distance 0]

* Check neighbors:

  * (2,0): INF → set to 1, push (2,0)
  * (4,0): OOB
  * (3,1): -1 → skip
  * (3,-1): OOB

Now grid has some 1s filled in.

Next layer of BFS:

Pop (1,2) [distance 1]

* Neighbors:

  * (2,2): INF → set to 2, push (2,2)
  * (0,2): already 0, skip
  * (1,3): -1, skip
  * (1,1): INF → set to 2, push (1,1)

Pop (0,3) [distance 1]

* Neighbors:

  * (1,3): -1
  * (-1,3): OOB
  * (0,4): OOB
  * (0,2): 0
    (no new cells)

Pop (2,0) [distance 1]

* Neighbors:

  * (3,0): 0
  * (1,0): INF → set to 2, push (1,0)
  * (2,1): -1
  * (2,-1): OOB

Pop (2,2) [distance 2]

* Neighbors:

  * (3,2): INF → set to 3, push (3,2)
  * (1,2): 1
  * (2,3): -1
  * (2,1): -1

Pop (1,1) [distance 2]

* Neighbors:

  * (2,1): -1
  * (0,1): -1
  * (1,2): 1
  * (1,0): already set = 2

Pop (1,0) [distance 2]

* Neighbors:

  * (2,0): 1
  * (0,0): INF → set to 3, push (0,0)
  * (1,1): 2
  * (1,-1): OOB

Pop (3,2) [distance 3]

* Neighbors:

  * (3,3): INF → set to 4, push (3,3)
  * (2,2): 2
  * (4,2): OOB
  * (3,1): -1

Pop (0,0) [distance 3]

* Neighbors:

  * (1,0): 2
  * (-1,0): OOB
  * (0,1): -1
  * (0,-1): OOB
    (no new)

Pop (3,3) [distance 4]

* Neighbors:

  * (2,3): -1
  * (4,3): OOB
  * (3,4): OOB
  * (3,2): 3
    (no new)

End BFS.

Result: every reachable `INF` cell now holds its *minimum distance* to a treasure.

We’re done.

---

## Final takeaways

### Problems in your code

* Doing a BFS from each starting cell → way too slow, and also mutates the grid mid-iteration.
* Distance tracking (`dist++`) is not by BFS levels, so it’s wrong.
* Directions array is wrong (missing left).
* You didn’t attach a distance to each queued cell or do level-based processing.
* Conceptually you're going “from each land to nearest treasure,” instead of the efficient “from all treasures to all land.”

### Fix

Use **one multi-source BFS**:

* Put all treasures (0s) in the queue first with distance 0.
* Expand outward, filling neighbors with `currDist + 1` the first time you see them.
* Each cell is visited at most once; runtime O(mn); no need to recompute anything.
*/

class Solution {
    private static final int INF = 2147483647;

    public void islandsAndTreasure(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        Queue<int[]> q = new ArrayDeque<>();

        // 1. Initialize the queue with ALL treasure cells (value == 0).
        // These are our BFS sources.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 0) {
                    q.offer(new int[]{r, c});
                }
            }
        }

        // 4-directional movement
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        // 2. BFS outward from all treasures at once.
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            int cr = cell[0];
            int cc = cell[1];

            // Look at all four neighbors
            for (int[] d : dirs) {
                int nr = cr + d[0];
                int nc = cc + d[1];

                // bounds check
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                    continue;
                }

                // we only care about land cells that haven't been assigned yet:
                // INF means "unreached land"
                if (grid[nr][nc] == INF) {
                    // The neighbor's distance is current cell's distance + 1
                    grid[nr][nc] = grid[cr][cc] + 1;
                    q.offer(new int[]{nr, nc});
                }

                // We do NOT enqueue water (-1).
                // We do NOT enqueue cells we've already set to a finite distance.
            }
        }
    }
}
