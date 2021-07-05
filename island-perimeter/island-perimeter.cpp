class Solution {
//     Method 1: DFS 
    void dfs(vector<vector<int>>& grid,int i,int j,int& perimeter){
        grid[i][j] = 2;
        if(i==0 || grid[i-1][j]==0) perimeter++; //check top
        if(i==grid.size()-1 || grid[i+1][j]==0) perimeter++;  // check bottom
        if(j==0 || grid[i][j-1]==0) perimeter++; // check left
        if(j==grid[0].size()-1 || grid[i][j+1]==0) perimeter++; // check right
        
        if(i>0 && grid[i-1][j]==1) dfs(grid,i-1,j,perimeter); // dfs of top
        if(i<grid.size()-1 && grid[i+1][j]==1) dfs(grid,i+1,j,perimeter); // dfs of bottom
        if(j>0 && grid[i][j-1]==1) dfs(grid,i,j-1,perimeter); // dfs of left
        if(j<grid[0].size()-1 && grid[i][j+1]==1) dfs(grid,i,j+1,perimeter); // dfs of right
    }
    
public:
    int islandPerimeter(vector<vector<int>>& grid) {
        int perimeter = 0;
        int r = grid.size();
        int c = grid[0].size();
        
        for(int i=0;i<r;i++){
            for(int j=0;j<c;j++){
                if(grid[i][j]==1){
                    dfs(grid,i,j,perimeter);
                }
            }
        }
        return perimeter;
    }
};

// Method 2: BFS 
   
   /*
     int dir[4][2] = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
    
    int islandPerimeter(vector<vector<int>>& grid) {
        int i, j, ans = 0, l, n = grid.size(), m = grid[0].size(), pos_i, pos_j, temp;
        vector<vector<bool>> vis(n, vector<bool>(m, false));
        queue<pair<int, int>> q;
        
        for(i = 0; i < n; i++){
            for(j = 0; j < m; j++){
                if(grid[i][j] && !vis[i][j]){
                    q.push({i, j});
                    vis[i][j] = true;
                    while(!q.empty()){
                        auto t = q.front();
                        q.pop();
                        temp = 4;
                        for(l = 0; l < 4; l++){
                            pos_i = t.first + dir[l][0];
                            pos_j = t.second + dir[l][1];
                            if(pos_i >= 0 && pos_i < n && pos_j >= 0 && pos_j < m && grid[pos_i][pos_j]){
                                temp--;
                                if(!vis[pos_i][pos_j]){
                                    vis[pos_i][pos_j] = true;
                                    q.push({pos_i, pos_j});
                                }
                            }
                            
                        }
                        ans += temp;
                    }
                    break;
                }
            }
        }
        return ans;
        
    } int dir[4][2] = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
    
    int islandPerimeter(vector<vector<int>>& grid) {
        int i, j, ans = 0, l, n = grid.size(), m = grid[0].size(), pos_i, pos_j, temp;
        vector<vector<bool>> vis(n, vector<bool>(m, false));
        queue<pair<int, int>> q;
        
        for(i = 0; i < n; i++){
            for(j = 0; j < m; j++){
                if(grid[i][j] && !vis[i][j]){
                    q.push({i, j});
                    vis[i][j] = true;
                    while(!q.empty()){
                        auto t = q.front();
                        q.pop();
                        temp = 4;
                        for(l = 0; l < 4; l++){
                            pos_i = t.first + dir[l][0];
                            pos_j = t.second + dir[l][1];
                            if(pos_i >= 0 && pos_i < n && pos_j >= 0 && pos_j < m && grid[pos_i][pos_j]){
                                temp--;
                                if(!vis[pos_i][pos_j]){
                                    vis[pos_i][pos_j] = true;
                                    q.push({pos_i, pos_j});
                                }
                            }
                            
                        }
                        ans += temp;
                    }
                    break;
                }
            }
        }
        return ans;
        
    }
   */


// Method 3: Iterative DFS
   /*
    
    int ans=0,dir[5]={1,0,-1,0,1};
    void dfs(vector<vector<int>>& grid, vector<vector<bool>>& vis, int i, int j){
        int cnt = 0;
        vis[i][j] = true;
        for(int a=0;a<4;++a){
            int curi = i+dir[a], curj = j+dir[a+1];
            if(curi < grid.size() && curj < grid[0].size() && curi >= 0 && curj >= 0){
                if(vis[curi][curj]) continue;
                if(grid[curi][curj])
                    dfs(grid,vis,curi,curj);
                else ++cnt;
            } else ++cnt;
        }
        ans += cnt;
    }
    int islandPerimeter(vector<vector<int>>& grid) {
        vector<vector<bool>> vis(grid.size(),vector<bool>(grid[0].size(),false));
        for(int i=0;i<grid.size();++i)
            for(int j=0;j<grid[0].size();++j)
                if(grid[i][j] == 1) {
                    dfs(grid,vis,i,j);
                    return ans;
                } 
        return ans;
    }
   */

// Method 4: Brute Force
   /*
    
     int islandPerimeter(vector<vector<int>>& grid) {
        int ans = 0 , row = grid.size(), col = grid[0].size();    
        for(int i = 0 ; i < row ; i++)
        {
           for(int j = 0 ; j < col ; j++)
           {
               if( grid[i][j])
               {
                  ans+=4;
                  if(i < row - 1 && grid[i+1][j])ans--; 
                  if(j < col -1 && grid[i][j+1])ans--; 
                  if(j  && grid[i][j-1])ans--; 
                  if(i  && grid[i-1][j])ans--; 
               }
           } 
        }
        return ans;
    }
   */