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
    int maxDepth(Node* root) {
        if(root==NULL){
            return 0;
        }
       int maxheight = 0;
        for(auto node: root->children){
            maxheight = max(maxheight,maxDepth(node));
        }
       return 1+maxheight;
     }
};

// Method 2: Iterative DFS
   /*
    int maxDepth(Node* root){
    
    stack<pair<Node*,int>> s1;
    int currdepth,maxdepth = 0;
    Node* currnode;
    if(root){
    s1.push({root,1});
    }
    
    while(s1.size()){
       currnode = s1.top().first;
       currdepth = s1.top().second;
       
       s1.pop();
       maxdepth = max(maxdepth,currdepth);
       for(auto child: currnode->children){
          s1.push({child,currdepth+1});
       }
    }
      return maxdepth;
    }
   */

// Method 3: Iterative BFS
   /*
    int maxDepth(Node* root) {
        if (root == NULL) {
            return 0;
        }
        int depth = 0;
        queue<Node*> nodes;
        nodes.push(root);
        while (!nodes.empty()) {
            int currentQueueSize = nodes.size();
            while (currentQueueSize--) {      
                Node* currentNode = nodes.front();
                nodes.pop();
                for (auto child : currentNode->children) {
                    nodes.push(child);
                }
            }
            depth++;
        }
        return depth;
    }
   */

// Method 4: 


