class Solution {
public:
//     Method 1: DFS
    // Check for 3 things:
// One root (using indegree)
// No bidirectional edge or cycle (using dfs)
// One connected components (using visited array)

//     Observation: The index represent the node and the value at that index represent the node value
//              Eg) At index 2 of leftChild, the value is 3 in Example 1. Hence it can be seen that the 
//                   the left value of 2 is 3 as shown in the graph
    
      int flag = 0;
    bool validateBinaryTreeNodes(int n, vector<int>& leftChild, vector<int>& rightChild) {
          vector<int> vis(n,0);
        vector<int> indeg(n,0);
        int root = 0;
        
        for(int i=0;i<n;i++){
            if(leftChild[i] != -1)  indeg[leftChild[i]]++;
            if(rightChild[i] != -1) indeg[rightChild[i]]++;
        }
        
        int count = 0;
        for(int i=0;i<n;i++){
            if(indeg[i] == 0){
                count++;
                root = i;
            }
        }
        if(count != 1) return false;
        
        dfs(root,leftChild, rightChild, vis);
        
        for(int i=0;i<n;i++){
            if(vis[i]==0) return false;
        }
        
        if(flag == 1) return false;
        
        return true;
    }
    
    void dfs(int v, vector<int>& leftchild, vector<int>& rightchild, vector<int>& vis){
        
        vis[v] = 1;
        
        if(leftchild[v] != -1){
            if(vis[leftchild[v]]==1){
                flag = 1; // shows that there is bidirectional edge or cycle.
                return;
            }
            else{
                dfs(leftchild[v],leftchild, rightchild, vis);
            }
        }
        
        if(rightchild[v] != -1){
            if(vis[rightchild[v]] == 1){
                flag = 1;
                return;
            }
            else{
                dfs(rightchild[v],leftchild,rightchild,vis);
            }
        }
    }
};






