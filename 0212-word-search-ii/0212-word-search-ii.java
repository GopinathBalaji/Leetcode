// Backtracking with highly helpful Trie pruning
/*
Mark a cell as visited by temporarily setting it to '#', then restore it on backtrack.
Move to the child node first (using the current board char), then check if that child node completes a word.
De-dup by nulling out the stored word (or use a Set).
Only kick off DFS from cells that exist under root.children.

Why this is correct:
Visited control: '#' prevents revisiting the same cell in the current path.
Correct order: We advance to next via c first, then check next.word.
De-dup: next.word = null ensures each word is added once.
Pruning: Early start-cell check and leaf pruning keep the search small.


the line you’re asking about is the small “leaf-pruning” step:

```java
// (Optional micro-opt) prune dead leaves from trie
if (next.children.isEmpty()) {
    node.children.remove(c);
}
```

# Why this is an optimization (with a tiny example)

**Goal:** shrink the trie as soon as a branch can no longer lead to any new words. Smaller trie ⇒ fewer hash lookups and earlier prefix prunes on later DFS starts.

**Example**

Board:

```
o a t h
```

Words: `["oat", "oath"]`

Trie (relevant branch): `o → a → t (word=oat), h (word=oath)`

Start DFS at cell `o(0,0)`:

1. We walk `o→a→t` and find `"oat"`. After exploring all neighbors from `t`, suppose there are **no more paths** that extend beyond `t` (true here, because `"oat"` is a leaf word; there’s no `"oat..."` in the list).
2. When we backtrack from `t` to `a`, `t.children` is empty **and** we already consumed `"oat"` once (we nulled its `word`). So we remove the edge `a.children.remove('t')`.
   Now under `a`, only `'h'` remains (for `"oath"`).
3. Later DFS starts (from other cells) or later steps that come down `o→a` will no longer even consider `'t'`. Fewer child checks → faster pruning.

On big inputs (hundreds/thousands of words with overlapping prefixes), this pruning keeps cutting off dead twigs as you discover words, so many future DFS branches die **one hash lookup earlier**. It’s small but measurable, especially when you start DFS from many cells.

> Safety intuition: we remove a child **after** fully exploring it (post-DFS) and after possibly consuming its `word` (set to `null`). No other unexplored path can need that child anymore.

## Walkthrough, step by step

* Build a trie from `words`, storing the **full word** at terminal nodes (`node.word`).
* For each board cell whose letter exists under `root.children`, start DFS.
* In DFS:

  1. Move to the child node for the current letter.
  2. If `next.word != null`, add it and set it to `null` (de-dup).
  3. Mark current board cell as visited by setting it to `'#'`.
  4. Recurse to neighbors that are in bounds and not `'#'`.
  5. Restore the board cell.
  6. (Optional) If `next.children` is empty, remove it from its parent.

I’ll walk this on the classic example.

## Step A: Build the trie

Insert “oath”, “pea”, “eat”, “oat”.

The top of the trie looks like:

* `root`

  * `o` → `a` → `t` (terminal “oat”) → `h` (terminal “oath”)
  * `p` → `e` → `a` (terminal “pea”)
  * `e` → `a` → `t` (terminal “eat”)

Note: A terminal node has `node.word = "thatWord"`.

## Step B: Start DFS from each cell with an existing root child

Board letters:
Row 0: `o a a n`
Row 1: `e t a e`
Row 2: `i h k r`
Row 3: `i f l v`

Root has children for `o`, `p`, `e` (not `n`, `t`, …). So we start from positions whose letters are `o`, `p`, or `e`. That includes:

* (0,0)=`o`, (0,1)=`a` doesn’t start (root has no `a`), (0,2)=`a` no, (0,3)=`n` no
* Row 1: (1,0)=`e` yes, (1,1)=`t` no, (1,2)=`a` no, (1,3)=`e` yes
* Row 2: (2,0)=`i` no, (2,1)=`h` no, (2,2)=`k` no, (2,3)=`r` no
* Row 3: (3,0)=`i` no, (3,1)=`f` no, (3,2)=`l` no, (3,3)=`v` no

We’ll go through the two that actually discover words: (0,0)=`o` → finds “oat” and “oath”, and one of the `e` starts → finds “eat”.

---

## Walkthrough from (0,0) = 'o' (discover “oat” and “oath”)

**Call** `dfs(board, 0, 0, root, ans)`.

1. `c = 'o'`. `next = root.children.get('o')` (exists).

   * `next.word` is `null` (we’re at prefix `'o'`, not a complete word yet).
2. Mark visited: set `board[0][0]` from `'o'` → `'#'`.
3. Recurse to neighbors in order (up, left, down, right—but your code tries top/left/down/right; order doesn’t matter for correctness). Assume we try **down** `(1,0)='e'`, **right** `(0,1)='a'`, etc.

   * `'#'` prevents immediately stepping back into `(0,0)`.

Let’s go to `(0,1)='a'` (the “right” neighbor) since it leads somewhere:

### Into (0,1) = 'a'

* We are still inside the same DFS call stack, at the child `next` for 'o'.
* At `(0,1)`, `c='a'`. Query `next.children.get('a')`:

  * That exists (the `'oa'` node).
* `next.word` here is still `null`.
* Mark `(0,1)` visited: `'a' → '#'`.
* Explore neighbors of `(0,1)` that are not `'#'`. The promising path is to `(1,1)='t'`.

### Into (1,1) = 't' (this should complete “oat”)

* Current trie node is for prefix `'oa'`. `c='t'`. `next = node.children.get('t')` exists, and **this node stores `word="oat"`**.
* Because `next.word != null`, add `"oat"` to `ans`, then set `next.word = null` to avoid duplicates later.
* Mark `(1,1)` visited: `'t' → '#'`.
* Explore neighbors to see if there’s a longer word continuing beyond `'oat'`. In the trie, from `'oat'` there **is** a child `'h'` (i.e., `'oath'`), so we keep going.

From `(1,1)`, the neighbor `(2,1)='h'` is the key.

### Into (2,1) = 'h' (this should complete “oath”)

* Current trie node is the `'oat'` node (which we just nulled its word), `c='h'`.
* `next = node.children.get('h')` exists and has `word="oath"`.
* Add `"oath"` to `ans`, then set `next.word = null`.
* Mark `(2,1)` visited: `'h' → '#'`.
* Try neighbors of `(2,1)`. There are no longer children after `'h'` in this branch, so all next `node.children.get(...)` checks will fail → they prune immediately.
* Backtrack:

  * Restore `(2,1)` to `'h'`.
  * **Optional leaf prune:** If the `'oath'` node has no children now, it’s a leaf; the code removes it from its parent (`'oat'` node). That shrinks the trie.

Return to `(1,1)`:

* Restore `(1,1)` to `'t'`.
* If the `'oat'` node is now childless (it likely is after removing `'h'`) and `word` was nulled, it may also be pruned from `'oa'`.

Return to `(0,1)`:

* Restore `(0,1)` to `'a'`.
* If `'oa'` node lost all children, prune it from `'o'`.

Return to `(0,0)`:

* Restore `(0,0)` to `'o'`.
* If `'o'` node became childless, prune from root. (In this example, it probably still has something else—or not—depending on other words sharing `'o'`.)

At this point, `ans` contains `["oat", "oath"]`.

Key things you saw:

* **Shared work:** The same traversal found **two** words (`oat`, then `oath`) without restarting from the beginning.
* **De-dup:** If another path later hits those terminals, it won’t re-add them because we nulled `word`.
* **Leaf pruning:** Future starts that go through `o` might immediately prune, making later searches faster.

---

## Walkthrough from an `e` start (discover “eat”)

Now start from `(1,0)='e'` or `(1,3)='e'`. Let’s use `(1,0)='e'`.

**Call** `dfs(board, 1, 0, root, ans)`.

1. `c='e'`. `next = root.children.get('e')` (exists).

   * `next.word == null` (we’re at `'e'`).
2. Mark `(1,0)` visited → `'#'`.
3. Explore neighbors; the relevant path is `'e' → 'a' → 't'`.

Suppose we go to `(1,1)='t'` first:

* At `'e'` node, asking for `'t'` child fails (`get('t') == null`) → **prefix prune** immediately. No wasted DFS.

We try `(0,0)='o'`:

* `get('o') == null` → prune.

We try `(2,0)='i'`:

* `get('i') == null` → prune.

We try `(1,1)='t'` we already checked. So eventually we try `(0,0)`, `(2,0)`, `(1,1)`, `(1,-1)` out of bounds—most prune.

But from `(1,0)='e'`, a better neighbor is `(1,2)='a'`—that’s not adjacent, so we won’t get it from here. Instead, the other start `(1,3)='e'` reaches the adjacent `'a'` at `(0,2)` or `(1,2)` depending on path. Let’s switch to `(1,3)='e'`.

**Call** `dfs(board, 1, 3, root, ans)`.

1. `c='e'` → trie child ok.
2. Mark visited.
3. Try neighbors. The `'a'` we need for “eat” is `(1,2)='a'` (left):

### Into (1,2) = 'a'

* From `'e'` node, `get('a')` exists → move to `'ea'` node.
* Mark `(1,2)` visited.
* Neighbors of `(1,2)` include `(1,1)='t'`:

### Into (1,1) = 't' (completes “eat”)

* From `'ea'`, `get('t')` exists, and **this node has `word="eat"`**.
* Add `"eat"` to `ans`, set `next.word = null`.
* Mark `(1,1)` visited, explore neighbors (will prune), backtrack, restore cells, and possibly prune leaves.

Now `ans` is `["oat", "oath", "eat"]`. “pea” won’t be found on this board.

---

## How every key line in the code plays out

* **`if (root.children.containsKey(board[i][j]))` start check:**
  Prevents pointless DFS calls from cells whose letter isn’t even a first letter of any word.

* **`TrieNode.word` instead of `isWord + StringBuilder`:**

  * When you reach a terminal trie node, you already have the entire word (free!), so no string building or tracking the path string is needed.
  * Setting `node.word = null` after adding the result is a **constant-time de-dup** trick.

* **Prefix pruning (`next == null` → return):**
  Instant cut-off for dead branches.

* **Visited marking with `'#'`:**
  Guarantees you never reuse a cell in the current path; simpler and faster than a separate `visited[][]` array.

* **Backtracking (restore the char):**
  Ensures the board remains intact for other starting positions.

* **Optional leaf pruning:**

  ```java
  if (next.children.isEmpty()) {
      node.children.remove(c);
  }
  ```

  Once a suffix is exhausted (no children, `word == null`), remove it so the trie itself becomes smaller during the run. Later DFS calls hit prunes sooner.

---

## Complexity intuition (why it’s much faster)

* Without a trie: roughly `O(#starts × branching^depth × #words_or_checks)`.
* With the trie:

  * Each step only expands to letters that exist from the current trie node.
  * Dead prefixes die immediately.
  * Shared prefixes are explored once for all words.
  * Duplicate reporting is O(1) prevented.
  * Leaf pruning reduces future search sizes.

In practice, that’s a **big** constant-factor win and often the difference between TLE and AC.
*/
class Solution {
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        String word; // non-null only at terminal nodes; stores the full word
    }

    private final TrieNode root = new TrieNode();

    private void addWord(String w) {
        TrieNode cur = root;
        for (int i = 0; i < w.length(); i++) {
            char c = w.charAt(i);
            cur = cur.children.computeIfAbsent(c, k -> new TrieNode());
        }
        cur.word = w; // mark terminal by storing the whole word
    }

    public List<String> findWords(char[][] board, String[] words) {
        for (String w : words) addWord(w);

        List<String> ans = new ArrayList<>();
        int m = board.length, n = board[0].length;

        // Start DFS only from cells that can be a first letter (cheap prune)
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (root.children.containsKey(board[i][j])) {
                    dfs(board, i, j, root, ans);
                }
            }
        }
        return ans;
    }

    private void dfs(char[][] b, int i, int j, TrieNode node, List<String> ans) {
        char c = b[i][j];
        TrieNode next = node.children.get(c);
        if (next == null) return; // prefix prune

        // If this node completes a word, add it once, then null it to de-dup
        if (next.word != null) {
            ans.add(next.word);
            next.word = null; // prevents duplicates via other paths
        }

        // Mark visited (so we can't reuse this cell on this path)
        b[i][j] = '#';

        // Explore neighbors (up, left, down, right)
        if (i > 0           && b[i-1][j] != '#') dfs(b, i-1, j, next, ans);
        if (j > 0           && b[i][j-1] != '#') dfs(b, i,   j-1, next, ans);
        if (i+1 < b.length  && b[i+1][j] != '#') dfs(b, i+1, j,   next, ans);
        if (j+1 < b[0].length && b[i][j+1] != '#') dfs(b, i, j+1, next, ans);

        // Unmark visited on backtrack
        b[i][j] = c;

        // --- micro-optimization: prune dead leaf we just finished exploring ---
        // If the child we descended into has no further children and no word,
        // remove it from its parent so future searches don't even try it.
        if (next.children.isEmpty() && next.word == null) {
            node.children.remove(c);
        }
    }
}
