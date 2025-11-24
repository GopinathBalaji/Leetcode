// Method 1: Backtracking
/*
## 2. Core idea in plain language

The input `digits` is a string like `"23"`.

Each digit corresponds to a set of letters (like on a phone keypad):

* `'2' → "abc"`
* `'3' → "def"`, etc.

We want **all combinations** where we pick:

* one letter for the first digit,
* one letter for the second digit,
* one for the third, and so on.

So if `digits = "23"`:

* For `'2'` we can choose **a, b, or c**
* For `'3'` we can choose **d, e, or f**

All combinations (Cartesian product) are:

```text
ad, ae, af, bd, be, bf, cd, ce, cf
```

Backtracking is a natural way to generate these:

* Think of building the combination **one position at a time**.
* At position `idx` (digit index), we try each possible letter for that digit.
* We recurse to the next digit.
* After recursion, we undo the choice (backtrack) and try the next letter.

---

## 3. Backtracking state

We track four things:

1. `digits` – the original input string.
2. `idx` – which index in `digits` we’re currently processing.
3. `sb` – a `StringBuilder` containing the **partial combination** built so far.
4. `ans` – list of all completed combinations.

### Base case

```java
if (idx == digits.length()) {
    ans.add(sb.toString());
    return;
}
```

Meaning:
If we’ve chosen one letter for every digit (we’re at the end), then `sb` now holds a valid full combination. Add it to the result and stop this path.

### Recursive step

For the current digit `digits[idx]`:

1. Look up its letters from `map` (e.g. `'2' -> "abc"`).
2. For each letter `c` in those letters:

   * Append `c` to `sb`.
   * Recurse with `idx + 1`.
   * Remove the last char from `sb` (backtrack) so we can try a different letter.

This is a typical pattern:

```java
for (each choice) {
    make choice
    recurse
    undo choice
}
```

---

## 4. Detailed walkthrough with example: `digits = "23"`

Mapping (relevant part):

* `'2' → "abc"`
* `'3' → "def"`

We start with:

```text
ans = []
sb = ""
idx = 0
```

Call:

```java
backtrack("23", map, ans, sb, 0)
```

---

### Level 1: processing digit '2' at idx = 0

* `idx = 0 != 2` → not base case.
* `digit = digits.charAt(0) = '2'`
* `letters = map.get('2') = "abc"`

We loop over `"abc"`.

#### 1) First letter: 'a'

* `sb.append('a')` → `sb = "a"`
* Recurse:

  ```java
  backtrack("23", map, ans, "a", idx = 1)
  ```

---

### Level 2: processing digit '3' at idx = 1, sb = "a"

* `idx = 1 != 2` → not base case.
* `digit = digits.charAt(1) = '3'`
* `letters = map.get('3') = "def"`

Loop over `"def"`:

##### 1.1) First letter: 'd'

* `sb.append('d')` → `sb = "ad"`
* Recurse:

  ```java
  backtrack("23", map, ans, "ad", idx = 2)
  ```

Now:

* `idx = 2 == digits.length()` → base case.

* Add `"ad"` to `ans`:

  ```text
  ans = ["ad"]
  ```

* Backtrack: `sb.deleteCharAt(sb.length() - 1)` → remove `'d'`, `sb` becomes `"a"`.

##### 1.2) Second letter: 'e'

* `sb = "a"`
* `sb.append('e')` → `sb = "ae"`
* Recurse:

  ```java
  backtrack("23", map, ans, "ae", idx = 2)
  ```

Base case again:

* Add `"ae"` → `ans = ["ad", "ae"]`.
* Backtrack: remove `'e'` → `sb = "a"`.

##### 1.3) Third letter: 'f'

* `sb.append('f')` → `sb = "af"`
* Recurse:

  ```java
  backtrack("23", map, ans, "af", idx = 2)
  ```

Base case:

* Add `"af"` → `ans = ["ad", "ae", "af"]`.
* Backtrack: `sb = "a"`.

Done with letters `"def"` for the second digit when first digit is `'a'`.

Backtrack again from level 2 to level 1:

* Now we remove `'a'` from `sb`:

  * `sb = ""`.

---

### Back to Level 1: still digit '2', we now try 'b' and 'c'

#### 2) Second letter: 'b'

* `sb.append('b')` → `sb = "b"`
* Recurse:

  ```java
  backtrack("23", map, ans, "b", idx = 1)
  ```

Again:

* `letters = "def"` for digit '3'.

For each:

* `'d'` → `"bd"` → add to ans.
* `'e'` → `"be"` → add.
* `'f'` → `"bf"` → add.

After this branch:

```text
ans = ["ad", "ae", "af", "bd", "be", "bf"]
sb = ""
```

#### 3) Third letter: 'c'

* `sb.append('c')` → `sb = "c"`
* Recurse:

  ```java
  backtrack("23", map, ans, "c", idx = 1)
  ```

Again for `'3' → "def"`:

* `'d'` → `"cd"` → add.
* `'e'` → `"ce"` → add.
* `'f'` → `"cf"` → add.

Final `ans`:

```text
["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"]
```

`sb` ends as empty again.

---

## 5. What backtracking is doing for us

Think of it like exploring a tree:

* Level 0: no letters yet.
* Level 1: choose one letter for the first digit (`a, b, c`).
* Level 2: for each first letter, choose one letter for the second digit (`d, e, f`).

Backtracking pattern:

1. **Choose** a letter (append to `sb`).
2. **Explore** deeper (call backtrack for `idx + 1`).
3. **Unchoose** the letter (remove last char), so we can try the next letter.

This prevents us from creating new strings every time from scratch for partial combinations. We reuse the same `StringBuilder` and just modify it as we go down and back up the recursion tree.

---

## 6. Edge case: empty digits

If `digits = ""`:

* We hit:

  ```java
  if (digits == null || digits.length() == 0) {
      return ans;  // ans is []
  }
  ```

So we return `[]` immediately, which is what LeetCode expects (no combinations when there are no digits).

Without this check, we’d add a single `""` (empty string) as a “combination”, which is incorrect for this problem.

---

## 7. Complexity

Let:

* `k` = number of digits,
* each digit maps to at most 4 letters (only 7 and 9 have 4 letters).

Number of combinations:

* At most `4^k` (e.g., `"7777"`).

Time complexity:

* We generate each combination once and copy it: `O(k)` per combination.
* Total: `O(k * 4^k)`.

Space complexity:

* `O(k)` for the recursion depth + the `StringBuilder` content.
* Plus `O(k * 4^k)` for the output list (unavoidable).
*/
class Solution {
    public List<String> letterCombinations(String digits) {
        List<String> ans = new ArrayList<>();
        if(digits == null || digits.length() == 0){
            return ans;
        }

        Map<Character, String> map = Map.of(
            '2', "abc",
            '3', "def",
            '4', "ghi",
            '5', "jkl",
            '6', "mno",
            '7', "pqrs",
            '8', "tuv",
            '9', "wxyz"
        );

        StringBuilder sb = new StringBuilder();

        backtrack(digits, map, ans, sb, 0);

        return ans;
    }

    public static void backtrack(String digits, Map<Character, String> map, List<String> ans, StringBuilder sb, int idx){
        if(idx == digits.length()){
            ans.add(sb.toString());
            return;
        }

        String val = map.get(digits.charAt(idx));
        for(int i=0; i<val.length(); i++){
            sb.append(val.charAt(i));
            
            backtrack(digits, map, ans, sb, idx + 1);

            sb.deleteCharAt(sb.length() - 1);
        }

        return;
    }
}