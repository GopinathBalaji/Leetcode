// Method 1: Sliding Window based on need vs. have
/*
# WHAT WAS I DOING WRONG:

There are two big problems with this approach: one is **performance (will TLE)** and one is **correctness-adjacent / fragility** in how you’re checking “window covers `t`”.

---

## 1) `mapS.entrySet().containsAll(mapT.entrySet())` is too slow (likely TLE)

You call this inside a `while (...)` that can run many times per `right`.

### Why it’s expensive

* `mapS.entrySet()` is a view; `containsAll(...)` iterates over **every entry in `mapT`** and for each entry does a `contains` check on `mapS.entrySet()`.
* Each `contains` on an entry set is not guaranteed O(1) in a helpful way for “(key,value) pair exists” lookups; and you’re doing it **repeatedly**.

So worst case you get something like:

* Outer loop: `O(nS)`
* Inner while: can move `left` up to `nS` times total
* Each check: ~`O(|unique(t)|)` (and with overhead)
  ⇒ overall ~`O(nS * |unique(t)|)` checks but *multiplied by constant factors and repeated map view ops*; in practice this often **times out** on LeetCode 76 when `s` is large.

The standard solution avoids recomputing “is valid window?” from scratch. It maintains:

* `needCount` per char
* `haveCount` per char
* a scalar `formed` (# of required characters satisfied)
  so the validity test is **O(1)**.

---

## 2) The “covers t” check is a brittle/awkward way to express the condition

Logically, `containsAll(mapT.entrySet())` means:

> for every (char, requiredCount) in `mapT`, `mapS` contains **the exact same entry** (char mapped to requiredCount)

That *sounds* okay, but it works only because:

* `mapS` is storing counts,
* you keep extra chars too,
* and you’re requiring `mapS.get(c) == requiredCount` (or more?) — wait, here’s the subtlety:

### Subtlety: it requires **exact equality**, not “at least”

Actually, `entrySet.contains(entry)` checks whether there is an entry with the same key and same value.

But for Minimum Window Substring, the window is valid when:

* for every char `c` in `t`: `countS[c] >= countT[c]`

Your check requires `countS[c] == countT[c]` for all `c` in `t`.

However, you’re using `containsAll(mapT.entrySet())`, and `mapS` might have `countS[c] > countT[c]`, which would make `(c, countT[c])` **not** present in `mapS.entrySet()` (because `mapS` would contain `(c, biggerValue)` instead).

So the condition is **wrong** for cases where the window contains more than the needed count of some required character.

### Concrete failing example

* `s = "AAAB"`
* `t = "AAB"`

`mapT = {A=2, B=1}`

Consider window `"AAAB"`:
`mapS = {A=3, B=1}`

This window **is valid** because A(3) ≥ 2 and B(1) ≥ 1.

But your check:

* Does `mapS.entrySet()` contain entry `(A,2)`? **No** (it has `(A,3)`).
  So `containsAll(...)` is false, and your algorithm would fail to recognize a valid window (and could return `""` or a wrong longer/shorter result depending on input).

So this is not just performance — it’s actually incorrect.

✅ This is the biggest logical bug.

---

## Summary of what’s wrong

1. **Incorrect validity condition**:
   `containsAll(mapT.entrySet())` checks equality of counts, but you need `>=` counts.
2. **Inefficient repeated checking**:
   Even if fixed, recomputing the condition via set/map operations inside the while loop is too slow.

# ###########################################

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
        int nS = s.length(), nT = t.length();
        if (nT > nS) return "";

        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) {
            need.put(c, need.getOrDefault(c, 0) + 1);
        }

        Map<Character, Integer> window = new HashMap<>();
        int required = need.size();
        int formed = 0;

        int left = 0;
        int bestLen = Integer.MAX_VALUE;
        int bestL = 0, bestR = 0;

        for (int right = 0; right < nS; right++) {
            char c = s.charAt(right);
            window.put(c, window.getOrDefault(c, 0) + 1);

            if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()) {
                formed++;
            }

            // Try shrinking while window is valid
            while (formed == required) {
                // record answer BEFORE removing left char
                int len = right - left + 1;
                if (len < bestLen) {
                    bestLen = len;
                    bestL = left;
                    bestR = right;
                }

                char cl = s.charAt(left);
                window.put(cl, window.get(cl) - 1);
                if (window.get(cl) == 0) window.remove(cl);

                if (need.containsKey(cl) && window.getOrDefault(cl, 0) < need.get(cl)) {
                    formed--;
                }

                left++;
            }
        }

        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestL, bestR + 1);
    }
}







// Method 2: Same sliding windown approach as above but with arrays
/*
## Core idea

You want the smallest substring `s[l..r]` that contains **all characters of `t` with correct multiplicities**.

Key trick:

* Keep an array `need[128]` where `need[c]` is how many more of character `c` you still need to satisfy `t`.
* Maintain a sliding window `[left..right]` over `s`.
* Use an integer `missing` = total number of characters still missing (counts multiplicity).

### Invariant

* If `missing == 0`, the current window contains all required characters (valid).
* We then shrink from the left to make it minimal.

### Why this works (intuition)

* `need[c]` starts as “required count from `t`”.
* As you expand the window with `right`, you do `need[cr]--`.

  * If `cr` was still needed (`need[cr] > 0` before decrement), you reduced the missing total.
  * If it wasn’t needed, `need[cr]` just becomes negative (meaning “we have extra of `cr` in window”).
* When shrinking from `left`, you do `need[cl]++` (because you’re removing that char from the window).

  * If after increment `need[cl] > 0`, it means the window now lacks one required `cl`, so it becomes invalid again (`missing++`).

Time complexity is **O(|s|)** because each pointer (`left`, `right`) moves forward at most `nS` times.

---

## Thorough example walkthrough

Classic example:

* `s = "ADOBECODEBANC"`
* `t = "ABC"`

### Step 1: Initialize `need[]` from `t`

`need['A']=1`, `need['B']=1`, `need['C']=1` (everything else 0)

`missing = 3` (we need 3 total chars)

`left = 0`, `bestLen = INF`

---

## Expand `right` and update `need/missing`

I’ll focus on important characters; other letters just make `need` go negative and don’t change `missing`.

### right = 0, cr = 'A'

* `need['A']` is 1 > 0 ⇒ this helps ⇒ `missing = 2`
* `need['A']--` ⇒ `need['A']=0`
  Window: `"A"` (not valid)

### right = 1..2: 'D','O'

Not needed. Their `need` becomes -1, `missing` stays 2.

### right = 3, cr = 'B'

* `need['B']=1 > 0` ⇒ `missing = 1`
* `need['B']--` ⇒ `need['B']=0`
  Window so far: `"ADOB"`

### right = 4: 'E'

Not needed.

### right = 5, cr = 'C'

* `need['C']=1 > 0` ⇒ `missing = 0`
* `need['C']--` ⇒ `need['C']=0`
  Now `missing == 0` ⇒ window `[left..right] = [0..5] = "ADOBEC"` is valid.

---

## Shrink from the left while valid

We try to remove unnecessary stuff from the left.

Current valid window: `"ADOBEC"` (length 6)

* Update best: bestLen = 6, bestStart = 0

Now shrink:

* left points to 'A' (cl='A')
* `need['A']++` ⇒ `need['A']=1`
* since `need['A'] > 0`, we *now* miss an 'A' ⇒ `missing = 1`
* move `left` to 1
  Stop shrinking (invalid again).

So far best = `"ADOBEC"`

---

## Continue expanding right

We need another 'A' to make window valid again.

We move `right` forward through `"ODE"` etc. until we hit another 'A'.

### right = 6..9: 'O','D','E','B'

* When we see 'B' at right=9:

  * `need['B']` currently is 0, so it’s not needed anymore
  * `need['B']--` makes it -1 (extra B in window)
  * `missing` stays 1

### right = 10, cr = 'A'

* `need['A']=1 > 0` ⇒ `missing = 0`
* `need['A']--` ⇒ `need['A']=0`
  Now window `[left..right] = [1..10] = "DOBECODEBA"` is valid again.

---

## Shrink again while valid

We try to push `left` rightwards as far as possible without breaking validity.

Window: `"DOBECODEBA"` (length 10)

Shrink steps (key point: we can discard non-required extras):

* left=1 'D': `need['D']++` goes from -1 to 0 (still not >0) ⇒ missing stays 0 ⇒ left++
* left=2 'O': same idea
* left=3 'B': `need['B']++` from -1 to 0 ⇒ still missing 0 (because we still have enough B)
* left=4 'E': same
* left=5 'C': `need['C']++` from 0 to 1 ⇒ now `need['C'] > 0` ⇒ missing becomes 1 ⇒ stop

At the moment just before removing 'C', the window was `[5..10]`? Let’s track carefully:
After incrementing and shifting:

* We stopped when trying to remove the 'C' at index 5, and we *did* remove it logically by making missing=1 and left moved to 6 in code.
  But the **last valid window** was when `left` was still 5:
* valid window = `s[5..10] = "CODEBA"` (length 6)
  That ties bestLen=6, not better than `"ADOBEC"`.

---

## Continue expanding right

We need a 'C' again.

### right = 11 'N' (not needed)

### right = 12 'C'

* `need['C']=1 > 0` ⇒ `missing = 0`
* `need['C']--` ⇒ `need['C']=0`

Window is valid again. Now shrink hard:

Current left is 6 (pointing at 'O') and right is 12 ('C').

Shrink while valid:

* Remove 'O','D','E' (they’re extras)
* Eventually you get window `"BANC"` when left hits index 9:

At left=9 window = `s[9..12] = "BANC"` (length 4)
This is smaller than bestLen=6 ⇒ update best to `"BANC"`.

Try shrinking one more:

* remove 'B' at index 9:

  * `need['B']++` from 0 to 1 ⇒ missing becomes 1 ⇒ invalid, stop.
    So `"BANC"` is the minimal.

Return `"BANC"`.

---

## Why this array method avoids your earlier bug

Your earlier approach tried to check “do we contain all of `t`?” by comparing map entries, which doesn’t capture the **>= counts** requirement.

This method encodes `>=` naturally:

* Having extra characters simply pushes `need[c]` negative.
* A window is valid exactly when `missing == 0`.
*/

// class Solution {
//     public String minWindow(String s, String t) {
//         int nS = s.length(), nT = t.length();
//         if (nT > nS) return "";

//         int[] need = new int[128]; // ASCII
//         for (int i = 0; i < nT; i++) {
//             need[t.charAt(i)]++;
//         }

//         int missing = nT;     // total required chars (with multiplicity) still missing
//         int left = 0;

//         int bestStart = 0;
//         int bestLen = Integer.MAX_VALUE;

//         for (int right = 0; right < nS; right++) {
//             char cr = s.charAt(right);

//             // If need[cr] > 0, this char helps satisfy a needed requirement
//             if (need[cr] > 0) {
//                 missing--;
//             }
//             // Decrement need no matter what: can go negative meaning "extra" in window
//             need[cr]--;

//             // When missing == 0, window [left..right] is valid; shrink from left
//             while (missing == 0) {
//                 int windowLen = right - left + 1;
//                 if (windowLen < bestLen) {
//                     bestLen = windowLen;
//                     bestStart = left;
//                 }

//                 char cl = s.charAt(left);

//                 // We're about to remove cl from the window, so restore need[cl]
//                 need[cl]++;

//                 // If need[cl] becomes > 0 after increment, we now miss one required char
//                 if (need[cl] > 0) {
//                     missing++;
//                 }

//                 left++;
//             }
//         }

//         return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen);
//     }
// }
