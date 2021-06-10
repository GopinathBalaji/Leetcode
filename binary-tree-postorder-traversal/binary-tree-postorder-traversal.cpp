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
    vector<int> postorderTraversal(TreeNode* root) {
//         vector<int> vec;
//         if(root==NULL){
//             return vec;
//         }
        
//         vector<int> x;
//         if(root->left){
//       x =  postorderTraversal(root->left);
//         vec.insert(vec.end(),x.begin(),x.end());
//         }
//         if(root->right){
//        x = postorderTraversal(root->right);
//         vec.insert(vec.end(),x.begin(),x.end());
//         }
//         vec.push_back(root->val);
//         return vec;
        //////////////////
        // vector<int> vec;
        // stack<TreeNode*> s;
        // if(root){
        // s.push(root);
        // }
        // while(!s.empty()){
        //     auto node = s.top();
        //     s.pop();
        //     vec.insert(vec.begin(),node->val);
        //     if(node->left){
        //         s.push(node->left);
        //     }
        //      if(node->right){
        //         s.push(node->right);
        //     }
        // }
        // return vec;
        
          vector<int> res;
        if(!root) {
            return res;
        }
      stack<TreeNode*> s;
      s.push(root);
      while(!s.empty()) {
          TreeNode* curr = s.top();
          res.push_back(curr->val);
          s.pop();
          if(curr->left){
              s.push(curr->left);
          }
          if(curr->right) {
              s.push(curr->right);
          }
      }
        reverse(res.begin(),res.end());
        return res;
    }
};