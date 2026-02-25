// Method 1: Using HashMap based Trie (generic Trie)
/*
Notes:
Flexible alphabet: since children are in a Map, you’re not limited to 'a'..'z'. Any char works (e.g., uppercase, digits, punctuation).
Deletion logic: On the way back up the recursion, a node is removed from its parent iff its subtree no longer represents any word (isWord == false) and it has no children—so shared prefixes stay intact.
Edge cases: If you want to allow empty strings, calling insert("") will set root.isWord = true; search("") will then return true. If you don’t want to allow them, guard in insert/delete.

Here are **progressive hints** to build a Trie for **LeetCode 208** using the **HashMap children** approach (instead of fixed array `[26]`).

---

## Hint 1: What a Trie node should store

Each node represents a prefix. A node needs **two things**:

1. `children` → mapping from character to next node
   (`HashMap<Character, TrieNode>`)
2. `isEnd` → whether a word ends at this node

Think:

* path = prefix
* `isEnd = true` means a full inserted word ends here

---

## Hint 2: Create a separate `TrieNode` class

Don’t try to store everything in the Trie class directly.

You usually want:

* `TrieNode root`
* `TrieNode` has:

  * `Map<Character, TrieNode> children`
  * `boolean isEnd`

Root is an **empty starting node** (does not store a char necessarily).

---

## Hint 3: `insert(word)` = walk/create nodes

For each character `c` in the word:

* if current node does **not** have child `c`, create it
* move to that child

After the loop ends:

* mark current node `isEnd = true`

Key HashMap methods that help:

* `containsKey`
* `get`
* `put`
* or cleaner: `computeIfAbsent`

---

## Hint 4: `search(word)` = walk only, no creation

For each character `c`:

* if child `c` does not exist → return `false`
* otherwise move to it

After all chars are processed:

* return `current.isEnd` (not just true because prefix may exist without full word)

Example:

* inserted `"apple"`
* `search("app")` should be `false` unless `"app"` was inserted too

---

## Hint 5: `startsWith(prefix)` is almost same as `search`

Same traversal as `search`, but after consuming all prefix chars:

* return `true` (you do **not** care about `isEnd`)

So:

* `search` checks path + `isEnd`
* `startsWith` checks path only

---

## Hint 6: HashMap approach vs array[26]

Why HashMap works:

* flexible (works for any character set)
* memory-efficient if sparse children

Tradeoff:

* array is faster for lowercase English letters only
* HashMap is cleaner/general

---

## Hint 7: Common bug to avoid

Do **not** mark `isEnd = true` on every node while inserting.
Only mark it at the **last character** node.

Otherwise `search("app")` would incorrectly return true after inserting `"apple"`.

---

## Hint 8: Mental walkthrough (quick)

Insert `"cat"`:

* root -> 'c' -> 'a' -> 't'
* mark node('t').isEnd = true

Insert `"car"`:

* root -> 'c' (already exists)
* -> 'a' (already exists)
* create 'r'
* mark node('r').isEnd = true

Now:

* `search("ca")` → false (`isEnd` at 'a' is false)
* `startsWith("ca")` → true
* `search("cat")` → true

---

## Hint 9: Nice Java method to simplify insert

Inside insert, instead of:

* if not exists create
* then get

use:

* `curr = curr.children.computeIfAbsent(c, k -> new TrieNode());`

This makes insertion very short.

---

## Hint 10: Skeleton shape (without full solution)

You can aim for this structure:

* `class TrieNode { Map<Character, TrieNode> children; boolean isEnd; }`
* `class Trie { TrieNode root; ... }`
* methods:

  * `insert(String word)`
  * `search(String word)`
  * `startsWith(String prefix)`

#################### Approach for delete method #####################

## Core idea

Deleting a word has **2 phases**:

1. **Unmark the end of word** (`isEnd = false`) at the last node.
2. **Prune nodes backward** only if they are no longer needed:

   * node has **no children**
   * and `isEnd == false` (not ending another word)

That’s why recursion is a very natural fit.

---

## Cases your delete must handle

Suppose trie contains: `"cat"`, `"car"`, `"care"`

### Case 1: Delete a word that doesn’t exist (`"cab"`)

* Do nothing
* return `false`

### Case 2: Delete a word that is a prefix of another (`"car"` when `"care"` exists)

* Unmark `isEnd` at `"r"`
* **Do not delete nodes**, because `"care"` still needs them

### Case 3: Delete a word that has another word as prefix (`"care"` when `"car"` exists)

* Remove only the unique suffix (`'e'`)
* Keep `"car"` intact

### Case 4: Delete a standalone branch (`"cat"` when nothing else uses `'t'`)

* Remove nodes back up until a shared node is reached

---

# Recommended recursive design (HashMap-based Trie)

Use a helper that returns:

> **Should this node be pruned from its parent?** (`true/false`)

### Why this works

When you return from deeper recursion, the parent can decide whether to remove that child from `children`.


# Thorough walkthrough (step by step)

Let’s insert:

* `"cat"`
* `"car"`
* `"care"`

Trie structure (conceptually):

* root

  * `'c'`

    * `'a'`

      * `'t'` (`isEnd=true`)   // "cat"
      * `'r'` (`isEnd=true`)   // "car"

        * `'e'` (`isEnd=true`) // "care"

---

## Example 1: delete("care")

### Call flow

`deleteHelper(root, "care", 0)`

* idx=0, ch='c' → recurse into node `'c'`
* idx=1, ch='a' → recurse into node `'a'`
* idx=2, ch='r' → recurse into node `'r'`
* idx=3, ch='e' → recurse into node `'e'`
* idx=4 == word.length()

### At node `'e'` (end of word)

* set `isEnd = false`
* `'e'` has no children → return `true` (prune me)

### Back to node `'r'`

* child `'e'` should be deleted → remove `'e'`
* now `'r'` still has:

  * `isEnd = true` (because `"car"` still exists)
* so `'r'` **cannot** be pruned → return `false`

### Back to `'a'`, `'c'`, root

* no further deletions

✅ Result:

* `"care"` deleted
* `"car"` and `"cat"` remain

---

## Example 2: delete("car") after deleting "care"

Current branch:

* `'r'` is end of `"car"`, and has no children now

### At node `'r'`

* set `isEnd = false`
* no children → return `true`

### Back to `'a'`

* remove child `'r'`
* `'a'` still has child `'t'` (for `"cat"`) → cannot prune

✅ Result:

* `"car"` deleted
* `"cat"` remains

---

## Example 3: delete("cat")

Now `"cat"` is the only remaining word in that branch.

### At `'t'`

* unmark end, no children → return `true`

### Back to `'a'`

* remove `'t'`
* `'a'` has no children and `isEnd=false` → return `true`

### Back to `'c'`

* remove `'a'`
* `'c'` has no children and `isEnd=false` → return `true`

### Back to root

* remove `'c'`
* root remains (we never delete root object)

✅ Trie becomes empty

---

# Why recursion is easier than iterative delete

With iterative traversal, you need to store the path (stack of parent nodes + chars) and then walk backward to prune. Recursion naturally gives you that backward path.

---

# Optional improvement (single-pass delete without calling `search`)

Right now `delete()` does:

* `search(word)` (O(L))
* `deleteHelper(...)` (O(L))

So total is still O(L), just ~2 passes.

If you want a cleaner one-pass version, the helper can return more info (like “word existed?” + “prune?”), but that adds complexity. For interviews, the `search` + recursive delete approach is perfectly fine and easy to explain.

---

If you want, I can also show:

* a **one-pass delete** version (without calling `search` first), or
* the **array[26] Trie delete** version so you can compare both styles.
*/
class Trie {

    private class TrieNode{
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
    }

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }
    
    public void insert(String word) {
        TrieNode node = root;

        for(char c: word.toCharArray()){
            if(!node.children.containsKey(c)){
                node.children.put(c, new TrieNode());
            }

            node = node.children.get(c);
        }

        node.isEnd = true;
    }
    
    public boolean search(String word) {
        TrieNode node = root;

        for(char c: word.toCharArray()){
            if(!node.children.containsKey(c)){
                return false;
            }
            node = node.children.get(c);
        }

        return node.isEnd;
    }
    
    public boolean startsWith(String prefix) {
        TrieNode node = root;

        for(char c: prefix.toCharArray()){
            if(!node.children.containsKey(c)){
                return false;
            }

            node = node.children.get(c);
        }

        return true;
    }

    // Returns true if deletion actually happened, false if word not present
    public boolean delete(String word) {
        if (!search(word)) return false;   // simplest way to handle "not found"
        deleteHelper(root, word, 0);
        return true;
    }

    // Returns true if this node should be pruned from its parent
    private boolean deleteHelper(TrieNode node, String word, int idx) {
        // Reached end of the word
        if (idx == word.length()) {
            node.isEnd = false; // unmark word end

            // If no children, this node is useless now and can be pruned
            return node.children.isEmpty();
        }

        char ch = word.charAt(idx);
        TrieNode child = node.children.get(ch);

        // Because we already checked search(word), child should exist.
        // Still safe to guard:
        // if (child == null) return false;

        boolean shouldDeleteChild = deleteHelper(child, word, idx + 1);

        if (shouldDeleteChild) {
            node.children.remove(ch);
        }

        // Current node can be pruned only if:
        // 1) it is not the end of another word, and
        // 2) it has no remaining children
        // (root will never be removed because caller ignores this return)
        return !node.isEnd && node.children.isEmpty();
    }
}






// Method 2: Using Array based Trie (specifically only for lower case inputs)
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
// class Trie {

//     private class TrieNode {
//         TrieNode[] children = new TrieNode[26]; // for 'a' to 'z'
//         boolean isEnd = false;
//     }

//     private final TrieNode root;

//     public Trie() {
//         root = new TrieNode();
//     }

//     public void insert(String word) {
//         TrieNode node = root;

//         for (char c : word.toCharArray()) {
//             int idx = c - 'a';
//             if (node.children[idx] == null) {
//                 node.children[idx] = new TrieNode();
//             }
//             node = node.children[idx];
//         }

//         node.isEnd = true;
//     }

//     public boolean search(String word) {
//         TrieNode node = root;

//         for (char c : word.toCharArray()) {
//             int idx = c - 'a';
//             if (node.children[idx] == null) {
//                 return false;
//             }
//             node = node.children[idx];
//         }

//         return node.isEnd;
//     }

//     public boolean startsWith(String prefix) {
//         TrieNode node = root;

//         for (char c : prefix.toCharArray()) {
//             int idx = c - 'a';
//             if (node.children[idx] == null) {
//                 return false;
//             }
//             node = node.children[idx];
//         }

//         return true;
//     }

//     // Returns true if deletion actually happened, false if word not present
//     public boolean delete(String word) {
//         if (!search(word)) return false;   // simplest way to handle "not found"
//         deleteHelper(root, word, 0);
//         return true;
//     }

//     // Returns true if this node should be pruned from its parent
//     private boolean deleteHelper(TrieNode node, String word, int pos) {
//         // Reached end of the word
//         if (pos == word.length()) {
//             node.isEnd = false; // unmark word end

//             // If no children, this node is useless now and can be pruned
//             return hasNoChildren(node);
//         }

//         int idx = word.charAt(pos) - 'a';
//         TrieNode child = node.children[idx];

//         // Since search(word) was checked, child should exist.
//         // Still safe to guard if you want:
//         // if (child == null) return false;

//         boolean shouldDeleteChild = deleteHelper(child, word, pos + 1);

//         if (shouldDeleteChild) {
//             node.children[idx] = null;
//         }

//         // Current node can be pruned only if:
//         // 1) it is not the end of another word, and
//         // 2) it has no remaining children
//         return !node.isEnd && hasNoChildren(node);
//     }

//     private boolean hasNoChildren(TrieNode node) {
//         for (TrieNode child : node.children) {
//             if (child != null) return false;
//         }
//         return true;
//     }
// }





/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */