// Method 1: Using BFS 

import java.util.*;

class Solution {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        
        // The below works for building graph, or even these work: 
        // Map<String, Map<String, Double>> graph
        // HashMap<String, List<Pair<String, Double>>> graph

        // Build graph: u -> (v, weight)
        Map<String, List<AbstractMap.SimpleEntry<String, Double>>> graph = new HashMap<>();

        for (int i = 0; i < equations.size(); i++) {
            String u = equations.get(i).get(0);
            String v = equations.get(i).get(1);
            double k = values[i];

            graph.putIfAbsent(u, new ArrayList<>());
            graph.putIfAbsent(v, new ArrayList<>());

            graph.get(u).add(new AbstractMap.SimpleEntry<>(v, k));        // u -> v (k)
            graph.get(v).add(new AbstractMap.SimpleEntry<>(u, 1.0 / k));  // v -> u (1/k)
        }

        List<Double> ans = new ArrayList<>(queries.size());

        for (int i = 0; i < queries.size(); i++) {
            String src = queries.get(i).get(0);
            String dst = queries.get(i).get(1);

            // missing vars
            if (!graph.containsKey(src) || !graph.containsKey(dst)) {
                ans.add(-1.0);
                continue;
            }
            // same var
            if (src.equals(dst)) {
                ans.add(1.0);
                continue;
            }

            // BFS: queue holds (node, productFromSrc)
            Deque<AbstractMap.SimpleEntry<String, Double>> q = new ArrayDeque<>();
            Set<String> visited = new HashSet<>();

            q.offer(new AbstractMap.SimpleEntry<>(src, 1.0));
            visited.add(src);

            double result = -1.0;

            while (!q.isEmpty()) {
                AbstractMap.SimpleEntry<String, Double> cur = q.poll();
                String u = cur.getKey();
                double prod = cur.getValue();

                if (u.equals(dst)) {        // found target
                    result = prod;
                    break;
                }

                for (AbstractMap.SimpleEntry<String, Double> e : graph.get(u)) {
                    String v = e.getKey();
                    double w = e.getValue();
                    if (!visited.contains(v)) {
                        visited.add(v);
                        q.offer(new AbstractMap.SimpleEntry<>(v, prod * w));
                    }
                }
            }

            ans.add(result);
        }

        // convert to double[]
        double[] out = new double[ans.size()];
        for (int i = 0; i < ans.size(); i++) out[i] = ans.get(i);
        return out;
    }
}



// Method 2: Shortest Path Appraoch (Bellman-Ford Algorithm)
/*
Totally—here’s how to do **Shortest Path** for 399 using a clean, principled reduction to graph shortest paths.

## Big idea (why shortest path makes sense)

For each equation `A / B = k`, build a directed edge:

* `A → B` with weight `k`
* `B → A` with weight `1/k`

A query `X / Y` is the **product** of edge weights along any path from `X` to `Y`. To turn “product along a path” into a **sum** so we can use shortest paths, take logs:

* Let `cost(u→v) = -log(weight(u→v))`.
* Then the path product `Π w_i` becomes `exp( - Σ cost_i )`.

So, **minimizing** the sum of `cost`s gives the correct product after exponentiating back.

### Why not Dijkstra?

`cost = -log(k)` can be **negative** when `k > 1` (since `log(k) > 0`), and Dijkstra requires **non-negative** edge weights. Use **Bellman–Ford** (handles negative weights; no negative cycles here under consistent equations).


### Complexity

* Building the graph: O(E) with E ≈ 2·(#equations)
* Each query: Bellman–Ford O(V·E); in LeetCode 399, V is small (distinct variables), so this is perfectly fine.

---

## Step-by-step example walkthrough

**Equations**

```
a / b = 2.0
b / c = 3.0
```

**Queries**

```
a / c   ,   c / a
```

**Graph edges (weights)**

* `a → b` weight `2.0`  → cost = `-log(2)` ≈ `-0.6931`
* `b → a` weight `1/2`  → cost = `-log(0.5)` = `+0.6931`
* `b → c` weight `3.0`  → cost = `-log(3)` ≈ `-1.0986`
* `c → b` weight `1/3`  → cost = `-log(1/3)` = `+1.0986`

**Query 1: a / c**

* Initialize: `dist[a]=0`, others `+∞`.
* Relax edges:

  * From `a`: `dist[b] = min(+∞, 0 + (-0.6931)) = -0.6931`
  * From `b`: `dist[c] = min(+∞, -0.6931 + (-1.0986)) = -1.7918` (which is `-log(6)`)
* No more improvements after another pass.
* `dist[c] = -log(6)` → answer `exp(-dist[c]) = exp(log(6)) = 6.0`.

**Query 2: c / a**

* Initialize: `dist[c]=0`, others `+∞`.
* Relax:

  * From `c`: `dist[b] = 0 + 1.0986 = 1.0986`
  * From `b`: `dist[a] = 1.0986 + 0.6931 = 1.7917` (≈ `log(6)`)
* Answer: `exp(-dist[a]) = exp(-log(6)) = 1/6`.

**Why it works**

* Products along a path become sums via log.
* Shortest sum path (Bellman–Ford) gives us the correct total log-ratio; exponentiating returns the ratio.

---

## Notes & gotchas

* Use **uppercase** `'O'`? (Not relevant here, but common LC typo.)
* Don’t use Dijkstra on `-log(k)` since those edges can be negative.
* If you prefer **all-pairs** upfront, you can run **Floyd–Warshall** on the log-costs in O(V³) and answer queries in O(1).
* Precision: doubles are fine for LC 399; avoid integer division.

If you’d like the Floyd–Warshall variant or a version that caches results across queries, I can share that too.
*/
// import java.util.*;

// class Solution {

//     static class Edge {
//         int u, v;
//         double w; // w = -log(weight)
//         Edge(int u, int v, double w) { this.u = u; this.v = v; this.w = w; }
//     }

//     public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
//         // 1) Map variables to integer ids
//         Map<String, Integer> id = new HashMap<>();
//         int nextId = 0;
//         for (List<String> eq : equations) {
//             String a = eq.get(0), b = eq.get(1);
//             if (!id.containsKey(a)) id.put(a, nextId++);
//             if (!id.containsKey(b)) id.put(b, nextId++);
//         }

//         int n = nextId;
//         List<Edge> edges = new ArrayList<>();

//         // 2) Build directed edges with costs = -log(weight)
//         for (int i = 0; i < equations.size(); i++) {
//             String a = equations.get(i).get(0);
//             String b = equations.get(i).get(1);
//             double k = values[i];

//             int u = id.get(a), v = id.get(b);
//             edges.add(new Edge(u, v, -Math.log(k)));       // a -> b  with cost -log(k)
//             edges.add(new Edge(v, u, -Math.log(1.0 / k))); // b -> a  with cost -log(1/k) = +log(k)
//         }

//         double[] out = new double[queries.size()];

//         // 3) Answer each query via Bellman–Ford from src to dst
//         for (int qi = 0; qi < queries.size(); qi++) {
//             String srcS = queries.get(qi).get(0);
//             String dstS = queries.get(qi).get(1);

//             // Missing variables
//             if (!id.containsKey(srcS) || !id.containsKey(dstS)) {
//                 out[qi] = -1.0;
//                 continue;
//             }

//             int src = id.get(srcS), dst = id.get(dstS);

//             // Trivial same-variable query (as long as the variable exists)
//             if (src == dst) {
//                 out[qi] = 1.0;
//                 continue;
//             }

//             // Bellman–Ford: dist[] are sums of costs (-log products)
//             double[] dist = new double[n];
//             Arrays.fill(dist, Double.POSITIVE_INFINITY);
//             dist[src] = 0.0;

//             // Relax edges up to (n-1) times
//             for (int it = 0; it < n - 1; it++) {
//                 boolean any = false;
//                 for (Edge e : edges) {
//                     if (dist[e.u] != Double.POSITIVE_INFINITY && dist[e.u] + e.w < dist[e.v]) {
//                         dist[e.v] = dist[e.u] + e.w;
//                         any = true;
//                     }
//                 }
//                 if (!any) break; // early stop if no updates
//             }

//             if (dist[dst] == Double.POSITIVE_INFINITY) {
//                 out[qi] = -1.0; // unreachable
//             } else {
//                 // result = exp( - dist[dst] )
//                 out[qi] = Math.exp(-dist[dst]);
//             }
//         }

//         return out;
//     }
// }






// Method 3: Recursive DFS
/*
Idea:
Each query src / dst asks: is there a path from src to dst?
Explore with recursion:
If src == dst, return the current product (base case).
For each neighbor (n, w) of src, if not visited, recurse on n with product × w.
If no path, return -1.0.
*/

// import java.util.*;

// class SolutionRecursiveDFS {
//     static class Edge {
//         String to;
//         double w;
//         Edge(String to, double w) { this.to = to; this.w = w; }
//     }

//     public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
//         Map<String, List<Edge>> graph = new HashMap<>();

//         // Build graph
//         for (int i = 0; i < equations.size(); i++) {
//             String a = equations.get(i).get(0);
//             String b = equations.get(i).get(1);
//             double k = values[i];
//             graph.putIfAbsent(a, new ArrayList<>());
//             graph.putIfAbsent(b, new ArrayList<>());
//             graph.get(a).add(new Edge(b, k));
//             graph.get(b).add(new Edge(a, 1.0 / k));
//         }

//         double[] res = new double[queries.size()];
//         for (int i = 0; i < queries.size(); i++) {
//             String src = queries.get(i).get(0);
//             String dst = queries.get(i).get(1);

//             if (!graph.containsKey(src) || !graph.containsKey(dst)) {
//                 res[i] = -1.0;
//             } else if (src.equals(dst)) {
//                 res[i] = 1.0;
//             } else {
//                 res[i] = dfs(graph, src, dst, new HashSet<>(), 1.0);
//             }
//         }
//         return res;
//     }

//     private double dfs(Map<String, List<Edge>> graph, String cur, String target,
//                        Set<String> visited, double accProduct) {
//         if (cur.equals(target)) return accProduct;
//         visited.add(cur);

//         for (Edge e : graph.get(cur)) {
//             if (!visited.contains(e.to)) {
//                 double ans = dfs(graph, e.to, target, visited, accProduct * e.w);
//                 if (ans != -1.0) return ans; // found a valid path
//             }
//         }
//         return -1.0; // no path found
//     }
// }





// Method 4: Iterative DFS
/*
Idea:
Instead of recursion, use an explicit stack storing (node, productSoFar).
Pop node:
    If node == target, return product.
    Otherwise push all unvisited neighbors with updated product.
*/
// import java.util.*;

// class SolutionIterativeDFS {
//     static class Edge {
//         String to;
//         double w;
//         Edge(String to, double w) { this.to = to; this.w = w; }
//     }

//     static class State {
//         String node;
//         double prod;
//         State(String node, double prod) { this.node = node; this.prod = prod; }
//     }

//     public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
//         Map<String, List<Edge>> graph = new HashMap<>();

//         // Build graph
//         for (int i = 0; i < equations.size(); i++) {
//             String a = equations.get(i).get(0);
//             String b = equations.get(i).get(1);
//             double k = values[i];
//             graph.putIfAbsent(a, new ArrayList<>());
//             graph.putIfAbsent(b, new ArrayList<>());
//             graph.get(a).add(new Edge(b, k));
//             graph.get(b).add(new Edge(a, 1.0 / k));
//         }

//         double[] res = new double[queries.size()];
//         for (int i = 0; i < queries.size(); i++) {
//             String src = queries.get(i).get(0);
//             String dst = queries.get(i).get(1);

//             if (!graph.containsKey(src) || !graph.containsKey(dst)) {
//                 res[i] = -1.0;
//             } else if (src.equals(dst)) {
//                 res[i] = 1.0;
//             } else {
//                 res[i] = dfsIter(graph, src, dst);
//             }
//         }
//         return res;
//     }

//     private double dfsIter(Map<String, List<Edge>> graph, String src, String dst) {
//         Set<String> visited = new HashSet<>();
//         Deque<State> stack = new ArrayDeque<>();
//         stack.push(new State(src, 1.0));
//         visited.add(src);

//         while (!stack.isEmpty()) {
//             State cur = stack.pop();
//             if (cur.node.equals(dst)) return cur.prod;

//             for (Edge e : graph.get(cur.node)) {
//                 if (!visited.contains(e.to)) {
//                     visited.add(e.to);
//                     stack.push(new State(e.to, cur.prod * e.w));
//                 }
//             }
//         }
//         return -1.0;
//     }
// }
