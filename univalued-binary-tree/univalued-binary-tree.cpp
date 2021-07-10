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
//     Method 1: Preorder DFS Traversal
    bool isUnivalTree(TreeNode* root) {
        stack<TreeNode*> s1;
        TreeNode* prev = root;
        s1.push(root);
        while(!s1.empty()){
             TreeNode* curr = s1.top();
             s1.pop();
            if(prev->val != curr->val){
                return false;
            }            
            prev = curr;
            if(curr->right){
                s1.push(curr->right);
            }
            if(curr->left){
                s1.push(curr->left);
            }
        }
        return true;
    }
};

// Method 2: Recursive DFS
   /*
    public boolean isUnivalTree(TreeNode root) {
    if(root == null)
        return true;
    
    if(root.left != null)
        if(root.val != root.left.val)
            return false;
    
    if(root.right != null)
        if(root.val != root.right.val)
            return false;
    
    return isUnivalTree(root.left) && isUnivalTree(root.right);
}
   */