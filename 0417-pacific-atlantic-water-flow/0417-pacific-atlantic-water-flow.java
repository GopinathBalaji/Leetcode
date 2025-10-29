// Method 1: DFS from Pacific and Atlantic
/*
## The naive idea you’re describing

For every cell `(r, c)`:

1. Run a DFS/BFS “downhill or equal” to see if water from `(r, c)` can eventually flow to the **Pacific**.
2. Run a DFS/BFS again from `(r, c)` to see if water can eventually flow to the **Atlantic**.
3. If both are true, include `(r, c)` in the answer.

Why this is bad:

* There are `m * n` cells.
* Each DFS can walk a big chunk of the grid.
* So worst case becomes ~O(m * n * (m + n)), which is too expensive for larger grids.

Also it's repeating work like crazy. You’ll keep rediscovering the same reachable coastline paths over and over from different starting cells.

So: that approach is conceptually fine but not efficient.

---

## The good approach (what you should actually do)

Flip the perspective.

Instead of asking:

> "From this cell, can water flow *to* the Pacific?"

Ask:

> "From the Pacific, which cells can water flow *from* to get here?"

Why is this legit?

* Water can flow from cell A to cell B if `height[A] >= height[B]` (i.e. water can go downhill or flat).
* So if you're standing at the ocean and you “climb uphill or equal height only,” you're effectively reversing the flow.

Same for Atlantic.

So the efficient algorithm is:

1. Create `pacificReachable[m][n]`, all false.

2. Create `atlanticReachable[m][n]`, all false.

3. Do a DFS/BFS **starting from all Pacific-border cells at once** (top row and left col).

   * From each border cell, move to neighbors that are **height >= current height** (so we only go uphill or flat).
   * Mark everything we can reach as `pacificReachable = true`.

   Intuition: those are all cells whose water could eventually drain *down* to the Pacific.

4. Do the same thing for Atlantic (bottom row and right col), filling `atlanticReachable`.

5. At the end, any cell `(r, c)` where:

   * `pacificReachable[r][c] == true`
   * and `atlanticReachable[r][c] == true`
     is part of the answer.

Why this is great:

* You run at most 2 big graph searches total:

  * one multi-source search from the Pacific edges,
  * one multi-source search from the Atlantic edges.
* Each search touches each cell at most once.
* Time is O(m * n), space O(m * n).

No per-cell DFS. No repeated work.

---

## TL;DR

* You do **not** want to DFS twice *per cell*.
* You want to DFS/BFS twice total:

  * once “reverse-flowing uphill” from the Pacific borders,
  * once “reverse-flowing uphill” from the Atlantic borders.
* Then take the intersection of those two reachable sets.
*/
class Solution {
    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        int rows = heights.length;
        int cols = heights[0].length;
        List<List<Integer>> ans = new ArrayList<>();

        boolean[][] pacificReachable = new boolean[rows][cols];
        boolean[][] atlanticReachable = new boolean[rows][cols];

        for(int i=0; i<cols; i++){
            dfs(heights, 0, i, pacificReachable);
        }
        for(int j=1; j<rows; j++){
            dfs(heights, j, 0, pacificReachable);
        }

        for(int j=0; j<rows; j++){
            dfs(heights, j, cols-1, atlanticReachable);
        }
        for(int i=0; i<cols-1; i++){
            dfs(heights, rows-1, i, atlanticReachable);
        }

        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(pacificReachable[i][j] == true && atlanticReachable[i][j] == true){
                    List<Integer> temp = new ArrayList<>();
                    temp.add(i);
                    temp.add(j);
                    ans.add(temp);
                }
            }
        }

        return ans;
    }


    private void dfs(int[][] heights, int i, int j, boolean[][] visited){
        if(i<0 || i>=heights.length || j<0 || j>=heights[0].length){
            return;
        }
        
        if(visited[i][j] == true){
            return;
        }

        visited[i][j] = true;

        if(i+1 < heights.length && heights[i+1][j] >= heights[i][j]){
            dfs(heights, i+1, j, visited);
        }
        if(j+1 < heights[0].length && heights[i][j+1] >= heights[i][j]){
            dfs(heights, i, j+1, visited);
        }
        if(j-1 >= 0 && heights[i][j-1] >= heights[i][j]){
            dfs(heights, i, j-1, visited);
        }
        if(i-1 >= 0 && heights[i-1][j] >= heights[i][j]){
            dfs(heights, i-1, j, visited);
        }

        return;
    }
}





// Method 2: Iterative DFS from Pacific and Atlantic
/*
## 2. Why this works

### Problem restated in plain terms

* Each cell has a height.
* Water from a cell can flow **to a neighbor** if the neighbor’s height is **≤** current height (water can only go downhill or flat).
* Pacific touches the top and left edges.
* Atlantic touches the bottom and right edges.
* We want all coordinates `(r, c)` where water can eventually reach **both** oceans.

### Naive way (don’t do this)

For every cell, “can I get to Pacific?” and “can I get to Atlantic?” — two searches per cell. That's too slow.

### Trick (the standard solution)

Flip the direction of thinking:

Instead of:

> From cell X, can water go downhill to the Pacific?

We do:

> Starting at the Pacific edge, if water *could* have come downhill into me, which cells could have fed me?

That means:

* Starting from ocean edges, do a search *backwards/uphill*.
* You’re allowed to move from `(r,c)` to `(nr,nc)` **only if** `heights[nr][nc] >= heights[r][c]`.

  * Translation: that neighbor is at least as high, so water could have flowed from that neighbor down to you.

So:

* `pacificReachable[r][c] = true` means: “I can climb from the Pacific into (r,c) by only stepping uphill or flat.” Equivalent to: “Water from (r,c) can flow down to the Pacific.”
* `atlanticReachable` is the same for Atlantic.

At the end, any cell marked true in both arrays can drain to both oceans.

### Why iterative DFS?

Your recursive DFS version is logically correct but could blow the Java call stack on big/monotone cases. Iterative DFS:

* Uses our own `Stack` (`Deque<int[]>`),
* Marks visited when we push into the stack,
* Explores outward exactly like recursive DFS would,
* Never risks `StackOverflowError`.

Functionally it's the same search.

### Complexity

* Each cell gets visited at most once for Pacific and once for Atlantic.
* So time = O(rows * cols), space = O(rows * cols) for the two `visited` arrays.
* This is optimal.

---

## 3. Walkthrough with an example

Let's use the classic example:

```text
heights =
[
  [1, 2, 2, 3, 5],
  [3, 2, 3, 4, 4],
  [2, 4, 5, 3, 1],
  [6, 7, 1, 4, 5],
  [5, 1, 1, 2, 4]
]
rows = 5, cols = 5
Pacific: touches top row (r=0) and left col (c=0)
Atlantic: touches bottom row (r=4) and right col (c=4)
```

### Phase 1: Fill `pacificReachable`

We do iterative DFS starting from:

* All cells in row 0: (0,0), (0,1), (0,2), (0,3), (0,4)
* All cells in col 0: (0,0), (1,0), (2,0), (3,0), (4,0)

When we start a DFS from, say, (0,4) which has height 5:

* We push (0,4) on the stack, mark visited.
* Pop (0,4). We look at its neighbors.

  * (0,3) has height 3 (NOT >= 5) → can't go there, because you can't climb downhill.
  * (1,4) has height 4 (NOT >= 5) → can't.
  * (0,5) OOB
  * (-1,4) OOB
* So from (0,4) nothing else is reachable "uphill". So only (0,4) is marked Pacific-reachable by that starting point.

Now consider a more interesting one: (1,0) has height 3.
From (1,0), iterative DFS can climb to any neighbor whose height is ≥ 3:

* Up: (0,0) has height 1 (1 < 3) → can't climb there.
* Down: (2,0) has 2 (<3) → can't.
* Right: (1,1) has 2 (<3) → can't.
* Left: OOB.

So from (1,0), it's stuck too.

But what about (3,0) with height 6?

* Start stack at (3,0) [height 6].
* Neighbors:

  * (4,0) has 5 (<6) → can't.
  * (2,0) has 2 (<6) → can't.
  * (3,1) has 7 (≥6!) → YES, we can go to (3,1).

    * Mark (3,1) visited for Pacific and push it.

Now pop (3,1) (height 7):

* Neighbors that are ≥7:

  * (2,1) = 4 (<7) no
  * (4,1) = 1 (<7) no
  * (3,0) = 6 (<7) no (also already visited)
  * (3,2) = 1 (<7) no
    So we stop there.

What this told us:

* Because (3,0) touches the Pacific edge (col 0),
* and from (3,0) we could "climb uphill" to (3,1),
* both (3,0) and (3,1) are cells whose water can flow down to the Pacific.

Do this for all Pacific-border starts, and you eventually fill out `pacificReachable[r][c] = true` for every cell that can drain to the Pacific.

Notably, a bunch of cells toward the upper-left / middle will get marked.

---

### Phase 2: Fill `atlanticReachable`

Now we run the same iterative DFS, but starting from:

* All cells in last row (r=4): (4,0), (4,1), (4,2), (4,3), (4,4)
* All cells in last col (c=4): (0,4), (1,4), (2,4), (3,4), (4,4)

This time, we’re saying:
"From the Atlantic edges inward, where can we *climb* (to equal or higher heights) if we only allow steps where neighborHeight >= currentHeight?"

For example, look at (4,4) with height 4.

* From (4,4), we can climb to (3,4) = 5 (since 5 >= 4),
* From (3,4)=5, we can climb to (2,4)=1? No (1 < 5)
* We can climb to (3,3)=4? Yes (4 >= 5? No. So actually no. Careful: must be ≥ current cell's height. 4 is NOT ≥ 5.)
  So (3,4) might not spread far here, depending on surrounding heights.

Look at (2,2) which is height 5 in the grid:

* It will eventually be marked reachable from **both** oceans in the official answer.
* Why is it Atlantic-reachable? Because from the Atlantic side, you can climb “uphill” paths that link back up to that 5.

Running this process for the Atlantic edges fills `atlanticReachable`.

---

### Phase 3: Intersection

Now we just check:

```text
for each cell (r,c):
    if pacificReachable[r][c] && atlanticReachable[r][c]:
        add [r,c]
```

These are the coordinates from which water can eventually flow to both oceans.

Intuition check with a famous cell:

* (2,2) = 5 ends up in both sets because:

  * From Pacific: you can start on the upper/left edges and "climb up" to reach this 5.
  * From Atlantic: you can start on the bottom/right edges and "climb up" to reach this 5.
  * That means water sitting on (2,2) can flow downhill toward both oceans.

Cells that cannot feed both oceans will only show up in one of the matrices.

---

## Key points to remember in an interview

1. **We do NOT DFS from each cell.**
   We DFS (iteratively) from each ocean border once. That’s what makes this O(m·n).

2. **We reverse the water direction conceptually.**
   Instead of “can it flow down to the ocean,” we ask “can the ocean climb up to it (only stepping to same or higher heights)?”

3. **Two visited matrices.**
   One for Pacific reachability, one for Atlantic. Then intersect.

4. **Iterative DFS avoids stack overflow.**
   This is especially good to mention in Java interviews, because recursive DFS can blow the call stack on pathological monotone surfaces.

---

## Final summary

* Your recursive version was logically correct.
* The iterative DFS version above keeps the exact same rules and search order, but replaces recursion with an explicit stack.
* We seed Pacific and Atlantic separately, mark everything they can reach by climbing “uphill”, then output cells reachable by both.
*/
// class Solution {

//     public List<List<Integer>> pacificAtlantic(int[][] heights) {
//         int rows = heights.length;
//         int cols = heights[0].length;

//         boolean[][] pacificReachable = new boolean[rows][cols];
//         boolean[][] atlanticReachable = new boolean[rows][cols];

//         // Run iterative DFS from all Pacific-border cells
//         // Pacific touches: top row (row = 0) and left col (col = 0)
//         for (int c = 0; c < cols; c++) {
//             dfsIter(heights, 0, c, pacificReachable);
//         }
//         for (int r = 1; r < rows; r++) { // start from 1 to avoid (0,0) twice
//             dfsIter(heights, r, 0, pacificReachable);
//         }

//         // Run iterative DFS from all Atlantic-border cells
//         // Atlantic touches: bottom row (row = rows-1) and right col (col = cols-1)
//         for (int c = 0; c < cols; c++) {
//             dfsIter(heights, rows - 1, c, atlanticReachable);
//         }
//         for (int r = 0; r < rows - 1; r++) { // stop at rows-2 to avoid (rows-1, cols-1) twice
//             dfsIter(heights, r, cols - 1, atlanticReachable);
//         }

//         // Any cell that can reach BOTH oceans goes in the answer
//         List<List<Integer>> result = new ArrayList<>();
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (pacificReachable[r][c] && atlanticReachable[r][c]) {
//                     List<Integer> cell = new ArrayList<>(2);
//                     cell.add(r);
//                     cell.add(c);
//                     result.add(cell);
//                 }
//             }
//         }

//         return result;
//     }

//     // Iterative DFS using an explicit stack instead of recursion
//     private void dfsIter(int[][] heights, int sr, int sc, boolean[][] visited) {
//         int rows = heights.length;
//         int cols = heights[0].length;

//         // If we've already explored this cell for this ocean, skip
//         if (visited[sr][sc]) return;

//         Deque<int[]> stack = new ArrayDeque<>();
//         stack.push(new int[]{sr, sc});
//         visited[sr][sc] = true; // mark on push to avoid duplicates

//         // Directions: down, up, right, left
//         int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

//         while (!stack.isEmpty()) {
//             int[] cell = stack.pop();
//             int r = cell[0];
//             int c = cell[1];

//             // Try to "climb" to neighbors that are >= current height
//             for (int[] d : dirs) {
//                 int nr = r + d[0];
//                 int nc = c + d[1];

//                 // bounds check
//                 if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
//                     continue;
//                 }

//                 // We are doing reverse flow:
//                 // We can go from (r,c) -> (nr,nc) if heights[nr][nc] >= heights[r][c]
//                 // because that means water could have flowed downhill from (nr,nc) to (r,c)
//                 if (!visited[nr][nc] && heights[nr][nc] >= heights[r][c]) {
//                     visited[nr][nc] = true;
//                     stack.push(new int[]{nr, nc});
//                 }
//             }
//         }
//     }
// }








// Method 3: BFS from Pacific and Atlantic
/*
## Big picture (what we're solving)

You're given a height matrix:

* Water can flow from cell A to cell B if **B's height ≤ A's height** (so water can go downhill or flat).
* Pacific ocean touches the **top row and left column**.
* Atlantic ocean touches the **bottom row and right column**.
* We want all coordinates where water can eventually flow to **both** oceans.

Instead of running a search *from every cell outward*, we flip it:

> We start from each ocean’s border and BFS *inward*, but only stepping from low → high (i.e. to neighbors with `height >= current height`).
> Any cell we can reach that way is a cell whose water can drain to that ocean.

Then we intersect the two reachable maps.

This is the same trick as DFS, but we'll use BFS with queues.

## Key ideas in this BFS

### 1. Why BFS from borders?

We’re answering:

* "Which cells can drain to Pacific?"
  Instead of trying every cell and seeing if it can escape to Pacific (which is expensive), we reverse it:
* Start **at the Pacific itself** and ask: "Which cells can I climb back into if I’m only allowed to move from lower/equal → higher?"

That gives you a `pacificReachable[r][c]`.

Then do the same for Atlantic, giving `atlanticReachable`.

Any cell that both oceans can “climb into” is a cell whose water can run downhill to both oceans.

### 2. Why `heights[nr][nc] >= heights[r][c]`?

Normally, water flows *from high to low*:
`canFlow(A -> B)` if `height[B] <= height[A]`.

We reverse that direction. In reverse:
We can move from B to A only if `height[A] >= height[B]`.

So in BFS:

```java
if (heights[nr][nc] >= heights[r][c]) {
    // (nr,nc) can drain down to (r,c), so (nr,nc) is also reachable
}
```

### 3. Why we mark visited on enqueue

We do:

```java
visited[nr][nc] = true;
queue.offer(...);
```

This prevents adding the same cell many times. Classic BFS hygiene.

### 4. Complexity

* Each cell goes into each queue at most once.
* So total time is O(rows * cols).
* Space is O(rows * cols) for the visited arrays and queues.

This is optimal.

---

## Full example walkthrough

Let's walk the standard example grid:

```text
heights =
[
  [1, 2, 2, 3, 5],
  [3, 2, 3, 4, 4],
  [2, 4, 5, 3, 1],
  [6, 7, 1, 4, 5],
  [5, 1, 1, 2, 4]
]
rows = 5, cols = 5
Pacific = top row & left col
Atlantic = bottom row & right col
```

We'll conceptually simulate the BFS.

---

### Phase A. Build `pacificReachable`

**Initial Pacific queue seeds:**

* Top row: (0,0) (0,1) (0,2) (0,3) (0,4)
* Left col: (0,0) (1,0) (2,0) (3,0) (4,0)

We mark all of those `pacificReachable[r][c] = true` before BFS even starts.

Now we BFS. Let's zoom in on one seed and see how the rule works.

#### Example: Start from (0,4) with height 5

Neighbors are:

* (0,3): height 3 → 3 >= 5 ? No. Can't go.
* (1,4): height 4 → 4 >= 5 ? No.
* (-1,4), (0,5): out of bounds.

So from (0,4), we don't expand. That means only (0,4) itself is known Pacific-reachable via that path.

That makes sense: (0,4) is literally on the Pacific border.

#### Example: Start from (3,0) with height 6

This is left edge, so it's also Pacific seed.

Check neighbors of (3,0):

* (2,0) has height 2 → 2 >= 6 ? No.
* (4,0) has height 5 → 5 >= 6 ? No.
* (3,1) has height 7 → 7 >= 6 ? Yes! We can mark (3,1).
* (3,-1) OOB

So we mark `pacificReachable[3][1] = true` and enqueue `(3,1)`.

Now process (3,1) which has height 7:
Neighbors:

* (2,1) = 4 → 4 >= 7 ? No
* (4,1) = 1 → 1 >= 7 ? No
* (3,0) = 6 → already true, but 6 >= 7 ? No anyway
* (3,2) = 1 → 1 >= 7 ? No

So that branch stops.

What did we learn?

* Because (3,0) touches the Pacific,
* and we could "climb uphill" to (3,1),
* that means water sitting at (3,1) can run downhill to (3,0) and out to the Pacific.

So: (3,0) and (3,1) are Pacific-reachable.

#### Another seed: (1,0) with height 3

Neighbors:

* (0,0)=1 → 1 >= 3 ? No
* (2,0)=2 → 2 >= 3 ? No
* (1,1)=2 → 2 >= 3 ? No
  So (1,0) doesn't expand.

Over the full BFS, this continues until no new cells are marked.

At the end of Pacific BFS:

* `pacificReachable[r][c] = true` for any cell we could "climb into" starting from the top row or left col, only moving to neighbors with `neighborHeight >= currHeight`.

Intuition: Those are cells from which water can drain down to the Pacific.

---

### Phase B. Build `atlanticReachable`

Now do the same BFS logic, but starting from Atlantic borders:

* Bottom row: (4,0) (4,1) (4,2) (4,3) (4,4)
* Right col: (0,4) (1,4) (2,4) (3,4) (4,4)

We seed those into the Atlantic queue and mark them visited in `atlanticReachable`.

Then BFS with the same rule:
We can move from (r,c) to (nr,nc) if `heights[nr][nc] >= heights[r][c]`.

Let's look at (4,4) which has height 4 (bottom-right corner, Atlantic border).

Neighbors of (4,4):

* (3,4)=5 → 5 >= 4 ? Yes → mark (3,4) reachable by Atlantic
* (4,3)=2 → 2 >= 4 ? No
* (5,4) OOB
* (4,5) OOB

Now pop (3,4) (height 5):
Neighbors:

* (2,4)=1 → 1 >= 5 ? No
* (4,4)=4 → already marked
* (3,3)=4 → 4 >= 5 ? No
* (3,5) OOB
  So that branch stops.

Another interesting seed: (2,4) has height 1.
From (2,4)=1:

* (1,4)=4 → 4 >= 1 ? Yes, good.
* (3,4)=5 → 5 >= 1 ? Yes.
* (2,3)=3 → 3 >= 1 ? Yes.
* (2,5) OOB
  So (2,4) basically "climbs" into a bunch of higher ground cells.

That means lots of interior cells become `atlanticReachable`.

Same interpretation:
If the Atlantic BFS can "climb up" to you, then water at your cell can flow downhill to the Atlantic.

---

### Phase C. Take the intersection

At the end we have:

* `pacificReachable[r][c] == true` means: water here can reach Pacific.
* `atlanticReachable[r][c] == true` means: water here can reach Atlantic.

So if both are true:

```java
if (pacificReachable[r][c] && atlanticReachable[r][c]) {
    add [r, c] to the answer
}
```

Classic output for this example includes cells like:

* (2,2) (height 5): it’s high enough that:

  * Pacific can reach it by climbing from the left/top edges,
  * Atlantic can reach it by climbing from the right/bottom edges.
    So its water can drain to both oceans.

---

## Mental model to remember

Here’s the interview story you can tell:

1. Water flows downhill.
2. So instead of asking "can this cell reach the ocean by going downhill?", I reverse it and ask:

   * "Starting at the ocean, if I only walk to neighbors that are **higher or equal**, where can I get to?"
3. I run that search twice:

   * once from the Pacific edges,
   * once from the Atlantic edges.
4. Anywhere both searches can reach is a cell whose water can drain to both oceans.
5. I can do this with BFS instead of DFS by using a queue and marking visited when I enqueue.
6. Total time O(m·n), total space O(m·n), no recursion depth issues.
*/

// class Solution {
//     public List<List<Integer>> pacificAtlantic(int[][] heights) {
//         int rows = heights.length;
//         int cols = heights[0].length;

//         boolean[][] pacificReachable = new boolean[rows][cols];
//         boolean[][] atlanticReachable = new boolean[rows][cols];

//         Queue<int[]> pacificQueue = new ArrayDeque<>();
//         Queue<int[]> atlanticQueue = new ArrayDeque<>();

//         // 1. Seed BFS with all Pacific-border cells
//         // Pacific: top row (0, c) and left col (r, 0)
//         for (int c = 0; c < cols; c++) {
//             pacificQueue.offer(new int[]{0, c});
//             pacificReachable[0][c] = true;
//         }
//         for (int r = 0; r < rows; r++) {
//             pacificQueue.offer(new int[]{r, 0});
//             pacificReachable[r][0] = true;
//         }

//         // 2. Seed BFS with all Atlantic-border cells
//         // Atlantic: bottom row (rows-1, c) and right col (r, cols-1)
//         for (int c = 0; c < cols; c++) {
//             atlanticQueue.offer(new int[]{rows - 1, c});
//             atlanticReachable[rows - 1][c] = true;
//         }
//         for (int r = 0; r < rows; r++) {
//             atlanticQueue.offer(new int[]{r, cols - 1});
//             atlanticReachable[r][cols - 1] = true;
//         }

//         // 3. BFS outward from Pacific and Atlantic
//         bfs(heights, pacificQueue, pacificReachable);
//         bfs(heights, atlanticQueue, atlanticReachable);

//         // 4. Cells reachable by BOTH oceans are the answer
//         List<List<Integer>> result = new ArrayList<>();
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (pacificReachable[r][c] && atlanticReachable[r][c]) {
//                     List<Integer> cell = new ArrayList<>(2);
//                     cell.add(r);
//                     cell.add(c);
//                     result.add(cell);
//                 }
//             }
//         }

//         return result;
//     }

//     private void bfs(int[][] heights, Queue<int[]> queue, boolean[][] visited) {
//         int rows = heights.length;
//         int cols = heights[0].length;
//         int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

//         while (!queue.isEmpty()) {
//             int[] cell = queue.poll();
//             int r = cell[0];
//             int c = cell[1];

//             // Try to move to neighbors that are >= current height
//             // That means those neighbors are high enough that water
//             // could flow down from them to us.
//             for (int[] d : dirs) {
//                 int nr = r + d[0];
//                 int nc = c + d[1];

//                 if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
//                     continue;
//                 }

//                 // We have two conditions to continue BFS to (nr, nc):
//                 // 1. Not visited yet for this ocean.
//                 // 2. heights[nr][nc] >= heights[r][c]
//                 //
//                 // Why #2?
//                 // Because we're "reversing" the water flow.
//                 // If water can go downhill from (nr,nc) to (r,c),
//                 // then in reverse we can move uphill from (r,c) to (nr,nc).
//                 if (!visited[nr][nc] && heights[nr][nc] >= heights[r][c]) {
//                     visited[nr][nc] = true;
//                     queue.offer(new int[]{nr, nc});
//                 }
//             }
//         }
//     }
// }
