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
## What the function is supposed to return

`maxDepth(root)` returns:

* `0` if the tree is empty (`root == null`)
* otherwise, the number of nodes along the **longest path** from `root` down to any leaf

So a single node tree has depth **1**.

---

## The recursive DFS idea (what recursion is doing for you)

When you call:

```java
maxDepth(root)
```

you are asking:

> “How deep is the tree whose root is `root`?”

To answer that, you break it into smaller subproblems:

* How deep is the **left subtree**?
* How deep is the **right subtree**?

Then the depth at `root` is:

> 1 (for the root itself) + max(leftDepth, rightDepth)

This is exactly what your code does.

---

## Line-by-line explanation of your code

```java
class Solution {
    public int maxDepth(TreeNode root) {
        if(root == null){
            return 0;
        }
```

### ✅ Base case

If `root` is `null`, there is no tree, so depth is **0**.

This base case is crucial because recursion must eventually stop.
Leaf nodes have `left == null` and `right == null`, so recursion will hit this condition.

---

```java
        int left = maxDepth(root.left);
        int right = maxDepth(root.right);
```

### ✅ Recursive calls

You compute:

* `left` = max depth of the left subtree
* `right` = max depth of the right subtree

Each call again follows the same rules:

* if that subtree root is null → return 0
* otherwise compute depths of its children, etc.

---

```java
        return Math.max(left + 1, right + 1);
    }
}
```

### ✅ Combine results

The depth of the current tree = **1 + max(leftDepth, rightDepth)**.

You wrote it as:

* `Math.max(left + 1, right + 1)`

Equivalent to:

* `1 + Math.max(left, right)`

Both are correct.

---

## Intuition: why “+1”?

Because `left` and `right` represent the depth **below** the current node.
To include the current node itself in the count, you add **1**.

---

## Thorough example walkthrough

Consider this tree:

```
        1
       / \
      2   3
     /
    4
```

* The longest root-to-leaf path is `1 -> 2 -> 4`
* Depth should be **3**

Let’s walk through the exact calls and returns.

---

### Step 1: Call on root

`maxDepth(1)`

`root != null`, so compute:

* `left = maxDepth(2)`
* `right = maxDepth(3)`

---

### Step 2: Compute `maxDepth(2)`

`maxDepth(2)`

Again not null, compute:

* `left = maxDepth(4)`
* `right = maxDepth(null)` (because 2’s right is missing)

---

### Step 3: Compute `maxDepth(4)`

`maxDepth(4)`

Not null, compute:

* `left = maxDepth(null)`
* `right = maxDepth(null)` (4 is a leaf)

Now both of these hit the base case:

* `maxDepth(null) = 0`
* `maxDepth(null) = 0`

So at node 4:

* `left = 0`, `right = 0`
* return `max(0+1, 0+1) = 1`

✅ `maxDepth(4) = 1`

---

### Step 4: Back to node 2

We had:

* `left = maxDepth(4) = 1`
* `right = maxDepth(null) = 0`

So at node 2:

* return `max(1+1, 0+1) = max(2, 1) = 2`

✅ `maxDepth(2) = 2`

---

### Step 5: Compute `maxDepth(3)`

`maxDepth(3)`

Node 3 is a leaf (both children null):

* `left = maxDepth(null) = 0`
* `right = maxDepth(null) = 0`

Return:

* `max(0+1, 0+1) = 1`

✅ `maxDepth(3) = 1`

---

### Step 6: Back to root node 1

We had:

* `left = maxDepth(2) = 2`
* `right = maxDepth(3) = 1`

So at node 1:

* return `max(2+1, 1+1) = max(3, 2) = 3`

✅ `maxDepth(1) = 3`

---

## Final answer returned: **3**

That matches the longest path `1 -> 2 -> 4`.

---

## What recursion “stack frames” look like (mental model)

Each node waits for its children’s answers before it can return:

* Node 1 waits for node 2 and node 3
* Node 2 waits for node 4 and null
* Node 4 waits for null and null

Then results return upward (“bottom-up”).

---

## Complexity

* **Time:** `O(n)` because you visit each node once
* **Space:** `O(h)` recursion stack, where `h` is tree height

  * worst case skewed tree: `O(n)`
  * balanced tree: `O(log n)`
*/
class Solution {
    public int maxDepth(TreeNode root) {
        if(root == null){
            return 0;
        }

        int left = maxDepth(root.left);
        int right = maxDepth(root.right);

        return 1 + Math.max(left, right);
    }
}






// Method 2: Iterative DFS
/*
*/
// class Solution {
//     private static class Pair{
//         TreeNode node;
//         int depth;
//         Pair(TreeNode node, int depth){
//             this.node = node;
//             this.depth = depth;
//         }
//     }

//     public int maxDepth(TreeNode root) {
//         if(root == null){
//             return 0;
//         }

//         int ans = 0;
//         Deque<Pair> stack = new ArrayDeque<>();
//         stack.addLast(new Pair(root, 1));

//         while(!stack.isEmpty()){
//             Pair curr = stack.pollLast();
//             TreeNode node = curr.node;
//             int depth = curr.depth;

//             ans = Math.max(ans, depth);

//             if(node.left != null){
//                 stack.addLast(new Pair(node.left, depth + 1));
//             }
//             if(node.right != null){
//                 stack.addLast(new Pair(node.right, depth + 1));
//             }
//         }

//         return ans;
//     }
// }







// Method 3: BFS approach
/*
*/
// class Solution {

//     public int maxDepth(TreeNode root) {
//         if(root == null){
//             return 0;
//         }

//         int depth = 0;
//         Deque<TreeNode> queue = new ArrayDeque<>();
//         queue.addLast(root);

//         while(!queue.isEmpty()){
//             int levelSize = queue.size();

//             for(int i=0; i<levelSize; i++){
//                 TreeNode node = queue.pollFirst();
//                 if(node.left != null){
//                     queue.addLast(node.left);
//                 }
//                 if(node.right != null){
//                     queue.addLast(node.right);
//                 }
//             }

//             depth++;
//         }

//         return depth;
//     }
// }