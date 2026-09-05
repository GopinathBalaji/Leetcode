// Method 1: Reverse DFS
/*
This is another **reverse-thinking grid DFS/BFS** problem, similar in spirit to Pacific Atlantic.

The key observation is:

> Any `'O'` connected to the border can **never** be surrounded.

So instead of finding surrounded regions directly, find the `'O'` cells that are **safe**.

### Hint 1: Start from the border

Check all cells on:

* top row
* bottom row
* left column
* right column

Whenever you find `'O'`, run DFS/BFS from it.

Those border-connected `'O'` cells should not be flipped.

### Hint 2: Mark safe cells temporarily

During DFS, change safe `'O'` cells to something like:

```cpp
'#'
```

Then recurse in 4 directions:

```text
up
down
left
right
```

Only continue into other `'O'` cells.

### Hint 3: After marking border-connected regions

Now scan the whole board.

There are only two important cases:

```text
'O'  → surrounded, so flip to 'X'
'#'  → safe, restore back to 'O'
```

So conceptually:

```cpp
if (board[r][c] == 'O') {
    board[r][c] = 'X';
}
else if (board[r][c] == '#') {
    board[r][c] = 'O';
}
```

### Hint 4: DFS skeleton

```cpp
void dfs(vector<vector<char>>& board, int r, int c) {
    if (r < 0 || r >= board.size() ||
        c < 0 || c >= board[0].size() ||
        board[r][c] != 'O') {
        return;
    }

    board[r][c] = '#';

    dfs(board, r + 1, c);
    dfs(board, r - 1, c);
    dfs(board, r, c + 1);
    dfs(board, r, c - 1);
}
```

### Hint 5: Overall strategy

```text
1. DFS from every border 'O'
2. Mark all border-connected 'O' as safe
3. Scan entire board:
      remaining O → X
      safe marker → O
```

For example:

```text
X X X X
X O O X
X X O X
X O X X
```

The bottom-left `'O'` touches the border, so it survives.

The middle region does not touch the border, so it gets flipped.

The main mental model is:

```text
border-connected O = safe
all other O = surrounded
```
*/
class Solution {
private:
    void dfs(vector<vector<char>>& board, int row, int col){
        if(row < 0 || row >= board.size() || col < 0 || col >= board[0].size()){
            return;
        }

        if(board[row][col] != 'O'){
            return;
        }

        board[row][col] = '#';

        dfs(board, row + 1, col);
        dfs(board, row - 1, col);
        dfs(board, row, col + 1);
        dfs(board, row, col - 1);
    }

public:
    void solve(vector<vector<char>>& board) {
        int rows = board.size();
        int cols = board[0].size();

        for(int row = 0; row < rows; row++){
            if(board[row][0] == 'O'){
                dfs(board, row, 0);
            }
        }

        for(int row = 0; row < rows; row++){
            if(board[row][cols - 1] == 'O'){
                dfs(board, row, cols - 1);
            }
        }

        for(int col = 0; col < cols; col++){
            if(board[0][col] == 'O'){
                dfs(board, 0, col);
            }
        }

        for(int col = 0; col < cols; col++){
            if(board[rows - 1][col] == 'O'){
                dfs(board, rows - 1, col);
            }
        }

        for(int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++){
                if(board[row][col] == 'O'){
                    board[row][col] = 'X';
                }else if(board[row][col] == '#'){
                    board[row][col] = 'O';
                }
            }
        }

    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna