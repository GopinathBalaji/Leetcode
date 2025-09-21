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

// Method 1: My answer by creating new nodes
class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(-1);
        ListNode curr = dummy;

        while(list1 != null && list2 != null){
            if(list1.val <= list2.val){
                curr.next = new ListNode(list1.val);
                curr = curr.next;
                list1 = list1.next;
            }else{
                curr.next = new ListNode(list2.val);
                curr = curr.next;
                list2 = list2.next;
            }
        }

        while(list1 != null){
            curr.next = new ListNode(list1.val);
            curr = curr.next;
            list1 = list1.next;
        }

        while(list2 != null){
            curr.next = new ListNode(list2.val);
            curr = curr.next;
            list2 = list2.next;
        }

        return dummy.next;
    }
}


// Method 1.5: Same answer without creating new nodes
// class Solution {
//     public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
//         ListNode dummy = new ListNode(0);
//         ListNode cur = dummy;

//         while (list1 != null && list2 != null) {
//             if (list1.val <= list2.val) {
//                 cur.next = list1;          // reuse node
//                 list1 = list1.next;
//             } else {
//                 cur.next = list2;          // reuse node
//                 list2 = list2.next;
//             }
//             cur = cur.next;
//         }

//         // attach the leftover list (one of them is null)
//         cur.next = (list1 != null) ? list1 : list2;

//         return dummy.next;
//     }
// }
