class Solution {
    public int jump(int[] nums) {
        if(nums.length < 2){
            return 0;
        }
        
        int jumps = 0;
        int currentEnd = 0;
        int furthest = 0;

        for(int i=0;i<nums.length-1;i++){
            furthest = Math.max(furthest, i + nums[i]);

            if(i == currentEnd){
                jumps ++;
                currentEnd = furthest;

                if(currentEnd >= nums.length-1){
                    break;
                }
            }
        }

        return jumps;
    }
}

// Explanation
// furthest always tracks the best place you could land with one more jump.
// When i hits currentEnd, you know there’s no way to reach beyond it without spending another jump, so you increment jumps and extend currentEnd to that furthest.
// You stop as soon as currentEnd reaches the last index, guaranteeing that jumps is the minimum number needed.