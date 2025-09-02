// Two Pointer Approach (fast/slow)
/*
Keep i as the write index (next position to put a new unique).
Scan with j. When you see a new value, write it at i and advance i.

Why it works:
nums[0..i-1] is always the de-duplicated prefix. Because the array is sorted, a new unique occurs exactly when nums[j] != nums[i-1].
*/
class Solution {
    public int removeDuplicates(int[] nums) {
       
        int i = 1;

        for(int j=0; j<nums.length; j++){
            if(nums[j] != nums[i-1]){
                nums[i] = nums[j];
                i++;
            }
        }

        return i;
    }
}