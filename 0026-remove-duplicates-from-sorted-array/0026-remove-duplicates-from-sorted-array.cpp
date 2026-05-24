// Method 1: Two-pointer approach
/*
Keep k as the write index (next position to put a new unique).
Scan with i. When you see a new value, write it at k and advance k.
*/
class Solution {
public:
    int removeDuplicates(vector<int>& nums) {
        int n = nums.size();
        if(n == 0){
            return 0;
        }

        int k = 1;

        for(int i=1; i<n; i++){
            if(nums[i] != nums[i-1]){
                nums[k] = nums[i];
                k++;
            }
        }

        return k;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna