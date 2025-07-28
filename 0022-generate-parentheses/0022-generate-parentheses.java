// Backtracking with loop
/*
## What Your Code Is Doing Right

* You’re correctly using **backtracking**.
* You’re keeping track of the number of open and close parentheses added.
* You're ensuring that at no point do close parentheses outnumber open ones.
* You correctly use `StringBuilder` to build strings efficiently.
* You use `.deleteCharAt()` to backtrack, undoing the last decision ( well done!).

---

##  Detailed Step-by-Step Walkthrough (n = 2)

Let’s walk through your code with `n = 2`, so we want to generate all combinations of 2 pairs of parentheses.

Your initial call is:

```java
backtrack(2, "", [], 0, 0);
```

Now let's walk through the recursive calls in a tree-like structure. Each node is a state: `StringBuilder`, `openCount`, `closeCount`.

---

### First Call:

* `sb = ""`, `openCount = 0`, `closeCount = 0`
* Loop over `['(', ')']`

####  Try `'('`:

* Valid because `openCount < n`
* Append `'('` → `sb = "("`
* Recurse with `openCount = 1`, `closeCount = 0`

---

### Second Call:

* `sb = "("`, `openCount = 1`, `closeCount = 0`
* Loop over `['(', ')']`

####  Try `'('`:

* Valid again (`openCount = 1 < 2`)
* Append `'('` → `sb = "(("`
* Recurse with `openCount = 2`, `closeCount = 0`

---

### Third Call:

* `sb = "(("`, `openCount = 2`, `closeCount = 0`
* Loop over `['(', ')']`

####  `'('` is **invalid** now (we already have `n` opens)

####  Try `')'`:

* Valid because `closeCount < openCount` (0 < 2)
* Append `')'` → `sb = "(()"`
* Recurse with `openCount = 2`, `closeCount = 1`

---

### Fourth Call:

* `sb = "(()"`, `openCount = 2`, `closeCount = 1`
* Loop over `['(', ')']`

####  `'('` is invalid (`openCount == n`)

####  `')'` is valid (`closeCount < openCount`)

* Append `')'` → `sb = "(())"`
* Recurse with `openCount = 2`, `closeCount = 2`

---

### Fifth Call:

* `sb = "(())"`, `openCount = 2`, `closeCount = 2`
* Base case: `sb.length() == 2 * n` and open == close → done
* Add `"(())"` to `ans`

Now we **backtrack**:

* Remove last character: `sb = "(()"`

Backtrack again: `sb = "(("`

Backtrack again: `sb = "("`

---

####  Back in Second Call, try `')'`:

* Invalid, because `closeCount = 0`, `openCount = 1`, so still valid!
* Append `')'` → `sb = "()"`, recurse with `openCount = 1`, `closeCount = 1`

---

### New Branch:

* `sb = "()"`, `openCount = 1`, `closeCount = 1`
* Loop over `['(', ')']`

#### Try `'('`:

* Valid (`openCount < n`)
* `sb = "()("`, recurse with `openCount = 2`, `closeCount = 1`

#### Then try `')'`:

* Valid (`closeCount < openCount`)
* `sb = "()()"`, recurse → base case met → add to answer

So we generate:

* `(())`
* `()()`

---

## Why Your Loop Works (but Slightly Inefficient)

You're looping over both `'('` and `')'` at every level. But most of the time, **one of those choices is invalid**, so it gets skipped. This introduces **extra logic and branching**, even though only one or two choices are valid at each step.

This is why most solutions write:

```java
if (openCount < n) backtrack with '('
if (closeCount < openCount) backtrack with ')'
```

— separately, without a loop — to make it cleaner.

---

##  Why You Need `new ArrayList<>(sb)`

You don’t in this problem — but here’s the logic behind it for future use:

Whenever you use **mutable objects like `StringBuilder` or `List`**, if you pass them to a result list directly, they’ll keep getting modified after being added (because of Java references).

So you do:

```java
ans.add(new ArrayList<>(path));
```

or

```java
ans.add(sb.toString());
```

to make a **copy of the current state** — freezing it in its current form before it gets changed in further backtracking.
*/
class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> ans = new ArrayList<>();
        
        backtrack(n, new StringBuilder(), ans, 0, 0);
        
        return ans;
    }

    private void backtrack(int n, StringBuilder sb, List<String> ans, int openCount, int closeCount){
        if(sb.length() == 2 * n && openCount == closeCount){
            ans.add(sb.toString());

            return;
        }

        for(char c: new char[] {'(', ')'}){
            // Try adding '(' if we still have some left
            if(openCount < n && c == '('){
                sb.append(c);
                backtrack(n, sb, ans, openCount + 1, closeCount);
                sb.deleteCharAt(sb.length() - 1);
            }

            // Try adding ')' if we can match an existing '('
            if(closeCount < openCount && c == ')'){
                sb.append(c);
                backtrack(n, sb, ans, openCount, closeCount + 1);
                sb.deleteCharAt(sb.length() - 1);
            }
        }

    }
}


// Same backtracking logic but without loop

// private void backtrack(int n, StringBuilder sb, List<String> ans, int openCount, int closeCount) {
//     if (sb.length() == 2 * n) {
//         ans.add(sb.toString());
//         return;
//     }

//     // Try adding '(' if we still have some left
//     if (openCount < n) {
//         sb.append('(');
//         backtrack(n, sb, ans, openCount + 1, closeCount);
//         sb.deleteCharAt(sb.length() - 1); // backtrack
//     }

//     // Try adding ')' if we can match an existing '('
//     if (closeCount < openCount) {
//         sb.append(')');
//         backtrack(n, sb, ans, openCount, closeCount + 1);
//         sb.deleteCharAt(sb.length() - 1); // backtrack
//     }
// }