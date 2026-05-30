// Method 1: Using Sliding Window with Set and removing character one by one
/*
## Hint 1: You need a substring, not a subsequence

A **substring** must be continuous.

Example:

```cpp
s = "abcabcbb"
```

Valid substrings:

```cpp
"abc"
"bca"
"cab"
```

Not a substring:

```cpp
"acb"
```

because those characters are not continuous in that order.

---

## Hint 2: Think sliding window

You want to maintain a window:

```cpp
s[left...right]
```

such that this window has **no repeating characters**.

As you move `right`, you include a new character.

If the new character creates a duplicate, move `left` until the window becomes valid again.

---

## Hint 3: Use a set to track characters in the current window

You can use:

```cpp
unordered_set<char> seen;
```

This set contains all characters currently inside the window.

When moving `right`, check:

```cpp
seen.count(s[right])
```

If it is not present, add it.

If it is already present, you need to remove characters from the left until the duplicate is gone.

---

## Hint 4: Window expansion and shrinking

For each `right`:

```cpp
while s[right] is already in seen:
    remove s[left] from seen
    left++
```

Then after the duplicate is gone:

```cpp
insert s[right]
```

Now the window is valid again.

Update answer:

```cpp
ans = max(ans, right - left + 1)
```

---

## Hint 5: Example

```cpp
s = "abcabcbb"
```

Start:

```cpp
left = 0
seen = {}
ans = 0
```

Process characters:

```cpp
right = 0, char = 'a'
window = "a"
ans = 1

right = 1, char = 'b'
window = "ab"
ans = 2

right = 2, char = 'c'
window = "abc"
ans = 3
```

Now:

```cpp
right = 3, char = 'a'
```

`'a'` is already in the window `"abc"`.

So move `left` and remove characters until `'a'` is gone:

```cpp
remove 'a'
left = 1
```

Now insert the new `'a'`.

Current window:

```cpp
"bca"
```

Length is still `3`.

---

## Core idea

```cpp
Use a sliding window.
Keep expanding right.
If a duplicate appears, move left until the window has no duplicates.
Track the maximum window length.
```

Time complexity:

```cpp
O(n)
```

Space complexity:

```cpp
O(1)
```

Technically `O(1)` because the character set size is bounded, but you can also think of it as `O(k)` where `k` is the number of possible characters.
*/
class Solution {
public:
    int lengthOfLongestSubstring(string s) {
        int n = s.size();
        if(n <= 1){
            return n;
        }

        unordered_set<char> seen;
        int right = 0;
        int left = 0;
        int ans = 0;

        while(right < n){
            if(seen.count(s[right])){
                while(seen.count(s[right])){
                    seen.erase(s[left]);
                    left++;
                }
            }

            seen.insert(s[right]);
            ans = std::max(ans, right - left + 1);
            right++;
        }

        return ans;
    }
};






// Method 2: Sliding Window HashMap approach
/*

## Hint 6: There is also an optimized map approach

Instead of removing one by one, you can store the last index of each character:

```cpp
unordered_map<char, int> lastSeen;
```

When you see a repeated character, jump `left` directly:

```cpp
left = max(left, lastSeen[s[right]] + 1);
```

But the `unordered_set` sliding window approach is easier to understand first.

---
*/
// class Solution {
// public:
//     int lengthOfLongestSubstring(string s) {
//         unordered_map<char, int> lastSeen;

//         int left = 0;
//         int ans = 0;

//         for (int right = 0; right < s.size(); right++) {
//             char ch = s[right];

//             // If ch was seen before and it is inside the current window,
//             // move left just after its previous position.
//             if (lastSeen.find(ch) != lastSeen.end()) {
//                 left = max(left, lastSeen[ch] + 1);
//             }

//             // Update latest position of current character
//             lastSeen[ch] = right;

//             // Current valid window length
//             ans = max(ans, right - left + 1);
//         }

//         return ans;
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna