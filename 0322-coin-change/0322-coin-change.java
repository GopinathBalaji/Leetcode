// Top Down DP
/*
Explanation
State (dp(rem))
The function dp(coins, rem, memo) returns the minimum number of coins needed to form the amount rem, or -1 if it’s impossible.

Memoization array

We size memo to amount+1 so we can index directly by remainder rem.

We fill it with -2 to mean “not yet computed.” Once computed, it holds either -1 (impossible) or a nonnegative coin‐count.

Base cases

rem < 0 → no solution, return -1.

rem == 0 → zero coins needed, return 0.

Recursive recurrence
For each coin value c, we ask:


int sub = dp(coins, rem - c, memo);
If sub >= 0, that means we can form rem - c with sub coins, so one more coin (the c we just used) gives sub + 1 total. We take the minimum over all coins.

Caching results
Before returning, we store:


memo[rem] = (minCoins == Integer.MAX_VALUE ? -1 : minCoins);
so that any later call to dp with the same rem skips recomputation and returns in O(1).

Overall complexity

Time: O(amount × number_of_coins), since each rem from 0..amount is computed once, and each computation loops through coins.

Space: O(amount) for the memo array plus recursion stack of depth up to O(amount).
*/
class Solution {
    public int coinChange(int[] coins, int amount) {
        // We size memo to amount+1 so we can index directly by remainder rem.
        // memo[r] will hold:
        //   -2 if we haven't computed dp(r) yet,
        //   -1 if it's impossible to make r,
        //   otherwise the minimum coins needed for amount = r.
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -2);
        return dp(coins, amount, memo);
    }

    int dp(int[] coins, int rem, int[] memo){
        if(rem < 0){
            return -1;  // no solution
        }

        // Return cached result if we’ve seen this remainder before
        if(rem == 0){
            return 0;   // zero coins needed
        }

        if(memo[rem] != -2){
            return memo[rem];
        }
        
        int minCoins = Integer.MAX_VALUE;
        // Try using each coin as the last coin
        for(int coin : coins){
            int res = dp(coins, rem - coin, memo);
            if(res >= 0 && res < minCoins){
                minCoins = res + 1;
            }
        }

        // If we never found a valid way, record -1; else record the min
        memo[rem] = (minCoins == Integer.MAX_VALUE ? -1 : minCoins);
        return memo[rem];
    }
}

// Bottom Up DP
/*
1. Defining the DP state
dp[i] represents the fewest number of coins needed to make up exactly the amount i.

We build this up for every i from 0 to amount.

2. Initialization
dp[0] = 0: zero coins are required to make an amount of 0.

dp[i] = amount+1 for i > 0: we fill with a sentinel value larger than any possible solution (you can never need more than amount coins of denomination 1), effectively modelling “infinite” or “not yet reachable.”

3. Recurrence relation
To compute dp[i], consider every coin c:

If c > i, you can’t use that coin for amount i.

Otherwise, if you do use coin c as your last coin, you pay 1 coin plus whatever best solution you already have for the remaining amount i - c. That is:


candidate = dp[i - c] + 1
You take the minimum over all such candidates:


dp[i] = min(dp[i], dp[i - c] + 1)
By iterating over coins inside the loop for i, you ensure you’ve already computed every dp[i - c] (since i-c < i).

4. Final answer and “no solution” check
After filling the array, if dp[amount] remains greater than amount, it means no coin combination ever lowered it from its sentinel. In that case, return -1.

Otherwise, dp[amount] holds the minimum number of coins needed.

5. Time & Space Complexity
Time: You have two nested loops:

Outer loop runs i = 1…amount, so O(amount) iterations.

Inner loop iterates over each c in coins, so O(#coins) per i.

Overall O(amount × #coins).

Space: One array of size amount+1, so O(amount) extra space
*/

// class Solution {
//     public int coinChange(int[] coins, int amount) {
//         // 1) dp[i] = minimum coins needed to make amount i,
//         //    with dp[0] = 0 (zero coins to make zero)
//         int[] dp = new int[amount + 1];
//         Arrays.fill(dp, amount+1); // use amount+1 as mock “infinity”
//         dp[0] = 0;

//         // 2) Build the table from 1 up to amount
//         for(int i=1;i<=amount;i++){
//             // Try every coin for this sub-amount i
//             for(int c : coins){

//                 // If c > i, you can’t use that coin for amount i
//                 if(c <= i){

//                     // If we use coin c, we need 1 coin plus whatever best solution you already have for the remaining amount i - c
//                     dp[i] = Math.min(dp[i], dp[i-c] + 1);
//                 }
//             }
//         }

//         // 3) If dp[amount] is still “infinite,” no solution exists
//         return (dp[amount] > amount) ? -1 : dp[amount];
//     }
// }