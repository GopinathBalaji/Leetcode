// Method 1: Greedy Approach using TreeMap (for sorted keys)
/*
Short version: at the smallest remaining card value `x`, suppose its count is `c`. Those `c` copies of `x` **must** start `c` consecutive groups right now. Therefore, for each next value `x+1, x+2, …, x+W-1`, you need **at least `c`** copies to pair with those `c` groups. Checking “> 0” is too weak: it only guarantees you can extend **one** of the `c` groups, leaving the other `c−1` groups stranded.

Let’s make that concrete.

---

## Why `>= c` (not `> 0`) is necessary

### Invariant

We always pick the **smallest key** `x` left in the map. Let its count be `c = count[x]`. Because there is **no smaller number** left, these `c` copies of `x` **cannot** appear in the middle of any consecutive group—they have no `x−1` to precede them. So they **must** be the first element of `c` groups:

```
[x, x+1, ..., x+W-1]   (repeated c times in parallel)
```

To build those `c` parallel groups, each next value must have enough supply to feed **all `c` groups at once**. Hence we require:

```
count[x]   >= c   (trivial)
count[x+1] >= c
count[x+2] >= c
...
count[x+W-1] >= c
```

If any is `< c`, at least one of the `c` groups will miss that value; since groups must be consecutive, that arrangement is impossible.

---

## Counterexamples where “> 0” would mislead you

### Example A

```
hand: [1,1,1, 2,2, 3]   W = 3
counts: {1:3, 2:2, 3:1}
x = 1, c = 3
```

If you only checked “> 0”:

* `count[2] = 2` (that’s >0) ✅
* `count[3] = 1` (that’s >0) ✅

But you actually need **3** twos and **3** threes to build

```
[1,2,3], [1,2,3], [1,2,3]
```

You only have 2 twos and 1 three → impossible. The correct check `count[v] >= c` catches this immediately.

### Example B

```
hand: [1,1, 2,2, 3, 4]   W = 3
counts: {1:2, 2:2, 3:1, 4:1}
x = 1, c = 2
```

You need two groups of length 3 starting at 1:

```
[1,2,3] and [1,2,3]
```

But `count[3] = 1 < c (2)` → impossible.
“>0” would wrongly think you can proceed.

---

## How the algorithm uses this (batch vs. per-group view)

There are two equivalent ways to “consume” counts; seeing both makes the `>= c` check intuitive.

### 1) Batched (the common TreeMap solution)

For the smallest `x` with `c = count[x]`, do in one sweep:

```text
for v in [x, x+1, ..., x+W-1]:
    if count[v] < c → return false
    count[v] -= c
    if count[v] == 0 → remove v
```

This says: “I’m starting `c` groups at `x`; consume `c` of each required value.”

### 2) Per-group (same logic, just slower)

Conceptually, do it `c` times:

```text
repeat c times:
  for v in [x, x+1, ..., x+W-1]:
      if count[v] == 0 → return false
      count[v] -= 1
```

If you unroll that loop algebraically, it’s identical to the batched version.
The batched check `count[v] >= c` is exactly what ensures the inner per-group loop wouldn’t run out of cards.

So checking merely “> 0” corresponds to ensuring you can extend **one** group once. But we’re starting **`c` parallel groups**, so we need **`c` copies** of each next value immediately.

---

## Detailed walkthrough: success case

```
hand = [1,1,2,2,3,3,4,4],  W = 4
counts: {1:2, 2:2, 3:2, 4:2}
```

* Smallest `x = 1`, `c = 2`.
* Need `count[2] >= 2`, `count[3] >= 2`, `count[4] >= 2` → all true.
* Subtract 2 from each: counts become `{}` → success.

Interpretation: we formed two groups in parallel:

```
[1,2,3,4] and [1,2,3,4]
```

---

## Detailed walkthrough: fail case caught by `>= c`

```
hand = [1,1,1, 2,2, 3,3, 4],  W = 3
counts: {1:3, 2:2, 3:2, 4:1}
```

* `x = 1`, `c = 3`
* Need `count[2] >= 3` (but it’s 2) → fail immediately.

Even though there is “> 0” of `2` and `3`, there aren’t enough to support **three** parallel groups that must start at `1`. Some `1`s would be stranded because we can’t skip `2` or `3` in a length-3 consecutive group.


**TL;DR:**
At the smallest remaining value `x`, its `c` copies **must** begin `c` consecutive groups **right now**. Therefore each of the next `W−1` values must have **at least `c`** copies to feed those groups. Checking “> 0” only proves you can extend a single group; it doesn’t prevent stranding the other `c−1` groups.

NOTE: Question can also be solve using a similar approach and Priority Queue to maintain order instead
of TreeMap
*/

class Solution {
    public boolean isNStraightHand(int[] hand, int groupSize) {
        int n = hand.length;
        if (n % groupSize != 0) return false;
        if (groupSize == 1) return true;

        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int card : hand) {
            map.put(card, map.getOrDefault(card, 0) + 1);
        }

        while (!map.isEmpty()) {
            int start = map.firstKey();      // smallest remaining
            int c = map.get(start);          // how many groups must start here

            // form c groups: [start, start+1, ..., start+groupSize-1]
            for (int v = start; v < start + groupSize; v++) {
                Integer cnt = map.get(v);
                if (cnt == null || cnt < c) return false; // gap or not enough
                if (cnt == c) {
                    map.remove(v);
                } else {
                    map.put(v, cnt - c);
                }
            }
        }

        return true;
    }
}


