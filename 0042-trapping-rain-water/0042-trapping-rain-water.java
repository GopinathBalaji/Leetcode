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
class Solution {
    public int trap(int[] h) {
        int n = h.length;
        if (n < 3) return 0;

        int left = 0, right = n - 1;
        int leftMax = 0, rightMax = 0;
        int ans = 0;

        while (left < right) {
            if (h[left] <= h[right]) {
                if (h[left] >= leftMax) leftMax = h[left];
                else ans += leftMax - h[left];
                left++;
            } else {
                if (h[right] >= rightMax) rightMax = h[right];
                else ans += rightMax - h[right];
                right--;
            }
        }
        return ans;
    }
}





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