class Solution {
public:
//     Method 1: Union Find
//      Find the number of connected components in the network
    
      vector<int> parent;
    int makeConnected(int n, vector<vector<int>>& connections) {
        if(connections.size() < n-1){
            return -1;
        }
        for(int i=0;i<n;i++){
            parent.push_back(i);
        }
        for(auto& v: connections){
            int a = findroot(v[0]);
            int b = findroot(v[1]);
             uni(a,b);   
        }
        
        int ans = 0;
        for(int i=0;i<parent.size();i++){
           if(parent[i]==i){
               ans++;
           }
        }
       return ans-1;
    }
    
    int findroot(int node){
        if(node != parent[node]){
            parent[node] = findroot(parent[node]);
        }
        return parent[node];
    }
    
    void uni(int a,int b){
        parent[a] = b;
    }
};

// Method 2: DFS to find Number of Connected Components
   /*
    void dfs_traversal(vector<vector<int>>& graph , vector<bool>& visited , int start){
        visited[start]=true;
        for(auto it : graph[start]){
            if(!visited[it]){
                dfs_traversal(graph , visited , it );
            }
        }
    }
    
    int makeConnected(int n, vector<vector<int>>& connections) {
        if(n>connections.size()+1)
            return -1;
        
        vector<vector<int>> graph(n);
        for(auto con : connections){
            graph[con[0]].push_back(con[1]);
            graph[con[1]].push_back(con[0]);
        }
        vector<bool> visited(n ,false);
        int component=0;
        for(int i=0;i<n;i++){
            if(!visited[i]){
                component++;
                dfs_traversal(graph , visited , i);
            }
        }
        return component-1;
    }
   */