// Method 1: My inefficient approach using a minHeap
/*
#################### WHY IS IT INEFFICIENT ###################
Your solution is **logically correct** (it will return *some* `k` points with smallest distance to the origin), but there are a few practical issues—mostly **unnecessary work** and **fragile distance math**.

---

## 1) Main issue: time complexity is higher than needed

You push **all `n` points** into a **min-heap**, then pop `k`.

* Build heap: `O(n log n)` (because you insert `n` items)
* Pop k: `O(k log n)`
* Total: **`O(n log n)`**

The standard optimized heap approach is **`O(n log k)`**:

* Use a **max-heap of size k**
* Keep only the k closest seen so far (evict the farthest when size exceeds k)

For large `n` and small `k`, this is a big improvement (and often what LeetCode expects).

---

## 2) You don’t need `sqrt()` (and you don’t need doubles)

You’re computing:

```java
sqrt(x^2 + y^2)
```

But for comparisons, `sqrt()` is monotonic:

> `sqrt(A) < sqrt(B)`  ⇔  `A < B`  (for nonnegative A, B)

So you can compare **squared distances**:

```java
x*x + y*y
```

This avoids floating point, avoids `sqrt`, and is faster.

---

## 3) `Math.pow()` + casts are unnecessary (and can be risky in general)

You do:

```java
(int) Math.pow(a[1], 2) + (int) Math.pow(a[0], 2)
```

Problems:

* `Math.pow` returns `double` (slow + unnecessary)
* Casting back to `int` is pointless here
* With larger constraints, `int` squaring can overflow; using `long` is safer.

For LeetCode 973 constraints (|x|,|y| ≤ 10^4), `x*x + y*y` fits in `int`, but writing it as `long` is a good habit and more robust.

---

## 4) Comparator recomputes distance *a lot*

A `PriorityQueue` comparator is called many times during heap operations; your comparator recomputes distances every compare. It’s not wrong, just extra overhead.

#############################################################
*/
class Solution {
    public int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> {
            double distA = (double) Math.sqrt((int) Math.pow(a[1], 2) + (int) Math.pow(a[0], 2));
            double distB = (double) Math.sqrt((int) Math.pow(b[1], 2) + (int) Math.pow(b[0], 2));

            return Double.compare(distA, distB);
        });

        for(int[] point: points){
            minHeap.add(point);
        }

        int[][] ans = new int[k][2];

        for(int i=0; i<k; i++){
            ans[i] = minHeap.poll();
        }

        return ans;
    }
}






// Method 1.5: Optimized minHeap approach
/*
*/
// class Solution {
//     public int[][] kClosest(int[][] points, int k) {
//         PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> {
//             long da = 1L * a[0] * a[0] + 1L * a[1] * a[1];
//             long db = 1L * b[0] * b[0] + 1L * b[1] * b[1];
//             return Long.compare(da, db);
//         });

//         for (int[] p : points) minHeap.offer(p);

//         int[][] ans = new int[k][2];
//         for (int i = 0; i < k; i++) ans[i] = minHeap.poll();
//         return ans;
//     }
// }







// Method 2: Most Optimal Heap / PriorityQueue approach (uses MaxHeap)
/*
Heap is reduced to only of size: k 
Time complexity: (O(n log k))
*/
// class Solution {
//     public int[][] kClosest(int[][] points, int k) {
//         PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> {
//             long da = 1L * a[0] * a[0] + 1L * a[1] * a[1];
//             long db = 1L * b[0] * b[0] + 1L * b[1] * b[1];
//             return Long.compare(db, da); // reversed => max-heap by distance
//         });

//         for (int[] p : points) {
//             maxHeap.offer(p);
//             if (maxHeap.size() > k) maxHeap.poll(); // remove farthest
//         }

//         int[][] ans = new int[k][2];
//         for (int i = 0; i < k; i++) ans[i] = maxHeap.poll();
//         return ans;
//     }
// }








// Method 3: QuickSelect / Divide and Conquer Approach
/*
The key idea is the same as “kth smallest”: we **partition by distance** until the element at index `k-1` is in its correct spot. Then the first `k` points are the `k` closest (in any order).

---

## Why Quickselect works here

Define distance to origin:

[
d^2 = x^2 + y^2
]

We want the **k smallest distances**.

Quickselect does this without fully sorting:

1. Pick a pivot (we’ll use `points[high]`).
2. Partition array so that:

   * all points with distance **<= pivot distance** are on the **left**
   * all points with distance **> pivot distance** are on the **right**
3. Pivot ends up at some index `p` (its “final” sorted position by distance).
4. Compare `p` with the target index:

   * target index = `k - 1`
   * if `p == k-1`: done
   * if `p < k-1`: recurse right
   * if `p > k-1`: recurse left

After this finishes, the first `k` elements are guaranteed to be the k closest (not sorted among themselves).


## Thorough example walkthrough (with real partition steps)

Let:

```text
points = [[1,3], [-2,2], [2,-2], [5,8], [0,1]]
k = 2
targetIdx = k-1 = 1
```

Compute squared distances:

* [1,3]   → 1²+3² = 10
* [-2,2]  → 4+4 = 8
* [2,-2]  → 4+4 = 8
* [5,8]   → 25+64 = 89
* [0,1]   → 0+1 = 1

We want the **2 smallest distances** → distances `{1, 8}`.

---

### Call 1: `quickselect(lo=0, high=4, targetIdx=1)`

Pivot = `arr[high] = [0,1]`, pivotDist = 1.

Partition with `i = lo - 1 = -1`, `j` from 0 to 3:

* j=0: [1,3], dist=10 <= 1? **No**
* j=1: [-2,2], dist=8 <= 1? **No**
* j=2: [2,-2], dist=8 <= 1? **No**
* j=3: [5,8], dist=89 <= 1? **No**

No swaps happened; `i` stays -1.

Finally swap pivot into place: swap `i+1 = 0` with `high = 4`.

Array becomes:

```text
[[0,1], [-2,2], [2,-2], [5,8], [1,3]]
```

PivotIndex = 0.

Compare to targetIdx=1:

* pivotIndex (0) < target (1) → **go right**

---

### Call 2: `quickselect(lo=1, high=4, targetIdx=1)`

Pivot = `arr[4] = [1,3]`, pivotDist = 10.

Partition subarray indices 1..4:

* start `i = lo - 1 = 0`
* j runs 1..3

j=1: [-2,2], dist=8 <=10? **Yes**

* i=1, swap(1,1) (no change)

j=2: [2,-2], dist=8 <=10? **Yes**

* i=2, swap(2,2) (no change)

j=3: [5,8], dist=89 <=10? **No**

Now swap pivot into place: swap `i+1 = 3` with `high = 4`.

Array becomes:

```text
[[0,1], [-2,2], [2,-2], [1,3], [5,8]]
```

PivotIndex = 3.

Compare to targetIdx=1:

* pivotIndex (3) > target (1) → **go left**

---

### Call 3: `quickselect(lo=1, high=2, targetIdx=1)`

Pivot = `arr[2] = [2,-2]`, pivotDist = 8.

Partition subarray indices 1..2:

* i = lo - 1 = 0
* j runs only 1

j=1: [-2,2], dist=8 <=8? **Yes**

* i=1, swap(1,1) (no change)

Swap pivot into place: swap `i+1 = 2` with `high = 2` (no change)

Array stays:

```text
[[0,1], [-2,2], [2,-2], [1,3], [5,8]]
```

PivotIndex = 2.

Compare to targetIdx=1:

* pivotIndex (2) > target (1) → **go left**

---

### Call 4: `quickselect(lo=1, high=1, targetIdx=1)`

Base case `lo >= high` → stop.

---

## What do we know now?

The array is:

```text
[[0,1], [-2,2], [2,-2], [1,3], [5,8]]
```

The first `k=2` points are:

```text
[[0,1], [-2,2]]
```

Distances are `1` and `8`.
That is correct: the 2 closest points are `[0,1]` and **either** `[-2,2]` or `[2,-2]` (tie at distance 8). Quickselect can return either tie.

**Important:** The first k are correct, but they are **not guaranteed to be sorted**.

---

## Complexity

* Average time: **O(n)** (because each partition discards ~half on average)
* Worst case: **O(n²)** (bad pivots repeatedly, since pivot is always `arr[high]`)
* Space: **O(1)** extra (in-place), plus recursion stack **O(log n)** average / **O(n)** worst

If you want to reduce worst-case risk while keeping the same structure, the usual trick is to **randomly choose a pivot index and swap it into `high`**, then do the exact same `arr[high]` partition.
*/

// class Solution {
//     public int[][] kClosest(int[][] points, int k) {
//         int n = points.length;
//         if (k >= n) return points;

//         // We want the element that would be at index (k-1) in sorted-by-distance order
//         quickselect(points, 0, n - 1, k - 1);

//         // First k points are the k closest (any order)
//         return Arrays.copyOfRange(points, 0, k);
//     }

//     // Quickselect to place the element with rank "targetIdx" in correct position
//     private void quickselect(int[][] arr, int lo, int high, int targetIdx) {
//         if (lo >= high) return;

//         int pivotIndex = partition(arr, lo, high);

//         if (pivotIndex == targetIdx) {
//             return;
//         } else if (pivotIndex < targetIdx) {
//             quickselect(arr, pivotIndex + 1, high, targetIdx);
//         } else {
//             quickselect(arr, lo, pivotIndex - 1, targetIdx);
//         }
//     }

//     // Lomuto partition using arr[high] as pivot
//     private int partition(int[][] arr, int lo, int high) {
//         long pivotDist = dist2(arr[high]);   // pivot = arr[high]
//         int i = lo - 1;                      // boundary of "<= pivotDist" region

//         for (int j = lo; j < high; j++) {
//             if (dist2(arr[j]) <= pivotDist) {
//                 i++;
//                 swap(arr, i, j);
//             }
//         }

//         // Place pivot just after the <= region
//         swap(arr, i + 1, high);
//         return i + 1;
//     }

//     // squared distance (use long to be safe)
//     private long dist2(int[] p) {
//         long x = p[0];
//         long y = p[1];
//         return x * x + y * y;
//     }

//     private void swap(int[][] arr, int a, int b) {
//         int[] tmp = arr[a];
//         arr[a] = arr[b];
//         arr[b] = tmp;
//     }
// }