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

// Method 1: DFS to explore main tree + DFS to explore if subtree and subRoot are same
/*
Use the same idea as **LeetCode 100. Same Tree**, but with one extra layer.

You need to check:

> Is there any node in `root` where the subtree starting at that node is exactly the same as `subRoot`?

## Main idea

You need two recursive functions:

### 1. `isSameTree(a, b)`

This checks whether two trees are exactly identical.

Same logic as LeetCode 100:

```cpp
bool isSameTree(TreeNode* a, TreeNode* b)
```

Base cases:

```cpp
if (a == nullptr && b == nullptr) return true;
if (a == nullptr || b == nullptr) return false;
if (a->val != b->val) return false;
```

Then recursively check both sides:

```cpp
return isSameTree(a->left, b->left) &&
       isSameTree(a->right, b->right);
```

## 2. `isSubtree(root, subRoot)`

This searches through every node in `root`.

At each node, ask:

```cpp
Does the tree starting here match subRoot?
```

So:

```cpp
if (isSameTree(root, subRoot)) {
    return true;
}
```

If not, try the left and right children:

```cpp
return isSubtree(root->left, subRoot) ||
       isSubtree(root->right, subRoot);
```

## Important base case

If `root == nullptr`, then there is no place left to search.

```cpp
if (root == nullptr) {
    return false;
}
```

Usually `subRoot` is non-empty in this problem, but logically, an empty tree could be considered a subtree.


## How to think about it

For every node in the main tree:

```text
Try matching subRoot here.
If not, search left.
If not, search right.
```

## Complexity

Worst-case time complexity:

```text
O(n * m)
```

where:

* `n` = number of nodes in `root`
* `m` = number of nodes in `subRoot`

Because for many nodes in `root`, you may call `isSameTree`.

Space complexity:

```text
O(h)
```

for recursion stack, where `h` is the tree height.
*/
class Solution {
private:
    bool isSameTree(TreeNode* a, TreeNode* b){
        if(a == nullptr && b == nullptr){
            return true;
        }
        if(a == nullptr || b == nullptr){
            return false;
        }

        if(a->val != b->val){
            return false;
        }

        return isSameTree(a->left, b->left) && isSameTree(a->right, b->right);
    }


public:
    bool isSubtree(TreeNode* root, TreeNode* subRoot) {
        if(root == nullptr){
            return false;
        }

        if(isSameTree(root, subRoot)){
            return true;
        }

        return isSubtree(root->left, subRoot) || isSubtree(root->right, subRoot);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna