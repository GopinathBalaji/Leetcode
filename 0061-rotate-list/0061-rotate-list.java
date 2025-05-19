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

//  My attempt: Works only for 1 rotation
// class Solution {
//     public ListNode rotateRight(ListNode head, int k) {
//         if(head == null || head.next == null){
//             return head;
//         }

//         ListNode end = head;
//         ListNode start = head;

//         while(end.next.next != null){
//             end = end.next;
//         }

//         while(k != 0){
//             ListNode temp = end.next;
//             end.next = null;
//             temp.next = head;
//             head = temp;
//             k--;
//         }
//     }
// }

class Solution {
    public ListNode rotateRight(ListNode head, int k) {
        if(head == null){
            return head;
        }

        ListNode copy1 = head;
        int count = 0;

        while(copy1 != null){
            count++;
            copy1 = copy1.next;
        }

        k = k % count;

        if(k == 0){
            return head;
        }

        ListNode copy2 = head;

        while(copy2 != null){
            if(copy2.next == null){
                copy2.next = head;
                break;
            }
            copy2 = copy2.next;
        }

        int n = 0;

        ListNode tail = head;

        while(n != count - k - 1 && tail != null){
            n++;
            tail = tail.next;
        }

        ListNode newHead = tail.next;
        tail.next = null;

        return newHead;
    }
}