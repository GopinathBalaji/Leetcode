class Solution {
public:
//     Method 1: Recursive DFS (Similar to #463 Island Perimeter question)
    vector<vector<int>> floodFill(vector<vector<int>>& image, int sr, int sc, int newColor) {
        int colour = image[sr][sc];
        if(colour != newColor) dfs(image,sr,sc,colour,newColor);
        return image;
    }
    
    void dfs(vector<vector<int>>& image,int r,int c,int color,int newColor){
        if(image[r][c]==color){
            image[r][c] = newColor;
            if(r>=1) dfs(image,r-1,c,color,newColor);
            if(c>=1) dfs(image,r,c-1,color,newColor);
            if(r+1<image.size()) dfs(image,r+1,c,color,newColor);
            if(c+1<image[0].size()) dfs(image,r,c+1,color,newColor);
        }
    }
};


// Method 2: Iterative BFS
   /*
     vector<vector<int>> floodFill(vector<vector<int>>& image, int sr, int sc, int newColor) {
        if (image[sr][sc] == newColor) return image;
        
        int row = image.size(), col = image[0].size();
        queue<pair<int, int>> q;
        q.push(make_pair(sr, sc));
        
        int orginalColor = image[sr][sc];
        
        vector<int> offsets = {-1, 0, 1, 0, -1};
        
        while(q.size()) {
            pair<int, int> p = q.front();
            q.pop();
            
            image[p.first][p.second] = newColor;
            
            for (int i=0; i < offsets.size()-1; i++) {
                if(
                    p.first+offsets[i] >= 0          // within upper bound
                    && p.first+offsets[i] < row      // within lower bound
                    && p.second+offsets[i+1] >= 0    // within left bound
                    && p.second+offsets[i+1] < col   // within right bound
                    && image[p.first+offsets[i]][p.second+offsets[i+1]] == orginalColor
                ) 
                    q.push(make_pair(p.first+offsets[i], p.second+offsets[i+1]));
            }
        }
        
        return image;
    }
   */

// Method 3: Iterative DFS
   /*
     vector<vector<int>> floodFill(vector<vector<int>>& image, int sr, int sc, int newColor) {
        if(image.size()==0 || image[0].size()==0)
            return image;
        
        int m=image.size(),n=image[0].size(),r=0,c=0;
        int color=image[sr][sc];
 
        stack<pair<int,int>> process;
        process.push({sr,sc});
        while(!process.empty())
        {
            auto& p=process.top();
            r=p.first;
            c=p.second;
            image[r][c]=newColor;
            process.pop();
            
            if(r-1>=0 && image[r-1][c]==color && image[r-1][c]!=newColor)
                process.push({r-1,c});
            if(r+1<m && image[r+1][c]==color && image[r+1][c]!=newColor)
                process.push({r+1,c});
            if(c-1>=0 && image[r][c-1]==color && image[r][c-1]!=newColor)
                process.push({r,c-1});
            if(c+1<n && image[r][c+1]==color && image[r][c+1]!=newColor)
                process.push({r,c+1});
        }
        
        return image;
    }
   */