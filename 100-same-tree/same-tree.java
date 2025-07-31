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

//  Simple BFS method
class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {

        record NodePair(TreeNode first, TreeNode second) {}
        Queue<NodePair> queue = new LinkedList<>();

        queue.offer(new NodePair(p, q));


        while(!queue.isEmpty()){
            NodePair pair = queue.poll();
            TreeNode node1 = pair.first();
            TreeNode node2 = pair.second();

            if(node1 == null && node2 == null){
                continue;
            }

            if((node1 == null && node2 != null) || (node1 != null && node2 == null)){
                return false;
            }

            if(node1.val != node2.val){
                return false;
            }

            queue.offer(new NodePair(node1.left, node2.left));
            queue.offer(new NodePair(node1.right, node2.right));
        }

        return true;
    }
}


// DFS method 
/*
Recursive DFS Thought Process

Base Cases:
If both nodes are null, they are the same at this branch → return true.
If only one node is null, shapes differ → return false.

Check Current Node Values:
If both are non‑null but node1.val != node2.val → return false.

Recursive Step:
Check left subtrees with DFS.
Check right subtrees with DFS.
Trees are the same only if both left and right subtrees are the same.
*/

// class Solution {
//     public boolean isSameTree(TreeNode p, TreeNode q) {
//         if(p == null && q == null){
//             return true;
//         }

//         if((p == null && q != null) || (p != null && q == null)){
//             return false;
//         }

//         if(p.val != q.val){
//             return false;
//         }

//         return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
//     }
// }