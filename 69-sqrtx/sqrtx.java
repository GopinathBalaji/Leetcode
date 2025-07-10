// Binary search over the range. Square root can never be greater than half the number so high = num / 2
class Solution {
    public int mySqrt(int x) {
        if (x < 2) return x;

        int low = 1;
        int high = x / 2;
        int ans = 0;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (mid <= x / mid) { // avoid overflow
                ans = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return ans;
    }
}
