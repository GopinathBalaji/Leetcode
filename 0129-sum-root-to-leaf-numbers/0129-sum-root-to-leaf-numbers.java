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
class Solution {
    public int sumNumbers(TreeNode root) {
        if(root == null){
            return 0;
        }

        if(root.left == null && root.right == null){
            return root.val;
        }
        
        if(root.left != null){
            root.left.val += root.val * 10;
        }

        if(root.right != null){
            root.right.val += root.val * 10;
        }

        return sumNumbers(root.left) + sumNumbers(root.right);
    }
}