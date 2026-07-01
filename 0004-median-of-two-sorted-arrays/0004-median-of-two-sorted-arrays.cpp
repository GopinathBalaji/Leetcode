// Method 1: Binary Search
/*
This is the classic **binary search on a partition**, not a merge problem.

You want to divide both sorted arrays into a left half and right half such that:

```text
all values in left half <= all values in right half
```

and the left half contains half of the total elements.

## Key idea

Suppose:

```text
A = [1, 3, 8, 9, 15]
B = [7, 11, 18, 19, 21, 25]
```

Choose a cut in each array:

```text
A: [1, 3, 8 | 9, 15]
B: [7, 11, 18 | 19, 21, 25]
```

The total number of elements on the left should be:

```cpp
leftSize = (m + n + 1) / 2;
```

The `+1` makes the left half contain one extra element when the total length is odd.

If you take `i` elements from `A` into the left side, then you must take:

```cpp
j = leftSize - i;
```

elements from `B`.

## What to binary search

Always binary search on the **smaller array**.

```cpp
if (nums1.size() > nums2.size()) {
    swap(nums1, nums2);
}
```

Why? It keeps `i` and `j` valid and gives:

```text
O(log(min(m, n)))
```

time.

## Partition values

For a cut `i` in `A` and `j` in `B`, define:

```text
Aleft  = A[i - 1]
Aright = A[i]

Bleft  = B[j - 1]
Bright = B[j]
```

But cuts may be at the edges, so use sentinels:

```cpp
Aleft  = (i == 0) ? INT_MIN : A[i - 1];
Aright = (i == m) ? INT_MAX : A[i];

Bleft  = (j == 0) ? INT_MIN : B[j - 1];
Bright = (j == n) ? INT_MAX : B[j];
```

## Correct-partition condition

You found the correct split when:

```cpp
Aleft <= Bright && Bleft <= Aright
```

That means no left-side value is larger than a right-side value.

## How to move binary search

If:

```cpp
Aleft > Bright
```

you took too many elements from `A`, so move left:

```cpp
right = i - 1;
```

Otherwise, if:

```cpp
Bleft > Aright
```

you did not take enough from `A`, so move right:

```cpp
left = i + 1;
```

## How to calculate the median once the partition is valid

For an odd total number of elements:

```cpp
max(Aleft, Bleft)
```

For an even total number of elements:

```cpp
(max(Aleft, Bleft) + min(Aright, Bright)) / 2.0
```

## Binary-search skeleton

```cpp
int m = nums1.size();
int n = nums2.size();

int left = 0;
int right = m;

while (left <= right) {
    int i = left + (right - left) / 2;
    int j = (m + n + 1) / 2 - i;

    // Compute Aleft, Aright, Bleft, Bright.

    if (Aleft <= Bright && Bleft <= Aright) {
        // Correct partition: compute and return median.
    } 
    else if (Aleft > Bright) {
        right = i - 1;
    } 
    else {
        left = i + 1;
    }
}
```

A useful way to remember the problem:

> Find a partition where the left side has half the elements and its largest value is no greater than the right side’s smallest value.

Try tracing these examples:

```text
nums1 = [1, 3], nums2 = [2]
nums1 = [1, 2], nums2 = [3, 4]
nums1 = [], nums2 = [1]
nums1 = [0, 0], nums2 = [0, 0]
```
*/
class Solution {
public:
    double findMedianSortedArrays(vector<int>& nums1, vector<int>& nums2) {
        if(nums1.size() > nums2.size()){
            std::swap(nums1, nums2);
        }

        int m = nums1.size();
        int n = nums2.size();

        int left = 0;
        int right = m;

        while(left <= right){
            int i = left + (right - left) / 2;
            int j = (m + n + 1) / 2 - i;

            int Aleft = (i == 0) ? INT_MIN : nums1[i-1];
            int Aright = (i == m) ? INT_MAX : nums1[i];

            int Bleft = (j == 0) ? INT_MIN : nums2[j - 1];
            int Bright = (j == n) ? INT_MAX : nums2[j];

            if(Aleft <= Bright && Bleft <= Aright){
                if((m + n) % 2 != 0){
                    return std::max(Aleft, Bleft);
                }
                else{
                    return (std::max(Aleft, Bleft) + std::min(Aright, Bright)) / 2.0;
                }
            }else if(Aleft > Bright){
                right = i - 1;
            }else{
                left = i + 1;
            }
        }

        return 0;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna