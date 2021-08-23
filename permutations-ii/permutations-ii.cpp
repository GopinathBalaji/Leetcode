class Solution {
public:
//     Method 1: Backtracking using Hashmap to keep track of the number of occurences of each number
// A key insight to avoid generating any redundant permutation is that at each step rather than 
// viewing each number as a candidate, we consider each unique number as the true candidate. For instance, 
// at the very beginning, given in the input of [1, 1, 2], we have only two true candidates instead of 
// three.
//   This is why we iterate over the hashmap in the search function and not the array. If we iterate over
//     the array then we may be using the value twice because of duplicates present in the array.
//       For better understanding refer to the Backtracking recursion tree.


    vector<vector<int>> permuteUnique(vector<int>& nums) {
        vector<vector<int>> res;
        vector<int> v;
        unordered_map<int,int> m1;
        for(int i=0;i<nums.size();i++){
            ++m1[nums[i]];
        }
        int size = nums.size();
        search(m1,nums,res,v,size);
        return res;
    }
    
    bool isValidState(vector<int> &v,int size){
        return v.size() == size;
    }
    
    // bool isSafeCandidate(){}
    
    void search(unordered_map<int,int> &m1,vector<int> &nums,vector<vector<int>> &res,vector<int> &v,int size){
        
        if(isValidState(v,size)){
            res.push_back(v);
            return;
        }
        
        for(auto [key,value]: m1){
            if(value==0){
                continue;
            }
            
            v.push_back(key);
            --m1[key];
            search(m1,nums,res,v,size);
            v.pop_back();
            ++m1[key];
        }
    }
};