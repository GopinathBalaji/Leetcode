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


// Mehtod 1: Do k head insertions per iteration for reversal
/*
# WHAT WAS I DOING WRONG:

Two main issues (one **logic / off-by-one**, one **missing reconnection**) make this incorrect.

---

## 1) Your `kth` finder is off by one (it lands on the (k+1)-th node)

You start with `i = -1` and loop while `i < k`, incrementing `i` **before** moving `kth`:

```java
int i = -1;
ListNode kth = groupPrev;
while (kth != null && i < k) {
    i++;
    kth = kth.next;
}
```

For example, if `k = 2`:

* start: `i=-1`, `kth=groupPrev`
* iter1: `i=0`, `kth=1st node`
* iter2: `i=1`, `kth=2nd node`
* iter3: `i=2`, `kth=3rd node`  ✅ loop stops here

So `kth` ends up at the **3rd node**, not the 2nd.
That means you reverse **k+1 nodes** (until `groupNext`) instead of k.

**Fix:** move exactly `k` steps from `groupPrev`:

```java
ListNode kth = groupPrev;
for (int i = 0; i < k && kth != null; i++) kth = kth.next;
if (kth == null) break;
```

---

## 2) You never reconnect the reversed group back to the previous part

After the reversal loop:

* `prev` is the **new head** of the reversed group
* `newGroupTail` is the **old head** (now the tail)

But you never do:

```java
groupPrev.next = prev;
```

So the list before the group still points to the **old head**, and your result is wrong (e.g., `dummy.next` never changes from the original head).

#################################
# Explanation:
Detaching the moved node: curr.next = move.next; prevents cycles and keeps the rest of the list reachable.
Exactly k-1 moves: Head-inserting k-1 nodes reverses a k-node block; doing k would overrun/NullPointer.
Correct stitching: Tail (curr) connects to nextGroupHead. prev connects to the new head via the head-insertions.
Prev advancement: After reversing, the group’s original head (curr) is now the tail; set prev = curr.

## Thorough example walkthrough

**Input:** `1 → 2 → 3 → 4 → 5`, `k = 3`
Goal: reverse in groups of 3 → `3 → 2 → 1 → 4 → 5`

Initialize:

```
dummy → 1 → 2 → 3 → 4 → 5
prev = dummy
```

### Round 1: reverse the first 3 nodes

1. **Find kth** node starting from `prev`:

   * start at `prev = dummy`, walk 3 steps: `kth = 3`
   * `nextGroupHead = kth.next = 4`
   * `curr = prev.next = 1`

2. **Head-insert (k-1 = 2 times)**

* **Iteration i=1**

  * `move = curr.next = 2`
  * Detach: `curr.next = move.next` → `1.next = 3`
  * Insert at front:
    `move.next = prev.next` → `2.next = 1`
    `prev.next = move` → `dummy.next = 2`
  * List now: `dummy → 2 → 1 → 3 → 4 → 5`
  * `curr` remains `1` (tail of the partially reversed block)

* **Iteration i=2**

  * `move = curr.next = 3`
  * Detach: `curr.next = move.next` → `1.next = 4`
  * Insert at front:
    `move.next = prev.next` → `3.next = 2`
    `prev.next = move` → `dummy.next = 3`
  * List now: `dummy → 3 → 2 → 1 → 4 → 5`

3. **Stitch tail to next group**

   * `curr` is the original head `1`, now tail of the reversed block
   * `curr.next = nextGroupHead` → `1.next = 4` (already true after detaches; this line ensures it)
   * Advance `prev = curr` → `prev = 1`

State after Round 1:

```
dummy → 3 → 2 → 1(prev) → 4 → 5
```

### Round 2: try to reverse the next 3 nodes

1. **Find kth** from `prev=1`:

   * steps: 1→`4`, 2→`5`, 3→`null` → fewer than k nodes
   * Stop. No more reversals.

**Result:** `3 → 2 → 1 → 4 → 5` ✅

---

## Complexity

* Each node is moved/relabeled a constant number of times: **O(n)** time.
* Only constant extra pointers: **O(1)** space.

---

### Common pitfalls (all present in your original attempt)

* Forgetting `curr.next = move.next` (detaching the moved node).
* Doing `k` head-insertions instead of `k-1`.
* Not tracking `nextGroupHead` (right boundary) to stitch back.
* Advancing `prev` incorrectly (should become the **tail** of the reversed block).

If you prefer a slightly different pattern, an alternative is to reverse the `[groupStart..kth]` segment with the standard 3-pointer reversal using `nextGroupHead` as a sentinel. But the head-insertion version above is concise and fast for interviews.
*/
class Solution {
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k <= 1) return head;

        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        ListNode prev = dummy; // prev stands BEFORE the group to reverse

        while (true) {
            // 1) Check there are k nodes ahead: [prev.next ... kth]
            ListNode kth = prev;
            for (int i = 0; i < k && kth != null; i++) {
                kth = kth.next;
            }
            if (kth == null) break; // fewer than k nodes remain → done

            // 2) Reverse the k block by head insertion relative to 'prev'
            ListNode curr = prev.next;      // first node in this group
            ListNode nextGroupHead = kth.next; // node after the group

            // perform (k-1) head insertions:
            // repeatedly move 'move = curr.next' to the front (right after prev)
            for (int i = 1; i < k; i++) {
                ListNode move = curr.next;          // node to move
                curr.next = move.next;               // detach 'move' from after curr
                move.next = prev.next;               // link move before current group head
                prev.next = move;                    // move now becomes new head of the group
            }

            // 3) Now 'curr' is the tail of the reversed group; attach it to nextGroupHead
            curr.next = nextGroupHead;

            // 4) Advance prev to the tail (which is 'curr') for next round
            prev = curr;
        }

        return dummy.next;
    }
}



// Method 2: Single and Double Recursive version
/*
NOTE: For reversing a bath of k nodes, in the double-recursive solution "recursing and reversing from 
tail" method is used

Following solution is recursive in both senses:
it processes the list group-by-group via recursion, and
it reverses the first k nodes recursively as well (no loops needed).


Here’s a clean **recursive** solution for LeetCode 25 (Reverse Nodes in k-Group) that is recursive in **both** senses:

1. it processes the list group-by-group via recursion, and
2. it reverses the first k nodes **recursively** as well (no loops needed).

### Why this works

* We first **verify** there are at least `k` nodes; if not, we return `head` unchanged.
* `reverseFirstKRec(head, k)` reverses exactly `k` nodes (pure recursion).

  * Base `k==1`: sever after the k-th node and return that node as the new head of this block.
  * On unwinding, each caller points `head.next.next = head` to flip the edge.
* After reversing the first k nodes, the original `head` becomes the **tail** of this k-block. We connect that tail to the recursively processed remainder.

**Complexities**

* Time: **O(n)** (each node touched O(1) times)
* Extra space: **O(n/k)** call stack depth for the group recursion + **O(k)** for the inner reverse (worst-case stack depth = O(n) if k is large, but typical constraints are fine)

---

## 🧭 Example walkthrough (1→2→3→4→5→6→7, k=3)

Goal: `3→2→1→6→5→4→7`

### First call: `reverseKGroup(1, k=3)`

* Check 3 nodes exist (1,2,3): yes → proceed.
* `reverseFirstKRec(1,3)`:

  * `reverseFirstKRec(2,2)`:

    * `reverseFirstKRec(3,1)` → base: returns `(newHead=3, nextStart=4)`, and cuts `3→null`
    * Unwind at node 2:

      * `2.next.next = 2` → `3.next = 2`
      * `2.next = null` → cut after 2
      * returns `(newHead=3→2→null, nextStart=4)`
    * Unwind at node 1:

      * `1.next.next = 1` → `2.next = 1`
      * `1.next = null`
      * returns `(newHead=3→2→1→null, nextStart=4)`
* Now stitch: original head `1` (tail of this block) connects to recursion of the rest:

  * `1.next = reverseKGroup(4,3)`

### Second call: `reverseKGroup(4,3)`

* Check 3 nodes exist (4,5,6): yes → proceed.
* Reverse first 3: `reverseFirstKRec(4,3)` returns `(6→5→4, nextStart=7)`
* Stitch: `4.next = reverseKGroup(7,3)`

### Third call: `reverseKGroup(7,3)`

* Check 3 nodes exist (7, null, …): fail → return `7` unchanged.

### Stitching back

* Second call returns `6→5→4→7`
* First call returns `3→2→1→(6→5→4→7)`

**Final:** `3 → 2 → 1 → 6 → 5 → 4 → 7`

---

## (Alt) Same recursion across groups, with an **iterative** k-reverse

Some prefer to keep the group recursion but reverse k nodes with a tiny loop:

```java
class Solution {
    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k <= 1) return head;

        // ensure k nodes exist
        ListNode check = head;
        for (int i = 0; i < k; i++) {
            if (check == null) return head;
            check = check.next;
        }

        // reverse first k nodes iteratively
        ListNode prev = null, curr = head;
        for (int i = 0; i < k; i++) {
            ListNode nxt = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nxt;
        }
        // head is now the tail of this block; connect to recursively processed rest
        head.next = reverseKGroup(curr, k);
        return prev; // new head of the reversed k-block
    }
}
```

Both versions are accepted; pick the style you like. The fully recursive one mirrors the problem statement nicely and is a great practice for pointer recursion.
*/

// class Solution {
//     public ListNode reverseKGroup(ListNode head, int k) {
//         if (head == null || k <= 1) return head;

//         // 1) Check we have at least k nodes starting from head
//         ListNode check = head;
//         for (int i = 0; i < k; i++) {
//             if (check == null) return head; // fewer than k: leave as-is
//             check = check.next;
//         }

//         // 2) Recursively reverse the first k nodes.
//         //    reverseK returns {newHeadOfReversed, firstNodeAfterTheReversedBlock}
//         Pair p = reverseFirstKRec(head, k);

//         // 3) 'head' is now the tail of the reversed block; attach the rest (recursively processed)
//         head.next = reverseKGroup(p.nextStart, k);

//         // 4) Return the new head of this reversed k-block
//         return p.newHead;
//     }

//     // Helper holder
//     private static class Pair {
//         ListNode newHead;
//         ListNode nextStart;
//         Pair(ListNode h, ListNode n) { newHead = h; nextStart = n; }
//     }

//     // Recursively reverse exactly k nodes starting at 'head'.
//     // Precondition: there ARE at least k nodes available.
//     // Returns (newHead of reversed block, pointer to node after the block).
//     private Pair reverseFirstKRec(ListNode head, int k) {
//         if (k == 1) {
//             // Base: cut after the kth node and return
//             ListNode next = head.next; // node after the k-block
//             head.next = null;          // detach k-block
//             return new Pair(head, next);
//         }
//         // Reverse the tail of the block [head.next .. kth]
//         Pair tail = reverseFirstKRec(head.next, k - 1);

//         // Append current head to the reversed tail
//         head.next.next = head;
//         head.next = null;

//         // newHead is tail.newHead, and the node after the block is tail.nextStart
//         return new Pair(tail.newHead, tail.nextStart);
//     }
// }
