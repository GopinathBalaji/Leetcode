class Solution {
public:
//     Method 1
//     loop over the length of array, rather than the candidate numbers, and generate all combinations 
//      for a given length with the help of backtracking technique. This means we need to do i+1 rather 
//       than index+1 when backtracking. This is because we want to generate unique subsets, so if we do
//       index+1 we will get many redundant subsets
    
    vector<vector<int>> subsets(vector<int>& nums) {
        vector<vector<int>> res;
        vector<int> v;
        int index = 0;
        search(index,nums.size(),v,nums,res);
        return res;
    }
    
    // bool isValidState(int size,vector<int> &v){}
    
    // bool isSafeCandidate(){}
    
    void search(int index,int size,vector<int> &v, vector<int> &nums, vector<vector<int>> &res){
            res.push_back(v);
        for(int i=index;i<nums.size();i++){            
            v.push_back(nums[i]);
            search(i+1,size,v,nums,res);
            v.pop_back();
        }
    }
};
