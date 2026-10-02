// Method 1: 1D DP and Expand around Center
/*
This is an **Expand Around Center** problem.

The key idea is that every palindrome has a **center**.

That center can be:

```text
one character
```

for odd-length palindromes, or:

```text
between two characters
```

for even-length palindromes.

From each possible center, expand outward while the characters match.

### Hint 1: What makes a substring a palindrome?

A substring is a palindrome when:

```text
left side == reversed right side
```

More concretely, while expanding:

```cpp
s[left] == s[right]
```

you can keep going outward.

So if:

```cpp
s[left] == s[right]
```

then try:

```cpp
left--;
right++;
```

---

### Hint 2: Every palindrome has a center

Consider:

```text
"racecar"
```

Its center is:

```text
e
```

So you can start at:

```cpp
left = 3;
right = 3;
```

and expand:

```text
e
cec
aceca
racecar
```

This handles **odd-length** palindromes.

---

### Hint 3: Even-length palindromes have a different center

Consider:

```text
"abba"
```

There is no single center character.

The center lies between the two `b`s:

```text
ab|ba
```

So start with:

```cpp
left = 1;
right = 2;
```

and expand:

```text
bb
abba
```

That means for every index `i`, you should check two centers:

```cpp
expand(i, i);       // odd length
expand(i, i + 1);   // even length
```

---

### Hint 4: Write a reusable expand helper

A helper can take:

```cpp
int left,
int right
```

and expand while:

```cpp
left >= 0
right < s.size()
s[left] == s[right]
```

Conceptually:

```cpp
while (
    left >= 0 &&
    right < s.size() &&
    s[left] == s[right]
) {
    left--;
    right++;
}
```

One important detail:

When the loop ends, you have gone **one step too far**.

So the actual palindrome boundaries are:

```text
left + 1
right - 1
```

---

### Hint 5: How do you calculate the palindrome length?

After expansion stops, the valid palindrome is:

```text
[left + 1 ... right - 1]
```

Its length is:

```cpp
right - left - 1
```

Example:

```text
"aba"
```

After expanding:

```text
left = -1
right = 3
```

So:

```cpp
length = 3 - (-1) - 1;
```

which gives:

```text
3
```

---

### Hint 6: Track the best substring boundaries

Instead of constantly creating substrings, keep:

```cpp
int start = 0;
int maxLen = 1;
```

Whenever you find a palindrome longer than your current best:

```cpp
if (len > maxLen) {
    // update start
    // update maxLen
}
```

Then at the very end:

```cpp
return s.substr(start, maxLen);
```

---

### Hint 7: Converting a center + length into a start index

Suppose you are centered around index `i`, and expansion gives you a palindrome of length `len`.

You can derive its start position.

For an odd palindrome like:

```text
"babad"
   ^
```

or an even palindrome like:

```text
"cbbd"
  ^^
```

one common formula is:

```cpp
int start = i - (len - 1) / 2;
```

But if you find this formula confusing, your helper can instead return the actual:

```text
left boundary
right boundary
```

after expansion.

That is often easier to reason about.

---

### Hint 8: Try every possible center

Loop through every index:

```cpp
for (int i = 0; i < s.size(); i++) {
```

and calculate:

```cpp
int odd = expand(s, i, i);
int even = expand(s, i, i + 1);
```

Then:

```cpp
int len = max(odd, even);
```

If `len` is larger than your current best, update your answer.

---

### Hint 9: Example

Take:

```cpp
s = "babad";
```

At index `1`:

```text
b a b a d
  ^
```

Start with:

```cpp
left = 1;
right = 1;
```

Expand:

```text
"a"
```

then:

```text
"bab"
```

Then expansion stops because the next characters don't match.

So one palindrome is:

```text
"bab"
```

At another center, you may find:

```text
"aba"
```

Both are valid answers because they have the same maximum length.

---

### Hint 10: Why not check every substring?

A brute-force approach might:

```text
generate every substring
+
check whether each one is a palindrome
```

There are roughly:

```text
O(n²)
```

substrings, and checking each could take:

```text
O(n)
```

giving:

```text
O(n³)
```

Expand-around-center reduces this to:

```text
Time:  O(n²)
Space: O(1)
```

because there are only about:

```text
2n
```

possible centers, and each expansion takes at most `O(n)`.

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
        while (
            left >= 0 &&
            right < s.size() &&
            s[left] == s[right]
        ) {
            left--;
            right++;
        }

        // return length of valid palindrome
        // remember left/right are now one step too far
    }

public:
    string longestPalindrome(string s) {
        int start = 0;
        int maxLen = 1;

        for (int i = 0; i < s.size(); i++) {

            int odd = expand(s, i, i);

            int even = expand(s, i, i + 1);

            int len = max(odd, even);

            if (len > maxLen) {
                // compute new start
                // update maxLen
            }
        }

        return s.substr(start, maxLen);
    }
};
```

The core idea is:

```text
every palindrome has a center
        ↓
try every possible center
        ↓
expand while characters match
        ↓
track the longest one found
```

The trickiest implementation detail is remembering that you need to check **both odd and even centers**.
*/
class Solution {
private:
    int expand(string& s, int left, int right){
        while(left >= 0 && right < s.size() && s[left] == s[right]){
            left--;
            right++;
        }

        return right - left - 1; 
    }

public:
    string longestPalindrome(string s) {
        int n = s.size();

        int start = 0;
        int maxLen = 1;

        for(int i=0; i<n; i++){
            int odd = expand(s, i, i);
            int even = expand(s, i, i+1);

            int len = std::max(odd, even);
            
            if(len > maxLen){
                start = i - (len - 1) / 2;
                maxLen = len;
            }
        }

        return s.substr(start, maxLen);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna