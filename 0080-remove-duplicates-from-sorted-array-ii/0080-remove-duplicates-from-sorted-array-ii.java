class Solution {
    public int removeDuplicates(int[] nums) {
        int pointer = 2;

        for(int i=2;i<nums.length;i++){
            if(nums[i] != nums[pointer-2]){
                nums[pointer] = nums[i];
                pointer++;
            }
        }
        return pointer;
    }
}