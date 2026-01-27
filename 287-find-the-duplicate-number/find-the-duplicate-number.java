// Method 1: Binary Search Approach
/*
## Why a duplicate must exist (pigeonhole principle)

You have:

* `n + 1` numbers in the array
* each number is in the range `1..n` (only **n** possible values)

Think of each possible value `1..n` as a **pigeonhole** (a slot).
Each array element is a **pigeon** that must go into the slot matching its value.

You have **n+1 pigeons** but only **n holes**.
If you put each pigeon into its hole, at least one hole must contain **2 or more pigeons**.

That means **some value repeats** → a duplicate exists.

---

## Binary search idea (search the value domain, not indices)

Instead of searching positions, we search the possible duplicate value in the range:

* `low = 1`, `high = n`

Pick `mid`.

Let:
`count = number of elements in nums that are <= mid`

### Key counting fact

If there were **no duplicates among values 1..mid**, then there can be **at most mid** numbers `<= mid` (one for each distinct value).

So:

* If `count > mid`, that means the range `1..mid` has **more numbers than distinct values available** → duplicate is **in [1..mid]**
* Else duplicate is **in [mid+1..n]**

This creates a monotonic condition, so binary search works.


**Time:** `O(n log n)`
**Extra space:** `O(1)`
Array is not modified.

---

## Thorough walkthrough example

Example: `nums = [1, 3, 4, 2, 2]`

Here `n = 4` (since length is 5), value range is `[1..4]`.
Duplicate is `2`.

### Start

`low = 1`, `high = 4`

---

### Iteration 1

`mid = (1 + 4) / 2 = 2`

Count elements `<= 2`:

Array: `[1, 3, 4, 2, 2]`

* `1` <= 2 ✅
* `3` <= 2 ❌
* `4` <= 2 ❌
* `2` <= 2 ✅
* `2` <= 2 ✅

So `count = 3`

Compare `count` with `mid`:

* `count = 3`
* `mid = 2`
* `count > mid` ✅

Interpretation (pigeonhole in action):

* Values allowed in `[1..2]` are only `{1,2}` → only **2 holes**
* But we found **3 numbers** that fall in that range → **3 pigeons**
* So there must be a duplicate among `{1,2}`

Therefore:
`high = mid = 2`

Now range is `[1..2]`

---

### Iteration 2

`low = 1`, `high = 2`
`mid = (1 + 2) / 2 = 1`

Count elements `<= 1`:

* only the `1` qualifies

So `count = 1`

Compare:

* `count = 1`
* `mid = 1`
* `count > mid` ❌ (it’s equal)

Interpretation:

* In `[1..1]` there is only `{1}` (1 hole)
* We found exactly 1 number ≤ 1, so no “overflow” in this left half
* Duplicate is not forced into `[1..1]`

So:
`low = mid + 1 = 2`

Now `low = high = 2`

Stop and return `2`.

✅ Duplicate = 2

---

## Why the condition is monotonic (why binary search is valid)

Define:
`f(x) = count of nums elements <= x`

As `x` increases, `f(x)` never decreases (more values qualify).

* If at some `x`, `f(x) > x`, that means “too many pigeons in first x holes”.
* For any larger `x`, `f(x)` will still be ≥ that earlier count, but the comparison changes. Still, the “duplicate is in the left side” logic lets you shrink consistently.

This gives a reliable left/right decision at every mid.

---

## Another quick example (different layout)

`nums = [3,1,3,4,2]` (n=4, duplicate=3)

* range [1..4], mid=2
* count <=2: {1,2} → 2 (not >2) → go right [3..4]
* mid=3
* count <=3: {3,1,3,2} → 4 (>3) → go left [3..3]
  Return 3.

---

If you want, I can also contrast this with the **Floyd cycle detection** approach and explain why that one is `O(n)` time while this is `O(n log n)`, and when you might prefer one over the other.
*/
class Solution {
    public int findDuplicate(int[] nums) {
        int n = nums.length - 1;   // values are in [1..n]
        int low = 1, high = n;

        while (low < high) {
            int mid = low + (high - low) / 2;

            int count = 0;
            for (int x : nums) {
                if (x <= mid) count++;
            }

            if (count > mid) {
                // duplicate is in [low..mid]
                high = mid;
            } else {
                // duplicate is in [mid+1..high]
                low = mid + 1;
            }
        }

        return low;
    }
}







// Method 2: 2 Pointer (Fast and Slow) Approach
/*
## 1) Turn the array into a “next pointer” graph

You’re given:

* `nums.length = n + 1`
* `nums[i] ∈ [1..n]`

Define a function / pointer:

* **next(i) = nums[i]**

So from an index `i`, you “jump” to index `nums[i]`.

Example: if `nums[0] = 3`, then from node `0` you go to node `3`.

This creates a directed graph where each node has **out-degree 1** (exactly one outgoing edge), which is exactly a “linked list” shape (a chain that eventually cycles).

### Why a cycle must exist (pigeonhole intuition)

After the first step from `0`, you land in `[1..n]`. From then on, you’re always in `[1..n]` (because values are in `[1..n]`).

But there are only `n` possible nodes in `[1..n]`. If you keep jumping forever, you must eventually revisit some node → that forms a **cycle**.

---

## 2) Why the duplicate corresponds to the cycle entry

Because there are `n+1` indices but only `n` possible next-values `[1..n]`, at least one value repeats (the duplicate). In the pointer view, “a value repeats” means:

Two different indices point to the same next node:

* `nums[a] = x` and `nums[b] = x` with `a ≠ b`

That means node `x` has **in-degree ≥ 2**, which is exactly where paths merge—this is the **entry point** into the cycle when starting from `0`.

So:

* **Duplicate number = cycle entry node**

---

## 3) Floyd’s algorithm in two phases

### Phase 1: Find an intersection inside the cycle

Use two pointers:

* `slow` moves 1 step each time: `slow = nums[slow]`
* `fast` moves 2 steps each time: `fast = nums[nums[fast]]`

They must meet somewhere inside the cycle.

### Phase 2: Find the cycle entry (the duplicate)

Reset one pointer to the start:

* set `slow = nums[0]` (or `slow = 0` in a slightly different variant)
* keep `fast` at the meeting point

Then move both 1 step at a time:

* `slow = nums[slow]`
* `fast = nums[fast]`

Where they meet is the **cycle entry**, i.e., the duplicate value.

Time: **O(n)**
Extra space: **O(1)**
Does not modify the array.

---

## 4) Thorough example walkthrough

### Example: `nums = [1, 3, 4, 2, 2]`

Here `n=4`, length is 5, values are in `[1..4]`. Duplicate is `2`.

Let’s write the “next” links (index → nums[index]):

* `0 → 1`
* `1 → 3`
* `3 → 2`
* `2 → 4`
* `4 → 2`

You can see the cycle: `2 → 4 → 2 → 4 → ...`
Cycle entry is `2` (duplicate).

---

### Phase 1: slow/fast meet inside the cycle

Start:

* `slow = nums[0] = 1`
* `fast = nums[0] = 1`

Now iterate (I’ll show (slow, fast)):

1.

* slow = nums[1] = 3
* fast = nums[nums[1]] = nums[3] = 2
  → (3, 2)

2.

* slow = nums[3] = 2
* fast = nums[nums[2]] = nums[4] = 2
  → (2, 2) ✅ meet

They met at node `2` (sometimes it’s `4` depending on array; any meeting point inside the cycle is fine).

---

### Phase 2: reset slow, move both 1 step to entry

Reset:

* `slow = nums[0] = 1`
* `fast` stays `2`

Now move both one step:

1.

* slow = nums[1] = 3
* fast = nums[2] = 4
  → (3, 4)

2.

* slow = nums[3] = 2
* fast = nums[4] = 2
  → (2, 2) ✅ meet at entry

Return `2`.

---

## 5) Why Phase 2 works (short but complete reasoning)

Let:

* `μ` = distance from start to cycle entry
* `λ` = cycle length

When slow and fast meet in Phase 1, fast has traveled twice as many steps as slow. The difference in steps is a multiple of the cycle length `λ`, meaning the meeting point is some offset inside the cycle.

When you reset one pointer to the start and move both one step at a time:

* one pointer is `μ` steps away from the entry (from the start)
* the other pointer is also effectively `μ` steps away from the entry (from the meeting point, wrapping around the cycle)

So they meet exactly at the entry.
*/
// class Solution {
//     public int findDuplicate(int[] nums) {
//         // Phase 1: find intersection point in cycle
//         int slow = nums[0];
//         int fast = nums[0];

//         do {
//             slow = nums[slow];         // 1 step
//             fast = nums[nums[fast]];   // 2 steps
//         } while (slow != fast);

//         // Phase 2: find entrance to the cycle
//         slow = nums[0];
//         while (slow != fast) {
//             slow = nums[slow];
//             fast = nums[fast];
//         }

//         return slow; // or fast
//     }
// }







// Method 3: Bit Manipulation method
/*
## Core idea

You know:

* `nums` has length `n+1`
* each `nums[i]` is in `[1..n]`
* exactly one value is duplicated (may appear more than twice)

If there were **no duplicate**, then the multiset of numbers in `nums` would match the multiset `{1,2,3,...,n}` exactly.

But with a duplicate, `nums` contains:

* **one extra copy** of the duplicate value `d`
* and is missing some other value (call it `m`) because length is still `n+1`

Now focus on each bit position `b` (0th bit, 1st bit, …):

* Let `countNums[b]` = how many numbers in `nums` have bit `b` set
* Let `countRange[b]` = how many numbers in `1..n` have bit `b` set

If the duplicate `d` has bit `b = 1`, then `countNums[b]` tends to be **larger** than `countRange[b]` (because we added an extra `d` with that bit).
If `d` has bit `b = 0`, `countNums[b]` tends to be **not larger**.

In fact, for this problem’s constraints, the rule works:

> If `countNums[b] > countRange[b]`, then the duplicate number has bit `b` set.

So you can reconstruct the duplicate number bit-by-bit.

### Complexity

* Time: `O(n * log n)` (count bits across ~log n bit positions)
* Extra space: `O(1)`

---

## Thorough example walkthrough

Example: `nums = [1, 3, 4, 2, 2]`

Here:

* `n = 4`
* range is `1..4`
* expected duplicate is `2`

Binary forms:

* `1 = 001`
* `2 = 010`
* `3 = 011`
* `4 = 100`

We examine each bit position needed for `n=4` → bits 0,1,2 (1,2,4).

---

### Bit 0 (mask = 1, the 1’s place)

Count ones in range `1..4`:

* 1 (001) → 1 ✅
* 2 (010) → 0
* 3 (011) → 1 ✅
* 4 (100) → 0
  `countRange = 2`

Count ones in nums `[1,3,4,2,2]`:

* 1 (001) → 1 ✅
* 3 (011) → 1 ✅
* 4 (100) → 0
* 2 (010) → 0
* 2 (010) → 0
  `countNums = 2`

Compare:

* `countNums (2) > countRange (2)`? ❌ no
  So duplicate bit0 = 0.

---

### Bit 1 (mask = 2, the 2’s place)

Count ones in range `1..4`:

* 1 (001) → 0
* 2 (010) → 1 ✅
* 3 (011) → 1 ✅
* 4 (100) → 0
  `countRange = 2`

Count ones in nums:

* 1 (001) → 0
* 3 (011) → 1 ✅
* 4 (100) → 0
* 2 (010) → 1 ✅
* 2 (010) → 1 ✅
  `countNums = 3`

Compare:

* `3 > 2` ✅ yes
  So duplicate bit1 = 1.

---

### Bit 2 (mask = 4, the 4’s place)

Count ones in range `1..4`:

* only 4 (100) has this bit → `countRange = 1`

Count ones in nums:

* only 4 (100) has this bit → `countNums = 1`

Compare:

* `1 > 1`? ❌ no
  So duplicate bit2 = 0.

---

### Reconstruct duplicate

Bits found:

* bit2 = 0
* bit1 = 1
* bit0 = 0

So duplicate = `010` (binary) = **2** ✅

---

## Why “>`” is the right comparison (intuition)

For each bit `b`, the range `1..n` gives the “expected” number of 1s at that bit if every value appears once.
Your array has one extra value (the duplicate). If that duplicate’s bit is 1, it contributes an extra “1” to that bit count, making `countNums` strictly larger.
*/

// class Solution {
//     public int findDuplicate(int[] nums) {
//         int n = nums.length - 1; // values are in [1..n]
//         int dup = 0;

//         // Enough bits to cover [1..n]
//         int maxBit = 31;
//         while (maxBit > 0 && ((n >> maxBit) & 1) == 0) {
//             maxBit--;
//         }

//         for (int b = 0; b <= maxBit; b++) {
//             int mask = 1 << b;

//             int countNums = 0;
//             for (int x : nums) {
//                 if ((x & mask) != 0) countNums++;
//             }

//             int countRange = 0;
//             for (int x = 1; x <= n; x++) {
//                 if ((x & mask) != 0) countRange++;
//             }

//             if (countNums > countRange) {
//                 dup |= mask;
//             }
//         }

//         return dup;
//     }
// }
