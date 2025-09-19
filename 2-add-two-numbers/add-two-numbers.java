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

// Method 1: My answer by iterating from the back
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    
        int result = 0;
        int carry = 0;
        ListNode dummyHead = new ListNode(-1);
        ListNode curr = dummyHead;

        while(l1 != null && l2 != null){
            result = l1.val + l2.val + carry;
            if(result >= 10){
                curr.next = new ListNode(result % 10);
                curr = curr.next;
                carry = 1;
            }else{
                curr.next = new ListNode(result);
                curr = curr.next;
                carry = 0;
            }

            l1 = l1.next;
            l2 = l2.next;
        }


        while(l1 != null){
            result = l1.val + carry;
            if(result >= 10){
                curr.next = new ListNode(result % 10);
                curr = curr.next;
                carry = 1;
            }else{
                curr.next = new ListNode(result);
                curr = curr.next;
                carry = 0;
            }

            l1 = l1.next;
        }


        while(l2 != null){
            result = l2.val + carry;
            if(result >= 10){
                curr.next = new ListNode(result % 10);
                curr = curr.next;
                carry = 1;
            }else{
                curr.next = new ListNode(result);
                curr = curr.next;
                carry = 0;
            }

            l2 = l2.next;
        }

        if(carry == 1){
            curr.next = new ListNode(1);
        }


        return dummyHead.next;
    }
}



// Method 1.5: Same as my answer but more clean
// class Solution {
//     public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
//         ListNode dummy = new ListNode(0), cur = dummy;
//         int carry = 0;

//         while (l1 != null || l2 != null || carry != 0) {
//             int x = (l1 != null) ? l1.val : 0;
//             int y = (l2 != null) ? l2.val : 0;
//             int sum = x + y + carry;

//             cur.next = new ListNode(sum % 10);
//             cur = cur.next;
//             carry = sum / 10;

//             if (l1 != null) l1 = l1.next;
//             if (l2 != null) l2 = l2.next;
//         }
//         return dummy.next;
//     }
// }






// Method 2: Using Recursion
/*
Answer is very clean with recursion because the lists are in **reverse order** (least-significant digit first). You can recurse from the heads forward, carrying the `carry` along.

# How the recursive approach works

* Let `helper(l1, l2, carry)` return the **sum list** for the sublists starting at `l1`, `l2`, plus an incoming `carry`.
* **Base case:** if `l1 == null`, `l2 == null`, and `carry == 0`, return `null` (no more digits to add).
* **Work:** sum the current digits (use `0` when a list is `null`) + `carry`.
  Create a node with `sum % 10`, and set `node.next = helper(l1?.next, l2?.next, sum / 10)`.
* This naturally handles different lengths and a final carry.


**Complexity:**

* Time `O(n + m)` — each node visited once.
* Space `O(n + m)` — recursion stack (plus result list). If you worry about very long lists, prefer the iterative version.

# Walkthrough (step-by-step)

Example: `l1 = [2,4,3]` (342), `l2 = [5,6,4]` (465) → expect `[7,0,8]` (807).

1. Call `add(2, 5, 0)` → `sum=7` → node `7`, recurse on `(4,6,0)`.
2. `add(4, 6, 0)` → `sum=10` → node `0`, recurse on `(3,4,1)`.
3. `add(3, 4, 1)` → `sum=8` → node `8`, recurse on `(null,null,0)`.
4. Base case → `null`.

Unwinding links: `7 -> 0 -> 8 -> null` ✅

Another edgey one: `l1 = [9,9,9,9]`, `l2 = [1]`

* `add(9,1,0)` → sum 10 → node 0, carry 1
* `add(9,null,1)` → sum 10 → node 0, carry 1
* `add(9,null,1)` → sum 10 → node 0, carry 1
* `add(9,null,1)` → sum 10 → node 0, carry 1
* `add(null,null,1)` → sum 1  → node 1, carry 0 → next is base case

Result: `[0,0,0,0,1]` ✅

---

If you ever get the **forward-order** variant (LeetCode 445), recursion flips: you first recurse to the ends (or pad to equal length), then add on the way **back**. But for LC 2 (reverse order), the above is the simplest recursive solution.

*/

// class Solution {
//     public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
//         return add(l1, l2, 0);
//     }

//     private ListNode add(ListNode a, ListNode b, int carry) {
//         if (a == null && b == null && carry == 0) return null;

//         int x = (a != null) ? a.val : 0;
//         int y = (b != null) ? b.val : 0;
//         int sum = x + y + carry;

//         ListNode node = new ListNode(sum % 10);
//         node.next = add(a != null ? a.next : null,
//                         b != null ? b.next : null,
//                         sum / 10);
//         return node;
//     }
// }