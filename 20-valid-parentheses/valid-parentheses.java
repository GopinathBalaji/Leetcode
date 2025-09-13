// Method 1: Without HashMap
/*
We only pop when we see a closer and it matches the expected opener on top of the stack.
Any mismatch or early closer with an empty stack returns false.
At the end, the stack must be empty (no unmatched openers left).
*/
class Solution {
    public boolean isValid(String s) {
        Deque<Character> st = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '(': case '{': case '[':
                    st.push(c);
                    break;
                case ')':
                    if (st.isEmpty() || st.pop() != '(') return false;
                    break;
                case '}':
                    if (st.isEmpty() || st.pop() != '{') return false;
                    break;
                case ']':
                    if (st.isEmpty() || st.pop() != '[') return false;
                    break;
                default:
                    return false; // if inputs can contain other chars
            }
        }
        return st.isEmpty();
    }
}



// Method 2: Using HashMap
// class Solution {
//     public boolean isValid(String s) {
//         Deque<Character> st = new ArrayDeque<>();
//         Map<Character, Character> need = Map.of(
//             ')', '(',
//             '}', '{',
//             ']', '['
//         );

//         for (int i = 0; i < s.length(); i++) {
//             char c = s.charAt(i);

//             if (need.containsKey(c)) {            // c is a closing bracket
//                 if (st.isEmpty() || st.pop() != need.get(c)) {
//                     return false;                 // mismatch or no opener
//                 }
//             } else {                               // c is an opening bracket
//                 st.push(c);
//             }
//         }
//         return st.isEmpty();                       // no unmatched openers
//     }
// }