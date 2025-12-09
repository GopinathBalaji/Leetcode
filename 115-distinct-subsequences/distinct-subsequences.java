// Method 1: Top-Down 2D DP
/*
## 1. Problem in your own words

We want:

> How many distinct subsequences of `s` are exactly equal to `t`?

A **subsequence** means we can delete characters from `s` (possibly none) without changing the order of the remaining characters.

So each **choice of indices** in `s` that read as `t` (in order) counts as one way.

---

## 2. Top-down DP idea

We’ll use recursion with memoization.

### State definition

Let:

> `dp(i, j)` = number of distinct subsequences of **`s[i..]`** (suffix starting at `i`)
> that equal **`t[j..]`** (suffix starting at `j`).

That is: starting at `s[i]` and `t[j]`, how many ways can we continue to match the rest of `t` from the rest of `s`?

We want:

```java
dp(0, 0)
```

because initially we are at the start of both strings.

Let:

```java
m = s.length(), n = t.length()
```

Indices:

* `i` ranges from `0..m`
* `j` ranges from `0..n`

---

### Base cases

Think about when one of the strings is “exhausted”:

1. **If we’ve matched all of `t` (i.e., j == n)**

We’ve successfully matched every character of `t`.

* There is **exactly 1** way to do that: the current sequence of choices we’ve already made.
* Any remaining characters in `s` can just be ignored.

So:

```java
if (j == n) return 1;
```

2. **If we’ve exhausted `s` but still have characters in `t` (i == m and j < n)**

We have no characters left in `s`, but we still need to match some of `t`. Impossible.

So:

```java
if (i == m) return 0;
```

These two base cases are the **only** ones we need.

---

### Transition (the main recurrence)

At state `dp(i, j)`, we are looking at `s[i]` and `t[j]`.

Two situations:

#### Case 1: Characters don’t match

If `s.charAt(i) != t.charAt(j)`, then we **cannot** use `s[i]` to match `t[j]`. Our only option is to skip this character of `s`:

```java
dp(i, j) = dp(i + 1, j);
```

We move forward in `s` but not in `t`.

---

#### Case 2: Characters match

If `s.charAt(i) == t.charAt(j)`, we have **two choices**:

1. **Use** `s[i]` to match `t[j]`:

   * Then we need to match `t[j+1..]` from `s[i+1..]`:

   ```java
   use = dp(i + 1, j + 1);
   ```

2. **Skip** `s[i]`:

   * Maybe this occurrence isn’t the one we want; we look for another potential match later:

   ```java
   skip = dp(i + 1, j);
   ```

Total ways:

```java
dp(i, j) = use + skip;
```

So overall:

```java
if (s[i] == t[j]):
    dp(i, j) = dp(i + 1, j + 1) + dp(i + 1, j);
else:
    dp(i, j) = dp(i + 1, j);
```

---

### Memoization

We’d recompute the same `(i, j)` states many times without memoization. So we store the result in a 2D array:

```java
Integer[][] memo = new Integer[m][n];
```

* `memo[i][j] == null` means “not computed yet”.
* Otherwise it stores the number of ways.

Pattern:

```java
if (memo[i][j] != null) return memo[i][j];

int val = ... // compute using recurrence

memo[i][j] = val;
return val;
```


Time complexity: `O(m * n)`
Space complexity: `O(m * n)` for `memo` + `O(m + n)` recursion stack in worst case.

---

## 4. Thorough example walkthrough: `s = "babgbag"`, `t = "bag"`

We know from the problem statement the answer should be **5**.

Positions in `s` (0-based):

```text
s = b   a   b   g   b   a   g
    0   1   2   3   4   5   6

t = b   a   g
    0   1   2
```

We’re computing:

```text
dp(0, 0) = # ways to form "bag" from "babgbag"
```

### 4.1. Intuition with indices

To make `"bag"` from `"babgbag"`, we need:

* A `'b'` somewhere,
* followed by an `'a'` later,
* followed by a `'g'` later.

The 5 valid choices of indices are (b,a,g):

1. (0, 1, 3)
2. (0, 1, 6)
3. (0, 5, 6)
4. (2, 5, 6)
5. (4, 5, 6)

Our DP will count these 5 combinations.

---

### 4.2. Understanding `dp(i, j)` as a table

Let’s tabulate `dp(i, j)` values (computed by the recursion + memo).
We’ll show them (precomputed) to make the explanation concrete:

`i` goes from 0..7 (`i = 7` means s[i:] = `""`).
`j` goes from 0..3 (`j = 3` means t[j:] = `""`).

Here is the table `dp(i, j)`:

```text
         j=0   j=1   j=2   j=3
i=0      5     3     2     1
i=1      2     3     2     1
i=2      2     1     2     1
i=3      1     1     2     1
i=4      1     1     1     1
i=5      0     1     1     1
i=6      0     0     1     1
i=7      0     0     0     1
```

Remember:

* `dp(i, j)` = ways to form `t[j..]` from `s[i..]`.
* Column `j=3` (rightmost) is always `1`, because `t[3..]` is the empty string — there is exactly 1 way to form it (by choosing nothing), no matter what `i` is.
* Row `i=7` (bottom) is all zero except `dp(7,3) = 1`, because `s[7..]` is empty and:

  * we can only match empty `t` from empty `s`.

Let’s see how some of these are computed via the recurrence.

---

### 4.3. Start from the base row: `i = 7`

`s[7..]` is empty string.

For any `j`:

* `j = 3` (t[3..] = ""): `dp(7,3) = 1` (empty t from empty s).
* `j = 0,1,2`: `dp(7,j) = 0` (can’t form non-empty t from empty s).

So bottom row:

```text
i=7:  dp(7,0)=0, dp(7,1)=0, dp(7,2)=0, dp(7,3)=1
```

---

### 4.4. Row `i = 6`: s[6] = 'g'

Now consider `s[6] = 'g'`.

We compute `dp(6, j)`:

#### `dp(6,2)`: match `t[2..] = "g"` from `s[6..] = "g"`

* `s[6] == 'g'`, `t[2] == 'g'` → characters match.

So:

```text
dp(6,2) = dp(7,3) + dp(7,2)
        = 1       + 0
        = 1
```

Interpretation: from `s[6..] = "g"`, there is **exactly 1** subsequence equal to `"g"`: pick this 'g'.

#### `dp(6,1)`: match `t[1..] = "ag"` from `"g"`

* `s[6] = 'g'`, `t[1] = 'a'` → mismatch.

So:

```text
dp(6,1) = dp(7,1) = 0
```

No way to match `"ag"` from a single 'g'.

#### `dp(6,0)`: match `t[0..] = "bag"` from `"g"`

* `s[6] = 'g'`, `t[0] = 'b'` → mismatch.

So:

```text
dp(6,0) = dp(7,0) = 0
```

Row `i=6`:

```text
dp(6,0)=0, dp(6,1)=0, dp(6,2)=1, dp(6,3)=1
```

(`dp(6,3)=1` from base: empty t.)

---

### 4.5. Row `i = 5`: s[5] = 'a'

`s[5..] = "ag"`.

We compute:

#### `dp(5,2)`: match "g" from "ag"

* `s[5] = 'a'`, `t[2] = 'g'` → mismatch:

```text
dp(5,2) = dp(6,2) = 1
```

(We skip the 'a' and rely on the "g" at i=6.)

#### `dp(5,1)`: match "ag" from "ag"

* `s[5] = 'a'`, `t[1] = 'a'` → match:

We can:

1. Use this 'a', then match "g" from "g":

   ```text
   use  = dp(6, 2) = 1
   ```

2. Skip this 'a' and try to match "ag" from "g":

   ```text
   skip = dp(6, 1) = 0
   ```

So:

```text
dp(5,1) = use + skip = 1 + 0 = 1
```

One way: use 'a' at i=5, then 'g' at i=6.

#### `dp(5,0)`: match "bag" from "ag"

* `s[5] = 'a'`, `t[0] = 'b'` → mismatch:

```text
dp(5,0) = dp(6,0) = 0
```

Row `i=5`:

```text
dp(5,0)=0, dp(5,1)=1, dp(5,2)=1, dp(5,3)=1
```

---

### 4.6. Row `i = 4`: s[4] = 'b'

`s[4..] = "bag"`.

We compute:

#### `dp(4,2)`: match "g" from "bag"

* `s[4] = 'b'`, `t[2] = 'g'` → mismatch:

```text
dp(4,2) = dp(5,2) = 1
```

(So 1 way: 'g' at i=6.)

#### `dp(4,1)`: match "ag" from "bag"

* `s[4] = 'b'`, `t[1] = 'a'` → mismatch:

```text
dp(4,1) = dp(5,1) = 1
```

(Still 1 way: 'a' at i=5, 'g' at i=6.)

#### `dp(4,0)`: match "bag" from "bag"

* `s[4] = 'b'`, `t[0] = 'b'` → match:

1. Use this 'b' (i=4), then match `"ag"` from `s[5..]`:

   ```text
   use  = dp(5, 1) = 1
   ```

2. Skip this 'b' and match `"bag"` from `s[5..]`:

   ```text
   skip = dp(5, 0) = 0
   ```

So:

```text
dp(4,0) = use + skip = 1 + 0 = 1
```

This 1 way corresponds to indices `(4,5,6)` (b at 4, a at 5, g at 6).

Row `i=4`:

```text
dp(4,0)=1, dp(4,1)=1, dp(4,2)=1, dp(4,3)=1
```

---

### 4.7. Row `i = 3`: s[3] = 'g'

`s[3..] = "gbag"`.

#### `dp(3,2)`: match "g" from "gbag"

* `s[3] = 'g'`, `t[2] = 'g'` → match:

```text
dp(3,2) = dp(4,3) + dp(4,2)
        = 1       + 1
        = 2
```

Interpretation: from `"gbag"` there are two ways to get `"g"`:

* Use the 'g' at i=3.
* Skip it and use the 'g' at i=6.

#### `dp(3,1)`: match "ag" from "gbag"

* `s[3] = 'g'`, `t[1] = 'a'` → mismatch:

```text
dp(3,1) = dp(4,1) = 1
```

(That one way is still the `(a,g)` at indices `(5,6)`, same as before.)

#### `dp(3,0)`: match "bag" from "gbag"

* `s[3] = 'g'`, `t[0] = 'b'` → mismatch:

```text
dp(3,0) = dp(4,0) = 1
```

Row `i=3`:

```text
dp(3,0)=1, dp(3,1)=1, dp(3,2)=2, dp(3,3)=1
```

---

### 4.8. Row `i = 2`: s[2] = 'b'

`s[2..] = "bgbag"`.

#### `dp(2,2)`: match "g" from "bgbag"

* `s[2] = 'b'`, `t[2] = 'g'` → mismatch:

```text
dp(2,2) = dp(3,2) = 2
```

Two ways from `"gbag"` to get "g".

#### `dp(2,1)`: match "ag" from "bgbag"

* `s[2] = 'b'`, `t[1] = 'a'` → mismatch:

```text
dp(2,1) = dp(3,1) = 1
```

#### `dp(2,0)`: match "bag" from "bgbag"

* `s[2] = 'b'`, `t[0] = 'b'` → match:

```text
use  = dp(3,1) = 1
skip = dp(3,0) = 1
dp(2,0) = use + skip = 1 + 1 = 2
```

Interpretation: there are **2 ways** to make "bag" from `"bgbag"` starting at index 2.

Those correspond to:

* Use `b` at 2 → then we must form `"ag"` from indices 3..6: that ultimately leads to (2,5,6).
* Skip `b` at 2 → match `"bag"` from indices 3..6: that leads to the earlier `(4,5,6)` combination.

Row `i=2`:

```text
dp(2,0)=2, dp(2,1)=1, dp(2,2)=2, dp(2,3)=1
```

---

### 4.9. Row `i = 1`: s[1] = 'a'

`s[1..] = "abgbag"`.

#### `dp(1,2)`: match "g" from "abgbag"

* `s[1] = 'a'`, `t[2] = 'g'` → mismatch:

```text
dp(1,2) = dp(2,2) = 2
```

#### `dp(1,1)`: match "ag" from "abgbag"

* `s[1] = 'a'`, `t[1] = 'a'` → match:

```text
use  = dp(2,2) = 2
skip = dp(2,1) = 1
dp(1,1) = use + skip = 2 + 1 = 3
```

So from `"abgbag"` there are 3 ways to get `"ag"`.

#### `dp(1,0)`: match "bag" from "abgbag"

* `s[1] = 'a'`, `t[0] = 'b'` → mismatch:

```text
dp(1,0) = dp(2,0) = 2
```

Row `i=1`:

```text
dp(1,0)=2, dp(1,1)=3, dp(1,2)=2, dp(1,3)=1
```

---

### 4.10. Row `i = 0`: s[0] = 'b' (the main answer)

Finally:

`s[0..] = "babgbag"`.

#### `dp(0,2)`: match "g" from "babgbag"

* `s[0] = 'b'`, `t[2] = 'g'` → mismatch:

```text
dp(0,2) = dp(1,2) = 2
```

#### `dp(0,1)`: match "ag" from "babgbag"

* `s[0] = 'b'`, `t[1] = 'a'` → mismatch:

```text
dp(0,1) = dp(1,1) = 3
```

#### `dp(0,0)`: match "bag" from "babgbag" — this is what we want.

* `s[0] = 'b'`, `t[0] = 'b'` → match:

```text
use  = dp(1,1) = 3
skip = dp(1,0) = 2
dp(0,0) = use + skip = 3 + 2 = 5
```

So there are **5** distinct subsequences of `"babgbag"` equal to `"bag"`, as expected.

Those 5 correspond exactly to the index triples we listed earlier:

1. (0, 1, 3)
2. (0, 1, 6)
3. (0, 5, 6)
4. (2, 5, 6)
5. (4, 5, 6)

---

## 5. Summary

Top-down DP for Distinct Subsequences:

* **State:** `dp(i, j)` = ways to form `t[j..]` from `s[i..]`.
* **Base cases:**

  * `j == n` → 1 (matched all of `t`),
  * `i == m` → 0 (no `s` left but still need `t`).
* **Transition:**

  * If `s[i] == t[j]`:

    ```text
    dp(i, j) = dp(i+1, j+1) + dp(i+1, j)
    ```
  * Else:

    ```text
    dp(i, j) = dp(i+1, j)
    ```
* **Memoize** `dp(i, j)` to avoid recomputation.
* Answer: `dp(0, 0)`.
*/
class Solution {
    public int numDistinct(String s, String t) {
        int m = s.length();
        int n = t.length();

        if (m < n) {
            return 0;  // impossible to form longer t from shorter s
        }

        Integer[][] memo = new Integer[m][n];
        return dp(s, t, memo, m, n, 0, 0);
    }

    private int dp(String s, String t, Integer[][] memo, int m, int n, int i, int j) {
        // If we've matched all of t, that's 1 valid subsequence
        if (j == n) {
            return 1;
        }

        // If we've exhausted s but still have characters in t, no way
        if (i == m) {
            return 0;
        }

        if (memo[i][j] != null) {
            return memo[i][j];
        }

        int val = 0;
        if (s.charAt(i) != t.charAt(j)) {
            // Can't use s[i] to match t[j], so skip s[i]
            val = dp(s, t, memo, m, n, i + 1, j);
        } else {
            // Option 1: use s[i] to match t[j]
            int use = dp(s, t, memo, m, n, i + 1, j + 1);
            // Option 2: skip s[i]
            int skip = dp(s, t, memo, m, n, i + 1, j);
            val = use + skip;
        }

        memo[i][j] = val;
        return val;
    }
}





// Method 2: Bottom-Up 2D DP
/*
## 1. Problem restated in DP terms

We want:

> Number of **distinct subsequences** of `s` that are exactly equal to `t`.

A subsequence is formed by deleting zero or more characters from `s` **without reordering** the rest.

Example:

```text
s = "babgbag"
t = "bag"
answer = 5
```

Each way is a different choice of indices `(i1 < i2 < i3)` in `s` whose characters spell `"bag"`.

---

## 2. Bottom-up DP: 2D prefix formulation

We’ll define:

> `dp[i][j]` = number of distinct subsequences of **`s[0..i-1]`**
> that equal **`t[0..j-1]`**.

So:

* `i` = length of the prefix of `s` we’re using (`0..m`)
* `j` = length of the prefix of `t` we’re matching (`0..n`)

We want `dp[m][n]`.

Let:

```text
m = s.length()
n = t.length()
```

### 2.1. Base cases

1. **Empty `t`** (`j = 0`)

There is exactly **1** way to form the empty string from any prefix of `s`: delete everything.

So:

```text
dp[i][0] = 1  for all i in [0..m]
```

2. **Empty `s`** (`i = 0`) and non-empty `t`

We cannot form a non-empty `t` from an empty `s`.

So:

```text
dp[0][j] = 0  for all j in [1..n]
```

These fill the first row and first column of the DP table.

---

### 2.2. Transition (recurrence)

For `i >= 1` and `j >= 1`, look at the last characters:

* `s[i-1]` (last char of the prefix `s[0..i-1]`)
* `t[j-1]` (last char of the prefix `t[0..j-1]`)

#### Case 1: Characters match

If `s[i-1] == t[j-1]`, we have **two options**:

1. **Use** this `s[i-1]` to match `t[j-1]`
   Then we need to form `t[0..j-2]` from `s[0..i-2]`:

   ```text
   dp[i-1][j-1]
   ```

2. **Skip** this `s[i-1]`
   Use only the first `i-1` characters of `s` to form `t[0..j-1]`:

   ```text
   dp[i-1][j]
   ```

Total ways:

```text
dp[i][j] = dp[i-1][j] + dp[i-1][j-1]
```

#### Case 2: Characters don’t match

If `s[i-1] != t[j-1]`, we **cannot** use `s[i-1]` to match `t[j-1]`.
Only option: **skip** `s[i-1]`:

```text
dp[i][j] = dp[i-1][j]
```

So, unified:

```text
dp[i][j] = dp[i-1][j];
if (s[i-1] == t[j-1]) {
    dp[i][j] += dp[i-1][j-1];
}
```

## 4. Thorough example: `s = "babgbag"`, `t = "bag"`

```text
s = "babgbag"
    1 2 3 4 5 6 7 (using 1-based positions here, but DP is 0-based lengths)
t = "bag"
    1 2 3

m = 7, n = 3
```

Remember:

* `dp[i][j]` = # ways to form `t[0..j-1]` from `s[0..i-1]`.

We’ll build a table `dp[0..7][0..3]`.

### 4.1. Initialize base cases

* `dp[i][0] = 1` for all `i` (empty `t`):

```text
i\j   0   1   2   3
---------------------
0     1   0   0   0
1     1
2     1
3     1
4     1
5     1
6     1
7     1
```

(Other cells start as 0.)

### 4.2. Fill row by row

We go i = 1..7, j = 1..3.

I’ll denote `s[i-1]` and `t[j-1]` each time.

---

#### Row i = 1 (using `s[0] = 'b'` → prefix `"b"`)

* `j = 1` (t[0] = `'b'`):

  `s[0] == t[0]`:

  ```text
  dp[1][1] = dp[0][1]       + dp[0][0]
           = 0              + 1
           = 1
  ```

* `j = 2` (we need `"ba"` from `"b"`):

  `s[0] = 'b'`, `t[1] = 'a'` → not equal:

  ```text
  dp[1][2] = dp[0][2] = 0
  ```

* `j = 3` (need `"bag"` from `"b"`):

  `dp[1][3] = dp[0][3] = 0`

Row 1:

```text
i\j   0   1   2   3
---------------------
0     1   0   0   0
1     1   1   0   0
2     1
3     1
4     1
5     1
6     1
7     1
```

---

#### Row i = 2 (using `s[0..1] = "ba"`)

`s[1] = 'a'`.

* `j = 1` (need `"b"` from `"ba"`):

  `s[1] = 'a'`, `t[0] = 'b'` → mismatch:

  ```text
  dp[2][1] = dp[1][1] = 1
  ```

  (We just ignore the 'a', still 1 way: use the 'b' at position 1.)

* `j = 2` (need `"ba"` from `"ba"`):

  `s[1] = 'a'`, `t[1] = 'a'` → match:

  ```text
  dp[2][2] = dp[1][2]       + dp[1][1]
           = 0              + 1
           = 1
  ```

  Interpretation: exactly one subsequence `"ba"`: use 'b' at pos1 and 'a' at pos2.

* `j = 3` (need `"bag"` from `"ba"`):

  `s[1] = 'a'`, `t[2] = 'g'` → mismatch:

  ```text
  dp[2][3] = dp[1][3] = 0
  ```

Row 2:

```text
i\j   0   1   2   3
---------------------
0     1   0   0   0
1     1   1   0   0
2     1   1   1   0
3     1
4     1
5     1
6     1
7     1
```

---

#### Row i = 3 (using `"bab"`)

`s[2] = 'b'`.

* `j = 1` (need `"b"` from `"bab"`):

  `s[2] = 'b'`, `t[0] = 'b'` → match:

  ```text
  dp[3][1] = dp[2][1]       + dp[2][0]
           = 1              + 1
           = 2
  ```

  Interpretation: two ways to get `"b"` from `"bab"`:

  * Take the first 'b' (pos1),
  * Take the third char 'b' (pos3).

* `j = 2` (need `"ba"` from `"bab"`):

  `s[2] = 'b'`, `t[1] = 'a'` → mismatch:

  ```text
  dp[3][2] = dp[2][2] = 1
  ```

  Still only the `"ba"` using positions (1,2).

* `j = 3` (need `"bag"` from `"bab"`):

  `s[2] = 'b'`, `t[2] = 'g'` → mismatch:

  ```text
  dp[3][3] = dp[2][3] = 0
  ```

Row 3:

```text
i\j   0   1   2   3
---------------------
0     1   0   0   0
1     1   1   0   0
2     1   1   1   0
3     1   2   1   0
4     1
5     1
6     1
7     1
```

---

#### Row i = 4 (using `"babg"`)

`s[3] = 'g'`.

* `j = 1` (need `"b"` from `"babg"`):

  `s[3] = 'g'`, `t[0] = 'b'` → mismatch:

  ```text
  dp[4][1] = dp[3][1] = 2
  ```

* `j = 2` (need `"ba"` from `"babg"`):

  `s[3] = 'g'`, `t[1] = 'a'` → mismatch:

  ```text
  dp[4][2] = dp[3][2] = 1
  ```

* `j = 3` (need `"bag"` from `"babg"`):

  `s[3] = 'g'`, `t[2] = 'g'` → match:

  ```text
  dp[4][3] = dp[3][3]       + dp[3][2]
           = 0              + 1
           = 1
  ```

  Interpretation: one way: choose 'b' at pos1, 'a' at pos2, 'g' at pos4 → indices (1,2,4).

Row 4:

```text
i\j   0   1   2   3
---------------------
0     1   0   0   0
1     1   1   0   0
2     1   1   1   0
3     1   2   1   0
4     1   2   1   1
5     1
6     1
7     1
```

---

#### Row i = 5 (using `"babgb"`)

`s[4] = 'b'`.

* `j = 1` (need `"b"`):

  `s[4] = 'b'`, `t[0] = 'b'` → match:

  ```text
  dp[5][1] = dp[4][1]       + dp[4][0]
           = 2              + 1
           = 3
  ```

  Now we have 3 ways to get `"b"` from `"babgb"` (positions 1,3,5).

* `j = 2` (need `"ba"`):

  `s[4] = 'b'`, `t[1] = 'a'` → mismatch:

  ```text
  dp[5][2] = dp[4][2] = 1
  ```

* `j = 3` (need `"bag"`):

  `s[4] = 'b'`, `t[2] = 'g'` → mismatch:

  ```text
  dp[5][3] = dp[4][3] = 1
  ```

Row 5:

```text
i\j   0   1   2   3
---------------------
0     1   0   0   0
1     1   1   0   0
2     1   1   1   0
3     1   2   1   0
4     1   2   1   1
5     1   3   1   1
6     1
7     1
```

---

#### Row i = 6 (using `"babgba"`)

`s[5] = 'a'`.

* `j = 1` (need `"b"`):

  `s[5] = 'a'`, `t[0] = 'b'` → mismatch:

  ```text
  dp[6][1] = dp[5][1] = 3
  ```

* `j = 2` (need `"ba"`):

  `s[5] = 'a'`, `t[1] = 'a'` → match:

  ```text
  dp[6][2] = dp[5][2]       + dp[5][1]
           = 1              + 3
           = 4
  ```

  Those 4 ways correspond to choosing any of the 3 `b`s (positions 1,3,5) with this `a` at pos6.

* `j = 3` (need `"bag"`):

  `s[5] = 'a'`, `t[2] = 'g'` → mismatch:

  ```text
  dp[6][3] = dp[5][3] = 1
  ```

Row 6:

```text
i\j   0   1   2   3
---------------------
0     1   0   0   0
1     1   1   0   0
2     1   1   1   0
3     1   2   1   0
4     1   2   1   1
5     1   3   1   1
6     1   3   4   1
7     1
```

---

#### Row i = 7 (using full `"babgbag"`)

`s[6] = 'g'`.

* `j = 1` (need `"b"`):

  `s[6] = 'g'`, `t[0] = 'b'` → mismatch:

  ```text
  dp[7][1] = dp[6][1] = 3
  ```

* `j = 2` (need `"ba"`):

  `s[6] = 'g'`, `t[1] = 'a'` → mismatch:

  ```text
  dp[7][2] = dp[6][2] = 4
  ```

* `j = 3` (need `"bag"`):

  `s[6] = 'g'`, `t[2] = 'g'` → match:

  ```text
  dp[7][3] = dp[6][3]       + dp[6][2]
           = 1              + 4
           = 5
  ```

Final table:

```text
i\j   0   1   2   3
---------------------
0     1   0   0   0
1     1   1   0   0
2     1   1   1   0
3     1   2   1   0
4     1   2   1   1
5     1   3   1   1
6     1   3   4   1
7     1   3   4   5  <-- answer = 5
```

So `dp[7][3] = 5`, which is exactly the correct number of distinct subsequences.

---

## 5. Compressing to 1D DP

Notice from the recurrence:

```text
dp[i][j] depends only on dp[i-1][j] and dp[i-1][j-1]
```

That means we only ever need the **previous row** to compute the current row. So we can compress to:

> `dp[j]` = number of ways to form `t[0..j-1]` using the processed prefix of `s`.

### 5.1. 1D recurrence

When we process a new character `s[i-1]`, we want:

```text
new_dp[j] = dp[j]                 // skip s[i-1]
if s[i-1] == t[j-1]:
    new_dp[j] += dp[j-1]          // use s[i-1]
```

Instead of having `new_dp` and `old_dp`, we can update **in-place** as long as we update `j` **right to left** (from `n` down to `1`), so that `dp[j-1]` still refers to the previous row’s value.

Algorithm:

1. Initialize:

   ```text
   dp[0] = 1
   dp[j>0] = 0
   ```

2. For each character `s[i-1]` (i from 1 to m):

   ```text
   for (int j = n; j >= 1; j--) {
       if (s[i-1] == t[j-1]) {
           dp[j] += dp[j-1];
       }
   }
   ```

   * `dp[j]` initially stores the “skip” part (because we haven’t changed it yet in this iteration),
   * Adding `dp[j-1]` accounts for “use” when chars match.

3. Answer: `dp[n]`.

### 5.2. Java 1D implementation

```java
class Solution {
    public int numDistinct(String s, String t) {
        int m = s.length();
        int n = t.length();

        if (m < n) {
            return 0;
        }

        long[] dp = new long[n + 1];
        dp[0] = 1; // empty t

        for (int i = 1; i <= m; i++) {
            char sc = s.charAt(i - 1);
            // Go backwards so dp[j-1] is from previous "row"
            for (int j = n; j >= 1; j--) {
                if (sc == t.charAt(j - 1)) {
                    dp[j] += dp[j - 1];
                }
            }
        }

        return (int) dp[n];
    }
}
```

### 5.3. Tiny sanity example with 1D

Take a smaller example: `s = "babg"`, `t = "bag"`.

* `dp` initially: `[1, 0, 0, 0]` (for j = 0..3)

Process each `s[i]`:

1. `s[0] = 'b'`:

   * j = 3: t[2] = 'g' → no change.
   * j = 2: t[1] = 'a' → no change.
   * j = 1: t[0] = 'b' → match → `dp[1] += dp[0]` → `dp[1] = 1`.

   `dp = [1, 1, 0, 0]`.

2. `s[1] = 'a'`:

   * j = 3: t[2] = 'g' → no change.
   * j = 2: t[1] = 'a' → match → `dp[2] += dp[1]` → `dp[2] = 1`.
   * j = 1: t[0] = 'b' → no change.

   `dp = [1, 1, 1, 0]`.

3. `s[2] = 'b'`:

   * j = 3: t[2] = 'g' → no change.
   * j = 2: t[1] = 'a' → no change.
   * j = 1: t[0] = 'b' → match → `dp[1] += dp[0]` → `dp[1] = 2`.

   `dp = [1, 2, 1, 0]`.

4. `s[3] = 'g'`:

   * j = 3: t[2] = 'g' → match → `dp[3] += dp[2]` → `dp[3] = 1`.
   * j = 2: t[1] = 'a' → no change.
   * j = 1: t[0] = 'b' → no change.

   `dp = [1, 2, 1, 1]`.

Answer: `dp[3] = 1`, which matches the single subsequence `"bag"` in `"babg"`.

Same logic scales to the full `"babgbag"/"bag"` example and produces 5.

---

So:

* 2D DP: `dp[i][j] = dp[i-1][j] + (s[i-1]==t[j-1] ? dp[i-1][j-1] : 0)`, answer `dp[m][n]`.
* 1D DP: same recurrence, but with `j` loop going **backwards** to avoid overwriting needed previous values.

*/
// class Solution {
//     public int numDistinct(String s, String t) {
//         int m = s.length();
//         int n = t.length();

//         if (m < n) {
//             // Cannot form longer t from shorter s
//             return 0;
//         }

//         // dp[i][j] = # ways to form t[0..j-1] from s[0..i-1]
//         long[][] dp = new long[m + 1][n + 1]; // use long internally for safety

//         // Base: dp[i][0] = 1 for all i
//         for (int i = 0; i <= m; i++) {
//             dp[i][0] = 1;
//         }

//         // First row dp[0][j>0] is already 0 by default

//         // Fill table
//         for (int i = 1; i <= m; i++) {
//             for (int j = 1; j <= n; j++) {
//                 // Always can skip s[i-1]
//                 dp[i][j] = dp[i - 1][j];

//                 // If last characters match, add ways that use this char
//                 if (s.charAt(i - 1) == t.charAt(j - 1)) {
//                     dp[i][j] += dp[i - 1][j - 1];
//                 }
//             }
//         }

//         // The answer fits in int as per problem statement
//         return (int) dp[m][n];
//     }
// }
