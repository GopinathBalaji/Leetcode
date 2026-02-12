/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */


// Method 1: Recursive DFS
/*
# WHERE WAS I GOING WRONG:

This approach is **wrong** for BST validation because it only checks the **immediate children** against `root`, not the **entire subtree constraints**.

A valid BST requires:

* **All** values in the left subtree `< root.val`
* **All** values in the right subtree `> root.val`

Your code only enforces:

* `root.left.val < root.val`
* `root.right.val > root.val`
  …and recursively enforces that each subtree is “locally” valid, but that still misses violations that occur deeper.

### Counterexample (your code returns true, but it’s not a BST)

```
    5
   / \
  1   7
     /
    4
```

This is invalid because `4` is in the **right subtree of 5** but `4 < 5`.

What your code checks:

* At `5`: left child `1 < 5` ok, right child `7 > 5` ok
* At `7`: left child `4 < 7` ok
  So it incorrectly returns `true`.

---

## How to fix it (correct idea)

You must carry **bounds** down the recursion:

* Left subtree must be in `(min, root.val)`
* Right subtree must be in `(root.val, max)`
####################################################

You must carry bounds down the recursion:
Left subtree must be in (min, root.val)
Right subtree must be in (root.val, max)

Why long?
To avoid overflow when the tree contains Integer.MIN_VALUE or Integer.MAX_VALUE.

# Example 1 — **Valid BST**

Tree:

```
      5
     / \
    1   7
       / \
      6   8
```

Start: `dfs(5, -∞, +∞)` → `(-∞, +∞)` means `(Long.MIN_VALUE, Long.MAX_VALUE)` in code.

1. `node=5, (min=-∞, max=+∞)`

   * Check: `-∞ < 5 < +∞` 
   * Recurse left: `dfs(1, -∞, 5)` and right: `dfs(7, 5, +∞)`.

2. **Left subtree**: `node=1, (min=-∞, max=5)`

   * Check: `-∞ < 1 < 5` 
   * Left child: `dfs(null, -∞, 1)` → `true` (base case)
   * Right child: `dfs(null, 1, 5)` → `true`
   * Result for node 1: `true && true = true`.

3. **Right subtree**: `node=7, (min=5, max=+∞)`

   * Check: `5 < 7 < +∞` 
   * Left child: `dfs(6, 5, 7)`
   * Right child: `dfs(8, 7, +∞)`

   3a) `node=6, (min=5, max=7)`
   \- Check: `5 < 6 < 7` 
   \- Left: `dfs(null, 5, 6)` → `true`
   \- Right: `dfs(null, 6, 7)` → `true`
   \- Result: `true`.

   3b) `node=8, (min=7, max=+∞)`
   \- Check: `7 < 8 < +∞` 
   \- Left: `dfs(null, 7, 8)` → `true`
   \- Right: `dfs(null, 8, +∞)` → `true`
   \- Result: `true`.

4. Back at `node=7`: `left=true && right=true` ⇒ `true`.

5. Back at `node=5`: `left=true && right=true` ⇒ **overall `true`**.

Why the base case matters: every time we hit `null` (beyond a leaf), we return `true`, which says “this empty subtree doesn’t violate anything,” letting valid branches remain valid.

---

# Example 2 — **Invalid BST (deep violation)**

Tree:

```
      5
     / \
    4   6
       / \
      3   7
```

This tree is **not** a BST because `3` sits in the **right** subtree of `5` but is **less than 5**.

1. `node=5, (min=-∞, max=+∞)`

   * Check: `-∞ < 5 < +∞` 
   * Recurse left: `dfs(4, -∞, 5)` and right: `dfs(6, 5, +∞)`.

2. **Left subtree**: `node=4, (min=-∞, max=5)`

   * Check: `-∞ < 4 < 5` 
   * Left: `dfs(null, -∞, 4)` → `true`
   * Right: `dfs(null, 4, 5)` → `true`
   * Result: `true`.

3. **Right subtree**: `node=6, (min=5, max=+∞)`

   * Check: `5 < 6 < +∞` 
   * Left: `dfs(3, 5, 6)`
   * Right: `dfs(7, 6, +∞)`

   3a) **Here’s the violation**: `node=3, (min=5, max=6)`
   \- Check: is `5 < 3 < 6`?  (3 ≤ 5)
   \- Return `false` immediately.
   \- Because of short-circuiting, `dfs(6’s right)` is **not even called**—no need to do extra work once a violation is found.

4. Back at `node=6`: left was `false`, so `false && (…skipped…)` ⇒ `false`.

5. Back at `node=5`: left was `true`, right is `false` ⇒ `true && false` ⇒ **overall `false`**.

This example shows why just checking immediate children isn’t enough; the problem is **deeper** (the `3` is under the right subtree of `5` but breaks the global rule).

---

# Tiny edge-case notes

* **Duplicates are not allowed** in a strict BST for this problem. That’s why we use `<= min` and `>= max` as violations.
*/
class Solution {
    public boolean isValidBST(TreeNode root) {
        if(root == null){
            return true;
        }

        return dfs(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean dfs(TreeNode node, long lo, long hi){
        if(node == null){
            return true;
        }

        if(node.val <= lo || node.val >= hi){
            return false;
        }

        return dfs(node.left, lo, node.val) && dfs(node.right, node.val, hi);
    }
}






// Method 2: Iterative DFS version
/*
You have two good iterative DFS styles:

1. **Stack with bounds (most direct DFS)**
2. **Inorder traversal with “prev” (also DFS, but uses BST inorder property)**

Since you asked for iterative DFS, I’ll give the **stack + bounds** version first (it mirrors the correct recursive solution).

---

## Iterative DFS with bounds (stack of frames)

### Core idea

For every node, maintain the valid range `(low, high)` it must fall into:

* All nodes in the left subtree must be `< current.val`
* All nodes in the right subtree must be `> current.val`
* And also must respect ancestors’ constraints.

So when you push children:

* left child range becomes `(low, node.val)`
* right child range becomes `(node.val, high)`

Use `long` bounds to avoid overflow with `Integer.MIN_VALUE/MAX_VALUE`.


## Detailed explanation

Each stack entry is a “promise”:

> “This node must be strictly between `low` and `high`.”

When you pop:

1. Verify the node satisfies that promise.
2. Create new promises for its children using updated bounds.

Why strict (`<` / `>` not `<=` / `>=`)?

* BST definition in LeetCode 98 requires **strictly** increasing inorder, i.e., no duplicates allowed in either subtree.

---

## Thorough example walkthrough

### Example 1: the classic invalid case you mentioned

`root = [5,1,4,null,null,3,6]`:

```
      5
     / \
    1   4
       / \
      3   6
```

Initialize:

* stack = `[(node=5, low=-∞, high=+∞)]`

I’ll write `(-∞, +∞)` as `(MIN, MAX)`.

---

#### Step 1: pop (5, MIN, MAX)

Check: `MIN < 5 < MAX` ✅

Push children with updated bounds:

* right child 4 must be in `(5, MAX)` → push `(4, 5, MAX)`
* left child 1 must be in `(MIN, 5)` → push `(1, MIN, 5)`

stack (top first): `[(1, MIN, 5), (4, 5, MAX)]`

---

#### Step 2: pop (1, MIN, 5)

Check: `MIN < 1 < 5` ✅
No children → push nothing

stack: `[(4, 5, MAX)]`

---

#### Step 3: pop (4, 5, MAX)

Check: must satisfy `5 < 4 < MAX` ❌ (4 is not > 5)

Return `false`.

✅ Correct result: **invalid BST**

Notice how this catches the violation even though 4 is “locally” fine relative to its own children—because its **bound comes from ancestor 5**.

---

### Example 2: invalid case your original code misses

```
    5
   / \
  1   7
     /
    4
```

Start:

* push `(5, MIN, MAX)`

Pop 5: ok
Push:

* `(7, 5, MAX)`
* `(1, MIN, 5)`

Pop 1: ok
Pop 7 with bounds `(5, MAX)`: ok
Push left child 4 with bounds `(5, 7)`  ← key

Pop 4:

* must satisfy `5 < 4 < 7` ❌
  Return `false`.

This is exactly the “global” constraint your child-only approach can’t enforce.

---

## Complexity

* **Time:** `O(n)` each node pushed/popped once
* **Space:** `O(h)` average / `O(n)` worst-case for skewed tree (stack frames)
*/
// class Solution {
//     private static class Frame {
//         TreeNode node;
//         long low, high; // node.val must be strictly between (low, high)
//         Frame(TreeNode node, long low, long high) {
//             this.node = node;
//             this.low = low;
//             this.high = high;
//         }
//     }

//     public boolean isValidBST(TreeNode root) {
//         if (root == null) return true;

//         Deque<Frame> stack = new ArrayDeque<>();
//         stack.push(new Frame(root, Long.MIN_VALUE, Long.MAX_VALUE));

//         while (!stack.isEmpty()) {
//             Frame f = stack.pop();
//             TreeNode node = f.node;

//             if (node.val <= f.low || node.val >= f.high) {
//                 return false;
//             }

//             // Right subtree: (node.val, f.high)
//             if (node.right != null) {
//                 stack.push(new Frame(node.right, node.val, f.high));
//             }

//             // Left subtree: (f.low, node.val)
//             if (node.left != null) {
//                 stack.push(new Frame(node.left, f.low, node.val));
//             }
//         }

//         return true;
//     }
// }







// Method 3: The BST inorder property can be used if we maintain previous node value
// Since the inorder traversal of BST gives sorted values we can check if current
// value is greater than the previous value.
/*
Why this works
We recursively visit nodes in order: left → root → right.

For a valid BST, this sequence must be strictly increasing.

We keep track of the last visited node (prev) and check if the current node’s value is greater. If not, it’s invalid.

Comparing the Two Correct Approaches:
Both are O(n) time and O(h) space (h = tree height).
*/

// class Solution{

//     private TreeNode prev = null;

//     public boolean isValidBST(TreeNode root){
//         return inorder(root);
//     }

//     private boolean inorder(TreeNode node){
//         if(node == null){
//             return true;
//         }

//         if(!inorder(node.left)){
//             return false;
//         }

//         if(prev != null && node.val <= prev.val){
//             return false;
//         }

//         prev = node;

//         return inorder(node.right);
//     }
// }







// Method 4: Iterative version of the above inorder approach
/*
### Key BST fact

If a binary tree is a valid BST (with **strict** ordering), then an **inorder traversal** (Left → Node → Right) visits values in a **strictly increasing** sequence.

So the whole algorithm is:

* Do inorder traversal iteratively with a stack.
* Keep `prev` = the value of the previously visited node.
* If you ever see `current.val <= prev`, it’s **not** a BST.

### Why `Long prev`?

* `cur.val` is `int`, but using `Long` avoids any awkwardness with initializing to `Integer.MIN_VALUE`.
* Using `null` cleanly represents “no previous node yet”.

### Why `<=`?

BST requires **strictly increasing** inorder values:

* duplicates are invalid → `cur.val == prev` must return false.

---

## Thorough walkthrough 1: invalid example `[5,1,4,null,null,3,6]`

Tree:

```
      5
     / \
    1   4
       / \
      3   6
```

We’ll track:

* `cur`
* `stack` (top on left)
* `prev`
* visited order

### Start

* `cur = 5`
* `stack = []`
* `prev = null`

### Step A: go left as far as possible

Push 5, go left → push 1, go left → null

* `stack = [1, 5]`
* `cur = null`

### Step B: pop/visit

Pop 1:

* `prev = null` so no comparison
* set `prev = 1`
* move to `cur = 1.right` (null)

Visited so far: `1`

### Step C: cur null, pop/visit again

Pop 5:

* compare: `5 <= prev(1)`? no
* set `prev = 5`
* move to `cur = 5.right` (node 4)

Visited so far: `1, 5`

### Step D: go left from 4

Push 4, go left → push 3, go left → null

* `stack = [3, 4]`
* `cur = null`
* `prev = 5`

### Step E: pop/visit

Pop 3:

* compare: `3 <= prev(5)`? **YES** → return **false**

✅ Correct: it’s not a BST, because inorder should be increasing, but we got `..., 5, 3, ...`.

---

## Thorough walkthrough 2: valid BST example

Tree:

```
      5
     / \
    1   7
       / \
      6   8
```

Inorder should be: `1, 5, 6, 7, 8` (strictly increasing)

Process (high level):

* Visit 1 → prev=1
* Visit 5 → 5 > 1 ok, prev=5
* Visit 6 → 6 > 5 ok, prev=6
* Visit 7 → 7 > 6 ok, prev=7
* Visit 8 → 8 > 7 ok, prev=8
  Finish → true

---

## Why this works (intuition)

Inorder traversal visits:

* everything in left subtree (all must be smaller)
* then the node
* then everything in right subtree (all must be larger)

So if at any point the sequence stops increasing, some node ended up on the wrong side of an ancestor boundary → not a BST.

---

## Common pitfalls

* Using `<` instead of `<=` would incorrectly allow duplicates.
* Forgetting the “go-left loop” (`while (cur != null)`) breaks traversal.
* Using a sentinel int for `prev` can fail at min int; `Long prev = null` avoids it.
*/
// class Solution {
//     public boolean isValidBST(TreeNode root) {
//         Deque<TreeNode> stack = new ArrayDeque<>();
//         TreeNode cur = root;

//         Long prev = null; // previous inorder value (use Long to avoid edge issues)

//         while (cur != null || !stack.isEmpty()) {
//             // 1) Go as left as possible
//             while (cur != null) {
//                 stack.push(cur);
//                 cur = cur.left;
//             }

//             // 2) Visit node
//             cur = stack.pop();

//             if (prev != null && cur.val <= prev) {
//                 return false;
//             }
//             prev = (long) cur.val;

//             // 3) Go right
//             cur = cur.right;
//         }

//         return true;
//     }
// }
