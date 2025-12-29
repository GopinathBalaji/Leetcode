// Method 1: Set based sliding window
/*
Maintain a set of chars in the current window.
When you see a duplicate, pop from the left until it’s gone.
Update the answer with i - j + 1.
*/
class Solution {
    public int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        int ans = 0, j = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            while (set.contains(c)) {
                set.remove(s.charAt(j));   // shrink until c is unique
                j++;
            }
            set.add(c);                       // include c
            ans = Math.max(ans, i - j + 1);   // update best
        }
        return ans;
    }
}





// Method 1.5: My Set-based solution (O(n^2) approach - NOT OPTIMAL)
/*
Your code is **correct** (it returns the right length), but it’s **not the intended efficient approach**.

### What’s “wrong” (practically)

* **Time complexity is O(n²)** in the worst case.

  * You restart a new `HashSet` at every `i` and scan forward until you hit a repeat.
  * For a string like `"abcdefg..."` (all unique), you’ll scan almost the whole suffix each time:

    * work ≈ (n + (n-1) + (n-2) + …) = O(n²)

This will usually still pass on small inputs, but it can be slow for long strings.

### Minor notes

* `maxLen = Math.max(maxLen, set.size())` is fine because the loop stops exactly when a repeat is found or end reached, so `set.size()` is the length of the current maximal unique run starting at `i`.
* Space is fine: O(min(n, alphabet)) per iteration, but recreated each time.

### What the optimal approach is

Use a **sliding window** with:

* a `HashSet` and two pointers, or
* better: a `Map<Character, Integer>` (last seen index) to jump the left pointer

That gives **O(n)** time.
*/
// class Solution {
//     public int lengthOfLongestSubstring(String s) {
//         int n = s.length();

//         int maxLen = 0;

//         for(int i=0; i<n; i++){
//             int start = i;
//             Set<Character> set = new HashSet<>();

//             while(start < n && !set.contains(s.charAt(start))){
//                 set.add(s.charAt(start));
//                 start++;
//             }

//             maxLen = Math.max(maxLen, set.size());
//         }

//         return maxLen;        
//     }
// }






// Method 2: Map to last index
/*
Map char → last seen index.
If the char was seen inside the current window (last >= j), jump j to last+1.
Update best with i - j + 1.

## Quick walkthrough (“abba”)

* Start `j=0`.
* i=0 ‘a’: window “a” → ans=1.
* i=1 ‘b’: window “ab” → ans=2.
* i=2 ‘b’ (duplicate):
  last(‘b’)=1 ≥ j → set `j=1+1=2`; now window “b” (indices 2..2) → ans=2.
* i=3 ‘a’ (earlier ‘a’ was at 0, which is < j=2): no jump; window “ba” → ans stays 2.
  Answer = **2** (“ab” or “ba”).
*/

// class Solution {
//     public int lengthOfLongestSubstring(String s) {
//         Map<Character, Integer> last = new HashMap<>();
//         int ans = 0, j = 0;
//         for (int i = 0; i < s.length(); i++) {
//             char c = s.charAt(i);
//             if (last.containsKey(c) && last.get(c) >= j) {
//                 j = last.get(c) + 1;          // move left past previous c
//             }
//             last.put(c, i);                    // update last index
//             ans = Math.max(ans, i - j + 1);
//         }
//         return ans;
//     }
// }
