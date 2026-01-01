// Method 1: My approach (not very efficient)
/*
What’s “wrong” is mostly **style/efficiency**, plus one subtle edge-case note:

### 1) It’s two-pass (unnecessary extra work)

You build the map in one loop, then search in another. That’s still **O(n)** time, but you can do it in **one pass** and return earlier.

### 2) Overwrites indices for duplicates (usually fine, but worth understanding)

`map.put(nums[i], i)` keeps only the **last** index for each value.
This is generally fine because:

* If the solution uses two equal numbers (e.g., target=6, nums contains two 3s), the map will store the last 3, and when `i` is the first 3, you’ll return `(first3, last3)` correctly.
* If `i` is the last 3, you avoid using the same index via `idx2 != i`.

So it still works, but it’s not the cleanest way to reason about duplicates.

### 3) Can return indices in any order

You might return `{i, idx2}` where `idx2 < i`. That’s still accepted.
*/
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for(int i=0; i<nums.length; i++){
            map.put(nums[i], i);
        }

        for(int i=0; i<nums.length; i++){
            int required = target - nums[i];
            if(map.containsKey(required)){
                int idx2 = map.get(required);

                if(idx2 != i){
                    return new int[] {i, idx2};
                }
            }
        }

        return new int[] {-1, -1};
    }
}




// Method 2: Better, more efficient approach
/*
*/
// class Solution {
//     public int[] twoSum(int[] nums, int target) {
//         HashMap<Integer, Integer> map = new HashMap<>();

//         for(int i=0;i<nums.length;i++){
 
//             if(map.containsKey(target - nums[i])){
//                return new int[] {map.get(target - nums[i]), i};
//             }

//            map.put(nums[i], i);            
//         }

//         return new int[] {};
//     }
// }