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
//     Method 1: Brute Force Recursive DFS
    int rangeSumBST(TreeNode* root, int low, int high) {
        int sum = 0;
        dfs(root,low,high,sum);
        return sum;
    }
    
    void dfs(TreeNode* root,int low,int high,int &sum){
        if(!root) return;
        
        if(root->val>=low && root->val<=high) sum += root->val;
        dfs(root->left,low,high,sum);
        dfs(root->right,low,high,sum);
    }
};

// Method 2: Iterative DFS
   /*
    int rangeSumBST(TreeNode* root,int low,int high){
        int sum = 0;
        stack<TreeNode*> s1;
        s1.push(root);
        while(!s1.empty()){
           TreeNode* curr = s1.top();
           s1.pop();
           if(curr->val>=low && curr->val<=high){
             sum += curr->val;
           } 
           if(curr->right){
             s1.push(curr->right);
           }
           if(curr->left){
              s1.push(curr->left);
           }
        }
        return sum;
    }
   */

// Method 3: Only search necessary parts
//  If current node is in range then add. If current node is greater than low then search left 
//  subtree. If node is lesser than high then search right subtree.
   /*
    int sum = 0;
void preorder_traversal(TreeNode* root, int low, int high){
	ios::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
	
    if(root == nullptr){
        return;
    }
    
    if(root->val <=high && root->val >=low)
        sum += root->val;
    
    if(root->val >= low)
    preorder_traversal(root->left, low, high);
    
    if(root->val <= high)
    preorder_traversal(root->right, low, high);
}
int rangeSumBST(TreeNode* root, int low, int high) {
    preorder_traversal(root, low, high);
    return sum;
}
   */











