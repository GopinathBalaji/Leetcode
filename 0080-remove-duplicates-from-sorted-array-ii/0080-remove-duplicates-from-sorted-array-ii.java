// Two Pointer Approach
/*
The key comparison is not “current vs some moving j”. 
For “keep at most two,” the decisive check is current vs the element 
two positions before the write index.

Example:
[0,0,1,1,1,2,2,3,3,3]
Start w=2.

    i=2 (1): compare to nums[w-2]=nums[0]=0 → different → write, w=3.
    i=3 (1): compare to nums[1]=0 → different → write, w=4.
    i=4 (1): compare to nums[2]=1 → equal → skip (3rd 1).
    … ends with prefix [0,0,1,1,2,2,3,3], length 8.
*/
class Solution {
    public int removeDuplicates(int[] nums) {
        if(nums.length <= 2){
            return nums.length;
        }

        int writeIndex = 2;
        for(int i=2; i<nums.length; i++){
            if(nums[i] != nums[writeIndex-2]){
                nums[writeIndex] = nums[i];
                writeIndex++;
            }
        }

        return writeIndex;
    }
}


// Generalization for “keep at most k duplicates”
/*
Maintain a write index w for the de-duplicated prefix. For each value nums[i]:
    Accept it if either w < k (we haven’t filled k slots yet) or nums[i] != nums[w - k].
    Otherwise it’d be the (k+1)-th copy → skip.

Why this works: in a sorted array, the (k+1)-th copy of a value is equal to the element exactly k positions behind the write index.
*/
// public int removeDuplicatesAtMostK(int[] nums, int k) {
//     if(k <= 0){
//         return 0;
//     }

//     for(int x: nums){
//         if(w < k || x != nums[w-k]){
//             nums[w++] = x;
//         }
//     }

//     return w;
// }