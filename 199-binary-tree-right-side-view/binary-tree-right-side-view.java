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

// Method 1: Recursive DFS approach
/*
HINTS FOR DFS (both recursive and iterative):
Do a preorder DFS but visit right child before left.
Track the current depth (root depth = 0).
Maintain a list ans.
When you first reach a depth you haven’t seen yet (depth == ans.size()), add that node’s value.
Because you go right-first, the first node you see at each depth is the right-side view.

######################################

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
    public List<Integer> rightSideView(TreeNode root) {
        if(root == null){
            return new ArrayList<>();
        }

        List<Integer> ans = new ArrayList<>();
        dfs(root, 0, ans);

        return ans;
    }

    private void dfs(TreeNode root, int depth, List<Integer> ans){
        if(root == null){
            return;
        }

        if(depth == ans.size()){
            ans.add(root.val);
        }

        dfs(root.right, depth + 1, ans);
        dfs(root.left, depth + 1, ans);
    }
}






// Method 2: Iterative DFS Approach
/*
Stack order: We push left then right. Since stack is LIFO, the right 
child is popped first, ensuring right-first DFS.

Depth tracking: Store (node, depth) in the stack. That’s how we know when 
to add a new value to the result.

Condition: if (depth == ans.size()) ensures we only add the first 
node encountered at that depth (the rightmost).
*/
// class Solution {
//     static class Pair{
//         TreeNode node;
//         int depth;
//         Pair(TreeNode node, int depth){
//             this.node = node;
//             this.depth = depth;
//         }
//     }

//     public List<Integer> rightSideView(TreeNode root) {
//         if(root == null){
//             return new ArrayList<>();
//         }

//         List<Integer> ans = new ArrayList<>();

//         Deque<Pair> stack = new ArrayDeque<>();
//         stack.push(new Pair(root, 0));

//         while(!stack.isEmpty()){
//             Pair pair = stack.pop();
//             TreeNode node = pair.node;
//             int depth = pair.depth;

//             if(depth == ans.size()){
//                 ans.add(node.val);
//             }

//             if(node.left != null){
//                 stack.push(new Pair(node.left, depth + 1));
//             }
//             if(node.right != null){
//                 stack.push(new Pair(node.right, depth + 1));
//             }
//         }

//         return ans;
//     }
// }







// Method 3: BFS approach
/*
HINTS FOR BFS:
Use a queue.
Process the tree level by level.
For each level, loop size = queue.size() times.
As you pop nodes in that level, keep updating a variable lastVal (or if you iterate i=0..size-1, take the node when i==size-1).
After finishing the level, append that lastVal to the answer.
Push children into the queue (left then right is fine, because you’re explicitly taking the last node of the level).
*/
// class Solution {
//     public List<Integer> rightSideView(TreeNode root) {

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