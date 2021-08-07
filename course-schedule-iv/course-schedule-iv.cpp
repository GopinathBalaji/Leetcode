class Solution {
public:
//     Method 1: DFS
 vector<bool> checkIfPrerequisite(int n, vector<vector<int>>& prerequisites, vector<vector<int>>& queries) {
        
        if(queries.size() == 0)
            return {};
        
        vector<vector<int>> adj(n);
        for(auto it : prerequisites)
        {
            adj[it[0]].push_back(it[1]);
        }
        
        vector<bool> res;
        for(int i=0; i<queries.size(); i++)
        {
            vector<bool> visited(n, false);
            res.push_back(dfs(queries[i][0], queries[i][1], adj, visited));
        }
        
        return res;
    }
    
    bool dfs(int src, int dest, vector<vector<int>>& adj, vector<bool>& visited)
    {
        if(src == dest)
            return true;
        if(visited[src])
            return false;
        
        visited[src] = true;
        for(auto neigh : adj[src])
        {
            if(!visited[neigh])
            {
                if(dfs(neigh, dest, adj, visited))
                    return true;
            }
        }
        
        return false;
    }
};

// Method 2: Topological Sort using Khan's Algorithm
   /*
    class Solution {
    vector<vector<int>> g;
    vector<unordered_set<int>> req;
public:
    vector<bool> checkIfPrerequisite(int n, vector<vector<int>>& p, vector<vector<int>>& q) {
        req = vector<unordered_set<int>>(n);
        g = vector<vector<int>>(n);
        vector<int> indgree(n);
        for(auto& pre: p) {
            int u = pre[0], v = pre[1];
            g[v].push_back(u);
            indgree[u]++;
        }
        queue<int> que;
        for(int i=0; i<n; i++) {
            if(indgree[i] == 0)
                que.push(i);
        }
        while(not que.empty()) {
            auto cur = que.front(); que.pop();
            for(int nxt: g[cur]) {
                if(--indgree[nxt] == 0) 
                    que.push(nxt);
                req[nxt].insert(cur);
                for(int rc: req[cur]) {
                    req[nxt].insert(rc);
                }
            }
        }
        vector<bool> ret;
        for(auto& qry: q) {
            if(req[qry[0]].count(qry[1]))
                ret.push_back(true);
            else 
                ret.push_back(false);
        }
        return ret;
    }
};
   */