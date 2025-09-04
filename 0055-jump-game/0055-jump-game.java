// Forward Greedy Solution (track farthest reach)
/*
Keep the farthest index we can reach so far. If we ever stand at an index beyond that, we’re stuck.

Why it works:

Every time you visit an index you could reach, update how far you can go next. If you encounter an index you can’t reach, there’s no path.

Walkthrough on [3,2,1,0,4]:
i=0 → maxReach=3
i=1 → maxReach=max(3,1+2)=3
i=2 → maxReach=max(3,2+1)=3
i=3 → maxReach=max(3,3+0)=3
i=4 is > maxReach → return false.
*/
class Solution {
    public boolean canJump(int[] nums) {
        int maxReach = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) return false;                 // can't even get here
            maxReach = Math.max(maxReach, i + nums[i]);     // extend reach
            if (maxReach >= nums.length - 1) return true;   // early success
        }
        return true;
    }
}


// Backwards Greedy (move the goal left) (DP with state compression)
/*
Track the leftmost index that can reach the end. Initialize goal = n-1; 
scan left: if i + nums[i] >= goal, move goal = i. At the end, check goal == 0.
*/
// class Solution {
//     public boolean canJump(int[] nums) {
//         int goal = nums.length - 1;
//         for (int i = nums.length - 2; i >= 0; i--) {
//             if (i + nums[i] >= goal) goal = i;
//         }
//         return goal == 0;
//     }
// }



// Iterative Dynamic Programming (O(n²) time, O(n) space)
/*
Why it works:
Each dp[i] asks: “is any position I can jump to from i good (i.e., can itself reach the end)?” If yes, i is also good.

Complexity:
Time: worst-case O(n²) (inner loop over jump range).
Space: O(n) for dp.

Example: [1,0,2]

dp[2] = true

i=1: far = min(2, 1+0) = 1 → there’s no j in [2..1] → dp[1]=false

i=0: far = min(2, 0+1) = 1 → check dp[1]=false → dp[0]=false
Return dp[0]=false (correct: from 0 you can only reach 1, which is dead).
*/
// class Solution {
//     public boolean canJump(int[] nums) {
//         int n = nums.length;
//         if (n <= 1) return true;

//         boolean[] dp = new boolean[n];
//         dp[n - 1] = true; // base case

//         for (int i = n - 2; i >= 0; i--) {
//             int far = Math.min(n - 1, i + nums[i]);
//             boolean can = false;
//             for (int j = i + 1; j <= far; j++) {
//                 if (dp[j]) {
//                     can = true;
//                     break; // found a reachable good spot
//                 }
//             }
//             dp[i] = can;
//         }
//         return dp[0];
//     }
// }




// Recursive Top-Down DP
/*
# Core idea (in words)

Define `can(i)` = “from index `i`, can I reach the last index (or beyond)?”

* Base:

  * If `i >= n-1` → already at/past the last index → **true**.
* Transition:

  * From `i`, you may jump to any `j` in `[i+1, min(n-1, i + nums[i])]`.
  * If **any** such `j` satisfies `can(j) == true`, then `can(i) = true`, else `false`.

Use a memo so each `i` is solved once:

* `UNKNOWN = -1`, `BAD = 0`, `GOOD = 1`.

Small optimization: try farther jumps first; reaching the end earlier prunes work.

**What’s happening:**

* `dfs(i)` explores all reachable next positions from `i`.
* Memoization ensures each index becomes **GOOD** or **BAD** once, avoiding exponential blow-up.

---

# Walkthrough 1 (reachable): `[2,3,1,1,4]`

`n=5`, target index `4`.

* `dfs(0)`: `furthest = min(4, 0+2) = 2` → try `j=2`, then `j=1`

  * `dfs(2)`: `furthest = min(4, 2+1) = 3` → try `j=3`

    * `dfs(3)`: `furthest = min(4, 3+1) = 4` → try `j=4`

      * `dfs(4)`: `i >= 4` → **true** → mark `3` GOOD → return true
    * `dfs(2)` sees a true child → mark `2` GOOD → return true
  * `dfs(0)` sees a true child (`2`) → mark `0` GOOD → return **true**

(With the “farthest-first” loop, it actually finds `0→1→4` quickly too; either way, `true`.)

---

# Walkthrough 2 (not reachable): `[3,2,1,0,4]`

`n=5`, target index `4`.

* `dfs(0)`: `furthest = min(4, 0+3) = 3` → try `j=3,2,1`

  * `dfs(3)`: `furthest = min(4, 3+0) = 3` → no next `j` → mark `3` BAD → false
  * `dfs(2)`: `furthest = min(4, 2+1) = 3` → only `j=3` (BAD) → mark `2` BAD → false
  * `dfs(1)`: `furthest = min(4, 1+2) = 3` → try `j=3,2` (both BAD) → mark `1` BAD → false
* All children of `0` are BAD → mark `0` BAD → return **false**.

---

# Complexity

* **Time:** At most each pair `(i, j)` is considered once before memo marks `i` as GOOD/BAD. In worst case, **O(n²)**; in practice often much faster with farthest-first pruning.
* **Space:** O(n) for the memo + O(n) recursion stack in the worst case.

---

# When to use this

* Great for explaining the **DP reasoning** (“good”/“bad” indices) and pruning via memoization.
* For the most efficient solution in interviews, mention the **O(n) greedy** (`maxReach`) or **compressed DP goal-pointer** after presenting this.
*/
// class Solution {
//     // memo[i] = -1 (UNKNOWN), 0 (BAD), 1 (GOOD)
//     private int[] memo;
//     private int[] nums;

//     public boolean canJump(int[] nums) {
//         int n = nums.length;
//         if (n <= 1) return true;         // 0 or 1 element: trivially reachable

//         this.nums = nums;
//         this.memo = new int[n];
//         for (int i = 0; i < n; i++) memo[i] = -1;

//         return dfs(0);
//     }

//     private boolean dfs(int i) {
//         int n = nums.length;

//         // Base: at or beyond the last index → success
//         if (i >= n - 1) return true;

//         // If we've solved this index before, reuse the result
//         if (memo[i] != -1) return memo[i] == 1;

//         int furthest = Math.min(n - 1, i + nums[i]);

//         // Try jumps from farthest to nearest to find success sooner
//         for (int j = furthest; j > i; j--) {
//             if (dfs(j)) {
//                 memo[i] = 1;             // mark GOOD
//                 return true;
//             }
//         }

//         memo[i] = 0;                     // mark BAD
//         return false;
//     }
// }
