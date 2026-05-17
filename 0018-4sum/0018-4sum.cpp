// Method 1: Fix 2 numbers and then use the two pointer approach used for 2sum
/*
### Hint 1: Think of it as an extension of 3Sum

For **3Sum**, you usually:

```cpp
sort(nums.begin(), nums.end());
fix one number;
use two pointers for the remaining two numbers;
```

For **4Sum**, do the same idea but fix **two numbers** first.

So the structure is:

```cpp
sort(nums.begin(), nums.end());

for first number:
    for second number:
        use two pointers to find remaining two numbers
```

---

### Hint 2: Sort the array first

Sorting helps with two things:

1. You can use the two-pointer approach.
2. You can easily skip duplicates.

Example:

```cpp
nums = [1, 0, -1, 0, -2, 2]
```

After sorting:

```cpp
[-2, -1, 0, 0, 1, 2]
```

Now the array has structure, and duplicate handling becomes easier.

---

### Hint 3: Fix the first two numbers

Suppose you choose:

```cpp
nums[i]
nums[j]
```

Then you need two more numbers whose sum is:

```cpp
target - nums[i] - nums[j]
```

So the problem becomes a normal **2Sum in sorted array** using two pointers.

---

### Hint 4: Use two pointers after `j`

After fixing `i` and `j`, set:

```cpp
left = j + 1;
right = n - 1;
```

Then compute:

```cpp
sum = nums[i] + nums[j] + nums[left] + nums[right];
```

If:

```cpp
sum == target
```

you found one quadruplet.

If:

```cpp
sum < target
```

move `left++`.

If:

```cpp
sum > target
```

move `right--`.

---

### Hint 5: Skip duplicates carefully

This is the most important part.

For `i`:

```cpp
if (i > 0 && nums[i] == nums[i - 1]) continue;
```

For `j`:

```cpp
if (j > i + 1 && nums[j] == nums[j - 1]) continue;
```

After finding a valid quadruplet, move both pointers and skip duplicates:

```cpp
left++;
right--;

while (left < right && nums[left] == nums[left - 1]) left++;
while (left < right && nums[right] == nums[right + 1]) right--;
```

This prevents repeated answers like:

```cpp
[-2, 0, 0, 2]
[-2, 0, 0, 2]
```

---

### Hint 6: Watch out for integer overflow

The constraints can make this overflow:

```cpp
nums[i] + nums[j] + nums[left] + nums[right]
```

So use `long long`:

```cpp
long long sum = 1LL * nums[i] + nums[j] + nums[left] + nums[right];
```

---

Time complexity:

```cpp
O(n^3)
```

Space complexity, ignoring the answer:

```cpp
O(1)
```

Main idea:

```cpp
4Sum = fix two numbers + solve 2Sum with two pointers.
```
*/
class Solution {
public:
    vector<vector<int>> fourSum(vector<int>& nums, int target) {
        int n = nums.size();
        sort(nums.begin(), nums.end());

        vector<vector<int>> ans;

        for(int i=0; i<n; i++){
            if(i > 0 && nums[i] == nums[i-1]){
                continue;
            }

            for(int j=i+1; j<n; j++){
                if(j > i+1 && nums[j] == nums[j-1]){
                    continue;
                }

                // Now normal 2 pointer based approach for 2sum
                int left = j+1;
                int right = n - 1;

                while(left < right){
                    long long sum = 1LL * nums[i] + nums[j] + nums[left] + nums[right];

                    if(sum == target){
                        ans.push_back({nums[i], nums[j], nums[left], nums[right]});

                        left++;
                        right--;

                        while(left < right && nums[left] == nums[left - 1]){
                            left++;
                        }
                        while(left < right && nums[right] == nums[right + 1]){
                            right--;
                        }
                    }else if(sum < target){
                        left++;
                    }else{
                        right--;
                    }
                }
            }
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna