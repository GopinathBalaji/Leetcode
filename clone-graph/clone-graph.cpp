/*
// Definition for a Node.
class Node {
public:
    int val;
    vector<Node*> neighbors;
    Node() {
        val = 0;
        neighbors = vector<Node*>();
    }g
    Node(int _val) {
        val = _val;
        neighbors = vector<Node*>();
    }
    Node(int _val, vector<Node*> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
};
*/

class Solution {
public:
//     Method 1: BFS
    Node* cloneGraph(Node* node) {
        if(!node) return NULL;
        
        // maintain an adjaceny list
       map<int,set<int>> adjlist;
       queue<Node*> q1; 
       q1.push(node);
        while(!q1.empty()){
          Node* w = q1.front();
            q1.pop();
            if(adjlist.count(w->val)==0){
                adjlist[w->val] = {};
            }
            for(Node* v: w->neighbors){
                if(adjlist[w->val].count(v->val)==0){
                    adjlist[w->val].insert(v->val);
                    q1.push(v);
                }
            }
        }
        
        // first create all the nodes, then add their edges in the second pass
        map<int,Node*> m2;
        for(auto& [w,_]: adjlist){
            m2[w] = new Node(w);
        }
        for(auto& [w,ns]: adjlist){
            for(auto v: ns){
                m2[w]->neighbors.push_back(m2[v]);
            }
        }
        return m2[1];
    }
};

// Method 2: Reursive DFS
   /*
    class Solution {
public:
    Node* cloneGraph(Node* node) {
        
        //stores the address of the nodes created
        if(node==NULL)
            return NULL;
        vector<Node*> created(101,NULL);
        buildGraph(node,created);
        return created[node->val];
    }
    void buildGraph(Node* node,vector<Node*>& created)
    {
        
        //create the node
        Node* x;
        if(created[node->val]==NULL){
             x=new Node(node->val);
            created[node->val]=x;
        }
        else
            x=created[node->val];
        
        for(auto adj:node->neighbors)
        {
            //if node is not created yet create it
            if(created[adj->val]==NULL)
            {
                Node* n = new Node(adj->val);
                
                created[adj->val]=n;
				//add the created node to the neighbors
                x->neighbors.push_back(n);
                buildGraph(adj,created);
            }
            else{
                x->neighbors.push_back(created[adj->val]);
            }
        }
    }
   */













