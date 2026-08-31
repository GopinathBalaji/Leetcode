// Method 1: DFS method
/*
This is very similar to **200. Number of Islands**, except instead of counting how many islands there are, you want the **size of the largest one**.

### Hint 1: Let DFS return the area

For every land cell:

```cpp
grid[r][c] == 1
```

your DFS can return:

> how many connected land cells belong to this island?

So your recursive function could be:

```cpp
int dfs(vector<vector<int>>& grid, int row, int col)
```

### Hint 2: Base case

If you go out of bounds or hit water:

```cpp
if (row < 0 || row >= grid.size() ||
    col < 0 || col >= grid[0].size() ||
    grid[row][col] == 0) {
    return 0;
}
```

### Hint 3: Mark the cell visited

Once you reach land:

```cpp
grid[row][col] = 0;
```

This prevents counting the same cell twice.

Then the current cell contributes:

```text
1
```

to the area.

### Hint 4: Add the four neighboring areas

Think:

```cpp
int area = 1;
```

then add:

```cpp
area += dfs(grid, row + 1, col);
area += dfs(grid, row - 1, col);
area += dfs(grid, row, col + 1);
area += dfs(grid, row, col - 1);
```

Finally return:

```cpp
return area;
```

### Hint 5: Scan the entire grid

Just like Number of Islands:

```cpp
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        if (grid[r][c] == 1) {
            // compute this island's area
            // update maximum
        }
    }
}
```

You want something like:

```cpp
maxArea = max(maxArea, dfs(grid, r, c));
```

### Skeleton

```cpp
class Solution {
private:
    int dfs(vector<vector<int>>& grid, int row, int col) {

        // invalid / water → return 0

        // mark visited

        int area = 1;

        // add area from 4 directions

        return area;
    }

public:
    int maxAreaOfIsland(vector<vector<int>>& grid) {
        int maxArea = 0;

        // scan every cell

        return maxArea;
    }
};
```

The key difference from LeetCode 200 is:

```text
200:
each DFS finds one island
→ count += 1

695:
each DFS returns the number of cells in one island
→ maxArea = max(maxArea, islandArea)
```
*/
class Solution {
private:
    int dfs(vector<vector<int>>& grid, int area, int row, int col){
        if(row < 0 || row >= grid.size() || col < 0 || col >= grid[0].size()){
            return 0;
        }

        if(grid[row][col] != 1){
            return 0;
        }

        grid[row][col] = 0;

        area = 1 + dfs(grid, area, row + 1, col) + dfs(grid, area, row - 1, col) + dfs(grid, area, row, col + 1) + dfs(grid, area, row, col - 1);

        return area;
    }

public:
    int maxAreaOfIsland(vector<vector<int>>& grid) {
        int rows = grid.size();
        int cols = grid[0].size();

        int area = 0;

        for(int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++){
                if(grid[row][col] == 1){
                    area = std::max(area, dfs(grid, area, row, col));
                }   
            }
        }   

        return area;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna