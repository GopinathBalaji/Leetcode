/*
// Definition for a Node.
class Node {
public:
    int val;
    vector<Node*> children;

    Node() {}

    Node(int _val) {
        val = _val;
    }

    Node(int _val, vector<Node*> _children) {
        val = _val;
        children = _children;
    }
};
*/

class Solution {
public:
//     Method 1: Recursive DFS
    vector<int> postorder(Node* root) {
        if(!root) return {};
        vector<int> v1;
       dfs(root,v1); 
        return v1;
    }
    
    void dfs(Node* root, vector<int>& v1){
        if(!root) return;
        
        for(int i=0;i<root->children.size();i++){
            dfs(root->children[i],v1);
        }      
         v1.push_back(root->val);
      }
};

// Method 2: Iterative DFS using two stacks
   /*
    vector<int> postorder(Node* root) {
        if(!root)   return {};
        stack<Node*> parent, precomp;
        
        parent.push(root);
        
        while(!parent.empty()){
           Node* temp  = parent.top();
           precomp.push(temp);
           parent.pop();
           
           for(auto i: temp->children){
              parent.push(i);
           }
           
           vector<int> ans;
           
           while(!precomp.empty()){
              ans.push_back(precomp.top()->val);
              precomp.pop();
           }
           return ans;
        }
   */