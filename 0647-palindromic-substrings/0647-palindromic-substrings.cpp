// Method 1: 1D DP and expand around center
/*
This is an **Expand Around Center** problem.

It is very similar to **5. Longest Palindromic Substring**, except instead of finding the longest palindrome, you need to **count every palindromic substring**.

The key idea is:

```text
every palindrome has a center
```

So for every position, expand outward for both:

```text
odd-length palindromes
even-length palindromes
```

and count every valid expansion.

### Hint 1: Reuse the same center idea

For an odd-length palindrome like:

```text
"aba"
```

the center is:

```text
b
```

So start with:

```cpp
left = i;
right = i;
```

For an even-length palindrome like:

```text
"abba"
```

the center is between the two `b`s.

So start with:

```cpp
left = i;
right = i + 1;
```

For every `i`, check both:

```cpp
expand(i, i);
expand(i, i + 1);
```

---

### Hint 2: The big difference from problem 5

In **Longest Palindromic Substring**, you expanded outward and only cared about the maximum length.

Here, **every successful expansion is one palindrome**.

For example:

```text
"aaa"
```

Starting from the middle `a`:

```text
"a"   → palindrome
"aaa" → palindrome
```

That center contributes:

```text
2
```

palindromic substrings.

So inside your expansion loop, every time:

```cpp
s[left] == s[right]
```

you should increment a counter.

---

### Hint 3: Your expand helper can return a count

Instead of returning palindrome length, let your helper return:

```text
how many palindromes were found from this center
```

Something like:

```cpp
int expand(string& s, int left, int right)
```

Inside:

```cpp
int count = 0;
```

Then:

```cpp
while (
    left >= 0 &&
    right < s.size() &&
    s[left] == s[right]
) {
    count++;

    left--;
    right++;
}
```

Finally:

```cpp
return count;
```

---

### Hint 4: Why does `count++` happen before expanding?

Suppose:

```text
s = "aba"
```

Start:

```cpp
left = 1;
right = 1;
```

You have:

```text
"b"
```

which is already a valid palindrome.

So:

```cpp
count++;
```

Then expand:

```cpp
left--;
right++;
```

Now:

```text
"aba"
```

is another palindrome, so count again.

Each valid pair of boundaries represents one distinct substring.

---

### Hint 5: Every single character is automatically a palindrome

For:

```cpp
expand(s, i, i)
```

the first comparison is always:

```cpp
s[i] == s[i]
```

So every character contributes at least one palindrome.

Example:

```text
"abc"
```

contains:

```text
"a"
"b"
"c"
```

So even if there are no longer palindromes, the answer is at least:

```cpp
s.size()
```

---

### Hint 6: Example with `"aaa"`

Let's count.

At `i = 0`:

Odd center:

```text
"a"
```

→ 1 palindrome

Even center:

```text
"aa"
```

→ 1 palindrome

At `i = 1`:

Odd center:

```text
"a"
"aaa"
```

→ 2 palindromes

Even center:

```text
"aa"
```

→ 1 palindrome

At `i = 2`:

Odd center:

```text
"a"
```

→ 1 palindrome

Total:

```text
1 + 1 + 2 + 1 + 1 = 6
```

Those substrings are:

```text
"a" at index 0
"a" at index 1
"a" at index 2
"aa" at indices 0-1
"aa" at indices 1-2
"aaa"
```

---

### Hint 7: Identical strings at different positions count separately

This is important.

For:

```text
"aaa"
```

there are three substrings equal to:

```text
"a"
```

and two substrings equal to:

```text
"aa"
```

They count separately because they occur at different positions.

You're counting substrings, not unique string values.

---

### Hint 8: Main loop

Your main function can look conceptually like:

```cpp
int ans = 0;

for (int i = 0; i < s.size(); i++) {
    ans += expand(s, i, i);
    ans += expand(s, i, i + 1);
}
```

Then return:

```cpp
ans
```

No need to track:

```cpp
start
maxLen
```

like you did in problem 5.

---

### Hint 9: Why this counts every palindrome exactly once

Every palindrome has exactly one center.

For example:

```text
"racecar"
```

has center:

```text
e
```

and:

```text
"abba"
```

has center:

```text
between the two b's
```

So when you try every odd and even center, every palindromic substring will be discovered from exactly one center.

---

### Hint 10: Complexity

There are:

```text
O(n)
```

possible centers.

Each center may expand up to:

```text
O(n)
```

characters.

So:

```text
Time:  O(n²)
Space: O(1)
```

---

### Skeleton

```cpp
class Solution {
private:
    int expand(
        string& s,
        int left,
        int right
    ) {
        int count = 0;

        while (
            left >= 0 &&
            right < s.size() &&
            s[left] == s[right]
        ) {
            // found one palindrome

            // expand outward
        }

        return count;
    }

public:
    int countSubstrings(string s) {
        int ans = 0;

        for (int i = 0; i < s.size(); i++) {

            // odd-length palindromes

            // even-length palindromes
        }

        return ans;
    }
};
```

The core idea is:

```text
try every center
        ↓
expand while characters match
        ↓
every successful expansion = one palindrome
        ↓
count them all
```

The biggest difference from **Longest Palindromic Substring** is that you do **not** only keep the longest one—every valid expansion contributes `1` to the answer.
*/
class Solution {
private:
    int expand(string& s, int left, int right){
        int count = 0;

        while(left >= 0 && right < s.size() && s[left] == s[right]){
            count++;

            left--;
            right++;
        }

        return count;
    }

public:
    int countSubstrings(string s) {
        int ans = 0;

        for(int i=0; i<s.size(); i++){
            ans += expand(s, i, i);
            ans += expand(s, i, i+1);
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna