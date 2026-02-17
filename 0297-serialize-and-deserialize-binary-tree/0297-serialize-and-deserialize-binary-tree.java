/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */



// Your Codec object will be instantiated and called as such:
// Codec ser = new Codec();
// Codec deser = new Codec();
// TreeNode ans = deser.deserialize(ser.serialize(root));



// Method 1: Preorder DFS with null markers
/*
###################### WHAT WAS WRONG WITH MY TWO TRAVERSAL APPROACH ########################

A lot is wrong here — the core issue is that **(inorder, postorder) traversals do not uniquely identify a binary tree unless all values are unique**, and in LeetCode 297 values are **not unique**. Plus there are several implementation bugs that would break even with unique values.

Here are the main problems:

---

## 1) You only return `inorder` from `serialize()`

```java
return inorder.toString();
```

…but your `deserialize()` uses **both** `inorder` and `postorder` fields. `postorder` is never passed in `data`, so `deserialize(data)` cannot reconstruct from `data` at all.

Even worse: LeetCode calls `deserialize` on a **new Codec instance** in many test harnesses, so `inorder`/`postorder` would be empty and you’d return null.

 Serialization must encode *all required information* into the returned string.

---

## 2) Not unique reconstruction (duplicates break it)

Even if you returned both traversals, reconstruction from inorder + postorder assumes **unique values**. In 297:

* node values can repeat (e.g., `[1,1,1]`)
* many different trees yield the same inorder and postorder sequences

So the encoding is **ambiguous**, meaning you cannot reliably deserialize to the original tree.

This is why standard solutions include **null markers** (or level-order with nulls), making the structure explicit.

---

## 3) You’re not encoding nulls at all

Your traversals skip null children. That loses structure information.

Example: these two different trees have the same inorder and postorder if values cooperate (and with duplicates it’s trivial to collide). Without null markers, structure isn’t guaranteed.

LeetCode 297 requires exact reconstruction, so you must encode missing children.

---

## 4) You never reset `inorder` / `postorder`

They are fields and keep accumulating across calls:

```java
private StringBuilder inorder = new StringBuilder();
private StringBuilder postorder = new StringBuilder();
```

If `serialize()` is called twice on the same Codec object, the second serialization will contain junk from the first.

You must clear them at the start of `serialize()` (or use locals).

---

## 5) Your “parsing” only works for single-digit non-negative values

You do:

```java
root.val = postorder.charAt(...) - '0'
```

That assumes each value is one character `'0'..'9'`.

But in 297 values can be:

* negative (e.g., -10)
* multi-digit (e.g., 12, 100)

So `"12"` would be read as `'1'` then `'2'` as two separate nodes — completely wrong.

You need delimiters like commas and parse integers.

---

## 6) You don’t use `data` at all in `deserialize`

`deserialize(String data)` ignores `data` and reads from the object fields `inorder` and `postorder`.

That violates the problem contract: deserialize must reconstruct **from the string**.

---

## 7) Even your reconstruction logic is for a BST-style unique reconstruction

What you wrote in `deserialize()` resembles the iterative construction trick for inorder + postorder, but it depends on:

* being able to match nodes by value in inorder
* no duplicates
* correct tokenization

All of those are broken here.

---

# What you should do instead (what 297 expects)

Use an encoding that includes **null markers**. Two common correct approaches:

### A) Preorder DFS with null markers

Serialize: `val,left,right` with `#` for null
Example: `1,2,#,#,3,4,#,#,5,#,#`

Deserialize by reading tokens in order.

### B) BFS level-order with null markers

Serialize level-order with `null` placeholders.

Both are unambiguous, handle duplicates, and handle any integer values.
###################################################################################################


Below is the **preorder DFS + null markers** approach for **LeetCode 297 (Serialize and Deserialize Binary Tree)**, with a detailed explanation and a thorough walkthrough.

---

# Why preorder + null markers works

A binary tree is defined by:

* node values
* and exactly where children are missing (`null`)

A traversal **without nulls** loses structure information.
So we explicitly record nulls using a marker like `#`.

### Serialization format

We’ll store a preorder sequence:

**Preorder order:** `node, left, right`

Rules:

* If node is null → write `#`
* Else write the integer value
* Separate tokens with commas (`,`)

Example:

```
1
/ \
2  3
  / \
 4  5
```

Serialized as:
`1,2,#,#,3,4,#,#,5,#,#`

This is **unambiguous** even with:

* duplicate values
* negative values
* multi-digit values

---

# Intuition for deserialization

During deserialization, we read tokens in the same preorder order.

At any point:

* If token is `#` → return `null`
* Else create a node with that value, then recursively build its left subtree, then right subtree

Because the serialized string includes nulls, the recursion knows exactly when a subtree ends.

### Notes

* We use commas so multi-digit/negative values work.
* The trailing comma means `split(",")` can produce a last empty token; we guard `t.isEmpty()`.

---

# Thorough example walkthrough

Let’s use the classic example from LeetCode:

Tree:

```
        1
       / \
      2   3
         / \
        4   5
```

---

## Step 1: Serialization (preorder)

Preorder visits: `1, 2, #, #, 3, 4, #, #, 5, #, #`

Walk it:

### At node 1

* write `1,`
* serialize left subtree (node 2)
* serialize right subtree (node 3)

### Node 2

* write `2,`
* left child is null → write `#,`
* right child is null → write `#,`

### Node 3

* write `3,`
* left subtree is node 4
* right subtree is node 5

### Node 4

* write `4,`
* left null → `#,`
* right null → `#,`

### Node 5

* write `5,`
* left null → `#,`
* right null → `#,`

Final serialized string:

```
"1,2,#,#,3,4,#,#,5,#,#,"
```

(That trailing comma is fine.)

---

## Step 2: Deserialization (rebuild from tokens)

Tokens (ignoring the last empty one):
Index:Token
0:`1`
1:`2`
2:`#`
3:`#`
4:`3`
5:`4`
6:`#`
7:`#`
8:`5`
9:`#`
10:`#`

We keep an index pointer `idx` that moves forward as we consume tokens.

### parse at idx=0

* token = `1` → create node(1)
* build left = parse(idx=1)
* build right = parse(after left finishes)

#### build left of 1: parse idx=1

* token = `2` → create node(2)
* build left of 2: parse idx=2

  * token `#` → null
* build right of 2: parse idx=3

  * token `#` → null
    Return node(2)

So far: node(1).left = node(2)

#### build right of 1: parse idx=4

* token = `3` → create node(3)
* build left of 3: parse idx=5

##### build left of 3: parse idx=5

* token = `4` → create node(4)
* left: parse idx=6 → `#` → null
* right: parse idx=7 → `#` → null
  Return node(4)

Now node(3).left = node(4)

##### build right of 3: parse idx=8

* token = `5` → create node(5)
* left: parse idx=9 → `#` → null
* right: parse idx=10 → `#` → null
  Return node(5)

Now node(3).right = node(5)

Return node(3)

Now node(1).right = node(3)

✅ Full tree rebuilt exactly.

---

# Why this is guaranteed to be unique

Because every node produces exactly:

* one value token, plus
* two child encodings (which could be `#`)

So the sequence fully determines structure.
Even if values repeat (e.g., all nodes are `1`), the `#` positions still make structure unique.

---

# Complexity

Let `n` be number of nodes.

* Serialize: `O(n)` time, output size `O(n)`
* Deserialize: `O(n)` time, recursion depth `O(h)` (`h` height; worst-case `O(n)`)
*/

public class Codec {

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        build(root, sb);
        return sb.toString();
    }

    private void build(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("#,");
            return;
        }
        sb.append(node.val).append(",");
        build(node.left, sb);
        build(node.right, sb);
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        // split into tokens
        String[] tokens = data.split(",");
        int[] idx = new int[]{0}; // mutable index pointer
        return parse(tokens, idx);
    }

    private TreeNode parse(String[] tokens, int[] idx) {
        // Defensive: if input ends unexpectedly
        if (idx[0] >= tokens.length) return null;

        String t = tokens[idx[0]++];
        if (t.equals("#")) return null;
        if (t.isEmpty()) return null; // handles trailing comma edge

        TreeNode node = new TreeNode(Integer.parseInt(t));
        node.left = parse(tokens, idx);
        node.right = parse(tokens, idx);
        return node;
    }
}







// Method 2: BFS / Level-order approach with null markers
/*
Below is the **BFS / level-order** approach with **null markers** for **LeetCode 297 (Serialize and Deserialize Binary Tree)**, with a detailed explanation and a thorough walkthrough.

---

# Big idea

We encode the tree exactly the way you’d read it level by level:

* Use a **queue** (BFS).
* For each node popped:

  * If it’s non-null: write its value, and enqueue its left and right children (even if they’re null).
  * If it’s null: write a special marker (like `#`) and do **not** enqueue children.

This preserves structure because `#` records “a child is missing at this position”.

We separate tokens with commas so values can be multi-digit/negative.

---

# Serialization format

Example output:

```
"1,2,3,#,#,4,5,#,#,#,#,"
```

(That trailing comma is optional; I’ll keep it for easy appending.)

Tokens mean:

* integers: node values
* `#`: null child placeholder


### Notes

* Works with duplicates, negatives, multi-digit values.
* The `trimTrailingNulls` part is **optional**. You can remove it to keep code simpler.

If you don’t want trimming at all, just return `sb.toString()` and in `deserialize` ignore empty tokens at end.

---

# Thorough example walkthrough

Use the classic tree:

```
        1
       / \
      2   3
         / \
        4   5
```

---

## Part 1: Serialize (BFS)

Initialize:

* `queue = [1]`
* `output = ""`

We pop and append tokens:

### Step 1: pop 1

* output: `1,`
* enqueue children: left=2, right=3
* queue: `[2, 3]`

### Step 2: pop 2

* output: `1,2,`
* enqueue children of 2: left=null, right=null
* queue: `[3, null, null]`

### Step 3: pop 3

* output: `1,2,3,`
* enqueue children: left=4, right=5
* queue: `[null, null, 4, 5]`

### Step 4: pop null

* output: `1,2,3,#,`
* do NOT enqueue children
* queue: `[null, 4, 5]`

### Step 5: pop null

* output: `1,2,3,#,#,`
* queue: `[4, 5]`

### Step 6: pop 4

* output: `1,2,3,#,#,4,`
* enqueue children: null, null
* queue: `[5, null, null]`

### Step 7: pop 5

* output: `1,2,3,#,#,4,5,`
* enqueue children: null, null
* queue: `[null, null, null, null]`

### Steps 8–11: pop four nulls

Append `#` four times:

* output: `1,2,3,#,#,4,5,#,#,#,#,`
* queue becomes empty

Final serialized string (without trimming):

```
"1,2,3,#,#,4,5,#,#,#,#,"
```

This fully captures the exact structure.

---

## Part 2: Deserialize (BFS rebuild)

Tokens:
Index: value
0: "1"
1: "2"
2: "3"
3: "#"
4: "#"
5: "4"
6: "5"
7: "#"
8: "#"
9: "#"
10:"#"
(there may be an empty token at the very end if trailing comma; we ignore empties)

Initialize:

* root = 1
* queue = [1]
* idx = 1

Now attach children in pairs (left, right) for each node popped:

### Parent = 1 (pop from queue)

* left token tokens[1] = "2" → create node(2), attach to 1.left, enqueue 2
* right token tokens[2] = "3" → create node(3), attach to 1.right, enqueue 3
* idx becomes 3
* queue: `[2, 3]`

### Parent = 2

* left token tokens[3] = "#" → 2.left = null
* right token tokens[4] = "#" → 2.right = null
* idx becomes 5
* queue: `[3]`

### Parent = 3

* left token tokens[5] = "4" → 3.left = 4, enqueue 4
* right token tokens[6] = "5" → 3.right = 5, enqueue 5
* idx becomes 7
* queue: `[4, 5]`

### Parent = 4

* left token tokens[7] = "#" → null
* right token tokens[8] = "#" → null
* idx becomes 9
* queue: `[5]`

### Parent = 5

* left token tokens[9] = "#" → null
* right token tokens[10] = "#" → null
* idx becomes 11
* queue: `[]`

Done. Tree reconstructed exactly.

---

# Why this is unambiguous

Because every node’s position in the level-order sequence has explicit placeholders for missing children. Even if values repeat, the `#` pattern nails down structure.

---

# Complexity

* **Serialize:** `O(n)` time, `O(n)` output size, `O(w)` queue space (`w` max width)
* **Deserialize:** `O(n)` time, `O(w)` queue space

*/

// public class Codec {

//     // Encodes a tree to a single string (level-order with null markers).
//     public String serialize(TreeNode root) {
//         if (root == null) return "#,";

//         StringBuilder sb = new StringBuilder();
//         Deque<TreeNode> q = new ArrayDeque<>();
//         q.offer(root);

//         while (!q.isEmpty()) {
//             TreeNode node = q.poll();

//             if (node == null) {
//                 sb.append("#,");
//                 continue;
//             }

//             sb.append(node.val).append(",");
//             q.offer(node.left);
//             q.offer(node.right);
//         }

//         // Optional: trim trailing "#," tokens to shorten the string
//         // (Not required for correctness; deserialize can handle extra nulls)
//         return trimTrailingNulls(sb.toString());
//     }

//     private String trimTrailingNulls(String s) {
//         // Remove trailing "#," pairs to reduce size
//         // Keep at least one token.
//         int i = s.length() - 1;
//         // Work with tokens by scanning from end; simplest token trim:
//         // repeatedly remove "#," at end.
//         while (s.endsWith("#,")) {
//             s = s.substring(0, s.length() - 2);
//         }
//         return s.isEmpty() ? "#," : s + ","; // keep trailing comma convention
//     }

//     // Decodes your encoded data to tree.
//     public TreeNode deserialize(String data) {
//         if (data == null || data.isEmpty()) return null;

//         String[] tokens = data.split(",");
//         int n = tokens.length;

//         // handle cases like "#," or "#"
//         if (n == 0 || tokens[0].equals("#") || tokens[0].isEmpty()) return null;

//         TreeNode root = new TreeNode(Integer.parseInt(tokens[0]));
//         Deque<TreeNode> q = new ArrayDeque<>();
//         q.offer(root);

//         int idx = 1; // next token index

//         while (!q.isEmpty() && idx < n) {
//             TreeNode parent = q.poll();

//             // ---- left child ----
//             if (idx < n && !tokens[idx].equals("#") && !tokens[idx].isEmpty()) {
//                 TreeNode left = new TreeNode(Integer.parseInt(tokens[idx]));
//                 parent.left = left;
//                 q.offer(left);
//             }
//             idx++;

//             // ---- right child ----
//             if (idx < n && !tokens[idx].equals("#") && !tokens[idx].isEmpty()) {
//                 TreeNode right = new TreeNode(Integer.parseInt(tokens[idx]));
//                 parent.right = right;
//                 q.offer(right);
//             }
//             idx++;
//         }

//         return root;
//     }
// }

