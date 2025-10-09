// 2 binary search solution: once to find leftmost target and the other to find the rightmost target
/*
# A. Problem in one line

Given a **sorted** array `nums` and a `target`, return `[firstIndex, lastIndex]` where `nums[firstIndex] == nums[lastIndex] == target`. If `target` doesn’t occur, return `[-1, -1]`.

Why binary search? Because the array is sorted and we need boundary positions—classic “bounds” use-case.

---

# B. Two-pass “boundary” idea (logic, no half-open yet)

We do two binary searches:

1. **Left boundary (first occurrence):**
   Find the *smallest* index `i` such that `nums[i] >= target`.
   If `i` is in range and `nums[i] == target`, that’s the first index.

   * Invariant: answer for the left boundary lies in `[l, r]`.
   * Update rule:

     * If `nums[mid] >= target`, the first occurrence can be at `mid` or to its left → move `r = mid - 1` **but remember `mid` as candidate**.
     * Else `nums[mid] < target`, move `l = mid + 1`.

2. **Right boundary (last occurrence):**
   Find the *largest* index `j` such that `nums[j] <= target`.
   If `j` is in range and `nums[j] == target`, that’s the last index.

   * Invariant: answer for the right boundary lies in `[l, r]`.
   * Update rule:

     * If `nums[mid] <= target`, the last occurrence can be at `mid` or to its right → move `l = mid + 1` **but remember `mid` as candidate**.
     * Else `nums[mid] > target`, move `r = mid - 1`.

These are the classic “push toward boundary” versions using a **closed interval** (`while (l <= r)`), returning remembered candidates.

**Edge handling**

* If the left boundary search doesn’t find equality, return `[-1, -1]` early.
* The right boundary search can start from anywhere (0..n-1), but starting from `leftBoundary` is a tiny micro-opt.

**Complexity:** both passes are `O(log n)`; space `O(1)`.

---

# C. Same thing with **lower_bound / upper_bound** using **half-open** intervals

This is often cleaner and harder to get wrong. You never return inside the loop; you always converge to a boundary and then check.

## Lower bound (`first index i with nums[i] >= target`)

Half-open invariant: answer is in `[l, r)`.

* Init: `l = 0`, `r = n`.
* While `l < r`:

  * `mid = l + (r - l) / 2`
  * If `nums[mid] >= target` → **keep** `mid` and everything left: `r = mid`
  * Else `nums[mid] < target` → **discard** `mid`: `l = mid + 1`
* End: `l` is the least index with `nums[l] >= target` (or `l == n` if none).
  If `l == n` or `nums[l] != target`, then target not present.

## Upper bound (`first index i with nums[i] > target`)

* Init: `l = 0`, `r = n`.
* While `l < r`:

  * If `nums[mid] > target` → `r = mid`
  * Else → `l = mid + 1`
* End: `l` is the first index *strictly greater* than `target`.
  Therefore, the **last occurrence** is `l - 1` (must check in range).

### Put together

* `first = lower_bound(target)`
* If `first == n || nums[first] != target` → return `[-1, -1]`
* `last = upper_bound(target) - 1`
* Return `[first, last]`

This is exactly LC 34 in its canonical “bounds” form.

---

# D. Step-by-step walkthrough

Example: `nums = [5,7,7,8,8,10]`, `target = 8`, `n = 6`.

### Lower bound for 8 (first ≥ 8)

* `[l,r) = [0,6)`

  * mid=3 → nums[3]=8 ≥ 8 → `r = 3` → `[0,3)`
  * mid=1 → nums[1]=7 < 8 → `l = 2` → `[2,3)`
  * mid=2 → nums[2]=7 < 8 → `l = 3` → `[3,3)` stop
* `first = 3`; `nums[3] == 8` → ok.

### Upper bound for 8 (first > 8)

* `[l,r) = [0,6)`

  * mid=3 → nums[3]=8 ≤ 8 → `l = 4` → `[4,6)`
  * mid=5 → nums[5]=10 > 8 → `r = 5` → `[4,5)`
  * mid=4 → nums[4]=8 ≤ 8 → `l = 5` → `[5,5)` stop
* `upper = 5` → `last = upper - 1 = 4`.
* Answer `[3,4]`.

---

# E. Why the half-open lower/upper-bound style is robust

* The predicate you test (“`a[mid] >= x`?” for lower, “`a[mid] > x`?” for upper) is **monotone** in a sorted array. That’s what guarantees convergence.
* The updates are always one of `{ r = mid }` or `{ l = mid + 1 }`, so the range strictly shrinks.
* You never need special casing for “equal to target” in the loop; equality naturally drives to the correct boundary.

---

# F. Common pitfalls (and how bounds avoid them)

* **Off-by-one at the last step** → bounds style returns an index that is *by definition* a boundary; you do a final equality check to distinguish “found vs not found.”
* **Using `while (l <= r)` but mixing `r = mid`** → that’s a style mix-up.

  * Closed interval `[l, r]` pairs with `r = mid - 1 / l = mid + 1`.
  * Half-open `[l, r)` pairs with `r = mid / l = mid + 1`.
* **Returning too early** → bounds style returns only after convergence, reducing branch errors.

---

# G. Edge cases to verify

* Empty array (`n == 0`) → immediately return `[-1,-1]`.
* All elements < target → `lower_bound == n` → `[-1, -1]`.
* All elements > target → `lower_bound == 0` but `nums[0] != target` → `[-1, -1]`.
* All elements equal to target → `first = 0`, `last = n-1`.
* Single element arrays.

---

# H. Tiny mental checklist to pick a style

* Need **first/last/insertion** positions? → **lower/upper bound** (half-open) is ideal.
* Need a **yes/no exact index** (just one equality)? → classic exact-match with closed interval is fine.
* When in doubt for boundaries, prefer the **bounds style**; it’s less error-prone.
*/
class Solution {
    public int[] searchRange(int[] nums, int target) {
        int[] ans = new int[] {-1, -1};
        if(nums.length == 0){
            return ans;
        }

        int l = 0;
        int r = nums.length - 1;

        int leftmost = -1;

        while(l <= r){
            int mid = l + (r - l) / 2;

            if(nums[mid] == target){
                leftmost = mid;
                r = mid - 1;
            }else if(nums[mid] > target){
                r = mid - 1;
            }else{
                l = mid + 1;
            }
        }

        if(leftmost == -1){
            return ans;
        }else{
            ans[0] = leftmost;
        }

        int rightmost = -1;
        l = leftmost + 1;
        r = nums.length - 1;

        while(l <= r){
            int mid = l + (r - l) / 2;

            if(nums[mid] == target){
                rightmost = mid;
                l = mid + 1;
            }else if(nums[mid] > target){
                r = mid - 1;
            }else{
                l = mid + 1;
            }
        }

        if(rightmost == -1){
            ans[1] = leftmost;
        }else{
            ans[1] = rightmost;
        }

        return ans;
    }
}