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
                set.remove(s.charAt(j++));   // shrink until c is unique
            }
            set.add(c);                       // include c
            ans = Math.max(ans, i - j + 1);   // update best
        }
        return ans;
    }
}




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
