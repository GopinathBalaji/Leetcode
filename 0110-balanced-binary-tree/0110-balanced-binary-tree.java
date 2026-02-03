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


// Method 1: My recursive DFS approach (Not optimal O(n^2))
/*
# WHAT WAS I DOING WRONG:

Two things are wrong (one is a **correctness bug**, the other is a **performance bug**).

---

## 1) **Correctness bug:** you compute `left` and `right`… then ignore them

You do:

```java
boolean left = isBalanced(root.left);
boolean right = isBalanced(root.right);
...
return (Math.abs(hL - hR) <= 1);
```

This returns `true` whenever the **current node’s height difference** is ≤ 1, **even if** a subtree is already unbalanced.

So you must include `left && right` in the return condition:

```java
return left && right && (Math.abs(hL - hR) <= 1);
```

### Simple counterexample

A tree where the root’s left/right heights differ by ≤ 1, but the left subtree is internally unbalanced.

Example:

```
      1
     / \
    2   3
   /
  4
 /
5
```

At root `1`: left height = 4? (depending), right height = 1, might already differ >1, but you can craft variants where root diff is ≤1 while imbalance is deeper. The key point: balance must hold **at every node**, not just the root.

---

## 2) **Performance bug:** `height()` is recomputed too many times → **O(n²)**

For each node, you call `height(root.left)` and `height(root.right)`, and each `height()` walks the subtree again.

In a skewed tree (like a linked list), this becomes:

* height at root scans ~n nodes
* height at next scans ~n-1
* ...
  Total ~n².

So this approach will likely TLE on worst cases.
*/
class Solution {
    public boolean isBalanced(TreeNode root) {
        if(root == null){
            return true;
        }

        boolean left = isBalanced(root.left);
        boolean right = isBalanced(root.right);

        int hL = height(root.left);
        int hR = height(root.right);

        return left && right && (Math.abs(hL - hR) <= 1);
    }

    private int height(TreeNode node){
        if(node == null){
            return 0;
        }

        int hL = height(node.left);
        int hR = height(node.right);

        return 1 + Math.max(hL, hR);
    }
}




// Method 1.5: Optimal DFS Approach
/*
Why this works:
Each node’s height is computed once
The moment a subtree is unbalanced, you bubble up -1 and stop extra work

Time: O(n)
Space: O(h) recursion stack
*/
// class Solution {
//     public boolean isBalanced(TreeNode root) {
//         return dfs(root) != -1;
//     }

// // returns height if balanced, else -1
//     private int dfs(TreeNode node){
//         if(node == null){
//             return 0;
//         }

//         int hL = dfs(node.left);
//         if(hL == -1){
//             return -1;
//         }

//         int hR = dfs(node.right);
//         if(hR == -1){
//             return -1;
//         }

//         if(Math.abs(hL - hR) > 1){
//             return -1;
//         }

//         return 1 + Math.max(hL, hR);
//     }
// }






// Method 2: 
/*
I’ll give you a clean iterative postorder DFS solution (no recursion), explain the key invariant and data structures, then walk through a concrete tree step-by-step showing the stack states and how imbalance is detected.


Here’s a solid **iterative DFS (postorder)** solution for **LeetCode 110: Balanced Binary Tree**, plus a **detailed explanation** and a **thorough walkthrough**.

---

## Key idea (iterative version)

Balanced means: **for every node**,
`abs(height(left) - height(right)) <= 1`.

Just like the recursive O(n) solution, we want to compute heights **bottom-up** (postorder). Iteratively, that means:

1. Traverse in **postorder** (children before node)
2. When processing a node, both children heights are already known
3. If imbalance is found, return `false` immediately

We’ll store computed heights in a `Map<TreeNode, Integer>`:

* `height(null) = 0`
* `height(node) = 1 + max(height(left), height(right))`



### Why the “visited” flag matters

* First time you see a node (`visited=false`): you **don’t** know its children’s heights yet → so you push it back as `visited=true` and process children first.
* Second time (`visited=true`): children have been processed, so their heights are ready in the map → now you can check balance and compute the node’s height.

This is exactly what recursion does naturally.

---

## Thorough walkthrough (step-by-step)

Let’s walk through an **unbalanced** example so you see early detection:

```
        1
       /
      2
     /
    3
```

This is unbalanced at node `1` (left height 2 vs right height 0 → diff 2).

### Notation

* `1f` = `(node 1, visited=false)`
* `1t` = `(node 1, visited=true)`
* Stack shown as **top first** (leftmost is top)

Start:

* stack: `[1f]`
* height: `{}`

---

### Step 1: pop `1f` (expand 1)

Push `1t`, then its child `2f`.

* stack: `[2f, 1t]`
* height: `{}`

---

### Step 2: pop `2f` (expand 2)

Push `2t`, then its child `3f`.

* stack: `[3f, 2t, 1t]`
* height: `{}`

---

### Step 3: pop `3f` (expand 3)

Push `3t`. (3 is a leaf, no children)

* stack: `[3t, 2t, 1t]`
* height: `{}`

---

### Step 4: pop `3t` (process 3)

Children are null:

* `hl = 0`, `hr = 0`, diff = 0 ✅ balanced

* height(3) = `1 + max(0,0) = 1`

* stack: `[2t, 1t]`

* height: `{3=1}`

---

### Step 5: pop `2t` (process 2)

Children:

* left = 3 → `hl = height(3) = 1`

* right = null → `hr = 0`

* diff = 1 ✅ balanced

* height(2) = `1 + max(1,0) = 2`

* stack: `[1t]`

* height: `{3=1, 2=2}`

---

### Step 6: pop `1t` (process 1)

Children:

* left = 2 → `hl = 2`
* right = null → `hr = 0`
* diff = 2 ❌ **unbalanced**

Return `false` immediately.

✅ Correct outcome.

---

## Balanced example quick check

If the tree is:

```
      1
     / \
    2   3
```

Then:

* height(2)=1, height(3)=1
* at node 1: diff=0 → balanced
  Return `true`.

---

## Complexity

* **Time:** `O(n)` (each node processed twice: expanded + processed)
* **Space:** `O(n)` for the height map, and `O(h)` stack (worst-case O(n))
*/

// class Solution {
//     private static class Frame {
//         TreeNode node;
//         boolean visited; // false = first time, true = process after children
//         Frame(TreeNode node, boolean visited) {
//             this.node = node;
//             this.visited = visited;
//         }
//     }

//     public boolean isBalanced(TreeNode root) {
//         if (root == null) return true;

//         Map<TreeNode, Integer> height = new HashMap<>();
//         Deque<Frame> stack = new ArrayDeque<>();
//         stack.push(new Frame(root, false));

//         while (!stack.isEmpty()) {
//             Frame cur = stack.pop();
//             TreeNode node = cur.node;

//             if (!cur.visited) {
//                 // Schedule node to be processed after its children (postorder)
//                 stack.push(new Frame(node, true));

//                 // Push children (unvisited) so they get processed first
//                 if (node.right != null) stack.push(new Frame(node.right, false));
//                 if (node.left != null)  stack.push(new Frame(node.left, false));
//             } else {
//                 // Children are done => their heights are known
//                 int hl = height.getOrDefault(node.left, 0);
//                 int hr = height.getOrDefault(node.right, 0);

//                 // Check balance at this node
//                 if (Math.abs(hl - hr) > 1) return false;

//                 // Store this node's height
//                 height.put(node, 1 + Math.max(hl, hr));
//             }
//         }

//         return true;
//     }
// }