// Top Down DP approach
/*
My following base case condition is invalid:
if(list >= triangle.size() || idx >= triangle[list].size()){
            return 0;
}
because this base‐case will silently let you “fall off” the triangle and pretend those nonexistent paths cost you nothing, which can corrupt your min‐sum. In particular:
You never pick up the value in the bottom row.
Once you recurse past the last list, you return 0 instead of the actual triangle entry you landed on.
Invalid indices shouldn’t contribute a zero cost.
If idx runs past the end of a row, that path really isn’t valid—you’d want to treat it as “infinite” cost, not zero, so it never wins the Math.min.


We don't recur inside a loop because of the following reason:
If we loop over i runs from 0…triangle.length, rather than from currList+1 to the end means you’re reconsidering all earlier lists  on every call, rather than only looking “forward.”

Why not a 1-D memo array?
In our top-down DP, the subproblem is characterized by two parameters—list (which row you’re on) and idx (which position in that row). A 1-D array can only cache a single varying parameter. You’d lose information about where in the triangle you came from, and different (list, idx) pairs could collide in the same slot.
*/
class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        
        int[][] memo = new int[triangle.size()][triangle.size()];
        for(int[] row : memo){
            Arrays.fill(row, -1);
        }
        return dp(0, 0, triangle, memo);
    }

    public int dp(int list, int idx, List<List<Integer>> triangle, int[][] memo){
        if(list == triangle.size()-1){
            return triangle.get(list).get(idx);
        }

        if(memo[list][idx] != -1){
            return memo[list][idx];
        }

        int val = triangle.get(list).get(idx);

        int bestBelow = Math.min(dp(list + 1, idx, triangle, memo), dp(list + 1, idx + 1, triangle, memo));

        memo[list][idx] = bestBelow + val;
        return memo[list][idx];
    }
}

// Bottom Up DP
/*
## Detailed Explanation

1. **DP State**
   We use a 1D array `dp` of length `n`, where after processing row `i`,

   ```
   dp[j] = minimum path sum from triangle[i][j] down to the last row.
   ```

   In particular, before any processing, we set `dp[j]` to the values in the last row (when `i = n-1`).

2. **Initialization**

   ```java
   for (int j = 0; j < n; j++) {
       dp[j] = triangle.get(n - 1).get(j);
   }
   ```

   At this point, `dp[j]` correctly holds the min-sum for the bottom row trivially, since “the path from a bottom‐row element to itself” is just that element.

3. **Bottom‐Up Recurrence**
   We move upward through the triangle. For each row `i` from `n-2` down to `0`, and for each position `j` in that row:

   ```java
   dp[j] = triangle.get(i).get(j)
         + Math.min(dp[j], dp[j + 1]);
   ```

   * `dp[j]` on the right-hand side is the min‐sum from the element *directly below* (`i+1, j`).
   * `dp[j+1]` is the min‐sum from the element *below‐and‐to‐the‐right* (`i+1, j+1`).
     We choose the smaller of those two “child” paths and add the current cell’s value.

4. **In‐Place Update**
   By overwriting `dp[j]` in place as we go, we ensure that after finishing row `i`, `dp[0..i]` represent the correct values for that row. We don’t care about `dp[i+1..]` anymore, so it’s safe to discard.

5. **Final Answer**
   Once we process up to row 0, `dp[0]` is exactly the minimum total path sum from the top of the triangle to the bottom.

---

### Complexity

* **Time:**

  * We process each of the \~`n(n+1)/2` elements exactly once in the nested loops.
  * That gives **O(n²)** time, where `n` is the number of rows.

* **Space:**

  * We only use the 1D `dp` array of size `n`, plus a handful of loop variables.
  * That’s **O(n)** extra space, optimal for this problem.


## Walkthrough on the example

Triangle (n=4):

```
    [2]
   [3,4]
  [6,5,7]
 [4,1,8,3]
```

### Step 1: Initialize with last row

We set

```
dp = [4, 1, 8, 3]
```

because each bottom‐row element’s min‐path to itself is just its own value.

| j      | 0 | 1 | 2 | 3 |
| ------ | - | - | - | - |
| dp\[j] | 4 | 1 | 8 | 3 |

---

### Step 2: Process row i = 2  (values `[6,5,7]`)

We only update `dp[0]`, `dp[1]`, and `dp[2]` (because row 2 has 3 elements):

* **j = 0**:

  * `below    = dp[0] = 4`
  * `belowRight = dp[1] = 1`
  * Pick `min(4,1)=1` and add `triangle[2][0]=6` → new `dp[0] = 6+1 = 7`

* **j = 1**:

  * `below    = dp[1] = 1`
  * `belowRight = dp[2] = 8`
  * Pick `1` and add `5` → `dp[1] = 5+1 = 6`

* **j = 2**:

  * `below    = dp[2] = 8`
  * `belowRight = dp[3] = 3`
  * Pick `3` and add `7` → `dp[2] = 7+3 = 10`

After row 2, `dp` becomes:

| j   | 0 | 1 | 2  | 3   |
| --- | - | - | -- | --- |
| old | 4 | 1 | 8  | 3   |
| new | 7 | 6 | 10 | 3\* |

\* `dp[3]` is stale now (we only use `dp[0..2]` going forward).

---

### Step 3: Process row i = 1  (values `[3,4]`)

* **j = 0**:

  * `below    = dp[0] = 7`
  * `belowRight = dp[1] = 6`
  * Pick `6` and add `3` → `dp[0] = 3+6 = 9`

* **j = 1**:

  * `below    = dp[1] = 6`
  * `belowRight = dp[2] = 10`
  * Pick `6` and add `4` → `dp[1] = 4+6 = 10`

After row 1, `dp` is:

| j   | 0 | 1  | 2\* | 3\* |
| --- | - | -- | --- | --- |
| old | 7 | 6  | 10  | 3   |
| new | 9 | 10 | —   | —   |

\* Only `dp[0..1]` are now relevant.

---

### Step 4: Process row i = 0  (value `[2]`)

* **j = 0**:

  * `below    = dp[0] = 9`
  * `belowRight = dp[1] = 10`
  * Pick `9` and add `2` → `dp[0] = 2+9 = 11`

Now `dp[0] = 11`, which is the minimum path sum:

```
2 → 3 → 5 → 1  gives 2+3+5+1 = 11
```

---

## Why this works

* We **reuse** the same `dp[]` array, always holding the min‐sums for the row *below* our current one.
* By overwriting from left to right within each row, we ensure we never accidentally use an out‐of‐date value.
* At the end, `dp[0]` has been chased all the way up to represent the top element’s best path.

This is an **O(n²)** algorithm (you visit each triangle element once), using only **O(n)** extra space.

*/

// class Solution {
//     public int minimumTotal(List<List<Integer>> triangle) {
//         int n = triangle.size();
//         // dp[j] will hold the minimum path sum starting from
//         // position j in the “current” row down to the bottom.
//         // We initialize it with the values of the last row.
//         int[] dp = new int[n];
//         for (int j = 0; j < n; j++) {
//             dp[j] = triangle.get(n - 1).get(j);
//         }

//         // Now work our way upward, from the second‐to‐last row up to row 0
//         for (int i = n - 2; i >= 0; i--) {
//             // For each element in row i (there are i+1 elements)
//             for (int j = 0; j <= i; j++) {
//                 // The best path from triangle[i][j] is its own value
//                 // plus the min of the two possible paths directly below it
//                 dp[j] = triangle.get(i).get(j) + Math.min(dp[j], dp[j + 1]);
//             }
//             // After this inner loop, dp[0..i] hold the correct min-sums
//             // for row i. dp[i+1] is now “stale” and will be ignored.
//         }

//         // By the time we reach row 0, dp[0] is the min path sum from top to bottom.
//         return dp[0];
//     }
// }

