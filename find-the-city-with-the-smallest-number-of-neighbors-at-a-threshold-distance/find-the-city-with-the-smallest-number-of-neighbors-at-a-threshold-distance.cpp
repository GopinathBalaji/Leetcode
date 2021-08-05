class Solution {
public:
//     Method 1: Dijkstra's Algorithm
    
              unordered_map<int, vector<pair<int,int>>> adj;
              vector<pair<int,int>> ans;
              int no;
    int findTheCity(int n, vector<vector<int>>& edges, int distanceThreshold) {
        no = n;
        for(auto& v: edges){
            adj[v[0]].push_back({v[1],v[2]});
            adj[v[1]].push_back({v[0],v[2]});
        }
        
        for(int i=0;i<n;i++){
            dijk(i, distanceThreshold);
        }
        
        sort(ans.begin(),ans.end(),cmp);  //Sort the vector of pairs in a way that the 1st element
                                // of the vector will contain the desired output, and the second of the
                                // 1st element is the required city number.
        
        return ans[0].second;
    }
    
    void dijk(int source, int thresh){
        priority_queue< pair<int,int>, vector<pair<int,int>>, greater<pair<int,int>> > pq;
        vector<int> dist(no+1,INT_MAX);
        pq.push({0,source});
        dist[source] = 0;
        
        while(!pq.empty()){
            int u = pq.top().second;
            pq.pop();
            
            for(auto& p: adj[u]){
                int  v = p.first;
                int weight = p.second;
                
                if(dist[v] > dist[u]+weight){
                    dist[v] = dist[u] + weight;
                    pq.push({dist[v],v});
                }
            }
        }
        
        int count = 0;
        for(int i=0;i<no;i++){
            if(i != source && dist[i] <= thresh){
                count++;
            }
        }
         ans.push_back({count,source});  // 1st element denotes the number of reachable cities to a 
                                          // particular city and the 2nd element represents the 
                                          // number of that city (that is used to break the tie).
    }
    
    bool static cmp(const pair<int,int> p1, const pair<int,int> p2){
          if(p1.first != p2.first){
              return p1.first < p2.first;
          }      
        
        return p1.second > p2.second;
    }
};

// ------------------------OR-------------------
   /*
      
    // custom comparator for minimum priority queue <node,distance> based on distance 
    struct comparator { 
        bool operator()(pair<int,int> const& a, pair<int,int> const& b) 
        { 
            return a.second > b.second;
        } 
    }; 
  
    int findTheCity(int n, vector<vector<int>>& edges, int K) {
        
        // graph adjacency list of  nodes and weights
        vector<vector<pair<int,int>>>graph(n);
        
        // populate graph adjacency list <node, distance>
        for(int i=0;i<edges.size();i++)
        {
            graph[edges[i][0]].push_back(make_pair(edges[i][1],edges[i][2]));
            graph[edges[i][1]].push_back(make_pair(edges[i][0],edges[i][2]));
        }
        
        // distance vector of nodes
        vector<int>distance(n);
        
        // minimum priority queue <node,distance> based on distance
        priority_queue<pair<int,int>,vector<pair<int,int>>,comparator>Q;
        
        // minimum visited node count of nodes
        int minNodeCount = INT_MAX;

		// city with smallest number of neighbors at athreshold distance
        int expectedCity;
        
        // run BFS / Dijkstra from every node and count the number of visited nodes with in threshold distance
        for(int i=0;i<n;i++)
        {
            int source = i;
            
            // set distance of all nodes to threshold + 1
            fill(distance.begin(),distance.end(),K+1);
            
            // push the source node with distance 0 as it is the source node
            Q.push(make_pair(source,0));
            distance[source] = 0;
            
            while(!Q.empty())
            {                
                int node = Q.top().first;
                int node_dis = Q.top().second;
                Q.pop();
                
                // ignore nodes which has lowest distance than the node distance in queue means already visited
                if(distance[node]<node_dis) continue;
                
                // add neighbour nodes in to queue which has higher distance than current node distance + edge distance   
                for(int j=0;j<graph[node].size();j++)
                {
                    int neighbour = graph[node][j].first;
                    int edge_dis = graph[node][j].second;
                    
                    if(edge_dis+distance[node]<distance[neighbour])
                    {
                        distance[neighbour] = distance[node] + edge_dis;
                        Q.push(make_pair(neighbour,distance[neighbour]));
                    }
                }
            }
            
            // count the number of visited nodes in threashold distance
            int visitCount = 0;
            for(int j=0;j<n;j++)
                if(source!=j && distance[j]<=K)visitCount++;                

            // update minNodeCount if count is less or equal cause 
            // we need larger node/city number incase of equal count 
            if(minNodeCount>=visitCount)
            {
                expectedCity = source;
                minNodeCount = visitCount;
            }
        }
        
        return expectedCity;
    }
   */



// Method 2: Floyd Warshall 
// Floyd Warshall is an algorithm to compute the minimum distances between any of the two nodes in an 
// undirected and weighted graph. The idea is that for the min distance between node i and node j,
// there're two possibilities:
// (1) the distance is the weight of their direct edge (dp[i][j] = edge[i][j]);
// (2) there're other nodes involved in the shortest path (dp[i][j] = dp[i][k] + dp[k][j]),
    // where dp[i][k] and dp[k][j] are also likely to be either direct edges or indirect paths.

// The algorithm takes O(N^3) time to compute all distances dp[i][j], with just three for loops.
// The key point is that k must be in the first loop. The reason is that if we don't put k in the
// first loop. When we try to compute dp[i][k]+dp[k][j] the values of dp[i][k] and dp[k][j] are 
// not guaranteed to have been updated. It is possible that nothing is updated in a loop. However,
// if we put k in the first loop, it is guaranteed that if edge[i][k] and edge[k][j] are direct
// edges, dp[i][j] can be updated by dp[i][k]+dp[k][j]. And the whole matrix is updated in a 
// diffusive way.

// Suppose we have four nodes 0-1-2-3. When we have k=1 first, we can compute 
// dp[0][2] = min(dp[0][2], dp[0][1]+dp[1][2]), then when k=2, we can compute 
// dp[0][3] = min(dp[0][3], dp[0][2]+dp[2][3]). On the contrary, if we have k=2 first,
// we will get dp[1][3] first, and when k=1, we can use dp[1][3] to compute dp[0][3]. So the
// order of k actually doesn't matter. For each k we're increasing the information and 
// finally all distances can be computed.

/*
  int findTheCity(int n, vector<vector<int>>& edges, int distanceThreshold) {
        vector<vector<int>> dp(n, vector<int>(n, 20000));
        for (const auto& e : edges) {
            dp[e[0]][e[1]] = e[2];
            dp[e[1]][e[0]] = e[2];
        }
		// Floyd Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j) continue;
                    dp[i][j] = min(dp[i][j], dp[i][k]+dp[k][j]);
                }
            }
        }
        int ret = 0;
        int min_count = n;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (dp[i][j] <= distanceThreshold) count++;
            }
            if (count <= min_count) {
                min_count = count;
                ret = i;
            }
        }
        return ret;
    }
*/



