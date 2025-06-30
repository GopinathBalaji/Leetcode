// Top Down DP solution
/*
Below is a full walk-through of the **top-down DP** on the small example

```
s1 = "ab"
s2 = "cd"
s3 = "acbd"
```

showing every call `dp(i,j)`, the value returned, where we store it in `memo`, and how (if it happened) a memo hit would short-circuit a deeper recursion.

Recall our DP:

```
dp(i,j) = can s3[0…i+j−1] be formed by interleaving s1[0…i−1] and s2[0…j−1]?
```

and

```java
if (memo[i][j] != null) 
   return memo[i][j];

char need = s3.charAt(i+j−1);
boolean ok = false;
if (i>0 && s1.charAt(i−1)==need)
    ok = dp(i−1, j);
if (!ok && j>0 && s2.charAt(j−1)==need)
    ok = dp(i, j−1);

memo[i][j] = ok;
return ok;
```

We start with `dp(2,2)` since `s1.length()==2`, `s2.length()==2`.

```
                                           dp(2,2)?
                                    need = s3[3] = 'd'
            ┌─────────────────────────────────────────────────────┐
            ↓                                                     │
 i>0? s1[1]='b' == 'd'? → no             j>0? s2[1]='d'=='d'? → yes
            │                                                     │
            └───────────────┐                                     │
                            ↓                                     │
                        dp(2,1)?                                  │
                   need = s3[2] = 'b'
            ┌────────────────────────────────────────┐
            ↓                                         │
 i>0? s1[1]='b'=='b'? → yes     (we skip the s2 branch since ok=true)
            │
            ↓
        dp(1,1)?  
       need = s3[1] = 'c'
 ┌─────────────────────────────────┐
 ↓                                  │
i>0? s1[0]='a'=='c'? → no   j>0? s2[0]='c'=='c'? → yes
            │                                  │
            └─────────────┐                    │
                          ↓                    │
                      dp(1,0)?                │
                 need = s3[0] = 'a'
            ┌─────────────────────────┐
            ↓                          │
 i>0? s1[0]='a'=='a'? → yes  (skip s2 branch)
            │
            ↓
        dp(0,0)?
       (i=0,j=0 base case) → return true
       memo[0][0] = true
        ↑
        │   memo[1][0] = true
        │           memo[1][1] = true
        │                   memo[2][1] = true
        │                           memo[2][2] = true
        └─────────────────────────────────────────────────
```

### Annotated steps

1. **`dp(2,2)`**

   * Next needed char = `s3[3] = 'd'`.
   * **s1-branch**: `i>0` but `s1[1]='b'≠'d'` → skip.
   * **s2-branch**: `s2[1]='d'==need` → recurse `dp(2,1)`.

2. **`dp(2,1)`**

   * Next needed = `s3[2] = 'b'`.
   * **s1-branch**: `s1[1]='b'==need` → recurse `dp(1,1)`.
   * Since that yields `true`, we don’t try the s2-branch.

3. **`dp(1,1)`**

   * Next needed = `s3[1] = 'c'`.
   * **s1-branch**: `s1[0]='a'≠'c'` → skip.
   * **s2-branch**: `s2[0]='c'==need` → recurse `dp(1,0)`.

4. **`dp(1,0)`**

   * Next needed = `s3[0] = 'a'`.
   * **s1-branch**: `s1[0]='a'==need` → recurse `dp(0,0)`.

5. **`dp(0,0)`**

   * Base case `(i==0 && j==0)` returns **true**, and we store `memo[0][0]=true`.

6. **Unwinding**

   * `dp(1,0)` sees its recursive call was true → sets `memo[1][0]=true`, returns true.
   * `dp(1,1)` sets `memo[1][1]=true`, returns true.
   * `dp(2,1)` sets `memo[2][1]=true`, returns true.
   * `dp(2,2)` sets `memo[2][2]=true`, returns true.

7. **Memo hits**
   In this particular example we never encounter the same `(i,j)` twice in the recursion, so there are no memo hits.  However, had we needed to explore both branches or revisit a state, at the top of `dp(i,j)` we’d immediately return the cached `memo[i][j]` in O(1) without diving deeper.

---

### Final answer

* `isInterleave("ab","cd","acbd")` → **true**, because `dp(2,2)` ends up `true`.
* We built up every needed `dp(i,j)` exactly once (thanks to memo), in **O(len1×len2)** time and space.

*/
class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        if(s3.length() != s1.length() + s2.length()){
            return false;
        }

        Boolean[][] memo = new Boolean[s1.length() + 1][s2.length() + 1];
        return dp(s1, s2, s3, memo, s1.length(), s2.length());
    }

    public boolean dp(String s1, String s2, String s3, Boolean[][] memo, int s1Len, int s2Len){
        
        if(s1Len == 0 && s2Len == 0){
            return true;
        }
        if(memo[s1Len][s2Len] != null){
            return memo[s1Len][s2Len];
        }

        boolean matchFromS1 = (s1Len > 0 && dp(s1, s2, s3, memo, s1Len - 1, s2Len) && s1.charAt(s1Len - 1) == s3.charAt(s1Len + s2Len - 1));
        boolean matchFromS2 = (s2Len > 0 && dp(s1, s2, s3, memo, s1Len, s2Len - 1) && s2.charAt(s2Len - 1) == s3.charAt(s1Len + s2Len - 1));

        memo[s1Len][s2Len] = matchFromS1 || matchFromS2;

        return memo[s1Len][s2Len];
    }
}

// Bottom Up DP
/*
## Detailed Explanation

1. **State Definition**
   We build a 2D boolean table `dp` of size `(m+1)×(n+1)`, where

   ```
   dp[i][j] == true
     ⇔ s3[0..i+j−1] is an interleaving of s1[0..i−1] and s2[0..j−1].
   ```

   Here `i` characters have been taken from `s1`, and `j` from `s2`, composing the first `i+j` characters of `s3`.

2. **Length Check**
   If `s3.length() != m + n`, it’s impossible to interleave exactly, so we immediately return `false`.

3. **Base Case `dp[0][0]`**
   With zero characters from both `s1` and `s2`, you form the empty prefix of `s3` in exactly one way → `dp[0][0] = true`.

4. **First Column `dp[i][0]`**
   You’ve taken `i` characters from `s1` and none from `s2`.  You can only match `s3[0..i−1]` if all those `i` characters exactly equal `s1[0..i−1]` in order. Thus:

   ```
   dp[i][0] = dp[i−1][0]  &&  (s1[i−1] == s3[i−1])
   ```

5. **First Row `dp[0][j]`**
   Symmetrically, taking `j` from `s2` and none from `s1`:

   ```
   dp[0][j] = dp[0][j−1]  &&  (s2[j−1] == s3[j−1])
   ```

6. **General Recurrence**
   At `(i,j)` (both ≥1), the next character in `s3` is at index `i+j−1`. You can arrive there either by:

   * **Taking from `s1`**: if the last taken char `s1[i−1]` matches `s3[i+j−1]`, and `dp[i−1][j]` was `true`; or
   * **Taking from `s2`**: if `s2[j−1]` matches `s3[i+j−1]`, and `dp[i][j−1]` was `true`.

   So:

   ```java
   dp[i][j] = (dp[i−1][j] && s1.charAt(i−1) == need)
           || (dp[i][j−1] && s2.charAt(j−1) == need);
   ```

7. **Answer**
   After filling the table row by row, column by column, the value at `dp[m][n]` tells you whether the entire `s3` can be formed by interleaving all of `s1` and `s2`.

---

### Complexity

* **Time:**  We fill an `(m+1)×(n+1)` table, doing O(1) work per cell → **O(m·n)**.
* **Space:** We store O(m·n) booleans in the DP array → **O(m·n)** extra space.

This bottom-up DP systematically builds all sub-results so that each decision—“did I pull the next char from `s1` or `s2`?”—is resolved in constant time using previously computed states.

Let’s illustrate the bottom-up DP table filling on this concrete example:

```
s1 = "aab"
s2 = "axy"
s3 = "aaxaby"
```

Lengths:

* m = s1.length() = 3
* n = s2.length() = 3
* s3.length() = 6 = m+n, so it’s possible.

We build a (m+1)×(n+1) table `dp`, where

```
dp[i][j] = true  iff  s3[0..i+j-1] is an interleaving of s1[0..i-1] and s2[0..j-1].
```

### Step 1: Initialize the table

| i\j   | 0    | 1 | 2 | 3 |
| ----- | ---- | - | - | - |
| **0** | true | ? | ? | ? |
| **1** | ?    |   |   |   |
| **2** | ?    |   |   |   |
| **3** | ?    |   |   |   |

* `dp[0][0] = true` (empty+empty→empty).

### Step 2: Fill first row (i=0)

We only use `s2` to match `s3`’s prefix:

* j=1: need `s3[0]=='a'`, and `s2[0]=='a'`, and `dp[0][0]` was true → **true**
* j=2: need `s3[1]=='a'`, but `s2[1]=='x'` → **false**
* j=3: need `s3[2]=='x'`, but `s2[2]=='y'` → **false**

| i\j   | 0    | 1        | 2         | 3         |
| ----- | ---- | -------- | --------- | --------- |
| **0** | true | **true** | **false** | **false** |
| **1** | ?    |          |           |           |
| **2** | ?    |          |           |           |
| **3** | ?    |          |           |           |

### Step 3: Fill first column (j=0)

We only use `s1` to match `s3`’s prefix:

* i=1: need `s3[0]=='a'`, `s1[0]=='a'`, `dp[0][0]==true` → **true**
* i=2: need `s3[1]=='a'`, `s1[1]=='a'`, `dp[1][0]==true` → **true**
* i=3: need `s3[2]=='x'`, but `s1[2]=='b'` → **false**

| i\j   | 0     | 1    | 2     | 3     |
| ----- | ----- | ---- | ----- | ----- |
| **0** | true  | true | false | false |
| **1** | true  |      |       |       |
| **2** | true  |      |       |       |
| **3** | false |      |       |       |

### Step 4: Fill the rest

Use the recurrence

```
dp[i][j] =  
       (dp[i-1][j] && s1[i-1] == s3[i+j-1])  
    || (dp[i][j-1] && s2[j-1] == s3[i+j-1])
```

* **i=1,j=1**: need `s3[1]=='a'`

  * from s1? `dp[0][1]=true` && `s1[0]=='a'` → true
  * from s2? `dp[1][0]=true` && `s2[0]=='a'` → true
    → **dp\[1]\[1]=true**

* **i=1,j=2**: need `s3[2]=='x'`

  * from s1? `dp[0][2]=false` → no
  * from s2? `dp[1][1]=true` && `s2[1]=='x'` → true
    → **dp\[1]\[2]=true**

* **i=1,j=3**: need `s3[3]=='a'`

  * from s1? `dp[0][3]=false`
  * from s2? `dp[1][2]=true` && `s2[2]=='y'` ≠ 'a'
    → **dp\[1]\[3]=false**

* **i=2,j=1**: need `s3[2]=='x'`

  * from s1? `dp[1][1]=true` && `s1[1]=='a'` ≠ 'x'
  * from s2? `dp[2][0]=true` && `s2[0]=='a'` ≠ 'x'
    → **dp\[2]\[1]=false**

* **i=2,j=2**: need `s3[3]=='a'`

  * from s1? `dp[1][2]=true` && `s1[1]=='a'` → true
  * (no need to check s2 branch since already true)
    → **dp\[2]\[2]=true**

* **i=2,j=3**: need `s3[4]=='b'`

  * from s1? `dp[1][3]=false`
  * from s2? `dp[2][2]=true` && `s2[2]=='y'` ≠ 'b'
    → **dp\[2]\[3]=false**

* **i=3,j=1**: need `s3[3]=='a'`

  * from s1? `dp[2][1]=false`
  * from s2? `dp[3][0]=false`
    → **dp\[3]\[1]=false**

* **i=3,j=2**: need `s3[4]=='b'`

  * from s1? `dp[2][2]=true` && `s1[2]=='b'` → true
    → **dp\[3]\[2]=true**

* **i=3,j=3**: need `s3[5]=='y'`

  * from s1? `dp[2][3]=false`
  * from s2? `dp[3][2]=true` && `s2[2]=='y'` → true
    → **dp\[3]\[3]=true**

The final table:

| i\j   | 0     | 1     | 2     | 3     |
| ----- | ----- | ----- | ----- | ----- |
| **0** | true  | true  | false | false |
| **1** | true  | true  | true  | false |
| **2** | true  | false | true  | false |
| **3** | false | false | true  | true  |

Because `dp[m][n] = dp[3][3] = true`, `"acbd"` **is** an interleaving of `"aab"` and `"axy"`.

---

### Why this works

* **dp\[i]\[j]** captures **all** ways to build the first `i+j` chars of `s3` from prefixes of `s1` and `s2`.
* The **boundary rows/columns** handle the cases where you’ve taken exclusively from one string.
* The **double-choice recurrence** at `(i,j)` exactly mirrors “did the last char come from `s1` or `s2`?”
* Filling in increasing `i, j` order ensures every subproblem `(i−1,j)` and `(i,j−1)` is computed before it’s needed.
* Final answer is whether the full lengths `(m,n)` succeed.

*/

// class Solution {
//     public boolean isInterleave(String s1, String s2, String s3) {
//         int m = s1.length(), n = s2.length();
//         // 1) Quick length check
//         if (s3.length() != m + n) return false;

//         // 2) dp[i][j] = true if s3[0..i+j-1] can be formed by interleaving
//         //    s1[0..i-1] and s2[0..j-1].
//         boolean[][] dp = new boolean[m+1][n+1];

//         // 3) Base case: empty s1 and s2 make empty s3
//         dp[0][0] = true;

//         // 4) First column: only use s1 to match prefix of s3
//         for (int i = 1; i <= m; i++) {
//             dp[i][0] = dp[i-1][0]
//                 && s1.charAt(i-1) == s3.charAt(i-1);
//         }

//         // 5) First row: only use s2 to match prefix of s3
//         for (int j = 1; j <= n; j++) {
//             dp[0][j] = dp[0][j-1]
//                 && s2.charAt(j-1) == s3.charAt(j-1);
//         }

//         // 6) Fill the rest: choose to take next char from s1 or s2
//         for (int i = 1; i <= m; i++) {
//             for (int j = 1; j <= n; j++) {
//                 char need = s3.charAt(i + j - 1);
//                 // Option A: last char came from s1
//                 boolean fromS1 = dp[i-1][j]
//                     && s1.charAt(i-1) == need;
//                 // Option B: last char came from s2
//                 boolean fromS2 = dp[i][j-1]
//                     && s2.charAt(j-1) == need;
//                 dp[i][j] = fromS1 || fromS2;
//             }
//         }

//         // 7) The full lengths m,n tell us if full s3 is interleaving
//         return dp[m][n];
//     }
// }
