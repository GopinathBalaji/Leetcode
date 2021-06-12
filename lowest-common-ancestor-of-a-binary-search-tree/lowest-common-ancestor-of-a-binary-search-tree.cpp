/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode(int x) : val(x), left(NULL), right(NULL) {}
 * };
 */

class Solution {
public:
    TreeNode* lowestCommonAncestor(TreeNode* root, TreeNode* p, TreeNode* q) {
        
        vector<int> pathv1;
        vector<int> pathv2;
       bool a1 = path(root,pathv1,p); 
       bool a2 = path(root,pathv2,q);
           
        int i;
        for(i=0;i<pathv1.size() && i < pathv2.size();i++){
            if(pathv1[i] != pathv2[i]){
                break;
            }
        }
        int a = pathv1[i-1];
         TreeNode* node = new TreeNode(a);
        node->val = a;
        node->left = NULL;
        node->right = NULL;
        
        return node;
    }
    
    bool path(TreeNode* root,vector<int> &vec,TreeNode* node){
        if(root==NULL){
            return false;
        }
        
        vec.push_back(root->val);
        
        if(root->val == node->val){
            return true;
        }
        
        if((root->left && path(root->left,vec,node)) || (root->right && path(root->right,vec,node))){
            return true;
        }
        
       vec.pop_back();
       return false;
    }
};