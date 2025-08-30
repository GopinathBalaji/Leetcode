// Idea: Since array is circular, maxSubArray can be equals (Total - Min Subarray Sum)
/*
# How the **min subarray sum** part works (the logic)

Think of Kadane as: “best subarray **ending at i**.” For the **max** version, at each `i` you choose:

* extend the previous run: `currMax + nums[i]`, or
* start fresh at `nums[i]`,
  whichever is larger.

For the **min** version, you do the exact same thing but with **min**:

* `currMin = min(nums[i], currMin + nums[i])`
* `bestMin = min(bestMin, currMin)`

Interpretation:

* `currMin` is the **smallest sum** among all subarrays that **end at i**.
  If the previous `currMin` is **negative**, extending it makes things even smaller (good for a min).
  If the previous `currMin` is **positive**, it *hurts* the sum, so you **restart** at `nums[i]`.
* `bestMin` tracks the **smallest over all i**.

Why we need `bestMin`: in the circular case, a wrapping maximum subarray equals **total sum – (minimum middle block)**. So we find that minimum block via the min-Kadane. The one caveat is when **all numbers are negative**: then `bestMax` is the largest (least negative) single element and “wrapping” would incorrectly pick the empty array; we guard with `if (bestMax < 0) return bestMax;`.


Overall Algorithm:

# High-level idea

Two scenarios:

1. **Non-wrap** max = standard Kadane (best contiguous subarray in a linear array).
2. **Wrap** max = `totalSum - minSubarraySum` (remove the worst middle block; remaining suffix+prefix wraps).

Answer = `max(nonWrapMax, wrapMax)` with one crucial edge case.

---

# Algorithm (step-by-step)

1. **Initialize**

   * `total = 0`
   * For Kadane (max): `currMax = -∞`, `bestMax = -∞`
   * For Kadane (min): `currMin = +∞`, `bestMin = +∞`

2. **Single left-to-right pass** over `nums`:

   * Update running totals:

     * `total += x`
   * Update **max-subarray (Kadane)**:

     * `currMax = max(x, currMax + x)`
     * `bestMax = max(bestMax, currMax)`
   * Update **min-subarray (Kadane)**:

     * `currMin = min(x, currMin + x)`
     * `bestMin = min(bestMin, currMin)`

3. **Edge case (all numbers negative):**

   * If `bestMax < 0` (i.e., every element ≤ 0), return `bestMax`.
     (Because `wrapMax = total - bestMin` would be 0 from removing the entire array, which isn’t a valid non-empty subarray.)

4. **Compute wrap candidate:**

   * `wrapMax = total - bestMin`

5. **Return answer:**

   * `ans = max(bestMax, wrapMax)`

---

# Complexity

* **Time:** O(n) (single pass)
* **Space:** O(1)

---

# Optional: also return indices

If you need start/end indices:

* Track for **max Kadane**: when you choose `x` over `currMax + x`, set `tempStart = i`. When you improve `bestMax`, record `start = tempStart`, `end = i`.
* Track for **min Kadane** similarly (to find the “worst middle block”).
* If non-wrap wins → answer is `[start..end]`.
* If wrap wins → answer is **everything except** the min block `[minStart..minEnd]`, i.e., indices wrap from `(minEnd+1 .. n-1)` then `(0 .. minStart-1)`.

---

# Sanity checks

* Single element → returns that element.
* All negative → returns the largest (least negative) value (edge case #3 catches this).
* Clear wrap case → e.g., `[5, -3, 5]`:

  * `bestMax = 7` (from `[5, -3, 5]` linear gives 7 anyway)
  * `total = 7`, `bestMin = -3` → `wrapMax = 10` → answer `10` (wrap uses `[5] + [5]` around the `-3`).
*/

class Solution {
    public int maxSubarraySumCircular(int[] nums) {
        int total = 0;

        // Kadane for max subarray (non-wrap)
        int currMax = nums[0], bestMax = nums[0];

        // Kadane for min subarray (to compute wrap candidate)
        int currMin = nums[0], bestMin = nums[0];

        total = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            total += x;

            // standard Kadane (max)
            currMax = Math.max(x, currMax + x);
            bestMax = Math.max(bestMax, currMax);

            // "min-Kadane" (mirror image)
            currMin = Math.min(x, currMin + x);
            bestMin = Math.min(bestMin, currMin);
        }

        // Edge case: all numbers negative → wrap is illegal (would choose empty)
        if (bestMax < 0) return bestMax;

        int wrapMax = total - bestMin;   // remove the worst middle block
        return Math.max(bestMax, wrapMax);
    }
}