// Method 1: Backtracking with highly helpful (modified) Trie pruning
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
// class Solution {
//     static class TrieNode {
//         Map<Character, TrieNode> children = new HashMap<>();
//         String word; // non-null only at terminal nodes; stores the full word
//     }

//     private final TrieNode root = new TrieNode();

//     private void addWord(String w) {
//         TrieNode cur = root;
//         for (int i = 0; i < w.length(); i++) {
//             char c = w.charAt(i);
//             cur = cur.children.computeIfAbsent(c, k -> new TrieNode());
//         }
//         cur.word = w; // mark terminal by storing the whole word
//     }

//     public List<String> findWords(char[][] board, String[] words) {
//         for (String w : words) addWord(w);

//         List<String> ans = new ArrayList<>();
//         int m = board.length, n = board[0].length;

//         // Start DFS only from cells that can be a first letter (cheap prune)
//         for (int i = 0; i < m; i++) {
//             for (int j = 0; j < n; j++) {
//                 if (root.children.containsKey(board[i][j])) {
//                     dfs(board, i, j, root, ans);
//                 }
//             }
//         }
//         return ans;
//     }

//     private void dfs(char[][] b, int i, int j, TrieNode node, List<String> ans) {
//         char c = b[i][j];
//         TrieNode next = node.children.get(c);
//         if (next == null) return; // prefix prune

//         // If this node completes a word, add it once, then null it to de-dup
//         if (next.word != null) {
//             ans.add(next.word);
//             next.word = null; // prevents duplicates via other paths
//         }

//         // Mark visited (so we can't reuse this cell on this path)
//         b[i][j] = '#';

//         // Explore neighbors (up, left, down, right)
//         if (i > 0           && b[i-1][j] != '#') dfs(b, i-1, j, next, ans);
//         if (j > 0           && b[i][j-1] != '#') dfs(b, i,   j-1, next, ans);
//         if (i+1 < b.length  && b[i+1][j] != '#') dfs(b, i+1, j,   next, ans);
//         if (j+1 < b[0].length && b[i][j+1] != '#') dfs(b, i, j+1, next, ans);

//         // Unmark visited on backtrack
//         b[i][j] = c;

//         // --- micro-optimization: prune dead leaf we just finished exploring ---
//         // If the child we descended into has no further children and no word,
//         // remove it from its parent so future searches don't even try it.
//         if (next.children.isEmpty() && next.word == null) {
//             node.children.remove(c);
//         }
//     }
// }











// Method 1.5: Similar approach as the above solution but using array based modified Trie
/*
################# WHAT WAS I DOING WRONG ####################
You’re very close on the overall idea (Trie + DFS + prefix pruning). The main missing piece is **visited marking**, and the big optimization is to **pass the current Trie node during DFS** instead of repeatedly calling `startsWith(sb.toString())` and `search(sb.toString())`.


# Why your current approach fails / becomes problematic

Your updated version fixed some earlier issues (good job), but it still has two major problems:

### 1) No visited marking

You are still not marking the current cell as visited, so the DFS can revisit the same cell in the same path (illegal for Word Search).

Example bad path:

* from `(0,0)` go right to `(0,1)`
* then go left back to `(0,0)`
* then right again...
  This can create invalid words and even infinite recursion loops.

### 2) Too much repeated Trie/string work

You repeatedly do:

* `sb.toString()`
* `trie.startsWith(...)`
* `trie.search(...)`

That means at every DFS step you:

* build a new string
* re-traverse the Trie from the root

This is much slower than passing the **current TrieNode** down the recursion.

#############################################


# Key idea of the optimized version (important)

Instead of this pattern:

* build current string
* call `startsWith(currentString)`
* call `search(currentString)`

we do this:

* keep a pointer to the current Trie node (`parent`)
* read current board char `ch`
* move directly to `parent.children[ch - 'a']`

So prefix pruning becomes **O(1)** transition per step (not O(length-of-string) every time).

---

# Detailed explanation (line-by-line logic)

## 1) Trie stores the word at terminal nodes

```java
String word;
```

At the end of inserting `"oath"`, we store:

```java
node.word = "oath";
```

Why this is nice:

* During DFS, when we reach a terminal trie node, we can directly do:

  ```java
  ans.add(node.word);
  ```
* We don’t need a `StringBuilder` to reconstruct the word.

---

## 2) DFS signature

```java
dfs(int r, int c, TrieNode parent)
```

Meaning:

> “Try exploring from board cell `(r,c)` assuming we are currently at trie node `parent` (which represents the prefix formed so far).”

At each step:

* look at `board[r][c]`
* see if that character continues any trie path from `parent`
* if not, prune immediately

---

## 3) Prefix pruning (the whole reason Trie helps)

```java
TrieNode node = parent.children[idx];
if (node == null) return;
```

If the current board path is not a prefix of any word, stop exploring this branch.

This is what makes the solution fast.

---

## 4) Found a word

```java
if (node.word != null) {
    ans.add(node.word);
    node.word = null; // avoid duplicates
}
```

Why `node.word = null`?

* The same word may be found again by another path / starting point.
* LeetCode 212 wants unique words in result.
* Nulling the terminal word ensures it is added only once.

(Alternative would be a `Set<String>`, but this Trie trick is cleaner and faster.)

---

## 5) Choose / Explore / Unchoose (backtracking pattern)

### Choose

```java
board[r][c] = '#';
```

Mark current cell as visited.

### Explore

Try all 4 neighbors:

```java
dfs(r - 1, c, node);
dfs(r + 1, c, node);
dfs(r, c - 1, node);
dfs(r, c + 1, node);
```

### Unchoose

```java
board[r][c] = ch;
```

Restore the original character so other DFS paths can use the cell.

This is the correct backtracking pattern.

---

## 6) Optional Trie pruning

After exploring, if this trie node is now useless (no children, no terminal word), remove it from parent:

```java
if (node.word == null && hasNoChildren(node)) {
    parent.children[idx] = null;
}
```

Why this helps:

* Suppose a word is found and there are no longer any other words in that subtree.
* Future DFS calls won’t waste time going into dead trie branches.

This is an optimization, not mandatory for correctness.

---

# Thorough example walkthrough (classic LeetCode example)

## Input

```text
board =
[
  ['o','a','a','n'],
  ['e','t','a','e'],
  ['i','h','k','r'],
  ['i','f','l','v']
]

words = ["oath","pea","eat","rain"]
```

Expected output:

```text
["oath","eat"]
```

---

## Step 1: Build the Trie

Trie contains paths for:

* `o -> a -> t -> h` (terminal word = "oath")
* `p -> e -> a` (terminal word = "pea")
* `e -> a -> t` (terminal word = "eat")
* `r -> a -> i -> n` (terminal word = "rain")

---

## Step 2: Start DFS from every cell

We iterate all cells. I’ll trace the two successful words.

---

# Walkthrough for `"oath"`

### Start at `(0,0)` = `'o'`

Call:

```text
dfs(0,0,root)
```

* `ch = 'o'`
* `root.children['o']` exists → move to trie node for prefix `"o"`
* `node.word == null` (not a complete word yet)
* mark board[0][0] = '#'

Board temporarily:

```text
# a a n
e t a e
i h k r
i f l v
```

Now explore neighbors.

---

### From `(0,0)`, try `(0,1)` = `'a'`

Call:

```text
dfs(0,1,node("o"))
```

* Trie child for `'a'` under `"o"` exists → prefix `"oa"`
* mark visited

Board:

```text
# # a n
e t a e
i h k r
i f l v
```

Explore neighbors.

---

### From `(0,1)`, try `(1,1)` = `'t'`

Call:

```text
dfs(1,1,node("oa"))
```

* Trie child for `'t'` exists → prefix `"oat"`
* mark visited

Board:

```text
# # a n
e # a e
i h k r
i f l v
```

Explore neighbors.

---

### From `(1,1)`, try `(2,1)` = `'h'`

Call:

```text
dfs(2,1,node("oat"))
```

* Trie child for `'h'` exists → prefix `"oath"`
* `node.word = "oath"` ✅ FOUND WORD
* add `"oath"` to answer
* set `node.word = null` to avoid duplicate insertion
* mark visited

Board:

```text
# # a n
e # a e
i # k r
i f l v
```

Then it explores neighbors, but no further trie children exist for `"oath"` path, so those branches prune quickly.

Backtrack:

* restore `(2,1)` to `'h'`
* return to previous frame

Then restore `(1,1)`, `(0,1)`, `(0,0)` as recursion unwinds.

✅ `"oath"` is collected.

---

# Walkthrough for `"eat"`

We continue starting DFS from all cells.

### Start at `(1,3)` = `'e'`

Call:

```text
dfs(1,3,root)
```

* `root.children['e']` exists (for `"eat"`)
* move to trie node `"e"`
* mark visited

Explore neighbors.

---

### From `(1,3)`, try `(1,2)` = `'a'`

* `"e" -> "a"` exists in trie
* mark visited

Explore neighbors.

---

### From `(1,2)`, try `(1,1)` = `'t'`

* `"ea" -> "t"` exists
* terminal node has `word = "eat"` ✅ FOUND
* add `"eat"`
* set `node.word = null`

Backtrack normally.

✅ `"eat"` is collected.

---

## Why `"pea"` and `"rain"` are not found

The DFS starts from all cells, but:

* `"p"` does not exist on the board → `"pea"` impossible
* `"rain"` path does not exist adjacently in the right sequence → DFS prunes before completion

---

# Result

Final `ans` contains:

```text
["oath", "eat"]
```

(Order may vary depending on DFS traversal order, which is okay.)

---

# Complexity (important interview explanation)

Let:

* `M x N` = board size
* `W` = number of words
* `L` = average/max word length

### Trie build

* `O(sum of all word lengths)` = `O(W * L)` roughly

### DFS

Worst-case (theoretical) is still exponential in path length:

* `O(M * N * 4^L)` style upper bound

But in practice Trie prefix pruning cuts off most branches very early.

### Space

* Trie storage: `O(sum of all word lengths)`
* Recursion stack: `O(L)` depth max (or board path depth)
*/
class Solution {

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word; // non-null only at the end of a word
    }

    private TrieNode root = new TrieNode();

    private void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.word = word; // store full word at terminal node
    }

    private boolean hasNoChildren(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) return false;
        }
        return true;
    }


    private List<String> ans;
    private char[][] board;
    private int rows, cols;

    public List<String> findWords(char[][] board, String[] words) {
        this.board = board;
        this.rows = board.length;
        this.cols = board[0].length;
        this.ans = new ArrayList<>();

        // Build trie
        for (String w : words) {
            insert(w);
        }

        // Start DFS from every cell
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                dfs(r, c, root);
            }
        }

        return ans;
    }

    

    private void dfs(int r, int c, TrieNode parent) {
        // Boundary / visited checks
        if (r < 0 || r >= rows || c < 0 || c >= cols) return;

        char ch = board[r][c];
        if (ch == '#') return; // already used in current path

        int idx = ch - 'a';
        TrieNode node = parent.children[idx];

        // Prefix pruning: if no trie child for this char, stop immediately
        if (node == null) return;

        // Found a full word
        if (node.word != null) {
            ans.add(node.word);
            node.word = null; // avoid duplicates
        }

        // Choose: mark visited
        board[r][c] = '#';

        // Explore 4 directions
        dfs(r - 1, c, node);
        dfs(r + 1, c, node);
        dfs(r, c - 1, node);
        dfs(r, c + 1, node);

        // Unchoose: restore
        board[r][c] = ch;

        // Optional trie pruning (optimization):
        // If this trie node has no children and is not terminal anymore, remove it.
        if (node.word == null && hasNoChildren(node)) {
            parent.children[idx] = null;
        }
    }
}








// Method 1.5.2: What I was doing (different from previous approaches)
/*
########### Corrections that were needed in my code ################
Here’s the **minimal corrected version of your original approach** (same style: `StringBuilder` + `trie.startsWith()` + `trie.search()`), followed by a **detailed explanation of every place you were going wrong before**.

# ✅ What was wrong in your previous versions (every key issue)

You were actually very close conceptually. Most issues were classic backtracking pitfalls.

---

## 1) **You forgot to mark end-of-word in Trie `insert()`** (in the earlier version)

### Your earlier bug

You inserted characters but did **not** do:

```java
node.isEnd = true;
```

### Why this broke your solution

Your `search(temp)` returns `node.isEnd`. If `isEnd` is never marked:

* `search(...)` always returns `false`
* so you never add any word

### Fix

At the end of `insert(word)`:

```java
node.isEnd = true;
```

---

## 2) **You only started DFS from `(0,0)`** (earlier version)

### Why it was wrong

In Word Search II, a word can start from **any board cell**. Starting only from top-left misses most valid words.

### Correct approach

Start DFS from **every** `(i,j)`:

```java
for (int i = 0; i < board.length; i++) {
    for (int j = 0; j < board[0].length; j++) {
        backtracking(..., i, j, ...);
    }
}
```

✅ You already fixed this in your later attempt.

---

## 3) **Your bounds check originally missed negative indices**

### Earlier issue

You checked:

```java
if (row >= board.length || col >= board[0].length) return;
```

but your recursion does:

* `row - 1`
* `col - 1`

So `row`/`col` can become negative.

### Why that crashes

You then do:

```java
board[row][col]
```

with `row = -1` or `col = -1` → `ArrayIndexOutOfBoundsException`.

### Correct bounds check

```java
if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) return;
```

✅ You fixed this too in your later version.

---

## 4) **No visited marking (this is the biggest remaining bug)**

This was the most important missing piece in your later version.

### Why it is wrong

In Word Search II, **you cannot reuse the same cell in the same word path**.

Without visited marking, your DFS can do illegal paths like:

* `(0,0)` → `(0,1)` → `(0,0)` → `(0,1)` → ...
  which can:
* generate invalid words
* cause infinite bouncing / very deep recursion / stack overflow

### Fix (choose/unchoose)

When entering a cell:

* mark visited, e.g. `board[row][col] = '#'`

After exploring neighbors:

* restore it back to original char

That’s what the corrected code does:

```java
char ch = board[row][col];
board[row][col] = '#';
...
board[row][col] = ch;
```

---

## 5) **You were not preventing duplicate words in the answer**

### Why duplicates happen

The same word can be found:

* starting from different cells
* through different valid paths

LeetCode 212 expects **unique** words.

### Your earlier code

```java
ans.add(temp);
```

This can add duplicates multiple times.

### Fix options

* Use `Set<String>` (what I used in the minimal corrected version)
* Or do Trie dedupe (`node.word = null`) in the optimized solution

In your current style, `Set<String>` is the simplest fix.

---

## 6) **You were checking `search(temp)` after exploring neighbors**

This is not always “wrong,” but it’s awkward and can create extra work.

### Your previous order

You did:

1. append char
2. recurse 4 directions
3. then `search(temp)`

### Why this is not ideal

If `temp` is already a valid word, you want to record it **immediately** before going deeper.
This is cleaner and easier to reason about.

### Better order (used in corrected code)

1. append char
2. prefix prune
3. if `search(cur)` → add result
4. recurse deeper
5. unchoose

---

## 7) **You added prefix pruning (good), but were still missing visited**

You correctly added:

```java
if (!trie.startsWith(sb.toString())) return;
```

That’s great — it prunes dead paths.

But prefix pruning alone **does not** stop illegal reuse of the same cell.
You still need visited marking.

So your later version had:

* ✅ start-from-every-cell
* ✅ negative bounds check
* ✅ prefix pruning
* ❌ visited
* ❌ duplicate handling

That’s why it was still incorrect.

---

## 8) **Performance issue in your style (not correctness, but important)**

Your approach repeatedly does:

* `sb.toString()`
* `trie.startsWith(cur)`
* `trie.search(cur)`

That means at every DFS step you:

* create a new string
* traverse the trie from root once for `startsWith`
* traverse again from root for `search`

This works (after fixes), but it is slower than the standard optimized solution that passes the current `TrieNode` pointer.

### But since you specifically wanted *your style fixed*:

✅ the code above is correct and keeps your approach.

#####################################

# Detailed walkthrough of the corrected version (your style)

Let’s use a small example so you can see exactly how `StringBuilder`, `startsWith`, visited marking, and unchoose work.

## Example

```text
board =
[
  ['o','a'],
  ['t','h']
]

words = ["oat", "oath", "hat", "oa"]
```

Trie contains:

* `"oat"`
* `"oath"`
* `"hat"`
* `"oa"`

---

## Start DFS from (0,0) = 'o'

`sb = ""`

### Enter (0,0)

* append `'o'` → `sb = "o"`
* `startsWith("o")` → true
* `search("o")` → false
* mark visited: board[0][0] = '#'

Board now:

```text
# a
t h
```

Explore neighbors.

---

## Go right to (0,1) = 'a'

* append `'a'` → `sb = "oa"`
* `startsWith("oa")` → true
* `search("oa")` → true ✅ add `"oa"` to set
* mark visited

Board:

```text
# #
t h
```

Explore neighbors from here.

---

## Go down to (1,1) = 'h'

* append `'h'` → `sb = "oah"`
* `startsWith("oah")` → false ❌
* prune immediately:

  * remove last char → `sb = "oa"`
  * return

This is prefix pruning doing its job.

---

## Back to "oa", go down-left path via (1,0) = 't' from valid neighbor chain

From `(0,1)` another route might go to `(1,1)` etc depending on layout; in a standard board, `"oat"` might require `(0,0)->(0,1)->(1,0)` which is diagonal (not allowed), so it won’t be found here.

Suppose a valid path existed; the same logic applies:

* append
* prefix check
* word check
* recurse
* unchoose

---

## Backtracking happens cleanly

When done exploring from `(0,1)`:

* restore board[0][1] = 'a'
* delete last char → `sb` goes from `"oa"` back to `"o"`

When done exploring from `(0,0)`:

* restore board[0][0] = 'o'
* delete last char → `sb = ""`

This “choose / explore / unchoose” at every recursion level is what prevents path contamination.

---

# Why `StringBuilder` unchoose is crucial

You were already doing:

```java
sb.deleteCharAt(sb.length() - 1);
```

That part was good. But it only handles the path string.
You also needed the **board visited unchoose**:

* `board[row][col] = '#';` (choose)
* recurse
* `board[row][col] = ch;` (unchoose)

Backtracking needs **both**:

1. undo path string state
2. undo board visited state

---

# Final note: this is correct, but slower than the TrieNode-pointer solution

Your corrected style is absolutely valid, but the optimized version is better because it avoids:

* repeated `toString()`
* repeated `startsWith()`
* repeated `search()`

Still, for learning backtracking + trie integration, this corrected version is a great stepping stone.

---

If you want, I can next give you a **side-by-side comparison**:

* **your corrected StringBuilder version**
  vs
* **optimized TrieNode-pointer version**

…and show exactly how the same DFS state is represented in both.
*/
// class Solution {
//     class Trie {
//         class TrieNode {
//             TrieNode[] children = new TrieNode[26];
//             boolean isEnd = false;
//         }

//         private final TrieNode root;

//         public Trie() {
//             root = new TrieNode();
//         }

//         public void insert(String word) {
//             TrieNode node = root;

//             for (char c : word.toCharArray()) {
//                 int idx = c - 'a';
//                 if (node.children[idx] == null) {
//                     node.children[idx] = new TrieNode();
//                 }
//                 node = node.children[idx];
//             }

//             // IMPORTANT: mark end of word
//             node.isEnd = true;
//         }

//         public boolean search(String word) {
//             TrieNode node = root;

//             for (char c : word.toCharArray()) {
//                 int idx = c - 'a';
//                 if (node.children[idx] == null) {
//                     return false;
//                 }
//                 node = node.children[idx];
//             }

//             return node.isEnd;
//         }

//         public boolean startsWith(String prefix) {
//             TrieNode node = root;

//             for (char c : prefix.toCharArray()) {
//                 int idx = c - 'a';
//                 if (node.children[idx] == null) {
//                     return false;
//                 }
//                 node = node.children[idx];
//             }

//             return true;
//         }
//     }

//     public List<String> findWords(char[][] board, String[] words) {
//         Trie trie = new Trie();

//         for (String word : words) {
//             trie.insert(word);
//         }

//         // Use Set to avoid duplicates
//         Set<String> found = new HashSet<>();

//         for (int i = 0; i < board.length; i++) {
//             for (int j = 0; j < board[0].length; j++) {
//                 backtracking(board, trie, found, i, j, new StringBuilder());
//             }
//         }

//         return new ArrayList<>(found);
//     }

//     private void backtracking(char[][] board, Trie trie, Set<String> found,
//                               int row, int col, StringBuilder sb) {
//         // 1) boundary checks (including negative)
//         if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
//             return;
//         }

//         // 2) visited check (cannot reuse same cell in current path)
//         if (board[row][col] == '#') {
//             return;
//         }

//         // choose current char
//         char ch = board[row][col];
//         sb.append(ch);

//         String cur = sb.toString();

//         // 3) prefix pruning: stop early if no word starts with current path
//         if (!trie.startsWith(cur)) {
//             sb.deleteCharAt(sb.length() - 1); // unchoose StringBuilder change
//             return;
//         }

//         // 4) if current path is a complete word, collect it
//         if (trie.search(cur)) {
//             found.add(cur); // Set avoids duplicates
//         }

//         // 5) mark visited (choose)
//         board[row][col] = '#';

//         // 6) explore 4 directions
//         backtracking(board, trie, found, row - 1, col, sb);
//         backtracking(board, trie, found, row + 1, col, sb);
//         backtracking(board, trie, found, row, col - 1, sb);
//         backtracking(board, trie, found, row, col + 1, sb);

//         // 7) unchoose (restore visited + path)
//         board[row][col] = ch;
//         sb.deleteCharAt(sb.length() - 1);
//     }
// }