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


// Method 1: Recursive DFS approach
/*
*/
class Solution {
public:
    int maxDepth(TreeNode* root) {
        if(root == nullptr){
            return 0;
        }

        return std::max(1 + maxDepth(root->left), 1 + maxDepth(root->right));
    }
};





// Method 2: BFS approach
/*
*/
// class Solution {
// public:
//     int maxDepth(TreeNode* root) {
//         if(root == nullptr){
//             return 0;
//         }

//         std::queue<TreeNode*> q;
//         q.push(root);

//         int maxDepth = 0;

//         while(!q.empty()){
//             int size = q.size();
//             maxDepth++;

//             for(int i=0; i<size; i++){
//                 TreeNode* curr = q.front();
//                 q.pop();

//                 if(curr->left != nullptr){
//                     q.push(curr->left);
//                 }
//                 if(curr->right != nullptr){
//                     q.push(curr->right);
//                 }
//             }
//         }

//         return maxDepth
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna