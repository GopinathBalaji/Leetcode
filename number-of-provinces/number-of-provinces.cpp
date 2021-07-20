class Solution {
public:
  vector<int> parent;
    int findCircleNum(vector<vector<int>>& isConnected) {
       parent.resize(isConnected.size(), -1);
        int count = 0;
        
        for(int i=0;i<isConnected.size()-1;i++){
            for(int j=i+1;j<isConnected.size();j++){
                if(isConnected[i][j]){
                    Union(i,j);
                }
            }
        }
        
        for(int i: parent){
            if(i == -1){
                count++;
            }
        }
        return count;
    }
    
    
    void Union(int from, int to){
        from = findroot(from);
        to = findroot(to);
        
        if(from != to){
        parent[from] = to;
        }
    }
    
    int findroot(int v){
        if(parent[v] == -1){
            return v;
        }
       return findroot(parent[v]);
    }
};

// Method 2: DFS
   /*
     int findCircleNum(vector<vector<int>>& isConnected) {
         vector<int> visited(isConnected.size(), 0);
         int count = 0;
         for(int i=0;i<isConnected.size();i++){
            if(!vis[i]){
              dfs(isConnected, vis, i);
              count++;
            }
         }
         return count;
     }
     
     void dfs(vector<vector<int>>& isConnected, vector<int> &visited, int v){
        if(vis[v]) return;
        
        vis[v] = 1;
        vector<int> nodes = isConnected[v];
        
        for(int i=0;i<nodes.size();i++){
           if(nodes[i] != 0){
             dfs(isConnected, vis, i);
           }
        }
     }
   */







