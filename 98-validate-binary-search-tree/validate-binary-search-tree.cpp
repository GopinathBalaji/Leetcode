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

// Method 1: Using allowed value range in the recursive function (alternate method is to use strictly increasing inorder traversal)
/*
### Hint 1

Checking only this is **not enough**:

```cpp
node->left->val < node->val
node->right->val > node->val
```

A node must satisfy constraints created by **all its ancestors**, not just its parent.

### Hint 2

Pass an allowed value range into your recursive function:

```cpp
bool validate(TreeNode* node, long long lower, long long upper)
```

Every node must satisfy:

```cpp
lower < node->val && node->val < upper
```

### Hint 3

When moving left, the current node becomes the new upper bound:

```cpp
validate(node->left, lower, node->val)
```

When moving right, it becomes the new lower bound:

```cpp
validate(node->right, node->val, upper)
```

### Hint 4

The base case is:

```cpp
if (node == nullptr) {
    return true;
}
```

An empty subtree is a valid BST.

### Hint 5

Use `long long` bounds rather than `int`, because node values may include `INT_MIN` or `INT_MAX`.

Start with:

```cpp
validate(root, LLONG_MIN, LLONG_MAX);
```

### Alternative idea

An inorder traversal of a valid BST must produce values in **strictly increasing order**. Keep track of the previously visited value and ensure:

```cpp
previous < current
```

“Strictly” matters because duplicate values are not allowed in this problem.
*/
class Solution {
private:
    bool validate(TreeNode* node, long long lower, long long upper){
        if(node == nullptr){
            return true;
        }

        if(lower >= node->val || upper <= node->val){
            return false;
        }

        return validate(node->left, lower, node->val) && validate(node->right, node->val, upper);
    }

public:
    bool isValidBST(TreeNode* root) {
        return validate(root, LLONG_MIN, LLONG_MAX);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna