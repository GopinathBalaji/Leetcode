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

// Method 1: Level-order traversal
/*
*/
class Solution {
public:
    vector<int> rightSideView(TreeNode* root) {
        if(root == nullptr){
            return {};
        }

        vector<int> ans;

        queue<TreeNode*> q;
        q.push(root);

        while(!q.empty()){
            int size = q.size();
            vector<int> level;

            for(int i=0; i<size; i++){
                TreeNode* node = q.front();
                q.pop();

                if(node->left != nullptr){
                    q.push(node->left);
                }                
                if(node->right != nullptr){
                    q.push(node->right);
                }

                if(i == size-1){
                    ans.push_back(node->val);
                }
            }
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna