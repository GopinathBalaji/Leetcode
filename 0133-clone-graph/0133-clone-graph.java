/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> neighbors;
    public Node() {
        val = 0;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val) {
        val = _val;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val, ArrayList<Node> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
}
*/


// Method 1: My iterative DFS appraoch
class Solution {
    public Node cloneGraph(Node node) {
        if(node == null){
            return node;
        }

        HashMap<Node, Node> map = new HashMap<>();

        HashSet<Node> visited = new HashSet<>();
        Deque<Node> stack = new ArrayDeque<>();
        stack.addFirst(node);

        while(!stack.isEmpty()){
            Node oldNode = stack.pop();
            if(visited.contains(oldNode)){
                continue;
            }

            visited.add(oldNode);

            Node newNode;
            if(!map.containsKey(oldNode)){
                newNode = new Node(oldNode.val);
                map.put(oldNode, newNode);
            }else{
                newNode = map.get(oldNode);
            }

            for(Node n : oldNode.neighbors){
                stack.addFirst(n);

                Node newNeighbor;
                if(map.containsKey(n)){
                    newNeighbor = map.get(n);
                    newNode.neighbors.add(newNeighbor);
                }else{
                    newNeighbor = new Node(n.val);
                    map.put(n, newNeighbor);
                    newNode.neighbors.add(newNeighbor);
                }
            }
        }

        return map.get(node);
    }
}



// Method 1.5: Better Iterative DFS approach (O(V+E))
/*
Why this is robust:
No visited set needed: the map tells us if we’ve seen a node.
Push once per node: we only push(v) the very first time we discover v.
Edges added exactly once per original adjacency: For each (u,v) in original, we add u'->v' when processing u. In LC’s undirected graphs, v also contains u in its neighbor list, so the reverse edge will be added when v is processed.
*/

// class Solution {
//     public Node cloneGraph(Node node) {
//         if (node == null) return null;

//         Map<Node, Node> map = new HashMap<>();
//         Deque<Node> st = new ArrayDeque<>();

//         // create clone for the start node and seed the stack
//         map.put(node, new Node(node.val));
//         st.push(node);

//         while (!st.isEmpty()) {
//             Node u = st.pop();
//             Node uClone = map.get(u);

//             for (Node v : u.neighbors) {
//                 if (!map.containsKey(v)) {
//                     // first time we see v: create its clone and push original v
//                     map.put(v, new Node(v.val));
//                     st.push(v);
//                 }
//                 // add edge u' -> v'
//                 uClone.neighbors.add(map.get(v));
//             }
//         }
//         return map.get(node);
//     }
// }




// Method 2: BFS approach  (O(V+E))
// class Solution {
//     public Node cloneGraph(Node node) {
//         if (node == null) return null;

//         Map<Node, Node> map = new HashMap<>();
//         Queue<Node> q = new ArrayDeque<>();

//         map.put(node, new Node(node.val));
//         q.offer(node);

//         while (!q.isEmpty()) {
//             Node u = q.poll();
//             Node uClone = map.get(u);

//             for (Node v : u.neighbors) {
//                 if (!map.containsKey(v)) {
//                     map.put(v, new Node(v.val));
//                     q.offer(v);
//                 }
//                 uClone.neighbors.add(map.get(v));
//             }
//         }
//         return map.get(node);
//     }
// }




// Method 3: Recursive DFS approach
/*
The key is to use a HashMap<Node, Node> to memoize already-cloned nodes so you (1) avoid infinite recursion on cycles, and (2) reuse clones when neighbors point back.

Idea:
If node == null → return null.
If we’ve already cloned node, return the clone from the map.
 Otherwise:
Create a clone with the same value.
Put it in the map before cloning neighbors (prevents infinite loops).
Recursively clone each neighbor and append to the clone’s neighbor list.
*/

// class Solution {
//     private Map<Node, Node> map = new HashMap<>();

//     public Node cloneGraph(Node node) {
//         if (node == null) return null;
//         return dfs(node);
//     }

//     private Node dfs(Node u) {
//         // already cloned? return it
//         if (map.containsKey(u)) return map.get(u);

//         // create clone and memoize immediately
//         Node copy = new Node(u.val);
//         map.put(u, copy);

//         // clone all neighbors recursively
//         for (Node v : u.neighbors) {
//             copy.neighbors.add(dfs(v));
//         }
//         return copy;
//     }
// }