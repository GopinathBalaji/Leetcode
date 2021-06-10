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
  
    bool isBalanced(TreeNode* root) {
     
        int lh,rh;
        
        if(root==NULL){
            return true;
        }
        
        lh = height(root->left);
        rh = height(root->right);
        
        if(abs(lh-rh)<=1 && isBalanced(root->left) && isBalanced(root->right)){
            return true;
        }
        return false;
    }
    
    int height(TreeNode* root){
        if(root==NULL){
            return 0;
        }
        else{
       int ldepth = height(root->left);
        int rdepth = height(root->right);
        
        if(ldepth>rdepth){
            return (ldepth + 1);
        }else{
            return (rdepth + 1);
        }
    }
  }
};