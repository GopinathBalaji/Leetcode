// Method 1: Recursive DFS with mutating the original grid to mark visisted
/*
Idea: Scan all cells. When you hit an unvisited '1', that’s a new island. Run DFS from it to mark the whole island visited.
Steps:

count = 0
For each cell (r,c):
    If grid[r][c] == '1' and not visited:
    count++
    DFS from (r,c):
        Mark visited (or set to '0')
        Recurse/stack to its 4 neighbors that are in-bounds and '1'
Return count.

Complexity: O(mn) time, O(mn) worst-case space (visited + recursion/stack).
When to avoid recursion: If the grid can be very large (risk of stack overflow), use an explicit stack instead.
*/
class Solution {
    public int numIslands(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int count = 0;

        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(grid[i][j] == '1'){
                    count++;
                    dfs(grid, rows, cols, i, j);
                }
            }
        }

        return count;
    }

    private void dfs(char[][] grid, int rows, int cols, int i, int j){
        if(i < 0 || i >= rows || j < 0 || j >= cols || grid[i][j] != '1'){
            return;
        }

        grid[i][j] = '0';

        dfs(grid, rows, cols, i, j+1);
        dfs(grid, rows, cols, i+1, j);
        dfs(grid, rows, cols, i, j-1);
        dfs(grid, rows, cols, i-1, j);
    }
}





// Method 2: Iterative DFS approach
// class Solution {
//     public int numIslands(char[][] grid) {
//         if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;

//         int rows = grid.length, cols = grid[0].length;
//         int islands = 0;
//         int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (grid[r][c] == '1') {
//                     islands++;
//                     // iterative DFS: use a stack
//                     Deque<int[]> st = new ArrayDeque<>();
//                     st.push(new int[]{r, c});
//                     grid[r][c] = '0'; // mark visited on push

//                     while (!st.isEmpty()) {
//                         int[] cell = st.pop();
//                         int i = cell[0], j = cell[1];

//                         for (int[] d : DIRS) {
//                             int ni = i + d[0], nj = j + d[1];
//                             if (ni >= 0 && ni < rows && nj >= 0 && nj < cols && grid[ni][nj] == '1') {
//                                 grid[ni][nj] = '0';          // mark visited when discovered
//                                 st.push(new int[]{ni, nj});  // push neighbor
//                             }
//                         }
//                     }
//                 }
//             }
//         }
//         return islands;
//     }
// }






// Method 3: BFS Approach
// class Solution {
//     public int numIslands(char[][] grid) {
//         if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;

//         int rows = grid.length, cols = grid[0].length;
//         int islands = 0;

//         // 4-direction moves
//         int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (grid[r][c] == '1') {
//                     islands++;
//                     // BFS from this land cell and sink the whole island
//                     Queue<int[]> q = new ArrayDeque<>();
//                     q.offer(new int[]{r, c});
//                     grid[r][c] = '0'; // mark visited immediately when enqueued

//                     while (!q.isEmpty()) {
//                         int[] cell = q.poll();
//                         int i = cell[0], j = cell[1];

//                         for (int[] d : DIRS) {
//                             int ni = i + d[0], nj = j + d[1];
//                             if (ni >= 0 && ni < rows && nj >= 0 && nj < cols && grid[ni][nj] == '1') {
//                                 grid[ni][nj] = '0';      // mark visited as soon as discovered
//                                 q.offer(new int[]{ni, nj});
//                             }
//                         }
//                     }
//                 }
//             }
//         }
//         return islands;
//     }
// }






// Method 4: Disjoint Set Union (DSU) / Union Find
/*
Absolutely—let’s build a rock-solid understanding of **Disjoint Set Union (DSU)**, also known as **Union–Find**, and then use it to solve **LeetCode 200: Number of Islands** with full code and a step-by-step walkthrough.

---

# What is DSU / Union–Find?

DSU maintains a partition of elements into **disjoint sets** and supports two core operations efficiently:

1. **find(x)** — returns a canonical **representative** (aka “root”) of the set containing `x`.
2. **union(x, y)** — merges the sets containing `x` and `y` (if different).

Internally, DSU represents each set as a **rooted tree**:

* Each element has a **parent pointer**.
* A **root** is its own parent (`parent[root] == root`).
* Elements in the same set eventually point to the same root.

## Two crucial optimizations

* **Path Compression (in `find`)**:
  After finding the root, make every node on the path **point directly to the root**.
  → Dramatically flattens the tree, making future finds almost O(1).

* **Union by Rank / Size**:
  Always attach the smaller tree under the larger one (by **rank** \~ depth upper bound, or by **size** \~ number of nodes).
  → Keeps trees shallow.

With both, the amortized cost per operation is **α(N)** (inverse Ackermann), which is effectively **constant** for all practical N.

---

# When & where is DSU used?

Think **connectivity** that evolves via unions, where queries ask “are these in the same component?” or “how many components are there?”

Common applications:

* **Connected components** in static or dynamic graphs.
* **Kruskal’s algorithm** (Minimum Spanning Tree).
* **Percolation** / grid connectivity.
* **Image segmentation** (merging adjacent same-color pixels).
* **Equations satisfiability** (e.g., union variables that must be equal).
* **Friend circles** / grouping users by relationships.
* **String / parity constraints** (e.g., union-find with extra state).

For **Number of Islands**, we treat each land cell `'1'` as a node; edges connect **adjacent land** (4-direction). The number of islands = **number of connected components** among land cells.

---

# DSU design choices for a grid

1. **Indexing:** map `(r, c)` → `id = r * cols + c`.
2. **Initialization:**

   * Option A: Make DSU for all cells, but only “activate” (`count++`) when the cell is `'1'`.
   * Option B: Build parent arrays only for land. (Implementation typically keeps arrays length `rows*cols` for simplicity.)
3. **Unions:** Only union a `'1'` cell with **right** and **down** neighbors that are `'1'`.
   (Left/up are redundant—would double-process the same edge.)
4. **Counting islands:**

   * Start `count = number of '1's`.
   * Each time `union(a, b)` **actually merges** two different roots, do `count--`.
   * Answer is `count`.

## Why this works

* We treat each land cell as a node and unify it with adjacent land.
* `dsu.count` starts as the number of land cells and decrements on **successful merges** → leaving us with the number of connected components = islands.
* We only union **right** and **down** neighbors to process each adjacency once.

## Complexity

* **Time:** \~`O(R*C * α(R*C))` ≈ **O(R\*C)** (near-linear).
* **Space:** `O(R*C)` for DSU arrays.

---

# Detailed Example Walkthrough

Consider:

```
grid =
1 1 0 0
1 0 0 1
0 0 1 1
```

(‘1’ = land, ‘0’ = water), `rows = 3`, `cols = 4`, `n = 12`.

### Step A — Activate land cells

Coordinates (r,c) and id = r\*4 + c:

* (0,0)->0, (0,1)->1, (0,3)->3 is water so skip, etc.
  Land cells: {0, 1, 4, 7, 10, 11} (assuming we scan all; actual set here:
  (0,0)=0, (0,1)=1, (1,0)=4, (1,3)=7, (2,2)=10, (2,3)=11)
  `count = 6`.

### Step B — Union right/down neighbors for each land cell

Check each land:

* (0,0)=0: right → (0,1)=1 (land) → union(0,1) merges → `count = 5`.
  down  → (1,0)=4 (land) → union(0,4) merges → `count = 4`.

* (0,1)=1: right → (0,2)=2 (water) skip.
  down  → (1,1)=5 (water) skip.

* (1,0)=4: right → (1,1)=5 (water) skip.
  down  → (2,0)=8 (water) skip.
  (Note: 0,1,4 are already connected through unions.)

* (1,3)=7: right → (1,4) oob skip.
  down  → (2,3)=11 (land) → union(7,11) merges → `count = 3`.

* (2,2)=10: right → (2,3)=11 (land) → union(10,11) merges → `count = 2`.
  (This also connects 10 with 7 via 11.)

* (2,3)=11: right oob, down oob → nothing.

At the end: `count = 2` → **two islands**:

* Island A: {0,1,4} (the top-left blob)
* Island B: {7,10,11} (the right-bottom blob)

Matches the visual intuition.

---

# Variations & Tips

* If the grid is huge and memory is tight, you can lazily activate and union row-by-row; but the above is simplest and fast.
* If diagonals counted as neighbors (they don’t in LC200), add those directions in `DIRS`.
* If the grid is **mutable**, BFS/DFS flood-fill is also great. DSU shines when:

  * You like clear connected-component counting,
  * You want to handle union queries efficiently,
  * Or you’re combining this with other DSU tasks (e.g., dynamic connectivity).

---

# Quick DSU Implementation Checklist

* Arrays: `parent[]`, `size[]` (or `rank[]`).
* `makeSet(x)`: set `parent[x]=x`, `size[x]=1`, `count++`.
* `find(x)`: with path compression.
* `union(a,b)`: find roots; if different, attach smaller to larger; `count--`.

Mastering DSU lets you recognize and solve a wide class of connectivity problems quickly and cleanly. If you want, I can also show the **Union by Rank** variant or a **space-optimized** approach tailored to very large grids.
*/

// class Solution {

//     // ---- Disjoint Set Union (Union-Find) with path compression + union by size ----
//     static class DSU {
//         int[] parent;
//         int[] size;
//         int count; // number of disjoint sets currently active (we'll track only land sets)

//         DSU(int n) {
//             parent = new int[n];
//             size = new int[n];
//             // We'll initialize on demand; count starts at 0
//             count = 0;
//             for (int i = 0; i < n; i++) {
//                 parent[i] = -1; // -1 means "not active" (e.g., water)
//                 size[i] = 0;
//             }
//         }

//         // Activate an element as a singleton set (for a land cell)
//         void makeSet(int x) {
//             if (parent[x] == -1) {
//                 parent[x] = x;
//                 size[x] = 1;
//                 count++;
//             }
//         }

//         int find(int x) {
//             // path compression
//             if (parent[x] != x) {
//                 parent[x] = find(parent[x]);
//             }
//             return parent[x];
//         }

//         // returns true if a merge happened
//         boolean union(int a, int b) {
//             int ra = find(a), rb = find(b);
//             if (ra == rb) return false;

//             // union by size (attach smaller under larger)
//             if (size[ra] < size[rb]) {
//                 int tmp = ra; ra = rb; rb = tmp;
//             }
//             parent[rb] = ra;
//             size[ra] += size[rb];
//             count--; // merged two sets into one
//             return true;
//         }
//     }

//     public int numIslands(char[][] grid) {
//         if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;

//         int rows = grid.length, cols = grid[0].length;
//         int n = rows * cols;
//         DSU dsu = new DSU(n);

//         // Step 1: activate all land cells as singleton sets
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (grid[r][c] == '1') {
//                     int id = r * cols + c;
//                     dsu.makeSet(id);
//                 }
//             }
//         }

//         // Step 2: union adjacent land cells (right and down to avoid duplicates)
//         int[][] DIRS = {{0, 1}, {1, 0}}; // right, down
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (grid[r][c] != '1') continue;

//                 int id = r * cols + c;
//                 for (int[] d : DIRS) {
//                     int nr = r + d[0], nc = c + d[1];
//                     if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == '1') {
//                         int nid = nr * cols + nc;
//                         dsu.union(id, nid);
//                     }
//                 }
//             }
//         }

//         // Step 3: number of islands equals the number of active disjoint sets
//         return dsu.count;
//     }
// }
