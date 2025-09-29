// Method 1: Inner-region first
/*
Check every internal 'O' connected region using DFS to see if that 
region touches the border. If not, only then mark that region with 'X' using
another DFS call.

## Why this works

* We treat the board as a graph. Each time we see an **interior** `'O'` that hasn’t been probed, we **probe** its connected component with `dfsTouchesBorder`.
* The probe marks all `'O'` cells in that component as `visited` and returns **true** if any cell in that component lies on the border.
* If the probe returns **false** (fully enclosed), we run a second DFS `flipRegion` starting from the same seed `(i, j)` to flip the connected component’s `'O'` cells to `'X'`. We keep a separate `visited2` to avoid reflooding cells while flipping.

> We never start from border cells in the outer loop (`i>0…<rows-1`, `j>0…<cols-1`), so we only consider interior components. Any component that has a path to the border will be detected during the probe and **not flipped**.

## Complexity

* Each cell is visited O(1) times across the two DFS passes.
* **Time:** O(R·C).
* **Space:** O(R·C) for the two `visited` arrays and recursion stack in the worst case.

## Small walkthrough

Input:

```
X X X X
X O O X
X X O X
X O X X
```

1. Start at (1,1) `'O'` (interior, unvisited) → probe:

   * It explores {(1,1),(1,2),(2,2)}; none are on the border → `touchesBorder = false`.
2. Flip from (1,1):

   * `flipRegion` sets those three to `'X'`.

Board now:

```
X X X X
X X X X
X X X X
X O X X
```

3. The `'O'` at (3,1) is on the border; we never start a probe from border cells, so it remains `'O'`.

Final result is correct.

---

### Optional: a simpler alternative

Many people prefer the **border-first** method: flood-fill from every border `'O'` marking them (e.g., `'#'`), then flip the rest `'O'`→`'X'`, and finally turn `'#'` back to `'O'`. It avoids the boolean-return DFS and the second `visited2` array.
*/ 

class Solution {
    public void solve(char[][] board) {
        if (board == null || board.length == 0 || board[0].length == 0) return;

        int rows = board.length;
        int cols = board[0].length;

        boolean[][] visited = new boolean[rows][cols];   // for probe (touches border?)
        boolean[][] visited2 = new boolean[rows][cols];  // for flipping enclosed regions

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                // Start only from interior cells that are 'O' and not yet probed
                if (i > 0 && i < rows - 1 && j > 0 && j < cols - 1
                        && !visited[i][j]
                        && board[i][j] == 'O') {

                    boolean touchesBorder = dfsTouchesBorder(board, visited, rows, cols, i, j);

                    // If the region is enclosed, flip it
                    if (!touchesBorder) {
                        flipRegion(board, visited2, rows, cols, i, j);
                    }
                }
            }
        }
    }

    // Returns true if the 'O'-region containing (i,j) touches the border.
    private boolean dfsTouchesBorder(char[][] board, boolean[][] visited, int rows, int cols, int i, int j) {
        // out of bounds or not 'O' or already visited => no border contribution from here
        if (i < 0 || i >= rows || j < 0 || j >= cols) return false;
        if (board[i][j] != 'O' || visited[i][j]) return false;

        // mark before exploring neighbors to avoid cycles / rework
        visited[i][j] = true;

        // is this cell on the border?
        boolean isBorder = (i == 0 || i == rows - 1 || j == 0 || j == cols - 1);

        // explore four directions; if any branch touches the border, the whole region does
        boolean right = dfsTouchesBorder(board, visited, rows, cols, i, j + 1);
        boolean down  = dfsTouchesBorder(board, visited, rows, cols, i + 1, j);
        boolean left  = dfsTouchesBorder(board, visited, rows, cols, i, j - 1);
        boolean up    = dfsTouchesBorder(board, visited, rows, cols, i - 1, j);

        return isBorder || right || down || left || up;
    }

    // Flip the entire (now-known-to-be-enclosed) 'O'-region containing (i,j) to 'X'.
    private void flipRegion(char[][] board, boolean[][] visited2, int rows, int cols, int i, int j) {
        if (i < 0 || i >= rows || j < 0 || j >= cols) return;
        if (board[i][j] != 'O' || visited2[i][j]) return;

        visited2[i][j] = true;
        board[i][j] = 'X';

        flipRegion(board, visited2, rows, cols, i, j + 1);
        flipRegion(board, visited2, rows, cols, i + 1, j);
        flipRegion(board, visited2, rows, cols, i, j - 1);
        flipRegion(board, visited2, rows, cols, i - 1, j);
    }
}





// Method 2: Using Border-first approach (with recursive DFS)
/*
Mark “safe” regions: any 'O' connected to the border (up/down/left/right) must stay 'O'. We flood-fill from the border, temporarily marking those cells (e.g., '#').
Flip enclosed regions: any remaining 'O' is surrounded → flip to 'X'.
Restore marks: turn '#' back to 'O'.
This guarantees we only flip interior regions not touching the border.
*/

// class Solution {
//     private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

//     public void solve(char[][] board) {
//         if (board == null || board.length == 0 || board[0].length == 0) return;
//         int R = board.length, C = board[0].length;

//         // 1) Mark all border-connected 'O' as '#'
//         for (int c = 0; c < C; c++) {
//             if (board[0][c] == 'O') dfs(board, 0, c, R, C);
//             if (board[R-1][c] == 'O') dfs(board, R-1, c, R, C);
//         }
//         for (int r = 0; r < R; r++) {
//             if (board[r][0] == 'O') dfs(board, r, 0, R, C);
//             if (board[r][C-1] == 'O') dfs(board, r, C-1, R, C);
//         }

//         // 2) Flip enclosed 'O' to 'X'; 3) restore '#' to 'O'
//         for (int r = 0; r < R; r++) {
//             for (int c = 0; c < C; c++) {
//                 if (board[r][c] == 'O') board[r][c] = 'X';
//                 else if (board[r][c] == '#') board[r][c] = 'O';
//             }
//         }
//     }

//     private void dfs(char[][] b, int i, int j, int R, int C) {
//         if (i < 0 || i >= R || j < 0 || j >= C || b[i][j] != 'O') return;
//         b[i][j] = '#';
//         for (int[] d : DIRS) dfs(b, i + d[0], j + d[1], R, C);
//     }
// }





// Method 3: Using border first approach (Iterative DFS (stack))
/*
Mark “safe” regions: any 'O' connected to the border (up/down/left/right) must stay 'O'. We flood-fill from the border, temporarily marking those cells (e.g., '#').
Flip enclosed regions: any remaining 'O' is surrounded → flip to 'X'.
Restore marks: turn '#' back to 'O'.
This guarantees we only flip interior regions not touching the border.
*/

// class Solution {
//     private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

//     public void solve(char[][] board) {
//         if (board == null || board.length == 0 || board[0].length == 0) return;
//         int R = board.length, C = board[0].length;

//         // 1) Mark border-connected 'O' with '#'
//         Deque<int[]> st = new ArrayDeque<>();
//         // top/bottom rows
//         for (int c = 0; c < C; c++) {
//             if (board[0][c] == 'O') pushMark(board, st, 0, c);
//             if (board[R-1][c] == 'O') pushMark(board, st, R-1, c);
//         }
//         // left/right cols
//         for (int r = 0; r < R; r++) {
//             if (board[r][0] == 'O') pushMark(board, st, r, 0);
//             if (board[r][C-1] == 'O') pushMark(board, st, r, C-1);
//         }

//         while (!st.isEmpty()) {
//             int[] cell = st.pop();
//             int i = cell[0], j = cell[1];
//             for (int[] d : DIRS) {
//                 int ni = i + d[0], nj = j + d[1];
//                 if (ni >= 0 && ni < R && nj >= 0 && nj < C && board[ni][nj] == 'O') {
//                     pushMark(board, st, ni, nj);
//                 }
//             }
//         }

//         // 2) Flip enclosed; 3) restore marks
//         for (int r = 0; r < R; r++) {
//             for (int c = 0; c < C; c++) {
//                 if (board[r][c] == 'O') board[r][c] = 'X';
//                 else if (board[r][c] == '#') board[r][c] = 'O';
//             }
//         }
//     }

//     private void pushMark(char[][] b, Deque<int[]> st, int i, int j) {
//         b[i][j] = '#';             // mark on push to avoid duplicates
//         st.push(new int[]{i, j});
//     }
// }



// Method 4: Using border first approach (BFS (queue))
/*
Mark “safe” regions: any 'O' connected to the border (up/down/left/right) must stay 'O'. We flood-fill from the border, temporarily marking those cells (e.g., '#').
Flip enclosed regions: any remaining 'O' is surrounded → flip to 'X'.
Restore marks: turn '#' back to 'O'.
This guarantees we only flip interior regions not touching the border.
*/

// class Solution {
//     private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

//     public void solve(char[][] board) {
//         if (board == null || board.length == 0 || board[0].length == 0) return;
//         int R = board.length, C = board[0].length;

//         Queue<int[]> q = new ArrayDeque<>();

//         // 1) Enqueue and mark all border 'O' as '#'
//         for (int c = 0; c < C; c++) {
//             if (board[0][c] == 'O') offerMark(board, q, 0, c);
//             if (board[R-1][c] == 'O') offerMark(board, q, R-1, c);
//         }
//         for (int r = 0; r < R; r++) {
//             if (board[r][0] == 'O') offerMark(board, q, r, 0);
//             if (board[r][C-1] == 'O') offerMark(board, q, r, C-1);
//         }

//         while (!q.isEmpty()) {
//             int[] cell = q.poll();
//             int i = cell[0], j = cell[1];
//             for (int[] d : DIRS) {
//                 int ni = i + d[0], nj = j + d[1];
//                 if (ni >= 0 && ni < R && nj >= 0 && nj < C && board[ni][nj] == 'O') {
//                     offerMark(board, q, ni, nj);
//                 }
//             }
//         }

//         // 2) Flip enclosed; 3) restore marks
//         for (int r = 0; r < R; r++) {
//             for (int c = 0; c < C; c++) {
//                 if (board[r][c] == 'O') board[r][c] = 'X';
//                 else if (board[r][c] == '#') board[r][c] = 'O';
//             }
//         }
//     }

//     private void offerMark(char[][] b, Queue<int[]> q, int i, int j) {
//         b[i][j] = '#';             // mark on enqueue
//         q.offer(new int[]{i, j});
//     }
// }





// Method 5: Union Find / Disjoin Set Union (DSU) approach
/*
# What is DSU / Union-Find (quick but solid)

DSU maintains a partition of elements into disjoint sets with two O(α(N)) (effectively O(1)) ops:

* `find(x)`: return the canonical **root** (representative) of x’s set
* `union(x, y)`: merge the sets containing x and y (if different)

Implementation: every element has a parent pointer; **roots** point to themselves. Two key speedups:

* **Path compression** (in `find`): after finding the root, make every node on the path point directly to the root (flattens the tree).
* **Union by size/rank**: always attach the smaller tree under the larger tree (keeps trees shallow).

---

# Applying DSU to “Surrounded Regions” (LC 130)

## Problem recap

Flip all interior `'O'` regions fully surrounded by `'X'`. Any `'O'` that touches the **border** (directly or via neighbors) must **not** be flipped.

## Modeling with DSU

1. **Node mapping**
   Treat each cell `(r, c)` as a node with id `id = r * cols + c`. We’ll also create **one extra “virtual” node** `BORDER = rows * cols` to represent the **border**.

2. **Unions**

   * For every `'O'` on the **border**, **union** it with `BORDER`.
   * For every `'O'` cell, **union** it with its **right** and **down** neighbor if that neighbor is also `'O'` (this connects each `'O'` region; right/down suffices to avoid duplicate work).

3. **Flip or keep**

   * After all unions:
     For each cell `(r, c)` that is `'O'`:

     * If `find(id) == find(BORDER)`: it’s connected to the border → **keep as `'O'`**
     * Else: it’s enclosed → **flip to `'X'`**

This is elegant because the border connectivity is captured by the DSU structure. No recursion/queues needed.

**Complexity:** Building unions is \~O(R*C), each union/find amortized O(1). Two passes over the grid → \*\*O(R*C)\*\* time, **O(R\*C)** space.


**Notes**

* We never mutate non-‘O’ cells.
* Using only right & down neighbors avoids doubling unions (left/up would add the same edges).
* The virtual `BORDER` node is what makes post-processing trivial.

---

# Thorough example walkthrough

Input:

```
R = 4, C = 4

X X X X
X O O X
X X O X
X O X X
```

Index each cell by `id = r * C + c`:

```
(0,0)=0  (0,1)=1  (0,2)=2  (0,3)=3
(1,0)=4  (1,1)=5  (1,2)=6  (1,3)=7
(2,0)=8  (2,1)=9  (2,2)=10 (2,3)=11
(3,0)=12 (3,1)=13 (3,2)=14 (3,3)=15
BORDER = 16
```

**‘O’ cells:** (1,1)=5, (1,2)=6, (2,2)=10, (3,1)=13

### Step 1 — Unions

* Border ‘O’s: only (3,1)=13 is on the border (last row).
  → `union(13, BORDER)`.

* Adjacency unions (right, down):

  * (1,1)=5 is ‘O’, right neighbor (1,2)=6 is ‘O’ → `union(5, 6)`.
  * (1,2)=6 is ‘O’, down neighbor (2,2)=10 is ‘O’ → `union(6, 10)`.
  * (2,2)=10 is ‘O’, right/down are X or oob → no more.
  * (3,1)=13 is ‘O’, right/down are X/oob → no more.

Connected components now:

* Component A: {5, 6, 10} (the interior blob in rows 1–2)
* Component B: {13, BORDER} (the bottom ‘O’ touching the border)

### Step 2 — Flip or keep

Compute `borderRoot = find(BORDER)`.

* For each ‘O’:

  * id 5: `find(5)` == root of component A, **not** `borderRoot` → flip to ‘X’.
  * id 6: same component → flip.
  * id 10: same component → flip.
  * id 13: `find(13) == borderRoot` → **keep** as ‘O’.

**Result:**

```
X X X X
X X X X
X X X X
X O X X
```

Exactly the intended transformation: the interior region is captured, border-connected ‘O’ is preserved.

---

## DSU vs. BFS/DFS (border-first)

* **DFS/BFS border-first**: mark all border-connected ‘O’ as safe (e.g., `'#'`), flip the rest, restore.

  * Simple, very popular.
* **DSU**: more “algorithmic”—great when you want a reusable **connectivity** scaffold or if the problem morphs (multiple queries, dynamic unions, counting components, etc.). Time/space are comparable here.

---

## Pitfalls to avoid

* Using `'0'` (zero) instead of `'O'` (letter O).
* Forgetting the **virtual border node** (then you can’t easily tell which components are safe).
* Unioning all directions redundantly (right+down is enough).
* Not compressing paths or unioning by size → can still pass, but slower.
* Flipping everything in place before unions (don’t do that; flip only after unions).

---

If you’d like, I can also show a DSU version that **builds sets only for ‘O’ cells** (saves a bit of space) or extend this DSU to answer extra queries like “how many separate safe regions are there?”
*/

// class Solution {
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
//             if (parent[x] != x) parent[x] = find(parent[x]); // path compression
//             return parent[x];
//         }

//         void union(int a, int b) {
//             int ra = find(a), rb = find(b);
//             if (ra == rb) return;
//             // union by size: attach smaller under larger
//             if (size[ra] < size[rb]) {
//                 int t = ra; ra = rb; rb = t;
//             }
//             parent[rb] = ra;
//             size[ra] += size[rb];
//         }
//     }

//     public void solve(char[][] board) {
//         if (board == null || board.length == 0 || board[0].length == 0) return;
//         int R = board.length, C = board[0].length;
//         int N = R * C;
//         int BORDER = N; // virtual node id

//         DSU dsu = new DSU(N + 1); // +1 for the virtual border node

//         // Helper to map (r,c) -> id
//         java.util.function.BiFunction<Integer,Integer,Integer> id = (r, c) -> r * C + c;

//         // 1) Union border 'O's with BORDER, and union adjacent 'O's (right/down)
//         for (int r = 0; r < R; r++) {
//             for (int c = 0; c < C; c++) {
//                 if (board[r][c] != 'O') continue;

//                 int cur = id.apply(r, c);

//                 // if on the border, connect to BORDER
//                 if (r == 0 || r == R - 1 || c == 0 || c == C - 1) {
//                     dsu.union(cur, BORDER);
//                 }
//                 // union with right neighbor if 'O'
//                 if (c + 1 < C && board[r][c + 1] == 'O') {
//                     dsu.union(cur, id.apply(r, c + 1));
//                 }
//                 // union with down neighbor if 'O'
//                 if (r + 1 < R && board[r + 1][c] == 'O') {
//                     dsu.union(cur, id.apply(r + 1, c));
//                 }
//             }
//         }

//         // 2) Flip enclosed 'O's: those not connected to BORDER
//         int borderRoot = dsu.find(BORDER);
//         for (int r = 0; r < R; r++) {
//             for (int c = 0; c < C; c++) {
//                 if (board[r][c] == 'O') {
//                     int root = dsu.find(id.apply(r, c));
//                     if (root != borderRoot) board[r][c] = 'X';
//                 }
//             }
//         }
//     }
// }
