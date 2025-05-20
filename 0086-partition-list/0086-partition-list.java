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

        ListNode lesser = new ListNode(-101);
        ListNode cpyLe = lesser;
        ListNode greaterEq = new ListNode(101);
        ListNode cpyGe = greaterEq;

        ListNode copy1 = head;

        while(copy1 != null){
            if(copy1.val < x){
                lesser.next = copy1;
                lesser = lesser.next;
            }else{
                greaterEq.next = copy1;
                greaterEq = greaterEq.next;
            }

            copy1 = copy1.next;
        }

        greaterEq.next = null;

        lesser.next = cpyGe.next;

        return cpyLe.next;
    }
}