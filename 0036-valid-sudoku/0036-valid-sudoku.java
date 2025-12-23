// Method 1: Brute-force single-pass O(n^2) approach (O(3 * n^2) == O(n^2) space)
/*
## Detailed one-pass hints

### 1) What you iterate over

You’ll do the standard double loop:

* `r` from `0..8`
* `c` from `0..8`

At each cell:

* If `board[r][c] == '.'` → **skip**
* Else it’s a digit `'1'..'9'`

### 2) Convert the char digit to an index

You want a stable “digit id” to store in your seen structures.

* Let `ch = board[r][c]`
* Convert to int: `d = ch - '0'` (so `'1'` → 1, … `'9'` → 9)

Why 1..9 is nice: you can use arrays sized `[10]` and ignore index 0.

### 3) Compute which 3×3 box this cell belongs to

This is the most common place people mess up.

* `box = (r / 3) * 3 + (c / 3)`

Sanity checks:

* `(0,0)` → box 0
* `(0,8)` → box 2
* `(4,4)` → box 4
* `(8,8)` → box 8

So `box` always in `0..8`.

### 4) Your “seen” structures

You need *three* places to record “have I seen digit d here before?”

Two clean choices:

**Option A (fast + simple): boolean tables**

* `rows[9][10]`
* `cols[9][10]`
* `boxes[9][10]`

Meaning:

* `rows[r][d] == true` means digit `d` already appeared in row `r`.

**Option B: arrays of sets**

* `Set<Character>[] rows = new HashSet[9]` etc.
  Works fine, just slightly more overhead.

### 5) The exact check logic per cell

At a filled cell `(r,c)` with digit `d` in box `b`:

Check *before* marking:

* If `rows[r][d]` is already true → duplicate in that row → return false
* If `cols[c][d]` is already true → duplicate in that col → return false
* If `boxes[b][d]` is already true → duplicate in that box → return false

Only if all are false, then “claim” it:

* `rows[r][d] = true`
* `cols[c][d] = true`
* `boxes[b][d] = true`

This lets you finish in one pass.

### 6) A small walk-through example (mental model)

Suppose at `(r=0,c=1)` you see `'5'`:

* `d = 5`
* `b = (0/3)*3 + (1/3) = 0`
* If any of `rows[0][5]`, `cols[1][5]`, `boxes[0][5]` is already true → invalid
* Else mark all three as true and continue

### 7) Common pitfalls to avoid

* **Forgetting to skip '.'**
* **Using `d = ch - '1'` but still allocating `[10]` and mixing 0-based/1-based**
* **Wrong box formula** (must be `(r/3)*3 + (c/3)`)
* **Marking before checking** (always check first)
*/
class Solution {
    public boolean isValidSudoku(char[][] board) {
        int numRows = board.length;
        int numCols = board[0].length;

        boolean[][] rows = new boolean[9][10];
        boolean[][] cols = new boolean[9][10];
        boolean[][] boxes = new boolean[9][10];

        for(int r=0; r<numRows; r++){
            for(int c=0; c<numCols; c++){
                if(board[r][c] == '.'){
                    continue;
                }
                char ch = board[r][c];
                int d = ch - '0';
                int box = (r / 3) * 3 + (c / 3);

                if(rows[r][d] == true){
                    return false;
                }else{
                    rows[r][d] = true;
                }

                if(cols[c][d] == true){
                    return false;
                }else{
                    cols[c][d] = true;
                }

                if(boxes[box][d] == true){
                    return false;
                }else{
                    boxes[box][d] = true;
                }
            }
        }

        return true;
    }
}






// Method 2: Constant space approach using single HashSet (O(n^2) time and O(n) space)
/*
## Core idea (single `HashSet`)

As you scan each filled cell `(r, c)` with value `d`, you create **three “signatures”** that represent:

1. “digit `d` has appeared in row `r`”
2. “digit `d` has appeared in col `c`”
3. “digit `d` has appeared in 3×3 box `b`”

You store these signatures in one HashSet.
If you ever try to add a signature that already exists → you found a duplicate → invalid.

### Why it works

A Sudoku board is valid iff **no digit repeats** in:

* any row
* any column
* any 3×3 box

So you track *exactly those three constraints*.

---

## How to compute the 3×3 box index

For cell `(r, c)`:

```java
int box = (r / 3) * 3 + (c / 3);
```

This maps the 9 boxes to indices 0..8.

Examples:

* (0,0) → 0
* (0,8) → 2
* (4,4) → 4
* (8,8) → 8

### Notes

* `seen.add(x)` returns `false` if `x` was already in the set.
* The `"#"` separator just prevents accidental ambiguity (good practice).

---

## Thorough walkthrough with an example

Consider (a partial board view):

Row 0:
`[ '5', '3', '.', '.', '7', '.', '.', '.', '.' ]`

### Step 1: Cell (0,0) = '5'

* `r=0, c=0, ch='5'`
* `box = (0/3)*3 + (0/3) = 0`

Keys:

* rowKey = `"r0#5"`
* colKey = `"c0#5"`
* boxKey = `"b0#5"`

`seen` is empty initially, so all 3 adds succeed.

Now `seen` contains:

* r0#5
* c0#5
* b0#5

### Step 2: Cell (0,1) = '3'

* `r=0, c=1, ch='3'`
* `box = (0/3)*3 + (1/3) = 0`

Keys:

* `"r0#3"`
* `"c1#3"`
* `"b0#3"`

All are new → add succeeds.

### Step 3: Cell (0,4) = '7'

* `r=0, c=4, ch='7'`
* `box = (0/3)*3 + (4/3) = 1`

Keys:

* `"r0#7"`
* `"c4#7"`
* `"b1#7"`

Add succeeds.

So far so good: no duplicates in row 0, col 0/1/4, or boxes 0/1.

---

## How it detects an invalid case

### Case A: duplicate in a row

Suppose later you encounter another `'5'` in row 0 at `(0,6)`.

* `r=0, c=6, ch='5'`
* rowKey = `"r0#5"`

But `"r0#5"` is already in `seen` from `(0,0)`.
So `seen.add("r0#5")` returns false → immediately return `false`.

### Case B: duplicate in a column

If you see `'3'` again in column 1 somewhere like `(4,1)`:

* colKey = `"c1#3"` already exists → return false.

### Case C: duplicate in a 3×3 box

If you see `'7'` again inside the same box 1:

Any cell with `(r/3)*3 + (c/3) == 1` and value `'7'` would create `"b1#7"` again → return false.

---

## Complexity

* Time: **O(81)** = constant (scan the board once)
* Space: up to **3 × (#filled cells)** keys, worst-case `3*81 = 243` → still constant.
*/

// class Solution {
//     public boolean isValidSudoku(char[][] board) {
//         Set<String> seen = new HashSet<>();

//         for (int r = 0; r < 9; r++) {
//             for (int c = 0; c < 9; c++) {
//                 char ch = board[r][c];
//                 if (ch == '.') continue; // ignore empty cells

//                 int box = (r / 3) * 3 + (c / 3);

//                 // Build 3 unique keys for row, col, and box constraints.
//                 String rowKey = "r" + r + "#" + ch;   // digit ch in row r
//                 String colKey = "c" + c + "#" + ch;   // digit ch in col c
//                 String boxKey = "b" + box + "#" + ch; // digit ch in box box

//                 // If any key already exists, we have a duplicate -> invalid.
//                 if (!seen.add(rowKey)) return false;
//                 if (!seen.add(colKey)) return false;
//                 if (!seen.add(boxKey)) return false;
//             }
//         }
//         return true;
//     }
// }
