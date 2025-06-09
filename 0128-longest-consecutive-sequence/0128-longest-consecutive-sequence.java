// MY CODE (commented because leetcode gives TLE for some reason)
// class Solution {
//     public int longestConsecutive(int[] nums) {

//         Set<Integer> set = new HashSet<>();

//         for(int i=0;i<nums.length;i++){
//             set.add(nums[i]);
//         }

//         int ans = 0;
//         for(int num : nums){
//             if(!set.contains(num - 1)){
//                 int count = 1;
//                 int val = num + 1;
//                 while(set.contains(val)){
//                     count++;
//                     val++;
//                 }
//                 ans = Math.max(ans, count);
//             }
//         }

//         return ans;
//     }
// }

// GPT code
class Solution {
    public int longestConsecutive(int[] nums) {
        if (nums.length == 0) return 0;

        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num); // ensures uniqueness
        }

        int longestStreak = 0;

        for (int num : set) {
            // only start counting if it's the beginning of a sequence
            if (!set.contains(num - 1)) {
                int currentNum = num;
                int currentStreak = 1;

                while (set.contains(currentNum + 1)) {
                    currentNum++;
                    currentStreak++;
                }

                longestStreak = Math.max(longestStreak, currentStreak);
            }
        }

        return longestStreak;
    }
}
