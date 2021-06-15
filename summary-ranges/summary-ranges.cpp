class Solution {
public:
    vector<string> summaryRanges(vector<int>& nums) {
        if(nums.empty()){
            return {};
        }
        vector<string> vec;
        if(nums.size()==1){
            vec.push_back(to_string(nums[0]));
            return vec;
        }
        
       for(int i=0;i<nums.size();i++){
           int j=i;
           
           while(j<nums.size()-1 && nums[j]+1 == nums[j+1]){
               j++;
           }
           if(i==j){
               vec.push_back(to_string(nums[i]));
           }else{
               vec.push_back(to_string(nums[i])+"->"+to_string(nums[j]));
           }
       i=j;
       }
        return vec;
    }
};