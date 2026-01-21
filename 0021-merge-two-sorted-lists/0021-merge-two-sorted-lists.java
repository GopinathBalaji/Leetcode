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
/*
*/
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
/*
*/
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







// Method 2: My Recursive approach
/*
Your code is **actually correct** as written.
It works because you’re not trying to “update `dummy` for the caller” (which would fail in Java); instead you pass the **current tail node** into the next recursive call, and you mutate its `.next`. The merged list is built via pointer rewiring, so `head.next` ends up correct.

That said, there are a couple of things that people often *think* are wrong here, plus one real practical drawback:

---

## Why it’s not broken (despite Java pass-by-value)

Even though Java passes references by value, this is fine because:

* `dummy` is a reference to a node **in the already-built merged list**
* You mutate `dummy.next = ...`
* That mutation affects the actual node object, so the list grows correctly
* Reassigning `dummy = dummy.next` only needs to be correct for the *next recursive call*, and it is.

So the “pass-by-value” issue that broke your reverse-list attempt does **not** break this merge approach.

---

## The real drawback: recursion depth (stack overflow risk)

Worst case recursion depth is `m + n` (you recurse once per node).
On LeetCode it usually passes, but in general this can overflow the stack on very long lists.

So it’s correct but not always robust.

---

## Minor style/simplicity improvements (not correctness issues)

### 1) `head` variable is redundant

You can just return `dummy.next` since `dummy` is never used after setup.

### 2) You don’t need a dummy at all in recursive solutions


## Tiny note

If you accidentally changed the call to `merge(dummy, ...)` to `merge(head, ...)` you’d still be fine because `head == dummy` at that point. So no issue there either.
*/
// class Solution {
//     public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
//         ListNode dummy = new ListNode(0);
//         ListNode head = dummy;

//         merge(dummy, list1, list2);

//         return head.next;
//     }

//     private void merge(ListNode dummy, ListNode list1, ListNode list2){
//         if(list1 == null){
//             dummy.next = list2;
//             return;
//         }
//         if(list2 == null){
//             dummy.next = list1;
//             return;
//         }

//         if(list1.val <= list2.val){
//             dummy.next = list1;
//             dummy = dummy.next;
//             list1 = list1.next;
//         }else{
//             dummy.next = list2;
//             dummy = dummy.next;
//             list2 = list2.next;
//         }

//         merge(dummy, list1, list2);
//     }
// }





// Method 2.5: Better Recursive Solution
/*
## What this recursion is doing

Think of `mergeTwoLists(a, b)` as:

> “Return the head of the merged sorted list formed by merging the lists starting at `a` and `b`.”

At every call, you decide which node should be the **first** node in the merged result:

* If `a.val <= b.val`, then `a` is the smaller (or equal) head → `a` must come first.

  * So the merged list head is `a`
  * And `a.next` should be the merge of the remaining part: `merge(a.next, b)`

* Else, `b` must come first.

  * Head is `b`
  * `b.next = merge(a, b.next)`

This is exactly the same logic you’d do iteratively, just expressed as “pick head + recursively build the rest”.

---

## Base cases (why they’re correct)

```java
if (a == null) return b;
if (b == null) return a;
```

If one list is empty, merging is trivial: the result is just the other list (already sorted).

These base cases are also what stop recursion.

---

## Key invariant

At the start of each call `mergeTwoLists(a, b)`:

* Both `a` and `b` point to sorted lists.
* The function returns a sorted merged list containing **all remaining nodes** from both.
* No new nodes are created; we only rewire `.next`.

---

## Why it stays sorted

Suppose `a.val <= b.val`. We return `a` as head.
We then set:

```java
a.next = mergeTwoLists(a.next, b);
```

* Everything in `a.next` is `>= a.val` because list `a` is sorted.
* Everything in `b` is `>= b.val >= a.val`.
* The recursive merge returns a sorted list of those remaining nodes.
* Therefore `a` followed by that merged tail remains sorted.

Same logic holds symmetrically when picking `b`.

---

# Thorough example walkthrough

Let:

* `a = 1 -> 2 -> 4`
* `b = 1 -> 3 -> 4`

We’ll track calls and rewiring. I’ll use `M(a,b)` to denote `mergeTwoLists(a,b)`.

---

## Call 1: `M(1->2->4, 1->3->4)`

Compare heads: `a.val = 1`, `b.val = 1`

* `a.val <= b.val` ✅ so choose `a` as head.
* We will return node `a(1)` eventually.
* But first we must set its `.next`:

```java
a.next = M(a.next, b)
```

So we call:

### Call 2: `M(2->4, 1->3->4)`

---

## Call 2: `M(2->4, 1->3->4)`

Compare heads: `2` vs `1`

* `2 <= 1` ❌ so choose `b` as head.
* We will return node `b(1)` for this call.
* Set:

```java
b.next = M(a, b.next)
```

So we call:

### Call 3: `M(2->4, 3->4)`

---

## Call 3: `M(2->4, 3->4)`

Compare: `2` vs `3`

* `2 <= 3` ✅ choose `a` (node 2)
* Set:

```java
a.next = M(a.next, b)
```

Call:

### Call 4: `M(4, 3->4)`

---

## Call 4: `M(4, 3->4)`

Compare: `4` vs `3`

* `4 <= 3` ❌ choose `b` (node 3)
* Set:

```java
b.next = M(a, b.next)
```

Call:

### Call 5: `M(4, 4)`

---

## Call 5: `M(4, 4)`

Compare: `4` vs `4`

* `4 <= 4` ✅ choose `a` (node 4 from list a)
* Set:

```java
a.next = M(a.next, b)
```

But `a.next` is `null` (since this is the last node in `a`).

Call:

### Call 6: `M(null, 4)`

---

## Call 6 (base case): `M(null, 4)`

`a == null` → return `b`

So Call 6 returns the list:

```
4 -> null
```

---

# Unwinding (this is where pointers get connected)

Now we return back up the stack, filling in the `.next` pointers that were waiting.

### Return to Call 5

Call 5 chose `a(4)` and had:

* `a.next = result_of_Call6`

So now:

* `a(4).next = 4(from b)`

Call 5 returns:

```
4(a) -> 4(b) -> null
```

---

### Return to Call 4

Call 4 chose `b(3)` and had:

* `b.next = result_of_Call5`

So now:

* `3.next = 4(a) -> 4(b)`

Call 4 returns:

```
3 -> 4 -> 4 -> null
```

---

### Return to Call 3

Call 3 chose `a(2)` and had:

* `2.next = result_of_Call4`

So Call 3 returns:

```
2 -> 3 -> 4 -> 4 -> null
```

---

### Return to Call 2

Call 2 chose `b(1)` and had:

* `1(b).next = result_of_Call3`

So Call 2 returns:

```
1(b) -> 2 -> 3 -> 4 -> 4 -> null
```

---

### Return to Call 1

Call 1 chose `a(1)` and had:

* `1(a).next = result_of_Call2`

So final merged list returned by Call 1 is:

```
1(a) -> 1(b) -> 2 -> 3 -> 4(a) -> 4(b) -> null
```

✅ Sorted and correct.

---

## Why no dummy is needed

A dummy node is useful in iterative merges to simplify “first insertion”.
In this recursive version, you *always* return the correct head:

* If `a` is smaller, the merged head is `a`
* Otherwise it’s `b`

So recursion naturally handles the “head selection” cleanly.

---

## Complexity

* **Time:** `O(m + n)` (each node chosen once)
* **Space:** `O(m + n)` recursion stack in worst case (depth equals total nodes)
*/
// class Solution {
//     public ListNode mergeTwoLists(ListNode a, ListNode b) {
//         if (a == null) return b;
//         if (b == null) return a;

//         if (a.val <= b.val) {
//             a.next = mergeTwoLists(a.next, b);
//             return a;
//         } else {
//             b.next = mergeTwoLists(a, b.next);
//             return b;
//         }
//     }
// }
