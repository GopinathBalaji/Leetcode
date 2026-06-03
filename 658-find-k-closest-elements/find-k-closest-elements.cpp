// Method 1: Sliding Window - Two-Pointer Approach
/*
## Hint 1: Understand what “closest” means

You are given a sorted array:

```cpp
arr = [1, 2, 3, 4, 5]
k = 4
x = 3
```

You need to return the `k` elements closest to `x`.

The answer is:

```cpp
[1, 2, 3, 4]
```

because those are the 4 closest values to `3`.

---

## Hint 2: Tie-breaking rule

If two numbers are equally close to `x`, choose the smaller number.

Example:

```cpp
arr = [1, 2, 3, 4, 5]
k = 4
x = 3
```

Both `1` and `5` are distance `2` from `3`.

Tie rule says choose the smaller one, so choose `1`.

That is why the answer is:

```cpp
[1, 2, 3, 4]
```

not:

```cpp
[2, 3, 4, 5]
```

---

## Hint 3: Since array is sorted, answer will be contiguous

This is important.

The final `k` closest elements will always form a continuous subarray inside `arr`.

So you are not randomly picking elements. You are finding a window of size `k`.

Example:

```cpp
arr = [1, 2, 3, 4, 5, 6, 7]
k = 3
x = 5
```

Answer:

```cpp
[4, 5, 6]
```

A contiguous window.

---

## Hint 4: Simple two-pointer shrinking approach

Start with the full array:

```cpp
left = 0
right = n - 1
```

While the window size is greater than `k`, remove one element from either the left side or the right side.

Current window size:

```cpp
right - left + 1
```

Shrink until:

```cpp
right - left + 1 == k
```

---

## Hint 5: Decide which side to remove

Compare the distance of the two ends from `x`:

```cpp
abs(arr[left] - x)
abs(arr[right] - x)
```

If the left side is farther, remove it:

```cpp
left++;
```

If the right side is farther, remove it:

```cpp
right--;
```

---

## Hint 6: Tie case

If both ends are equally far:

```cpp
abs(arr[left] - x) == abs(arr[right] - x)
```

You should remove the **right** side.

Why?

Because the smaller value is preferred, and `arr[left]` is smaller than `arr[right]`.

So keep the left value and remove the right value.

That means your condition can be:

```cpp
if (abs(arr[left] - x) > abs(arr[right] - x)) {
    left++;
} else {
    right--;
}
```

Notice the `else` removes right even during ties.

---

## Hint 7: Return the remaining window

After shrinking, return:

```cpp
arr[left], arr[left + 1], ..., arr[right]
```

In C++, that can be:

```cpp
vector<int>(arr.begin() + left, arr.begin() + right + 1)
```

---

## Core idea

```cpp
Start with the whole sorted array.
Repeatedly remove the farther endpoint.
If both endpoints are equally far, remove the larger endpoint.
When only k elements remain, return them.
```

This approach is simple and accepted.

Time complexity:

```cpp
O(n - k)
```

Space complexity, ignoring the answer:

```cpp
O(1)
```

There is also a binary search solution in `O(log(n - k) + k)`, but the two-pointer shrinking approach is usually easier to understand first.
*/
class Solution {
public:
    vector<int> findClosestElements(vector<int>& arr, int k, int x) {
        int n = arr.size();
        
        int left = 0;
        int right = n - 1;

        while((right - left + 1) > k){
            int leftDiff = std::abs(arr[left] - x);
            int rightDiff = std::abs(arr[right] - x);

            if(leftDiff <= rightDiff){
                right--;
            }else if(leftDiff > rightDiff){
                left++;
            }
        }

        return std::vector<int> (arr.begin() + left, arr.begin() + right + 1);
    }
};






// Method 2: Binary Search approach
/*
The binary search idea is:

Instead of binary searching for `x`, we binary search for the **starting index of the best window of size `k`**.

Because the answer is always a **contiguous subarray** of length `k`.

---

## Key idea

Suppose the answer window starts at index `start`.

Then the window is:

```cpp
arr[start], arr[start + 1], ..., arr[start + k - 1]
```

So possible starting indices are:

```cpp
0 to n - k
```

We binary search in that range.

---

## How to decide direction

At some `mid`, compare two candidate windows:

Window starting at `mid`:

```cpp
arr[mid] ... arr[mid + k - 1]
```

Window starting at `mid + 1`:

```cpp
arr[mid + 1] ... arr[mid + k]
```

The only elements that differ are:

```cpp
arr[mid]
arr[mid + k]
```

So compare which side is farther from `x`.

Use:

```cpp
x - arr[mid]
```

and:

```cpp
arr[mid + k] - x
```

If:

```cpp
x - arr[mid] > arr[mid + k] - x
```

then `arr[mid]` is farther from `x` than `arr[mid + k]`, so the better window is more to the right.

So do:

```cpp
left = mid + 1;
```

Otherwise, keep the left window:

```cpp
right = mid;
```

This also handles ties correctly. If both are equally close, we choose the smaller elements, so we keep the left window.


## Why `right = n - k`?

Because the window has size `k`.

If:

```cpp
n = 5
k = 4
```

then possible windows are:

```cpp
start = 0 -> arr[0..3]
start = 1 -> arr[1..4]
```

So the largest valid start is:

```cpp
n - k
```

---

## Example

```cpp
arr = [1, 2, 3, 4, 5]
k = 4
x = 3
```

Possible windows:

```cpp
start = 0 -> [1, 2, 3, 4]
start = 1 -> [2, 3, 4, 5]
```

Binary search starts with:

```cpp
left = 0
right = 1
mid = 0
```

Compare:

```cpp
x - arr[mid] = 3 - 1 = 2
arr[mid + k] - x = arr[4] - 3 = 5 - 3 = 2
```

They are equal.

Tie means choose the smaller elements, so keep the left window:

```cpp
right = mid
right = 0
```

Final start:

```cpp
left = 0
```

Answer:

```cpp
[1, 2, 3, 4]
```

---

## Why this works

At every `mid`, you are asking:

Should the best window start at `mid` or somewhere to the right?

Compare the element that would be removed on the left:

```cpp
arr[mid]
```

with the element that would be added on the right:

```cpp
arr[mid + k]
```

If the left element is farther from `x`, shift right.

If the right element is farther or equally far, stay left.

---

## Complexity

```cpp
Time Complexity: O(log(n - k) + k)
```

The binary search takes:

```cpp
O(log(n - k))
```

Creating the answer vector takes:

```cpp
O(k)
```

Space complexity ignoring output:

```cpp
O(1)
```
*/
// class Solution {
// public:
//     vector<int> findClosestElements(vector<int>& arr, int k, int x) {
//         int n = arr.size();

//         int left = 0;
//         int right = n - k;

//         while (left < right) {
//             int mid = left + (right - left) / 2;

//             int leftDistance = x - arr[mid];
//             int rightDistance = arr[mid + k] - x;

//             if (leftDistance > rightDistance) {
//                 left = mid + 1;
//             } else {
//                 right = mid;
//             }
//         }

//         return vector<int>(arr.begin() + left, arr.begin() + left + k);
//     }
// };








// Method 3: Max-Heap Approach
/*
## Idea

We want to keep only the best `k` elements.

For every element `num` in `arr`, calculate:

```cpp
distance = abs(num - x)
```

We use a **max heap** of size at most `k`.

The heap stores:

```cpp
{distance, value}
```

The worst element among the current chosen elements should stay on top.

“Worst” means:

1. Larger distance from `x`
2. If distance is same, larger value is worse because the problem prefers smaller values

So if the heap size becomes greater than `k`, pop the top.

At the end, the heap contains the `k` closest elements. Then sort them before returning because the answer must be in ascending order.

---

## Why `priority_queue<pair<int, int>>` works

C++ `priority_queue<pair<int, int>>` is a max heap.

For pairs, it compares:

```cpp
first value first
```

then if tied:

```cpp
second value
```

So the largest pair comes to the top.

Example:

```cpp
{2, 5}
{2, 1}
```

The heap considers:

```cpp
{2, 5}
```

larger because distance is same, but value `5` is larger.

That is exactly what we want to remove first, because if two elements have the same distance from `x`, the smaller value should be preferred.

---

## Example

```cpp
arr = [1, 2, 3, 4, 5]
k = 4
x = 3
```

Distances:

```cpp
1 -> distance 2
2 -> distance 1
3 -> distance 0
4 -> distance 1
5 -> distance 2
```

We need 4 closest values.

Both `1` and `5` have distance `2`, but the problem prefers the smaller value, so keep `1` and remove `5`.

Final answer:

```cpp
[1, 2, 3, 4]
```

---

## Complexity

```cpp
Time Complexity: O(n log k + k log k)
```

Explanation:

```cpp
n log k
```

for pushing/popping from a heap of size at most `k`.

```cpp
k log k
```

for sorting the final answer.

Space complexity:

```cpp
O(k)
```
*/
// class Solution {
// public:
//     vector<int> findClosestElements(vector<int>& arr, int k, int x) {
//         priority_queue<pair<int, int>> maxHeap;

//         for (int num : arr) {
//             int dist = abs(num - x);

//             // Store distance and value
//             maxHeap.push({dist, num});

//             // Keep only k closest elements
//             if (maxHeap.size() > k) {
//                 maxHeap.pop();
//             }
//         }

//         vector<int> ans;

//         while (!maxHeap.empty()) {
//             ans.push_back(maxHeap.top().second);
//             maxHeap.pop();
//         }

//         // Required output should be sorted in ascending order
//         sort(ans.begin(), ans.end());

//         return ans;
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna