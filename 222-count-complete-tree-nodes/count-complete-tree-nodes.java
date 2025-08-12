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


// Using the fact that the tree is complete except
// for the last level. Find the height of the subtrees
// recursively. O(log² n) solution.
/*
## Key idea of the algorithm

We measure:

* **leftHeight** = height going down the *leftmost* path from the root.
* **rightHeight** = height going down the *rightmost* path from the root.

But in the optimized solution, we actually compare:

* `leftHeight` from **root.left**
* `rightHeight` from **root.right**

---

### The crucial observation

A **complete binary tree** is always filled from **left to right** at the last level.

So:

1. **If `leftHeight == rightHeight`**
   This means the left subtree is **perfect** — completely filled, height = `leftHeight`.
   Why? Because in a complete tree, if the leftmost height from the left subtree equals the leftmost height from the right subtree, it means the left subtree filled up entirely before the right subtree could start filling.

   → So we count nodes in the left subtree in O(1) and recurse on the right subtree.

---

2. **If `leftHeight != rightHeight`**
   This means the **right subtree is perfect** — not the left.

   **Why is the right subtree perfect here?**

   * If the heights differ, it means the left subtree is taller.
   * That can only happen if the left subtree is *not* full at the last level — because in a complete tree, if the last level is not full, the "incomplete" nodes will be in the left subtree.
   * Since the right subtree has smaller height, it must be **completely filled up to its height**.

   → So we count nodes in the right subtree in O(1) and recurse on the left subtree.


At a given node:
Find the height of its left subtree.
Find the height of its right subtree.

If leftHeight == rightHeight:
Left subtree is perfect → node count = (1 << leftHeight) - 1 + 1 (root) + recurse on right subtree.

Else:
Right subtree is perfect → node count = (1 << rightHeight) - 1 + 1 (root) + recurse on left subtree.

Repeat recursively.


### Small example

```
       1
     /   \
    2     3
   / \   / \
  4   5 6   7   ← perfect tree
```

Here:

* leftHeight = 3
* rightHeight = 3 → **left subtree is perfect**.

---

Now with missing nodes:

```
       1
     /   \
    2     3
   / \   / 
  4   5 6
```

Here:

* leftHeight = 3
* rightHeight = 2 → **right subtree is perfect**.
  Why? Because all its nodes (3 and 6) fill completely for its height before left subtree finished.
*/

class Solution {
    public int countNodes(TreeNode root) {
        if (root == null) return 0;

        int leftHeight = getHeight(root.left);
        int rightHeight = getHeight(root.right);

        if (leftHeight == rightHeight) {
            // Left subtree is perfect: (2^h - 1 nodes) + 1 (root) + recurse on right subtree
            return (1 << leftHeight) + countNodes(root.right);
        } else {
            // Right subtree is perfect: (2^h - 1 nodes) + 1 (root) + recurse on left subtree
            return (1 << rightHeight) + countNodes(root.left);
        }
    }

    // Calculates height from a given node going leftwards only
    private int getHeight(TreeNode node) {
        int height = 0;
        while (node != null) {
            height++;
            node = node.left;
        }
        return height;
    }
}