/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */


// Logic of solution given below:
/*
Here’s a way to *think* about 236 (LCA of a Binary Tree) so you can reliably re-derive it from scratch.

# 0) First question to ask yourself

**“What should my function return to its parent?”**

Design a helper `f(node)` whose **return value** carries exactly the info the parent needs.
Make this your contract (invariant):

> `f(node)` returns **one of**:
>
> * `null` — neither `p` nor `q` is anywhere in this subtree.
> * `p` or `q` — this subtree contains exactly one target (and this is the node found).
> * **the LCA** — both targets exist in this subtree and this node is their lowest common ancestor.

If you hold this invariant, the combination logic becomes obvious.

# 1) Base cases (checkpoint 1)

* If `node == null` → return `null`. (No targets here.)
* If `node == p` or `node == q` → return `node`.
  Why? You just found a target; your parent will decide whether a matching target exists elsewhere.

> Tip: You *can* short-circuit here and not recurse further; it remains correct under the standard LeetCode assumption that both `p` and `q` exist in the tree.

# 2) Recurse (checkpoint 2)

Ask both sides independently:

```
left  = f(node.left)
right = f(node.right)
```

Each is one of: `null`, `p`, `q`, or an **LCA** from that subtree.

# 3) Combine results (checkpoint 3)

Now do plain case analysis:

* **Case A: `left != null` and `right != null`**
  You’ve found one target (or an LCA) on the left and one target (or an LCA) on the right.
  That means the *current* `node` is the **first place** where both sides meet → **return `node`** (the LCA).

* **Case B: exactly one side is non-null**
  Propagate that non-null up:

  ```
  return (left != null) ? left : right;
  ```

  Why? So far there’s only one target found in this entire subtree; your parent will see if the other target appears on the other side.

* **Case C: both null**
  Return `null`. (No targets below.)

These three lines are the whole problem.

# 4) Why this works (intuitive proof)

* The only way two targets end up in different branches of the *same* node is that this node is their **lowest** common ancestor (the split point). That’s Case A.
* If both targets are on the **same** side, the first node where they split will be discovered *within that side*, and you’ll bubble that answer up unchanged (Case B).
* If a subtree has neither target, it contributes nothing (`null`), preventing false positives (Case C).

# 5) Complexity

* **Time:** `O(n)` — each node is visited once.
* **Space:** `O(h)` — recursion stack height (`h` is tree height).
  (Balanced tree ≈ `O(log n)`, skewed ≈ `O(n)`.)

# 6) Mental checklist when you code

1. **Write the contract** in a comment: what your helper returns.
2. **Base cases first:** `null` → `null`; `node == p || node == q` → `node`.
3. **Recurse** left/right.
4. **Three-way combine** (both → `node`; one → that one; none → `null`).
5. Return value from the root call is the LCA.

# 7) Dry run in your head (two patterns)

**Pattern 1 (ancestor case):** `p` is an ancestor of `q`.

* The subtree rooted at `p` will eventually see `q` below on one side and `null` on the other; the combine rule returns `p`. That bubbles all the way up unchanged.

**Pattern 2 (split case):** `p` in left subtree, `q` in right subtree of some node `X`.

* Left returns `p`, right returns `q` → at `X` you hit Case A → return `X`.

# 8) Common pitfalls to avoid

* **Returning booleans** instead of nodes. Booleans force extra bookkeeping to reconstruct *which node* is the LCA. Returning the node avoids that.
* **Over-thinking early exits.** It’s fine to return immediately when `node == p || node == q`. The combine logic still works.
* **Forgetting problem assumptions.** LeetCode 236 assumes both nodes exist.
  If not guaranteed, you’d track whether both were found (e.g., with a counter) and only accept the LCA if both were discovered.

# 9) How to re-derive under pressure

If you forget the code, remember this mantra:

> “**What do I return to my parent?**
> `null`, a **found node**, or the **LCA**.
> Recurse both sides, and if both return non-null, **I am the LCA**; else pass the non-null up.”

That’s it—stick to the contract, and the implementation falls out naturally.
*/

class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if(root == null){
            return null;
        }

        if(root == p || root == q){
            return root;
        }

        TreeNode leftRes = lowestCommonAncestor(root.left, p, q);
        TreeNode rightRes = lowestCommonAncestor(root.right, p, q);

        if(leftRes != null && rightRes != null){
            return root;
        }

        if(leftRes != null){
            return leftRes;
        }else{
            return rightRes;
        }
    }
}