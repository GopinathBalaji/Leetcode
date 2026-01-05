// Method 1: Sliding window approach using Two-Pointer and HashMap
/*
# WHAT WAS I DOING WRONG:

The core bug is that you’re sliding the window based on **number of distinct characters** (`map.size()`), but the problem requires the window length to be exactly `s1.length()` and the **full frequency counts** to match.

### What’s wrong (with concrete counterexamples)

#### 1) You shrink when `mapS2.size() > mapS1.size()` — but you should shrink when window length > `n1`

Distinct-count is not the same as window size.

Example:

* `s1 = "ab"` → `mapS1.size() = 2`
* `s2 = "aa"`
  At `right=1`, `mapS2 = {a:2}` → `mapS2.size() = 1` (not > 2), so you **don’t shrink**.
  You then compare maps only when sizes match (never), return false (correct here) — but the real issue shows up when there *is* a valid permutation.

Example where it **fails**:

* `s1 = "ab"` (needs window length 2)
* `s2 = "eidbaooo"` has `"ba"` → should return **true**

Walk your code quickly:

* When you reach `"eidb"` you have many distinct chars; you shrink until distinct-count is 2, but that window can still be length 3,4,… and can skip over the correct alignment. Your shrinking condition is simply not tied to the required window length.

You need: **if (right - left + 1 > n1) shrink**.

---

#### 2) Even if `mapS1.size() == mapS2.size()`, maps can match only if window length matches too

Your code checks equality when the **number of distinct keys** matches, but the window might be longer than `n1` and still have the same distinct keys but different counts.

Example:

* `s1 = "ab"` → counts `{a:1,b:1}`
* Suppose current window in `s2` is `"aab"` → counts `{a:2,b:1}`
* Distinct sizes both 2, but it’s not a permutation (window length 3). Your approach allows this situation because you never enforce window length.

---

#### 3) It can miss valid permutations because shrinking-by-distinct doesn’t maintain a consistent window invariant

Sliding window solutions work because you maintain a strict invariant like:

* window length == `n1` (fixed size), or
* counts are “within limits” and shrink when exceeded.

Your invariant is “distinct chars <= distinct chars of s1”, which is unrelated to the actual requirement.

---

### Minimal fix conceptually

Use a fixed-size window of length `n1`:

* Add `s2[right]`
* If window length > `n1`, remove `s2[left]` and increment `left`
* When window length == `n1`, compare counts

With HashMaps this is O(26) comparisons per step (or more for general chars), but with lowercase letters you should really use `int[26]` for speed.
*/

class Solution {
    public boolean checkInclusion(String s1, String s2) {
        int n1 = s1.length();
        int n2 = s2.length();
        Map<Character, Integer> mapS1 = new HashMap<>();

        for(char c: s1.toCharArray()){
            mapS1.put(c, mapS1.getOrDefault(c, 0) + 1);
        }

        Map<Character, Integer> mapS2 = new HashMap<>();
        int left = 0;

        for(int right=0; right<n2; right++){
            mapS2.put(s2.charAt(right), mapS2.getOrDefault(s2.charAt(right), 0) + 1);

            if(right - left + 1 > n1){
                int val = mapS2.get(s2.charAt(left));
                int newVal = val - 1;

                if(newVal == 0){
                    mapS2.remove(s2.charAt(left));
                }else{
                    mapS2.put(s2.charAt(left), newVal);
                }

                left++;
            }

            if(right - left + 1 == n1 && mapS1.equals(mapS2)){
                return true;
            }
        }

        return false;
    }
}







// Method 2: Same Slding window approach using Two-Pointer but with arrays instead of HashMap
/*
## Key idea

A permutation of `s1` exists in `s2` **iff** there is some substring of `s2` of length `|s1|` whose **character frequencies match** `s1`.

So we:

1. Build frequency arrays for `s1` and the **first window** of `s2` of size `n1`.
2. Track how many of the 26 letters currently match in counts (`matches`).
3. Slide the window one char at a time:

   * Add the new right character
   * Remove the old left character
   * Update `matches` in O(1)
4. If at any point `matches == 26`, all counts match ⇒ permutation found.

Why `matches` works: Instead of comparing 26 counts every time, we maintain the number of positions where the counts are equal.

### Complexity

* Time: **O(n2 + 26)** → essentially **O(n2)**
* Space: **O(26)** → constant

---

## Why the `matches` updates are correct

For a particular letter index `i`, we only care whether `cnt2[i] == cnt1[i]`.

When we do `cnt2[i]++`:

* If it was equal before increment, it becomes unequal → `matches--`
* After increment, if it becomes equal (possible when it was one less than cnt1), then `matches++`

Same idea for `cnt2[i]--`.

This lets us keep `matches` accurate without re-checking all 26 letters every time.

---

## Thorough example walkthrough

Use the classic test:

* `s1 = "ab"`
* `s2 = "eidbaooo"`
* `n1 = 2`

We’re looking for any length-2 substring of `s2` that is a permutation of `"ab"` (i.e., `"ab"` or `"ba"`).

### Step 1: Build counts for s1

`"ab"` →

* `cnt1[a]=1`
* `cnt1[b]=1`
  Everything else 0.

### Step 2: First window in s2 of length 2

First window is `s2[0..1] = "ei"` →

* `cnt2[e]=1`
* `cnt2[i]=1`

So currently `cnt1` and `cnt2` differ in several letters.

### Step 3: Initialize `matches`

We compare all 26 letters:

* For most letters both are 0 → those positions match
* But:

  * `a`: cnt1=1, cnt2=0 → mismatch
  * `b`: cnt1=1, cnt2=0 → mismatch
  * `e`: cnt1=0, cnt2=1 → mismatch
  * `i`: cnt1=0, cnt2=1 → mismatch

So `matches = 26 - 4 = 22`.

Not 26 → not a permutation.

---

## Slide the window

We slide by 1 each time. The window size stays fixed at 2.

I’ll show each step: window, incoming char, outgoing char, and the key counts.

### Window 0: `"ei"` (already done)

* matches = 22

---

### Move to Window 1: `"id"`

* right moves to index 2 (incoming = `'d'`)
* left moves off index 0 (outgoing = `'e'`)

**Incoming `'d'`:**

* Before: cnt2[d]=0, cnt1[d]=0 → they match → `matches--` (temporarily breaks)
* Increment: cnt2[d]=1
* Now: cnt2[d]=1, cnt1[d]=0 → mismatch → no `matches++`

So net effect for `'d'`: `matches` decreased by 1.

**Outgoing `'e'`:**

* Before removal: cnt2[e]=1, cnt1[e]=0 → mismatch → no `matches--`
* Decrement: cnt2[e]=0
* After: cnt2[e]=0, cnt1[e]=0 → match → `matches++`

So net effect for `'e'`: `matches` increased by 1.

Overall: `matches` stays **22**.

---

### Window 2: `"db"`

Incoming = `'b'` (index 3), Outgoing = `'i'` (index 1)

**Incoming `'b'`:**

* Before: cnt2[b]=0, cnt1[b]=1 → mismatch → no `matches--`
* Increment: cnt2[b]=1
* After: cnt2[b]=1, cnt1[b]=1 → match → `matches++`

So `matches` becomes **23**.

**Outgoing `'i'`:**

* Before: cnt2[i]=1, cnt1[i]=0 → mismatch → no `matches--`
* Decrement: cnt2[i]=0
* After: cnt2[i]=0, cnt1[i]=0 → match → `matches++`

So `matches` becomes **24**.

Still not 26.

---

### Window 3: `"ba"`  ✅ this is a permutation

Incoming = `'a'` (index 4), Outgoing = `'d'` (index 2)

**Incoming `'a'`:**

* Before: cnt2[a]=0, cnt1[a]=1 → mismatch → no `matches--`
* Increment: cnt2[a]=1
* After: cnt2[a]=1, cnt1[a]=1 → match → `matches++`

`matches` becomes **25**.

**Outgoing `'d'`:**

* Before removal: cnt2[d]=1, cnt1[d]=0 → mismatch → no `matches--`
* Decrement: cnt2[d]=0
* After: cnt2[d]=0, cnt1[d]=0 → match → `matches++`

`matches` becomes **26**.

Since `matches == 26`, that means **all 26 letters have matching counts** between `s1` and the current window of `s2`.

Current window is `"ba"` → permutation found → return **true**.

---

## Why your original “map size” approach fails (in one sentence)

Because it uses the wrong invariant: it shrinks based on **distinct characters**, but the problem requires checking **all windows of exact length `|s1|`**.
*/

// class Solution {
//     public boolean checkInclusion(String s1, String s2) {
//         int n1 = s1.length(), n2 = s2.length();
//         if (n1 > n2) return false;

//         int[] cnt1 = new int[26];
//         int[] cnt2 = new int[26];

//         // Count frequencies in s1 and in the first window of s2 (length n1)
//         for (int i = 0; i < n1; i++) {
//             cnt1[s1.charAt(i) - 'a']++;
//             cnt2[s2.charAt(i) - 'a']++;
//         }

//         // matches = how many letters have equal counts in cnt1 and cnt2
//         int matches = 0;
//         for (int i = 0; i < 26; i++) {
//             if (cnt1[i] == cnt2[i]) matches++;
//         }

//         // If all 26 letters match, the first window is already a permutation
//         if (matches == 26) return true;

//         // Slide the window across s2
//         int left = 0;
//         for (int right = n1; right < n2; right++) {
//             int inIdx = s2.charAt(right) - 'a';   // incoming char index
//             int outIdx = s2.charAt(left) - 'a';   // outgoing char index
//             left++;

//             // 1) Add incoming char: cnt2[inIdx]++
//             // Update matches based on whether this index changes from match->mismatch or mismatch->match
//             if (cnt2[inIdx] == cnt1[inIdx]) matches--;  // it WAS matching, will become non-matching after ++
//             cnt2[inIdx]++;
//             if (cnt2[inIdx] == cnt1[inIdx]) matches++;  // it BECOMES matching after ++

//             // 2) Remove outgoing char: cnt2[outIdx]--
//             if (cnt2[outIdx] == cnt1[outIdx]) matches--; // it WAS matching, will become non-matching after --
//             cnt2[outIdx]--;
//             if (cnt2[outIdx] == cnt1[outIdx]) matches++; // it BECOMES matching after --

//             // If all letters match, window is a permutation
//             if (matches == 26) return true;
//         }

//         return false;
//     }
// }

