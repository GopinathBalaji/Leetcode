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

// My solution using recursive Inorder traversal since it is sorted
// class Solution {
    
//     int smallest = 0;
//     int value = 0;

//     public int kthSmallest(TreeNode root, int k) {
//         recursiveDfs(root, k);
//         return value;
//     }

//     private void recursiveDfs(TreeNode node, int k){
//         if(node == null){
//             return;
//         }

//         recursiveDfs(node.left, k);
//         if(smallest != k){
//             value = node.val;
//             smallest++;
//         }else if(smallest == k){
//             return;
//         }
//         recursiveDfs(node.right, k);
//     }
// }



// ChatGPT polished version of my code, that only recurses till
// we find the Kth smallest and not after that
/*
### Polished version (recursive DFS with short-circuit)

### Key refinements

* Use `count` as a direct counter instead of `smallest`.
* As soon as `count == k`, record the result and **stop recursion**.
* Avoid unnecessary traversal of the right subtree once we already found the k-th element.

This way, the traversal only goes as far as needed. Time complexity remains **O(h + k)**, but in practice, it’s a bit faster since recursion doesn’t continue uselessly.
*/
class Solution {

    private int count = 0;
    private int result = 0;

    public int kthSmallest(TreeNode root, int k) {
        inorder(root, k);
        return result;
    }

    private void inorder(TreeNode node, int k) {
        if (node == null) return;

        inorder(node.left, k);

        count++;
        if (count == k) {
            result = node.val;
            return;  // stop once we find it
        }

        // Only keep going if not found
        if (count < k) {
            inorder(node.right, k);
        }
    }
}


// Iterative stack version
// class Solution {
//     public int kthSmallest(TreeNode root, int k) {
//         int smallest = 0;
//         int value = 0;

//         Stack<TreeNode> stack = new Stack<>();
//         TreeNode current = root;

//         while(current != null || !stack.isEmpty()){
//             while(current != null){
//                 stack.push(current);
//                 current = current.left;
//             }

//             current = stack.pop();
//             smallest++;
//             if(smallest == k){
//                 value = current.val;
//                 return value;
//             }

//             current = current.right;
//         }

//         return value;
//     }
// }



// Answer to follow up: using Augmented BST with subtree sizes/order statistics: 
/*
### Why a **min-heap** (priority queue) is *not* a good choice here

At first glance, you might think:

* Just dump all values into a **min-heap**, then pop `k` times to get the k-th smallest.

But in practice:

1. **Heap pops are O(k log n)**

   * Getting the k-th element requires popping `k` times. That’s too slow if we get many queries.
   * For example, if `k ≈ n/2`, that’s `O(n log n)` in the worst case per query.

2. **No efficient “random access”**

   * A heap only gives you the *minimum* (or maximum).
   * You can’t directly ask “what’s the 7th smallest?” without popping through everything before it.

3. **Insertions/Deletions don’t solve the problem**

   * While insert/delete in a heap are `O(log n)`, the *query* for “k-th smallest” is still too expensive.
   * So the bottleneck remains.

In short: **Heaps are great for min/max queries, but terrible for order-statistics (like k-th smallest).**

---

### Better approach: **Augmented BST with subtree sizes**

Here’s the key idea:

* Every node in the BST stores an **extra field**: the **size of its subtree**.

  * Example: if a node has 7 nodes in its entire subtree (including itself), store `subtreeSize = 7`.

Why this helps:

* When you want the `k`-th smallest:

  * Look at the **left subtree size**.
  * If `leftSize + 1 == k`: current node is the k-th smallest.
  * If `leftSize ≥ k`: recurse into the left subtree.
  * If `leftSize + 1 < k`: recurse into the right subtree, adjusting `k -= (leftSize + 1)`.

This gives you:

* **Query k-th smallest in O(h)**, where `h` is tree height (log n if balanced).
* **Insert/Delete in O(h)** while keeping subtree sizes updated.

---

### Analogy to remember

Think of this like **binary search on the BST itself**.

* The subtree sizes give you the “indexing” power that a heap doesn’t.
* You can “skip over” large chunks of the tree without linearly iterating.

---

This is why in real implementations, people use **Order-Statistic Trees** (e.g., a Red-Black Tree with subtree sizes).
That’s the right tool for handling **frequent modifications** plus k-th smallest queries efficiently.
*/

// class TreeNode {
//     int val;
//     TreeNode left, right;
//     int count; // number of nodes in this subtree (including itself)

//     TreeNode(int x) {
//         val = x;
//         count = 1; // initially, just this node
//     }
// }

// class Solution {
//     // Find kth smallest element
//     public int kthSmallest(TreeNode root, int k) {
//         return findKth(root, k);
//     }

//     private int findKth(TreeNode node, int k) {
//         if (node == null) {
//             throw new IllegalArgumentException("k is larger than tree size");
//         }

//         int leftCount = (node.left != null) ? node.left.count : 0;

//         if (leftCount + 1 == k) {
//             // current node is the kth element
//             return node.val;
//         } else if (leftCount >= k) {
//             // kth is in the left subtree
//             return findKth(node.left, k);
//         } else {
//             // kth is in the right subtree
//             return findKth(node.right, k - leftCount - 1);
//         }
//     }

//     // Update counts after insertion
//     public TreeNode insert(TreeNode root, int val) {
//         if (root == null) return new TreeNode(val);

//         if (val < root.val) {
//             root.left = insert(root.left, val);
//         } else {
//             root.right = insert(root.right, val);
//         }

//         // update count after insertion
//         root.count = 1 + getCount(root.left) + getCount(root.right);
//         return root;
//     }

//     // Helper
//     private int getCount(TreeNode node) {
//         return node == null ? 0 : node.count;
//     }
// }
