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

//  DFS
/*
Don't think about just pre or post order traversal.
You need to traverse both the left and the right branches simultaneously.
So we must compare in pairs in DFS, not traverse singly.
*/
class Solution {
    public boolean isSymmetric(TreeNode root) {
        return isMirror(root.left, root.right);
    }

    private boolean isMirror(TreeNode node1, TreeNode node2){
        if(node1 == null && node2 == null){
            return true;
        }
        if((node1 == null && node2 != null) || (node1 != null && node2 == null)){
            return false;
        }
        if(node1.val != node2.val){
            return false;
        }

        boolean outerPair = isMirror(node1.left, node2.right);
        boolean innerPair = isMirror(node1.right, node2.left);

        return outerPair && innerPair;
    }
}


// Iterative DFS approach using a stack
/*
Use the same logic as the recursive DFS.
Push the root's left and right children as a pair of nodes.
Apply the same checks:
Both null → continue

One null → asymmetric → return false

Values differ → return false

Then push the mirrored children pairs onto the stack in the correct order
*/
// class Solution {
//     public boolean isSymmetric(TreeNode root) {
//         Stack<TreeNode[]> stack = new Stack<>();
//         stack.push(new TreeNode[]{root.left, root.right});

//         while(!stack.isEmpty()){
//             TreeNode[] pair = stack.pop();
//             TreeNode node1 = pair[0];
//             TreeNode node2 = pair[1];

//             if(node1 == null && node2 == null){
//                 continue;
//             }
//             if((node1 == null && node2 != null) || (node1 != null && node2 == null)){
//                 return false;
//             }
//             if(node1.val != node2.val){
//                 return false;
//             }

//             stack.push(new TreeNode[]{node1.left, node2.right}); // outer pair
//             stack.push(new TreeNode[]{node1.right, node2.left}); // inner pair
//         }

//         return true;
//     }
// }



// BFS
/*
Use the same logic as the iterative DFS.
Push the root's left and right children as a pair of nodes.
Apply the same checks:
Both null → continue

One null → asymmetric → return false

Values differ → return false

Then push the mirrored children pairs onto the queue in the correct order
*/
// class Solution {
//     public boolean isSymmetric(TreeNode root) {
//         Queue<TreeNode[]> queue = new LinkedList<>();
//         queue.offer(new TreeNode[]{root.left, root.right});

//         while(!queue.isEmpty()){
//             TreeNode[] pair = queue.poll();
//             TreeNode node1 = pair[0];
//             TreeNode node2 = pair[1];

//             if(node1 == null && node2 == null){
//                 continue;
//             }
//             if((node1 == null && node2 != null) || (node1 != null && node2 == null)){
//                 return false;
//             }
//             if(node1.val != node2.val){
//                 return false;
//             }

//             queue.offer(new TreeNode[]{node1.left, node2.right}); // outer pair
//             queue.offer(new TreeNode[]{node1.right, node2.left}); // inner pair
//         }

//         return true;
//     }
// }