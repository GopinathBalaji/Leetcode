// Method 1: Applying Binary Search on the range of possible answers and not on the input array
/*
## Problem in one sentence

You have piles of bananas. If Koko eats at speed `k` bananas/hour, she needs
[
\sum \lceil \frac{p_i}{k} \rceil
]
hours total. Find the **minimum** `k` such that total hours `<= h`.

---

## Why binary search works

The key property is **monotonicity**:

* If Koko can finish with speed `k`, then she can also finish with any speed `k' > k` (eating faster can’t take more time).
* If she **cannot** finish with speed `k`, then any `k' < k` also won’t work.

So the predicate `canFinish(k)` is **monotone** (false → true as `k` increases), which screams: **binary search the answer**.

---

## How to compute hours for a given speed `k`

For a pile size `p`:

* If `k = 3` and `p = 10`, she needs `ceil(10/3) = 4` hours.

Use integer math (no floating point):

```java
hours = (p + k - 1) / k
```

This is a standard ceil-division trick.

We also should use `long` for total hours, because sums can be large.

---

## Search range for `k`

* Minimum possible speed: `low = 1`
* Maximum needed speed: `high = max(piles)`

  * At speed `maxPile`, each pile takes at most 1 hour, so total hours is at most number of piles (and constraints guarantee a solution).

Then do **lower-bound binary search**:

* If `canFinish(mid)` is true → try smaller (`high = mid`)
* Else → need larger (`low = mid + 1`)
  Stop when `low == high`.


# Thorough example walkthrough

### Example 1

`piles = [3, 6, 7, 11]`, `h = 8`

#### Step 1: Define search space

* `low = 1`
* `high = max(piles) = 11`

We binary search the smallest `k`.

---

## Iteration details

### Iteration 1

* `low = 1`, `high = 11`
* `mid = 1 + (11 - 1)/2 = 6`

Check `canFinish(k=6)`:

Compute hours per pile:

* pile 3:  ceil(3/6)  = 1  (since (3+5)/6 = 8/6 = 1)
* pile 6:  ceil(6/6)  = 1
* pile 7:  ceil(7/6)  = 2
* pile 11: ceil(11/6) = 2

Total hours = 1 + 1 + 2 + 2 = **6** ≤ 8 → feasible

So:

* `high = mid = 6` (try smaller)

---

### Iteration 2

* `low = 1`, `high = 6`
* `mid = 1 + (6 - 1)/2 = 3`

Check `canFinish(k=3)`:

* 3:  ceil(3/3)  = 1
* 6:  ceil(6/3)  = 2
* 7:  ceil(7/3)  = 3
* 11: ceil(11/3) = 4

Total = 1 + 2 + 3 + 4 = **10** > 8 → not feasible

So:

* `low = mid + 1 = 4`

---

### Iteration 3

* `low = 4`, `high = 6`
* `mid = 4 + (6 - 4)/2 = 5`

Check `canFinish(k=5)`:

* 3:  ceil(3/5)  = 1
* 6:  ceil(6/5)  = 2
* 7:  ceil(7/5)  = 2
* 11: ceil(11/5) = 3

Total = 1 + 2 + 2 + 3 = **8** ≤ 8 → feasible

So:

* `high = 5`

---

### Iteration 4

* `low = 4`, `high = 5`
* `mid = 4 + (5 - 4)/2 = 4`

Check `canFinish(k=4)`:

* 3:  ceil(3/4)  = 1
* 6:  ceil(6/4)  = 2
* 7:  ceil(7/4)  = 2
* 11: ceil(11/4) = 3

Total = 1 + 2 + 2 + 3 = **8** ≤ 8 → feasible

So:

* `high = 4`

Now `low == high == 4`, stop.

✅ Answer = **4**

---

## Why 4 is minimal

* We found `k=4` works.
* We also tested a smaller one (`k=3`) and it failed.
  So 4 is the smallest feasible speed.

---

### Example 2 (quick)

`piles=[30,11,23,4,20], h=5`

* `k=30` → hours = 1+1+1+1+1 = 5 feasible
* Try smaller: `k=23` → hours = 2 + 1 + 1 + 1 + 1 = 6 not feasible
  So answer ends up 30 (and binary search will find it).

---

## Common mistakes to avoid

* Using `p / k` with integer division (that’s floor), you need **ceil**.
* Using floating point `Math.ceil((double)p/k)` (works but slower and unnecessary).
* Not using `long` for total hours (can overflow for big inputs).
* Using wrong binary search update rules (this is a lower-bound search).
*/
class Solution {
    public int minEatingSpeed(int[] piles, int h) {
        int low = 1;
        int high = 0;
        for (int p : piles) {
            high = Math.max(high, p);
        }

        while (low < high) {
            int mid = low + (high - low) / 2;

            if (canFinish(piles, h, mid)) {
                high = mid;      // mid works, try smaller
            } else {
                low = mid + 1;   // mid doesn't work, need bigger
            }
        }

        return low;
    }

    private boolean canFinish(int[] piles, int h, int k) {
        long hours = 0;
        for (int p : piles) {
            hours += (p + k - 1) / k; // ceil(p/k)
            if (hours > h) return false; // early exit
        }
        return hours <= h;
    }
}
