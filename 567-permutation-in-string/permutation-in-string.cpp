// Method 1: Slidind window and hashmap approach
/*
## Hint 1: What does “permutation” mean here?

You are given:

```cpp
s1 = "ab"
s2 = "eidbaooo"
```

You need to check whether **any substring of `s2`** is a permutation of `s1`.

Since `"ab"` has permutations:

```cpp
"ab", "ba"
```

And `s2` contains:

```cpp
"ba"
```

the answer is `true`.

---

## Hint 2: A permutation has the same character counts

Instead of generating all permutations, compare frequencies.

For example:

```cpp
s1 = "ab"
```

Frequency:

```cpp
a -> 1
b -> 1
```

Substring:

```cpp
"ba"
```

Frequency:

```cpp
a -> 1
b -> 1
```

Same frequency means it is a permutation.

---

## Hint 3: Use a fixed-size sliding window

Every valid substring must have length:

```cpp
s1.size()
```

So slide a window of size `s1.length()` over `s2`.

Example:

```cpp
s1 = "ab"
s2 = "eidbaooo"
```

Window size is `2`.

Check substrings:

```cpp
"ei"
"id"
"db"
"ba"  // match
```

---

## Hint 4: Use two frequency arrays

Since the string contains lowercase English letters, use arrays of size `26`.

```cpp
vector<int> freq1(26, 0);
vector<int> freq2(26, 0);
```

Build `freq1` from `s1`.

Build/update `freq2` for the current window in `s2`.

If:

```cpp
freq1 == freq2
```

then the current window is a permutation.

---

## Hint 5: How to slide the window

For each `right`, add:

```cpp
s2[right]
```

to the window frequency.

If the window becomes larger than `s1.size()`, remove the leftmost character:

```cpp
s2[left]
```

Then move:

```cpp
left++;
```

So your window always stays length `s1.size()`.

---

## Hint 6: Basic logic

```cpp
for each character in s1:
    freq1[ch]++

left = 0

for right from 0 to s2.size() - 1:
    add s2[right] to freq2

    if window size > s1.size():
        remove s2[left] from freq2
        left++

    if freq1 == freq2:
        return true

return false
```

---

## Hint 7: Edge case

If `s1` is longer than `s2`, it is impossible:

```cpp
if (s1.size() > s2.size()) return false;
```

Core idea:

```cpp
Permutation check = same character counts.
Use a sliding window of size s1.length() over s2.
```
*/
class Solution {
public:
    bool checkInclusion(string s1, string s2) {
        int n1 = s1.size();
        int n2 = s2.size();

        if(n2 < n1){
            return false;
        }

        vector<int> freq1(26, 0);
        vector<int> freq2(26, 0);

        for(char c: s1){
            int idx = c - 'a';
            freq1[idx]++;
        }

        int left = 0;
        int right = 0;

        while(right < n2){
            int idx = s2[right] - 'a';
            freq2[idx]++;

            if((right - left + 1) > n1){
                int leftIdx = s2[left] - 'a';
                freq2[leftIdx]--;
                left++;
            }

            if(freq1 == freq2){
                return true;
            }

            right++;
        }

        return false;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna