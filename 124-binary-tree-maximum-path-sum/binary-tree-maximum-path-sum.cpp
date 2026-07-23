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
int maxSum = INT_MIN;

int dfs(TreeNode* root){
    if(root == nullptr){
        return 0;
    }

    int leftGain = dfs(root->left);
    int rightGain = dfs(root->right);

    leftGain = std::max(0, leftGain);
    rightGain = std::max(0, rightGain);

    maxSum = std::max(maxSum, root->val + leftGain + rightGain);

    return root->val + std::max(leftGain, rightGain);
}

public:
    int maxPathSum(TreeNode* root) {
        dfs(root);

        return maxSum;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna