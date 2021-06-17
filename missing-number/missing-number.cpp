class Solution {
public:
    int missingNumber(vector<int>& nums) {
        if(nums.size()==1 && nums[0]==0){
            return 1;
        }
        int b = nums.size();
        while(b>=0){
            if(std::find(nums.begin(),nums.end(),b) == nums.end()){
                return b;
            }
            b--;
        }
        return 0;
    }
};