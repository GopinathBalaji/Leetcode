class Solution {
public:
//     Method 1: Similar to standard Lynn Zheng template
//          Only difference is that you have to skip a iteration if the element is already present in the
//           permutation.
    
    vector<vector<int>> permute(vector<int>& nums) {
        vector<vector<int>> res;
        vector<int> v;
        int size = nums.size();
        search(nums,v,res,size);
        return res;
    }
    
    bool isValidState(vector<int> &v,int size){
        if(v.size()==size){
            return true;
        }
        return false;
    }
    
    // bool isSafeCandidate(){}
    
    void search(vector<int> &nums,vector<int> &v,vector<vector<int>> &res,int size){
        if(isValidState(v,size)){
            res.push_back(v);
            return;
        }
        for(int i=0;i<nums.size();i++){
            vector<int>::iterator it;  
            it = find(v.begin(),v.end(),nums[i]);
            if(it!=v.end()){
                continue;
            }
            
            v.push_back(nums[i]);
            search(nums,v,res,size);
            v.pop_back();
        }
    }
};

// Method 2: Inplace swap Backtracking
   /*
      void permute(vector<int> nums, int i, vector<vector<int>> &ans) {
        if(i == nums.size()) {
            ans.push_back(nums);
            return;
        }
        for(int j = i; j < nums.size(); j++) {
            swap(nums[i],nums[j]);
            permute(nums,i+1,ans);
            swap(nums[i],nums[j]);
        }
    }
    vector<vector<int>> permute(vector<int>& nums) {
        vector<vector<int>> ans;
        permute(nums,0,ans);
        return ans;
    }
   */