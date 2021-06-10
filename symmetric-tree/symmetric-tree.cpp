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
//     bool isSymmetric(TreeNode* root) {
    
//      bool ans  = isSimmi(root,root);
//         return ans;
//     }
    
//     bool isSimmi(TreeNode* root1,TreeNode* root2){
//         if(root1==NULL && root2==NULL){
//             return true;
//         }
// if(root1 && root2 && root1->val == root2->val){
//         return (isSimmi(root1->left,root2->right) && isSimmi(root1->right,root2->left));
// }
//         return false;
//     }
    
     bool isSymmetric(TreeNode* root) {
         
         if(root==NULL){
             return true;
         }    
         if(!root->left && !root->right){
             return true;
         }
         
         queue<TreeNode*> q;
         
         q.push(root);
         q.push(root);
         
         TreeNode* leftnode;
         TreeNode* rightnode;
         
         while(!q.empty()){
             
             leftnode = q.front();
             q.pop();
             rightnode = q.front();
             q.pop();
             
             if(leftnode->val != rightnode->val){
                 return false;
             }
             
             if(leftnode->left && rightnode->right){
                 q.push(leftnode->left);
                 q.push(rightnode->right);
             }
             else if(leftnode->left || rightnode->right){
                 return false;
             }
             
             if(leftnode->right && rightnode->left){
                 q.push(leftnode->right);
                 q.push(rightnode->left);
             }
             else if(leftnode->right || rightnode->left){
                 return false;
             }
         }
         return true;
     }
};




