class Solution {
public:
    int removeStones(vector<vector<int>>& stones) {
       bool visited[stones.size()];
        memset(visited,0,stones.size());
        int result = 0;
        for(int i=0;i<stones.size();i++){
            if(!visited[i]){
                result += dfs(i,visited,stones);
            }
        }
        return result;
    }
    
    int dfs(int index, bool* visited, vector<vector<int>>& stones){
        visited[index] = true;
        int result = 0;
        for(int i=0;i<stones.size();i++){
            if(!visited[i] && (stones[i][0]==stones[index][0] || stones[i][1]==stones[index][1])){
                result += dfs(i, visited, stones) + 1;
            }
        }
        return result;
    }
}; 

// Method 2: Union Find
// Imagine each stone has an ID corresponding to its index in the input array.
// Map each occupied row to a vector of all stone IDs in that row. Repeat for columns.
// Union each stone ID with all other stone IDs in the same row or in the same column.
// Each connected group can have all but one stone removed. Thus, we count the number of unique groups
// and subtract this from the total number of stones to get our answer.
   /*
            vector<int> parent;   
      int removeStones(vector<vector<int>>& stones) {
           unordered_map<int,int> rowmap,colmap;
          for(int i=0;i<stones.size();i++){
             rowmap[stones[i][0]].push_back(i);
             colmap[stones[i][1]].push_back(i);
          }             
          
          for(int i=0;i<stones.size();i++){
             parent.push_back(i);
          }
          for(int i=0;i<stones.size();i++){
             for(int j: rowmap[stones[i][0]]) union(i,j);
             for(int j: colmap[stones[i][1]]) union(i,j);
          }
          
          unordere_set<int> unique;
          for(int n: parent){
              int a = find(n);
              unique.insert(a);
          }
          
          return stones.size() - unique.size();
      }
      
      int find(int n){
          if(parent[n]==n){
             return n;
          }
          return parent[n] = find(parent[n]);
      }
      
      void union(int a, int b){
           int x = find(a);
           int y = find(y);
           parent[x] = y;
      }
   */













