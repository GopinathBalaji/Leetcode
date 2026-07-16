/*
// Definition for a QuadTree node.
class Node {
public:
    bool val;
    bool isLeaf;
    Node* topLeft;
    Node* topRight;
    Node* bottomLeft;
    Node* bottomRight;
    
    Node() {
        val = false;
        isLeaf = false;
        topLeft = NULL;
        topRight = NULL;
        bottomLeft = NULL;
        bottomRight = NULL;
    }
    
    Node(bool _val, bool _isLeaf) {
        val = _val;
        isLeaf = _isLeaf;
        topLeft = NULL;
        topRight = NULL;
        bottomLeft = NULL;
        bottomRight = NULL;
    }
    
    Node(bool _val, bool _isLeaf, Node* _topLeft, Node* _topRight, Node* _bottomLeft, Node* _bottomRight) {
        val = _val;
        isLeaf = _isLeaf;
        topLeft = _topLeft;
        topRight = _topRight;
        bottomLeft = _bottomLeft;
        bottomRight = _bottomRight;
    }
};
*/


// Method 1: Recursive Approach
/*
Here are progressively stronger hints.

### Hint 1

A quad tree node represents a **square region** of the grid.

For each region, ask:

> Are all values inside this square the same?

* If yes, create a leaf node.
* If no, divide the square into four equal quadrants.

### Hint 2

Your recursive function should probably receive:

```cpp
(row, col, size)
```

where `(row, col)` is the top-left corner of the current square.

### Hint 3

The four recursive regions are:

```text
top-left:     (row,        col)
top-right:    (row,        col + half)
bottom-left:  (row + half, col)
bottom-right: (row + half, col + half)
```

where:

```cpp
int half = size / 2;
```

### Hint 4

The base case can be a region of size `1`.

A single cell is automatically uniform:

```cpp
new Node(grid[row][col], true);
```

### Hint 5

A simple solution checks every cell in the current square to see whether it matches the first cell:

```cpp
int value = grid[row][col];
```

If every cell equals `value`, return a leaf.

### Hint 6

When the region is not uniform, create an internal node:

```cpp
isLeaf = false;
```

Then recursively construct its four children.

For an internal node, the `val` field can be either `true` or `false`; it does not affect correctness.

### Recursive shape

```cpp
Node* build(int row, int col, int size) {
    if (region is uniform ) {
        return new Node( value , true);
    }

    int half = size / 2;

    Node* topLeft = build(...);
    Node* topRight = build(...);
    Node* bottomLeft = build(...);
    Node* bottomRight = build(...);

    return new Node(
        any value ,
        false,
        topLeft,
        topRight,
        bottomLeft,
        bottomRight
    );
}
```

### Important detail

Do not divide based only on the four corner values. Different cells inside the region could still contain another value. You must verify the entire region, unless you use a prefix-sum optimization.
*/

class Solution {
private:
    bool isUniform(vector<vector<int>>& grid, int row, int col, int size){
        int expected = grid[row][col];

        for(int i=row; i<row + size; i++){
            for(int j=col; j<col + size; j++){
                if(grid[i][j] != expected){
                    return false;
                }
            }
        }

        return true;
    }

    Node* build(vector<vector<int>>& grid, int row, int col, int size){
        if(isUniform(grid, row, col, size)){
            return new Node(grid[row][col], true);
        }

        int half = size / 2;

        Node* topLeft = build(grid, row, col, half);
        Node* topRight = build(grid, row, col + half, half);
        Node* bottomLeft = build(grid, row + half, col, half);
        Node* bottomRight = build(grid, row + half, col + half, half);

        return new Node(0, false, topLeft, topRight, bottomLeft, bottomRight);
    }

public:
    Node* construct(vector<vector<int>>& grid) {
        return build(grid, 0, 0, grid.size());
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna