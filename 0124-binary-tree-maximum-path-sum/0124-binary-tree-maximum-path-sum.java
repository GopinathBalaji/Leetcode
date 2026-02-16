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


// Method 1: DFS + Dynamic Programming
/*
Postorder DFS while keeping track of 2 different values:
Path sum starting from this node and going down → what you can “return” to the parent for its path.
Path sum passing through this node → possibly including both left and right child paths plus this node’s value.

The idea of **tracking two variables** here comes from separating:

1. **What you can give to your parent** (the *return value*).
2. **What might be the best possible answer if you stop here** (the *local best*).

---

### Why the parent can’t take both branches

Imagine you’re the parent node in the recursion:

* You can only connect to **one** child when you extend a path upward (because a path must be continuous, you can’t “fork” upward).
* That means the value you **return** must be the best *single downward path* from the current node.

So:

```
downPath(node) = node.val + max(0, downPath(left), downPath(right))
```

(`0` if the branch is negative so you just don’t take it.)

---

### Why you still consider both branches for the answer

Even though your **parent** can only take one branch, the **answer** for the entire tree might come from a path that:

* Starts somewhere deep in the left subtree
* Passes **through you**
* Continues down the right subtree

That’s why, at *each node*, you check:

```
candidatePathSum = node.val + max(0, downPath(left)) + max(0, downPath(right))
```

This is the “**local best**” path *if the path goes through this node*.

---

### Why keep `globalMax`

Since the max path can be anywhere (not just from the root), you keep a variable:

```
globalMax = max(globalMax, candidatePathSum)
```

This variable persists across all recursive calls and gets updated whenever you find a better path.

---

### General pattern for future problems

Whenever you see a tree problem where:

* You’re looking for a **global optimum** that may not involve the root.
* You still need to **pass information upward** in recursion.

…it’s a good signal to:

* **Return** something that makes sense for the parent to extend.
* **Track separately** something that might be the best answer if the path/segment stops at the current node.

You’re basically separating **“what’s usable to my parent”** from **“what’s the best local answer”**.


Tree:
    -10
    /  \
   9    20
       /  \
      15   7
```

We’ll track two things for each node:

* **downPath** = max path sum starting at this node and going down (single branch only — this is what we return to the parent).
* **globalMax** = best path sum found anywhere so far (can include both branches).

---

### Step 1 — Start at 15

* Left child: null → downPath = 0
* Right child: null → downPath = 0
* **downPath(15)** = `15 + max(0, 0)` = **15**
* Possible path through 15 = `15 + 0 + 0` = 15
* **globalMax** = max(-∞, 15) → **15**

---

### Step 2 — Node 7

* Left: null → 0
* Right: null → 0
* **downPath(7)** = `7 + max(0, 0)` = **7**
* Path through 7 = 7
* **globalMax** = max(15, 7) → still **15**

---

### Step 3 — Node 20

* Left: **downPath(15)** = 15
* Right: **downPath(7)** = 7
* **downPath(20)** = `20 + max(15, 7)` = **35** (choose the better branch: left)
* Path through 20 = `20 + 15 + 7` = **42**
* **globalMax** = max(15, 42) → **42**

---

### Step 4 — Node 9

* Left: null → 0
* Right: null → 0
* **downPath(9)** = `9 + max(0, 0)` = **9**
* Path through 9 = 9
* **globalMax** = max(42, 9) → still **42**

---

### Step 5 — Node -10 (root)

* Left: **downPath(9)** = 9
* Right: **downPath(20)** = 35
* **downPath(-10)** = `-10 + max(9, 35)` = `-10 + 35` = **25** (take right branch because it’s bigger)
* Path through -10 = `-10 + 9 + 35` = **34**
* **globalMax** = max(42, 34) → **42**

---

**Final answer**: **42**, which is the path `15 → 20 → 7`.

---

If you notice, the key is:

* `downPath` = node’s value + best single branch below it.
* **globalMax** gets updated with `node.val + left + right` because that’s the best possible path that passes through this node.
*/
class Solution {
    
    int globalMax = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        if(root == null){
            return 0;
        }

        dfs(root);

        return globalMax;
    }

    private int dfs(TreeNode node) {
        if (node == null) {
            return 0;
        }

        // Get max downward path from left and right
        int left = Math.max(0, dfs(node.left));   // don't take negative paths
        int right = Math.max(0, dfs(node.right));

        // Check if the path through this node is the best so far
        int candidate = node.val + left + right;
        globalMax = Math.max(globalMax, candidate);

        // Return the best downward path to the parent
        return node.val + Math.max(left, right);
    }
}