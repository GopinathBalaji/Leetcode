// Method 1: Top-Down 2D DP
/*
## 3. Top-down LCS idea (what `dp(i, j)` means)

Define:

> `dp(i, j)` = length of the **Longest Common Subsequence** of
> `text1[i..end]` and `text2[j..end]`.

We want `dp(0, 0)`.

### Base case

If either string is exhausted:

* If `i == m` (past end of `text1`) or `j == n` (past end of `text2`), there is no common subsequence left.
* So:

```java
if (i == m || j == n) return 0;
```

### Recurrence

At state `(i, j)`:

* If `text1.charAt(i) == text2.charAt(j)`:

  * This character **can** be part of the LCS.
  * So we take it, add 1, and move both indices:

    ```java
    dp(i,j) = 1 + dp(i+1, j+1);
    ```

* Else (characters differ):

  * We have two options:

    1. Skip `text1[i]` → move `i` forward: `dp(i+1, j)`.
    2. Skip `text2[j]` → move `j` forward: `dp(i, j+1)`.
  * We want the better of these:

    ```java
    dp(i,j) = max( dp(i+1, j), dp(i, j+1) );
    ```

### Memoization

Because many `(i, j)` states recur from different paths, we store results in `memo[i][j]`:

* If `memo[i][j] != null`, return it directly.
* Otherwise compute, store, and return.

This gives:

* Time: `O(m * n)`
* Space: `O(m * n)` for the memo table

---

## 4. Example walkthrough: `text1 = "abcde"`, `text2 = "ace"`

We know the LCS is `"ace"` with length **3**.

Let’s trace the recursion at the top level.

```text
text1 = "a b c d e"
          0 1 2 3 4
text2 = "a c e"
          0 1 2
m = 5, n = 3
```

We call `dp(0,0)`.

---

### Step 1: `dp(0,0)` → compare 'a' and 'a'

`text1[0] = 'a'`, `text2[0] = 'a'` → they match.

So:

```text
dp(0,0) = 1 + dp(1,1)
```

We now need `dp(1,1)`.

---

### Step 2: `dp(1,1)` → compare 'b' and 'c'

`text1[1] = 'b'`, `text2[1] = 'c'` → no match.

So:

```text
dp(1,1) = max( dp(2,1), dp(1,2) )
```

We’ll compute both.

---

#### 2a. Compute `dp(2,1)` → compare 'c' and 'c'

`text1[2] = 'c'`, `text2[1] = 'c'` → match.

```text
dp(2,1) = 1 + dp(3,2)
```

Need `dp(3,2)`.

##### 2a-i. `dp(3,2)` → compare 'd' and 'e'

`text1[3] = 'd'`, `text2[2] = 'e'` → no match.

```text
dp(3,2) = max( dp(4,2), dp(3,3) )
```

Compute:

* `dp(4,2)` → compare 'e' and 'e'
* `dp(3,3)` → `j == n`, so base case 0.

###### `dp(4,2)`:

`text1[4] = 'e'`, `text2[2] = 'e'` → match.

```text
dp(4,2) = 1 + dp(5,3)
```

Now `i == 5 == m` and `j == 3 == n`, so:

* `dp(5,3) = 0` (base case)

Thus:

```text
dp(4,2) = 1 + 0 = 1
memo[4][2] = 1
```

Back to `dp(3,2)`:

```text
dp(3,2) = max( dp(4,2), dp(3,3) ) = max(1, 0) = 1
memo[3][2] = 1
```

Back to `dp(2,1)`:

```text
dp(2,1) = 1 + dp(3,2) = 1 + 1 = 2
memo[2][1] = 2
```

So the best LCS length starting from `text1[2..] = "cde"` and `text2[1..] = "ce"` is 2 → `"ce"`.

---

#### 2b. Compute `dp(1,2)` → compare 'b' and 'e'

`text1[1] = 'b'`, `text2[2] = 'e'` → no match.

```text
dp(1,2) = max( dp(2,2), dp(1,3) )
```

* `dp(1,3)` → `j == n` → 0.
* `dp(2,2)` → compare 'c' and 'e'.

For `dp(2,2)`:

* `text1[2] = 'c'`, `text2[2] = 'e'` → no match.

```text
dp(2,2) = max( dp(3,2), dp(2,3) )
```

We already computed `dp(3,2) = 1`.
`dp(2,3)` → `j == n` → 0.

So:

```text
dp(2,2) = max(1, 0) = 1
memo[2][2] = 1
```

Back to `dp(1,2)`:

```text
dp(1,2) = max( dp(2,2), dp(1,3) ) = max(1, 0) = 1
memo[1][2] = 1
```

---

#### 2c. Finish `dp(1,1)`

Now we have:

* `dp(2,1) = 2`
* `dp(1,2) = 1`

So:

```text
dp(1,1) = max(2, 1) = 2
memo[1][1] = 2
```

Interpretation: best LCS of `"bcde"` and `"ce"` has length 2 (the subsequence `"ce"`).

---

### Step 3: Finish `dp(0,0)`

Recall:

```text
dp(0,0) = 1 + dp(1,1)
```

And we found `dp(1,1) = 2`.

So:

```text
dp(0,0) = 1 + 2 = 3
memo[0][0] = 3
```

That matches the expected LCS length ( `"ace"` → length 3).
*/
class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();

        Integer[][] memo = new Integer[m][n];

        return dp(text1, text2, memo, m, n, 0, 0);
    }

    private int dp(String text1, String text2,
                   Integer[][] memo, int m, int n,
                   int i, int j) {

        // Base case: one string exhausted
        if (i == m || j == n) {
            return 0;
        }

        // Memo check
        if (memo[i][j] != null) {
            return memo[i][j];
        }

        int ans;
        if (text1.charAt(i) == text2.charAt(j)) {
            // Characters match: must be part of LCS
            ans = 1 + dp(text1, text2, memo, m, n, i + 1, j + 1);
        } else {
            // Characters don't match: skip one side or the other
            int skipText1 = dp(text1, text2, memo, m, n, i + 1, j);
            int skipText2 = dp(text1, text2, memo, m, n, i, j + 1);
            ans = Math.max(skipText1, skipText2);
        }

        memo[i][j] = ans;
        return ans;
    }
}







// Method 2: Bottom-Up 2D DP
/*
## 1. DP idea: define `dp[i][j]` on prefixes

For bottom-up, it’s most natural to define the state on **prefixes**:

> `dp[i][j]` = length of the **Longest Common Subsequence** of
> the first `i` characters of `text1` and the first `j` characters of `text2`.

Formally:

* `dp[i][j]` = LCS( `text1[0..i-1]`, `text2[0..j-1]` )

We’ll build `dp` for **all** `i = 0..m`, `j = 0..n`, where:

* `m = text1.length()`
* `n = text2.length()`

Our final answer will be:

> `dp[m][n]`

because that’s LCS of the *entire* `text1` and `text2`.

We use a `(m+1) x (n+1)` table so that row `0` and column `0` represent “empty prefix”.

---

## 2. Transition and base cases

### Base cases: empty prefixes

If one prefix is empty, LCS is length `0`:

* If `i == 0` (text1 prefix is empty), then `dp[0][j] = 0` for all `j`.
* If `j == 0` (text2 prefix is empty), then `dp[i][0] = 0` for all `i`.

So we initialize the **first row and first column to 0**.

---

### Transition

For `i ≥ 1` and `j ≥ 1`, compare `text1[i-1]` and `text2[j-1]`:

1. **If characters match**:
   Say `text1[i-1] == text2[j-1]`.

   That character *can* be part of the LCS of these prefixes, and then we look at the LCS of the prefixes *before* these characters:

   ```text
   dp[i][j] = 1 + dp[i-1][j-1]
   ```

2. **If characters differ**:
   The LCS must end by skipping one of them:

   * Either skip `text1[i-1]`: look at `dp[i-1][j]`
   * Or skip `text2[j-1]`: look at `dp[i][j-1]`

   We take the best:

   ```text
   dp[i][j] = max(dp[i-1][j], dp[i][j-1])
   ```

We fill this table row by row, column by column.

* Time: **O(m · n)**
* Space: **O(m · n)** (you can optimize to O(min(m,n)) with 1D DP, but this is the clearest form)

---

## 4. Thorough example walkthrough

Let’s walk through:

```text
text1 = "abcde"
text2 = "ace"
```

We know by inspection LCS is `"ace"` with length **3**.

### 4.1. Setup the DP table

`m = 5`, `n = 3`.

We create a `(m+1) x (n+1)` = `6 x 4` table:

* Rows: `i = 0..5` → prefix lengths 0..5 of `"abcde"`.
* Columns: `j = 0..3` → prefix lengths 0..3 of `"ace"`.

We’ll show it like this (`i` is row, `j` is column):

```text
      j→   0   1   2   3
          ""  a   c   e
i ↓
0 ""      0   0   0   0
1 a       0   ?   ?   ?
2 b       0   ?   ?   ?
3 c       0   ?   ?   ?
4 d       0   ?   ?   ?
5 e       0   ?   ?   ?
```

Row 0 and column 0 are all zeros (empty-prefix LCS).

---

### 4.2. Fill row by row

#### Row i = 1 (prefix `"a"`)

* `i = 1` → `text1[0] = 'a'`

We compute `dp[1][1..3]`.

**j = 1** → compare `text1[0]` vs `text2[0]`:

* `c1 = 'a'`, `c2 = 'a'` → match.

So:

```text
dp[1][1] = 1 + dp[0][0] = 1 + 0 = 1
```

**j = 2** → compare `text1[0]` vs `text2[1]`:

* `c1 = 'a'`, `c2 = 'c'` → differ.

So:

```text
dp[1][2] = max(dp[0][2], dp[1][1])
         = max(0, 1)
         = 1
```

**j = 3** → compare `text1[0]` vs `text2[2]`:

* `c1 = 'a'`, `c2 = 'e'` → differ.

So:

```text
dp[1][3] = max(dp[0][3], dp[1][2])
         = max(0, 1)
         = 1
```

Row 1 now:

```text
i=1:
dp[1] = [0, 1, 1, 1]
```

Table:

```text
      j→   0   1   2   3
          ""  a   c   e
i ↓
0 ""      0   0   0   0
1 a       0   1   1   1
2 b       0   ?   ?   ?
3 c       0   ?   ?   ?
4 d       0   ?   ?   ?
5 e       0   ?   ?   ?
```

---

#### Row i = 2 (prefix `"ab"`)

* `i = 2` → `text1[1] = 'b'`

**j = 1** → compare `'b'` vs `'a'`:

* differ →

```text
dp[2][1] = max(dp[1][1], dp[2][0])
         = max(1, 0)
         = 1
```

**j = 2** → compare `'b'` vs `'c'`:

* differ →

```text
dp[2][2] = max(dp[1][2], dp[2][1])
         = max(1, 1)
         = 1
```

**j = 3** → compare `'b'` vs `'e'`:

* differ →

```text
dp[2][3] = max(dp[1][3], dp[2][2])
         = max(1, 1)
         = 1
```

Row 2:

```text
i=2:
dp[2] = [0, 1, 1, 1]
```

Table:

```text
      j→   0   1   2   3
          ""  a   c   e
i ↓
0 ""      0   0   0   0
1 a       0   1   1   1
2 b       0   1   1   1
3 c       0   ?   ?   ?
4 d       0   ?   ?   ?
5 e       0   ?   ?   ?
```

Interpretation so far: LCS of `"ab"` and `"ace"` is still just `"a"` of length 1.

---

#### Row i = 3 (prefix `"abc"`)

* `i = 3` → `text1[2] = 'c'`

**j = 1** → compare `'c'` vs `'a'`:

* differ →

```text
dp[3][1] = max(dp[2][1], dp[3][0])
         = max(1, 0)
         = 1
```

**j = 2** → compare `'c'` vs `'c'`:

* match →

```text
dp[3][2] = 1 + dp[2][1]
         = 1 + 1
         = 2
```

**j = 3** → compare `'c'` vs `'e'`:

* differ →

```text
dp[3][3] = max(dp[2][3], dp[3][2])
         = max(1, 2)
         = 2
```

Row 3:

```text
i=3:
dp[3] = [0, 1, 2, 2]
```

Table:

```text
      j→   0   1   2   3
          ""  a   c   e
i ↓
0 ""      0   0   0   0
1 a       0   1   1   1
2 b       0   1   1   1
3 c       0   1   2   2
4 d       0   ?   ?   ?
5 e       0   ?   ?   ?
```

Now we see: LCS of `"abc"` and `"ace"` is `"ac"` with length 2.

---

#### Row i = 4 (prefix `"abcd"`)

* `i = 4` → `text1[3] = 'd'`

**j = 1** → `'d'` vs `'a'` (differ):

```text
dp[4][1] = max(dp[3][1], dp[4][0])
         = max(1, 0)
         = 1
```

**j = 2** → `'d'` vs `'c'` (differ):

```text
dp[4][2] = max(dp[3][2], dp[4][1])
         = max(2, 1)
         = 2
```

**j = 3** → `'d'` vs `'e'` (differ):

```text
dp[4][3] = max(dp[3][3], dp[4][2])
         = max(2, 2)
         = 2
```

Row 4:

```text
i=4:
dp[4] = [0, 1, 2, 2]
```

Table:

```text
      j→   0   1   2   3
          ""  a   c   e
i ↓
0 ""      0   0   0   0
1 a       0   1   1   1
2 b       0   1   1   1
3 c       0   1   2   2
4 d       0   1   2   2
5 e       0   ?   ?   ?
```

Still LCS length = 2: `"ac"`.

---

#### Row i = 5 (prefix `"abcde"`)

* `i = 5` → `text1[4] = 'e'`

**j = 1** → `'e'` vs `'a'` (differ):

```text
dp[5][1] = max(dp[4][1], dp[5][0])
         = max(1, 0)
         = 1
```

**j = 2** → `'e'` vs `'c'` (differ):

```text
dp[5][2] = max(dp[4][2], dp[5][1])
         = max(2, 1)
         = 2
```

**j = 3** → `'e'` vs `'e'` (match):

```text
dp[5][3] = 1 + dp[4][2]
         = 1 + 2
         = 3
```

Row 5:

```text
i=5:
dp[5] = [0, 1, 2, 3]
```

Final table:

```text
      j→   0   1   2   3
          ""  a   c   e
i ↓
0 ""      0   0   0   0
1 a       0   1   1   1
2 b       0   1   1   1
3 c       0   1   2   2
4 d       0   1   2   2
5 e       0   1   2   3
```

The answer is `dp[5][3] = 3`, which matches the LCS `"ace"`.

---

## Quick connection to your top-down version

Your top-down state was:

> `dfs(i, j)` = LCS of `text1[i..]` and `text2[j..]` (suffix-based).

Bottom-up here is:

> `dp[i][j]` = LCS of `text1[0..i-1]` and `text2[0..j-1]` (prefix-based).

They’re essentially mirror images:

* Top-down: start from the beginning and move forward (`i+1`, `j+1`) in recursion.
* Bottom-up: build all smaller prefixes first (`i-1`, `j-1`) so they’re ready when we compute larger ones.

Same recurrence, just different indexing direction.

---

If you like, next we can:

* Show how to reconstruct the **actual LCS string** (not just length) from this `dp` table, or
* Compress this 2D DP into a 1D `int[] dp` using the same recurrence.
*/
// class Solution {
//     public int longestCommonSubsequence(String text1, String text2) {
//         int m = text1.length();
//         int n = text2.length();
        
//         // dp[i][j] = LCS length of text1[0..i-1] and text2[0..j-1]
//         int[][] dp = new int[m + 1][n + 1];
        
//         // First row and first column are already 0 by default:
//         // dp[0][*] = 0, dp[*][0] = 0
        
//         // Fill the table
//         for (int i = 1; i <= m; i++) {
//             char c1 = text1.charAt(i - 1);
//             for (int j = 1; j <= n; j++) {
//                 char c2 = text2.charAt(j - 1);
                
//                 if (c1 == c2) {
//                     // Characters match → extend LCS from i-1, j-1
//                     dp[i][j] = 1 + dp[i - 1][j - 1];
//                 } else {
//                     // Characters differ → skip one side and take best
//                     dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
//                 }
//             }
//         }
        
//         // Answer is LCS of full strings
//         return dp[m][n];
//     }
// }
