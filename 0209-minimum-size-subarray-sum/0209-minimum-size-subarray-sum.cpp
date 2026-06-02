// Method 1: Sliding Window Approach
/*
For this problem, once the current window sum becomes at least target, you should keep shrinking from the left as much as possible while the sum is still valid. That is how you find the minimum length.
*/
class Solution {
public:
    int minSubArrayLen(int target, vector<int>& nums) {
        int n = nums.size();

        int left = 0;
        int right = 0;
        int sum = 0;
        int ans = std::numeric_limits<int>::max();

        while(right < n){
            sum += nums[right];

            while(sum >= target){
                ans = std::min(ans, right - left + 1);
                sum -= nums[left];
                left++;
            }

            right++;
        }

        if(ans == std::numeric_limits<int>::max()){
            return 0;
        }

        return ans;
    }
};





// Method 2: Binary Search and Prefix Sum O(n log(n)) approach
/*
Use **prefix sum + binary search**:

Because all numbers in `nums` are **positive**, the prefix sum array is increasing. That allows binary search.

## Idea

Create prefix sum array:

```cpp
prefix[i] = sum of nums[0] to nums[i - 1]
```

So:

```cpp
sum from index i to j - 1 = prefix[j] - prefix[i]
```

For every starting index `i`, we want the smallest ending index `j` such that:

```cpp
prefix[j] - prefix[i] >= target
```

Rearrange:

```cpp
prefix[j] >= target + prefix[i]
```

So for each `i`, binary search for:

```cpp
target + prefix[i]
```

inside the prefix array.


## Example

```cpp
target = 7
nums = [2, 3, 1, 2, 4, 3]
```

Prefix sum:

```cpp
prefix = [0, 2, 5, 6, 8, 12, 15]
```

For `i = 4`:

```cpp
prefix[i] = 8
need = target + prefix[i]
need = 7 + 8 = 15
```

Find first prefix value `>= 15`.

```cpp
prefix[6] = 15
```

So:

```cpp
j = 6
length = j - i = 6 - 4 = 2
```

That corresponds to:

```cpp
nums[4] to nums[5] = [4, 3]
```

Sum is `7`, length is `2`.

## Complexity

```cpp
Time Complexity: O(n log n)
Space Complexity: O(n)
```
*/

// class Solution {
// public:
//     int minSubArrayLen(int target, vector<int>& nums) {
//         int n = nums.size();

//         vector<long long> prefix(n + 1, 0);

//         // Build prefix sum array
//         for (int i = 0; i < n; i++) {
//             prefix[i + 1] = prefix[i] + nums[i];
//         }

//         int ans = INT_MAX;

//         for (int i = 0; i < n; i++) {
//             long long need = target + prefix[i];

//             // Find the first index j where prefix[j] >= need
//             auto it = lower_bound(prefix.begin(), prefix.end(), need);

//             if (it != prefix.end()) {
//                 int j = it - prefix.begin();

//                 // Subarray is nums[i] to nums[j - 1]
//                 int length = j - i;

//                 ans = min(ans, length);
//             }
//         }

//         if (ans == INT_MAX) {
//             return 0;
//         }

//         return ans;
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna