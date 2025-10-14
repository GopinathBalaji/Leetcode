// Method 1: Two heaps (One Maxheap and One Minheap), O(log n) add, O(1) median
/*
# Why this works

## Invariants

1. **Order invariant:** every element in the lower half ≤ every element in the upper half
   We enforce this by swapping the *tops* if we ever see `maxHeap.peek() > minHeap.peek()`.

2. **Size invariant:** the heaps differ in size by at most 1
   We also adopt a **bias**: when the total count is odd, `maxHeap` holds the extra element. This makes `findMedian()` trivial.

With these invariants:

* **Odd count:** the median is the top of the lower half → `maxHeap.peek()`.
* **Even count:** the median is the average of the two middles → `(maxHeap.peek() + minHeap.peek()) / 2.0`.

## Complexity

* `addNum`: each heap operation is `O(log n)` → overall `O(log n)`.
* `findMedian`: just peeks → `O(1)`.
* Space: `O(n)` to store the stream.

---

# Detailed insertion logic

We do **three small steps** on every insertion:

1. **Route:**
   Put `num` into `maxHeap` if it belongs to the lower half (i.e., `num <= maxHeap.peek()`), otherwise into `minHeap`. If `maxHeap` is empty, start there.

2. **Fix order (only if needed):**
   If `maxHeap.peek() > minHeap.peek()`, the halves are “crossed”. Swap their tops (move one from each heap across) to restore the order invariant.

3. **Rebalance sizes:**
   If size difference exceeds 1, move the top from the larger heap to the smaller.
   Additionally, if `minHeap` ends up larger by 1, move its top to `maxHeap` so the **bias** (extra element in `maxHeap`) holds.

This pattern is robust against any input order (increasing, decreasing, duplicates, negatives, etc.).

---

# Thorough example walkthrough

Let’s stream numbers and track both heaps after each `addNum`.
We’ll use: `[-1, 5, 3, 8, 2, 2]`, and compute the median after each insertion.

Notation:
`maxHeap` (lower half, shown as a sorted list descending by top)
`minHeap` (upper half, shown ascending by top)

### 1) add -1

* Route: `maxHeap` empty → push to `maxHeap`: `{ -1 }`
* Order fix: minHeap empty → nothing to fix
* Rebalance: sizes are fine
* Median (odd): `top(maxHeap) = -1`

Heaps:

* `maxHeap`: [-1]
* `minHeap`: []
  Median: **-1.0**

---

### 2) add 5

* Route: `5 > maxHeap.peek() (-1)` → push to `minHeap`: `{ 5 }`
* Order fix: `max.peek() (-1) <= min.peek() (5)` → ok
* Rebalance: sizes equal → ok
* Median (even): `(-1 + 5)/2 = 2.0`

Heaps:

* `maxHeap`: [-1]
* `minHeap`: [5]
  Median: **2.0**

---

### 3) add 3

* Route: `3 > -1` → push to `minHeap`: `{ 3, 5 }` (minHeap top is 3)
* Order fix: `max.peek() (-1) <= min.peek() (3)` → ok
* Rebalance: `minHeap` has 2, `maxHeap` has 1 → move minHeap.top (3) to maxHeap

  * `maxHeap`: [-1, 3] → as a max-heap, top becomes 3
  * `minHeap`: [5]
* Median (odd): `top(maxHeap) = 3`

Heaps:

* `maxHeap`: [3, -1]
* `minHeap`: [5]
  Median: **3.0**

---

### 4) add 8

* Route: `8 > max.peek() (3)` → push to `minHeap`: `{ 5, 8 }` (top 5)
* Order fix: `3 <= 5` → ok
* Rebalance: sizes 2 vs 2 → ok
* Median (even): `(3 + 5)/2 = 4.0`

Heaps:

* `maxHeap`: [3, -1]
* `minHeap`: [5, 8]
  Median: **4.0**

---

### 5) add 2

* Route: `2 <= max.peek() (3)` → push to `maxHeap`: `{ 3, -1, 2 }` (top becomes 3)
* Order fix: compare `3` and `5` → ok
* Rebalance: sizes 3 (max) vs 2 (min) → ok (bias satisfied)
* Median (odd): `top(maxHeap) = 3`

Heaps:

* `maxHeap`: [3, -1, 2]
* `minHeap`: [5, 8]
  Median: **3.0**

---

### 6) add 2 (duplicate)

* Route: `2 <= 3` → push to `maxHeap`: `{ 3, 2, 2, -1 }` (top 3)
* Order fix: `3 > 5`? no → ok
* Rebalance: maxHeap now 4 vs minHeap 2 → too big by 2

  * Move maxHeap.top (3) to minHeap → minHeap becomes [3, 8, 5] (top 3), maxHeap [2, 2, -1]
  * Sizes now 3 vs 3 → balanced
* Median (even): `(top(max)=2 + top(min)=3)/2 = 2.5`

Heaps:

* `maxHeap`: [2, 2, -1]
* `minHeap`: [3, 8, 5]
  Median: **2.5**

All along, the invariants hold: lower half ≤ upper half, and sizes differ by at most 1 (with maxHeap allowed the extra).

---

# Pitfalls this avoids

* **Never use `poll()` in `findMedian()`** (don’t destroy state). Use `peek()`.
* **Don’t compare tops without emptiness checks**—the order-fix step checks both non-empty.
* **Keep a consistent size bias** so your median read is trivial and correct.
* **Route → order fix → rebalance** is a stable sequence that avoids ping-ponging elements.

---

If you want a version that biases the **minHeap** instead (or a variant that supports deletion with lazy removal), I can share those too, but the above is the canonical pattern that passes all tests cleanly.
*/

class MedianFinder {

    // maxHeap: stores the LOWER half (largest at the top)
    private final PriorityQueue<Integer> maxHeap;
    // minHeap: stores the UPPER half (smallest at the top)
    private final PriorityQueue<Integer> minHeap;

    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        // 1) Route to a heap (prefer putting into maxHeap first)
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }

        // 2) Fix order if needed: every element in maxHeap must be <= every element in minHeap
        if (!maxHeap.isEmpty() && !minHeap.isEmpty() && maxHeap.peek() > minHeap.peek()) {
            int a = maxHeap.poll();
            int b = minHeap.poll();
            maxHeap.offer(b);
            minHeap.offer(a);
        }

        // 3) Rebalance sizes so |size difference| <= 1 and
        //    (convention) allow maxHeap to hold the extra element when odd count
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        int total = maxHeap.size() + minHeap.size();
        if (total == 0) return 0.0; // just a guard; the judge won’t call on empty

        if (maxHeap.size() == minHeap.size()) {
            // even count: average the two middles (non-destructive; use peek, not poll)
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        } else {
            // odd count: we bias maxHeap to hold the extra
            return maxHeap.peek();
        }
    }
}


/**
 * Your MedianFinder object will be instantiated and called as such:
 * MedianFinder obj = new MedianFinder();
 * obj.addNum(num);
 * double param_2 = obj.findMedian();
 */






//  Method 2 (Follow up 1): Bucket Scan
/*
When all numbers lie in a tiny fixed range [0, 100], you can drop heaps entirely and use a frequency table (bucket counting). That gives O(1) space (101 buckets), O(1) add time, and O(101) = O(1) median time. That’s already faster/simpler than two heaps for this constraint.

Below are two versions:
Simple bucket scan (very clear; usually best)
Pointer-aided bucket scan (micro-optimized to walk less on average)

Great follow-up! When all numbers lie in a tiny fixed range `[0, 100]`, you can drop heaps entirely and use a **frequency table (bucket counting)**. That gives **O(1) space** (101 buckets), **O(1) add** time, and **O(101) = O(1)** median time. That’s already faster/simpler than two heaps for this constraint.

Below are two versions:

1. **Simple bucket scan** (very clear; usually best)
2. **Pointer-aided bucket scan** (micro-optimized to walk less on average)

---

# 1) Simple bucket scan (recommended)

## Idea

* Keep an array `freq[0..100]` where `freq[x]` is how many times `x` has appeared.
* Track total count `n`.
* Median:

  * If `n` is **odd**, median is the element at **rank** `mid = n/2` (0-based) in the sorted multiset.
  * If `n` is **even**, median is the **average** of elements at ranks `mid1 = n/2 - 1` and `mid2 = n/2`.

Because there are only 101 buckets, a linear pass to accumulate counts and stop at the needed rank(s) is constant time.

### Complexity

* `addNum`: **O(1)**
* `findMedian`: scans at most 101 buckets ⇒ **O(1)** (tiny constant)
* Space: `101 * sizeof(int)` ⇒ **O(1)**

---

## Walkthrough (thorough)

Stream: `2, 3, 3, 5, 7, 7, 7`

We’ll track `freq[]`, `n`, and find median each time.

1. **add 2**

   * `freq[2] = 1`, `n = 1` (odd)
   * target rank `0` (n/2)
   * cumulating:

     * v=0..1 → cumulative 0
     * v=2 → cumulative = 1 > 0 → **median = 2.0**

2. **add 3**

   * `freq[3] = 1`, `n = 2` (even)
   * ranks `left=0`, `right=1`
   * cumulate:

     * v=0..1 → 0
     * v=2 → cumulative=1 → covers rank 0 ⇒ `leftVal = 2`
     * v=3 → cumulative=2 → covers rank 1 ⇒ `rightVal = 3`
   * **median = (2 + 3) / 2 = 2.5**

3. **add 3**

   * `freq[3] = 2`, `n = 3` (odd)
   * target rank `1`
   * cumulate: v=2 → 1 (≤1), v=3 → 3 (>1) ⇒ **median = 3.0**

4. **add 5**

   * `freq[5] = 1`, `n = 4` (even)
   * ranks `1` and `2`
   * cumulate: v=2 → 1 → covers rank 1: `leftVal=2`
     v=3 → 3 → covers rank 2: `rightVal=3`
   * **median = (2 + 3) / 2 = 2.5** (note: because we have 2,3,3,5)

5. **add 7**

   * `freq[7] = 1`, `n = 5` (odd), target rank `2`
   * cumulate: v=2 → 1; v=3 → 3 (>2) ⇒ **median = 3.0**

6. **add 7**

   * `freq[7] = 2`, `n = 6` (even), ranks `2` and `3`
   * cumulate: v=2 → 1
     v=3 → 3 → covers rank 2: `leftVal=3`
     v=4..6 → still 3
     v=7 → 5 → covers rank 3: `rightVal=7`
   * **median = (3 + 7) / 2 = 5.0**

7. **add 7**

   * `freq[7] = 3`, `n = 7` (odd), target rank `3`
   * cumulate: v=2 → 1; v=3 → 3; v=4..6 → 3; v=7 → 6 (>3)
   * **median = 7.0**

Everything is constant-time per query and simple to reason about.

---

# 2) Pointer-aided bucket scan (micro-optimization, optional)

If you call `findMedian()` extremely frequently, you can avoid scanning from `0` every time by maintaining **two running pointers** to the current median ranks and adjusting only as needed when `addNum()` changes counts.

### Sketch (idea only)

* Track `n`, and if `n` is odd keep a single pointer `(midVal, cumBefore)` such that the median is at `midVal`.
* If `n` is even, keep two pointers `(loVal, hiVal)` for the two middle ranks.
* On each `addNum(x)`, increment `freq[x]` and update:

  * If `x < loVal`, median ranks shift right by 1 (or one of the pointers stays, one moves), etc.
  * You adjust the pointer(s) by moving at most a few steps along the 0..100 range until cumulative counts match the new target ranks.

This can make `findMedian()` O(1) without any loop, and `addNum()` still O(1) expected (small moves). But the logic is more intricate and the benefit over scanning ≤101 buckets is usually negligible.

---

## Why this beats heaps for [0,100]

* Heaps: `addNum` is `O(log n)`; `findMedian` is `O(1)`.
* Buckets: `addNum` is **O(1)**; `findMedian` is **O(101) = O(1)**.
* With such a small domain, **bucket counting is simpler, faster, and uses less memory**.

---

## Edge cases

* Empty stream: return a default (problem won’t call `findMedian` before `addNum`, but we add a guard).
* Many duplicates: buckets handle them by incrementing a single slot.
* Values at bounds (0 or 100): just indexes in the same array.

---

### TL;DR

Use a **frequency array of size 101** and scan to the appropriate median rank(s). It’s constant-time, constant-space, and very straightforward—perfect for the range-bounded follow-up.
*/
// import java.util.*;

// class MedianFinder {
//     private final int[] freq = new int[101]; // counts of 0..100
//     private int n = 0;                       // total numbers seen

//     public MedianFinder() {}

//     public void addNum(int num) {
//         // assume 0 <= num <= 100 per follow-up constraint
//         freq[num]++;
//         n++;
//     }

//     public double findMedian() {
//         if (n == 0) return 0.0; // guard for completeness

//         if ((n & 1) == 1) {
//             // Odd: pick the (n/2)-th element (0-based)
//             int targetRank = n / 2;
//             int cumulative = 0;
//             for (int v = 0; v <= 100; v++) {
//                 cumulative += freq[v];
//                 if (cumulative > targetRank) {
//                     return v; // found the element at rank targetRank
//                 }
//             }
//         } else {
//             // Even: average the two middles: ranks n/2 - 1 and n/2
//             int leftRank = n / 2 - 1;
//             int rightRank = n / 2;
//             int cumulative = 0;
//             Integer leftVal = null, rightVal = null;

//             for (int v = 0; v <= 100; v++) {
//                 cumulative += freq[v];

//                 if (leftVal == null && cumulative > leftRank) {
//                     leftVal = v;
//                 }
//                 if (cumulative > rightRank) {
//                     rightVal = v;
//                     break; // found both
//                 }
//             }
//             return (leftVal + rightVal) / 2.0;
//         }

//         // Should never reach with valid state
//         return 0.0;
//     }
// }






// Method 3 (Follow up 2): hybrid counting (buckets scan + sparse maps)
/*
# Idea (hybrid counting + sparse maps)

Keep three disjoint partitions:

1. **Low outliers**: numbers `< 0`
   Store in an **ordered map** `low` from value → frequency.
2. **In-range bulk**: numbers `0..100`
   Store in a **fixed 101-bucket** frequency array `freq[0..100]`.
3. **High outliers**: numbers `> 100`
   Store in an **ordered map** `high` from value → frequency.

Maintain the **total count** `n`, plus cumulative counts:

* `cntLow`  = total numbers `< 0`
* `cntMid`  = total numbers in `[0,100]`
* `cntHigh` = total numbers `> 100` (so `n = cntLow + cntMid + cntHigh`)

To answer the median (the **k-th order statistic**), compare `k` with these block sizes:

* If `k < cntLow`, the k-th element is inside `low` → scan its few (distinct) keys.
* Else if `k < cntLow + cntMid`, it’s inside the 101 buckets → scan `freq`.
* Else it’s inside `high` with adjusted rank → scan its few keys.

Because **99%** of numbers are in `[0,100]`, both `low` and `high` are **tiny** in practice, so these scans are very fast.

**Complexities**

* `addNum`:

  * O(1) when in `[0,100]`
  * O(log U) for outliers (TreeMap insert), where `U` = distinct outlier values (very small)
* `findMedian`:

  * O(101 + U) worst-case ≈ **O(1)** in practice (101 is a fixed tiny constant)

No heaps needed here—and you get exact medians.


# Why this is a good optimization for the 99% case

* The heavy bulk (`[0,100]`) benefits from **O(1) add** and **O(101)** (constant) scan.
* Outliers are only ~1% → even scanning their distinct values is tiny.
  (`TreeMap` also keeps them sorted for free, and `merge` handles frequencies neatly.)
* No complex heap rebalancing or tricky edge cases—just **deterministic counting**.

---

# Thorough walkthrough

Let’s insert a mixed stream and compute the median after each step.

Stream: `[-5, 2, 3, 150, 7, 7, -1, 101, 4, 3]`

We’ll track:

* `low`: TreeMap for `<0`
* `freq[0..100]`
* `high`: TreeMap for `>100`
* (`cntLow`, `cntMid`, `cntHigh`, `n`)

We’ll also compute the median after each insertion.

---

### 1) add **-5**

* Goes to `low`: `{ -5:1 }`, `cntLow=1`, `n=1`
* Median: `n=1` (odd), `k=0`

  * `k < cntLow (1)` → in `low`: the 0-th is **-5**
* **median = -5.0**

---

### 2) add **2**

* In `[0,100]`: `freq[2]++`, `cntMid=1`, `n=2`
* Multiset: `[-5, 2]`
* `n=2` (even): `k1=0`, `k2=1`

  * `k1=0 < cntLow=1` → **-5**
  * `k2=1`: subtract `cntLow` → `k2' = 0` in mid. Scan buckets: first nonzero is `2`.
* **median = (-5 + 2)/2 = -1.5**

---

### 3) add **3**

* In `[0,100]`: `freq[3]++`, `cntMid=2`, `n=3`
* Multiset: `[-5, 2, 3]`
* `n=3` (odd): `k=1`

  * `k=1 ≥ cntLow=1`, move to mid with `k' = 0`
  * In buckets, first element is `2` → **median = 2.0**

---

### 4) add **150**

* Goes to `high`: `{ 150:1 }`, `cntHigh=1`, `n=4`
* Multiset: `[-5, 2, 3, 150]`
* `n=4` (even): `k1=1`, `k2=2`

  * `k1=1` → `≥ cntLow=1` so mid with `k1' = 0` → `2`
  * `k2=2` → `≥ cntLow=1` so mid with `k2' = 1` → next is `3`
* **median = (2 + 3)/2 = 2.5**

---

### 5) add **7**

* In `[0,100]`: `freq[7]++`, `cntMid=3`, `n=5`
* Multiset: `[-5, 2, 3, 7, 150]`
* `n=5` (odd): `k=2`

  * `k=2` → mid with `k' = 1`
  * Order in mid buckets: `2, 3, 7,...`
  * `k'=1` is `3`
* **median = 3.0**

---

### 6) add **7**

* `freq[7]++`, `cntMid=4`, `n=6`
* Multiset: `[-5, 2, 3, 7, 7, 150]`
* `n=6` (even): `k1=2`, `k2=3`

  * `k1=2` → mid `k1' = 1` → value `3`
  * `k2=3` → mid `k2' = 2` → value `7`
* **median = (3 + 7)/2 = 5.0**

---

### 7) add **-1**

* `low.merge(-1,1,…)` → `low = { -5:1, -1:1 }`, `cntLow=2`, `n=7`
* Multiset: `[-5, -1, 2, 3, 7, 7, 150]`
* `n=7` (odd): `k=3`

  * `k=3 ≥ cntLow=2` → mid with `k' = 1`
  * Mid values: `2, 3, 7, 7` → `k'=1` is `3`
* **median = 3.0**

---

### 8) add **101**

* `high.merge(101,1,…)` → `high = { 101:1, 150:1 }`, `cntHigh=2`, `n=8`
* Multiset: `[-5, -1, 2, 3, 7, 7, 101, 150]`
* `n=8` (even): `k1=3`, `k2=4`

  * `k1=3` → mid `k1' = 1` → `3`
  * `k2=4` → mid `k2' = 2` → `7`
* **median = (3 + 7)/2 = 5.0**

---

### 9) add **4**

* `freq[4]++`, `cntMid=5`, `n=9`
* Multiset: `[-5, -1, 2, 3, 4, 7, 7, 101, 150]`
* `n=9` (odd): `k=4`

  * `k=4` → mid `k' = 2`
  * Mid order: `2, 3, 4, 7, 7,…` → `k'=2` is `4`
* **median = 4.0**

---

### 10) add **3**

* `freq[3]++`, `cntMid=6`, `n=10`
* Multiset: `[-5, -1, 2, 3, 3, 4, 7, 7, 101, 150]`
* `n=10` (even): `k1=4`, `k2=5`

  * `k1=4` → mid `k1' = 2` → value `4`? Wait carefully:
    Mid multiset in order: `2, 3, 3, 4, 7, 7`
    Indices (0-based inside mid): `0:2, 1:3, 2:3, 3:4, 4:7, 5:7`
    But remember there are also 2 low values before mid (`-5, -1`).

    * For `k1=4`: subtract `cntLow=2` → `k1' = 2` → **3**
    * For `k2=5`: subtract `cntLow=2` → `k2' = 3` → **4**
* **median = (3 + 4)/2 = 3.5**

Everything matches what you’d get by explicitly sorting the sequence, but we never store all numbers or sort globally.

---

## Notes & tweaks

* If you want `O(1)` `findMedian()` (no scans at all), you can maintain **running pointers** to the current median ranks, adjusting them in `addNum`. That’s more bookkeeping; with only **101** mid buckets and tiny outlier sets, the above is already extremely fast and simple.
* If outliers could explode (e.g., adversarial input), you can switch the two TreeMaps to a **Fenwick/segment tree** on a **compressed** outlier coordinate set to make k-th queries `O(log U)`. In typical “99% in range” streams, TreeMaps are perfectly fine (small `U`).

---

### TL;DR

* Use **101 buckets** for the 99% in `[0,100]`.
* Use two small **TreeMaps** to keep counts of rare outliers `<0` and `>100`.
* To find the median, compute its **rank** and pick it from one of the three partitions.
* This gives **O(1)** adds for the bulk, **O(log U)** for rare outliers, and **O(1)**–ish median queries in practice.
*/

// import java.util.*;

// class MedianFinder {

//     // Buckets for 0..100
//     private final int[] freq = new int[101];
//     private int cntMid = 0;

//     // Outliers
//     private final TreeMap<Integer, Integer> low  = new TreeMap<>(); // values < 0
//     private final TreeMap<Integer, Integer> high = new TreeMap<>(); // values > 100
//     private int cntLow  = 0;
//     private int cntHigh = 0;

//     private int n = 0; // total numbers seen

//     public MedianFinder() {}

//     public void addNum(int num) {
//         if (0 <= num && num <= 100) {
//             freq[num]++; cntMid++; n++;
//             return;
//         }
//         if (num < 0) {
//             low.merge(num, 1, Integer::sum);
//             cntLow++; n++;
//             return;
//         }
//         // num > 100
//         high.merge(num, 1, Integer::sum);
//         cntHigh++; n++;
//     }

//     public double findMedian() {
//         if (n == 0) return 0.0; // guard

//         // 0-based median ranks
//         if ((n & 1) == 1) {
//             int k = n / 2;
//             int v = getKthValue(k);
//             return v;
//         } else {
//             int k1 = n / 2 - 1;
//             int k2 = n / 2;
//             int v1 = getKthValue(k1);
//             int v2 = getKthValue(k2);
//             return (v1 + v2) / 2.0;
//         }
//     }

//     // Return the 0-based k-th smallest value across low + [0..100] + high
//     private int getKthValue(int k) {
//         // 1) In low outliers?
//         if (k < cntLow) {
//             return kthInMap(low, k);
//         }

//         // 2) In 0..100 buckets?
//         k -= cntLow;
//         if (k < cntMid) {
//             int cum = 0;
//             for (int v = 0; v <= 100; v++) {
//                 cum += freq[v];
//                 if (cum > k) return v;
//             }
//             // Should not reach here if counts are consistent
//             throw new IllegalStateException("Inconsistent mid counts");
//         }

//         // 3) In high outliers
//         k -= cntMid;
//         return kthInMap(high, k);
//     }

//     // Return the k-th (0-based) value within a TreeMap<value, freq> in ascending order
//     private int kthInMap(TreeMap<Integer, Integer> map, int k) {
//         int cum = 0;
//         for (Map.Entry<Integer, Integer> e : map.entrySet()) {
//             int value = e.getKey();
//             int count = e.getValue();
//             if (cum + count > k) return value;
//             cum += count;
//         }
//         throw new IllegalStateException("k out of range for the map");
//     }
// }
