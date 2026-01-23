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

// Method 1: Using Deque (O(n) space and O(n) time)
/*
# WHAT WAS I DOING WRONG:

Your idea (deque of nodes, then weave front/back) is fine, but this implementation has a **pointer-cycle / self-link bug** and it can also leave the list **not properly terminated**.

### 1) You can link a node to itself (creates a cycle)

Because you **don’t remove the front node from the deque**, the last poll can return the **same node as `copyHead2`** when you reach the middle.

Example: `1 -> 2 -> 3 -> 4`

Deque initially: `[1,2,3,4]`

* Iter 1 (`copyHead2=1`): `endNode=4`, do `1.next=4`, `4.next=2` ✅
  List so far: `1->4->2->3...`
* Iter 2 (`copyHead2=2`): deque is still `[1,2,3]` (you never removed 1 or 2)
  `endNode=3`, do `2.next=3`, and `3.next=next(=3)` → **3.next = 3** ❌ self-loop

That happens because `next = copyHead2.next` is `3`, and `endNode` is also `3`, so `endNode.next = next` makes a self-cycle.

Same issue shows up on odd lengths too (the middle node can become `endNode` while also being `next` or `copyHead2`).

### 2) You never explicitly break the tail

Even if you avoid self-links, you still need to ensure the final node’s `.next = null`. Otherwise you can keep old `.next` pointers and get cycles/extra nodes.

### 3) Logic issue: you should also consume from the front (or stop when pointers meet)

If you want to use a deque, you must take from **both ends** (`pollFirst` and `pollLast`) or carefully stop when you’ve woven enough nodes.

#################################################

We want to reorder:

`L0 → L1 → L2 → ... → Ln`
into
`L0 → Ln → L1 → Ln-1 → L2 → Ln-2 → ...`

The idea with a deque is simple: if we can pop from **front** and **back**, we can alternately append nodes in the exact required order.


## Why this works

### 1) Deque gives you both ends efficiently

* `pollFirst()` gives `L0, L1, L2, ...`
* `pollLast()` gives `Ln, Ln-1, Ln-2, ...`

Alternating those gives exactly:
`L0, Ln, L1, Ln-1, L2, Ln-2, ...`

### 2) You build the new order by rewiring `.next`

We maintain a `tail` pointer to the last node already placed in the new list:

* `tail.next = node` attaches the next chosen node
* `tail = node` advances the tail

This is like building a list in order, except we’re reusing existing nodes (no new list nodes except a dummy).

### 3) `tail.next = null` prevents cycles (super important)

Nodes still have their old `.next` pointers from the original list. If we don’t break the final node’s next pointer, the list may:

* keep pointing into old parts of the list, or
* form a cycle.

Setting `tail.next = null` guarantees the reordered list terminates.

---

## How it fixes your original bug

Your attempt only did `pollLast()` and never removed the front node. Eventually you can end up “linking” a node to itself (self-loop) or reusing nodes incorrectly.

Here, every node is removed exactly once from the deque (either from front or back), so:

* no node gets reused
* no self-links happen
* the pattern is correct by construction

---

# Thorough example walkthrough

## Example A: Even length

Input: `1 → 2 → 3 → 4`

### Step 0: Load into deque

Deque (front … back): `[1, 2, 3, 4]`

Initialize:

* `dummy -> null`
* `tail = dummy`
* `takeFront = true`

---

### Iteration 1 (takeFront = true)

* `node = pollFirst()` → `1`
* Attach: `tail.next = 1`, move `tail = 1`
* Flip: `takeFront = false`

Now:

* built list: `dummy → 1`
* deque: `[2, 3, 4]`

---

### Iteration 2 (takeFront = false)

* `node = pollLast()` → `4`
* Attach: `1.next = 4`, `tail = 4`
* Flip: `takeFront = true`

Now:

* built list: `dummy → 1 → 4`
* deque: `[2, 3]`

---

### Iteration 3 (takeFront = true)

* `node = pollFirst()` → `2`
* Attach: `4.next = 2`, `tail = 2`
* Flip: `takeFront = false`

Now:

* built list: `dummy → 1 → 4 → 2`
* deque: `[3]`

---

### Iteration 4 (takeFront = false)

* `node = pollLast()` → `3` (only element left)
* Attach: `2.next = 3`, `tail = 3`
* Flip: `takeFront = true`
* deque is empty

Built list currently: `dummy → 1 → 4 → 2 → 3 → (old next?)`

### Final termination step

`tail.next = null` → `3.next = null`

✅ Final: `1 → 4 → 2 → 3`

---

## Example B: Odd length

Input: `1 → 2 → 3 → 4 → 5`

Deque: `[1, 2, 3, 4, 5]`

We alternate:

1. pollFirst → `1`  → list: `1`
2. pollLast  → `5`  → list: `1 → 5`
3. pollFirst → `2`  → list: `1 → 5 → 2`
4. pollLast  → `4`  → list: `1 → 5 → 2 → 4`
5. pollFirst → `3`  → list: `1 → 5 → 2 → 4 → 3`

Then set `3.next = null`.

✅ Final: `1 → 5 → 2 → 4 → 3`

Notice the middle element `3` naturally lands at the end (as expected).

---

## Why `tail.next = null` is required (quick demo)

Suppose we didn’t do it in the odd example.

Originally node `3` had `3.next = 4` (old list). Even after we append `3` last, without nulling:

* the result would continue as `... → 3 → 4 → ...` and can create cycles/duplicates.

So that one line is the “clean cut” that prevents leftover wiring from the original list.

---

## Complexity

* Time: `O(n)` to load deque + `O(n)` to rebuild ⇒ **O(n)**
* Space: deque holds `n` nodes ⇒ **O(n)**

(LeetCode’s optimal solution uses O(1) extra space, but this deque solution is still valid and simpler.)

---

If you want, I can also give the **O(1) space** solution (find middle, reverse second half, merge alternately) with the same kind of step-by-step pointer walkthrough.

*/
class Solution {
    public void reorderList(ListNode head) {
        if(head == null || head.next == null){
            return;
        }

        Deque<ListNode> deque = new ArrayDeque<>();

        ListNode copyHead1 = head;
        while(copyHead1 != null){
            deque.addLast(copyHead1);
            copyHead1 = copyHead1.next;
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        boolean takeFront = true; // front, then back, then front, ...

        while(!deque.isEmpty()){
            ListNode node = takeFront ? deque.pollFirst() : deque.pollLast();
            tail.next = node;
            tail = node;
            takeFront = !takeFront;
        }

        tail.next = null; // critical: break old links

        // head is already modified in-place because we rewired nodes
    }
}





// Method 2: Optimal Two-Pointer Approach
/*
Below is the **O(1) extra space** approach for **LC 143: Reorder List**:

1. **Find the middle** of the list (slow/fast pointers)
2. **Reverse the second half**
3. **Merge** the two halves by alternating nodes

This is the standard optimal solution: **O(n) time, O(1) space**.


## Why this works

### Goal pattern

Reorder:
`L0 → L1 → L2 → ... → Ln`
to
`L0 → Ln → L1 → Ln-1 → L2 → Ln-2 → ...`

If we split the list into two halves:

* First half: `L0 → L1 → L2 → ...`
* Second half: `... → Ln-1 → Ln`

If we **reverse the second half**, it becomes:

* Reversed second half: `Ln → Ln-1 → Ln-2 → ...`

Now the reorder is just an **alternating merge** of:

* first half forward: `L0, L1, L2, ...`
* second half reversed: `Ln, Ln-1, Ln-2, ...`

---

## Step 1: Find the middle

Use slow/fast pointers:

* `slow` moves 1 step
* `fast` moves 2 steps

When `fast` reaches the end, `slow` is around the middle.

The loop condition:

```java
while (fast.next != null && fast.next.next != null)
```

makes `slow` stop at the “end of the first half”, which is perfect for splitting.

Then:

```java
second = slow.next;
slow.next = null;
```

cuts the list into two separate lists.

---

## Step 2: Reverse the second half

Standard iterative reversal turns:

`a → b → c → null` into `c → b → a → null`

This lets us pop the “largest remaining tail” (Ln, then Ln-1, …) from the front of the reversed list.

---

## Step 3: Merge alternating

At each step we connect:

* `first.next = second`  (place a tail element after a front element)
* `second.next = old first.next` (reconnect to continue)

We must store temporary next pointers (`t1`, `t2`) before rewiring so we don’t lose the rest.

---

# Thorough example walkthrough

## Example: `1 → 2 → 3 → 4 → 5`

### Step 1: Find middle

Start:

* `slow=1`, `fast=1`

Iteration 1:

* `slow=2`
* `fast=3`

Iteration 2:

* `slow=3`
* `fast=5`

Stop (because `fast.next == null`).

So:

* first half ends at `slow=3`

Split:

* first = `1 → 2 → 3 → null`
* second = `4 → 5 → null`

### Step 2: Reverse second half

Reverse `4 → 5 → null`:

* result: `5 → 4 → null`

Now:

* first = `1 → 2 → 3 → null`
* second = `5 → 4 → null`

### Step 3: Merge alternating

Let `first=1`, `second=5`

**Merge iteration 1**

* `t1 = first.next` = `2`
* `t2 = second.next` = `4`

Rewire:

* `first.next = second` → `1.next = 5`
* `second.next = t1` → `5.next = 2`

Now list looks like:
`1 → 5 → 2 → 3 → null` (3 is still connected from first half)

Advance:

* `first = t1` → `first=2`
* `second = t2` → `second=4`

**Merge iteration 2**

* `t1 = first.next` = `3`
* `t2 = second.next` = `null`

Rewire:

* `2.next = 4`
* `4.next = 3`

Now:
`1 → 5 → 2 → 4 → 3 → null`

Advance:

* `first = 3`
* `second = null` stop

✅ Final: `1 → 5 → 2 → 4 → 3`

---

## Example: `1 → 2 → 3 → 4`

### Step 1: find middle

* slow ends at `2`
  Split:
* first: `1 → 2 → null`
* second: `3 → 4 → null`

### Step 2: reverse second

* second becomes `4 → 3 → null`

### Step 3: merge

* `1 → 4 → 2`
* then `2 → 3`
  Result:
  ✅ `1 → 4 → 2 → 3`

---

## Complexity

* Finding middle: `O(n)`
* Reversing half: `O(n)`
* Merging: `O(n)`
  Total: **O(n) time**
  Extra space: **O(1)** (only pointers)
*/
// class Solution {
//     public void reorderList(ListNode head) {
//         if (head == null || head.next == null) return;

//         // 1) Find middle (slow will end at mid)
//         ListNode slow = head, fast = head;
//         while (fast.next != null && fast.next.next != null) {
//             slow = slow.next;
//             fast = fast.next.next;
//         }

//         // Split into two lists:
//         // first: head ... slow
//         // second: slow.next ... end
//         ListNode second = slow.next;
//         slow.next = null; // cut

//         // 2) Reverse second half
//         second = reverse(second);

//         // 3) Merge alternating
//         ListNode first = head;
//         while (second != null) {
//             ListNode t1 = first.next;
//             ListNode t2 = second.next;

//             first.next = second;
//             second.next = t1;

//             first = t1;
//             second = t2;
//         }
//     }

//     private ListNode reverse(ListNode head) {
//         ListNode prev = null, curr = head;
//         while (curr != null) {
//             ListNode next = curr.next;
//             curr.next = prev;
//             prev = curr;
//             curr = next;
//         }
//         return prev;
//     }
// }






// Method 3: Stack based approach (O(n) space)
/*
## Idea

We want:
`L0 → L1 → L2 → ... → Ln`
to become:
`L0 → Ln → L1 → Ln-1 → L2 → Ln-2 → ...`

A stack gives us **last-in-first-out**, so if we push all nodes onto a stack, popping gives us:
`Ln, Ln-1, Ln-2, ...`

Then we walk from the front with a pointer `cur` and weave in one node popped from the stack after each front node.

Key point: we only need to do this for `n/2` pops, otherwise we’d start undoing the work / create cycles.

Finally, we must set the tail’s `next = null` to terminate the list.


## Why `n/2` iterations is enough

Each iteration fixes **two positions** in the final ordering:

* It keeps one node from the front (current `cur`)
* It inserts one node from the back (`end`) right after it

After doing this `n/2` times, the reorder is complete.

If you kept going beyond `n/2`, you’d start popping nodes that are already part of the “front half” and you could create self-links/cycles.

---

## Why `cur.next = null` at the end is critical

Nodes you pop from the stack still have their **old `.next` pointers** from the original list.
If you don’t explicitly terminate the final node, the list might:

* continue into the old ordering, or
* form a cycle.

So `cur.next = null` is the safety cut.

---

# Thorough example walkthrough

## Example 1 (odd length)

Input: `1 → 2 → 3 → 4 → 5`

### Step 1: push onto stack

Stack top-to-bottom (top pops first):
`[5, 4, 3, 2, 1]`
`n = 5`, so we do `n/2 = 2` iterations.

`cur = 1`

---

### Iteration 1 (i=0)

* `end = pop()` → `5`
* `next = cur.next` → `2`

Rewire:

* `cur.next = end` → `1.next = 5`
* `end.next = next` → `5.next = 2`

Now list prefix:
`1 → 5 → 2 → 3 → 4 → ...`

Move:

* `cur = next` → `cur = 2`

---

### Iteration 2 (i=1)

* `end = pop()` → `4`
* `next = cur.next` → `3`

Rewire:

* `2.next = 4`
* `4.next = 3`

Now:
`1 → 5 → 2 → 4 → 3 → ...`

Move:

* `cur = 3`

---

### Finish (cut off)

After 2 iterations, stop weaving. Set:

* `cur.next = null` → `3.next = null`

Final:
✅ `1 → 5 → 2 → 4 → 3`

---

## Example 2 (even length)

Input: `1 → 2 → 3 → 4`

Stack: `[4, 3, 2, 1]`, `n=4`, `n/2=2`

`cur=1`

### Iteration 1

* pop `4`, `next=2`
* `1 → 4 → 2 → 3 ...`
* `cur=2`

### Iteration 2

* pop `3`, `next=3` (careful: cur.next currently points to 3)
* rewire:

  * `2.next = 3`
  * `3.next = next` (which is 3) would be a self-loop **if we weren’t stopping correctly**

But notice: in this even case, after iteration 1 the list is:
`1 → 4 → 2 → 3 → null` (3 is already last)

On iteration 2, `end` is exactly `next` (both are node 3). If you do `end.next = next`, you create `3.next = 3`.

So for even lengths, you must ensure you cut correctly. The simplest fix is to cut using the node after weaving, not `cur`. A robust way is:

* Track the node you just placed (`end`) and cut its `.next` after the loop.
* Or adjust termination to avoid the “end == next” situation.

### Robust stack version (avoids the self-loop)

Use a `tail` pointer and cut it at the end:

```java
import java.util.*;

class Solution {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) return;

        Deque<ListNode> st = new ArrayDeque<>();
        int n = 0;
        for (ListNode p = head; p != null; p = p.next) {
            st.push(p);
            n++;
        }

        ListNode cur = head;
        for (int i = 0; i < n / 2; i++) {
            ListNode end = st.pop();
            ListNode next = cur.next;

            cur.next = end;
            end.next = next;

            cur = next;
        }

        // 'cur' is at the middle (odd) or the start of the second half (even).
        // The node just before 'cur' in the reordered list should point to null.
        // We can safely cut by popping one more and nulling it, or just null 'cur.next'
        // after moving appropriately. The safest: walk to the end and cut once.
        // But that adds O(n). Instead, do a simple cut:
        if (cur != null) cur.next = null;
    }
}
```

However, as you saw, the naive `cur.next = null` can still be wrong depending on pointer state.

✅ The most reliable stack-based method is to **use the stack only for the second half** (push nodes from middle onward), which avoids ever popping a node that’s equal to `next`.

---

## Best stack-based approach (push only second half) ✅

This is stack-based, O(n) extra, but avoids self-loop pitfalls:

```java
import java.util.*;

class Solution {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) return;

        // 1) Find middle
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // 2) Push second half onto stack
        Deque<ListNode> st = new ArrayDeque<>();
        ListNode cur = slow.next;
        slow.next = null; // cut first half
        while (cur != null) {
            st.push(cur);
            cur = cur.next;
        }

        // 3) Weave
        cur = head;
        while (!st.isEmpty()) {
            ListNode end = st.pop();
            ListNode next = cur.next;

            cur.next = end;
            end.next = next;

            cur = next;
        }
    }
}
```

### Walkthrough for `1→2→3→4`

* middle at `2`, second half `3→4`
* stack top: `4,3`
* weave:

  * `1→4→2`
  * `2→3→null`
    Final: `1→4→2→3`

No chance for `end == next` anymore.

---

### Summary

* “Push all nodes” stack approach is doable but easy to get self-loops unless carefully handled.
* The clean stack-based approach is: **push only the second half**, then weave.
*/

// class Solution {
//     public void reorderList(ListNode head) {
//         if (head == null || head.next == null) return;

//         Deque<ListNode> stack = new ArrayDeque<>();
//         int n = 0;

//         // Push all nodes and count length
//         for (ListNode cur = head; cur != null; cur = cur.next) {
//             stack.push(cur);
//             n++;
//         }

//         ListNode cur = head;

//         // Weave in exactly n/2 nodes from the end
//         for (int i = 0; i < n / 2; i++) {
//             ListNode end = stack.pop();      // Ln, Ln-1, ...
//             ListNode next = cur.next;        // save L(i+1)

//             cur.next = end;                  // Li -> Lend
//             end.next = next;                 // Lend -> L(i+1)

//             cur = next;                      // advance to next front node
//         }

//         // Important: cut off tail to avoid cycle
//         cur.next = null;
//     }
// }






// Method 4: Recursion based approach
/*
Here’s a **recursive** solution for **LeetCode 143: Reorder List** that mirrors the “take from front + take from back” idea by using the **call stack to reach the tail**, then rewiring pointers while unwinding.

This is a common recursion pattern:

* A global pointer `left` walks from the front.
* The recursive parameter `right` walks to the end (via recursion).
* On the way back (unwinding), you stitch `left` with `right`.


### Complexity

* Time: **O(n)** (each node is handled a constant number of times)
* Extra space: **O(n)** due to recursion stack (this is the trade-off vs the O(1) iterative solution)

---

# Why this recursion works

## What recursion gives you for free

The recursion `reorderFromBack(head)` calls itself until `right` becomes the tail.

So the call stack naturally visits nodes in this order during unwinding:

**tail → ... → middle → ... → head**

That means when unwinding, you see `right` as:
`Ln, Ln-1, Ln-2, ...`

Meanwhile, you maintain `left` from the front:
`L0, L1, L2, ...`

So on each unwind step, you can connect:
`left -> right -> left.next`

Which is exactly the reorder pattern:
`L0 -> Ln -> L1 -> Ln-1 -> L2 -> Ln-2 ...`

---

## Why we need the stopping condition

Eventually, the front pointer and back pointer meet/cross.

Two cases:

### Odd length (meet at the same node)

Example: `1→2→3→4→5`

* At the center, both pointers hit node `3`
* Condition: `left == right`

### Even length (cross with adjacent nodes)

Example: `1→2→3→4`

* You eventually have `left` at `2` and `right` at `3`
* Condition: `left.next == right`

At that point, we must:

* set `right.next = null` to terminate properly
* stop further rewiring to avoid cycles

---

# Thorough example walkthrough (odd length)

Input: `1 → 2 → 3 → 4 → 5`

Let’s name nodes: `L0=1, L1=2, L2=3, L3=4, L4=5`

### Phase 1: recursion goes to the end

Calls go down like:

* `reorder(1)` → `reorder(2)` → `reorder(3)` → `reorder(4)` → `reorder(5)` → `reorder(null)`

Now we unwind from `right = 5, 4, 3, 2, 1`

Initial: `left = 1`

---

### Unwind step with `right = 5`

* `left = 1`, `right = 5`
* Not stopping (`left != right`, `left.next != right`)
* `leftNext = 2`
* Rewire: `1.next = 5`, `5.next = 2`
* Move `left = 2`

List now looks like:
`1 → 5 → 2 → 3 → 4 → ...` (rest still exists via original links)

---

### Unwind step with `right = 4`

* `left = 2`, `right = 4`
* `leftNext = 3`
* Rewire: `2.next = 4`, `4.next = 3`
* Move `left = 3`

Now:
`1 → 5 → 2 → 4 → 3 → ...`

---

### Unwind step with `right = 3`

* `left = 3`, `right = 3`
* Condition `left == right` hit (middle node)
* Set `right.next = null` → `3.next = null`
* `stop = true`

Final:
✅ `1 → 5 → 2 → 4 → 3 → null`

Further unwind steps (`right=2`, `right=1`) do nothing because `stop` is true.

---

# Thorough example walkthrough (even length)

Input: `1 → 2 → 3 → 4`

Unwind order: `right = 4, 3, 2, 1`
Start: `left = 1`

### Unwind with `right = 4`

* `leftNext = 2`
* `1.next = 4`, `4.next = 2`
* `left = 2`

Now: `1 → 4 → 2 → 3 → ...`

### Unwind with `right = 3`

* `left = 2`, `right = 3`
* Check stop: `left.next == right` is true (`2.next` is `3`)
* Set `3.next = null`
* stop

Final:
✅ `1 → 4 → 2 → 3 → null`

---

## Common pitfalls this code avoids

* **Cycles**: fixed by terminating at the meet/cross point with `right.next = null`
* **Over-rewiring**: prevented by the `stop` flag
* **Losing the next node**: prevented by saving `leftNext` before rewiring

---

If you want, I can also show a version that returns a boolean instead of using a `stop` flag, but the logic above is the cleanest to understand and debug.

*/
// class Solution {
//     private ListNode left;
//     private boolean stop;

//     public void reorderList(ListNode head) {
//         if (head == null || head.next == null) return;
//         left = head;
//         stop = false;
//         reorderFromBack(head);
//     }

//     private void reorderFromBack(ListNode right) {
//         if (right == null) return;

//         // Go all the way to the end
//         reorderFromBack(right.next);

//         if (stop) return;

//         // If pointers meet (odd length) or cross (even length), finish
//         if (left == right || left.next == right) {
//             right.next = null;  // terminate list
//             stop = true;
//             return;
//         }

//         // Stitch: left -> right -> leftNext
//         ListNode leftNext = left.next;
//         left.next = right;
//         right.next = leftNext;

//         // Advance left for the next unwinding step
//         left = leftNext;
//     }
// }
