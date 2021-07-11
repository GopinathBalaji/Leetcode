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
//     Method 1: Using BFS to traverse and HashMap to keep track of parent of each node
//     Erase previous level from map, then add the next level, then check for cousin 
    bool isCousins(TreeNode* root, int x, int y) {
       queue<TreeNode*> q1;
        unordered_map<int,int> m1;
        
        if(root->left){
            q1.push(root->left);
            m1.insert({root->left->val,root->val});
        }
        if(root->right){
            q1.push(root->right);
            m1.insert({root->right->val,root->val});
        }
        
        while(!q1.empty()){
            int s = q1.size();
            while(s){
                TreeNode* node = q1.front();
                q1.pop();
                m1.erase(node->val);
                if(node->left){
                    q1.push(node->left);
                    m1.insert({node->left->val,node->val});
                }
                if(node->right){
                    q1.push(node->right);
                    m1.insert({node->right->val,node->val});
                }
                --s;
            }
            if(m1.find(x)!=m1.end() && m1.find(y)!=m1.end() && m1[x]!=m1[y]){
                return true;
            }
        }
        return false;
    }
};

// Method 2: Only BFS
   /*
    bool isCousin(TreeNode* root,int x,int y){
         queue<TreeNode*> q;
         q.push(root);
         int size = 1, children = 0, count_cousin = 0;
         bool cousin = false;
         while(!q.empty() && size>0){
              while(size>0){
                    TreeNode* curr = q.front();
                    q.pop();
                    if(curr->left && curr->right && ((curr->left->val==x && curr->right->val==y)) || (curr->left->val==y && curr->right->val==x)){
                          cousin = false;
                          break;
                    }
                    if(curr->left){
                       if(curr->left->val==x || curr->left->val==y){
                         count_cousin++;
                       }
                       q.push(curr->left);
                       children++;
                    }
                    
                    if(curr->right){
                       if(curr->right->val==x || curr->right->val==y){
                         count_cousin++;
                       }
                       q.push(curr->right);
                       children++;
                    }
                    size--;
              }
              if(count_cousin==2){
                 cousin = true;
                 break
              }
              cousin = false;
              size = children;
              children =0;
              count_cousin = 0;
         }
         return cousin;
    }
   */

// Method 3: Finding parent and level by Recursion
   /*
    int getParent(TreeNode* currRoot, int find)
    { 
        if(currRoot == NULL || find == NULL)
            return 0;
        
        else if((currRoot->left!=NULL && currRoot->left->val == find) || 
                (currRoot->right!=NULL && currRoot->right->val == find))
            return currRoot->val;
        
        else
        {
            int parent = getParent(currRoot->left, find);
            
            if(parent == 0)
                parent = getParent(currRoot->right, find);
            
            return parent;
        }
        
    }
    
    int getLevel(TreeNode* currRoot, int find, int lvl)
    {
        if(currRoot == NULL)
            return 0;
        
        if(currRoot->val == find)
            return lvl;
        
        int Level = getLevel(currRoot->left, find, lvl+1);
        if(Level != 0)
            return Level;
        
        Level = getLevel(currRoot->right, find, lvl+1);
        
        return Level;
        
        
    }
    
    bool isCousins(TreeNode* root, int x, int y)
    {
        int levelX = getLevel(root, x, 0);
        int levelY = getLevel(root, y, 0);
        
        int parentX = getParent(root, x); 
        int parentY = getParent(root, y);
        
        if(levelX == levelY && parentX != parentY)
            return true;
        
        else
            return false;
    
        
    }
   */

// Method 4: Recursive DFS using Inorder
   /*
    void solve(TreeNode* root, int x,int &xd,int y,int &yd,int depth,TreeNode* prev,int &p1,int &p2){
        if(!root) return;
        
        solve(root->left,x,xd,y,yd,depth+1,root,p1,p2);
        if(root->val==x){
            xd=depth;
            p1=prev->val;
        }
        
        if(root->val==y){
            yd=depth;
            p2=prev->val;
        }
        
        solve(root->right,x,xd,y,yd,depth+1,root,p1,p2);
        
        

    }
    bool isCousins(TreeNode* root, int x, int y) {
        int xd,yd;
        int p1,p2;
        solve(root,x,xd,y,yd,0,root,p1,p2);
        
        return xd==yd && p1!=p2;
    }
   */












