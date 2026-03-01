// Method 1: DSU / Union-Find by size
/*
# Big idea of the DSU approach for 695

In DFS, you “walk an island” and count cells.

In DSU, you do something different:

* Treat every land cell (`1`) as a node
* If two land cells are adjacent (up/down/left/right), they belong to the **same island**
* Use DSU to merge connected land cells into groups
* DSU stores the **size of each group**
* The answer is the **largest group size**

So DSU turns the problem into:

> “Find the largest connected component of land cells.”

---

# Why Union-Find *by size* is a good fit here

You need the **area** (number of cells) of each island.

With DSU by size:

* each root stores `size[root] = number of cells in that island`
* when you merge two islands, you do:
  `size[newRoot] += size[otherRoot]`

That means island area is available directly.

---

# Important clarification (based on your earlier code)

For **LeetCode 695**, you should **NOT** remove edge-connected land first.

That edge DFS trick is for a different problem (like **Number of Enclaves**).

For 695:

* keep the grid as-is
* build DSU directly on all land cells

---

---

# Part 1: Detailed explanation of the DSU class (line by line)

## DSU fields

```java
int[] parent;
int[] size;
```

### `parent[x]`

This tells you who `x` points to in the DSU tree.

* If `parent[x] == x`, then `x` is the **root** (leader) of its set
* If `parent[x] = y`, then `x` belongs to the set whose root is somewhere above `y`

### `size[x]`

Meaningful **only for roots**.

* `size[root]` = number of nodes in that connected component
* For non-root nodes, `size[...]` may be stale/irrelevant and we don’t use it

---

## Constructor

```java
DSU(int n) {
    parent = new int[n];
    size = new int[n];
    Arrays.fill(parent, -1); // -1 means "not active" (water)
}
```

### Why `-1`?

Because in a grid, not every cell is land.

You are creating DSU of size `rows * cols`, which includes:

* land cells
* water cells

So we use `parent[id] = -1` to mean:

> “This node is not part of any set (it is water).”

This is a very useful grid trick.

---

## `makeSet(int x)`

```java
void makeSet(int x) {
    parent[x] = x;
    size[x] = 1;
}
```

This is called **only for land cells**.

It means:

* activate this node
* it starts as its own island
* area = 1

So initially every land cell is a separate island of size 1.

---

## `find(int x)` with path compression

```java
int find(int x) {
    if (parent[x] != x) {
        parent[x] = find(parent[x]);
    }
    return parent[x];
}
```

### What it does

Returns the **root** of `x`’s set.

### Why path compression matters

Suppose a chain is:
`13 -> 8 -> 3 -> 3`

If you call `find(13)`, it eventually finds root `3`, and then rewrites:

* `parent[13] = 3`
* `parent[8] = 3` (as recursion unwinds)

So future finds become much faster.

Think of it as:

> “Flatten the tree whenever you search.”

---

## `union(int a, int b)` by size

```java
boolean union(int a, int b) {
    int ra = find(a);
    int rb = find(b);

    if (ra == rb) return false;

    if (size[ra] < size[rb]) {
        int temp = ra;
        ra = rb;
        rb = temp;
    }

    parent[rb] = ra;
    size[ra] += size[rb];
    return true;
}
```

### Step-by-step meaning

1. Find roots of `a` and `b`
2. If same root, they are already in same island → nothing to do
3. If different, merge smaller island into larger island
4. Update the size of the new root

### Why attach smaller to bigger?

This keeps the DSU trees shallow, which makes `find()` faster.

This is the “by size” heuristic.

---

## `getSize(int x)`

```java
int getSize(int x) {
    int rx = find(x);
    return size[rx];
}
```

This returns the island area containing cell `x`.

Even if `x` is not the root, `find(x)` gives the root, and then you read `size[root]`.

---

---

# Part 2: How this plugs into LeetCode 695

You gave the skeleton logic, which is exactly the right DSU structure. Let’s break down **why it is written in 3 phases**.

## Phase 1: Initialize land cells as singleton sets

You create DSU of size `rows * cols`, but only activate land.

For every `grid[r][c] == 1`:

* compute its id
* call `makeSet(id)`

At this point:

* every land cell is its own island (area 1)

---

## Phase 2: Union adjacent land cells

Now scan again.

For each land cell, check:

* right neighbor
* down neighbor

If neighbor is land → union them.

### Why only right and down?

Because checking all 4 directions causes duplicate work.

Example:

* If `(r,c)` unions with `(r,c+1)` (right),
  you do **not** need `(r,c+1)` later to union back left.

So right + down covers every adjacency exactly once.

---

## Phase 3: Find the maximum island size

After all unions, DSU has grouped all connected land cells.

Now scan all land cells again and compute:

* `maxArea = max(maxArea, dsu.getSize(id))`

This works even if `id` is not a root, because `getSize()` uses `find()`.

---

---

# Part 3: Thorough example walkthrough (full trace)

Let’s walk through a complete example.

## Example grid (4 x 5)

```text
[
  [1, 1, 0, 0, 0],
  [1, 0, 0, 1, 1],
  [0, 0, 1, 1, 0],
  [0, 0, 0, 0, 1]
]
```

### Visual islands

* Island A: top-left → cells `(0,0), (0,1), (1,0)` → area 3
* Island B: middle-right → cells `(1,3), (1,4), (2,3), (2,2)` → area 4
* Island C: bottom-right `(3,4)` → area 1

Expected answer = **4**

---

## Step A: 2D to 1D mapping

`cols = 5`, so:

`id = r * 5 + c`

Mapping:

* `(0,0) -> 0`

* `(0,1) -> 1`

* `(0,2) -> 2`

* `(0,3) -> 3`

* `(0,4) -> 4`

* `(1,0) -> 5`

* `(1,1) -> 6`

* `(1,2) -> 7`

* `(1,3) -> 8`

* `(1,4) -> 9`

* `(2,0) -> 10`

* `(2,1) -> 11`

* `(2,2) -> 12`

* `(2,3) -> 13`

* `(2,4) -> 14`

* `(3,0) -> 15`

* `(3,1) -> 16`

* `(3,2) -> 17`

* `(3,3) -> 18`

* `(3,4) -> 19`

Land IDs are: `0, 1, 5, 8, 9, 12, 13, 19`

---

## Step B: Phase 1 initialization (`makeSet` on land)

Initially constructor does:

* `parent[*] = -1` for all 20 cells

After `makeSet` on land cells:

* `parent[0] = 0`, `size[0] = 1`
* `parent[1] = 1`, `size[1] = 1`
* `parent[5] = 5`, `size[5] = 1`
* `parent[8] = 8`, `size[8] = 1`
* `parent[9] = 9`, `size[9] = 1`
* `parent[12] = 12`, `size[12] = 1`
* `parent[13] = 13`, `size[13] = 1`
* `parent[19] = 19`, `size[19] = 1`

All other `parent[id] = -1` (water).

At this moment, each land cell is its own island.

`maxArea` becomes `1` (because we saw land)

---

## Step C: Phase 2 unions (right and down only)

Now scan row by row.

---

### Cell `(0,0)` = land, id = 0

Check right `(0,1)` = land, id = 1
→ `union(0, 1)`

* `find(0) = 0`
* `find(1) = 1`
* sizes equal (1,1)
* attach `1` under `0` (because code attaches `rb` to `ra` when equal)
* `parent[1] = 0`
* `size[0] = 2`

Now set `{0,1}` has area 2.

Check down `(1,0)` = land, id = 5
→ `union(0, 5)`

* `find(0) = 0`
* `find(5) = 5`
* `size[0] = 2`, `size[5] = 1`
* attach 5 under 0
* `parent[5] = 0`
* `size[0] = 3`

Now island A root is `0`, size `3`.

---

### Cell `(0,1)` = land, id = 1

Check right `(0,2)` = water → no union
Check down `(1,1)` = water → no union

---

### Cell `(1,0)` = land, id = 5

Check right `(1,1)` = water
Check down `(2,0)` = water

No unions.

---

### Cell `(1,3)` = land, id = 8

Check right `(1,4)` = land, id = 9
→ `union(8, 9)`

* `find(8) = 8`
* `find(9) = 9`
* equal size
* attach 9 under 8
* `parent[9] = 8`
* `size[8] = 2`

Check down `(2,3)` = land, id = 13
→ `union(8, 13)`

* `find(8) = 8`
* `find(13) = 13`
* `size[8] = 2`, `size[13] = 1`
* attach 13 under 8
* `parent[13] = 8`
* `size[8] = 3`

Island B currently (root 8) has area 3.

---

### Cell `(1,4)` = land, id = 9

Check right: out of bounds
Check down `(2,4)` = water

No union.

---

### Cell `(2,2)` = land, id = 12

Check right `(2,3)` = land, id = 13
→ `union(12, 13)`

This is a great moment to see `find()` + path compression.

* `find(12) = 12`
* `find(13)`:

  * `parent[13] = 8` (not root of itself)
  * `find(8) = 8`
  * compress: `parent[13] = 8`
  * returns `8`

So roots are:

* `ra = 12`
* `rb = 8`

Now compare sizes:

* `size[12] = 1`
* `size[8] = 3`

Since `size[12] < size[8]`, swap roots inside union so larger remains root.

Attach 12 under 8:

* `parent[12] = 8`
* `size[8] = 4`

Now island B has area 4.

Check down `(3,2)` = water

---

### Cell `(2,3)` = land, id = 13

Check right `(2,4)` = water
Check down `(3,3)` = water

No union.

---

### Cell `(3,4)` = land, id = 19

Check right: out of bounds
Check down: out of bounds

No union.

---

## Final DSU groups after all unions

### Island A

Root = `0`
Members = `0, 1, 5`
`size[0] = 3`

### Island B

Root = `8`
Members = `8, 9, 13, 12`
`size[8] = 4`

### Island C

Root = `19`
Members = `19`
`size[19] = 1`

So max area should be 4.

---

## Step D: Phase 3 final scan for max size

Now scan all land cells and call `getSize(id)`.

### Example calls

* `getSize(0)` → root 0 → size 3
* `getSize(1)` → find(1) compresses to 0 → size 3
* `getSize(8)` → size 4
* `getSize(12)` → find(12)=8 → size 4
* `getSize(19)` → size 1

Take max across all → **4**

✅ Answer = **4**

---

# Why the 3rd pass works even with duplicates

You might think:

> “If I call `getSize` for every cell in the same island, I’ll see the same island size many times.”

Yes, and that’s okay.

Example:

* `getSize(8) = 4`
* `getSize(9) = 4`
* `getSize(12) = 4`
* `getSize(13) = 4`

Repeated values don’t hurt because you’re just taking `max`.

This makes the code simpler.

---

# Time and space complexity

Let `m = rows`, `n = cols`, total cells `N = m*n`.

### Time

* Initialize pass: `O(N)`
* Union pass: `O(N)` unions/checks (constant neighbors)
* Final max pass: `O(N)`

Each `find/union` is almost constant amortized (technically `α(N)`, inverse Ackermann).

So total:

* **O(N * α(N))**, which is effectively **O(N)** in practice.

### Space

* `parent[]`, `size[]` each size `N`
* Total **O(N)**

---

# Common mistakes (very relevant for this approach)

## 1) DSU size mismatch

If you use `id = r * cols + c`, then DSU must be sized for **all cells**:

* `new DSU(rows * cols)`

Not “number of land cells”.

---

## 2) Calling `find()` on water

If `parent[id] == -1` (water), `find(id)` is invalid.

Avoid this by only calling:

* `makeSet`
* `union`
* `getSize`

for cells where `grid[r][c] == 1`.

Your skeleton already does this correctly.

---

## 3) Wrong 2D→1D formulas due to missing parentheses

Correct:

* `rightId = r * cols + (c + 1)`
* `downId = (r + 1) * cols + c`

Parentheses matter.

---

## 4) Checking all 4 directions and getting duplicate unions

Not wrong, just unnecessary.

Right + down is cleaner and faster.

---

## 5) Accidentally solving another problem

For **695**, do **not** zero boundary land first.

That changes the problem.

---

# Optional improvement (advanced but simple)

Instead of a third pass, you can update `maxArea` during unions:

* After each successful union, compute size of the root and update max
* Also keep `maxArea = 1` if any land exists

But for learning and debugging, the **third pass is better** because it’s easier to reason about.

---

# Mental model to remember for interviews

If you forget implementation details, remember this:

1. **Every land cell starts as island of area 1**
2. **Union neighboring lands**
3. **DSU root stores island area in `size[root]`**
4. **Maximum `size[root]` is the answer**

That’s the entire DSU solution.
*/
class Solution {
    class DSU{
        int[] parent;
        int[] size;

        DSU(int n){
            parent = new int[n];
            size = new int[n];

            Arrays.fill(parent, -1);  // -1 means "not active" (water)
        }
        
        public int find(int x){
            if(x != parent[x]){
                parent[x] = find(parent[x]);
            }

            return parent[x];
        }

        // union by size: attach smaller tree to bigger tree
        public boolean union(int a, int b){
            int ra = find(a);
            int rb = find(b);

            if(ra == rb){
                return false;
            }

            if(size[ra] < size[rb]){
                int temp = ra;
                ra = rb;
                rb = temp;
            }

            parent[rb] = ra;
            size[ra] += size[rb];
            return true;
        }
    }


    public int maxAreaOfIsland(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        DSU dsu = new DSU(rows * cols);
        
        int maxArea = 0;

        // 1) initialize land cells
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(grid[i][j] == 1){
                    int id = i * cols + j;
                    dsu.parent[id] = id;
                    dsu.size[id] = 1;

                    maxArea = 1;  // at least one land exists
                }
            }
        }


        // 2) union adjacent land cells (right and down only)
        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                if(grid[r][c] == 1){
                    int id = r * cols + c;

                    if(c+1 < cols && grid[r][c + 1] == 1){
                        int rightId = r * cols + (c+1);
                        dsu.union(id, rightId);
                    }

                    if(r+1 < rows && grid[r + 1][c] == 1){
                        int downId = (r+1) * cols + c;
                        dsu.union(id, downId);
                    }
                }
            }
        }


        // 3) scan roots to get max size
        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                if(grid[r][c] == 1){
                    int id = r * cols + c;
                    int root = dsu.find(id);

                    int size = dsu.size[root];

                    maxArea = Math.max(maxArea, size);
                }
            }
        }

        return maxArea;
    }
}







// Method 1.5: DSU / Union-Find by rank
/*
Absolutely. This is a very good follow-up because it highlights an important distinction:

* **Union by size** helps keep trees shallow *and* directly gives area
* **Union by rank** keeps trees shallow, but **rank is not area**

So for **LeetCode 695 (Max Area of Island)**, if you use **DSU by rank**, you still need a separate way to track island area (usually a `size[]` array).

That’s the key idea.

---

# Big idea of DSU by rank for 695

We still do the same high-level steps:

1. Treat each land cell as a DSU node
2. Union adjacent land cells
3. Track connected component sizes
4. Return the maximum size

### Important difference

With **union by rank**:

* `rank[]` is only used to decide which root becomes parent
* `rank[]` is **not** the island area
* You still maintain a separate `size[]` for area

So the DSU stores **two different concepts**:

* `rank[root]` → balancing heuristic
* `size[root]` → actual island area

---

# Part 1: DSU by rank (with size tracking) — detailed explanation

## Fields you need

```java
int[] parent;
int[] rank;
int[] size;
```

### `parent[x]`

Points to parent of `x` in DSU tree.

* `parent[x] == x` means `x` is root
* `parent[x] == -1` means water/inactive

### `rank[x]`

Used only for union balancing.

* Standard initialization for singleton set is `0`
* Only meaningful for roots

### `size[x]`

Stores the number of land cells in the connected component.

* Only meaningful for roots
* This is what gives you the island area

---

## Why both `rank[]` and `size[]`?

Because rank does **not** represent the number of nodes.

Example:

* A root might have `rank = 2` but `size = 17`
* Another root might have `rank = 1` but `size = 8`

Rank is just a tree-shape heuristic.

---

## Constructor

```java
DSU(int n) {
    parent = new int[n];
    rank = new int[n];
    size = new int[n];
    Arrays.fill(parent, -1); // water / inactive by default
}
```

All nodes start inactive. We activate only land cells.

---

## `makeSet(int x)` for land cells

```java
void makeSet(int x) {
    parent[x] = x;
    rank[x] = 0;
    size[x] = 1;
}
```

Meaning:

* this cell is land
* its own parent (new component)
* rank 0 (single node tree)
* area 1

---

## `find(int x)` with path compression

```java
int find(int x) {
    if (parent[x] != x) {
        parent[x] = find(parent[x]);
    }
    return parent[x];
}
```

Returns the root of x’s component.

### Path compression effect

If `x -> y -> z -> z`, after `find(x)`:

* `x -> z`
* `y -> z`

Trees become flatter → future operations faster.

---

## `union(int a, int b)` by rank (while updating size)

This is the most important part.

```java
boolean union(int a, int b) {
    int ra = find(a);
    int rb = find(b);

    if (ra == rb) return false; // already same island

    if (rank[ra] < rank[rb]) {
        parent[ra] = rb;
        size[rb] += size[ra];
    } else if (rank[ra] > rank[rb]) {
        parent[rb] = ra;
        size[ra] += size[rb];
    } else {
        parent[rb] = ra;       // choose one as new root
        size[ra] += size[rb];  // merge area
        rank[ra]++;            // only because ranks were equal
    }

    return true;
}
```

### What’s happening here?

* `rank` decides root attachment
* `size` accumulates area into the new root
* `rank` changes only when two equal-rank trees merge

---

## `getSize(int x)`

```java
int getSize(int x) {
    int rx = find(x);
    return size[rx];
}
```

Gives area of the island containing `x`.

---

# Part 2: How this solves LeetCode 695

## Step-by-step algorithm (same as size version)

### 1) Create DSU for all cells

If `rows = m`, `cols = n`, total cells = `m*n`
Use:

* `new DSU(m * n)`

Why all cells?
Because you map cell `(r,c)` to:

* `id = r * cols + c`

This ID can be anywhere from `0` to `m*n - 1`.

---

### 2) Activate land cells

Loop through grid:

* If `grid[r][c] == 1`, call `makeSet(id)`

Also set `maxArea = 1` if any land exists (otherwise answer stays 0).

---

### 3) Union adjacent land cells

Loop again:

* For each land cell, check only **right** and **down**
* If neighbor is land, union them

Why only right/down?
To avoid duplicate unions. (Left/up will be handled from earlier cells.)

---

### 4) Get max area

Final pass over land cells:

* `maxArea = Math.max(maxArea, dsu.getSize(id))`

Return `maxArea`.


# Part 4: Thorough example walkthrough (rank + size)

Let’s use the same example, but now explicitly track **rank** too.

## Example grid (4 x 5)

```text
[
  [1, 1, 0, 0, 0],
  [1, 0, 0, 1, 1],
  [0, 0, 1, 1, 0],
  [0, 0, 0, 0, 1]
]
```

Expected max area = **4**

---

## Step A: 2D → 1D mapping

`cols = 5`, so `id = r * 5 + c`

Land cells:

* `(0,0)=0`
* `(0,1)=1`
* `(1,0)=5`
* `(1,3)=8`
* `(1,4)=9`
* `(2,2)=12`
* `(2,3)=13`
* `(3,4)=19`

---

## Step B: Initialization (`makeSet` on land)

For each land ID, we set:

* `parent[id] = id`
* `rank[id] = 0`
* `size[id] = 1`

So initially:

* roots: `0,1,5,8,9,12,13,19`
* each has rank 0, size 1

At this point:

* every land cell is a separate island of area 1

---

## Step C: Union pass (right + down only)

We scan row by row.

---

### 1) Cell `(0,0)` id=0

#### Right neighbor `(0,1)` id=1 → union(0,1)

* `find(0)=0`, `find(1)=1`
* `rank[0]=0`, `rank[1]=0` (equal)

Equal-rank rule:

* attach `1` under `0`
* `parent[1]=0`
* `size[0]=2`
* `rank[0]++` → `rank[0]=1`

Now:

* root 0 has rank 1, size 2

#### Down neighbor `(1,0)` id=5 → union(0,5)

* `find(0)=0`
* `find(5)=5`
* `rank[0]=1`, `rank[5]=0`

Since rank[0] > rank[5]:

* attach `5` under `0`
* `parent[5]=0`
* `size[0]=3`
* rank unchanged (`rank[0]=1`)

Now island A:

* root 0
* size 3
* rank 1

---

### 2) Cell `(0,1)` id=1

Right `(0,2)` is water
Down `(1,1)` is water

No union.

---

### 3) Cell `(1,0)` id=5

Right `(1,1)` is water
Down `(2,0)` is water

No union.

---

### 4) Cell `(1,3)` id=8

#### Right neighbor `(1,4)` id=9 → union(8,9)

* `find(8)=8`, `find(9)=9`
* equal rank (0,0)

Attach 9 under 8:

* `parent[9]=8`
* `size[8]=2`
* `rank[8]=1`

#### Down neighbor `(2,3)` id=13 → union(8,13)

* `find(8)=8`
* `find(13)=13`
* `rank[8]=1`, `rank[13]=0`

Attach 13 under 8:

* `parent[13]=8`
* `size[8]=3`
* rank unchanged (`rank[8]=1`)

Now island B (partial):

* root 8
* size 3
* rank 1

---

### 5) Cell `(1,4)` id=9

Right out of bounds
Down `(2,4)` is water

No union.

---

### 6) Cell `(2,2)` id=12

#### Right neighbor `(2,3)` id=13 → union(12,13)

Now this is interesting because 13 is not a root anymore.

* `find(12)=12`
* `find(13)`:

  * `parent[13]=8`, so root is `8`
  * path compression keeps `parent[13]=8`

So effectively union(12, 8)

Compare ranks:

* `rank[12]=0`
* `rank[8]=1`

Attach 12 under 8:

* `parent[12]=8`
* `size[8]=4`
* rank unchanged (`rank[8]=1`)

Now island B:

* root 8
* size 4
* rank 1

#### Down neighbor `(3,2)` is water

No union.

---

### 7) Cell `(2,3)` id=13

Right `(2,4)` water
Down `(3,3)` water

No union.

---

### 8) Cell `(3,4)` id=19

No right/down neighbors in bounds that are land.

Island C remains:

* root 19
* size 1
* rank 0

---

## Final components after unions

### Island A

Members: `{0,1,5}`

* root = 0
* `size[0] = 3`
* `rank[0] = 1`

### Island B

Members: `{8,9,13,12}`

* root = 8
* `size[8] = 4`
* `rank[8] = 1`

### Island C

Members: `{19}`

* root = 19
* `size[19] = 1`
* `rank[19] = 0`

So max area = **4**

---

## Step D: Final max scan

We now scan all land cells and ask `getSize(id)`:

* `getSize(0)` → 3
* `getSize(1)` → find(1)=0 → 3
* `getSize(5)` → 3
* `getSize(8)` → 4
* `getSize(9)` → find(9)=8 → 4
* `getSize(12)` → 4
* `getSize(13)` → 4
* `getSize(19)` → 1

Maximum = **4**

✅ Answer = **4**

---

# Part 5: Rank vs Size — what to remember (very important)

This is the most common confusion.

## `rank[]`

* Used for balancing only
* Approximation of tree height
* **Not** area
* Only increase when equal-rank roots are merged

## `size[]`

* Actual island area
* Updated on every successful union
* Used to answer 695

So for 695 with union by rank:

> **rank chooses the root, size gives the answer**

---

# Common mistakes in DSU-by-rank for 695

### 1) Using `rank` as area ❌

Wrong. Rank is not number of nodes.

### 2) Forgetting `size[]` updates ❌

If using rank-based union, you still must merge sizes.

### 3) Increasing rank in every union ❌

Only when `rank[ra] == rank[rb]`.

### 4) DSU size mismatch ❌

If `id = r * cols + c`, DSU must be `rows * cols`.

### 5) Calling `find()` on water ❌

Avoid by only unioning land cells.

### 6) Wrong mapping due to missing parentheses ❌

Use:

* `r * cols + (c + 1)`
* `(r + 1) * cols + c`

---

# Time and space complexity

Let `N = rows * cols`.

* Initialization: `O(N)`
* Union pass: `O(N * α(N))`
* Final scan: `O(N * α(N))` (or effectively `O(N)`)

Overall: **O(N * α(N)) ≈ O(N)**

Space: **O(N)** for `parent`, `rank`, `size`.

---

# Mental template for interviews (rank version)

If you need to explain it concisely:

1. Map each land cell to a DSU node
2. Use DSU with `parent`, `rank`, and `size`
3. `rank` balances unions, `size` tracks island area
4. Union adjacent lands (right/down)
5. Maximum component `size` is the answer
*/

// class Solution {

//     class DSU {
//         int[] parent;
//         int[] rank;
//         int[] size;

//         DSU(int n) {
//             parent = new int[n];
//             rank = new int[n];
//             size = new int[n];
//             Arrays.fill(parent, -1); // inactive by default (water)
//         }

//         void makeSet(int x) {
//             parent[x] = x;
//             rank[x] = 0;
//             size[x] = 1;
//         }

//         int find(int x) {
//             if (parent[x] != x) {
//                 parent[x] = find(parent[x]); // path compression
//             }
//             return parent[x];
//         }

//         boolean union(int a, int b) {
//             int ra = find(a);
//             int rb = find(b);

//             if (ra == rb) return false;

//             if (rank[ra] < rank[rb]) {
//                 parent[ra] = rb;
//                 size[rb] += size[ra];
//             } else if (rank[ra] > rank[rb]) {
//                 parent[rb] = ra;
//                 size[ra] += size[rb];
//             } else {
//                 parent[rb] = ra;
//                 size[ra] += size[rb];
//                 rank[ra]++;
//             }

//             return true;
//         }

//         int getSize(int x) {
//             int rx = find(x);
//             return size[rx];
//         }
//     }

//     public int maxAreaOfIsland(int[][] grid) {
//         if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;

//         int rows = grid.length;
//         int cols = grid[0].length;
//         DSU dsu = new DSU(rows * cols);

//         int maxArea = 0;

//         // 1) Activate land cells
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (grid[r][c] == 1) {
//                     int id = r * cols + c;
//                     dsu.makeSet(id);
//                     maxArea = 1; // at least one land exists
//                 }
//             }
//         }

//         // 2) Union adjacent land cells (right and down only)
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (grid[r][c] != 1) continue;

//                 int id = r * cols + c;

//                 // right neighbor
//                 if (c + 1 < cols && grid[r][c + 1] == 1) {
//                     int rightId = r * cols + (c + 1);
//                     dsu.union(id, rightId);
//                 }

//                 // down neighbor
//                 if (r + 1 < rows && grid[r + 1][c] == 1) {
//                     int downId = (r + 1) * cols + c;
//                     dsu.union(id, downId);
//                 }
//             }
//         }

//         // 3) Find maximum component size
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (grid[r][c] == 1) {
//                     int id = r * cols + c;
//                     maxArea = Math.max(maxArea, dsu.getSize(id));
//                 }
//             }
//         }

//         return maxArea;
//     }
// }











// Method 2: Graph DFS
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

// class Solution {
//     public int maxAreaOfIsland(int[][] grid) {
//         int rows = grid.length;
//         int cols = grid[0].length;

//         boolean[][] visited = new boolean[rows][cols];

//         int max = 0;
//         for(int i=0; i<rows; i++){
//             for(int j=0; j<cols; j++){
//                 if(grid[i][j] != 0 && visited[i][j] == false){
//                     max = Math.max(max, dfs(grid, visited, rows, cols, i, j));
//                 }
//             }
//         }

//         return max;
//     }

//     private int dfs(int[][] grid, boolean[][] visited, int rows, int cols, int i, int j){
//         if(i<0 || i>=rows || j<0 || j>=cols){
//             return 0;
//         }
//         if(visited[i][j] == true){
//             return 0;
//         }
//         if(grid[i][j] == 0){
//             return 0;
//         }

//         visited[i][j] = true;

//         int up = dfs(grid, visited, rows, cols, i-1, j);
//         int down = dfs(grid, visited, rows, cols, i+1, j);
//         int left = dfs(grid, visited, rows, cols, i, j-1);
//         int right = dfs(grid, visited, rows, cols, i, j+1);

//         return 1 + up + down + left + right;
//     }
// }







// Method 3: Iterative DFS
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








// Method 4: Graph BFS
/*
### DFS vs BFS here

* DFS with a stack dives deep before backing up.
* BFS with a queue spreads outward layer by layer.
* In this problem, they both:

  * visit exactly the same set of land cells in an island,
  * count how many cells are in that component,
  * compute the same answer.
* Runtime and memory are both O(rows * cols), because in the worst case you visit every cell once.

Pick whichever traversal the interviewer prefers, or write both and flex 😎

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

