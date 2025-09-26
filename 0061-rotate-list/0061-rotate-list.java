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

// Method 1: My Reverse 3 times approach
/*
Reverse the complete list
Reverse the first portion again
Reverse the next portion again
*/
class Solution {
    public ListNode rotateRight(ListNode head, int k) {
        if(head == null){
            return head;
        }

        ListNode dummy = new ListNode(-1);
        dummy.next = head;

        int length = 0;
        ListNode ite1 = head;
        while(ite1 != null){
            ite1 = ite1.next;
            length++;
        }

        k = k % length;

        // First reversal of entire list
        ListNode prev = dummy;
        ListNode curr = prev.next;
        for(int i=0; i<length-1; i++){
            ListNode move = curr.next;
            curr.next = move.next;
            move.next = prev.next;
            prev.next = move;
        }

        // Second reversal of k nodes from the front
        curr = prev.next;
        for(int i=0; i<k-1; i++){
            ListNode move = curr.next;
            curr.next = move.next;
            move.next = prev.next;
            prev.next = move;
        }


        // Travel k nodes in the from first
        for(int i=0; i<k; i++){
            prev = prev.next;
        }

        // Reverse the remaining nodes after the first k nodes
        curr = prev.next;
        for(int i=0; i<length-k-1; i++){
            ListNode move = curr.next;
            curr.next = move.next;
            move.next = prev.next;
            prev.next = move;
        }


        return dummy.next;
    }
}


// Method 1.5: Cleaner 3 reversals method
/*
Rotate right by k equals: reverse all, reverse first k, reverse last n-k.
Use a helper to reverse the first m nodes of a list and return both the new head and the tail (so we can anchor safely).
*/
// class Solution {
//     public ListNode rotateRight(ListNode head, int k) {
//         if (head == null || head.next == null || k == 0) return head;

//         int n = 0; for (ListNode t = head; t != null; t = t.next) n++;
//         k %= n; if (k == 0) return head;

//         // reverse whole list
//         Pair p1 = reverseFirstM(head, n);      // p1.head = reversed head, p1.tail = original head

//         // reverse first k
//         Pair p2 = reverseFirstM(p1.head, k);   // p2.head = correct first-k after phase 2
//         // p2.tail is the tail of that k-block; connect its next to the remainder (already attached)

//         // reverse remaining n-k
//         Pair p3 = reverseFirstM(p2.tail.next, n - k);
//         // glue everything: p2.tail.next now points to p3.head
//         p2.tail.next = p3.head;

//         return p2.head;
//     }

//     // Reverse first m nodes of 'start'. Returns (newHead, newTail).
//     private Pair reverseFirstM(ListNode start, int m) {
//         if (m <= 1 || start == null) return new Pair(start, start);

//         ListNode prev = null, curr = start;
//         for (int i = 0; i < m; i++) {
//             ListNode nxt = curr.next;
//             curr.next = prev;
//             prev = curr;
//             curr = nxt;
//         }
//         // 'start' is now the tail; connect it to remainder 'curr'
//         start.next = curr;
//         return new Pair(prev, start);
//     }

//     private static class Pair {
//         ListNode head, tail;
//         Pair(ListNode h, ListNode t) { head = h; tail = t; }
//     }
// }





// Method 2: Make a ring, then break it (O(n) time, O(1) space)
/*
Idea:
Compute n and connect tail → head (make it circular).
Normalize k %= n.
New tail is the (n - k - 1)-th node from the head; new head is newTail.next.
Break the ring at newTail.

Walkthrough (1→2→3→4→5, k=2):
n=5, k=2. Ring: 1→2→3→4→5→(back to 1).
New tail is n-k-1 = 2 steps from head: start at 1 → 2 → 3 → stop at 3.
New head = 3.next = 4. Break after 3 → 4→5→1→2→null.
*/
// class Solution {
//     public ListNode rotateRight(ListNode head, int k) {
//         if (head == null || head.next == null || k == 0) return head;

//         // 1) compute length and get tail
//         int n = 1;
//         ListNode tail = head;
//         while (tail.next != null) {
//             tail = tail.next;
//             n++;
//         }

//         // 2) normalize k
//         k %= n;
//         if (k == 0) return head;

//         // 3) make it circular
//         tail.next = head;

//         // 4) find new tail: (n - k - 1) steps from head
//         int steps = n - k - 1;
//         ListNode newTail = head;
//         for (int i = 0; i < steps; i++) newTail = newTail.next;

//         // 5) new head is next; break the ring
//         ListNode newHead = newTail.next;
//         newTail.next = null;

//         return newHead;
//     }
// }



// Method 3: Two passes, split & stitch
/*
Idea:
First pass: length n. k %= n. If k==0, return head.
Second pass: move to the (n-k-1)-th node (newTail), cut after it, and stitch original tail to original head.

Walkthrough (1→2→3→4→5, k=3):
n=5, k=3. steps = 5-3-1 = 1.
Move 1 step: newTail = 2.
newHead = 3; cut after 2 → 1→2 and 3→4→5.
Stitch tail(=5).next = 1 → 3→4→5→1→2.
*/

// class Solution {
//     public ListNode rotateRight(ListNode head, int k) {
//         if (head == null || head.next == null || k == 0) return head;

//         // Pass 1: length and tail
//         int n = 1;
//         ListNode tail = head;
//         while (tail.next != null) { tail = tail.next; n++; }

//         k %= n;
//         if (k == 0) return head;

//         // Pass 2: find newTail at (n-k-1)
//         int steps = n - k - 1;
//         ListNode newTail = head;
//         for (int i = 0; i < steps; i++) newTail = newTail.next;

//         ListNode newHead = newTail.next; // (n-k)-th node
//         newTail.next = null;             // cut
//         tail.next = head;                // stitch tail to old head

//         return newHead;
//     }
// }
