// Method 1: Binary Search over closed interval
/*
*/
class Solution {
public:
    int mySqrt(int x) {
        if(x <= 1){
            return x;
        }

        int left = 1;
        int right = x;
        int ans = 0;

        while(left <= right){
            int mid = left + (right - left) / 2;

            if(1LL * mid * mid <= x){
                left = mid + 1;
                ans = mid;
            }else{
                right = mid - 1;
            }
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna