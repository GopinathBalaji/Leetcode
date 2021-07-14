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
//     Method 1: Recursive DFS
    bool leafSimilar(TreeNode* root1, TreeNode* root2) {
       vector<int> v1;
        vector<int> v2;
        dfs(root1,v1);
        dfs(root2,v2);
        
        if(v1==v2){
            return true;
        }
        return false;
    }
    
    void dfs(TreeNode* root,vector<int>& vec){
        if(!root) return;
        if(!root->left && !root->right) vec.push_back(root->val);
        
        dfs(root->left,vec);
        dfs(root->right,vec);
    }
};

// Method 2: Iterative DFS
   /*
     bool leafSimilar(TreeNode* root1, TreeNode* root2) {
        vector<int> v1, v2;
        stack<TreeNode*> s;
        s.push(root1);
        while(!s.empty()) {
            TreeNode* current = s.top();
            s.pop();
            if(!current->left && !current->right) {
                v1.push_back(current->val);
            }
            else {
                if(current->left) s.push(current->left);
                if(current->right) s.push(current->right);
            }
        }
        s.push(root2);
        while(!s.empty()) {
            TreeNode* current = s.top();
            s.pop();
            if(!current->left && !current->right) {
                v2.push_back(current->val);
            }
            else {
                if(current->left) s.push(current->left);
                if(current->right) s.push(current->right);
            }
        }
        return v1 == v2;
    }
   */