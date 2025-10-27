// Method 1: Graph DFS
/*
# What I was doing wrong:

## 1. Initial value of `max`

You did:

```java
int max = Integer.MIN_VALUE / 4;
```

Why this is a problem:

* It's totally possible for the grid to have **no land at all** (all 0s). In that case the max island area should be `0`.
* With your current init, you'd return something like `-500 million` instead of `0`.

Much safer is to start with:

```java
int max = 0;
```

Because the area of an island can't be negative.

---

## 2. Your DFS base cases are returning `-1` (that's a bug)

You have:

```java
if (i < 0 || i >= rows || j < 0 || j >= cols) {
    return -1;
}
if (visited[i][j] == true) {
    return -1;
}
```

Why `-1` is wrong:

You're trying to compute area = number of land cells in this connected component.

The DFS should sum:

```text
1 (this cell) + area(up) + area(down) + area(left) + area(right)
```

If you ever return `-1`, you'll start subtracting from the area when you hit edges or water, which will destroy the count. You want "no contribution" from invalid / already-seen / water cells, not "negative contribution".

So these base cases should return `0`, not `-1`.

Also: you still need to check if the cell is water.

---

## 3. You're not checking if `grid[i][j] == 0` inside `dfs`

In `maxAreaOfIsland`, you only *start* DFS if `grid[i][j] != 0`, which is good:

```java
if (grid[i][j] != 0 && visited[i][j] == false) {
    max = Math.max(max, dfs(...));
}
```

But once you recurse in DFS, you move to neighbors that might be water.

Inside `dfs`, before you continue, you need to say:

* if this cell is water (`grid[i][j] == 0`), return 0.

Right now you don't check that at all, so neighbors that are water will still get marked visited and counted, which is wrong.

So the order in `dfs` needs to look like:

1. If out of bounds → return 0.
2. If already visited → return 0.
3. If this cell is water (`grid[i][j] == 0`) → return 0.
4. Otherwise, it's land you haven't visited yet → mark visited and explore neighbors.

You're missing #3.

---

## 4. You're marking `visited` before confirming it's actually land

You currently do:

```java
if (visited[i][j] == true) {
    return -1;
}

visited[i][j] = true;
```

But you haven't ruled out water yet. That means you’ll mark water cells as visited, so later you might block real land exploration coming from another direction.

You absolutely must only mark a cell visited **after** you've decided:

* it's in bounds
* it's not visited yet
* it's land (1)

So the correct order inside `dfs` should be:

```java
// 1. bounds guard
// 2. visited guard
// 3. water guard
// 4. mark visited
// 5. recurse 4 directions and add them
```

Your current code doesn't follow that order yet.

---

## 5. Your recursive step is incomplete

You started:

```java
int up = 
```

The shape you're probably aiming for is:

```java
int up = dfs(..., i-1, j);
int down = dfs(..., i+1, j);
int left = dfs(..., i, j-1);
int right = dfs(..., i, j+1);

// total area contributed by this connected island piece:
return 1 + up + down + left + right;
```

Why `1 + ...`?

* `1` counts the current land cell.
* Each recursive call returns the total area reachable in that direction from neighboring land that hasn't yet been visited.

Right now, you haven't actually added the `1` for the current cell. That's another missing piece.

---

## 6. Summary of the logical issues

Let's list them cleanly:

1. `max` should start at `0`, not `Integer.MIN_VALUE / 4`, so you correctly handle the "no islands" case.

2. Your DFS base cases should return `0`, not `-1`.
   You don't want to subtract from the area when you hit borders, water, or revisits.

3. In `dfs`, you're missing:

   * a check for `grid[i][j] == 0` → return `0`.
   * and that check needs to happen before marking visited.

4. You mark `visited[i][j] = true` too early.
   You should only mark visited once you've confirmed you're on land.

5. In your DFS return value, you haven't added:

   ```java
   1 + area(up) + area(down) + area(left) + area(right)
   ```

   That `1` is critical, otherwise you never count the current cell.

6. You didn't finish the neighbor exploration in `dfs`.

---

## 7. What the final shape of your DFS should look like (conceptually)

I'm not going to give you the full final code, but this is the exact logical skeleton you want to implement in your `dfs`:

* Guard cases:

  * If out of bounds → return 0
  * If already visited → return 0
  * If water (`grid[i][j] == 0`) → return 0

* Mark `visited[i][j] = true`

* Recurse in 4 directions

* Return `1 + up + down + left + right`

Then in `maxAreaOfIsland`, keep `max = Math.max(max, dfs(...))` with `max` starting at 0.

If you fix those 6 issues you'll get the correct behavior:

* count only land,
* don't double count,
* don't leak negative values,
* don't return some nonsense giant negative when there's no island.
*/

class Solution {
    public int maxAreaOfIsland(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        boolean[][] visited = new boolean[rows][cols];

        int max = 0;
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(grid[i][j] != 0 && visited[i][j] == false){
                    max = Math.max(max, dfs(grid, visited, rows, cols, i, j));
                }
            }
        }

        return max;
    }

    private int dfs(int[][] grid, boolean[][] visited, int rows, int cols, int i, int j){
        if(i<0 || i>=rows || j<0 || j>=cols){
            return 0;
        }
        if(visited[i][j] == true){
            return 0;
        }
        if(grid[i][j] == 0){
            return 0;
        }

        visited[i][j] = true;

        int up = dfs(grid, visited, rows, cols, i-1, j);
        int down = dfs(grid, visited, rows, cols, i+1, j);
        int left = dfs(grid, visited, rows, cols, i, j-1);
        int right = dfs(grid, visited, rows, cols, i, j+1);

        return 1 + up + down + left + right;
    }
}





// Method 2: Iterative DFS
/*
# High-level strategy:

* Scan all cells.
* When you find an unvisited land cell (`1`), explore that whole island (flood fill).
* Count how big it is.
* Track the max.

We’ll go step by step.

---

## Shared ideas before code

### Visited array

We need to avoid counting the same land cell multiple times. We'll keep:

```java
boolean[][] visited;
```

where `visited[r][c] == true` means “we’ve already included this land cell in an island calculation.”

### Directions

We'll explore 4-connected neighbors using a helper like:

```java
int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
```

That means:

* down:    (r+1, c)
* up:      (r-1, c)
* right:   (r, c+1)
* left:    (r, c-1)

### Bounds check

When exploring a neighbor `(nr, nc)`, we must ensure:

* `0 <= nr < rows`
* `0 <= nc < cols`

### Area counting

When we expand an island, we count how many cells we visit in that connected component. That count is the island’s area.


### Why this works

* We loop through all cells.
* When we find untouched land, we treat that as the start of *one* island.
* We then explore that whole island using a stack (LIFO). This is depth-first search, just without recursion.
* For each connected island:

  * We count how many cells we pop.
  * That’s the area.
* We update `maxArea`.

### Why we mark `visited` when pushing, not when popping

This prevents pushing the same cell multiple times. If you only mark on pop, you'd push neighbors over and over on the stack. Marking on push is standard flood-fill practice.
*/

// class Solution {
//     public int maxAreaOfIsland(int[][] grid) {
//         int rows = grid.length;
//         int cols = grid[0].length;

//         boolean[][] visited = new boolean[rows][cols];
//         int maxArea = 0;

//         // Explore every cell
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {

//                 // Start a new DFS only if:
//                 // - it's land (1)
//                 // - and we haven't visited it yet
//                 if (grid[r][c] == 1 && !visited[r][c]) {

//                     // We'll do an iterative DFS from (r, c)
//                     int area = 0;
//                     Stack<int[]> stack = new Stack<>();
//                     stack.push(new int[]{r, c});
//                     visited[r][c] = true;  // mark as soon as we push

//                     while (!stack.isEmpty()) {
//                         int[] cell = stack.pop();
//                         int cr = cell[0];
//                         int cc = cell[1];

//                         // We popped a real land cell in this island
//                         area++;

//                         // Explore its 4 neighbors
//                         int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
//                         for (int[] d : dirs) {
//                             int nr = cr + d[0];
//                             int nc = cc + d[1];

//                             // Check bounds
//                             if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
//                                 continue;
//                             }

//                             // Only visit if:
//                             // - neighbor is land
//                             // - neighbor has not been visited
//                             if (grid[nr][nc] == 1 && !visited[nr][nc]) {
//                                 visited[nr][nc] = true; // VERY IMPORTANT: mark visited when pushing
//                                 stack.push(new int[]{nr, nc});
//                             }
//                         }
//                     }

//                     // After finishing this DFS, we know the full island area
//                     maxArea = Math.max(maxArea, area);
//                 }
//             }
//         }

//         return maxArea;
//     }
// }





// Method 3: Graph BFS
/*
### DFS vs BFS here

* DFS with a stack dives deep before backing up.
* BFS with a queue spreads outward layer by layer.
* In this problem, they both:

  * visit exactly the same set of land cells in an island,
  * count how many cells are in that component,
  * compute the same answer.
* Runtime and memory are both O(rows * cols), because in the worst case you visit every cell once.

Pick whichever traversal the interviewer prefers, or write both and flex \U0001f60e

---

## Full Walkthrough Example

Let’s walk through this grid:

```text
grid = [
  [0,0,1,0],
  [1,1,1,0],
  [0,1,0,0],
  [1,0,0,1]
]
```

Visually (row,col indices shown):

Row 0:  (0,0)=0  (0,1)=0  (0,2)=1  (0,3)=0
Row 1:  (1,0)=1  (1,1)=1  (1,2)=1  (1,3)=0
Row 2:  (2,0)=0  (2,1)=1  (2,2)=0  (2,3)=0
Row 3:  (3,0)=1  (3,1)=0  (3,2)=0  (3,3)=1

We’ll run iterative DFS. BFS would behave analogously, just queue instead of stack.

### Step 1: r=0,c=0

* grid[0][0] = 0 → water → skip

r=0,c=1

* grid[0][1] = 0 → skip

r=0,c=2

* grid[0][2] = 1 and not visited
* Start new island DFS from (0,2)

Initialize:

```text
stack = [(0,2)]
visited[0][2] = true
area = 0
```

#### Pop (0,2)

* area = 1
* neighbors of (0,2):

  * down  -> (1,2) = 1 and not visited → push, mark visited
  * up    -> (-1,2) out of bounds → ignore
  * right -> (0,3) = 0 → ignore
  * left  -> (0,1) = 0 → ignore
    Now:

```text
stack = [(1,2)]
visited includes (0,2),(1,2)
```

#### Pop (1,2)

* area = 2
* neighbors of (1,2):

  * down  -> (2,2) = 0 → ignore
  * up    -> (0,2) already visited → ignore
  * right -> (1,3) = 0 → ignore
  * left  -> (1,1) = 1 → push (1,1), mark visited
    Now:

```text
stack = [(1,1)]
visited includes (0,2),(1,2),(1,1)
```

#### Pop (1,1)

* area = 3
* neighbors:

  * down  -> (2,1) = 1 → push (2,1), mark visited
  * up    -> (0,1) = 0 → ignore
  * right -> (1,2) visited → ignore
  * left  -> (1,0) = 1 → push (1,0), mark visited
    Now:

```text
stack = [(2,1),(1,0)]
visited includes (0,2),(1,2),(1,1),(2,1),(1,0)
```

Note: order can vary if you push in a different order. That’s fine.

#### Pop (1,0)

* area = 4
* neighbors:

  * down  -> (2,0) = 0 → ignore
  * up    -> (0,0) = 0 → ignore
  * right -> (1,1) visited → ignore
  * left  -> (1,-1) out of bounds → ignore
    Stack now:

```text
stack = [(2,1)]
```

#### Pop (2,1)

* area = 5
* neighbors:

  * down  -> (3,1) = 0 → ignore
  * up    -> (1,1) visited → ignore
  * right -> (2,2) = 0 → ignore
  * left  -> (2,0) = 0 → ignore
    Stack now:

```text
stack = []
```

Island finished.
This island (the blob in the upper middle) has area = 5.

Update:

```text
maxArea = max(0, 5) = 5
```

Visited so far covers cells:
(0,2), (1,2), (1,1), (2,1), (1,0)

---

Resume scanning the grid:

r=0,c=3 → 0 water

r=1,c=0 → already visited (part of that island) → skip
r=1,c=1 → visited → skip
r=1,c=2 → visited → skip
r=1,c=3 → 0 → skip

r=2,c=0 → 0 skip
r=2,c=1 → visited → skip
r=2,c=2 → 0 skip
r=2,c=3 → 0 skip

r=3,c=0

* grid[3][0] = 1 and NOT visited
* New island

New DFS:

```text
stack = [(3,0)]
visited[3][0] = true
area = 0
```

Pop (3,0):

* area = 1
* neighbors:

  * down -> (4,0) OOB → ignore
  * up   -> (2,0) = 0 → ignore
  * right-> (3,1) = 0 → ignore
  * left -> (3,-1) OOB → ignore
    stack = []
    So that island’s area = 1.
    maxArea = max(5,1) = 5.

Next:

r=3,c=1 → 0
r=3,c=2 → 0
r=3,c=3 → 1 and not visited
Start DFS from (3,3):

* This is isolated land in the corner.

We'll get area = 1 again.
maxArea stays 5.

---

### Final result for this grid:

`maxArea = 5`

Which matches the largest connected blob we saw.

So both iterative DFS and BFS will compute:

* Island 1 area = 5
* Island 2 area = 1
* Island 3 area = 1
  Answer = 5.

---

## Key Takeaways

**1. Flood-fill shape is the same.**
You:

* Loop over all cells,
* When you find land that’s not visited,
* Traverse the whole component,
* Count it,
* Track the best.

**2. DFS (stack) vs BFS (queue)**
Both work. DFS can go deep and use stack memory (or explicit Stack to avoid recursion depth issues). BFS spreads level by level with a queue. Interviews are fine with either.

**3. Mark visited immediately when you enqueue / push.**
This prevents duplicate work.

**4. Return 0 if a neighbor is out of bounds or water.**
Never return -1 for island area, because areas are sums, and you don’t want to subtract.

**5. maxArea should start at 0.**
It's valid to have no island at all.
*/

// class SolutionBFS {
//     public int maxAreaOfIsland(int[][] grid) {
//         int rows = grid.length;
//         int cols = grid[0].length;

//         boolean[][] visited = new boolean[rows][cols];
//         int maxArea = 0;

//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {

//                 if (grid[r][c] == 1 && !visited[r][c]) {

//                     int area = 0;
//                     Queue<int[]> q = new ArrayDeque<>();
//                     q.offer(new int[]{r, c});
//                     visited[r][c] = true;

//                     while (!q.isEmpty()) {
//                         int[] cell = q.poll();
//                         int cr = cell[0];
//                         int cc = cell[1];

//                         area++;

//                         int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
//                         for (int[] d : dirs) {
//                             int nr = cr + d[0];
//                             int nc = cc + d[1];

//                             if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
//                                 continue;
//                             }

//                             if (grid[nr][nc] == 1 && !visited[nr][nc]) {
//                                 visited[nr][nc] = true;
//                                 q.offer(new int[]{nr, nc});
//                             }
//                         }
//                     }

//                     maxArea = Math.max(maxArea, area);
//                 }
//             }
//         }

//         return maxArea;
//     }
// }
