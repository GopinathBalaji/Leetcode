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

// Method 1: Recursive approach using BST property
/*
Here are progressively stronger hints.

### Hint 1

Think about how you search for a value in a BST.

Insertion follows **exactly the same path**. Keep moving left or right until you find a `nullptr`.

---

### Hint 2

At each node:

* If `val < root->val`, the new node belongs somewhere in the left subtree.
* Otherwise, it belongs in the right subtree.

Eventually you'll reach an empty child.

---

### Hint 3 (Recursive)

What should happen when `root` is `nullptr`?

That's the perfect place to insert the new node.

```cpp
if (root == nullptr)
    // create and return a new node
```

---

### Hint 4

After recursively inserting into one subtree, don't forget to reconnect it.

Think about statements like:

```cpp
root->left = ...
```

or

```cpp
root->right = ...
```

Why is this necessary?

---

### Hint 5

At the end of every recursive call, what should you return?

The current `root`.

This allows the parent to reconnect the subtree correctly.

---

### Hint 6 (Iterative)

Instead of recursion, keep two pointers:

* `curr` — traverses the tree.
* `parent` — remembers the previous node.

When `curr` becomes `nullptr`, attach the new node as either:

* `parent->left`
* `parent->right`

depending on the value.

---

### Complexity

* **Time:** `O(h)` where `h` is the height of the BST (`O(log n)` on a balanced tree, `O(n)` in the worst case).
* **Space:** `O(h)` for the recursive approach, or `O(1)` for the iterative approach.
*/
class Solution {
public:
    TreeNode* insertIntoBST(TreeNode* root, int val) {
        if(root == nullptr){
            return new TreeNode(val);
        }

        if(val < root->val){
            root->left = insertIntoBST(root->left, val);
        }else{
            root->right = insertIntoBST(root->right, val);
        }

        return root;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna