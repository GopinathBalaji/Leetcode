class Solution {
public:
//     Method 1: Union Find Method
    
    unordered_map<string,string> root;
    unordered_map<string,double> rate;
    
    vector<double> calcEquation(vector<vector<string>>& equations, vector<double>& values, vector<vector<string>>& queries) {
        int n = equations.size();
        for(int i=0;i<n;i++){
            string x = equations[i][0];
            string y = equations[i][1];
            if(root.find(x)==root.end()){
            root.insert({x,x});
            rate.insert({x,1.0});
            }
            if(root.find(y)==root.end()){
            root.insert({y,y});
            rate.insert({y,1.0});
            }
        }
   
        for(int i=0;i<n;i++){
            string x = equations[i][0];
            string y = equations[i][1];
            Union(x,y,values[i]);
        }
        
        vector<double> result;
        for(int i=0;i<queries.size();i++){
            string x = queries[i][0];
            string y = queries[i][1];
            if(root.find(x)==root.end() || root.find(y)==root.end()){
                result.push_back(-1);
                continue;
            }
            
            string rootx = findroot(x,x,1.0);
            string rooty = findroot(y,y,1.0);
            double temp = rootx==rooty ? rate[x] / rate[y] : -1.0;
            result.push_back(temp);            
        }
        
        return result;
    }
    
    void Union(string x,string y,double v){
        string rootx = findroot(x,x,1.0);
        string rooty = findroot(y,y,1.0);
        root[rootx] = rooty;
        double r1 = rate[x];
        double r2 = rate[y];
        rate[rootx] =  v * r2/r1;
    }
    
    string findroot(string originalx, string x,double r){
        if(root[x]==x){
           root[originalx] = x;
            rate[originalx] = r * rate[x];
            return x;
        }
        
        return findroot(originalx, root[x], r * rate[x]);
    }
};


// Method 2: DFS
   /*
      double dfs(unordered_map<string, unordered_map<string, double>> &graph, 
    string from, string to, unordered_set<string> &visited) {
    // Source not found
    if (graph.find(from) == graph.end()) {
        return -1.0;
    }
    
    // Direct edge
    if (graph[from].find(to) != graph[from].end()) {
        return graph[from][to];
    }
    
    // Mark source as visited
    visited.insert(from);
    
    for (auto i : graph[from]) {
        if (visited.find(i.first) == visited.end()) {
            // For all unvisited neighbors of source do dfs
            double ans = dfs(graph, i.first, to, visited);
            if (ans != -1.0) {
                // If ans of any neighbor is not -1, return (edge weight * ans of neighbor)
                return (ans * i.second);
            }
        }
    }
    
    // All neighbors returned -1, return -1
    return -1.0;
}
vector<double> calcEquation(vector<vector<string>>& equations, 
                vector<double>& values, 
                vector<vector<string>>& queries) {
  // Build the graph
  unordered_map<string, unordered_map<string, double>> graph;
  for (int i = 0; i < equations.size(); i++) {
      string from = equations[i][0];
      string to = equations[i][1];
      
      // From source to destination the weight of edge is values[i]
      if (graph.find(from) == graph.end()) {
          unordered_map<std::string, double> m;
          m.insert(make_pair(to, values[i]));
          graph.insert(make_pair(from, m));
      } else {
          graph[from].insert(make_pair(to, values[i]));
      }
      
      // From destination to source the weight of edge is (1 / values[i])
      if (graph.find(to) == graph.end()) {
          unordered_map<std::string, double> m;
          m.insert(make_pair(from, 1 / values[i]));
          graph.insert(make_pair(to, m));
      } else {
          graph[to].insert(make_pair(from, 1 / values[i]));
      }
  }
  
  // Solve queries
  vector<double> ans;
  for (int i = 0; i < queries.size(); i++) {
      unordered_set<string> visited;
      ans.push_back(dfs(graph, queries[i][0], queries[i][1], visited));
  }
  return ans;
}
   */



















