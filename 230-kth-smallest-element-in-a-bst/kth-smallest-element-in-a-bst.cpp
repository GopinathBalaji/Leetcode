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

// Method 1: Iterative Inorder traversal
/*
### Hint 1

Think about what an **inorder traversal** produces for a Binary Search Tree:

```text
left → node → right
```

The values appear in sorted ascending order.

### Hint 2

During inorder traversal, keep a counter of how many nodes you have visited.

When the counter reaches `k`, the current node is the answer.

### Hint 3

A recursive helper could use references:

```cpp
void inorder(TreeNode* node, int& k, int& answer)
```

At each visited node:

```cpp
k--;
```

When:

```cpp
k == 0
```

store `node->val`.

### Hint 4

Once the answer is found, avoid unnecessary traversal. You can return early when `k == 0`.

### Hint 5

An iterative inorder traversal uses a stack:

1. Push all left children.
2. Pop one node.
3. Decrease `k`.
4. If `k == 0`, return its value.
5. Move to its right child.

The core shape is:

```cpp
while (root != nullptr || !st.empty()) {
    while (root != nullptr) {
        st.push(root);
        root = root->left;
    }

    root = st.top();
    st.pop();

    // visit root

    root = root->right;
}
```

### Complexity

You may stop after visiting the first `k` nodes:

* Time: approximately `O(h + k)`
* Space: `O(h)`

where `h` is the tree height.
*/
class Solution {
private: 
    void inorder(TreeNode* node, int& k, int& ans){
        if(node == nullptr){
            return;
        }
        

        inorder(node->left, k, ans);

        k--;
        if(k == 0){
            ans = node->val;
            return;
        }

        inorder(node->right, k, ans);
    }
public:
    int kthSmallest(TreeNode* root, int k) {
        int ans = 0;
        inorder(root, k, ans);

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna