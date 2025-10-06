// Method 1: Recursive DFS for Search
/*
## Why this matches the intended logic

* **Base case**: when `idx == pattern.length`, we return `node.isWord`. This ensures only complete words match (not just prefixes).
* **Letter case**: follow exactly one edge; if missing, fail fast.
* **Dot case**: explore **all** children (backtracking). If any branch returns `true`, we’re done.
* **Returns on all paths**: both branches return booleans; no fall-through.

---

## Tiny walkthrough

Assume you added: `bad`, `dad`, `mad`.

* `search("pad")`:

  * At root, need `'p'`: missing → `false`.

* `search(".ad")`:

  * `'.'` → branch to `b`, `d`, `m`.
  * Try `b`: next `'a'` ok, next `'d'` ok, end & `isWord=true` → return `true` (short-circuit).

* `search("b..")`:

  * `'b'` ok → `'.'` → branch over children of `'b'` node → continue one more `'.'` similarly; match if any 3-letter word under `b` exists.
*/

class WordDictionary {

    static class TrieNode{
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isWord = false;
    }

    public WordDictionary() {}
    
    
    private final TrieNode root = new TrieNode();

    
    public void addWord(String word) {
        if(word == null){
            return;
        }

        TrieNode cur = root;
        for(int i=0; i<word.length(); i++){
            char ch = word.charAt(i);
            cur = cur.children.computeIfAbsent(ch, k -> new TrieNode());
        }

        cur.isWord = true;
    }
    
    public boolean search(String word) {
        if(word == null){
            return false;
        }

        return dfs(root, word, 0);
    }


    private boolean dfs(TrieNode node, String word, int idx){
        if(idx == word.length()){
            return node.isWord;
        }

        char ch = word.charAt(idx);

        if(ch == '.'){
            for(TrieNode child: node.children.values()){
                if(dfs(child, word, idx + 1)){
                    return true;
                }
            }

            return false;
        }else{
            TrieNode child = node.children.get(ch);
            if(child == null){
                return false;
            }

            return dfs(child, word, idx + 1);
        }

    }
}

/**
 * Your WordDictionary object will be instantiated and called as such:
 * WordDictionary obj = new WordDictionary();
 * obj.addWord(word);
 * boolean param_2 = obj.search(word);
 */





//  Method 2: Recursive DFS for Search + Memoization + Length Pruning
/*
## TL;DR (what to memorize)

* Keep a `Map<TrieNode, Map<Integer, Boolean>> memo`.
* Key = the **trie node** + the **pattern index** (`idx`).
* Value = whether `dfs(node, idx)` returns `true`.
* Before computing, **check memo**; after computing, **store** the result.


### Why this is easy + fast

* **Easy to recall:** “Memo is just a map from `(node, idx)` to boolean.”
  Implemented as `Map<TrieNode, Map<Integer, Boolean>>`.
* **IdentityHashMap** ensures keys are per-node identity (not `.equals`).
* **Length pruning** avoids useless work when the pattern length doesn’t exist.
* **Time saver on dot-heavy patterns:** avoids recomputing the same subproblem via different routes.

### Mini walkthrough

Suppose the trie has `bad`, `dad`, `mad`.
Search `".ad"`:

* `dfs(root, 0)` sees `.`, tries children `b/d/m`.
* Say it tries `b` first → `dfs(bNode, 1)`; then `a` → `dfs(aNode, 2)`; then `d` → `dfs(dNode, 3)` hits base (`idx==len`) and `isWord=true`.
* On backtrack, results get memoized at each `(node, idx)` so if another branch revisits the same state, it returns instantly.

*/

// class WordDictionary {

//     static class TrieNode {
//         Map<Character, TrieNode> children = new HashMap<>();
//         boolean isWord = false;
//     }

//     private final TrieNode root = new TrieNode();
//     private final Set<Integer> lengths = new HashSet<>(); // length pruning

//     public void addWord(String word) {
//         if (word == null) return;
//         TrieNode cur = root;
//         for (int i = 0; i < word.length(); i++) {
//             char ch = word.charAt(i);
//             cur = cur.children.computeIfAbsent(ch, k -> new TrieNode());
//         }
//         cur.isWord = true;
//         lengths.add(word.length());
//     }

//     public boolean search(String pattern) {
//         if (pattern == null) return false;
//         if (!lengths.contains(pattern.length())) return false; // quick reject

//         // Simple memo: node -> (idx -> result)
//         Map<TrieNode, Map<Integer, Boolean>> memo = new IdentityHashMap<>();
//         return dfs(pattern, 0, root, memo);
//     }

//     private boolean dfs(String pat, int idx, TrieNode node,
//                         Map<TrieNode, Map<Integer, Boolean>> memo) {
//         // Memo check
//         Map<Integer, Boolean> atNode = memo.get(node);
//         if (atNode != null && atNode.containsKey(idx)) {
//             return atNode.get(idx);
//         }

//         boolean ans;
//         if (idx == pat.length()) {
//             ans = node.isWord; // only complete words match
//         } else {
//             char ch = pat.charAt(idx);
//             if (ch == '.') {
//                 ans = false;
//                 for (TrieNode child : node.children.values()) {
//                     if (dfs(pat, idx + 1, child, memo)) { ans = true; break; }
//                 }
//             } else {
//                 TrieNode child = node.children.get(ch);
//                 ans = (child != null) && dfs(pat, idx + 1, child, memo);
//             }
//         }

//         // Memo store
//         memo.computeIfAbsent(node, k -> new HashMap<>()).put(idx, ans);
//         return ans;
//     }
// }







//  Method 3: Iterative version for Search
/*

* **Clear data structure:** standard Trie with `Map<Character, TrieNode>`.
* **Dot handling (iterative):** when you see `'.'`, push **all** children with `idx+1`; when a letter, push that one child.
* **Length pruning:** since `.` matches exactly one char, different lengths can’t match—fast reject via `lengths` set.
* **No recursion:** avoids stack-overflow worries and is easy to trace.
* **Seen set:** prevents revisiting the same `(node, idx)` during one search. (In a trie it’s rarely necessary, but it’s a nice guard and keeps the code robust if you tweak things later.)
* **Time complexity:** proportional to the number of visited states. Worst-case you explore many branches when the pattern has lots of dots, but pruning and early success end it quickly.
* **Space complexity:** O(#states on stack + size of trie). The trie grows with the total characters inserted.

### How it works step-by-step (quick mental model)

1. Start with `(root, 0)` on the stack.
2. Pop a state:

   * If `idx == pattern.length()`: return `node.isWord`.
   * Else look at `pattern[idx]`:

     * If it’s a **letter**, push `(child(letter), idx+1)` if that child exists.
     * If it’s a **dot**, push `(each child, idx+1)` for all children.
3. Use `seen` so you don’t push/process the same `(node, idx)` again.
4. If the stack empties, there was no match → return `false`.

That’s it—simple, iterative, and ready for a whiteboard.

*/

// class WordDictionary {

//     static class TrieNode {
//         Map<Character, TrieNode> children = new HashMap<>();
//         boolean isWord = false;
//     }

//     private final TrieNode root = new TrieNode();
//     private final Set<Integer> lengths = new HashSet<>(); // length pruning

//     public WordDictionary() {}

//     // O(L)
//     public void addWord(String word) {
//         if (word == null) return;
//         TrieNode cur = root;
//         for (int i = 0; i < word.length(); i++) {
//             char ch = word.charAt(i);
//             cur = cur.children.computeIfAbsent(ch, k -> new TrieNode());
//         }
//         cur.isWord = true;
//         lengths.add(word.length());
//     }

//     // Iterative DFS (stack), O(#states visited)
//     public boolean search(String pattern) {
//         if (pattern == null) return false;
//         // '.' matches exactly one character, so lengths must match
//         if (!lengths.contains(pattern.length())) return false;

//         // Stack holds (node, nextIndexToMatch)
//         Deque<State> stack = new ArrayDeque<>();
//         stack.push(new State(root, 0));

//         // Avoid re-processing the same (node, idx) within this query
//         Set<State> seen = new HashSet<>();

//         final int n = pattern.length();
//         while (!stack.isEmpty()) {
//             State s = stack.pop();
//             TrieNode node = s.node;
//             int idx = s.idx;

//             // Already processed?
//             if (!seen.add(s)) continue;

//             // Reached end of pattern: match only if we're at a word end
//             if (idx == n) {
//                 if (node.isWord) return true;
//                 continue;
//             }

//             char ch = pattern.charAt(idx);
//             if (ch == '.') {
//                 // Try all children
//                 for (TrieNode child : node.children.values()) {
//                     stack.push(new State(child, idx + 1));
//                 }
//             } else {
//                 TrieNode child = node.children.get(ch);
//                 if (child != null) {
//                     stack.push(new State(child, idx + 1));
//                 }
//             }
//         }
//         return false;
//     }

//     // Small immutable state record for the seen set
//     private static class State {
//         final TrieNode node;
//         final int idx;
//         State(TrieNode node, int idx) { this.node = node; this.idx = idx; }
//         @Override public boolean equals(Object o) {
//             if (this == o) return true;
//             if (!(o instanceof State)) return false;
//             State that = (State) o;
//             return idx == that.idx && node == that.node; // identity compare for node
//         }
//         @Override public int hashCode() { return System.identityHashCode(node) * 31 + idx; }
//     }
// }
