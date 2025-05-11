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
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode ans = dummy;
        int carry = 0;
        int digit1 = 0;
        int digit2 = 0;
        int newDigit = 0;
        int sum = 0;

        while(l1 != null || l2 != null){
            if(l1 == null){
                digit1 = 0;
            }else{
                digit1 = l1.val;
            }

            if(l2 == null){
                digit2 = 0;
            }else{
                digit2 = l2.val;
            }

            sum = digit1 + digit2 + carry;

            newDigit = sum % 10;
            ans.next = new ListNode(newDigit);
            ans = ans.next;

            carry = sum / 10;

            if(l1 != null){
                l1 = l1.next;
            }

            if(l2 != null){
                l2 = l2.next;
            }

        }

        if (carry > 0) {
            ans.next = new ListNode(carry);
        }

        return dummy.next;
    }
}