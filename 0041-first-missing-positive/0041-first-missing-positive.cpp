// Method 1: O(n) time and uses O(1) auxiliary space
/*
### Hint 1: Ignore useless numbers

For an array of size `n`, the answer must be in the range:

```cpp
1 to n + 1
```

So any number:

```cpp
<= 0
> n
```

does not directly matter.

Example:

```cpp
nums = [3, 4, -1, 1]
n = 4
```

The answer must be one of:

```cpp
1, 2, 3, 4, 5
```

---

### Hint 2: Try to put each number in its “correct” index

Since array indices are `0`-based, the number `x` should ideally be placed at index:

```cpp
x - 1
```

So:

```cpp
1 should be at index 0
2 should be at index 1
3 should be at index 2
...
n should be at index n - 1
```

Example target arrangement:

```cpp
nums = [1, 2, 3, 4]
```

---

### Hint 3: Use the array itself as storage

The problem asks for:

```cpp
O(n) time
O(1) extra space
```

So you should not use a `set`, `unordered_set`, or extra boolean array.

Instead, rearrange numbers inside `nums`.

---

### Hint 4: Swap while the current number belongs somewhere else

For each index `i`, while `nums[i]` is a valid positive number and is not already in the correct position, swap it into its correct position.

Condition idea:

```cpp
while (
    nums[i] >= 1 &&
    nums[i] <= n &&
    nums[nums[i] - 1] != nums[i]
)
```

Then swap:

```cpp
swap(nums[i], nums[nums[i] - 1]);
```

The duplicate check is important. Without it, cases like this can loop forever:

```cpp
[1, 1]
```

---

### Hint 5: After rearranging, scan again

After the placement step, the first index `i` where this is false:

```cpp
nums[i] == i + 1
```

gives the answer:

```cpp
i + 1
```

If all positions are correct, then the answer is:

```cpp
n + 1
```

---

### Example walkthrough

```cpp
nums = [3, 4, -1, 1]
```

Try to place each valid number:

```cpp
3 should go to index 2
4 should go to index 3
1 should go to index 0
```

After rearrangement, the array becomes something like:

```cpp
[1, -1, 3, 4]
```

Now scan:

```cpp
index 0 has 1 correct
index 1 should have 2, but has -1
```

So answer is:

```cpp
2
```

Core idea:

```cpp
Put value x at index x - 1.
Then find the first missing position.
```
*/
class Solution {
public:
    int firstMissingPositive(vector<int>& nums) {
        int n = nums.size();
        int i = 0;

        while(i < n){
            if(nums[i] >= 1 && nums[i] <= n && nums[nums[i] - 1] != nums[i]){
                std::swap(nums[i], nums[nums[i] - 1]);
            }else{
                i++;
            }
        }

        for(int i=0; i<n; i++){
            if(nums[i] != i+1){
                return i+1;
            }
        }

        return n + 1;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna