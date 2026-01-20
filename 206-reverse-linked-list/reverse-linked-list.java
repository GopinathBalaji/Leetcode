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


// Method 1: Iterative method
/*
*/
class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode copyHead = head;
        ListNode dummy = null;

        ListNode prev = dummy;
        ListNode next;
        
        while(copyHead != null){
            next = copyHead.next;
            copyHead.next = prev;
            prev = copyHead;
            copyHead = next;
        }

        
        return prev;
    }
}






// Method 2: Head Recursive method
/*
## What the parameters mean

At any call `reverse(prev, curr)`:

* `prev` = the head of the **already reversed** part (left side)
* `curr` = the head of the **not-yet-processed** part (right side)

Visually, the list is conceptually split like this:

```
(reversed part)     (remaining part)
prev  -> ...        curr -> curr.next -> ...
```

The goal is to move nodes one-by-one from the remaining part into the reversed part.

---

## The invariant (what stays true every time)

Before each recursive call:

* All nodes **before** `curr` have been reversed and are reachable from `prev`.
* `curr` is the next node we still need to reverse.
* No nodes are lost because we store `curr.next` in `next` before rewiring.

---

## What happens in one recursion step

Inside `reverse(prev, curr)` we do:

1. `next = curr.next`

   * Save where the rest of the list continues (so we don’t lose it after rewiring)

2. `curr.next = prev`

   * Reverse the pointer for the current node

3. Recurse with:

   * new `prev = curr` (current node becomes the new head of reversed part)
   * new `curr = next` (continue with the remaining part)

So the call:

```java
return reverse(curr, next);
```

means: “I’ve reversed `curr`, now keep going and eventually return the final head.”

---

## Why the base case returns `prev`

When `curr == null`, it means we ran past the end of the original list. At that moment:

* All nodes have been reversed.
* `prev` is pointing to the new head of the reversed list.

---

## Thorough example walkthrough

Take the list:

```
1 -> 2 -> 3 -> null
```

We call:

```
reverseList(1) -> reverse(null, 1)
```

### Call 1: reverse(prev=null, curr=1)

**State before rewiring:**

* prev: `null`
* curr: `1 -> 2 -> 3 -> null`

Do:

* `next = curr.next` → `next = 2`
* `curr.next = prev` → `1.next = null`

Now pointers look like:

* Reversed part: `1 -> null`
* Remaining part (still saved via `next`): `2 -> 3 -> null`

Recurse:

```
reverse(prev=1, curr=2)
```

---

### Call 2: reverse(prev=1, curr=2)

**State:**

* prev: `1 -> null`
* curr: `2 -> 3 -> null`

Do:

* `next = 3`
* `2.next = 1`

Now:

* Reversed part: `2 -> 1 -> null`
* Remaining part: `3 -> null`

Recurse:

```
reverse(prev=2, curr=3)
```

---

### Call 3: reverse(prev=2, curr=3)

**State:**

* prev: `2 -> 1 -> null`
* curr: `3 -> null`

Do:

* `next = null`
* `3.next = 2`

Now:

* Reversed part: `3 -> 2 -> 1 -> null`
* Remaining part: `null`

Recurse:

```
reverse(prev=3, curr=null)
```

---

### Call 4: reverse(prev=3, curr=null)  ✅ base case

`curr == null` → return `prev`

So returns:

```
return 3
```

This return value bubbles all the way back up, and `reverseList` returns `3`, the new head.

Final list:

```
3 -> 2 -> 1 -> null
```

---

## Why your original version returned null (what this fixes)

In your original code, `prev` was updated only inside recursive frames, but you never **returned** it. In Java, that doesn’t update the caller’s local variable.

This approach fixes that by **returning the final `prev`** from the base case and forwarding it through all recursive returns.

---

## Complexity

* Time: **O(n)** (each node processed once)
* Space: **O(n)** recursion stack (because recursion depth = number of nodes)
*/
// class Solution {
//     public ListNode reverseList(ListNode head) {
//         return reverse(null, head);
//     }

//     private ListNode reverse(ListNode prev, ListNode curr){
//         if(curr == null){
//             return prev;
//         }

//         ListNode next = curr.next;
//         curr.next = prev;

//         return reverse(curr, next);
//     }
// }






// Method 3: Recurse and reverse from tail
/*
Here’s a detailed explanation + a thorough walkthrough of the **classic recursion that reverses from the tail**:

```java
class Solution {
    public ListNode reverseList(ListNode head) {
        if (head == null || head.next == null) return head;

        ListNode newHead = reverseList(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }
}
```

---

## What this recursion is doing (big picture)

Instead of carrying `prev` forward, this approach:

1. Recursively reverses the sublist starting at `head.next`
2. When it returns, it **attaches `head` to the end** of that reversed sublist

So the recursion works “from the back”:

* the base case returns the last node as the new head
* as the call stack unwinds, each node points backward

---

## Base case

```java
if (head == null || head.next == null) return head;
```

Two situations stop recursion:

* `head == null`: empty list → reversed is still empty
* `head.next == null`: single node list → already reversed, and that node is the new head

In the normal (non-empty) case, this base case hits at the **tail node** of the original list.

---

## The key rewiring step

After this line:

```java
ListNode newHead = reverseList(head.next);
```

the sublist `head.next` is already reversed, and `newHead` points to the head of that reversed sublist.

Now you need to place `head` at the end of that reversed sublist.

That’s done by:

```java
head.next.next = head;
head.next = null;
```

### Why `head.next.next = head` works

* Before rewiring, `head.next` is the node right after `head`
* After reversing the rest, `head.next` ends up being the **tail** of the reversed sublist (in the current stack frame’s perspective)
* Setting `head.next.next = head` makes that node point back to `head`

### Why `head.next = null` is necessary

Without this, the original forward pointer from `head` would remain and you’d create a **cycle**.

Example if you forget `head.next = null`:

* you’d still have `head -> head.next` (old direction)
* and you also set `head.next -> head` (new direction)
* that creates a loop between two nodes.

So `head.next = null` “cuts off” the old link and makes `head` the new tail at that stage.

---

## Why we return `newHead`

`newHead` is the head of the fully reversed list (which is the original tail).
Every stack frame must return that same head back upward.

---

# Thorough example walkthrough

Consider:

```
1 -> 2 -> 3 -> 4 -> null
```

Call: `reverseList(1)`

---

## Call stack expansion (going down)

### Call A: reverseList(1)

* not base case (`1.next != null`)
* calls `reverseList(2)`

### Call B: reverseList(2)

* calls `reverseList(3)`

### Call C: reverseList(3)

* calls `reverseList(4)`

### Call D: reverseList(4)

* base case (`4.next == null`)
* returns `4`

So now we start **unwinding**.

---

## Unwinding step-by-step (rewiring happens here)

### Return to Call C (head = 3)

We currently have:

* `newHead = 4`
* and the reversed sublist from `head.next` is just: `4 -> null`

Now execute:

1. `head.next.next = head`

* `head.next` is `4`
* so `4.next = 3`

2. `head.next = null`

* `3.next = null`

Now the list segment becomes:

```
4 -> 3 -> null
```

Return `newHead` (=4) to caller.

---

### Return to Call B (head = 2)

Now `newHead = 4`, and the reversed sublist from `head.next` is:

```
4 -> 3 -> null
```

But notice: in this frame, `head.next` is still the node `3` (the original `2.next`), and after reversal, that node `3` is currently the **tail** of the reversed sublist.

Do rewiring:

1. `head.next.next = head`

* `head.next` is `3`
* set `3.next = 2`

2. `head.next = null`

* set `2.next = null`

Now we have:

```
4 -> 3 -> 2 -> null
```

Return `newHead` (=4).

---

### Return to Call A (head = 1)

Now `newHead = 4`, reversed sublist is:

```
4 -> 3 -> 2 -> null
```

In this frame, `head.next` is node `2` (originally `1.next`), and `2` is currently the tail.

Rewire:

1. `head.next.next = head`

* `head.next` is `2`
* set `2.next = 1`

2. `head.next = null`

* set `1.next = null`

Now final list:

```
4 -> 3 -> 2 -> 1 -> null
```

Return `newHead` (=4) from the top call.

✅ Answer head is `4`.

---

## Visual intuition (one-liner)

When the recursion returns at node `head`, you have already reversed:

```
head.next ... tail
```

So you just “flip the edge” between `head` and `head.next`:

* make `head.next` point back to `head`
* cut `head`’s forward link

---

## Complexity

* Time: **O(n)** (each node rewired once)
* Space: **O(n)** recursion stack (depth = list length)
*/
// class Solution {
//     public ListNode reverseList(ListNode head) {
//         if (head == null || head.next == null) return head;

//         ListNode newHead = reverseList(head.next);
//         head.next.next = head;
//         head.next = null;
//         return newHead;
//     }
// }
