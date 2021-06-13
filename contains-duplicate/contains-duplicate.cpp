class Solution {
public:
    bool containsDuplicate(vector<int>& nums) {
        set<int> s;
        int len1 = nums.size();
        for(int i=0;i<nums.size();i++){
            s.insert(nums[i]);
        }
        int len2 = s.size();
        if(len1!=len2){
            return true;
        }
        return false;
    }
};