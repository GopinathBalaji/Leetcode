class Solution {
public:
//     Method 1: Graph Coloring using BFS
    bool isBipartite(vector<vector<int>>& graph) {
         vector<int> color(graph.size(),-1);
        
        for(int i=0;i<graph.size();i++){
            if(color[i]  != -1) continue;
            
            color[i] = 1;
            queue<int> q;
            q.push(i);
            
            while(!q.empty()){
                int t = q.front();
                q.pop();
                
                for(int j=0;j<graph[t].size();j++){
                    if(color[graph[t][j]] == -1){
                        color[graph[t][j]] = 1-color[t];
                        q.push(graph[t][j]);
                    }else if(color[graph[t][j]] == color[t]){
                        return false;
                    }
                }
            }
        }
        return true;
    }
};

// Method 2: Graph Coloring using DFS
   /*
     bool dfs(vector<vector<int>>& graph, vector<int>& color, int node, int c)     {
            color[node] = c;   
            for(int v: graph[node]){           
                if(color[v]==-1){
                    int tmp = c==0?1:0;                  
                    if(!dfs(graph,color,v,tmp))
                        return false;
                }
                 else if(color[v]==c){
                    return false;
                }
            }
        return true;
    }
    bool isBipartite(vector<vector<int>>& graph) {
        vector<int> color(graph.size(),-1); //unvisited
        for(int i=0;i<graph.size();i++){    // for loop since some nodes in graph can be disconnected
            if(color[i]==-1){      
                color[i]=0;
                if(!dfs(graph,color,i,0)){
                    return false;
                }
            }
        }
        return true;
    }
   */

// Method 3: Union Find 
   /*
       vector<int> parent;
public:
    bool isBipartite(vector<vector<int>>& graph) {
        for (int i = 0; i < graph.size(); i++) parent.push_back(i);
        for (int i = 0; i < graph.size(); i++) {
            for (int j = 0; j < graph[i].size(); j++) {
            // If the node is in the same component as its neighbors, then they are in the same
            //   set and the graph is not bipartite.
            // Bipartite means each node can only be connected to a node in another set/component
                if (find(i) == find(graph[i][j])) {
                return false;
                }
            // We union all the neighbors together
                uni(graph[i][0], graph[i][j]);
            }
        }
        return true;
    }
    
    int find(int a) {
        if (parent[a] != a) parent[a] = find(parent[a]);
        return parent[a];
    }
    void uni(int a, int b) {
        int pa = find(a), pb = find(b);
        if (pa != pb) parent[pa] = pb;
    }
   */


