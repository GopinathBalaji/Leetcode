// Method 1: Using HashMap
/*
*/
class Solution {
    public boolean containsDuplicate(int[] nums) {
        HashMap<Integer, Integer> map = new HashMap<>();

        for(int num: nums){
            int count =  map.getOrDefault(num, 0) + 1;
            
            if(count > 1){
                return true;
            }

            map.put(num, count);
        }

        return false;
    }
}




// Method 2: Using Set
/*
*/
// class Solution {
//     public boolean containsDuplicate(int[] nums) {
//         HashSet<Integer> seen = new HashSet<>();
//         for (int num : nums) {
//             if (!seen.add(num)) return true; // add returns false if already present
//         }
//         return false;
//     }
// }




// Method 3: Sorting Approach
/*
*/
// class Solution {
//     public boolean containsDuplicate(int[] nums) {
//         Arrays.sort(nums);              // O(n log n)
//         for (int i = 1; i < nums.length; i++) {
//             if (nums[i] == nums[i - 1]) {
//                 return true;
//             }
//         }
//         return false;
//     }
// }
