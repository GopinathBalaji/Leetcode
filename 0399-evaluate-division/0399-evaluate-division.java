// DFS Code
/*
Intuition behind why are we using birectional weighted graph for this question:

Great question!

---

### \U0001f9e0 **Intuition: Why Use a Graph in Leetcode 399 - Evaluate Division**

The problem is essentially asking you to evaluate **relationships between pairs of variables** based on given equations. For example:

* `a / b = 2.0`
* `b / c = 3.0`

From these, you should be able to deduce:

* `a / c = 6.0`
* `c / a = 1/6.0`

This is **exactly what graphs do well**: model relationships and allow traversal between nodes to discover indirect connections.

#### \U0001f517 Think of it as:

* Each **variable** is a **node**.
* Each **equation** is a **directed edge** between nodes, with a **weight** (the division value).
* To evaluate `x / y`, you are effectively asking: *is there a path from `x` to `y`? If yes, what's the product of weights along that path?*

That’s classic **graph traversal**, using DFS or BFS.

---

### \U0001f50d **How to Recognize When a Graph is Needed**

Here are cues that should trigger "graph" in your mind:

#### ✅ 1. **Pairs with Relations**

* You’re given a list of **entity pairs** with relationships between them.
* You're asked to **infer indirect relationships**.

#### ✅ 2. **Need to Traverse or Connect Entities**

* If the problem involves determining how something is connected to something else (directly or indirectly).

#### ✅ 3. **Weighted Relationships**

* If there's a cost, ratio, or multiplier between entities.
* Especially if combining multiple such relationships to get the result.

#### ✅ 4. **Querying Connections**

* You’re not just asked to process the data, but to **answer queries** based on whether two entities are connected or what the result of that connection is.

---

### \U0001f4cc Summary:

You know to use a **graph** when:

* The problem involves **connecting nodes (variables, objects, people, etc.)**.
* There are **indirect relationships** to explore.
* The solution requires **finding paths** and **aggregating weights or properties** along them.

In Leetcode 399, each query is like asking: *is there a path from A to B? If yes, what's the product of edge weights along that path?*
*/

class Solution {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        HashMap<String, Map<String, Double>> graph = new HashMap<>();

        for(int i=0;i<equations.size();i++){
            addEdge(graph, equations.get(i), values[i]);
        }

        double[] result = new double[queries.size()];
        for(int i=0; i<queries.size(); i++){
            String src = queries.get(i).get(0);
            String dst = queries.get(i).get(1);
            Set<String> visited = new HashSet<>();

            result[i] = dfs(graph, src, dst, 1.0, visited);
        }

        return result;
    }

    public double dfs(HashMap<String, Map<String, Double>> graph, String src, String dst, double product, Set<String> visited){
        if(!graph.containsKey(src) || !graph.containsKey(dst)){
            return -1.0;
        }

        if(src.equals(dst)){
            return product;
        }

        visited.add(src);

        for(Map.Entry<String, Double> neighbor: graph.get(src).entrySet()){
            String next = neighbor.getKey();
            double weight = neighbor.getValue();

            if(!visited.contains(next)){
                double result = dfs(graph, next, dst, product * weight, visited);
                if(result != -1.0){
                    return result;
                }
            }
        }

        return -1.0;
    }

    public void addEdge(HashMap<String, Map<String, Double>> graph, List<String> vertices, double weight){
        String from = vertices.get(0);
        String to = vertices.get(1);
        graph.putIfAbsent(from, new HashMap<>());
        graph.putIfAbsent(to, new HashMap<>());

        graph.get(from).put(to, weight);
        graph.get(to).put(from, 1.0 / weight);
    }
}

// BFS Code
/*
Here is the **BFS-based** solution for **Leetcode 399. Evaluate Division**, with a complete explanation:

---

### ✅ **Approach: Breadth-First Search (BFS)**

* Represent the equations as a **bidirectional weighted graph**.
* For each query, perform **BFS from `start` to `end`**, multiplying edge weights along the way.
* If no path exists → return `-1.0`.

---

### \U0001f9e0 Explanation

#### Graph Building

* `"a / b = 2.0"` ➝ `a → b (2.0)`, `b → a (0.5)`
* This ensures **bidirectional traversal**.

#### BFS Logic

* From `start`, try to reach `end` using BFS.
* Track:

  * `visited` set to avoid cycles.
  * `product` (cumulative value from start to current node).
* Multiply edge weights as you move along.
* If you reach `end`, return the accumulated product.
* If not reachable, return `-1.0`.

---

### \U0001f4e6 Time Complexity

* **Graph Build:** O(N), where N is the number of equations.
* **Each Query BFS:** O(V + E) in the worst case.
*/
// class Solution {
//     public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
//         // Build the graph
//         Map<String, Map<String, Double>> graph = new HashMap<>();

//         for (int i = 0; i < equations.size(); i++) {
//             String a = equations.get(i).get(0);
//             String b = equations.get(i).get(1);
//             double value = values[i];

//             graph.putIfAbsent(a, new HashMap<>());
//             graph.putIfAbsent(b, new HashMap<>());
//             graph.get(a).put(b, value);
//             graph.get(b).put(a, 1.0 / value);
//         }

//         // Process queries using BFS
//         double[] result = new double[queries.size()];
//         for (int i = 0; i < queries.size(); i++) {
//             String start = queries.get(i).get(0);
//             String end = queries.get(i).get(1);
//             result[i] = bfs(graph, start, end);
//         }

//         return result;
//     }

//     private double bfs(Map<String, Map<String, Double>> graph, String start, String end) {
//         if (!graph.containsKey(start) || !graph.containsKey(end)) return -1.0;
//         if (start.equals(end)) return 1.0;

//         Set<String> visited = new HashSet<>();
//         Queue<Pair<String, Double>> queue = new LinkedList<>();
//         queue.offer(new Pair<>(start, 1.0));
//         visited.add(start);

//         while (!queue.isEmpty()) {
//             Pair<String, Double> current = queue.poll();
//             String node = current.getKey();
//             double product = current.getValue();

//             if (node.equals(end)) return product;

//             for (Map.Entry<String, Double> neighbor : graph.get(node).entrySet()) {
//                 String next = neighbor.getKey();
//                 if (!visited.contains(next)) {
//                     visited.add(next);
//                     queue.offer(new Pair<>(next, product * neighbor.getValue()));
//                 }
//             }
//         }

//         return -1.0;
//     }
// }

