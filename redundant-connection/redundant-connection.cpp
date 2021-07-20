class Solution {
public:
//     Method 1: Union Find
    vector<int> parent;
    vector<int> findRedundantConnection(vector<vector<int>>& edges) {
        
        parent.resize(edges.size(),-1);
        for(int i=0;i<edges.size();i++){
            int a = findroot(edges[i][0]-1);
            int b = findroot(edges[i][1]-1);
            
            if(a == b){
                vector<int> res;
                res.push_back(edges[i][0]);
                res.push_back(edges[i][1]);
                return res;
            }
            Union(a,b);
        }
        return {};
    }
    
    void Union(int a, int b){
        parent[a] = b;
    }
    
    int findroot(int v){
        if(parent[v]==-1) return v;
        
        return findroot(parent[v]);
    }
};


// Method 2: DFS
// Add an edge each time, and then judge whether it will form a ring after adding this edge;
// Determine if a map has a ringdfs, here needs to maintain onepreVariable indicating the last visited 
// node and then usingvisThe array tag and the accessed node, if accessed again, indicate that there
// is a ring;
   /*
     public int[] findRedundantConnection(int[][] edges) {
        if (edges == null || edges.length == 0)
            return new int[2];
        int n = edges.length;
        ArrayList<Integer> G[] = new ArrayList[n+1]; // The integer in the two-dimensional array is between 1 and N, where N is the size of the input array.
        for(int i = 1; i <= n; i++)
            G[i] = new ArrayList<>();
        for (int i = 0; i < edges.length; i++) {
            int from = edges[i][0];
            int to = edges[i][1];
            G[from].add(to);
            G[to].add(from);
            boolean[] vis = new boolean[n+1];
            if(!dfs(from, -1, vis, G))// Find from the current node
                return edges[i];
        }
        return new int[2];
    }
    
    / / Determine whether a graph has a ring (maintain a pre variable)
    private boolean dfs(int v, int pre, boolean[] vis, ArrayList<Integer>G[]){
        if(vis[v])
            return false;
        vis[v] = true;
        for(int next : G[v]){
            if(next != pre)
                if(!dfs(next, v, vis, G))
                    return false;
        }
        return true;
    }
   */




