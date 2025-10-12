// Method 1: Max Heap O(n)

class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());

        for(int num: nums){
            pq.add(num);
        }

        while(k != 1){
            pq.poll();
            k--;
        }

        return pq.poll();
    }
}



// Method 2: Quick Select
/*
## What Quickselect does (in one sentence)

It’s like Quicksort, **but instead of sorting both sides**, it **partitions once** and then **recurse/iterate only into the side that contains the desired order statistic**—so average time is linear.

---

## The moving parts

### 1) Partition (Lomuto)

Given a subarray `a[lo..hi]` and a chosen pivot:

* Move pivot to the end.
* Sweep `j=lo..hi-1`, keeping an index `i` so that:

  * elements `a[lo..i-1]` are `<= pivot`,
  * elements `a[i..j-1]` are `> pivot`.
* Swap pivot into position `i`.
* **Return `i`** = final pivot index. Now:

  * `a[lo..i-1] ≤ a[i]`
  * `a[i+1..hi] ≥ a[i]`

### 2) Selection logic

Let `targetIdx` be the 0-based order statistic we need (e.g., for k-th smallest, `targetIdx = k-1`).

* If `p == targetIdx`: done, pivot is exactly the element we want.
* If `p < targetIdx`: the desired element lies **to the right** → set `lo = p+1`.
* If `p > targetIdx`: desired element lies **to the left** → set `hi = p-1`.

### 3) Randomized pivot

Choosing a random pivot avoids adversarial cases like already sorted arrays that can force quadratic time.

---

## Complexity

* **Average time:** (O(n)) — because each partition discards ~half the elements in expectation.
* **Worst time:** (O(n^2)) — if every pivot is awful (randomization makes this extremely unlikely).
* **Space:** (O(1)) extra (in-place). The iterative version avoids recursion depth.

---

# Thorough example walkthrough (visuals)

**Goal:** Find the **5-th smallest** (i.e., index 4 in 0-based) in

```
a = [9, 1, 5, 3, 7, 2, 8, 6, 4]
k = 5  → targetIdx = 4
lo=0, hi=8
```

We’ll show one plausible run (random pivot picks may vary). Suppose the random pivot chosen in the first call is the last element `4` (because we swapped a random index to `hi`).

### Partition 1 (lo=0, hi=8, pivot=4)

Start:

```
[9, 1, 5, 3, 7, 2, 8, 6, 4]
 lo                    hi
 i=0
 j sweeps 0..7
```

Scan `j`:

* j=0: a[0]=9 > 4 → do nothing
* j=1: a[1]=1 ≤ 4 → swap(a[1], a[0]) → `[1, 9, 5, 3, 7, 2, 8, 6, 4]`; i=1
* j=2: 5 > 4 → skip
* j=3: 3 ≤ 4 → swap(a[3], a[1]) → `[1, 3, 5, 9, 7, 2, 8, 6, 4]`; i=2
* j=4: 7 > 4 → skip
* j=5: 2 ≤ 4 → swap(a[5], a[2]) → `[1, 3, 2, 9, 7, 5, 8, 6, 4]`; i=3
* j=6: 8 > 4 → skip
* j=7: 6 > 4 → skip

Finally swap pivot into place at `i=3`:

```
swap(a[3], a[8])  ⇒  [1, 3, 2, 4, 7, 5, 8, 6, 9]
                         ^
                        p=3
```

Now:

* Left of p (indices 0..2): `[1, 3, 2]` (all ≤ 4)
* Right of p (indices 4..8): `[7, 5, 8, 6, 9]` (all ≥ 4)

Our `targetIdx=4`.

* Since `p=3 < 4`, the 5-th smallest is in the **right** side.
* Update **lo = p+1 = 4**, **hi = 8**. The order statistic we want is **still index 4 in the entire array**, but relative to the subarray `[4..8]`, it’s the (4−4)=0-th element **by value order** among that subarray after full sort (we don’t recompute; the quickselect loop handles this logically).

Array (current state):

```
[1, 3, 2, 4, 7, 5, 8, 6, 9]
               ^lo        ^hi
```

### Partition 2 (lo=4, hi=8)

Suppose random pivot ends up as `6` (by swapping some random index to `hi`).

Subarray to partition: `[7, 5, 8, 6, 9]`

Lomuto within that window:

* After partitioning around 6, one possible result:

```
[1, 3, 2, 4, 5, 6, 8, 7, 9]
                 ^ p=5 (global index)
```

Reasonable partition trace inside `[4..8]`:

* Move pivot 6 to end, sweep:

  * 7 > 6 skip
  * 5 ≤ 6 swap with start → puts 5 at index 4; i points to 5’s next spot
  * 8 > 6 skip
  * (pivot) 6 placed at index 5
  * (9) right of pivot
* Final pivot index `p=5`.

Compare `p` to `targetIdx=4`:

* `p=5 > 4` → desired item lies **left** of `p`.
* Update **hi = p - 1 = 4**.

Now the active window is a single index `[lo..hi] = [4..4]`:

```
[1, 3, 2, 4, 5, 6, 8, 7, 9]
               ^lo=hi=4
```

Loop exits next iteration returning `a[4] = 5`.
**Answer:** the **5-th smallest** is **5** (indeed the median of 1..9).

> **Key insight:** We never sorted the whole array—only partitioned twice, then zoomed into the side that could contain the answer.

---

## Variants & Tips

* **k-th largest:** Reduce to k-th smallest by index transform (as shown).
* **Hoare partition:** Slightly different partition scheme (two pointers from ends) with often fewer swaps. If you use Hoare, be careful: it returns a split index where elements on left are ≤ pivot and right are ≥ pivot, but the pivot position may not be at that index—update the loop logic accordingly.
* **Duplicates:** Using `<= pivot` in Lomuto keeps duplicates with the pivot on the left. Both sides still shrink; correctness holds.
* **Deterministic median-of-medians (BFPRT):** Guarantees (O(n)) worst-case by picking a good pivot deterministically. More code, slower constants; rarely needed in interviews unless explicitly asked.

---

## Common pitfalls (and how this code avoids them)

* **Not advancing bounds correctly:** Always compare `p` (pivot’s final index) with `targetIdx` and update one side only.
* **Recursing both sides:** That’s Quicksort; Quickselect *only* descends into one side.
* **Forgetting 1-indexed vs 0-indexed k:** Convert once (`k-1`) and stay consistent.
* **Non-random pivot on adversarial data:** Increases risk of (O(n^2)). Randomization is a simple fix.

---

## When to use Quickselect

* You need just the **k-th order statistic** (or a small fixed set of them), not a full sort.
* You want better than `O(n log n)` time on average and are fine with probabilistic guarantees.
* You can mutate the array (in-place).

If you want, I can also show:

* A **recursive** variant (same logic, fewer loops).
* A **stable selection** approach (not in-place, `O(n)` extra space).
* A **median-of-medians** pivot chooser for worst-case linear time.

*/

// public class QuickSelect {

//     private static final Random RNG = new Random();

//     // Returns the k-th smallest element (1-indexed). Throws if k is out of range.
//     public static int kthSmallest(int[] a, int k) {
//         if (k < 1 || k > a.length) throw new IllegalArgumentException("k out of range");
//         return quickSelect(a, 0, a.length - 1, k - 1); // convert to 0-indexed order statistic
//     }

//     // Convenience: k-th largest (1-indexed)
//     public static int kthLargest(int[] a, int k) {
//         if (k < 1 || k > a.length) throw new IllegalArgumentException("k out of range");
//         int idx = a.length - k; // k-th largest == (n-k)-th smallest (0-indexed)
//         return quickSelect(a, 0, a.length - 1, idx);
//     }

//     // Iterative Quickselect using Lomuto partition and randomized pivot
//     private static int quickSelect(int[] a, int left, int right, int targetIdx) {
//         int lo = left, hi = right;
//         while (lo <= hi) {
//             int p = randomizedPartition(a, lo, hi); // partition, returns final pivot index
//             if (p == targetIdx) return a[p];
//             if (p < targetIdx) {
//                 lo = p + 1;          // search right side
//             } else {
//                 hi = p - 1;          // search left side
//             }
//         }
//         // Should never happen when inputs are valid
//         throw new IllegalStateException("Quickselect failed");
//     }

//     // Lomuto partition with random pivot: partitions a[lo..hi] around pivot, returns pivot's final index
//     private static int randomizedPartition(int[] a, int lo, int hi) {
//         int pivotIdx = lo + RNG.nextInt(hi - lo + 1);
//         swap(a, pivotIdx, hi);                  // move pivot to end
//         int pivot = a[hi];

//         int i = lo;                             // place for next <= pivot
//         for (int j = lo; j < hi; j++) {
//             if (a[j] <= pivot) {                // <= yields a stable-ish behavior for duplicates
//                 swap(a, i, j);
//                 i++;
//             }
//         }
//         swap(a, i, hi);                         // put pivot in its final spot
//         return i;
//     }

//     private static void swap(int[] a, int i, int j) {
//         if (i != j) { int tmp = a[i]; a[i] = a[j]; a[j] = tmp; }
//     }

//     // Example usage
//     public static void main(String[] args) {
//         int[] arr = {9, 1, 5, 3, 7, 2, 8, 6, 4};
//         System.out.println(kthSmallest(arr.clone(), 1)); // 1
//         System.out.println(kthSmallest(arr.clone(), 5)); // 5 (median here)
//         System.out.println(kthLargest(arr.clone(), 1));  // 9
//         System.out.println(kthLargest(arr.clone(), 3));  // 7
//     }
// }
