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
//     Method 1: Iterative Inorder (DFS)
// Think of this question like a variation on "what is the smallest distance between any 
// two values in a sorted array?" With a sorted array, all you do is scan through the
// entire array looking for the absolute minimum difference between two adjacent values 
// since you know, when the array is sorted, that the answer can only be found between
// those two ADJACENT values.
// To scan the BST like a sorted array, just go through IN-ORDER traversal and keep track of the 
// previous node evaluated.
    int getMinimumDifference(TreeNode* root) {
        int globalmin = INT_MAX;
        stack<TreeNode*>s1;
        TreeNode* prev = nullptr, *curr = root;
        
        while(true){
            while(curr!=nullptr){
                s1.push(curr);
                curr = curr->left;
            }
            
            if(s1.empty()){
                break;
            }
            curr = s1.top();
            s1.pop();
            
            if(prev){
                globalmin = min(globalmin,abs(curr->val - prev->val));
            }
            prev = curr;
            curr = curr->right;
        }
        return globalmin;
    }
};

// Method 2: Using Recursive Inorder (DFS)
// To scan the BST like a sorted array, just go through IN-ORDER traversal and keep track of the
// previous node evaluated.
   /*
    void dfs( TreeNode *node, TreeNode* &prev, int &diff ) {
        if( !node ) return;

        dfs( node->left, prev, diff );
        if( prev ) diff = min( diff, node->val - prev->val );
        prev = node;
        dfs( node->right, prev, diff );
    }

    int getMinimumDifference( TreeNode* root ) {
        int diff = INT_MAX;
        TreeNode *prev = nullptr;
        dfs( root, prev, diff );
        return diff;
    }
   */

// Method 3: Store values in Vector and find min using Sliding Window
   /*
    public int getMinimumDifference(TreeNode root) {
	Stack<TreeNode> h = new Stack<>();
	List<Integer> m = new ArrayList<>();
	h.add(root);
	while (!h.isEmpty()){
		TreeNode l = h.pop();
		if (l != null) {
			m.add(l.val);
			h.add(l.left);
			h.add(l.right);
		}
	}
	Collections.sort(m);
	int min = Integer.MAX_VALUE;
	for (int i = 0; i < m.size() - 1; i++) {
		int difference = m.get(i + 1) - m.get(i);
		if (difference < min){
			min = difference;
		}
	}
	return min;
}
   */

