// Method 1: Backtracking
/*
# WHAT WAS WRONG WITH MY APPROACH:

## 1. Outer `for` loop in `partition` is wrong

You currently have:

```java
public List<List<String>> partition(String s) {
    List<List<String>> ans = new ArrayList<>();
    List<String> palindrome = new ArrayList<>();

    for(int i=0; i<s.length(); i++){
        backtrack(s, ans, palindrome, i);
    }

    return ans;
}
```

Conceptually, for **Palindrome Partitioning** each partition must cover the **entire string** from index `0` to `s.length()-1`.

You only need **one** backtracking pass starting from `start = 0`:

* At each step, you choose a palindromic substring starting at `start`.
* When `start == s.length()`, you’ve partitioned the whole string → add current path.

By looping `i` from `0` to `n-1` and calling `backtrack(s, ans, palindrome, i)`, you are:

* Generating partitions for **every suffix** `s[i..]`, not just the full string.
* So for `s = "aab"` you’ll get:

  * From `i = 0`: correct partitions like `["a","a","b"]`, `["aa","b"]`
  * From `i = 1`: extra partitions like `["a","b"]`, `["ab"]` (only cover `s[1..]`)
  * From `i = 2`: `["b"]`
* These extra ones are **not valid answers** per the problem (they don’t partition the whole string).

**Fix:**

Just call backtracking **once** from `0`:

```java
public List<List<String>> partition(String s) {
    List<List<String>> ans = new ArrayList<>();
    backtrack(s, ans, new ArrayList<>(), 0);
    return ans;
}
```

---

## 2. `isPalindrome` is using wrong indices

In `backtrack`:

```java
for (int end = start; end < s.length(); end++) {
    String substr = s.substring(start, end + 1);
    if (isPalindrome(substr, start, end)) {
        palindrome.add(substr);
        backtrack(s, ans, palindrome, end + 1);
        palindrome.remove(palindrome.size() - 1);
    }
}
```

And `isPalindrome`:

```java
private static boolean isPalindrome(String s, int start, int end){
    while(start < end){
        if(s.charAt(start) == s.charAt(end)){
            start++;
            end--;
        }else{
            return false;
        }
    }
    return true;
}
```

Look at what’s happening:

* You compute `substr = s.substring(start, end + 1)` using the **original string** `s`.
* Then you call `isPalindrome(substr, start, end)`.

But inside `isPalindrome`, `s` is now the **substring**, not the original string, while `start` and `end` are **indices from the original string**.

Example: `s = "aab"`

* Suppose `start = 1`, `end = 2` in `backtrack`.
* `substr = s.substring(1, 3) = "ab"` (length 2, indices 0 and 1).
* Call `isPalindrome("ab", 1, 2)`:

  * It tries `s.charAt(1)` (ok, `'b'`) and `s.charAt(2)` → **IndexOutOfBoundsException** (substring length is 2, index 2 invalid).

Even when it doesn’t crash, it’s checking the **wrong characters**.

**You must do one of these:**

### Option A (recommended): use original string for palindrome check

Don’t build `substr` for checking. Just:

```java
for (int end = start; end < s.length(); end++) {
    if (isPalindrome(s, start, end)) {
        String substr = s.substring(start, end + 1);
        palindrome.add(substr);
        backtrack(s, ans, palindrome, end + 1);
        palindrome.remove(palindrome.size() - 1);
    }
}
```

Now `isPalindrome(s, start, end)` is consistent: same string, same indices.

### Option B: use substring indices correctly

If you really want to pass the substring, call:

```java
String substr = s.substring(start, end + 1);
if (isPalindrome(substr, 0, substr.length() - 1)) {
    ...
}
```

But that’s more allocations; Option A is standard.

---

## 3. Minor: base case should return immediately

Inside `backtrack`:

```java
if (start == s.length()) {
    ans.add(new ArrayList<>(palindrome));
}
```

You should normally `return` right after this:

```java
if (start == s.length()) {
    ans.add(new ArrayList<>(palindrome));
    return; // <-- important for clarity
}
```

In this specific code, it *works* without the `return` because the `for` loop:

```java
for (int end = start; end < s.length(); end++) {
    ...
}
```

won’t execute when `start == s.length()`, but adding `return` makes the intent clear and is a common pattern.



## 5. Tiny walkthrough on `"aab"` with this fixed code

`s = "aab"`

* Start: `start = 0`, `path = []`

  * `end = 0`: `"a"` palindrome → path = `["a"]`, recurse from `start = 1`

    * `end = 1`: `"a"` palindrome → path = `["a","a"]`, recurse from `start = 2`

      * `end = 2`: `"b"` palindrome → path = `["a","a","b"]`, recurse `start = 3 == n`

        * Add `["a","a","b"]` to ans
      * backtrack to `["a","a"]`
    * `end = 2`: `"ab"` not palindrome → skip
    * backtrack to `["a"]`
  * `end = 1`: `"aa"` palindrome → path = `["aa"]`, recurse from `start = 2`

    * `end = 2`: `"b"` palindrome → path = `["aa","b"]`, recurse `start = 3`

      * Add `["aa","b"]` to ans
    * backtrack to `["aa"]`
  * `end = 2`: `"aab"` not palindrome → skip
  * backtrack to `[]`

Result: `[["a","a","b"], ["aa","b"]]`.

No extra partitions from suffixes, no index crashes, and all palindromes are checked correctly.

---

**Summary of what was wrong:**

1. The outer `for` loop calls `backtrack` starting from every index → generates partitions that don’t cover the whole string.
2. `isPalindrome(substr, start, end)` passes substring with **original** indices → index mismatch and/or runtime error.
3. Minor: base case should `return` after adding a partition.
*/
class Solution {
    public List<List<String>> partition(String s) {
        List<List<String>> ans = new ArrayList<>();
        backtrack(s, ans, new ArrayList<>(), 0);
        return ans;
    }

    private static void backtrack(String s,
                                  List<List<String>> ans,
                                  List<String> path,
                                  int start) {
        // If we've consumed the whole string, record the current partition
        if (start == s.length()) {
            ans.add(new ArrayList<>(path));
            return;
        }

        // Try all possible end positions for the next palindrome
        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end)) {
                // Choose substring s[start..end]
                path.add(s.substring(start, end + 1));

                // Recurse from end+1
                backtrack(s, ans, path, end + 1);

                // Backtrack
                path.remove(path.size() - 1);
            }
        }
    }

    private static boolean isPalindrome(String s, int start, int end) {
        while (start < end) {
            if (s.charAt(start) != s.charAt(end)) {
                return false;
            }
            start++;
            end--;
        }
        return true;
    }
}






// Method 2: Backtracking with precomputed palindrome DP table
/*
## 1. Big picture: why hybrid?

Plain backtracking does this at every step:

* Try all substrings `s[start..end]`,
* For each, check `isPalindrome(s, start, end)` by scanning characters inward.

That palindrome check is O(n), and we may do it over and over for the same `(start, end)` pairs.

**Hybrid idea:**

* First, use DP to precompute `pal[i][j] = true` if `s[i..j]` is a palindrome.
* Then, during backtracking, checking if a substring is palindrome is just:

```java
if (pal[start][end]) { ... }
```

That’s O(1) per check.

So the heavy palindrome work is done once up front.

---

## 2. Step 1 – Palindrome DP table

Let `n = s.length()`.

We create a 2D boolean array:

```java
pal[i][j] == true  ⇔  s.substring(i, j+1) is a palindrome
```

We fill it using this logic:

* Single characters: always palindromes

  * `pal[i][i] = true`
* Two or more characters:

  * `pal[i][j] = (s.charAt(i) == s.charAt(j)) && (j - i < 2 || pal[i+1][j-1])`

    * `j - i < 2` covers length 2 (`"aa"`) and length 1 (already covered, but still okay).
    * For length ≥ 3, we also need the middle substring `s[i+1..j-1]` to be palindrome.

We fill `pal` with `i` going from right to left (n−1 to 0) and `j` from `i` to n−1 so that `pal[i+1][j-1]` is already known when we need it.

---

## 3. Step 2 – Backtracking with precomputed pal[i][j]

Backtracking function signature idea:

```java
void dfs(int start, List<String> path, List<List<String>> ans)
```

Where:

* `start` = index in `s` where we’re about to cut next.
* `path` = current list of palindromic pieces.
* `ans` = final list of all partitions.

Logic:

1. If `start == n`, we’re past the last index → we used the whole string:

   * Add a **copy** of `path` to `ans`.
   * Return.

2. Otherwise, for each `end` from `start` to `n-1`:

   * If `pal[start][end]` is true:

     * We can use `s[start..end]` as the next palindrome piece.
     * Add it to `path`.
     * Recurse with `start = end + 1`.
     * Backtrack: remove the last added substring.


## 5. Detailed example walkthrough: `s = "aab"`

We’ll go through:

1. Building the `pal` table.
2. Running the DFS.

### 5.1 Build pal[i][j] for "aab"

`s = "aab"`, `n = 3`, indexed as:

* 0 → 'a'
* 1 → 'a'
* 2 → 'b'

We fill `pal` for `i = 2..0`, `j = i..2`.

#### i = 2

* j = 2:

  * `s[2] == s[2]` → `'b' == 'b'` → true.
  * `j - i = 0 < 2` → `pal[2][2] = true`.

So substring `"b"` is palindrome.

#### i = 1

* j = 1:

  * `'a' == 'a'`, `j - i = 0 < 2` → `pal[1][1] = true` → `"a"` palindrome.
* j = 2:

  * `s[1] == s[2]` → `'a' == 'b'` → false → `pal[1][2] = false` → `"ab"` not palindrome.

#### i = 0

* j = 0:

  * `'a' == 'a'`, `j - i = 0 < 2` → `pal[0][0] = true` → `"a"` palindrome.
* j = 1:

  * `s[0] == s[1]` → `'a' == 'a'`, `j - i = 1 < 2` → `pal[0][1] = true` → `"aa"` palindrome.
* j = 2:

  * `s[0] == s[2]` → `'a' == 'b'` → false → `pal[0][2] = false` → `"aab"` not palindrome.

So final `pal` table:

* `pal[0][0] = true`  → "a"
* `pal[0][1] = true`  → "aa"
* `pal[0][2] = false` → "aab"
* `pal[1][1] = true`  → "a"
* `pal[1][2] = false` → "ab"
* `pal[2][2] = true`  → "b"

---

### 5.2 DFS from start = 0

Initial call:

```java
dfs("aab", start = 0, pal, path = [], ans = [])
```

#### At start = 0, path = []

We try all `end` from 0 to 2:

* `end = 0`: check `pal[0][0]` → true → substring `"a"` is palindrome.
* `end = 1`: check `pal[0][1]` → true → substring `"aa"` is palindrome.
* `end = 2`: check `pal[0][2]` → false → skip `"aab"`.

We’ll explore these in order.

---

#### Choice 1: use substring "a" (start=0, end=0)

We add `"a"` to path:

```text
path = ["a"]
dfs(start = 1, path = ["a"])
```

---

##### At start = 1, path = ["a"]

We now partition the suffix `"ab"` starting at index 1.

Try `end` from 1 to 2:

* `end = 1`: `pal[1][1] == true` → substring `"a"` is palindrome.
* `end = 2`: `pal[1][2] == false` → `"ab"` is not palindrome → skip.

So only `"a"` works here.

Pick `"a"` (s[1..1]):

```text
path = ["a", "a"]
dfs(start = 2, path = ["a", "a"])
```

---

###### At start = 2, path = ["a", "a"]

Now we’re at index 2, looking at suffix `"b"`.

Try `end` from 2 to 2:

* `end = 2`: `pal[2][2] == true` → `"b"` is palindrome.

Pick `"b"`:

```text
path = ["a", "a", "b"]
dfs(start = 3, path = ["a", "a", "b"])
```

---

###### At start = 3, path = ["a", "a", "b"]

Now `start == n == 3`, so we have consumed the entire string.

Base case:

```java
ans.add(new ArrayList<>(path));
```

So we record:

```text
["a", "a", "b"]
```

Then we return and **backtrack**:

* Return to `start = 2`, remove last element `"b"` → `path = ["a", "a"]`.
* No more `end` values at `start = 2`, return to `start = 1`.
* At `start = 1`, we also tried all `end` (1 and 2), so backtrack again:

  * Remove last `"a"` → `path = ["a"]`.
* Back to `start = 0` with `path = ["a"]`.

Now we’ve fully explored all partitions that **start with "a"** at index 0.

---

#### Choice 2: use substring "aa" (start=0, end=1)

Back at `start = 0, path = []`, next `end = 1`:

* `pal[0][1] == true`, substring `"aa"` is palindrome.

Choose `"aa"`:

```text
path = ["aa"]
dfs(start = 2, path = ["aa"])
```

---

##### At start = 2, path = ["aa"]

Suffix is `"b"` from index 2.

Try `end` from 2 to 2:

* `end = 2`: `pal[2][2] == true` → `"b"`.

Pick `"b"`:

```text
path = ["aa", "b"]
dfs(start = 3, path = ["aa", "b"])
```

At `start = 3`:

* `start == n` → record `["aa","b"]` in `ans`.

Backtrack:

* Remove `"b"` → `path = ["aa"]`
* Return to `start = 2` → no more `end`, back up.
* Remove `"aa"` → `path = []`
* Return to `start = 0`.

Now all possible `end` values at `start = 0` have been processed:

* `end = 0` → gave `["a","a","b"]`
* `end = 1` → gave `["aa","b"]`
* `end = 2` → skipped (not palindrome)

DFS finishes.

---

### 5.3 Final result

`ans` contains:

```text
[
  ["a", "a", "b"],
  ["aa", "b"]
]
```

Exactly all palindrome partitions of `"aab"`.

---

## 6. Why this is a good interview answer

* Shows understanding of:

  * **Backtracking**: explore all ways to cut, use path, backtrack after recursive calls.
  * **DP**: precompute palindrome information to avoid repeated work.
* Very clean separation of concerns:

  * Palindrome DP table (`pal`),
  * DFS that just uses `pal` and string slicing.
* Easy to discuss complexity:

  * Precomputation: O(n²),
  * DFS: exponential in number of partitions (unavoidable because we output all),
  * Palindrome check itself is now O(1) during DFS.
*/

// class Solution {
//     public List<List<String>> partition(String s) {
//         int n = s.length();
//         List<List<String>> ans = new ArrayList<>();
//         if (n == 0) return ans;

//         // 1) Precompute palindrome table pal[i][j]
//         boolean[][] pal = new boolean[n][n];

//         // Fill from bottom up, right to left
//         for (int i = n - 1; i >= 0; i--) {
//             for (int j = i; j < n; j++) {
//                 if (s.charAt(i) == s.charAt(j)) {
//                     // Length 1 or 2, or middle substring is palindrome
//                     if (j - i < 2) {
//                         pal[i][j] = true;
//                     } else {
//                         pal[i][j] = pal[i + 1][j - 1];
//                     }
//                 } else {
//                     pal[i][j] = false;
//                 }
//             }
//         }

//         // 2) Backtracking using the palindrome table
//         dfs(s, 0, pal, new ArrayList<>(), ans);
//         return ans;
//     }

//     // Backtracking: build partitions starting from index 'start'
//     private void dfs(String s, int start, boolean[][] pal,
//                      List<String> path, List<List<String>> ans) {
//         int n = s.length();
//         // If we've consumed the whole string, record current path
//         if (start == n) {
//             ans.add(new ArrayList<>(path));
//             return;
//         }

//         // Try all palindromic substrings starting at 'start'
//         for (int end = start; end < n; end++) {
//             if (pal[start][end]) {  // s[start..end] is palindrome
//                 path.add(s.substring(start, end + 1)); // choose
//                 dfs(s, end + 1, pal, path, ans);       // explore
//                 path.remove(path.size() - 1);          // backtrack
//             }
//         }
//     }
// }





// Method 3: Backtracking only approach
/*
## 1. Core idea

We want all ways to cut `s` into substrings where **each substring is a palindrome**.

Example: `s = "aab"`

Valid partitions:

* `["a", "a", "b"]`
* `["aa", "b"]`

Instead of checking `isPalindrome` over and over in recursion, we can:

1. Precompute `pal[i][j] = true/false` for all `0 ≤ i ≤ j < n`.
2. Use another DP `dp[i]` that stores **all** palindrome partitions of the suffix `s[i..n-1]`.

Then:

* Answer = `dp[0]`.

---

## 2. Step 1: Palindrome DP table

Let `n = s.length()`.

We define:

```text
pal[i][j] = true if s[i..j] is a palindrome
```

Recurrence:

* Base:

  * Single chars: `pal[i][i] = true`.
  * Two chars: `pal[i][i+1] = (s[i] == s[i+1])`.
* General:

  * For `i < j`: `pal[i][j] = (s[i] == s[j]) && pal[i+1][j-1]`.

We fill `pal` in order of **increasing substring length**, or equivalently:

* Loop `i` from `n-1` down to `0`
* For each `i`, loop `j` from `i` up to `n-1`

  * Use the recurrence.

---

## 3. Step 2: Partition DP on suffixes

Define:

```text
dp[i] = List of all palindrome partitions of the substring s[i..n-1]
```

We want `dp[0]`.

Base:

* `dp[n] = [[]]`
  (One way to partition the empty suffix: with an empty list of parts.)

Transition:

For each position `i` from `n-1` down to `0`:

* Look at all possible `end` from `i` to `n-1`:

  * If `pal[i][end] == true`, then `s[i..end]` is a palindrome.
  * Take this palindrome prefix `p = s.substring(i, end + 1)`.
  * For every partition `suffixPartition` in `dp[end + 1]`:

    * Create a new list: `[p] + suffixPartition`.
    * Add it to `dp[i]`.

This ensures:

* Every partition of `s[i..]` starts with some palindrome `s[i..end]` and is followed by a valid partition of `s[end+1..]`.



## 5. Detailed example walkthrough: `s = "aab"`

Let’s go through both DP steps.

### 5.1 Precompute pal[i][j]

`s = "aab"`, indices 0,1,2:

* `s[0] = 'a'`
* `s[1] = 'a'`
* `s[2] = 'b'`

We fill `pal` with `i` from 2 down to 0.

#### i = 2

* j = 2: compare `s[2]` with `s[2]` → `'b' == 'b'` → yes.
  `j - i = 0 ≤ 2` → `pal[2][2] = true`.

So:

* `pal[2][2] = true` (substring `"b"`)

#### i = 1

* j = 1: `s[1] == s[1]` → `'a' == 'a'` → true, `j - i = 0` → `pal[1][1] = true`.

  * `"a"` at index 1 is palindrome.
* j = 2: `s[1] == s[2]` → `'a' == 'b'` → false → `pal[1][2] = false`.

So:

* `pal[1][1] = true` (`"a"`)
* `pal[1][2] = false` (`"ab"` is not palindrome)

#### i = 0

* j = 0: `'a' == 'a'` → `pal[0][0] = true` (`"a"`).
* j = 1: `'a' == 'a'`, `j - i = 1 ≤ 2` → `pal[0][1] = true` (`"aa"`).
* j = 2: `'a' == 'b'` → false → `pal[0][2] = false` (`"aab"`, not palindrome).

Summarizing pal:

* `pal[0][0] = true`  ("a")
* `pal[0][1] = true`  ("aa")
* `pal[0][2] = false` ("aab")
* `pal[1][1] = true`  ("a")
* `pal[1][2] = false` ("ab")
* `pal[2][2] = true`  ("b")

---

### 5.2 Building dp[i] from right to left

We want `dp[i] = all partitions of s[i..2]`.

Base:

```text
dp[3] = [ [] ]  // empty suffix
```

#### i = 2 (suffix "b")

We’re partitioning `"b"` (s[2..2]):

* Initialize `curList = []`
* Try end from 2 to 2:

  * end = 2:

    * `pal[2][2] == true`  → substring `"b"` is palindrome.
    * prefix = `"b"`
    * Look at `dp[end + 1] = dp[3] = [ [] ]`

      * For each suffixPartition in dp[3] (only `[]`):

        * newPartition = `["b"] + []` = `["b"]`
        * add to curList

So:

```text
dp[2] = [ ["b"] ]
```

These are all palindrome partitions of `"b"`.

---

#### i = 1 (suffix "ab")

We’re partitioning `"ab"` (s[1..2]):

* `curList = []`
* Try end from 1 to 2:

  * end = 1:

    * `pal[1][1] == true` → `"a"` is palindrome.
    * prefix = `"a"`
    * `dp[end+1] = dp[2] = [ ["b"] ]`

      * Combine prefix `"a"` with each partition in dp[2]:

        * `["a"] + ["b"]` = `["a","b"]`
      * Add `["a","b"]` to `curList`.
  * end = 2:

    * `pal[1][2] == false` → `"ab"` not palindrome → skip.

So:

```text
dp[1] = [ ["a","b"] ]
```

(There’s no `["ab"]` because `"ab"` is not palindrome.)

---

#### i = 0 (suffix "aab")

We’re partitioning `"aab"` (s[0..2]):

* `curList = []`
* Try end from 0 to 2:

  * end = 0:

    * `pal[0][0] == true` → `"a"` palindrome.
    * prefix = `"a"`
    * `dp[1] = [ ["a","b"] ]`

      * Combine:

        * newPartition = `["a"] + ["a","b"]` = `["a","a","b"]`
      * Add `["a","a","b"]` to `curList`.

  * end = 1:

    * `pal[0][1] == true` → `"aa"` palindrome.
    * prefix = `"aa"`
    * `dp[2] = [ ["b"] ]`

      * Combine:

        * newPartition = `["aa"] + ["b"]` = `["aa","b"]`
      * Add `["aa","b"]` to `curList`.

  * end = 2:

    * `pal[0][2] == false` → `"aab"` not palindrome → skip.

Result:

```text
dp[0] = [
  ["a", "a", "b"],
  ["aa", "b"]
]
```

Which matches the expected answer for `"aab"`.

---

## 6. Why this is a valid “DP approach” (not just backtracking)

* We use **DP to precompute palindromes** (`pal[i][j]`) in O(n²).
* We use a **bottom-up DP** (`dp[i]`) to build all partitions from `i` to end.
* We never do recursive backtracking; everything is tabulation-like, though structurally similar to recursion.

Time complexity:

* Palindrome DP: O(n²).
* Partition DP:

  * Each `(i,end)` pair considered once → O(n²),
  * For each, we may clone and append strings; total output size is exponential in the worst case (because there are exponentially many partitions).
* So overall: O(n² + total_output_size * average_partition_length). That’s optimal given you must return all partitions.
*/

// class Solution {
//     public List<List<String>> partition(String s) {
//         int n = s.length();
//         if (n == 0) return new ArrayList<>();

//         // 1) Palindrome DP: pal[i][j] = true if s[i..j] is palindrome
//         boolean[][] pal = new boolean[n][n];

//         for (int i = n - 1; i >= 0; i--) {
//             for (int j = i; j < n; j++) {
//                 if (s.charAt(i) == s.charAt(j)) {
//                     if (j - i <= 2) {
//                         // length 1 or 2, or 3 (i..j with middle one char)
//                         pal[i][j] = true;
//                     } else {
//                         pal[i][j] = pal[i + 1][j - 1];
//                     }
//                 } else {
//                     pal[i][j] = false;
//                 }
//             }
//         }

//         // 2) Partition DP: dp[i] = list of all partitions of s[i..n-1]
//         List<List<String>>[] dp = new ArrayList[n + 1];

//         // Base: empty suffix has one partition: empty list
//         dp[n] = new ArrayList<>();
//         dp[n].add(new ArrayList<>());

//         // Fill from right to left
//         for (int i = n - 1; i >= 0; i--) {
//             List<List<String>> curList = new ArrayList<>();

//             // Try all possible palindromic substrings starting at i
//             for (int end = i; end < n; end++) {
//                 if (pal[i][end]) {
//                     String prefix = s.substring(i, end + 1);
//                     // Append this prefix to each partition of the remaining suffix
//                     for (List<String> suffixPartition : dp[end + 1]) {
//                         List<String> newPartition = new ArrayList<>();
//                         newPartition.add(prefix);
//                         newPartition.addAll(suffixPartition);
//                         curList.add(newPartition);
//                     }
//                 }
//             }

//             dp[i] = curList;
//         }

//         return dp[0];
//     }
// }
