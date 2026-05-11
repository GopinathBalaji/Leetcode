// Method 1: Dutch National Flag algorithm (Three-pointer approach)
/*
The one-pass, constant-space approach is the **Dutch National Flag algorithm**.

For LeetCode 75, the array only contains:

```cpp
0 // red
1 // white
2 // blue
```

We want:

```text
all 0s first, then all 1s, then all 2s
```

Your counting sort solution is valid, but it makes **two passes**. The follow-up asks for **one pass** and **O(1) extra space**.

---

## Core idea

Use three pointers:

```cpp
low  = position where next 0 should go
mid  = current element being processed
high = position where next 2 should go
```

The array is divided into four regions:

```text
[0 ... low - 1]       -> all 0s
[low ... mid - 1]     -> all 1s
[mid ... high]        -> unknown
[high + 1 ... n - 1]  -> all 2s
```

Initially:

```cpp
low = 0;
mid = 0;
high = nums.size() - 1;
```

Everything is unknown.

---

## Decision at `nums[mid]`

### Case 1: `nums[mid] == 0`

A `0` belongs at the front.

So swap it with `nums[low]`.

```cpp
swap(nums[low], nums[mid]);
low++;
mid++;
```

Why increment both?

Because after swapping, the element at `low` is correctly placed as `0`, and the element moved to `mid` must be a `1` or already processed region value.

---

### Case 2: `nums[mid] == 1`

A `1` is already in the middle region.

So just move forward:

```cpp
mid++;
```

---

### Case 3: `nums[mid] == 2`

A `2` belongs at the end.

So swap it with `nums[high]`.

```cpp
swap(nums[mid], nums[high]);
high--;
```

Important: do **not** increment `mid` here.

Why?

Because the element swapped from `high` into `mid` is still unknown. It could be `0`, `1`, or `2`, so we must check it again.

---

## Example walkthrough

Input:

```text
[2, 0, 2, 1, 1, 0]
```

Initial:

```text
low = 0, mid = 0, high = 5
```

`nums[mid] = 2`, swap with `high`:

```text
[0, 0, 2, 1, 1, 2]
low = 0, mid = 0, high = 4
```

Do not increment `mid`, because the new `nums[mid]` must be checked.

Now `nums[mid] = 0`, swap with `low`:

```text
[0, 0, 2, 1, 1, 2]
low = 1, mid = 1, high = 4
```

Now `nums[mid] = 0`, swap with `low`:

```text
[0, 0, 2, 1, 1, 2]
low = 2, mid = 2, high = 4
```

Now `nums[mid] = 2`, swap with `high`:

```text
[0, 0, 1, 1, 2, 2]
low = 2, mid = 2, high = 3
```

Now `nums[mid] = 1`:

```text
mid = 3
```

Now `nums[mid] = 1`:

```text
mid = 4
```

Loop stops because:

```cpp
mid > high
```

Final:

```text
[0, 0, 1, 1, 2, 2]
```

---

## Complexity

```text
Time:  O(n)
Space: O(1)
```
*/
class Solution {
public:
    void sortColors(vector<int>& nums) {
        int low = 0;
        int mid = 0;
        int high = nums.size() - 1;

        while (mid <= high) {
            if (nums[mid] == 0) {
                swap(nums[low], nums[mid]);
                low++;
                mid++;
            } else if (nums[mid] == 1) {
                mid++;
            } else {
                swap(nums[mid], nums[high]);
                high--;
            }
        }
    }
};






// Method 2: Two-pass Counting Sort
/*
*/
// class Solution {
// public:
//     void sortColors(vector<int>& nums) {
//         int redCount = 0;
//         int whiteCount = 0;
//         int blueCount = 0;

//         for(int num : nums){
//             if(num == 0){
//                 redCount++;
//             }else if(num == 1){
//                 whiteCount++;
//             }else{
//                 blueCount++;
//             }
//         }

//         for(int i=0; i<nums.size(); i++){
//             if(redCount != 0){
//                 nums[i] = 0;
//                 redCount--;
//             }else if(whiteCount != 0){
//                 nums[i] = 1;
//                 whiteCount--;
//             }else{
//                 nums[i] = 2;
//                 blueCount--;
//             }
//         }

//         return;
//     }
// };