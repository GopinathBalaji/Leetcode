class Solution {
public:
//     Method 1: Topological Sorting using DFS (White Grey Black method)
//         This question indirectly asks us to find those nodes that do not from a loop.
//          But Topological sorting only works on Directed Acyclic Graphs (DAG). Therefor modify
//         the usual Topological sorting to keep track of Terminating or not and Visited or not, instead
//         of just Visited or not. This can be done by taking 3 number to track: 0 for unvisited nodes, 
//        1 nodes that form a cycle and 2 for nodes that lead to termination. Initailly nodes are assumed
//     to form a cycle hence assigned a value 1
        
    vector<int> eventualSafeNodes(vector<vector<int>>& graph) {
         int n = graph.size();
            vector<int> ans;
         vector<int> track(n,0);
        for(int i=0;i<n;i++){
            if(dfs(i,graph,track)){
                ans.push_back(i);
            }
        }
        return ans;
    }
    
    bool dfs(int node,vector<vector<int>>& graph, vector<int>& track){
        if(track[node] > 0){
            return track[node]==2;
        }
                
        track[node] = 1;
        for(int i:graph[node]){
            if(track[i]==2){
                continue;
            }
            if(track[i]==1 || !dfs(i,graph,track)){
                return false;
            }
        }
        
        track[node] = 2;
        return true;
    }
};


// Method 2: Reverse Edges and Apply Topological sort
// Our goal is only to remove those nodes who are contained in a cyclic path. This could be done by 
// topological sort.
// In original topological sort, we start from those with 0 in-degree.
// This time, we start from those with 0 out-degree.
// So, the map is reversed.
   /*
     vector<int> eventualSafeNodes(vector<vector<int>>& graph){
        int n=graph.size();
        unordered_map<int,vector<int>> next;
        unordered_map<int,int> odegree;
        unordered_map<int,bool> visited;
        for (int i=0;i<n;++i){
            odegree[i]=graph[i].size();
            visited[i]=false;
            for (auto j:graph[i]) next[j].push_back(i);
        }
        vector<int> ans;
        while (true){
            bool flag=false;
            for (int i=0;i<n;++i)
                if (odegree[i]==0&&!visited[i]){
                    flag=true;
                    visited[i]=true;
                    ans.push_back(i);
                    for (auto j:next[i]) --odegree[j];
                }
            if (!flag) break;
        }
        sort(ans.begin(),ans.end());
        return ans;
    }
   */



