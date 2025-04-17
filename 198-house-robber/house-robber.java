// Bottom-Up with Memo
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;
        if(n == 1){
            return nums[0];
        }

        int prev2 = nums[0];
        int prev1 = Math.max(nums[0], nums[1]);

        for(int i=2;i<n;++i){
            int cur = Math.max(nums[i] + prev2, prev1);
            prev2 = prev1;
            prev1 = cur;
        }

        return prev1;
    }
}


// ###############################################
// Top-Down (Recursion with Memo)
// class Solution {
//     public int rob(int[] nums) {
//         int n = nums.length;
//         int[] memo = new int[n];
//         Arrays.fill(memo, -1);

//         return dp(nums, n-1, memo);
//     }

//     public int dp(int[] nums, int i, int[] memo){
//         if(i<0){
//             return 0;
//         }

//         if(memo[i] != -1){
//             return memo[i];
//         }

//         int robCur = dp(nums, i-2, memo) + nums[i];
//         int skipCur = dp(nums, i-1, memo);
//         memo[i] = Math.max(robCur, skipCur);

//         return memo[i];
//     }
// }