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

// Method 1: Iterative inorder traversal using Stack
/*
*/
class Solution {
public:
    vector<int> inorderTraversal(TreeNode* root) {
        if(root == nullptr){
            return {};
        }

        vector<int> ans;
        stack<TreeNode*> stack;

        TreeNode* curr = root;
        
        while(curr != nullptr || !stack.empty()){

            while(curr != nullptr){
                stack.push(curr);
                curr = curr->left;
            } 

            curr = stack.top();
            stack.pop();

            ans.push_back(curr->val);

            curr = curr->right;
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna