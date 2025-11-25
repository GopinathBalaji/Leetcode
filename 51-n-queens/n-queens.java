// Method 1: Backtracking
/*
### Hint 1 – What are we actually choosing?

You need to place **n queens on an n×n board** so that:

* No two queens share the same **row**
* No two queens share the same **column**
* No two queens share the same **diagonal**

You don’t need to “search the board randomly.”
Instead, think:

> *At row r, which column c should I put the queen in?*

So your choices are **column indices** per row.

---

### Hint 2 – Backtracking state

Define a recursive function something like:

```text
dfs(row, currentPlacement, answers)
```

Where:

* `row` → which row you’re currently trying to place a queen in
* `currentPlacement` → where you’ve already put queens in previous rows (e.g., `col[rowIndex] = colOfQueen`)
* `answers` → list of all full valid boards found

At each `row`, you try **all columns 0..n-1** that are safe.

---

### Hint 3 – How to detect conflicts fast

For a given `(row, col)`:

* **Column conflict**: if any previous row already used this `col`.
* **Main diagonal conflict ( \ )**: all cells on same diagonal share `(row - col)` value.
* **Anti-diagonal conflict ( / )**: all cells on same diagonal share `(row + col)` value.

So you can maintain 3 sets/boolean arrays:

* `colsUsed[c] = true` if some queen is already in column `c`
* `diagUsed[row - col + (n-1)] = true`  (shift by `n-1` to make index non-negative)
* `antiDiagUsed[row + col] = true`

Then to check if you can place at `(row, col)`:

```text
if colsUsed[col] or diagUsed[row-col+shift] or antiDiagUsed[row+col]:
    cannot place
else:
    can place
```

---

### Hint 4 – Recurrence structure

At each `row`:

1. If `row == n`:

   * You’ve placed queens in all `n` rows → build a board from `currentPlacement` and add to answer.
   * Return.

2. Otherwise:

   * Loop `col` from `0` to `n-1`:

     * If `(row, col)` is **safe** w.r.t. `colsUsed`, `diagUsed`, `antiDiagUsed`:

       * Mark them as used.
       * Record `col` as queen position for this `row`.
       * Recurse to `row + 1`.
       * Backtrack (undo marks and placement).

Classic backtracking pattern:

> choose → recurse → undo

---

### Hint 5 – How to represent a board for the result

LeetCode wants each solution as a `List<String>` where:

* Each string = one row with:

  * `'Q'` where the queen is placed,
  * `'.'` elsewhere.

So if for `n = 4` you have placement: `cols = [1, 3, 0, 2]` (meaning:

* row 0 → col 1
* row 1 → col 3
* row 2 → col 0
* row 3 → col 2

Then you build:

```text
row 0: . Q . .
row 1: . . . Q
row 2: Q . . .
row 3: . . Q .
```

As `[".Q..", "...Q", "Q...", "..Q."]`.

---

### Hint 6 – Mini walkthrough for n = 4 (conceptual)

You try to place queens row by row:

* `row = 0`:

  * try `col = 0,1,2,3` (all initially free).
  * Let’s say you pick `col = 1`.

* `row = 1`:

  * Try `col = 0..3`, but skip any conflicting:

    * column 1 is taken;
    * diagonals that clash with `(0,1)`:

      * main diag: `row-col = 0-1 = -1`
      * anti diag: `row+col = 0+1 = 1`
  * Suppose only `col = 3` is safe; place queen at `(1,3)`.

* `row = 2`:

  * Now avoid:

    * columns {1,3},
    * main diags {0-1, 1-3} = {-1, -2},
    * anti diags {1,4}.
  * Maybe `(2,0)` is safe; place there.

* `row = 3`:

  * Repeat check; maybe only `(3,2)` is safe.
  * You place queen at `(3,2)`.
  * `row == n` → record this as one solution.

Then you backtrack and try different columns earlier (e.g., different `col` at `row 3`, then at `row 2`, etc.), which eventually produces all distinct solutions for `n = 4`.

---

### Hint 7 – Complexity intuition

* You’re exploring a search tree where:

  * Depth = `n` (one queen per row),
  * Branching factor ≤ `n` (number of columns).
* Backtracking + pruning (via used columns/diagonals) greatly cuts down illegal branches.
* Time is roughly `O(number_of_solutions * n)` to build boards, plus the exploration overhead.


NOTE: n-1 is used as offset in the mainDiagIndex, it has no other significance.
*/

class Solution {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> ans = new ArrayList<>();

        // columns used
        boolean[] colsUsed = new boolean[n];

        // main diagonals (\): indexed by (row - col + n - 1)
        boolean[] diagUsed = new boolean[2 * n - 1];

        // anti-diagonals (/): indexed by (row + col)
        boolean[] antiDiagUsed = new boolean[2 * n - 1];

        // currentPlacement[row] = col where the queen is in that row
        int[] currentPlacement = new int[n];
        Arrays.fill(currentPlacement, -1);

        backtrack(0, n, currentPlacement, colsUsed, diagUsed, antiDiagUsed, ans);

        return ans;
    }

    private void backtrack(int row, int n,
                           int[] currentPlacement,
                           boolean[] colsUsed,
                           boolean[] diagUsed,
                           boolean[] antiDiagUsed,
                           List<List<String>> ans) {
        // If we've placed queens in all rows, build a board
        if (row == n) {
            buildBoard(currentPlacement, ans, n);
            return;
        }

        for (int col = 0; col < n; col++) {
            int mainDiagIndex = row - col + n - 1; // range [0, 2n-2]
            int antiDiagIndex = row + col;         // range [0, 2n-2]

            if (!colsUsed[col] && !diagUsed[mainDiagIndex] && !antiDiagUsed[antiDiagIndex]) {
                // Place queen
                colsUsed[col] = true;
                diagUsed[mainDiagIndex] = true;
                antiDiagUsed[antiDiagIndex] = true;
                currentPlacement[row] = col;

                // Recurse to next row
                backtrack(row + 1, n, currentPlacement, colsUsed, diagUsed, antiDiagUsed, ans);

                // Backtrack
                colsUsed[col] = false;
                diagUsed[mainDiagIndex] = false;
                antiDiagUsed[antiDiagIndex] = false;
                currentPlacement[row] = -1;
            }
        }
    }

    private void buildBoard(int[] currentPlacement, List<List<String>> ans, int n) {
        List<String> board = new ArrayList<>();

        for (int row = 0; row < n; row++) {
            char[] rowChars = new char[n];
            Arrays.fill(rowChars, '.');

            int col = currentPlacement[row];
            if (col >= 0) {
                rowChars[col] = 'Q';
            }

            board.add(new String(rowChars));
        }

        ans.add(board);
    }
}
