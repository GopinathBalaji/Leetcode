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


// My solution using StringBuilder (finding leafs and adding the path value to sum)
// class Solution {
//     public int sumNumbers(TreeNode root) {

//         int sum = 0;
//         Queue<Pair<TreeNode, StringBuilder>> queue = new LinkedList<>();
//         queue.offer(new Pair<>(root, new StringBuilder().append(root.val)));

//         while(!queue.isEmpty()){
//             Pair<TreeNode, StringBuilder> current = queue.poll();
//             TreeNode node = current.getKey();
//             StringBuilder path = current.getValue();

//             if(node.left == null && node.right == null){
//                 sum += Integer.parseInt(path.toString());
//             }
            
//             if(node.left != null){
//                 queue.offer(new Pair<>(node.left, new StringBuilder(path).append(node.left.val)));
//             }

//             if(node.right != null){
//                 queue.offer(new Pair<>(node.right, new StringBuilder(path).append(node.right.val)));
//             }
//         }

//         return sum;
//     }
// }


// Better BFS Approach without Stringbuilder
// class Solution {
//     public int sumNumbers(TreeNode root) {
//         int sum = 0;
//         Queue<Pair<TreeNode, Integer>> queue = new LinkedList<>();
//         queue.offer(new Pair<>(root, root.val));

//         while(!queue.isEmpty()){
//             Pair<TreeNode, Integer> current = queue.poll();
//             TreeNode node = current.getKey();
//             int currSum = current.getValue();

//             if(node.left == null && node.right == null){
//                 sum += currSum;
//             }

//             if(node.left != null){
//                 queue.offer(new Pair<>(node.left, currSum * 10 + node.left.val));
//             }

//             if(node.right != null){
//                 queue.offer(new Pair<>(node.right, currSum * 10 + node.right.val));
//             }
//         }

//         return sum;
//     }
// }



// Recursive DFS solution
/*
Pass the path sum down before recursion - currSum = currSum * 10 + node.val; is done at 
the start of each call.
Leaf nodes return the full path number, not just node.val.
No mixing of accumulator and path — currSum only tracks the path so far, 
and the recursion naturally sums up numbers from all paths.
*/
class Solution {
    public int sumNumbers(TreeNode root) {
        return dfs(root, 0);
    }

    private int dfs(TreeNode node, int currSum){
        if(node == null){
            return 0;
        }

        currSum = currSum * 10 + node.val;

        if(node.left == null && node.right == null){
            return currSum;
        }

        return dfs(node.left, currSum) + dfs(node.right, currSum);
    }
}


// Iterative DFS version (similar to the Iterative BFS version)
// class Solution {
//     public int sumNumbers(TreeNode root) {
//         int sum = 0;
//         Stack<Pair<TreeNode, Integer>> stack = new Stack<>();
//         stack.push(new Pair<>(root, root.val));

//         while(!stack.isEmpty()){
//             Pair<TreeNode, Integer> current = stack.pop();
//             TreeNode node = current.getKey();
//             int currSum = current.getValue();

//             if(node.left == null && node.right == null){
//                 sum += currSum;
//             }

//             if(node.right != null){
//                 stack.push(new Pair<>(node.right, currSum * 10 + node.right.val));
//             }
//             if(node.left != null){
//                 stack.push(new Pair<>(node.left, currSum * 10 + node.left.val));
//             }
//         }

//         return sum;
//     }
// }