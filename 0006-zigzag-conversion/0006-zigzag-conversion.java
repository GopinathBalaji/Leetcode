// Method 1: First find cycle length, the top and bottom rows will follow this.
// The middle rows to vertical, and vertical to middle rows have a different jump
/*
# Key idea (in plain terms)

* A full “V” in the zigzag repeats every **cycleLen = 2*(numRows-1)*\* characters.
* **Top row (0)** and **bottom row (numRows-1)** take characters spaced exactly `cycleLen` apart.
* **Middle rows (i)** take **two** characters per cycle: one from the vertical stroke and one from the diagonal stroke.

  * The gaps between picks in a middle row alternate:

    * `gapA = cycleLen - 2*i`
    * `gapB = 2*i`
* Special cases:

  * If `numRows == 1` or `numRows >= s.length()`, the string doesn’t change.


**Why this is safe:**

* For middle rows `1..numRows-2`, both `gapA` and `gapB` are positive.
* For the top and bottom rows, we never use `gapA/gapB`; we only step by `cycle`.
* We never access out of bounds; every `j` is checked against `n`.

**Complexity:**

* Time: O(n) — each character is appended exactly once.
* Space: O(n) for the output builder.

---

# Thorough example walkthrough

**Input:**
`s = "PAYPALISHIRING"`, `numRows = 4`
Indices and chars:

```
 idx:  0 1 2 3 4 5 6 7 8 9 10 11 12 13
 char: P A Y P A L I S H I  R  I  N  G
```

**cycleLen = 2*(4-1) = 6*\*

We’ll build the answer row by row:

### Row 0 (top row)

* Step by `+6`: indices `0, 6, 12`
* Chars: `P (0)`, `I (6)`, `N (12)`
  → Row 0 contributes: **"PIN"**

### Row 1 (middle row, row=1)

* `gapA = 6 - 2*1 = 4`, `gapB = 2*1 = 2`, alternate 4,2,4,2,...
* Start at `j = 1`:

  * append `s[1] = 'A'`, j += 4 → 5
  * append `s[5] = 'L'`, j += 2 → 7
  * append `s[7] = 'S'`, j += 4 → 11
  * append `s[11] = 'I'`, j += 2 → 13
  * append `s[13] = 'G'`, j += 4 → 17 (stop)
    → Row 1 contributes: **"ALSIG"**

### Row 2 (middle row, row=2)

* `gapA = 6 - 4 = 2`, `gapB = 2*2 = 4`, alternate 2,4,2,4,...
* Start at `j = 2`:

  * append `s[2] = 'Y'`, j += 2 → 4
  * append `s[4] = 'A'`, j += 4 → 8
  * append `s[8] = 'H'`, j += 2 → 10
  * append `s[10] = 'R'`, j += 4 → 14 (stop)
    → Row 2 contributes: **"YAHR"**

### Row 3 (bottom row)

* Step by `+6`: indices `3, 9`
* Chars: `P (3)`, `I (9)`
  → Row 3 contributes: **"PI"**

**Concatenate rows in order:**
Row0 + Row1 + Row2 + Row3 = `"PIN"` + `"ALSIG"` + `"YAHR"` + `"PI"`
**Result:** `"PINALSIGYAHRPI"` (which matches the expected LeetCode output).

---

## Quick edge cases to sanity-check

* `numRows = 1` → return original string (`"ABCD" → "ABCD"`).
* `numRows >= s.length()` → return original string.
* `s = "AB"`, `numRows = 2`:

  * cycle = 2
  * Row0: idx 0 → "A"
  * Row1: idx 1 → "B"
  * Result "AB" (same as input, as expected).
*/

/*
How to Derive the Formulas instead of memorizing them:

# 1) Cycle length (why `2*(numRows-1)`)

Draw one full “V” of the zigzag:

* You go **down** from row `0` to row `numRows-1`: that’s `numRows-1` steps.
* Then you go **up** from row `numRows-1` back to row `0`: another `numRows-1` steps.
* Total characters covered before the pattern repeats:
  **`cycleLen = (numRows-1) + (numRows-1) = 2*(numRows-1)`**.

Quick sanity:

* If `numRows = 1`, there’s no zig or zag → cycle is effectively the whole string (treat as a guard case).
* If `numRows = 4`, cycle = `2*(4-1)=6`. You can literally count 6 steps to return to the same row *and direction*.

# 2) Where do characters for a given row fall inside a cycle?

Think “position mod cycle”. For any index `t` in the string, let `x = t % cycleLen`.

* The row occupied by `t` is `row(t) = min(x, cycleLen - x)` (reflecting the “down then up”).
* For a fixed row `r`, the indices within each cycle that land on row `r` satisfy:

  * `x = r`  (the **vertical** stroke),
  * `x = cycleLen - r` (the **diagonal** stroke),
    except for the **top** (`r=0`) and **bottom** (`r=numRows-1`) rows, where these coincide (so they only have one pick per cycle).

This gives you the insight: **middle rows appear twice per cycle**; top/bottom only once.

# 3) Jump lengths for middle rows (why `gapA = cycleLen - 2*r`, `gapB = 2*r`)

Within a single cycle, a middle row `r` hits two positions: `x=r` and `x=cycleLen - r`.

* From **vertical → diagonal** in the same cycle:
  distance = `(cycleLen - r) - r = cycleLen - 2*r` → **`gapA`**.
* From **diagonal → next cycle’s vertical**:
  next vertical is at `x=r` in the *next* cycle, i.e., index `… + cycleLen + r`.
  distance = `(cycleLen + r) - (cycleLen - r) = 2*r` → **`gapB`**.

So the row’s jumps **alternate** between `gapA` and `gapB`. Notice `gapA + gapB = cycleLen` (nice consistency check).

Edge rows:

* `r = 0` → `gapA = cycleLen`, `gapB = 0` → only use `cycleLen` (one pick per cycle).
* `r = numRows-1` → `gapA = 0`, `gapB = cycleLen` → again, only `cycleLen`.

# 4) A tiny numeric check (so you can do it live)

Say `numRows = 4` → `cycleLen = 6`.

* Row `r=1`: `gapA = 6 - 2 = 4`, `gapB = 2`. Alternates **+4, +2, +4, +2…**
* Row `r=2`: `gapA = 6 - 4 = 2`, `gapB = 4`. Alternates **+2, +4, +2, +4…**
* Row `r=0` and `r=3` (top/bottom): step **+6** each time.

# 5) Interview-friendly recipe (what to *say* out loud)

1. “One zigzag cycle goes down `R-1` and up `R-1`, so `L = 2*(R-1)`.”
2. “Per row `r`, picks happen at residues `r` and `L - r` modulo `L` (top/bottom only once).”
3. “The two jumps for middle rows are the distances between those residues:
   `gapA = L - 2r`, `gapB = 2r`, and they alternate. Edge rows only use `L`.”

If you blank under pressure, just draw 4 rows, annotate indices 0.., mark where a row is hit inside a 6-length cycle, and read off the gaps—you’ll re-derive the formulas in \~20 seconds.
*/
class Solution {
    public String convert(String s, int numRows) {
        int n = s.length();
        // Edge cases where the zigzag is identical to the original string
        if (numRows == 1 || numRows >= n) return s;

        StringBuilder ans = new StringBuilder(n);
        int cycle = 2 * (numRows - 1);

        for (int row = 0; row < numRows; row++) {
            // First and last rows: simple step by full cycle
            if (row == 0 || row == numRows - 1) {
                for (int j = row; j < n; j += cycle) {
                    ans.append(s.charAt(j));
                }
            } else {
                // Middle rows: alternate between two gap sizes within each cycle
                int gapA = cycle - 2 * row; // jump from vertical to diagonal char
                int gapB = 2 * row;         // jump from diagonal back to next vertical
                boolean useA = true;
                int j = row;
                while (j < n) {
                    ans.append(s.charAt(j));
                    j += useA ? gapA : gapB;
                    useA = !useA;
                }
            }
        }

        return ans.toString();
    }
}





// Method 2: Simulating the patter with pointer that goes up and down 
// (Easier but inefficient approach: (O(n) time, O(n) extra space)
/*
Simulate the zigzag with a pointer that goes “down” then “up” across rows.
Keep a StringBuilder per row; append characters as you walk; then join them.
*/

// class Solution {
//     public String convert(String s, int numRows) {
//         int n = s.length();
//         if (numRows == 1 || numRows >= n) return s;

//         List<StringBuilder> rows = new ArrayList<>(numRows);
//         for (int i = 0; i < numRows; i++) rows.add(new StringBuilder());

//         int r = 0, dir = 1; // +1 = down, -1 = up
//         for (int k = 0; k < n; k++) {
//             rows.get(r).append(s.charAt(k));
//             if (r == 0) dir = 1;
//             else if (r == numRows - 1) dir = -1;
//             r += dir;
//         }

//         StringBuilder ans = new StringBuilder(n);
//         for (StringBuilder sb : rows) ans.append(sb);
//         return ans.toString();
//     }
// }