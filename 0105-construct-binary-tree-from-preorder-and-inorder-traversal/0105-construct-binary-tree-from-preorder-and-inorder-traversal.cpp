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

// Method 1: Recusive approach after finding the root
/*
### Hint 1

Look at what the traversals tell you:

* **Preorder:** `root → left → right`
* **Inorder:** `left → root → right`

The first value in preorder is always the root of the current subtree.

### Hint 2

Find that root value inside the inorder array.

Everything to its left belongs to the left subtree, and everything to its right belongs to the right subtree.

```text
inorder: [left subtree] root [right subtree]
```

### Hint 3

Once you know the size of the left subtree, you can divide the preorder range too.

Suppose:

```cpp
leftSize = rootIndexInInorder - inorderStart;
```

Then:

```text
preorder root:          preorderStart
left subtree preorder: preorderStart + 1 ... preorderStart + leftSize
right subtree preorder: everything after that
```

### Hint 4

Use a recursive helper with array boundaries:

```cpp
TreeNode* build(
    int preorderStart,
    int preorderEnd,
    int inorderStart,
    int inorderEnd
)
```

Base case:

```cpp
if (preorderStart > preorderEnd) {
    return nullptr;
}
```

### Hint 5

The recursive structure is approximately:

```cpp
int rootValue = preorder[preorderStart];
TreeNode* root = new TreeNode(rootValue);

int inorderRootIndex = find rootValue in inorder ;
int leftSize = inorderRootIndex - inorderStart;

root->left = build(left ranges );
root->right = build( right ranges *);

return root;
```

### Hint 6

Searching the inorder array every recursive call can make the solution `O(n²)`.

Create a hash map first:

```cpp
unordered_map<int, int> inorderIndex;
```

Store:

```cpp
inorderIndex[inorder[i]] = i;
```

Then each root’s inorder position can be found in `O(1)`.

### Hint 7

Be especially careful with the preorder boundaries:

```text
left preorder:
preStart + 1
to
preStart + leftSize

right preorder:
preStart + leftSize + 1
to
preEnd
```

With the hash map, the overall complexity is `O(n)` time and `O(n)` extra space.
*/
class Solution {
private:
    TreeNode* build(vector<int>& preorder, unordered_map<int, int>& inorderIndex, int preorderStart, int preorderEnd, int inorderStart, int inorderEnd){
        if(preorderStart > preorderEnd){
            return nullptr;
        }

        int rootVal = preorder[preorderStart];
        TreeNode* root = new TreeNode(rootVal);

        int inorderRootIndex = inorderIndex[rootVal];
        int leftSize = inorderRootIndex - inorderStart;

        root->left = build(preorder, inorderIndex, preorderStart + 1, preorderStart + leftSize, inorderStart, inorderRootIndex - 1);
        root->right = build(preorder, inorderIndex, preorderStart + leftSize + 1, preorderEnd, inorderRootIndex + 1, inorderEnd);

        return root;
    }

public:
    TreeNode* buildTree(vector<int>& preorder, vector<int>& inorder) {
        unordered_map<int, int> inorderIndex;

        for(int i=0; i<inorder.size(); i++){
            inorderIndex[inorder[i]] = i;
        }

        return build(preorder, inorderIndex, 0, preorder.size()-1, 0, inorder.size()-1);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna