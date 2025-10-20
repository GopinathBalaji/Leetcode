// Method 1: Top-Down DP Approach
/*
### Idea:

* **Memoization** stores `dp(i, j)` once; later calls return in O(1).
* **Base at last row** avoids extra `dp(n, *)` calls and never touches invalid indices.
* Time: O(R²) with R = number of rows (there are ~R² states). Space: O(R²) for memo, O(R) recursion depth.

### Quick walkthrough

```
[
  [2],          // i=0
  [3, 4],       // i=1
  [6, 5, 7],    // i=2
  [4, 1, 8, 3]  // i=3  (last row)
]
```

### Reminder of the function

* `dp(i, j)` = minimum path sum from `triangle[i][j]` down to the bottom.
* Base: if `i == lastRow`, return `triangle[i][j]`.
* Memo: `if (memo[i][j] != null) return memo[i][j];`
* Recur: `triangle[i][j] + min(dp(i+1,j), dp(i+1,j+1))`.

---

## Step-by-step call flow (with memo activity)

We start with `dp(0,0)`.

1. **dp(0,0)**
   Needs **dp(1,0)** and **dp(1,1)**. No memo yet.

2. **dp(1,0)**
   Needs **dp(2,0)** and **dp(2,1)**.

3. **dp(2,0)**
   Needs **dp(3,0)** and **dp(3,1)** (last row).

   * **dp(3,0)** → base row ⇒ returns `4`.
   * **dp(3,1)** → base row ⇒ returns `1`.

   Compute: `dp(2,0) = 6 + min(4,1) = 6 + 1 = 7`.
   **Write memo[2][0] = 7.**

4. Back to **dp(1,0)**, now compute **dp(2,1)**
   Needs **dp(3,1)** and **dp(3,2)** (last row).

   * **dp(3,1)** → base row ⇒ `1`.
   * **dp(3,2)** → base row ⇒ `8`.

   Compute: `dp(2,1) = 5 + min(1,8) = 6`.
   **Write memo[2][1] = 6.**

5. Finish **dp(1,0)**: `3 + min(dp(2,0), dp(2,1)) = 3 + min(7,6) = 9`.
   **Write memo[1][0] = 9.**

6. Back to **dp(0,0)**, now compute **dp(1,1)**
   Needs **dp(2,1)** and **dp(2,2)**.

   * **dp(2,1)**: **Memo hit!** `memo[2][1] = 6` (no recursion done here).
   * **dp(2,2)** needs **dp(3,2)** and **dp(3,3)** (last row):

     * **dp(3,2)** → base row ⇒ `8`.
     * **dp(3,3)** → base row ⇒ `3`.

     Compute: `dp(2,2) = 7 + min(8,3) = 10`.
     **Write memo[2][2] = 10.**

7. Finish **dp(1,1)**: `4 + min(dp(2,1), dp(2,2)) = 4 + min(6,10) = 10`.
   **Write memo[1][1] = 10.**

8. Finish **dp(0,0)**: `2 + min(dp(1,0), dp(1,1)) = 2 + min(9,10) = 11`.
   **Write memo[0][0] = 11.**

**Answer = 11.**

---

## Where memoization saved work

* The overlapping subproblem **`dp(2,1)`** appears **twice**:

  * First under `dp(1,0)` (we computed and **stored** it as 6).
  * Then under `dp(1,1)` (we **hit** the memo and returned 6 immediately).

Without memoization, `dp(2,1)` would recursively recompute `dp(3,1)` and `dp(3,2)` again. Here it returned in **O(1)** thanks to:

```java
if (memo[i][j] != null) return memo[i][j];
```

On larger triangles, this reuse is dramatic: every `dp(i,j)` is computed **once**, turning an exponential recursion tree into **O(R²)** total work (there are ~R² states for R rows).

---

## Final memo table (values after completion)

(Only entries that were needed are filled.)

```
i\j    0    1    2    3
-------------------------
0:     11
1:      9   10
2:      7    6   10
3:      4    1    8    3   (base row, returned directly—not stored in memo[][])
```

* Every **write** corresponds to the first time we solve that `(i,j)`.
* Every later reference to the same `(i,j)` is a **read hit**, skipping recursion.
*/
class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        Integer[][] memo = new Integer[n][n];
        return dp(triangle, 0, 0, n, memo);
    }

    private int dp(List<List<Integer>> triangle, int i, int j, int n, Integer[][] memo){
        if(i == n - 1){
            return triangle.get(i).get(j);
        }

        if(memo[i][j] != null){
            return memo[i][j];
        }

        int down = dp(triangle, i+1, j, n, memo);
        int diag = dp(triangle, i+1, j+1, n, memo);
        return memo[i][j] = triangle.get(i).get(j) + Math.min(down, diag);
    }
}



// Method 2: Better Bottom-Up 1-D DP using O(n) extra space
/*
# Key idea

* Let `dp[j]` hold the **minimum path sum from row `i+1` downward** starting at column `j`.
* Initialize `dp` with the **last row** (these are already the costs from bottom to bottom).
* Then, for each row going upward (`i = n-2 … 0`), update in place:

  ```
  dp[j] = triangle[i][j] + min(dp[j], dp[j+1])
  ```

  After finishing the top row, `dp[0]` is the answer.

Why O(n)? `dp` is a single array of length equal to the last row size, which equals the number of rows in a triangle.


### Complexity

* **Time:** O(n²) (you touch each triangle cell once).
* **Space:** O(n) for the 1-D `dp`.

---

## Thorough example walkthrough

Triangle:

```
[
  [2],          // row 0
  [3, 4],       // row 1
  [6, 5, 7],    // row 2
  [4, 1, 8, 3]  // row 3 (bottom)
]
```

### Step 1 — Initialize `dp` with last row

```
dp = [4, 1, 8, 3]
```

Meaning: the minimum cost from each bottom cell to the bottom is just itself.

### Step 2 — Process row 2 (i = 2, values [6, 5, 7])

For each `j = 0..2`:

* `j=0`: `dp[0] = 6 + min(dp[0], dp[1]) = 6 + min(4, 1) = 7`
* `j=1`: `dp[1] = 5 + min(dp[1], dp[2]) = 5 + min(1, 8) = 6`
* `j=2`: `dp[2] = 7 + min(dp[2], dp[3]) = 7 + min(8, 3) = 10`

Now:

```
dp = [7, 6, 10, 3]
```

Interpretation: from row 2,

* min path starting at (2,0) is 7,
* at (2,1) is 6,
* at (2,2) is 10.

### Step 3 — Process row 1 (i = 1, values [3, 4])

* `j=0`: `dp[0] = 3 + min(dp[0], dp[1]) = 3 + min(7, 6) = 9`
* `j=1`: `dp[1] = 4 + min(dp[1], dp[2]) = 4 + min(6,10) = 10`

Now:

```
dp = [9, 10, 10, 3]
```

### Step 4 — Process row 0 (i = 0, values [2])

* `j=0`: `dp[0] = 2 + min(dp[0], dp[1]) = 2 + min(9, 10) = 11`

Final:

```
dp = [11, 10, 10, 3]
```

### Result

`dp[0] = 11` — the minimum path sum from the top (`2 → 3 → 5 → 1`).

---

## Why in-place works safely

At row `i`, you only read `dp[j]` and `dp[j+1]` from the **previous row’s results** (which live in `dp` from the iteration below). Because you update `j` left-to-right, `dp[j+1]` hasn’t been overwritten yet in the current row’s pass, so each update uses the correct pair from the row beneath.

That’s the essence of achieving **O(n)** extra space while preserving correctness.
*/

// class Solution {
//     public int minimumTotal(List<List<Integer>> triangle) {
//         int n = triangle.size();
//         // dp length equals last row length == n
//         int[] dp = new int[n];

//         // 1) Initialize dp as the bottom row
//         List<Integer> last = triangle.get(n - 1);
//         for (int j = 0; j < n; j++) {
//             dp[j] = last.get(j);
//         }

//         // 2) Roll upwards: for row i, update dp[j] using dp[j] and dp[j+1]
//         for (int i = n - 2; i >= 0; i--) {
//             List<Integer> row = triangle.get(i);
//             for (int j = 0; j <= i; j++) {
//                 dp[j] = row.get(j) + Math.min(dp[j], dp[j + 1]);
//             }
//         }

//         // 3) dp[0] now holds the min path sum from the top
//         return dp[0];
//     }
// }
