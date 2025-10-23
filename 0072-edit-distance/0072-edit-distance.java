// Method 1: Top-Down DP
/*
# What was wrong in your code:

1. **Wrong top-level base case**

   ```java
   if (word1.length() == 0 || word2.length() == 0) return 0;
   ```

   *Edit distance to the empty string is the length of the other string*, not 0. It should be `return word2.length()` if `word1` empty (and vice-versa).

2. **Indexing off by one in `charAt`**
   You call `dp(..., i = word1.length(), j = word2.length())`, so `i`/`j` represent **prefix lengths**. The last character of those prefixes is at `word1.charAt(i-1)` and `word2.charAt(j-1)`, not `charAt(i)`/`charAt(j)`.

3. **Bad base cases inside `dp`**

   ```java
   if (i < 0 || j < 0) return 10000;
   ```

   You should never go negative if you define `dp(i,j)` on prefix lengths. Proper bases are:

   * if `i == 0` → need `j` inserts
   * if `j == 0` → need `i` deletes

4. **Using `memo[i-1][j-1]` directly**

   ```java
   memo[i][j] = memo[i-1][j-1];
   ```

   That cell may be `null`. You must *compute* the subproblem via `dp(word1, word2, memo, i-1, j-1)` (which also memoizes).

5. **`Math.min` with three arguments**
   Java’s `Math.min` takes **two** args. You must nest it:

   ```java
   1 + Math.min( dp(i-1,j), Math.min(dp(i,j-1), dp(i-1,j-1)) )
   ```

6. **Not memoizing on the “equal chars” path via `dp`**
   You should set `memo[i][j] = dp(i-1, j-1)` when `word1[i-1] == word2[j-1]`.

---

### Why this works

* **State:** `dp(i,j)` is the min edits for the first `i` chars of `word1` to the first `j` of `word2`.
* **Bases:** converting to/from empty strings costs `i` or `j`.
* **Transition:** if last chars equal, drop both; else take `1 + min(delete, insert, replace)`.
* **Memoization:** each `(i,j)` computed once → `O(m·n)` time, `O(m·n)` space.

---

# Quick walkthrough: `word1 = "horse"`, `word2 = "ros"`

We compute `dp(5,3)` (lengths). Key subcalls:

* `dp(5,3)`: compare `'e'` vs `'s'` (≠)
  → `1 + min(dp(4,3) [del 'e'], dp(5,2) [ins 's'], dp(4,2) [rep 'e'→'s'])`

* `dp(4,3)` compares `'s'` vs `'s'` (equal)
  → `dp(3,2)`

* `dp(3,2)` compares `'r'` vs `'o'` (≠)
  → `1 + min(dp(2,2), dp(3,1), dp(2,1))`

* `dp(2,2)` compares `'o'` vs `'o'` (equal) → `dp(1,1)`

* `dp(1,1)` compares `'h'` vs `'r'` (≠)
  → `1 + min(dp(0,1)=1, dp(1,0)=1, dp(0,0)=0)` = `1 + 0` = `1`

So `dp(2,2) = 1`, then `dp(3,2) = 1 + min(1, dp(3,1), dp(2,1)) = ... = 2`,
`dp(4,3) = dp(3,2) = 2`, and the other branches `dp(5,2)`, `dp(4,2)` give costs ≥2, so
`dp(5,3) = 1 + min(2, 3, 2) = 3`.

You can follow the memo table filling: once `dp(3,2)` or `dp(2,2)` is computed, later calls reuse them instantly.

---

If you want, I can also show the **O(n)-space** rolling-row bottom-up version and how to keep the “diagonal” value (`dp[i-1][j-1]`) with a temp variable.
*/
import java.util.Arrays;

class Solution {
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        // If one is empty, cost is the length of the other
        if (m == 0) return n;
        if (n == 0) return m;

        Integer[][] memo = new Integer[m + 1][n + 1];
        return dp(word1, word2, m, n, memo);
    }

    // dp(i, j): edit distance to convert word1[0..i-1] -> word2[0..j-1]
    private int dp(String a, String b, int i, int j, Integer[][] memo) {
        if (i == 0) return j; // insert j chars
        if (j == 0) return i; // delete i chars
        if (memo[i][j] != null) return memo[i][j];

        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            // last chars equal: no cost here
            return memo[i][j] = dp(a, b, i - 1, j - 1, memo);
        } else {
            int del = dp(a, b, i - 1, j,     memo); // delete a[i-1]
            int ins = dp(a, b, i,     j - 1, memo); // insert b[j-1]
            int rep = dp(a, b, i - 1, j - 1, memo); // replace a[i-1]->b[j-1]
            return memo[i][j] = 1 + Math.min(del, Math.min(ins, rep));
        }
    }
}





// Method 2: Bottom-Up Approach
/*
# Idea (what `dp[i][j]` means)

Let
`dp[i][j] = min # of edits to convert word1[0..i-1] → word2[0..j-1]`.

We build a table of size `(m+1) × (n+1)` where `m = word1.length()`, `n = word2.length()`.

## Base rows/cols

* `dp[0][j] = j` (empty → first `j` chars) → need `j` **insertions**
* `dp[1][0] = 1`, …, `dp[i][0] = i` (first `i` chars → empty) → need `i` **deletions**

## Transition

Compare last characters of the prefixes:

* `a = word1[i-1]`, `b = word2[j-1]`
* If `a == b`: `dp[i][j] = dp[i-1][j-1]`  (no cost, just carry diagonal)
* Else:

  ```
  dp[i][j] = 1 + min(
      dp[i-1][j],   // delete a
      dp[i][j-1],   // insert b
      dp[i-1][j-1]  // replace a -> b
  )
  ```

The final answer is `dp[m][n]`.

**Time:** `O(m·n)` **Space:** `O(m·n)` (can be reduced to `O(n)` with a rolling row; ask if you want that variant).

---

# Step-by-step walkthrough: `word1 = "horse"`, `word2 = "ros"`

So `m=5`, `n=3`. We’ll build a `(6×4)` table. Let rows be `i=0..5` (“”, h, ho, hor, hors, horse) and columns `j=0..3` (“”, r, ro, ros).

### 1) Initialize base row/col

```
      j→    0  1  2  3
           ''  r  o  s
i
0 ''        0  1  2  3
1 h         1
2 ho        2
3 hor       3
4 hors      4
5 horse     5
```

### 2) Fill the table

I’ll show each row’s key computations; use the recurrence above.

**Row i=1 (‘h’ vs r,o,s):**

* `dp[1][1]`: 'h' vs 'r' (≠) → `1 + min(dp[0][1]=1, dp[1][0]=1, dp[0][0]=0) = 1`
* `dp[1][2]`: 'h' vs 'o' (≠) → `1 + min(2,1,1) = 2`
* `dp[1][3]`: 'h' vs 's' (≠) → `1 + min(3,2,2) = 3`

```
0 1 2 3
1 1 2 3
```

**Row i=2 (‘ho’ vs r,o,s):**

* `dp[2][1]`: 'o' vs 'r' (≠) → `1 + min(1,2,1) = 2`
* `dp[2][2]`: 'o' vs 'o' (=) → `dp[1][1] = 1`
* `dp[2][3]`: 'o' vs 's' (≠) → `1 + min(3,1,2) = 2`

```
0 1 2 3
1 1 2 3
2 2 1 2
```

**Row i=3 (‘hor’ vs r,o,s):**

* `dp[3][1]`: 'r' vs 'r' (=) → `dp[2][0] = 2`
* `dp[3][2]`: 'r' vs 'o' (≠) → `1 + min(1,2,2) = 2`
* `dp[3][3]`: 'r' vs 's' (≠) → `1 + min(2,2,1) = 2`

```
0 1 2 3
1 1 2 3
2 2 1 2
3 2 2 2
```

**Row i=4 (‘hors’ vs r,o,s):**

* `dp[4][1]`: 's' vs 'r' (≠) → `1 + min(2,4,3) = 3`
* `dp[4][2]`: 's' vs 'o' (≠) → `1 + min(2,3,2) = 3`
* `dp[4][3]`: 's' vs 's' (=) → `dp[3][2] = 2`

```
0 1 2 3
1 1 2 3
2 2 1 2
3 2 2 2
4 3 3 2
```

**Row i=5 (‘horse’ vs r,o,s):**

* `dp[5][1]`: 'e' vs 'r' (≠) → `1 + min(3,5,4) = 4`
* `dp[5][2]`: 'e' vs 'o' (≠) → `1 + min(3,4,3) = 4`
* `dp[5][3]`: 'e' vs 's' (≠) → `1 + min(2,4,3) = 3`

```
0 1 2 3
1 1 2 3
2 2 1 2
3 2 2 2
4 3 3 2
5 4 4 3   ← dp[5][3] = 3
```

**Answer:** `dp[5][3] = 3`.
One optimal sequence (not required by the problem, but helpful for intuition):

* `horse` → **replace** `h→r` ⇒ `rorse` (cost 1)
* `rorse` → **delete** `r` after `o` ⇒ `rose` (cost 2)
* `rose` → **delete** `e` ⇒ `ros` (cost 3)

Different but equivalent sequences exist; the DP finds the minimal count.

---

# Why this works

* The DP enumerates all ways to reach each prefix pair via **insert/delete/replace**, always taking the cheapest.
* Matching last characters simply carries the diagonal `dp[i-1][j-1]` (no extra cost).
* Because every `(i,j)` depends only on smaller indices, a row-major fill is valid and complete.

# Common pitfalls (and how this avoids them)

* **Off-by-one**: use `i-1`/`j-1` when indexing chars, but `i`/`j` as lengths in `dp`.
* **Forgetting base cases**: first row/column are the cost to/from empty strings.
* **Charging for equal chars**: don’t—use the diagonal as-is when `a==b`.

If you want the **O(n)-space** rolling-row version (with the `prevDiag` trick for `dp[i-1][j-1]`), I can provide that next.

*/
// class Solution {
//     public int minDistance(String word1, String word2) {
//         int m = word1.length(), n = word2.length();
//         int[][] dp = new int[m + 1][n + 1];

//         // Base cases: empty prefixes
//         for (int i = 0; i <= m; i++) dp[i][0] = i;   // delete i chars
//         for (int j = 0; j <= n; j++) dp[0][j] = j;   // insert j chars

//         for (int i = 1; i <= m; i++) {
//             char a = word1.charAt(i - 1);
//             for (int j = 1; j <= n; j++) {
//                 char b = word2.charAt(j - 1);
//                 if (a == b) {
//                     dp[i][j] = dp[i - 1][j - 1]; // match: no extra cost
//                 } else {
//                     int del = dp[i - 1][j];      // delete a
//                     int ins = dp[i][j - 1];      // insert b
//                     int rep = dp[i - 1][j - 1];  // replace a->b
//                     dp[i][j] = 1 + Math.min(del, Math.min(ins, rep));
//                 }
//             }
//         }
//         return dp[m][n];
//     }
// }



// Method 3: Bottom-Up DP with O(n)-space rolling-row tabulation
/*

# Core idea:

Define `dp[i][j]` = min edits to convert `word1[0..i-1]` → `word2[0..j-1]`.

Bottom-up recurrences:

* Base: `dp[0][j]=j` (insert `j`), `dp[i][0]=i` (delete `i`)
* Step:

  * If `word1[i-1] == word2[j-1]`: `dp[i][j] = dp[i-1][j-1]`
  * Else: `dp[i][j] = 1 + min( dp[i-1][j] (del), dp[i][j-1] (ins), dp[i-1][j-1] (rep) )`

To get **O(n)** space with `n = word2.length()`, we keep only the current row in a 1-D array `dp[0..n]`. While sweeping `j` from left→right:

* `dp[j]` (before updating) is **old** `dp[i-1][j]` (the “delete” term).
* `dp[j-1]` (already updated in this row) is **new** `dp[i][j-1]` (the “insert” term).
* `prevDiag` holds **old** `dp[i-1][j-1]` (the “match/replace” diagonal).
  We update `prevDiag` each step to the previous `dp[j]` value.


### Why `prevDiag` works

At column `j`, before we overwrite `dp[j]`, it still holds `dp[i-1][j]`. We stash it in `temp`, then use:

* `prevDiag` (which is old `dp[i-1][j-1]`) for the match/replace case,
* `temp + 1` for delete,
* `dp[j-1] + 1` for insert (note `dp[j-1]` is already the current row’s value).
  Finally we set `prevDiag = temp` to shift the diagonal for the next column.

# \U0001f9ed Step-by-step walkthrough: `word1 = "horse"`, `word2 = "ros"`

Let `m=5` (“h o r s e”), `n=3` (“r o s”).
We’ll print the 1-D `dp` after each row (each `i`).

**Init (i=0, empty → “ros”):**

```
dp = [0, 1, 2, 3]
# j:   0  1  2  3
#      "" r  ro ros
```

### Row i=1 (use 'h')

Start: `prevDiag = dp[0]=0`, then set `dp[0]=1` (cost to delete 'h' to reach empty)

* j=1: a='h', b='r', cost=1
  temp=old dp[1]=1, delete=1+1=2, insert=dp[0]+1=1+1=2, replace=prevDiag+1=0+1=1 → dp[1]=1, prevDiag=1
* j=2: a='h', b='o', cost=1
  temp=2, delete=2+1=3, insert=dp[1]+1=1+1=2, replace=prevDiag+1=1+1=2 → dp[2]=2, prevDiag=2
* j=3: a='h', b='s', cost=1
  temp=3, delete=3+1=4, insert=dp[2]+1=2+1=3, replace=prevDiag+1=2+1=3 → dp[3]=3, prevDiag=3

Result row i=1:

```
dp = [1, 1, 2, 3]
```

### Row i=2 (use 'o')

Start: `prevDiag=dp[0]=1`, set `dp[0]=2`

* j=1: a='o', b='r', cost=1
  temp=1, del=1+1=2, ins=dp[0]+1=2+1=3, rep=prevDiag+1=1+1=2 → dp[1]=2, prevDiag=1
* j=2: a='o', b='o', cost=0
  temp=2, del=2+1=3, ins=dp[1]+1=2+1=3, rep=prevDiag+0=1+0=1 → **dp[2]=1**, prevDiag=2
* j=3: a='o', b='s', cost=1
  temp=3, del=3+1=4, ins=dp[2]+1=1+1=2, rep=prevDiag+1=2+1=3 → **dp[3]=2**, prevDiag=3

Row i=2:

```
dp = [2, 2, 1, 2]
```

### Row i=3 (use 'r')

Start: `prevDiag=dp[0]=2`, set `dp[0]=3`

* j=1: a='r', b='r', cost=0
  temp=2, del=2+1=3, ins=dp[0]+1=3+1=4, rep=prevDiag+0=2+0=2 → **dp[1]=2**, prevDiag=2
* j=2: a='r', b='o', cost=1
  temp=1, del=1+1=2, ins=dp[1]+1=2+1=3, rep=prevDiag+1=2+1=3 → **dp[2]=2**, prevDiag=1
* j=3: a='r', b='s', cost=1
  temp=2, del=2+1=3, ins=dp[2]+1=2+1=3, rep=prevDiag+1=1+1=2 → **dp[3]=2**, prevDiag=2

Row i=3:

```
dp = [3, 2, 2, 2]
```

### Row i=4 (use 's')

Start: `prevDiag=dp[0]=3`, set `dp[0]=4`

* j=1: a='s', b='r', cost=1
  temp=2, del=2+1=3, ins=dp[0]+1=4+1=5, rep=prevDiag+1=3+1=4 → **dp[1]=3**, prevDiag=2
* j=2: a='s', b='o', cost=1
  temp=2, del=2+1=3, ins=dp[1]+1=3+1=4, rep=prevDiag+1=2+1=3 → **dp[2]=3**, prevDiag=2
* j=3: a='s', b='s', cost=0
  temp=2, del=2+1=3, ins=dp[2]+1=3+1=4, rep=prevDiag+0=2+0=2 → **dp[3]=2**, prevDiag=2

Row i=4:

```
dp = [4, 3, 3, 2]
```

### Row i=5 (use 'e')

Start: `prevDiag=dp[0]=4`, set `dp[0]=5`

* j=1: a='e', b='r', cost=1
  temp=3, del=3+1=4, ins=dp[0]+1=5+1=6, rep=prevDiag+1=4+1=5 → **dp[1]=4**, prevDiag=3
* j=2: a='e', b='o', cost=1
  temp=3, del=3+1=4, ins=dp[1]+1=4+1=5, rep=prevDiag+1=3+1=4 → **dp[2]=4**, prevDiag=3
* j=3: a='e', b='s', cost=1
  temp=2, del=2+1=3, ins=dp[2]+1=4+1=5, rep=prevDiag+1=3+1=4 → **dp[3]=3**, prevDiag=2

Final row:

```
dp = [5, 4, 4, 3]
```

**Answer** = `dp[n] = dp[3] = 3`, which matches the known minimum edits (`horse` → `ros`).

# \U0001f9e0 Why this is correct

* We’ve preserved the exact same recurrence as the 2-D DP.
* At each cell `(i,j)`, we combine:

  * **delete**: old `dp[i-1][j]` (that’s `temp`)
  * **insert**: current row’s `dp[i][j-1]` (that’s `dp[j-1]` after update)
  * **replace/match**: old diagonal `dp[i-1][j-1]` (that’s `prevDiag`)
* By updating left→right and carrying `prevDiag`, we never lose needed values.

# ⏱️ Complexity & tips

* **Time:** `O(m·n)` comparisons/ops.
* **Space:** `O(n)` integers.
* Works for empty strings automatically (base initialization handles it).
* If you need the **actual edit script** (sequence of ops), use full 2-D DP and backtrack; the 1-D version only gives the distance.
*/
// class Solution {
//     public int minDistance(String word1, String word2) {
//         int m = word1.length(), n = word2.length();
//         // dp[j] represents dp[current_i][j] for current row i
//         int[] dp = new int[n + 1];

//         // Base row: converting "" -> word2[0..j-1] costs j inserts
//         for (int j = 0; j <= n; j++) dp[j] = j;

//         for (int i = 1; i <= m; i++) {
//             int prevDiag = dp[0];   // old dp[i-1][0]
//             dp[0] = i;              // base col: converting word1[0..i-1] -> "" costs i deletes

//             char a = word1.charAt(i - 1);
//             for (int j = 1; j <= n; j++) {
//                 int temp = dp[j];   // save old dp[i-1][j] before overwriting

//                 char b = word2.charAt(j - 1);
//                 int cost = (a == b) ? 0 : 1;

//                 int deleteCost  = temp + 1;         // dp[i-1][j] + 1
//                 int insertCost  = dp[j - 1] + 1;    // dp[i][j-1] + 1  (already updated this row)
//                 int replaceCost = prevDiag + cost;  // dp[i-1][j-1] + (0 or 1)

//                 dp[j] = Math.min(Math.min(deleteCost, insertCost), replaceCost);

//                 // move the diagonal for next j: old dp[i-1][j] becomes new prevDiag
//                 prevDiag = temp;
//             }
//         }
//         return dp[n];
//     }
// }

