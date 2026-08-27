// Method 1: DFS
/*
Think of each land cell as contributing up to **4 sides** to the perimeter.

### Hint 1: Start from the simplest observation

For every land cell:

```text
1
```

it has 4 possible perimeter edges:

```text
top
bottom
left
right
```

But if one of those sides touches another land cell, that side is **not** part of the perimeter.

### Hint 2: One easy counting approach

For each land cell:

```cpp
if (grid[r][c] == 1) {
    perimeter += 4;
}
```

Then look at neighboring land cells.

If two land cells touch, the shared edge was counted twice, so subtract:

```text
2
```

A common trick is to only check:

```text
up
left
```

instead of all four directions, so each shared edge is handled exactly once.

Conceptually:

```cpp
if (grid[r][c] == 1) {
    perimeter += 4;

    if (r > 0 && grid[r - 1][c] == 1) {
        perimeter -= 2;
    }

    if (c > 0 && grid[r][c - 1] == 1) {
        perimeter -= 2;
    }
}
```

### Hint 3: Why subtract 2?

Suppose two adjacent land cells are:

```text
[1][1]
```

Each contributes 4 initially:

```text
4 + 4 = 8
```

But their touching sides are internal:

```text
one side from first cell
one side from second cell
```

So subtract `2`.

### Hint 4: There is also a DFS way

If you want to solve it like an island traversal problem:

* If DFS goes out of bounds → that contributes `1` perimeter.
* If DFS reaches water → that contributes `1` perimeter.
* If DFS reaches already-visited land → contributes `0`.
* Otherwise visit the land cell and recurse in 4 directions.

The recursive idea would be:

```cpp
if (out of bounds) {
    return 1;
}

if (grid[r][c] == 0) {
    return 1;
}

if (already visited) {
    return 0;
}

mark visited;

return dfs(up)
     + dfs(down)
     + dfs(left)
     + dfs(right);
```

For this particular problem, though, the direct counting solution is simpler: **`+4` per land cell, `-2` per shared edge**.
*/
class Solution {
private:
    int dfs(vector<vector<int>>& grid, int row, int col){
        if(row < 0 || row >= grid.size() || col < 0 || col >= grid[0].size()){
            return 1;
        }

        if(grid[row][col] == 0){
            return 1;
        }

        if(grid[row][col] == -1){
            return 0;
        }

        grid[row][col] = -1;

        return dfs(grid, row + 1, col) + dfs(grid, row - 1, col) + dfs(grid, row, col - 1) + dfs(grid, row, col + 1);
    }

public:
    int islandPerimeter(vector<vector<int>>& grid) {
        
        for(int r=0; r<grid.size(); r++){
            for(int c=0; c<grid[0].size(); c++){
                if(grid[r][c] == 1){
                    return dfs(grid, r, c);
                }
            }
        }
        
        return 0;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna