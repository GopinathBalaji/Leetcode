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


// Method 1: Using front insertion
/*
**Idea:** Keep `prev` fixed at the node before the sublist. Keep `curr` at the start of the (partially reversed) sublist. Repeatedly take the node **after** `curr` (call it `move`) and *move* it to the **front** right after `prev`.

---

# Walkthrough Example

**List:** `1 → 2 → 3 → 4 → 5`
**left = 2, right = 4** (we will reverse the sublist `2,3,4`)

## Initial setup

* Add `dummy` so we can handle `left = 1` cleanly:

```
dummy → 1 → 2 → 3 → 4 → 5
```

* Move `prev` to the node **before** `left`. Since `left = 2`, `prev` ends at node `1`.
* Set `curr = prev.next` → `curr = 2`.

So we have:

```
prev = 1
curr = 2
Sublist to reverse = [2 .. 4]
```

We will run the loop `right - left = 2` times.

---

## Iteration 1 (i = 0)

**Before**:

```
dummy → 1(prev) → 2(curr) → 3(move) → 4 → 5
```

1. `move = curr.next` → `move = 3`
2. Detach `move` from after `curr`:

   * `curr.next = move.next`  → `2.next = 4`
3. Insert `move` at the front of the sublist (right after `prev`):

   * `move.next = prev.next`  → `3.next = 2`
   * `prev.next = move`       → `1.next = 3`

**After Iteration 1**:

```
dummy → 1(prev) → 3 → 2(curr) → 4 → 5
```

Observations:

* The reversed portion (right after `prev`) is now `3 → 2`.
* `curr` stays at `2` (the tail of the partially reversed sublist).
* Nodes outside `[left..right]` (i.e., `1` before and `5` after) remain intact.

---

## Iteration 2 (i = 1)

**Before**:

```
dummy → 1(prev) → 3 → 2(curr) → 4(move) → 5
```

1. `move = curr.next` → `move = 4`
2. Detach `move`:

   * `curr.next = move.next` → `2.next = 5`
3. Insert `move` at the front:

   * `move.next = prev.next` → `4.next = 3`
   * `prev.next = move`      → `1.next = 4`

**After Iteration 2**:

```
dummy → 1(prev) → 4 → 3 → 2(curr) → 5
```

We’ve done the loop `2` times, which equals `right-left`. The sublist `[2,3,4]` is now reversed to `[4,3,2]`.

**Final list:** `1 → 4 → 3 → 2 → 5`

---

# What stayed stable (invariant)

* `prev` never moves; it always points to the node just **before** the reversing region.
* `curr` stays as the **tail** of the partially reversed region (it doesn’t move forward); only its `.next` changes as we pull the next `move` out.
* Each iteration takes the node right after `curr` and drops it at the **front** (after `prev`). That’s why it’s called **head insertion**.

---

# Another quick example (edge case: left = 1)

**List:** `1 → 2 → 3 → 4`, `left = 1`, `right = 3`
We want `3 → 2 → 1 → 4`.

* `dummy → 1 → 2 → 3 → 4`
* `prev = dummy` (before position 1), `curr = 1`
* Run `right - left = 2` iterations:

**Iter 1:** move `2` to the front after `prev=dummy`
`dummy → 2 → 1(curr) → 3 → 4`

**Iter 2:** move `3` to the front after `prev=dummy`
`dummy → 3 → 2 → 1(curr) → 4`

Return `dummy.next` → `3 → 2 → 1 → 4`.
No special cases or separate logic needed—`dummy` made this uniform.

---

# Why this is correct

* After `k = right-left` iterations, the `k+1` nodes that began at `left` have been reordered so the original order `L0,L1,…,Lk` becomes `Lk,L(k-1),…,L0`.
* Everything before `prev` and after the sublist remains untouched.
* We never lose track of the remainder of the list because we always store `move = curr.next` **before** changing links.

---

# Complexity

* **Time:** $O(n)$ (one pass to reach `prev`, then $O(right-left)$ head-insertions).
* **Space:** $O(1)$ (just a few pointers).
*/
class Solution {
public:
    ListNode* reverseBetween(ListNode* head, int left, int right) {
        if(left == right){
            return head;
        }

        ListNode dummy(0, head);
        ListNode* prev = &dummy;
        ListNode* curr = head;

        int count = 1;

        while(count < left){
            prev = curr;
            curr = curr->next;
            count++;
        }

        // Removing the node after curr and inserting it immediately after prev
        for(int i=0; i<(right - left); i++){
            ListNode* temp = curr->next;

            curr->next = temp->next;
            temp->next = prev->next;
            prev->next = temp;
        }

        return dummy.next;
    }
};






// Method 2: Multiple passes but no extra space (Iterative reversal in O(1) space)
/*
Multiple passes: (1) reach left-1, (2) reach right & detach, (3) reverse, then stitch.
No extra space: only a few pointers; no arrays or stacks.
Edge-safe: dummy handles left == 1; after can be null when reversing to tail.



# Iterative reversal walkthrough using an example:

We’ll reverse the detached sublist `2 → 3 → 4 → null`.
Initial pointers: `prev = null`, `curr = 2`.

### Loop invariant (what’s always true before each iteration)

* `prev` points to the **already reversed** prefix (initially empty).
* `curr` points to the **next node to move** into the reversed prefix.
* Everything after `curr` is still in original order and **untouched**.

### Step-by-step

We'll track three pointers each time: `prev`, `curr`, `nxt`.

#### Before loop

* `prev = null`
* `curr = 2`
* list looks like: `null ← (prev)   2 (curr) → 3 → 4 → null`

---

#### Iteration 1

1. Save next: `nxt = curr.next` → `nxt = 3`
2. Reverse the link of `curr`: `curr.next = prev` → `2.next = null`
3. Advance pointers:

   * `prev = curr` → `prev = 2`
   * `curr = nxt` → `curr = 3`

**State now**

* Reversed prefix: `2 → null` (headed by `prev`)
* Remaining suffix: `3 → 4 → null` (headed by `curr`)
  Diagram:

```
(prev) 2 → null      (curr) 3 → 4 → null
```

---

#### Iteration 2

1. `nxt = curr.next` → `nxt = 4`
2. `curr.next = prev` → `3.next = 2`
3. Move up:

   * `prev = curr` → `prev = 3`
   * `curr = nxt` → `curr = 4`

**State now**

* Reversed prefix: `3 → 2 → null`
* Remaining: `4 → null`

```
(prev) 3 → 2 → null      (curr) 4 → null
```

---

#### Iteration 3

1. `nxt = curr.next` → `nxt = null`
2. `curr.next = prev` → `4.next = 3`
3. Move up:

   * `prev = curr` → `prev = 4`
   * `curr = nxt` → `curr = null`  ← loop stops

**State now**

* Reversed prefix: `4 → 3 → 2 → null` (headed by `prev`)
* Remaining: `null` (done)

The function returns `prev`, i.e., the new head `4`.

---

### Putting it back into the whole list

If the original list was `1 → 2 → 3 → 4 → 5` and we reversed `[2..4]`:

* `before` (node at position `left-1`) points to `1`.
* `start` (original left node) was `2` and is now the **tail** of the reversed chunk.
* `after` (node at `right+1`) is `5`.

After reversing the detached `2→3→4` to `4→3→2`:

* Connect `before.next = newHead` → `1.next = 4`
* Connect the tail of the reversed chunk to the rest: `start.next = after` → `2.next = 5`

Final list: `1 → 4 → 3 → 2 → 5`.

---

### Why this is safe and O(1) space

* We only use three pointers (`prev`, `curr`, `nxt`) and relink one edge per iteration.
* We never lose the remainder of the list because we always cache `nxt = curr.next` **before** rewiring `curr.next`.
*/

// class Solution {
// public:
//     ListNode* reverseBetween(ListNode* head, int left, int right) {
//         if (head == nullptr || left == right) {
//             return head;
//         }

//         ListNode dummy(0);
//         dummy.next = head;

//         // PASS 1: move prev to the node before position left
//         ListNode* prev = &dummy;
//         for (int i = 1; i < left; i++) {
//             prev = prev->next;
//         }

//         // PASS 2: find end at position right, then detach [left..right]
//         ListNode* start = prev->next;   // node at position left
//         ListNode* end = start;          // will move to position right

//         for (int i = left; i < right; i++) {
//             end = end->next;
//         }

//         ListNode* after = end->next;    // node after position right
//         end->next = nullptr;            // detach sublist

//         // PASS 3: reverse detached sublist
//         ListNode* newHead = reverse(start);

//         // STITCH back together
//         prev->next = newHead;
//         start->next = after;

//         return dummy.next;
//     }

// private:
//     ListNode* reverse(ListNode* node) {
//         ListNode* prev = nullptr;
//         ListNode* curr = node;

//         while (curr != nullptr) {
//             ListNode* nxt = curr->next;
//             curr->next = prev;
//             prev = curr;
//             curr = nxt;
//         }

//         return prev;
//     }
// };







// Method 3: Using multiple passes and extra space
/*
Walk to before = (left-1) and after = (right+1).
Collect exactly left..right.
Explicitly rewire .next pointers in reversed order.
Stitch before → reversed → after; update via dummy so left == 1 is handled.
*/
// class Solution {
// public:
//     ListNode* reverseBetween(ListNode* head, int left, int right) {
//         if (head == nullptr || left == right) {
//             return head;
//         }

//         ListNode dummy(0, head);

//         // before = node at position left - 1
//         ListNode* before = &dummy;
//         for (int i = 1; i < left; i++) {
//             before = before->next;
//         }

//         // curr = node at position left
//         ListNode* curr = before->next;

//         vector<ListNode*> nodes;
//         for (int i = left; i <= right; i++) {
//             nodes.push_back(curr);
//             curr = curr->next;
//         }

//         // after = node at position right + 1
//         ListNode* after = curr;

//         // Rewire reversed segment
//         for (int i = (int)nodes.size() - 1; i >= 1; i--) {
//             nodes[i]->next = nodes[i - 1];
//         }

//         // Old first node becomes tail of reversed segment
//         nodes[0]->next = after;

//         // Connect node before reversed segment to new head
//         before->next = nodes.back();

//         return dummy.next;
//     }
// };