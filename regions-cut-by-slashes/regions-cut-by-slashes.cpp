class Solution {
public:
//     Method 1: Assume Graph and then apply Union Find
//              Graph is created by dividing the 1x1 tile into 4 parts and each part has is made up of
//              4 triangles, numbered 0,1,2,3 in that order
         // Split a cell in to 4 parts like this.
        // We give it a number top is 0, right is 1, bottom is 2 left is 3.
    // Two adjacent parts in different cells are contiguous regions.
// In case '/', top and left are contiguous, botton and right are contiguous.
// In case '\\', top and right are contiguous, bottom and left are contiguous.
// In case ' ', all 4 parts are contiguous.
        
    int count,n;
    vector<int> parent;
    int regionsBySlashes(vector<string>& grid) {
        n = grid.size();
        count = n*n*4;             //total number of polygons
        
        for(int i=0;i<n*n*4;i++){      //initialize the parent array
            parent.push_back(i);
        }
        
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(i>0){              // merge triangles 2 and 0 from two squares vertically               
                    Union(g(i-1,j,2), g(i,j,0)); 
                }
                if(j>0){              // merge trianges 1 and 3 from two squares horizontally.
                     Union(g(i,j-1,1), g(i,j,3));
                }
                if(grid[i][j] != '/'){        // if it is "\\" or empty, apply common operations
                    Union(g(i,j,0), g(i,j,1));
                    Union(g(i,j,2), g(i,j,3));
                }
                
                if(grid[i][j] != '\\'){   // if it is "/" or empty, apply common operations.
                    Union(g(i,j,0), g(i,j,3));  // These two if statements solve the repetition issue 
                                                // caused by traditional three if statements
                    Union(g(i,j,2), g(i,j,1));
                }
            }
        }
        return count;                        //return number of polygons in the graph
    }
    
    int find(int x){
        if(x != parent[x]){
            parent[x] = find(parent[x]);      //path compression
        }        
        return parent[x];
    }
    
    void Union(int x,int y){
        x = find(x);
        y = find(y);
        if(x != y){
            parent[x] = y;
            count--;            //merge two polygons to a bigger polygon will decrease number of 
                                // polygons by 1
        }
    }
    
    int g(int i, int j, int k){
        return (i * n + j) * 4 + k;  //find the corresponding position for current triangle. Thinks this 
                                     // 1D array as a 2D array and each element is an array of size 4.
    }
};












