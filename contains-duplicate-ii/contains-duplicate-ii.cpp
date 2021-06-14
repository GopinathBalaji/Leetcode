class Solution {
public:
    bool containsNearbyDuplicate(vector<int>& nums, int k) {
    //    for(int i =0;i<nums.size();i++){
    //        for(int j=0;j<nums.size();i++){
    //            if(abs(j-i)<=k && nums[i]==nums[j] && i!=j){
    //                return true;
    //            }
    //        }
    //    }
    //     return false;
        
        unordered_set<int> s;
        for(int i=0;i<nums.size();i++){
            if(s.find(nums[i]) != s.end()){
                return true;
            }
            s.insert(nums[i]);
            if(i>=k){
                s.erase(nums[i-k]);
            }
        }
     return false;
    }   
};