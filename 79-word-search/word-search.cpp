// Method 1: Backtracking + DFS
/*
This is a **DFS + backtracking** problem on a 2D grid.

### Hint 1: Try every cell as the starting point

The word could begin anywhere, so loop through every cell:

```cpp
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        // try starting DFS here
    }
}
```

Your recursive function can track:

```cpp
(row, col, wordIndex)
```

where `wordIndex` is the character in `word` you're currently trying to match.

### Hint 2: Reject invalid paths early

Your DFS should stop if:

* `row` or `col` is outside the board
* `board[row][col] != word[wordIndex]`
* the cell has already been used in the current path

Something like:

```cpp
if (row < 0 || row >= rows ||
    col < 0 || col >= cols ||
    board[row][col] != word[index]) {
    return false;
}
```

### Hint 3: What is the success condition?

Once you've matched all characters in the word, you're done.

One way is:

```cpp
if (index == word.size()) {
    return true;
}
```

Just be careful about when you check this relative to accessing `word[index]`.

### Hint 4: Explore four directions

After matching the current character, try:

```text
up
down
left
right
```

So conceptually:

```cpp
dfs(row + 1, col, index + 1)
dfs(row - 1, col, index + 1)
dfs(row, col + 1, index + 1)
dfs(row, col - 1, index + 1)
```

If **any** of them succeeds, return `true`.

### Hint 5: You cannot reuse a cell

This is the important backtracking part.

Suppose you matched:

```text
board[row][col]
```

Temporarily mark that cell as visited before exploring neighbors.

A common trick is:

```cpp
char original = board[row][col];

board[row][col] = '#';

// explore neighbors

board[row][col] = original;
```

That last line is the backtracking step.

### Hint 6: Why restore it?

A cell can't be reused **within one path**, but it should be available when trying a completely different path.

For example:

```text
A B
C D
```

If one failed search uses `A`, you still need `A` available for later searches.

### Skeleton

```cpp
bool dfs(vector<vector<char>>& board,
         string& word,
         int row,
         int col,
         int index) {

    if (index == word.size()) {
        return true;
    }

    // bounds check
    // character check
    // visited check

    // mark visited

    bool found =
        dfs( down ) ||
        dfs( up ) ||
        dfs( right ) ||
        dfs( left );

    // restore cell

    return found;
}
```

The main mental model is:

```text
match current character
→ mark cell used
→ explore 4 neighbors
→ restore cell
```

The biggest trap is forgetting to **restore the board after the recursive calls**.
*/
class Solution {
private:
    bool backtrack_dfs(vector<vector<char>>& board, string& word, int row, int col, int index){
        if(index == word.size()){
            return true;
        }

        if(row < 0 || row >= board.size() || col < 0 || col >= board[0].size() || board[row][col] != word[index]){
            return false;
        }

        char original = board[row][col];
        board[row][col] = '#';

        bool found = backtrack_dfs(board, word, row + 1, col, index + 1) || backtrack_dfs(board, word, row - 1, col, index + 1) || backtrack_dfs(board, word, row, col + 1, index + 1) || backtrack_dfs(board, word, row, col - 1, index + 1);

        board[row][col] = original;

        return found;
    }

public:
    bool exist(vector<vector<char>>& board, string word) {
        int rows = board.size();
        int cols = board[0].size();

        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                if(backtrack_dfs(board, word, r, c, 0)){
                    return true;
                }
            }
        }

        return false;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna