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

//  Simple BFS and swapping the children of each node
// class Solution {
//     public TreeNode invertTree(TreeNode root) {
//         if(root == null){
//             return root;
//         }

//         Queue<TreeNode> queue = new LinkedList<>();

//         queue.offer(root);

//         while(!queue.isEmpty()){
//             TreeNode node = queue.poll();
//             TreeNode temp = node.left;
//             node.left = node.right;
//             node.right = temp;
            
//             if(node.left != null){
//                 queue.offer(node.left);
//             }
//             if(node.right != null){
//                 queue.offer(node.right);
//             }
//         }

//         return root;
//     }
// }


// DFS Approach
/*
### \U0001f539 1. Key Idea of DFS Inversion

* DFS means you **go deep** into the tree before coming back.
* At each node:

  1. **Swap its left and right children**.
  2. Recursively invert the **left subtree**.
  3. Recursively invert the **right subtree**.

---

### \U0001f539 2. Recursive DFS Steps

1. **Base Case**:

   * If the current node is `null`, just return. (Nothing to swap in an empty subtree)

2. **Swap Operation**:

   * Swap `node.left` and `node.right`.

3. **Recursive Calls**:

   * Invert the left subtree.
   * Invert the right subtree.

4. **Return** the current node after processing both sides.

---

### \U0001f539 3. Intuitive Example

Original tree:

```
    4
   / \
  2   7
 / \ / \
1  3 6  9
```

* DFS goes **root → left**:

  1. At `4`: swap children → `7,2`
  2. Go into new left (7) → swap → `9,6`
  3. Continue recursively until all leaves are processed

Final inverted tree:

```
    4
   / \
  7   2
 / \ / \
9  6 3  1
```

---

### \U0001f539 4. DFS Variations

You can implement DFS in **three main ways**:

1. **Recursive DFS (Pre‑order)** → Swap at current node, then recurse left/right.
2. **Recursive DFS (Post‑order)** → Recurse left/right first, then swap at the node.
3. **Iterative DFS with Stack** → Push nodes into a stack and process like recursion manually.

---

### \U0001f539 5. Key Considerations

* **Recursion Depth**: For very deep trees, recursive DFS can risk stack overflow.
* **Iterative DFS** with a stack avoids this by simulating the call stack.
* **Time Complexity**: O(n) — each node is visited once.
* **Space Complexity**: O(h), where h = tree height (stack/recursion depth).
*/
class Solution {
    public TreeNode invertTree(TreeNode root) {
        
        dfs(root);

        return root;
    }


    public void dfs(TreeNode root){
        if(root == null){
            return;
        }

        TreeNode node = root.left;
        root.left = root.right;
        root.right = node;

        dfs(root.left);
        dfs(root.right);
    }
}