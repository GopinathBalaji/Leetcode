/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode() : val(0), left(nullptr), right(nullptr) {}
 *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
 *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 * };
 */

// Method 1: Recursive deletion
/*
This problem is mainly about carefully handling **three deletion cases**.

## Hint 1

First, locate the node to delete just like you search in a BST.

* If `key < root->val`, recurse left.
* If `key > root->val`, recurse right.
* Otherwise, you've found the node to delete.

---

## Hint 2

Once you've found the node, ask:

**How many children does it have?**

There are only **three possibilities**.

---

## Hint 3 — Case 1: No children (leaf)

This is the easiest case.

Delete the node and return:

```cpp
nullptr
```

so the parent's pointer becomes `nullptr`.

---

## Hint 4 — Case 2: One child

Suppose only the left child exists.

Instead of keeping the current node:

* save the left child
* delete the current node
* return the child

The parent will now point directly to that child.

The same idea applies if only the right child exists.

---

## Hint 5 — Case 3: Two children

This is the interesting one.

You cannot simply delete the node because you'd lose one subtree.

Instead:

1. Find a replacement value.
2. Copy that value into the current node.
3. Delete the replacement node from its original location.

---

## Hint 6

Which node should replace it?

Two equally valid choices:

* **Inorder successor**

  * smallest node in the right subtree

or

* **Inorder predecessor**

  * largest node in the left subtree

Most solutions use the successor.

---

## Hint 7

How do you find the inorder successor?

Start at:

```text
root->right
```

and keep moving:

```text
left
left
left
...
```

until you can't go further.

---

## Hint 8

After copying the successor's value:

```text
root->val = successor->val;
```

you still have **two nodes with the same value**.

How do you fix that?

Recursively delete the successor from the right subtree.

---

## Hint 9

Just like insertion, always reconnect the returned subtree.

Think carefully about statements like:

```cpp
root->left = ...
```

or

```cpp
root->right = ...
```

Without these assignments, changes made deeper in the tree won't be reflected in the parent.

---

## Overall recursive structure

```text
delete(root, key)

    if root == nullptr
        return nullptr

    if key < root->val
        recurse left

    else if key > root->val
        recurse right

    else
        // found node
        Case 1
        Case 2
        Case 3

    return root
```

---

## Complexity

* **Time:** `O(h)` where `h` is the tree height.
* **Space:** `O(h)` due to recursion (`O(log n)` for a balanced BST, `O(n)` in the worst case).
*/
class Solution {
public:
    TreeNode* deleteNode(TreeNode* root, int key) {
        if(root == nullptr){
            return root;
        }

        if(key < root->val){
            root->left = deleteNode(root->left, key);
        }else if(key > root->val){
            root->right = deleteNode(root->right, key);
        }else{

            if(root->left == nullptr && root->right == nullptr){
                delete root;
                return nullptr;
            }
            else if(root->left != nullptr && root->right == nullptr){
                TreeNode* leftChild = root->left;
                delete root;
                return leftChild;
            }
            else if(root->left == nullptr && root->right != nullptr){
                TreeNode* rightChild = root->right;
                delete root;
                return rightChild;
            }
            else{
                // Two children: find inorder successor
                TreeNode* successor = root->right;

                while (successor->left != nullptr) {
                    successor = successor->left;
                }

                // Copy successor's value
                root->val = successor->val;

                // Remove the duplicate successor node
                root->right = deleteNode(root->right, successor->val);
            }
        }

        return root;        
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna