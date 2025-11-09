// Method 1: Kadane's Algorithm
/*
Conceptually you want two variables:

curr = best sum of subarray ending at i
bestSoFar = best sum of subarray seen so far
*/
class Solution {
    public int maxSubArray(int[] nums) {
        int bestSoFar = nums[0];
        int cur = nums[0];

        for(int i=1; i<nums.length; i++){
            cur = Math.max(nums[i], cur + nums[i]); // extend or restart
            bestSoFar = Math.max(bestSoFar, cur); // update global best
        }

        return bestSoFar;
    }
}