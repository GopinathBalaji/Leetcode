class Solution {
public:
    // Method 1: Graph Indegree and Outdegree
    int findJudge(int n, vector<vector<int>>& trust) {
// Graph Solution- According to the two conditions for being a judge,we can relate it to such
// a node of a graph that has indegree=n-1(n-1 people trust him) and outdegree=0(He trusts
// nobody).So we create an adjacency list and count indegree and outdegree for each node(person).
        if(n==1 && trust.size()==0) return n;
        if(n>1 && trust.size()==0) return -1;
        
        vector<int> adj_list[n+1];
        vector<int> indegree(n+1,0);
        vector<int> outdegree(n+1,0);
        
        for(int i=0;i<trust.size();i++){
            adj_list[trust[i][1]].push_back(trust[i][0]);
        }
        for(int i=0;i<n+1;i++){
            for(int j=0;j<adj_list[i].size();j++){
                indegree[i]++;
                outdegree[adj_list[i][j]]++;
            }
        }
       for(int i=0;i<n+1;i++){
           if(indegree[i]==n-1 && outdegree[i]==0){
               return i;
           }
       }
        return -1;
    }
    
//  -----------------OR---------------
    /*
     int findJudge(int N, vector<vector<int>>& trust) {
    vector<int> inDegree(N + 1,0);
    vector<int> outDegree(N + 1,0);
    
    for(vector<int> &t : trust){
        int u = t[0];
        int v = t[1];
        
        inDegree[v]++;
        outDegree[u]++;
    }
    
    for(int i = 1; i <= N; i++){
        if(inDegree[i] == N - 1 && outDegree[i] == 0) return i;
    }
    
    return -1;
}
    */
};



// Method 2: Array Solutiom
// We take a vector arr and initialize all its elements to 0. Then we traverse trust and mark
// arr[trust[i][0]] as 1 bcoz trust[i][0] trusts trust[i][1] so it can never be judge(as judge
// trusts nobody). Now,we've marked all the people who trust someone as 1. If there is a 
// solution then there should be only one element(ans) in arr which is 0 ( apart from arr[0]).
// Another condition-Everybody trusts judge, so we keep a counter and increment it if trust[i]
// [1]==ans. If counter=n-1(i.e. n-1 people trust him) then ans is our answer otherwise return -1.
     int findJudge(int n, vector<vector<int>>& trust) {
        vector<int> v1(n+1,0);
        int ans,count=0;
        for(int i=0;i<trust.size();i++){
            v1[trust[i][0]] = 1;
        }
        for(int j=1;j<=n;j++){
            if(v1[j]==0){
                ans = j;
            }
        }
        for(int k=0;k<trust.size();k++){
            if(trust[k][1]==ans) count++;
        }
        if(count==n-1) return ans;
        
        return -1;
    }

// Method 3: Using Hash Map
   /*
    
     int findJudge(int n, vector<vector>& trust) {
if(trust.size()==1)
{
return trust[0][1];
}
if(n==1)
{
return 1;
}
map<int,int>m;map<int,int>m1;

    for(int i=0;i<trust.size();i++)
    {
        m[trust[i][1]]++;
        
    }
     
    for(int i=0;i<trust.size();i++)
    {
        m1[trust[i][0]]++;
        
    }
    int ans=-1;
    
    for(auto x:m)
    {
       if(x.second==n-1)
       {
          ans=x.first; 
       }
    }
    for(auto x:m1)
    {
       if(x.first==ans)
       {
           return -1;
       }
    }
   
    return ans;
}
   */