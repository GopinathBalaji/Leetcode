// Method 1: Reversing different parts of the array (In place, O(1) space)
/*
Why it works (right rotation by k):
Reversing all puts the last k elements in front but backwards; 
then reversing the first k fixes that block; reversing the rest fixes the tail.
Example [1,2,3,4,5,6,7], k=3 → reverse all → [7,6,5,4,3,2,1] → reverse first 3 → [5,6,7,4,3,2,1] → reverse last 4 → [5,6,7,1,2,3,4].

If k >= n, end = k - 1 can go out of bounds. You must do k %= n (and handle k == 0 early).
*/
class Solution {
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        if (n == 0) return;

        k %= n;           // normalize
        if (k == 0) return;

        reverse(nums, 0, n - 1);   // 1) reverse whole array
        reverse(nums, 0, k - 1);   // 2) reverse first k
        reverse(nums, k, n - 1);   // 3) reverse last n-k
    }

    private void reverse(int[] a, int l, int r) {
        while (l < r) {
            int t = a[l];
            a[l] = a[r];
            a[r] = t;
            l++; r--;
        }
    }
}


// Method 2: Cyclic Replacements (In place, O(1) space)
/*
# \U0001f6b4‍♂️ Intuition (plain words)

Right-rotating by `k` means every index `i` moves to `(i + k) % n`.
If you follow that jump repeatedly, you eventually come back to where you started—forming a **cycle**.
We can move elements **cycle by cycle**:

* Pick a start index.
* Carry the value in your hand (`prev`).
* Jump to the next index `(cur + k) % n`, put the value in your hand there, and pick up what was there.
* Keep jumping until you return to the start—cycle done.
* Start a new cycle from the next unvisited index, until all elements are moved.

Think “passing a hot potato” around a loop.

**Why this stops exactly:**

* Every write places one element in its final position → `moved++`.
* There are `gcd(n, k)` cycles, but you don’t even need to compute it; the `moved < n` guard ensures we start the next cycle only if needed.

---

# \U0001f9ea Walkthrough #1 (single cycle): `[1,2,3,4,5,6,7]`, `k=3`

* `n=7`, `k=3`, `gcd(7,3)=1` → one big cycle

Start cycle at `start=0`
`cur=0`, `prev=1`

1. `next=(0+3)%7=3` → put `1` at index 3
   pickup old `nums[3]=4` → `prev=4`
   array: `[1,2,3,1,5,6,7]`, `cur=3`, `moved=1`

2. `next=(3+3)%7=6` → put `4` at index 6
   pickup 7 → `prev=7`
   array: `[1,2,3,1,5,6,4]`, `cur=6`, `moved=2`

3. `next=(6+3)%7=2` → put `7` at index 2
   pickup 3 → `prev=3`
   array: `[1,2,7,1,5,6,4]`, `cur=2`, `moved=3`

4. `next=(2+3)%7=5` → put `3` at index 5
   pickup 6 → `prev=6`
   array: `[1,2,7,1,5,3,4]`, `cur=5`, `moved=4`

5. `next=(5+3)%7=1` → put `6` at index 1
   pickup 2 → `prev=2`
   array: `[1,6,7,1,5,3,4]`, `cur=1`, `moved=5`

6. `next=(1+3)%7=4` → put `2` at index 4
   pickup 5 → `prev=5`
   array: `[1,6,7,1,2,3,4]`, `cur=4`, `moved=6`

7. `next=(4+3)%7=0` → put `5` at index 0
   pickup 1 → `prev=1`
   array: `[5,6,7,1,2,3,4]`, `cur=0`, `moved=7`

`cur == start` → cycle closed. `moved == n` → done.
**Result:** `[5,6,7,1,2,3,4]` ✅

---

# \U0001f9ea Walkthrough #2 (multiple cycles): `[1,2,3,4,5,6]`, `k=2`

* `n=6`, `k=2`, `gcd(6,2)=2` → 2 cycles, each of length 3

**Cycle 1 (start=0):**
`cur=0`, `prev=1`

* next=2: put 1 at 2, pick 3 → `[1,2,1,4,5,6]` (moved=1)
* next=4: put 3 at 4, pick 5 → `[1,2,1,4,3,6]` (moved=2)
* next=0: put 5 at 0, pick 1 → `[5,2,1,4,3,6]` (moved=3)
  back to start → cycle 1 done.

**Cycle 2 (start=1):**
`cur=1`, `prev=2`

* next=3: put 2 at 3, pick 4 → `[5,2,1,2,3,6]` (moved=4)
* next=5: put 4 at 5, pick 6 → `[5,2,1,2,3,4]` (moved=5)
* next=1: put 6 at 1, pick 2 → `[5,6,1,2,3,4]` (moved=6)
  back to start → cycle 2 done. `moved == n` → stop.

**Result:** `[5,6,1,2,3,4]` ✅ (rotate right by 2)

---

## \U0001f9f7 Common pitfalls (how to avoid bugs)

* **Forget `k %= n`:** if `k >= n`, you’ll write out of bounds or do extra work.
* **Infinite loops:** always increment `moved` and stop a cycle when `cur == start`.
* **Skipping cycles:** don’t assume one cycle; rely on `moved < n` to start more as needed.

---

## ⏱️ Complexity

* **Time:** O(n) — every element moved exactly once
* **Space:** O(1) — in place
*/

// class Solution {
//     public void rotate(int[] nums, int k) {
//         int n = nums.length;
//         if (n == 0) return;

//         // Normalize k (rotating by n does nothing; by n+1 equals 1, etc.)
//         k %= n;
//         if (k == 0) return;

//         int moved = 0; // how many elements we've placed correctly
//         // We may have multiple cycles; start new ones until all n elements are moved.
//         for (int start = 0; moved < n; start++) {
//             int cur = start;
//             int prev = nums[start]; // carry what's at 'start'

//             do {
//                 int next = (cur + k) % n; // where 'cur' should move to
//                 // place 'prev' into its correct spot and pick up displaced value
//                 int temp = nums[next];
//                 nums[next] = prev;
//                 prev = temp;

//                 cur = next;
//                 moved++;
//             } while (cur != start); // cycle closes when we return to the start
//         }
//     }
// }



// Method 3: Block-Swap (Gries–Mills) rotation
/*
We convert “right rotate by `k`” into “left rotate by `d = n − k`” and then apply block-swaps on subarrays until done.


**Complexity:** Every element is moved at most a constant number of times → **O(n)** time, **O(1)** extra space.

---

## \U0001f9e0 Why this works (plain English)

Split the (sub)array into **L** (left block of size `d`) and **R** (right block). We want `L R → R L`.

* If both blocks are the **same size**, one swap finishes it.
* If **L is smaller**, swap L with the **leftmost** L-sized chunk of R. L is now placed, and only the suffix still needs the same rotation amount.
* If **R is smaller**, swap R with the **rightmost** R-sized chunk of L. R is now placed, and only the prefix still needs rotation with a **smaller** amount.

We keep shrinking the problem until it’s all swapped into place.

---

## \U0001f9ea Thorough walkthrough (right rotate `k=3`)

Example: `nums = [1,2,3,4,5,6,7]`, `k=3`
Right-rotate by 3 == left-rotate by `d = n - k = 7 - 3 = 4`.

Start on the whole array (start=0, n=7, d=4):
`L = [1,2,3,4] (size 4)`, `R = [5,6,7] (size 3)` → **L > R**

1. **L > R**: swap the **rightmost |R|=3** of L with R
   Swap `[2,3,4]` (indices 1..3) with `[5,6,7]` (indices 4..6)
   Array → `[1,5,6,7,2,3,4]`

   Now only the **prefix** of length `L=4` still needs rotation by `d' = L − R = 4 − 3 = 1`.
   Focus on subarray `[1,5,6,7]` (indices 0..3), left-rotate by **1**.

2. On subarray `[1,5,6,7]`: `L=[1] (size 1)`, `R=[5,6,7] (size 3)` → **L < R**
   Swap L with the **leftmost |L|=1** of R → swap `1` and `5`
   Array → `[5,1,6,7,2,3,4]`

   Now only the **suffix** (after the placed `5`) of length `n-L = 3` (subarray `[1,6,7]`, indices 1..3) still needs rotation by the **same** `d = 1`.

3. On subarray `[1,6,7]`: `L=[1] (1)`, `R=[6,7] (2)` → **L < R**
   Swap L with leftmost |L| of R → swap `1` and `6`
   Array → `[5,6,1,7,2,3,4]`

   Now suffix of length 2 (subarray `[1,7]`, indices 2..3) needs rotation by `d=1`.

4. On subarray `[1,7]`: `L=[1]`, `R=[7]` → **equal sizes**
   Swap them once → subarray becomes `[7,1]`
   **Final array:** `[5,6,7,1,2,3,4]` ✅

That’s exactly “rotate right by 3.”

---

### Tips / gotchas

* Always **normalize `k`** with `k %= n`.
* Keep careful track of the subarray you’re rotating: `(start, n, d)`.
* The two swap patterns are symmetric; stick to the exact rules above to avoid off-by-one errors.
*/

// class Solution {
//     public void rotate(int[] nums, int k) {
//         int n = nums.length;
//         if (n == 0) return;

//         k %= n;                 // normalize
//         if (k == 0) return;     // nothing to do

//         int d = n - k;          // right-rotate k == left-rotate d
//         blockSwapLeft(nums, 0, n, d);
//     }

//     // Left-rotate the subarray nums[start .. start+n-1] by d (0 < d < n)
//     private void blockSwapLeft(int[] a, int start, int n, int d) {
//         if (d == 0 || d == n) return;

//         int L = d;        // |L|
//         int R = n - d;    // |R|

//         if (L == R) {
//             // L and R same size: one final swap completes XY -> YX
//             swapRange(a, start, start + L, L);
//             return;
//         }

//         if (L < R) {
//             // L smaller than R:
//             // swap L with the LEFTMOST |L| elements of R
//             // [L | R1 R2]  --swap(L, R1)-->  [R1 | L R2]
//             swapRange(a, start, start + L, L);
//             // Now rotate the suffix [L R2] (length n - L) by the SAME d (= L)
//             blockSwapLeft(a, start + L, n - L, d);
//         } else { // L > R
//             // R smaller than L:
//             // swap the RIGHTMOST |R| of L with R
//             // [L1 L2 | R] --swap(L2, R)--> [L1 R | L2]
//             swapRange(a, start + (L - R), start + L, R);
//             // Now rotate the PREFIX [L1 R] (length L) by the REDUCED d' = L - R
//             blockSwapLeft(a, start, L, L - R);
//         }
//     }

//     // Swap a[i .. i+len-1] with a[j .. j+len-1]
//     private void swapRange(int[] a, int i, int j, int len) {
//         for (int t = 0; t < len; t++) {
//             int tmp = a[i + t];
//             a[i + t] = a[j + t];
//             a[j + t] = tmp;
//         }
//     }
// }