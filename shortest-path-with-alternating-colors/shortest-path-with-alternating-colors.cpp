class Solution {
public:
    //         Method 1: BFS
//        For finding the shortest path in a unweighted graph always use BFS
// A key thing here is seeing if a node is visited(i.e. we can ignore it) or not is not trivial.
// Because it may have been visited with a red side, whereas we can get different paths if we visit it
// with blue side.
    
    vector<int> shortestAlternatingPaths(int n, vector<vector<int>>& red_edges, vector<vector<int>>& blue_edges) {
        vector<int> ans(n);
        vector<vector<pair<int,int>>> adj(n);    //index, {neighbor, color}
        for(auto& vec: red_edges){
            adj[vec[0]].push_back({vec[1],0});      //red edges are denoted by 0 
        }
        for(auto& vec: blue_edges){
            adj[vec[0]].push_back({vec[1],1});      //blue edges are denoted by 1
        }
       
        queue<pair<int,int>> q;
        vector<vector<int>> vcost(n, vector<int>(2,-1));
        vcost[0] = {0,0};      // cost at start is 0
        
    // try starting with either red or blue colors
        q.push({0,0});
        q.push({0,1});
        
        while(!q.empty()){
            auto [i,c1] = q.front();
            q.pop();
            
            for(const auto& [j,c2]: adj[i]){
               if(vcost[j][c2] != -1 || c1==c2){         // either visited or same color
                   continue;
               } 
                
                vcost[j][c2] = vcost[i][c1] + 1;
                q.push({j,c2});
            }
        }
        
        vector<int> res;
        for(const auto& v: vcost){
           int val = v[1];
            if(v[0]<val && v[0]!=-1 || val==-1){
                val = v[0];
            }
            res.push_back(val);
        }
        return res;
    }
};


// ------------------------OR---------------
    /*
      int d[2][102]; // d[0][i] stores minimum distance from node 0 to node i, where last edge is red
                  // d[1][i] stores minimum distance from node 0 to node i, where last edge is blue
    vector<int> edge[2][102]; // edge[0][i] stores the red edges starting from node i
                            // edge[1][i] stores the blue edges starting from node i
    vector<int> shortestAlternatingPaths(int n, vector<vector<int>>& red_edges, vector<vector<int>>& blue_edges) {
        for(auto e:red_edges){
            edge[0][e[0]].push_back(e[1]);
        }
        for(auto e:blue_edges){
            edge[1][e[0]].push_back(e[1]);
        }
        memset(d,-1, sizeof(d));
        d[1][0] = d[0][0] = 0;
        queue<pair<int,int> > q; // node, last edge type
        q.push({0,0}); // starting node, last edge is red
        q.push({0,1}); // starting node, last edge is blue
        while(!q.empty()){
            pair<int,int> cur = q.front(); q.pop();
            int u = cur.first;
            int type = cur.second; // 0 = red, 1 = blue
            for(auto v:edge[1-type][u]){ // 1-0 = 1, 1-1 = 0, so 1-type is opposite color
                if(d[1-type][v] == -1){ // unvisited with 1-type as last edge
                    d[1-type][v] = d[type][u] + 1;
                    q.push({v,1-type});
                }
            }
        }
        vector<int> ans;
        for(int i = 0;i<n;i++){ // find the minimum distance for all nodes
            if(d[1][i]==-1) 
                ans.push_back(d[0][i]);
            else if(d[0][i]==-1) 
                ans.push_back(d[1][i]);
            else
                ans.push_back(min(d[0][i],d[1][i]));
        }
        return ans;
    }
   */