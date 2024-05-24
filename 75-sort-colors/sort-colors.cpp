// Method 1: Count Sort
// class Solution {
// public:
//     void sortColors(vector<int>& nums) {
//         int high = INT_MIN;
//         for(int i=0;i<nums.size();i++){
//             high = max(high, nums[i]);
//         }

//         vector<int> counts(high+1,0);

//         for(int i=0;i<nums.size();i++){
//             counts[nums[i]]++;
//         }
//         nums.erase();
//         for(int i=0;i<counts.size();i++){
//             for(int j=0;j<counts[i];j++){
//                 nums.push_back(j);
//             }
//         }

//     }
// };


// Method 2: 3 Pointers (Dutch National Flag Algorithm)
class Solution {
public:
    void sortColors(vector<int>& nums) {
        int low = 0;
        int mid = 0;
        int high = nums.size()-1;

        while(mid <= high){
            if(nums[mid]==0){
                swap(nums[low],nums[mid]);
                low++;
                mid++;
            }
            else if(nums[mid]==1){
                mid++;
            }
            else{
                swap(nums[mid],nums[high]);
                high--;
            }
        }

    }
};