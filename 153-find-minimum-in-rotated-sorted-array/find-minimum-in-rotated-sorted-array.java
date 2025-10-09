// Finding interval based on mid and right comparison
/*
Only conditions needed:
* If `nums[mid] > nums[r]`, the minimum is guaranteed to be in **(mid, r]** → move **`l = mid + 1`**.
* If `nums[mid] < nums[r]`, the minimum is guaranteed to be in **[l, mid]** → move **`r = mid`**.


# What my original attempt got wrong:
Your code adds extra checks on `nums[mid + 1]` and sometimes moves the wrong pointer, which can **throw away the half that actually contains the minimum**.

## The specific problems

1. **Wrong move when `nums[mid] > nums[r]`.**
   In this case, the rotation (and thus the minimum) is on the **right** side. You must move `l = mid + 1`.
   Your code does:

   * If `nums[mid + 1] < nums[mid]` → `l = mid + 1` (OK only when the pivot is *exactly* at `mid+1`).
   * **Else** → `r = mid` (**wrong**): this discards the right side where the minimum actually lies.

   ### Counterexample

   `nums = [2,3,4,5,6,7,0,1]`

   * `l=0, r=7, mid=3 (nums[mid]=5), nums[r]=1` → `nums[mid] > nums[r]` (left side is sorted)
   * `nums[mid+1]=6 > 5` so your code sets `r = mid = 3`
   * Now you’re searching `[0..3]`, but the minimum is in the right half `[4..7]`.
     This leads to returning `2` instead of `0`.

   The correct rule here is unconditional: **if `nums[mid] > nums[r]` → `l = mid + 1`**.

2. **Unnecessary/nebulous `nums[mid + 1]` checks.**
   LC153 (no duplicates) doesn’t need neighbor peeks. The array is strictly increasing on each monotone piece, and the pivot creates a single “drop.” The **right endpoint comparison** (`nums[mid]` vs `nums[r]`) alone is sufficient to locate the side containing the minimum. Bringing `nums[mid+1]` into the decision can easily break the monotonic “must be left / must be right” guarantee.

3. **Invariant mixing with a lower-bound style loop.**
   You’re using `while (l < r)` and updates of the form `r = mid` / `l = mid + 1`, which is the classic **half-open / lower-bound** shrinking pattern. That style works **only if** each branch strictly preserves “the minimum is still inside the kept interval.”
   Because of point (1), one branch doesn’t preserve that invariant → wrong answers.

---

## Intuition refresher (why the simple rule works)

* If the subarray `[mid..r]` is **sorted** (`nums[mid] < nums[r]`), then its minimum is `nums[mid]` and the global minimum cannot be to the right of `mid`. So we keep the **left** part including `mid`: **`r = mid`**.
* If `[mid..r]` is **not** sorted (`nums[mid] > nums[r]`), the “drop” (rotation) lies inside `(mid, r]`, and so does the minimum. So we discard the left, move **`l = mid + 1`**.
* Duplicates are **not** present in LC153, so equality case doesn’t arise.

This guarantees strict shrinkage and convergence; when `l == r`, that index is the minimum.

---

## TL;DR

* The extra `nums[mid+1]` checks are what break you.
* The **only** correct moves you need (for LC153 with no duplicates) are:

  * `nums[mid] > nums[r]` → **`l = mid + 1`**
  * `nums[mid] < nums[r]` → **`r = mid`**
* With `while (l < r)`, that invariant is airtight; your version sometimes sets `r = mid` when the min is definitely on the right, causing wrong results on inputs like `[2,3,4,5,6,7,0,1]`.
*/
class Solution {
    public int findMin(int[] nums) {
        int l = 0, r = nums.length - 1;
        // Invariant: the minimum lies in [l, r]
        while (l < r) {
            int mid = l + (r - l) / 2;

            // If right half [mid..r] is strictly increasing,
            // then the minimum cannot be to the right of mid.
            // It is either nums[mid] or somewhere on the left.
            if (nums[mid] < nums[r]) {
                r = mid;            // keep mid
            } else {
                // Otherwise [mid..r] is not sorted -> the "drop" (pivot) is in (mid, r]
                // so the minimum is guaranteed in the right half strictly after mid.
                l = mid + 1;
            }
        }
        return nums[l]; // l == r: index of the minimum
    }
}
