// Method 1: Sliding Window (using arrays and not updating maxCount)
/*
Here are the key hints for **LeetCode 424: Longest Repeating Character Replacement** (the classic sliding window).

## Hint 1: Think “window I can fix with ≤ k changes”

For any substring (window) `[l..r]`, the minimum replacements needed to make the whole window one repeating character is:

[
\text{replacements} = \text{windowLen} - \text{(max frequency of any char in window)}
]

Because you keep the most frequent char as-is, and change the rest.

So the window is valid if:
[
(r-l+1) - maxCount <= k
]

## Hint 2: Sliding window + frequency counts

Maintain:

* `int[] freq = new int[26]`
* `l = 0`
* `maxCount = 0` (max frequency of a single char seen in the current window *or* maintained as a running maximum)
* iterate `r` from 0..n-1:

  * add `s[r]` to freq
  * update `maxCount = max(maxCount, freq[s[r]])`
  * while window invalid (`(r-l+1) - maxCount > k`):

    * remove `s[l]` from freq
    * `l++`
  * update answer with window length

## Hint 3: The “maxCount doesn’t decrease” trick

Many accepted solutions never decrease `maxCount` when moving `l`.
That feels weird but still works because:

* `maxCount` being a bit “stale” can only make the window look *more valid* than it truly is,
* but the algorithm’s `ans` will still be correct because `r` only moves forward and the window size only grows when it’s feasible under some max-frequency history.

(If you don’t like that, you can recompute max over 26 each time you shrink—still O(26n) = O(n).)

## Hint 4: Quick sanity check

Example: `s="AABABBA", k=1`
Answer is 4 (`"AABA"` or `"ABBA"`).

## Common pitfalls

* Using `maxCount` as “count of current char” instead of the max over the window.
* Forgetting that replacements = windowLen - maxCount (not maxCount - windowLen).
* Not shrinking the window enough (must be `while`, not `if`).
*/
class Solution {
    public int characterReplacement(String s, int k) {
        int n = s.length();
        int ans = 0;

        int[] freq = new int[26];
        int left = 0;
        int maxCount = 0;

        for(int right=0; right<n; right++){
            int c = s.charAt(right) - 'A';
            freq[c]++;
            maxCount = Math.max(maxCount, freq[c]);

            while((right - left + 1) - maxCount > k){
                freq[s.charAt(left) - 'A']--;
                left++;
            }

            ans = Math.max(ans, right - left + 1);
        }

        return ans;
    }
}



// Method 1.5: Same as above but recomputing maxCount
/*
## Detailed explanation of this “recompute maxCount” version

### Window validity rule

For a window `[left..right]`:

* Let `maxCount` be the frequency of the most common character in the window.
* If you make the whole window one repeating character, you keep those `maxCount` chars and replace the rest.

So replacements needed:
[
\text{needed} = \text{windowLen} - \text{maxCount}
]
Window is valid if `needed <= k`.

### What this code does

* Expand `right` one step at a time, updating `freq`.
* If the window becomes invalid, shrink from `left` until it becomes valid again.
* Each time we test validity, we recompute `maxCount` by scanning `freq[26]`.

  * That’s `O(26)` per check, which is still `O(n)` overall since 26 is constant.

---

## Why recomputing maxCount is **not necessary** (the “stale maxCount” trick)

Most optimal solutions do this instead:

* Maintain `maxCount = max(maxCount, freq[newChar])` when moving `right`.
* **Do not decrease** `maxCount` when moving `left`.

This means `maxCount` might be **too large** for the current window (stale).

### Why that still works

If `maxCount` is stale (too big), then:

* `windowLen - maxCount` becomes **smaller** than the true replacements needed,
* so the window might look valid even if it isn’t.

But crucially:

* `maxCount` only ever increases when you expand `right`.
* The algorithm only grows `ans` when it finds a large window.
* Any time you record a window length `L`, there exists (at some earlier point as `right` moved forward) a window of length `L` where that `maxCount` was *actually achieved* (not stale), meaning a valid window of that size is attainable with ≤ k replacements.

Another way to see it:

* Stale `maxCount` can delay shrinking a bit, but it **never causes you to miss the true maximum**.
* It may temporarily allow a window that isn’t truly valid, but the maximum length you end up returning is still achievable by a truly valid window when the max frequency was real.

### When recomputing is fine

* When alphabet size is small (here 26), recomputing is still effectively O(n) and very readable.
*/

// class Solution {
//     public int characterReplacement(String s, int k) {
//         int n = s.length();
//         int[] freq = new int[26];

//         int left = 0;
//         int ans = 0;

//         for (int right = 0; right < n; right++) {
//             freq[s.charAt(right) - 'A']++;

//             // Shrink until valid; recompute maxCount each time (simple + always correct)
//             while (true) {
//                 int maxCount = 0;
//                 for (int x : freq) maxCount = Math.max(maxCount, x);

//                 int windowLen = right - left + 1;
//                 int needed = windowLen - maxCount; // replacements needed
//                 if (needed <= k) {
//                     ans = Math.max(ans, windowLen);
//                     break;
//                 }

//                 freq[s.charAt(left) - 'A']--;
//                 left++;
//             }
//         }

//         return ans;
//     }
// }







// Method 2: Sliding window using HashMap instead of Array
/*
## How the hash table approach works

### 1) What makes a window valid?

For any window `s[left..right]`:

* Let `maxCount` = frequency of the most common character in this window.
* To make the whole window the same character, you keep those `maxCount` chars and replace the rest.

So replacements needed:

[
\text{needed} = \text{windowLen} - \text{maxCount}
]

Window is valid if:

[
\text{windowLen} - \text{maxCount} \le k
]

### 2) Sliding window strategy

* Expand `right` to grow the window and count chars in a HashMap.
* If the window becomes invalid, move `left` forward (shrinking) until it’s valid again.
* Track the largest valid window length.

### 3) Why HashMap?

Instead of `int[26]`, we store counts dynamically:

* Works for any character set (not just `'A'..'Z'`)
* Same logic, just a different storage structure.

---

## Why `maxCount` is allowed to be “stale” (not recomputed on shrink)

When `left` moves, the true maximum frequency inside the window could decrease, but we **don’t** update `maxCount` downward.

This is still correct because:

* `maxCount` only ever **increases** as `right` moves.
* A stale (too-large) `maxCount` can make a window look “more valid” than it truly is, possibly delaying shrinking.
* But it **never causes the algorithm to miss the true maximum length**, because the maximum window length we record will correspond to some moment where that `maxCount` was genuinely achievable (when those frequent chars were actually in the window as `right` expanded).

If you prefer a simpler-to-reason-about version, you can recompute `maxCount` from the map during shrinking (it’s slower but still fine for small alphabets). But the stale trick is the standard optimal approach.

---

## Thorough walkthrough: `s = "AABABBA", k = 1`

We’ll track:

* window `[left..right]`
* freq map
* `maxCount`
* check `windowLen - maxCount <= k`

Start: `left=0, ans=0, maxCount=0, freq={}`

### right = 0, char = 'A'

freq = {A:1}, maxCount=1
window="A" len=1 → needed = 1-1=0 ✅
ans = 1

### right = 1, char = 'A'

freq = {A:2}, maxCount=2
window="AA" len=2 → needed = 2-2=0 ✅
ans = 2

### right = 2, char = 'B'

freq = {A:2, B:1}, maxCount=2
window="AAB" len=3 → needed = 3-2=1 ✅ (replace B→A)
ans = 3

### right = 3, char = 'A'

freq = {A:3, B:1}, maxCount=3
window="AABA" len=4 → needed = 4-3=1 ✅
ans = 4

### right = 4, char = 'B'

freq = {A:3, B:2}, maxCount=3
window="AABAB" len=5 → needed = 5-3=2 ❌ too many replacements

Shrink from left until valid:

* remove s[left]='A' (left=0):
  freq becomes {A:2, B:2}, left=1
  window="ABAB" len=4 → needed = 4-3=1 ✅ (note: maxCount is stale at 3, true max is 2)

Now valid, update:
ans = max(4,4)=4

### right = 5, char = 'B'

freq = {A:2, B:3}, maxCount stays 3
window="ABABB" len=5 → needed = 5-3=2 ❌

Shrink:

* remove s[left]='A' (left=1):
  freq {A:1, B:3}, left=2
  window="BABB" len=4 → needed = 4-3=1 ✅
  ans = 4

### right = 6, char = 'A'

freq {A:2, B:3}, maxCount=3
window="BABBA" len=5 → needed = 5-3=2 ❌

Shrink:

* remove s[left]='B' (left=2):
  freq {A:2, B:2}, left=3
  window="ABBA" len=4 → needed = 4-3=1 ✅
  ans = 4

Final answer: **4** ✅ (e.g., "AABA" or "ABBA")

---

## Summary

* HashMap keeps counts in the current window.
* Validity test is `windowLen - maxCount <= k`.
* Slide right to expand; slide left to restore validity.
* Track maximum window size.
*/

// class Solution {
//     public int characterReplacement(String s, int k) {
//         Map<Character, Integer> freq = new HashMap<>();

//         int left = 0;
//         int maxCount = 0; // max frequency of any single char seen in the current window (can be stale)
//         int ans = 0;

//         for (int right = 0; right < s.length(); right++) {
//             char cr = s.charAt(right);
//             freq.put(cr, freq.getOrDefault(cr, 0) + 1);

//             // Update the best (maximum) frequency in the window
//             maxCount = Math.max(maxCount, freq.get(cr));

//             // If window needs more than k replacements, shrink from left
//             while ((right - left + 1) - maxCount > k) {
//                 char cl = s.charAt(left);
//                 freq.put(cl, freq.get(cl) - 1);
//                 if (freq.get(cl) == 0) freq.remove(cl);
//                 left++;
//             }

//             ans = Math.max(ans, right - left + 1);
//         }

//         return ans;
//     }
// }
