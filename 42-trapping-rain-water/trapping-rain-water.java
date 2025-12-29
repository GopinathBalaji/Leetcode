// Method 1: Using Prefix and Suffix Max Arrays (Dynamic Programming Approach)
/*
## Detailed explanation

At each index `i`, water can only rise as high as the **shorter** of:

* the tallest wall to the **left** of `i` (including `i`) → `leftMax[i]`
* the tallest wall to the **right** of `i` (including `i`) → `rightMax[i]`

So the maximum water level above `i` is:

* `waterLevel(i) = min(leftMax[i], rightMax[i])`

Then trapped water at `i` is:

* `water(i) = max(0, waterLevel(i) - height[i])`

### Why prefix/suffix arrays help

If you compute `leftMax[i]` and `rightMax[i]` for all `i`, then each `water(i)` is O(1), so total is O(n).

---

## Thorough example walkthrough

Use the classic:
`height = [0,1,0,2,1,0,1,3,2,1,2,1]`

Index:     `0 1 2 3 4 5 6 7 8 9 10 11`
Height:    `0 1 0 2 1 0 1 3 2 1  2  1`

### 1) Build `leftMax`

`leftMax[i] = max(height[0..i])`

* i=0: leftMax[0] = 0
* i=1: max(0,1)=1
* i=2: max(1,0)=1
* i=3: max(1,2)=2
* i=4: max(2,1)=2
* i=5: max(2,0)=2
* i=6: max(2,1)=2
* i=7: max(2,3)=3
* i=8: max(3,2)=3
* i=9: max(3,1)=3
* i=10: max(3,2)=3
* i=11: max(3,1)=3

So:
`leftMax = [0,1,1,2,2,2,2,3,3,3,3,3]`

### 2) Build `rightMax`

`rightMax[i] = max(height[i..n-1])` (build from right to left)

* i=11: rightMax[11] = 1
* i=10: max(1,2)=2
* i=9:  max(2,1)=2
* i=8:  max(2,2)=2
* i=7:  max(2,3)=3
* i=6:  max(3,1)=3
* i=5:  max(3,0)=3
* i=4:  max(3,1)=3
* i=3:  max(3,2)=3
* i=2:  max(3,0)=3
* i=1:  max(3,1)=3
* i=0:  max(3,0)=3

So:
`rightMax = [3,3,3,3,3,3,3,3,2,2,2,1]`

### 3) Compute trapped water per index

For each `i`:

* `waterLevel = min(leftMax[i], rightMax[i])`
* `trapped = waterLevel - height[i]` if positive

Let’s tabulate the key ones:

| i  | height | leftMax | rightMax | waterLevel | trapped |
| -- | ------ | ------- | -------- | ---------- | ------- |
| 0  | 0      | 0       | 3        | 0          | 0       |
| 1  | 1      | 1       | 3        | 1          | 0       |
| 2  | 0      | 1       | 3        | 1          | 1       |
| 3  | 2      | 2       | 3        | 2          | 0       |
| 4  | 1      | 2       | 3        | 2          | 1       |
| 5  | 0      | 2       | 3        | 2          | 2       |
| 6  | 1      | 2       | 3        | 2          | 1       |
| 7  | 3      | 3       | 3        | 3          | 0       |
| 8  | 2      | 3       | 2        | 2          | 0       |
| 9  | 1      | 3       | 2        | 2          | 1       |
| 10 | 2      | 3       | 2        | 2          | 0       |
| 11 | 1      | 3       | 1        | 1          | 0       |

Sum of trapped: `1 + 1 + 2 + 1 + 1 = 6`

✅ Final answer: **6**

---

### Common pitfalls (that your earlier version hit)

* `rightMax` must be built **from right to left**, because it depends on `rightMax[i+1]`.
* Handle `n == 0` to avoid indexing errors.
*/
class Solution {
    public int trap(int[] height) {
        int n = height.length;
        if(n == 0){
            return 0;
        }

        int ans = 0;

        int[] leftMax = new int[n];
        leftMax[0] = height[0];

        int[] rightMax = new int[n];
        rightMax[n-1] = height[n-1];

        for(int i=1; i<n; i++){
            leftMax[i] = Math.max(leftMax[i-1], height[i]);
        }

        for(int i=n-2; i>=0; i--){
            rightMax[i] = Math.max(rightMax[i+1], height[i]);
        }

        for(int i=0; i<n; i++){
            int val = Math.min(leftMax[i], rightMax[i]) - height[i];
            if(val > 0){
                ans += val;
            }
        }

        return ans;
    }
}








// Method 2: Two Pointer (does the same thing as Prefix and Suffix Max-Arrays without extra space)
/*
## Detailed explanation (two pointers, O(1) space)

### 1) What water at an index depends on

At index `i`, the water level is limited by the **shorter** of:

* the tallest bar to its left
* the tallest bar to its right

So conceptually:
`water[i] = max(0, min(leftMax(i), rightMax(i)) - height[i])`

### 2) Why we can do it with two pointers

Instead of building full `leftMax[]` and `rightMax[]`, we keep:

* `leftMax`: tallest bar seen so far from the left (0..l)
* `rightMax`: tallest bar seen so far from the right (r..n-1)

We move pointers inward (`l` and `r`) and **finalize** water on one side each step.

### 3) The key insight: finalize the side with the smaller boundary

If `height[l] <= height[r]`, then the **right side has a wall at least as high as `height[r]`, which is ≥ `height[l]`**.
That means for position `l`, the limiting factor is the **left side** (what we’ve seen as `leftMax`), because the right boundary is “tall enough” to hold water up to at least `height[l]`.

So when `height[l] <= height[r]`, we can safely compute water at `l` using only `leftMax`:

* if `height[l] < leftMax`, trapped water is `leftMax - height[l]`
* else update `leftMax`

Symmetrically, if `height[l] > height[r]`, we finalize on the right side using `rightMax`.

---

## Thorough walkthrough example

Use the classic:
`height = [0,1,0,2,1,0,1,3,2,1,2,1]`

Start:

* `l=0`, `r=11`
* `leftMax=0`, `rightMax=0`
* `ans=0`

I’ll show the important steps (every move is the same logic).

### Step-by-step trace

**1) l=0 (0), r=11 (1)**
`height[l] <= height[r]` → process left

* `height[l]=0 >= leftMax(0)` → `leftMax=0`
* `l++` → l=1

**2) l=1 (1), r=11 (1)**
process left

* `1 >= leftMax(0)` → `leftMax=1`
* l=2

**3) l=2 (0), r=11 (1)**
process left

* `0 < leftMax(1)` → trap `1 - 0 = 1`
* `ans=1`
* l=3

**4) l=3 (2), r=11 (1)**
Now `height[l] > height[r]` → process right

* `height[r]=1 >= rightMax(0)` → `rightMax=1`
* r=10

**5) l=3 (2), r=10 (2)**
`2 <= 2` → process left

* `2 >= leftMax(1)` → `leftMax=2`
* l=4

**6) l=4 (1), r=10 (2)**
process left

* `1 < leftMax(2)` → trap `2 - 1 = 1`
* ans=2
* l=5

**7) l=5 (0), r=10 (2)**
process left

* trap `2 - 0 = 2`
* ans=4
* l=6

**8) l=6 (1), r=10 (2)**
process left

* trap `2 - 1 = 1`
* ans=5
* l=7

**9) l=7 (3), r=10 (2)**
`3 > 2` → process right

* `height[r]=2 >= rightMax(1)` → `rightMax=2`
* r=9

**10) l=7 (3), r=9 (1)**
process right

* `1 < rightMax(2)` → trap `2 - 1 = 1`
* ans=6
* r=8

**11) l=7 (3), r=8 (2)**
process right

* `2 >= rightMax(2)` → `rightMax=2`
* r=7

Stop (l == r).
Final `ans = 6` ✅

Matches expected output.

---

## Why this never misses water

* We only compute water on a side when we *know* the opposite side has a boundary tall enough that the limiting factor is our current side’s max (`leftMax` or `rightMax`).
* Each index is processed exactly once (either when `l` passes it or `r` passes it).
*/

// class Solution {
//     public int trap(int[] height) {
//         int n = height.length;
//         int l = 0, r = n - 1;

//         int leftMax = 0, rightMax = 0;
//         int ans = 0;

//         while (l < r) {
//             // Decide which side we can "finalize" based on the smaller boundary
//             if (height[l] <= height[r]) {
//                 // left side is the limiting side
//                 if (height[l] >= leftMax) {
//                     leftMax = height[l];          // new best left wall
//                 } else {
//                     ans += leftMax - height[l];   // water trapped above l
//                 }
//                 l++;
//             } else {
//                 // right side is the limiting side
//                 if (height[r] >= rightMax) {
//                     rightMax = height[r];         // new best right wall
//                 } else {
//                     ans += rightMax - height[r];  // water trapped above r
//                 }
//                 r--;
//             }
//         }

//         return ans;
//     }
// }








// Method 3: Stack / Monotonic Decreasing Stack Approach
/*
### Intuition

Think of bars as forming “bowls.” Water gets trapped when you find a **left boundary** and a **right boundary** with some **lower bars in between**.

A **monotonic decreasing stack** (store indices) helps you detect exactly when a right boundary appears that can trap water over a “valley.”

**Time:** `O(n)` (each index pushed/popped at most once)
**Space:** `O(n)` in worst case (strictly decreasing heights)

---

## Why it works (detailed explanation)

### What the stack stores

* The stack stores **indices**.
* Their heights are in **non-increasing order** from top to bottom (i.e., stack top is the most recent, and it is the smallest among the recent candidates).

So if the stack is:
`[top ... bottom] = [idxA, idxB, idxC]`
then:
`height[idxA] <= height[idxB] <= height[idxC]`

### When do we trap water?

When we see a new bar `height[i]` that is **higher than the height at the top** of the stack, it means:

* the top index is a **valley bottom** that now has a **right boundary** at `i`.
* after popping bottom, the new top (if exists) is the **left boundary**.

Now we can compute water trapped above that bottom:

* `left = st.peek()` (after popping)
* `right = i`
* `bottom = popped index`

**Width** between boundaries (number of cells that can hold water):
[
width = right - left - 1
]

**Water height** above the bottom:
[
boundedHeight = \min(height[left], height[right]) - height[bottom]
]

Add:
[
ans += width \times boundedHeight
]

If after popping, stack is empty → no left boundary → cannot trap water.

---

## Thorough walkthrough (classic example)

`height = [0,1,0,2,1,0,1,3,2,1,2,1]`

I’ll show the key trapping moments.
Stack holds indices; I’ll annotate as `index(height)`.

### Start

`ans = 0`, `st = []`

---

### i = 0, h=0

Push 0
`st = [0(0)]`

### i = 1, h=1

While `1 > height[0]=0` → pop bottom = 0
Now stack empty → break (no left boundary)
Push 1
`st = [1(1)]`

### i = 2, h=0

`0 > height[1]=1`? no
Push 2
`st = [2(0), 1(1)]` (top first)

---

### i = 3, h=2  ✅ big event

Current 2 > height[2]=0 → pop bottom = 2(0)
Now left boundary is `st.peek() = 1(1)` (stack not empty)

Compute:

* left=1 (height 1)
* right=3 (height 2)
* bottom=2 (height 0)
* width = 3 - 1 - 1 = 1
* boundedHeight = min(1,2) - 0 = 1

Add water: `1 * 1 = 1` → `ans=1`

Continue while:
2 > height[1]=1 → pop bottom=1
stack empty → break

Push 3
`st = [3(2)]`

---

### i = 4, h=1

1 > 2? no
Push 4
`st = [4(1), 3(2)]`

### i = 5, h=0

0 > 1? no
Push 5
`st = [5(0), 4(1), 3(2)]`

### i = 6, h=1  ✅ event

1 > height[5]=0 → pop bottom=5(0)
left boundary = 4(1)

* width = 6 - 4 - 1 = 1
* boundedHeight = min(height[4]=1, height[6]=1) - height[5]=0
  = 1 - 0 = 1
  Add `1*1=1` → `ans=2`

Now check again:
1 > height[4]=1? no (strict >), stop

Push 6
`st = [6(1), 4(1), 3(2)]`

---

### i = 7, h=3  ✅ huge event (multiple pops)

3 > height[6]=1 → pop bottom=6(1)
left boundary = 4(1)

* width = 7 - 4 - 1 = 2 (indices 5 and 6 in between)
* boundedHeight = min(1,3) - 1 = 0 → add 0

Continue:
3 > height[4]=1 → pop bottom=4(1)
left boundary = 3(2)

* width = 7 - 3 - 1 = 3 (indices 4,5,6)
* boundedHeight = min(2,3) - 1 = 1
  Add `3 * 1 = 3` → `ans=5`

Continue:
3 > height[3]=2 → pop bottom=3(2)
stack empty → break

Push 7
`st = [7(3)]`

(That “3 units” corresponds to filling positions 4,5,6 up to height 2.)

---

### i = 8, h=2

2 > 3? no
Push 8
`st = [8(2), 7(3)]`

### i = 9, h=1

Push 9
`st = [9(1), 8(2), 7(3)]`

### i = 10, h=2  ✅ event

2 > height[9]=1 → pop bottom=9(1)
left boundary = 8(2)

* width = 10 - 8 - 1 = 1
* boundedHeight = min(2,2) - 1 = 1
  Add `1*1=1` → `ans=6`

Now 2 > height[8]=2? no
Push 10
`st = [10(2), 8(2), 7(3)]`

### i = 11, h=1

Push 11, end.
Final `ans = 6` ✅

Matches the expected answer.

---

## What to remember (implementation checklist)

* Stack stores **indices**.
* Maintain **decreasing heights** in the stack.
* When `height[i] > height[top]`, pop a **bottom** and compute water with:

  * `left = new top after pop`
  * `right = i`
  * `width = right - left - 1`
  * `boundedHeight = min(h[left], h[right]) - h[bottom]`
* If stack becomes empty after popping → no left boundary → stop that calculation.
*/

// class Solution {
//     public int trap(int[] height) {
//         int n = height.length;
//         int ans = 0;

//         Deque<Integer> st = new ArrayDeque<>(); // holds indices, heights are decreasing

//         for (int i = 0; i < n; i++) {
//             // While current bar is taller than the bar at stack top,
//             // we've found a right boundary that can trap water.
//             while (!st.isEmpty() && height[i] > height[st.peek()]) {
//                 int bottom = st.pop(); // valley bottom index

//                 // If stack is empty now, there's no left boundary
//                 if (st.isEmpty()) break;

//                 int left = st.peek();  // left boundary index
//                 int width = i - left - 1;

//                 int boundedHeight = Math.min(height[left], height[i]) - height[bottom];
//                 if (boundedHeight > 0) {
//                     ans += width * boundedHeight;
//                 }
//             }

//             // Push current bar as a candidate boundary/valley
//             st.push(i);
//         }

//         return ans;
//     }
// }












/*
##################### ALL OLD APPROACHES #####################

// Two Pointer Method (O(n) time, O(1) space)
/*
Idea: keep left and right pointers plus leftMax and rightMax.
Always advance the pointer with the smaller height, because that side’s water is decided by its own max.

Walkthrough on [4,2,0,3,2,5]

Start: left=0 (4), right=5 (5), leftMax=0, rightMax=0, ans=0.
h[left] <= h[right] → handle left:
leftMax=4, move left=1.
left=1 (2): 2 < leftMax(4) → add 4-2=2 (ans=2). left=2.
left=2 (0): add 4-0=4 (ans=6). left=3.
left=3 (3): add 4-3=1 (ans=7). left=4.
left=4 (2): add 4-2=2 (ans=9). left=5.
loop ends. Answer = 9.

At each step we only need the smaller-side max to determine water immediately on that side.
*/
// class Solution {
//     public int trap(int[] h) {
//         int n = h.length;
//         if (n < 3) return 0;

//         int left = 0, right = n - 1;
//         int leftMax = 0, rightMax = 0;
//         int ans = 0;

//         while (left < right) {
//             if (h[left] <= h[right]) {
//                 if (h[left] >= leftMax) leftMax = h[left];
//                 else ans += leftMax - h[left];
//                 left++;
//             } else {
//                 if (h[right] >= rightMax) rightMax = h[right];
//                 else ans += rightMax - h[right];
//                 right--;
//             }
//         }
//         return ans;
//     }
// }





// Method 2: Prefix and Suffix Maxima (O(n) time, O(n) space)
/*
The **prefix & suffix maxima** method works by turning the “global context” each index needs into two simple lookups. Here’s the idea, why it’s correct, and what those arrays buy you.

---

# The physics in one line

At position `i`, the water level can rise only as high as the **shorter** of the tallest walls to its **left** and to its **right**:

```
water(i) = max(0, min( leftMax(i), rightMax(i) ) - height[i])
```

So if we knew, for every `i`:

* `left[i]`  = the tallest bar from index `0 … i`
* `right[i]` = the tallest bar from index `i … n-1`

then the water above `i` is just:

```
level(i) = min(left[i], right[i])
water(i) = max(0, level(i) - height[i])
```

That’s exactly what the **prefix** (left) and **suffix** (right) maxima arrays give us.

---

# What the two arrays achieve

1. **Condense global info into O(1) lookups.**
   Every `i` needs to know “how high is the best wall somewhere to my left?” and “…to my right?”. The two arrays precompute those answers once so each index can be handled in constant time.

2. **Make the waterline formula local.**
   With `left[i]` and `right[i]` precomputed, `water(i)` doesn’t depend on any other `j` at runtime. You just apply `min(left[i], right[i]) - height[i]`.

3. **Correctness is obvious from the min-of-two-walls picture.**
   If either side has no wall taller than `height[i]`, water spills; if both sides have tall walls, the shorter side caps the waterline.

4. **Linear time, simple code.**
   Build `left` in one forward pass; build `right` in one backward pass; then sum water in one more pass. Total **O(n)** time, **O(n)** extra space.

---

# Mini diagram intuition

Think of cross-sections:

```
left wall ┃      ┃ right wall
           ╲    ╱
            ╲__╱   <-- water surface is flat, bounded by the shorter wall
```

If the right wall is shorter, water can’t exceed it even if the left wall is huge.
So `level = min(leftMax, rightMax)` is the right cap.

---

# Worked example

`height = [4, 2, 0, 3, 2, 5]`

## 1) Prefix maxima (left)

Scan left→right, carry the max so far:

```
left:   [4, 4, 4, 4, 4, 5]
          ^  ^  ^  ^  ^  ^
 tallest seen up to i
```

## 2) Suffix maxima (right)

Scan right→left, carry the max so far:

```
right:  [5, 5, 5, 5, 5, 5]
```

## 3) Water at each index

`level[i] = min(left[i], right[i])`
`water[i] = max(0, level[i] - height[i])`

| i | height | left | right | level=min | water=level−height |
| - | ------ | ---- | ----- | --------- | ------------------ |
| 0 | 4      | 4    | 5     | 4         | 0                  |
| 1 | 2      | 4    | 5     | 4         | 2                  |
| 2 | 0      | 4    | 5     | 4         | 4                  |
| 3 | 3      | 4    | 5     | 4         | 1                  |
| 4 | 2      | 4    | 5     | 4         | 2                  |
| 5 | 5      | 5    | 5     | 5         | 0                  |

Sum = `0+2+4+1+2+0 = 9`.

Notice how we never needed to “look around” once we had `left[]` and `right[]`.

---

# Another quick example

`height = [0,1,0,2,1,0,1,3,2,1,2,1]`

Compute (abbreviated):

```
left  = [0,1,1,2,2,2,2,3,3,3,3,3]
right = [3,3,3,3,3,2,2,3,2,2,2,1]
level =  min(left,right) = [0,1,1,2,2,2,2,3,2,2,2,1]
water = level - height   = [0,0,1,0,1,2,1,0,0,1,0,0]
sum = 6
```

Again, at each index, the shorter of the two caps defines the waterline.

---

# Why edges don’t hold water

At `i=0`, `left[0] = height[0]` (there’s nothing to the left).
At `i=n-1`, `right[n-1] = height[n-1]` (nothing to the right).
So `level` equals the bar itself, and `level - height = 0`.

---

# Complexity, trade-offs, and alternatives

* **Time:** O(n) for three linear passes.
* **Space:** O(n) for `left[]` and `right[]`.

If you want **O(1) space**, use the **two-pointer** method: keep `leftMax` and `rightMax` on the fly and move the pointer on the side with the smaller current height. It computes the same `min(leftMax,rightMax)` logic without storing the arrays.

The **monotonic stack** method is another O(n) alternative that builds water basins by finding left/right walls around “bottoms”.

---

## TL;DR

* `left[i]` and `right[i]` summarize “best wall to the left/right”.
* Water at `i` is `min(left[i], right[i]) - height[i]` (clamped at 0).
* Precomputing them lets you compute each cell’s water in O(1) and sum in O(n).
*/

// class Solution {
//     public int trap(int[] h) {
//         int n = h.length;
//         if (n < 3) return 0;

//         int[] left = new int[n];
//         int[] right = new int[n];

//         left[0] = h[0];
//         for (int i = 1; i < n; i++) left[i] = Math.max(left[i - 1], h[i]);

//         right[n - 1] = h[n - 1];
//         for (int i = n - 2; i >= 0; i--) right[i] = Math.max(right[i + 1], h[i]);

//         int ans = 0;
//         for (int i = 0; i < n; i++) {
//             int level = Math.min(left[i], right[i]);
//             if (level > h[i]) ans += level - h[i];
//         }
//         return ans;
//     }
// }






// Method 3: Monotonic Stack (O(n) time, O(n) space)
/*
# Intuition (what are we modeling?)

Water trapped over index `i` depends on the **nearest higher wall on the left** and the **nearest higher wall on the right**. If we sweep left→right, every time we encounter a bar that is **taller** than something earlier, we might have just **closed a basin**: the earlier lower bar can now trap water between a **left wall** (some bar before it) and the **current bar** (right wall).

So we maintain a stack of **indices** forming a **non-increasing sequence of heights**. That way, when a higher bar arrives, we pop “valley bottoms” and compute water for the basin bounded by:

* **right wall** = current bar `i`
* **bottom** = popped index
* **left wall** = new top of stack after popping

# Invariant (why a non-increasing stack?)

At any time, `height[stack[0]] ≥ height[stack[1]] ≥ …` (top is last).
This ensures: when a new bar `i` has `height[i] > height[stack.peek()]`, the popped bar is a **bottom** strictly lower than both a left wall (still on stack) and the current right wall. That gives a well-defined basin.

# Geometry per pop

When we pop `bottom`, if the stack becomes empty → there’s **no left wall** ⇒ no water (skip).

Otherwise, let:

* `left = stack.peek()` (index of the left wall)
* `right = i` (current index, right wall)
* **Width** between walls (excluding them): `width = right - left - 1`
* **Bounded height**: `min(height[left], height[right]) - height[bottom]`
* **Water added**: `boundedHeight * width` (if boundedHeight > 0)

Why exclude the walls in the width? Water sits **between** walls, not on top of them.


### Notes

* We compare with `>` (not `>=`). Equal heights do **not** trigger a pop; this avoids double counting across plateaus. (Using `>=` also works if you’re careful; `>` is the common pattern.)
* Using **indices** (not heights) lets us compute widths.
* Complexity: **O(n)** time (each index pushed/popped at most once), **O(n)** space (stack).

---

# Thorough walkthrough

Let’s walk the classic example:

```
height = [0,1,0,2,1,0,1,3,2,1,2,1]
index     0 1 2 3 4 5 6 7 8 9 10 11
```

We’ll track `(stack content as indices | heights)`, and the water added when we pop.

Start: `ans=0`, `st=[]`

**i=0, h=0**

* Stack empty → push 0 → `st=[0|0]`

**i=1, h=1**

* h\[1]=1 > h\[0]=0 → pop bottom=0; stack empty now → no left wall → no water
* Push 1 → `st=[1|1]`

**i=2, h=0**

* 0 > 1? no → push 2 → `st=[2|0, 1|1]` (top to left)

**i=3, h=2**

* 2 > h\[2]=0 → pop bottom=2
  left = st.peek = 1
  width = 3 - 1 - 1 = 1
  bounded = min(h\[1]=1, h\[3]=2) - h\[2]=0 = 1 → add 1×1 = **1** (ans=1)
* 2 > h\[1]=1 → pop bottom=1
  stack now empty → no left wall → stop popping
* Push 3 → `st=[3|2]`

**i=4, h=1**

* 1 > 2? no → push 4 → `st=[4|1, 3|2]`

**i=5, h=0**

* 0 > 1? no → push 5 → `st=[5|0, 4|1, 3|2]`

**i=6, h=1**

* 1 > h\[5]=0 → pop bottom=5
  left = 4
  width = 6 - 4 - 1 = 1
  bounded = min(h\[4]=1, h\[6]=1) - h\[5]=0 = 1 → add 1×1 = **1** (ans=2)
* 1 > h\[4]=1? no (equal) → stop
* Push 6 → `st=[6|1, 4|1, 3|2]`

**i=7, h=3**

* 3 > h\[6]=1 → pop bottom=6
  left = 4
  width = 7 - 4 - 1 = 2
  bounded = min(h\[4]=1, h\[7]=3) - h\[6]=1 = 0 → add **0**
* 3 > h\[4]=1 → pop bottom=4
  left = 3
  width = 7 - 3 - 1 = 3
  bounded = min(h\[3]=2, h\[7]=3) - h\[4]=1 = 1 → add 1×3 = **3** (ans=5)
* 3 > h\[3]=2 → pop bottom=3
  stack empty → stop
* Push 7 → `st=[7|3]`

**i=8, h=2**

* 2 > 3? no → push 8 → `st=[8|2, 7|3]`

**i=9, h=1**

* 1 > 2? no → push 9 → `st=[9|1, 8|2, 7|3]`

**i=10, h=2**

* 2 > h\[9]=1 → pop bottom=9
  left = 8
  width = 10 - 8 - 1 = 1
  bounded = min(h\[8]=2, h\[10]=2) - h\[9]=1 = 1 → add 1×1 = **1** (ans=6)
* 2 > h\[8]=2? no → stop
* Push 10 → `st=[10|2, 8|2, 7|3]`

**i=11, h=1**

* 1 > 2? no → push 11 → `st=[11|1, 10|2, 8|2, 7|3]`

End → `ans = 6`, which matches the known answer.

### What happened conceptually

* Each time we found a **right wall** taller than a **bottom**, we computed a basin bounded by that right wall and the closest **left wall** (still on stack).
* Width spans the bars **between** left and right; bounded height is “how much higher the shorter wall is than the bottom.”
* Because the stack is kept non-increasing, basins don’t overlap and every trapped rectangle is counted exactly once.

---

# Another quick example

`height = [4,2,0,3,2,5]` (answer 9)

You’ll see pops at `i=3` (closing `[4,2,0,3]`) and at `i=5` (closing everything up to left wall 4), adding areas `2 + 2 + 1 + 1 + 4 = 9`. (I can expand this step-by-step if you want the full trace, but it follows exactly the same mechanics.)

---

# Common pitfalls & FAQs

* **Why indices, not heights?** You need **positions** to compute `width = right - left - 1`. Storing just heights loses that.
* **Why break when stack becomes empty?** No left wall ⇒ water spills left; that basin contributes 0.
* **Why `>` and not `>=` in the while?** Using `>` treats equal bars as a single plateau wall, avoiding double counting. `>=` can also work if you change how you push plateaus (advanced detail) — the `>` version is the standard, safer choice.
* **Can bounded be negative?** With the invariant, it shouldn’t be; the `> h[bottom]` test ensures the current bar is higher than the bottom, and the left wall is ≥ the bottom (otherwise it wouldn’t still be on the stack). The `max(…, 0)` check is optional safety.

---

## TL;DR

* Maintain a **non-increasing stack of indices**.
* On a taller bar, **pop a bottom**, pair it with the **nearest left wall** (stack.peek) and the **current right wall**, and add the rectangle `min(left,right) − bottom` (height) × gap (width).
* Linear time, clean geometry, and very interview-friendly once you picture the basins.
*/

// class Solution {
//     public int trap(int[] h) {
//         int n = h.length, ans = 0;
//         Deque<Integer> st = new ArrayDeque<>(); // stack of indices (heights non-increasing)

//         for (int i = 0; i < n; i++) {
//             // Close basins while current bar is a taller right wall
//             while (!st.isEmpty() && h[i] > h[st.peek()]) {
//                 int bottom = st.pop();

//                 if (st.isEmpty()) break;  // no left wall -> no water

//                 int left = st.peek();     // left wall index
//                 int width = i - left - 1; // exclude walls
//                 int bounded = Math.min(h[left], h[i]) - h[bottom];
//                 if (bounded > 0) ans += bounded * width;
//             }
//             st.push(i); // push current bar as potential wall/bottom
//         }
//         return ans;
//     }
// }
