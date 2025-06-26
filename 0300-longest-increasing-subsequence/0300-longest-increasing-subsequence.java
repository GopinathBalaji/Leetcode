// Top Down DP Approach
/*
We are not using a 1D array because of the following reason:
If we only memoizing on currIdx, but your result also depends on prevIdx. Two calls: dp(nums, memo, 3, -1) and dp(nums, memo, 3, 1)
will produce different answers (because in one case you can immediately take nums[3], in the other you can’t) — yet you only have a single slot memo[3] to cache both!

We don't recur inside a loop because of the following reason:
If we loop over i runs from 0…nums.length, rather than from currIdx+1 to the end. That means you’re reconsidering all earlier elements (and even nums[currIdx] itself) on every call, rather than only looking “forward.”


Why this solution works
State: (curr, prev) fully describes what remains to be decided and the last value you picked.

Choices: at each curr, you either skip it or, if valid, take it and advance prev to curr.

Memo: a 2D array keyed by both indices avoids recomputing the same subproblem twice.

Complexity: you fill at most n × (n+1) states, each in O(1) work → O(n²) time and O(n²) space.
*/
class Solution {
    public int lengthOfLIS(int[] nums) {
        // dp[idx][prev+1] caches the result of dp(idx, prev)
        // we offset prevIdx by +1 so that prevIdx == -1 maps to slot 0.
        int[][] memo = new int[nums.length][nums.length + 1];
        for(int[] row : memo){ 
            Arrays.fill(row, -1);  // -1 means “not computed yet”
        }
        return dp(nums, memo, 0, -1);
    }

    public int dp(int[] nums, int[][] memo, int currIdx, int prevIdx){
        if(currIdx == nums.length){
            return 0;
        }
        if(memo[currIdx][prevIdx + 1] != -1){
            return memo[currIdx][prevIdx + 1];
        }
        
        // 1) Option A: skip nums[curr]
        int best = dp(nums, memo, currIdx + 1, prevIdx);

        // 2) Option B: take nums[curr], if valid
        if(prevIdx < 0 || nums[currIdx] > nums[prevIdx]){
            best = Math.max(best, 1 + dp(nums, memo, currIdx + 1, currIdx));
        }

        // store and return
        return memo[currIdx][prevIdx + 1] = best;
    }
}

// Bottom Up DP Approach
/*
State definition
Define an array dp of length n, where:
dp[i] = length of the longest strictly increasing subsequence that ends exactly at index i

Initialization

For any single index i, the subsequence consisting of just nums[i] has length 1.

Hence we start by setting dp[i] = 1 for all i.

Recurrence
To compute dp[i], we look at all earlier indices j < i:

If nums[j] < nums[i], then any increasing subsequence ending at j can be extended by nums[i].

That candidate length is dp[j] + 1.

We take the maximum over all such valid j:
dp[i] = max(dp[j] + 1)  over all j < i with nums[j] < nums[i],
or 1 if no such j exists.

Filling order

We fill dp[0] first (trivially 1), then dp[1], up through dp[n−1].

By the time we compute dp[i], all dp[j] for j < i are already known.

Tracking the answer

The longest increasing subsequence in the entire array might end at any index, so we keep a running maxLen = max(maxLen, dp[i]) as we go.

At the end, maxLen is the length of the LIS across the whole array.

Time & space complexity

Time: O(n²), because for each i (n of them) we loop over all earlier j (up to n more).

Space: O(n) for the dp array.
*/

// class Solution {
//     public int lengthOfLIS(int[] nums) {
//         if (nums == null || nums.length == 0) {
//             return 0;                  // no elements → no subsequence
//         }
//         int n = nums.length;
//         // dp[i] = length of the longest increasing subsequence **ending** at index i
//         int[] dp = new int[n];
//         // Every element by itself is an LIS of length 1
//         Arrays.fill(dp, 1);

//         int maxLen = 1;                 // overall best
    
//         // Build dp[] in increasing order of i
//         for (int i = 1; i < n; i++) {
//             // Look at all j < i to see if we can append nums[i] after nums[j]
//             for (int j = 0; j < i; j++) {
//                 if (nums[j] < nums[i]) {
//                     // If nums[i] can extend the LIS ending at j,
//                     // consider that candidate length
//                     dp[i] = Math.max(dp[i], dp[j] + 1);
//                 }
//             }
//             // Keep track of the global maximum
//             maxLen = Math.max(maxLen, dp[i]);
//         }

//         return maxLen;
//     }
// }
