// Method 1: Reverse DFS
/*
This is a **reverse DFS/BFS** problem.

The tricky part is that water normally flows from higher/equal cells to lower/equal cells, but it’s easier to search **backward from the oceans**.

### Hint 1: Don’t start from every cell

A brute-force idea would be:

```text
for every cell:
    can it reach Pacific?
    can it reach Atlantic?
```

That repeats a lot of work.

Instead, start from the oceans and ask:

> Which cells can flow **into** this ocean?

### Hint 2: Pacific and Atlantic borders

The Pacific touches:

```text
top row
left column
```

The Atlantic touches:

```text
bottom row
right column
```

Create two visited grids:

```cpp
vector<vector<bool>> pacific(rows, vector<bool>(cols, false));
vector<vector<bool>> atlantic(rows, vector<bool>(cols, false));
```

Then run DFS/BFS from all Pacific-border cells into `pacific`, and from all Atlantic-border cells into `atlantic`.

### Hint 3: Reverse the height condition

Normally water can flow:

```text
current height >= next height
```

But because you're searching **from the ocean inward**, reverse that:

```text
next height >= current height
```

So while traversing from `(r, c)` to `(nr, nc)`:

```cpp
if (heights[nr][nc] >= heights[r][c]) {
    // can continue
}
```

Why? Because if the neighbor is higher, water from that neighbor could flow down to your current cell and eventually reach the ocean.

### Hint 4: DFS helper

A useful structure:

```cpp
void dfs(vector<vector<int>>& heights,
         int r,
         int c,
         vector<vector<bool>>& visited) {

    visited[r][c] = true;

    // try 4 directions

    // skip:
    //   out of bounds
    //   already visited
    //   neighbor height < current height

    // recurse
}
```

### Hint 5: Start DFS from every ocean-border cell

Pacific:

```cpp
for (int r = 0; r < rows; r++) {
    dfs(heights, r, 0, pacific);
}

for (int c = 0; c < cols; c++) {
    dfs(heights, 0, c, pacific);
}
```

Atlantic is similar, but use:

```text
right column
bottom row
```

### Hint 6: Final answer is the intersection

Once both searches are done, scan the grid:

```cpp
if (pacific[r][c] && atlantic[r][c]) {
    // add {r, c}
}
```

Those are exactly the cells that can reach both oceans.

The key mental shift is:

```text
Normal water flow:
higher → lower

Reverse search from ocean:
lower → higher
```

So instead of asking “where can this cell flow?”, ask “which cells could eventually flow into this ocean?”
*/
class Solution {
private:
    void dfs(vector<vector<int>>& heights, int row, int col, vector<vector<bool>>& visited){
        visited[row][col] = true;

        int dirs[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for(auto& dir : dirs){
            int nr = row + dir[0];
            int nc = col + dir[1];

            if(nr < 0 || nr >= heights.size() || nc < 0 || nc >= heights[0].size()){
                continue;
            }

            if(visited[nr][nc]){
                continue;
            }

            if(heights[nr][nc] < heights[row][col]){
                continue;
            }

            dfs(heights, nr, nc, visited);
        }
    }

public:
    vector<vector<int>> pacificAtlantic(vector<vector<int>>& heights) {
        int rows = heights.size();
        int cols = heights[0].size();

        vector<vector<bool>> pacific(rows, vector<bool>(cols, false));
        vector<vector<bool>> atlantic(rows, vector<bool>(cols, false));

        vector<vector<int>> ans;

        for(int r=0; r<rows; r++){
            dfs(heights, r, 0, pacific);
        }

        for(int c=0; c<cols; c++){
            dfs(heights, 0, c, pacific);
        }

        for(int r=0; r<rows; r++){
            dfs(heights, r, cols - 1, atlantic);
        }

        for(int c=0; c<cols; c++){
            dfs(heights, rows - 1, c, atlantic);
        }


        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                if(pacific[r][c] && atlantic[r][c]){
                    ans.push_back({r, c});
                }
            }
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna