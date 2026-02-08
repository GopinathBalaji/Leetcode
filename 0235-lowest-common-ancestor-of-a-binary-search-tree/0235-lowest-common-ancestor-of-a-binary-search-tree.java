/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */


// Method 1: My recursive DFS approach
/*
### Core BST insight

At any node `cur`:

* If **both** `p.val` and `q.val` are **less** than `cur.val` → LCA must be in the **left** subtree.
* If **both** are **greater** than `cur.val` → LCA must be in the **right** subtree.
* Otherwise (they’re on **different sides**, or one equals `cur`) → **`cur` is the LCA**.

### How to apply it (iterative or recursive)

1. Let `a = min(p.val, q.val)`, `b = max(p.val, q.val)`.
2. Start at `cur = root`.
3. While `cur` is not null:

   * If `b < cur.val` → go left
   * Else if `a > cur.val` → go right
   * Else → return `cur` (this is the split point / ancestor)

### Edge cases to remember

* If `p` is an ancestor of `q` (or vice versa), you’ll hit the ancestor and the “otherwise” case triggers because `cur.val == p.val` or it lies between.
* Values are unique in typical BST problems.

### Quick mental example

BST with root 6:

* `p=2`, `q=8`: at 6 → `2 < 6 < 8` ⇒ LCA = 6.
* `p=2`, `q=4`: at 6 → both < 6 go left to 2 → `cur == p` ⇒ LCA = 2.
*/
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        int a = Math.min(p.val, q.val);
        int b = Math.max(p.val, q.val);

        return lca(root, a, b);
    }

    private TreeNode lca(TreeNode root, int a, int b){
        if(root == null){
            return null;
        }

        TreeNode node;
        if(b < root.val){
            node = lca(root.left, a, b);
        }else if(a > root.val){
            node = lca(root.right, a, b);
        }else{
            node = root;
        }

        return node;
    }
}







// Method 1.5: Similar but cleaner recursive approach
/*
*/
// class Solution {
//     public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
//         int a = Math.min(p.val, q.val);
//         int b = Math.max(p.val, q.val);

//         return lca(root, a, b);
//     }

//     private TreeNode lca(TreeNode root, int a, int b) {
//         if (root == null) return null;
//         if (b < root.val) return lca(root.left, a, b);
//         if (a > root.val) return lca(root.right, a, b);
//         return root;
//     }
// }







// Method 2: My Iterative DFS Approach
/*
*/
// class Solution {
//     public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
//         int a = Math.min(p.val, q.val);
//         int b = Math.max(p.val, q.val);

//         Deque<TreeNode> stack = new ArrayDeque<>();
//         stack.push(root);

//         TreeNode ans = null;

//         while(!stack.isEmpty()){
//             TreeNode node = stack.pop();
            
//             if(b < node.val && node.left != null){
//                 stack.push(node.left);
//             }else if(a > node.val && node.right != null){
//                 stack.push(node.right);
//             }else{
//                 ans = node;
//                 break;
//             }
//         }

//         return ans;
//     }
// }





// Method 2.5: Simpler Iterative approach (No need of DFS with stack, just traversal)
/*
A BST LCA search is a single path walk (like binary search). You will never need to “branch” and come back, so a Deque is extra machinery.
*/
// class Solution {
//     public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
//         int a = Math.min(p.val, q.val);
//         int b = Math.max(p.val, q.val);

//         TreeNode cur = root;
//         while (cur != null) {
//             if (b < cur.val) cur = cur.left;
//             else if (a > cur.val) cur = cur.right;
//             else return cur;
//         }
//         return null;
//     }
// }