// Method 1: Using Array based Trie (specifically only for lower case inputs)
/*
# Option A design (lowercase ‘a’..‘z’)

* **Node**

  * `TrieNode[] child = new TrieNode[26];`
  * `boolean isWord;` — `true` **only** at the terminal node of an inserted word.
* **Root**

  * Empty node (`isWord = false`, all `child = null`).

### How deletion works (the tricky part)

* We recurse down to the terminal node for `word`.
* If the terminal node **wasn’t** `isWord=true`, the word didn’t exist → return `false` and don’t change anything.
* Otherwise:

  * Set `isWord=false`.
  * If that node now has **no children**, we tell the parent it’s safe to **prune** by returning `true`.
  * Each parent, on the unwind, removes its child pointer if the child was prunable; then the parent itself is prunable only if it **also** has no other children **and** is not an end of another word (`isWord=false`).

This ensures:

* We **never** remove nodes needed by other words (shared prefixes).
* We **do** clean up dead branches to keep the trie compact.

---

## Common pitfalls (compare against your code)

1. **Forgetting `isWord`** or setting it on every node.
   Only set `isWord=true` on the **last** node of the word.

2. **Returning `true` from `search` after just matching the path.**
   You must also check `node.isWord` (exact word), not just that the path exists.

3. **Index math mistakes.**
   `int idx = ch - 'a';` works only for lowercase. Guard against out-of-range chars.

4. **Deletion that doesn’t prune, or prunes too aggressively.**
   You need the recursive “should prune?” boolean. Don’t prune if node is still needed
   (has other children or `isWord=true`).

5. **Empty strings.**
   LC-208 won’t ask to insert empty strings. If your inputs might, decide a policy (e.g., allow and mark `root.isWord=true`), but keeping it out simplifies logic.

---

## Quick walkthrough

Insert `"apple"`, then `"app"`:

* Insert `"apple"`:

  * `a → p → p → l → e`, create nodes as needed.
  * Mark `isWord=true` at `e`.

* Insert `"app"`:

  * Reuse existing `a → p → p`.
  * Mark `isWord=true` at the second `p`.

**Search**

* `search("app")`:

  * Traverse to second `p`, `isWord=true` → **true**.
* `search("appl")`:

  * Traverse to `l`, but `isWord=false` → **false**.

**startsWith**

* `startsWith("appl")`:

  * Traverse to `l` (exists) → **true**.

**Delete `"apple"`**

* Unmark `e.isWord=false`.
* `e` has no children → prune `e`.
* Move to `l`: not `isWord` (since `"app"` ends earlier) and no children now → prune `l`.
* Move to second `p`: `isWord=true` (it marks `"app"`), so **stop pruning here**.

After deletion, trie still stores `"app"` correctly.

---

## Complexity

Let `L` be the string length.

* `insert`, `search`, `startsWith`: **O(L)** time, **O(1)** extra space per operation (not counting new nodes for `insert`).
* `delete`: **O(L)** time, recursion depth `O(L)`.
* Space usage across all words is `O(total characters)` in the worst case.

#################################
Deletion explain in detail
#######################

Awesome — let’s walk through **exactly** how the `delete` method from the array-based Trie works, step by step, under several realistic scenarios. I’ll reference the recursive helper:

```java
private boolean delete(TrieNode node, String word, int i) {
    if (node == null) return false;

    if (i == word.length()) {
        if (!node.isWord) return false;       // word didn't exist
        node.isWord = false;                  // unmark end of word
        return !hasAnyChild(node);            // prune if no children
    }

    int idx = word.charAt(i) - 'a';
    if (idx < 0 || idx >= 26 || node.child[idx] == null) {
        return false;                         // word didn't exist
    }

    boolean childPrunable = delete(node.child[idx], word, i + 1);

    if (childPrunable) node.child[idx] = null;  // physically prune child

    // Current node is prunable only if it isn't a word end and has no other children
    return !node.isWord && !hasAnyChild(node);
}
```

Key idea: the helper returns **“should this node be pruned?”** to its parent. Prune means “parent can set its pointer to `null`”.

---

## Case A — Delete a word that is a **suffix extension** of another (e.g., delete `"apple"` while `"app"` exists)

### Setup

Insert: `"apple"`, `"app"`, `"apt"`.
Trie (letters along edges; a `●` marks `isWord=true`):

```
root
 └─ a
    └─ p
       ├─ p ● (end of "app")
       │  └─ l
       │     └─ e ● (end of "apple")
       └─ t ● (end of "apt")
```

### Delete `"apple"`

We call `delete(root, "apple", 0)` and recurse:

1. `i=0 ('a')` → go down to child `a`.
2. `i=1 ('p')` → go down to child `p`.
3. `i=2 ('p')` → go down to second `p`.
4. `i=3 ('l')` → go down to `l`.
5. `i=4 ('e')` → go down to `e`.
6. `i=5 == word.length()` at node `e`:

   * `e.isWord` is `true` (it marks `"apple"`), set to `false`.
   * `e` has **no children** → return `true` (prunable).

Unwinding:

* At `l`: child `e` is prunable → set `l.child['e']=null`.
  Now `l.isWord=false` and `l` has **no children** → return `true` (prunable).
* At second `p`: child `l` prunable → set `p.child['l']=null`.
  But this `p` **isWord=true** (it ends `"app"`) → **cannot prune**; return `false`.
* Upper nodes receive `false` and do nothing more.

### Result

`"apple"` removed, `"app"` and `"apt"` intact:

```
root
 └─ a
    └─ p
       ├─ p ● (end of "app")
       └─ t ● (end of "apt")
```

Takeaway: pruning stops the moment we hit a node that still represents another word’s end (the `"app"` node).

---

## Case B — Delete a word that is a **prefix** of a longer word (e.g., delete `"app"` while `"apple"` exists)

### Setup

Insert: `"app"`, `"apple"`:

```
root
 └─ a
    └─ p
       └─ p ● (end of "app")
          └─ l
             └─ e ● (end of "apple")
```

### Delete `"app"`

Recurse down to the terminal node for `"app"` (the second `p`) and unmark:

* At `"app"` node: `isWord` becomes `false`.
* But this node **has a child (`l`)** → return `false` (not prunable).

Unwinding: every ancestor sees `false` → **no structural pruning** occurs.

### Result

`"app"` is no longer a word, but `"apple"` stays:

```
root
 └─ a
    └─ p
       └─ p (isWord=false)
          └─ l
             └─ e ●
```

Takeaway: if the deleted word is a **prefix** of another, we only unmark the end; we **don’t** remove nodes.

---

## Case C — Delete the **only** word that passes through a chain (e.g., delete `"cat"` with no other overlapping words)

### Setup

Insert: `"cat"`:

```
root
 └─ c
    └─ a
       └─ t ●
```

### Delete `"cat"`

* At `t`: unmark `isWord=false`, `t` has **no children** → return `true`.
* At `a`: child (`t`) prunable → remove; now `a.isWord=false` and no children → return `true`.
* At `c`: remove child (`a`); `c.isWord=false` and no children → return `true`.
* At `root`: parent may ignore the final `true` (root is typically never pruned, but its child pointer to `c` is already cleared).

### Result

All three nodes (`t`, `a`, `c`) were pruned; the trie is back to an empty root.

---

## Case D — Delete a word that **doesn’t exist**

### D1: Path breaks mid-way

Delete `"car"` when only `"cat"` is present:

* At `c` (ok), at `a` (ok), at `r` (child is `null`) → return `false`.
* No changes are made; the entire delete returns `false`.

### D2: Path exists but terminal isn’t a word

Delete `"app"` when only `"apple"` is present:

* Reaches the `"app"` node; `isWord=false` already → return `false`.
* No changes, trie unchanged.

Takeaway: no harm done if you attempt to delete something that isn’t there.

---

## Case E — Delete a word that shares only part of the path with others (e.g., `"apt"` while `"apple"` exists)

### Setup

Insert: `"apple"`, `"apt"`:

```
root
 └─ a
    └─ p
       ├─ p
       │  └─ l
       │     └─ e ●
       └─ t ●
```

### Delete `"apt"`

* At `t`: unmark `isWord=false`, `t` no children → prunable.
* At `p` (the first `p` node): remove `t` child; still has child `p` (for `"apple"`) → not prunable.
* Done.

### Result

`"apt"` gone; `"apple"` intact:

```
root
 └─ a
    └─ p
       └─ p
          └─ l
             └─ e ●
```

---

## What to watch for (typical bugs)

1. **Pruning too aggressively**: removing nodes that are still needed (node has other children or `isWord=true`).
   → Always decide prunability **after** recursion: `return !node.isWord && !hasAnyChild(node)`.

2. **Not pruning at all**: leaving dead chains, bloating memory.
   → Use the boolean return to signal “parent can null out this child”.

3. **Forgetting to unmark `isWord`** at the terminal node.
   → The word would still “exist” even if you prune below.

4. **Stopping at first mismatch without returning `false`** correctly.
   → Ensure you propagate `false` all the way up when the word isn’t present.

---

## Quick mental template for deletion

* **Go down** to the target word’s end.
* If it **wasn’t** a word → `false`.
* Else **unmark** the end; if **no children** → tell parent to prune (return `true`).
* On unwind:

  * If child is prunable → **null it out**.
  * This node is prunable iff **not a word end** and has **no other children**.
* Root is never “removed”, but its child pointers get cleared.

This mental model will let you reason through any deletion scenario without surprises.
*/

class Trie {
    private static class TrieNode {
        TrieNode[] child = new TrieNode[26];
        boolean isWord = false;
    }

    private final TrieNode root = new TrieNode();

    /** Inserts a word into the trie. */
    public void insert(String word) {
        if (word == null) return;
        TrieNode cur = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            int idx = ch - 'a';                // assumes 'a'..'z'
            if (idx < 0 || idx >= 26) {
                throw new IllegalArgumentException("Only lowercase a-z supported");
            }
            if (cur.child[idx] == null) {
                cur.child[idx] = new TrieNode();
            }
            cur = cur.child[idx];
        }
        cur.isWord = true;
    }

    /** Returns true if the exact word exists in the trie. */
    public boolean search(String word) {
        TrieNode node = traverse(word);
        return node != null && node.isWord;
    }

    /** Returns true if there is any word in the trie that starts with the given prefix. */
    public boolean startsWith(String prefix) {
        return traverse(prefix) != null;
    }

    /**
     * Deletes a word from the trie.
     * Returns true if the word existed and was removed; false if it didn't exist.
     */
    public boolean delete(String word) {
        if (word == null) return false;
        return delete(root, word, 0);
    }

    // ---- helpers ----

    // Walks the trie following s; returns the node at the end, or null if path breaks.
    private TrieNode traverse(String s) {
        if (s == null) return null;
        TrieNode cur = root;
        for (int i = 0; i < s.length(); i++) {
            int idx = s.charAt(i) - 'a';
            if (idx < 0 || idx >= 26) return null;
            cur = cur.child[idx];
            if (cur == null) return null;
        }
        return cur;
    }

    /**
     * Recursive delete:
     * - returns true if the current node should be pruned (no children & not a word),
     *   so the parent can null out its pointer.
     * - returns false otherwise.
     */
    private boolean delete(TrieNode node, String word, int i) {
        if (node == null) return false;  // path broken -> word not present

        if (i == word.length()) {
            // At terminal node for 'word'
            if (!node.isWord) return false; // word not present
            node.isWord = false;            // unmark the word end
            return !hasChildren(node);      // prune if no children
        }

        int idx = word.charAt(i) - 'a';
        if (idx < 0 || idx >= 26 || node.child[idx] == null) {
            return false;                   // word not present
        }

        boolean shouldDeleteChild = delete(node.child[idx], word, i + 1);

        if (shouldDeleteChild) {
            node.child[idx] = null;         // detach prunable child
        }

        // Decide if *this* node should be pruned
        return !node.isWord && !hasAnyChild(node);
    }

    private boolean hasChildren(TrieNode node) {
        return hasAnyChild(node);
    }

    private boolean hasAnyChild(TrieNode node) {
        for (TrieNode ch : node.child) if (ch != null) return true;
        return false;
    }
}


/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */




//  Method 2: Map based Trie (for any type of characters, not just lowercase)
/*
Notes:
Flexible alphabet: since children are in a Map, you’re not limited to 'a'..'z'. Any char works (e.g., uppercase, digits, punctuation).
Deletion logic: On the way back up the recursion, a node is removed from its parent iff its subtree no longer represents any word (isWord == false) and it has no children—so shared prefixes stay intact.
Edge cases: If you want to allow empty strings, calling insert("") will set root.isWord = true; search("") will then return true. If you don’t want to allow them, guard in insert/delete.
*/

// public class Trie {
//     private static class TrieNode {
//         Map<Character, TrieNode> children = new HashMap<>();
//         boolean isWord = false;
//     }

//     private final TrieNode root = new TrieNode();

//     /** Inserts a word into the trie. */
//     public void insert(String word) {
//         if (word == null) return;
//         TrieNode cur = root;
//         for (int i = 0; i < word.length(); i++) {
//             char ch = word.charAt(i);
//             cur = cur.children.computeIfAbsent(ch, k -> new TrieNode());
//         }
//         cur.isWord = true; // mark terminal
//     }

//     /** Returns true iff the exact word exists in the trie. */
//     public boolean search(String word) {
//         TrieNode node = traverse(word);
//         return node != null && node.isWord;
//     }

//     /** Returns true iff any word in the trie starts with the given prefix. */
//     public boolean startsWith(String prefix) {
//         return traverse(prefix) != null;
//     }

//     /**
//      * Deletes a word from the trie.
//      * @return true if the word existed and was removed; false if it didn't exist.
//      */
//     public boolean delete(String word) {
//         if (word == null) return false;
//         return delete(root, word, 0);
//     }

//     // ---------- Helpers ----------

//     // Follow the path of s; return node at the end or null if path breaks.
//     private TrieNode traverse(String s) {
//         if (s == null) return null;
//         TrieNode cur = root;
//         for (int i = 0; i < s.length(); i++) {
//             char ch = s.charAt(i);
//             cur = cur.children.get(ch);
//             if (cur == null) return null;
//         }
//         return cur;
//     }

//     /**
//      * Recursive delete with pruning.
//      * Returns true to the parent if the current node became prunable:
//      *   (no children AND not an end-of-word).
//      */
//     private boolean delete(TrieNode node, String word, int i) {
//         if (node == null) return false; // path broken → word not present

//         if (i == word.length()) {
//             if (!node.isWord) return false; // word not present
//             node.isWord = false;            // unmark terminal
//             return node.children.isEmpty(); // prune if now childless
//         }

//         char ch = word.charAt(i);
//         TrieNode child = node.children.get(ch);
//         if (child == null) return false;    // word not present

//         boolean childPrunable = delete(child, word, i + 1);

//         if (childPrunable) {
//             node.children.remove(ch);       // physically prune child
//         }

//         // Current node is prunable only if it's not a word end and has no children now.
//         return !node.isWord && node.children.isEmpty();
//     }
// }

