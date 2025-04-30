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
    public List<Integer> rightSideView(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        List<Integer> ans = new ArrayList<>();
        if(root == null){
            return ans;
        }
        
        queue.offer(root);


        while(!queue.isEmpty()){
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();

            for(int i=0;i<levelSize;i++){
                TreeNode node = queue.poll();
                level.add(node.val);

                if(node.left != null){
                    queue.offer(node.left);
                }
                
                if(node.right != null){
                    queue.offer(node.right);
                }
            }
            int val = level.get(level.size() - 1);
            ans.add(val);
        }

        return ans;
    }
}