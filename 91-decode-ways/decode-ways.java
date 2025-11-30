// Method 1: Top-Down 1D DP
/*
## 3. Detailed explanation of the top-down idea

### Problem recap

We get a string `s` of digits. Each number `"1"` to `"26"` can be mapped to `'A'`–`'Z'`:

* `"1"` → `'A'`, `"2"` → `'B'`, …, `"26"` → `'Z'`.

We want the **number of ways** to split the string into valid 1- or 2-digit chunks, each representing a letter.

Examples:

* `"12"` → `"1" "2"` (`A B`) and `"12"` (`L`) → 2 ways
* `"06"` → invalid (can’t start with `'0'`) → 0 ways

---

### DP state: `dp(i)`

Define:

> `dp(i)` = number of ways to decode the substring `s[i..end]`.

We want `dp(0)`.

### Base cases

1. **End of string**: `i == n`

   If we’ve consumed the whole string successfully, that’s **one** valid decoding path.

   ```java
   if (i == n) return 1;
   ```

2. **Leading zero**: `s[i] == '0'`

   A `'0'` cannot be decoded by itself.

   ```java
   if (s.charAt(i) == '0') return 0;
   ```

### Recursive choices

From index `i` (where `s[i] != '0'`):

1. **Take one digit**:
   Interpret `s[i]` as a single letter (since it's `'1'`..`'9'`), then decode the rest:

   ```java
   ways1 = dp(i + 1);
   ```

2. **Take two digits**:
   If `i+1 < n`, build the integer value of `s[i..i+1]`. Call it `twoDigit`.

   * If `10 <= twoDigit <= 26`, then it’s a valid letter too. So you can also decode from `i+2`:

     ```java
     ways2 = dp(i + 2);
     ```

   * Otherwise, there is **no** 2-digit option here.

Total ways from `i`:

```java
dp(i) = ways1 + (ways2 if valid)
```

### Memoization

We use `memo[i]` to store `dp(i)` once it’s computed:

* At the top of `dp(i)`:

  ```java
  if (memo[i] != null) return memo[i];
  ```

* At the end:

  ```java
  memo[i] = ways;
  return ways;
  ```

This avoids recomputing overlapping subproblems and gives O(n) time.

---

## 4. Example walkthroughs

### Example 1: `"226"` (classic)

We know:

* `"2 2 6"` → `B B F`
* `"2 26"` → `B Z`
* `"22 6"` → `V F`

So answer should be **3**.

Let’s walk through `dp(i)` from the top-down perspective.

String:

```text
s = "226"
i:    0   1   2
      '2' '2' '6'
n = 3
```

We want `dp(0)`.

---

#### Step 1: `dp(0)`

`i = 0`, `s[0] = '2'` (not '0'), not in memo.

* **One-digit option**: `'2'`

  → `ways1 = dp(1)`

* **Two-digit option**: `s[0..1] = "22"`

  * `twoDigit = 22` (valid, between 10 and 26)
    → `ways2 = dp(2)`

So:

```text
dp(0) = dp(1) + dp(2)
```

We need `dp(1)` and `dp(2)`.

---

#### Step 2: `dp(2)`

Let’s actually do `dp(2)` first (easier).

`i = 2`, `s[2] = '6'`

* One-digit option:

  ```text
  ways1 = dp(3)
  ```

  * Now `dp(3)`:

    * `i == n` → base case → return 1.

  So `ways1 = 1`.

* Two-digit option: `i+1 < n`?

  * `i+1 = 3` is not `< 3` → no two-digit option.

So:

```text
dp(2) = 1
```

Memo: `memo[2] = 1`.

---

#### Step 3: `dp(1)`

`i = 1`, `s[1] = '2'`

* One-digit option:

  ```text
  ways1 = dp(2)
  ```

  We already computed `dp(2) = 1`.

* Two-digit option: `s[1..2] = "26"`

  * `twoDigit = 26`, valid (10..26)
  * So:

    ```text
    ways2 = dp(3)
    ```

    * `dp(3)` is base case `i == n` → 1.

So:

```text
dp(1) = 1 (from dp(2)) + 1 (from dp(3)) = 2
```

Memo: `memo[1] = 2`.

Interpretation:

* From `"26"` at index 1:

  * `"2" "6"` → `"B F"`
  * `"26"` → `"Z"`
    → 2 ways.

---

#### Step 4: Back to `dp(0)`

Recall:

```text
dp(0) = dp(1) + dp(2)
```

We now have:

* `dp(1) = 2`
* `dp(2) = 1`

So:

```text
dp(0) = 2 + 1 = 3
```

→ final answer = 3, as expected.

Interpretation of the three paths:

1. `"2" | "2" | "6"` → `B B F`
2. `"2" | "26"` → `B Z`
3. `"22" | "6"` → `V F`

---

### Example 2 (shows your original bug): `"27"`

True answer: `"27"` can only be decoded as `"2" "7"` → `'B' 'G'` → **1 way**.

Two-digit `"27"` is **not** valid because it’s > 26.

Let’s see what **correct** DP does vs your original.

String:

```text
s = "27"
i:    0   1
      '2' '7'
n = 2
```

#### Correct DP (with validation)

We compute `dp(0)`.

`dp(0)`:

* `s[0] = '2'`
* One-digit option: `"2"` → `dp(1)`.
* Two-digit option: check `twoDigit = 27` → not in [10..26], so **no** 2-digit option.

So:

```text
dp(0) = dp(1)
```

Now `dp(1)`:

* `s[1] = '7'`
* One-digit option: `"7"` → `dp(2)`.
* Two-digit option: `i+1` is out of bounds, so none.

`dp(2)`:

* `i == n` → 1.

So:

```text
dp(1) = 1
dp(0) = 1
```

Final answer = **1** ✅

#### Your original version (without 2-digit check)

From your code:

```java
memo[i] = dp(i+1);
if (i+1 < n) {
    memo[i] += dp(i+2);
}
```

At `i = 0`:

* `memo[0] = dp(1);`
* `i+1 = 1 < 2` → true

  * so `memo[0] += dp(2);`

Here:

* `dp(1) = 1` (as before)
* `dp(2) = 1` (base case)

So your `dp(0)` becomes:

```text
dp(0) = 1 (from dp(1)) + 1 (from dp(2)) = 2
```

You’d return **2**, counting an invalid decoding path for `"27"` as if `"27"` itself were a valid code.

That’s exactly what the extra `twoDigit` validity check fixes.

---

## 5. Complexity

With memoization and the correct checks:

* Each index `i` is computed once.
* Each `dp(i)` does O(1) work.
* Overall time: **O(n)**.
* Space: **O(n)** for the memo.

---

### TL;DR

* The only real problem with your approach is:
  You always consider a 2-digit decode (`dp(i+2)`) whenever `i+1` exists, **even when the 2-digit number is not between 10 and 26**.

* Fix: compute `twoDigit` and only add `dp(i+2)` if `10 ≤ twoDigit ≤ 26`.
*/

class Solution {
    public int numDecodings(String s) {
        int n = s.length();
        Integer[] memo = new Integer[n];

        int count = dp(s, memo, 0);

        return count;
    }

    private int dp(String s, Integer[] memo, int i){
        if(i == s.length()){
            return 1;
        }

        if(s.charAt(i) == '0'){
            return 0;
        }

        if(memo[i] != null){
            return memo[i];
        }


        int ways = dp(s, memo, i+1);

        if(i+1 < s.length()){
            int twoDigit = (s.charAt(i) - '0') * 10 + (s.charAt(i+1) - '0');
            if(twoDigit >= 10 && twoDigit <= 26){
                ways += dp(s, memo, i+2);
            }
        }

        memo[i] = ways;

        return memo[i];
    }
}





// Method 2: Bottom-Up 1D DP
/*
## 1. Rethinking the problem in DP terms

We’re given a digit string `s` like `"226"` and a mapping:

* `"1"` → `'A'`, `"2"` → `'B'`, ..., `"26"` → `'Z'`

We want the **number of ways** to decode the entire string.

Instead of thinking “global string” all at once, we define:

> **`dp[i]` = number of ways to decode the substring `s[i..n-1]` (suffix starting at `i`).**

* `n = s.length()`
* We want **`dp[0]`** → ways to decode from the very beginning.

We’ll build `dp` **bottom-up**, from the **end of the string back to the start**.

---

## 2. Base case: what is `dp[n]`?

Consider `i == n`, i.e., we’re “at” the position **just after** the last character.

That means:

* We have successfully consumed the entire string.
* There is exactly **1** way to decode “nothing more” → the empty suffix.

So:

```text
dp[n] = 1
```

This is a very standard trick:
“1 way to finish once everything is valid so far.”

We’ll use an array of size `n + 1`:

* `dp[0..n]`
* `dp[n] = 1`

---

## 3. Handling zeros: why `'0'` is special

Important rule:

* The digit `'0'` **cannot** be decoded on its own.
* It only appears as part of `"10"` or `"20"` (which map to `'J'` or `'T'`).

So if `s[i] == '0'`:

> There are **0 ways** to decode starting at position `i`.

Thus:

```text
if s[i] == '0': dp[i] = 0
```

and we skip further logic for that `i`.

---

## 4. Recurrence: 1-digit and 2-digit choices

If `s[i] != '0'`, then:

### Option 1: take **one digit** `s[i]`

* This digit (1–9) maps to one letter.
* We decode the rest starting at `i + 1`.

So:

```text
ways_from_one_digit = dp[i + 1]
```

### Option 2: take **two digits** `s[i..i+1]`

We are allowed to do this **only if**:

* `i + 1 < n` (so there *is* a next character)
* The number formed by `s[i..i+1]` is between **10 and 26** (inclusive)

Compute:

```text
twoDigit = (s[i] - '0') * 10 + (s[i+1] - '0')
```

If `10 <= twoDigit <= 26`, then:

```text
ways_from_two_digits = dp[i + 2]
```

Otherwise `ways_from_two_digits = 0`.

### Total ways for `i`

Combine both:

```text
dp[i] = ways_from_one_digit + (ways_from_two_digits if valid else 0)
```

We compute this for `i` from `n-1` down to `0`.

---

## 5. Iteration order

We fill `dp` like this:

1. Initialize `dp[n] = 1`
2. For `i` from `n-1` down to `0`:

   * If `s[i] == '0'` → `dp[i] = 0`
   * Else:

     * `dp[i] = dp[i+1]` (1-digit)
     * If valid 2-digit number → `dp[i] += dp[i+2]`
3. Return `dp[0]`.

This bottom-up version is basically the *same logic* as the top-down recursion, just written iteratively.

* Time: **O(n)**
* Space: **O(n)** (you can optimize to O(1) later by keeping just two variables like we did in other problems).

---

## 7. Thorough example: `"226"`

We know:

* `"2 2 6"` → `B B F`
* `"2 26"` → `B Z`
* `"22 6"` → `V F`

→ result should be **3**.

Let’s walk with DP.

```text
s = "226"
n = 3

Indices:
 i:   0   1   2
      '2' '2' '6'
dp size: dp[0..3]
```

### Step 0: initialize

```text
dp[3] = 1   // empty suffix
dp[0], dp[1], dp[2] initially 0
```

We’ll fill `i = 2, 1, 0`.

---

### i = 2  → substring "6"

`c = s[2] = '6'` (not '0').

* **One-digit option**:

  ```text
  dp[2] = dp[3] = 1
  ```

* **Two-digit option**:
  `i + 1 = 3`, which is not `< n (=3)` → no two-digit option.

So:

```text
dp[2] = 1
```

Interpretation: substring `"6"` can be decoded **1 way** → `'F'`.

Current `dp`:

```text
dp[0] = ?
dp[1] = ?
dp[2] = 1
dp[3] = 1
```

---

### i = 1  → substring "26"

`c = s[1] = '2'` (not '0').

* **One-digit option** (`"2"`):

  ```text
  dp[1] = dp[2] = 1
  ```

* **Two-digit option** (`"26"`):

  * `i + 1 = 2 < 3` → okay
  * `twoDigit = (2)*10 + 6 = 26`
  * `26` is between 10 and 26 → valid

  So:

  ```text
  dp[1] += dp[3];   // dp[3] = 1
  dp[1] = 1 (from dp[2]) + 1 (from dp[3]) = 2
  ```

Interpretation of `dp[1] = 2`:

* Substring `"26"` (from index 1):

  * `"2" "6"` → `"B" "F"`
  * `"26"` → `"Z"`

Current `dp`:

```text
dp[0] = ?
dp[1] = 2
dp[2] = 1
dp[3] = 1
```

---

### i = 0  → substring "226"

`c = s[0] = '2'`.

* **One-digit option** (`"2"`):

  ```text
  dp[0] = dp[1] = 2
  ```

* **Two-digit option** (`"22"`):

  * `i + 1 = 1 < 3` → okay
  * `twoDigit = 22` → valid (10..26)

  So:

  ```text
  dp[0] += dp[2];  // dp[2] = 1
  dp[0] = 2 + 1 = 3
  ```

Interpretation of `dp[0] = 3`:

From `"226"`:

1. Take `"2"` + decode `"26"`:

   * `"2"` + (`"2" "6"`) → `"2" "2" "6"` → `B B F`
   * `"2"` + (`"26"`) → `"2" "26"` → `B Z`
2. Take `"22"` + decode `"6"`:

   * `"22"` + `"6"` → `V F`

So total = **3**.

Final `dp`:

```text
dp[0] = 3
dp[1] = 2
dp[2] = 1
dp[3] = 1
```

Answer: `dp[0] = 3`. ✅

---

## 8. Another example with zeros: `"2101"`

This is a good test because of tricky zeros.

```text
s = "2101"
i:   0   1   2   3
     '2' '1' '0' '1'
n = 4
dp[0..4]
dp[4] = 1
```

We’ll fill `i = 3, 2, 1, 0`.

---

### i = 3  → substring "1"

`c = '1'`:

* One-digit: `dp[3] = dp[4] = 1`
* Two-digit: `i+1 = 4` not < 4 → none.

So:

```text
dp[3] = 1
```

---

### i = 2  → substring "01"

`c = '0'`:

* `'0'` cannot be decoded alone → `dp[2] = 0`
* We do **not** add any two-digit option here in this bottom-up logic (it would be handled from the perspective of positions before index 2, like `"10"` at `i=1`).

So:

```text
dp[2] = 0
```

---

### i = 1  → substring "101"

`c = '1'`:

* One-digit (`"1"`):

  ```text
  dp[1] = dp[2] = 0
  ```

* Two-digit `"10"`:

  * `twoDigit = 10`, valid (10..26)
    → add `dp[3] = 1`

So:

```text
dp[1] = 0 + dp[3] = 1
```

Interpretation: substring `"101"` decodes only as `"10" "1"` → `"J" "A"`.

---

### i = 0  → substring "2101"

`c = '2'`:

* One-digit (`"2"`):

  ```text
  dp[0] = dp[1] = 1
  ```

* Two-digit `"21"`:

  * `twoDigit = 21`, valid
    → add `dp[2] = 0`

So:

```text
dp[0] = 1 + 0 = 1
```

Interpretation:

* `"2" "10" "1"` → `"B" "J" "A"` is the only valid decoding.

Final `dp`:

```text
dp[0] = 1
dp[1] = 1
dp[2] = 0
dp[3] = 1
dp[4] = 1
```

Answer: `1`. ✅

---

## 9. Summary

Bottom-up DP for **Decode Ways**:

* Define `dp[i] = #ways to decode s[i..n-1]`.
* Base: `dp[n] = 1`.
* For `i` from `n-1` down to `0`:

  * If `s[i] == '0'` → `dp[i] = 0`.
  * Else:

    * `dp[i] = dp[i+1]`
    * If the two-digit number `s[i..i+1]` is between 10 and 26, add `dp[i+2]`.
* Answer = `dp[0]`.

If you’d like, next we can compress this DP down to **O(1) space** (like using `prev1` and `prev2`), similar to how we did for House Robber and Min Cost Climbing Stairs.
*/

// class Solution {
//     public int numDecodings(String s) {
//         int n = s.length();
//         // dp[i] = number of ways to decode s[i..n-1]
//         int[] dp = new int[n + 1];
        
//         // Base case: empty suffix has 1 valid decoding
//         dp[n] = 1;
        
//         // Fill from right to left
//         for (int i = n - 1; i >= 0; i--) {
//             char c = s.charAt(i);
            
//             // If current char is '0', no valid decoding starts here
//             if (c == '0') {
//                 dp[i] = 0;
//                 continue;
//             }
            
//             // Option 1: take one digit
//             dp[i] = dp[i + 1];
            
//             // Option 2: take two digits if valid (10..26)
//             if (i + 1 < n) {
//                 int twoDigit = (c - '0') * 10 + (s.charAt(i + 1) - '0');
//                 if (twoDigit >= 10 && twoDigit <= 26) {
//                     dp[i] += dp[i + 2];
//                 }
//             }
//         }
        
//         return dp[0];
//     }
// }