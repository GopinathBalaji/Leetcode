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

// Method 1: Longest path from a node is leftHeight + rightHeight
/*
*/
class Solution {
private:
    int diameter = 0;

    int dfs(TreeNode* root){
        if(root == nullptr){
            return 0;
        }

        int leftHeight = dfs(root->left);
        int rightHeight = dfs(root->right);

        diameter = std::max(diameter, leftHeight + rightHeight);

        return 1 + std::max(leftHeight, rightHeight);
    }

public:
    int diameterOfBinaryTree(TreeNode* root) {
        dfs(root);
        
        return diameter;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna