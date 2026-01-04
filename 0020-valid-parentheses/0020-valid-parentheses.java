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





// Method 1.5: Similar to above without HashMap approach
/*
# WHAT WAS I DOING WRONG:

### 1) It can throw a `NullPointerException`

When you see a closing bracket, you do `stack.peek()` and compare it to `'('`, `'['`, `'{'`.

But if the stack is empty (e.g., input starts with `")"` or `"]"` or `"}"`), then `stack.peek()` returns `null`, and Java will try to auto-unbox that `Character` to a `char` for the `!= '('` comparison → **NullPointerException**.

Example:

* `s = ")"`
  `stack` is empty → `stack.peek()` is `null` → comparing to `'('` blows up.

Fix: check `stack.isEmpty()` before peeking/popping.

---

### 2) It returns `true` even when there are unmatched opening brackets

At the end you `return true;` no matter what.

So inputs like:

* `"("`
* `"((("`
* `"[("`

…will all return `true` even though they’re invalid, because you never verify the stack is empty.

Fix: `return stack.isEmpty();`
*/
// class Solution {
//     public boolean isValid(String s) {
//         int n = s.length();
//         Deque<Character> stack = new ArrayDeque<>();

//         for(int i=0; i<n; i++){
//             char c = s.charAt(i);

//             if(c == ')'){
//                 if(stack.isEmpty() || stack.peek() != '('){
//                     return false;
//                 }

//                 stack.pop();

//             }else if(c == ']'){
//                 if(stack.isEmpty() || stack.peek() != '['){
//                     return false;
//                 }

//                 stack.pop();

//             }else if(c == '}'){
//                 if(stack.isEmpty() || stack.peek() != '{'){
//                     return false;
//                 }

//                 stack.pop();

//             }else{
//                 stack.push(c);
//             }            
//         }

//         return stack.isEmpty();
//     }
// }







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
