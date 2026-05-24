// Method 1: Two-pointer approach from the back
/*
## Hint 1: Understand the setup

You are given two sorted arrays:

```cpp
nums1 = [1, 2, 3, 0, 0, 0]
m = 3

nums2 = [2, 5, 6]
n = 3
```

Only the first `m` elements of `nums1` are real:

```cpp
[1, 2, 3]
```

The last `n` zeroes are just extra space.

You need to merge `nums2` into `nums1` in-place:

```cpp
[1, 2, 2, 3, 5, 6]
```

---

## Hint 2: Avoid merging from the front

A natural idea is to compare from the beginning:

```cpp
nums1[0] and nums2[0]
```

But this is annoying because if you need to insert something from `nums2` into the front of `nums1`, you would have to shift elements.

That can become inefficient.

---

## Hint 3: Use the empty space at the end

Since `nums1` already has extra space at the end, use that space directly.

Instead of filling from the front, fill from the back.

Compare the largest remaining elements from both arrays.

---

## Hint 4: Use three pointers

Use:

```cpp
i = m - 1;        // last real element in nums1
j = n - 1;        // last element in nums2
k = m + n - 1;    // last position in nums1
```

Example:

```cpp
nums1 = [1, 2, 3, 0, 0, 0]
nums2 = [2, 5, 6]
```

Start:

```cpp
i = 2  // nums1[i] = 3
j = 2  // nums2[j] = 6
k = 5  // place largest value here
```

---

## Hint 5: Put the larger value at `nums1[k]`

If:

```cpp
nums1[i] > nums2[j]
```

then put `nums1[i]` at the end:

```cpp
nums1[k] = nums1[i];
i--;
k--;
```

Otherwise put `nums2[j]` at the end:

```cpp
nums1[k] = nums2[j];
j--;
k--;
```

---

## Hint 6: Continue until `nums2` is fully copied

You only really need to continue while:

```cpp
j >= 0
```

Why?

Because if `nums2` still has elements, they must be copied into `nums1`.

But if `nums1` still has elements, they are already in the correct place.

---

## Example walkthrough

```cpp
nums1 = [1, 2, 3, 0, 0, 0]
nums2 = [2, 5, 6]
```

Compare `3` and `6`:

```cpp
6 is bigger
nums1 = [1, 2, 3, 0, 0, 6]
```

Compare `3` and `5`:

```cpp
5 is bigger
nums1 = [1, 2, 3, 0, 5, 6]
```

Compare `3` and `2`:

```cpp
3 is bigger
nums1 = [1, 2, 3, 3, 5, 6]
```

Compare `2` and `2`:

```cpp
put nums2's 2
nums1 = [1, 2, 2, 3, 5, 6]
```

Now `nums2` is fully used, so we are done.

---

## Core idea

```cpp
Fill nums1 from the back using the largest remaining value.
```

Use:

```cpp
i = m - 1
j = n - 1
k = m + n - 1
```

Then move `k` backward after each placement.
*/
class Solution {
public:
    void merge(vector<int>& nums1, int m, vector<int>& nums2, int n) {
        int i = m-1;
        int j = n-1;
        int k = m + n - 1;

        while(j >= 0){
            if(i >= 0 && nums1[i] > nums2[j]){
                nums1[k] = nums1[i];
                i--;
                k--;
            }else{
                nums1[k] = nums2[j];
                j--;
                k--;
            }
        }

        return;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna