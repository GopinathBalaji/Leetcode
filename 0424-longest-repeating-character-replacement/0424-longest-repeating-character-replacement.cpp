// Method 1: Sliding Window approach with fixed size array for hashmap
/*
## Hint 1: Understand what replacement means

You are given a string:

```cpp
s = "AABABBA"
k = 1
```

You can replace at most `k` characters.

You want the longest substring that can become all one repeated character.

Example:

```cpp
"ABBA"
```

If you replace `'A'` with `'B'`, it becomes:

```cpp
"BBBB"
```

So length `4` is possible.

---

## Hint 2: Think sliding window

You want to maintain a window:

```cpp
s[left...right]
```

Inside this window, ask:

Can this window be converted into all the same character using at most `k` replacements?

---

## Hint 3: How many replacements does a window need?

Suppose the current window is:

```cpp
"AABAB"
```

Length is `5`.

Character counts:

```cpp
A -> 3
B -> 2
```

To make the whole window one repeated character, it is best to keep the most frequent character.

Here, keep `'A'`.

You only need to replace the other characters:

```cpp
window length - max frequency
= 5 - 3
= 2
```

So this window needs `2` replacements.

---

## Hint 4: Window is valid if replacements needed <= k

For any window:

```cpp
windowSize = right - left + 1
maxFreq = count of most frequent character in the window
```

The number of replacements needed is:

```cpp
windowSize - maxFreq
```

So the window is valid if:

```cpp
windowSize - maxFreq <= k
```

---

## Hint 5: Expand right, shrink left if invalid

Move `right` through the string.

Each time you include `s[right]`, update its frequency.

Then check:

```cpp
(right - left + 1) - maxFreq > k
```

If this is true, the window is invalid, so shrink from the left:

```cpp
count[s[left]]--;
left++;
```

---

## Hint 6: Track the maximum valid window size

After making sure the window is valid, update:

```cpp
ans = max(ans, right - left + 1);
```

---

## Hint 7: Since there are only uppercase English letters

The problem uses uppercase English letters, so you can use an array of size `26`:

```cpp
vector<int> freq(26, 0);
```

For a character:

```cpp
int idx = s[right] - 'A';
freq[idx]++;
```

---

## Hint 8: Important trick about `maxFreq`

You can keep `maxFreq` as the largest frequency ever seen while expanding the window:

```cpp
maxFreq = max(maxFreq, freq[s[right] - 'A']);
```

You do not necessarily need to decrease `maxFreq` when moving `left`.

Why? Because `maxFreq` is used only to decide when the window becomes too large. Even if it is slightly stale, the final answer still remains correct because it only allows the window size to grow when such a size was possible at some point.

For learning, you can also recompute `maxFreq` every time from the 26 counts. That is still efficient because `26` is constant.

---

## Core idea

```cpp
Use sliding window.
Keep character frequencies inside the window.
The window is valid if:

window length - most frequent character count <= k
```

So the main condition is:

```cpp
while ((right - left + 1) - maxFreq > k) {
    shrink from the left
}
```

Time complexity:

```cpp
O(n)
```

Space complexity:

```cpp
O(1)
```

because there are only 26 uppercase letters.
*/
class Solution {
public:
    int characterReplacement(string s, int k) {
        int n = s.size();
        if(n <= 1){
            return n;
        }

        vector<int> freq(26, 0);
        int left = 0;
        int right = 0;
        int ans = 0;
        int maxFreq = 0;
        
        while(right < n){
            int idx = s[right] - 'A';
            freq[idx]++;
            maxFreq = std::max(maxFreq, freq[idx]);

            while((right - left + 1) - maxFreq > k){
                int leftIdx = s[left] - 'A';
                freq[leftIdx]--;
                left++;
            }

            ans = std::max(ans, right - left + 1);

            right++;
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna