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

// Recursive DFS solution  
class Solution {
    public boolean hasPathSum(TreeNode root, int targetSum) {
        return dfs(root, targetSum, 0);
    }

    private boolean dfs(TreeNode node, int targetSum, int sum){
        if(node == null){
            return false;
        }

        sum += node.val;

        if(node.left == null && node.right == null && sum == targetSum){
            return true;
        }

        return dfs(node.left, targetSum, sum) || dfs(node.right, targetSum, sum);
    }
}


// Iterative DFS using Pair<> to store both, the node and the current sum in a stack
// class Solution {
//     public boolean hasPathSum(TreeNode root, int targetSum) {
//         if(root == null){
//             return root;
//         }

//         Stack<Pair<TreeNode, Integer>> stack = new Stack<>();
//         stack.push(new Pair<>(root, root.val));

//         while(!stack.isEmpty()){
//             Pair<TreeNode, Integer> current = stack.pop();
//             TreeNode node = current.getKey();
//             int sum = current.getValue();

//             if(node.left == null && node.right == null && sum == targetValue){
//                 return true;
//             }

//             if(node.right != null){
//                 stack.push(new Pair<>(node.right, sum + node.right.val));
//             }
//             if(node.left != null){
//                 stack.push(new Pair<>(node.left, sum + node.left.val));
//             }
//         }

//         return false;
//     }
// }


// BFS solution using Pair<> to hold both, the node and the current sum in a queue 
// class Solution {
//     public boolean hasPathSum(TreeNode root, int targetSum) {
//         if(root == null){
//             return false;
//         }

//         Queue<Pair<TreeNode, Integer>> queue = new LinkedList<>();

//         queue.offer(new Pair<>(root, root.val));

//         while(!queue.isEmpty()){
//             Pair<TreeNode, Integer> current = queue.poll();

//             TreeNode node = current.getKey();
//             int sum = current.getValue();

//             if(node.left == null && node.right == null && sum == targetSum){
//                 return true;
//             }

//             if(node.left != null){
//                 queue.offer(new Pair<>(node.left, sum + node.left.val));
//             }

//             if(node.right != null){
//                 queue.offer(new Pair<>(node.right, sum + node.right.val));
//             }
//         }

//         return false;
//     }
// }