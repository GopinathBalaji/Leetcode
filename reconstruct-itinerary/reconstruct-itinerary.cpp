class Solution {
public:
unordered_map<string,multiset<string>> adjlist;
//     Method 1: DFS variety (EULERIAN PATH)
    /*
                                                EXPLANATION
                                                -----------
     Our general intuition is to jump to algorithms like DFS/BFS. The DFS/BFS algorithms help us traverse all nodes in the graph, but fail to cover all edges in the graph, so we need to look for something more special.

This is where Eulerian Path comes into play. An Eulerian Path is a list of all edges in a graph in a sequence such that you can go from point A to all other nodes in the graph multiple times, as long as an edge is not visited. This should solve our problem.

Eulerian Path can be implemented using a slightly modified version of DFS. A normal DFS algorithm would look like this:

// Normal DFS Pseudo Code

DFS(node)
{
	if (visited[node])
		return;
	visited[node]=true;
	for (neighbour : graph[node]) {
		DFS(neighbour);
	}
}
Note that here, we only check if a node is visited, which does not guarantee if all paths going to the node have been visited or not. One way to track if all edges have been visited to a node, is to track the outdegree of the node and continuously decrementing the outdegree until it becomes 0. So our modified DFS becomes:

ModifiedDFS(node)
{
	if (outdegree[node] == 0) // no more outgoing edges to visit
		return;
		--outdegree[node]       // next neighbour will be visited, reduce outdegree
	for (next_unvisited_neighbour : graph[node])
	{
		ModifiedDFS(next_unvisited_neighbour);
	}
}
In addition to this, we would also like to make sure that while returning, we capture the node that we have completed visiting while returing back. We could either reverse the order at the end of capturing all nodes we visited to get our path, or we could just use list and record our nodes from back to front.
    */
    /*
     For input like :
[[JFK, A], [JFK, D] , [A,C], [B,C] , [C, JFK], [C , D], [D,A] , [D, B] ]

It will build graph like this 
{'JFK': ['A', 'D'], 'A': ['C'], 'B': ['C'], 'C': ['D', 'JFK'], 'D': ['A', 'B']}

     targets = {'JFK': ['A', 'D'], 'A': ['C'], 'B': ['C'], 'C': ['D', 'JFK'], 'D': ['A', 'B']}
    route = []
    stack = ['JFK']
    First point at which we get stuck:

   
    targets = {'JFK': ['D'], 'A': [], 'B': ['C'], 'C': ['D' , 'JFK',], 'D': ['B']}
    route = []
    stack = ['JFK', 'A', 'C', 'D', 'A']
    Update route:

   
    targets = {'JFK': ['D'], 'A': [], 'B': ['C'], 'C': ['JFK'], 'D': ['B']}
    route = ['A']
    stack = ['JFK', 'A', 'C', 'D']
    Search forward again until stuck:

   
    targets = {'JFK': [], 'A': [], 'B': [], 'C': [], 'D': []}
    route = ['A']
    stack = ['JFK', 'A', 'C', 'D', 'B', 'C', 'JFK', 'D']
    Update route:

   
    targets = {'JFK': ['D'], 'A': [], 'B': [], 'C': ['JFK'], 'D': []}
    route = ['A', 'D', 'JFK', 'C', 'B', 'D', 'C', 'A', 'JFK']
    stack = []
    Return route in reverse:

   
    route = ['JFK', 'A', 'C', 'D', 'B', 'C', 'JFK', 'D', 'A']
    
    */
    vector<string> findItinerary(vector<vector<string>>& tickets) {
        for(vector<string> ticket: tickets){
            adjlist[ticket[0]].insert(ticket[1]);
        }
        
        vector<string> res;
       dfs(res,"JFK");
        reverse(res.begin(),res.end());
        
        return res;
    }
    
    void dfs(vector<string>& res,string source){
       
        while(!adjlist[source].empty()){
            string destination = *adjlist[source].begin();
            adjlist[source].erase(adjlist[source].begin());
            dfs(res,destination);
        }
        res.push_back(source);
    }
    
    // ------------------------OR-----------------------------
    /*
      void findPath(string currNode, list <string> &path, map <string, 
                    vector <string>>& graph, map <string, int> &outDegree)
    {
	    while (outDegree[currNode]) {
            findPath(graph[currNode][--outDegree[currNode]],path, graph, outDegree);
	    }
	    path.push_front(currNode);
    }

    vector<string> findItinerary(vector<vector<string>>& tickets) {
        list <string> path;
	    string startNode = "JFK";
	    map <string, vector <string>> graph;
        map <string, int> outDegree;

	    for (auto e : tickets) {
		    graph[e[0]].push_back(e[1]);
		    outDegree[e[0]]++;
	    }
       
        for (auto &g: graph) {
            sort(g.second.begin(), g.second.end(), greater<string>());
        }

	    findPath(startNode, path, graph, outDegree);
	    return vector <string> (path.begin(), path.end());
    }
    */
    
};


// Method 2: Iterative DFS variety
   /*
    vector<string> findItinerary(vector<pair<string, string>> tickets) {
		// Each node (airport) contains a set of outgoing edges (destination).
		unordered_map<string, multiset<string>> graph;
		// We are always appending the deepest node to the itinerary, 
		// so will need to reverse the itinerary in the end.
		vector<string> itinerary;
		if (tickets.size() == 0){
			return itinerary;
		}
		// Construct the node and assign outgoing edges
		for (pair<string, string> eachTicket : tickets){
			graph[eachTicket.first].insert(eachTicket.second);
		}
		stack<string> dfs;
		dfs.push("JFK");
		while (!dfs.empty()){
			string topAirport = dfs.top();
			if (graph[topAirport].empty()){
				// If there is no more outgoing edges, append to itinerary
				// Two cases: 
				// 1. If it searchs the terminal end first, it will simply get
				//    added to the itinerary first as it should, and the proper route
				//    will still be traversed since its entry is still on the stack.
				// 2. If it search the proper route first, the dead end route will also
				//    get added to the itinerary first.
				itinerary.push_back(topAirport);
				dfs.pop();
			}
			else {
				// Otherwise push the outgoing edge to the dfs stack and 
				// remove it from the node.
				dfs.push(*(graph[topAirport].begin()));
				graph[topAirport].erase(graph[topAirport].begin());
			}
		}
		// Reverse the itinerary.
		reverse(itinerary.begin(), itinerary.end());
		return itinerary;
	}
   */

