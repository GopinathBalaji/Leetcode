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
// Since postorder is L->R->Root, start choosing the root
// from the end of the postorder array and build the right
// subtree first.
/*
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

### **\U0001f539 Key Points from the Walkthrough**

1. **Postorder → last element is root**
2. **Split inorder into left/right subtrees using root index**
3. **Build right subtree first** because postorder is L → R → Root
4. Use **postIndex** to track root selection from the back
5. Each recursive call returns a **subtree root** which is attached to its parent
*/
class Solution {
    HashMap<Integer, Integer> inorderMap = new HashMap<>();
    int postorderIndex = 0;
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        postorderIndex = postorder.length - 1;
        for(int i=0; i<inorder.length;i++){
            inorderMap.put(inorder[i], i);
        }

        return create(inorder, postorder, 0, inorder.length - 1);
    }

    private TreeNode create(int[] inorder, int[] postorder, int startIndex, int endIndex){
        if(startIndex > endIndex){
            return null;
        }

        int rootVal = postorder[postorderIndex];
        postorderIndex--;
        TreeNode root = new TreeNode(rootVal);
        
        int rootIndex = inorderMap.get(rootVal);
        
        root.right = create(inorder, postorder, rootIndex+1, endIndex);
        root.left = create(inorder, postorder, startIndex, rootIndex-1);
        
        return root;
    }
}