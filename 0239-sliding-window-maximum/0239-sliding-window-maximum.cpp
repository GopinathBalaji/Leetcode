// Method 1: Sliding Window using a Monotonic Queue (here deque is used as the monotonic queue)
/*
Here are progressive hints for **LeetCode 239. Sliding Window Maximum**.

## Hint 1: Brute force is too slow

For every window of size `k`, you could scan all `k` elements and find the maximum.

```text
nums = [1,3,-1,-3,5,3,6,7], k = 3
```

Windows:

```text
[1, 3, -1]     max = 3
[3, -1, -3]    max = 3
[-1, -3, 5]    max = 5
[-3, 5, 3]     max = 5
[5, 3, 6]      max = 6
[3, 6, 7]      max = 7
```

But scanning every window takes:

```text
O(n * k)
```

You need better.

---

## Hint 2: Think about what matters inside a window

Suppose your current window has:

```text
[1, 3, -1]
```

The maximum is `3`.

Notice that `1` is useless once `3` appears after it, because:

```text
3 > 1
```

and `3` will stay in the window longer than or as long as `1`.

So smaller elements before a larger element can be discarded.

---

## Hint 3: Use a deque

Use a `deque` to store indices, not values.

The deque should maintain elements in **decreasing order of value**.

That means:

```text
nums[deque[0]] >= nums[deque[1]] >= nums[deque[2]]
```

The front of the deque always contains the index of the maximum element of the current window.

---

## Hint 4: Remove elements that are out of the window

For each index `right`, your window is:

```text
[right - k + 1, right]
```

Any index smaller than:

```text
right - k + 1
```

is outside the window.

So before using the front as the answer, remove it if it is too old.

```cpp
if (!dq.empty() && dq.front() < right - k + 1) {
    dq.pop_front();
}
```

---

## Hint 5: Maintain decreasing order

Before inserting `right`, remove all indices from the back whose values are smaller than or equal to `nums[right]`.

```cpp
while (!dq.empty() && nums[dq.back()] <= nums[right]) {
    dq.pop_back();
}
```

Why?

Because if `nums[right]` is bigger, those smaller previous elements can never become maximum while `nums[right]` is still in the window.

Then push the current index:

```cpp
dq.push_back(right);
```

---

## Hint 6: Start recording answers only after the first full window

A window becomes valid when:

```cpp
right >= k - 1
```

At that point, the max is:

```cpp
nums[dq.front()]
```

So append it to the answer.

---

## Overall idea

For each `right` from `0` to `n - 1`:

```text
1. Remove indices from the front if they are outside the current window.
2. Remove indices from the back while their values are smaller than nums[right].
3. Push right into the deque.
4. If the window size is at least k, add nums[deque.front()] to answer.
```

The key insight is that the deque stores only **useful candidates** for the maximum.
*/
class Solution {
public:
    vector<int> maxSlidingWindow(vector<int>& nums, int k) {
        int n = nums.size();
        vector<int> ans;        
        deque<int> deque;

        int right = 0;

        while(right < n){
            if(!deque.empty() && deque.front() < right - k + 1){
                deque.pop_front();
            }

            while(!deque.empty() && nums[deque.back()] <= nums[right]){
                deque.pop_back();
            }

            deque.push_back(right);

            if(right >= k - 1){
                ans.push_back(nums[deque.front()]);
            }

            right++;
        }

        return ans;
    }
};






// Method 2: Using Priority Queue
/*
## Idea

Use a max heap to always keep the largest element at the top.

But the problem is that some elements in the heap may be **outside the current window**.

So we store:

```cpp
{value, index}
```

Then, before taking the maximum, we remove heap elements whose index is no longer inside the current window.

For window ending at index `i`, the valid window is:

```cpp
[i - k + 1, i]
```

So any index:

```cpp
<= i - k
```

is outdated and should be removed.


## Walkthrough

For:

```text
nums = [1, 3, -1, -3, 5, 3, 6, 7]
k = 3
```

At every index `i`, we push:

```cpp
{nums[i], i}
```

The heap keeps the largest value on top.

For example, when the current window is:

```text
[1, 3, -1]
```

the heap top is:

```text
3
```

So we add `3` to the answer.

Later, suppose the heap top is an old value that is no longer inside the window. We remove it using:

```cpp
while (!pq.empty() && pq.top().second <= i - k) {
    pq.pop();
}
```

This is called **lazy deletion**.

We do not remove old elements immediately when they leave the window. We remove them only when they reach the top of the heap.

---

## Why store index?

If you only store values, you cannot know whether the maximum belongs to the current window.

Example:

```text
nums = [9, 1, 1, 1]
k = 2
```

At window:

```text
[1, 1]
```

the `9` is no longer valid.

But if the heap only stores values, it may still think `9` is the maximum.

That is why we store both:

```cpp
{value, index}
```

---

## Complexity

### Time Complexity

```text
O(n log n)
```

Each element is pushed into the heap once, and each element can be popped at most once.

Heap operations take:

```text
O(log n)
```

### Space Complexity

```text
O(n)
```

In the worst case, the heap can store many elements before they are lazily removed.

The deque approach is better with `O(n)` time, but the priority queue approach is easier to think about.
*/
// class Solution {
// public:
//     vector<int> maxSlidingWindow(vector<int>& nums, int k) {
//         int n = nums.size();

//         vector<int> ans;

//         // max heap: {value, index}
//         priority_queue<pair<int, int>> pq;

//         for (int i = 0; i < n; i++) {
//             // Add current element
//             pq.push({nums[i], i});

//             // Remove elements that are outside the current window
//             while (!pq.empty() && pq.top().second <= i - k) {
//                 pq.pop();
//             }

//             // Start adding answers once we have a full window
//             if (i >= k - 1) {
//                 ans.push_back(pq.top().first);
//             }
//         }

//         return ans;
//     }
// };

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna