class Solution {
public:
//     Method 1: Dijkstra's Algorithm with modification
//           Store probabilities instead of distances. Use Max Priority Queue because we want max 
//            probability
             // Use max priority queue because we need to maximise probability.
            // Instead of addition use product operation
    
    double maxProbability(int n, vector<vector<int>>& edges, vector<double>& succProb, int start, int end)      {
        vector<vector<pair<int,double>>> adj(n);
        for(int i=0;i<edges.size();i++){
            adj[edges[i][0]].push_back({edges[i][1], succProb[i]});
            adj[edges[i][1]].push_back({edges[i][0], succProb[i]});
        }
        
        vector<double> dist(n,0);   // Initializing distance vector to store probability of each node 
        dist[start] = 1;
        
        priority_queue< pair<int,double>, vector<pair<int,double>> > pq; //Initializing max priority 
                                                                         // queue.
        pq.push({1,start}); 
        
        while(!pq.empty()){
            int u = pq.top().second;
            pq.pop();
            
            for(auto p: adj[u]){
                int v = p.first;
                double weight = p.second;
                if(dist[u] * weight > dist[v]){
                    dist[v] = dist[u] * weight;
                    pq.push({dist[v],v});
                }
            }
        }
        
        
        return dist[end];   //returning probability of destination node.
    }
};

// Method2: Dijkstra's Algorithm with modification. Negative Log
   // We need to use -log(p) since log(p) when p < 1 is always negative.
// We use log to avoid precision problems and finally inverse it using 1 / exp(cummulative_probablity).
   /*
    class Solution {
public:
    using Node = pair<int,double>;
    using Graph = vector<vector<Node>>;
    using NodeDistPair = pair<double,int>;

    double maxProbability(int n, vector<vector<int>>& edges, vector<double>& succProb, int start, int end) {
        Graph graph(n);
        for (int i = 0; i < edges.size(); i++) {
            auto edge = edges[i];
            double w = -log(succProb[i]);
            graph[edge[0]].push_back({ edge[1], w });
            graph[edge[1]].push_back({ edge[0], w });
        }

        vector<double> dist(n, DBL_MAX);
        dist[start] = 0;

        priority_queue<NodeDistPair, vector<NodeDistPair>, greater<NodeDistPair>> pq;
        pq.push({ 0, start });

        while (!pq.empty()) {
            int nodeId = pq.top().second;
            pq.pop();

            for (auto [adjNodeId, adjNodeProb]: graph[nodeId]) {
                if (dist[adjNodeId] > dist[nodeId] + adjNodeProb) {
                    dist[adjNodeId] = dist[nodeId] + adjNodeProb;
                    pq.push({ dist[adjNodeId], adjNodeId });
                }
            }
        }

        return dist[end] == DBL_MAX ? 0 : 1 / exp(dist[end]);
    }
};
   */

// Method 3: Modified BFS
   /*
    double maxProbability(int n, vector<vector<int>>& edges, vector<double>& succProb, int start, int end) {
    vector<vector<pair<int, double>>> to(n);
        for(int i=0;i<edges.size();i++){
            to[edges[i][0]].emplace_back(edges[i][1], succProb[i]);
            to[edges[i][1]].emplace_back(edges[i][0], succProb[i]);
        }
        vector<double> Prob(n, 0);
        queue<int> Q;
        Q.push(start);
        Prob[start] = 1;
        while (!Q.empty()){
            int node = Q.front();
            Q.pop();
            for (auto child : to[node]){
                int nxt = child.first;
                double p = child.second;
                if (Prob[nxt] < Prob[node] * p) Prob[nxt] = Prob[node] * p, Q.push(nxt);
            }
        }
        return Prob[end];
    }
   */