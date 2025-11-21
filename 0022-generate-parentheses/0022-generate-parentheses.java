// Method 1: Backtracking
/*
## 1. The standard backtracking strategy

For `n` pairs of parentheses:

* We build the string one char at a time in a `StringBuilder`.
* We track:

  * `openCount` = how many `'('` used so far.
  * `closeCount` = how many `')'` used so far.
* At each step:

  * We can add `'('` **if** `openCount < n`.
  * We can add `')'` **if** `closeCount < openCount` (must not close more than we’ve opened).

When the string reaches length `2 * n`, we add it to the answer list.


Key points:

* **Base case**: `sb.length() == 2*n` → record string and return.
* **Two branches**:

  * Add `'('` if `openCount < n`.
  * Add `')'` if `closeCount < openCount`.
* `StringBuilder` is mutated, then undone (`deleteCharAt`) to backtrack.

---

## 4. Detailed walkthrough for `n = 2`

We expect: `["(())","()()"]`.

Start:

```text
sb = ""
openCount = 0, closeCount = 0
```

### Step 1 — from `""`

* `sb.length() = 0`, not full yet.
* Can we add `'('`? `openCount < n → 0 < 2` → yes.
* Can we add `')'`? `closeCount < openCount → 0 < 0` → no.

So we only try `'('` first:

#### Add '(':

```text
sb = "("
openCount = 1, closeCount = 0
call backtrack(...)
```

---

### Step 2 — from `"("`

* `sb.length() = 1`, not full.
* Can we add `'('`? `1 < 2` → yes.
* Can we add `')'`? `0 < 1` → yes.

So two branches from here.

---

#### Branch A: add '(' again

`sb = "(("`, `openCount = 2`, `closeCount = 0`.

* `sb.length() = 2`, not full (we need 4).
* Can we add `'('`? `openCount < n → 2 < 2` → no.
* Can we add `')'`? `closeCount < openCount → 0 < 2` → yes.

Add `')'`:

```text
sb = "(()"
openCount = 2, closeCount = 1
```

* `sb.length() = 3`, not full.
* `'('`? `2 < 2` → no.
* `')'`? `1 < 2` → yes.

Add `')'`:

```text
sb = "(())"
openCount = 2, closeCount = 2
```

* `sb.length() = 4 == 2*n` → **base case**:

  * add "(())" to ans.

Backtrack: remove last char each time we return up:

* From `"(()")` back to `"(()"`
* Then to `"(("`
* Then we’re done exploring from `"(("`.

---

#### Branch B: from `"("`, instead add ')' now

Go back up to `"("` (after finishing branch A, we had backtracked to there):

`sb = "("`, `openCount = 1`, `closeCount = 0`.

Now try adding `')'`:

```text
sb = "()"
openCount = 1, closeCount = 1
```

* `sb.length() = 2`, not full.
* `'('`? `1 < 2` → yes.
* `')'`? `1 < 1` → no.

Add `'('`:

```text
sb = "()("
openCount = 2, closeCount = 1
```

* `sb.length() = 3`, not full.
* `'('`? `2 < 2` → no.
* `')'`? `1 < 2` → yes.

Add `')'`:

```text
sb = "()()"
openCount = 2, closeCount = 2
```

* `sb.length() = 4 == 2*n` → base case:

  * add "()()" to ans.

Backtrack back to `"("`, then to `""`. No more possibilities.

---

## 5. Final result for `n = 2`

`ans = ["(())", "()()"]`.

You can see:

* We never generate invalid sequences (like `")("` or `"())("`), because:

  * We never allow more `)` than `(` (`closeCount < openCount`).
  * We never add more than `n` `'('`.
*/
class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> ans = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int openCount = 0;
        int closeCount = 0;

        backtrack(ans, sb, n, 2 * n, openCount, closeCount);

        return ans;
    }

    static private void backtrack(List<String> ans, StringBuilder sb, int n, int limit, int openCount, int closeCount){
        if(openCount + closeCount > limit){
            return;
        }
        if(closeCount > openCount){
            return;
        }
        if(openCount == closeCount && openCount + closeCount == limit){
            String valid = sb.toString();
            ans.add(valid);
        }

        // Option 1: add '(' if we still can
        if(openCount < n){
            sb.append('(');
            backtrack(ans, sb, n, limit, openCount + 1, closeCount);
            sb.deleteCharAt(sb.length() - 1);
        }

        // Option 2: add ')' if it won't break validity
        if(closeCount < openCount){
            sb.append(')');
            backtrack(ans, sb, n, limit, openCount, closeCount + 1);
            sb.deleteCharAt(sb.length() - 1);
        }

        return;
    }
}




// Method 2: Dynamic Programming (Catalan-number problem)
/*
## 1. Key structural insight

Any valid parentheses string with `n` pairs has this form:

```text
"(" + A + ")" + B
```

Where:

* `A` is **some valid parentheses string** with `i` pairs,
* `B` is **some valid parentheses string** with the remaining `n - 1 - i` pairs.

Why?

* The very first character of any valid string must be `'('`.
* There will be some position where this first `'('` is matched by a `')'`.
  Everything **inside those two** is `A`.
  Everything **after** that `)` is `B`.

So for every `n`, any valid string can be decomposed into:

> A pair `"(" + ... + ")"` that wraps some smaller valid string, plus another valid string after it.

That’s exactly what DP will use.

---

## 2. Defining the DP state

Let:

```text
dp[k] = list of all valid parentheses strings with exactly k pairs
```

We want `dp[n]`.

Base case:

```text
dp[0] = [""]   // one valid string with 0 pairs: the empty string
```

Recurrence (for k ≥ 1):

```text
dp[k] = all strings of the form
    "(" + left + ")" + right
where
    left  ∈ dp[i]
    right ∈ dp[k - 1 - i]
for all i from 0 to k-1
```

This is the direct translation of the structure:

* Put `i` pairs **inside** the first pair of parentheses,
* and the remaining `k-1-i` pairs **after** that.

We’re basically splitting `k-1` (the remaining pairs after we use one pair for the outermost `"(" and ")"`) into `i` and `k-1-i`.


## 4. Detailed walkthrough with `n = 3`

We’ll build `dp[0]`, `dp[1]`, `dp[2]`, `dp[3]`.

### Step 0: Base case

```text
dp[0] = [ "" ]
```

Just the empty string.

---

### Step 1: Build dp[1]

We want all valid strings with **1 pair**.

By the recurrence:

```text
dp[1] = "(" + dp[i] + ")" + dp[1-1-i] for i = 0..0
```

Only `i = 0`:

* `inside = dp[0] = [""]`
* `after  = dp[0] = [""]`

Combine:

* take `in = ""`, `out = ""`:

  * `"(" + "" + ")" + ""` = `"()"`

So:

```text
dp[1] = [ "()" ]
```

---

### Step 2: Build dp[2]

Now for 2 pairs:

```text
dp[2] = "(" + dp[i] + ")" + dp[2 - 1 - i] for i = 0..1
      = "(" + dp[i] + ")" + dp[1 - i]
```

#### Case i = 0

* `inside = dp[0] = [""]`
* `after  = dp[1] = ["()"]`

Combine:

* `in = ""`, `out = "()"`:

  * `"(" + "" + ")" + "()"` = `"()" + "()"` = `"()()"`

So from `i = 0`, we get: `"()()"`.

#### Case i = 1

* `inside = dp[1] = ["()"]`
* `after  = dp[0] = [""]`

Combine:

* `in = "()"`, `out = ""`:

  * `"(" + "()" + ")" + ""` = `"(())"`

So from `i = 1`, we get: `"(())"`.

Collecting everything:

```text
dp[2] = [ "()()", "(())" ]
```

(Any order is fine; you might see `["(())", "()()"]` depending on iteration order.)

---

### Step 3: Build dp[3]

Now `k = 3`, so:

```text
dp[3] = "(" + dp[i] + ")" + dp[3 - 1 - i] for i = 0..2
      = "(" + dp[i] + ")" + dp[2 - i]
```

We’ll go case by case.

---

#### Case i = 0

* `inside = dp[0] = [""]`
* `after  = dp[2] = ["()()", "(())"]`

For each `in` and `out`:

1. `in = ""`, `out = "()()"`:

   * `"(" + "" + ")" + "()()"` = `"()" + "()()"` = `"()()()"`

2. `in = ""`, `out = "(())"`:

   * `"(" + "" + ")" + "(())"` = `"()" + "(())"` = `"()(())"`

So from `i = 0`, we get:

```text
"()()()", "()(())"
```

---

#### Case i = 1

* `inside = dp[1] = ["()"]`
* `after  = dp[1] = ["()"]`

Combine:

1. `in = "()"`, `out = "()"`:

   * `"(" + "()" + ")" + "()"` = `"(())" + "()"` = `"(())()"`

So from `i = 1`, we get:

```text
"(())()"
```

---

#### Case i = 2

* `inside = dp[2] = ["()()", "(())"]`
* `after  = dp[0] = [""]`

Combine:

1. `in = "()()"`, `out = ""`:

   * `"(" + "()()" + ")" + ""` = `"(()())"`

2. `in = "(())"`, `out = ""`:

   * `"(" + "(())" + ")" + ""` = `"((()))"`

So from `i = 2`, we get:

```text
"(()())", "((()))"
```

---

### Collecting all for dp[3]

From all `i`:

* From `i=0`: `"()()()"`, `"()(())"`
* From `i=1`: `"(())()"`
* From `i=2`: `"(()())"`, `"((()))"`

So:

```text
dp[3] =
[
  "()()()",
  "()(())",
  "(())()",
  "(()())",
  "((()))"
]
```

(Again, exact ordering may differ, but the set is the same.)

These are exactly all valid parentheses strings with 3 pairs.

---

## 5. How to explain this in an interview

You can phrase it like:

> “Any valid parentheses string with `n` pairs can be decomposed as
> `'(' + A + ')' + B`, where `A` is a valid sequence with `i` pairs and `B` is a valid sequence with `n-1-i` pairs.
> That gives a natural DP:
>
> * `dp[0] = [""]`,
> * `dp[n] = for all i in [0..n-1], combine '(' + left + ')' + right for `left in dp[i]`, `right in dp[n-1-i]`.”

Then show them the loop:

```java
for (int k = 1; k <= n; k++) {
    for (int i = 0; i < k; i++) {
        for (String in : dp[i])
            for (String out : dp[k-1-i])
                add "(" + in + ")" + out;
    }
}
*/

// class Solution {
//     public List<String> generateParenthesis(int n) {
//         // dp[k] will hold all valid strings with k pairs
//         List<List<String>> dp = new ArrayList<>();
//         dp.add(new ArrayList<>()); // dp[0]
//         dp.get(0).add("");         // base case: one empty string

//         // Build up from 1 pair to n pairs
//         for (int k = 1; k <= n; k++) {
//             List<String> curList = new ArrayList<>();

//             // Split k-1 pairs into i inside and k-1-i after
//             for (int i = 0; i < k; i++) {
//                 List<String> inside = dp.get(i);          // dp[i]
//                 List<String> after  = dp.get(k - 1 - i);  // dp[k-1-i]

//                 // Combine all inside/after possibilities
//                 for (String in : inside) {
//                     for (String out : after) {
//                         curList.add("(" + in + ")" + out);
//                     }
//                 }
//             }

//             dp.add(curList);
//         }

//         return dp.get(n);
//     }
// }
