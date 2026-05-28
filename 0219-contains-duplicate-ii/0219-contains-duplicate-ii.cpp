// Method 1: Using HashMap
/*
Use:

unordered_map<int, int> lastSeen;

For each index i:

if nums[i] already exists in lastSeen:
    if i - lastSeen[nums[i]] <= k:
        return true

lastSeen[nums[i]] = i

At the end:

return false;
*/
class Solution {
public:
    bool containsNearbyDuplicate(vector<int>& nums, int k) {
        int n = nums.size();

        if(n <= 1){
            return false;
        }

        unordered_map<int, int> lastSeen;

        for(int i=0; i<n; i++){
            if(lastSeen.find(nums[i]) != lastSeen.end()){
                if(i - lastSeen[nums[i]] <= k){
                    return true;
                }
            }

            lastSeen[nums[i]] = i;
        }

        return false;
    }
};






// Method 2: Sliding Window Approach
/*
### Idea

At every index `i`, the `window` stores numbers from the previous at most `k` positions.

So before inserting `nums[i]`, the set represents:

```cpp
nums[i - k], nums[i - k + 1], ..., nums[i - 1]
```

If `nums[i]` is already inside the set, then there exists some previous index `j` such that:

```cpp
nums[i] == nums[j]
```

and:

```cpp
i - j <= k
```

So we return `true`.

After inserting `nums[i]`, if the window size becomes bigger than `k`, remove the element that is now too far away:

```cpp
nums[i - k]
```

### Example

```cpp
nums = [1, 2, 3, 1]
k = 3
```

At `i = 3`, the window contains:

```cpp
{1, 2, 3}
```

Now `nums[3] = 1`, and `1` is already in the window, so return:

```cpp
true
```

Time complexity:

```cpp
O(n)
```

Space complexity:

```cpp
O(k)
```
*/
// class Solution {
// public:
//     bool containsNearbyDuplicate(vector<int>& nums, int k) {
//         unordered_set<int> window;

//         for (int i = 0; i < nums.size(); i++) {
//             // If nums[i] is already in the current window,
//             // then we found the same value within distance k.
//             if (window.find(nums[i]) != window.end()) {
//                 return true;
//             }

//             // Add current number to the window
//             window.insert(nums[i]);

//             // Keep only the last k elements in the window
//             if (window.size() > k) {
//                 window.erase(nums[i - k]);
//             }
//         }

//         return false;
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna