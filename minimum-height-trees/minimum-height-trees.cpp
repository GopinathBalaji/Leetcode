class Solution {
public:
//     Method 1: BFS
//     For the tree-alike graph, the number of centroids is no more than 2. 
//     Keep pruning the the leaves until the number of nodes will only remain 2.
    
    vector<int> findMinHeightTrees(int n, vector<vector<int>>& edges) {
        
        // base case
        if(n<2){
            vector<int> v1;
            for(int i=0;i<n;i++){
                v1.push_back(i);
            }
            return v1;
        }
        
        // Build graph with Adjacency list and keep track of indegree for each node
        vector<vector<int>> adjlist(n);
        vector<int> indegree(n,0);
        
        for(auto &edge: edges){
            adjlist[edge[0]].push_back(edge[1]);
            adjlist[edge[1]].push_back(edge[0]);
            ++indegree[edge[0]];
            ++indegree[edge[1]];
        }
        
        // add first layer of leaf nodes
        queue<int> leaves;
        for(int i=0;i<n;i++){
            if(indegree[i]==1){
                leaves.push(i);
            }            
        }
        
        while(n>2){
            int popele = leaves.size();
            n -= popele;
            
            for(int i=0;i<popele;i++){
                int t  = leaves.front();
                leaves.pop();
                
                for(auto j=adjlist[t].begin();j != adjlist[t].end();j++){
                    indegree[*j]--;
                    if(indegree[*j]==1){
                        leaves.push(*j);
                    }
                }
            }
        }
        
        vector<int> res;
        while(!leaves.empty()){
            res.push_back(leaves.front());
            leaves.pop();
        }
        
        return res;
    }
};





