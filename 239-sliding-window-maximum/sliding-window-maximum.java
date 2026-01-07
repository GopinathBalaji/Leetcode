// Method 1: Deque (Monotonic Queue), Sliding Window Approach
/*
# WHAT WAS I DOING WRONG:

## What’s wrong

### 1) You’re treating the deque ends inconsistently

In the monotonic-deque approach:

* One end should represent the **front = max candidate** (largest value).
* The other end is the **back = smallest candidate**.

Your code does these operations:

* While building monotonic property you do:

  ```java
  while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[right]) deque.pollLast();
  ```

  This assumes the **back (last)** holds the *smallest* candidates and you remove them.

* But then you insert new indices using:

  ```java
  deque.addFirst(right);
  ```

  That puts the newest index at the **front**, not the back.

* Then you remove out-of-window using:

  ```java
  while (!deque.isEmpty() && deque.peekLast() <= right - k) deque.pollLast();
  ```

  That assumes the **back** holds the *oldest* indices (so you can evict them), which is also inconsistent with `addFirst`.

* Finally you read the max using:

  ```java
  nums[deque.peekFirst()]
  ```

  But because you’re pushing at the front and popping from the back, the front is **not guaranteed** to be the maximum candidate.

✅ The standard invariant is: **push at the back, pop at the back, evict from the front, read max from the front** (or the exact opposite consistently). Your code mixes “front” and “back” roles.

---

## Failing example walkthrough

Take:

* `nums = [1, 3, -1, -3, 5, 3, 6, 7]`, `k = 3`
  Correct output:
* `[3, 3, 5, 5, 6, 7]`

Your initialization finds max of first window `[1,3,-1]`:

* `maxVal = 3`, `maxIdx = 1`
* `deque = [1]` (front=back=1)
* `ans[0]=3`

Now `right = 3`, `nums[right] = -3`:

* popLast while nums[last] <= -3? nums[1]=3 <= -3 false → no pop
* addFirst(3) → deque becomes `[3, 1]` (front=3, back=1)
* evict out of window: remove last if <= right-k = 0

  * back=1 <= 0 false → no eviction
* currWindowMax = nums[peekFirst] = nums[3] = -3  ❌
  But the max of window `[3, -1, -3]` is `3`.

So it breaks immediately.

Why? Because after `addFirst(3)`, the front became index 3 even though it’s not the max.

# ###############################################
# WHAT WAS I DOING WRONG (2):

This version is **almost correct**, and in fact the *core deque logic in the loop is fine*. The thing that’s wrong is more subtle:

## What’s wrong

### 1) Your deque is **not initialized correctly** for the monotonic invariant

You only do:

```java
deque.addFirst(maxIdx);
```

That means before you start sliding, the deque contains **just the index of the max** from the first window — but a correct monotonic deque should contain **all relevant candidates from the first window**, in decreasing order, because those candidates matter after the window slides.

When you keep only `maxIdx`, you *lose information* about other elements in the first window that could become the max once `maxIdx` slides out.

### Does that actually break correctness?

Yes — there are cases where the maximum of the next window is an element from the first window that is **not** the maximum of the first window, and you discarded it, so you won’t be able to output it when it should become the max.

### Counterexample

`nums = [9, 1, 8, 2]`, `k = 2`

Correct window maxima:

* window [9,1] → 9
* window [1,8] → 8
* window [8,2] → 8
  Answer: `[9, 8, 8]`

Your initialization:

* first window maxIdx = 0 (value 9)
* deque = [0]

Now `right = 2` (we’re forming window [1,8], left boundary is `right-k+1 = 1`)
Loop steps at `right=2`:

* pop from back while <= nums[2]=8:

  * nums[0]=9 <= 8? no → keep 0
* evict out-of-window from front: check `deque.peekFirst() <= right-k` → `0 <= 0` yes → pop 0
* addLast(2) → deque=[2]
* output nums[2]=8 ✅

So far ok. Let’s try a case where the “second-best from the first window” matters.

Better counterexample:
`nums = [5, 4, 3, 2, 1]`, `k = 3`

Correct maxima:

* [5,4,3] → 5
* [4,3,2] → 4
* [3,2,1] → 3
  Answer: `[5,4,3]`

Your init:

* maxIdx=0 (5), deque=[0], ans[0]=5

Now right=3 (window [4,3,2], left boundary = 1, `right-k=0`)

* pop back while <= nums[3]=2:

  * nums[0]=5 <= 2? no
* evict old: 0 <= 0 yes → pop 0 (deque empty)
* addLast(3) → deque=[3]
* output nums[3]=2 ❌ (should be 4)

Why did it fail?
Because the correct max for window [4,3,2] is **index 1 (value 4)**, which was in the first window but wasn’t the max, and you never stored it. Once 5 slides out, you have no remaining candidate from that earlier window, so you incorrectly fall back to the new element 2.

So the bug is: **initializing the deque with only the first window’s max is insufficient**.

---

## 2) Minor comment issue: “ascending order”

This is just wording, but it can confuse you later.

Your while loop:

```java
while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[right]) deque.pollLast();
```

maintains the deque in **decreasing order of values from front to back** (front is max), not ascending.
# ##############################

---

## Correct approach (Monotonic Deque)

### Invariant

Maintain a deque of indices such that:

1. Indices are **in increasing order** (front is oldest). (That means we'll insert from the end)
2. Values are **in decreasing order**: (This means we'll remove smaller values than the current candidate from the deque from the end. So deque will be in ascending order finally.)
   `nums[dq[0]] >= nums[dq[1]] >= ...`

Then:

* Front always holds the max index for the current window.
* Before adding a new element `right`, remove from the back any indices whose values are ≤ `nums[right]` (they’ll never be max again).
* Before recording max, remove from the front if it’s out of the window.

### Why it’s O(n)

Each index is:

* added once,
* removed at most once from the deque,
  so total operations are linear.


---

## Thorough example walkthrough (same input)

`nums = [1,3,-1,-3,5,3,6,7], k=3`

We’ll track deque as indices (and their values).

### right=0 (1)

* dq empty → add 0
* dq: [0] (values [1])
* right < 2 → no answer yet

### right=1 (3)

* evict out-of-window: none
* pop back while <= 3: nums[0]=1 <= 3 → pop 0
* add 1
* dq: [1] (values [3])
* no answer yet

### right=2 (-1)

* evict none
* pop back while <= -1: nums[1]=3 <= -1? no
* add 2
* dq: [1,2] (values [3,-1])
* right>=2 → ans[0] = nums[1] = 3 ✅

### right=3 (-3)

* evict indices <= 0: dq front=1, 1<=0? no
* pop back while <= -3: nums[2]=-1 <= -3? no
* add 3
* dq: [1,2,3] (values [3,-1,-3])
* ans[1] = nums[1] = 3 ✅

### right=4 (5)

* evict <= 1: dq front=1, 1<=1 yes → pop 1
* dq now [2,3]
* pop back while <= 5:

  * nums[3]=-3 <= 5 pop
  * nums[2]=-1 <= 5 pop
* add 4
* dq: [4] (values [5])
* ans[2] = 5 ✅

…and continuing gives `[3,3,5,5,6,7]`.
*/
class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        if (k == 1) return nums;

        int[] ans = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>(); // stores indices

        for (int right = 0; right < n; right++) {
            // 1) Remove indices that are out of this window
            // Window is [right - k + 1, right]
            while (!dq.isEmpty() && dq.peekFirst() <= right - k) {
                dq.pollFirst();
            }

            // 2) Maintain decreasing order in deque by value
            while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[right]) {
                dq.pollLast();
            }

            // 3) Add current index
            dq.addLast(right);

            // 4) If we have formed a full window, record the max
            if (right >= k - 1) {
                ans[right - k + 1] = nums[dq.peekFirst()];
            }
        }

        return ans;
    }
}






// Method 2: Max-Heap and sliding window approach
/*
## Idea: Max-heap of (value, index) + lazy deletion

We want the max in each window `[i..i+k-1]`.

Use a max-heap (PriorityQueue) that stores pairs:

* `value = nums[idx]`
* `index = idx`

As we slide the window, we push the new element into the heap.
The heap’s top might be an element that’s **no longer in the current window** (its index < left boundary). We remove such elements **lazily**: keep popping while top index is out of window.

### Why we need indices

Because values can repeat; the index tells us whether an entry is still inside the current window.

---

## Complexity

* Each element is inserted once: `O(n log n)` total insert cost.
* Each element is removed at most once: also `O(n log n)`.
* Overall: **O(n log n)** time, **O(n)** extra space.

This is slower than the deque O(n) solution, but it’s straightforward and very common.


## Thorough example walkthrough

Example:

* `nums = [1, 3, -1, -3, 5, 3, 6, 7]`
* `k = 3`

Windows and correct answers:

* `[1, 3, -1] -> 3`
* `[3, -1, -3] -> 3`
* `[-1, -3, 5] -> 5`
* `[-3, 5, 3] -> 5`
* `[5, 3, 6] -> 6`
* `[3, 6, 7] -> 7`

We’ll track `(value,index)` in the heap conceptually. (Heap internal order is not sorted list, but “top” is max.)

### right = 0, add (1,0)

Heap top: (1,0)
Window not full yet (need 3 items)

### right = 1, add (3,1)

Heap top: (3,1)
Window not full yet

### right = 2, add (-1,2)

Now window `[0..2]` full, left = 0
Heap top is (3,1), index 1 >= 0, valid
Answer for window starting at 0: `ans[0] = 3`

---

### right = 3, add (-3,3)

Now window `[1..3]`, left = 1
Heap top still (3,1), index 1 >= 1, valid
`ans[1] = 3`

---

### right = 4, add (5,4)

Now window `[2..4]`, left = 2
Heap top becomes (5,4), index 4 >= 2, valid
`ans[2] = 5`

---

### right = 5, add (3,5)

Now window `[3..5]`, left = 3
Heap top is still (5,4), index 4 >= 3, valid
`ans[3] = 5`

---

### right = 6, add (6,6)

Now window `[4..6]`, left = 4
Heap top becomes (6,6), index 6 >= 4, valid
`ans[4] = 6`

---

### right = 7, add (7,7)

Now window `[5..7]`, left = 5
Heap top becomes (7,7), index 7 >= 5, valid
`ans[5] = 7`

Final: `[3, 3, 5, 5, 6, 7]`

---

## Where “lazy deletion” matters (quick illustration)

Suppose the heap top is an old max that slid out:

Example small:

* `nums = [9, 1, 2, 3]`, `k=2`

At window `[1,2]` (indices 1..2), the heap might still have (9,0) at the top from earlier.
Since index 0 < left(=1), we pop it until the top index is inside the window.

That’s exactly what:

```java
while (pq.peek()[1] < left) pq.poll();
```

does.

---

If you want, I can also show an alternative heap approach using a `TreeMap` (multiset counts) which gives `O(n log k)` and avoids stale entries, but the above PriorityQueue version is the classic.
*/

// class Solution {
//     public int[] maxSlidingWindow(int[] nums, int k) {
//         int n = nums.length;
//         if (k == 1) return nums;

//         int[] ans = new int[n - k + 1];

//         // Max-heap by value; if tie, higher index first (tie-break not required but harmless)
//         PriorityQueue<int[]> pq = new PriorityQueue<>(
//             (a, b) -> {
//                 if (b[0] != a[0]) return b[0] - a[0]; // value descending
//                 return b[1] - a[1];                   // index descending
//             }
//         );

//         int left = 0;

//         for (int right = 0; right < n; right++) {
//             // add current element
//             pq.offer(new int[]{nums[right], right});

//             // once we have a full window of size k
//             if (right >= k - 1) {
//                 left = right - k + 1;

//                 // remove elements that are left of the window
//                 while (pq.peek()[1] < left) {
//                     pq.poll();
//                 }

//                 // top is the max in current window
//                 ans[left] = pq.peek()[0];
//             }
//         }

//         return ans;
//     }
// }
