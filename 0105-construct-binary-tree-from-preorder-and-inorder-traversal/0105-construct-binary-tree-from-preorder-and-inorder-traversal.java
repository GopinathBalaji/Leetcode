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

//  Use Preorder to get the root for every iteration.
//  Use Inorder to find the left and right children.
/*
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

## **\U0001f539 Key Takeaways from the Walkthrough**

1. **Preorder drives root creation**, while **inorder determines boundaries** of subtrees.
2. `preIndex` increments **once per node**.
3. Each recursion call **splits inorder** into left and right ranges using the root’s index.
4. No array copies are needed; just pass indices.
5. HashMap lookup makes finding the root index **O(1)**.
*/
class Solution {
    private int preIndex = 0;
    private HashMap<Integer, Integer> inorderMap = new HashMap<>();

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        for(int i=0; i<inorder.length; i++){
            inorderMap.put(inorder[i], i);
        }

        return build(preorder, 0, inorder.length-1);
    }

    private TreeNode build(int[] preorder, int inStart, int inEnd){
        if(inStart > inEnd){
            return null;
        }

        int rootVal = preorder[preIndex];
        preIndex++;
        TreeNode root = new TreeNode(rootVal);

        int rootIndex = inorderMap.get(rootVal);

        root.left = build(preorder, inStart, rootIndex - 1);
        root.right = build(preorder, rootIndex + 1, inEnd);

        return root;
    }
}