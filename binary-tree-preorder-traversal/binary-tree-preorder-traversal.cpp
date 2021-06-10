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
    vector<int> preorderTraversal(TreeNode* root) {
        // vector<int> vec;
        // vec = preorder(root,vec);
        // return vec;
        
        vector<int> vec;
        stack<TreeNode*> s;
        if(root){
        s.push(root);
        }
        while(!s.empty()){
            auto p = s.top();
            s.pop();
            vec.push_back(p->val);
             if(p->right){
                s.push(p->right);
            }
            if(p->left){
                s.push(p->left);
            }
            
        }
        return vec;
    }
    
    
    // vector<int> preorder(TreeNode* root,vector<int> &vec){
    //     if(root==NULL){
    //         return {};
    //     }
    //      vec.push_back(root->val);
    //     preorder(root->left,vec);
    //     preorder(root->right,vec);
    //     return vec;    
    // }
};