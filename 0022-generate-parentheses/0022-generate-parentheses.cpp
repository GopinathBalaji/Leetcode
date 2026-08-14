// Method 1: Backtracking with constraints
/*
Think of this as **backtracking with constraints**. You’re building the string one character at a time, but unlike permutations, you can’t place `(` or `)` freely.

A useful recursive state is:

```cpp
(open, close, current)
```

where `open` is how many `(` you’ve used and `close` is how many `)` you’ve used.

The first rule is that you can add an opening parenthesis as long as you haven’t used all `n` of them:

```cpp
if (open < n) {
    // add '('
}
```

The more important rule is for closing parentheses. You can only add `)` when there is an unmatched `(` available:

```cpp
if (close < open) {
    // add ')'
}
```

That condition prevents invalid strings like:

```text
")("
"())("
```

Your base case is when the string has used all `n` opening and all `n` closing parentheses:

```cpp
if (open == n && close == n) {
    // save current
    return;
}
```

A skeleton:

```cpp
void backtrack(int n,
               int open,
               int close,
               string& current,
               vector<string>& result) {

    if (open == n && close == n) {
        // save current
        return;
    }

    if (open < n) {
        current.push_back('(');

        // recurse

        current.pop_back();
    }

    if (close < open) {
        current.push_back(')');

        // recurse

        current.pop_back();
    }
}
```

For `n = 3`, one path looks like:

```text
""
"("
"(("
"(()"
"(())"
"(())("
"(())()"
```

The key idea to remember is:

```text
open < n      → you may add '('
close < open  → you may add ')'
```

Those two conditions ensure you generate only valid parentheses strings instead of generating everything and filtering afterward.
*/
class Solution {
private:
    void backtrack(int open, int close, int n, string& current, vector<string>& result){
        if(open == n && close == n){
            result.push_back(current);
            return;
        }

        if(open < n){
            current.push_back('(');

            backtrack(open + 1, close, n, current, result);

            current.pop_back();
        }

        if(close < open){
            current.push_back(')');

            backtrack(open, close + 1, n, current, result);

            current.pop_back();
        }
    }

public:
    vector<string> generateParenthesis(int n) {
        string current = "";
        vector<string> result;

        backtrack(0, 0, n, current, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna