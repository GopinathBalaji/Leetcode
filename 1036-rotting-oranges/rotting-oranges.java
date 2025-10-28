// Graph BFS without maintaining visited
/*
# What I was doing wrong:

## 1. You are not doing BFS by “minutes,” you're doing BFS by “oranges”

Your loop:

```java
while (!q.isEmpty()) {
    int[] coord = q.poll();
    ...
    if (grid[cr][cc] == 1) {
        time++;
    }
    ...
}
```

What this does:

* Every time you pop an orange that *used to be fresh*, you increment `time`.

What it *should* do:

* Each "minute" in the problem means: ALL currently rotten oranges infect all adjacent fresh oranges at the same time.
* So the time should increase **once per layer**, not once per orange.

In BFS terms:

* You need to process in waves. That means:

  * Get `size = q.size()`
  * Process exactly `size` items (the current layer),
  * Infect neighbors and push them for the *next* layer.
  * After finishing that whole layer, increment `minutes`.

Your current code increments `time` per infected orange instead of per wave. That gives the wrong number of minutes.

---

## 2. Your boundary check is broken

You wrote:

```java
if (nr < 0 || nc >= rows || nc < 0 || nc >= cols) {
    continue;
}
```

Bugs:

* You check `nc >= rows` instead of `nr >= rows`.
* You're checking `nc` twice and never checking `nr >= rows` correctly.

It should be:

```java
if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
    continue;
}
```

Right now you can walk off the grid vertically without being caught, which will crash for certain inputs.

---

## 3. You're never updating the grid to mark new oranges as rotten

When you infect a neighbor:

```java
if (!visited[nr][nc] && grid[nr][nc] == 1) {
    visited[nr][nc] = true;
    infectedCount++;
    q.offer(new int[]{nr, nc});
}
```

Issues:

* You don’t mark `grid[nr][nc]` as 2 (rotten) when you infect it.
* That means if you later look at the board, it still “looks fresh,” and you might try to infect it again from somewhere else if you hadn't marked it visited.
* You're relying only on `visited`, which is okay-ish, but…

More importantly:

* You don't attach any "time of infection" to the orange. So when you eventually `poll()` it, you're not able to tell how long it's been rotting.

The usual fix is:

* When an orange gets infected, set it to 2 immediately (so it's now rotten and will infect others next minute).
* And either:

  * track the minute in the queue, or
  * do layered BFS and increment time once per layer.

---

## 4. You don't track minutes the standard BFS way

The standard BFS pattern for this problem:

* Start with all rotten oranges in the queue.
* Keep `minutes = 0`.
* While queue not empty:

  * Process one layer (`size = queue.size()`).
  * Infect neighbors, and enqueue them.
  * After processing that layer:

    * if you infected at least one new orange during this layer, `minutes++`.

That models the "simultaneous spread per minute".
Your code instead tries to infer timing by looking at what you popped, but that does not match the process model in the problem.

---

## 5. Wrong initial `time` handling and edge case with no fresh oranges

You start with:

```java
int time = -1;
```

Then at the end:

```java
return (infectedCount == freshCount) ? time : -1;
```

Edge case:

* If there are **no fresh oranges** at all, the correct answer is `0` minutes.
* Your code:

  * `freshCount = 0`
  * `infectedCount = 0`
  * BFS runs but never increments `time`, so `time` stays `-1`
  * `infectedCount == freshCount` → true
  * You return `-1` ❌

So starting `time = -1` is incorrect. Minutes should start at `0`, and only increase via BFS waves.

Related: your idea of comparing `infectedCount` with `freshCount` at the end is on the right track (it's how we detect unreachable fresh oranges), but because time is tracked incorrectly, even the “good” case returns the wrong number of minutes.

---

## 6. You never mark initial rotten oranges as visited

You enqueue all currently rotten oranges:

```java
if (grid[i][j] == 2){
    q.offer(new int[]{i, j});
}
```

but you don't mark them in `visited`. That means multiple rotten neighbors in the queue can try to re-process each other or re-infect.

Not fatal if you’re careful in other ways, but it's sloppy. We can avoid `visited` entirely by mutating the grid (`1 → 2`) as soon as a fresh orange is infected.

---

## 7. Minor but important: your BFS doesn't carry time per cell

Right now each queue element is just `{row, col}`.
But BFS for rotting oranges needs to know when that orange rotted, to compute the total minutes.
Two common solutions:

* Either:

  * store `{row, col, minute}` in the queue, and update `time = max(time, minute)`
* Or:

  * process level by level, incrementing minute after each level

Your code does neither.

---

## Pulling it together: what a correct BFS needs to do

Here’s the shape you actually want:

1. Count all fresh oranges.
2. Push all initially rotten oranges into a queue.
3. Run BFS **by minute**:

   * For each minute:

     * Process everything currently in the queue (the oranges that are rotten at this minute).
     * Infect all 4-direction fresh neighbors.
     * For each infected neighbor:

       * mark it rotten in the grid
       * decrement `freshCount`
       * push it into the queue for the *next* minute
   * After finishing that layer, increment minutes if you infected at least one orange.
4. At the end:

   * If `freshCount == 0`, return minutes.
   * Otherwise return -1 (some fresh oranges never got infected).


### How this fixes each bug:

* **Bug #1 (time per orange vs per minute):**
  We now increment `minutes` once per BFS layer (`if (infectedThisMinute)`), not per popped cell.

* **Bug #2 (wrong bounds check):**
  We use:

  ```java
  if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
  ```

* **Bug #3 (we didn’t rot new oranges in the grid):**
  We now do `grid[nr][nc] = 2;` the moment we infect it. That both:

  * prevents it from being double-enqueued,
  * marks it as a spread source for the next minute.

* **Bug #4 (time starts at -1):**
  We now track `minutes = 0` and only increment after real spread.
  We also handle the special case “no fresh at start” → return 0.

* **Bug #5 (fresh vs infectedCount mismatch):**
  We now maintain `fresh` directly, decrementing as we rot new cells.
  At the end, if `fresh != 0`, that means there's unreachable fresh somewhere → return -1.

* **Bug #6 (visited confusion):**
  We don't even need a `visited` matrix. The grid itself becomes the visited structure:

  * 1 → fresh / not visited
  * 2 → rotten / already processed (or in queue)

---

## TL;DR of what's wrong with your code

1. You increment `time` once per orange, not once per minute layer of infection.
2. You start `time = -1`, which returns `-1` even in valid cases like "no fresh oranges".
3. You don't propagate time correctly in BFS (no levels, no per-cell timestamp).
4. Your bounds check is wrong (`nc >= rows` instead of `nr >= rows`).
5. You never mutate `grid[nr][nc]` from 1 → 2 when it rots, so you're not modeling the infection state properly.
6. You try to detect completion with `infectedCount == freshCount`, but infectedCount is counting enqueues, not guaranteed spread by minute, which is coupled with the broken timing logic.
*/

class Solution {
    public int orangesRotting(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        Queue<int[]> q = new ArrayDeque<>();
        int fresh = 0;

        // 1. Init: count fresh and enqueue all rotten
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) {
                    q.offer(new int[]{r, c});
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }

        // Edge case: no fresh oranges at all
        if (fresh == 0) {
            return 0;
        }

        int minutes = 0;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        // 2. BFS layer by layer
        while (!q.isEmpty()) {
            int size = q.size();
            boolean infectedThisMinute = false;

            for (int k = 0; k < size; k++) {
                int[] cur = q.poll();
                int r = cur[0];
                int c = cur[1];

                // Try to rot neighbors
                for (int[] d : dirs) {
                    int nr = r + d[0];
                    int nc = c + d[1];

                    // bounds check (fixed!)
                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                        continue;
                    }

                    // If neighbor is fresh, rot it
                    if (grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;        // now rotten
                        fresh--;                 // one less fresh orange left
                        infectedThisMinute = true;
                        q.offer(new int[]{nr, nc});
                    }
                }
            }

            // We only add a minute if we actually infected something this round
            if (infectedThisMinute) {
                minutes++;
            }
        }

        // 3. After BFS:
        // If all fresh became rotten, return minutes. Otherwise, impossible.
        return (fresh == 0) ? minutes : -1;
    }
}