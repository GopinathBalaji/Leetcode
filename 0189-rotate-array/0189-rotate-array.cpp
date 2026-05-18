// Method 1: 3-reverse method
/*
Why it works (right rotation by k):
Reversing all puts the last k elements in front but backwards; 
then reversing the first k fixes that block; reversing the rest fixes the tail.
Example [1,2,3,4,5,6,7], k=3 → reverse all → [7,6,5,4,3,2,1] → reverse first 3 → [5,6,7,4,3,2,1] → reverse last 4 → [5,6,7,1,2,3,4].

If k >= n, end = k - 1 can go out of bounds. You must do k %= n (and handle k == 0 early).
*/
class Solution {
public:
    void rotate(vector<int>& nums, int k) {
        int n = nums.size();
        k = k % n;

        if(k == 0){
            return;
        }

        reverse(nums, 0, n-1);
        reverse(nums, 0, k-1);
        reverse(nums, k, n-1);
    }

    void reverse(vector<int>& nums, int l, int r){
        while(l < r){
            int temp = nums[r];
            nums[r] = nums[l];
            nums[l] = temp;

            l++;
            r--;
        }
    }
};




// Method 2:  Extra space approach
/*
For each index i, move nums[i] to:

(i + k) % n
*/
// class Solution {
// public:
//    void rotate(vector<int>& nums, int k) {
//        int n = nums.size();

//        // If k is larger than n, reduce it
//        k = k % n;

//        vector<int> temp(n);

//        // Move each element to its rotated position
//        for (int i = 0; i < n; i++) {
//            int newIndex = (i + k) % n;
//            temp[newIndex] = nums[i];
//        }

//        // Copy back to nums
//        for (int i = 0; i < n; i++) {
//            nums[i] = temp[i];
//        }
//    }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna