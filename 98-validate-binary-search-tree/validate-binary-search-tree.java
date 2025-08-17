/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */

// Only doing an Inorder traversal and comparing immediate children 
// (i.e node.left and node.right) values to its root is not enough. This is because 
// it will miss violations deeper in the tree.
/*
Only comparing immediate children is not enough.
A BST requires that all nodes in the left subtree are less than the current node, and all nodes in the right subtree are greater. Your code only checks leftNode.val and rightNode.val (immediate children), so it will miss violations deeper in the tree.

Returning nodes is unnecessary.
What you really need to propagate are bounds (minimum and maximum allowed values) down the recursion.

Correct and Improved Solution
We fix this by passing min and max constraints at each recursion step.

Why this works:
Each recursive call narrows the allowed range:
Left child must be < node.val.
Right child must be > node.val.
We use long for min and max to avoid overflow if node values are at the integer limits.

Why do we return true as base case?
A null node means we’ve reached beyond a leaf.
An empty tree (or empty subtree) is by definition valid, because there are no nodes that can break the BST rule.
So returning true as the base case simply says: “There’s nothing here to invalidate the BST.”


Key ideas to keep in mind:

* Each node must be **strictly** between `(min, max)`.
* Left subtree narrows the **upper bound** to `node.val`.
* Right subtree raises the **lower bound** to `node.val`.
* `null` ⇒ “empty subtree” ⇒ **valid** (`true`).
* The `&&` is **short-circuiting**: if the left returns `false`, Java won’t even call the right side.

---

# Example 1 — **Valid BST**

Tree:

```
      5
     / \
    1   7
       / \
      6   8
```

Start: `dfs(5, -∞, +∞)` → `(-∞, +∞)` means `(Long.MIN_VALUE, Long.MAX_VALUE)` in code.

1. `node=5, (min=-∞, max=+∞)`

   * Check: `-∞ < 5 < +∞` 
   * Recurse left: `dfs(1, -∞, 5)` and right: `dfs(7, 5, +∞)`.

2. **Left subtree**: `node=1, (min=-∞, max=5)`

   * Check: `-∞ < 1 < 5` 
   * Left child: `dfs(null, -∞, 1)` → `true` (base case)
   * Right child: `dfs(null, 1, 5)` → `true`
   * Result for node 1: `true && true = true`.

3. **Right subtree**: `node=7, (min=5, max=+∞)`

   * Check: `5 < 7 < +∞` 
   * Left child: `dfs(6, 5, 7)`
   * Right child: `dfs(8, 7, +∞)`

   3a) `node=6, (min=5, max=7)`
   \- Check: `5 < 6 < 7` 
   \- Left: `dfs(null, 5, 6)` → `true`
   \- Right: `dfs(null, 6, 7)` → `true`
   \- Result: `true`.

   3b) `node=8, (min=7, max=+∞)`
   \- Check: `7 < 8 < +∞` 
   \- Left: `dfs(null, 7, 8)` → `true`
   \- Right: `dfs(null, 8, +∞)` → `true`
   \- Result: `true`.

4. Back at `node=7`: `left=true && right=true` ⇒ `true`.

5. Back at `node=5`: `left=true && right=true` ⇒ **overall `true`**.

Why the base case matters: every time we hit `null` (beyond a leaf), we return `true`, which says “this empty subtree doesn’t violate anything,” letting valid branches remain valid.

---

# Example 2 — **Invalid BST (deep violation)**

Tree:

```
      5
     / \
    4   6
       / \
      3   7
```

This tree is **not** a BST because `3` sits in the **right** subtree of `5` but is **less than 5**.

1. `node=5, (min=-∞, max=+∞)`

   * Check: `-∞ < 5 < +∞` 
   * Recurse left: `dfs(4, -∞, 5)` and right: `dfs(6, 5, +∞)`.

2. **Left subtree**: `node=4, (min=-∞, max=5)`

   * Check: `-∞ < 4 < 5` 
   * Left: `dfs(null, -∞, 4)` → `true`
   * Right: `dfs(null, 4, 5)` → `true`
   * Result: `true`.

3. **Right subtree**: `node=6, (min=5, max=+∞)`

   * Check: `5 < 6 < +∞` 
   * Left: `dfs(3, 5, 6)`
   * Right: `dfs(7, 6, +∞)`

   3a) **Here’s the violation**: `node=3, (min=5, max=6)`
   \- Check: is `5 < 3 < 6`?  (3 ≤ 5)
   \- Return `false` immediately.
   \- Because of short-circuiting, `dfs(6’s right)` is **not even called**—no need to do extra work once a violation is found.

4. Back at `node=6`: left was `false`, so `false && (…skipped…)` ⇒ `false`.

5. Back at `node=5`: left was `true`, right is `false` ⇒ `true && false` ⇒ **overall `false`**.

This example shows why just checking immediate children isn’t enough; the problem is **deeper** (the `3` is under the right subtree of `5` but breaks the global rule).

---

# Tiny edge-case notes

* **Duplicates are not allowed** in a strict BST for this problem. That’s why we use `<= min` and `>= max` as violations.
* We use **`long`** for bounds so we can set `min = Long.MIN_VALUE` and `max = Long.MAX_VALUE` safely, even if node values are `Integer.MIN_VALUE` or `Integer.MAX_VALUE`. This avoids overflow/edge issues.
*/
class Solution {
    public boolean isValidBST(TreeNode root) {
        return dfs(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean dfs(TreeNode node, long min, long max) {
        if (node == null) {
            return true;
        }

        // Node value must be strictly between min and max
        if (node.val <= min || node.val >= max) {
            return false;
        }

        // Left subtree: values must be < node.val
        // Right subtree: values must be > node.val
        return dfs(node.left, min, node.val) && dfs(node.right, node.val, max);
    }
}


// Method 2: The BST inorder property can be used if we maintain previous node value
// Since the inorder traversal of BST gives sorted values we can check if current
// value is greater than the previous value.
/*
Why this works
We recursively visit nodes in order: left → root → right.

For a valid BST, this sequence must be strictly increasing.

We keep track of the last visited node (prev) and check if the current node’s value is greater. If not, it’s invalid.

Comparing the Two Correct Approaches:
Both are O(n) time and O(h) space (h = tree height).
*/

// class Solution{

//     private TreeNode prev = null;

//     public boolean isValidBST(TreeNode root){
//         return inorder(root);
//     }

//     private boolean inorder(TreeNode node){
//         if(node == null){
//             return true;
//         }

//         if(!inorder(node.left)){
//             return false;
//         }

//         if(prev != null && node.val <= prev.val){
//             return false;
//         }

//         prev = node;

//         return inorder(node.right);
//     }
// }