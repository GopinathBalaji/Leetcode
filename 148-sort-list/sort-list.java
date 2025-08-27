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

// Recursive Merget Sort O(n log n) time, O(log n) space because of recursive call stack
/*
Find mid with slow/fast pointers.
Cut at mid: mid.next = null.
Recurse on each half; both calls return sorted heads.
Merge two sorted lists; return merged head.
*/
class Solution {
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;

        // split list into [head..mid] and [midNext..] using fast and slow pointer approach
        ListNode slow = head, fast = head, prev = null;
        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }
        prev.next = null;                 // cut

        ListNode l1 = sortList(head);     // sort left
        ListNode l2 = sortList(slow);     // sort right
        return merge(l1, l2);
    }

    private ListNode merge(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), tail = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) { tail.next = a; a = a.next; }
            else { tail.next = b; b = b.next; }
            tail = tail.next;
        }
        tail.next = (a != null) ? a : b;
        return dummy.next;
    }
}



// Iterative merge sort O(n log n) time, O(1) space
/*
length(head): count nodes.
Outer loop doubles size.
Inner loop repeatedly:
    left = curr
    right = split(left, size) // cuts after size nodes, returns head of the next run
    curr = split(right, size) // cuts the second run
    merge(left, right) // returns (mergedHead, mergedTail)
    stitch to the global result with a tail pointer.




We’ll sort this list:

```
head: 4 → 1 → 7 → 3 → 2 → 6 → 5 → null
```

n = 7 (from `length(head)`), `dummy.next = head`.

---

# Pass 1 — `size = 1`

At this size, you merge **runs of length 1** into sorted runs of length up to 2.

* Initialize: `curr = dummy.next (4)`, `tail = dummy`.

## Chunk 1

1. `left = curr = 4`
2. `right = split(left, 1)`

   * `split` cuts **after 1 node**: returns the head of the next run (`1`) and sets `4.next = null`.
   * Now `left` is `4 → null`, `right` is `1 → 7 → 3 → 2 → 6 → 5`.
3. `curr = split(right, 1)`

   * Cuts `1` off: returns `7`, sets `1.next = null`.
   * Now `right` is `1 → null`, `curr` is `7`.
4. `merge(left, right)` = merge(4, 1) → `1 → 4` (tail = 4)
5. Stitch: `tail.next = 1`, `tail = 4`.
   Current list (stitched so far): `1 → 4`

## Chunk 2

1. `left = curr = 7`
2. `right = split(7, 1)` → returns `3`, cut `7.next = null`
3. `curr = split(3, 1)` → returns `2`, cut `3.next = null`
4. merge(7, 3) → `3 → 7` (tail = 7)
5. Stitch after `4`: list becomes `1 → 4 → 3 → 7`. `tail = 7`.

## Chunk 3

1. `left = curr = 2`
2. `right = split(2, 1)` → returns `6`, cut `2.next = null`
3. `curr = split(6, 1)` → returns `5`, cut `6.next = null`
4. merge(2, 6) → `2 → 6` (tail = 6)
5. Stitch: `1 → 4 → 3 → 7 → 2 → 6`. `tail = 6`.

## Chunk 4 (trailing single)

1. `left = curr = 5`
2. `right = split(5, 1)` → returns `null`, `5` already isolated
3. `curr = split(null, 1)` → `null`
4. merge(5, null) → `5` (tail = 5)
5. Stitch: `1 → 4 → 3 → 7 → 2 → 6 → 5`. `tail = 5`.

End of pass (`curr == null`).
`dummy.next` now points to the head of this partially sorted sequence:

```
After size=1: 1 → 4 → 3 → 7 → 2 → 6 → 5
```

These are now runs of length \~2 (except the last run length 1).

---

# Pass 2 — `size = 2`

Merge **runs of length 2** into runs of length up to 4.

* `curr = head = 1`, `tail = dummy`.

## Chunk 1 (two 2-length runs)

* `left = 1 → 4`
  `right = split(left, 2)` walks two nodes (1,4) and cuts there → `right` becomes `3 → 7 → 2 → 6 → 5`, and `left` is isolated: `1 → 4 → null`.
* `curr = split(right, 2)` walks (3,7) and cuts → `curr = 2`, `right = 3 → 7 → null`.

Merge:

* merge(`1 → 4`, `3 → 7`) → `1 → 3 → 4 → 7` (merged tail = 7)

Stitch:

* `tail.next = 1`, `tail = 7`.
  Current stitched prefix: `1 → 3 → 4 → 7`

## Chunk 2 (run of 2 and run of 1)

* `left = curr = 2 → 6` (after `split(2,2)` → isolates `2 → 6`)
* `right = split(5, 2)` → only one node exists, so `right = 5`, `curr = null`.

Merge:

* merge(`2 → 6`, `5`) → `2 → 5 → 6` (tail = 6)

Stitch:

* `1 → 3 → 4 → 7 → 2 → 5 → 6`. `tail = 6`.

End of pass. Sequence is now runs of length \~4 (and a tail).

```
After size=2: 1 → 3 → 4 → 7 → 2 → 5 → 6
```

---

# Pass 3 — `size = 4`

Merge **runs of length 4** into runs of length up to 8 (i.e., entire list).

* `curr = head = 1`, `tail = dummy`.

Only one big merge this pass:

* `left = 1 → 3 → 4 → 7` (after `split(left,4)` cuts there)
* `right = 2 → 5 → 6` (after `split(right,4)` it just isolates whatever remains; there are only 3 nodes)

Merge:

* merge(`1,3,4,7`, `2,5,6`) → `1 → 2 → 3 → 4 → 5 → 6 → 7` (tail = 7)

Stitch:

* `dummy.next = 1`, `tail = 7`

`curr` becomes `null` → pass ends.
Next size would be `8` (≥ n), outer loop stops.

Final sorted list:

```
1 → 2 → 3 → 4 → 5 → 6 → 7
```

---

## How the helpers behave (intuition)

* `split(head, size)`
  Walks up to `size - 1` links (or until the sublist ends), **cuts** there (`curr.next = null`), and returns the head of the **next** run. This is how we turn one long list into consecutive fixed-length runs without using indices.

* `merge(a, b)`
  Standard two-pointer merge that **relinks existing nodes** (no new nodes except a small local dummy). It also walks to the tail and returns `{mergedHead, mergedTail}` so the caller can stitch quickly with `tail.next = mergedHead; tail = mergedTail`.

* `tail`
  Always points to the **last node of the already-stitched part** in the current pass. After each merge, we glue the merged run after `tail` and advance `tail` to the merged tail.

---

## Why this works (and what to say in an interview)

* Each pass doubles the run size (`1, 2, 4, …`), so after `⌈log₂ n⌉` passes the list is sorted.
* Time: **O(n log n)** — every node participates in `log n` merges; each merge work is linear in the total nodes processed that pass.
* Space: **O(1)** auxiliary (no recursion), just a few pointers; stable because we link from the left run on `<=`.
*/

// class Solution {
//     public ListNode sortList(ListNode head) {
//         // If list is empty or has one node, it's already sorted.
//         if (head == null || head.next == null) return head;

//         // Compute total length n so we know how many pass sizes we need (1,2,4,8,...).
//         int n = length(head);

//         // Dummy node simplifies stitching merged runs back into the list.
//         // We'll always connect after 'dummy'/'tail' and return dummy.next at the end.
//         ListNode dummy = new ListNode(0);
//         dummy.next = head; // current list head is the start of the first pass

//         // size = current run length to merge. Start from 1, double each pass,
//         // that is, size = length of runs to merge: 1, 2, 4, 8, ...
//         for (int size = 1; size < n; size <<= 1) {
//             // 'curr' walks through the list, cutting successive runs of length 'size'
//             // 'tail' is the end of the already merged/stiched part for this pass
//             ListNode curr = dummy.next;
//             ListNode tail = dummy; // initially nothing is merged in this pass

//             // Process the entire list in chunks: [left run of 'size'] + [right run of 'size']
//             while (curr != null) {
//                 // 1) 'left' points to the start of the first run
//                 ListNode left = curr;

//                 // 2) Split after 'size' nodes to isolate the left run.
//                 //    Return value is the head of the right run (may be null).
//                 //    Also, the split() call terminates the left run by setting its tail.next = null.
//                 ListNode right = split(left, size);

//                 // 3) Split after another 'size' nodes to isolate the right run.
//                 //    The return value is the head of the remainder (next chunk) for the next loop turn.
//                 curr = split(right, size);

//                 // 4) Merge the two sorted runs ('left' and 'right'), which yields a merged sorted run.
//                 //    'merged[0]' is the head of the merged run; 'merged[1]' is its tail.
//                 ListNode[] merged = merge(left, right);

//                 // Stitch the merged run right after 'tail' (the already merged prefix for this pass).
//                 tail.next = merged[0];

//                 // Advance 'tail' to the end of the merged run, so the next stitched run attaches after it.
//                 tail = merged[1];
//             }
//             // End of one pass for this 'size'. Next iteration doubles 'size'.
//         }

//         // After all passes, dummy.next points to the fully sorted list head.
//         return dummy.next;
//     }

//     // Returns the number of nodes in the list (used to bound the outer loop).
//     private int length(ListNode head) {
//         int cnt = 0;               // running count of nodes
//         while (head != null) {     // iterate until end of list
//             cnt++;                 // count this node
//             head = head.next;      // move to next
//         }
//         return cnt;                // total length
//     }

//     // Split the list after 'size' nodes starting at 'head'.
//     // - If there are fewer than 'size' nodes, it just cuts at the end of the run (or not at all if head==null).
//     // - It returns the head of the next segment (i.e., the node after the cut) or null if no more nodes.
//     // - It also terminates the first segment by setting its tail.next = null (so 'head' becomes an isolated run).
//     private ListNode split(ListNode head, int size) {
//         if (head == null) return null; // nothing to split; next segment is null

//         ListNode curr = head;          // walk 'size - 1' steps (stop at segment tail)
//         for (int i = 1; i < size && curr.next != null; i++) {
//             curr = curr.next;          // advance within this segment
//         }

//         // 'curr' is now at the tail of the segment we want to isolate.
//         ListNode next = curr.next;     // this is the head of the next segment (may be null)
//         curr.next = null;              // cut: terminate this segment so it's an independent run
//         return next;                   // return head of the next segment
//     }

//     // Merge two sorted lists 'a' and 'b' into one sorted list.
//     // Returns an array {mergedHead, mergedTail} so the caller can quickly stitch and keep the tail.
//     private ListNode[] merge(ListNode a, ListNode b) {
//         // Dummy head to simplify merges; 't' trails and builds the merged list.
//         ListNode dummy = new ListNode(0), t = dummy;

//         // Standard two-pointer merge: pick the smaller head node, advance that list.
//         while (a != null && b != null) {
//             if (a.val <= b.val) { // '<=' preserves stability (a's equal elements come before b's)
//                 t.next = a;       // link 'a' node into merged list
//                 a = a.next;       // advance 'a'
//             } else {
//                 t.next = b;       // link 'b' node into merged list
//                 b = b.next;       // advance 'b'
//             }
//             t = t.next;           // advance tail of the merged list
//         }

//         // At least one list is exhausted; append the remainder of the other list.
//         t.next = (a != null) ? a : b;

//         // Move 't' to the true tail of the merged list (last node),
//         // so callers can attach subsequent runs in O(1).
//         while (t.next != null) t = t.next;

//         // Return both the head (dummy.next) and the tail (t) of the merged run.
//         return new ListNode[]{dummy.next, t};
//     }
// }