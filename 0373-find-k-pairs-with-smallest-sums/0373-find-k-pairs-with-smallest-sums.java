// Method 1: Adding values using Min Heap but avoiding all pairs check.
// Also keeping track of index of nums2 so that we can use it later to explore
// other values in nums2. (Note: The third values we are adding, i.e the index of nums2,
// is not part of the priority comparison we have defined. It only exists to give extra info,
// and not decide priority.)
/*
How does Java know to compare only the first two?
Because of this custom comparator:

PriorityQueue<int[]> minHeap = new PriorityQueue<>(
    (a, b) -> Integer.compare(a[0] + a[1], b[0] + b[1])
);
This tells the PriorityQueue:

"Whenever you compare two items a and b, use the sum a[0] + a[1] and b[0] + b[1]."

This comparator only uses index 0 and index 1 of the array.
It completely ignores a[2] or b[2] (even though it’s part of the array being stored).




##  Example for code walkthrough

Let's use:

```java
nums1 = [1, 7]
nums2 = [3, 5, 6]
k = 5
```

That means we want the **5 pairs with the smallest sums** from all possible combinations of one element from `nums1` and one from `nums2`.

---

###  All Possible Pairs (with sums):

| Pair   | Sum |
| ------ | --- |
| (1, 3) | 4   |
| (1, 5) | 6   |
| (1, 6) | 7   |
| (7, 3) | 10  |
| (7, 5) | 12  |
| (7, 6) | 13  |

Out of these, we want the **5 smallest sum pairs** → result should be:

```
[[1,3], [1,5], [1,6], [7,3], [7,5]]
```

---

##  Code Structure and Logic

We're using a **min-heap (priority queue)** where each element is a triplet:

```java
new int[] { nums1[i], nums2[j], j }
```

| Index | Value     | Meaning                   |
| ----- | --------- | ------------------------- |
| 0     | nums1\[i] | First number of the pair  |
| 1     | nums2\[j] | Second number of the pair |
| 2     | j         | Current index in nums2    |

We do this to efficiently generate the next smallest pair for the same `nums1[i]` by incrementing `j`.

---

##  Detailed Walkthrough

### ➤ Initial Heap Setup

We insert the **first `min(k, nums1.length)`** pairs using `nums2[0]` (smallest element in `nums2`) for each `nums1[i]`.

```java
for (int i = 0; i < Math.min(nums1.length, k); i++) {
    minHeap.offer(new int[] { nums1[i], nums2[0], 0 });
}
```

### Initial Heap Contents:

* `{1, 3, 0}` → sum = 4
* `{7, 3, 0}` → sum = 10

Heap stores pairs ordered by `nums1[i] + nums2[j]`

---

###  Begin Popping & Pushing

We'll now extract the smallest-sum pair and, if possible, push the next pair using the same `nums1[i]` and `nums2[j + 1]`.

---

###  Iteration 1

**Pop:** `{1, 3, 0}` → sum = 4 → add `[1, 3]` to result
**j = 0** → we can try `j + 1 = 1`
**Push:** `{1, 5, 1}` → sum = 6

**Result so far:** `[[1, 3]]`

**Heap now contains:**

* `{1, 5, 1}` → sum = 6
* `{7, 3, 0}` → sum = 10

---

###  Iteration 2

**Pop:** `{1, 5, 1}` → sum = 6 → add `[1, 5]` to result
**j = 1** → push `{1, 6, 2}` → sum = 7

**Result so far:** `[[1, 3], [1, 5]]`

**Heap:**

* `{1, 6, 2}` → sum = 7
* `{7, 3, 0}` → sum = 10

---

###  Iteration 3

**Pop:** `{1, 6, 2}` → sum = 7 → add `[1, 6]`
**j = 2** → no next element in `nums2`, so **don't push anything**

**Result so far:** `[[1, 3], [1, 5], [1, 6]]`

**Heap:**

* `{7, 3, 0}` → sum = 10

---

###  Iteration 4

**Pop:** `{7, 3, 0}` → sum = 10 → add `[7, 3]`
**j = 0** → push `{7, 5, 1}` → sum = 12

**Result so far:** `[[1, 3], [1, 5], [1, 6], [7, 3]]`

**Heap:**

* `{7, 5, 1}` → sum = 12

---

###  Iteration 5

**Pop:** `{7, 5, 1}` → sum = 12 → add `[7, 5]`
**j = 1** → push `{7, 6, 2}` → sum = 13 (we won’t need this, since `k = 5`)

**Result so far:** `[[1, 3], [1, 5], [1, 6], [7, 3], [7, 5]]`  Done!

---

### Final Output:

```java
[[1, 3], [1, 5], [1, 6], [7, 3], [7, 5]]
```

---

##  What's Great About This Approach

| Strength                                 | Why It Matters                                          |
| ---------------------------------------- | ------------------------------------------------------- |
| **Avoids full O(mn) cartesian product**  | Only inserts pairs when needed, up to `k` elements      |
| **Min-heap keeps smallest sum on top**   | We only pop and push the next possible smallest sum     |
| **Index tracking avoids duplicate work** | By knowing `j`, we can build new pairs only when needed |

---

##  Visual Summary of Heap State (iteration by iteration)

```
Heap: [(1,3,0), (7,3,0)]           → Pop (1,3,0), push (1,5,1)
Heap: [(1,5,1), (7,3,0)]           → Pop (1,5,1), push (1,6,2)
Heap: [(1,6,2), (7,3,0)]           → Pop (1,6,2), no push
Heap: [(7,3,0)]                    → Pop (7,3,0), push (7,5,1)
Heap: [(7,5,1)]                    → Pop (7,5,1), push (7,6,2) (ignored)
```
*/
class Solution {
    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        // Base case: if either array is empty
        if (nums1.length == 0 || nums2.length == 0 || k == 0)
            return result;

        // Min-heap sorted by sum of pair: (a[0] + a[1])
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(a[0] + a[1], b[0] + b[1])
        );

        // Insert first k pairs (nums1[i], nums2[0]) into the heap
        for (int i = 0; i < Math.min(nums1.length, k); i++) {
            minHeap.offer(new int[] { nums1[i], nums2[0], 0 }); // third element is index in nums2
        }

        while (k-- > 0 && !minHeap.isEmpty()) {
            int[] pair = minHeap.poll();
            result.add(Arrays.asList(pair[0], pair[1]));

            int i = pair[0];
            int j = pair[2];

            // Push next pair (same nums1[i], nums2[j + 1])
            if (j + 1 < nums2.length) {
                minHeap.offer(new int[] { pair[0], nums2[j + 1], j + 1 });
            }
        }

        return result;
    }
}



// Method 2: Min-Heap of Index-Pairs
/*
 Heap Element: int[] = {i, j}
Heap holds indices into nums1 and nums2 (instead of values).

Comparisons are done using nums1[i] + nums2[j]

 How it Works
Start by inserting the first min(k, nums1.length) pairs of the form (i, 0) into the heap (i.e., index in nums1 with first element in nums2).

On each pop:

Convert (i, j) into the actual pair: nums1[i], nums2[j].

Push the next pair (i, j+1) into the heap.

This avoids duplicate pairs like (nums1[i], nums2[j]) and keeps track of next column for a given row.
*/

// class Solution {
//     public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
//         List<List<Integer>> result = new ArrayList<>();

//         if (nums1.length == 0 || nums2.length == 0 || k == 0)
//             return result;

//         // Min-heap: compare by nums1[i] + nums2[j]
//         PriorityQueue<int[]> minHeap = new PriorityQueue<>(
//             (a, b) -> Integer.compare(nums1[a[0]] + nums2[a[1]], nums1[b[0]] + nums2[b[1]])
//         );
//         /*
//         Another way to define this Min heap by using Compactor:
//         PriorityQueue<int[]> minHeap = new PriorityQueue<>(new Comparator<int[]>() {
//             @Override
//             public int compare(int[] a, int[] b){
//                 return (nums1[a[0]] + nums2[a[1]]) - (nums1[b[0]] + nums2[b[1]]);
//             }
//         });
//         */

//         // Start with first element in each row: (i, 0)
//         for (int i = 0; i < Math.min(nums1.length, k); i++) {
//             minHeap.offer(new int[] { i, 0 });  // i = index in nums1, j = index in nums2
//         }

//         while (k-- > 0 && !minHeap.isEmpty()) {
//             int[] indices = minHeap.poll();
//             int i = indices[0], j = indices[1];
//             result.add(Arrays.asList(nums1[i], nums2[j]));

//             if (j + 1 < nums2.length) {
//                 minHeap.offer(new int[] { i, j + 1 });
//             }
//         }

//         return result;
//     }
// }