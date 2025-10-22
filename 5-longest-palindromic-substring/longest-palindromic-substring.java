// Method 1: Expanding around center O(n²) time, O(n) space
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
class Solution {
    public String longestPalindrome(String s) {
        int n = s.length();
        if (n < 2) return s;

        int bestStart = 0, bestLen = 1;

        for (int i = 0; i < n; i++) {
            // odd center at (i, i)
            int[] odd = expand(s, i, i);
            // even center at (i, i+1)
            int[] even = expand(s, i, i + 1);

            int[] better = (odd[1] >= even[1]) ? odd : even;
            if (better[1] > bestLen) {
                bestStart = better[0];
                bestLen   = better[1];
            }
        }
        return s.substring(bestStart, bestStart + bestLen);
    }

    // Expand while s[l] == s[r], then return {start, len} of the last valid window
    private int[] expand(String s, int l, int r) {
        int n = s.length();
        while (l >= 0 && r < n && s.charAt(l) == s.charAt(r)) {
            l--; r++;
        }
        // Now (l, r) is invalid; the palindrome is (l+1 .. r-1)
        int start = l + 1;
        int len   = r - l - 1;
        return new int[]{start, len};
    }
}



// Method 2: DP Using Tabulation
/*
## \U0001f9e9 Core idea

We want to know whether each substring `s[l..r]` (inclusive) is a palindrome.
Define a 2-D boolean DP table:

```
dp[l][r] = true  if s[l..r] is palindrome
         = false otherwise
```

Once we know which substrings are palindromes, we can keep track of the **longest** one.

---

## \U0001f9e0 Recurrence

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

## \U0001f3d7️ Filling order

We must compute `dp[l+1][r-1]` **before** using it, so we fill the table by **increasing substring length**.

1. Start with length = 1 (single letters)
2. Then length = 2
3. Then length ≥ 3 …


## \U0001f9ee Complexity

* **Time:** O(n²) — we check every substring once.
* **Space:** O(n²) for the boolean table.

---

## \U0001f9ed Walkthrough example: `"babad"`

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

## \U0001f4a1 Intuition check

* Length-1 windows initialize palindromes.
* Length-2 checks handle pairs like `"bb"`.
* From length 3 onward, we “grow” palindromes outward.
* Every cell `dp[l][r]` reuses the result of a smaller window, which is the hallmark of dynamic programming.

---

## \U0001f50d Key pitfalls avoided

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




// Top-Down / Recursive solution is very inefficient even after memoization