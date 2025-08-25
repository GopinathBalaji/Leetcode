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

// My BFS Solution (Not optimal as Collections.reverse(level) costs O(k) per level)
// class Solution {
//     public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
//         List<List<Integer>> ans = new ArrayList<>();
//         if(root == null){
//             return ans;
//         }

//         Queue<TreeNode> queue = new LinkedList<>();
//         queue.offer(root);
//         int depth = 0;

//         while(!queue.isEmpty()){
//             int levelSize = queue.size();
//             List<Integer> level = new ArrayList<>();

//             for(int i=0; i<levelSize; i++){
//                 TreeNode node = queue.poll();

//                 level.add(node.val);

//                 if(node.left != null){
//                     queue.offer(node.left);
//                 }
//                 if(node.right != null){
//                     queue.offer(node.right);
//                 }
//             }

//             if(depth % 2 != 0){
//                 Collections.reverse(level);
//                 ans.add(level);
//             }else{
//                 ans.add(level);
//             }

//             depth++;
//         }

//         return ans;
//     }
// }


// Better BFS Solution (Same idea but better way to do it)
/*
Idea

Keep a boolean leftToRight that flips each level.
Use a LinkedList<Integer> (as a deque) for the current level:
    If leftToRight → addLast(node.val)
    Else → addFirst(node.val)
Use an ArrayDeque<TreeNode> for the BFS queue (faster than LinkedList as a queue).
*/
// class Solution {
//     public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
//         List<List<Integer>> ans = new ArrayList<>();
//         if(root == null){
//             return ans;
//         }

//         Deque<TreeNode> queue = new ArrayDeque<>();
//         queue.offer(root);
//         boolean leftToRight = true;

//         while(!queue.isEmpty()){
//             int levelSize = queue.size();
//             LinkedList<Integer> level = new LinkedList<>();

//             for(int i=0; i<levelSize; i++){
//                 TreeNode node = queue.poll();

//                 if(leftToRight){
//                     level.addLast(node.val);
//                 }else{
//                     level.addFirst(node.val);
//                 }

//                 if(node.left != null){
//                     queue.offer(node.left)
//                 }
//                 if(node.right != null){
//                     queue.offer(node.right);
//                 }
//             }

//             ans.add(level);
//             leftToRight = !leftToRight;
//         }

//         return ans;
//     }
// }


// DFS method
/*
Key Ideas for DFS Zigzag

Carry depth: Each DFS call includes the current depth (starting at 0 for the root).

Result structure: Use List<List<Integer>> levels. Each index corresponds to one depth.

Insert order depends on depth parity (use a Linked List):

If depth is even, append value at the end (add()).

If depth is odd, insert at the front (add(0, val)).

Recurse left and right: The traversal order doesn’t matter for correctness, but usually left→right is more natural.
*/
class Solution {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        dfs(root, 0, ans);
        return ans;
    }

    private void dfs(TreeNode node, int depth, List<List<Integer>> ans) {
        if (node == null) return;

        // Ensure this depth has a list
        if (depth == ans.size()) {
            ans.add(new LinkedList<>()); // use LinkedList for efficient addFirst
        }

        // Even level → add at end; Odd level → add at front
        if (depth % 2 == 0) {
            ans.get(depth).add(node.val);
        } else {
            ans.get(depth).add(0, node.val);
        }

        // Recurse children
        dfs(node.left, depth + 1, ans);
        dfs(node.right, depth + 1, ans);
    }
}



// Iterative DFS
// class Solution {
//     public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
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
//                 ans.add(new LinkedList<>());
//             }

//             if(depth % 2 == 0){
//                 ans.get(depth).add(node.val);   
//             }else{
//                 ans.get(depth).addFirst(node.val);
//             }


//             if(node.right != null){
//                 stack.push(new Pair<>(node.right, depth+1));
//             }
//             if(node.left != null){
//                 stack.push(new Pair<>(node.left, depth+1));
//             }
//         }

//         return ans;
//     }
// }