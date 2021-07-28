class Solution {
public:
//     Method 1: Recursive DFS
    bool canVisitAllRooms(vector<vector<int>>& rooms) {
        map<int,bool> visited;
        int src = 0;
        dfs(rooms,visited,src);
        
        for(int i=0;i<rooms.size();i++){
            if(visited.find(i) == visited.end()){
                return false;
            }
        }
        return true;
    }
    
    void dfs(vector<vector<int>>& rooms, map<int,bool>& visited, int src){
        
         visited[src] = true;
        vector<int>::iterator it;
        for(it=rooms[src].begin();it!=rooms[src].end();it++){
            if(!visited[*it]){
                dfs(rooms,visited,*it);
            }
        }
    }
};

// Method 2: Iterative DFS
   /*
     public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        boolean[] seen = new boolean[rooms.size()];
        seen[0] = true;
        Stack<Integer> stack = new Stack();
        stack.push(0);

        //At the beginning, we have a todo list "stack" of keys to use.
        //'seen' represents at some point we have entered this room.
        while (!stack.isEmpty()) { // While we have keys...
            int node = stack.pop(); // Get the next key 'node'
            for (int nei: rooms.get(node)) // For every key in room # 'node'...
                if (!seen[nei]) { // ...that hasn't been used yet
                    seen[nei] = true; // mark that we've entered the room
                    stack.push(nei); // add the key to the todo list
                }
        }

        for (boolean v: seen)  // if any room hasn't been visited, return false
            if (!v) return false;
        return true;
    }
   */