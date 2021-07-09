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
//         Method 1: Iterative BFS using Two Queues
    vector<double> averageOfLevels(TreeNode* root) {
       vector<double> v1;
        queue<TreeNode*> q1;
        q1.push(root);
        while(!q1.empty()){
            long sum = 0,count = 0;
            queue<TreeNode*> temp;
            while(!q1.empty()){
                TreeNode* node = q1.front();
                q1.pop();
                sum += node->val;
                count++;
                if(node->left!=NULL){
                    temp.push(node->left);
                }
                if(node->right!=NULL){
                    temp.push(node->right);
                }
            }
            q1 = temp;
            v1.push_back(sum*1.0/count);
        }
        return v1;
    }
};


// Method 2: Iterative BFS using One Queue
   /*
     public List<Double> averageOfLevels(TreeNode root) {
        List<Double> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        double sum = 0;
        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; ++i) {
                TreeNode node = q.poll();
                sum += node.val;
                if (node.left != null) {
                    q.add(node.left);
                }
                
                if (node.right != null) {
                    q.add(node.right);
                }
            }
            
            result.add(sum / size);
            sum = 0;
        }
        
        return result;
    }
   */


// Method 2: Iterative DFS
// Use a variable l to indicate the level depth. Store the counter and sum for each level in the 
// corresponding index.
   /*
     class Solution {
public:
    vector<double> averageOfLevels(TreeNode* root) {
        vector<int> counts;
        vector<double> avgs;
        average(root, counts, avgs, 0);
        for (int i = 0; i < avgs.size(); i++) {
            avgs[i] /= counts[i];
        }
        return avgs;
    }
private:
    void average(TreeNode* root, vector<int>& counts, vector<double>& sums, int l) {
        if (!root) {
            return;
        }
        if (counts.size() <= l) {
            counts.push_back({});
        }
        if (sums.size() <= l) {
            sums.push_back({});
        }
        counts[l]++;
        sums[l] += root -> val;
        average(root -> left, counts, sums, l + 1);
        average(root -> right, counts, sums, l + 1);
    }
};
   */