// Method 1: Top-Down 2D DP
/*
### Minor notes (not bugs but worth knowing)

* `if (n == 1) return 1;` is unnecessary: the DP will handle this case correctly anyway, but it doesn’t hurt.
* `if (l > r) return false;` is never actually used with your current recurrence (you never call `dp` with `l > r`), so it’s basically dead code. Still not harmful.
* You don’t memoize base cases (length 1, length 2 palindromes). That’s okay; they’re cheap to recompute.


## 3. What does this top-down DP do?

### The DP state

`dp(l, r)` means:

> “Is the substring `s[l..r]` (inclusive) a palindrome?”

We define it recursively:

* **Base cases:**

  * If `l == r`: single character → always palindrome.
  * If `r == l + 1` and `s[l] == s[r]`: two equal chars → palindrome (like `"aa"`).

* **Recursive case (length ≥ 3):**

  * For `s[l..r]` to be a palindrome:

    1. The first and last chars must match: `s[l] == s[r]`.
    2. The inner substring `s[l+1..r-1]` must also be a palindrome: `dp(l+1, r-1) == true`.

  So:

  ```text
  dp(l, r) = (s[l] == s[r]) && dp(l+1, r-1)
  ```

We store answers in `memo[l][r]` to avoid recomputing.

### The outer loops

```java
for (int l = 0; l < n; l++) {
    for (int r = l; r < n; r++) {
        if (dp(s, memo, l, r)) {
            count++;
        }
    }
}
```

This enumerates **every substring** `s[l..r]` exactly once.
For each one, we ask: “is it palindrome?”

* If yes → increment `count`.

Because of memoization, each `(l, r)` pair’s palindrome result is computed at most once, so the total complexity is O(n²).

---

## 4. Detailed example walkthrough: `s = "aaa"`

This is a good test case, because it has many overlapping palindromes.

```text
s = "aaa"
n = 3
indexes: 0 1 2
         a a a
```

All palindromic substrings:

* `"a"` (0)
* `"a"` (1)
* `"a"` (2)
* `"aa"` (0–1)
* `"aa"` (1–2)
* `"aaa"` (0–2)

Total = **6**.

We’ll see if the algorithm counts 6.

### Outer loops: all (l, r)

Pairs (l, r) with l ≤ r:

1. (0, 0) → `"a"`
2. (0, 1) → `"aa"`
3. (0, 2) → `"aaa"`
4. (1, 1) → `"a"`
5. (1, 2) → `"aa"`
6. (2, 2) → `"a"`

We’ll go one by one.

---

### 4.1. (l = 0, r = 0) → `"a"`

Call `dp("aaa", memo, 0, 0)`:

* `l > r`? 0 > 0 → no
* `r == l`? 0 == 0 → yes

So:

```java
return true;
```

Back in outer loop:

* `if (dp(...))` → true → `count++`

Now `count = 1`.

---

### 4.2. (l = 0, r = 1) → `"aa"`

Call `dp("aaa", memo, 0, 1)`:

* `l > r`? 0 > 1 → no
* `r == l`? 1 == 0 → no
* `r == l + 1 && s[0] == s[1]`?

  * `r == l + 1` → 1 == 0 + 1 → true
  * `s[0] == s[1]` → `'a' == 'a'` → true

So base case for length-2 palindrome hits:

```java
return true;
```

Back in outer loop:

* `count++` → `count = 2`.

---

### 4.3. (l = 0, r = 2) → `"aaa"`

Call `dp("aaa", memo, 0, 2)`:

* `l > r`? 0 > 2 → no
* `r == l`? 2 == 0 → no
* `r == l + 1 && ...`? 2 == 1 → no (so not a length-2 base case)
* `memo[0][2]` null? Yes (fresh memo), so compute recursively.

Check:

```java
if (s.charAt(l) == s.charAt(r) && dp(s, memo, l+1, r-1)) {
    ...
}
```

* `s[0] == s[2]` → `'a' == 'a'` → true
* Need `dp(s, memo, 1, 1)`.

#### Inside dp(1, 1) → `"a"`

Call `dp("aaa", memo, 1, 1)`:

* `l > r`? 1 > 1 → no
* `r == l`? 1 == 1 → yes → return `true`.

Back to `dp(0, 2)`:

* Condition is: `s[0] == s[2]` (true) AND `dp(1,1)` (true) → `isPalindrome = true`.

So:

```java
memo[0][2] = true;
return true;
```

Back in outer loop:

* `count++` → `count = 3`.

So far we have counted:

* (0,0): "a"
* (0,1): "aa"
* (0,2): "aaa"

---

### 4.4. (l = 1, r = 1) → `"a"`

Call `dp("aaa", memo, 1, 1)`:

* Same as before: length 1 substring → base case → `true`.

Back in outer loop:

* `count++` → `count = 4`.

---

### 4.5. (l = 1, r = 2) → `"aa"`

Call `dp("aaa", memo, 1, 2)`:

* `l > r`? 1 > 2 → no
* `r == l`? 2 == 1 → no
* `r == l + 1 && s[1] == s[2]`?

  * `2 == 2` → true
  * `s[1] == s[2]` → `'a' == 'a'` → true
    → return `true`.

Back in outer loop:

* `count++` → `count = 5`.

---

### 4.6. (l = 2, r = 2) → `"a"`

Call `dp("aaa", memo, 2, 2)`:

* `r == l` → true → return `true`.

Back in outer loop:

* `count++` → `count = 6`.

Now all `(l, r)` pairs are done. Final result:

```text
count = 6
```

Which matches the true number of palindromic substrings in `"aaa"`.

---

## 5. Complexity

* There are `O(n²)` substrings `(l, r)`.
* Each substring’s “is palindrome” result is computed at most once because of `memo`.
* Each `dp` call does O(1) work plus up to one recursive call.

So:

* **Time:** O(n²)
* **Space:** O(n²) for the `memo` table.
*/
class Solution {
    public int countSubstrings(String s) {
        int n = s.length();
        if(n == 1){
            return 1;
        }
        
        Boolean[][] memo = new Boolean[n][n];
        int count = 0;

        for(int l=0; l<n; l++){
            for(int r=l; r<n; r++){
                if(dp(s, memo, l, r)){
                    count++;
                }
            }
        }

        return count;
    }

    private boolean dp(String s, Boolean[][] memo, int l, int r){
        if(l > r){
            return false;
        }
        if(r == l || (r == l+1 && s.charAt(l) == s.charAt(r))){
            return true;
        }

        if(memo[l][r] != null){
            return memo[l][r];
        }

        boolean isPalindrome = false;
        if(s.charAt(l) == s.charAt(r) && dp(s, memo, l+1, r-1)){
            isPalindrome = true;
        }

        memo[l][r] = isPalindrome;

        return memo[l][r];
    }
}








// Method 2: Bottom-Up Approach
/*
## 1. DP idea and state

We’re counting **how many substrings are palindromes**.

Natural DP state:

> `dp[l][r]` = `true` if the substring `s[l..r]` (inclusive) is a palindrome, else `false`.

String length = `n`. Valid indices: `0..n-1`.

We want to fill a `boolean[][] dp` of size `n x n` and count how many `dp[l][r]` are true.

---

## 2. Recurrence: when is `s[l..r]` a palindrome?

`dp[l][r]` should be `true` if **both**:

1. End characters match: `s.charAt(l) == s.charAt(r)`
2. The "inside" substring is a palindrome (or there is no inside).

Break it into cases by length:

* **Length 1** (`l == r`):
  Any single character is a palindrome.

  ```java
  dp[l][r] = true;
  ```

* **Length 2** (`r == l + 1`):
  Two-character string like `"aa"` or `"ab"`. It’s a palindrome if both chars are equal:

  ```java
  dp[l][r] = (s.charAt(l) == s.charAt(r));
  ```

* **Length ≥ 3**:
  `s[l..r]` is a palindrome if:

  * the ends match: `s[l] == s[r]`, and
  * the middle is also a palindrome: `dp[l+1][r-1] == true`

  ```java
  if (s.charAt(l) == s.charAt(r) && dp[l+1][r-1]) {
      dp[l][r] = true;
  }
  ```

So in summary:

```text
dp[l][r] is true if:
  - l == r, or
  - r == l + 1 and s[l] == s[r], or
  - s[l] == s[r] and dp[l+1][r-1] is true
```

Every time we set `dp[l][r] = true`, that substring is a palindromic substring, so we increment `count`.

---

## 3. Bottom-up filling order

We need to ensure when we compute `dp[l][r]`, the entry `dp[l+1][r-1]` is already known. Notice `s[l+1..r-1]` is **shorter** than `s[l..r]`.

So we fill `dp` in order of **increasing substring length**:

1. Fill all length-1 palindromes where `l == r`.
2. Then length-2 substrings.
3. Then length-3, length-4, …, up to length `n`.

Loop structure:

```java
// length 1: l == r
for (int i = 0; i < n; i++) {
    dp[i][i] = true;
    count++;
}

// lengths from 2 to n
for (int len = 2; len <= n; len++) {
    for (int l = 0; l + len - 1 < n; l++) {
        int r = l + len - 1;
        // use the recurrence
    }
}
```

This guarantees that when we handle a substring of length `len`, we’ve already handled all substrings of length `< len`, including `dp[l+1][r-1]` (which has length `len - 2`).



## 5. Detailed walkthrough on `"aaa"`

Let’s go step by step.

```text
s = "aaa"
n = 3
indices: 0 1 2
         a a a
```

All palindromic substrings (so we know what to expect):

1. `"a"` at 0
2. `"a"` at 1
3. `"a"` at 2
4. `"aa"` at 0–1
5. `"aa"` at 1–2
6. `"aaa"` at 0–2

Total = 6.

We’ll see if DP gives `count = 6`.

### Step 1: length 1 substrings

Loop:

```java
for (int i = 0; i < n; i++) {
    dp[i][i] = true;
    count++;
}
```

* i = 0:

  * dp[0][0] = true (substring `"a"`)
  * count = 1
* i = 1:

  * dp[1][1] = true (substring `"a"`)
  * count = 2
* i = 2:

  * dp[2][2] = true (substring `"a"`)
  * count = 3

So far we counted all 3 single-character palindromes.

`dp` matrix (T = true, F = false, blank = false):

```
      0    1    2   (r)
    -------------------
0 |  T    .    .
1 |  .    T    .
2 |  .    .    T
(l)
```

`count = 3`.

---

### Step 2: length 2 substrings (len = 2)

Now:

```java
for (int len = 2; len <= 3; len++) {
    // ...
}
```

First `len = 2`.

Inner loop:

```java
for (int l = 0; l + len - 1 < n; l++) {
    int r = l + len - 1;
}
```

For `len = 2`, `l + 1 < 3` → `l <= 1`. So:

* `l = 0`, `r = 1`
* `l = 1`, `r = 2`

#### Case: (l = 0, r = 1) → substring `"aa"`

Check:

```java
if (s.charAt(0) == s.charAt(1)) {
    if (len == 2 || dp[1][0]) {
        dp[0][1] = true;
        count++;
    }
}
```

* `s[0] == s[1]` → `'a' == 'a'` → true.
* `len == 2` → true → no need to look at `dp[1][0]`.

So:

* `dp[0][1] = true`
* `count++` → `count = 4`

Now `dp`:

```
      0    1    2
    ----------------
0 |  T    T    .
1 |  .    T    .
2 |  .    .    T
```

#### Case: (l = 1, r = 2) → substring `"aa"`

Similarly:

* `s[1] == s[2]` → `'a' == 'a'` → true.
* `len == 2` → true.

So:

* `dp[1][2] = true`
* `count++` → `count = 5`

Updated `dp`:

```
      0    1    2
    ----------------
0 |  T    T    .
1 |  .    T    T
2 |  .    .    T
```

We’ve now counted both `"aa"` substrings: indices (0,1) and (1,2).

---

### Step 3: length 3 substrings (len = 3)

Now `len = 3`.

Condition `l + len - 1 < n` → `l + 2 < 3` → `l <= 0`.
So only `l = 0`, `r = 2`.

Substring `"aaa"`.

Check:

```java
if (s.charAt(0) == s.charAt(2)) {
    if (len == 2 || dp[1][1]) {
        dp[0][2] = true;
        count++;
    }
}
```

* `s[0] == s[2]` → `'a' == 'a'` → true.
* `len == 2`? No, len = 3.
* So we check `dp[1][1]`, which corresponds to substring `"a"`.

We already set `dp[1][1] = true` in the length-1 step.

So:

* `dp[1][1]` is true → thus `dp[0][2] = true`.
* `count++` → `count = 6`.

Final `dp`:

```
      0    1    2
    ----------------
0 |  T    T    T
1 |  .    T    T
2 |  .    .    T
```

Every `T` corresponds to a palindromic substring, and we counted exactly 6 of them:

* (0,0): "a"
* (1,1): "a"
* (2,2): "a"
* (0,1): "aa"
* (1,2): "aa"
* (0,2): "aaa"

Matches our expectation.

---

## 6. Quick walkthrough on `"ababa"`

Now a slightly more interesting string:

```text
s = "ababa"
n = 5
idx: 0 1 2 3 4
     a b a b a
```

Let’s just look at the structure (not every single step) to see the DP pattern.

### Length 1

All single-character substrings are palindromes:

* (0,0): "a"
* (1,1): "b"
* (2,2): "a"
* (3,3): "b"
* (4,4): "a"

So `dp[i][i] = true` for i = 0..4, `count = 5`.

### Length 2

Check pairs:

* (0,1): "ab" → ends differ → false
* (1,2): "ba" → ends differ → false
* (2,3): "ab" → false
* (3,4): "ba" → false

So no length-2 palindromes here.

### Length 3

Check `len = 3`:

* (0,2): "aba"

  * ends 'a' and 'a' match
  * inside is dp[1][1] = true
    → dp[0][2] = true, count++

* (1,3): "bab"

  * ends 'b' and 'b' match
  * inside is dp[2][2] = true
    → dp[1][3] = true, count++

* (2,4): "aba"

  * ends 'a' and 'a' match
  * inside is dp[3][3] = true
    → dp[2][4] = true, count++

So we add 3 palindromes of length 3.

### Length 4

Check `len = 4`:

* (0,3): "abab"

  * ends 'a' and 'b' differ → false
* (1,4): "baba"

  * ends 'b' and 'a' differ → false

No length-4 palindromes.

### Length 5

Check `len = 5`:

* (0,4): "ababa"

  * ends 'a' and 'a' match
  * inside dp[1][3] (substring "bab") is true
    → dp[0][4] = true, count++

So we add 1 palindrome of length 5.

Final palindromes:

* length 1: 5
* length 3: 3 ("aba", "bab", "aba")
* length 5: 1 ("ababa")

Total = 9 palindromic substrings.

---

## Wrap-up

**Bottom-up 2D DP for 647:**

* `dp[l][r]` = whether `s[l..r]` is a palindrome.
* Fill by increasing substring length: 1 → 2 → ... → n.
* Use recurrence:

  * length 1: always true
  * length 2: true if chars equal
  * length ≥ 3: true if ends match and `dp[l+1][r-1]` is true.
* Count every time you set `dp[l][r] = true`.
*/

// class Solution {
//     public int countSubstrings(String s) {
//         int n = s.length();
//         if(n == 1){
//             return 1;
//         }

//         boolean[][] memo = new boolean[n][n];
//         int count = 0;

//         for(int i=0; i<n; i++){
//             memo[i][i] = true;
//             count++;
//         }

//         for(int len=2; len<=n; len++){
//             for(int l=0; l + len - 1 < n; l++){
//                 int r = l + len - 1;

//                 if(s.charAt(l) == s.charAt(r)){
//                     if(len == 2 || memo[l+1][r-1]){
//                         memo[l][r] = true;
//                         count++;
//                     }
//                 }
//             }
//         }

//         return count;
//     }
// }









// Method 3: Expanding around center O(n²) time, O(n) space
/*
## 1. Core idea

Every palindrome is defined by its **center**.

* Odd-length palindrome (like `"aba"`, `"racecar"`):

  * Center is at a **single character**. Example: `"aba"` centered at `'b'`.

* Even-length palindrome (like `"aa"`, `"abba"`):

  * Center is **between** two characters.

    * `"abba"` is centered between the two `b`’s.

So the trick is:

> For every possible center, expand pointers `L` and `R` outward as long as `s[L] == s[R]`.
> Every time they match, we’ve found a palindromic substring.

We just **count** how many we find.

Number of centers for a string of length `n`:

* `n` odd centers (each index 0..n-1)
* `n - 1` even centers (between i and i+1)
  → total = `2n - 1` centers.

---

## 2. How to implement (odd + even centers)

We loop over each possible *index* `i` and use it as:

* An **odd center**: `L = i`, `R = i`
* An **even center**: `L = i`, `R = i + 1`

For each `(L, R)` pair, we expand as long as:

* `L >= 0`
* `R < n`
* `s.charAt(L) == s.charAt(R)`

And we do:

* `count++` for each match
* then `L--`, `R++`

We don’t need any memo table; this is completely local.



**Complexity:**

* For each of `2n - 1` centers, we expand at most O(n) steps.
* So overall time: O(n²).
* Extra space: O(1).

---

## 4. Thorough walkthrough on `"aaa"`

Let’s really trace it.

```text
s = "aaa"
n = 3
index: 0 1 2
       a a a
```

We know by hand all palindromic substrings:

1. `"a"` (0)
2. `"a"` (1)
3. `"a"` (2)
4. `"aa"` (0–1)
5. `"aa"` (1–2)
6. `"aaa"` (0–2)

Total expected = **6**.

We’ll see how the algorithm gets 6.

---

### Initialize

```java
int count = 0;
for (int center = 0; center < 3; center++) {
    count += expandFromCenter(s, center, center);     // odd
    count += expandFromCenter(s, center, center + 1); // even
}
```

We’ll go center by center: 0, 1, 2.

---

### Center = 0

#### 4.1. Odd center at index 0 → expandFromCenter(s, 0, 0)

`left = 0`, `right = 0`

While condition:

* `left >= 0` → 0 >= 0 → true
* `right < 3` → 0 < 3 → true
* `s[0] == s[0]` → `'a' == 'a'` → true

So inside loop:

* `cnt++` → from 0 to 1
  → Palindrome found: `s[0..0] = "a"`
* Move outward: `left--` → -1, `right++` → 1

Next iteration:

* `left >= 0`? -1 >= 0 → false → stop.

So `expandFromCenter(s, 0, 0)` returns `1`.

We add that to `count`:

* `count = 0 + 1 = 1`
  So far we counted `"a"` at index 0.

---

#### 4.2. Even center between 0 and 1 → expandFromCenter(s, 0, 1)

`left = 0`, `right = 1`

While condition:

* `left >= 0` → 0 >= 0 → true
* `right < 3` → 1 < 3 → true
* `s[0] == s[1]` → `'a' == 'a'` → true

Inside loop:

* `cnt++` → from 0 to 1
  → Palindrome: `s[0..1] = "aa"`
* Move outward: `left--` → -1, `right++` → 2

Next iteration:

* `left >= 0`? -1 >= 0 → false → stop.

So `expandFromCenter(s, 0, 1)` returns `1`.

Add to `count`:

* `count = 1 + 1 = 2`

So after center=0, we’ve found:

* `"a"` (0)
* `"aa"` (0–1)

---

### Center = 1

#### 4.3. Odd center at index 1 → expandFromCenter(s, 1, 1)

`left = 1`, `right = 1`

Check:

* `1 >= 0` → true
* `1 < 3` → true
* `s[1] == s[1]` → `'a' == 'a'` → true

Inside:

* `cnt++` → 0 → 1
  → Palindrome: `"a"` (1)
* Move: `left = 0`, `right = 2`

Next iteration:

* `0 >= 0` → true
* `2 < 3` → true
* `s[0] == s[2]` → `'a' == 'a'` → true

Inside:

* `cnt++` → 1 → 2
  → Palindrome: `s[0..2] = "aaa"`
* Move: `left = -1`, `right = 3`

Next iteration:

* `left >= 0`? -1 >= 0 → false → stop.

So `expandFromCenter(s, 1, 1)` returns `2`.

Add to `count`:

* `count = 2 + 2 = 4`

Palindromes added:

* `"a"` (1)
* `"aaa"` (0–2)

---

#### 4.4. Even center between 1 and 2 → expandFromCenter(s, 1, 2)

`left = 1`, `right = 2`

Check:

* `1 >= 0` → true
* `2 < 3` → true
* `s[1] == s[2]` → `'a' == 'a'` → true

Inside:

* `cnt++` → 0 → 1
  → Palindrome: `"aa"` (1–2)
* Move: `left = 0`, `right = 3`

Next iteration:

* `right < 3`? 3 < 3 → false → stop.

So `expandFromCenter(s, 1, 2)` returns `1`.

Add to `count`:

* `count = 4 + 1 = 5`

So after center=1, total palindromes found:

* Previous: `"a"`(0), `"aa"`(0–1)
* New: `"a"`(1), `"aaa"`(0–2), `"aa"`(1–2)

---

### Center = 2

#### 4.5. Odd center at index 2 → expandFromCenter(s, 2, 2)

`left = 2`, `right = 2`

Check:

* `2 >= 0` → true
* `2 < 3` → true
* `s[2] == s[2]` → `'a' == 'a'` → true

Inside:

* `cnt++` → 0 → 1
  → Palindrome: `"a"` (2)
* Move: `left = 1`, `right = 3`

Next iteration:

* `right < 3`? 3 < 3 → false → stop.

So `expandFromCenter(s, 2, 2)` returns `1`.

Add to `count`:

* `count = 5 + 1 = 6`

#### 4.6 Even center between 2 and 3 → expandFromCenter(s, 2, 3)

`left = 2`, `right = 3`

Check:

* `right < 3`? 3 < 3 → false

Loop never runs, returns `0`.
`count` stays `6`.

---

### Final result

Total `count = 6`, which matches exactly:

* `"a"` (0)
* `"a"` (1)
* `"a"` (2)
* `"aa"` (0–1)
* `"aa"` (1–2)
* `"aaa"` (0–2)

---

## 5. Quick check on `"ababa"` (to see odd/even behavior)

```text
s = "ababa"
idx: 0 1 2 3 4
     a b a b a
```

Palindromic substrings (not exhaustive listing by hand, but some):

* Single chars: `"a","b","a","b","a"` → 5
* 3-length: `"aba"(0–2), "bab"(1–3), "aba"(2–4)` → 3
* 5-length: `"ababa"(0–4)` → 1
  Total = 9 palindromes (you’d also check to confirm no 2-length or 4-length palindromes).

The center-expansion will:

* At each index:

  * odd: find `"a"`, `"aba"`, `"ababa"` depending on center
  * even: usually fail because adjacent chars differ (`"ab"`, `"ba"`)

The final count will be 9, matching expectations.

---

## Summary

**Expand-around-center** approach for 647:

* Treat each index as a potential center for odd palindromes, and each gap as a center for even palindromes.
* For each center (L,R), expand outward while characters match.
* Each successful expansion is one palindromic substring.
* Time: O(n²), Space: O(1), and usually much simpler to code and think about than full DP.
*/
// class Solution {
//     public int countSubstrings(String s) {
//         int n = s.length();
//         if(n == 1){
//             return 1;
//         }

//         int count = 0;

//         for(int i=0; i<n; i++){
//             count += expandFromCenter(s, i, i);

//             count += expandFromCenter(s, i, i+1);
//         }

//         return count;
//     }


//     private int expandFromCenter(String s, int l, int r){
//         int n = s.length();
//         int cnt = 0;

//         while(l >= 0 && r < n && s.charAt(l) == s.charAt(r)){
//             cnt++;
//             l--;
//             r++;
//         }

//         return cnt;
//     }
// }