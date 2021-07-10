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
//     Method 1: 
    int minDiffInBST(TreeNode* root) {
       stack<TreeNode*> s1;
        TreeNode* curr = root;
        TreeNode* prev = nullptr;
        int diff = INT_MAX;
        
        while(true){
            while(curr!=NULL){
                s1.push(curr);
                curr = curr->left;
            }
            if(s1.empty()){
                break;
            }
            curr = s1.top();
            s1.pop();
            
            if(prev){
              diff = min(diff,curr->val - prev->val);
            }
            prev = curr;
            curr = curr->right;
        }
        return diff;
    }
};

// Method 2: Recursive DFS (Inorder)
   /*
     int minDiffInBST(TreeNode* root){
        int diff = INT_MAX;
        TreeNode* curr = root;
         TreeNode* prev = nullptr;
        dfs(curr,prev,diff);
        return diff;
    }
    
    void dfs(TreeNode* node,TreeNode* prev,int &diff){
        if(!node) return;
         dfs(node->left,prev,diff);
         if(prev){
            diff = min(diff,node->val - prev->val);
         }
        prev = node;
         dfs(node->right,prev,diff);
    }
   */