// Meeting 1: Greedy approach using sorting by end of interval
/*
For this problem the greedy idea is:

> **Keep as many non-overlapping intervals as possible by always picking the interval that ends earliest.**
> Then answer = `total intervals - intervals kept`.

Let’s go slowly and really walk through what happens.

---

## 1. Greedy algorithm (concept + code)

### Intuition

* If you pick an interval with a **small end time**, it leaves more space for future intervals.
* If you pick one that ends later than necessary, you risk losing future options.

So:

1. **Sort intervals by their end** (`interval[1]`).
2. Always take the interval whose **start** is ≥ **end of the last chosen** interval.
3. Count how many intervals you take.
4. Result = `n - count`.


Now let’s see this in action with detailed step-by-step examples.

---

## 2. Example 1 (classic LC example)

Say:

```text
intervals = [[1,2], [2,3], [3,4], [1,3]]
```

We want the **minimum number of intervals to remove** so that the rest don’t overlap.

### Step 1: Sort by end

Sort by `end` (second value):

* `[1,2]`  (end 2)
* `[2,3]`  (end 3)
* `[1,3]`  (end 3) – ties can be in any order but this is fine
* `[3,4]`  (end 4)

So sorted:

```text
[1,2], [2,3], [1,3], [3,4]
```

### Step 2: Initialize

* `countNonOverlap = 1` (we choose the first interval `[1,2]`)
* `prevEnd = 2`

We now scan from `i = 1`.

---

### Step 3: i = 1 → interval `[2, 3]`

* `start = 2`, `end = 3`
* Check overlap with the last chosen interval `[1,2]`:

  ```text
  Does [2,3] overlap [1,2]?
  Condition: start >= prevEnd ?
  2 >= 2 → yes, it's NON-overlapping (touching at endpoint is allowed)
  ```

Because we treat overlap as **strict** (i.e., intervals overlap only if `start < prevEnd`), `[1,2]` and `[2,3]` are considered **non-overlapping**.

So we **keep** it:

* `countNonOverlap++` → `countNonOverlap = 2`
* `prevEnd = 3`

Chosen intervals so far:
`[1,2]`, `[2,3]`.

---

### Step 4: i = 2 → interval `[1, 3]`

* `start = 1`, `end = 3`
* Check overlap with last chosen `[2,3]`:

  ```text
  start >= prevEnd ?
  1 >= 3 ? → false
  ```

So `[1,3]` **overlaps** with `[2,3]`.

Greedy choice:

* **Do not** take `[1,3]`.
* Keep `[2,3]` because it ends earlier or equal to `[1,3]` (both end at 3, so either is fine, but we already chose `[2,3]`).

No changes:

* `countNonOverlap = 2`
* `prevEnd = 3`
* Chosen intervals remain `[1,2]`, `[2,3]`.

We are implicitly choosing to "remove" `[1,3]`.

---

### Step 5: i = 3 → interval `[3, 4]`

* `start = 3`, `end = 4`
* Check overlap with last chosen `[2,3]`:

  ```text
  start >= prevEnd ?
  3 >= 3 ? → true
  ```

So there is **no overlap** (again, touching endpoints is fine). We take it:

* `countNonOverlap++` → `countNonOverlap = 3`
* `prevEnd = 4`

Chosen intervals:
`[1,2]`, `[2,3]`, `[3,4]`

---

### Step 6: Compute answer

Total intervals `n = 4`, non-overlapping intervals kept `countNonOverlap = 3`.

Minimum to remove:

```text
result = n - countNonOverlap = 4 - 3 = 1
```

So the algorithm removes exactly 1 interval (which is optimal: remove `[1,3]`).

---

## 3. Example 2 (the "danger" case where sorting by start fails)

Take the set we used earlier to show the bug in your original solution:

```text
intervals = [[1,10], [2,3], [4,5], [6,7]]
```

Intuitively:

* `[1,10]` overlaps everything.
* Best is to **remove `[1,10]` once**, and keep the three small ones `[2,3]`, `[4,5]`, `[6,7]`.
* Answer should be `1`.

Let’s see how the **end-sorted greedy** handles this.

### Step 1: Sort by end

* `[2,3]`  (end 3)
* `[4,5]`  (end 5)
* `[6,7]`  (end 7)
* `[1,10]` (end 10)

Sorted:

```text
[2,3], [4,5], [6,7], [1,10]
```

### Step 2: Initialize

* `countNonOverlap = 1` (take `[2,3]`)
* `prevEnd = 3`

---

### Step 3: i = 1 → `[4,5]`

* `start = 4`, `end = 5`
* Check: `start >= prevEnd` → `4 >= 3` → true
  → Non-overlapping, so we **keep** it.

Update:

* `countNonOverlap = 2`
* `prevEnd = 5`
* Chosen: `[2,3]`, `[4,5]`

---

### Step 4: i = 2 → `[6,7]`

* `start = 6`, `end = 7`
* Check: `start >= prevEnd` → `6 >= 5` → true
  → Non-overlapping, keep.

Update:

* `countNonOverlap = 3`
* `prevEnd = 7`
* Chosen: `[2,3]`, `[4,5]`, `[6,7]`

---

### Step 5: i = 3 → `[1,10]`

* `start = 1`, `end = 10`
* Check: `start >= prevEnd` → `1 >= 7`? → false
  → It **overlaps** with `[6,7]` (and actually overlaps everything).

We **skip** `[1,10]` (i.e., treat it as removed) because it comes later in end-sorted order and overlaps our existing chosen set.

---

### Step 6: Compute answer

Total intervals `n = 4`, non-overlapping intervals kept `countNonOverlap = 3`.

```text
result = 4 - 3 = 1
```

Exactly what we expect.

Notice how the end-time sorting automatically forces us to:

* First choose short, early-ending intervals,
* And only then consider long ones like `[1,10]`, which we reject if they overlap.

This is the **core of the greedy proof**: choosing the earliest ending interval is always safe and leads to a maximal set of non-overlapping intervals.

---

## 4. How this relates to your “sort by start” version

The version I suggested to “patch” your code sorts by **start** but, when there’s an overlap, it manually enforces the *same greedy principle*:

```java
if (intervals[i][0] < maxEnd) {
    count++;
    maxEnd = Math.min(maxEnd, intervals[i][1]); // keep the one that ends earlier
} else {
    maxEnd = intervals[i][1];
}
```

* On overlap, instead of automatically discarding the “new” interval, we keep whichever one has **smaller end**.
* That effectively mimics the behavior of end-time sorting.

So:

* **Canonical approach:** sort by end directly, count kept intervals, return `n - kept`.
* **Your adjusted approach:** sort by start, but in the overlap case explicitly keep the smaller end. That’s logically equivalent in terms of what ends up being kept.
*/
class Solution {
    public int eraseOverlapIntervals(int[][] intervals) {
        int n = intervals.length;
        if (n <= 1) return 0;

        // 1) Sort by end time
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));

        // 2) Pick the first interval
        int countNonOverlap = 1;
        int prevEnd = intervals[0][1];

        // 3) Greedily pick intervals
        for (int i = 1; i < n; i++) {
            int start = intervals[i][0];
            int end = intervals[i][1];

            // If this interval does not overlap the previous chosen one
            if (start >= prevEnd) {
                countNonOverlap++;
                prevEnd = end;  // update the "current" end
            }
        }

        // 4) Minimum to remove = total - kept
        return n - countNonOverlap;
    }
}




// Method 1.5: My Greedy approach using sorting by start of interval
/* 
# WHAT I WAS DOING WRONG:
My approach keeps minimum number of intervals but we need to do the opposite by 
removing minimum number of intervals and keeping as many intervals as we can.

The main issue is that your **greedy choice is wrong in the overlap case**, so you can return a larger-than-necessary removal count on some inputs.

## What your code is doing

You:

1. Sort by **start** time:

   ```java
   Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
   ```

2. Track a current merged interval via:

   ```java
   int minStart = intervals[0][0];
   int maxEnd = intervals[0][1];
   ```

3. For each next interval:

   ```java
   if (intervals[i][0] < maxEnd) {
       // overlap
       count++;              // we "remove" this one
   } else {
       // no overlap
       minStart = intervals[i][0];
       maxEnd = intervals[i][1];
   }
   ```

So if the new interval overlaps the current one, you always increment `count` and implicitly “remove” the new one, and you **keep the old interval unchanged**.

---

## Why that greedy is wrong

For this problem, the optimal greedy strategy is:

> When two intervals overlap, **keep the one that ends earlier** (smaller end), because it leaves more room for future intervals.

Your code does the opposite in some cases:
it always keeps the *earlier-starting* interval, no matter how long it is.

### Counterexample

Take:

```java
int[][] intervals = {
    {1, 10},
    {2, 3},
    {4, 5},
    {6, 7}
};
```

Correct answer: **1** interval removed
(better to remove `[1,10]` and keep the three small ones).

Let’s see what your code does:

Sorted by start, it's already:

```text
[1, 10], [2, 3], [4, 5], [6, 7]
```

* Start: `minStart = 1`, `maxEnd = 10`, `count = 0`

**i = 1**, interval `[2,3]`:

* `2 < 10` → overlap → `count = 1`
* But `maxEnd` stays `10`

**i = 2**, interval `[4,5]`:

* `4 < 10` → overlap → `count = 2`
* Still keep `maxEnd = 10`

**i = 3**, interval `[6,7]`:

* `6 < 10` → overlap → `count = 3`

You return **3**, which is wrong.

Why? Because you always “remove” the new shorter interval, even when you *should* remove the long one `[1,10]`.

---

## How to fix it (minimal change)

Still sorting by start is okay, but in the overlap branch, you must **keep the interval with smaller end**:

```java
if (intervals[i][0] < maxEnd) {
    // overlap → remove one interval
    count++;
    // keep the one that ends earlier
    maxEnd = Math.min(maxEnd, intervals[i][1]);
} else {
    // no overlap → move window
    maxEnd = intervals[i][1];
}
```

`minStart` isn’t actually needed; you can drop it.

So a fixed version in your style:

```java
class Solution {
    public int eraseOverlapIntervals(int[][] intervals) {
        int n = intervals.length;
        if (n <= 1) {
            return 0;
        }

        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

        int count = 0;
        int maxEnd = intervals[0][1];

        for (int i = 1; i < n; i++) {
            // Overlap
            if (intervals[i][0] < maxEnd) {
                count++;
                // Keep the interval with smaller end
                maxEnd = Math.min(maxEnd, intervals[i][1]);
            } else {
                // No overlap, advance the window
                maxEnd = intervals[i][1];
            }
        }

        return count;
    }
}
```

Alternatively, the classic approach is:

* Sort by **end** time,
* Greedily count how many intervals you can keep,
* Answer = `n - keepCount`.

But the key bug in your original code is:
**on overlap, you never update `maxEnd` to the smaller end, so you keep a suboptimal long interval and over-count removals.**

*/
// class Solution {
//     public int eraseOverlapIntervals(int[][] intervals) {
//         int n = intervals.length;
//         if (n <= 1) {
//             return 0;
//         }

//         Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

//         int count = 0;
//         int minEnd = intervals[0][1];

//         for (int i = 1; i < n; i++) {
//             // Overlap
//             if (intervals[i][0] < minEnd) {
//                 count++;
//                 // Keep the interval with smaller end
//                 minEnd = Math.min(minEnd, intervals[i][1]);
//             } else {
//                 // No overlap, advance the window
//                 minEnd = intervals[i][1];
//             }
//         }

//         return count;
//     }
// }








// Method 2: Dynamic Programming approach
/*
We’ll treat this as a **“select max non-overlapping intervals”** DP problem and then convert that to “min removals”.

---

## 1. Problem restatement

Given `intervals[i] = [start_i, end_i]`, we want to remove the **fewest** intervals so that the remaining ones **don’t overlap**.

Equivalent view:

> Maximize the number of non-overlapping intervals we **keep**, then
> `answer = total_intervals - max_non_overlapping`.

This is basically the unweighted version of **interval scheduling / weighted interval scheduling**.

---

## 2. Sorting for DP

First, sort the intervals by **end time** (second coordinate):

```java
Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));
```

Why by end?

* Intervals that finish earlier leave more room to fit others afterward.
* This ordering is what makes both the greedy and the DP transitions clean.

Let `n = intervals.length`.

---

## 3. DP definition

We build a 1D DP:

> `dp[i]` = **maximum number of non-overlapping intervals we can pick from `intervals[0..i]`** (i.e., among the first `i+1` intervals) **such that the i-th interval may or may not be included, whichever is best**.

Base:

```java
dp[0] = 1; // with only one interval, we can take it
```

For `i > 0`, we have two choices:

1. **Don’t take** interval `i`
   → Then best we can do is `dp[i-1]`.

2. **Take** interval `i`
   → Then we must ensure we don’t overlap with the **last chosen interval** before `i`.

To compute option 2, we need to find some index `j < i` such that:

* Interval `j` **does not overlap** interval `i`, i.e., `intervals[j][1] <= intervals[i][0]` (end_j ≤ start_i).
* Among such `j`, we want the one that gives the best `dp[j]`.

So:

```java
bestPrev = 0 or max dp[j] where 0 <= j < i and intervals[j][1] <= intervals[i][0]
take_i = 1 + bestPrev
```

If **no** such `j` exists (interval `i` can’t follow any earlier interval), then `take_i = 1` (we take this interval alone).

Finally:

```java
dp[i] = Math.max(dp[i-1], take_i);
```

Answer:

```java
maxNonOverlap = dp[n-1];
minToRemove   = n - maxNonOverlap;
```

---

## 4. Time complexity

* Naive DP: for each `i`, we scan backwards over all `j < i` → `O(n^2)`.
* Memory: `O(n)`.

(You *can* optimize to `O(n log n)` by precomputing a “previous non-overlapping index” using binary search on end times, but let’s keep it simple and clear for now.)


Key detail:
Because intervals are sorted by **end time**, when we scan `j` backward and find the **first** non-overlapping interval (with `intervals[j][1] <= intervals[i][0]`), that `j` is already the one with the **largest possible `dp[j]`** among all candidates (since `dp` is non-decreasing with `i`). So we can safely `break` at the first compatible `j`.

---

## 6. Detailed example walkthrough

Let’s use the standard example:

```text
intervals = [[1,2], [2,3], [3,4], [1,3]]
```

### Step 1: Sort by end

Sort by `end`:

Original:       [1,2], [2,3], [3,4], [1,3]
Ends:              2      3      4      3

Sorted by end:

1. `[1,2]`  (end 2)
2. `[2,3]`  (end 3)
3. `[1,3]`  (end 3)  ← tie, order between [2,3] and [1,3] doesn’t matter
4. `[3,4]`  (end 4)

So after sorting:

```text
i=0: [1,2]
i=1: [2,3]
i=2: [1,3]
i=3: [3,4]
```

We will now fill `dp[0..3]`.

---

### Step 2: Initialize

* `dp[0] = 1`
  Using only interval `[1,2]`, best we can do is take it.

Current `dp`:

```text
dp[0] = 1
dp[1] = ?
dp[2] = ?
dp[3] = ?
```

---

### Step 3: i = 1 → interval `[2,3]`

We consider all intervals from `0` to `1`:

* Option 1: **Skip** `[2,3]`:
  `best = dp[0] = 1` (we only keep `[1,2]`)

* Option 2: **Take** `[2,3]`:

  We need a `j < 1` that doesn’t overlap with `[2,3]`:

  * Check `j = 0`: `[1,2]` end = 2, start of `[2,3]` = 2
    → `2 <= 2` → **non-overlapping** (touching at the boundary is allowed).

  So we can chain `[1,2]` → `[2,3]`:

  ```text
  take = 1 + dp[0] = 1 + 1 = 2
  ```

  We break here since this is the first compatible `j` going backward and we know it’s optimal.

Now:

```text
dp[1] = max(best, take) = max(1, 2) = 2
```

Interpretation: Among `[1,2]` and `[2,3]`, best non-overlapping set has size 2, namely `[1,2], [2,3]`.

---

### Step 4: i = 2 → interval `[1,3]`

We consider intervals `[1,2]`, `[2,3]`, `[1,3]`.

* Option 1: **Skip** `[1,3]`:

  ```text
  best = dp[1] = 2   // we can keep [1,2] and [2,3]
  ```

* Option 2: **Take** `[1,3]`:

  We must find some `j < 2` with no overlap: `intervals[j][1] <= intervals[2][0]`:

  Interval `i = 2` is `[1,3]` → start = 1.

  Check backwards:

  * `j = 1`: interval `[2,3]` end = 3.
    Condition: `3 <= 1`? → **false** → overlaps.
  * `j = 0`: interval `[1,2]` end = 2.
    Condition: `2 <= 1`? → **false** → also overlaps.

No non-overlapping predecessor found. So `[1,3]` can only be taken **alone** in this prefix:

```text
take = 1
```

Now:

```text
dp[2] = max(best, take) = max(2, 1) = 2
```

Interpretation: Among `[1,2], [2,3], [1,3]`, the best non-overlapping set is still size 2
(e.g. `[1,2], [2,3]` is better than just `[1,3]`).

Current `dp` state:

```text
dp[0] = 1
dp[1] = 2
dp[2] = 2
dp[3] = ?
```

---

### Step 5: i = 3 → interval `[3,4]`

Now consider all 4 intervals.

* Option 1: **Skip** `[3,4]`:

  ```text
  best = dp[2] = 2
  ```

* Option 2: **Take** `[3,4]`:

  Find a `j < 3` with no overlap: `intervals[j][1] <= intervals[3][0]`:

  Interval `i = 3` is `[3,4]` → start = 3.

  Check backwards:

  * `j = 2`: `[1,3]` end = 3.
    `3 <= 3` → non-overlapping (again, touching is okay).

    So we **could** chain `[1,3]` → `[3,4]`
    → `take = 1 + dp[2] = 1 + 2 = 3`.

    Wait, is that logically valid?
    Among the first 3 intervals, `dp[2] = 2` is using the best set, which we know is `[1,2], [2,3]`, not `[1,3]`. But the DP definition is “best among the first `i+1` intervals”, not necessarily ending at `j`. The trick is: if we take `[3,4]`, the immediate predecessor must be an interval that **ends before or at 3** and is the **last in that chosen chain**.

    This is why some solutions actually define `dp[i]` as “best **when taking interval i as last**”. To keep it consistent with our current setup, the `break` logic actually works best when we think of `dp` as non-decreasing by i, which it is, but the more precise definition people frequently use is:

    > `dp[i]` = the maximum number of non-overlapping intervals among those **ending at or before** `intervals[i]`, with `intervals[i]` included.

  To avoid confusion, let’s do the more standard form now (see below). But conceptually, we know we can achieve 3 intervals.

Even with the simpler “formal” DP, you end up with:

```text
dp[3] = 3
```

So final `dp`:

```text
dp[0] = 1
dp[1] = 2
dp[2] = 2
dp[3] = 3
```

---

### 6. Answer

* Maximum non-overlapping intervals = `dp[3] = 3`
  (e.g., `[1,2]`, `[2,3]`, `[3,4]`).
* Total intervals `n = 4`.

So **minimum intervals to remove**:

```text
4 - 3 = 1
```

Matches the greedy approach and the expected answer.

---

## 7. Alternative (cleaner) DP definition (if you like)

A slightly cleaner way to define DP (common in weighted-interval-scheduling style):

> After sorting by end:
> `dp[i]` = **maximum number of non-overlapping intervals if we are forced to take interval `i` as the last one in the subset**.

Then:

* For each `i`, find the rightmost `j < i` where `intervals[j][1] <= intervals[i][0]`.
* If such `j` exists: `dp[i] = 1 + dp[j]`, else `dp[i] = 1`.

Final answer is `max(dp[i])` over all `i`, and result = `n - max(dp[i])`.

This may be a bit conceptually cleaner but requires a separate loop to find that `j` for each `i` (again `O(n^2)` if done naively, or `O(n log n)` with binary search).

---

If you want, next I can:

* Rewrite the DP in that “must-include-i” version, or
* Show you how to switch from this `O(n^2)` DP to an `O(n log n)` DP using binary search on ends.
*/

// class Solution {
//     public int eraseOverlapIntervals(int[][] intervals) {
//         int n = intervals.length;
//         if (n <= 1) return 0;

//         // 1) Sort by end time
//         Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));

//         // 2) dp[i] = max non-overlapping intervals among intervals[0..i]
//         int[] dp = new int[n];
//         dp[0] = 1;  // with first interval, best you can do is take it

//         for (int i = 1; i < n; i++) {
//             // Option 1: skip interval i
//             int best = dp[i - 1];

//             // Option 2: take interval i
//             int take = 1;  // take this alone if no compatible previous

//             // Find best j < i that doesn't overlap i
//             for (int j = i - 1; j >= 0; j--) {
//                 if (intervals[j][1] <= intervals[i][0]) {
//                     // intervals[j] ends before or at the start of intervals[i]
//                     take = Math.max(take, 1 + dp[j]);
//                     break; // because we sorted by end time, the first such j going backwards is optimal
//                 }
//             }

//             dp[i] = Math.max(best, take);
//         }

//         int maxNonOverlap = dp[n - 1];
//         return n - maxNonOverlap;
//     }
// }
