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

// Method 1: My answer maintaining 2 seperate lists and then connecting the second list to the first
class Solution {
    public ListNode partition(ListNode head, int x) {
        if(head == null){
            return head;
        }

        ListNode dummy1 = new ListNode(-1);
        dummy1.next = head;
        ListNode curr1 = dummy1;

        ListNode dummy2 = new ListNode(-2);
        // dummy2.next = head;
        ListNode curr2 = dummy2;

        ListNode dummy3 = new ListNode(-3);
        // dummy3 = head;
        ListNode curr3 = dummy3;

        while(curr1.next != null){
            if(curr1.next != null && curr1.next.val < x){
                curr2.next = curr1.next;
                curr2 = curr2.next;
                curr1 = curr1.next;
            }else if(curr1.next != null && curr1.next.val >= x){
                curr3.next = curr1.next;
                curr3 = curr3.next;
                curr1 = curr1.next;
            }

            // curr1 = curr1.next;
        }

        curr3.next = null;
        curr2.next = dummy3.next;

        return dummy2.next;
    }
}




// Method 1.5: Cleaner version of my answer
/*
Idea: 
Walk once. 
For each node, detach it, append to one of two tails (< x or >= x). 
Finally, connect the two lists.
*/
// class Solution {
//     public ListNode partition(ListNode head, int x) {
//         if (head == null) return null;

//         ListNode lessDummy = new ListNode(0), lessTail = lessDummy;
//         ListNode geDummy   = new ListNode(0), geTail   = geDummy;

//         ListNode curr = head;
//         while (curr != null) {
//             ListNode next = curr.next; // save next BEFORE detaching
//             curr.next = null;          // detach current node from original list

//             if (curr.val < x) {
//                 lessTail.next = curr;
//                 lessTail = curr;
//             } else {
//                 geTail.next = curr;
//                 geTail = curr;
//             }
//             curr = next; // advance
//         }

//         // stitch: all <x followed by all >=x
//         lessTail.next = geDummy.next;
//         return lessDummy.next;
//     }
// }





// Method 2: In-place “stable splice” (no second list allocation)
/*
Idea: 
Keep a lessTail that marks the end of the < x segment inside the original list. 
Scan with prev/curr. When you find a < x that’s not already right after lessTail, splice it out from after prev and insert it after lessTail. 
This preserves order.

Why it’s correct:
Moves each < x node forward once, preserving the encounter order among < x.
Nodes >= x remain in relative order because we only move < x nodes across them, keeping the scan stable.

Walkthrough (1→4→3→2→5→2, x=3):
lessTail at dummy, scan 1 (<3) and is right after lessTail: move lessTail=1, advance.
4 (≥3): just advance.
3 (≥3): advance.
2 (<3) and not right after lessTail:
Splice 2 out (from after 3), insert after 1: list becomes 1→2→4→3→5→2; lessTail=2; curr continues at node after 3 → 5.
5 (≥3): advance.
Last 2 (<3) not right after lessTail:
Splice it out (after 5), insert after 2: list becomes 1→2→2→4→3→5.
Done.
*/
// class Solution {
//     public ListNode partition(ListNode head, int x) {
//         if (head == null) return null;

//         ListNode dummy = new ListNode(0);
//         dummy.next = head;

//         // lessTail: last node in the <x region
//         // prev: node before curr (scanning pointer)
//         ListNode lessTail = dummy, prev = dummy, curr = head;

//         while (curr != null) {
//             if (curr.val < x) {
//                 if (lessTail.next == curr) {
//                     // already immediately after lessTail → advance all
//                     lessTail = curr;
//                     prev = curr;
//                     curr = curr.next;
//                 } else {
//                     // splice curr out after prev
//                     prev.next = curr.next;
//                     // insert curr after lessTail
//                     curr.next = lessTail.next;
//                     lessTail.next = curr;
//                     // advance lessTail to the newly inserted node
//                     lessTail = curr;
//                     // curr should now be prev.next for next iteration
//                     curr = prev.next;
//                 }
//             } else {
//                 // keep in >=x region; just advance scan
//                 prev = curr;
//                 curr = curr.next;
//             }
//         }
//         return dummy.next;
//     }
// }
