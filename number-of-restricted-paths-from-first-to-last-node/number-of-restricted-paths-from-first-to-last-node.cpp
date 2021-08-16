class Solution {
public:
//     Method 1: Shortest Path + DFS
//       We can use dijsktra Algorithm for finding the distance from src = lastNode to each node. 
//        Start a DFS traversal from the start node 1 and travel as many different paths that you can. 
       // HOWEVER the requirement is that you can only move from a node to its neighbor if 
       // distanceToEnd(node) > distanceToEnd(nei).
    
    const int mod = 1e9+7;
				int dp[20005];
				int dfs(int start,vector<pair<int,int>>adj[],int n,int dist[])
				{
					if(start==n)
					{
						return 1;
					}
					if(dp[start]!=-1)
					{
						return dp[start];
					}
					int ans = 0;
					for(auto x:adj[start])
					{

						if(dist[start]>dist[x.first])
						{
							ans = (ans + dfs(x.first,adj,n,dist))%mod;
						}
					}
					return dp[start] = ans%mod;
				}

				int countRestrictedPaths(int n, vector<vector<int>>& edges) {


					int dist[n+1];
					memset(dist,INT_MAX,sizeof(dist));

					memset(dp,-1,sizeof(dp));
					vector<pair<int,int>>adj[n+1];
					for(auto x:edges)
					{
						adj[x[0]].push_back({x[1],x[2]});   
						adj[x[1]].push_back({x[0],x[2]});
					}
					bool vis[n+1];
					memset(vis,false,sizeof(vis));
					priority_queue<pair<int,int>,vector<pair<int,int>>,greater<pair<int,int>>>pq;
					dist[n] = 0;
					pq.push({0,n});
					for(int i = 0;i<n;i++)
					{
						dist[i] = INT_MAX;
					}
					while(!pq.empty())
					{

						auto cur = pq.top();
						pq.pop();
						int u = cur.second;
						vis[u] = true;


						for(auto v:adj[u])
						{

							if(!vis[v.first]  and dist[u]!=INT_MAX and dist[u]+v.second < dist[v.first])
							{
							   dist[v.first]  = dist[u] + v.second;  
								pq.push({dist[v.first],v.first});
							}

						}
					}
					return dfs(1,adj,n,dist);

    }
};