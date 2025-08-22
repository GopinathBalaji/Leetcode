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

// BFS level-order traversal
// class Solution {
//     public List<Double> averageOfLevels(TreeNode root) {
//         List<Double> ans = new ArrayList<>();
//         Queue<TreeNode> queue = new LinkedList<>();
//         queue.offer(root);
        
//         while(!queue.isEmpty()){
//             int levelSize = queue.size();

//             double sum = 0.0;
//             double count = 0.0;
//             for(int i=0; i<levelSize; i++){
//                 TreeNode node = queue.poll();

//                 sum += node.val;
//                 count++;

//                 if(node.left != null){
//                     queue.offer(node.left);
//                 }
//                 if(node.right != null){
//                     queue.offer(node.right);
//                 }
//             }
            
//             ans.add(sum/count);
//         }

//         return ans;
//     }
// }


// My DFS solution (Using a hashmap to track the sum and count at each depth) NOT OPTIMAL
// class Solution {

//     // Depth, Sum, Count in hashmap
//     private HashMap<Integer, Pair<Double, Double>> map = new HashMap<>();

//     public List<Double> averageOfLevels(TreeNode root) {
//         dfs(root, 0);
//         List<Double> ans = new ArrayList<>();
//         int maxDepth = Collections.max(map.keySet()); // getting maximum depth

//         for(int d=0; d<maxDepth; d++){
//             Pair<Double, Double> level = map.get(d);
//             double avg = level.getKey() / level.getValue();
//             ans.add(avg);
//         }

//         return ans;
//     }

//     private void dfs(TreeNode node, int depth){
//         if(node == null){
//             return;
//         }

//         map.putIfAbsent(depth, new Pair<>(0.0, 0.0));
//         Pair<Double, Double> old = map.get(depth);
//         map.put(depth, new Pair<>(old.getKey() + node.val, old.getValue() + 1));
        
//         dfs(node.left, depth + 1);
//         dfs(node.right, depth + 1);
//     }
// }


// Optimal DFS Solution
/*
Why this is optimal

No HashMap, no sorting: We index directly by depth, so results are already in correct order.
O(n) time: Each node visited once; building averages is O(D) where D = #levels.
O(D) extra space: For sums and counts (plus recursion stack O(h), h = height).
Numerically safe: long prevents overflow when summing many int node values.


Mental model

Carry depth in DFS.
On first visit to a new depth, append 0 entries to sums/counts.
Add current node.val to sums[depth], increment counts[depth].
After traversal, average[depth] = sums[depth] / counts[depth].


NOTE:
Notice how we can find out the first time we are visiting a depth becuase
we only add a node after check if the size equals the current depth.
*/

class Solution {
    // sums.get(d) = total of values at depth d (use long to avoid overflow)
    private final List<Long> sums = new ArrayList<>();
    // counts.get(d) = number of nodes at depth d
    private final List<Integer> counts = new ArrayList<>();

    public List<Double> averageOfLevels(TreeNode root) {
        if (root == null) return Collections.emptyList();

        dfs(root, 0);

        // Build result in order: depth 0..maxDepth
        List<Double> ans = new ArrayList<>(sums.size());
        for (int d = 0; d < sums.size(); d++) {
            ans.add(sums.get(d) / (double) counts.get(d));
        }
        return ans;
    }

    private void dfs(TreeNode node, int depth) {
        if (node == null) return;

        // First time we reach this depth: grow arrays
        if (depth == sums.size()) {
            sums.add(0L);
            counts.add(0);
        }

        // Accumulate sum and count at this depth
        sums.set(depth, sums.get(depth) + (long) node.val);
        counts.set(depth, counts.get(depth) + 1);

        // DFS children
        dfs(node.left, depth + 1);
        dfs(node.right, depth + 1);
    }
}


// Iterative DFS solution
// class Solution {
//     public List<Double> averageOfLevels(TreeNode root) {
//         if (root == null) return Collections.emptyList();

//         List<Long> sums = new ArrayList<>();
//         List<Integer> counts = new ArrayList<>();

//         // Stack holds (node, depth)
//         Deque<Pair<TreeNode, Integer>> stack = new ArrayDeque<>();
//         stack.push(new Pair<>(root, 0));

//         while (!stack.isEmpty()) {
//             Pair<TreeNode, Integer> curr = stack.pop();
//             TreeNode node = curr.getKey();
//             int depth = curr.getValue();

//             if (node == null) continue;

//             // Expand lists if this is the first node at this depth
//             if (depth == sums.size()) {
//                 sums.add(0L);
//                 counts.add(0);
//             }

//             // Accumulate sum and count
//             sums.set(depth, sums.get(depth) + node.val);
//             counts.set(depth, counts.get(depth) + 1);

//             // Push children: left and right (order doesn’t matter here, both will be visited)
//             stack.push(new Pair<>(node.right, depth + 1));
//             stack.push(new Pair<>(node.left, depth + 1));
//         }

//         // Build averages
//         List<Double> ans = new ArrayList<>(sums.size());
//         for (int i = 0; i < sums.size(); i++) {
//             ans.add(sums.get(i) / (double) counts.get(i));
//         }

//         return ans;
//     }
// }