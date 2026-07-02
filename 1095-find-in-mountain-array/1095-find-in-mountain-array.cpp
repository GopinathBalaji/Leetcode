// Method 1: 3 Binary Search method
/*
This problem has three binary searches.

A mountain array looks like:

```text
[1, 3, 5, 8, 6, 4, 2]
          peak
```

It strictly increases up to one peak, then strictly decreases.

## Step 1: Find the peak index

Use binary search.

At `mid`, compare:

```cpp
mountainArr.get(mid)
mountainArr.get(mid + 1)
```

* If `get(mid) < get(mid + 1)`, you are on the increasing slope, so the peak is to the right.

```cpp
left = mid + 1;
```

* Otherwise, you are on the decreasing slope or at the peak, so the peak is at `mid` or to its left.

```cpp
right = mid;
```

Template:

```cpp
int left = 0;
int right = mountainArr.length() - 1;

while (left < right) {
    int mid = left + (right - left) / 2;

    if (mountainArr.get(mid) < mountainArr.get(mid + 1)) {
        left = mid + 1;
    } else {
        right = mid;
    }
}

int peak = left;
```

## Step 2: Binary search the increasing side

Search from:

```cpp
[0, peak]
```

This is an ordinary ascending binary search.

* If `value < target`, go right.
* If `value > target`, go left.

Search this side first because the problem asks for the **smallest index**. If the target exists on both slopes, the left-side occurrence is the answer.

## Step 3: Binary search the decreasing side

If not found on the left, search:

```cpp
[peak + 1, n - 1]
```

But this side is sorted in descending order.

So reverse the usual comparisons:

* If `value < target`, target must be to the **left**.

```cpp
right = mid - 1;
```

* If `value > target`, target must be to the **right**.

```cpp
left = mid + 1;
```

## Overall structure

```cpp
peak = findPeak();

answer = binarySearchAscending(0, peak);
if (answer != -1) {
    return answer;
}

return binarySearchDescending(peak + 1, n - 1);
```

## Important details

* Do not try to copy the whole `MountainArray` into a normal vector. The problem limits calls to `get()`.
* Three binary searches use about `O(log n)` calls each, comfortably under the 100-call limit.
* The peak itself is included in the ascending-side search, so do not search it twice unnecessarily.
* `MountainArray` is an interface, so use:

```cpp
int n = mountainArr.length();
int value = mountainArr.get(index);
```

The key idea to remember is:

> Find the peak, binary-search the rising side normally, then binary-search the falling side with reversed comparisons.
*/

/**
 * // This is the MountainArray's API interface.
 * // You should not implement it, or speculate about its implementation
 * class MountainArray {
 *   public:
 *     int get(int index);
 *     int length();
 * };
 */

class Solution {
public:
    int findInMountainArray(int target, MountainArray &mountainArr) {
        int left = 0;
        int right = mountainArr.length() - 1;

        while(left < right){
            int mid = left + (right - left) / 2;

            if(mountainArr.get(mid) < mountainArr.get(mid + 1)){
                left = mid + 1;
            }else{
                right = mid;
            }
        }

        int peak = left;

        left = 0;
        right = peak;

        while(left <= right){
            int mid = left + (right - left) / 2;

            int middleValue = mountainArr.get(mid);

            if(middleValue == target){
                return mid;
            }else if(middleValue < target){
                left = mid + 1;
            }else{
                right = mid - 1;
            }
        }

        left = peak + 1;
        right = mountainArr.length() - 1;

        while(left <= right){
            int mid = left + (right - left) / 2;

            int middleValue = mountainArr.get(mid);

            if(middleValue == target){
                return mid;
            }else if(middleValue < target){
                right = mid - 1;
            }else{
                left = mid + 1;
            }
        }

        return -1;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna