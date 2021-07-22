class Solution {
public:
//     Method 1: DIJKSTRA'S ALGORITHM (without Priority Queue)
// We visit each node at some time, and if that time is better than
// the fastest time we've reached this node, we travel along that edge. 
// Therefore, we could use Dijkstra's algorithm.
    
    
    int networkDelayTime(vector<vector<int>>& times, int n, int k) {
       vector<vector<pair<int,int>>> adj(n+1);
       for(auto t: times){
           adj[t[0]].push_back({t[1],t[2]});
       } 
        vector<bool> visited(n+1, false);
        vector<int> dist(n+1, INT_MAX);
        dist[k] = 0;
        
        for(int i=0;i<n-1;i++){
            int k = findSmallestV(dist,visited);
            visited[k] = true;
            for(auto v: adj[k]){
                if(!visited[v.first] && dist[k]!=INT_MAX && dist[k]+v.second < dist[v.first]){
                    dist[v.first] = dist[k] + v.second;
                }
            }
        }
        int res = 0;
        for(int i=1;i<=n;i++){
            res = max(res,dist[i]);
        }
        
        return res == INT_MAX ? -1 : res;
    }
    
     int findSmallestV(vector<int>& dist, vector<bool> &visited) {
	    int v, minVal = INT_MAX;
	    for(int i = 1; i < dist.size(); i++){
		if (!visited[i] && dist[i]<minVal) v = i, minVal = dist[i];
        }
	    return v;
    }
};


// Method 2: Dijkstra's Algorithm using Priority Queue (min heap)
/* 
  class Solution {
    typedef pair<int,int> ipair;
public:
    int networkDelayTime(vector<vector<int>>& times, int n, int k) {
        list<pair<int,int>> *adj = new list<pair<int,int>>[n+1];
        for(auto e: times)
            adj[e[0]].push_back({e[1],e[2]});
        vector<int> dist(n+1,INT_MAX);
        priority_queue<pair<int,int>,vector<pair<int,int>>,greater<pair<int,int>>> pq;
        pq.push({0,k});
        dist[k] = 0;
        vector<bool> visit(n+1,false);
        
        while(!pq.empty()) {
            int u = pq.top().second;
            pq.pop();
            visit[u] = true;
            
            for(auto i = adj[u].begin();i!=adj[u].end();i++) {
                int v = (*i).first;
                int wt = (*i).second;
                if(!visit[v] && dist[v] > dist[u]+wt) {
                    dist[v] = dist[u] + wt;
                    pq.push({dist[v],v});
                }
            }
        }
        int max = INT_MIN;
        for(int i=1;i<=n;i++) {
            if(dist[i] == INT_MAX)
                return -1;
            if(dist[i] >max)
                max = dist[i];
        }
        return max;
    }
};
*/

// Method 3: Bellman-Ford Algorithm
   /*
    int networkDelayTime(vector<vector<int>>& times, int n, int k) {
        vector<int> dist(n+1,INT_MAX);
        int e = times.size();
        dist[k] = 0;
        for(int i=0;i<n-1;i++) {
            for(auto e: times) {
                int s = e[0];
                int d = e[1];
                int w = e[2];
                if(dist[s] != INT_MAX && dist[d] > dist[s] + w) {
                    dist[d] = dist[s] +w;
                }
            }
        }
         // for(auto e: times) {
         //        int s = e[0];
         //        int d = e[1];
         //        int w = e[2];
         //        if(dist[s] != INT_MAX && dist[d] > dist[s] + w){
         //            return -1;
         //        }
         //    }
        int max = INT_MIN;
        for(int i=1;i<=n;i++) {
            if(dist[i] == INT_MAX)
                return -1;
            if(max < dist[i])
                max = dist[i];
        }
        return max;
    }
    
    
    
    
    ---------------------OR------------------
    
    
    
    int graphcost[102][102];
    int cost[102][102];
    vector<vector<int>>adjecentlist;
    int networkDelayTime(vector<vector<int>>& times, int N, int K) {
        //Bellman-Ford
        int t=N-1;int res=-1;
        for(int i=0;i<=N;i++){
            vector<int>v;adjecentlist.push_back(v);
        }
        for(int i=0;i<102;i++){
            for(int j=0;j<102;j++){
                graphcost[i][j]=INT_MAX;
                cost[i][j]=INT_MAX;
            }
        }
        for(vector<int>&v:times){
            int v1=v[0];int v2=v[1];int cost=v[2];
            graphcost[v1][v2]=cost;
            adjecentlist[v1].push_back(v2);
        }
        BellmanFord(K,t,times);
        for(int i=1;i<=N;i++){
            if((K==i)){
                continue;
            }
            res=max(res,cost[K][i]);//reacheable
        }
        if(res==INT_MAX)return -1;
        return res;
    }
    
    void BellmanFord(int source,int times,vector<vector<int>>& edges){
        cost[source][source]=0;
        for(int i=0;i<times;i++){
            queue<int>q;
            q.push(source);
            unordered_set<int>hash;
            hash.insert(source);
            while(q.size()!=0){//bfs
                int parent=q.front();
                q.pop();
                vector<int>&childs=adjecentlist[parent];
                for(int &child:childs){
                    int c=cost[source][parent]+graphcost[parent][child];
                    cost[source][child]=min(cost[source][child],c);
                    if(hash.find(child)==hash.end()){
                        hash.insert(child);q.push(child);
                    }
                }
            }
        }
    }
   */


// Method 4: Shortest Path Faster Algorithm (SPFA is a improvement on Bellman Ford algo)
   /*
     int spfa(const vector<vector<int>>& times, const int n, const int k)
    {
        vector alist(n, vector<pair<int, int>>{});
        for (auto& time : times)
            alist[time[0] - 1].emplace_back(time[1] - 1, time[2]);        

        vector arrivals(n, INT_MAX);
        arrivals[k - 1] = 0;
        std::deque q = {k - 1};
        while (!q.empty())
        {
            const auto source = q.front();
            for (const auto [dest, t] : alist[source])
            {
                if (arrivals[dest] > arrivals[source] + t)
                {
                    arrivals[dest] = arrivals[source] + t;
                    q.push_back(dest);
                }
            }
            
            q.pop_front();
        }
        
        const auto res = *std::max_element(arrivals.begin(), arrivals.end());
        return res == INT_MAX ? -1 : res;        
    }
   */

