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

//  Method 1: DFS DP
/*
### Hint 1

At each house/node, you have two choices:

* Rob the current node.
* Skip the current node.

The catch is that if you rob a node, you cannot rob its children.

### Hint 2

A single value from each subtree is not enough.

For every node, return two results:

```cpp
robThisNode
skipThisNode
```

You can represent them with:

```cpp
pair<int, int>
```

### Hint 3

Suppose your helper returns:

```cpp
{rob, skip}
```

For a `nullptr` node:

```cpp
return {0, 0};
```

### Hint 4

Recursively get the two choices for both children:

```cpp
auto left = dfs(node->left);
auto right = dfs(node->right);
```

Think of:

```cpp
left.first   // rob left child
left.second  // skip left child
```

and similarly for `right`.

### Hint 5

When you **rob the current node**, you must skip both children:

```cpp
robCurrent =
    node->val +
    left.second +
    right.second;
```

### Hint 6

When you **skip the current node**, each child can independently be robbed or skipped—whichever gives more money:

```cpp
skipCurrent =
    max(left.first, left.second) +
    max(right.first, right.second);
```

### Hint 7

Your helper has this general shape:

```cpp
pair<int, int> dfs(TreeNode* node) {
    if (node == nullptr) {
        return {0, 0};
    }

    auto left = dfs(node->left);
    auto right = dfs(node->right);

    int robCurrent =  ... ;
    int skipCurrent = ... ;

    return {robCurrent, skipCurrent};
}
```

At the root, the answer is:

```cpp
max(result.first, result.second);
```

### Complexity

Each node is processed once:

* Time: `O(n)`
* Recursive space: `O(h)` where `h` is the tree height.
*/
class Solution {
private:
    pair<int, int> dfs(TreeNode* node){
        if(node == nullptr){
            return {0, 0};
        }

        auto left = dfs(node->left);
        auto right = dfs(node->right);

        int robCurrent = node->val + left.second + right.second;
        int skipCurrent = std::max(left.first, left.second) + std::max(right.first, right.second);

        return {robCurrent, skipCurrent};
    }

public:
    int rob(TreeNode* root) {
        auto result = dfs(root);

        return std::max(result.first, result.second);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna