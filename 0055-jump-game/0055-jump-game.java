class Solution {
    public boolean canJump(int[] nums) {
        if(nums.length == 1){
            return true;
        }
        int maxInterval = 0;
        maxInterval = Math.max(maxInterval, nums[0]);
        for(int i=1;i<nums.length;i++){
            if(i <= maxInterval){
                maxInterval = Math.max(maxInterval, i + nums[i]);
                if(maxInterval >= nums.length-1){
                    return true;
                }
            }
        }

        return false;
    }
}