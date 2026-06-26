// Method 1: Binary Search Approach
/*
Think of the array as two sorted parts:

```text
[4, 5, 6, 7, 0, 1, 2]
```

The minimum is the point where the order “drops” from a large number to a small number.

Use binary search to locate that drop.

### Main comparison

At each step, compare `nums[mid]` with `nums[right]`.

* If `nums[mid] > nums[right]`:

  * `mid` is in the left, larger sorted portion.
  * The minimum must be strictly to the right of `mid`.

```cpp
left = mid + 1;
```

* Otherwise, `nums[mid] <= nums[right]`:

  * `mid` could be the minimum, or the minimum is to its left.
  * Keep `mid`.

```cpp
right = mid;
```


### Why compare with `right`?

`nums[right]` helps you identify which side contains the rotation point.

For:

```text
[4, 5, 6, 7, 0, 1, 2]
             mid       right
```

Since:

```text
nums[mid] = 7 > 2 = nums[right]
```

the minimum must be to the right of `mid`.

For:

```text
[0, 1, 2, 4, 5, 6, 7]
       mid           right
```

Since:

```text
nums[mid] < nums[right]
```

the current range is already ordered around `mid`, and the minimum is at `mid` or to its left.

### Important edge cases

* Array is not rotated: `[1, 2, 3, 4]` → answer is `1`.
* One element: `[1]` → answer is `1`.
* Minimum may be the final element: `[2, 3, 4, 1]` → answer is `1`.

This problem guarantees all values are unique. With duplicates, the binary-search rule needs a small change.
*/
class Solution {
public:
    int findMin(vector<int>& nums) {
        int left = 0;
        int right = nums.size() - 1;

        while(left < right){
            int mid = left + (right - left) / 2;

            if(nums[mid] > nums[right]){
                left = mid + 1;
            }else{
                right = mid;
            }
        }

        return nums[left];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna