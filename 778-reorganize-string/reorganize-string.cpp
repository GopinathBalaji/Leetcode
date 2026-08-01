// Method 1: Using MaxHeap
/*
### Hint 1

Count the frequency of every character.

The character with the highest frequency is the hardest one to place without creating adjacent duplicates.

---

### Hint 2

Use a **max-heap** containing:

```cpp
{frequency, character}
```

The heap lets you repeatedly choose the character with the largest remaining count.

```cpp
priority_queue<pair<int, char>> maxHeap;
```

---

### Hint 3

You should not immediately put the character you just used back into the heap.

Otherwise, it could be selected again on the next iteration.

Keep the previously used character outside the heap for one round:

```cpp
pair<int, char> previous = {0, '#'};
```

---

### Hint 4

At each step:

1. Pop the most frequent available character.
2. Append it to the result.
3. Decrease its frequency.
4. Put the previous character back into the heap if it still has occurrences.
5. Make the current character the new `previous`.

The order of steps 4 and 5 matters.

---

### Hint 5

The main loop can look like:

```cpp
while (!maxHeap.empty()) {
    auto current = maxHeap.top();
    maxHeap.pop();

    // append current character
    // decrease current frequency

    // reinsert previous if still needed

    // previous = current
}
```

Since the current character remains outside the heap until the following iteration, it cannot be selected twice consecutively.

---

### Hint 6

After the heap becomes empty, check whether the previous character still has an unused occurrence.

If it does, then reorganizing the string was impossible:

```cpp
if (previous.first > 0) {
    return "";
}
```

---

### Hint 7

You can detect impossibility before building the result.

For a string of length `n`, the largest frequency must satisfy:

```cpp
maxFrequency <= (n + 1) / 2
```

For example:

```text
"aaab"
```

has length `4`, but `a` occurs `3` times:

```text
3 > (4 + 1) / 2
```

so no valid arrangement exists.

---

### Complexity

With at most 26 lowercase letters:

* Time: `O(n log 26)`, effectively `O(n)`
* Space: `O(26)`, effectively `O(1)`
*/
class Solution {
public:
    string reorganizeString(string s) {
        unordered_map<char, int> freq;
        int maxFreq = 0;

        for(char c: s){
            freq[c]++;
            maxFreq = std::max(maxFreq, freq[c]);
        }

        if(maxFreq > (s.size() + 1) / 2){
            return "";
        }

        priority_queue<pair<int, char>> maxHeap;

        for(const auto& [c, value]: freq){
            maxHeap.push({value, c});
        }
        
        pair<int, char> previous = {0, '#'};
        string ans = "";

        while(!maxHeap.empty()){
            auto current = maxHeap.top();
            maxHeap.pop();

            int freq = current.first;
            char c = current.second;

            ans += c;
            freq--;

            if(previous.first > 0){
                maxHeap.push(previous);
            }

            previous = {freq, c};
        }

        if(previous.first > 0){
            return "";
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna