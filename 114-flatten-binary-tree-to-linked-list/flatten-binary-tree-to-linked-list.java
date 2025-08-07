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

// Using reverse pre-order traversal while maintaing 
// previous node. (Creating tree bottom-up)
/*
We are traversing Right -> Left -> Root.

Why traverse right before left?
Because we’re unwinding the tree from the bottom up:
We want the prev node to always be the one that was just processed.
So we start with the last node in preorder, and build the list backward.

Key Idea:
Maintain a prev pointer to track the last visited node in the traversal.

As you visit each node:
Set prev.right = current.
Set prev.left = null.
Move prev forward.

Use a class-level variable (or a single-element array) to persist prev across recursive calls.

### \U0001f537 Input Tree:

```
       1
      / \
     2   5
    / \   \
   3   4   6
```

---

### \U0001f527 Our Goal: Flatten to this **preorder traversal order**:

```
1
 \
  2
   \
    3
     \
      4
       \
        5
         \
          6
```

### \U0001f50d Walkthrough:

Let’s trace the execution from the bottom of the tree upward.

---

### Step-by-step Recursive Stack:

| Call Stack (top = current) | Operation                  | `prev` |
| -------------------------- | -------------------------- | ------ |
| flatten(1)                 | flatten right (5)          | null   |
| flatten(5)                 | flatten right (6)          | null   |
| flatten(6)                 | flatten right (null)       | null   |
|                            | flatten left (null)        | null   |
|                            | root.right = prev (= null) |        |
|                            | root.left = null           |        |
|                            | prev = 6                   | 6      |

⬆️ Return to `flatten(5)`

\| flatten(5)                 | root.right = prev (= 6)                     | 6            |
\|                            | root.left = null                           |              |
\|                            | prev = 5                                   | 5            |

⬆️ Return to `flatten(1)` — now call `flatten(2)`

\| flatten(2)                 | flatten right (4)                           | 5            |
\| flatten(4)                 | flatten right (null)                        | 5            |
\|                            | flatten left (null)                         |              |
\|                            | root.right = prev (= 5)                    |              |
\|                            | root.left = null                           |              |
\|                            | prev = 4                                   | 4            |

⬆️ Return to `flatten(2)` — now call `flatten(3)`

\| flatten(3)                 | flatten right (null)                        | 4            |
\|                            | flatten left (null)                         |              |
\|                            | root.right = prev (= 4)                    |              |
\|                            | root.left = null                           |              |
\|                            | prev = 3                                   | 3            |

⬆️ Return to `flatten(2)`

\| flatten(2)                 | root.right = prev (= 3)                     | 3            |
\|                            | root.left = null                           |              |
\|                            | prev = 2                                   | 2            |

⬆️ Return to `flatten(1)`

\| flatten(1)                 | root.right = prev (= 2)                     | 2            |
\|                            | root.left = null                           |              |
\|                            | prev = 1                                   | 1            |

---

### \U0001f51a Final Structure:

Now, the tree has been modified in-place to:

```
1
 \
  2
   \
    3
     \
      4
       \
        5
         \
          6

*/
class Solution {
    private TreeNode prev = null; // tracks the previous node in the flattened list

    public void flatten(TreeNode root) {
        if (root == null) return;

        // Recursively flatten right and left subtrees (right first!)
        flatten(root.right);
        flatten(root.left);

        // Rewire the current node's right to point to prev
        root.right = prev;
        root.left = null; // left should be null in the flattened list

        // Move prev to current node
        prev = root;
    }
}



// Iterative DFS (making the tree from the top while maintaining previous node)
/*
Here we push right child before the left child in the stack
because we want left to be processed first (we want left to be processed first
because pre-order is traversal is root->left->right when we make the tree from the top)
*/
// class Solution {
//     public void flatten(TreeNode root) {
//         if(root == null){
//             return;
//         }

//         Stack<TreeNode> stack = new Stack<>();
//         stack.push(root);

//         TreeNode prev = null;
        
//         while(!stack.isEmpty()){
//             TreeNode curr = stack.pop();

//             if(prev != null){
//                 prev.right = curr;
//                 prev.left = null;
//             }

//             if(curr.right != null){
//                 stack.push(curr.right);
//             }

//             if(curr.left != null){
//                 stack.push(curr.left);
//             }

//             prev = curr;
//         }

//     }
// }




// Using extra space to create new nodes (WON'T WORK, ADDED JUST TO EXPLAIN WHY REASSIGNING DOESN'T WORK)
/*
In my original solution I was doing:
    private void preorder(TreeNode root, TreeNode dummy){
        if(root == null){
            return;
        }

        dummy.right = new TreeNode(root.val);
        dummy = dummy.right;

        preorder(root.left, dummy);
        preorder(root.right, dummy);
    }
But this approach passes dummy by value (because objects are passed by reference but 
reassigning them breaks that), and you're reassigning it
dummy = dummy.right; doesn't persist outside the scope of the current recursive call. 
Each recursive call has its own local copy of dummy.

So my original recursion is always attaching new nodes 
to a "stale" dummy — it’s not keeping track of the most recently attached node 
across the entire traversal.

So the fix for this is to use a wrapper for the current 
node pointer so it can be updated across recursive calls.

Use a TreeNode[] of size 1 or a class field to hold 
and update the dummy reference.

The other option is to use global / class-level variable.
*/
// class Solution {
//     public void flatten(TreeNode root) {
//         if (root == null) return;

//         TreeNode dummy = new TreeNode(-1);
//         TreeNode[] curr = new TreeNode[] { dummy }; // Use an array to hold mutable reference

//         preorder(root, curr);
        
//         // Optional: If you want to assign the result back to root, you'd need to copy
//         // but Leetcode doesn't require returning — just flatten in-place (so this won't apply here)
//     }

//     private void preorder(TreeNode node, TreeNode[] curr) {
//         if (node == null) return;

//         curr[0].right = new TreeNode(node.val); // still using new nodes
//         curr[0] = curr[0].right; // advance the pointer

//         preorder(node.left, curr);
//         preorder(node.right, curr);
//     }
// }