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

// Using Inorder traversal because it is sorted
/*
##  Start from the problem constraints

* It’s a **BST**.
* You want the **minimum absolute difference** between values of *any two nodes*.

The BST property tells you:

> **In-order traversal** of a BST produces sorted values.

If values are sorted, what’s the simplest way to find the smallest difference between any two numbers?
Just compare **adjacent** numbers in the sorted order.

---

## Link the property to traversal

That means your DFS can be an **in-order DFS**:

1. Go left
2. Process current node
3. Go right

While processing the current node, you keep track of:

* **`prev`** (the value of the previously visited node)
* **`minDiff`** (smallest difference found so far)

---

## Why does comparing only adjacent nodes work?

Think about it:

* If the list is sorted: `a ≤ b ≤ c`,
  then `|a - c|` ≥ `|b - a|` and `|b - c|`.
* So the smallest difference **must** be between neighbors in sorted order.
* No need for nested loops (which would be O(n²)).

---

## Step-by-step mental model

When coding:

1. **Initialize** `prev = null` and `minDiff = infinity`.
2. **DFS left** until null.
3. **At each node**:

   * If `prev` exists, calculate `curr.val - prev` and update `minDiff` if smaller.
   * Update `prev` to current node’s value.
4. **DFS right**.
5. Return `minDiff`.

---

##  Quick mental rehearsal

If the BST is:

```
    4
   / \
  2   6
 / \
1   3
```

In-order sequence: `1, 2, 3, 4, 6`

* Compare 1 & 2 → diff = 1
* Compare 2 & 3 → diff = 1
* Compare 3 & 4 → diff = 1
* Compare 4 & 6 → diff = 2
  Min diff = 1

*/
class Solution {

    Integer prev = null;
    int minDiff = Integer.MAX_VALUE;

    public int getMinimumDifference(TreeNode root) {
        recursiveDfs(root);
        return minDiff;
    }

    private void recursiveDfs(TreeNode node){
        if(node == null){
            return;
        }

        recursiveDfs(node.left);
        if(prev != null){
            minDiff = Math.min(minDiff, Math.abs(node.val - prev));
        }
        prev = node.val;
        recursiveDfs(node.right);

    }
}


// Iterative inorder DFS version
// class Solution {
//     public int getMinimumDifference(TreeNode root) {
//         Stack<TreeNode> stack = new Stack<>();
//         TreeNode current = root;
//         Integer prev = null;  // store value of previous node
//         int minDiff = Integer.MAX_VALUE;

//         // Standard iterative inorder traversal
//         while (current != null || !stack.isEmpty()) {
//             // Go as left as possible
//             while (current != null) {
//                 stack.push(current);
//                 current = current.left;
//             }

//             // Pop the node
//             current = stack.pop();

//             // Process the node
//             if (prev != null) {
//                 minDiff = Math.min(minDiff, current.val - prev);
//             }
//             prev = current.val;

//             // Move right
//             current = current.right;
//         }

//         return minDiff;
//     }
// }



// BFS version is less optimal because:
/* 
 Key observation
The problem asks for the minimum absolute difference 
between values of any two nodes in a BST.
The BST property is critical: inorder traversal gives sorted values.
BFS (level-order) doesn’t give sorted order. If we just traverse by BFS, 
values aren’t guaranteed to be adjacent in sorted order, so you can’t simply compare neighbors.

So, if you do BFS alone:
You’d have to collect all values in a list.
Then sort the list.
Finally, compute the min difference between adjacent values.
*/