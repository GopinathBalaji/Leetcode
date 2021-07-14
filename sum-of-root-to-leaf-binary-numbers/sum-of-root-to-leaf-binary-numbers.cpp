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
//    Method 1: Using Recursive DFS and In-built function to calculate binary to decimal
    int sumRootToLeaf(TreeNode* root) {
        vector<string> v1;
        int sum = 0;
        string s = "";
        dfs(root,v1,s);
        for(int i=0;i<v1.size();i++){
            bitset<32> bits(v1[i]);
            int number = bits.to_ulong();
            sum += number;
        }
        return sum;
    }
    
    void dfs(TreeNode* root, vector<string>& v1, string s){
        if(!root) return;
        
        if(!root->left && !root->right){
            v1.push_back(s+to_string(root->val));
        }
        
        dfs(root->left, v1, s+to_string(root->val));
        dfs(root->right, v1, s+to_string(root->val));
    }
};

// Method 2: Using Bit Maniputlation and Recursive DFS
// cur_val = (cur_val << 1) | rt->val;
// cur_val << 1 means we're shifting the bits of cur_val to the left by 1 place, which is 
// equivalent to multiplying cur_val by 2 (in base 2). 

// ( This is a directly analogous to when you work with base 10.
// Take the number 3 in base 10. Shift it left: you get 30, which is 3⋅10 (and the factor 10 
// appears because you are working with base 10).
// The same applies to base 2. Shifting left is the same as multiplying by 2. )

// Then Bitwise OR is used to set a particular bit.

// Let's take an example!
// Suppose, the root-to-leaf path is 1->0->1.
// Then, at the root, initially cur_val is 0. We multiply it by 2 and perform bitwise OR
// with 1 which makes cur_val 1.
// Next, cur_val is 1. We multiply it by 2 and perform bitwise OR with 0 which makes cur_val 2.
// Last, at the leaf, cur_val is 2. We multiply it by 2 and perform bitwise OR with 1
// which makes cur_val 5. 
// 101 (base 2) is 5 (base 10).

   /*
    
    int rootToLeaf = 0;
    
    public void preorder(TreeNode r, int currNumber) {
        if (r != null) {
            currNumber = (currNumber << 1) | r.val;
            // if it's a leaf, update root-to-leaf sum
            if (r.left == null && r.right == null) {
            rootToLeaf += currNumber;
            }
            preorder(r.left, currNumber);
            preorder(r.right, currNumber);
        }
    }

    public int sumRootToLeaf(TreeNode root) {
        preorder(root, 0);
        return rootToLeaf;
    }
   */

// Method 3: Iterative DFS and Bit Manipulation
   /*
    
     int sumRootToLeaf(TreeNode* rt) {
        if(! rt) return 0;
        
        int ans = 0;
        stack<pair<TreeNode*,int>> st;
        st.push({rt,0});
        
        while(! st.empty()) {
            auto curp = st.top(); st.pop();
            TreeNode* cur = curp.first;
            int cur_val = curp.second;
            
            // Equivalent to left shift by 1 and then ORing by cur->val.
            cur_val = cur_val*2 + cur->val;
            
            if(!cur->left && !cur->right)
                ans += cur_val;
            
            if(cur->left) st.push({cur->left, cur_val});
            if(cur->right) st.push({cur->right, cur_val});
        }
        
        return ans;
        
    }
   */

// Method 3: Morris Preorder Traversal
   /*
     public int sumRootToLeaf(TreeNode root) {
        int rootToLeaf = 0, currNumber = 0;
        int steps;
        TreeNode predecessor;

        while (root != null) {
            // If there is a left child,
            // then compute the predecessor.
            // If there is no link predecessor.right = root --> set it.
            // If there is a link predecessor.right = root --> break it.
            if (root.left != null) {
                // Predecessor node is one step to the left
                // and then to the right till you can.
                predecessor = root.left;
                steps = 1;
                while (predecessor.right != null && predecessor.right != root) {
                    predecessor = predecessor.right;
                    ++steps;
                }

                // Set link predecessor.right = root
                // and go to explore the left subtree
                if (predecessor.right == null) {
                    currNumber = (currNumber << 1) | root.val;
                    predecessor.right = root;
                    root = root.left;
                }
                // Break the link predecessor.right = root
                // Once the link is broken,
                // it's time to change subtree and go to the right
                else {
                    // If you're on the leaf, update the sum
                    if (predecessor.left == null) {
                        rootToLeaf += currNumber;
                    }
                    // This part of tree is explored, backtrack
                    for(int i = 0; i < steps; ++i) {
                        currNumber >>= 1;
                    }
                    predecessor.right = null;
                    root = root.right;
                }
            }
            // If there is no left child
            // then just go right.
            else {
                currNumber = (currNumber << 1) | root.val;
                // if you're on the leaf, update the sum
                if (root.right == null) {
                    rootToLeaf += currNumber;
                }
                root = root.right;
            }
        }
        return rootToLeaf;
    }
   */