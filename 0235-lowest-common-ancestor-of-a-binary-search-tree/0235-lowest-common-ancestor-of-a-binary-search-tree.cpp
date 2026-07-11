/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode(int x) : val(x), left(NULL), right(NULL) {}
 * };
 */

// Method 1: Use BST propery
/*
### Hint 1

Use the **Binary Search Tree property**:

* Every value in the left subtree is smaller than the current node.
* Every value in the right subtree is larger than the current node.

### Hint 2

At the current node, compare both `p->val` and `q->val` with `root->val`.

* If both are smaller, the LCA must be in the left subtree.
* If both are larger, the LCA must be in the right subtree.

### Hint 3

What happens when `p` and `q` are on different sides of the current node?

That current node is the first point where their paths split, so it is the LCA.

### Hint 4

The current node is also the answer when it is equal to either `p` or `q`.

### Iterative structure

```cpp
while (root != nullptr) {
    if ( both smaller ) {
        root = root->left;
    } else if (both larger ) {
        root = root->right;
    } else {
        return root;
    }
}
```

You only follow one root-to-leaf path, giving **O(h)** time and **O(1)** extra space, where `h` is the tree height.
*/

class Solution {
public:
    TreeNode* lowestCommonAncestor(TreeNode* root, TreeNode* p, TreeNode* q) {

        if(p->val < root->val && q->val < root->val){
            return lowestCommonAncestor(root->left, p, q);
        }else if(p->val > root->val && q->val > root->val){
            return lowestCommonAncestor(root->right, p, q);
        }

        return root;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna