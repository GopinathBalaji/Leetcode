// Method 1: Top-Down DP Approach
/*
### What I was doing wrong:

1. **Out-of-bounds before checks.**
   You call `s1.charAt(i)` / `s2.charAt(j)` without ensuring `i < m` / `j < n`. When one string is fully consumed, this throws.

2. **Memo table too small (or mis-indexed).**
   You created `memo = new Boolean[m][n]`, but your valid states include `(m, j)` and `(i, n)` (when one string is exhausted). You either need a `(m+1)×(n+1)` table, or guard accesses so you *never* index `memo[m][*]` or `memo[*][n]`.

3. **Overwriting instead of OR-ing.**
   You do:

   ```java
   memo[i][j] = dp(i+1,j);   // if s1 matches
   memo[i][j] = dp(i,j+1);   // if s2 matches
   ```

   The second assignment erases the first; you need **logical OR** of both possibilities.

4. **No “false” write when neither side matches.**
   If neither branch matches, `memo[i][j]` stays `null`, and returning it can NPE due to auto-unboxing to `boolean`.

5. **Return of `Boolean` slot directly.**
   Returning `memo[i][j]` (a `Boolean`) relies on auto-unboxing; if it’s `null` you get an NPE. Always compute a local `boolean ans` and store it.


### Why this fixes it

* Guards `i < m`, `j < n` before `charAt`.
* Allows states `(m, j)` and `(i, n)` via `(m+1)×(n+1)` memo.
* Combines branches with **OR**, not overwrite.
* Always writes `true/false` to memo, so no `null` returns.

### Tiny sanity walkthrough

`s1="ab"`, `s2="cd"`, `s3="acbd"` → `dfs(0,0)`

* Match `a` from `s1` → `dfs(1,0)`
* Match `c` from `s2` → `dfs(1,1)`
* Match `b` from `s1` → `dfs(2,1)`
* Match `d` from `s2` → `dfs(2,2)` hits base case → `true`, which memo bubbles back up.
*/
class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();
        if (m + n != s3.length()) return false;

        Boolean[][] memo = new Boolean[m + 1][n + 1];
        return dfs(0, 0, s1, s2, s3, memo);
    }

    // dfs(i,j): can s1[i:] and s2[j:] form s3[i+j:] ?
    private boolean dfs(int i, int j, String s1, String s2, String s3, Boolean[][] memo) {
        int m = s1.length(), n = s2.length();
        if (i == m && j == n) return true;

        if (memo[i][j] != null) return memo[i][j];

        boolean ans = false;
        // take from s1
        if (i < m && s1.charAt(i) == s3.charAt(i + j)) {
            ans = dfs(i + 1, j, s1, s2, s3, memo);
        }
        // take from s2 (only try if not already true)
        if (!ans && j < n && s2.charAt(j) == s3.charAt(i + j)) {
            ans = dfs(i, j + 1, s1, s2, s3, memo);
        }
        return memo[i][j] = ans;
    }
}





// Method 2: Bottom-Up DP (O(m·n) time, O(m·n) space)
/*
## Idea

Let `dp[i][j]` be **true** iff the prefix `s3[0 .. i+j-1]` can be formed by interleaving `s1[0 .. i-1]` and `s2[0 .. j-1]`.

**Transition**

* If the last char of `s3` (at `i+j-1`) came from `s1`, then we must have
  `dp[i-1][j] == true` **and** `s1[i-1] == s3[i+j-1]`.
* Or it came from `s2`, then
  `dp[i][j-1] == true` **and** `s2[j-1] == s3[i+j-1]`.

## Walkthrough (classic true case)

`s1="aabcc" (m=5)`, `s2="dbbca" (n=5)`, `s3="aadbbcbcac"`

* `dp[0][0]=true`
* Seed first column with matches of `s1` against `s3`:

  * `dp[1][0] = s1[0]=a == s3[0]=a → true`
  * `dp[2][0] = s1[1]=a == s3[1]=a → true`
  * `dp[3][0] = s1[2]=b == s3[2]=d → false` (so the rest of this column becomes false)
* Seed first row with `s2` vs `s3`:

  * `dp[0][1] = s2[0]=d == s3[0]=a → false`
  * etc.

Then fill the table by the recurrence. You’ll end at `dp[5][5] = true`.
(Conversely, with `s3="aadbbbaccc"`, you’ll eventually get `dp[5][5] = false`.)

*/
// class Solution {
//     public boolean isInterleave(String s1, String s2, String s3) {
//         int m = s1.length(), n = s2.length();
//         if (m + n != s3.length()) return false;

//         boolean[][] dp = new boolean[m + 1][n + 1];
//         dp[0][0] = true;

//         // First column: only s1 contributes
//         for (int i = 1; i <= m; i++) {
//             dp[i][0] = dp[i - 1][0] && (s1.charAt(i - 1) == s3.charAt(i - 1));
//         }
//         // First row: only s2 contributes
//         for (int j = 1; j <= n; j++) {
//             dp[0][j] = dp[0][j - 1] && (s2.charAt(j - 1) == s3.charAt(j - 1));
//         }

//         for (int i = 1; i <= m; i++) {
//             for (int j = 1; j <= n; j++) {
//                 char need = s3.charAt(i + j - 1);
//                 boolean from1 = dp[i - 1][j] && s1.charAt(i - 1) == need;
//                 boolean from2 = dp[i][j - 1] && s2.charAt(j - 1) == need;
//                 dp[i][j] = from1 || from2;
//             }
//         }
//         return dp[m][n];
//     }
// }





// Method 3:  Rolling array optimization (O(m·n) time, O(n) space)
/*
# 2) Rolling array optimization (O(m·n) time, O(n) space)

## Idea

We only need the **previous row** to compute the current one, so compress the 2D table into a 1D array `dp[j]` representing `dp[i][j]` for the current `i`.

* `dp[0]` along the way is the first column: only `s1` used.
* For each row `i`, sweep `j` from 0..n:

  * When `j==0`, update from `dp[0] && s1[i-1]==s3[i-1]`.
  * Otherwise:

    ```
    dp[j] = (dp[j]   && s1[i-1] == s3[i+j-1])   // take from s1 (above)
          || (dp[j-1] && s2[j-1] == s3[i+j-1]); // take from s2 (left)
    ```

## Walkthrough (same example)

Initialize (`i=0` row):

* `dp[0]=true`
* Fill first row with s2 vs s3:
  `dp[1]= (dp[0] && s2[0]==s3[0]) = (true && d==a)=false`,
  `dp[2]= false & (b==a)=false`, … (eventually some may turn true when prefixes match).

Iterate rows `i=1..m`:

* Update `dp[0]` each row from s1 and s3 (first column).
* For each `j`, recompute `dp[j]` from “above” (old `dp[j]`) or “left” (current `dp[j-1]`), comparing the needed `s3[i+j-1]` char appropriately.

When done, `dp[n]` equals the answer (`true` for the classic `aadbbcbcac`, `false` for `aadbbbaccc`).

---

## Edge cases & pitfalls (both methods)

* **Length check first**: if `m+n != s3.length()`, return false.
* **Indexing**: watch the `-1` offsets (`i-1`,`j-1`,`i+j-1`).
* **Initialization**: seed row/column correctly—these represent using only one string.
* **Rolling array order**: sweep `j` left→right; we rely on `dp[j]` being from the **previous row** and `dp[j-1]` being the **current row’s left**.

---

## Complexity

* Time: `O(m·n)`
* Space: `O(m·n)` for the 2D table, `O(n)` for the rolling array.
*/
// class Solution {
//     public boolean isInterleave(String s1, String s2, String s3) {
//         int m = s1.length(), n = s2.length();
//         if (m + n != s3.length()) return false;

//         boolean[] dp = new boolean[n + 1];
//         dp[0] = true; // empty + empty -> empty

//         // Seed first row: only s2 used
//         for (int j = 1; j <= n; j++) {
//             dp[j] = dp[j - 1] && (s2.charAt(j - 1) == s3.charAt(j - 1));
//         }

//         // Process rows for s1
//         for (int i = 1; i <= m; i++) {
//             // First column: only s1 used
//             dp[0] = dp[0] && (s1.charAt(i - 1) == s3.charAt(i - 1));

//             for (int j = 1; j <= n; j++) {
//                 char need = s3.charAt(i + j - 1);
//                 boolean from1 = dp[j] && (s1.charAt(i - 1) == need);   // previous dp[j] was from row i-1
//                 boolean from2 = dp[j - 1] && (s2.charAt(j - 1) == need); // dp[j-1] is current row, left cell
//                 dp[j] = from1 || from2;
//             }
//         }
//         return dp[n];
//     }
// }
