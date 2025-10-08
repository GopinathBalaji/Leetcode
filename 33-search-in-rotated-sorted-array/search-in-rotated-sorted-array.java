// At every step first dentify sorted part, then see if which part target might lie in.
/*
# Part 1 — Correct logic for LC33 (rotated, strictly increasing, no duplicates)

**Goal:** Find index of `target` in a rotated sorted array with all distinct elements.

**Core idea:** At every step, at least one half is strictly sorted. Identify that half, then decide if the `target` lies *numerically* inside that sorted half’s range. If yes, keep it; if not, discard it and keep the other (possibly rotated) half.

**Loop invariant:** The target, if present, is always within `[l, r]`.

**Per-iteration steps:**

1. Compute `mid`.
2. If `nums[mid] == target`, return `mid` (short-circuit).
3. Decide which half is sorted (no duplicates, so the comparison is strict):

   * **Right half sorted** if `nums[mid] < nums[r]`.
   * **Left half sorted** if `nums[mid] > nums[r]`.
4. If the **right half is sorted** (`mid..r`):

   * Keep right half **iff** `nums[mid] < target ≤ nums[r]`
     Otherwise, discard it (move `r` to `mid`).
5. If the **left half is sorted** (`l..mid`):

   * Keep left half **iff** `nums[l] ≤ target < nums[mid]`
     Otherwise, discard it (move `l` to `mid + 1`).

**Why these bounds?**

* In a sorted half, values are in normal ascending order, so we can use standard ≤ / < comparisons to check membership.
* Using `nums[r]` as the comparator when the right half is sorted, and `nums[l]` when the left half is sorted, is crucial; comparing only to `nums[mid]` isn’t enough to know if `target` sits *inside that half’s numeric interval*.

**Termination:**
You’re using `while (l < r)` with updates `r = mid` or `l = mid + 1`. This *always* shrinks the interval. When it collapses (`l == r`), check `nums[l] == target`.

**Edge behavior:**

* With `l + 1 == r`, `mid == l`. If you go right, you do `l = mid + 1` (now `l == r`). If you go left, you do `r = mid` (now `r == l`). No infinite loops.
* Arrays satisfy LC33 constraints: `n ≥ 1`, no duplicates, rotated once (possibly 0 times).

**Note on duplicates:**
If duplicates exist, the “which half is sorted?” test can be ambiguous (e.g., `nums[mid] == nums[r]`). Then you need extra tie-breaking (e.g., shrink both ends). LC33 doesn’t require that.

---

# Part 2 — “Lower_bound-style” vs other binary-search styles

Binary search has a few **styles**—patterns with specific **interval conventions**, **update rules**, and **return semantics**. Mixing them accidentally is a common source of off-by-one bugs.

## 1) Lower-bound style (half-open or closed variant)

**What it solves:** Find the **first index where a predicate becomes true**, or the **first index `i` with `a[i] ≥ x`** (“lower_bound”).
**Key characteristics:**

* You **never** return inside the loop.
* You **always** shrink toward the boundary using:

  * **Right-keeping update:** `r = mid` (keep `mid` when condition is “could still be the first true”).
  * **Left-moving update:** `l = mid + 1` (discard `mid` when it’s definitely too small / false).
* Loop guard typically `while (l < r)`, and you return `l` at the end.

**Intervals used:**

* Half-open `[l, r)` is the canonical version (with `r = mid` / `l = mid + 1`).
* Closed `[l, r]` can also be used, but then the updates must still mimic “keep mid on the right” / “jump over mid on the left.”

**Why it works:** The predicate is **monotone** (false…false, then true…true). The “keep mid” vs “skip mid” rules ensure convergence to the **first true** (or the lower bound).

**Common uses:**

* LC 35 Search Insert Position (lower_bound)
* LC 34 First/Last Position (first = lower_bound(t), last = upper_bound(t)−1)
* First index satisfying a condition (“firstTrue”).

**What I meant by your mix:**
Your loop used `while (l < r)` with `r = mid` / `l = mid + 1` (a lower-bound pattern), **but** the decision logic in rotated arrays is **not globally monotone** unless you first gate it by “which half is sorted” **and** “is target in that half’s numeric range.” Without those guards, you’re trying to use lower-bound mechanics on a non-monotone predicate, which can prune the wrong side. Your current version **does** include the right guards, so it’s consistent.

---

## 2) Exact-match style (classic)

**What it solves:** Find any index where `a[mid] == target`.
**Key characteristics:**

* Interval is typically **closed** `[l, r]`.
* Loop guard `while (l <= r)`.
* Updates: `if a[mid] < target → l = mid + 1; else r = mid − 1`.
* You **return immediately** when `a[mid] == target`.
* If not found, return sentinel (e.g., −1).

**Use cases:**

* “Plain” binary search for equality in sorted arrays.
* Also used for LC33 (rotated) by adding the sorted-half logic, but you must preserve progress (`l = mid + 1` / `r = mid − 1`) and check equality inside.

**Pros/cons:**
Simple for exact match, but not ideal for “first/last occurrence” without adjustments.

---

## 3) Binary search on answer (predicate/parametric)

**What it solves:** Search **over the space of answers** (speeds, capacities, days) for the minimal `x` such that `ok(x)` is true.
**Key characteristics:**

* Exactly the **lower-bound** style, but the “array” is conceptual; you test `ok(mid)` by simulating.
* Requires a **monotone** predicate (`false…false, true…true`).

**Uses:**

* LC 875 Koko Eating Bananas (min speed), LC 1011 Ship Packages (min capacity), LC 410 Split Array (min largest sum), etc.

---

## 4) Upper-bound style (first `>` target)

**What it solves:** First index `i` with `a[i] > x`.
**Mechanics:** Same as lower-bound but with a strict comparison in the keep/skip rule. Then `count ≤ x = upper_bound(x)`; `last < x = lower_bound(x) − 1`, etc.

---

# How to pick the style quickly

* **Need first/last occurrence or insertion point?**
  → **Lower/upper bound** style (`while (l < r)`, `r = mid`, `l = mid + 1`, return `l`).
* **Need exact equality in a plain sorted array?**
  → **Exact-match** (`while (l <= r)`, return on `==`).
* **Rotated/noisy structure where monotonicity isn’t global?**
  → Use an exact-match loop **plus** structural reasoning (e.g., identify sorted half, then numeric membership).
  You can still use lower-bound mechanics *if and only if* your branch logic restores an effective monotone decision (your LC33 code does this by gating on the sorted half and the bounds).
* **Minimize a parameter where an `ok(x)` check is monotone?**
  → **Binary search on answer** (lower-bound pattern).

---

## Small, concrete LC33 walk-through (why your guards matter)

`nums = [4,5,6,7,0,1,2]`, `target = 0`

* `l=0, r=6`, `mid=3`, `nums[mid]=7`, `nums[r]=2` → `nums[mid] > nums[r]` ⇒ **left half sorted**
  Check bounds for left half: `nums[l]=4 ≤ target(0) < 7`? No → **discard left**, go right (`l = mid + 1 = 4`).
* `l=4, r=6`, `mid=5`, `nums[mid]=1`, `nums[r]=2` → `nums[mid] < nums[r]` ⇒ **right half sorted**
  Check `nums[mid] < target ≤ nums[r]` → `1 < 0 ≤ 2`? No → keep left (`r = mid = 5`).
* `l=4, r=5`, `mid=4`, `nums[mid]=0` → equality → return 4.

Every decision preserved the half that **must** contain the target because you combined:

* “which half is sorted?” and
* “is target within that half’s numeric range?”

That’s exactly how you safely use the lower-bound-style shrinking (`r = mid` / `l = mid + 1`) in a rotated setup.

---

## TL;DR

* Your LC33 logic is correct for **no-duplicate** arrays because you (1) detect which half is sorted and (2) only keep that half if `target` lies within its numeric bounds.
* “Lower_bound-style” means the **shrink-to-boundary** pattern (`while (l < r)` with `r = mid` / `l = mid + 1`, return `l`) used when the **kept side remains a valid candidate boundary** (monotone predicate).
* Use lower/upper-bound for first/last/insertion problems; use exact-match style for pure equality; use parametric (lower-bound) for binary search on answer; and for rotated arrays, combine structural reasoning with either style, making sure your updates preserve a correct invariant.
*/

class Solution {
    public int search(int[] nums, int target) {
        int l = 0;
        int r = nums.length - 1;

        while(l < r){
            int mid = l + (r - l) / 2;

            if(nums[mid] == target){
                return mid;
            }else if(nums[mid] < nums[r]){
                if(nums[mid] < target && target <= nums[r]){
                    l = mid + 1;
                }else{
                    r = mid;
                }
            }else if(nums[mid] > nums[r]){
                if(nums[l] <= target && target < nums[mid]){
                    r = mid;
                }else{
                    l = mid + 1;
                }
            }

        }


        return nums[l] == target ? l : -1;
    }
}