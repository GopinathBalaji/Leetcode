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
//     Method 1: Using DFS and Single Pass Hash Map
    bool findTarget(TreeNode* root, int k) {
        stack<TreeNode*> s1;
        TreeNode* curr = root;
        int i=0;
        unordered_map<int,int> m1;
        
        while(!s1.empty() || curr!=NULL){
            while(curr!=NULL){
                s1.push(curr);
                curr = curr->left;
            }
            curr = s1.top();
            s1.pop();
            int comp = k - curr->val;
            if(m1.find(comp)!=m1.end()){
                return true;
            }else{
            m1.insert({curr->val,i});
            }
            i++;
            curr = curr->right;
        }
        return false;
    }
};


// Method 2: Using Inorder DFS and Two Pointer
   /*
    public boolean findTarget(TreeNode root, int k) {
        List < Integer > list = new ArrayList();
        inorder(root, list);
        int l = 0, r = list.size() - 1;
        while (l < r) {
            int sum = list.get(l) + list.get(r);
            if (sum == k)
                return true;
            if (sum < k)
                l++;
            else
                r--;
        }
        return false;
    }
    public void inorder(TreeNode root, List < Integer > list) {
        if (root == null)
            return;
        inorder(root.left, list);
        list.add(root.val);
        inorder(root.right, list);
    }
   */

// Method 3: Recursive DFS using HashSet
   /*
     public boolean findTarget(TreeNode root, int k) {
        Set < Integer > set = new HashSet();
        return find(root, k, set);
    }
    public boolean find(TreeNode root, int k, Set < Integer > set) {
        if (root == null)
            return false;
        if (set.contains(k - root.val))
            return true;
        set.add(root.val);
        return find(root.left, k, set) || find(root.right, k, set);
    }
   */