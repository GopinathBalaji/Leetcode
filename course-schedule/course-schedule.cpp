class Solution {
public:
//     Method 1: Topological Sort using Khan's Algorithm
    
    bool canFinish(int numCourses, vector<vector<int>>& prerequisites) {
        vector<vector<int>> adj(numCourses,vector<int> ());
        vector<int> indegree(numCourses,0);
        
        for(auto& edges: prerequisites){
            int a = edges[0];
            int b = edges[1];
            adj[b].push_back(a);
            indegree[a]++;
        }
        
       queue<int> q;
        for(int i=0;i<numCourses;i++){
            if(indegree[i]==0){
                q.push(i);
            }
        }
        if(q.empty()){
            return false;
        }
        
        int remaining = numCourses;
        while(!q.empty()){
            int curr = q.front();
            q.pop();
            remaining--;
            for(auto next: adj[curr]){
                if(--indegree[next] == 0){
                    q.push(next);
                }
            }
        }
        if(remaining==0){
            return true;
        }
        return false;
    }
};

// Method 2: Topological Sort using DFS
   /*
    class Solution {
	private:
	vector<vector<int>> graph;
	vector<int> dp;
public:
	bool dfs(int a){
		if(dp[a]==1) return true;
		if(dp[a]==2) return false;
		dp[a]=1;
		for(auto v:graph[a]){
			if(dfs(v)){
				return true;
			}
		}
		dp[a]=2;
		return false;
	}
	bool canFinish(int numCourses, vector<vector<int>>& prerequisites) {
		if(prerequisites.size()==0) return true;
		graph=vector<vector<int>>(numCourses);
		for(auto x:prerequisites){
			graph[x[1]].push_back(x[0]);

		}
		dp=vector<int>(numCourses,0);
		for(int i=0;i<numCourses;i++){
			if(dfs(i)){
				return false;
			}
		}
		return true;

	}
};
   */