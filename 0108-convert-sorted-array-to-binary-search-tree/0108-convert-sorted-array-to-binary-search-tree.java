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

// My Recursive DFS solution (Optimal and correct)
/*
Picking the middle index ensures the tree is height-balanced.
The start > end base case guarantees termination.
The mid formula avoids overflow (good habit).
*/
class Solution {
    public TreeNode sortedArrayToBST(int[] nums) {
        int mid = 0 + (nums.length - 0) / 2;
        TreeNode node = new TreeNode(nums[mid]);

        node.left = dfs(0, mid-1, nums);
        node.right = dfs(mid+1, nums.length-1, nums);

        return node;
    }

    private TreeNode dfs(int start, int end, int[] nums){
        if(start > end){
            return null;
        }

        int mid = start + (end - start) / 2;
        TreeNode node = new TreeNode(nums[mid]);

        node.left = dfs(start, mid-1, nums);
        node.right = dfs(mid+1, end, nums);

        return node;
    }
}


// Better DFS Code (More clean ChatGPT version)
// class Solution {
//     public TreeNode sortedArrayToBST(int[] nums) {
//         return dfs(nums, 0, nums.length-1);
//     }

//     private TreeNode dfs(int[] nums, int start, int end){
//         if(start > end){
//             return null;
//         }

//         int mid = start + (end - start) / 2;
//         TreeNode root = new TreeNode(nums[mid]);

//         root.left = dfs(nums, start, mid-1);
//         root.right = dfs(numn, mid+1, end);

//         return root;
//     }
// }



// Iterative DFS
/*
ranges holds [start, end, side]:
    side = 0 means “root”
    side = 1 means “attach as left”
    side = 2 means “attach as right”

nodes stack keeps the parent for that range.
We push right before left so the left side is processed first when popped, mirroring recursive DFS.
*/
// class Solution {
//     public TreeNode sortedArrayToBST(int[] nums) {
//         if (nums == null || nums.length == 0) return null;

//         // Root node initially unknown, so create a dummy placeholder
//         TreeNode root = new TreeNode(0);

//         // Each stack entry: {start, end, parent node, isLeftChild?}
//         Deque<int[]> ranges = new ArrayDeque<>();
//         Deque<TreeNode> nodes = new ArrayDeque<>();

//         // Ranges holds: start, end, side. Side can be:
//         // 0 = root, 1 = left, 2 = right
//         ranges.push(new int[]{0, nums.length - 1, 0}); 
//         nodes.push(root);

//         while (!ranges.isEmpty()) {
//             int[] range = ranges.pop();
//             TreeNode parent = nodes.pop();

//             int start = range[0], end = range[1], pos = range[2];
//             if (start > end) continue;

//             int mid = start + (end - start) / 2;
//             TreeNode node = new TreeNode(nums[mid]);

//             if (pos == 0) {
//                 root = node; // this is the true root
//             } else if (pos == 1) {
//                 parent.left = node;
//             } else {
//                 parent.right = node;
//             }

//             // Push right then left (so left is processed first, like recursion)
//             ranges.push(new int[]{mid + 1, end, 2});
//             nodes.push(node);

//             ranges.push(new int[]{start, mid - 1, 1});
//             nodes.push(node);
//         }

//         return root;
//     }
// }



// BFS version (Similar method to the iterative DFS)
/*
Start with the full range [0, n-1]. Create the root from its mid.
Push the left range [start, mid-1] and right range [mid+1, end] into a queue, each paired with:
    the parent node to attach to, and
    which side to attach on (left/right).
Repeatedly pop a range, create its mid node, attach it to the parent on the correct side, and enqueue its left/right subranges.
*/
// class Solution {
//     public TreeNode sortedArrayToBST(int[] nums) {
//         if (nums == null || nums.length == 0) return null;

//         // Build root from the whole range
//         int n = nums.length;
//         int mid = (n - 1) / 2;
//         TreeNode root = new TreeNode(nums[mid]);

//         // Queues: one for ranges [start, end, side], one for parent nodes
//         // side: 1 = left child, 2 = right child (root’s children use these)
//         Deque<int[]> ranges = new ArrayDeque<>();
//         Deque<TreeNode> parents = new ArrayDeque<>();

//         // Enqueue left and right subranges with their parent (root)
//         if (mid - 1 >= 0) {
//             ranges.offer(new int[]{0, mid - 1, 1});
//             parents.offer(root);
//         }
//         if (mid + 1 <= n - 1) {
//             ranges.offer(new int[]{mid + 1, n - 1, 2});
//             parents.offer(root);
//         }

//         while (!ranges.isEmpty()) {
//             int[] rg = ranges.poll();
//             TreeNode par = parents.poll();

//             int start = rg[0], end = rg[1], side = rg[2];
//             if (start > end) continue;

//             int m = start + (end - start) / 2;
//             TreeNode node = new TreeNode(nums[m]);

//             // attach to parent
//             if (side == 1) par.left = node;
//             else           par.right = node;

//             // enqueue children ranges with this node as parent
//             if (start <= m - 1) {
//                 ranges.offer(new int[]{start, m - 1, 1});
//                 parents.offer(node);
//             }
//             if (m + 1 <= end) {
//                 ranges.offer(new int[]{m + 1, end, 2});
//                 parents.offer(node);
//             }
//         }

//         return root;
//     }
// }
