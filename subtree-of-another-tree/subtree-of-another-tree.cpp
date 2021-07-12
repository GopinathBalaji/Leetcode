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
    bool isSubtree(TreeNode* root, TreeNode* subRoot) {
        if(!root) return false;
        
        if(dfs(root,subRoot)){
          return true;
        }
        
        return isSubtree(root->left,subRoot) or isSubtree(root->right    ,subRoot);
    }
    
    bool dfs(TreeNode* root,TreeNode* subRoot){
      if(!root and !subRoot) return true;
      if(!root or !subRoot) return false;
      
        if(root->val != subRoot->val) return false;
        
        return (dfs(root->left,subRoot->left) && dfs(root->right,subRoot->right));
    }
};

// Method 2: Iterative BFS and Recursive DFS
   /*bool isSubtree(TreeNode* root,TreeNode* subRoot){
        queue<TreeNode*> bfs;
        bfs.push(root);
        while(!bfs.empty()){
          int n = bfs.size();
          for(int i=0;i<n;i++){
            TreeNode* node = bfs.top();
            bfs.pop();
            bool hassubtree = dfs(node,subRoot);
            if(hassubtree){
              return true;
            }else{
              if(node->left){
                 dfs.push(node->left);
              }
              if(node->right){
                 dfs.push(node->right);
              }
            }
          }
        }
        return false;
      }
        
    bool dfs(TreeNode* root,TreeNode* subRoot){
      if(!root and !subRoot) return true;
      if(!root or !subRoot) return false;
      
        if(root->val != subRoot->val) return false;
        
        return (dfs(root->left,subRoot->left) && dfs(root->right,subRoot->right));
    } 
   */


// Method 3: Fully Iterative DFS
   /*
     public boolean isSubtree(TreeNode s, TreeNode t) {
       
        Deque<TreeNode> stack = new LinkedList<>();
        stack.push(s);
        boolean same = false;
        
        while(!stack.isEmpty() && !same){
            
            TreeNode node = stack.pop();
            
            if(node.val == t.val){
               same = isSame(node, t);
            }
            
            if(node.left != null) stack.push(node.left);
            if(node.right != null) stack.push(node.right);
            
        }
        return same;
    }
    
    private boolean isSame(TreeNode p, TreeNode q){
        
        Deque<TreeNode> stack = new LinkedList<>();
        
        stack.push(p);
        stack.push(q);
        
        while(!stack.isEmpty()){
            
            TreeNode a = stack.pop();
            TreeNode b = stack.pop();
            
            if(a == null && b == null) continue;
        
            if(a == null || b == null) return false;
            
            if(a.val != b.val){
                return false;
            }
            
            stack.push(a.left);
            stack.push(b.left);
            stack.push(a.right);
            stack.push(b.right);
        }
        
        return true;
    }
   */