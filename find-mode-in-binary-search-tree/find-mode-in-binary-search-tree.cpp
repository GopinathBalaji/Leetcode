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
//     Method 1: Extra space using HashMap
    unordered_map<int,int> m1;
    vector<int> findMode(TreeNode* root) {
        dfs(root);
        int max = INT_MIN;
        for(auto i:m1){
            if(i.second>max){
                max = i.second;
            }
        }
         vector<int> v1;
        for(auto i:m1){
            if(i.second==max){
                v1.push_back(i.first);
            }
        }
        return v1;
    }
    
    void dfs(TreeNode* root){
         if(!root) return;
        dfs(root->left);
        m1[root->val]++;
        dfs(root->right);
    }
};

// Method 2: Using Constant space
   /*
    vector<int> nodes;
    int p = INT_MIN;
    int cur;
    int mx = INT_MIN;
    
    vector<int> findMode(TreeNode* root){
      inorder(root);
      return nodes;
    }
    
    void inorder(TreeNode* root){
        if(!root) return
        
        if(root->left){
           inorder(root->left);
        }
        
         if(p==root->val){
           cur++;
         }else{
            cur = 1;
         }
        
        if(cur==mx){
          nodes.push_back(root->val);
        }
        if(cur>mx){
          nodes.clear();
          nodes.push_back(root->val);
          mx = cur;
        }
        p = root->val;
         
         if(root->right){
            inorder(root->right);
         }
    }
   */