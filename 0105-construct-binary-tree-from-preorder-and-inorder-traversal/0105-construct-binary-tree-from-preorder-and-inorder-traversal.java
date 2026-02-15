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

// Method 1: Divide and Conquer approach using HashMap
/*
Use Preorder to get the root for every iteration.
Use Inorder to find the left and right children.

**Key observation**

* **Preorder** visits: `root, left..., right...`
* **Inorder** visits: `left..., root, right...`
  So the **first element of preorder is always the root** of the (sub)tree.

---

## Hint set 1: Recursive “slice by inorder index”

1. Build a **hash map** from value → index in `inorder` for O(1) splits.
2. Keep a pointer `preIdx` into `preorder` (starting at 0).
3. Write a function that builds a subtree given an inorder range `[L..R]`:

   * If `L > R`, return `null`.
   * Take `rootVal = preorder[preIdx++]`.
   * Find `mid = inorderIndex[rootVal]`.
   * Build left subtree from `[L .. mid-1]`.
   * Build right subtree from `[mid+1 .. R]`.
4. Return the root.

**Why left then right?** Because preorder after the root lists **all left subtree nodes first**.

###############################

preorder = [3, 9, 20, 15, 7]
inorder  = [9, 3, 15, 20, 7]
```

---

## **Step 0: Setup**

* `preIndex = 0` (tracks which node to create next from preorder)
* `inorderMap`:

```
9 → 0
3 → 1
15 → 2
20 → 3
7 → 4
```

* Start recursion: `build(preorder, 0, 4)`

  * `0` = start index in inorder
  * `4` = end index in inorder

---

## **Step 1: Create Root**

* `rootVal = preorder[0] = 3`
* `preIndex = 1`
* Inorder index of `3` = `1`

```
Inorder:  [9]  3  [15, 20, 7]
           L  R
```

* Left subtree = inorder\[0…0] → `[9]`
* Right subtree = inorder\[2…4] → `[15,20,7]`

Tree so far:

```
      3
     / \
   ?     ?
```

* Recurse left: `build(preorder, 0, 0)`
* Recurse right: `build(preorder, 2, 4)`

---

## **Step 2: Build Left Subtree (inorder\[0…0])**

* `rootVal = preorder[1] = 9`
* `preIndex = 2`
* Inorder index of `9` = `0`

```
Inorder left subtree: [9]
```

* Left subtree = inorder\[0…-1] → empty → `null`
* Right subtree = inorder\[1…0] → empty → `null`

Tree now:

```
      3
     / \
    9   ?
```

Return node `9` to be `3.left`.

---

## **Step 3: Build Right Subtree (inorder\[2…4])**

* `rootVal = preorder[2] = 20`
* `preIndex = 3`
* Inorder index of `20` = `3`

```
Inorder: [15] 20 [7]
```

* Left subtree = inorder\[2…2] → `[15]`
* Right subtree = inorder\[4…4] → `[7]`

Tree now:

```
      3
     / \
    9   20
        /  \
      ?      ?
```

---

## **Step 4: Build 20’s Left Subtree (inorder\[2…2])**

* `rootVal = preorder[3] = 15`
* `preIndex = 4`
* Inorder index of `15` = `2`

```
Inorder: [15]
```

* Left subtree = inorder\[2…1] → empty → `null`
* Right subtree = inorder\[3…2] → empty → `null`

Tree now:

```
      3
     / \
    9   20
        /  \
      15    ?
```

---

## **Step 5: Build 20’s Right Subtree (inorder\[4…4])**

* `rootVal = preorder[4] = 7`
* `preIndex = 5` (exhausted)
* Inorder index of `7` = `4`

```
Inorder: [7]
```

* Left subtree = inorder\[4…3] → empty
* Right subtree = inorder\[5…4] → empty

Tree now:

```
      3
     / \
    9   20
        /  \
      15    7
```

---

## **Step 6: Combine and Return**

* All recursive calls return up the chain.
* `preIndex` has reached `5` (end of preorder).
* **Final tree:**

```
      3
     / \
    9   20
        /  \
      15    7
```

---

## Key Takeaways from the Walkthrough**

1. **Preorder drives root creation**, while **inorder determines boundaries** of subtrees.
2. `preIndex` increments **once per node**.
3. Each recursion call **splits inorder** into left and right ranges using the root’s index.
4. No array copies are needed; just pass indices.
5. HashMap lookup makes finding the root index **O(1)**.
*/
class Solution {
    private int preIdx = 0;

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        
        Map<Integer, Integer> inorderIndex = new HashMap<>();
        for(int i=0; i<inorder.length; i++){
            inorderIndex.put(inorder[i], i);
        }

        return makeTree(preorder, inorderIndex, 0, inorder.length-1);
    }

    private TreeNode makeTree(int[] preorder, Map<Integer, Integer> inorderIndex, int l, int r){
        if(l > r){
            return null;
        }

        int rootVal = preorder[preIdx++];
        int midIdx = inorderIndex.get(rootVal);

        TreeNode root = new TreeNode(rootVal);
        root.left = makeTree(preorder, inorderIndex, l, midIdx - 1);
        root.right = makeTree(preorder, inorderIndex, midIdx + 1, r);

        return root;
    }
}







// Method 2: Iterative stack approach
/*
## Core idea behind the iterative method

* **Preorder** gives nodes in the order we *create* them: `root, left..., right...`
* **Inorder** tells us when we’ve finished a node’s **left subtree** and should start attaching **right** children.

### Key invariant

We maintain:

* a **stack** of nodes whose subtrees we’re still building
* an `inIdx` pointer into `inorder` that tracks the “next inorder node we should close”

When the stack top equals `inorder[inIdx]`, it means:

* we’ve finished building that node’s left side,
* so we should pop it (and possibly more), and attach the next preorder node as the **right** child of the last popped node.

If it does **not** equal, the next preorder node must be the **left** child of the stack top.


## Thorough example walkthrough

### Example

`preorder = [3, 9, 20, 15, 7]`
`inorder  = [9, 3, 15, 20, 7]`

We will build:

```
      3
     / \
    9  20
       / \
      15  7
```

### Initialization

* `root = 3`
* `stack = [3]` (top on left)
* `inIdx = 0` → `inorder[inIdx] = 9`

---

### preIdx = 1, val = 9

* `stack.peek() = 3`
* Compare `3` vs `inorder[inIdx]=9`

  * `3 != 9` → still building left chain
* Attach `9` as `3.left`
* Push `9`

State:

* Tree: `3.left = 9`
* `stack = [9, 3]`
* `inIdx = 0` (still 9)

---

### preIdx = 2, val = 20

* `stack.peek() = 9`
* Compare `9` vs `inorder[inIdx]=9`

  * `9 == 9` → means left subtree of 9 is done, time to “close” nodes

Pop while matches inorder:

1. pop `9`, `inIdx=1` → `inorder[1]=3`
2. now stack.peek() is `3`, and `3 == inorder[1]`

   * pop `3`, `inIdx=2` → `inorder[2]=15`
     Stop because stack is empty now.

The **last popped node** is `3`.
Attach `20` as `3.right`. Push `20`.

State:

* Tree: `3.right = 20`
* `stack = [20]`
* `inIdx = 2` (points to 15)

---

### preIdx = 3, val = 15

* `stack.peek() = 20`
* Compare `20` vs `inorder[inIdx]=15`

  * `20 != 15` → still going left under 20
* Attach `15` as `20.left`, push 15.

State:

* Tree: `20.left = 15`
* `stack = [15, 20]`
* `inIdx = 2` (still 15)

---

### preIdx = 4, val = 7

* `stack.peek() = 15`
* Compare `15` vs `inorder[inIdx]=15`

  * match → pop/close

Pop while matches inorder:

1. pop `15`, `inIdx=3` → `inorder[3]=20`
2. now stack.peek() is `20`, and `20 == inorder[3]`

   * pop `20`, `inIdx=4` → `inorder[4]=7`
     Stop (stack empty).

Last popped node is `20`.
Attach `7` as `20.right`. Push `7`.

State:

* Tree: `20.right = 7`
* `stack = [7]`
* `inIdx = 4`

Done (processed all preorder values).

✅ Final tree matches expected.

---

## Why this always works

* Preorder gives the next node we must create.
* The stack represents the path of ancestors we’re currently “inside”.
* Inorder pointer tells us when we’ve finished a node’s left subtree (and possibly the node itself), so we can attach a right child next.

---

## Complexity

* **Time:** `O(n)` each node pushed/popped at most once
* **Space:** `O(h)` stack height (worst-case `O(n)` for skewed tree)
*/

// class Solution {
//     public TreeNode buildTree(int[] preorder, int[] inorder) {
//         if (preorder == null || preorder.length == 0) return null;

//         TreeNode root = new TreeNode(preorder[0]);
//         Deque<TreeNode> stack = new ArrayDeque<>();
//         stack.push(root);

//         int inIdx = 0; // pointer in inorder

//         // Process remaining preorder nodes
//         for (int preIdx = 1; preIdx < preorder.length; preIdx++) {
//             int val = preorder[preIdx];
//             TreeNode node = stack.peek();

//             // If top of stack hasn't matched inorder[inIdx], we are still going left
//             if (node.val != inorder[inIdx]) {
//                 node.left = new TreeNode(val);
//                 stack.push(node.left);
//             } else {
//                 // Otherwise, pop until we find a node whose value != inorder[inIdx]
//                 while (!stack.isEmpty() && stack.peek().val == inorder[inIdx]) {
//                     node = stack.pop();
//                     inIdx++;
//                 }
//                 // The new val becomes the right child of the last popped node
//                 node.right = new TreeNode(val);
//                 stack.push(node.right);
//             }
//         }

//         return root;
//     }
// }
