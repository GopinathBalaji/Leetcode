/*
// Definition for a QuadTree node.
class Node {
    public boolean val;
    public boolean isLeaf;
    public Node topLeft;
    public Node topRight;
    public Node bottomLeft;
    public Node bottomRight;

    
    public Node() {
        this.val = false;
        this.isLeaf = false;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }
    
    public Node(boolean val, boolean isLeaf) {
        this.val = val;
        this.isLeaf = isLeaf;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }
    
    public Node(boolean val, boolean isLeaf, Node topLeft, Node topRight, Node bottomLeft, Node bottomRight) {
        this.val = val;
        this.isLeaf = isLeaf;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }
}
*/

// Recurse on each sub-grid by dividing the gird into 4 parts
/*

## 1 · Why a quad‑tree?

A **quad‑tree** compresses a 2‑D binary grid by **recursively merging uniform squares**:

```
┌────────────── root ──────────────┐
│     if whole grid = all 0/1      │  → leaf  
│ otherwise split into 4 quadrants │  → internal node
└──────────────────────────────────┘
```

Because each internal node represents a square and has exactly **four** children (TL, TR, BL, BR), the tree depth is `log₄ n` in the best case and at most `log₂ n` (because each division halves the side length).

---

## 2 · Top‑down recursive algorithm

### Signature

```
Node build(r, c, size)
```

*`r, c` = top‑left corner of current square; `size` = side length.*

### Steps per call

1. **Check uniformity**

   * Scan every cell in `[r … r+size‑1] × [c … c+size‑1]`.
   * If they’re all identical (`0` or `1`) → **leaf**.
2. **Otherwise**

   * `half = size / 2`
   * Recurse on four quadrants

     ```
     TL : (r        , c        , half)
     TR : (r        , c+half   , half)
     BL : (r+half   , c        , half)
     BR : (r+half   , c+half   , half)
     ```
   * Return an **internal** node whose children are those four nodes.

*Base case:* when `size == 1` the square is necessarily uniform.

---

## 3 · Walk‑through on a 4 × 4 example

```
Grid indices (r,c):
  0 1 2 3
0 1 1 0 0
1 1 1 0 0
2 0 0 0 0
3 0 0 0 1
```

### Call 1 – the whole 4×4

*Uniform?* No (mix of 0 & 1)
→ Split (`half = 2`).

```
Quadrants (size = 2)
TL r=0,c=0  «1 1» «1 1»            → all 1  → leaf(val=1)
TR r=0,c=2  «0 0» «0 0»            → all 0  → leaf(val=0)
BL r=2,c=0  «0 0» «0 0»            → all 0  → leaf(val=0)
BR r=2,c=2  «0 0» «0 1»            → mixed  → recurse
```

### Call 2 – BR quadrant (r = 2, c = 2, size = 2)

*Uniform?* No → split again (`half = 1`).

```
Individual cells (size = 1)
TL r=2,c=2  → 0  → leaf(0)
TR r=2,c=3  → 0  → leaf(0)
BL r=3,c=2  → 0  → leaf(0)
BR r=3,c=3  → 1  → leaf(1)
```

Return an **internal** node for this 2×2.

### Assemble upward

The original root now has:

```
root.isLeaf = false
root.topLeft      = leaf(1)
root.topRight     = leaf(0)
root.bottomLeft   = leaf(0)
root.bottomRight  = (internal containing 3×0‑leaves + 1×1‑leaf)
```

That’s the entire quad‑tree.

---

## 4 · Why the algorithm is optimal

| Property        | Reason                                                                                          |
| --------------- | ----------------------------------------------------------------------------------------------- |
| **Correctness** | Every square is either all‑same → leaf, or split until it becomes all‑same.                     |
| **Time**        | Each cell is examined at most once per tree level. Worst‑case `O(n² log n)`, best‑case `O(n²)`. |
| **Space**       | At most one recursion frame per level → `O(log n)` stack; heap stores the tree itself.          |

---

## 5 · Key take‑aways & common pitfalls

| Pitfall                                  | Fix                                                                           |
| ---------------------------------------- | ----------------------------------------------------------------------------- |
| Forgetting to pass `grid` into recursion | `build(grid, r, c, size)`                                                     |
| Off‑by‑one errors in loops               | Loop `i < r + size`, not `≤`                                                  |
| Continuing scan after non‑uniform found  | Break both loops to avoid wasted work                                         |
| Internal node’s `val` field              | It’s ignored when `isLeaf=false`; any boolean is fine (`false` by convention) |

---

### Mental model to remember

> *“At each square: if all bits match → stop & mark leaf; else quarter the square and repeat on the four quarters.”*

Master this pattern and you’ll recognize it in many 2‑D divide‑and‑conquer problems (image compression, game maps, etc.).

*/

/*****************************************************
 *  Solution: Top‑down Divide‑and‑Conquer (Quad Tree) *
 *****************************************************/
class Solution {

    /* Entry point – builds a quad‑tree for the whole grid */
    public Node construct(int[][] grid) {
        // Entire grid starts at (0,0) with side length = grid.length (n is a power of 2)
        return buildQuadTree(grid, 0, 0, grid.length);
    }

    /**
     * Recursively builds a quad‑tree representing the sub‑square
     * whose top‑left corner = (startRow, startCol) and side length = size.
     *
     * @param grid     original binary matrix (0 / 1)
     * @param startRow top‑left row index of current square
     * @param startCol top‑left col index of current square
     * @param size     side length of current square
     * @return         root Node that represents this square
     */
    private Node buildQuadTree(int[][] grid, int startRow, int startCol, int size) {

        /* -----------------------------------------------------------------
           Step 1: Check if this square is UNIFORM (all 0s or all 1s).
           ----------------------------------------------------------------- */

        int firstVal   = grid[startRow][startCol];  // value of the very first cell (0 or 1)
        boolean uniform = true;                     // assume uniform until proven otherwise

        // Scan every cell inside the current square.
        for (int i = startRow; i < startRow + size && uniform; i++) {
            for (int j = startCol; j < startCol + size; j++) {
                if (grid[i][j] != firstVal) {      // mixed values found!
                    uniform = false;
                    break;                         // break inner loop; outer loop guarded by 'uniform'
                }
            }
        }

        /* -----------------------------------------------------------------
           Step 2: If the square is uniform, create a LEAF node and return.
           ----------------------------------------------------------------- */

        if (uniform) {
            /*
             * new Node(boolean val, boolean isLeaf)
             * val should be true for 1, false for 0.
             */
            return new Node(firstVal == 1, /*isLeaf=*/true);
        }

        /* -----------------------------------------------------------------
           Step 3: Otherwise, divide the square into 4 equal quadrants
                   and build each quadrant recursively.
           ----------------------------------------------------------------- */

        int half = size / 2;   // side length of each quadrant

        // Top‑Left quadrant
        Node topLeft = buildQuadTree(grid,
                                     startRow,
                                     startCol,
                                     half);

        // Top‑Right quadrant
        Node topRight = buildQuadTree(grid,
                                      startRow,
                                      startCol + half,
                                      half);

        // Bottom‑Left quadrant
        Node bottomLeft = buildQuadTree(grid,
                                        startRow + half,
                                        startCol,
                                        half);

        // Bottom‑Right quadrant
        Node bottomRight = buildQuadTree(grid,
                                         startRow + half,
                                         startCol + half,
                                         half);

        /* -----------------------------------------------------------------
           Step 4: Create an INTERNAL node whose children are the 4 quadrants.
                   For internal nodes, 'val' may be anything (convention = false).
           ----------------------------------------------------------------- */
        return new Node(/*val=*/false,
                        /*isLeaf=*/false,
                        topLeft, topRight,
                        bottomLeft, bottomRight);
    }
}