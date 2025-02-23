class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        
        int minlen = Integer.MAX_VALUE;
        int j = 0;
        int cursum = 0;

        for(int i=0;i<nums.length;i++){
            cursum += nums[i];

            while(cursum >= target){
                if(i-j+1 < minlen){
                    minlen = i - j + 1;
                }
                cursum -= nums[j];
                j++;
            }
        }

        return minlen != Integer.MAX_VALUE ? minlen : 0;

    }
}