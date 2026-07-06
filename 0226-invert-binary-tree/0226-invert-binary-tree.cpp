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

// Method 1: Iterative approach
/*
*/
class Solution {
public:
    TreeNode* invertTree(TreeNode* root) {
        if(root == nullptr){
            return nullptr;
        }

        stack<TreeNode*> st;
        st.push(root);

        while(!st.empty()){
            TreeNode* curr = st.top();
            st.pop();

            TreeNode* temp = curr->left;
            curr->left = curr->right;
            curr->right = temp;

            if(curr->left != nullptr){
                st.push(curr->left);
            }
            if(curr->right != nullptr){
                st.push(curr->right);
            }
        }

        return root;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna