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


// //////////////////
// Using BFS
/*
Intuition

Great question.

To solve **Leetcode 133. Clone Graph**, here's the **intuition** and **mental model** you should build, without jumping to code:

---

###  **What Are You Being Asked?**

You are given a **reference node** of an **undirected graph**, and you're asked to create a **deep copy** (clone) of the entire graph.

This means:

* Every node must be **copied** (not referenced).
* All edges between nodes must be **reconstructed** exactly as they appear in the original graph.
* The cloned graph must be **structurally identical** but made of **new node instances**.

---

### **Key Challenge: Cycles and Repeated Nodes**

Graphs can contain:

* **Cycles** (e.g. A → B → A)
* **Multiple neighbors**
* **Shared neighbors** (two nodes pointing to the same third)

So the major challenge is:

> How do you avoid **cloning the same node multiple times**, or going into **infinite recursion** due to cycles?

---

###  **Core Idea (Mental Model)**

You want to **visit each node once** and **remember** its cloned version so you can:

* Reuse the cloned node if it's already been created.
* Link it properly in the neighbor list of the current node.

This leads to the intuition of using a **visited map** (or `HashMap<Node, Node>`) where:

* Key = original node
* Value = its clone

---

### **How to Traverse?**

Two approaches work equally well:

* **DFS** (depth-first search): Recurse into each neighbor.
* **BFS** (breadth-first search): Traverse layer by layer using a queue.

Both rely on the same invariant:

> “If I've seen a node before, I shouldn't recreate its clone—I'll just reuse the one in my map.”

---

###  **What You’re Building**

* A new **Node** for each original node.
* A new list of **neighbors**, where each neighbor is also a **cloned node**, not a reference.

---

###  Recap of the Thought Process:

1. **Clone the first node**.
2. **Use a map** to track already cloned nodes.
3. **Traverse the graph** using DFS or BFS.
4. **When visiting neighbors**, clone them if not already cloned, and add them to the neighbor list of the current node.

---
*/
class Solution{
    public Node cloneGraph(Node node) {
        if (node == null) return null;

        // Map from original node to its clone
        Map<Node, Node> cloneMap = new HashMap<>();
        
        // Clone the starting node
        cloneMap.put(node, new Node(node.val));
        
        // BFS queue
        Queue<Node> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Go through each neighbor
            for (Node neighbor : current.neighbors) {
                if (!cloneMap.containsKey(neighbor)) {
                    // Clone it if not already cloned
                    cloneMap.put(neighbor, new Node(neighbor.val));
                    queue.add(neighbor); // Visit this neighbor later
                }
                // Link the clone of the current node to the clone of the neighbor
                cloneMap.get(current).neighbors.add(cloneMap.get(neighbor));
            }
        }

        // Return the clone of the input node
        return cloneMap.get(node);
    }
}


// DFS code
/*
### \U0001f9e0 Thought Process

1. **Base Case**: If `node == null`, return `null`.
2. **Visited Map**: Use `cloneMap` to remember already cloned nodes and avoid cycles.
3. **Recursive Step**: For each neighbor, recursively call `dfs` and attach the clone of that neighbor to the current clone's `neighbors`.

---

### \U0001f4a1 How DFS Works Here

If the graph looks like:

```
1 -- 2
|    |
4 -- 3
```

* Call `dfs(1)`:

  * Clone 1
  * Visit neighbor 2 → `dfs(2)`

    * Clone 2
    * Visit neighbor 3 → `dfs(3)`

      * Clone 3
      * Visit 4 → `dfs(4)`

        * Clone 4
        * Visit 1 → Already cloned → return cloned 1
      * Attach cloned 1 to cloned 4, and so on...

Each node gets cloned **once**, and recursion naturally traverses the full graph.

---

*/

// class Solution{
//     public Node cloneGraph(Node node) {
//         if(node == null){
//             return null;
//         }
        
//         HashMap<Node, Node> cloneMap = new HashMap<>();    
//         return dfs(node, cloneMap);
//     }

//     public Node dfs(Node node, HashMap<Node, Node> cloneMap){
//         if(cloneMap.containsKey(node)){
//             return cloneMap.get(node);
//         }
        
//         Node clone = new Node(node.val);
//         cloneMap.put(node, clone);

//         for(Node neighbor: node.neighbors){
//             clone.neighbors.add(dfs(neighbor));
//         }

//         return clone;
//     }
// }