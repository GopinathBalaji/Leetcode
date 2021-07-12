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
//     Method 1: Use DFS to Recursively calculate the height of left and right subtrees. 
    // Update the height every iteration to max of left and right subtree. And Diameter will be
    // height of left and right subtrees plus 1.
        
    int count = 0;
    int diameterOfBinaryTree(TreeNode* root) {
        count = INT_MIN;
        int a = dfs(root);
        return count-1;  //node=edge+1, i.e; edge=node-1
    }
    
    int dfs(TreeNode* root){
        int h = 0;
        if(!root){    
            return 0;  //base case
        }
       int a = dfs(root->left);  //recursing down and calculating the Height
       int b = dfs(root->right);
        
        h = max(a,b);   //maximum height
        count = max(count,a+b+1);  //updating the maximum diameter
        
        h = h+1;  //increasing heigh covering every node
        return h;  //returning the maximum height
    }
    
    
//     ----------------OR----------------
    /*
      int findDiam(TreeNode* root, int &diam){
        if(root == NULL){
            return 0;
        }
        int leftheight = findDiam(root->left,diam);
        int rightheight = findDiam(root->right,diam);
        if(leftheight + rightheight > diam){
            diam = leftheight + rightheight;
        }
        return 1 + max(leftheight,rightheight);
    }
public:
    int diameterOfBinaryTree(TreeNode* root) {
        int diam = 0;
        int res = findDiam(root,diam);
        return diam;
    }
    */
};


// Method 2: Iterative DFS
 // Iterative one using postorder traversal,
// For each node calculate the max of the left and right sub trees depth and also simultaneously
// caluculate the overall max of the left and right subtrees count.
   /*
   
    int diameterOfBinaryTree(TreeNode* root) {
        if (!root) {
            return 0;
        }
        int diameter = 0;
        unordered_map<TreeNode*, int> depths;
        stack<TreeNode*> todo;
        todo.push(root);
        while (!todo.empty()) {
            TreeNode* node = todo.top();
            if (node -> left && depths.find(node -> left) == depths.end()) {
                todo.push(node -> left);
            } else if (node -> right && depths.find(node -> right) == depths.end()) {
                todo.push(node -> right);
            } else {
                todo.pop();
                int l = depths[node -> left], r = depths[node -> right];
                depths[node] = max(l, r) + 1;
                diameter = max(diameter, l + r);
            }
        }
        return diameter;
    }
   */