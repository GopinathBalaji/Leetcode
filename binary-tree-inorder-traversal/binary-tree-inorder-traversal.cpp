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
    vector<int> inorderTraversal(TreeNode* root) {
        stack<TreeNode *>stack;
        vector<int> vec;
        TreeNode* curr = root;
        
        while(curr != NULL || stack.empty() == false){
            
            while(curr!=NULL){
                stack.push(curr);
                curr=curr->left;
            }
            
            curr=stack.top();
            stack.pop();
            vec.push_back(curr->val);
            curr=curr->right;
        }
        return vec;
    }
};