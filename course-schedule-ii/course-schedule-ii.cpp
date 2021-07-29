class Solution {
public:
//     Method 1: Topological Sorting using DFS
//               Here we check if there is a cycle using DFS
    
    vector<int> toposort;
    vector<int> mark;
        
    vector<int> findOrder(int numCourses, vector<vector<int>>& prerequisites) {
        mark = vector<int>(numCourses,0);
        vector<vector<int>> adj(numCourses,vector<int>());
        vector<int> indegree(numCourses,0);
        
        for(auto& edges: prerequisites){
            int a = edges[0];
            int b = edges[1];
            adj[b].push_back(a);
            indegree[a]++;
        }
        for(int i=0;i<numCourses;i++){
            if(dfs(i,adj)){
                return {};
            }
        }
        reverse(toposort.begin(), toposort.end());
        return toposort;
    }
    
    bool dfs(int i, vector<vector<int>>& adj){
        if(mark[i]==1)  return true;
        if(mark[i]==2)  return false;
        mark[i] = 1;
        for(auto v: adj[i]){
            if(dfs(v, adj)){
                return true;
            }
        }
        mark[i] = 2;
        toposort.push_back(i);
        return false;
    }
};


// Method 2: Topological Sort using Khan's Algorithm
   /*
    vector<int> findOrder(int numCourses, vector<pair<int, int>>& prerequisites) {
        vector<vector<int>> graph(numCourses, vector<int>());
        queue<int> nodes;
        vector<int> indegrees(numCourses, 0);
        int visit_node_size = 0;
        vector<int> result;
        
        for (auto item : prerequisites) {
            graph[item.second].push_back(item.first);
            ++ indegrees[item.first];
        }
        for (int node_id = 0; node_id < indegrees.size(); ++ node_id) {
            if (indegrees[node_id] == 0) {
                nodes.push(node_id);
            }
        }
        while (!nodes.empty()) {
            ++ visit_node_size;
            int node_id = nodes.front();
            nodes.pop();
            result.push_back(node_id);
            for (auto neighber_id : graph[node_id]) {
                -- indegrees[neighber_id];
                if (indegrees[neighber_id] == 0) {
                    nodes.push(neighber_id);
                }
            }
        }
        
        return visit_node_size == numCourses ? result : vector<int>();
    }
   */