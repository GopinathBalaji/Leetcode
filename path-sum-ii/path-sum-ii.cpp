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
    vector<vector<int>> pathSum(TreeNode* root, int targetSum) {
        vector<vector<int>> res;
        vector<int> temp;
        int sum = 0;
        search(root,targetSum,sum,temp,res);
        return res;
    }
    
    bool isValidState(int targetsum, int sum, TreeNode* root){
        if(sum == targetsum && root->left==NULL && root->right==NULL){
            return true;
        }
        return false;
    }
    
    void search(TreeNode* root, int targetsum, int sum, vector<int> &temp, vector<vector<int>> &res){
       
        if(root == NULL){
          return;  
        }
        
        temp.push_back(root->val);
        sum += root->val;
         if(isValidState(targetsum, sum, root)){
            res.push_back(temp);
        }
        search(root->left,targetsum,sum,temp,res);
        search(root->right,targetsum,sum,temp,res);
        
        temp.pop_back();
    }
};