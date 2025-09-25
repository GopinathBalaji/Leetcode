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
                curr.next = null;
            }else{
                prev = prev.next;
            }
        }


        return dummy.next;
    }
}