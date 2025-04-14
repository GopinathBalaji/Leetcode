/*
// Definition for a QuadTree node.
class Node {
    public boolean val;
    public boolean isLeaf;
    public Node topLeft;
    public Node topRight;
    public Node bottomLeft;
    public Node bottomRight;

    
    public Node() {
        this.val = false;
        this.isLeaf = false;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }
    
    public Node(boolean val, boolean isLeaf) {
        this.val = val;
        this.isLeaf = isLeaf;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }
    
    public Node(boolean val, boolean isLeaf, Node topLeft, Node topRight, Node bottomLeft, Node bottomRight) {
        this.val = val;
        this.isLeaf = isLeaf;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }
}
*/

class Solution {
    // Method to construct the quad-tree from a given grid
    public Node construct(int[][] grid) {
        // Call the recursive function with the entire grid dimensions
        return buildTree(0, 0, grid.length - 1, grid[0].length - 1, grid);
    }

    // Recursive function to build the quad-tree
    private Node buildTree(int topLeftRow, int topLeftCol, int bottomRightRow, int bottomRightCol, int[][] grid) {
        int zeroCount = 0, oneCount = 0;

        // Check each element in the current grid section to count zeros and ones
        for (int row = topLeftRow; row <= bottomRightRow; ++row) {
            for (int col = topLeftCol; col <= bottomRightCol; ++col) {
                if (grid[row][col] == 0) {
                    zeroCount = 1;
                } else {
                    oneCount = 1;
                }
                // If we found at least one of each, we can break early
                if (zeroCount + oneCount == 2) {
                    break;
                }
            }
        }

        // Determine if the current section is a leaf node (has only zeros or only ones)
        boolean isLeaf = zeroCount == 0 || oneCount == 0;
        // If it's a leaf node and there was at least one '1', the value is true
        boolean val = isLeaf && oneCount == 1;

        // Create a new quad-tree node with the calculated value and isLeaf status
        Node node = new Node(val, isLeaf);

        // If it's not a leaf node, we need to divide the current section into four parts and recurse
        if (!isLeaf) {
            int midRow = (topLeftRow + bottomRightRow) / 2;
            int midCol = (topLeftCol + bottomRightCol) / 2;

            // Define the four children by dividing the current grid section
            node.topLeft = buildTree(topLeftRow, topLeftCol, midRow, midCol, grid);
            node.topRight = buildTree(topLeftRow, midCol + 1, midRow, bottomRightCol, grid);
            node.bottomLeft = buildTree(midRow + 1, topLeftCol, bottomRightRow, midCol, grid);
            node.bottomRight = buildTree(midRow + 1, midCol + 1, bottomRightRow, bottomRightCol, grid);
        }

        // Return the constructed quad-tree node
        return node;
    }
}