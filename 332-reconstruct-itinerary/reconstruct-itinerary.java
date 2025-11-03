// Method 1: Hierholzer-style DFS (Eulerian-path style)
/*
## Key ideas (read this first)

* Model tickets as a **directed multigraph**: `from -> to`. Multiple identical tickets = **parallel edges**.
* **Do not** use a `visited` set for **nodes**. You’re allowed to revisit airports many times.
* You must “mark” **edges** as used. In practice that means **consuming** them (removing from an adjacency structure or decreasing a count) during traversal.
* Because the answer must be **lexicographically smallest**, your adjacency for each airport should give you the **smallest destination first** (e.g., a min-heap or sorted list you consume from).


### A) Hierholzer-style DFS (Eulerian-path style)

Use when you want an elegant, linear backtracking:

1. Build `graph[from] = min-ordered collection of tos` (e.g., min-heap or sorted deque).
2. Start at `"JFK"`.
   While the current node has outgoing edges:

   * Repeatedly “take” the **smallest** available `to` and recurse from there (consuming that edge).
3. When a node has **no** more outgoing edges, **append** that airport to an `itinerary` (this is a postorder push).
4. At the end you’ll have the route in **reverse**. Reverse it once (or prepend during construction).

**Why it works:** You keep walking forward greedily, and whenever you hit a dead end you freeze that node’s position in the final path (classic Hierholzer). Because you always consume the **smallest** next edge first, the final itinerary is lexicographically smallest.

**Handling duplicates:** If multiple same `from→to` tickets exist, your adjacency must be able to hold **counts** (e.g., a multiset or map `to -> count`) so you can consume one at a time.


## Handling “visited”

* **Don’t track visited airports.** Airports can and will be revisited.
* Track **used tickets**:

  * Hierholzer: consuming/removing from adjacency as you traverse implicitly marks an edge used.
  * Backtracking: decrement/increment counts for each `(from, to)`.

---

## Data structure hints

* Adjacency: `Map<String, MinStructureOf<String>>`

  * Hierholzer: a **min-heap** per origin makes “always smallest next” easy.
  * Backtracking: a **sorted list** or a **TreeMap to->count** lets you iterate in order and adjust counts.
* Result placement:

  * Hierholzer: push airport **after** exhausting its edges → result is built **in reverse**.
  * Backtracking: grow the route forward; success when route length = `tickets + 1`.

---

## Tiny walkthrough (Hierholzer style)

Tickets:

```
[JFK, MUC], [MUC, LHR], [LHR, SFO], [SFO, SJC]
```

Adjacency (min-ordered):
`JFK -> [MUC]`, `MUC -> [LHR]`, `LHR -> [SFO]`, `SFO -> [SJC]`

* Start `JFK`: consume `JFK→MUC`, go to `MUC`.
* `MUC`: consume `MUC→LHR`, go to `LHR`.
* `LHR`: consume `LHR→SFO`, go to `SFO`.
* `SFO`: consume `SFO→SJC`, go to `SJC`.
* `SJC`: no outgoing → append `SJC`.
* Back to `SFO`: no more → append `SFO`.
* Back to `LHR`: append `LHR`.
* Back to `MUC`: append `MUC`.
* Back to `JFK`: append `JFK`.
  Reverse: `[JFK, MUC, LHR, SFO, SJC]`.

With branching, always consume the smallest `to` first; the postorder ensures global correctness.

---

## Common pitfalls

* Using a `visited` set for nodes (wrong). You’ll prematurely block valid revisits.
* Not consuming edges (you’ll reuse tickets).
* Building the path in preorder (you’ll miss edges / get stuck). For Hierholzer, **postorder** is key.
* Forgetting duplicates: multiple identical tickets require **counts**.
* Not guaranteeing lexicographic choice: you must **sort** per origin or use a **min-heap**.

---

## How to adapt your skeleton

* Replace `visited` (node) with a structure to **consume edges**:

  * Hierholzer: `Map<String, PriorityQueue<String>>` (or `TreeMap<String,Integer>` counts).
  * Backtracking: `Map<String, TreeMap<String,Integer>>` and a `route` list whose final size is `tickets+1`.
* In DFS:

  * **Hierholzer:** while there’s an outgoing edge from `from`, take the **smallest** `to`, recurse; then append `from` to the result.
  * **Backtracking:** try next `to` in sorted order, decrement count, recurse; if fail, increment back.

Stick to these hints, and your DFS will pass with the correct lexicographic itinerary.

###############

# Another way to store the graph:

You want each origin to expose its **smallest destination first** and you need to be able to **consume** tickets (edges). Here are three clean ways to build that adjacency, depending on which DFS style you’ll use.

## Pre-sort lists, consume from the end in O(1)
* Avoids heap overhead; still guarantees smallest-next if you pop from the **end**.
* Trick: sort **ascending**, then either:

  * store **descending** and `pollLast()`, or
  * store ascending and pop from the **front** using a `Deque` (not `ArrayList`, because `remove(0)` is O(n)).

```java
Map<String, List<String>> tmp = new HashMap<>();
for (List<String> t : tickets) {
    String from = t.get(0), to = t.get(1);
    tmp.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
}
Map<String, Deque<String>> g = new HashMap<>();
for (Map.Entry<String, List<String>> e : tmp.entrySet()) {
    List<String> lst = e.getValue();
    Collections.sort(lst);                    // ascending
    Collections.reverse(lst);                 // now descending
    g.put(e.getKey(), new ArrayDeque<>(lst)); // popLast() gives smallest
}
```

**Use it when:** your DFS does `while (!g[u].isEmpty()) { String v = g[u].pollLast(); dfs(v); }` and then appends `u` postorder.
*/
class Solution {
    public List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> g = new HashMap<>();

        for(List<String> e: tickets){
            String from = e.get(0);
            String to = e.get(1);

            g.computeIfAbsent(from, k -> new PriorityQueue<>()).offer(to);

            // While building the graph, you can also ensure sinks exist to avoid null checks
            g.computeIfAbsent(to, k -> new PriorityQueue<>());  // ensures key for sinks
        }

        List<String> ans = new ArrayList<>();

        dfs(g, ans, "JFK");
        Collections.reverse(ans);
        
        return ans;
    }

    
    private void dfs(Map<String, PriorityQueue<String>> g, List<String> ans, String from){
        PriorityQueue<String> pq = g.get(from);
        
        while(pq != null && !pq.isEmpty()){
            String to = pq.poll();  // consume the smallest available ticket
            dfs(g, ans, to);
        }

        ans.add(from);  // postorder push (will be reversed later)
    }
}







// Method 2: Backtracking with edge-counts
/*
## Why this works

* We need to use **every ticket exactly once** and return the **lexicographically smallest** valid route starting at `"JFK"`.
* Model tickets as a **multiset of edges**: `from -> (to -> count)`.
* Keep each origin’s destinations in a **TreeMap** (sorted ascending).
  During DFS, we try destinations in lexicographic order; if a path fails, we **restore** the used count and backtrack.
* The **first** complete path we find (after trying choices in sorted order) is already the **lexicographically smallest** itinerary.

**Key differences from the Hierholzer approach:**

* Here we do classic DFS with **explicit decrement/restore** counts (backtracking).
* We build the route **forward** (no reverse at the end).
* Perfect when you want the most “textbook” backtracking feel.

---

## Complexity

* Building the graph: (O(E \log D)) due to TreeMap inserts (where (E) is #tickets and (D) typical branching).
* Backtracking worst-case can be exponential, but with lexicographic pruning and problem constraints, it’s fast in practice (and accepted on LC).
* Memory: (O(E)) for counts + (O(V)) for keys, and recursion depth up to (E).

---

## Walkthrough 1 (linear path)

**Tickets**

```
[JFK, MUC], [MUC, LHR], [LHR, SFO], [SFO, SJC]
```

**Graph (TreeMap per origin)**

```
JFK -> { MUC:1 }
MUC -> { LHR:1 }
LHR -> { SFO:1 }
SFO -> { SJC:1 }
SJC -> { }        // added as empty so we can land there
```

**Backtracking**

* route = [JFK]; from=JFK → try MUC (count 1→0), route=[JFK,MUC]
* from=MUC → LHR (1→0), route=[JFK,MUC,LHR]
* from=LHR → SFO (1→0), route=[JFK,MUC,LHR,SFO]
* from=SFO → SJC (1→0), route=[JFK,MUC,LHR,SFO,SJC]
* route length = tickets + 1 = 5 → success.
  Answer: `[JFK, MUC, LHR, SFO, SJC]`.

---

## Walkthrough 2 (branching + lexicographic choice)

**Tickets**

```
[JFK, KUL], [JFK, NRT], [NRT, JFK]
```

Sorted destinations:

```
JFK -> { KUL:1, NRT:1 }   // "KUL" < "NRT"
NRT -> { JFK:1 }
KUL -> { }                // empty for sink
```

**Backtracking (lexicographic!)**

* route=[JFK]
* From JFK, **try KUL first** (smallest): use (JFK→KUL)

  * route=[JFK,KUL]; KUL has no outgoing, but route size=2 < 4 → dead end → backtrack
* Still at JFK, next try **NRT**: use (JFK→NRT)

  * route=[JFK,NRT]; from NRT → must use (NRT→JFK)

    * route=[JFK,NRT,JFK]; from JFK → only KUL left (count 1)

      * route=[JFK,NRT,JFK,KUL]; size=4 (= E+1) → success.

Because we tried destinations in ascending order and backtracked when needed, the **first** complete route we found is lexicographically smallest:
`[JFK, NRT, JFK, KUL]`.

---

## Common pitfalls (and how this code avoids them)

* **Using a visited set of airports:** wrong abstraction. Airports can be revisited; we must consume **tickets** (edge counts).
* **Not handling duplicate tickets:** counts (multiset) handle parallel edges cleanly.
* **Forgetting lexicographic order:** TreeMap keeps `to` in sorted order; the first full route is the smallest.
* **Dead-end handling:** if an origin has no remaining outgoing tickets before we’ve used them all, the recursion naturally backtracks.
* **Missing sink keys:** we `computeIfAbsent(to, …)` so landing at a pure sink won’t cause null surprises.
*/

// class Solution {
//     public List<String> findItinerary(List<List<String>> tickets) {
//         // Build: from -> (to -> count) in lexicographic order
//         Map<String, TreeMap<String, Integer>> g = new HashMap<>();
//         for (List<String> t : tickets) {
//             String from = t.get(0), to = t.get(1);
//             g.computeIfAbsent(from, k -> new TreeMap<>())
//              .merge(to, 1, Integer::sum);
//             // ensure destination exists as a key (helps avoid null checks)
//             g.computeIfAbsent(to, k -> new TreeMap<>());
//         }

//         // Route will have exactly tickets.size() + 1 airports
//         int targetLen = tickets.size() + 1;
//         List<String> route = new ArrayList<>(targetLen);
//         route.add("JFK");

//         // Backtrack; by iterating TreeMap in ascending order,
//         // the first complete route we find is lexicographically smallest.
//         backtrack(g, route, targetLen);
//         return route;
//     }

//     private boolean backtrack(Map<String, TreeMap<String, Integer>> g,
//                               List<String> route,
//                               int targetLen) {
//         if (route.size() == targetLen) return true; // used all tickets

//         String from = route.get(route.size() - 1);
//         TreeMap<String, Integer> dests = g.get(from);
//         if (dests == null) return false; // dead end

//         for (Map.Entry<String, Integer> e : dests.entrySet()) {
//             String to = e.getKey();
//             int cnt = e.getValue();
//             if (cnt == 0) continue;      // this ticket already used up

//             // choose
//             dests.put(to, cnt - 1);
//             route.add(to);

//             // explore
//             if (backtrack(g, route, targetLen)) return true;

//             // un-choose (backtrack)
//             route.remove(route.size() - 1);
//             dests.put(to, cnt);
//         }
//         return false;
//     }
// }
