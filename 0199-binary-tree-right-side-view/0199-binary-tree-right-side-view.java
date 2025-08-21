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

// Recursive DFS Solution by tracking depth and visiting right nodes first
/*
Since we start tracking depth from 0 and not 1, whenever we visit other nodes 
at the same depth we won't add them to the answer list since 
the condition if (depth == ans.size()) ans.add(node.val); won't satisfy.
This is because the number of nodes in the list will be 1 greater whenever we 
visit other nodes at the same level.

Also:
Don’t use depth++ in recursive calls. With post-increment, the right 
call gets depth, then depth is incremented, and the left call ends up 
with depth+1 more—so depths drift. Use depth + 1 for both calls.
*/
class Solution {
    
    private List<Integer> ans = new ArrayList<>();

    public List<Integer> rightSideView(TreeNode root) {
        if(root == null){
            return ans;
        }

        dfs(root, 0);

        return ans;
    }

    private void dfs(TreeNode node, int depth){
        if(node == null){
            return;
        }

        if(depth == ans.size()){
            ans.add(node.val);  // first time we reach this depth → rightmost due to order
        }

        dfs(node.right, depth + 1);  // right first 
        dfs(node.left, depth + 1);   // then left
    }
}


// Level-order BFS solution: add the last node at each level
// class Solution {
//     public List<Integer> rightSideView(TreeNode root) {
//         List<Integer> ans = new ArrayList<>();
//         if(root == null){
//             return ans;
//         }

//         Queue<TreeNode> queue = new LinkedList<>();
//         queue.offer(root);

//         while(!queue.isEmpty()){
//             int levelSize = queue.size();

//             for(int i=0; i<levelSize; i++){
//                 TreeNode node = queue.poll();

//                 if(i == levelSize - 1){
//                     ans.add(node.val);
//                 }

//                 if(node.left != null){
//                     queue.offer(node.left);
//                 }
//                 if(node.right != null){
//                     queue.offer(node.right);
//                 }
//             }
//         }

//         return ans;
//     }
// }


// Iterative DFS solution
/*
Stack order: We push left then right. Since stack is LIFO, the right 
child is popped first, ensuring right-first DFS.

Depth tracking: Store (node, depth) in the stack. That’s how we know when 
to add a new value to the result.

Condition: if (depth == ans.size()) ensures we only add the first 
node encountered at that depth (the rightmost).
*/

// class Solution {
//     public List<Integer> rightSideView(TreeNode root) {
//         List<Integer> ans = new ArrayList<>();
//         if (root == null) return ans;

//         // Stack will hold (node, depth)
//         Stack<Pair<TreeNode, Integer>> stack = new Stack<>();
//         stack.push(new Pair<>(root, 0));

//         while (!stack.isEmpty()) {
//             Pair<TreeNode, Integer> current = stack.pop();
//             TreeNode node = current.getKey();
//             int depth = current.getValue();

//             if (node != null) {
//                 // First time we see this depth → rightmost node
//                 if (depth == ans.size()) {
//                     ans.add(node.val);
//                 }

//                 // Push right AFTER left, so right is processed first
//                 stack.push(new Pair<>(node.left, depth + 1));
//                 stack.push(new Pair<>(node.right, depth + 1));
//             }
//         }

//         return ans;
//     }
// }


// Side note: You can also use AbstractMap.SimpleEntry instead of Pair 
// Works in the same way