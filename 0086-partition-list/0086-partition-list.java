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