// Method 1: Top-Down 2D DP
/*
## 2. Minor things you *could* tweak (but not required)

These aren’t “wrong”, just things to be aware of:

1. **The `l > r` base case**

   ```java
   if (l > r) {
       return false;
   }
   ```

   Mathematically, the **empty string** is often considered a palindrome, so many people set this to `true`.

   In **your code**, you actually never rely on this case; due to the way you structured the recursion (2-char palindromes handled as a separate base), you never call `dp` with `l > r`. So this line is basically dead code; not harmful, just unused.

2. **Not memoizing base cases**

   For `l == r` or length-2 palindromes, you `return true` directly, without touching `memo[l][r]`.

   That’s fine:

   * Those are cheap to recompute, and
   * You don’t rely on `memo` for correctness here.

   If you *wanted*, you could also store them into `memo` (micro-optimization) — but it’s not necessary.

3. **`bestLen` starting at 0**

   This is okay because every non-empty string has at least 1-length palindromes (single chars), and you correctly handle those (for `l == r`, `dp` returns true).

   For empty string input (`s = ""`):

   * `n = 0`, loops don’t run.
   * `bestLen = 0`, `bestStart = 0`.
   * `s.substring(0, 0)` returns `""`.
     Also fine.

So: **no real bug**. Your approach is correct.

---

## 3. Detailed walkthrough on `"babad"`

Let’s trace the key parts so you can see the DP in action.

### Setup

```text
s = "babad"
idx: 0 1 2 3 4
     b a b a d

n = 5
memo = 5x5, all null
bestStart = 0
bestLen = 0
```

You loop:

```java
for (l = 0..4)
  for (r = l..4)
    check dp(l, r)
```

I’ll focus on the **important palindromes** and when `bestLen` updates.

---

### 3.1. All length-1 substrings (l == r)

For `l = 0..4, r = l`:

Example: `l = 0, r = 0` (`"b"`)

* `dp(0,0)`:

  * `l > r`? no
  * `l == r`? yes → return `true`

So:

```text
len = 0 - 0 + 1 = 1
len > bestLen? 1 > 0 → yes
bestLen = 1
bestStart = 0
```

Similarly for `l = 1, r = 1` → `"a"` (true, length 1).
But `len = 1 > bestLen = 1`? no, so no update.

After all single characters, we have:

```text
bestLen = 1
bestStart = 0   // pointing to "b" at index 0
```

So we already know at least one palindrome of length 1.

---

### 3.2. Finding `"bab"` = s[0..2]

Now consider `l = 0, r = 2` → substring `"bab"`.

Call `dp(0, 2)`:

* `l > r`? `0 > 2` → no
* `l == r`? no
* `r == l+1 && s[0] == s[2]`?

  * `2 == 1`? no, so skip that
* `memo[0][2]`? null, so compute.

Check characters:

```java
if (s.charAt(l) == s.charAt(r) && dp(l+1, r-1, ...)) {
```

* `s[0] = 'b'`
* `s[2] = 'b'`
* They are equal, so we need `dp(1, 1)`.

#### Inside: `dp(1, 1)` → substring `"a"`

* `l > r`? `1 > 1` → no
* `l == r`? yes → return `true`.

So back to `dp(0,2)`:

* `s[0] == s[2]` **and** `dp(1,1)` is `true`
  → `isPalindrome = true`

You store:

```text
memo[0][2] = true
dp(0,2) -> true
```

In the outer loop:

```java
if (dp(0, 2, ...)) {
    len = 2 - 0 + 1 = 3
    if (len > bestLen) {  // 3 > 1 → yes
        bestLen = 3
        bestStart = 0
    }
}
```

Now:

```text
bestLen = 3
bestStart = 0
→ current longest palindrome = "bab"
```

---

### 3.3. Finding `"aba"` = s[1..3]

Next, consider `l = 1, r = 3` → `"aba"`.

Call `dp(1,3)`:

* `l > r`? no
* `l == r`? no
* `r == l+1 && s[1] == s[3]`?

  * `3 == 2`? no → skip
* `memo[1][3]`? null

Check chars:

* `s[1] = 'a'`, `s[3] = 'a'` → equal
* So we need `dp(2,2)`.

#### Inside: `dp(2,2)` → substring `"b"`

* `l == r` → return `true`.

Back to `dp(1,3)`:

* `s[1] == s[3]` **and** `dp(2,2)` is `true`
  → `isPalindrome = true`
* `memo[1][3] = true`

Outer loop:

```java
len = 3 - 1 + 1 = 3
len > bestLen? 3 > 3? → no
```

So we **don’t update** `bestLen`/`bestStart` (tie of length 3).

End result:

* You might return either `"bab"` (indices 0..2) or `"aba"` (indices 1..3), depending on which you saw first. In this loop order, it’ll be `"bab"`.

---

### 3.4. What about non-palindromes?

Example: `l = 0, r = 4` → `"babad"`

`dp(0,4)`:

* `l == r`? no

* `r == l+1 && ...`? no

* `memo[0][4]`? null

* Check `s[0] == s[4]`? → `'b' != 'd'` → false
  So the `if` block:

  ```java
  if (s.charAt(l) == s.charAt(r) && dp(l+1, r-1, ...)) {
      isPalindrome = true;
  }
  ```

  is skipped.

* So `isPalindrome` stays `false`

* `memo[0][4] = false`

Thus non-palindromes are correctly marked `false`.

---

## 4. Quick extra example: `"cbbd"`

Just to see the length-2 base case in action:

```text
s = "cbbd"
0:'c', 1:'b', 2:'b', 3:'d'
```

The correct answer is `"bb"` (indices 1..2).

Look at `l = 1, r = 2`:

`dp(1,2)`:

* `l > r`? → no
* `l == r`? → no
* `r == l+1 && s[1] == s[2]`?

  * `2 == 1 + 1` → true
  * `s[1] == 'b'`, `s[2] == 'b'` → equal
    → base case returns `true`.

Then:

```java
len = 2 - 1 + 1 = 2
if (len > bestLen) { // initially bestLen was 1 from single chars
    bestLen = 2;
    bestStart = 1;
}
```

So the algorithm correctly identifies `"bb"` as the longest palindromic substring.

---

## Summary

* Your top-down 2D DP is **correct**.
* It uses the classic recursive palindrome check:

  * Same ends + inside palindrome.
* The outer double loop checks **all substrings** and tracks the largest.
* Complexity is **O(n²)** time and **O(n²)** space.
* The only “odd” bits (like `l > r` → `false` and not memoizing base cases) are harmless given how you structured the recursion.
*/
// class Solution {
//     public String longestPalindrome(String s) {
//         int n = s.length();

//         Boolean[][] memo = new Boolean[n][n];
//         int bestStart = 0;
//         int bestLen = 0;

//         for(int l=0; l<n; l++){
//             for(int r=l; r<n; r++){
//                 if(dp(l, r, memo, s)){
//                     int len = r - l + 1;
//                     if(len > bestLen){
//                         bestLen = len;
//                         bestStart = l;
//                     }
//                 }
//             }
//         }

//         return s.substring(bestStart, bestStart + bestLen);
//     }

//     private boolean dp(int l, int r, Boolean[][] memo, String s){
//         if(l > r){
//             return false;
//         }
//         if(l == r || (r == l+1 && s.charAt(l) == s.charAt(r))){
//             return true;
//         }

//         if(memo[l][r] != null){
//             return memo[l][r];
//         }

//         boolean isPalindrome = false;
//         if(s.charAt(l) == s.charAt(r) && dp(l+1, r-1, memo, s)){
//             isPalindrome = true;
//         }

//         memo[l][r] = isPalindrome;

//         return memo[l][r];        
//     }
// }






// Method 1.5: My Top-Down Approach (similar to the one above)
/*
*/
class Solution {
    public String longestPalindrome(String s) {
        int n = s.length();
        int[][] memo = new int[n][n];

        for(int[] row: memo){
            Arrays.fill(row, -1);
        }

        int bestStart = 0;
        int bestLen = 0;

        for(int i=0; i<n; i++){
            for(int j=i; j<n; j++){
                if(isPalindromeDP(s, i, j, memo)){
                    if((j - i + 1) > bestLen){
                        bestStart = i;
                        bestLen = j - i + 1;
                    }
                }
            }
        }

        return s.substring(bestStart, bestStart + bestLen);
    }

    private boolean isPalindromeDP(String s, int left, int right, int[][] memo){
        if(left >= right){
            return true;
        }

        if(memo[left][right] != -1){
            return memo[left][right] == 1;
        }

        if(s.charAt(left) != s.charAt(right)){
            memo[left][right] = 0;
            return false;
        }

        memo[left][right] = isPalindromeDP(s, left + 1, right - 1, memo) ? 1 : 0;

        return memo[left][right] == 1;
    }
}






// Method 2: Bottom-Up 2D DP Approach
/*
## 🧩 Core idea

We want to know whether each substring `s[l..r]` (inclusive) is a palindrome.
Define a 2-D boolean DP table:

```
dp[l][r] = true  if s[l..r] is palindrome
         = false otherwise
```

Once we know which substrings are palindromes, we can keep track of the **longest** one.

---

## 🧠 Recurrence

A substring `s[l..r]` is a palindrome if:

1. The two boundary characters match: `s[l] == s[r]`
2. And **the inner substring** `s[l+1..r-1]` is also a palindrome (or the substring length ≤ 2).

Formally:

```
dp[l][r] = (s[l] == s[r]) && (r - l < 3 || dp[l+1][r-1])
```

Why `r - l < 3`?
Because any 1- or 2-char substring is automatically a palindrome if its ends match.

---

## 🏗️ Filling order

We must compute `dp[l+1][r-1]` **before** using it, so we fill the table by **increasing substring length**.

1. Start with length = 1 (single letters)
2. Then length = 2
3. Then length ≥ 3 …


## 🧮 Complexity

* **Time:** O(n²) — we check every substring once.
* **Space:** O(n²) for the boolean table.

---

## 🧭 Walkthrough example: `"babad"`

`n = 5`, indices 0-4: b a b a d
Initialize `dp[i][i] = true` (all singles).

We’ll show just the important states.

| l | r | s[l..r] | s[l]==s[r]? | Inner      | dp[l][r] | Longest                |
| - | - | ------- | ----------- | ---------- | -------- | ---------------------- |
| 0 | 1 | "ba"    | ✗           | —          | F        | len1                   |
| 1 | 2 | "ab"    | ✗           | —          | F        | len1                   |
| 2 | 3 | "ba"    | ✗           | —          | F        | len1                   |
| 3 | 4 | "ad"    | ✗           | —          | F        | len1                   |
| 0 | 2 | "bab"   | ✓           | dp[1][1]=T | **T**    | update → start=0,len=3 |
| 1 | 3 | "aba"   | ✓           | dp[2][2]=T | **T**    | tie len=3              |
| 2 | 4 | "bad"   | ✗           | —          | F        | —                      |

All longer windows (>3) fail.
Final answer: substring(0, 3) = "bab" (or "aba", both valid).

---

## 💡 Intuition check

* Length-1 windows initialize palindromes.
* Length-2 checks handle pairs like `"bb"`.
* From length 3 onward, we “grow” palindromes outward.
* Every cell `dp[l][r]` reuses the result of a smaller window, which is the hallmark of dynamic programming.

---

## 🔍 Key pitfalls avoided

* Forgetting to seed length-1 substrings (all palindromes).
* Accessing `dp[l+1][r-1]` before ensuring indices are valid (that’s why we fill by length).
* Not handling even-length case separately (our `len==2` condition covers it).
*/

// class Solution {
//     public String longestPalindrome(String s) {
//         int n = s.length();
//         if (n < 2) return s;

//         boolean[][] dp = new boolean[n][n];
//         int bestStart = 0, bestLen = 1;

//         // 1. Base: all single characters
//         for (int i = 0; i < n; i++) dp[i][i] = true;

//         // 2. Fill table by window length
//         for (int len = 2; len <= n; len++) {
//             for (int l = 0; l + len - 1 < n; l++) {
//                 int r = l + len - 1;

//                 if (s.charAt(l) == s.charAt(r)) {
//                     if (len == 2 || dp[l + 1][r - 1]) {
//                         dp[l][r] = true;
//                         if (len > bestLen) {
//                             bestStart = l;
//                             bestLen = len;
//                         }
//                     }
//                 }
//             }
//         }

//         return s.substring(bestStart, bestStart + bestLen);
//     }
// }







// Method 3: Expanding around center O(n²) time, O(n) space
/*
## What “centers” mean

A palindrome mirrors around a center. There are two types of centers in a string `s` of length `n`:

1. **Odd-length centers** at each index `i` (like `"aba"` centered on the middle `'b'`).
   Initialize `left = i`, `right = i`.

2. **Even-length centers** between `i` and `i+1` (like `"abba"` centered between the two `'b'`s).
   Initialize `left = i`, `right = i+1`.

Total centers to try: `n` odd + `n-1` even = **`2n-1`**.

---

## Expanding from a center

For a chosen `(left, right)`:

* While `left >= 0` and `right < n` **and** `s[left] == s[right]`:

  * Expand outward: `left--`, `right++`.
* When the loop stops, the palindrome is the **last valid range**: `(left+1 .. right-1)`.
  Its **length** is `right - left - 1`, and **start** index is `left + 1`.

Why this works: a palindrome is fully determined by its radius around the center. As soon as you hit a mismatch or boundary, you’ve maximized that center’s palindrome.

---

## Bookkeeping: tracking the best answer

Keep two integers:

* `bestStart` — start index of the longest palindrome seen so far
* `bestLen` — its length

After each expansion, compute `currLen = right - left - 1`.
If `currLen > bestLen`, update:

* `bestLen = currLen`
* `bestStart = left + 1`

(When lengths tie, you can keep the existing one; any longest is acceptable.)

---

## Step-by-step examples

### Example 1: `"babad"`

* Center `i=0` (odd): expand on `'b'` → `"b"` (len 1).
* Center between `0` and `1` (even): `'b'` vs `'a'` mismatch → len 0.
* `i=1` (odd): expand on `'a'`:

  * `'a'` matches → try `'b'` (left) and `'b'` (right) → `"bab"` (len 3).
  * Next step `'?'` vs `'a'` (out of bounds / mismatch) → stop. Record `"bab"`.
* Between `1` and `2` (even): `'a'` vs `'b'` mismatch.
* `i=2` (odd): expand on `'b'`:

  * `'b'` matches → try `'a'` and `'a'` → `"aba"` (len 3).
  * Next step out of bounds → stop. We now have `"bab"` and `"aba"` both length 3.
* Remaining centers won’t beat 3, but you still check. Final longest is length 3 (either substring is fine).

### Example 2: `"cbbd"`

* `i=1` (odd): `'b'` → just `"b"`.
* Between `1` and `2` (even): `'b'` vs `'b'` → expand to `"bb"` (len 2); next expand fails → record `"bb"`.
* No other center yields longer → answer length is 2.

---

## Edge cases & gotchas

* **Even centers are essential.** Forgetting them misses cases like `"bb"` and `"abba"`.
* **Index math:** when expansion stops at `(left, right)`, the valid palindrome is `[left+1, right)` (half-open). Length = `right - left - 1`.
* **Single-character strings** → longest is the string itself (length 1).
* **All identical chars** (`"aaaaa"`) → the whole string is palindromic. Even centers will chain outwards correctly.
* **Empty string** → length 0; handle upfront if needed.

---

## Complexity

* You attempt `~2n` centers, and each expansion moves `left` and `right` at most `n` times across all centers.
* **Time:** `O(n²)` worst case (e.g., `"aaaa…a"`).
* **Space:** `O(1)` (just a few integers).

---

## Optional pruning (micro-optimization)

If you already have `bestLen`, and for the current center the **maximum possible** palindrome (even if it extended to the edges) can’t exceed `bestLen`, you can skip expanding that center. (Good in practice, not required.)

---

## Result extraction (when you’re done)

Once you’ve scanned all centers, your answer is the substring:

```
s.substring(bestStart, bestStart + bestLen)
```

### Why this fixes your issues

* **No global temp state.** Each expansion is self-contained and returns its own result.
* **Proper update rule.** You only update `bestStart/bestLen` when you actually find a longer palindrome.
* **Correct length & start.** Computed from the final bounds after expansion (`right - left - 1`, `left + 1`).
* **Even/odd handled uniformly.** `expand` safely handles out-of-bounds by immediately returning length 0 if needed.

---

### Quick walkthrough (“babad”)

* `i=0`:

  * odd around `b` → “b” (start 0, len 1)
  * even between `b|a` → “” (len 0)
    Best so far: start 0, len 1
* `i=1`:

  * odd around `a` → expand to “bab” (start 0, len 3)
  * even `a|b` → “”
    Update: start 0, len 3
* `i=2`:

  * odd around `b` → “aba” (start 1, len 3) (ties ok)
  * even `b|a` → “”
    Keep len 3
* `i=3,4`: no longer palindrome than 3.
  Return substring with `(bestStart, bestLen)` → either “bab” or “aba”.

If you want, I can show a micro-optimization to skip centers that cannot beat `bestLen`.
*/

// class Solution {
//     public String longestPalindrome(String s) {
//         int n = s.length();
//         if (n < 2) return s;

//         int bestStart = 0, bestLen = 1;

//         for (int i = 0; i < n; i++) {
//             // odd center at (i, i)
//             int[] odd = expand(s, i, i);
//             // even center at (i, i+1)
//             int[] even = expand(s, i, i + 1);

//             int[] better = (odd[1] >= even[1]) ? odd : even;
//             if (better[1] > bestLen) {
//                 bestStart = better[0];
//                 bestLen   = better[1];
//             }
//         }
//         return s.substring(bestStart, bestStart + bestLen);
//     }

//     // Expand while s[l] == s[r], then return {start, len} of the last valid window
//     private int[] expand(String s, int l, int r) {
//         int n = s.length();
//         while (l >= 0 && r < n && s.charAt(l) == s.charAt(r)) {
//             l--; r++;
//         }
//         // Now (l, r) is invalid; the palindrome is (l+1 .. r-1)
//         int start = l + 1;
//         int len   = r - l - 1;
//         return new int[]{start, len};
//     }
// }

