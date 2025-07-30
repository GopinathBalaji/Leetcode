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

//  DFS 
/*
### \U0001f539 Key Points About the Algorithm

1. **Base Case**:

   ```java
   if (root == null) return 0;
   ```

   * A `null` node means we’ve gone past a leaf.
   * Returning `0` here represents that an empty subtree has depth 0.

2. **Recursive Case**:

   ```java
   1 + Math.max(dfs(root.left), dfs(root.right))
   ```

   * For a non-null node:

     * Recursively find the max depth of its **left subtree** and **right subtree**.
     * Take the **larger** of the two depths (`Math.max`).
     * Add `1` to count the **current node** level.

3. **Why This Computes Height**:

   * Height of a tree = 1 (root) + max(height of left subtree, height of right subtree)
   * This is exactly the recursive relation implemented.

---

### \U0001f539 Example Walkthrough

Take this tree:

```
        1
       / \
      2   3
     / \
    4   5
```

* **Height** of the tree = 3
  (Path: `1 → 2 → 4` or `1 → 2 → 5`)

---

#### **Initial Call**

```java
maxDepth(root = 1) → dfs(1)
```

`dfs(1)`:

* Not null → compute left and right depths
* `return 1 + Math.max(dfs(1.left), dfs(1.right))`

---

#### **Step 1: Compute dfs(1.left) → dfs(2)**

`dfs(2)`:

* Not null → `1 + Math.max(dfs(2.left), dfs(2.right))`

##### Compute `dfs(2.left) → dfs(4)`

`dfs(4)`:

* Not null → `1 + Math.max(dfs(4.left), dfs(4.right))`

  * `dfs(4.left)` → null → 0
  * `dfs(4.right)` → null → 0
* Returns `1 + Math.max(0, 0) = 1`

##### Compute `dfs(2.right) → dfs(5)`

`dfs(5)`:

* Same as node 4:

  * `dfs(5.left)` → 0
  * `dfs(5.right)` → 0
* Returns `1 + Math.max(0, 0) = 1`

Now `dfs(2)` returns:

```
1 + Math.max(1, 1) = 2
```

---

#### **Step 2: Compute dfs(1.right) → dfs(3)**

`dfs(3)`:

* Not null → `1 + Math.max(dfs(3.left), dfs(3.right))`
* Both children are null:

  * `dfs(3.left)` → 0
  * `dfs(3.right)` → 0
* Returns `1 + Math.max(0,0) = 1`

---

#### **Step 3: Combine Results at Root (1)**

Now back to `dfs(1)`:

```
1 + Math.max(dfs(2), dfs(3))
= 1 + Math.max(2, 1)
= 1 + 2
= 3
```

So the **maximum depth is 3**, which is correct.

---

### How the Recursion Computes Height

At each node:

1. Recursively ask left and right children: **“What’s your max depth?”**
2. Take the **larger** of the two.
3. Add `1` for the current level.

This bottom-up accumulation naturally computes the **height of the tree**.

---

###  Key Insights

* **DFS naturally explores all paths**.
* **Base case stops recursion at leaves**.
* **Math.max ensures we always track the longest path**.
* **Adding 1 per return counts the current node’s level**.

*/
class Solution {
    public int maxDepth(TreeNode root) {
        if(root == null){
            return 0;
        }

        return dfs(root);
    }

    public int dfs(TreeNode root){
        if(root == null){
            return 0;
        }

        return 1 + Math.max(dfs(root.left) , dfs(root.right));
    }
}



// (Level-Order) BFS

// class Solution {
//     public int maxDepth(TreeNode root) {
//         if(root == null){
//             return 0;
//         }

//         Queue<TreeNode> queue = new LinkedList<>();
//         queue.offer(root);
//         int depth = 0;

//         while(!queue.isEmpty()){
//             int levelSize = queue.size();  // Number of nodes in current level

//             for(int i=0; i<levelSize; i++){
//                 TreeNode node = queue.poll();

//                 if(node.left != null){
//                     queue.offer(node.left);
//                 }

//                 if(node.right != null){
//                     queue.offer(node.right);
//                 }
//             }

//             depth++;  // Finished one level
//         }

//         return depth;
//     }
// }