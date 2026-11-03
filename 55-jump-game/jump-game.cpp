// Method 1: Greedy
/*
## LeetCode 55: Jump Game (C++)

**Approach: Greedy | Difficulty: Medium**

### Hint 1: Think Greedy
You don't need to try every possible jump. Instead, track the **farthest index** you can reach.

### Hint 2: Update Maximum Reach
At each index `i`, calculate:

```cpp
maxReach = max(maxReach, i + nums[i]);
```

### Hint 3: Detect Failure
If your current index exceeds `maxReach`, you cannot move forward.

```cpp
if (i > maxReach) return false;
```

### Hint 4: Detect Success
If you can reach or pass the last index, return `true`.

```cpp
if (maxReach >= nums.size() - 1) return true;
```

### Complete C++ Solution

```cpp
class Solution {
public:
    bool canJump(vector<int>& nums) {
        int maxReach = 0;

        for (int i = 0; i < nums.size(); i++) {
            if (i > maxReach) return false;

            maxReach = max(maxReach, i + nums[i]);

            if (maxReach >= nums.size() - 1)
                return true;
        }

        return true;
    }
};
```

### Complexity
- **Time:** O(n) — Single traversal.
- **Space:** O(1) — Only one extra variable.

**Key intuition:** You don't need to know *how* to reach an index, only whether it's reachable.

**Interview tip:** Greedy works because every reachable position gives you another opportunity to extend your maximum reach.
*/
class Solution {
public:
    bool canJump(vector<int>& nums) {
        int maxReach = 0;

        for(int i=0; i<nums.size(); i++){
            if(i > maxReach){
                return false;
            }

            maxReach = std::max(maxReach, i + nums[i]);

            if(maxReach >= nums.size() - 1){
                return true;
            }
        }

        return true;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna