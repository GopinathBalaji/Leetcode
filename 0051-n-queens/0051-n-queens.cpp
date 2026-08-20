// Method 1: Backtrack after invalid position checking
/*
This is another **backtracking** problem. The main idea is: place exactly one queen in each row, and only choose columns that are safe.

### Hint 1: Recurse row by row

Instead of trying every square freely, make your recursive state:

```cpp
backtrack(row)
```

At each `row`, try placing the queen in every column:

```cpp
for (int col = 0; col < n; col++) {
    // can we safely place a queen at (row, col)?
}
```

Once you place one queen in a row, recurse to:

```cpp
row + 1
```

### Hint 2: What makes a position invalid?

A queen at:

```text
(row, col)
```

conflicts with another queen if they share:

* the same column
* the same `\` diagonal
* the same `/` diagonal

You don't need to check rows, because you're placing exactly one queen per row.

### Hint 3: Diagonals have useful formulas

For a cell `(row, col)`:

```text
\ diagonal  → row - col
/ diagonal  → row + col
```

Cells on the same diagonal have the same value.

For example:

```text
(0,0), (1,1), (2,2)
```

all have:

```text
row - col = 0
```

So you can track:

```cpp
unordered_set<int> cols;
unordered_set<int> diag1; // row - col
unordered_set<int> diag2; // row + col
```

### Hint 4: Check before placing

A position is invalid if:

```cpp
cols.count(col) ||
diag1.count(row - col) ||
diag2.count(row + col)
```

Otherwise, you may place the queen.

### Hint 5: Choose → recurse → undo

The backtracking step should look conceptually like:

```cpp
board[row][col] = 'Q';

cols.insert(col);
diag1.insert(row - col);
diag2.insert(row + col);

// recurse to row + 1

board[row][col] = '.';

cols.erase(col);
diag1.erase(row - col);
diag2.erase(row + col);
```

This is the same pattern you've been using:

```text
choose
→ mark constraints
→ recurse
→ undo constraints
```

### Hint 6: Base case

If:

```cpp
row == n
```

then you've successfully placed `n` queens.

At that point:

```cpp
result.push_back(board);
```

### Skeleton

```cpp
void backtrack(int row,
               int n,
               vector<string>& board,
               vector<vector<string>>& result) {

    if (row == n) {
        // save board
        return;
    }

    for (int col = 0; col < n; col++) {

        // check column + both diagonals

        // place queen

        // recurse to next row

        // remove queen
    }
}
```

The key mental model is:

```text
one row at a time
→ try every column
→ reject attacked positions
→ place queen
→ move to next row
→ undo
```

The most important trick in this problem is recognizing the diagonal identities:

```text
row - col
row + col
```

Those let you test whether a queen placement is safe in `O(1)` average time instead of scanning the board.
*/
class Solution {
private:
    unordered_set<int> cols;
    unordered_set<int> diag1;
    unordered_set<int> diag2;

    void backtrack(int row, int n, vector<string>& board, vector<vector<string>>& result){
        if(row == n){
            result.push_back(board);
            return;
        }

        for(int col=0; col<n; col++){
            if(cols.count(col) || diag1.count(row - col) || diag2.count(row + col)){
                continue;
            }

            board[row][col] = 'Q';

            cols.insert(col);
            diag1.insert(row - col);
            diag2.insert(row + col);

            backtrack(row + 1, n, board, result);

            board[row][col] = '.';

            cols.erase(col);
            diag1.erase(row - col);
            diag2.erase(row + col);
        }
    }


public:
    vector<vector<string>> solveNQueens(int n) {
        vector<string> board(n, string(n, '.'));
        vector<vector<string>> result;

        backtrack(0, n, board, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna