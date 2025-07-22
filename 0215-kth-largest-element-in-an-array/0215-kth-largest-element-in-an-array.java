// Using Heap (Simplest method to find Kth largest or smallest)
class Solution {
    public int findKthLargest(int[] nums, int k) {
        // One way to declare max heap in java: using Collections.reverseOrder()
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

        // Another way to declare max heap in java: Using Lamda Compactor
        // PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
        //     (a, b) -> Integer.compare(b, a)   // reverse order: bigger comes first
        // );

        // Another way: Also using Compactor
        // Comparator<Integer> cmp = Comparator.reverseOrder();
        // PriorityQueue<Integer> maxHeap = new PriorityQueue<>(cmp);

        for(int num: nums){
            maxHeap.add(num);
        }

        int ans = 0;
        while(k > 0 && !maxHeap.isEmpty()){
            ans = maxHeap.poll();
            k--;
        }


        return ans;
    }
}




// Mehod 2: Using Mergesort (Divide and Conquer)
// class Solution {
//     public int findKthLargest(int[] nums, int k) {
//         // A single temp array reused by every merge step
//         int[] buf = new int[nums.length];
//         mergeSort(nums, 0, nums.length - 1, buf);

//         // After ascending sort, the kth largest is at n-k
//         return nums[nums.length - k];
//     }

//     private void mergeSort(int[] nums, int left, int right, int[] buf){
//         // base case: 0 or 1 element
//         if(left >= right){
//             return;
//         }

//         int mid = left + (right - left) / 2;  // avoid (left+right)/2 overflow
//         mergeSort(nums, left, mid, buf);      // sort left half
//         mergeSort(nums, mid + 1, right, buf); // sort right half

//         merge(nums,left, mid, right, buf);  // merge the two halves
//     }

//     /*
//      * Merge two adjacent *sorted* ranges:
//      *   nums[left..mid] and nums[mid+1..right]
//      * into ascending order using buf as scratch
//      */
//     private void merge(int[] nums, int left, int mid, int right, int[] buf){
//         int i = left;       // pointer in left half
//         int j = mid + 1;    // pointer in right half
//         int t = 0;          // pointer in buf

//         while(i <= mid && j <= right){
//             buf[t++] = (nums[i] <= nums[j]) ? nums[i++] : nums[j++];
//         }

//         // leftovers if any
//         while(i <= mid){
//             buf[t++] = nums[i++];
//         }

//         // leftovers if any
//         while(j <= right){
//             buf[t++] = nums[j++];
//         }

//         // arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
//         System.arraycopy(buf, 0, nums, left, t);
//     }
// }




// // Method 3: Using Quickselect (Randomized Algorithm) (Best method with Average Time Complexity O(n))
/*
Below is the simplified walkthrough of the Quickselect algorithm and code where we always take the
last element as the pivot. In the actual algorithm the pivot selection is randomized
as shown in the actual code below.

```text
nums = [3, 2, 1, 5, 6, 4],   k = 2   (want the 2nd‑largest ⇒ 5)
```

---

## 1.  The code (trimmed to the two key methods)

```java
int findKthLargest(int[] nums, int k) {
    int n      = nums.length;      // 6
    int target = n - k;            // index 4 (ascending order)

    int left = 0, right = n - 1;   // search window [0, 5]

    while (true) {
        int p = partition(nums, left, right);  // Lomuto scheme
        if      (p == target) return nums[p];
        else if (p <  target) left  = p + 1;   // look right
        else                  right = p - 1;   // look left
    }
}

int partition(int[] a, int left, int right) {
    int pivot = a[right];          // choose last element as pivot (4)
    int i = left;                  // boundary of "≤ pivot" zone
    for (int j = left; j < right; j++)
        if (a[j] <= pivot) swap(a, i++, j);
    swap(a, i, right);             // place pivot in final spot
    return i;                      // pivot's index
}
```

*For teaching clarity we **always pick the last element as pivot**; in real code we randomise to avoid the worst‑case.*

---

## 2.  Detailed execution trace

| Pass  | `left`‑`right` range | **Array before partition** | `pivot` value (index) | `i`/`j` swaps inside `partition`                                                                                                              | **Array after partition** | `p` (pivot index) | Compare `p` with `target = 4`    | Next `left`/`right`   |
| ----- | -------------------- | -------------------------- | --------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------- | ----------------- | -------------------------------- | --------------------- |
| **1** | `[0, 5]`             | `[3, 2, 1, 5, 6, 4]`       | `4` (idx 5)           | *j = 0*: 3 ≤ 4 → swap(0,0) (noop) <br>*j = 1*: 2 ≤ 4 → swap(1,1) <br>*j = 2*: 1 ≤ 4 → swap(2,2) <br>*j = 3*: 5 ≤ 4? no <br>*j = 4*: 6 ≤ 4? no | `[3, 2, 1, 4, 6, 5]`      | **3**             | 3 < 4 ⇒ answer is **right side** | `left = 4, right = 5` |
| **2** | `[4, 5]`             | `[3, 2, 1, 4, 6, 5]`       | `5` (idx 5)           | *j = 4*: 6 ≤ 5? no                                                                                                                            | `[3, 2, 1, 4, 5, 6]`      | **4**             | 4 == 4 ⇒ **found it**            | stop                  |

`nums[4]` is `5`, which is the 2‑nd largest element—exactly what we wanted.

---

### Step‑by‑step narrative for Pass 1

1. **Window** is the whole array, indices 0 .. 5.

2. **Pivot** chosen = `nums[5] = 4`.

3. **Partition loop** (`j` travels left→right‑1):

   | j | `nums[j]` | ≤ pivot? | Action                 | i after action | Array snapshot       |
   | - | --------- | -------- | ---------------------- | -------------- | -------------------- |
   | 0 | 3         | yes      | swap(i=0, j=0) (no‑op) | 1              | `[3, 2, 1, 5, 6, 4]` |
   | 1 | 2         | yes      | swap(1,1) (no‑op)      | 2              | `[3, 2, 1, 5, 6, 4]` |
   | 2 | 1         | yes      | swap(2,2) (no‑op)      | 3              | `[3, 2, 1, 5, 6, 4]` |
   | 3 | 5         | no       | —                      | 3              | unchanged            |
   | 4 | 6         | no       | —                      | 3              | unchanged            |

4. **Post‑loop**: swap pivot into place → swap(i=3, right=5):
   `[3, 2, 1, 4, 6, 5]`, so pivot index `p = 3`.

5. Compare `p (=3)` with `target (=4)` → pivot is **left** of the target, so search the **right sub‑array** only (`left = 4`).

### Step‑by‑step narrative for Pass 2

1. **Window** is now indices 4 .. 5 (two elements `[6, 5]`).

2. **Pivot** = last element `5`.

3. **Partition loop** (`j` only = 4):

   * `nums[4] = 6` > pivot → no swap, `i` stays 4.

4. Swap pivot into place: swap(i=4, right=5) → `[…, 5, 6]`; pivot index `p = 4`.

5. `p == target`, so we **return `nums[4]` = `5`**.

---

## 3.  Why Quickselect is efficient

* **Discard halves**: after each partition we *throw away* one side; here we examined 3 + 2 = 5 elements instead of sorting all 6.
* **Average Θ(n)**: with random pivots, the expected leftover portion shrinks geometrically (½, ¼, ⅛, …).
* **In‑place**: only a few index variables; no extra arrays.

---

## 4.  Take‑aways for interviews

* **Explain `target = n − k`** before you code—it shows you understand 0‑indexing.
* **Walk through one partition by hand** exactly like above to prove correctness.
* **Mention worst‑case Θ(n²)** and the usual cure: a random (or median‑of‑medians) pivot.

With this detailed trace you can now *confidently narrate every line* of the algorithm on a whiteboard or code editor and justify each pointer movement.
*/
/**
 * Quickselect: average O(n), worst O(n^2), O(1) space.
 * We write it iteratively to avoid recursion depth issues.
 */

// class SolutionQuickselect {

//     // One Random instance is plenty; re‑using it avoids accidental patterns
//     private final Random rand = new Random();

//     public int findKthLargest(int[] nums, int k) {
//         int n = nums.length;
//         int target = n - k;          // index in ascending order

//         int left = 0, right = n - 1;
//         while (true) {
//             // Partition and capture pivot's final position
//             int p = randomizedPartition(nums, left, right);

//             if (p == target)        // Found the exact index
//                 return nums[p];
//             else if (p < target)    // Target is in the right part
//                 left = p + 1;
//             else                    // Target is in the left part
//                 right = p - 1;
//         }
//     }

//     /* ========= PRIVATE HELPERS ========= */

//     /** Swap a random element into position 'right', then partition. */
//     private int randomizedPartition(int[] a, int left, int right) {
//         int pivotIdx = left + rand.nextInt(right - left + 1);
//         swap(a, pivotIdx, right);
//         return partition(a, left, right);      // Lomuto scheme
//     }

//     /**
//      * Lomuto partition:
//      * After this returns, pivot is at its final sorted index,
//      * all items ≤ pivot are on its left, and > pivot are on its right.
//      * Returns that final pivot index.
//      */
//     private int partition(int[] a, int left, int right) {
//         int pivot = a[right];      // pivot value (last element)

//         int i = left;              // 'i' is the insertion point for smaller elems
//         for (int j = left; j < right; j++) {
//             if (a[j] <= pivot) {   // '<=' keeps algorithm stable for duplicates
//                 swap(a, i, j);
//                 i++;               // increment boundary of "≤ pivot" zone
//             }
//         }
//         swap(a, i, right);         // place pivot in its correct spot
//         return i;
//     }

//     /** Utility: swap a[i] and a[j] */
//     private void swap(int[] a, int i, int j) {
//         int tmp = a[i];
//         a[i] = a[j];
//         a[j] = tmp;
//     }
// }
