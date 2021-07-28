class Solution {
public:
//     Method 1: Check if graph is Bipartite
    
         bool possibleBipartition(int N, vector<vector<int>> &edges) 
    {
        vector<vector<int>> adj(N + 1); // adjacency list for undirected graph
        vector<int> color(N + 1, 0); // color of each vertex in graph, initially WHITE
        vector<bool> explored(N + 1, false); // to check if each vertex has been explored exactly once
        
        // create adjacency list from given edges
        for (auto &edge: edges)
        {
            int u = edge[0];
            int v = edge[1];
            adj[u].push_back(v);
            adj[v].push_back(u);
        }
        
        // queue to perform BFS over each connected component in the graph
        // while performing BFS, we check if we encounter any conflicts while
        // coloring the vertices of the graph
        // conflicts indicate that bi-partition is not possible
        queue<int> q;
        
        for (int i = 1; i <= N; ++i)
        {
            if (!explored[i])
            {
                // this component has not been colored yet
				// we color the first vertex RED and push it into the queue
                color[i] = 1;
                q.push(i);
                
                // perform BFS from vertex i
                while (!q.empty())
                {
                    int u = q.front();
                    q.pop();
                    
                    // check if u is already explored 
                    if (explored[u])
                    {
                        continue;
                    }
                    
                    explored[u] = true;
                    
                    // for each neighbor of u, execute this loop
                    for (auto v: adj[u])
                    {
                        // v is u's neighboring vertex
                        
                        // checking if there's any conflict in coloring
                        if (color[v] == color[u])
                        {
							// conflict edge found, so we return false 
							// as bi-partition will not be possible
                            return false;
                        }
                        
                        // we color v with the opposite color of u
                        if (color[u] == 1)
                        {
                            color[v] = 2;
                        }
                        else 
                        {
                            color[v] = 1;
                        }
                        
                        q.push(v);
                    }
                }
            }
        }
        
        // if no conflicts encountered then graph must be bipartite
        // so we return true
        
        return true;
    }
};

// Method 2: Union Find
   /*
    int find( vector<int> &parents, int i ) {
        if( i != parents[i] )
            i = parents[i];
        return i;
    }
public:
    bool possibleBipartition(int N, vector<vector<int>>& dislikes) {
        vector<int> parents(N);
        for( int i=0; i < N; i++ )
            parents[i] = i;
        
        unordered_map<int, vector<int>> adjList;
        for( auto dislike : dislikes ) {
            adjList[ dislike[0] - 1 ].push_back( dislike[1] - 1 );
            adjList[ dislike[1] - 1 ].push_back( dislike[0] - 1 );
        }
        
        for( auto adj : adjList ) {
            int root1 = find( parents, adj.first );
            for( auto nbr : adj.second ) {
                int root2 = find( parents, nbr );
                if( root1 == root2 )
                    return false;
                else
                    parents[root2] = adj.second[0];
            }
        }
        return true;
    }
   */





