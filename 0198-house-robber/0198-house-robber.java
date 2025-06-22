// Top-Down DP
/*
Think of the problem in terms of a single parameter: which house you’re about to consider next. That index is your “state,” and the value you store for each state is “the maximum amount I can rob from this house through the last one.”

1. Define your state clearly
Let dp(i) = the best you can do on the suffix of the street starting at house i (i.e. houses i, i+1, …, n–1).

2. Identify your choices (transitions)
At house i, you have exactly two options:

Rob it, take nums[i], and then you must skip the next house, so you add dp(i+2).

Skip it, and move on to consider house i+1, i.e. take dp(i+1) with no immediate reward.

So the recurrence is simply

lua
Copy code
dp(i) = max( nums[i] + dp(i+2),
             dp(i+1) )
3. Establish base cases
If i ≥ n (you’ve walked past the last house), there’s nothing left to rob, so dp(i) = 0.

4. Memoize to avoid re-work
Without caching, a naïve recursion will revisit the same dp(k) dozens of times (exponential blow-up). By storing each computed dp(i) in an array (or map), you ensure that each state is solved exactly once, giving you an O(n) algorithm.

5. Top-down “flow”
You start by asking for dp(0) (the very first state).

That in turn asks for dp(1) and dp(2), each of which asks further smaller values, until you hit the base case (i ≥ n).

Once dp(k) is computed, it’s stored; future calls for dp(k) return immediately from the cache.

The final answer bubbles back up to your original dp(0) call.
*/
class Solution {
    public int rob(int[] nums) {
        int[] memo = new int[nums.length];
        Arrays.fill(memo, -1);
        int house = 0;
        return dp(nums, memo, house);
    }

    public int dp(int[] nums, int[] memo, int house){
        if(house >= nums.length){
            return 0;
        }
        if(memo[house] != -1){
            return memo[house];
        }

        memo[house] = Math.max(nums[house] + dp(nums, memo, house + 2), dp(nums, memo, house + 1));

        return memo[house];
    }
}
// ////////////////////////////

// Bottom Up DP solution
/*
State definition
Let dp[i] be the maximum amount you can rob from the subarray of houses 0 through i (inclusive).

Base cases

dp[0] = nums[0] — with only the first house available, you either rob it or get 0.

dp[1] = max(nums[0], nums[1]) — with two houses, you choose the richer one.

Recurrence
For each house i ≥ 2, you have two choices:

Skip house i: you take whatever was optimal up to i-1, i.e. dp[i-1].

Rob house i: you add its value nums[i] to the best you could do up to i-2, i.e. dp[i-2] + nums[i].

So

dp[i] = max( dp[i-1], 
             dp[i-2] + nums[i] )
Filling order
You fill dp[0] and dp[1] first, then iterate i=2…n-1. By the time you compute dp[i], both dp[i-1] and dp[i-2] are known.

Answer
The value dp[n-1] represents the maximum robbery amount for the entire array of n houses.
*/
// class Solution {
//     public int rob(int[] nums) {
//         int n = nums.length;

//         if(n == 0){
//             return 0;
//         }
//         if(n == 1){
//             return nums[0];
//         }
        
//         // dp[i] = max amount you can rob from houses[0..i]
//         int[] dp = new int[n];
//         dp[0] = nums[0];
//         dp[1] = Math.max(nums[0], nums[1]);

//         for(int i=2;i<n;i++){
//             // Either skip house i (dp[i-1]) or rob it (+ nums[i] + dp[i-2])
//             dp[i] = Math.max(dp[i-1], dp[i-2] + nums[i]);
//         }

//         return dp[n-1];
//     }
// }

