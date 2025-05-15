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


//  Two-pass

// class Solution {
//     public ListNode removeNthFromEnd(ListNode head, int n) {
//         // Step 1: Add dummy node before head
//         ListNode dummy = new ListNode(0);
//         dummy.next = head;

//         // Step 2: First pass — count the total number of nodes
//         int count = 0;
//         ListNode current = head;
//         while (current != null) {
//             count++;
//             current = current.next;
//         }

//         // Step 3: Second pass — find the node just before the one to delete
//         ListNode prev = dummy;
//         for (int i = 0; i < count - n; i++) {
//             prev = prev.next;
//         }

//         // Step 4: Remove the nth node from the end
//         prev.next = prev.next.next;

//         // Step 5: Return the new head (might not be the same as input head)
//         return dummy.next;
//     }
// }

// ################################################

// One-pass
class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode slow = dummy;
        ListNode fast = dummy;

        for(int i=0;i<=n;i++){
            fast = fast.next;
        }

        while(fast != null){
            fast = fast.next;
            slow = slow.next;
        }

        slow.next = slow.next.next;

        return dummy.next;
    }
}