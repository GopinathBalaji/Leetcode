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

// Method 1: Single pass fast and slow pointer
/*
Why this works:
After the initial n-step advance, the gap between fast and slow is n.
When fast reaches the end, slow is exactly one node before the one to delete.
slow.next = slow.next.next removes the target in O(1).


# Example Walkthrough
We use the **dummy** technique and the condition `while (fast.next != null)`:

* List: `0(dummy) → 1 → 2 → 3 → 4 → 5`
* `n = 2`
* Start: `fast = dummy`, `slow = dummy`
* Advance `fast` by `n` steps:

  * step A1: `fast = 1`
  * step A2: `fast = 2`

Now move both until `fast.next == null`:

| Move | fast goes to | slow goes to |                                         |
| ---- | ------------ | ------------ | --------------------------------------- |
| M1   | 3            | 1            |                                         |
| M2   | 4            | 2            |                                         |
| M3   | 5            | 3            | ← stop here because `fast.next == null` |

At the stop:

* `slow = 3`
* So `slow.next = 4` (the node to delete)
* Do `slow.next = slow.next.next` → `slow.next = 5`
* Result: `1 → 2 → 3 → 5`

So the correct final position is **`slow = 3`**, not `2`.
That’s why the deletion targets node `4`, not `3`.

If you instead stopped when `fast == null` (a different loop condition some people use), you’d start with a slightly different setup (e.g., `while (fast != null) { fast = fast.next; slow = slow.next; }` with `fast` initially `n` ahead of `slow` but starting from `head` vs `dummy`). The version shown above (with `dummy` + `while (fast.next != null)`) lands `slow` exactly on the node **before** the one to remove.
*/
class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        // fast starts n steps ahead of slow
        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i < n; i++) {
            fast = fast.next; // assume 1 <= n <= length
        }

        // move both until fast is at the last node
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }

        // delete the nth from end: slow is before the target
        slow.next = slow.next.next;

        return dummy.next;
    }
}



// Method 2: Two pass method using length of the linked list
/*
### Why this works

* First pass gets the list length `len`.
* The target node (nth from end) is position `len - n + 1` from the front (1-indexed).
* We walk `len - n` steps from `dummy` to land on `prev` (the node **before** the target), then splice it out with `prev.next = prev.next.next`.

### Complexity

* **Time:** $O(L)$ — two linear passes over the list.
* **Space:** $O(1)$ — only a few pointers and counters.

---

## Detailed example walkthrough

**Input:** `1 → 2 → 3 → 4 → 5`, `n = 2`
Goal: remove the 2nd from the end → node `4`.

**Pass 1 — Count length**

* Traverse: count = 5.

**Compute steps**

* `steps = len - n = 5 - 2 = 3`.
* We want to move **3 steps from `dummy`** to reach `prev` (just before the node to delete).

**Pass 2 — Walk to `prev`**

* Start: `dummy → 1 → 2 → 3 → 4 → 5`
* Step 1: `prev = 1`
* Step 2: `prev = 2`
* Step 3: `prev = 3`
  Now `prev.next` is `4` (the node to remove), and `prev.next.next` is `5`.

**Delete**

* Do `prev.next = prev.next.next` → link `3` directly to `5`.

**Result**

* List becomes: `1 → 2 → 3 → 5`.

---

## Edge cases to keep in mind

* **Remove head** (`n == len`):

  * Then `steps = 0`, `prev` stays at `dummy`, and `prev.next = prev.next.next` removes the original head correctly.
* **Single node list** (`[x]`, `n=1`):

  * `len=1`, `steps=0`, `dummy.next` becomes `null` → returns empty list `[]`.
* **`n` within bounds (LeetCode guarantees it)**: If you were writing defensive code, you could early-return if `n <= 0` or `n > len`.
*/

// class Solution {
//     public ListNode removeNthFromEnd(ListNode head, int n) {
//         // Dummy simplifies removing the head when n == length
//         ListNode dummy = new ListNode(0);
//         dummy.next = head;

//         // PASS 1: compute length L
//         int len = 0;
//         for (ListNode cur = head; cur != null; cur = cur.next) {
//             len++;
//         }

//         // Node to remove is the (len - n + 1)-th from the start (1-indexed).
//         // We want 'prev' to stand just before it.
//         int steps = len - n; // number of steps from dummy to 'prev'

//         ListNode prev = dummy;
//         for (int i = 0; i < steps; i++) {
//             prev = prev.next;
//         }

//         // Delete: prev -> (skip) -> prev.next.next
//         prev.next = prev.next.next;

//         return dummy.next;
//     }
// }