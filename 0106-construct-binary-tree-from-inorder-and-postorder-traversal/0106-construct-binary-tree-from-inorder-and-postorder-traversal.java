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
Since postorder is L->R->Root, start choosing the root from the end of the postorder array and build the right subtree first, then left subtree.

postorder = [9, 15, 7, 20, 3]
inorder   = [9, 3, 15, 20, 7]
```

---

### **Step 0: Setup**

* **Observation:**

  * **Postorder** = Left → Right → Root
  * **Inorder** = Left → Root → Right
* **Root** is **last element** of postorder.
* Use a **HashMap** to store `inorder` indices for O(1) lookup:

```
inorderMap:
9 → 0
3 → 1
15 → 2
20 → 3
7 → 4
```

* Start recursion:

```
postIndex = 4   (points to last element of postorder)
build(inStart=0, inEnd=4)
```

---

### **Step 1: Create Root**

* `rootVal = postorder[4] = 3`
* `postIndex = 3` (decrement after using)
* Find in inorder: `3` → index `1`

```
Inorder: [9] 3 [15, 20, 7]
          L   R
```

* Left subtree = inorder\[0…0] → `[9]`
* Right subtree = inorder\[2…4] → `[15,20,7]`

⚠ **Important:** We must **build right subtree first** because in postorder, the right subtree appears **just before the root**.

---

### **Step 2: Build Right Subtree (inorder\[2…4])**

* Call `build(2, 4)`
* `rootVal = postorder[3] = 20`
* `postIndex = 2`
* Inorder index of 20 = `3`

```
Inorder: [15] 20 [7]
          L    R
```

* Left = inorder\[2…2] → `[15]`
* Right = inorder\[4…4] → `[7]`

Build **right subtree first** → `build(4, 4)`

---

### **Step 3: Build 20’s Right Subtree (inorder\[4…4])**

* `rootVal = postorder[2] = 7`
* `postIndex = 1`
* Inorder index of 7 = `4`

```
Inorder: [7]
```

* Left = `inorder[4…3]` → empty
* Right = `inorder[5…4]` → empty

Return Node `7`.

Tree so far:

```
      3
        \
         20
           \
            7
```

---

### **Step 4: Build 20’s Left Subtree (inorder\[2…2])**

* `rootVal = postorder[1] = 15`
* `postIndex = 0`
* Inorder index of 15 = `2`

```
Inorder: [15]
```

* Left = `inorder[2…1]` → empty
* Right = `inorder[3…2]` → empty

Return Node `15`.

Tree so far:

```
      3
        \
         20
        /  \
      15    7
```

---

### **Step 5: Build Left Subtree of 3 (inorder\[0…0])**

* `rootVal = postorder[0] = 9`
* `postIndex = -1` (used up all nodes)
* Inorder index of 9 = `0`

```
Inorder: [9]
```

* Left = empty
* Right = empty

Attach to tree:

```
        3
       / \
      9   20
         /  \
       15    7
```

---

### **Step 6: All Recursive Calls Return**

* `postIndex < 0` → All nodes used
* Final **constructed tree**:

```
        3
       / \
      9   20
         /  \
       15    7
```

---

### Key Points from the Walkthrough**

1. **Postorder → last element is root**
2. **Split inorder into left/right subtrees using root index**
3. **Build right subtree first** because postorder is L → R → Root
4. Use **postIndex** to track root selection from the back
5. Each recursive call returns a **subtree root** which is attached to its parent
*/
class Solution {
    private int postIdx;
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        Map<Integer, Integer> inorderIndexMap = new HashMap<>();
        for(int i=0; i<inorder.length; i++){
            inorderIndexMap.put(inorder[i], i);
        }

        postIdx = postorder.length - 1;

        return makeTree(inorderIndexMap, postorder, 0, inorder.length - 1);
    }

    private TreeNode makeTree(Map<Integer, Integer> inorderIndexMap, int[] postorder, int l, int r){
        if(l > r){
            return null;
        }

        TreeNode root = new TreeNode(postorder[postIdx]);

        int inorderRootIdx = inorderIndexMap.get(postorder[postIdx--]);

        root.right = makeTree(inorderIndexMap, postorder, inorderRootIdx + 1, r);
        root.left = makeTree(inorderIndexMap, postorder, l, inorderRootIdx - 1);

        return root;
    }   
}







// Method 2: Iterative approach using a stack
/*
## Key observations

* **Postorder** is: `left, right, root`

  * So the **last** element of `postorder` is the **root** of the whole tree.
  * If you traverse postorder **backwards**, you see nodes in order: `root, right, left`.

* **Inorder** is: `left, root, right`

  * If you traverse inorder **backwards**, you see nodes in order: `right, root, left`.

These “backwards” orders line up nicely to build the tree iteratively:

* We will attach **right children first**, then left children.

---

## Core idea (stack invariant)

We maintain:

* a `stack` of nodes whose **left child** is not fully attached yet
* an `inIdx` pointer starting at the **end** of `inorder`

Process `postorder` from the **end** (right-to-left):

1. Create the next node from `postorder[postIdx]`.
2. If the top of stack **does not** match `inorder[inIdx]`, we are still going down the **right spine**, so attach this new node as the **right child** of the stack top.
3. Otherwise, if stack top **does** match `inorder[inIdx]`, it means we have finished the right side for those nodes. Pop until it no longer matches, decreasing `inIdx` each pop. Then attach the new node as the **left child** of the last popped node.

This is the mirror of the iterative solution for preorder+inorder, just reversed.


## Thorough walkthrough with example

Use the classic example:

* `inorder   = [9, 3, 15, 20, 7]`
* `postorder = [9, 15, 7, 20, 3]`

Expected tree:

```
      3
     / \
    9  20
      /  \
     15   7
```

### Initialization

* `root = postorder[last] = 3`
* `stack = [3]`  (top on left)
* `inIdx = 4` → `inorder[inIdx] = 7`

We will iterate `postIdx` from 3 down to 0 over values: `20, 7, 15, 9`.

---

### Step 1: postIdx=3, val=20

* stack.peek() = 3
* compare with inorder[inIdx]=7

  * `3 != 7` ⇒ attach as **right child** of 3

Action:

* `3.right = 20`
* push 20

State:

* stack = `[20, 3]`
* inIdx still 4 (still expecting 7)

Tree so far:

```
  3
   \
    20
```

---

### Step 2: postIdx=2, val=7

* stack.peek() = 20
* inorder[inIdx] = 7

  * `20 != 7` ⇒ still building right chain ⇒ attach as **right child** of 20

Action:

* `20.right = 7`
* push 7

State:

* stack = `[7, 20, 3]`
* inIdx = 4

Tree so far:

```
  3
   \
    20
      \
       7
```

---

### Step 3: postIdx=1, val=15

* stack.peek() = 7
* inorder[inIdx] = 7

  * `7 == 7` ⇒ we “close” 7 in inorder (its right subtree is done)

Pop while matches inorder:

1. pop 7, `inIdx = 3` (now inorder[3]=20)
2. stack.peek() = 20, matches inorder[3]=20

   * pop 20, `inIdx = 2` (now inorder[2]=15)
3. stack.peek() = 3, inorder[2]=15, no match ⇒ stop

The **last popped node** was `20`.
Now attach `15` as **left child** of that last popped node (20).

Action:

* `20.left = 15`
* push 15

State:

* stack = `[15, 3]`
* inIdx = 2 (expecting 15 next)

Tree so far:

```
    3
     \
      20
     /  \
   15    7
```

---

### Step 4: postIdx=0, val=9

* stack.peek() = 15
* inorder[inIdx] = 15

  * match ⇒ close 15

Pop while matches inorder:

1. pop 15, `inIdx = 1` (now inorder[1]=3)
2. stack.peek() = 3 matches inorder[1]=3

   * pop 3, `inIdx = 0` (now inorder[0]=9)
     stack empty ⇒ stop

Last popped node was `3`.
Attach `9` as **left child** of 3.

Action:

* `3.left = 9`
* push 9

State:

* stack = `[9]`
* inIdx = 0

Final tree:

```
      3
     / \
    9  20
      /  \
     15   7
```

✅ Done.

---

## Why this works (intuition)

* Scanning postorder backwards gives you `root, right, left` creation order.
* The stack represents ancestors for which we haven’t attached the left side yet.
* Scanning inorder backwards tells you when you’ve finished the right side of a node and should switch to building its left subtree.

---

## Complexity

* **Time:** `O(n)` each node is pushed and popped at most once.
* **Space:** `O(h)` stack, worst-case `O(n)`.
*/
// class Solution {
//     public TreeNode buildTree(int[] inorder, int[] postorder) {

//         int n = postorder.length;

//         // Root is last in postorder
//         TreeNode root = new TreeNode(postorder[n - 1]);
//         Deque<TreeNode> stack = new ArrayDeque<>();
//         stack.push(root);

//         int inIdx = inorder.length - 1; // start from end of inorder

//         // Walk postorder backwards, skipping the last element (already used as root)
//         for (int postIdx = n - 2; postIdx >= 0; postIdx--) {
//             int val = postorder[postIdx];
//             TreeNode node = stack.peek();

//             // If stack top isn't the next inorder node to close,
//             // we are still building the right subtree chain.
//             if (node.val != inorder[inIdx]) {
//                 node.right = new TreeNode(val);
//                 stack.push(node.right);
//             } else {
//                 // Pop nodes whose inorder position is done
//                 while (!stack.isEmpty() && stack.peek().val == inorder[inIdx]) {
//                     node = stack.pop();
//                     inIdx--;
//                 }
//                 // Attach as left child of the last popped node
//                 node.left = new TreeNode(val);
//                 stack.push(node.left);
//             }
//         }

//         return root;
//     }
// }
