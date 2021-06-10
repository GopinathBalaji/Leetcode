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
    bool hasPathSum(TreeNode* root, int targetSum) {
        
     if(root == NULL){
         return false; 
     }   
        else{
            bool ans = false;
            
            int check = targetSum - root->val;
            
            if(root->left==NULL && root->right==NULL && check==0){
                return true;
            }
            
            if(root->left!=NULL){
                ans = ans||hasPathSum(root->left,check);
            }
            if(root->right!=NULL){
                ans = ans||hasPathSum(root->right,check);
            }
            return ans;
        }
    }
};