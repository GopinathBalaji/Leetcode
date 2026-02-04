/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */


// Method 1: MergeSort Recursive Divide and Conquer Approach 
/*
### 1) Think in ranges

* Write a helper that works on a **range of lists**: `mergeRange(lists, lo, hi)`.
* This lets you isolate the D\&C logic cleanly.

### 2) Base cases

* If the range is **empty** (`lo > hi`) → return `null`.
* If the range has **one list** (`lo == hi`) → return that list unchanged.

### 3) Divide step

* Compute `mid = lo + (hi - lo) / 2`.
* Recursively solve **left half** `[lo..mid]` and **right half** `[mid+1..hi]`.
* You now have **two sorted lists** (one per half).

### 4) Conquer (combine) step

* **Merge the two sorted lists** from the halves into one sorted list.
* Reuse the classic **2-list merge** pattern (dummy node + tail pointer; link the smaller node each step).

### 5) Stability/structure tips

* Keep the 2-list merge **stable** by choosing from the left list when values are equal.
* Reuse nodes; **don’t allocate new nodes** for values—just relink `next`.

### 6) Complexity intuition (to verify you’re on track)

* The recursion depth is about **log₂ k** (k lists).
* At each depth, every node across all lists is processed once ⇒ about **O(N)** per level.
* Total ≈ **O(N log k)** time, **O(log k)** recursion stack.

### 7) Edge cases to watch

* Some lists may be `null`—base cases should naturally handle these.
* `k == 0` → return `null`.
* Extremely uneven list lengths: D\&C still balances merges and avoids worst-case chaining.

### 8) Testing checklist (small to large)

* `[]` → `null`
* `[null, null]` → `null`
* `[ [1], [2], [3] ]` → `[1,2,3]`
* Mixed lengths: `[ [1,4,5], [1,3,4], [2,6] ]` → should be sorted correctly.

### 9) Alternative (same idea, iterative)

* If recursion is frowned upon, mimic merge-sort **bottom-up**: merge pairs with gap size `1, 2, 4, ...` until one list remains. Same O(N log k), no recursion.
*/

class Solution {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;   // empty input
        return mergeRange(lists, 0, lists.length - 1);          // D&C over full range
    }

    private ListNode mergeRange(ListNode[] lists, int low, int high) {
        if (low > high) return null;            // no lists in range
        if (low == high) return lists[low];     // single list (may be null)

        int mid = low + (high - low) / 2;

        // Recursively merge left and right halves
        ListNode left  = mergeRange(lists, low, mid);
        ListNode right = mergeRange(lists, mid + 1, high);

        // Merge two sorted linked lists
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (left != null && right != null) {
            if (left.val <= right.val) {
                tail.next = left;
                left = left.next;
            } else {
                tail.next = right;
                right = right.next;
            }
            tail = tail.next;
        }

        // Append remaining nodes
        tail.next = (left != null) ? left : right;

        return dummy.next;
    }
}







// Method 1.5: More clean mergesort approach (Same as above)
/*
## Why “merge sort” fits this problem

You already know how to merge **2** sorted linked lists efficiently in `O(n)` time (like LC 21).

To merge **k** lists, instead of merging one-by-one (which can be slow), you do what merge sort does:

1. **Split** the array of lists into two halves
2. Recursively merge the left half into one list
3. Recursively merge the right half into one list
4. **Merge the two results**

This keeps merges balanced, so each node participates in about `log k` merges.

---

## Time and space complexity

Let `N` = total number of nodes across all lists, and `k` = number of lists.

* **Time:** `O(N log k)`
  Because each “level” of merging touches all nodes once, and there are `log k` levels.
* **Extra Space:** `O(log k)` recursion depth (not counting the output list, which reuses nodes)


## Thorough example walkthrough

### Input

`k = 3` lists:

* L0: `1 → 4 → 5`
* L1: `1 → 3 → 4`
* L2: `2 → 6`

We call:
`mergeRange(lists, 0, 2)`

---

## Step 1: Split like merge sort

Range `[0..2]`, mid = 1
So we merge two halves:

* Left half: `[0..1]`  → merge L0 and L1
* Right half: `[2..2]` → just L2

So structure is:

```
mergeRange(0,2)
  = mergeTwo( mergeRange(0,1), mergeRange(2,2) )
```

---

## Step 2: Merge left half `mergeRange(0,1)`

Range `[0..1]`, mid = 0
So:

* `mergeRange(0,0)` returns L0 = `1→4→5`
* `mergeRange(1,1)` returns L1 = `1→3→4`

Now merge them: `mergeTwo(L0, L1)`

### Merging `1→4→5` and `1→3→4`

Let `a` point to L0, `b` point to L1.

Result building:

1. Compare `a=1` and `b=1` → take `a` (tie goes left)

   * result: `1`
   * a moves to `4`, b stays `1`
2. Compare `a=4` and `b=1` → take `b`

   * result: `1 → 1`
   * b moves to `3`
3. Compare `a=4` and `b=3` → take `b`

   * result: `1 → 1 → 3`
   * b moves to `4`
4. Compare `a=4` and `b=4` → take `a` (tie goes left)

   * result: `1 → 1 → 3 → 4`
   * a moves to `5`
5. Compare `a=5` and `b=4` → take `b`

   * result: `1 → 1 → 3 → 4 → 4`
   * b becomes `null`

Now b is done, append remainder of a (`5`):

* mergedLeft = `1 → 1 → 3 → 4 → 4 → 5`

So:
`mergeRange(0,1) = 1 → 1 → 3 → 4 → 4 → 5`

---

## Step 3: Right half `mergeRange(2,2)`

That’s just L2:

* right = `2 → 6`

---

## Step 4: Final merge

Now merge:

* left = `1 → 1 → 3 → 4 → 4 → 5`
* right = `2 → 6`

### Merging

1. Compare 1 vs 2 → take 1

   * `1`
2. Compare 1 vs 2 → take 1

   * `1 → 1`
3. Compare 3 vs 2 → take 2

   * `1 → 1 → 2`
4. Compare 3 vs 6 → take 3

   * `1 → 1 → 2 → 3`
5. Compare 4 vs 6 → take 4

   * `… → 4`
6. Compare 4 vs 6 → take 4

   * `… → 4`
7. Compare 5 vs 6 → take 5

   * `… → 5`
8. left ends, append 6

Final answer:
**`1 → 1 → 2 → 3 → 4 → 4 → 5 → 6`**

---

## Why this beats “merge one by one”

If you merge sequentially:

* merge (L0 + L1) then + L2 then + L3 …
  later merges keep getting bigger → more repeated work.

Divide & conquer keeps merges balanced, like merge sort, ensuring each node is reprocessed only `O(log k)` times.

---

If you want, I can also show the **iterative bottom-up version** (pair lists in rounds: 1-step merges, then 2-step, 4-step…) which avoids recursion but uses the same merge-sort idea.
*/

// class Solution {
//     public ListNode mergeKLists(ListNode[] lists) {
//         if (lists == null || lists.length == 0) return null;
//         return mergeRange(lists, 0, lists.length - 1);
//     }

//     private ListNode mergeRange(ListNode[] lists, int lo, int hi) {
//         if (lo == hi) return lists[lo];

//         int mid = lo + (hi - lo) / 2;
//         ListNode left = mergeRange(lists, lo, mid);
//         ListNode right = mergeRange(lists, mid + 1, hi);

//         return mergeTwo(left, right);
//     }

//     private ListNode mergeTwo(ListNode a, ListNode b) {
//         ListNode dummy = new ListNode(0);
//         ListNode tail = dummy;

//         while (a != null && b != null) {
//             if (a.val <= b.val) {
//                 tail.next = a;
//                 a = a.next;
//             } else {
//                 tail.next = b;
//                 b = b.next;
//             }
//             tail = tail.next;
//         }

//         // attach remainder
//         tail.next = (a != null) ? a : b;

//         return dummy.next;
//     }
// }









// Method 1.5.2: Iterative MergeSort O(N log k) time
/*
How it works (brief): we repeatedly merge adjacent pairs of lists with increasing interval sizes (1, 2, 4, …). After ⌈log₂ k⌉ rounds, everything is merged into lists[0].
*/
// class Solution {
//     public ListNode mergeKLists(ListNode[] lists) {
//         if (lists == null || lists.length == 0) return null;

//         int n = lists.length;
//         // interval = size of the gap between pairs to merge: 1,2,4,8,...
//         for (int interval = 1; interval < n; interval <<= 1) {
//             for (int i = 0; i + interval < n; i += interval << 1) {
//                 lists[i] = mergeTwo(lists[i], lists[i + interval]);
//             }
//         }
//         return lists[0];
//     }

//     // Standard merge of two sorted linked lists (relinks nodes, no new allocations except dummy)
//     private ListNode mergeTwo(ListNode a, ListNode b) {
//         ListNode dummy = new ListNode(0), tail = dummy;
//         while (a != null && b != null) {
//             if (a.val <= b.val) {
//                 tail.next = a; 
//                 a = a.next;
//             } else {
//                 tail.next = b; 
//                 b = b.next;
//             }
//             tail = tail.next;
//         }
//         tail.next = (a != null) ? a : b;
//         return dummy.next;
//     }
// }








// Method 2: Optimal Min Heap approach
/*
Great eye. Don’t heap **all nodes**—that does extra work.

### Two heap strategies

1. **Naïve heap of all nodes (avoid):**

   * Push every node from all lists into a heap, then pop in order.
   * **Time:** `O(N log N)` (because the heap size grows to N)
   * **Space:** `O(N)` heap
   * You also lose the streaming nature—pointless overhead.

2. **Optimal “k-way merge” heap (do this):**

   * Maintain a **min-heap of size ≤ k** with the *current head* of each non-empty list.
   * Repeatedly:

     * Pop the smallest node `x`.
     * Append `x` to the result.
     * If `x.next` exists, **push `x.next`** back into the heap.
   * **Time:** `O(N log k)` — each of the N nodes is pushed/popped once, heap size is at most k.
   * **Space:** `O(k)` for the heap (plus output).

This mirrors the classic k-way merge (like merging k sorted files).

### Why it’s more efficient

* Keeps the heap tiny (k items), so every heap op is `log k`, not `log N`.
* Streams results as you go, no need to hold everything.
* Reuses existing nodes; you only relink `next`.

### Practical tips (Java)

* Use a `PriorityQueue<ListNode>` with comparator `a.val - b.val` (careful: use `Integer.compare` to avoid overflow).
* Skip null lists up front.
* Relink nodes directly (no new allocations except a dummy head).

```java
// Sketch (concise)
PriorityQueue<ListNode> pq = new PriorityQueue<>( (a, b) -> Integer.compare(a.val, b.val) );
for (ListNode h : lists) if (h != null) pq.offer(h);

ListNode dummy = new ListNode(0), tail = dummy;
while (!pq.isEmpty()) {
    ListNode node = pq.poll();
    tail.next = node; tail = node;
    if (node.next != null) pq.offer(node.next);
}
return dummy.next;
```

### When to pick heap vs. divide-and-conquer

* **Both** are `O(N log k)`.
* Heap is great when you want a simple streaming merge and minimal extra memory (`O(k)`).
* Divide-and-conquer often has excellent constants and is recursion/iteration over merges; pick based on preference or constraints (e.g., avoid recursion depth, or leverage a preexisting `mergeTwo`).

**Bottom line:** Use the **k-sized min-heap** of current heads. Never heap all nodes at once.


Optimal **k-way merge with a min-heap**. It keeps the heap size ≤ *k* (one current head per list), so every heap op is `log k`, giving total time `O(N log k)` and space `O(k)`.

### How this works (step-by-step)

* **Initialization:** put the first node (head) of every non-empty list into the min-heap. Heap size ≤ *k*.
* **Loop:**

  1. Pop the smallest node `x` from the heap.
  2. Append `x` to the merged list (via `tail.next = x; tail = x`).
  3. If `x.next` exists, push `x.next` into the heap (that list’s next candidate).
* **Finish:** once the heap is empty, all nodes have been streamed into the result in sorted order.

### Why this is optimal

* Each of the **N** nodes is pushed and popped **once** → `2N` heap ops.
* Heap size is **at most k** → each op is `O(log k)`.
* Total time: **`O(N log k)`**, extra space: **`O(k)`** (heap).
* We **reuse nodes** (no copying), so memory overhead stays low and the merge is stable within each list.

### Edge cases & tips

* If some `lists[i]` is `null`, we simply don’t push it—no special handling needed.
* Duplicate values are fine (the PQ handles ties). If you needed global stability across lists, you could add a **tie-breaker** (e.g., a monotonically increasing sequence number) to the comparator, but LeetCode doesn’t require it.
* Be careful not to heap **all nodes** at once—that would be `O(N log N)` and extra memory for no benefit.
* If inputs are extremely large, this streaming approach keeps memory usage predictable (only **k** nodes in the heap at any time).

### Complexity recap

* **Time:** `O(N log k)`
* **Space:** `O(k)` (heap), `O(1)` additional aside from output links
*/

// LeetCode provides the ListNode definition:
// class ListNode {
//     int val;
//     ListNode next;
//     ListNode() {}
//     ListNode(int val) { this.val = val; }
//     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
// }

// class Solution {
//     public ListNode mergeKLists(ListNode[] lists) {
//         // Edge case: no lists at all
//         if (lists == null || lists.length == 0) return null;

//         // Min-heap ordered by node value (use Integer.compare to avoid overflow)
//         PriorityQueue<ListNode> pq = new PriorityQueue<>(
//             (a, b) -> Integer.compare(a.val, b.val)
//         );

//         // Seed the heap with the head of each non-empty list (≤ k elements)
//         for (ListNode head : lists) {
//             if (head != null) pq.offer(head);
//         }

//         // Dummy head to simplify building the result; 'tail' will always point to the last node in the merged list
//         ListNode dummy = new ListNode(0);
//         ListNode tail = dummy;

//         // Repeatedly extract the smallest current node, then advance that list by pushing its next node
//         while (!pq.isEmpty()) {
//             ListNode node = pq.poll();       // smallest among current heads
//             tail.next = node;                 // append to result
//             tail = tail.next;                 // advance tail (no new allocations; we just relink)

//             // IMPORTANT: bring in the next candidate from the same list
//             // This how we cover all the nodes in all the lists
//             if (node.next != null) {
//                 pq.offer(node.next);          
//             }
//         }

//         // Ensure the final list terminates (good hygiene; usually already true)
//         tail.next = null;

//         return dummy.next;
//     }
// }








// Method 2.5: Naive MinHeap approach
/*
*/

// class Solution {
//     public ListNode mergeKLists(ListNode[] lists) {
//         if(lists.length == 0){
//             return null;
//         }

//         PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> a.val - b.val);
//         // PriorityQueue<ListNode> minHeap = new PriorityQueue<>(Comparator.comparingInt(node -> node.val));

//         for(int i=0; i<lists.length; i++){
//             ListNode head = lists[i];
//             while(head != null){
//                 minHeap.offer(head);
//                 head = head.next;
//             }
//         }

//         ListNode dummy = new ListNode(0);
//         ListNode curr = dummy;

//         while(!minHeap.isEmpty()){
//             curr.next = minHeap.poll();
//             curr = curr.next;
//         }

//         curr.next = null;

//         return dummy.next;
//     }
// }


