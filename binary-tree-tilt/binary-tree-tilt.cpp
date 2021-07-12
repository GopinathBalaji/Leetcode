/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode() : val(0), left(nullptr), right(nullptr) {}
 *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
 *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 * };
 */
class Solution {
public:
//     Method 1: Post-Order DFS using Recursion
    
    int sum = 0;
    int findTilt(TreeNode* root) {
        dfs(root);
        return sum;
    }
    
   int dfs(TreeNode* root){
        if(!root){
            return 0;
        }
       int leftsum = dfs(root->left);
       int rightsum = dfs(root->right);
       int tilt = abs(leftsum - rightsum);
       sum += tilt;
      
       return root->val + leftsum + rightsum;
    }
};


// Method 2: Iterative PostOrder DFS
   /*
     public int findTilt(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int tilt = 0;
        Stack<TreeNode> stack = new Stack<>();
        Map<TreeNode, Integer> map = new HashMap<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.peek();
            if ((node.left == null || map.containsKey(node.left)) &&
                (node.right == null || map.containsKey(node.right))) {
                stack.pop();
                int left = map.containsKey(node.left) ? map.get(node.left) : 0;
                int right = map.containsKey(node.right) ? map.get(node.right) : 0;
                tilt += Math.abs(left - right);
                map.put(node, left + right + node.val);
            } else {
                if (node.left != null && !map.containsKey(node.left)) {
                    stack.push(node.left); 
                }
                
                if (node.right != null && !map.containsKey(node.right)) {
                    stack.push(node.right);
                }      
            }
        }
        return tilt;
    }
   */