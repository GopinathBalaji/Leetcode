class Solution {
public:
//     Method 1: DFS
    // Check for 3 things:
// One root (using indegree)
// No bidirectional edge or cycle (using dfs)
// One connected components (using visited array)

//     Observation: The index represent the node and the value at that index represent the node value
//              Eg) At index 2 of leftChild, the value is 3 in Example 1. Hence it can be seen that the 
//                   the left value of 2 is 3 as shown in the graph
    
      int flag = 0;
    bool validateBinaryTreeNodes(int n, vector<int>& leftChild, vector<int>& rightChild) {
          vector<int> vis(n,0);
        vector<int> indeg(n,0);
        int root = 0;
        
        for(int i=0;i<n;i++){
            if(leftChild[i] != -1)  indeg[leftChild[i]]++;
            if(rightChild[i] != -1) indeg[rightChild[i]]++;
        }
        
        int count = 0;
        for(int i=0;i<n;i++){
            if(indeg[i] == 0){
                count++;
                root = i;
            }
        }
        if(count != 1) return false;
        
        dfs(root,leftChild, rightChild, vis);
        
        for(int i=0;i<n;i++){
            if(vis[i]==0) return false;
        }
        
        if(flag == 1) return false;
        
        return true;
    }
    
    void dfs(int v, vector<int>& leftchild, vector<int>& rightchild, vector<int>& vis){
        
        vis[v] = 1;
        
        if(leftchild[v] != -1){
            if(vis[leftchild[v]]==1){
                flag = 1; // shows that there is bidirectional edge or cycle.
                return;
            }
            else{
                dfs(leftchild[v],leftchild, rightchild, vis);
            }
        }
        
        if(rightchild[v] != -1){
            if(vis[rightchild[v]] == 1){
                flag = 1;
                return;
            }
            else{
                dfs(rightchild[v],leftchild,rightchild,vis);
            }
        }
    }
};


// Method 2: Union Find
   /*
    class Solution {
public:
    vector<int> parent,size;

    int find(int i){
        while(parent[i]!=i){
            parent[i]=parent[parent[i]];
            i=parent[i];
        }
        return i;
    }
    void union_(int i,int j){
            parent[j]=i;
    }
    bool validateBinaryTreeNodes(int n, vector<int>& leftchild, vector<int>& rightchild) {
        vector<int> node_parent(n),vis(n,0);   //keeps count of parent
		//if any node has more than 1 parent return false
        for(int i=0;i<n;i++){
            if(leftchild[i]!=-1 ){
                node_parent[leftchild[i]]++;
                if(node_parent[leftchild[i]]>1)return false;
            }
            if(rightchild[i]!=-1){
                node_parent[rightchild[i]]++;
                if(node_parent[rightchild[i]]>1) return false;
            }
        }
        //checking for possible root
        int possibleroot=-1,count=0;
        for(int i=0;i<n;i++){
            if(node_parent[i]==0){
                possibleroot=i;
                count++;
                if(count>1)return false;  //if more than 2 root -> false
            }
        }
        if(possibleroot==-1)return false;   //no possible root
        //find connected component  dsu and check if only component is there so all nodes are connected
        parent.resize(n);size.resize(n,1);
        for(int i=0;i<n;i++)parent[i]=i;
        for(int i=0;i<n;i++){
            int u=i,v=leftchild[i],w=rightchild[i];
            int x=find(u);
            if(v!=-1){
                int y=find(v);
                if(x!=y)union_(x,y);
            }
            if(w!=-1){
                int z=find(w);
                if(x!=w)union_(x,w);
            }
            
        }
        int connectedcomponent=0;
        count=0;
        for(int i=0;i<n;i++){
            if (parent[i]==i){cout<<parent[i]<<" ";count++;}
        }
        if (count>1)return false;
        return true;
        
    }
};
   */





