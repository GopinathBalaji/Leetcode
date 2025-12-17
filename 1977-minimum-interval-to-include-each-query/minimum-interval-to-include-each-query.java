// Method 1: Line Sweep, Sorting, Heap/PriorityQueue
/*
# WHAT WAS WRONG WITH MY APPROACH:
Your logic is **correct** in the sense that it will return the right answer, but it has two big practical problems (and one small nuance):

## 1) Time complexity is too slow (will TLE)

You rebuild a heap **from scratch for every query**:

* For each query `q` you loop through **all intervals** and add those with `start <= q` → `O(n log n)` per query.
* Then you pop some expired ones → up to `O(n log n)` per query.
* Total worst case: **O(q * n log n)**, which will time out for LC 1851 constraints (both can be up to 10^5).

The intended solution is ~**O((n + q) log n)** by:

* sorting intervals by `start`
* sorting queries (keeping original indices)
* pushing intervals into ONE heap incrementally as queries increase
* popping intervals whose `end < query`
* answering from heap top

So the “wrong” thing is not correctness, it’s efficiency.

---

## 2) You’re missing an important filter: intervals that can’t ever cover the query

You only check:

```java
if (intervals[i][0] <= query) minHeap.add(intervals[i]);
```

That includes intervals with `end < query` too (you later pop those) — which is fine but expensive.

More importantly, you **never avoid adding** intervals where `end < query` even though they’re guaranteed useless for that query. Again: not incorrect, but unnecessary work.

A tighter per-query build (still too slow overall) would be:

```java
if (intervals[i][0] <= query && intervals[i][1] >= query) add
```

But the real fix is the global sweep approach.

---

## 3) Subtle but important: heap ordering alone can’t “clean” invalid intervals

You do clean-up with:

```java
while (!minHeap.isEmpty() && minHeap.peek()[1] < query) minHeap.poll();
```

This is correct only because you keep polling until the top is valid. Even if some invalid intervals are buried deeper in the heap, they don’t matter as long as the minimum-length valid interval rises to the top eventually.

So this part is **fine**.

---

# What the accepted approach looks like (high level)

1. Sort `intervals` by `start`.
2. Make `queriesWithIndex = [(queryValue, originalIndex)]` and sort by queryValue.
3. Maintain one min-heap ordered by interval length.
4. For each query in increasing order:

   * Push all intervals with `start <= query` into heap.
   * Pop while heap top has `end < query`.
   * Top gives answer, else -1.

That gets you the needed performance.

---

## Bottom line

* ✅ Your solution is **correct** logically.
* ❌ It will **time out** because it’s **O(Q * N log N)**.
* The fix is to **sort queries + sweep intervals once** with a single heap: **O((N+Q) log N)**.

############# CORRECT APPROACH ##############

Here’s the **accepted O((N+Q) log N)** approach for **LeetCode 1851: Minimum Interval to Include Each Query**, with a full walkthrough.

---

## Big picture

For each query `q`, we want:

* among all intervals `[l, r]` such that `l <= q <= r`,
* return the **minimum length** `(r - l + 1)`,
* else `-1`.

Brute force per query is too slow. The trick is to process queries in **increasing order** and add intervals only when they become relevant.

---

## Correct approach

### Step 1: Sort intervals by start

Sort `intervals` by `l` ascending.

This lets us “activate” intervals as queries increase: when we’re at query `q`, all intervals with `l <= q` are candidates (they *might* cover `q`).

### Step 2: Sort queries but keep original indices

Create an array `qArr = [(value, originalIndex)]` and sort by `value`.

We’ll compute answers in sorted-query order, but store them back into `ans[originalIndex]`.

### Step 3: Use a min-heap ordered by interval length

Maintain a min-heap of intervals that have **started** (`l <= q`). Heap ordering is:

1. interval length `(r - l + 1)` ascending
2. tie-break by `r` ascending (optional)

Why this works:

* Once we remove intervals that ended before `q`, then everything left in heap satisfies `l <= q` (by construction) and `r >= q` (by cleaning).
* So the heap top is the **shortest interval that covers q**.

### Step 4: Sweep through queries

For each query `q` in ascending order:

1. **Push** into heap all intervals with `start <= q`.
2. **Pop** from heap while `end < q` (these can’t cover this query or any later query).
3. If heap non-empty, answer is heap-top length; else -1.

---

## Why popping by `end < q` works even though heap is ordered by length

Some expired intervals might be buried deeper, but they only matter if they become the top.
By repeatedly removing expired intervals at the top, we ensure the top is always valid before answering.

Complexity:

* Sorting: `O(N log N + Q log Q)`
* Each interval inserted once and popped at most once: total heap ops `O((N+Q) log N)`

---

## Thorough example walkthrough

Use the classic example:

```text
intervals = [[1,4], [2,4], [3,6], [4,4]]
queries   = [2, 3, 4, 5]
```

Expected answers:

* q=2 → intervals covering 2: [1,4] len4, [2,4] len3 → answer 3
* q=3 → covers: [1,4] len4, [2,4] len3, [3,6] len4 → answer 3
* q=4 → covers: [1,4] len4, [2,4] len3, [3,6] len4, [4,4] len1 → answer 1
* q=5 → covers: [3,6] len4 → answer 4

### Step 1: Sort intervals by start

Already sorted by start:

1. [1,4]
2. [2,4]
3. [3,6]
4. [4,4]

### Step 2: Queries with indices, sorted

Queries already sorted:

* (2, idx0)
* (3, idx1)
* (4, idx2)
* (5, idx3)

We maintain:

* `i` pointer into intervals (starts at 0)
* min-heap `pq` (by length, then end)

---

### Process query = 2

**Add intervals with start <= 2**

* intervals[0] = [1,4] start 1 <= 2 → push
* intervals[1] = [2,4] start 2 <= 2 → push
* intervals[2] = [3,6] start 3 > 2 → stop

Heap contents (conceptually ordered by length):

* [2,4] len 3
* [1,4] len 4

**Remove expired (end < 2)**
Top is [2,4], end 4 < 2? no → nothing removed.

**Answer** = top length = 3
So ans[idx0] = 3

---

### Process query = 3

**Add intervals with start <= 3**

* intervals[2] = [3,6] start 3 <= 3 → push
* intervals[3] = [4,4] start 4 > 3 → stop

Heap now has:

* [2,4] len 3
* [1,4] len 4
* [3,6] len 4 (end 6)

**Remove expired (end < 3)**
Top [2,4] end 4 < 3? no → none removed.

**Answer** = 3
ans[idx1] = 3

---

### Process query = 4

**Add intervals with start <= 4**

* intervals[3] = [4,4] start 4 <= 4 → push
  Now i reaches end.

Heap contains:

* [4,4] len 1
* [2,4] len 3
* [1,4] len 4
* [3,6] len 4

**Remove expired (end < 4)**
Top is [4,4] end 4 < 4? no (equal is fine) → nothing removed.

**Answer** = top length = 1
ans[idx2] = 1

---

### Process query = 5

No more intervals to add (i already done).

Heap currently still has many intervals, but some don’t cover 5.

**Remove expired (end < 5)**
We keep popping while top’s end < 5:

* Top [4,4] has end 4 < 5 → pop it
* Now top likely [2,4] end 4 < 5 → pop it
* Next top [1,4] end 4 < 5 → pop it
* Next top [3,6] end 6 < 5? no → stop

Now heap top is [3,6] which *does* cover 5.

**Answer** = len([3,6]) = 4
ans[idx3] = 4

Final answer: `[3, 3, 1, 4]`

---

## Key takeaways

* Sort queries so you can sweep from small to large.
* Push intervals when they become eligible (`start <= q`).
* Pop intervals when they become impossible (`end < q`).
* Heap ordered by length gives the minimum covering interval.
*/

import java.util.*;

class Solution {
    public int[] minInterval(int[][] intervals, int[] queries) {
        // 1) Sort intervals by start
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // 2) Prepare queries with original indices, sort by query value
        int m = queries.length;
        int[][] qArr = new int[m][2]; // [queryValue, originalIndex]
        for (int i = 0; i < m; i++) {
            qArr[i][0] = queries[i];
            qArr[i][1] = i;
        }
        Arrays.sort(qArr, (a, b) -> Integer.compare(a[0], b[0]));

        // 3) Min-heap by interval length, tie by end
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
            int lenA = a[1] - a[0] + 1;
            int lenB = b[1] - b[0] + 1;
            if (lenA != lenB) return Integer.compare(lenA, lenB);
            return Integer.compare(a[1], b[1]);
        });

        int[] ans = new int[m];
        int i = 0; // pointer for intervals

        // 4) Sweep queries
        for (int[] q : qArr) {
            int queryVal = q[0];
            int originalIdx = q[1];

            // Add all intervals that start <= queryVal
            while (i < intervals.length && intervals[i][0] <= queryVal) {
                pq.add(intervals[i]);
                i++;
            }

            // Remove intervals that end < queryVal (can't cover this query)
            while (!pq.isEmpty() && pq.peek()[1] < queryVal) {
                pq.poll();
            }

            // Answer is min length among remaining intervals
            if (pq.isEmpty()) {
                ans[originalIdx] = -1;
            } else {
                int[] best = pq.peek();
                ans[originalIdx] = best[1] - best[0] + 1;
            }
        }

        return ans;
    }
}





// Method 2: Binary Search + Ordered Set approach
/*
Yep — what you found is a **binary-search + ordered-set** approach (often described as “sort intervals by length, then assign answers to all queries they cover using lower_bound”). In Java, the closest equivalent to C++ `set` with `lower_bound` + `erase(it)` is a **`TreeMap`** (or `TreeSet` with a custom object), and we’ll use it to “consume” queries once they’re answered.

This approach is **correct and efficient**: each query is inserted once and removed once, so overall it’s about
**O(N log N + Q log Q + (N + Q) log Q)**.

---

## Idea in one sentence

Process intervals in **increasing length**; for each interval `[l, r]`, binary-search the smallest query `>= l`, and while that query `<= r`, set its answer to this interval’s length (it’s the smallest possible), then remove it from the set.

Because we process intervals from smallest length to largest, the **first time a query is covered** is guaranteed to be its optimal answer.

---

## Why it works (key proof intuition)

* We process intervals by length from small → large.
* When we assign an answer to a query using interval `[l, r]`, that query lies inside `[l, r]`.
* Any later interval we might process has length **≥ current length**, so it cannot provide a smaller answer.
* Therefore once assigned, the answer is final, and we can remove that query to avoid extra work.

---

## Java implementation using `TreeMap<Integer, List<Integer>>`

We need to store:

* query value → list of original indices having that value (since queries can repeat)

We’ll use:

* `TreeMap<Integer, ArrayDeque<Integer>> map`

  * key = query value
  * value = queue/deque of indices where this query appeared

Then:

* `map.ceilingEntry(l)` gives the first query value ≥ `l` (this is the binary search).
* We keep taking entries while key ≤ `r`, assign answers, and remove those keys.


## Thorough example walkthrough

Use the same example:

```text
intervals = [[1,4],[2,4],[3,6],[4,4]]
queries   = [2,3,4,5]
```

### Step 1: Sort intervals by length

Compute lengths:

* [4,4] length 1
* [2,4] length 3
* [1,4] length 4
* [3,6] length 4

Sorted (one valid order):

1. [4,4] (len=1)
2. [2,4] (len=3)
3. [1,4] (len=4)
4. [3,6] (len=4)

### Step 2: Put queries into TreeMap

Queries with indices:

* 2 → [idx0]
* 3 → [idx1]
* 4 → [idx2]
* 5 → [idx3]

So the TreeMap keys in order: `2, 3, 4, 5`

Answers start as:

```text
ans = [-1, -1, -1, -1]
```

---

### Process interval [4,4], len=1

We binary-search: `ceilingEntry(4)` → key `4`.

Now check if key ≤ r=4 → yes.

* key=4 is inside [4,4]
* So queries with value 4 get answer 1:

  * ans[idx2] = 1
* Remove key 4 from map.

Map keys now: `2, 3, 5`
ans: `[-1, -1, 1, -1]`

Next `ceilingEntry(4)` → key `5`, but `5 <= 4` is false → stop.

---

### Process interval [2,4], len=3

Binary-search: `ceilingEntry(2)` → key `2`

While key ≤ 4:

* key=2 is inside [2,4]

  * ans[idx0] = 3
  * remove key 2
  * map keys: `3,5`

Next `ceilingEntry(2)` → key `3` (since 2 removed)

* key=3 is inside [2,4]

  * ans[idx1] = 3
  * remove key 3
  * map keys: `5`

Next `ceilingEntry(2)` → key `5`

* 5 ≤ 4? no → stop.

Now:

```text
ans = [3, 3, 1, -1]
map keys = {5}
```

---

### Process interval [1,4], len=4

Binary-search: `ceilingEntry(1)` → key `5`

* But 5 ≤ 4? no → interval doesn’t cover any remaining query. Nothing changes.

---

### Process interval [3,6], len=4

Binary-search: `ceilingEntry(3)` → key `5`

* 5 ≤ 6? yes → covered.

Assign:

* ans[idx3] = 4
  Remove key 5.
  Map is empty.

Final:

```text
ans = [3, 3, 1, 4]
```

Which is correct.

---

## Why this is “binary search approach”

The binary search part is exactly:

* “find the first query ≥ start”
  in an ordered structure (`TreeMap.ceilingEntry`), which is the Java equivalent of C++ `set.lower_bound`.

Then you advance through the ordered keys while they stay ≤ end, deleting as you go.

---

## When to prefer which solution

* **Heap + sorted queries** is the most common editorial solution.
* **This TreeMap/ordered-set** method is also excellent and often simpler once you see it, especially because it avoids needing to pop expired intervals repeatedly.

Both pass.

---

If you want, I can also show:

* A version using `TreeSet<Integer>` plus a separate map from query value → list of indices,
* Or an `int[][] queriesWithIndex` version that handles duplicates a bit differently.
*/

// class Solution {
//     public int[] minInterval(int[][] intervals, int[] queries) {
//         int n = intervals.length;
//         int m = queries.length;
//         int[] ans = new int[m];
//         Arrays.fill(ans, -1);

//         // 1) Sort intervals by length (end - start + 1), tie-break optional
//         Arrays.sort(intervals, (a, b) -> {
//             int lenA = a[1] - a[0] + 1;
//             int lenB = b[1] - b[0] + 1;
//             if (lenA != lenB) return Integer.compare(lenA, lenB);
//             // tie-break: smaller start or end, doesn't matter for correctness
//             return Integer.compare(a[0], b[0]);
//         });

//         // 2) Build TreeMap from query value -> indices with that value
//         TreeMap<Integer, ArrayDeque<Integer>> map = new TreeMap<>();
//         for (int i = 0; i < m; i++) {
//             map.computeIfAbsent(queries[i], k -> new ArrayDeque<>()).add(i);
//         }

//         // 3) Sweep intervals from smallest length to largest
//         for (int[] in : intervals) {
//             int l = in[0], r = in[1];
//             int len = r - l + 1;

//             // Find the first query >= l
//             Map.Entry<Integer, ArrayDeque<Integer>> entry = map.ceilingEntry(l);

//             // Consume all query keys within [l, r]
//             while (entry != null && entry.getKey() <= r) {
//                 int qVal = entry.getKey();
//                 ArrayDeque<Integer> idxs = entry.getValue();

//                 // All queries with this exact value get the same answer
//                 while (!idxs.isEmpty()) {
//                     ans[idxs.poll()] = len;
//                 }

//                 // Remove this key and move to next key >= l
//                 map.remove(qVal);
//                 entry = map.ceilingEntry(l); // could also use ceilingEntry(qVal+1), l is fine
//             }

//             // Optional small optimization: if all queries answered, we can stop early
//             if (map.isEmpty()) break;
//         }

//         return ans;
//     }
// }
