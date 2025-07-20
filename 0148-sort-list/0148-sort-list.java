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

// Using Merge Sort since it does not require random access
// which makes it perfect for linked list sorting
/*


##  Explanation

* You **can't use extra memory** like converting the list to an array — violates the O(1) space constraint.
* But you can use **merge sort**, which:

  * Has **O(n log n)** time.
  * Works well on **linked lists** because it doesn't need random access.
  * Can be implemented with **constant extra space** (just pointer manipulation).

This makes **merge sort** the perfect choice.

---

##  Merge Sort for Linked List — Intuition

1. **Divide** the list into halves recursively using slow and fast pointers.
2. **Conquer** (sort) both halves recursively.
3. **Combine** (merge) the two sorted halves.

Because each step reduces the size by half and merging takes O(n), total time is O(n log n).

---

##  Visual Example

Input:

```
4 -> 2 -> 1 -> 3
```

Split:

```
[4 -> 2]    and    [1 -> 3]
```

Sort each:

```
[2 -> 4]    and    [1 -> 3]
```

Merge:

```
[1 -> 2 -> 3 -> 4]
```

---

##  Step-by-Step Algorithm

### 1. Split the list into two halves

Use the **slow and fast pointer** approach to find the midpoint:

* `slow` moves one step at a time.
* `fast` moves two steps.
* When `fast` hits the end, `slow` is at the midpoint.

Break the list at midpoint into two parts.

### 2. Sort both halves recursively

```java
ListNode left = sortList(head);
ListNode right = sortList(mid);
```

### 3. Merge the two sorted halves

Standard merge routine:

* Use a dummy node and build the sorted list by comparing values.


##  Time and Space Complexity

| Resource       | Complexity | Why                                         |
| -------------- | ---------- | ------------------------------------------- |
| **Time**       | O(n log n) | log n levels of recursion, O(n) merge each  |
| **Space**      | O(log n)   | due to recursion stack (not heap space)     |
| **Heap Space** | O(1)       | We don't create extra nodes; reuse pointers |

---

##  Common Mistakes

| Mistake                                             | Fix                       |
| --------------------------------------------------- | ------------------------- |
| Forgetting to break list at mid                     | Use `prev.next = null`    |
| Using `fast != null` instead of `fast.next != null` | Always check `fast.next`  |
| Not handling `null` or `1-node` list                | Add base case check early |

---

##  Summary

* Use **merge sort** for linked list due to its natural fit and O(n log n) time.
* Use **slow/fast pointers** to find the middle and split the list.
* Use a **dummy node** to merge sorted lists cleanly.
* Maintain **O(1) extra space** by only using pointer operations.

Let me know if you want a dry-run on an example like `[5, 1, 3, 2]` or a visual trace!

*/
class Solution {
    public ListNode sortList(ListNode head) {
        if(head == null || head.next == null){
            return head;
        }

        ListNode slow = head;
        ListNode fast = head;
        ListNode prev = null;

        while(fast != null && fast.next != null){
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        if(prev != null){
            prev.next = null;
        }
        
        ListNode left = head;
        ListNode right = slow;

        left = sortList(left);
        right = sortList(right);


        return merge(left, right);
    }

    public ListNode merge(ListNode l1, ListNode l2){
        ListNode dummy = new ListNode(-1);
        ListNode tail = dummy;

        while(l1 != null && l2 != null){
            if(l1.val < l2.val){
                tail.next = l1;
                l1 = l1.next;
            }else{
                tail.next = l2;
                l2 = l2.next;
            }

            tail = tail.next;
        }

        tail.next = (l1 != null) ? l1 : l2;

        return dummy.next;
    }
}