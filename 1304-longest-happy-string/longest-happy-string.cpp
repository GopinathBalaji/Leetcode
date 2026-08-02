// Method 1: Using MaxHeap
/*
### Hint 1

You have counts for only three characters:

```cpp
a, b, c
```

At every step, try to use the character with the largest remaining count.

A **max-heap** is a natural fit.

---

### Hint 2

Store:

```cpp
{remainingCount, character}
```

For example:

```cpp
priority_queue<pair<int, char>> maxHeap;
```

Only push characters whose count is greater than zero.

---

### Hint 3

Before appending the most frequent character, check the last two characters already in the result.

You cannot append `ch` when:

```cpp
result.size() >= 2 &&
result[result.size() - 1] == ch &&
result[result.size() - 2] == ch
```

because that would create three identical consecutive characters.

---

### Hint 4

When the most frequent character cannot be used, temporarily pop the second-most frequent character.

Append that second character instead, decrease its count, and then put the first character back into the heap.

Think along these lines:

```cpp
auto first = maxHeap.top();
maxHeap.pop();

if (first would create three consecutive characters) {
    if (maxHeap.empty()) {
        break;
    }

    auto second = maxHeap.top();
    maxHeap.pop();

    // append second
    // decrease second's count
    // push second back if count remains

    maxHeap.push(first);
}
```

---

### Hint 5

When the most frequent character is safe to use:

1. Append it.
2. Decrease its remaining count.
3. Push it back if its count is still positive.

---

### Hint 6

Why do you stop when the preferred character is invalid and the heap has no second option?

Because every remaining character is the same, and adding it would create an invalid substring such as:

```text
aaa
```

So the current result is already the longest possible happy string.

---

### Main structure

```cpp
while (!maxHeap.empty()) {
    auto first = maxHeap.top();
    maxHeap.pop();

    if (first cannot be appended) {
        if (maxHeap.empty()) {
            break;
        }

        // use second-best character
    } else {
        // use first character
    }
}
```

### Complexity

The heap contains at most three elements:

* Time: `O(a + b + c)`
* Space: `O(1)` excluding the result string.
*/
class Solution {
public:
    string longestDiverseString(int a, int b, int c) {
        string result = "";
        priority_queue<pair<int, char>> maxHeap;

        if(a > 0){
            maxHeap.push({a, 'a'});
        }
        if(b > 0){
            maxHeap.push({b, 'b'});
        }
        if(c > 0){
            maxHeap.push({c, 'c'});
        }

        
        while(!maxHeap.empty()){
            auto first = maxHeap.top();
            maxHeap.pop();

            if(result.size() >= 2 && result[result.size() - 1] == first.second && result[result.size() - 2] == first.second){
                if(maxHeap.empty()){
                    break;
                }

                auto second = maxHeap.top();
                maxHeap.pop();

                result += second.second;

                if(second.first - 1 > 0){
                    maxHeap.push({second.first - 1, second.second});
                }

                maxHeap.push(first);
            }else{
                result += first.second;

                if(first.first - 1 > 0){
                    maxHeap.push({first.first - 1, first.second});
                }
            }
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna