// My attemp (same time complexity as the GPT code)
class Solution {
    public String reverseWords(String s) {
        StringBuilder ans = new StringBuilder();

        for(int i=s.length()-1;i>=0;i--){
            if(s.charAt(i) == ' '){
                continue;
            }else{
                StringBuilder sb = new StringBuilder();
                while(i >= 0 && s.charAt(i) != ' '){
                    sb.append(s.charAt(i));
                    i--;
                }
                sb.reverse();
                sb.append(' ');
                ans.append(sb);
            }
        }

        if (ans.length() > 0) {
            ans.deleteCharAt(ans.length() - 1);
        }
        return ans.toString();
    }
}

// More Efficient GPT code
/*
Here’s a single-pass (plus one pass to rebuild) approach that runs in O(n) time, uses O(n) extra space for the list of words (plus the output), and avoids any regex:

## Detailed Explanation

1. **Trim edges in O(n)**
   We use two pointers, `left` and `right`, to skip over any leading or trailing spaces. That ensures we only deal with meaningful characters in the next step.

2. **Extract words in one sweep**

   * We walk from `left` to `right` once.
   * If we see a non‐space, we append it to our `word` buffer.
   * On encountering a space *and* if `word` is non‐empty, we’ve just finished a word—so we `toString()` it into our `words` list and reset the buffer.
   * This handles runs of spaces automatically, and still works if there’s no space between words.
   * After the loop, if `word` has leftover characters, we add that final word.

3. **Reverse the word list in O(n)**

   * We iterate from the end of `words` back to the front, appending each to our answer builder.
   * We insert a single space between words (but *not* after the last one).

4. **Complexity**

   * **Time:**

     * Trimming: O(n)
     * Extraction pass: O(n)
     * Rebuilding reversed: O(n)
       → overall O(n).
   * **Space:**

     * O(k) for the `words` list (where k ≤ n/2 in the worst case of alternating char/space), plus O(n) for the output.

This is more efficient than nested loops or reversing each word individually because you only scan the input string once to collect words and once to output them in reverse—no per-word `reverse()` calls or regex overhead.

*/
// class Solution {
//     public String reverseWords(String s) {
//         int left = 0, right = s.length() - 1;
//         // 1) trim leading spaces
//         while (left <= right && s.charAt(left) == ' ') {
//             left++;
//         }
//         // 2) trim trailing spaces
//         while (left <= right && s.charAt(right) == ' ') {
//             right--;
//         }

//         List<String> words = new ArrayList<>();
//         StringBuilder word = new StringBuilder();
//         // 3) extract words into a list
//         while (left <= right) {
//             char c = s.charAt(left);
//             if (c != ' ') {
//                 word.append(c);
//             } else if (word.length() > 0) {
//                 // end of a word
//                 words.add(word.toString());
//                 word.setLength(0);
//             }
//             left++;
//         }
//         // add the last word if any
//         if (word.length() > 0) {
//             words.add(word.toString());
//         }

//         // 4) build the answer by reversing the word order
//         StringBuilder ans = new StringBuilder();
//         for (int i = words.size() - 1; i >= 0; i--) {
//             ans.append(words.get(i));
//             if (i > 0) {
//                 ans.append(' ');
//             }
//         }
//         return ans.toString();
//     }
// }