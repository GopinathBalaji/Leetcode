/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode() : val(0), next(nullptr) {}
 *     ListNode(int x) : val(x), next(nullptr) {}
 *     ListNode(int x, ListNode *next) : val(x), next(next) {}
 * };
 */


// Method 1: Using Iterative pointer reversal Approach
/*
No, that loop is **not usually called the head-insertion approach**.

This loop is the standard **iterative pointer-reversal approach**:

```cpp
while (curr != groupNext) {
    ListNode* temp = curr->next;
    curr->next = prev;
    prev = curr;
    curr = temp;
}
```

It reverses links one by one by making each `curr` point backward to `prev`.

For example, for:

```text
1 -> 2 -> 3 -> 4
k = 3
```

Before reversing the first group:

```text
prev = 4
curr = 1
```

Then:

```text
1 -> 4
2 -> 1 -> 4
3 -> 2 -> 1 -> 4
```

At the end:

```text
3 -> 2 -> 1 -> 4
```

So this is better described as:

```text
standard iterative reversal
```

or

```text
prev-curr-next reversal
```

---

The **head insertion approach** is slightly different. It keeps the first node of the group fixed as the temporary tail and repeatedly moves the next node to the front of the group.

For example:

```text
dummy -> 1 -> 2 -> 3 -> 4
k = 3
```

Move `2` after `dummy`:

```text
dummy -> 2 -> 1 -> 3 -> 4
```

Move `3` after `dummy`:

```text
dummy -> 3 -> 2 -> 1 -> 4
```

That style usually looks like this:

```cpp
ListNode* curr = groupPrev->next;

for (int i = 1; i < k; i++) {
    ListNode* moveNode = curr->next;

    curr->next = moveNode->next;
    moveNode->next = groupPrev->next;
    groupPrev->next = moveNode;
}
```

So the difference is:

```text
prev-curr reversal:
    reverse pointers by walking through the group

head insertion:
    repeatedly take the node after curr and insert it after groupPrev
```

Both are valid for LeetCode 25 and both are O(n), O(1).

#########################################################################

## Hint 1: Understand the operation

You are not reversing the node values.

You must reverse the actual linked list nodes in groups of size `k`.

Example:

```text
Input:  1 -> 2 -> 3 -> 4 -> 5
k = 2

Output: 2 -> 1 -> 4 -> 3 -> 5
```

The last group is left unchanged if it has fewer than `k` nodes.

---

## Hint 2: Think group by group

For every group of `k` nodes, you need to know four things:

```text
groupPrev -> node before the group
groupStart -> first node in the group
groupEnd -> kth node in the group
groupNext -> node after the group
```

Example for `k = 3`:

```text
groupPrev    groupStart       groupEnd    groupNext
   |             |               |           |
dummy -> 1  ->   2  ->   3  ->   4  ->   5
```

Actually for the first group:

```text
dummy -> 1 -> 2 -> 3 -> 4 -> 5
         ^         ^    ^
     groupStart groupEnd groupNext
```

---

## Hint 3: Use a dummy node

Because the head itself may change after reversing the first group, use:

```cpp
ListNode dummy(0);
dummy.next = head;
```

Then keep:

```cpp
ListNode* groupPrev = &dummy;
```

At the end, return:

```cpp
return dummy.next;
```

---

## Hint 4: Before reversing, check if k nodes exist

From `groupPrev`, move `k` steps forward.

```text
If you cannot move k steps, stop.
```

Because incomplete groups should remain unchanged.

You can write a helper:

```cpp
ListNode* getKth(ListNode* curr, int k)
```

It returns the kth node after `curr`.

Example:

```cpp
ListNode* kth = getKth(groupPrev, k);
if (kth == nullptr) break;
```

---

## Hint 5: Save the node after the group

Before reversing, save:

```cpp
ListNode* groupNext = kth->next;
```

This is important because after reversal, the old connections will change.

---

## Hint 6: Reverse only until `groupNext`

Use the normal linked list reversal idea, but stop at `groupNext`.

For one group:

```cpp
ListNode* prev = groupNext;
ListNode* curr = groupPrev->next;
```

Then reverse until `curr == groupNext`:

```cpp
while (curr != groupNext) {
    ListNode* temp = curr->next;
    curr->next = prev;
    prev = curr;
    curr = temp;
}
```

Why set:

```cpp
prev = groupNext;
```

Because after reversing, the original groupStart becomes the tail of the group, and it should point to `groupNext`.

---

## Hint 7: Reconnect the reversed group

Before reversing, save:

```cpp
ListNode* oldGroupStart = groupPrev->next;
```

After reversal:

```text
prev points to new group head
oldGroupStart points to new group tail
```

So reconnect:

```cpp
groupPrev->next = kth;
groupPrev = oldGroupStart;
```

Or more generally:

```cpp
ListNode* newGroupHead = kth;
ListNode* newGroupTail = oldGroupStart;
```

Then move `groupPrev` to the tail for the next group.

---

## Hint 8: Full loop shape

The whole algorithm looks like this:

```cpp
dummy.next = head;
groupPrev = &dummy;

while (true) {
    kth = getKth(groupPrev, k);

    if (kth == nullptr) {
        break;
    }

    groupNext = kth->next;

    // reverse group
    prev = groupNext;
    curr = groupPrev->next;

    while (curr != groupNext) {
        temp = curr->next;
        curr->next = prev;
        prev = curr;
        curr = temp;
    }

    // reconnect
    oldGroupStart = groupPrev->next;
    groupPrev->next = kth;
    groupPrev = oldGroupStart;
}

return dummy.next;
```

---

## Hint 9: Why `groupPrev = oldGroupStart`?

Before reversal:

```text
groupPrev -> 1 -> 2 -> 3 -> groupNext
```

After reversal:

```text
groupPrev -> 3 -> 2 -> 1 -> groupNext
```

The old group start `1` becomes the tail.

So for the next group, the node before the next group is `1`.

That is why:

```cpp
groupPrev = oldGroupStart;
```

---

## Hint 10: Complexity

Each node is visited a constant number of times.

```text
Time:  O(n)
Space: O(1)
```

The key trick is carefully saving pointers before reversing:

```cpp
ListNode* kth = getKth(groupPrev, k);
ListNode* groupNext = kth->next;
ListNode* oldGroupStart = groupPrev->next;
```
*/
class Solution {
public:
    ListNode* reverseKGroup(ListNode* head, int k) {
        ListNode dummy(0);
        dummy.next = head;

        ListNode* groupPrev = &dummy;

        ListNode* groupNext;
        ListNode* prev;
        ListNode* curr;


        while(true){
            ListNode* kth = getKth(groupPrev, k);

            if(kth == nullptr){
                break;
            }

            groupNext = kth->next;

            prev = groupNext;
            curr = groupPrev->next;

            while(curr != groupNext){
                ListNode* temp = curr->next;
                curr->next = prev;
                prev = curr;
                curr = temp;
            }

            ListNode* oldGroupStart = groupPrev->next;
            groupPrev->next = kth;
            groupPrev = oldGroupStart;
        }

        return dummy.next;
    }

private:
    ListNode* getKth(ListNode* node, int k){
        while(node != nullptr && k != 0){
            node = node->next;
            k--;
        }

        return node;
    }
};






// Mehtod 2: Do k head insertions per iteration for reversal
/*
# HINTS

Here are **high-signal hints** for **LeetCode 25: Reverse Nodes in k-Group** (no full code yet), aimed at the standard in-place O(1) extra space solution.

---

## Hint 1: Use a dummy node

Create `dummy -> head`.
This makes reversing the first group the same as reversing any later group (no special-case for head).

Keep a pointer:

* `groupPrev` = node **before** the current k-group (starts at `dummy`)

---

## Hint 2: Before reversing, verify you *have k nodes*

From `groupPrev`, walk `k` steps to find `kth` node:

* If you can’t reach `kth` (hit null early), you’re done: return `dummy.next`.

This check prevents partial group reversal.

---

## Hint 3: Identify the boundaries you will reconnect

Once you have `kth`:

* `groupNext = kth.next` (node after the group)
* The group to reverse is from `groupPrev.next` up to `kth` inclusive.

After reversal:

* `groupPrev.next` should point to `kth` (new head of this group)
* The old head (which becomes tail) should point to `groupNext`

---

## Hint 4: Reverse pointers *within the group* using a standard loop

Classic in-place reverse needs:

* `prev`, `curr`, `tmp`

But the trick here: initialize

* `prev = groupNext`
* `curr = groupPrev.next`

Then reverse until `curr == groupNext` (or do k iterations).

Why `prev = groupNext`?
So when you reverse the last node in the group, its `next` will correctly point to `groupNext` automatically.

---

## Hint 5: Move `groupPrev` forward for the next iteration

After reversing:

* `newGroupTail` is the old group head (the node you started with)
* Set `groupPrev = newGroupTail` to prepare for the next k-group.

---

## Hint 6: A mental picture helps

Think of pointers like:

```
groupPrev -> [a -> b -> c -> ... -> kth] -> groupNext
```

After reversing the inside, you want:

```
groupPrev -> [kth -> ... -> c -> b -> a] -> groupNext
```

So you only need to fix **two external connections**:

1. `groupPrev.next`
2. `newTail.next`

---

## Hint 7: Common pitfalls

* Forgetting to check there are k nodes (leads to reversing a partial group)
* Losing `groupNext` before reversing
* Updating `groupPrev` incorrectly (causes loops or skipping nodes)
* Off-by-one in the reversal loop (reversing one node too far)

##############################

# WHAT WAS I DOING WRONG:

Two main issues (one **logic / off-by-one**, one **missing reconnection**) make this incorrect.

---

## 1) Your `kth` finder is off by one (it lands on the (k+1)-th node)

You start with `i = -1` and loop while `i < k`, incrementing `i` **before** moving `kth`:

```java
int i = -1;
ListNode kth = groupPrev;
while (kth != null && i < k) {
    i++;
    kth = kth.next;
}
```

For example, if `k = 2`:

* start: `i=-1`, `kth=groupPrev`
* iter1: `i=0`, `kth=1st node`
* iter2: `i=1`, `kth=2nd node`
* iter3: `i=2`, `kth=3rd node`  ✅ loop stops here

So `kth` ends up at the **3rd node**, not the 2nd.
That means you reverse **k+1 nodes** (until `groupNext`) instead of k.

**Fix:** move exactly `k` steps from `groupPrev`:

```java
ListNode kth = groupPrev;
for (int i = 0; i < k && kth != null; i++) kth = kth.next;
if (kth == null) break;
```

---

## 2) You never reconnect the reversed group back to the previous part

After the reversal loop:

* `prev` is the **new head** of the reversed group
* `newGroupTail` is the **old head** (now the tail)

But you never do:

```java
groupPrev.next = prev;
```

So the list before the group still points to the **old head**, and your result is wrong (e.g., `dummy.next` never changes from the original head).

#################################
# Explanation:
Detaching the moved node: curr.next = move.next; prevents cycles and keeps the rest of the list reachable.
Exactly k-1 moves: Head-inserting k-1 nodes reverses a k-node block; doing k would overrun/NullPointer.
Correct stitching: Tail (curr) connects to nextGroupHead. prev connects to the new head via the head-insertions.
Prev advancement: After reversing, the group’s original head (curr) is now the tail; set prev = curr.

## Thorough example walkthrough

**Input:** `1 → 2 → 3 → 4 → 5`, `k = 3`
Goal: reverse in groups of 3 → `3 → 2 → 1 → 4 → 5`

Initialize:

```
dummy → 1 → 2 → 3 → 4 → 5
prev = dummy
```

### Round 1: reverse the first 3 nodes

1. **Find kth** node starting from `prev`:

   * start at `prev = dummy`, walk 3 steps: `kth = 3`
   * `nextGroupHead = kth.next = 4`
   * `curr = prev.next = 1`

2. **Head-insert (k-1 = 2 times)**

* **Iteration i=1**

  * `move = curr.next = 2`
  * Detach: `curr.next = move.next` → `1.next = 3`
  * Insert at front:
    `move.next = prev.next` → `2.next = 1`
    `prev.next = move` → `dummy.next = 2`
  * List now: `dummy → 2 → 1 → 3 → 4 → 5`
  * `curr` remains `1` (tail of the partially reversed block)

* **Iteration i=2**

  * `move = curr.next = 3`
  * Detach: `curr.next = move.next` → `1.next = 4`
  * Insert at front:
    `move.next = prev.next` → `3.next = 2`
    `prev.next = move` → `dummy.next = 3`
  * List now: `dummy → 3 → 2 → 1 → 4 → 5`

3. **Stitch tail to next group**

   * `curr` is the original head `1`, now tail of the reversed block
   * `curr.next = nextGroupHead` → `1.next = 4` (already true after detaches; this line ensures it)
   * Advance `prev = curr` → `prev = 1`

State after Round 1:

```
dummy → 3 → 2 → 1(prev) → 4 → 5
```

### Round 2: try to reverse the next 3 nodes

1. **Find kth** from `prev=1`:

   * steps: 1→`4`, 2→`5`, 3→`null` → fewer than k nodes
   * Stop. No more reversals.

**Result:** `3 → 2 → 1 → 4 → 5` ✅

---

## Complexity

* Each node is moved/relabeled a constant number of times: **O(n)** time.
* Only constant extra pointers: **O(1)** space.

---

### Common pitfalls (all present in your original attempt)

* Forgetting `curr.next = move.next` (detaching the moved node).
* Doing `k` head-insertions instead of `k-1`.
* Not tracking `nextGroupHead` (right boundary) to stitch back.
* Advancing `prev` incorrectly (should become the **tail** of the reversed block).

If you prefer a slightly different pattern, an alternative is to reverse the `[groupStart..kth]` segment with the standard 3-pointer reversal using `nextGroupHead` as a sentinel. But the head-insertion version above is concise and fast for interviews.
*/
// class Solution {
// public:
//     ListNode* reverseKGroup(ListNode* head, int k) {
//         if (head == nullptr || k <= 1) {
//             return head;
//         }

//         ListNode dummy(-1);
//         dummy.next = head;

//         // prev stands before the group we want to reverse
//         ListNode* prev = &dummy;

//         while (true) {
//             // 1. Check whether there are at least k nodes ahead
//             ListNode* kth = prev;

//             for (int i = 0; i < k && kth != nullptr; i++) {
//                 kth = kth->next;
//             }

//             // Fewer than k nodes remain
//             if (kth == nullptr) {
//                 break;
//             }

//             // 2. Reverse this k-group using head insertion
//             ListNode* curr = prev->next;          // first node in the group
//             ListNode* nextGroupHead = kth->next;  // node after the group

//             // Move curr->next to the front of the group, k - 1 times
//             for (int i = 1; i < k; i++) {
//                 ListNode* move = curr->next;

//                 // Detach move from after curr
//                 curr->next = move->next;

//                 // Insert move right after prev
//                 move->next = prev->next;
//                 prev->next = move;
//             }

//             // 3. curr is now the tail of the reversed group
//             curr->next = nextGroupHead;

//             // 4. Move prev to the tail of this reversed group
//             prev = curr;
//         }

//         return dummy.next;
//     }
// };