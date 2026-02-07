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

// Method 1: Iterative DFS to iteratve root and Recursive DFS to check if root and subRoot are same
/*
*/
class Solution {
    public boolean isSubtree(TreeNode root, TreeNode subRoot) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        while(!stack.isEmpty()){
            TreeNode node  = stack.pop();

            if(node.val == subRoot.val){
                boolean value = isSameTree(node, subRoot);
                if(value){
                    return true;
                }
            }

            if(node.right != null){
                stack.push(node.right);
            }
            if(node.left != null){
                stack.push(node.left);
            }
        }

        return false;
    }


    private boolean isSameTree(TreeNode root1, TreeNode root2){
        if(root1 == null || root2 == null){
            return root1 == root2;
        }

        boolean left = isSameTree(root1.left, root2.left);
        boolean right = isSameTree(root1.right, root2.right);

        return (root1.val == root2.val) && left && right;
    }
}








// Method 2: My approach Recursive DFS to iteratve root and Recursive DFS to check if root and subRoot are same
/*
*/
// class Solution {
//     boolean isSame = false;
//     public boolean isSubtree(TreeNode root, TreeNode subRoot) {
//         iterate(root, subRoot);

//         return isSame;
//     }

//     private void iterate(TreeNode root, TreeNode subRoot){
//         if(root == null){
//             return;
//         }

//         iterate(root.left, subRoot);
//         iterate(root.right, subRoot);

//         if(root.val == subRoot.val){
//             if(isSameTree(root, subRoot)){
//                 isSame = true;
//             }
//         }
//     }

//     private boolean isSameTree(TreeNode root1, TreeNode root2){
//         if(root1 == null || root2 == null){
//             return root1 == root2;
//         }

//         boolean left = isSameTree(root1.left, root2.left);
//         boolean right = isSameTree(root1.right, root2.right);

//         return (root1.val == root2.val) && left && right;
//     }
// }








// Method 2.5: Better Version of my double recursive approach
/*
# WHAT COULD GO WRONG IN MY VERSION OF DOUBLE RECURSION OR WHY IT IS NOT EFFICIENT:

Your approach is *almost* correct, but there are a few real problems / pitfalls:

### 1) **NPE when `subRoot` is null**

You do `subRoot.val` in `iterate()` without checking `subRoot == null`.

* In LeetCode 572 (Subtree of Another Tree), `subRoot` is typically non-null, but it’s not guaranteed by “math definition” and some variants/tests can include it.
* Correct behavior if `subRoot == null` is usually **true** (empty tree is a subtree of any tree).

So this can crash:

```java
if(root.val == subRoot.val) // subRoot could be null -> NPE
```

### 2) **You don’t early-exit once you found a match**

You set a global `isSame = true`, but you still traverse the entire tree. This won’t make it wrong, just slower. But it can matter for time limits.

### 3) **Global mutable state is risky**

It works on LeetCode because each test uses a new `Solution` instance, but in general it’s easy to forget to reset `isSame` (and it’s less clean). Also makes reasoning/debugging harder.

### 4) (Minor) **Unnecessary recursion after mismatch**

In `isSameTree`, you recurse left and right even if values differ. Not wrong, just wasted work.


### Summary

* **Potentially wrong**: NPE if `subRoot == null`.
* **Not wrong but suboptimal**: global `isSame`, no early exit, extra recursion.

If you want, I can show an even faster approach (tree serialization / hashing / KMP), but the above is the standard accepted DFS solution.
*/
// class Solution {
//     public boolean isSubtree(TreeNode root, TreeNode subRoot) {
//         if (subRoot == null) return true;   // avoid NPE + correct definition
//         if (root == null) return false;

//         return iterate(root, subRoot);
//     }

//     private boolean iterate(TreeNode root, TreeNode subRoot) {
//         if (root == null) return false;

//         // early match check at this node
//         if (root.val == subRoot.val && isSameTree(root, subRoot)) return true;

//         // early-exit on either side
//         return iterate(root.left, subRoot) || iterate(root.right, subRoot);
//     }

//     private boolean isSameTree(TreeNode a, TreeNode b) {
//         if (a == null || b == null) return a == b;
//         if (a.val != b.val) return false;
//         return isSameTree(a.left, b.left) && isSameTree(a.right, b.right);
//     }
// }








// Method 3: String Matching Approach
/*
The idea is:

1. **Serialize** both trees into strings in a way that preserves structure
2. Check if `subRootString` is a **substring** of `rootString`
3. Use a fast string-matching algorithm like **KMP** to do it in linear time

---

## Why serialization needs “null markers”

If you only serialize values (like preorder `1 2 3`) you can get false matches.

Example (false positive without nulls):

* `root`:   1
  /
  2

* `sub`:  1

  2

Both have preorder values `1,2`, but **structures differ**, so it’s **not** a subtree.

To fix this, we serialize including:

* **node value tokens**
* **explicit null tokens**
* **separators** so values don’t merge (e.g., `12` vs `1` + `2`)

---

## Serialization format (safe)

Use **preorder** with:

* `,` separator
* `#` for null
* prefix value with `v` or keep separators robust

Example token stream:

* Node: `,v3`
* Null: `,#`

So a tree becomes something like:
`,v3,v4,#,#,v5,#,#`

That string uniquely represents structure + values.


## Thorough example walkthrough

### Example

`root`:

```
      3
     / \
    4   5
   / \
  1   2
```

`subRoot`:

```
    4
   / \
  1   2
```

### Step 1: Serialize `subRoot` (preorder with null markers)

Preorder visits: `4, 1, null, null, 2, null, null`

With tokens:

* visit 4 → `,v4`
* visit 1 → `,v1`
* 1.left null → `,#`
* 1.right null → `,#`
* visit 2 → `,v2`
* 2.left null → `,#`
* 2.right null → `,#`

So:

```
sSub = ",v4,v1,#,#,v2,#,#"
```

### Step 2: Serialize `root`

Preorder visits: `3, 4, 1, null, null, 2, null, null, 5, null, null`

So:

```
sRoot = ",v3,v4,v1,#,#,v2,#,#,v5,#,#"
```

### Step 3: Substring check

Now you can literally see:

```
sRoot = ",v3, [ ,v4,v1,#,#,v2,#,# ] ,v5,#,#"
               ^^^^^^^^^^^^^^^^^^^^
sSub  =      ",v4,v1,#,#,v2,#,#"
```

Because `sSub` appears contiguously inside `sRoot`, `subRoot` is a subtree.

---

## Why KMP here (instead of `contains`)?

* `String.contains()` typically uses efficient algorithms internally in many runtimes, but it’s not guaranteed and can degrade.
* KMP guarantees **O(n + m)** worst-case.

**Time complexity**:

* Serialization: `O(N)` for root, `O(M)` for subRoot
* KMP: `O(|sRoot| + |sSub|)` which is `O(N + M)` in total size

**Space**:

* Serialized strings: `O(N + M)`
* LPS array: `O(M)`

---

## Common pitfalls this avoids

1. **No null markers** → false positives
2. **No separators** (e.g., `12` vs `1,2`) → false matches
3. **Only inorder traversal** → ambiguous structure

---

If you want, I can also show a **rolling-hash (Rabin–Karp)** version (often simpler than KMP) with collision handling, but KMP is the “clean deterministic” string-matching solution.
*/

// class Solution {

//     public boolean isSubtree(TreeNode root, TreeNode subRoot) {
//         // By common definition: empty tree is always a subtree
//         if (subRoot == null) return true;
//         if (root == null) return false;

//         String sRoot = serializePreorder(root);
//         String sSub  = serializePreorder(subRoot);

//         // Use KMP to find sSub in sRoot in O(n + m)
//         return kmpContains(sRoot, sSub);
//     }

//     // Preorder serialization with null markers and separators
//     private String serializePreorder(TreeNode node) {
//         StringBuilder sb = new StringBuilder();
//         build(node, sb);
//         return sb.toString();
//     }

//     private void build(TreeNode node, StringBuilder sb) {
//         if (node == null) {
//             sb.append(",#");          // null marker
//             return;
//         }
//         sb.append(",v").append(node.val); // value token, prefixed to avoid ambiguity
//         build(node.left, sb);
//         build(node.right, sb);
//     }

//     // KMP substring search: returns true if pattern appears in text
//     private boolean kmpContains(String text, String pattern) {
//         int n = text.length(), m = pattern.length();
//         if (m == 0) return true;

//         int[] lps = buildLPS(pattern);

//         int i = 0; // index in text
//         int j = 0; // index in pattern
//         while (i < n) {
//             if (text.charAt(i) == pattern.charAt(j)) {
//                 i++;
//                 j++;
//                 if (j == m) return true; // found full pattern
//             } else {
//                 if (j > 0) {
//                     j = lps[j - 1];
//                 } else {
//                     i++;
//                 }
//             }
//         }
//         return false;
//     }

//     // LPS = longest proper prefix which is also suffix for each prefix of pattern
//     private int[] buildLPS(String pattern) {
//         int m = pattern.length();
//         int[] lps = new int[m];

//         int len = 0; // length of current longest prefix-suffix
//         int i = 1;

//         while (i < m) {
//             if (pattern.charAt(i) == pattern.charAt(len)) {
//                 len++;
//                 lps[i] = len;
//                 i++;
//             } else {
//                 if (len > 0) {
//                     len = lps[len - 1];
//                 } else {
//                     lps[i] = 0;
//                     i++;
//                 }
//             }
//         }
//         return lps;
//     }
// }







// NOTE: This question can also be solved using hashing
// Method 4: Hash Function approach (generalizable for trees with more than 2 children)
/**/