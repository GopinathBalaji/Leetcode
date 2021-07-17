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

// Method 2: Finding Diameter method
   /* 
//     /* function that return nodes that form root with minimum height */
//     static ArrayList <Integer> rootMinHeight(ArrayList<ArrayList<Integer>> adj, int v)
//     {
//         /* vector that stores degree of each tree vertex */
//         ArrayList <Integer> degree = new ArrayList<>();
//         Queue <Integer> q = new LinkedList<>();
        
//         for(int i=0;i<v;i++)
//         {
//             degree.add(adj.get(i).size());
//             /* push leaf vertex (with degree 1) into the queue */
//             if(adj.get(i).size() == 1)
//             q.add(i);
//         }
        
//         /* begin BFS starting from leaf vertices (and deleting them) 
//         until only 2 or less vertices are left to 
//         be traversed. These vertices left unvisited
//         form roots with minimum height tree */
//       while (v > 2)
//       {
//         for (int i = 0; i < q.size(); i++)
//         {
//           int top = q.poll();
//           v--;
    
//           /* for neighbors of the leaf, decrease their degrees
//           and if those vertices turn out to be  leaf vertices
//           push them into the queue */
//           Iterator itr = adj.get(top).iterator();
          
//           while (itr.hasNext())
//           {
//               int j = (Integer)itr.next();
//             degree.set(j,degree.get(j)-1);
//             if (degree.get(j) == 1)
//               q.add(j);
//           }
//         }
//       }
    
//       /* the only vertices (or vertex) remaining in the queue
//       are the ones that form minimum height tree, store them
//       into a vector and return the vector */
//       ArrayList <Integer> result = new ArrayList<>();
//       while (!q.isEmpty())
//         result.add(q.poll());
      
//       return result;
//     }
 