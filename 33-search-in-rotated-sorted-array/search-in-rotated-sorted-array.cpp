// Method 1: Binary search in a closed interval
/*
Use binary search, but at every step determine **which half is sorted**.

Example:

```text
[4, 5, 6, 7, 0, 1, 2]
```

Even though the full array is rotated, for any `mid`, at least one side is normally sorted.

### Core idea

For each iteration:

```cpp
mid = left + (right - left) / 2;
```

First check:

```cpp
if (nums[mid] == target)
    return mid;
```

Then decide which half is sorted.

### Case 1: Left half is sorted

You know the left part is sorted when:

```cpp
nums[left] <= nums[mid]
```

For example:

```text
[4, 5, 6, 7, 0, 1, 2]
 L        M
```

Now ask:

> Is `target` inside the sorted range `[nums[left], nums[mid])`?

That condition is:

```cpp
nums[left] <= target && target < nums[mid]
```

* Yes → discard the right half:

```cpp
right = mid - 1;
```

* No → target must be in the rotated/right half:

```cpp
left = mid + 1;
```

### Case 2: Right half is sorted

Otherwise, the right half must be sorted:

```cpp
nums[mid] < nums[left]
```

Now ask:

> Is `target` inside `(nums[mid], nums[right]]`?

```cpp
nums[mid] < target && target <= nums[right]
```

* Yes → search right:

```cpp
left = mid + 1;
```

* No → search left:

```cpp
right = mid - 1;
```


### Important detail

Use `<=` here:

```cpp
nums[left] <= nums[mid]
```

not just `<`, because when `left == mid`, such as when only two elements remain, the left side is still considered sorted.

Try walking through:

```text
nums = [4,5,6,7,0,1,2], target = 0
nums = [4,5,6,7,0,1,2], target = 5
nums = [3,1], target = 1
```

The invariant is: one half is sorted, and you use the target’s numeric range to decide whether it belongs in that half.
*/
class Solution {
public:
    int search(vector<int>& nums, int target) {
        int left = 0;
        int right = nums.size() - 1;

        while(left <= right){
            int mid = left + (right - left) / 2;

            if(nums[mid] == target){
                return mid;
            }

            if(nums[left] <= nums[mid]){
                if(nums[left] <= target && target < nums[mid]){
                    right = mid - 1;
                }else{
                    left = mid + 1;
                }
            }else{
                if(nums[mid] < target && target <= nums[right]){
                    left = mid + 1;
                }else{
                    right = mid - 1;
                }
            }
        }

        return -1;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna