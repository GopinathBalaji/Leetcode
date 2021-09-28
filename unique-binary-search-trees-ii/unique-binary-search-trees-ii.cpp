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
    vector<TreeNode*> generateTrees(int n) {
        int start = 1;
        int end = n;
        return solve(start,end);     
    }
    
    vector<TreeNode*> solve(int start, int end){
        
        vector<TreeNode*> temp;
        if(start > end){
            temp.push_back(NULL);
            return temp;
        }
        if(start == end){
            temp.push_back(new TreeNode(start));
            return temp;
        }
        
        for(int i=start;i<=end;i++){
            
            vector<TreeNode*> leftPossibleTrees = solve(start,i-1);
            vector<TreeNode*> rightPossibleTrees = solve(i+1,end);
            
            for(int j=0;j<leftPossibleTrees.size();j++){
                
                TreeNode* left = leftPossibleTrees[j];
                for(int k=0;k<rightPossibleTrees.size();k++){
                    TreeNode* right = rightPossibleTrees[k];
                    TreeNode* node = new TreeNode(i);
                    node->left = left;
                    node->right = right;
                    temp.push_back(node);
                }
            }
        }
        return temp;
    }
};