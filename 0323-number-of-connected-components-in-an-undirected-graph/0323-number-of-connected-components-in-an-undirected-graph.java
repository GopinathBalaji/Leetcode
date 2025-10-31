// Answer to the Neetcode question: Number of Connected Components in an Undirected Graph / LeetCode question: 323. Number of Connected Components in an Undirected Graph

// Method 1: DSU / Union Find by rank
/*
### What I was doing wrong:

* You name the array `rank` (which should represent tree height) and compare it to decide which root becomes parent—**that’s union-by-rank**.
* But in the `<` / `>` branches you then do `rank[pb] += rank[pa]` (and vice versa) — **that’s union-by-size**.
* In union-by-rank you **never sum** the ranks; you **only** increment the rank by **1** when two equal-rank trees are merged.
* In union-by-size you **do** sum sizes, but then you must **initialize sizes to 1** (not 0), and you **never** do the `++` on ties—just attach smaller to larger and add sizes.

This mixup doesn’t usually break correctness (you still join sets), but it defeats the heuristic and can degrade performance; it’s also easy to get wrong comparisons later because your “rank” numbers no longer mean height **or** size consistently.

Also, this line at the top is redundant (not wrong):

```java
if (edges.length == 0) return n;
```

Your DSU already returns `n` in that case.


## Quick definitions (what `rank` means)

* We keep two arrays:

  * `parent[x]` — representative parent of the set containing `x`.
  * `rank[x]` — an **upper bound on the height** of the tree whose root is `x`.

    * Single-node set has height 0 ⇒ `rank = 0`.
    * **Only** when we merge two roots **of equal rank** do we increment the new root’s rank by 1.
    * If ranks are unequal, we attach the shorter tree under the taller tree and **do not** change rank (height doesn’t increase).

* **Path compression** flattens trees during `find`. Ranks don’t decrease afterward; they remain an **upper bound**.

---

## Example: step-by-step unions on 0..6

Start with `n = 7` nodes: `{0},{1},{2},{3},{4},{5},{6}`

```
parent = [0,1,2,3,4,5,6]
rank   = [0,0,0,0,0,0,0]
```

We'll perform these operations:

1. union(0,1)
2. union(2,3)
3. union(0,2)
4. union(4,5)
5. union(5,6)
6. union(3,6)   (this will showcase path compression + rank logic)

---

### 1) union(0,1)

* Roots: `find(0)=0` (rank 0), `find(1)=1` (rank 0) → **equal ranks**
* Make, say, `0` the parent and **increment** its rank.

```
parent = [0,0,2,3,4,5,6]
rank   = [1,0,0,0,0,0,0]
```

Tree:

```
   0 (rank 1)
  /
 1
```

Height increased from 0 to 1 because we merged two height-0 trees.

---

### 2) union(2,3)

* Roots: `2` (rank 0), `3` (rank 0) → **equal**
* Attach `3` under `2`, increment rank of `2`.

```
parent = [0,0,2,2,4,5,6]
rank   = [1,0,1,0,0,0,0]
```

Tree:

```
   2 (rank 1)
  /
 3
```

---

### 3) union(0,2)

* Roots: `0` (rank 1), `2` (rank 1) → **equal**
* Attach, say, `2` under `0`, and **increment** `rank[0]` to 2.

```
parent = [0,0,0,2,4,5,6]   // 2's parent becomes 0
rank   = [2,0,1,0,0,0,0]
```

Structure (conceptually):

```
      0 (rank 2)
     / \
    1   2
        /
       3
```

Why rank increased: we just combined **two height-1 trees**, so the new height becomes 2.

---

### 4) union(4,5)

* Roots: `4` (rank 0), `5` (rank 0) → **equal**
* Attach `5` under `4`, increment `rank[4]` to 1.

```
parent = [0,0,0,2,4,4,6]
rank   = [2,0,1,0,1,0,0]
```

Tree:

```
   4 (rank 1)
  /
 5
```

---

### 5) union(5,6)

* `find(5)` → root is `4` (since parent[5]=4).
* Roots: `4` (rank 1), `6` (rank 0) → **unequal ranks**
* Attach **shorter under taller**: `parent[6] = 4`, **no rank change**.

```
parent = [0,0,0,2,4,4,4]
rank   = [2,0,1,0,1,0,0]
```

Tree:

```
     4 (rank 1)
    / \
   5   6
```

Why no rank change: attaching a shorter tree (height 0) under a taller tree (height 1) doesn’t increase overall height; max height stays 1.

---

### 6) union(3,6)  ← shows path compression + rank choice

First get roots:

* `find(3)`:

  * parent[3] = 2, parent[2] = 0 → root is `0`
  * **Path compression** sets `parent[3] = 0` (and often `parent[2]=0` already)
* `find(6)`:

  * parent[6] = 4 → root is `4`

Now compare roots:

* RootA = `0` (rank **2**)
* RootB = `4` (rank **1**)
* **Unequal ranks** → attach **smaller under larger**: `parent[4] = 0` (no rank change)

Final arrays:

```
parent = [0,0,0,0,0,4,4]   // after path compression, future finds flatten more
rank   = [2,0,1,0,1,0,0]
```

Conceptual final structure:

```
        0 (rank 2)
      /  |   \
     1   2    4
         |   / \
         3  5   6
```

* Notice `rank[0]` stayed 2 because the other root (`4`) had smaller rank (1). Height doesn’t increase.
* Path compression will later make many nodes point directly (or almost directly) to `0`, flattening the tree for near-O(1) finds.

---

## The core rank rule (why we increment only sometimes)

Let `h(A)` be height of root A’s tree; `rank[A]` tracks an **upper bound** of `h(A)`.

* **Equal ranks:** `rank[A] == rank[B] == h`
  Merging two height-`h` trees can create a tree of height `h+1`.
  We attach one under the other and set the new root’s rank to `h+1` (i.e., `rank++`).

* **Unequal ranks:** suppose `rank[A] = h` and `rank[B] = k` with `h > k`.
  The new height is still `h` (the taller tree dominates).
  Attach `B` under `A`, and **do not** change `rank[A]`.

This keeps trees shallow and gives the near-constant amortized time.

---

## Path compression + rank = subtle point

* During `find`, we often do `parent[x] = root` for all nodes on the path (compression).
* That **reduces actual height** dramatically, but we **do not** reduce `rank`.
  This is fine because rank is only an *upper bound*, and correctness/performance don’t rely on it being tight—only monotone non-decreasing at roots.

---

## What goes wrong if you mix rank and size

* If you sometimes **sum** ranks (`rank[root] += rank[child]`) but also use rank comparisons, you’re no longer comparing heights. The heuristic becomes inconsistent.
* It still finds correct components, but can be slower because you may attach in suboptimal directions.

Stick to one:

* **Union-by-rank**: rank starts at 0, compare ranks, `rank++` **only** on equal ranks.
* **Union-by-size**: size starts at 1, compare sizes, attach smaller to larger, **add sizes**, never `++` on ties.

---

## Why all this matters (complexity)

* With **union-by-rank** + **path compression**, the amortized time per operation is `α(n)` (inverse Ackermann), which is < 5 for any realistic `n`.
* Practically: almost O(1) per `find`/`union`.

---

## Tiny sanity check

If you accidentally did:

```java
if (rank[ra] < rank[rb]) parent[ra] = rb;
else if (rank[ra] > rank[rb]) parent[rb] = ra;
else { parent[rb] = ra; /* rank[ra] stays same }  // (WRONG: forgot rank++)


Then when equal ranks merge, you **didn’t** bump the rank. Later you might attach another equal-rank tree under it, still thinking it’s not tall, which can grow the height more than necessary. That’s the key bug union-by-rank avoids.

---

### TL;DR

* **Rank = upper bound on height (at roots)**.
* **Increment rank only when merging equal ranks**; otherwise keep rank unchanged and attach the shorter under the taller.
* **Path compression** flattens trees during `find`, ranks don’t decrease.
* This combo gives you near-O(1) performance and keeps trees shallow.
*/
class Solution {
    public int countComponents(int n, int[][] edges) {
        DSU dsu = new DSU(n);
        for (int[] e : edges) dsu.union(e[0], e[1]);
        return dsu.components;
    }

    static class DSU {
        int[] parent, rank;  // rank = tree height upper bound
        int components;

        DSU(int n) {
            parent = new int[n];
            rank = new int[n];         // all zeros
            components = n;
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return;

            if (rank[ra] < rank[rb]) {
                parent[ra] = rb;
            } else if (rank[ra] > rank[rb]) {
                parent[rb] = ra;
            } else {
                parent[rb] = ra;
                rank[ra]++;            // only increment on equal ranks
            }
            components--;
        }
    }
}





// Method 2: DSU / Union Find by size
/*
In union-by-size you do sum sizes, but then you must 
initialize sizes to 1 (not 0), and you never do the ++ on 
ties—just attach smaller to larger and add sizes.
*/
// class Solution {
//     public int countComponents(int n, int[][] edges) {
//         DSU dsu = new DSU(n);
//         for (int[] e : edges) dsu.union(e[0], e[1]);
//         return dsu.components;
//     }

//     static class DSU {
//         int[] parent, size;  // size = subtree size
//         int components;

//         DSU(int n) {
//             parent = new int[n];
//             size = new int[n];
//             components = n;
//             for (int i = 0; i < n; i++) {
//                 parent[i] = i;
//                 size[i] = 1;          // IMPORTANT: start at 1
//             }
//         }

//         int find(int x) {
//             if (parent[x] != x) parent[x] = find(parent[x]);
//             return parent[x];
//         }

//         void union(int a, int b) {
//             int ra = find(a), rb = find(b);
//             if (ra == rb) return;

//             if (size[ra] < size[rb]) {
//                 parent[ra] = rb;
//                 size[rb] += size[ra];
//             } else {
//                 parent[rb] = ra;
//                 size[ra] += size[rb];
//             }
//             components--;
//         }
//     }
// }



