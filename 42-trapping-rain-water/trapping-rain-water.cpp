// Method 1: Prefix and Suffix Max Approach (Uses extra space)
/*
Here are progressive hints for **LeetCode 42: Trapping Rain Water**.

## Hint 1: Water depends on left and right boundaries

At any index `i`, water can be trapped only if there is a taller/equal bar on the left and a taller/equal bar on the right.

Example:

```cpp
height = [0, 1, 0, 2]
```

At index `2`, height is `0`.

There is a left boundary of height `1` and a right boundary of height `2`, so water can be trapped there.

---

## Hint 2: Formula for water at one index

For every position `i`, the amount of water above it is:

```cpp
min(max height on left, max height on right) - height[i]
```

But only if this value is positive.

So:

```cpp
waterAtI = min(leftMax[i], rightMax[i]) - height[i]
```

Example:

```cpp
height = [0, 1, 0, 2]
```

For index `2`:

```cpp
leftMax = 1
rightMax = 2

water = min(1, 2) - 0
      = 1
```

So index `2` stores `1` unit of water.

---

## Hint 3: Brute force idea

For every index `i`, you could scan left to find the maximum height and scan right to find the maximum height.

Pseudo-logic:

```cpp
for each index i:
    leftMax = max height from 0 to i
    rightMax = max height from i to n - 1

    water += min(leftMax, rightMax) - height[i]
```

This works, but it is slow:

```cpp
O(n^2)
```

because for every index, you scan left and right.

---

## Hint 4: Improve with prefix and suffix arrays

Instead of recalculating left and right maximums every time, precompute them.

Create:

```cpp
leftMax[i]  = maximum height from index 0 to i
rightMax[i] = maximum height from index i to n - 1
```

Then for every index:

```cpp
water += min(leftMax[i], rightMax[i]) - height[i]
```

This gives:

```cpp
O(n) time
O(n) space
```

---

## Hint 5: Example of prefix/suffix arrays

```cpp
height = [0, 1, 0, 2, 1, 0, 1, 3]
```

`leftMax` becomes:

```cpp
[0, 1, 1, 2, 2, 2, 2, 3]
```

`rightMax` becomes:

```cpp
[3, 3, 3, 3, 3, 3, 3, 3]
```

For index `5`:

```cpp
height[5] = 0
leftMax[5] = 2
rightMax[5] = 3

water = min(2, 3) - 0
      = 2
```

So index `5` traps `2` units of water.

---

## Hint 6: There is also an O(1) space two-pointer approach

Use two pointers:

```cpp
left = 0
right = n - 1
```

Also maintain:

```cpp
leftMax = 0
rightMax = 0
```

The key idea is:

If:

```cpp
height[left] < height[right]
```

then the water at `left` depends on `leftMax`.

Why? Because there is already a taller boundary on the right side, so the limiting factor is the left side.

Similarly, if:

```cpp
height[right] <= height[left]
```

then the water at `right` depends on `rightMax`.

---

## Hint 7: Two-pointer logic

While:

```cpp
left < right
```

If the left height is smaller:

```cpp
if (height[left] < height[right]) {
    if (height[left] >= leftMax) {
        leftMax = height[left];
    } else {
        water += leftMax - height[left];
    }

    left++;
}
```

Otherwise process the right side:

```cpp
else {
    if (height[right] >= rightMax) {
        rightMax = height[right];
    } else {
        water += rightMax - height[right];
    }

    right--;
}
```

---

## Core idea

For each index:

```cpp
water = min(tallest bar to the left, tallest bar to the right) - current height
```
*/
class Solution {
public:
    int trap(vector<int>& height) {
        int n = height.size();
        int water = 0;

        vector<int> leftMax(n);
        vector<int> rightMax(n);

        for(int i=0; i<n; i++){
            if(i == 0){
                leftMax[i] = height[i];
            }else{
                leftMax[i] = max(height[i], leftMax[i-1]);
            }
        }

        for(int i=n-1; i>=0; i--){
            if(i == n-1){
                rightMax[i] = height[i];
            }else{
                rightMax[i] = max(height[i], rightMax[i+1]);
            }
        }


        for(int i=0; i<n; i++){
            water += min(leftMax[i], rightMax[i]) - height[i];
        }

        return water;
    }
};






// Method 2: Two-pointer approach (No extra space)
/*
## Main idea

At any index, trapped water depends on:

```cpp
min(max height on left, max height on right) - current height
```

The prefix/suffix array approach stores `leftMax[i]` and `rightMax[i]`.

The two-pointer approach does the same idea, but without arrays.

We keep:

```cpp
left
right
leftMax
rightMax
```

Where:

```cpp
leftMax  = tallest bar seen so far from the left side
rightMax = tallest bar seen so far from the right side
```

---

## Key intuition

Suppose:

```cpp
height[left] < height[right]
```

This means the right side has a boundary taller than `height[left]`.

So for the current `left` position, the amount of water is controlled by the left side, because we already know there is some taller bar on the right.

Therefore, we can safely process `left`.

Similarly, if:

```cpp
height[right] <= height[left]
```

then we process `right`.


## Walkthrough

Take this example:

```cpp
height = [0, 1, 0, 2, 1, 0, 1, 3]
```

Start:

```cpp
left = 0
right = 7
leftMax = 0
rightMax = 0
water = 0
```

Array:

```cpp
index:   0  1  2  3  4  5  6  7
height:  0  1  0  2  1  0  1  3
```

Since:

```cpp
height[left] = 0
height[right] = 3
```

`height[left] < height[right]`, so process the left side.

At index `0`:

```cpp
height[0] = 0
leftMax = 0
```

Update:

```cpp
leftMax = 0
left++
```

Now:

```cpp
left = 1
```

---

At index `1`:

```cpp
height[1] = 1
leftMax = 0
```

Since `height[1] >= leftMax`, update:

```cpp
leftMax = 1
```

No water is trapped at index `1` because it is a new left boundary.

---

At index `2`:

```cpp
height[2] = 0
leftMax = 1
```

Since `height[2] < leftMax`, water can be trapped:

```cpp
water += leftMax - height[2]
water += 1 - 0
water += 1
```

So now:

```cpp
water = 1
```

---

At index `3`:

```cpp
height[3] = 2
leftMax = 1
```

Update:

```cpp
leftMax = 2
```

No water is trapped because this becomes a new left boundary.

---

Now you keep moving inward. Whenever the current bar is shorter than the max boundary seen so far from that side, it traps water.

---

## Why we process the smaller side

This is the most important part.

Suppose:

```cpp
height[left] < height[right]
```

Then we know there is a right boundary at least as tall as `height[left]`.

So for `left`, the limiting side is the left side.

The water at `left` is determined by:

```cpp
leftMax - height[left]
```

if `leftMax` is taller than `height[left]`.

We do not need to know the exact maximum on the right, because we already know the right side is high enough to trap water with the current left side.

Similarly, if:

```cpp
height[right] <= height[left]
```

then the left side is high enough, so we process the right side using:

```cpp
rightMax - height[right]
```


## Complexity

```cpp
Time Complexity:  O(n)
Space Complexity: O(1)
```

Compared to the prefix/suffix array approach:

```cpp
Prefix/Suffix approach: O(n) time, O(n) space
Two-pointer approach:   O(n) time, O(1) space
```

Core idea:

```cpp
Move the pointer with the smaller height.
Track the best boundary seen from that side.
If current height is below that boundary, add trapped water.
```
*/

// class Solution {
// public:
//     int trap(vector<int>& height) {
//         int n = height.size();

//         int left = 0;
//         int right = n - 1;

//         int leftMax = 0;
//         int rightMax = 0;

//         int water = 0;

//         while (left < right) {
//             if (height[left] < height[right]) {
//                 // We are processing the left side.

//                 if (height[left] >= leftMax) {
//                     // Current bar is taller than anything before it.
//                     // So update leftMax.
//                     leftMax = height[left];
//                 } else {
//                     // Current bar is shorter than leftMax.
//                     // Since right side has a taller boundary,
//                     // water can be trapped here.
//                     water += leftMax - height[left];
//                 }

//                 left++;
//             } else {
//                 // We are processing the right side.

//                 if (height[right] >= rightMax) {
//                     // Current bar is taller than anything after it.
//                     // So update rightMax.
//                     rightMax = height[right];
//                 } else {
//                     // Current bar is shorter than rightMax.
//                     // Since left side has a taller/equal boundary,
//                     // water can be trapped here.
//                     water += rightMax - height[right];
//                 }

//                 right--;
//             }
//         }

//         return water;
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna