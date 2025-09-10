// Method 1: Two Pointer approach O(n·m)
class Solution {
    public int strStr(String haystack, String needle) {
        int n = haystack.length(), m = needle.length();
        if (m == 0) return 0;          // required by problem
        if (m > n) return -1;

        int pointerNeed = 0;
        int firstOcc = -1;

        for (int i = 0; i < n; i++) {
            if (haystack.charAt(i) == needle.charAt(pointerNeed)) {
                if (firstOcc == -1) firstOcc = i;
                pointerNeed++;
                if (pointerNeed == m) return firstOcc;  // or i - m + 1
            } else {
                if (pointerNeed > 0) {
                    // roll back so next i++ tries firstOcc+1
                    i -= pointerNeed;
                }
                pointerNeed = 0;
                firstOcc = -1;
            }
        }
        return -1;
    }
}




// Method 2: KMP (Knuth–Morris–Pratt) — O(n+m) time, O(m) space
/*
Precompute an LPS (longest proper prefix that’s also a suffix) array for the needle. While scanning haystack, on a mismatch you don’t reset the pattern to start; you jump to lps[j-1] (the next best border), keeping time linear.

KMP dry-run** on the classic case:

* **haystack** = `"abxabcabcaby"`
* **needle**   = `"abcaby"`

# Step 1 — Build the LPS array for `"abcaby"`

`needle = a b c a b y` (indices 0..5)

* `lps[0] = 0` by definition.
* We track `len` = length of the longest border for the prefix ending at `i-1`.

| i | p\[i] | len | p\[len] | action                             | lps\[]                  | new len |
| - | ----- | --- | ------- | ---------------------------------- | ----------------------- | ------- |
| 1 | b     | 0   | a       | mismatch & len==0 → lps\[1]=0, i++ | \[0, **0**, ?, ?, ?, ?] | 0       |
| 2 | c     | 0   | a       | mismatch & len==0 → lps\[2]=0, i++ | \[0, 0, **0**, ?, ?, ?] | 0       |
| 3 | a     | 0   | a       | match → len=1; lps\[3]=1; i++      | \[0, 0, 0, **1**, ?, ?] | 1       |
| 4 | b     | 1   | b       | match → len=2; lps\[4]=2; i++      | \[0, 0, 0, 1, **2**, ?] | 2       |
| 5 | y     | 2   | c       | mismatch & len>0 → len=lps\[1]=0   | \[0, 0, 0, 1, 2, ?]     | 0       |
| 5 | y     | 0   | a       | mismatch & len==0 → lps\[5]=0; i++ | \[0, 0, 0, 1, 2, **0**] | 0       |

**LPS = `[0, 0, 0, 1, 2, 0]`.**

Interpretation:

* After matching `…abcab` (prefix length 5), the best border has length 2 (`"ab"`).
* After `…abcaby` (length 6), no border → 0.

---

# Step 2 — Scan the haystack with KMP

We maintain:

* `i` = index in `haystack`
* `j` = index in `needle` (also the **length of matched prefix** so far)

Start: `i=0, j=0`.
`hay = a  b  x  a  b  c  a  b  c  a  b  y`
`idx = 0  1  2  3  4  5  6  7  8  9  10 11`
`need = a  b  c  a  b  y`

| step | i  | j | hay\[i] | need\[j] | action                                     | new i,j      |
| ---- | -- | - | ------- | -------- | ------------------------------------------ | ------------ |
| 1    | 0  | 0 | a       | a        | match                                      | i=1, j=1     |
| 2    | 1  | 1 | b       | b        | match                                      | i=2, j=2     |
| 3    | 2  | 2 | x       | c        | mismatch, j>0 → j = lps\[1] = 0            | i=2, j=0     |
| 4    | 2  | 0 | x       | a        | mismatch, j==0 → i++                       | i=3, j=0     |
| 5    | 3  | 0 | a       | a        | match                                      | i=4, j=1     |
| 6    | 4  | 1 | b       | b        | match                                      | i=5, j=2     |
| 7    | 5  | 2 | c       | c        | match                                      | i=6, j=3     |
| 8    | 6  | 3 | a       | a        | match                                      | i=7, j=4     |
| 9    | 7  | 4 | b       | b        | match                                      | i=8, j=5     |
| 10   | 8  | 5 | c       | y        | mismatch, j>0 → j = lps\[4] = 2            | i=8, j=2     |
| 11   | 8  | 2 | c       | c        | match                                      | i=9, j=3     |
| 12   | 9  | 3 | a       | a        | match                                      | i=10, j=4    |
| 13   | 10 | 4 | b       | b        | match                                      | i=11, j=5    |
| 14   | 11 | 5 | y       | y        | match → j==m (6) → return i-m+1 = 11-6+1=6 | **answer=6** |

**Result:** index **6** (the substring `"abcaby"` starts at position 6 in `"abxabcabcaby"`).

---

## Why KMP is linear here

* On each mismatch, we **don’t** move `i` backward; we only reduce `j` using `lps`.
* Each character is compared at most a constant number of times across the scan.
* Total complexity: `O(n + m)`.

---

## (Optional) Overlap example to see the LPS “saves work”

Take `haystack = "aaaaaaab"`, `needle = "aaaab"`.

* `LPS("aaaab") = [0,1,2,3,0]`.
* You’ll match a run of `'a'`s, then on the mismatch, KMP jumps `j` from 4 → `lps[3]=3`, trying to reuse the prefix `"aaa"` instead of restarting—this is what prevents quadratic behavior.
*/
// class Solution {
//     public int strStr(String haystack, String needle) {
//         int n = haystack.length(), m = needle.length();
//         if (m == 0) return 0;
//         if (m > n) return -1;

//         int[] lps = buildLPS(needle);
//         int i = 0, j = 0; // i over haystack, j over needle
//         while (i < n) {
//             if (haystack.charAt(i) == needle.charAt(j)) {
//                 i++; j++;
//                 if (j == m) return i - m; // match ends at i-1
//             } else if (j > 0) {
//                 j = lps[j - 1];          // fall back in pattern
//             } else {
//                 i++;                      // no partial match: advance text
//             }
//         }
//         return -1;
//     }

//     private int[] buildLPS(String p) {
//         int m = p.length();
//         int[] lps = new int[m];
//         int len = 0; // length of current border
//         for (int i = 1; i < m; ) {
//             if (p.charAt(i) == p.charAt(len)) {
//                 lps[i++] = ++len;
//             } else if (len > 0) {
//                 len = lps[len - 1];
//             } else {
//                 lps[i++] = 0;
//             }
//         }
//         return lps;
//     }
// }



// Method 3: Rabin-Karp (Expected O(n+m) (rolling hash), O(1) extra)
/*
Idea:
Hash the needle and each m-length window in haystack with a rolling hash. When hashes match, verify by direct comparison (to guard against collisions). Typically very fast, simple to write.

Quick walkthrough:
haystack="aaaaab", needle="aaab", m=4
Initial window "aaaa" vs "aaab": hashes differ.
Slide 1: drop ‘a’, add ‘a’ → still "aaaa"; hashes differ.
Slide 2: now window "aaab" → hash equals; verify substring equals → return index 2.
*/
// class SolutionRabinKarp {
//     public int strStr(String s, String p) {
//         int n = s.length(), m = p.length();
//         if (m == 0) return 0;
//         if (m > n) return -1;

//         long base = 256;               // alphabet size (ASCII)
//         long mod  = 1_000_000_007L;    // large prime
//         long ph = 0, th = 0, pow = 1;  // pattern hash, text hash, base^(m-1)

//         for (int i = 0; i < m; i++) {
//             ph = (ph * base + p.charAt(i)) % mod;
//             th = (th * base + s.charAt(i)) % mod;
//             if (i < m - 1) pow = (pow * base) % mod;
//         }

//         if (ph == th && s.startsWith(p, 0)) return 0;

//         for (int i = m; i < n; i++) {
//             // remove leading char, add trailing char
//             th = (th - s.charAt(i - m) * pow) % mod;
//             if (th < 0) th += mod;
//             th = (th * base + s.charAt(i)) % mod;

//             int start = i - m + 1;
//             if (th == ph && s.startsWith(p, start)) return start;
//         }
//         return -1;
//     }
// }



// Method 4: Z-Algorithm — O(n+m), O(n+m) space
/*
Idea:
Compute Z-array on combined = needle + '#' + haystack (use any separator not in input). At any position i where Z[i] == m, you found a full match starting at i - (m+1) in the original haystack.

Walkthrough (canonical):
text="HERE IS A SIMPLE EXAMPLE", pat="EXAMPLE", m=7
Build shifts (default 7, but ‘E’, ‘X’, ‘A’, ‘M’, ‘P’, ‘L’ get smaller).
Align pattern at pos=0. Compare from end; on mismatch, look at the last char of window and jump by its shift.
You skip big chunks until the window with "EXAMPLE" aligns at pos=17 → then full match.
Horspool is short, fast, and great for large alphabets/random text.
*/

// class SolutionHorspool {
//     public int strStr(String text, String pat) {
//         int n = text.length(), m = pat.length();
//         if (m == 0) return 0;
//         if (m > n) return -1;

//         int ALPHA = 256;
//         int[] shift = new int[ALPHA];
//         Arrays.fill(shift, m);
//         for (int i = 0; i < m - 1; i++) {
//             shift[pat.charAt(i)] = m - 1 - i;
//         }

//         int pos = 0;
//         while (pos <= n - m) {
//             int j = m - 1;
//             while (j >= 0 && pat.charAt(j) == text.charAt(pos + j)) j--;
//             if (j < 0) return pos; // fully matched
//             // shift by the table value for the window's last character
//             pos += shift[text.charAt(pos + m - 1)];
//         }
//         return -1;
//     }
// }



// Method 5: DFA (Deterministic Finite Automaton) — O(n + Σ·m) build, O(n) run
/*
Idea:
Build a state machine for the pattern: state = length of matched prefix so far. For each state and character, precompute the next state. Then stream through haystack in O(n).

Quick intuition:
This is KMP “unrolled” into a table: from each partial match length, where do you go on each next char? Build once, then the scan is just table lookups.
*/
// class SolutionDFA {
//     public int strStr(String text, String pat) {
//         int n = text.length(), m = pat.length();
//         if (m == 0) return 0;
//         if (m > n) return -1;

//         int ALPHA = 256;
//         int[][] dp = new int[m][ALPHA];

//         // Build DFA
//         dp[0][pat.charAt(0)] = 1;
//         int X = 0; // shadow state
//         for (int j = 1; j < m; j++) {
//             for (int c = 0; c < ALPHA; c++) dp[j][c] = dp[X][c];
//             dp[j][pat.charAt(j)] = j + 1;
//             X = dp[X][pat.charAt(j)];
//         }

//         // Run DFA
//         int state = 0;
//         for (int i = 0; i < n; i++) {
//             state = dp[state][text.charAt(i)];
//             if (state == m) return i - m + 1;
//         }
//         return -1;
//     }
// }