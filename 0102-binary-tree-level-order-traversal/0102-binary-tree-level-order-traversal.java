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


// Method 1: Using BFS
/*
*/
class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();

        if(root == null){
            return ans;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while(!queue.isEmpty()){
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();

            for(int i=0; i<levelSize; i++){
                TreeNode node = queue.poll();
                level.add(node.val);

                if(node.left != null){
                    queue.offer(node.left);
                }
                if(node.right != null){
                    queue.offer(node.right);
                }
            }

            ans.add(level);
        }   

        return ans;
    }
}







// Method 2: Recursive DFS Solution
/*
*/
// class Solution {

//     private List<List<Integer>> ans = new ArrayList<>();

//     public List<List<Integer>> levelOrder(TreeNode root) {
//         if(root == null){
//             return ans;
//         }
//         dfs(root, 0);

//         return ans;
//     }

//     private void dfs(TreeNode node, int depth){
//         if(node == null){
//             return;
//         }

//         if(ans.size() == depth){
//             ans.add(new ArrayList<>());
//         }
//         ans.get(depth).add(node.val);

//         dfs(node.left, depth + 1);
//         dfs(node.right, depth + 1);
//     }
// }








// Method 3: Iterative DFS Solution
/*
*/
// class Solution {
//     public List<List<Integer>> levelOrder(TreeNode root) {
//         List<List<Integer>> ans = new ArrayList<>();
//         if(root == null){
//             return ans;
//         }

//         Deque<Pair<TreeNode, Integer>> stack = new ArrayDeque<>();
//         stack.push(new Pair<>(root, 0));

//         while(!stack.isEmpty()){
//             Pair<TreeNode, Integer> curr = stack.pop();
//             TreeNode node = curr.getKey();
//             int depth = curr.getValue();

//             if(depth == ans.size()){
//                 ans.add(new ArrayList<>());
//             }

//             ans.get(depth).add(node.val);

//             // Push right first, then left → so left pops first
//             if(node.right != null){
//                 stack.push(new Pair<>(node.right, depth + 1));
//             }
//             if(node.left != null){
//                 stack.push(new Pair<>(node.left, depth + 1));
//             }
//         }
        
//         return ans;
//     }
// }
