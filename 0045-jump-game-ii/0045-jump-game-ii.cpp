// Method 1: Greedy approach
/*
## LeetCode 45: Jump Game II (C++)

**Approach: Greedy | Difficulty: Medium**

### Hint 1: Think Greedy
Unlike Jump Game I, you need to find the **minimum number of jumps** to reach the last index.

Instead of trying every jump, track the farthest position reachable with your current jump.

### Hint 2: Track 3 Variables

```cpp
int jumps = 0;
int maxReach = 0;
int currentEnd = 0;
```

- `jumps`: Number of jumps taken.
- `maxReach`: Farthest index reachable from the current range.
- `currentEnd`: Boundary of the current jump.

### Hint 3: Update Maximum Reach

At each index `i`:

```cpp
maxReach = max(maxReach, i + nums[i]);
```

### Hint 4: When to Increment Jumps?

When you reach `currentEnd`, you must use another jump to continue.

```cpp
if (i == currentEnd) {
    jumps++;
    currentEnd = maxReach;
}
```

### Hint 5: Avoid an Extra Jump

**Stop before the last index**, because reaching it doesn't require another jump.

```cpp
for (int i = 0; i < nums.size() - 1; i++)
```

### Complete C++ Solution

```cpp
class Solution {
public:
    int jump(vector<int>& nums) {
        int jumps = 0;
        int maxReach = 0;
        int currentEnd = 0;

        for (int i = 0; i < nums.size() - 1; i++) {
            maxReach = max(maxReach, i + nums[i]);

            if (i == currentEnd) {
                jumps++;
                currentEnd = maxReach;
            }
        }

        return jumps;
    }
};
```

### Complexity

- **Time:** O(n) — Single traversal.
- **Space:** O(1) — Constant extra space.

### Example

`nums = [2, 3, 1, 1, 4]`

- Start at index `0`, jump range extends to index `2`.
- From indices `1` and `2`, the farthest reachable index is `4`.
- Minimum jumps = **2**.

**Key intuition:** Jump Game II is like BFS without a queue. Each jump explores a range of reachable indices, and you choose the next range that extends farthest.

**Interview tip:** Remember the difference:

- **Jump Game I:** Can you reach the end?
- **Jump Game II:** What is the minimum number of jumps to reach the end?
*/
class Solution {
public:
    int jump(vector<int>& nums) {
        int jumps = 0;
        int maxReach = 0;
        int currentEnd = 0;

        for(int i=0; i < nums.size() - 1; i++){
            maxReach = std::max(maxReach, i + nums[i]);

            if(i == currentEnd){
                jumps++;
                currentEnd = maxReach;
            }
        }

        return jumps;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna