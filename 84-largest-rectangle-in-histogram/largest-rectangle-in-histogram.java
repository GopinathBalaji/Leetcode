// Method 1: Using Monotonic Stack
/*
## Problem recap (what we’re computing)

You’re given bar heights `heights[0..n-1]`. Each bar has width `1`.

A rectangle in the histogram is defined by choosing:

* a contiguous range of bars `[L..R]`
* a height = **minimum height** in that range
  (because the rectangle can’t be taller than the shortest bar in that span)

Area:
[
\text{area} = (\min_{i \in [L..R]} heights[i]) \times (R-L+1)
]

Brute force: try every `[L..R]`, find min, compute area → **O(n²)** or worse.

We want **O(n)**.

---

## Key insight: “Each bar as the limiting (minimum) height”

Imagine we pick a bar at index `mid` with height `h = heights[mid]`.

If we want a rectangle where `h` is the minimum height, we can extend left and right until we hit a bar **strictly smaller** than `h`.

So for each bar `mid`, define:

* `leftSmaller(mid)` = nearest index to the left with height `< h`
* `rightSmaller(mid)` = nearest index to the right with height `< h`

Then the widest rectangle that uses `h` as the limiting height spans:

* from `leftSmaller(mid) + 1` to `rightSmaller(mid) - 1`

Width:
[
(rightSmaller - 1) - (leftSmaller + 1) + 1 = rightSmaller - leftSmaller - 1
]

Area:
[
h \times (rightSmaller - leftSmaller - 1)
]

So the whole problem becomes:

> For each index, find nearest smaller to the left and right efficiently.

That’s what the monotonic stack gives you.

---

## What the monotonic stack stores (and why)

We use a stack of **indices** (not heights), and we maintain this invariant:

### Invariant

`heights[stack[0]] < heights[stack[1]] < ... < heights[stack[top]]`

So heights are **strictly increasing** from bottom to top.

This means:

* the stack is a list of bars with increasing heights
* those bars have not yet found a smaller bar to their right

---

## How right boundary is found

We scan from left to right with index `i`.

When we see a new bar `heights[i]`:

* If `heights[i]` is **>=** the height on top of stack, we can push it safely (still increasing).
* If `heights[i]` is **smaller** than the height on the stack top, then we’ve found a “stop” to the right for that taller bar.

### Exactly when we pop

While:

```java
heights[stack.peek()] > heights[i]
```

we pop index `mid = stack.pop()`.

At that moment, we know:

✅ `rightSmaller(mid) = i`
Because `i` is the first index to the right where height is smaller than `heights[mid]`.

---

## How left boundary is found

After popping `mid`, the new stack top (if any) is the nearest index to the left with height < `heights[mid]`.

Why?

* Because the stack is increasing
* All bars taller than `heights[mid]` have been popped
* The one remaining on top is the closest smaller bar on the left

So:

✅ `leftSmaller(mid) = stack.peek()` (after the pop), if stack not empty
If stack is empty, it means there is no smaller bar on the left:

✅ `leftSmaller(mid) = -1`

---

## Computing width and area from boundaries

Once we know:

* `rightSmaller = i`
* `leftSmaller = stack.peek()` or `-1`

Then rectangle for `mid` can extend from:

* `leftSmaller + 1` to `rightSmaller - 1`

Width:

```java
width = rightSmaller - leftSmaller - 1
      = i - leftSmaller - 1
```

Area:

```java
area = heights[mid] * width
```

---

## Why we need a sentinel (flush at end)

If the histogram ends with increasing heights like `[2, 4, 5]`, nothing ever forces pops, so we would never compute rectangles for those bars.

Fix: do one extra iteration `i = n` with “height 0” (a guaranteed smaller bar), to pop everything.

# Thorough walkthrough (classic example)

`heights = [2, 1, 5, 6, 2, 3]`
Expected answer: `10` (rectangle covering bars 5 and 6 with height 5 → area 5×2=10)

We scan with `i` from 0 to n (n=6, so i goes 0..6).
Sentinel step `i=6` uses `currH=0`.

I’ll show stack as indices (and their heights).

---

## Start

* `stack = []`
* `maxArea = 0`

---

## i = 0, currH = 2

stack empty → push 0
stack: [0] (heights: [2])

---

## i = 1, currH = 1

Now `heights[stack.peek()] = heights[0] = 2 > 1` ⇒ pop.

### Pop mid = 0, h = 2

* after pop stack is empty ⇒ `leftSmaller = -1`
* `rightSmaller = i = 1`
* width = `1 - (-1) - 1 = 1`
* area = `2 * 1 = 2`
  maxArea = 2

Now push i=1
stack: [1] (heights: [1])

---

## i = 2, currH = 5

heights[1]=1 > 5? no → push 2
stack: [2, 1] (top-first) = indices [2,1]
Heights bottom→top: [1,5] increasing ✅

---

## i = 3, currH = 6

heights[2]=5 > 6? no → push 3
stack: [3,2,1]
Heights bottom→top: [1,5,6] increasing ✅

---

## i = 4, currH = 2

Now we have to pop while top height > 2.

### Pop mid = 3, h = 6

stack after pop: [2,1]

* leftSmaller = stack.peek() = 2 (height 5)
* rightSmaller = i = 4
* width = 4 - 2 - 1 = 1
* area = 6 * 1 = 6
  maxArea = max(2,6) = 6

### Still top height > 2?

top is index 2 height 5 > 2 ⇒ pop again

### Pop mid = 2, h = 5

stack after pop: [1]

* leftSmaller = 1 (height 1)
* rightSmaller = 4
* width = 4 - 1 - 1 = 2  (bars 2 and 3)
* area = 5 * 2 = 10
  maxArea = 10 ✅

### Still top height > 2?

top index 1 height 1 > 2? no stop popping

Now push i=4
stack: [4,1]
Heights bottom→top: [1,2] increasing ✅

---

## i = 5, currH = 3

top index 4 height 2 > 3? no → push 5
stack: [5,4,1]
Heights bottom→top: [1,2,3] increasing ✅

---

## i = 6 (sentinel), currH = 0

Now we pop everything > 0.

### Pop mid = 5, h = 3

stack after pop: [4,1]

* leftSmaller = 4 (height 2)
* rightSmaller = 6
* width = 6 - 4 - 1 = 1
* area = 3 * 1 = 3 (maxArea stays 10)

### Pop mid = 4, h = 2

stack after pop: [1]

* leftSmaller = 1
* rightSmaller = 6
* width = 6 - 1 - 1 = 4 (bars 2..5)
* area = 2 * 4 = 8 (maxArea stays 10)

### Pop mid = 1, h = 1

stack after pop: []

* leftSmaller = -1
* rightSmaller = 6
* width = 6 - (-1) - 1 = 6
* area = 1 * 6 = 6 (maxArea stays 10)

Push i=6 (optional, loop ends anyway)

Done. Answer = **10**.

---

## Why this finds the maximum

Every bar is popped exactly once, and at pop time we compute the **maximum-width rectangle** where that bar is the limiting height. So we evaluate all candidate “best rectangles” efficiently.

---

If you want, I can also explain how choosing `>` vs `>=` in the while condition affects handling equal heights (both can work if consistent), or give a version that stores `(startIndex, height)` pairs instead of indices.
*/
import java.util.*;

class Solution {
    public int largestRectangleArea(int[] heights) {
        int n = heights.length;
        Deque<Integer> stack = new ArrayDeque<>(); // indices, heights increasing
        int maxArea = 0;

        for (int i = 0; i <= n; i++) {
            int currH = (i == n) ? 0 : heights[i]; // sentinel 0 at the end

            // If current bar is smaller, finalize rectangles for taller bars
            while (!stack.isEmpty() && heights[stack.peek()] > currH) {
                int mid = stack.pop();
                int h = heights[mid];

                int leftSmaller = stack.isEmpty() ? -1 : stack.peek();
                int rightSmaller = i;

                int width = rightSmaller - leftSmaller - 1;
                int area = h * width;

                maxArea = Math.max(maxArea, area);
            }

            stack.push(i);
        }

        return maxArea;
    }
}
