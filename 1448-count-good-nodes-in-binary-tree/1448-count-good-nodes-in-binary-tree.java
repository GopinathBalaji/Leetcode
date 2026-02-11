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


// Method 1: Recursive DFS approach
/*
# WHAT WAS I DOING WRONG:
Your idea (carry a path max) is right, but the implementation is wrong because you’re **double-counting** by mixing a “global-ish” accumulator (`good`) with recursion returns.

### What’s wrong specifically

1. **You pass `good` down into recursion and also add recursion results back into `good`.**
   That means counts from upper levels get re-added multiple times.

Example: if `good` is already 3 at some node, you call:

```java
good += dfs(child, ..., good);
```

But `dfs(child, ..., good)` returns a value that already *includes* that starting `good` (because you passed it in), so you’re effectively adding `good` again.

2. **Your return values are inconsistent**

* Base case returns `0`
* Non-base returns `good` (which includes prior accumulated values)
  So the meaning of “what dfs returns” changes depending on the path.

3. Minor: you compute next `currMax` using `node.left.val` / `node.right.val`. It’s not wrong, but it’s cleaner and less error-prone to use `node.val`:

```java
nextMax = Math.max(currMax, node.val)
```

That’s “max on the path including current node,” which is what the definition wants.

---

## How to fix the pattern

Use **one** of these two styles:

### Style A (recommended): `dfs` returns “good nodes in this subtree”

No `good` parameter at all.

```java
private int dfs(TreeNode node, int currMax) {
    if (node == null) return 0;

    int good = (node.val >= currMax) ? 1 : 0;
    int nextMax = Math.max(currMax, node.val);

    good += dfs(node.left, nextMax);
    good += dfs(node.right, nextMax);
    return good;
}
```

### Style B: pass an accumulator by reference (global field) and return void

(Works too, but Style A is simpler.)

#####################################################

## Problem reminder: what is a “good node”?

A node `X` is **good** if, on the path from the **root** to `X` (including `X`), there is **no node with a value greater than `X.val`**.

Equivalently:

* Let `maxSoFar` = maximum value seen from root down to the parent of `X`.
* Then `X` is good iff `X.val >= maxSoFar`.

So the whole problem is: while you traverse, keep track of the **maximum value on the current root→node path**.

---

## The key idea of the approach (Style A)

We define:

> `dfs(node, currMax)` returns the **number of good nodes inside the subtree rooted at `node`**, given that `currMax` is the **maximum value on the path from the original root down to node’s parent**.

That “meaning” is crucial. Every recursive call returns a clean count for its subtree; we just add them up.

## Why this works (in plain English)

At each node:

1. Decide if **this node** is good by comparing its value to the best (max) seen so far on its path.
2. Update the max for children: `nextMax = max(currMax, node.val)`.
3. Recursively compute how many good nodes are in the left subtree and right subtree using that updated max.
4. Return:

   ```
   (good? 1 : 0) + leftGoodCount + rightGoodCount
   ```

No global variables, no passing an accumulator that might get double-counted—each call’s return value has a clear meaning.

---

## Thorough example walkthrough

Let’s use the classic example:

```
        3
       / \
      1   4
     /   / \
    3   1   5
```

**Correct answer:** 4 good nodes (3, 3, 4, 5)

### Step 0: Start

Call:

* `dfs(3, 3)` because `root.val = 3`

Interpretation: we are at node 3, and the maximum on path to its parent is 3 (root case).

---

### At node 3 with currMax=3

* Is 3 >= 3? yes → `good = 1`
* `nextMax = max(3, 3) = 3`

Now compute:

* left: `dfs(1, 3)`
* right: `dfs(4, 3)`

So:

```
dfs(3,3) = 1 + dfs(1,3) + dfs(4,3)
```

---

### Left subtree: node 1 with currMax=3

Call: `dfs(1, 3)`

* Is 1 >= 3? no → `good = 0`
* `nextMax = max(3, 1) = 3`

Children:

* left: `dfs(3, 3)`
* right: `dfs(null, 3)` → 0

So:

```
dfs(1,3) = 0 + dfs(3,3) + 0
```

#### Node 3 (left-left) with currMax=3

Call: `dfs(3, 3)`

* Is 3 >= 3? yes → `good = 1`
* `nextMax = max(3, 3) = 3`
* left null → 0
* right null → 0

So:

```
dfs(3,3) = 1
```

Therefore:

```
dfs(1,3) = 0 + 1 + 0 = 1
```

So the entire left subtree contributes **1 good node** (that node with value 3).

---

### Right subtree: node 4 with currMax=3

Call: `dfs(4, 3)`

* Is 4 >= 3? yes → `good = 1`
* `nextMax = max(3, 4) = 4`

Children:

* left: `dfs(1, 4)`
* right: `dfs(5, 4)`

So:

```
dfs(4,3) = 1 + dfs(1,4) + dfs(5,4)
```

#### Node 1 (right-left) with currMax=4

Call: `dfs(1, 4)`

* Is 1 >= 4? no → `good = 0`
* `nextMax = max(4, 1) = 4`
* both children null → returns 0

So:

```
dfs(1,4) = 0
```

#### Node 5 (right-right) with currMax=4

Call: `dfs(5, 4)`

* Is 5 >= 4? yes → `good = 1`
* `nextMax = max(4, 5) = 5`
* both children null → 0

So:

```
dfs(5,4) = 1
```

Therefore:

```
dfs(4,3) = 1 + 0 + 1 = 2
```

So the right subtree contributes **2 good nodes** (4 and 5).

---

### Combine at the root

We had:

```
dfs(3,3) = 1 + dfs(1,3) + dfs(4,3)
         = 1 + 1 + 2
         = 4
```

✅ Final answer: **4**

---

## What to notice in the walkthrough

* `currMax` changes only when you see a larger value on the path.
* Each `dfs` call returns a count for **its own subtree only**.
* We never pass a running “good” total downward, so we never double-add it.

---

## Complexity

* **Time:** `O(n)` (visit each node once)
* **Space:** `O(h)` recursion stack (`h` = tree height; worst-case `O(n)` for a skewed tree)
*/
class Solution {
    public int goodNodes(TreeNode root) {
        if(root == null){
            return 0;
        }

        return dfs(root, root.val);
    }

    private int dfs(TreeNode node, int currMax){
        if(node == null){
            return 0;
        }

        int good = (node.val >= currMax) ? 1 : 0;

        good += dfs(node.left, Math.max(currMax, node.val));
        good += dfs(node.right, Math.max(currMax, node.val));

        return good;
    }
}






// Method 2: Iterative DFS
/*
## Key idea (same as recursive)

A node is **good** if its value is **≥ the maximum value seen so far** on the path from the root to that node.

So in an iterative traversal, every stack entry should carry:

* the **node**
* the **maxSoFar** on the path *to that node*



### Why push right before left?

Not required. It just makes the traversal order resemble recursive preorder (root-left-right) when using a stack (LIFO). The count doesn’t depend on order.

---

## Detailed explanation of what happens

Each loop iteration processes exactly one node:

1. Pop `(node, maxSoFar)` from stack.
2. Determine if `node` is good using `node.val >= maxSoFar`.
3. Compute `nextMax = max(maxSoFar, node.val)` — this is the best max for the node’s children.
4. Push each child as `(child, nextMax)`.

This guarantees: when you later pop a child, the `maxSoFar` you see is exactly the maximum value along the path from root to that child.

---

## Thorough example walkthrough

Use the same tree:

```
        3
       / \
      1   4
     /   / \
    3   1   5
```

Expected answer: **4** good nodes (3, 3, 4, 5)

### Initialization

* `good = 0`
* stack = `[(3,3)]`

I’ll write stack top on the left.

---

### Step 1: pop (3,3)

* node=3, maxSoFar=3
* check: `3 >= 3` ✅ → good=1
* nextMax = max(3,3)=3
* push children:

  * push (4,3)
  * push (1,3)

stack = `[(1,3), (4,3)]`

---

### Step 2: pop (1,3)

* node=1, maxSoFar=3
* check: `1 >= 3` ❌ → good stays 1
* nextMax = max(3,1)=3
* push children:

  * left is 3 → push (3,3)
  * right is null

stack = `[(3,3), (4,3)]`

---

### Step 3: pop (3,3)  (this is the left-left 3)

* node=3, maxSoFar=3
* check: `3 >= 3` ✅ → good=2
* nextMax = max(3,3)=3
* children null → push nothing

stack = `[(4,3)]`

---

### Step 4: pop (4,3)

* node=4, maxSoFar=3
* check: `4 >= 3` ✅ → good=3
* nextMax = max(3,4)=4
* push children:

  * push (5,4)
  * push (1,4)

stack = `[(1,4), (5,4)]`

---

### Step 5: pop (1,4)  (right-left 1)

* node=1, maxSoFar=4
* check: `1 >= 4` ❌ → good=3
* nextMax = max(4,1)=4
* children null → push nothing

stack = `[(5,4)]`

---

### Step 6: pop (5,4)

* node=5, maxSoFar=4
* check: `5 >= 4` ✅ → good=4
* nextMax = max(4,5)=5
* children null

stack = `[]` (done)

✅ return **4**

---

## Common iterative bug to avoid

Don’t update `maxSoFar` using the child’s value directly like:

```java
Math.max(maxSoFar, child.val)
```

It can still work, but conceptually the clean rule is:

* compute `nextMax` based on the current node (`maxSoFar` vs `node.val`)
* pass that same `nextMax` to both children

That exactly matches the “max on the path up to this node” definition.
*/
// class Solution {
//     private static class Pair {
//         TreeNode node;
//         int maxSoFar;
//         Pair(TreeNode node, int maxSoFar) {
//             this.node = node;
//             this.maxSoFar = maxSoFar;
//         }
//     }

//     public int goodNodes(TreeNode root) {
//         if (root == null) return 0;

//         int good = 0;
//         Deque<Pair> stack = new ArrayDeque<>();
//         stack.push(new Pair(root, root.val));  // root is always good

//         while (!stack.isEmpty()) {
//             Pair cur = stack.pop();
//             TreeNode node = cur.node;
//             int maxSoFar = cur.maxSoFar;

//             // Check if current node is good
//             if (node.val >= maxSoFar) {
//                 good++;
//             }

//             // Update max for children paths
//             int nextMax = Math.max(maxSoFar, node.val);

//             // Push children with the updated path max
//             if (node.right != null) stack.push(new Pair(node.right, nextMax));
//             if (node.left  != null) stack.push(new Pair(node.left,  nextMax));
//         }

//         return good;
//     }
// }








// Method 3: BFS Approach
/*
## Key idea (same condition, different traversal)

A node is **good** if its value is **≥ the maximum value on the path from the root to that node**.

BFS visits nodes level by level, but the “goodness” still depends on the **path**, not the level.
So in BFS, each queue entry must store:

* the **node**
* the **maxSoFar** along the path to that node


## Why we carry `maxSoFar` in the queue

In BFS, when you reach a node, you no longer have the recursion stack to remember the path.
So you “attach” the path information (max so far) to the node in the queue.

That ensures when you process a node later, you still know the correct max along **its specific root→node path**.

---

## Thorough example walkthrough

Tree:

```
        3
       / \
      1   4
     /   / \
    3   1   5
```

Good nodes should be: `3 (root), 3 (left-left), 4, 5` → answer = **4**

### Initialization

* `good = 0`
* Queue = `[(3, max=3)]`

I’ll show the queue from front → back.

---

### Step 1: Dequeue (3,3)

* node = 3, maxSoFar = 3
* check: `3 >= 3` ✅ → `good = 1`
* nextMax = max(3,3) = 3
* enqueue children:

  * (1,3)
  * (4,3)

Queue: `[(1,3), (4,3)]`

---

### Step 2: Dequeue (1,3)

* node = 1, maxSoFar = 3
* check: `1 >= 3` ❌ → `good = 1`
* nextMax = max(3,1) = 3
* enqueue children:

  * left child is 3 → (3,3)
  * right child null

Queue: `[(4,3), (3,3)]`

---

### Step 3: Dequeue (4,3)

* node = 4, maxSoFar = 3
* check: `4 >= 3` ✅ → `good = 2`
* nextMax = max(3,4) = 4
* enqueue children:

  * (1,4)
  * (5,4)

Queue: `[(3,3), (1,4), (5,4)]`

---

### Step 4: Dequeue (3,3)  (left-left 3)

* node = 3, maxSoFar = 3
* check: `3 >= 3` ✅ → `good = 3`
* nextMax = max(3,3) = 3
* children null → enqueue nothing

Queue: `[(1,4), (5,4)]`

---

### Step 5: Dequeue (1,4)  (right-left 1)

* node = 1, maxSoFar = 4
* check: `1 >= 4` ❌ → `good = 3`
* nextMax = max(4,1) = 4
* children null

Queue: `[(5,4)]`

---

### Step 6: Dequeue (5,4)

* node = 5, maxSoFar = 4
* check: `5 >= 4` ✅ → `good = 4`
* nextMax = max(4,5) = 5
* children null

Queue: `[]` done.

✅ Return **4**

---

## Complexity

* **Time:** `O(n)` (each node enqueued/dequeued once)
* **Space:** `O(w)` where `w` is the max width of the tree (worst-case `O(n)`)

---

## Common BFS mistake

People try to keep a single `maxSoFar` variable globally in BFS. That fails because different nodes at the same level have **different paths**. The fix is exactly what we did: store `maxSoFar` *per node* in the queue.
*/
// class Solution {
//     private static class Pair {
//         TreeNode node;
//         int maxSoFar;
//         Pair(TreeNode node, int maxSoFar) {
//             this.node = node;
//             this.maxSoFar = maxSoFar;
//         }
//     }

//     public int goodNodes(TreeNode root) {
//         if (root == null) return 0;

//         int good = 0;
//         Deque<Pair> q = new ArrayDeque<>();
//         q.offer(new Pair(root, root.val)); // root is always good (max so far is root.val)

//         while (!q.isEmpty()) {
//             Pair cur = q.poll();
//             TreeNode node = cur.node;
//             int maxSoFar = cur.maxSoFar;

//             // Check if current node is good
//             if (node.val >= maxSoFar) {
//                 good++;
//             }

//             // Compute path max for children
//             int nextMax = Math.max(maxSoFar, node.val);

//             // Enqueue children with their path max
//             if (node.left != null)  q.offer(new Pair(node.left, nextMax));
//             if (node.right != null) q.offer(new Pair(node.right, nextMax));
//         }

//         return good;
//     }
// }
