// Method 1: Backtracking
/*
This is almost the same backtracking as **51. N-Queens**, except you only need the **count** of valid boards, not the boards themselves.

The recursive state can still be:

```cpp
backtrack(row)
```

At each row, try every column and reject positions that conflict with an existing queen.

You can reuse the same three constraint sets:

```cpp
unordered_set<int> cols;
unordered_set<int> diag1; // row - col
unordered_set<int> diag2; // row + col
```

A position is invalid if:

```cpp
cols.count(col) ||
diag1.count(row - col) ||
diag2.count(row + col)
```

The big difference from LeetCode 51 is the base case. Instead of saving a board when:

```cpp
row == n
```

you should increase a counter:

```cpp
count++;
return;
```

That means you don’t even need a `board` at all.

Your backtracking pattern becomes:

```cpp
// choose
cols.insert(col);
diag1.insert(row - col);
diag2.insert(row + col);

// recurse
backtrack(row + 1, n);

// undo
cols.erase(col);
diag1.erase(row - col);
diag2.erase(row + col);
```

A skeleton:

```cpp
class Solution {
private:
    unordered_set<int> cols;
    unordered_set<int> diag1;
    unordered_set<int> diag2;

    int count = 0;

    void backtrack(int row, int n) {
        if (row == n) {
            // found one valid arrangement
            return;
        }

        for (int col = 0; col < n; col++) {

            // check constraints

            // mark column + diagonals

            // recurse to next row

            // undo
        }
    }

public:
    int totalNQueens(int n) {
        backtrack(0, n);
        return count;
    }
};
```

The main transition from **51 → 52** is simply:

```text
N-Queens:
store each valid board

N-Queens II:
just count each valid board
```

So if your solution for 51 already works, you can simplify it quite a bit for 52.
*/
class Solution {
private:
    unordered_set<int> cols;
    unordered_set<int> diag1;
    unordered_set<int> diag2;

    int count = 0;

    void backtrack(int row, int n){
        if(row == n){
            count++;
            return;
        }

        for(int col=0; col<n; col++){
            if(cols.count(col) || diag1.count(row - col) || diag2.count(row + col)){
                continue;
            }

            cols.insert(col);
            diag1.insert(row - col);
            diag2.insert(row + col);

            backtrack(row + 1, n);

            cols.erase(col);
            diag1.erase(row - col);
            diag2.erase(row + col);
        }
    }

public:
    int totalNQueens(int n) {
        backtrack(0, n);

        return count;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna