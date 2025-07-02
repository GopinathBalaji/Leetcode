// My sliding window approach : O(m*n) time
class Solution {
    public int strStr(String haystack, String needle) {
        
        int hayLen = haystack.length();
        int nedLen = needle.length();

        if(nedLen == 0){
            return 0;
        }

        int ans = -1;
        for(int i=0;i<hayLen;i++){
            if(i + nedLen - 1 <= hayLen - 1){
                if(haystack.substring(i, i + nedLen).equals(needle)){
                    return i;
                }
            }
        } 

        return ans;
    }
}

// Method 2: KMP Algorithm
// class Solution {
//     public int strStr(String haystack, String needle) {
//         int n = haystack.length(), m = needle.length();
//         // 1) Edge case
//         if (m == 0) return 0;
//         if (n < m) return -1;

//         // 2) Build LPS array for ‘needle’
//         int[] lps = new int[m];
//         buildLPS(needle, lps);

//         // 3) Scan ‘haystack’ with two pointers i (text) and j (pattern)
//         int i = 0, j = 0;
//         while (i < n) {
//             if (haystack.charAt(i) == needle.charAt(j)) {
//                 i++; j++;
//                 if (j == m) {
//                     // found match ending at i−1, so start is (i−m)
//                     return i - m;
//                 }
//             } else if (j > 0) {
//                 // mismatch after j matches → fall back in the pattern
//                 j = lps[j - 1];
//             } else {
//                 // j == 0 → advance text pointer
//                 i++;
//             }
//         }
//         return -1;
//     }

//     private void buildLPS(String pat, int[] lps) {
//         int m = pat.length();
//         lps[0] = 0;
//         int len = 0;  // length of the previous longest prefix-suffix
//         int i = 1;    // we fill lps[1..m-1]

//         while (i < m) {
//             if (pat.charAt(i) == pat.charAt(len)) {
//                 len++;
//                 lps[i] = len;
//                 i++;
//             } else if (len > 0) {
//                 // fallback in the pattern without advancing i
//                 len = lps[len - 1];
//             } else {
//                 // no prefix-suffix match here
//                 lps[i] = 0;
//                 i++;
//             }
//         }
//     }
// }
/*
## How it works

### 1) Edge cases

* If `needle` is empty, return `0` by definition.
* If `haystack` is shorter than `needle`, there’s no match.

### 2) Preprocess `needle` → LPS array

* **`lps[k]`** = the length of the longest proper prefix of `needle[0…k]` that’s also a suffix of it.
* You build it in O(m) time:

  1. Initialize `lps[0] = 0`.
  2. Use two indices:

     * `len` tracks the current candidate prefix-suffix length,
     * `i` scans from 1 to `m−1`.
  3. If `pat[i] == pat[len]`, increment `len`, set `lps[i] = len`, and advance `i`.
  4. On mismatch, if `len > 0`, fallback `len = lps[len−1]` (reuse a shorter prefix); else set `lps[i] = 0` and `i++`.

### 3) Scan `haystack` in O(n)

Maintain two pointers, `i` over `haystack` and `j` over `needle`:

* **Match**: if `haystack[i] == needle[j]`, advance both.
* **Full match**: when `j == m`, you matched the entire `needle`; return `i − m`.
* **Mismatch with partial match** (`j > 0`): don’t restart `i`—instead fallback `j = lps[j−1]`.
* **Mismatch at pattern start** (`j == 0`): simply `i++`.

Because `i` never moves backward and `j` only moves forward or back by amounts that sum to O(m), the overall runtime is **O(n + m)**.

---

## Walkthrough on an example

**`haystack = "abxabcabcaby"`, `needle = "abcaby"`**
We expect to find `"abcaby"` starting at index **6**.

1. **Build LPS for `"abcaby"`**

   ```
   i:   0 1 2 3 4 5
   pat: a b c a  b  y
   lps: 0 0 0 1  2  0
   ```

   * At `i=3` (`'a'` vs `'a'`), len→1
   * At `i=4` (`'b'` vs `'b'`), len→2
   * At `i=5` (`'y'` vs `'c'` mismatch): fallback len=lps\[1]=0, set lps\[5]=0

2. **Scan text**

   ```
   i\j  0 1 2 3 4 5 6 7 8 9 10 11   (i over hay)
   hay: a b x a b c a b c a  b  y
   j over pat: a b c a b y
   ```

   * i=0,j=0: match ‘a’→i1,j1
   * i=1,j=1: match ‘b’→i2,j2
   * i=2,j=2: ‘x’≠‘c’, fallback j=lps\[1]=0
   * i=2,j=0: mismatch & j=0 → i3
   * i=3,j=0: match ‘a’→i4,j1
   * i=4,j=1: match ‘b’→i5,j2
   * i=5,j=2: match ‘c’→i6,j3
   * i=6,j=3: match ‘a’→i7,j4
   * i=7,j=4: match ‘b’→i8,j5
   * i=8,j=5: ‘c’≠‘y’, fallback j=lps\[4]=2
   * i=8,j=2: match ‘c’→i9,j3
   * i=9,j=3: match ‘a’→i10,j4
   * i=10,j=4: match ‘b’→i11,j5
   * i=11,j=5: match ‘y’→i12,j6 → j==m, return 12−6 = 6.

   ```
   ```

We find `"abcaby"` starting at index **6**, as desired.

*/