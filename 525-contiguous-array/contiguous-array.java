// Prefix Sum solution
class Solution {
    public int findMaxLength(int[] nums) {
        HashMap<Integer, Integer> prefixSum = new HashMap<>();
        int target = 0;
        prefixSum.put(0, 0); 
        // By pre-setting prefixSums.put(0, 0), we ensure that we handle the case where a 
        // subarray that starts at index 0 (the very beginning of the array) directly 
        // sums to the target. This is because in such a case, the prefix sum at that 
        // point minus the target would be 0, which will be found in the hashmap because
        //  of this initialization.
        int curSum = 0;
        int best = 0;
        for(int i=0;i<nums.length;i++){
            if(nums[i] == 0){
                curSum += -1;
            }else{
                curSum += nums[i];
            }

            int complement = curSum - target;
            if(prefixSum.containsKey(complement)){
                int prev = prefixSum.get(complement);
                best = Math.max(best, i+1 - prev); 
                // i + 1 because prev also contains a 1. So they cancel out
            }else{
                prefixSum.put(curSum, i + 1);
            }
        }

        return best;
    }
}