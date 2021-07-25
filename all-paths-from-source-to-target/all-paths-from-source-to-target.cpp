class Solution {
public:
//     Method 1: DFS + Backtracking
    vector<vector<int>> ans;
    vector<int> temp;
    
    vector<vector<int>> allPathsSourceTarget(vector<vector<int>>& graph) {
       int dest = graph.size() - 1;
        temp.push_back(0);
        dfs(graph,0,dest);
        return ans;
    }
    
    void dfs(vector<vector<int>>& graph, int start, int dest){
       if(start==dest){   //if start = dest means we have successfully generated a new path from vertex 0
           ans.push_back(temp);   //store the path generated
           return;
       }
        
        for(auto u:graph[start]){   //Now we explore the path from zero using recursion and store the 
                                       // vertices in that process
            temp.push_back(u);      //Push the vertex in the path 
            dfs(graph, u, dest);    //Then start dfs from recently pushed vertex
            temp.pop_back();        //Backtrack to explore if new path is possible
        }
    }
};


// Method 2: BFS
   /*
     vector<vector<int>> allPathsSourceTarget(vector<vector<int>>& graph) {
        //source 
        int s=0;
        //size of the graph
        int n=graph.size()-1;
        //to store path
        vector<int>path;
        path.push_back(s);
        //ans vector
        vector<vector<int>>ans;
        
        //creating queue for bfs traversal
        queue<vector<int>>q;
        q.push(path);
        while(!q.empty()){
            path=q.front();
            q.pop();
            
            int val=path.back();
            //checking if we have reached to our destination or not
            if(val==n){
                //if we are at our destination means 
                //this is a valid path between source and destination.
                ans.push_back(path);
            }
            for(int &i:graph[val]){
                vector<int>v(path);
                v.push_back(i);
                q.push(v);
            }
        }
        return ans;
    }
   */