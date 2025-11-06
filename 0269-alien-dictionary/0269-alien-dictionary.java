// Method 1: Khan's Algorithm for Topological Ordering and cycle detection (BFS with indegree) 
/*
### 1. Read the problem as: “recover an ordering from sorted strings”

You’re given words sorted according to some unknown alphabet order.

Think: if you had normal English sorted words:

* `["baa", "abcd", "abca", "cab", "cad"]`
  You can infer:
* From `baa` vs `abcd`: first differing char is `b` vs `a` → `b` comes before `a`.
* From `abcd` vs `abca`: first differing char is `d` vs `a` → `d` comes before `a`.
  …etc.

Exactly that, but on arbitrary letters.

---

### 2. Build a directed graph of letter constraints

Hints (no code):

1. Collect **all unique characters** present in all words. Each is a node.
2. For each **adjacent pair of words** `(w1, w2)`:

   * Find the **first position** `i` where they differ.
   * If found:

     * Add a directed edge: `w1[i] -> w2[i]`

       * Meaning: this character must come **before** that character.
     * Stop checking further characters in this pair.
   * If **no difference** within the min length:

     * Then if `w1` is **longer** than `w2`, it’s invalid (e.g., `["abc", "ab"]`).

       * This should make the answer `""`.

Your graph is: nodes = letters, edges = “must come before”.

---

### 3. Detect the letter order using topological sort

You now want a topological ordering of this directed graph:

* If there is a **cycle**, no valid order exists → `""`.
* Otherwise, any topological order consistent with all edges is valid.

Hints:

* Use either:

  * **Kahn’s algorithm (BFS with indegree)**, or
  * **DFS-based topo sort with cycle detection**.
* While topo-sorting, you **must include all letters**, even ones with no edges (no constraints).

For Kahn-style:

* Compute indegree for each node.
* Push all nodes with indegree 0 into a queue.
* Repeatedly pop, append to result, and decrease indegrees of neighbors.
* At the end:

  * If result length < number of unique chars → cycle/invalid → `""`.

---

### 4. Important edge cases to watch (these burn people)

1. **Prefix rule**:

   * `["wrtkj","wrt"]` is invalid because longer word comes before its prefix.
   * You’ll detect this when you compare and find no differing char and `w1.length > w2.length`.

2. **Characters with no edges**:

   * They must still appear in the answer.
   * Initialize them as nodes with indegree 0 if they never get edges.

3. **Multiple valid orders**:

   * The problem accepts any valid one.
   * You don’t need lexicographically smallest unless explicitly asked.

4. **Cycle detection**:

   * If topological sort doesn’t visit all characters → there’s a cycle in the constraints → return `""`.

---

### 5. Mental model to check your solution

Once you have an order string, you can verify it conceptually:

* Define a rank for each char by its position in your order.
* For every adjacent pair of words in the original list:

  * Find the first differing char.
  * Make sure `rank[w1[i]] < rank[w2[i]]`.
  * If no differing char, ensure `len(w1) <= len(w2)`.

###################################

## Core idea

You have a directed graph:

* Nodes = items you want to order (letters, tasks, etc.)
* Edge `u → v` = “u must come before v”.

A **topological order** is a linear ordering of all nodes such that **every** edge `u → v` has `u` before `v` in the order.

Kahn’s algorithm builds such an order by:

1. Always picking a node with **no remaining prerequisites** (indegree 0).
2. Outputting it.
3. Pretending to remove it (and its edges) from the graph, which may cause new nodes to become indegree 0.
4. Repeating until no nodes left (or you get stuck → cycle).

---

## Ingredients

### 1. Indegree

For each node `x`, define:

> **indegree[x]** = number of incoming edges (how many nodes must come before `x`).

Example: edges `A→C`, `B→C`, `C→D`:

* indegree[A] = 0
* indegree[B] = 0
* indegree[C] = 2 (from A,B)
* indegree[D] = 1 (from C)

### 2. Queue of “ready” nodes

All nodes with `indegree == 0` are “ready” to appear next in the order (no unmet prerequisites). We collect them in a **queue**:

* Using a queue makes this a BFS-style traversal “from prerequisites to dependents.”

---

## Kahn’s algorithm: steps

Given:

* `g`: adjacency list; `g[u]` = list of neighbors `v` such that `u → v`.
* `indegree[v]` computed from edges.

Algorithm:

1. **Initialize queue**

   * Push every node with `indegree[node] == 0` into `q`.

2. **Process queue**

   * While `q` not empty:

     * Pop `u` from `q`.
     * Append `u` to result order.
     * For each neighbor `v` in `g[u]`:

       * Decrease `indegree[v]` by 1 (we “used” u).
       * If `indegree[v]` becomes 0, push `v` into `q`.

3. **Check for cycles**

   * If we output all nodes → success; that order is a valid topological sort.
   * If some nodes never reach indegree 0 (i.e., result length < total nodes) → there is a cycle → no valid topo order.

---

## Mapping to your Alien Dictionary snippet

You had:

```java
Queue<Character> q = new ArrayDeque<>();
for (char c : g.keySet()) {
    if (indegree[c - 'a'] == 0) {
        q.offer(c);
    }
}

StringBuilder sb = new StringBuilder();
while (!q.isEmpty()) {
    char u = q.poll();
    sb.append(u);
    for (char v : g.get(u)) {
        indegree[v - 'a']--;
        if (indegree[v - 'a'] == 0) {
            q.offer(v);
        }
    }
}
```

This is exactly Kahn:

* `q` holds all letters whose prerequisites are satisfied.
* When you append `u`, you conceptually remove it from the graph.
* For each `v` after `u`, you reduce its indegree.
* When `indegree[v]` hits 0, `v` is now free to appear next, so you add it to `q`.
* At the end, if `sb.length() == g.size()`, no cycles; else, invalid (cycle among letters).

---

## Detailed example walkthrough (Alien-style)

Let’s use a small Alien Dictionary example:

Words:

```text
wrt
wrf
er
ett
rftt
```

Step 1: Extract constraints from adjacent pairs (I’ll just state the edges):

* From `"wrt"` vs `"wrf"` → first diff: `t` vs `f` → `t → f`
* From `"wrf"` vs `"er"` → first diff: `w` vs `e` → `w → e`
* From `"er"` vs `"ett"` → first diff: `r` vs `t` → `r → t`
* From `"ett"` vs `"rftt"` → first diff: `e` vs `r` → `e → r`

Graph edges:

```text
w → e
e → r
r → t
t → f
```

All characters: `{w, e, r, t, f}`

### 1. Compute indegree

* indegree[w] = 0      (no one points to w)
* indegree[e] = 1      (from w)
* indegree[r] = 1      (from e)
* indegree[t] = 1      (from r)
* indegree[f] = 1      (from t)

### 2. Init queue with indegree 0

* `q = [w]`

### 3. BFS loop

**Iteration 1**

* Pop `w`; append → result: `"w"`
* Look at neighbors:

  * `w → e`: indegree[e]-- (1 → 0)
* e now has indegree 0 → push `e`.
* `q = [e]`

**Iteration 2**

* Pop `e`; append → `"we"`
* Neighbors:

  * `e → r`: indegree[r]-- (1 → 0)
* Push `r`.
* `q = [r]`

**Iteration 3**

* Pop `r`; append → `"wer"`
* Neighbors:

  * `r → t`: indegree[t]-- (1 → 0)
* Push `t`.
* `q = [t]`

**Iteration 4**

* Pop `t`; append → `"wert"`
* Neighbors:

  * `t → f`: indegree[f]-- (1 → 0)
* Push `f`.
* `q = [f]`

**Iteration 5**

* Pop `f`; append → `"wertf"`
* `f` has no neighbors.

Queue empty. We appended all 5 chars; no cycles.

Final topo order: `"wertf"` — this is a valid alien alphabet consistent with the input words.

### Why this respects constraints

Check edges:

* `w` before `e` ✓
* `e` before `r` ✓
* `r` before `t` ✓
* `t` before `f` ✓

And because we always only enqueue characters whose all prerequisites are already output, the order is guaranteed to be valid.

---

## Cycle example (why we need the length check)

Suppose constraints:

```text
a → b
b → c
c → a
```

* indegree[a] = 1, indegree[b] = 1, indegree[c] = 1
* No node with indegree 0 → queue empty from the start.
* We never append anything → result length 0 < 3 → cycle → no topo order.

In Alien Dictionary, that means constraints are contradictory; return `""`.

---

## Summary mental model

* **Indegree = remaining prerequisites.**
* **Queue = all currently “unblocked” nodes.**
* Repeatedly:

  * Take a free node, append it.
  * Notify its neighbors that one prerequisite is done (indegree--).
  * Any neighbor with indegree 0 joins the queue.
* If you can schedule everyone → topological order.
* If some nodes always have indegree > 0 → there’s a cycle.

That’s all Kahn’s algorithm is.
*/
class Solution{
    public String foreignDictionary(String[] words) {
        // Graph: char -> list of next chars
        Map<Character, List<Character>> g = new HashMap<>();
        // indegree[c] = how many prerequisites this char has; -1 means "char not present"
        int[] indegree = new int[26];
        Arrays.fill(indegree, -1);

        // 1. Add all unique characters as nodes
        for (String w : words) {
            for (char c : w.toCharArray()) {
                if (!g.containsKey(c)) {
                    g.put(c, new ArrayList<>());
                    indegree[c - 'a'] = 0;
                }
            }
        }

        // 2. Build edges from *adjacent* word pairs
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i];
            String w2 = words[i + 1];

            int minLen = Math.min(w1.length(), w2.length());
            int j = 0;

            // find first differing character
            while (j < minLen && w1.charAt(j) == w2.charAt(j)) {
                j++;
            }

            // Case A: no difference within minLen
            if (j == minLen) {
                // If w1 is longer, invalid (prefix problem: "abc", "ab")
                if (w1.length() > w2.length()) {
                    return "";
                }
                // else no ordering info from this pair; continue
                continue;
            }

            // Case B: we found a differing position j
            char from = w1.charAt(j);
            char to = w2.charAt(j);

            // Add edge from -> to if not already present
            List<Character> adj = g.get(from);
            if (!adj.contains(to)) {
                adj.add(to);
                indegree[to - 'a']++;
            }
        }

        // 3. Topological sort (Kahn's) to get order
        Queue<Character> q = new ArrayDeque<>();
        for (char c : g.keySet()) {
            if (indegree[c - 'a'] == 0) {
                q.offer(c);
            }
        }

        StringBuilder sb = new StringBuilder();
        while (!q.isEmpty()) {
            char u = q.poll();
            sb.append(u);
            for (char v : g.get(u)) {
                indegree[v - 'a']--;
                if (indegree[v - 'a'] == 0) {
                    q.offer(v);
                }
            }
        }

        // If we didn't include all chars, there was a cycle → invalid
        return sb.length() == g.size() ? sb.toString() : "";
    }
}



// Method 2: DFS-based topo sort with cycle detection
/*
## How this DFS topo works (conceptually)

We want an order where for every edge `u -> v` (u must come before v), `u` appears before `v` in the final string.

DFS topo sort uses:

* A **color/state** per node:

  * `0` (unvisited)
  * `1` (visiting = in recursion stack)
  * `2` (visited = fully processed)
* **Postorder append**:

  * Recurse into neighbors first.
  * When a node is fully done, append it to `order`.
  * This naturally pushes prerequisites later in the string, so when we reverse at the end we get a valid topological order.
* **Cycle detection**:

  * If during DFS from `u` you see an edge to a node `v` that is currently `visiting (1)`, you’ve found a **back edge** → there is a cycle → no valid ordering.

This is the DFS analogue of Kahn’s algorithm.

---

## Detailed example walkthrough

Use the same classic example:

```text
["wrt", "wrf", "er", "ett", "rftt"]
```

### 1. Build graph

Adjacent pairs:

1. `"wrt"` vs `"wrf"`

   * Compare: `w==w`, `r==r`, then `t!=f` → edge `t -> f`.

2. `"wrf"` vs `"er"`

   * `w!=e` → edge `w -> e`.

3. `"er"` vs `"ett"`

   * `e==e`, `r!=t` → edge `r -> t`.

4. `"ett"` vs `"rftt"`

   * `e!=r` → edge `e -> r`.

Graph `g` (outgoing edges):

* `w: [e]`
* `e: [r]`
* `r: [t]`
* `t: [f]`
* `f: []` (added as node with no outgoing)

So constraints are:

```text
w -> e -> r -> t -> f
```

All characters `{w, e, r, t, f}` are keys in `g`.

### 2. DFS topo with states

`state` initially: all 0.

We iterate `for (c : g.keySet())`. Assume iteration order: `w, e, r, t, f` (hashmap order may vary, but any order works).

#### DFS from `w`

* `state[w] = 1` (visiting)

* neighbors: `e`

  DFS(`e`):

  * `state[e] = 1`

  * neighbors: `r`

    DFS(`r`):

    * `state[r] = 1`

    * neighbors: `t`

      DFS(`t`):

      * `state[t] = 1`

      * neighbors: `f`

        DFS(`f`):

        * `state[f] = 1`
        * neighbors: none
        * mark done: `state[f] = 2`, `order.append('f')`
        * return true

      * back to `t`: all neighbors processed

      * mark done: `state[t] = 2`, append `'t'` → `order = "ft"`

    * back to `r`:

      * mark done: `state[r] = 2`, append `'r'` → `order = "ftr"`

  * back to `e`:

    * mark done: `state[e] = 2`, append `'e'` → `order = "ftre"`

* back to `w`:

  * mark done: `state[w] = 2`, append `'w'` → `order = "ftrew"`

DFS from `w` successful, and it recursively covered all reachable nodes.

#### Remaining nodes

Loop continues:

* For `e, r, t, f`, `state` is already 2 → no new DFS calls.

So final `order` **before reverse** is:

```text
"ftrew"    // reverse postorder
```

We **reverse** it:

```text
"wertf"
```

This is a valid alien alphabet:

* `w` before `e`
* `e` before `r`
* `r` before `t`
* `t` before `f`

All constraints satisfied, no cycle → return `"wertf"`.

---

## What happens on a cycle?

Suppose graph has:

```text
a -> b
b -> c
c -> a
```

DFS:

* Start at `a`: `state[a]=1`

  * DFS(`b`): `state[b]=1`

    * DFS(`c`): `state[c]=1`

      * neighbor `a` has `state[a]=1` → back edge → cycle → return false all the way up.
* You return `""` as soon as `dfs` reports failure.

This is how DFS detects impossible/contradictory orderings.

---

## Key takeaways

* You already built the graph correctly; DFS topo is just:

  * `state` map,
  * recursive DFS with:

    * `1` → `1` edge = cycle
    * postorder append on `2`
  * reverse the result.
* Kahn’s (BFS+indegree) and DFS-topo are equivalent in power; DFS is often shorter once you’re comfortable with recursion + cycle detection.
*/

// class Solution {
//     public String foreignDictionary(String[] words) {
//         // 1. Build graph: char -> list of next chars
//         Map<Character, List<Character>> g = new HashMap<>();

//         // Add all unique characters as nodes
//         for (String w : words) {
//             for (char c : w.toCharArray()) {
//                 g.putIfAbsent(c, new ArrayList<>());
//             }
//         }

//         // 2. Build edges from adjacent word pairs
//         for (int i = 0; i < words.length - 1; i++) {
//             String w1 = words[i];
//             String w2 = words[i + 1];

//             int minLen = Math.min(w1.length(), w2.length());
//             int j = 0;

//             // Find first differing character
//             while (j < minLen && w1.charAt(j) == w2.charAt(j)) {
//                 j++;
//             }

//             // Case A: one is prefix of the other, and longer word comes first -> invalid
//             if (j == minLen) {
//                 if (w1.length() > w2.length()) {
//                     return "";
//                 }
//                 // Otherwise no edge; continue
//                 continue;
//             }

//             // Case B: differing chars define an ordering constraint
//             char from = w1.charAt(j);
//             char to = w2.charAt(j);

//             List<Character> adj = g.get(from);
//             if (!adj.contains(to)) {       // avoid duplicate edges
//                 adj.add(to);
//             }
//         }

//         // 3. DFS-based topological sort with cycle detection

//         // state: 0 = unvisited, 1 = visiting, 2 = visited
//         Map<Character, Integer> state = new HashMap<>();
//         StringBuilder order = new StringBuilder();

//         for (char c : g.keySet()) {
//             if (state.getOrDefault(c, 0) == 0) {
//                 if (!dfs(c, g, state, order)) {
//                     return "";            // cycle detected -> invalid
//                 }
//             }
//         }

//         // We added nodes in reverse topological order during DFS (postorder),
//         // so reverse to get the correct order.
//         return order.reverse().toString();
//     }

//     private boolean dfs(char u,
//                         Map<Character, List<Character>> g,
//                         Map<Character, Integer> state,
//                         StringBuilder order) {

//         state.put(u, 1); // mark as visiting

//         for (char v : g.get(u)) {
//             int sv = state.getOrDefault(v, 0);

//             if (sv == 1) {
//                 // Found a back edge: u -> v where v is in current stack => cycle
//                 return false;
//             }

//             if (sv == 0) {
//                 // Tree edge to unvisited node
//                 if (!dfs(v, g, state, order)) {
//                     return false; // propagate cycle failure up
//                 }
//             }
//             // if sv == 2, already fully processed, nothing to do
//         }

//         // All neighbors processed, mark as done
//         state.put(u, 2);
//         // Postorder append: ensures a node is placed after its neighbors,
//         // building reverse topological order.
//         order.append(u);

//         return true;
//     }
// }
