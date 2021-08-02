class Solution {
public:
//     Method 1: Find the color of neighbour and mark it as not available, then use the available value 
     //   for the current garden
    vector<int> gardenNoAdj(int n, vector<vector<int>>& paths) {
        vector<int> gardens(n);
        vector<vector<int>> adj(n);
        
        for(vector<int>& vec: paths){
            adj[vec[0]-1].push_back(vec[1]-1);
            adj[vec[1]-1].push_back(vec[0]-1);
        }
        
        for(int i=0;i<n;i++){
            int colors[5] = {};
            for(int neighbors: adj[i]){
                colors[gardens[neighbors]] = 1;
            }
            for(int c=1;c<=4;c++){
                if(colors[c] != 1){
                    gardens[i] = c;
                    break;
                }
            }
        }
        return gardens;
    }
};