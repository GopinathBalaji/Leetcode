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
## The recursive DFS idea in one sentence

At every node, compute the **height of its left subtree** and **height of its right subtree**; the longest path *through that node* is `leftHeight + rightHeight` (in edges), and the answer is the maximum of that over all nodes.

---

## What “diameter” means here

* The **diameter** is the number of **edges** on the longest path between **any two nodes**.
* The path does **not** have to go through the root.

---

## Key trick: compute heights, update diameter while returning height

You run a DFS that returns **height**.
While returning height, you also update a global `best` for diameter.

### Height convention (important)

We’ll use the common convention:

* `height(null) = 0`
* `height(node) = 1 + max(height(left), height(right))`

This `height` counts **nodes on the longest downward path** from `node` to a leaf.

With this convention, the diameter in **edges** through a node is:

* `leftHeight + rightHeight`

Why? Because:

* if leftHeight is “nodes down to deepest leaf” and rightHeight similarly,
* connecting those two paths through the current node uses exactly `leftHeight + rightHeight` edges.

Sanity check:

* single node: left=0, right=0 → diameter=0 ✅
* root with one child: left=1, right=0 → diameter=1 ✅


## Why this works (the “three cases” logic)

For any node, the best diameter in its subtree is either:

1. entirely in the left subtree
2. entirely in the right subtree
3. goes through the node (deepest left leaf → node → deepest right leaf)

Case (3) is exactly `leftHeight + rightHeight`.
Cases (1) and (2) will be discovered when DFS visits those subtrees and updates `best` there.

So taking the max across all nodes covers all cases.

---

## Thorough walkthrough on a concrete example

Consider this tree:

```
        1
       / \
      2   3
     / \
    4   5
```

Expected diameter path: `4 -> 2 -> 1 -> 3` (or `5 -> 2 -> 1 -> 3`)
That’s **3 edges**.

Let’s walk through the recursion **exactly**.

### Step-by-step DFS (postorder)

The DFS processes children first, then the node.

#### Call: `dfsHeight(1)`

To compute height(1), we must compute height(2) and height(3).

---

### Compute left subtree: `dfsHeight(2)`

To compute height(2), compute height(4) and height(5).

#### `dfsHeight(4)`

* `dfsHeight(null)` → 0 (left)
* `dfsHeight(null)` → 0 (right)
* left=0, right=0
* update `best = max(best, 0+0) = 0`
* return height(4) = `1 + max(0,0) = 1`

So: **height(4)=1**, best=0

#### `dfsHeight(5)`

Same logic as 4:

* left=0, right=0
* best stays 0
* return height(5)=1

So: **height(5)=1**, best=0

Now back at node 2:

* left = height(4)=1
* right = height(5)=1
* update diameter through 2: `left + right = 1 + 1 = 2`
* `best = max(0, 2) = 2`
* return height(2) = `1 + max(1,1) = 2`

So: **height(2)=2**, best=2

---

### Compute right subtree: `dfsHeight(3)`

Node 3 is a leaf:

* left=0, right=0
* best = max(2,0)=2
* return height(3)=1

So: **height(3)=1**, best=2

---

### Back at root: `dfsHeight(1)`

Now we have:

* left = height(2)=2
* right = height(3)=1

Update diameter through 1:

* `left + right = 2 + 1 = 3`
* `best = max(2,3)=3`

Return height(1):

* `1 + max(2,1)=3`

DFS finishes. Final answer: **best = 3**

✅ Diameter = **3 edges**, matches expectation.

---

## What `best` represented at each key node

* At node 4: best considered path through 4 → 0
* At node 2: best considered path 4–2–5 → 2 edges
* At node 1: best considered path (deepest in left) – 1 – (deepest in right) → 3 edges

---

## Complexity

* **Time:** `O(n)` each node visited once
* **Space:** `O(h)` recursion stack (h = height of tree)

  * worst case skewed tree: `O(n)`
  * balanced: `O(log n)`
*/
class Solution {

    int best = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        height(root);
        return best;
    }

    private int height(TreeNode root){
        if(root == null){
            return 0;
        }

        int leftHeight = height(root.left);
        int rightHeight = height(root.right);

        int currHeight = 1 + Math.max(leftHeight, rightHeight);
        best = Math.max(best,  leftHeight + rightHeight);

        return currHeight;
    }
}







// Method 2: Iterative DFS solution
/*
## What this iterative DFS is trying to replicate

The recursive solution works because it naturally does **postorder**:

1. compute left height
2. compute right height
3. use them to update diameter at the current node
4. return current node height

Iteratively, we must **force** that same “children first, then node” order.

---

## The two key pieces

### 1) `Frame(node, visited)`

Each node is pushed twice conceptually:

* `visited = false` means: “first time I see this node — schedule its children first”
* `visited = true` means: “children are done — now compute this node’s height and diameter contribution”

That mimics recursion: the second time is like the moment you “return” from recursive calls.

### 2) `height` map

`height.get(node)` stores the computed height of that node’s subtree using this convention:

* `height(null) = 0`
* `height(node) = 1 + max(height(left), height(right))`

So a leaf has height `1`.

---

## Why `best = max(best, hl + hr)` gives diameter in **edges**

At a node:

* `hl` is the number of nodes on the longest downward path into the left subtree
* `hr` is the number of nodes on the longest downward path into the right subtree

The longest path that goes **through** this node is:

* deepest-left-leaf → … → left-child → node → right-child → … → deepest-right-leaf

The number of **edges** on that path is exactly `hl + hr`.

Quick sanity:

* single node: `hl=0, hr=0` → `best=0` ✅
* root with one child leaf: `hl=1, hr=0` → `best=1` ✅


## Thorough walkthrough (stack + height + best)

Use this example tree:

```
        1
       / \
      2   3
     / \
    4   5
```

Expected diameter is `3` edges (path `4 → 2 → 1 → 3` or `5 → 2 → 1 → 3`).

### Notation

* `Nf` means `(N, visited=false)`
* `Nt` means `(N, visited=true)`
* Stack shown **top first** (leftmost is top)

---

### Start

* stack: `[1f]`
* height: `{}`
* best: `0`

---

### Step 1: pop `1f` (expand 1)

Push `1t`, then `3f`, `2f` (right first, then left so left is processed first)

* stack: `[2f, 3f, 1t]`
* height: `{}`
* best: `0`

---

### Step 2: pop `2f` (expand 2)

Push `2t`, then `5f`, `4f`

* stack: `[4f, 5f, 2t, 3f, 1t]`
* height: `{}`
* best: `0`

---

### Step 3: pop `4f` (expand 4)

Node 4 is a leaf. Push `4t` (no children)

* stack: `[4t, 5f, 2t, 3f, 1t]`
* height: `{}`
* best: `0`

---

### Step 4: pop `4t` (process 4)

Children are null:

* `hl = height(null)=0`
* `hr = height(null)=0`

Update:

* `best = max(0, 0+0)=0`

* `height(4)=1+max(0,0)=1`

* stack: `[5f, 2t, 3f, 1t]`

* height: `{4=1}`

* best: `0`

---

### Step 5: pop `5f` (expand 5)

Leaf → push `5t`

* stack: `[5t, 2t, 3f, 1t]`
* height: `{4=1}`
* best: `0`

---

### Step 6: pop `5t` (process 5)

* `hl=0`, `hr=0`

* `best = max(0,0)=0`

* `height(5)=1`

* stack: `[2t, 3f, 1t]`

* height: `{4=1, 5=1}`

* best: `0`

---

### Step 7: pop `2t` (process 2)

Now children heights are known:

* `hl = height(4)=1`
* `hr = height(5)=1`

Update:

* diameter through 2 = `hl+hr = 2`

* `best = max(0,2)=2`

* `height(2)=1+max(1,1)=2`

* stack: `[3f, 1t]`

* height: `{4=1, 5=1, 2=2}`

* best: `2`

---

### Step 8: pop `3f` (expand 3)

Leaf → push `3t`

* stack: `[3t, 1t]`
* height: `{4=1, 5=1, 2=2}`
* best: `2`

---

### Step 9: pop `3t` (process 3)

* `hl=0`, `hr=0`

* `best` stays `2`

* `height(3)=1`

* stack: `[1t]`

* height: `{4=1, 5=1, 2=2, 3=1}`

* best: `2`

---

### Step 10: pop `1t` (process 1)

Children heights:

* `hl = height(2)=2`
* `hr = height(3)=1`

Update:

* diameter through 1 = `2+1=3`

* `best = max(2,3)=3`

* `height(1)=1+max(2,1)=3`

* stack: `[]`

* height: `{4=1, 5=1, 2=2, 3=1, 1=3}`

* best: `3`

✅ Return `best = 3`

---

## Why the “visited flag” is essential

If you process a node the first time you pop it, you **don’t yet know** its children’s heights (because they haven’t been computed). The second pop (`visited=true`) guarantees children have already been processed and stored in the map.

---

## Complexity

* Time: `O(n)` (each node is pushed/popped a constant number of times)
* Space: `O(n)` worst-case (stack + height map)
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

//     public int diameterOfBinaryTree(TreeNode root) {
//         if (root == null) return 0;

//         int best = 0;

//         // height(node) = number of nodes on longest downward path from node
//         // We'll use height(null) = 0.
//         Map<TreeNode, Integer> height = new HashMap<>();

//         Deque<Frame> stack = new ArrayDeque<>();
//         stack.push(new Frame(root, false));

//         while (!stack.isEmpty()) {
//             Frame cur = stack.pop();
//             TreeNode node = cur.node;

//             if (node == null) continue;

//             if (!cur.visited) {
//                 // Postorder simulation:
//                 // push node back as "visited" so we process it after its children
//                 stack.push(new Frame(node, true));

//                 // children go in first (unvisited)
//                 if (node.right != null) stack.push(new Frame(node.right, false));
//                 if (node.left != null)  stack.push(new Frame(node.left, false));
//             } else {
//                 // children heights are ready now
//                 int hl = height.getOrDefault(node.left, 0);
//                 int hr = height.getOrDefault(node.right, 0);

//                 // diameter through this node (in edges) with this height convention:
//                 // hl + hr
//                 best = Math.max(best, hl + hr);

//                 // store this node's height (in nodes)
//                 height.put(node, 1 + Math.max(hl, hr));
//             }
//         }

//         return best;
//     }
// }