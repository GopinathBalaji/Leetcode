class Solution {
public:
//     Method 1: Find number of incoming edges.
//        If a node has an incoming edge then it can be reacheable, but if it doesn't then the node is not
//         reacheable. Therefore find the vertices with no incoming edges.
    vector<int> findSmallestSetOfVertices(int n, vector<vector<int>>& edges) {
        vector<vector<int>> adj(n);
        vector<int> indegree(n,0);
        for(auto& v: edges){
            adj[v[0]].push_back(v[1]);
            indegree[v[1]]++;
        }
        
        vector<int> ans;
        for(int i=0;i<indegree.size();i++){
            if(indegree[i]==0){
                ans.push_back(i);
            }
        }
        
        return ans;
    }
};

// Method 2: Union Find
   /*
    class Solution {
public:
    
    vector<int > parent;
    
    int find(int x)
    {
        if(parent[x]==-1) return x;
        return find(parent[x]);
    }
    
    void union1(int x, int y)
    {
        int px=find(x);
        int py=find(y);
        if(px!=py)
            parent[y]=x;
    }
    
    vector<int> findSmallestSetOfVertices(int n, vector<vector<int>>& ed) {
        parent=vector<int> (n,-1);
        for(int i=0;i<ed.size();i++)
        {
            int u=ed[i][0], v=ed[i][1];
            union1(u,v);
        }
        
        vector<int> ret;
        for(int i=0;i<n;i++)
        {
            if(parent[i]==-1) ret.push_back(i);
        }
        return ret;
    }
};
   */