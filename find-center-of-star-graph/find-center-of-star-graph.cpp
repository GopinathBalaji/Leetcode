class Solution {
public:
    int findCenter(vector<vector<int>>& edges) {
        vector<int> v1 = edges[0];
        vector<int> v2 = edges[1];
        
        vector<int>::iterator it;
        it = find(v2.begin(),v2.end(),v1[0]);
        if(it != v2.end()){
            return v1[0];
        }
        return v1[1];
    }
};

// Method 2: Simpler Version
   /*
     int findCenter(vector<vector<int>>& edges) {
        ios_base::sync_with_stdio(false);
        cin.tie(nullptr);
        
        return (edges[0][0] == edges[1][0] || edges[0][0] == edges[1][1]) ? edges[0][0] : edges[0][1];
    }
   */

// 