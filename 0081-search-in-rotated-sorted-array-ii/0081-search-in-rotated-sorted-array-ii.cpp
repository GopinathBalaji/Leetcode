// Method 1: Binary search using closed interval
/*
This is very similar to LeetCode 33, but duplicates create one ambiguity.

For example:

```text
[1, 1, 1, 3, 1]
```

Suppose:

```text
left = 0, mid = 2, right = 4
nums[left] = nums[mid] = nums[right] = 1
```

You cannot tell whether the rotation point is on the left or right. Both halves appear identical.

## Main idea

Still use binary search.

First:

```cpp
if (nums[mid] == target) {
    return true;
}
```

Then handle the duplicate ambiguity before deciding which half is sorted.

### Ambiguous duplicate case

When:

```cpp
nums[left] == nums[mid] && nums[mid] == nums[right]
```

you cannot know which side is sorted, so shrink both ends:

```cpp
left++;
right--;
```

This may degrade to `O(n)` in the worst case, but it is necessary.

## Otherwise, use the same logic as problem 33

### Left half is sorted

```cpp
if (nums[left] <= nums[mid])
```

Then check whether target lies in:

```cpp
[nums[left], nums[mid])
```

```cpp
if (nums[left] <= target && target < nums[mid]) {
    right = mid - 1;
} else {
    left = mid + 1;
}
```

### Right half is sorted

Otherwise, the right half is sorted.

Check whether target lies in:

```cpp
(nums[mid], nums[right]]
```

```cpp
if (nums[mid] < target && target <= nums[right]) {
    left = mid + 1;
} else {
    right = mid - 1;
}
```

## Skeleton

```cpp
int left = 0;
int right = nums.size() - 1;

while (left <= right) {
    int mid = left + (right - left) / 2;

    if (nums[mid] == target) {
        return true;
    }

    if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
        left++;
        right--;
    } 
    else if (nums[left] <= nums[mid]) {
        // left half is sorted
    } 
    else {
        // right half is sorted
    }
}

return false;
```

## Test these cases

```text
[2,5,6,0,0,1,2], target = 0   -> true
[2,5,6,0,0,1,2], target = 3   -> false
[1,0,1,1,1], target = 0       -> true
[1,1,1,3,1], target = 3       -> true
[1,1,1,1,1], target = 2       -> false
```

The only new rule compared with LeetCode 33 is:

```cpp
if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
    left++;
    right--;
}
```

That handles the case where duplicates prevent you from identifying the sorted half.
*/
class Solution {
public:
    bool search(vector<int>& nums, int target) {
        int left = 0;
        int right = nums.size() - 1;

        while(left <= right){
            int mid = left + (right - left) / 2;

            if(nums[mid] == target){
                return true;
            }

            if(nums[left] == nums[mid] && nums[mid] == nums[right]){
                left++;
                right--;
                continue;
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

        return false;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna