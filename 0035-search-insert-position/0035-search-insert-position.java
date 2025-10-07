// Method 1: Binary Search using the "lower bound" pattern (using half-open interval)
/*
# Lower bound (aka `first ≥ target`)

**Goal:** return the first index `i` with `a[i] ≥ x`. If none, returns `n`.
**Invariant:** we keep a half-open interval `[l, r)` such that the answer lies inside.
**Loop rule:** when `a[mid] ≥ x`, the answer is in `[l, mid]` → set `r = mid`; else it’s in `(mid, r)` → set `l = mid+1`.
**Return:** `l`.

```java
// lower_bound: first index with a[i] >= x; returns n if all < x
int lowerBound(int[] a, int x) {
    int l = 0, r = a.length;            // [l, r)
    while (l < r) {
        int m = l + (r - l) / 2;
        if (a[m] >= x) r = m;           // keep m
        else l = m + 1;
    }
    return l;
}
```

**Use it for:** LC 35 Search Insert Position (answer is exactly lower_bound). Also for “first index ≥ k”, “count of elements < k” (that’s `lower_bound(k)`), and as a building block for ranges.

---

# Other common bound variants

## Upper bound (aka `first > target`)

**Goal:** first index `i` with `a[i] > x`. If none, returns `n`.
Just flip the comparison:

```java
int upperBound(int[] a, int x) {
    int l = 0, r = a.length;            // [l, r)
    while (l < r) {
        int m = l + (r - l) / 2;
        if (a[m] > x) r = m;            // keep m
        else l = m + 1;
    }
    return l;
}
```

**Use it for:**

* “Count of elements ≤ x” = `upper_bound(x)`.
* LC 34 Find First and Last Position: `first = lower_bound(t)`, `last = upper_bound(t) - 1` (check bounds & equality).

## Last ≤ target / Last < target (right-bound)

Sometimes you want the *rightmost* index satisfying a condition. You can derive from lower/upper bounds:

* **Last ≤ x:** `upper_bound(x) - 1`
* **Last < x:** `lower_bound(x) - 1`

(Validate the index and equality before using.)

---

# Predicate variants (binary search on a monotonic boolean)

Many problems are “first index where a predicate turns true”. Think of a function `ok(i)` that’s **false … false, true … true** (monotone). Then you need **first true**:

```java
// first true in [0, n) for a monotone predicate ok(i)
int firstTrue(int n, java.util.function.IntPredicate ok) {
    int l = 0, r = n;                   // [l, r)
    while (l < r) {
        int m = l + (r - l) / 2;
        if (ok.test(m)) r = m;          // keep m
        else l = m + 1;
    }
    return l;                           // = n if none true
}
```

**Use it for:**

* LC 278 First Bad Version → `ok(mid) = isBadVersion(mid)`
* LC 410 Split Array Largest Sum, LC 875 Koko Eating Bananas, LC 1011 Ship Packages → **binary search on answer**: search minimal `x` such that `ok(x)` is true.

There’s also **last true** (false…true…true, then false…): you can do `firstFalse - 1`, or mirror the comparisons.

---

# How to pick the right variant (fast checklist)

1. **Are you returning an index among sorted values?**

   * “Where would `target` go?” → **lower_bound**
   * “First index strictly greater than `target`?” → **upper_bound**
   * “Rightmost ≤/ < something?” → derive from upper/lower as above.

2. **Are you searching for a boundary in a YES/NO world?**

   * If you can define a monotone predicate `ok(x)` (false then true), you want **firstTrue**.
   * If it’s true then false, flip logic or transform to firstTrue on complement.

3. **Range queries for a value `t` in a sorted array:**

   * `L = lower_bound(t)`, `R = upper_bound(t) - 1`; check `L <= R` and `a[L] == t` before using.

4. **Duplicates matter?**

   * Need the *first* position of `t` → `lower_bound(t)`
   * Need the *last* position of `t` → `upper_bound(t) - 1`

5. **Binary search on answer (parametric search):**

   * The “array” isn’t given; you invent the search space (e.g., speeds, capacities, days).
   * Prove `ok(x)` is monotone; then use **firstTrue** on `[lo, hi]`.

---

# Closed vs half-open intervals (pick one style and stick to it)

* **Half-open `[l, r)`** pairs naturally with bounds; no `l <= r` edge cases; return `l`.
* **Closed `[l, r]`** is fine too, but be meticulous with `l <= r` loop and mid updates.

Example closed form for “exact match or insert position”:

```java
int searchInsert(int[] a, int x) {
    int l = 0, r = a.length - 1;
    while (l <= r) {
        int m = l + (r - l) / 2;
        if (a[m] == x) return m;
        if (a[m] < x) l = m + 1;
        else r = m - 1;
    }
    return l; // insertion point
}
```

---

# Common pitfalls (and quick fixes)

* **Using `while (l < r)` with `r = mid - 1`** while expecting a bound → you’re mixing patterns. For bounds with `[l, r)`, use `r = mid` / `l = mid + 1`.
* **Returning `l + 1`** for LC35 → off-by-one; correct is `l`.
* **Forgetting duplicates** when you need first/last index → use lower/upper pairs.
* **Non-monotone predicate** in binary search on answer → fix the predicate or the search space.

---

# Fast mapping to popular problems

* **LC 35 Search Insert Position** → `lower_bound`
* **LC 34 First/Last Position of Element** → `lower_bound(t)`, `upper_bound(t)-1`
* **LC 278 First Bad Version** → `firstTrue`
* **LC 744 Next Greatest Letter** → `upper_bound(ch)` with wraparound
* **LC 875/1011/410** → binary search on answer (`firstTrue` over capacity/speed/limit)
*/

class Solution{
    public int searchInsert(int[] nums, int target) {
        int l = 0, r = nums.length;              // note: r = n (half-open [l, r))
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] >= target) r = mid;    // keep mid when nums[mid] == target
            else l = mid + 1;
        }
        return l;                                // first index with nums[i] >= target
    }
}


// Method 1.5: Another "lower bound" pattern (using closed intervals)
// public int searchInsert(int[] nums, int target) {
//     int l = 0, r = nums.length - 1;
//     while (l <= r) {
//         int mid = l + (r - l) / 2;
//         if (nums[mid] == target) return mid;
//         if (nums[mid] < target) l = mid + 1;
//         else r = mid - 1;
//     }
//     return l; // l ends up as insertion point
// }

