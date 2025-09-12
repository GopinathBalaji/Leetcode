// Sliding Windown (O(n))
/*
# A clean, accepted solution (O(n) over `L` passes)

**Idea:**

* Precompute `need` = frequency of each word.
* For each `offset` in `0..L-1`, slide a window `[left..right)` in steps of `L`.
* Maintain `seen` counts and a `count` of how many words in the current window don’t exceed `need`.
* If `seen[w] > need[w]`, shrink from the left by one word at a time until it’s valid again.
* When `count == words.length`, record `left` and then move left by one word to search for the next start.

### Why this works

* We only start/end at word boundaries because every valid concatenation is a sequence of `L`-length tokens.
* `count` tracks how many words in the current window are “within quota.” When it equals `m`, the window is exactly `m` tokens that match the multiset.
* Shrinking when `seen[w] > need[w]` guarantees we never carry an overcount.

---

# Thorough walkthrough

**Example:**
`s = "barfoothefoobarman"`
`words = ["foo","bar"]`
Here `L = 3`, `m = 2`. We expect `[0, 9]`.

Break `s` into `L=3`-length chunks by offset.

## Offset 0 (indices 0,3,6,9,12,15)

* `left = 0`, `seen = {}`, `count = 0`.

`right=0`: `w="bar"` ∈ need (`{bar:1, foo:1}`)
`seen[bar]=1` ≤ `need[bar]=1` → `count=1`. `count < m` → continue.

`right=3`: `w="foo"` ∈ need
`seen[foo]=1` ≤ `need[foo]=1` → `count=2 == m` → **add `left=0`**.
Then slide one word: remove `leftWord="bar"` → `seen[bar]=0`, `count=1`, `left=3`.

`right=6`: `w="the"` ∉ need → reset: `seen.clear()`, `count=0`, `left=9`.

`right=9`: `w="foo"` ∈ need
`seen[foo]=1` ≤ 1 → `count=1`.

`right=12`: `w="bar"` ∈ need
`seen[bar]=1` ≤ 1 → `count=2 == m` → **add `left=9`**.
Slide one word: remove `leftWord="foo"` → `seen[foo]=0`, `count=1`, `left=12`.

`right=15`: `w="man"` ∉ need → reset (end anyway).

**Found starts:** `[0, 9]`.

## Offset 1 and 2

They won’t add anything for this case (their tokens never line up to match both words with these offsets), which is expected.
*/

class Solution {
    public List<Integer> findSubstring(String s, String[] words) {
        List<Integer> res = new ArrayList<>();
        int n = s.length();
        int m = words.length;
        if (m == 0) return res;

        int L = words[0].length();
        if (L == 0 || n < m * L) return res;

        // Build target frequencies
        Map<String, Integer> need = new HashMap<>();
        for (String w : words) {
            need.put(w, need.getOrDefault(w, 0) + 1);
        }

        // Run L independent sliding windows by offset
        for (int offset = 0; offset < L; offset++) {
            int left = offset;                  // window start (inclusive)
            int count = 0;                      // #words in window that are within need limits
            Map<String, Integer> seen = new HashMap<>();

            for (int right = offset; right + L <= n; right += L) {
                String w = s.substring(right, right + L);

                if (!need.containsKey(w)) {
                    // Reset window if token not in dictionary
                    seen.clear();
                    count = 0;
                    left = right + L;
                    continue;
                }

                // Consume w
                seen.put(w, seen.getOrDefault(w, 0) + 1);
                if (seen.get(w) <= need.get(w)) {
                    count++;
                } else {
                    // Too many of w: shrink from left until valid
                    while (seen.get(w) > need.get(w)) {
                        String leftWord = s.substring(left, left + L);
                        seen.put(leftWord, seen.get(leftWord) - 1);
                        if (seen.get(leftWord) < need.get(leftWord)) {
                            count--;
                        }
                        left += L;
                    }
                }

                // If we've matched m words, record start and slide by one word
                if (count == m) {
                    res.add(left);
                    String leftWord = s.substring(left, left + L);
                    seen.put(leftWord, seen.get(leftWord) - 1);
                    count--;
                    left += L;
                }
            }
        }
        return res;
    }
}