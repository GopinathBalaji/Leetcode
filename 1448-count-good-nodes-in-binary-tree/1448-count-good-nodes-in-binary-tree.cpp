/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode() : val(0), left(nullptr), right(nullptr) {}
 *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
 *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 * };
 */

// Method 1: Recursive DFS approach
/*
### Hint 1

A node is “good” when no earlier node on the path from the root has a greater value.

So while traversing, keep track of:

```cpp
maxValueSoFar
```

### Hint 2

At each node, compare:

```cpp
root->val >= maxValueSoFar
```

When true, count that node as good.

### Hint 3

Before recursing into the children, update the maximum:

```cpp
int newMax = max(maxValueSoFar, root->val);
```

Pass `newMax` to both subtrees.

### Hint 4

A recursive helper could look like:

```cpp
int dfs(TreeNode* node, int maxValueSoFar)
```

Base case:

```cpp
if (node == nullptr) {
    return 0;
}
```

### Hint 5

The result for the current subtree is:

```text
current node's contribution
+ good nodes in left subtree
+ good nodes in right subtree
```

### Starting value

For the initial maximum, either use:

```cpp
root->val
```

or:

```cpp
INT_MIN
```

Then call the helper from the root.

### Complexity

You visit each node once:

* Time: `O(n)`
* Recursive space: `O(h)` where `h` is the tree height.
*/ 
class Solution {
private:
    int dfs(TreeNode* node, int maxValSoFar){
        if(node == nullptr){
            return 0;
        }

        int goodNode = 0;
        if(node->val >= maxValSoFar){
            goodNode = 1;
        }

        int newMax = std::max(maxValSoFar, node->val);

        return goodNode + dfs(node->left, newMax) + dfs(node->right, newMax);
    }

public:
    int goodNodes(TreeNode* root) {
        return dfs(root, root->val);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna