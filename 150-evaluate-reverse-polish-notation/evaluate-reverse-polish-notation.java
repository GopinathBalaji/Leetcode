// Use stack
/*
Every number token is pushed as an Integer.
On an operator, we pop the right operand first (b), then the left (a), and compute a op b. This is crucial for - and /.
Java integer division truncates toward zero, which matches LeetCode 150’s requirement.
Time O(n), space O(n) in the worst case (all numbers first).


## Quick walkthrough

### Example 1

`["2","1","+","3","*"]`

* push 2 → \[2]
* push 1 → \[2,1]
* `+` → pop 1,2 → push 3 → \[3]
* push 3 → \[3,3]
* `*` → pop 3,3 → push 9 → \[9] → **ans=9**

### Example 2

`["4","13","5","/","+"]`

* push 4 → \[4]
* push 13 → \[4,13]
* push 5 → \[4,13,5]
* `/` → 13/5 = 2 → \[4,2]
* `+` → 4+2 = 6 → \[6] → **ans=6**

### Example 3 (negatives)

`["10","6","9","3","+","-11","*","/","*","17","+","5","+"]` → **22** (LeetCode’s big one)

---
## Using Regex

* Change `Deque<String>` → `Deque<Integer>`.
* Replace the regex test with a simple “is operator?” test:

  ```java
  boolean isOp = tok.equals("+") || tok.equals("-") || tok.equals("*") || tok.equals("/");
  ```

  If not `isOp`, `Integer.parseInt(tok)` (handles negatives).
* Always use `tokens[i]` (not `tokens[0]`) when pushing.
* Use `.equals` for string comparisons.
* Return `st.pop()` (int), not a string.
*/

class Solution {
    public int evalRPN(String[] tokens) {
        Deque<Integer> st = new ArrayDeque<>();

        for (String tok : tokens) {
            switch (tok) {
                case "+":
                    st.push(st.pop() + st.pop());
                    break;
                case "-": {
                    int b = st.pop(), a = st.pop();
                    st.push(a - b);
                    break;
                }
                case "*":
                    st.push(st.pop() * st.pop());
                    break;
                case "/": {
                    int b = st.pop(), a = st.pop();
                    st.push(a / b);  // truncates toward 0 as required
                    break;
                }
                default:
                    // Handles negatives too, e.g., "-11"
                    st.push(Integer.parseInt(tok));
            }
        }
        return st.pop();
    }
}