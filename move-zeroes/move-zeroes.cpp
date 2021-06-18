class Solution {
public:
    void moveZeroes(vector<int>& nums) {
         int initial = nums.size();
        nums.erase(std::remove(nums.begin(),nums.end(),0),nums.end());
        int fin = nums.size();
        int act = initial - fin;
        for(int i=0;i<act;i++){
            nums.push_back(0);
        }
    }
};