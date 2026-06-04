// Method 1: Sliding Window and HashMap Approach
/*
### Hint 1: Think sliding window

You need the **smallest substring of `s`** that contains all characters of `t`.

This is a classic **variable-size sliding window** problem.

Use two pointers:

```cpp
left = 0
right = 0
```

Expand `right` until the current window contains all characters of `t`.

Then shrink from `left` while the window is still valid.

---

### Hint 2: Character frequencies matter

`t` may contain duplicate characters.

Example:

```text
s = "AAABBC"
t = "AABC"
```

The window must contain:

```text
A -> 2 times
B -> 1 time
C -> 1 time
```

So you need a frequency map for `t`.

```cpp
need[c] = how many times c is required
```

Also maintain another frequency map for the current window:

```cpp
window[c] = how many times c appears in current window
```

---

### Hint 3: Track how many requirements are satisfied

Instead of checking the whole frequency map every time, keep a variable like:

```cpp
formed
```

Also keep:

```cpp
required = number of unique characters in t
```

A character is considered satisfied when:

```cpp
window[c] == need[c]
```

When all unique characters are satisfied:

```cpp
formed == required
```

Then your current window is valid.


### Hint 5: Why this works

The window only becomes valid after expanding `right`.

Once valid, moving `left` tries to remove unnecessary characters.

The moment removing `s[left]` makes the window invalid, you stop shrinking and continue expanding again.

This guarantees you check every possible minimal valid window efficiently.

---

### Hint 6: Edge cases

Think about these:

```text
s = "ADOBECODEBANC", t = "ABC"  -> "BANC"
s = "a", t = "a"                -> "a"
s = "a", t = "aa"               -> ""
s = "ab", t = "b"               -> "b"
```

The answer should be empty if no valid window exists.

---

Time Complexity: O(m + n)
Building the frequency array from t takes O(n).
The sliding window over s takes O(m) because both left and right only move forward.

### Core idea in one sentence

Use a sliding window that expands until it contains all characters of `t`, then shrinks from the left to find the minimum valid window.
*/
class Solution {
public:
    string minWindow(string s, string t) {
        int ns = s.size();
        int nt = t.size();

        if(ns < nt){
            return "";
        }

        unordered_map<char, int> window;
        unordered_map<char, int> need;
        for(char c : t){
            need[c]++;
        }

        int left = 0;
        int right = 0;

        int formed = 0;
        int required = need.size();

        int minLen = INT_MAX;
        int substrStart = 0;

        while(right < ns){
            window[s[right]]++;

            if(need.find(s[right]) != need.end() && window[s[right]] == need[s[right]]){
                formed++;
            }

            while(formed == required){
                int currLen = right - left + 1;

                if(currLen < minLen){
                    minLen = currLen;
                    substrStart = left;
                }

                window[s[left]]--;

                if(need.find(s[left]) != need.end() && window[s[left]] < need[s[left]]){
                    formed--;
                }

                left++;
            }

            right++;
        }

        if(minLen == INT_MAX){
            return "";
        }

        return s.substr(substrStart, minLen);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna