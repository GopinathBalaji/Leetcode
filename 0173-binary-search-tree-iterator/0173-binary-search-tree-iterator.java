// My solution (uses O(n) memory to store all nodes in a list)
class BSTIterator{

    List<Integer> list;
    int index;

    BSTIterator(TreeNode root){
        list = new ArrayList<>();
        index = 0;
        inorder(root);
    }

    private void inorder(TreeNode node){
        if(node == null){
            return;
        }

        inorder(node.left);
        list.add(node.val);
        inorder(node.right);
    }
    
    public boolean hasNext(){
        
        return index < list.size();
    }

    public int next(){
        return list.get(index++);
    }
}



// Better Version using Stack (O(h) memory)
/*
Instead of flattening the tree into a list first (O(n) memory), 
we simulate inorder traversal using a stack, which only stores 
at most h nodes (where h is the tree height).

Core Idea (Using stack based inorder traversal):
Use a stack to store the path to the next smallest element.
Start from the root and push all left children until you hit null.
On next():
Pop the top node → this is the next smallest value.
If that node has a right child, push all its left descendants into the stack.
hasNext() → check if the stack is not empty.
*/
// class BSTIterator{

//     private Stack<Integer> stack;

//     BSTIterator(TreeNode root){
//         stack = new Stack<>();
//         pushNodesInorder(root);
//     }

//     private void pushNodesInorder(TreeNode node){
//         while(node != null){
//             stack.push(node.left);
//             node = node.left;
//         }
//     }

//     public boolean hasNext(){
//         return !stack.isEmpty();
//     }

//     public int next(){
//         TreeNode curr = stack.pop();
//         if(curr.right != null){
//             pushNodesInorder(node.right);
//         }

//         return curr.val;
//     }
// }



// Best solution using Morris (threaded) Traversal (O(1) space)
/*
Refer to this video in the future: https://www.youtube.com/watch?v=wGXB9OWhPTg
Below is just the Morris-based traversal code with example:

Morris traversal does an inorder walk **without recursion and without 
an explicit stack**, using **O(1)** extra space. It temporarily 
creates “threads” from a node’s inorder predecessor back to the 
node so you can return to the node after finishing its left subtree.
Those threads are removed as you go, so the tree ends up restored.

# Algorithm (plain steps)

Let `cur` start at `root`.

While `cur != null`:

1. If `cur.left == null`:

   * Visit `cur` (process `cur.val`).
   * `cur = cur.right`.
2. Else (there *is* a left subtree):

   * Find `pred` = rightmost node in `cur.left` (the inorder predecessor of `cur`).
   * If `pred.right == null`:

     * Set `pred.right = cur` (create a thread).
     * Move `cur = cur.left`.
   * Else (`pred.right == cur` — thread already exists):

     * Remove the thread: `pred.right = null`.
     * Visit `cur`.
     * Move `cur = cur.right`.

Repeat until `cur` becomes `null`.

---

# Java code (inorder collection)

```java
public List<Integer> morrisInorder(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    TreeNode cur = root;

    while (cur != null) {
        if (cur.left == null) {
            // no left subtree -> visit and go right
            result.add(cur.val);
            cur = cur.right;
        } else {
            // find predecessor (rightmost node in left subtree)
            TreeNode pred = cur.left;
            while (pred.right != null && pred.right != cur) {
                pred = pred.right;
            }

            if (pred.right == null) {
                // make thread to return to cur after left subtree
                pred.right = cur;
                cur = cur.left;
            } else {
                // thread exists -> remove it, visit cur, go right
                pred.right = null;
                result.add(cur.val);
                cur = cur.right;
            }
        }
    }
    return result;
}

# Worked example (step-by-step)

Tree:

```
    4
   / \
  2   5
 / \
1   3
```

Start: `cur = 4`

1. `cur=4`, `left!=null`. Find `pred` = rightmost in left subtree (start at 2 → go right to 3 → pred=3). `pred.right == null` → set `pred.right = 4` (thread), `cur = cur.left = 2`.

2. `cur=2`, `left!=null`. Find `pred` = rightmost in left subtree (1). `pred.right == null` → set `pred.right = 2`, `cur = 1`.

3. `cur=1`, `left==null` → visit `1`. `cur = cur.right` → because of thread `pred.right` we set earlier, `cur` becomes `2`.

4. `cur=2`, now predecessor (1) has `right == 2` (thread) → remove thread (`pred.right = null`), visit `2`, then `cur = 2.right = 3`.

5. `cur=3`, `left==null` → visit `3`, `cur = 3.right` → thread points to `4`, so `cur = 4`.

6. `cur=4`, predecessor (3) has `right == 4` → remove thread, visit `4`, `cur = 4.right = 5`.

7. `cur=5`, `left==null` → visit `5`, `cur = 5.right = null`. Done.

Visited order: `1, 2, 3, 4, 5`.
*/

// class BSTIterator {
//     private TreeNode cur; // current node in the traversal

//     public BSTIterator(TreeNode root) {
//         this.cur = root; // start at root
//     }

//     public boolean hasNext() {
//         return cur != null;
//     }

//     public int next() {
//         int val = -1;

//         while (cur != null) {
//             if (cur.left == null) {
//                 // Case 1: No left child → visit this node
//                 val = cur.val;
//                 cur = cur.right; // move to right child
//                 break; // return immediately
//             } else {
//                 // Case 2: Left child exists → find predecessor
//                 TreeNode pred = cur.left;
//                 while (pred.right != null && pred.right != cur) {
//                     pred = pred.right;
//                 }

//                 if (pred.right == null) {
//                     // Create the temporary thread
//                     pred.right = cur;
//                     cur = cur.left; // go down to left subtree
//                 } else {
//                     // Thread already exists → remove it and visit cur
//                     pred.right = null;
//                     val = cur.val;
//                     cur = cur.right;
//                     break; // return immediately
//                 }
//             }
//         }

//         return val;
//     }
// }