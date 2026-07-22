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

// Method 1: Recursive Postorder Traversal
/*
### Hint 1

Use **postorder traversal**:

```text
left → right → node
```

You must process the children before deciding whether the current node should be deleted.

### Hint 2

Your recursive helper can return the updated subtree root:

```cpp
TreeNode* remove(TreeNode* node, int target)
```

Base case:

```cpp
if (node == nullptr) {
    return nullptr;
}
```

### Hint 3

First update both children:

```cpp
node->left = remove(node->left, target);
node->right = remove(node->right, target);
```

The assignments are important because a child may be deleted and replaced with `nullptr`.

### Hint 4

After processing the children, check whether the current node has become a target leaf:

```cpp
node->left == nullptr &&
node->right == nullptr &&
node->val == target
```

If so, return `nullptr`.

### Hint 5

Why must deletion happen after the recursive calls?

A node might not initially be a leaf, but its target-valued children could be removed, causing it to become a new leaf that must also be removed.

For example:

```text
    2
   /
  2
```

With `target = 2`, deleting only the original leaves is not enough. After the child is removed, the root also becomes a target leaf.

### Overall structure

```cpp
TreeNode* remove(TreeNode* node, int target) {
    if (node == nullptr) {
        return nullptr;
    }

    node->left = remove(node->left, target);
    node->right = remove(node->right, target);

    if (node is now a target leaf ) {
        return nullptr;
    }

    return node;
}
```

Time complexity is `O(n)` because every node is visited once.
*/
class Solution {
public:
    TreeNode* removeLeafNodes(TreeNode* root, int target) {
        if(root == nullptr){
            return nullptr;
        }

        root->left = removeLeafNodes(root->left, target);
        root->right = removeLeafNodes(root->right, target);

        if(root->left == nullptr && root->right == nullptr && root->val == target){
            return nullptr;
        }

        return root;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna