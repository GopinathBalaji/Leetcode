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

//  Using Priority Queue

// class Solution {
//     public int kthSmallest(TreeNode root, int k) {
//         PriorityQueue<Integer> pq = new PriorityQueue<>();
//         Queue<TreeNode> queue = new LinkedList<>();
        
//         queue.offer(root);

//         while(!queue.isEmpty()){
//             TreeNode node = queue.poll();
//             pq.add(node.val);

//             if(node.left != null){
//                 queue.offer(node.left);
//             }
//             if(node.right != null){
//                 queue.offer(node.right);
//             }
//         }

//         for(int i=0;i<k-1;i++){
//             pq.poll();    
//         }

//         return pq.poll();
//     }
// }


// Using BST Inorder traversal property

class Solution {
    int count = 0;
    int result = -1;

    public int kthSmallest(TreeNode root, int k) {
        inorder(root, k);
        return result;
    }

    public void inorder(TreeNode node, int k){
        if(node == null){
            return;
        }
        
        inorder(node.left, k);

        count++;
        if(count == k){
            result = node.val;
            return;
        }

        inorder(node.right, k);
    }
}