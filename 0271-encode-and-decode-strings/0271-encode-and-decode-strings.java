// Method 1: Encode by adding string len + # + string
/*
# WHAT WAS I DOING WRONG:
Your **encode idea** (length + `#` + string) is the right pattern, but your **decode is incorrect** in a few important ways.

## What’s wrong

### 1) You only read **one digit** of the length

```java
int len = str.charAt(i) - '0';
```

This assumes the length is a single digit (0–9).
But strings can easily have length 10, 100, etc.

Example:

* original string `"abcdefghij"` has length 10
* encoded starts with `"10#abcdefghij"`
* your decode reads `len = 1` (only first digit), totally wrong.

✅ Fix: parse length as an integer by scanning until `#`.

---

### 2) You assume the separator `#` is always exactly at `i+1`

```java
int start = i + 2;
```

That’s only true if the length is one digit.

If length is `"12"`, then encoded is `"12#..."`, and `#` is at `i+2`, not `i+1`.

✅ Fix: find the position of `#` (or scan for it), then substring starts after it.

---

### 3) You don’t advance `i` correctly for multi-digit lengths

You do:

```java
i = end;
```

But `end` was computed assuming 1-digit length header. With multi-digit lengths, the header is longer, so your indices drift and you desync.

---

### 4) Minor: decode can throw if input is malformed

Not usually required for LeetCode, but with wrong parsing you’ll easily get `StringIndexOutOfBoundsException`.


### Why this works even if strings contain '#'

Because you don’t split on `#` in the content; you use the length to know exactly how many characters to read next.

---

### Quick failing test for your version

Input:

```java
["hello", "abcdefghij"]  // lengths 5 and 10
```

Encoded:

```text
"5#hello10#abcdefghij"
```

Your decode reads `len=5` ok, then next `len=1` instead of 10 → breaks.

---

So the main issue: **length parsing must handle multiple digits and must locate `#` dynamically.**
*/
class Solution {

    public String encode(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            sb.append(s.length()).append('#').append(s);
        }
        return sb.toString();
    }

    public List<String> decode(String str) {
        List<String> ans = new ArrayList<>();
        int i = 0;

        while (i < str.length()) {
            // 1) read length (could be multiple digits)
            int j = i;
            while (str.charAt(j) != '#') j++;
            int len = Integer.parseInt(str.substring(i, j));

            // 2) read the next 'len' characters
            int start = j + 1;
            int end = start + len;
            ans.add(str.substring(start, end));

            // 3) move i forward
            i = end;
        }

        return ans;
    }
}
