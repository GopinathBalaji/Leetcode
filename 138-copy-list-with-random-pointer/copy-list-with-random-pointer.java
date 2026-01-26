/*
// Definition for a Node.
class Node {
    int val;
    Node next;
    Node random;

    public Node(int val) {
        this.val = val;
        this.next = null;
        this.random = null;
    }
}
*/


// Method 1: Two pass, Using extra space with HashMap
/*
1. **Map old → new nodes.**
   Use a `Map<OriginalNode, CloneNode>`. Keys must be **node references**, not values—values can repeat.

2. **Two-pass pattern (easiest & reliable).**

   * **Pass 1:** Walk the original list once, create a new node for each old node (same `val`), store `map.put(old, new)`. Don’t wire `next`/`random` yet.
   * **Pass 2:** Walk again; for each `old`, set:

     * `new.next = map.get(old.next)`
     * `new.random = map.get(old.random)`
       If you store `map.put(null, null)` upfront, those lines work without null checks.

3. **Start/return.**
   The copied head is `map.get(originalHead)` (or `null` if list is empty).

4. **Why two passes?**
   You can’t set `random` pointers in pass 1 because some targets may not be created yet. The map guarantees you can resolve them later.

5. **Alternative single-pass with helper.**
   If you insist on one traversal, write a small helper `getClone(node)` that:

   * returns `null` for `null`,
   * if `node` is already in the map, return its clone,
   * else create & store a clone (only `val`), return it.
     Then as you walk, set `curClone.next = getClone(cur.next)` and `curClone.random = getClone(cur.random)`.

6. **Edge cases to handle.**

   * Empty list → return `null`.
   * `random == null`.
   * `random` pointing to **self**.
   * `random` pointing **forward** or **backward**; map handles both.

7. **Don’t mutate the original.**
   No interleaving nodes or rewiring (that’s the O(1) space trick). With extra space, keep original untouched.

8. **Complexity target.**
   Time **O(n)**, extra space **O(n)** for the map, where `n` is number of nodes.

9. **Common pitfalls.**

   * Using `val` as the map key (breaks with duplicates).
   * Forgetting to assign `random`.
   * Returning the dummy node instead of `map.get(head)`.
*/
class Solution {
    public Node copyRandomList(Node head) {
        if (head == null) return null;

        Map<Node, Node> map = new HashMap<>();

        // Pass 1: create clone node for each original node (no wiring yet)
        for (Node cur = head; cur != null; cur = cur.next) {
            map.put(cur, new Node(cur.val));
        }

        // Pass 2: wire next and random using the map
        for (Node cur = head; cur != null; cur = cur.next) {
            Node clone = map.get(cur);
            clone.next   = map.get(cur.next);     // clone's next points to clone of cur.next
            clone.random = map.get(cur.random);   // clone's random points to clone of cur.random
        }

        // Return cloned head
        return map.get(head);
    }
}







// Method 2: My single-pass approach
/*
# WHY IS IT INEFFICIENT:
You do up to 3 map lookups per pointer due to containsKey + get. Using computeIfAbsent reduces that.
*/

// class Solution {
//     public Node copyRandomList(Node head) {

//         Node copyHead = head;
//         Map<Node, Node> map = new HashMap<>();

//         while(copyHead != null){

//             Node newNode;
//             if(!map.containsKey(copyHead)){
//                 newNode = new Node(copyHead.val);
//                 map.put(copyHead, newNode);
//             }else{
//                 newNode = map.get(copyHead);
//             }

//             Node nextNode;
//             if(copyHead.next == null){
//                 nextNode = null;
//             }else if(!map.containsKey(copyHead.next)){
//                 nextNode = new Node(copyHead.next.val);
//                 map.put(copyHead.next, nextNode);
//             }else{
//                 nextNode = map.get(copyHead.next);
//             }

//             newNode.next = nextNode;

//             Node randomNode;
//             if(copyHead.random == null){
//                 randomNode = null;
//             }else if(!map.containsKey(copyHead.random)){
//                 randomNode = new Node(copyHead.random.val);
//                 map.put(copyHead.random, randomNode);
//             }else{
//                 randomNode = map.get(copyHead.random);
//             }

//             newNode.random = randomNode;

//             copyHead = copyHead.next;
//         }

//         return map.get(head);
//     }
// }









// Method 2.5: Better Single pass, with extra space for hashmap
/*
## Big picture (what the code is doing)

We want a deep copy of a linked list where each node has:

* `val` (an int),
* `next` (next node),
* `random` (points to any node in the list or `null`).

We keep a map `Map<Node, Node>` that pairs **original nodes** (keys) with their **clone nodes** (values). As we walk the original list with pointer `cur`, we **create each clone the first time we need it** and reuse it thereafter. That’s what `computeIfAbsent` does for us.

---

## The two things you asked about

### 1) What does `n -> new Node(n.val)` mean?

That’s a **Java lambda expression** (Java 8+). Read it as:

> “Given a key `n` (which is a `Node`), return `new Node(n.val)`.”

Formally, it’s the same as writing:

```java
Function<Node, Node> f = new Function<>() {
    @Override public Node apply(Node n) {
        return new Node(n.val);
    }
};
```

Type of `n` is inferred from the context (here, the key type of the `Map<Node, Node>`).

### 2) What does `computeIfAbsent` do?

`map.computeIfAbsent(key, mappingFunction)`:

* If `key` is **already** in the map (has a non-null value), it **returns the existing value** and **does not** call the function.
* If `key` is **not** in the map (or mapped to null), it calls `mappingFunction.apply(key)`, **inserts** the returned value into the map (unless the function returns null), and returns that value.

So it’s a concise “get-or-create”:

```java
Node clone = map.containsKey(cur)
    ? map.get(cur)
    : (map.put(cur, new Node(cur.val)), new Node(cur.val));
```

…but done correctly and atomically by the library method.

---

## Walkthrough of the loop

```java
Map<Node, Node> map = new HashMap<>();
Node cur = head;

while (cur != null) {
    // 1) Ensure we have a clone for the current original node
    Node clone = map.computeIfAbsent(cur, n -> new Node(n.val));

    // 2) Link clone.next to the clone of cur.next (or null)
    clone.next = (cur.next == null)
        ? null
        : map.computeIfAbsent(cur.next, n -> new Node(n.val));

    // 3) Link clone.random to the clone of cur.random (or null)
    clone.random = (cur.random == null)
        ? null
        : map.computeIfAbsent(cur.random, n -> new Node(n.val));

    // 4) Advance
    cur = cur.next;
}

return map.get(head);
```

* **Step 1:** If this original node hasn’t been cloned yet, create its clone and put it in the map; otherwise reuse the existing clone.
* **Step 2:** If `cur.next` exists, ensure its **clone** exists (create if needed) and set `clone.next` to it. If `cur.next` is `null`, set `clone.next = null`.
* **Step 3:** Same idea for the `random` pointer.
* **Step 4:** Move on.

This handles any order: if a `random` points *forward* to a node we haven’t reached via `next` yet, `computeIfAbsent` creates that clone early; when we later reach it by `next`, we simply reuse the same clone from the map.

---

## Tiny example

Original (O) nodes:

```
O1(val=7)  ->  O2(val=13)  ->  O3(val=11)
   |             |               |
  rnd: null     rnd: O1         rnd: O1
```

Map starts empty. At `cur = O2`, say:

* `clone2 = map.computeIfAbsent(O2, …)` → creates `C2(13)`.
* `clone2.next = computeIfAbsent(O3, …)` → creates `C3(11)`.
* `clone2.random = computeIfAbsent(O1, …)` → creates `C1(7)`.
  Now the map has `{ O2→C2, O3→C3, O1→C1 }`. When we later hit `O1` by `next`, `computeIfAbsent(O1, …)` just returns `C1` (no duplicate).

---

## Why this works / key points

* **Keys are node references.** We’re mapping **original node objects** to their clones. That’s crucial; mapping by `val` would break with duplicates. In LeetCode’s `Node`, `equals`/`hashCode` aren’t overridden, so HashMap uses **identity**—perfect here.
* **Each original gets cloned at most once.** `computeIfAbsent` ensures uniqueness and reuse.
* **One pass** over `next` pointers (but we may touch `random` targets early). Overall still **O(n)** time and **O(n)** extra space.

---

## Common pitfalls avoided

* Pointing clone pointers to **original** nodes (wrong). We always use `map.get/computeIfAbsent` to point to **clones**.
* Creating multiple clones for the same original (wastes memory and breaks wiring). `computeIfAbsent` prevents that.
* Missing `cur = cur.next;` (infinite loop).

---

## Subtleties (good to know)

* If your `Node` class **overrode** `equals/hashCode` (e.g., based on `val`), then different originals with equal values would collide in the map. LeetCode’s `Node` does **not** override them, so mapping by identity is safe.
* `computeIfAbsent`’s mapping function is only called when needed; if it returns `null`, no mapping is added (we never do that here).
* `HashMap` itself isn’t thread-safe; that’s fine—LeetCode runs single-threaded.

---

## Complexity

* **Time:** O(n) — each original node is cloned at most once; pointer assignments are O(1).
* **Space:** O(n) for the map + O(n) for the cloned list nodes themselves.

That’s the whole story: `n -> new Node(n.val)` is “how to create the clone when needed”, and `computeIfAbsent` is “do that only if we don’t already have a clone for this original node, otherwise reuse it.”

*/

// class Solution {
//     public Node copyRandomList(Node head) {
//         if (head == null) return null;
//         Map<Node, Node> map = new HashMap<>();
//         Node cur = head;
//         while (cur != null) {
//             Node clone = map.computeIfAbsent(cur, n -> new Node(n.val));
//             clone.next   = (cur.next   == null) ? null : map.computeIfAbsent(cur.next,   n -> new Node(n.val));
//             clone.random = (cur.random == null) ? null : map.computeIfAbsent(cur.random, n -> new Node(n.val));
//             cur = cur.next;
//         }
//         return map.get(head);
//     }
// }









// Method 3: In-place weave-set-unweave in O(1) space
// **Time:** O(n) — each node handled a constant number of times.
// **Extra space:** O(1) — no auxiliary map; we only use a few pointers.

// ---

// # Why this works (the 3 passes)

// ## Pass 1 — Interleave clones

// For each original node `X`, create its clone `X'` and splice it right after `X`.

// Before:

// ```
// X -> Y -> Z -> null
// ```

// After pass 1:

// ```
// X -> X' -> Y -> Y' -> Z -> Z' -> null
// ```

// Now the clone of any original node `X` is simply `X.next`.

// ## Pass 2 — Wire `random` pointers

// For each original `X`, if `X.random = R`, then `X'.random = R'`.
// But `R'` is right after `R` (because of pass 1), i.e. `R.next`.

// So: `X'.random = X.random.next`, guarded for `null`.

// ## Pass 3 — Unweave (separate)

// Walk the interleaved list and:

// * stitch all clones together to form the copied list,
// * restore the original list’s `next` pointers.

// ---

// # Detailed example walkthrough

// Consider 3 nodes:

// ```
// A(1) -> B(2) -> C(3) -> null
// A.random = C
// B.random = A
// C.random = C
// ```

// ## Pass 1 — Interleave

// We insert each clone right after its original:

// ```
// A  -> A' -> B  -> B' -> C  -> C' -> null
// ^          ^          ^
// |          |          |
// (1)        (2)        (3)
// ```

// At this stage:

// * `A'.val = 1`, `A'.next = B`
// * `B'.val = 2`, `B'.next = C`
// * `C'.val = 3`, `C'.next = null`
// * No `random` pointers set for clones yet.

// ## Pass 2 — Set `random` for clones

// We iterate `A, B, C` (skipping clones via `cur = cur.next.next`):

// * For `A`: `A.random = C`.
//   So `A'.random = A.random.next = C.next = C'`.

// * For `B`: `B.random = A`.
//   So `B'.random = B.random.next = A.next = A'`.

// * For `C`: `C.random = C`.
//   So `C'.random = C.random.next = C.next = C'` (self on clone).

// Now clones have correct `random`:

// ```
// A' .random -> C'
// B' .random -> A'
// C' .random -> C'
// ```

// ## Pass 3 — Unweave

// We separate originals and clones, restoring `next` for originals and forming the cloned list.

// * Start: `cur = A`, `clone = A'`, `nextOrig = B`.

//   * Append `A'` to cloned list.
//   * Restore `A.next = B`.
//   * Advance `cur` to `B`.

// * Next: `cur = B`, `clone = B'`, `nextOrig = C`.

//   * Append `B'`.
//   * Restore `B.next = C`.
//   * Advance `cur` to `C`.

// * Next: `cur = C`, `clone = C'`, `nextOrig = null`.

//   * Append `C'`.
//   * Restore `C.next = null`.
//   * `cur = null` stop.

// Final structures:

// Original:

// ```
// A -> B -> C -> null
// (random unchanged: A→C, B→A, C→C)
// ```

// Clone:

// ```
// A' -> B' -> C' -> null
// A'.random = C'
// B'.random = A'
// C'.random = C'
// ```

// We return `A'` (the head of the cloned list).

// ---

// # Common pitfalls to avoid

// * **Forgetting to jump by 2** in pass 2: `cur = cur.next.next` (must land on next original).
// * **Accidentally pointing clone.next to an original** during unweaving. Always use `clone = cur.next`, then `nextOrig = clone.next`.
// * **Not restoring** the original list’s `next` pointers (`cur.next = nextOrig`) — leaves the original list broken.
// * **Null checks**: if `cur.random == null`, do *not* dereference `.next`.

// This in-place weave–set–unweave pattern is the standard O(1)-space solution and is fast + elegant once you see the trick.


// class Solution {
//     public Node copyRandomList(Node head) {
//         if (head == null) return null;

//         // 1) Interleave: for each original node X, create X' and insert it right after X.
//         Node cur = head;
//         while (cur != null) {
//             Node clone = new Node(cur.val);
//             clone.next = cur.next;
//             cur.next = clone;
//             cur = clone.next;
//         }

//         // 2) Set random pointers for clones: X'.random = X.random'
//         cur = head;
//         while (cur != null) {
//             if (cur.random != null) {
//                 cur.next.random = cur.random.next; // X' -> X.random'
//             }
//             cur = cur.next.next; // jump over clone to the next original
//         }

//         // 3) Unweave: separate the interleaved list into original and cloned
//         Node pseudoHead = new Node(0);
//         Node copyCur = pseudoHead;
//         cur = head;
//         while (cur != null) {
//             Node clone = cur.next;         // X'
//             Node nextOrig = clone.next;    // next original (might be null)

//             copyCur.next = clone;          // append clone to copy list
//             copyCur = clone;

//             cur.next = nextOrig;           // restore original linkage (X -> next original)
//             cur = nextOrig;                // move to next original
//         }

//         return pseudoHead.next;
//     }
// }

























