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
    TreeNode* mergeTrees(TreeNode* root1, TreeNode* root2) {
//         Method 1: Iterative BFS (Using queue)
         if(root1==NULL){
            return root2;
          } 
        if(root2==NULL){
            return root1;
        }
        
        queue<TreeNode*> q1;
        q1.push(root1);
        queue<TreeNode*> q2;
        q2.push(root2);
        
        while(!q1.empty() && !q2.empty()){
            TreeNode* node1 = q1.front();
            TreeNode* node2 = q2.front();
            q1.pop();
            q2.pop();
            
            node1->val += node2->val;
            
            if(!node1->left && node2->left){
                node1->left = node2->left;
            }else if(node1->left && node2->left){
                q1.push(node1->left);
                q2.push(node2->left);
            }
            
            if(!node1->right && node2->right){
                node1->right = node2->right;
            }else if(node1->right && node2->right){
                q1.push(node1->right);
                q2.push(node2->right);
            }  
        }
        return root1;
    }
};

// Method 2: Iterative DFS (using stack)
   /*
     TreeNode* mergeTrees(TreeNode* t1, TreeNode* t2) {
        if(!t1 && !t2) return nullptr;
        if(!t1 || !t2) return t1? t1:t2;
				
        stack<TreeNode*> s1,s2;
        s1.push(t1);
        s2.push(t2);
        
        while(!s1.empty()){
            TreeNode* c1(s1.top());
            TreeNode* c2(s2.top());
            s1.pop();
            s2.pop();
            
            c1->val+=c2->val;
            
            if(!c1->left && c2->left) c1->left = c2->left;
            else if(c1->left && c2->left) { s1.push(c1->left); s2.push(c2->left); }
            
            if(!c1->right && c2->right) c1->right = c2->right;
            else if(c1->right && c2->right) { s1.push(c1->right); s2.push(c2->right); }
        }
        return t1;
    }
   */

// Method 3: Recursive DFS using Preorder
   /*
    TreeNode* mergeTrees(TreeNode* t1, TreeNode* t2) {
        if(!t1) return t2;
        if(!t2) return t1;
        t1->val+=t2->val;
        if(t2->left) t1->left = mergeTrees(t1->left,t2->left);
        if(t2->right) t1->right = mergeTrees(t1->right,t2->right);
        return t1;
    }
    
    ----------------OR------------
    
    TreeNode* mergeTrees(TreeNode* t1, TreeNode* t2) {
        if(!t1 && !t2) return nullptr;
        if(!t1) return t2;
        if(!t2) return t1;
        t1->val+=t2->val;
        t1->left = mergeTrees(t1->left,t2->left);
        t1->right = mergeTrees(t1->right,t2->right);
        return t1;
    }
   */