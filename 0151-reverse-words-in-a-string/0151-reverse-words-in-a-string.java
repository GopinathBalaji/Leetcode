// My attempt: Unclean and uses an extra temporary string builder
// class Solution {
//     public String reverseWords(String s) {
//         StringBuilder ans = new StringBuilder();
//         StringBuilder str = new StringBuilder();

//         for (int i = s.length() - 1; i >= 0; i--) {
//             if (s.charAt(i) != ' ') {
//                 int j = i;
//                 while (j >= 0 && s.charAt(j) != ' ') {
//                     str.append(s.charAt(j));
//                     j--;
//                 }
//                 i = j;                  // for-loop will i-- next
//                 str.reverse();
//                 if (ans.length() > 0) ans.append(' ');  // avoid leading space
//                 ans.append(str);
//                 str.setLength(0);
//             }
//         }
//         return ans.toString();
//     }
// }




// Method 2: Clean Two pointer approach with only one stringbuilder for holding the answer
class Solution {
    public String reverseWords(String s) {
        StringBuilder ans = new StringBuilder();
        int i = s.length() - 1;

        while (i >= 0) {
            // 1) skip trailing/middle spaces
            while (i >= 0 && s.charAt(i) == ' ') i--;
            if (i < 0) break;

            // 2) find the start of this word
            int j = i;
            while (j >= 0 && s.charAt(j) != ' ') j--;

            // 3) append separator only if we already have a word
            if (ans.length() > 0) ans.append(' ');

            // 4) append the word [j+1, i]
            ans.append(s, j + 1, i + 1);

            // 5) move i to the char before this word
            i = j - 1;
        }

        return ans.toString();
    }
}