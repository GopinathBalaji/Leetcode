// Sliding Window based on need vs. have
/*
Correct sliding-window pattern:
Build need: counts of each char in t.
Maintain window counts for the current window [l..r] in s.
formed = how many distinct chars currently meet their required count.
Expand r; when formed == required, shrink l as much as possible while staying valid, updating 
the best window.

## Quick walkthrough (classic case)

`s = "ADOBECODEBANC"`, `t = "ABC"`

* `need = {A:1,B:1,C:1}`, `required = 3`.
* Expand `r`:

  * At `r=5` (…`ADOBEC`): window meets all A/B/C → shrink from `l=0`:

    * Best becomes `"ADOBEC"` (len 6), then removing `A` breaks validity.
  * Continue expanding; next time all A/B/C are present is around `r=12`:

    * Shrink from `l=9` to get `"BANC"` (len 4) — this is minimal.
* Return `"BANC"`.

##########

* `s = "ADOBECODEBANC"`
* `t = "ABC"`

We’ll track only the **interesting** chars (`A,B,C`) in the window; other chars still live in `window`, but don’t affect `formed`.

---

## Setup

* Build `need` from `t`: `need = {A:1, B:1, C:1}`, so `required = 3`.
* `window = {}` initially, `formed = 0`.
* Best window trackers: `bestLen = ∞`, `bestL = 0`, `bestR = 0`.
* Pointers: `l = 0`, we iterate `r = 0..n-1`.

---

## Step-by-step scan

Legend: “win(A,B,C)” shows the counts of A/B/C inside `window` after handling `r`.
We only change `formed` when a count **reaches** exactly `need[c]`.
We **shrink** from the left while `formed == required`.

| r  | s\[r] | win(A,B,C) after adding s\[r] | formed | l (before shrink) | Shrink actions (while formed==required)                                                                                                                                                                                                                                                                                   | l (after) | Best window update           |
| -- | ----- | ----------------------------- | ------ | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------- | ---------------------------- |
| 0  | A     | (1,0,0)                       | 1      | 0                 | —                                                                                                                                                                                                                                                                                                                         | 0         | —                            |
| 1  | D     | (1,0,0)                       | 1      | 0                 | —                                                                                                                                                                                                                                                                                                                         | 0         | —                            |
| 2  | O     | (1,0,0)                       | 1      | 0                 | —                                                                                                                                                                                                                                                                                                                         | 0         | —                            |
| 3  | B     | (1,1,0)                       | 2      | 0                 | —                                                                                                                                                                                                                                                                                                                         | 0         | —                            |
| 4  | E     | (1,1,0)                       | 2      | 0                 | —                                                                                                                                                                                                                                                                                                                         | 0         | —                            |
| 5  | C     | (1,1,1)                       | 3      | 0                 | Window valid ⇒ try shrink: <br>• len=6 → best=(0,5) `"ADOBEC"` <br>• pop `A` (l=0): win→(0,1,1) → drops below need(A), formed=2, stop                                                                                                                                                                                     | 1         | bestLen=6, bestL=0, bestR=5  |
| 6  | O     | (0,1,1)                       | 2      | 1                 | —                                                                                                                                                                                                                                                                                                                         | 1         | —                            |
| 7  | D     | (0,1,1)                       | 2      | 1                 | —                                                                                                                                                                                                                                                                                                                         | 1         | —                            |
| 8  | E     | (0,1,1)                       | 2      | 1                 | —                                                                                                                                                                                                                                                                                                                         | 1         | —                            |
| 9  | B     | (0,2,1)                       | 2      | 1                 | (B over-need; formed unchanged)                                                                                                                                                                                                                                                                                           | 1         | —                            |
| 10 | A     | (1,2,1)                       | 3      | 1                 | Valid ⇒ shrink repeatedly: <br>• len=10 → (no better than 6) <br>• pop `D` (l=1): win (1,2,1) formed=3 <br>• pop `O` (l=2): formed=3 <br>• pop `B` (l=3): win→(1,1,1) formed=3 <br>• pop `E` (l=4): formed=3 <br>• pop `C` (l=5): win→(1,1,0) drops below need(C) → formed=2, stop                                        | 6         | best still (0,5)             |
| 11 | N     | (1,1,0)                       | 2      | 6                 | —                                                                                                                                                                                                                                                                                                                         | 6         | —                            |
| 12 | C     | (1,1,1)                       | 3      | 6                 | Valid ⇒ shrink: <br>• len=7 → (no better) <br>• pop `O` (l=6): formed=3 <br>• len=6 → (ties) <br>• pop `D` (l=7): formed=3 <br>• len=5 → **better** → best=(8,12) `"EBANC"` <br>• pop `E` (l=8): formed=3 <br>• len=4 → **better** → best=(9,12) `"BANC"` <br>• pop `B` (l=9): win→(1,0,1) below need(B) → formed=2, stop | 10        | bestLen=4, bestL=9, bestR=12 |

Loop ends. Return `s.substring(9, 12+1) = "BANC"`.

---

## Why this finds the minimum

* We **expand** `r` until the window becomes valid (`formed == required`), then **shrink** `l` as far as possible while staying valid. The first moment it becomes invalid, the **previous** window `[l..r]` was the **shortest** ending at `r`.
* We keep the best over all `r`. Because each `l` and `r` moves forward at most `n` times, the scan is linear.
* Crucially, we only change `formed` when a count crosses the **exact threshold**:

  * Increment `formed` when `window[c]` becomes `need[c]`.
  * Decrement `formed` when `window[c]` drops **below** `need[c]` during shrinking.

---

## Tiny second example (repeats in `t`)

`s = "AAABBC"`, `t = "AABC"`
`need = {A:2, B:1, C:1}`, `required = 3`.

* Expand to cover two A’s, one B, one C:

  * At `r=5` you first reach `formed=3` with window `"AABBC"` (indices 1..5).
  * Try shrinking: popping the left `A` would make `A` count drop below 2 → `formed` falls → stop.
  * So `"AABBC"` (length 5) is minimal here.

This shows the count logic handles duplicates correctly.

---

## Complexity (from this code)

* Build `need`: O(|t|).
* Two-pointer scan of `s`: each char is added once (by `r`) and removed once (by `l`) → O(|s|).
* Total **O(|s| + |t|)** time, O(Σ) space (Σ distinct chars used).

If you want, drop in your own `s`/`t` and I’ll trace the table exactly like above.




### Common pitfalls to avoid

* Incrementing `formed` when a count goes **beyond** `need` (only increment when it becomes **equal**).
* Forgetting to **decrement** `formed` when shrinking below `need`.
* Not updating the best window **inside the shrink loop**.
*/
class Solution {
    public String minWindow(String s, String t) {
        if (t == null || t.length() == 0) return "";
        int n = s.length(), m = t.length();
        if (m > n) return "";

        // 1) need counts + required distinct chars
        Map<Character, Integer> need = new HashMap<>();
        for (int k = 0; k < m; k++) {
            char c = t.charAt(k);
            need.put(c, need.getOrDefault(c, 0) + 1);
        }
        int required = need.size();

        // 2) sliding window
        Map<Character, Integer> window = new HashMap<>();
        int formed = 0;
        int bestLen = Integer.MAX_VALUE, bestL = 0, bestR = 0;

        int l = 0;
        for (int r = 0; r < n; r++) {
            char c = s.charAt(r);
            window.put(c, window.getOrDefault(c, 0) + 1);

            if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()) {
                formed++;
            }

            // 3) shrink while valid
            while (formed == required) {
                if (r - l + 1 < bestLen) {
                    bestLen = r - l + 1;
                    bestL = l;
                    bestR = r;
                }
                char left = s.charAt(l);
                window.put(left, window.get(left) - 1);
                if (need.containsKey(left) && window.get(left) < need.get(left)) {
                    formed--;
                }
                l++;
            }
        }

        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestL, bestR + 1);
    }
}
