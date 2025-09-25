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

// Method 1: My approach using two pointer method
/*
Maintain previous and current.
Move only current forward till all duplicates are found
Set previous.next to the node after all duplicates
*/
class Solution {
    public ListNode deleteDuplicates(ListNode head) {
        if(head == null){
            return head;
        }

        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        ListNode prev = dummy;

        while(prev != null){
            ListNode curr = prev.next;

            if(curr != null && curr.next != null && curr.val == curr.next.val){
                while(curr.next != null && curr.next.val == curr.val){
                    curr = curr.next;
                }
                prev.next = curr.next;
            }else{
                prev = prev.next;
            }
        }


        return dummy.next;
    }
}




// Method 1.5: More clean, better version but same idea
/*
### Why this works

* `prev` always points to the **last node we have decided to keep** (or `dummy` at start).
* `curr` scans forward:

  * If `curr` is **unique** (next is null or next value differs), we keep it: move `prev = curr`, `curr = curr.next`.
  * If `curr` starts a **duplicate run**, we record the duplicate value and advance `curr` until the value changes; then we connect `prev.next = curr` to drop the whole run.

No unnecessary rewiring (`curr.next = null`), no re-deriving `curr` from `prev` each loop, and the invariants are clear.

---

## Thorough example walkthrough

### Example 1

Input: `1 → 2 → 3 → 3 → 4 → 4 → 5`
Expected: `1 → 2 → 5`

Start:

```
dummy → 1 → 2 → 3 → 3 → 4 → 4 → 5
prev = dummy, curr = 1
```

* `curr=1`: next is 2, different → unique.
  keep it: `prev=1`, `curr=2`.

* `curr=2`: next is 3, different → unique.
  keep it: `prev=2`, `curr=3`.

* `curr=3`: next is 3, same → duplicate run of value 3.
  Skip all 3s: `curr` moves past both 3s → `curr=4`.
  Link past run: `prev.next = curr` → `2 → 4 …`

* `curr=4`: next is 4, same → duplicate run of value 4.
  Skip all 4s: `curr` moves past both 4s → `curr=5`.
  Link past run: `prev.next = 5` → `2 → 5`

* `curr=5`: next is null → unique.
  keep it: `prev=5`, `curr=null` → stop.

Result: `dummy.next = 1 → 2 → 5`.

### Example 2

Input: `1 → 1 → 1 → 2 → 3`
Expected: `2 → 3`

* `curr=1`: duplicate run (1s). Skip all 1s → `curr=2`.
  Link: `prev.next = 2` (prev is still `dummy`).

* `curr=2`: unique → keep (`prev=2`, `curr=3`).

* `curr=3`: unique → keep (`prev=3`, `curr=null`).
  Result: `2 → 3`.

### Example 3

Input: `1 → 2 → 2 → 2 → 3 → 3 → 4 → 4 → 5`
Expected: `1 → 5`

* `1` unique → keep (prev=1).
* `2` dup-run → skip all 2s, link `1.next = 3`.
* `3` dup-run → skip all 3s, link `1.next = 4`.
* `4` dup-run → skip all 4s, link `1.next = 5`.
* `5` unique → keep.
  Result: `1 → 5`.

---

## What to change in your code (minimal diffs)

* Track `curr` explicitly instead of recomputing `curr = prev.next` in each loop.
* Don’t set `curr.next = null` (not needed).
* Drive the loop by `while (curr != null)`, not `while (prev != null)`.
* When you skip a duplicate run, **do not move `prev`**; just link `prev.next = curr` (the first non-duplicate after the run).
*/

// class Solution {
//     public ListNode deleteDuplicates(ListNode head) {
//         if (head == null) return null;

//         ListNode dummy = new ListNode(0);
//         dummy.next = head;

//         ListNode prev = dummy;   // tail of the result list
//         ListNode curr = head;    // scanner

//         while (curr != null) {
//             boolean isDup = (curr.next != null && curr.val == curr.next.val);

//             if (!isDup) {
//                 // Unique node: keep it and advance both prev and curr
//                 prev = curr;
//                 curr = curr.next;
//             } else {
//                 // Duplicate run starting at curr; remember the value and skip all of them
//                 int dup = curr.val;
//                 while (curr != null && curr.val == dup) {
//                     curr = curr.next;  // skip entire block of dup values
//                 }
//                 // Link past the whole duplicate block
//                 prev.next = curr;
//                 // Note: do NOT advance prev here; we haven't kept any new node
//             }
//         }

//         return dummy.next;
//     }
// }
