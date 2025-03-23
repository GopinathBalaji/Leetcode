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
class BSTIterator {

    private Queue<TreeNode> queue = new LinkedList<>();

    public BSTIterator(TreeNode root) {
        addNodes(root);
    }

    private void addNodes(TreeNode root){
        
        if(root == null){
            return;
        }
            addNodes(root.left);
            queue.offer(root);
            addNodes(root.right);
        }
    

    public int next() {
        return queue.poll().val;
    }
    
    public boolean hasNext() {
        return !queue.isEmpty();
    }

}

/**
 * Your BSTIterator object will be instantiated and called as such:
 * BSTIterator obj = new BSTIterator(root);
 * int param_1 = obj.next();
 * boolean param_2 = obj.hasNext();
 */