// Method 1: Top-Down DP
/*
### Why this works (quick recap)

* **State:** `dp(i)` = best you can do from house `i` onward.
* **Choices:** rob `i` → `nums[i] + dp(i+2)`; skip `i` → `dp(i+1)`.
* **Base:** past the last index ⇒ 0.
* **Memoization:** prevents recomputation; `-1` avoids the “0 is unknown” bug.

### Walkthrough example

`nums = [2, 7, 9, 3, 1]`

* `dp(4) = max(1 + dp(6), dp(5)) = max(1, 0) = 1`
* `dp(3) = max(3 + dp(5), dp(4)) = max(3, 1) = 3`
* `dp(2) = max(9 + dp(4), dp(3)) = max(10, 3) = 10`
* `dp(1) = max(7 + dp(3), dp(2)) = max(10, 10) = 10`
* `dp(0) = max(2 + dp(2), dp(1)) = max(12, 10) = 12`

Each `dp(i)` is computed once, then reused.
**Time:** O(n) | **Space:** O(n) (recursion + memo).
*/
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;
        int[] memo = new int[n];           // only need size n (indices 0..n-1)
        Arrays.fill(memo, -1);             // -1 = unknown
        return dp(nums, 0, memo);
    }

    // dp(i) = max money from subarray nums[i..n-1]
    private int dp(int[] nums, int i, int[] memo) {
        if (i >= nums.length) return 0;    // base: no houses left

        if (memo[i] != -1) return memo[i]; // return cached

        int take = nums[i] + dp(nums, i + 2, memo); // rob i, skip i+1
        int skip = dp(nums, i + 1, memo);           // skip i

        memo[i] = Math.max(take, skip);    // store & return
        return memo[i];
    }
}




// Method 2: Bottom-Up DP with extra space
/*
## Intuition (bottom-up)

Let `dp[i]` be the **maximum money** you can rob from the first `i` houses (i.e., considering indices `0..i-1`).

* If you **skip** the i-th house: value is `dp[i-1]`.
* If you **rob** the i-th house (index `i-1`): you cannot take `(i-2)`th house, so value is `dp[i-2] + nums[i-1]`.
* Recurrence:
  `dp[i] = max(dp[i-1], dp[i-2] + nums[i-1])`

## Thorough walkthrough

Example: `nums = [2, 7, 9, 3, 1]`

We’ll show both perspectives (table and O(1) variables).

### Table (`dp[i]` = best for first `i` houses)

* `dp[0] = 0`
* `dp[1] = 2` (take the first house)

Now fill:

* `i = 2` (consider houses [2,7]):
  `dp[2] = max(dp[1], dp[0] + nums[1]) = max(2, 0 + 7) = 7`
* `i = 3` (consider [2,7,9]):
  `dp[3] = max(dp[2], dp[1] + nums[2]) = max(7, 2 + 9) = 11`
* `i = 4` (consider [2,7,9,3]):
  `dp[4] = max(dp[3], dp[2] + nums[3]) = max(11, 7 + 3) = 11`
* `i = 5` (consider all [2,7,9,3,1]):
  `dp[5] = max(dp[4], dp[3] + nums[4]) = max(11, 11 + 1) = 12`

Answer: `dp[5] = 12`.

### O(1) variables (`prev2 = dp[i-2]`, `prev1 = dp[i-1]`)

Start:

* `prev2 = 0` (dp[0])
* `prev1 = 2` (dp[1])

Loop i from 2..5:

* `i=2`:
  `take = prev2 + nums[1] = 0 + 7 = 7`
  `skip = prev1 = 2`
  `curr = 7` → update: `prev2=2`, `prev1=7`
* `i=3`:
  `take = 2 + 9 = 11`
  `skip = 7`
  `curr = 11` → update: `prev2=7`, `prev1=11`
* `i=4`:
  `take = 7 + 3 = 10`
  `skip = 11`
  `curr = 11` → update: `prev2=11`, `prev1=11`
* `i=5`:
  `take = 11 + 1 = 12`
  `skip = 11`
  `curr = 12` → update: `prev2=11`, `prev1=12`

Return `prev1 = 12`.

---

## Common pitfalls to avoid

* Off-by-one with indices (`nums[i-1]` aligns with `dp[i]`).
* Forgetting base cases for `n==0` and `n==1`.
* Accidentally using `dp[i-1] + nums[i-1]` (this would allow adjacent houses); must be `dp[i-2] + nums[i-1]`.
*/
// class SolutionArrayDP {
//     public int rob(int[] nums) {
//         int n = nums.length;
//         if (n == 0) return 0;
//         if (n == 1) return nums[0];

//         int[] dp = new int[n + 1];
//         dp[0] = 0;
//         dp[1] = nums[0];

//         for (int i = 2; i <= n; i++) {
               // Either skip house i (dp[i-1]) or rob it (+ nums[i] + dp[i-2])
//             dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i - 1]);
//         }
//         return dp[n];
//     }
// }






// Method 3: Bottom-Up DP with no extra space
/*
Base:

* `dp[0] = 0` (no houses ⇒ 0)
* `dp[1] = nums[0]` (best from first house)

We can compress space because each step only needs the **previous two** values.

### Why this is correct

* Each iteration computes `dp[i]` from `dp[i-1]` and `dp[i-2]`.
* `prev2` and `prev1` carry those two DP states forward, so memory is constant.
* Correctly enforces “no two adjacent houses” by using `+ nums[i-1]` only with `prev2` (i.e., skipping the immediate previous house).

### Complexity

* **Time:** `O(n)`
* **Space:** `O(1)`
*/
// class Solution {
//     public int rob(int[] nums) {
//         int n = nums.length;
//         if (n == 0) return 0;
//         if (n == 1) return nums[0];

//         // prev2 = dp[i-2], prev1 = dp[i-1]
//         int prev2 = 0;        // dp[0]
//         int prev1 = nums[0];  // dp[1]

//         for (int i = 2; i <= n; i++) {
//             // current = max(dp[i-1], dp[i-2] + nums[i-1])
//             int take = prev2 + nums[i - 1];  // rob current house (index i-1)
//             int skip = prev1;                 // skip current
//             int curr = Math.max(take, skip);

//             // slide the window
//             prev2 = prev1;
//             prev1 = curr;
//         }
//         return prev1; // dp[n]
//     }
// }
