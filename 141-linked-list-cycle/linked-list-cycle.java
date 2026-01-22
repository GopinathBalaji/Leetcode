/**
 * Definition for singly-linked list.
 * class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */


// Method 1: O(n) time, O(n) space by using Set to check duplicates
/*
*/
public class Solution {
    public boolean hasCycle(ListNode head) {
        if (head == null) return false;

        Set<ListNode> seen = new HashSet<>();
        ListNode cur = head;
        while (cur != null) {
            if (seen.contains(cur)) return true; // revisiting the same node => cycle
            seen.add(cur);
            cur = cur.next;
        }
        return false; // hit null => no cycle
    }
}






// Method 2: Slow and Fast pointer
// NOTE: The nodes eventually meet not necessarily at the cycle start location 
// but any location
/*
A -> B -> C -> D
      ^         |
      |_________|

| Iteration | slow moves to | fast moves to        | Meet?   |
| --------- | ------------- | -------------------- | ------- |
| 1         | `B`           | `C`  (`A -> B -> C`) | No      |
| 2         | `C`           | `B`  (`C -> D -> B`) | No      |
| 3         | `D`           | `D`  (`B -> C -> D`) | **Yes** |


1. **Iter 1**

   * `slow = A.next = B`
   * `fast = A.next.next = C`
     Not equal.

2. **Iter 2**

   * `slow = B.next = C`
   * `fast` was at `C`, so `fast = C.next.next = D.next = B`
     Not equal.

3. **Iter 3**

   * `slow = C.next = D`
   * `fast` was at `B`, so `fast = B.next.next = C.next = D`
     Now `slow == fast == D` → **cycle detected**, return `true`.

They meet **inside** the cycle (at `D` here), not necessarily at the cycle’s start.

## Why they must meet (intuition)

* Distance from head to cycle start is `L = 1` (`A -> B`).
  After 1 step, `slow` enters the cycle. `fast` enters immediately too (it skipped to `C` on step 1).
* Cycle length is `C = 3` (`B, C, D`).
  Once both are in the cycle, the relative speed is `2 − 1 = 1` node per iteration; so `fast` gains 1 node per loop and must catch `slow` within at most `C` iterations. In our trace, it happens in the 3rd loop.
*/

// public class Solution {
//     public boolean hasCycle(ListNode head) {
//         if (head == null || head.next == null) return false;
//         ListNode slow = head, fast = head;
//         while (fast != null && fast.next != null) {
//             slow = slow.next;
//             fast = fast.next.next;
//             if (slow == fast) return true; // pointers meet => cycle
//         }
//         return false; // fast hit null => no cycle
//     }
// }
