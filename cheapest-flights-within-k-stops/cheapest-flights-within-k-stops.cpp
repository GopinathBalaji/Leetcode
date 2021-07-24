class Solution {
public:
//     Method 1: Modified Dijkstra Algorithm using min Heap (Priority Queue)
//            Here we maintain the distance of each node from source node in terms of number of steps
//            and not add those node that exceed the number of steps
    int findCheapestPrice(int n, vector<vector<int>>& flights, int src, int dst, int k) {  
        unordered_map<int,vector<pair<int, int>>> adj;
        for( auto f : flights )
            adj[f[0]].push_back( { f[1], f[2] } );
        
         // minHeap based on cost of distance from source
        priority_queue< vector<int>, vector<vector<int>>, greater<vector<int>> > minheap;
        minheap.push({0, src, 0});   // cost, vertex, hops
        
        vector<int> dist(n+1, INT_MAX);
        
        while(!minheap.empty()){
           auto t = minheap.top();
            int cost = t[0];
            int curr = t[1];
            int stops = t[2];
            
            minheap.pop();
            
            if(curr == dst){
                return cost;
            }
            
            if(stops > k){
                continue;
            }
            if(dist[curr]<stops){
                continue;
            }
            dist[curr] = stops;
            for(auto next: adj[curr]){
                minheap.push({cost+next.second, next.first, stops+1});
            }
        }
        return -1;
    }
};


// Method 2: BFS
   /*
    int findCheapestPrice( int n, vector<vector<int>>& flights, int src, int dst, int K ) { 
	// Create adjList
	unordered_map<int, vector< pair<int,int> > > adjList;
	for( auto flight : flights )
		adjList[flight[0]].push_back( { flight[1], flight[2] } );

	 // BFS starting from src 
	queue< pair<int,int> > q;         // < node, dist_from_src > pair
	q.push( { src, 0 } );
	int srcToTgtDist = INT_MAX;        // result

	while( !q.empty() && K-- >= 0 ) {
		int size = q.size();
		for( int i = 0; i < size; i++ ) {
			auto curr = q.front(); q.pop();         
			for( auto nbr : adjList[curr.first] ) {
				if( srcToTgtDist < curr.second + nbr.second ) continue;

				q.push( { nbr.first, curr.second + nbr.second } );

				// update distance from src to dst
				if( dst == nbr.first ) 
					srcToTgtDist = min( srcToTgtDist, curr.second + nbr.second );
				}
		}
	}
	return srcToTgtDist == INT_MAX ? -1 : srcToTgtDist;
}
   */

// Method 3: Bellman Ford
//          Use two arrays actual and temporary distance, update only temporay distance continuously
   /*
        // In bellman-ford algo calculates the shortest distance from the source
        // point to all of the vertices.
        // Time complexity of Bellman-Ford is O(VE),
    
    int findCheapestPrice(int n, vector<vector<int>>& flights, int src, int dst, int K) {
         // distance from source to all other nodes 
        vector<int> dist( n, INT_MAX );
        dist[src] = 0;
        
        // Run only K+1 times since we want shortest distance in K hops
        for( int i=0; i <= K; i++ ) {
            vector<int> tmp( dist );
            for( auto flight : flights ) {
                if( dist[ flight[0] ] != INT_MAX ) {
                    tmp[ flight[1] ] = min( tmp[flight[1]], dist[ flight[0] ] + flight[2] );
                }
            }
            dist = tmp;
        }
        return dist[dst] == INT_MAX ? -1 : dist[dst];
    }
   */

// Method 3: Dynamic Programming
   /*
     int findCheapestPrice(int n, vector<vector<int>>& flights, int src, int dst, int K) {
    vector<vector<int>> dp(K + 2, vector<int> (n, INT_MAX));
    for(int i = 0; i <= K + 1; i++) dp[i][src] = 0;
    for(int i = 1; i <= K + 1; i++){
      for(auto& f:flights){
        if(dp[i-1][f[0]] != INT_MAX)
          dp[i][f[1]] = min(dp[i][f[1]], dp[i-1][f[0]] + f[2]);
      }
    }
    return dp[K+1][dst] == INT_MAX? -1: dp[K+1][dst];
  }
   */