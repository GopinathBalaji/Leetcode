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
class Solution {
public:
//     Method : Recursion by considering all the cases
// Case 1: Both the left child and the right child exist for the current node. 
// In this case, we need to put the braces () around both the left child's preorder
// traversal output and the right child's preorder traversal output.

// Case 2: None of the left or the right child exist for the current node. In this case,
// as shown in the figure below, considering empty braces for the null left and right 
// children is redundant. Hence, we need not put braces for any of them.
    
// Case 3: Only the left child exists for the current node. As the figure below shows, 
// putting empty braces for the right child in this case is unnecessary while considering 
// the preorder traversal. This is because the right child will always come after the left 
// child in the preorder traversal. Thus, omitting the empty braces for the right child also 
// leads to same mapping between the string and the binary tree.
        
 // Case 4: Only the right child exists for the current node. In this case, we need to 
// consider the empty braces for the left child. This is because, during the preorder
// traversal, the left child needs to be considered first. Thus, to indicate that the 
// child following the current node is a right child we need to put a pair of empty braces 
// for the left child.
    string tree2str(TreeNode* root) {
      if(!root) return "";
      if(!root->left && !root->right) return to_string(root->val);
      if(!root->right) return to_string(root->val) + '(' + tree2str(root->left) + ')';
      
      return to_string(root->val) + '(' + tree2str(root->left) + ")(" + tree2str(root->right) + ')';
    }
};


// Method 2: Using Iterative Preorder DFS
// Unlike normal iterative Preorder traversal,  even when a node is being processed, if 
// it has not already been visited, it isn't popped off from the stackstack. Only if 
// a node that has already been processed(i.e. its children have been considered already),
// it is popped off from the stackstack when encountered again.
 // Such a situation will occur for a node only when the preorder traversal of both 
// its left and right sub-trees has been completely done.
   /*
     public String tree2str(TreeNode t) {
        if (t == null)
            return "";
        Stack < TreeNode > stack = new Stack < > ();
        stack.push(t);
        Set < TreeNode > visited = new HashSet < > ();
        StringBuilder s = new StringBuilder();
        while (!stack.isEmpty()) {
            t = stack.peek();
            if (visited.contains(t)) {
                stack.pop();
                s.append(")");
            } else {
                visited.add(t);
                s.append("(" + t.val);
                if (t.left == null && t.right != null)
                    s.append("()");
                if (t.right != null)
                    stack.push(t.right);
                if (t.left != null)
                    stack.push(t.left);
            }
        }
        return s.substring(1, s.length() - 1);
    }
   */