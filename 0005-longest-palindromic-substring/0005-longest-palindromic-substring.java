// Top Down DP (try after pair of indices)
/*
## Detailed Explanation

1. **State definition**
   We use a 2D memo table `memo[i][j]` storing whether `s[i..j]` is a palindrome.  The entry is initially `null` to indicate “not computed.”

2. **Base cases**

   * If `i >= j`, the substring is length 0 or 1, trivially a palindrome → return `true`.
   * If `s.charAt(i) != s.charAt(j)`, the ends don’t match → return `false`.

3. **Recurrence**

   ```java
   isPal(i,j) = (s[i] == s[j]) && isPal(i+1, j-1)
   ```

   We only recurse one level deeper on the interior `(i+1, j-1)` once the end‐characters match.

4. **Memoization**
   Once `isPal(i,j)` is computed, we store it in `memo[i][j]`. Any subsequent call for the same `(i,j)` returns immediately in O(1).

5. **Finding the longest**
   We iterate all start/end pairs `(i,j)` with two nested loops. Whenever `isPal(i,j)` is `true`, we compare `j−i+1` to our current `bestLen`. If it’s larger, we update `bestLen` and record `bestStart = i`.

6. **Result extraction**
   After filling the DP via those calls, the longest palindromic substring is `s.substring(bestStart, bestStart + bestLen)`.

7. **Complexity**

   * **Time:** There are O(n²) possible `(i,j)` pairs; each `isPal` call is O(1) after memoization, so O(n²) overall.
   * **Space:** The `memo` table is O(n²), and recursion depth is O(n) in the worst case.

---

## Example & Recursion Tree

Let’s walk through `s = "babad"`. We’ll highlight how `isPal(0,2)` (“bab”) and `isPal(1,3)` (“aba”) get computed and memoized.

```
Calls for checking isPal(0,2):
isPal(0,2)
├─ s[0] == s[2]?  'b' == 'b' → yes
└─ call isPal(1,1)
   └─ i>=j (1>=1) → return true
└─ memo[0][2] = true

Calls for checking isPal(1,3):
isPal(1,3)
├─ s[1] == s[3]?  'a' == 'a' → yes
└─ call isPal(2,2)
   └─ i>=j (2>=2) → return true
└─ memo[1][3] = true
```

Detailed tree including memo hits:

```
isPal(0,2)
├─ (0,2) memo null
├─ chars match → recurse isPal(1,1)
│  └─ base i>=j → true
└─ store memo[0][2]=true → return true

isPal(1,3)
├─ (1,3) memo null
├─ chars match → recurse isPal(2,2)
│  └─ base i>=j → true
└─ store memo[1][3]=true → return true

Later, if we call isPal(0,2) again:
isPal(0,2)
└─ memo[0][2] != null → return true (no recursion)
```

Meanwhile, the outer loops will discover that both substrings of length 3 are palindromes, updating `bestLen` to 3 and `bestStart` to the first occurrence (index 0). The final returned substring is `"bab"`.

*/
class Solution {
    private String s;
    // memo[i][j] == null   → not yet computed
    // memo[i][j] == true   → s[i..j] is a palindrome
    // memo[i][j] == false  → s[i..j] is not a palindrome
    private Boolean[][] memo;
    private int bestStart, bestLen;

    public String longestPalindrome(String s) {
        this.s = s;
        int n = s.length();
        memo = new Boolean[n][n];
        bestStart = 0;
        bestLen   = 0;

        // Try every substring s[i..j]
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (isPal(i, j)) {
                    int length = j - i + 1;
                    if (length > bestLen) {
                        bestLen   = length;
                        bestStart = i;
                    }
                }
            }
        }

        return s.substring(bestStart, bestStart + bestLen);
    }

    private boolean isPal(int i, int j) {
        // Base case: empty or single-char is palindrome
        if (i >= j) {
            return true;
        }
        // Return cached result if available
        if (memo[i][j] != null) {
            return memo[i][j];
        }
        boolean res;
        // Ends must match, and the interior must be a palindrome
        if (s.charAt(i) != s.charAt(j)) {
            res = false;
        } else {
            res = isPal(i + 1, j - 1);
        }
        memo[i][j] = res;
        return res;
    }
}

// Bottom Up DP approch
/* 
## Detailed Explanation

1. **DP State**
   We define a 2D boolean array `dp` of size `n×n` (where `n = s.length()`):

   ```
   dp[i][j] == true  ⇔  the substring s[i..j] is a palindrome
   ```

2. **Initialization**

   * **Length = 1**: every single character is a palindrome, so for all `i`:

     ```java
     dp[i][i] = true;
     ```
   * **Length = 2**: check each adjacent pair; if they match, they form a palindrome of length 2:

     ```java
     if (s.charAt(i) == s.charAt(i+1)) {
         dp[i][i+1] = true;
     }
     ```

3. **General Recurrence**
   For any substring `s[i..j]` with length ≥ 3, it’s a palindrome exactly when:

   1. The end‐characters match: `s.charAt(i) == s.charAt(j)`, **and**
   2. The interior substring `s[i+1..j−1]` is also a palindrome (`dp[i+1][j-1] == true`).

   Thus for increasing lengths `len` from 3 up to `n`, we loop:

   ```java
   for (int len = 3; len <= n; len++) {
       for (int i = 0; i + len - 1 < n; i++) {
           int j = i + len - 1;
           if (s.charAt(i) == s.charAt(j) && dp[i+1][j-1]) {
               dp[i][j] = true;
               // update best length/start if this is the longest so far
           }
       }
   }
   ```

4. **Tracking the longest**

   * We maintain `bestStart` and `bestLen`.
   * Whenever we set `dp[i][j] = true`, we check if `(j − i + 1) > bestLen`; if so, we update `bestLen = j−i+1` and `bestStart = i`.

5. **Result Extraction**
   After the DP table is fully populated, the longest palindromic substring is simply

   ```java
   s.substring(bestStart, bestStart + bestLen);
   ```

---

### Complexity

* **Time:**

  * We fill an `n×n` table.
  * The outer loop over `len` runs O(n) times; the inner loop over valid starts `i` runs O(n) times; each step is O(1).
  * Total: **O(n²)**.

* **Space:**

  * We use an `n×n` boolean table → **O(n²)** extra space.
  * Plus O(1) for tracking `bestStart`, `bestLen`, loop indices, etc.

This bottom‐up DP systematically builds up palindrome information for all substrings, ensuring that when you need to know if `s[i..j]` is a palindrome, its interior `s[i+1..j-1]` has already been determined.

*/
// class Solution {
//     public String longestPalindrome(String s) {
//         int n = s.length();
//         if (n < 2) {
//             // Any single char or empty string is itself a palindrome
//             return s;
//         }

//         // dp[i][j] will be true if s[i..j] is a palindrome
//         boolean[][] dp = new boolean[n][n];

//         int bestStart = 0;
//         int bestLen = 1;

//         // 1) Base case: all substrings of length 1 are palindromes
//         for (int i = 0; i < n; i++) {
//             dp[i][i] = true;
//         }

//         // 2) Consider substrings of length 2 explicitly
//         for (int i = 0; i < n - 1; i++) {
//             if (s.charAt(i) == s.charAt(i + 1)) {
//                 dp[i][i + 1] = true;
//                 bestStart = i;
//                 bestLen = 2;
//             }
//         }

//         // 3) Build up for substrings of length 3…n
//         for (int len = 3; len <= n; len++) {
//             // i = starting index, j = ending index = i + len - 1
//             for (int i = 0; i + len - 1 < n; i++) {
//                 int j = i + len - 1;
//                 // A substring is palindrome if its ends match and its interior is a palindrome
//                 if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
//                     dp[i][j] = true;
//                     if (len > bestLen) {
//                         bestLen = len;
//                         bestStart = i;
//                     }
//                 }
//             }
//         }

//         // 4) Extract the longest palindrome
//         return s.substring(bestStart, bestStart + bestLen);
//     }
// }
