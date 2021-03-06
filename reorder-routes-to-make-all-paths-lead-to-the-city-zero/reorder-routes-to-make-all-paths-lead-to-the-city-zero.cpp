class Solution {
public:
//     Method 1: BFS by counting the revese edges
//         Simple DFS from Source to Destination won't work because the question asks for MINIMUM number 
//           of edges changed. 
// We put outward facing edges in one vector, and keep the reverse in another.
// Starting from the city, we count edges that are facing away from us.

// If there is a node that faces inward to us that we haven't visited yet, it would be in our back vector.
// We need to add inward facing nodes to the queue as well, since they might have neighbors that need 
// to be flipped.
    
    int minReorder(int n, vector<vector<int>>& connections) {
        vector<vector<int>> adj(n),back(n);
        vector<int> visited(n,0);
        
        for(auto& v: connections){
            adj[v[0]].push_back(v[1]);
            back[v[1]].push_back(v[0]);
        }
        int ans = 0;
        queue<int> q;
        q.push(0);
        
        while(!q.empty()){
            int curr = q.front();
            q.pop();
            visited[curr] = 1;
            
        // count direction for all arrows facing out
            for(auto a: adj[curr]){
                if(!visited[a]){
                    ans++;
                    q.push(a);
                }
            }
            
        // push other nodes so we visit everything
            for(auto a: back[curr]){
                if(!visited[a]){
                    q.push(a);
                }
            }
        }
        return ans;
    }
};     

// Method 2: DFS
//       Procedure is similar to the previous approach
   /*
    int dfs(int n,int num,vector<int>out[],vector<int>in[],vector<bool>& vis){
        int count=0;
        vis[num]=true;
        for(int i=0;i<out[num].size();i++){
            if(!vis[out[num][i]])
                count+=1+dfs(n,out[num][i],out,in,vis);
        }
        for(int i=0;i<in[num].size();i++){
            if(!vis[in[num][i]])
                count+=dfs(n,in[num][i],out,in,vis);
        }
        return count;
    }
    int minReorder(int n, vector<vector<int>>& connections) {
        vector<int>out[n];
        vector<int>in[n];
        vector<bool>vis(n,false);
        for(int i=0;i<n-1;i++){
            out[connections[i][0]].push_back(connections[i][1]);
            in[connections[i][1]].push_back(connections[i][0]);
        }
        return dfs(n,0,out,in,vis);
    }
   */