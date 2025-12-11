// Method 1: Top-Down 2D DP
/*
## 1. Core idea

We define a recursive function:

> `dp(i, j)` = **does `s[i:]` match `p[j:]`?**

* `i` is an index into the string `s`
* `j` is an index into the pattern `p`
* We want `dp(0, 0)`

We’ll add memoization so that each `(i, j)` state is computed only once.

---

## 2. Base case

### When pattern is finished

If `j == n` (where `n = p.length()`):

* The pattern is used up.
* It matches if and only if the string is also used up: `i == m`.

So:

```java
if (j == n) {
    return i == m;
}
```

This must be the first check in `dp`, **before** we access `p.charAt(j)`.

> Notice we allow `i == m` here — that’s an important state we need to handle safely.

---

## 3. First character match (for state `(i, j)`)

We need to know if the current characters can match:

```java
boolean firstMatch = (i < m) &&
    (s.charAt(i) == p.charAt(j) || p.charAt(j) == '.');
```

* `i < m` ensures we don’t go out of bounds on `s`.
* `p[j]` matches `s[i]` if:

  * They are the same character, or
  * `p[j] == '.'` (dot matches any single char).

We’ll use `firstMatch` in both the `*` case and the normal case.

---

## 4. Handling the `*` case

If the **next** character in the pattern is `'*'`:

```java
if (j + 1 < n && p.charAt(j + 1) == '*') {
    ...
}
```

Then the pattern at `j` and `j+1` is something like `'x*'`, which means:

* “zero or more of `x`”

So we have two options:

1. **Skip the `x*` entirely** (use it zero times)
   Move past both characters in the pattern: `j + 2`

   ```java
   dp(i, j + 2)
   ```

2. **Use `x*` to consume one character from `s`** (only if `firstMatch` is true)
   We advance `i` by 1 (consume `s[i]`), but **keep `j` where it is**, because we may use `x*` again:

   ```java
   firstMatch && dp(i + 1, j)
   ```

So combined:

```java
ans = dp(i, j + 2) || (firstMatch && dp(i + 1, j));
```

---

## 5. Normal (non-`*`) case

If the next char is **not** `'*'`:

```java
else {
    ans = firstMatch && dp(i + 1, j + 1);
}
```

* If `firstMatch` is false, this whole branch fails.
* If `firstMatch` is true, we consume one character from both the string and the pattern.

This handles both:

* middle of pattern, and
* last character of pattern (because `j + 1 == n` → `j + 1 < n` is false → we go to the `else` branch).

---

## 6. Memoization

There are `m + 1` possible `i` values (`0..m`) and `n + 1` possible `j` values (`0..n`).

So we use:

```java
Boolean[][] memo = new Boolean[m + 1][n + 1];
```

In `dp(i, j)`:

* If `memo[i][j]` is not `null`, return it.
* Otherwise compute `ans`, store it, and return it.


## 8. Example walkthrough: `"aab"` vs `"c*a*b"`

Let’s walk through `s = "aab"`, `p = "c*a*b"`.

Indices:

* `s = "a a b"`

  * `s[0] = 'a'`, `s[1] = 'a'`, `s[2] = 'b'`, length `m = 3`
* `p = "c * a * b"`

  * `p[0] = 'c'`
  * `p[1] = '*'`
  * `p[2] = 'a'`
  * `p[3] = '*'`
  * `p[4] = 'b'`, length `n = 5`

We start with `dp(0, 0)` meaning: does `"aab"` match `"c*a*b"`?

---

### Step 1: `dp(0, 0)` → s = `"aab"`, p = `"c*a*b"`

* `j = 0 < n` (pattern not finished)
* `firstMatch` compares `s[0] = 'a'` and `p[0] = 'c'`:

  * `'a' == 'c'`? → no
  * `p[0] == '.'`? → no
  * So `firstMatch = false`
* Look ahead: `p[1] = '*'`, so we’re in the `*` case.

We compute:

```java
ans = dp(0, 2) || (firstMatch && dp(1, 0))
    = dp(0, 2) || (false && ...)
    = dp(0, 2)
```

So `dp(0, 0)` reduces to checking **`dp(0, 2)`** (we skip `"c*"`).

---

### Step 2: `dp(0, 2)` → s = `"aab"`, p from 2 = `"a*b"`

Now pattern substring is `"a*b"`.

* `firstMatch`: compare `s[0] = 'a'`, `p[2] = 'a'`

  * `'a' == 'a'` → true
  * So `firstMatch = true`
* Look ahead: `p[3] = '*'`, star case again.

```java
ans = dp(0, 4) || (firstMatch && dp(1, 2));
```

So:

* Branch 1: Skip `"a*"` → `dp(0, 4)` (does `"aab"` match `"b"`?)
* Branch 2: Use `"a*"` and consume one `'a'` → `dp(1, 2)` (does `"ab"` match `"a*b"`?)

We need to see if either is true.

---

### Step 3: Branch 1 — `dp(0, 4)` → s = `"aab"`, p from 4 = `"b"`

* `firstMatch`: `s[0] = 'a'`, `p[4] = 'b'`

  * not equal, not dot → `firstMatch = false`
* `j + 1 = 5` which is `== n`, so no `*` case, go to normal case:

```java
ans = firstMatch && dp(1, 5) = false && ... = false
```

So `dp(0, 4) = false`.

Now we rely on **Branch 2**: `dp(1, 2)`.

---

### Step 4: Branch 2 — `dp(1, 2)` → s from 1 = `"ab"`, p from 2 = `"a*b"`

* `firstMatch`: compare `s[1] = 'a'`, `p[2] = 'a'` → match, so `firstMatch = true`
* Look ahead: `p[3] = '*'` → star case again.

```java
ans = dp(1, 4) || (firstMatch && dp(2, 2));
```

So we check:

* Branch 2.1: `dp(1, 4)` (skip `"a*"`, so `"ab"` vs `"b"`)
* Branch 2.2: `dp(2, 2)` (use `"a*"` to consume one `'a'`, so `"b"` vs `"a*b"`)

---

### Step 5: Branch 2.1 — `dp(1, 4)` → s from 1 = `"ab"`, p from 4 = `"b"`

* `firstMatch`: compare `s[1] = 'a'`, `p[4] = 'b'`

  * no, so `firstMatch = false`
* No `*` after `p[4]` (we’re at last char), so normal case:

```java
ans = firstMatch && dp(2, 5) = false && ... = false
```

So `dp(1, 4) = false`.

We rely on Branch 2.2: `dp(2, 2)`.

---

### Step 6: Branch 2.2 — `dp(2, 2)` → s from 2 = `"b"`, p from 2 = `"a*b"`

* `firstMatch`: compare `s[2] = 'b'`, `p[2] = 'a'`

  * not equal, not dot → `firstMatch = false`
* Still, `p[3] = '*'`, star case:

```java
ans = dp(2, 4) || (firstMatch && dp(3, 2))
    = dp(2, 4) || false;
```

So we must evaluate `dp(2, 4)` (skip `"a*"`) → `"b"` vs `"b"`.

---

### Step 7: `dp(2, 4)` → s from 2 = `"b"`, p from 4 = `"b"`

* `firstMatch`: `s[2] = 'b'`, `p[4] = 'b'` → equal → `firstMatch = true`
* `j + 1 = 5 == n`, so normal case:

```java
ans = firstMatch && dp(3, 5)
```

We need `dp(3, 5)`.

---

### Step 8: `dp(3, 5)` → `i = 3`, `j = 5`

* `j == n` (5 == length of pattern)
* So return `i == m`?
  `i = 3`, `m = 3` → yes, so `dp(3, 5) = true`.

Therefore:

* `dp(2, 4) = true && true = true`
* So `dp(2, 2) = true`
* So `dp(1, 2) = true`
* So `dp(0, 2) = true`
* So `dp(0, 0) = true`

And thus `"aab"` **matches** `"c*a*b"`.

---

## 9. Why memo helps

Without memo, you might revisit states like `(2, 4)` many times in different branches. With memoization:

* The first time you compute `dp(2, 4)`, you store `true`.
* Next time you encounter `dp(2, 4)`, you return immediately.

This makes the complexity roughly `O(m * n)`.
*/

class Solution {
    public boolean isMatch(String s, String p) {
        int m = s.length();
        int n = p.length();
        Boolean[][] memo = new Boolean[m + 1][n + 1];
        return dp(s, p, memo, m, n, 0, 0);
    }

    private boolean dp(String s, String p, Boolean[][] memo, int m, int n, int i, int j) {
        // if pattern is finished, string must also be finished
        if (j == n) {
            return i == m;
        }

        // if already computed
        if (memo[i][j] != null) {
            return memo[i][j];
        }

        // check if first char matches (only if i < m)
        boolean firstMatch = (i < m) &&
            (s.charAt(i) == p.charAt(j) || p.charAt(j) == '.');

        boolean ans;

        // star case: next char exists and is '*'
        if (j + 1 < n && p.charAt(j + 1) == '*') {
            ans = dp(s, p, memo, m, n, i, j + 2)         // skip x*
                  || (firstMatch && dp(s, p, memo, m, n, i + 1, j)); // use x* once
        } else {
            // normal case: must consume one char from both
            ans = firstMatch && dp(s, p, memo, m, n, i + 1, j + 1);
        }

        memo[i][j] = ans;
        return ans;
    }
}





// Method 2: Bottom-Up 2D DP
/*
## 1. DP definition

We build a 2D boolean table:

> `dp[i][j]` = does `s[0..i-1]` (first `i` chars of `s`) match `p[0..j-1]` (first `j` chars of `p`)?

* `i` ranges from `0` to `m` (inclusive), where `m = s.length()`.
* `j` ranges from `0` to `n` (inclusive), where `n = p.length()`.

So:

* `dp[0][0]` = does empty string match empty pattern → `true`.
* Our final answer is `dp[m][n]`.

We’re essentially filling a (m+1) x (n+1) grid.

---

## 2. Base initialization

### 2.1. Empty string vs empty pattern

```java
dp[0][0] = true;
```

### 2.2. Empty string vs non-empty pattern

We need to fill `dp[0][j]` for `j = 1..n`:

* Empty string can only be matched by patterns like: `"a*b*c*..."` where every “block” is `x*` that can be taken as zero occurrences.
* So for each `j`:

  If `p[j-1] == '*'`, then `dp[0][j]` can be `true` **only** if skipping the `x*` pair leaves a valid match:

  ```java
  dp[0][j] = dp[0][j-2]  // skip the x*
  ```

This corresponds to the recursive “skip `x*`” behavior, but we do it iteratively.

---

## 3. Transition rules

For general `i ≥ 1`, `j ≥ 1`:

### Case A: Pattern character is NOT `*` (`p[j-1] != '*'`)

We just try to match the current characters and then look at the previous prefix:

* Current characters match if:

  * `s[i-1] == p[j-1]` OR
  * `p[j-1] == '.'`

So:

```java
if (p[j-1] != '*') {
    boolean firstMatch = (s.charAt(i-1) == p.charAt(j-1) || p.charAt(j-1) == '.');
    dp[i][j] = firstMatch && dp[i-1][j-1];
}
```

This mirrors the top-down `firstMatch && dp(i+1, j+1)`.

---

### Case B: Pattern character is `*` (`p[j-1] == '*'`)

Here the `*` belongs with the **previous** pattern character `p[j-2]` (call it `'x'`), forming `x*`.

We have two sub-cases:

1. **Skip the `x*` entirely** (0 occurrences of `x`):

   ```java
   dp[i][j] = dp[i][j-2];
   ```

   This corresponds to the pattern prefix without the last two chars (`x*`).

2. **Use `x*` to match at least one `s[i-1]`**
   If `s[i-1]` matches `x` (or dot):

   ```java
   if (s[i-1] == p[j-2] || p[j-2] == '.') {
       dp[i][j] = dp[i][j] || dp[i-1][j];
   }
   ```

   Why `dp[i-1][j]`?

   * We have used one `x` (via `x*`) to match `s[i-1]`.
   * The pattern prefix up to `j` (still including `x*`) should match the prefix `s[0..i-2]` (length `i-1`).
   * That’s exactly `dp[i-1][j]`.

Complete logic for `*`:

```java
if (p.charAt(j-1) == '*') {
    // Option 1: Skip x*
    dp[i][j] = dp[i][j-2];

    // Option 2: Use x* if it can match s[i-1]
    if (s.charAt(i-1) == p.charAt(j-2) || p.charAt(j-2) == '.') {
        dp[i][j] = dp[i][j] || dp[i-1][j];
    }
}
```

Note: we require `j >= 2` for `p[j-2]` to be valid. By the problem’s constraints, `'*'` will always have a valid preceding character, so `j-2` is safe when `j ≥ 2`.


## 5. Example walkthrough: `s = "aab"`, `p = "c*a*b"`

Let’s walk through the DP table.

### 5.1. Setup

* `s = "aab"`, `m = 3`
* `p = "c*a*b"`, `n = 5`

Index pattern characters:

* `p[0] = 'c'`
* `p[1] = '*'`
* `p[2] = 'a'`
* `p[3] = '*'`
* `p[4] = 'b'`

The DP table `dp` is of size `4 x 6` (rows `i = 0..3`, cols `j = 0..5`).

`dp[i][j]` = match `s[0..i-1]` with `p[0..j-1]`.

---

### 5.2. Initialize row 0 (empty string vs pattern prefix)

* `dp[0][0] = true` (empty vs empty)

For `j = 1..5`:

* `j = 1`: pattern `"c"`
  `p[0] = 'c'`, not `'*'`, so `dp[0][1] = false`.

* `j = 2`: pattern `"c*"`
  `p[1] = '*'`, so we can skip `"c*"` if `dp[0][0]` is true:

  ```java
  if p[1] == '*' and dp[0][0] is true:
      dp[0][2] = true
  ```

  So `dp[0][2] = true`.

* `j = 3`: pattern `"c*a"`
  `p[2] = 'a'`, not `'*'`, so `dp[0][3] = false`.

* `j = 4`: pattern `"c*a*"`
  `p[3] = '*'`, so:

  ```java
  dp[0][4] = dp[0][2];
  ```

  Since `dp[0][2] = true`, we get `dp[0][4] = true`.

* `j = 5`: pattern `"c*a*b"`
  `p[4] = 'b'`, not `'*'`, so `dp[0][5] = false`.

So row `i = 0` is:

| i\j | 0    | 1     | 2    | 3     | 4    | 5     |
| --- | ---- | ----- | ---- | ----- | ---- | ----- |
| 0   | true | false | true | false | true | false |

Interpretation:

* empty string can match `"c*"` and `"c*a*"` (because you can take 0 of `c`, 0 of `a`),
* but not `"c"`, `"c*a"`, `"c*a*b"`.

---

### 5.3. Fill row i = 1 (s prefix `"a"`)

We’re matching `s[0..0] = "a"`.

#### j = 1: pattern `"c"`

* `p[0] = 'c'` (not `'*'`), normal case.
* `firstMatch = (s[0] == 'c'?)` → `'a' == 'c'`? no → `false`
* `dp[1][1] = firstMatch && dp[0][0] = false && true = false`.

#### j = 2: pattern `"c*"`

* `p[1] = '*'`, star case:

  * Option 1: skip `"c*"` → `dp[1][2] = dp[1][0]` → `dp[1][0]` is default `false`.
  * Option 2: use `"c*"`:

    * Check if `'c'` (p[0]) matches `s[0]` = `'a'` → no
    * So cannot use `x*` to consume `'a'`.
  * So `dp[1][2] = false`.

#### j = 3: pattern `"c*a"`

* `p[2] = 'a'` (not `'*'`), normal:

  * `firstMatch = (s[0] == p[2]?)` → `'a' == 'a'` → true
  * `dp[1][3] = firstMatch && dp[0][2] = true && true = true`.

#### j = 4: pattern `"c*a*"`

* `p[3] = '*'`, star case:

  * Option 1: skip `"a*"` → `dp[1][4] = dp[1][2] = false`
  * Option 2: use `"a*"` if `'a'` matches `s[0]`:

    * `x = p[2] = 'a'`
    * `s[0] = 'a'` → match
    * `dp[1][4] = dp[1][4] || dp[0][4] = false || true = true`.

So `dp[1][4] = true`.

#### j = 5: pattern `"c*a*b"`

* `p[4] = 'b'` (not `'*'`), normal:

  * `firstMatch = (s[0] == 'b'?)` → `'a' == 'b'`? no → `false`
  * `dp[1][5] = false && dp[0][4] = false`.

Row `i = 1`:

| i\j | 0     | 1     | 2     | 3     | 4    | 5     |
| --- | ----- | ----- | ----- | ----- | ---- | ----- |
| 0   | true  | false | true  | false | true | false |
| 1   | false | false | false | true  | true | false |

---

### 5.4. Row i = 2 (s prefix `"aa"`)

Now `s[0..1] = "aa"`.

#### j = 1: `"c"`

* `p[0] = 'c'`, not `'*'`, normal:

  * `firstMatch = ('a' == 'c'?)` → false
  * `dp[2][1] = false`.

#### j = 2: `"c*"`

* `p[1] = '*'`, star:

  * Skip: `dp[2][2] = dp[2][0]` → `false`.
  * Use `c*`: check if `'c'` matches `'a'` → no.
  * So `dp[2][2] = false`.

#### j = 3: `"c*a"`

* `p[2] = 'a'`, normal:

  * `firstMatch = (s[1] == 'a'?)` → `'a' == 'a'` → true
  * `dp[2][3] = true && dp[1][2] = true && false = false`.

#### j = 4: `"c*a*"`

* `p[3] = '*'`, star:

  * Skip `"a*"`: `dp[2][4] = dp[2][2] = false`.
  * Use `"a*"` if `s[1]` matches `'a'`:

    * `x = p[2] = 'a'`, `s[1] = 'a'` → match,
    * `dp[2][4] = dp[2][4] || dp[1][4] = false || true = true`.

So `dp[2][4] = true`.

#### j = 5: `"c*a*b"`

* `p[4] = 'b'`, normal:

  * `firstMatch = (s[1] == 'b'?)` → `'a' == 'b'`? no → `false`
  * `dp[2][5] = false`.

Row `i = 2`:

| i\j | 0     | 1     | 2     | 3     | 4    | 5     |
| --- | ----- | ----- | ----- | ----- | ---- | ----- |
| 0   | true  | false | true  | false | true | false |
| 1   | false | false | false | true  | true | false |
| 2   | false | false | false | false | true | false |

---

### 5.5. Row i = 3 (s prefix `"aab"`)

Now `s[0..2] = "aab"`.

#### j = 1: `"c"`

* `p[0] = 'c'`, normal:

  * `firstMatch = ('b' == 'c'?)` → false
  * `dp[3][1] = false`.

#### j = 2: `"c*"`

* `p[1] = '*'`, star:

  * Skip `"c*"`: `dp[3][2] = dp[3][0]` → `false`.
  * Use: `'c'` vs `'b'` → no match.
  * So `dp[3][2] = false`.

#### j = 3: `"c*a"`

* `p[2] = 'a'`, normal:

  * `firstMatch = ('b' == 'a'?)` → false
  * `dp[3][3] = false`.

#### j = 4: `"c*a*"`

* `p[3] = '*'`, star:

  * Skip `"a*"`: `dp[3][4] = dp[3][2] = false`.
  * Use `"a*"`: `x = 'a'` vs `s[2] = 'b'` → no match.
  * So `dp[3][4] = false`.

#### j = 5: `"c*a*b"`

* `p[4] = 'b'`, normal:

  * `firstMatch = ('b' == 'b'?)` → true
  * `dp[3][5] = true && dp[2][4]`.

We know from row 2 that `dp[2][4] = true`.

So:

* `dp[3][5] = true && true = true`.

Final row `i = 3`:

| i\j | 0     | 1     | 2     | 3     | 4     | 5     |
| --- | ----- | ----- | ----- | ----- | ----- | ----- |
| 0   | true  | false | true  | false | true  | false |
| 1   | false | false | false | true  | true  | false |
| 2   | false | false | false | false | true  | false |
| 3   | false | false | false | false | false | true  |

Our answer is `dp[3][5] = true`, so `"aab"` matches `"c*a*b"`.

---

## 6. How this mirrors the top-down version

* Top-down: `dp(i, j)` = does `s[i:]` match `p[j:]`?
* Bottom-up: `dp[i][j]` = does `s[0..i-1]` match `p[0..j-1]`?

They’re symmetric, just indexing from the other side:

* Top-down star case: `dp(i, j) = dp(i, j+2) || (firstMatch && dp(i+1, j))`
* Bottom-up star case: `dp[i][j] = dp[i][j-2] || (firstMatch && dp[i-1][j])`

The logic is identical, just flipped.
*/

// class Solution {
//     public boolean isMatch(String s, String p) {
//         int m = s.length();
//         int n = p.length();

//         boolean[][] dp = new boolean[m + 1][n + 1];

//         // 1) Empty string vs empty pattern
//         dp[0][0] = true;

//         // 2) Empty string vs pattern prefix
//         // Patterns like a*, a*b*, a*b*c* can match empty string
//         for (int j = 2; j <= n; j++) {
//             if (p.charAt(j - 1) == '*' && dp[0][j - 2]) {
//                 dp[0][j] = true;
//             }
//         }

//         // 3) Fill the rest of the table
//         for (int i = 1; i <= m; i++) {
//             for (int j = 1; j <= n; j++) {
//                 char pj = p.charAt(j - 1);

//                 if (pj != '*') {
//                     // Normal character or '.'
//                     boolean firstMatch = (s.charAt(i - 1) == pj || pj == '.');
//                     dp[i][j] = firstMatch && dp[i - 1][j - 1];
//                 } else {
//                     // pj == '*' => pattern is x* where x = p[j-2]
//                     // Option 1: skip "x*" (0 occurrence)
//                     dp[i][j] = dp[i][j - 2];

//                     // Option 2: use "x*" to match s[i-1] (1+ occurrences)
//                     char x = p.charAt(j - 2);
//                     if (s.charAt(i - 1) == x || x == '.') {
//                         dp[i][j] = dp[i][j] || dp[i - 1][j];
//                     }
//                 }
//             }
//         }

//         return dp[m][n];
//     }
// }
