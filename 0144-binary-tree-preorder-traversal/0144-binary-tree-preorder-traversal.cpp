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


// Method 1: Iteravtive Preorder traversal using Stack
/*
*/
class Solution {
public:
    vector<int> preorderTraversal(TreeNode* root) {
        if(root == nullptr){
            return {};
        }

        vector<int> ans;

        stack<TreeNode*> st;
        st.push(root);
        

        while(!st.empty()){
            TreeNode* curr = st.top();
            st.pop();

            ans.push_back(curr->val);

            if(curr->right != nullptr){
                st.push(curr->right);
            }

            if(curr->left != nullptr){
                st.push(curr->left);
            }
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna