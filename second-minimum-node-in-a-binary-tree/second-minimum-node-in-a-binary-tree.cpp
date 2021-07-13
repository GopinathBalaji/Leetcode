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
//     Method 1: Keeping track of Previous min and current min
    
    int findSecondMinimumValue(TreeNode* root) {
        return findSecondMinimumValue_Iterative(root, root->val);
    }
    
    int findSecondMinimumValue_Iterative(TreeNode* root, int val) {
        int* pRet = nullptr;

        queue<TreeNode*> q;
        if (root) {
            q.push(root);
        }
        while (!q.empty()) {
            TreeNode* node = q.front();
            q.pop();

            if (val < node->val) {
                if (pRet) {
                    if (node->val < *pRet) {
                        pRet = &(node->val);
                    }
                }
                else {
                    pRet = &(node->val);
                }
            }

            if (node->val == val) {
                if (node->left) {
                    q.push(node->left);
                }
                if (node->right) {
                    q.push(node->right);
                }
            }
        }

        return (pRet ? *pRet : -1);
    }
};

// Method 2: Recursive DFS
// Find the first node that is different from the root. If no different values then return -1.
   /*
    class Solution {
public:
    int findSecondMinimumValue(TreeNode* root) {
        if (!root) return -1;
        int ans = minval(root, root->val);
        return ans;
    }
private:
    int minval(TreeNode* p, int first) {
        if (p == nullptr) return -1;
        if (p->val != first) return p->val;
        int left = minval(p->left, first), right = minval(p->right, first);
        // if all nodes of a subtree = root->val, 
        // there is no second minimum value, return -1
        if (left == -1) return right;
        if (right == -1) return left;
        return min(left, right);
    }
};
   */