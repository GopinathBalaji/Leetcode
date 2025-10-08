// Using slope rule
/*
Main idea: There exists a peak in `[l, r]`.” When `l == r`, that index is a peak.

## Why this works (slope rule + invariant)

* LC 162 guarantees `nums[i] != nums[i+1]` and treats out-of-bounds as `-∞`.
* At any `mid`, compare `nums[mid]` and `nums[mid+1]`:

  * If `nums[mid] < nums[mid+1]` you’re on a **rising** slope, so there must be a peak to the **right** → move `l = mid + 1`.
  * Otherwise you’re **not rising** (i.e., falling), so a peak lies to the **left or at mid** → move `r = mid`.
* Each step strictly shrinks `[l, r]` and maintains the invariant “there exists a peak in `[l, r]`.” When `l == r`, that index is a peak.

**Complexity:** `O(log n)` time, `O(1)` space.

---

## Walkthroughs

### Example 1: `nums = [1, 2, 3, 1]`

* `l=0, r=3`
* `mid=1` → compare `2` vs `3` → rising → `l = 2`
* `l=2, r=3`
* `mid=2` → compare `3` vs `1` → not rising → `r = 2`
* `l=2, r=2` → stop. Return `2` (value `3`), which is a peak.

### Example 2: `nums = [1, 2, 1, 3, 5, 6, 4]`

There are peaks at index `1` (value `2`) and index `5` (value `6`); any is valid.

* `l=0, r=6`
* `mid=3` → compare `3` vs `5` → rising → `l = 4`
* `l=4, r=6`
* `mid=5` → compare `6` vs `4` → not rising → `r = 5`
* `l=4, r=5`
* `mid=4` → compare `5` vs `6` → rising → `l = 5`
* `l=5, r=5` → stop. Return `5` (value `6`), a valid peak.

### Edge cases

* **Single element** `[x]`: `l=0, r=0` → return `0` (only element is a peak).
* **Two elements**:

  * `[1,2]`: first step sees rising → `l=1` → returns `1` (peak at `2`).
  * `[2,1]`: first step sees not rising → `r=0` → returns `0` (peak at `2`).

---

## Common pitfalls (that this avoids)

* Getting stuck with `l = mid` on rising slopes (no progress when `r = l+1`).
* Overthinking borders (no need to fake `-∞`; the update rules already respect it).
* Requiring an exact “peak check”; the invariant ensures the final index is a peak.
*/ 
class Solution {
    public int findPeakElement(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            // Compare mid with its right neighbor
            if (nums[mid] < nums[mid + 1]) {
                // Rising slope ⇒ a peak exists in (mid, r]
                l = mid + 1;
            } else {
                // Not rising (falling) ⇒ a peak exists in [l, mid]
                r = mid;
            }
        }
        return l; // l == r, guaranteed to be a peak
    }
}
