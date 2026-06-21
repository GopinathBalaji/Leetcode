// Method 1: Binary Search (Half-Open Approach)
/*
*/
class Solution {
public:
    int searchInsert(vector<int>& nums, int target) {
        int n = nums.size();

        int left = 0;
        int right = n;
        
        int mid = 0;

        while(left < right){
            mid = left + (right - left) / 2;

            if(nums[mid] == target){
                return mid;
            }else if(target < nums[mid]){
                right = mid;
            }else{
                left = mid + 1;
            }
        }

        return left;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna