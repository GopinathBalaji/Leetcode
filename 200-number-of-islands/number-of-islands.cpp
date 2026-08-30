// Method 1: DFS by counting connected components
/*
This is a classic **DFS/BFS on a grid** problem.

### Hint 1: What counts as one island?

Every time you find a land cell:

```cpp
grid[r][c] == '1'
```

that has not been visited yet, you’ve discovered a **new island**.

So:

```text
find new unvisited land
→ increment islands
→ visit all connected land from there
```

### Hint 2: Use DFS to “erase” an island

Once you start DFS from a land cell, visit:

```text
up
down
left
right
```

and keep going through connected `'1'` cells.

A convenient trick is to mark visited land by changing it to:

```cpp
'0'
```

Then you don’t need a separate `visited` matrix.

### Hint 3: DFS base case

Your DFS should stop if:

```cpp
row < 0
row >= rows
col < 0
col >= cols
```

or if the cell is not land:

```cpp
grid[row][col] != '1'
```

So conceptually:

```cpp
if (out of bounds || grid[row][col] != '1') {
    return;
}
```

### Hint 4: Mark before recursing

Once you know the current cell is land:

```cpp
grid[row][col] = '0';
```

Then recurse in four directions:

```cpp
dfs(row + 1, col);
dfs(row - 1, col);
dfs(row, col + 1);
dfs(row, col - 1);
```

### Hint 5: Main loop

Scan every cell:

```cpp
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {

        if (grid[r][c] == '1') {
            // found a new island
        }
    }
}
```

When you find `'1'`:

```text
islands++
DFS from that cell
```

The DFS will mark the entire connected island as visited, so you won’t count it again later.

### Skeleton

```cpp
void dfs(vector<vector<char>>& grid, int row, int col) {

    // bounds check
    // check whether current cell is land

    // mark visited

    // explore 4 directions
}

int numIslands(vector<vector<char>>& grid) {
    int islands = 0;

    for (...) {
        for (...) {
            if (grid[r][c] == '1') {
                islands++;

                // dfs
            }
        }
    }

    return islands;
}
```

The mental model is:

```text
Every DFS call wipes out exactly one island.

Therefore:
number of times you start DFS = number of islands
```

This is very similar to the DFS approach you used for **463. Island Perimeter**, except here you count connected components instead of boundary edges.
*/
class Solution {
private:
    int count = 0;

    void dfs(vector<vector<char>>& grid, int row, int col){
        if(row < 0 || row >= grid.size() || col < 0 || col >= grid[0].size()){
            return;
        }

        if(grid[row][col] != '1'){
            return;
        }

        grid[row][col] = '0';

        dfs(grid, row + 1, col);
        dfs(grid, row - 1, col);
        dfs(grid, row, col - 1);
        dfs(grid, row, col + 1);


    }

public:
    int numIslands(vector<vector<char>>& grid) {
        int rows = grid.size();
        int cols = grid[0].size();

        for(int row=0; row<rows; row++){
            for(int col=0; col<cols; col++){
                if(grid[row][col] == '1'){
                    count++;
                    dfs(grid, row, col);
                }
            }
        }

        return count;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna