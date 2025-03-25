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
    public int height(TreeNode root){
        if(root == null){
            return -1;
        }
        return 1 + height(root.left);
    } 

    public int countNodes(TreeNode root) {
        int h = height(root);

        if(h < 0){
            return 0;
        }
        else{
            if(height(root.right) == h-1){
                return (1 << h) + countNodes(root.right);
            }
            else{
                return (1 << h-1) + countNodes(root.left);
            }
        }
    }
}