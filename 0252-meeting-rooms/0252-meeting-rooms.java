/**
 * Definition of Interval:
 * public class Interval {
 *     public int start, end;
 *     public Interval(int start, int end) {
 *         this.start = start;
 *         this.end = end;
 *     }
 * }
 */


// Method 1: Sorting intervals based on start
/*
LeetCode 252 (Meeting Rooms) asks: given a list of meeting time intervals, can one person attend **all** meetings? 
That’s true iff **no two meetings overlap**.

An interval is typically `[start, end)` (start inclusive, end exclusive), so `[1,3]` and `[3,5]` do **not** overlap.

---

## Key idea

If you **sort meetings by start time**, then you only need to check **neighbors** for overlap.

Why neighbors are enough:

* After sorting, meeting `i` starts at or after meeting `i-1` starts.
* The only meeting that can “still be running” when meeting `i` begins is the one with the latest end among earlier ones—but in a sorted-by-start list, if `i` overlaps anything earlier, it must overlap the immediately previous one when we’ve been scanning in order (because we would have already detected earlier overlaps by the time we reached `i`).

So algorithm:

1. Sort intervals by `start`.
2. Scan from left to right:

   * If `current.start < previous.end`, overlap → return `false`.
3. If no overlap found → return `true`.

Time: `O(n log n)` for sorting, scan is `O(n)`.
Space: `O(1)` extra (ignoring sort overhead).


Important detail: we use `<`, not `<=`.

* If `curr.start == prev.end`, they “touch” but don’t overlap, so it’s fine.

---

## Thorough example walkthrough

### Example 1: Overlapping meetings → should return `false`

Input:

```text
[(0, 30), (5, 10), (15, 20)]
```

#### Step 1: Sort by start time

They’re already sorted by start:

1. (0, 30)
2. (5, 10)
3. (15, 20)

#### Step 2: Scan and compare neighbors

* Start with `i = 1`:

  * `prev = (0, 30)`
  * `curr = (5, 10)`
  * Check overlap: `curr.start < prev.end` → `5 < 30` → **true**
  * That means when the second meeting starts at 5, the first meeting hasn’t ended (it ends at 30).
  * So they overlap → return **false immediately**.

✅ Correct result: `false`

---

### Example 2: Non-overlapping meetings → should return `true`

Input:

```text
[(7, 10), (2, 4)]
```

#### Step 1: Sort by start time

Sort results:

1. (2, 4)
2. (7, 10)

#### Step 2: Scan

* `i = 1`:

  * `prev = (2, 4)`
  * `curr = (7, 10)`
  * Check: `7 < 4` → false
  * No overlap. Continue.

Reached end of loop, no overlaps found → return **true**.

✅ Correct result: `true`

---

### Example 3: Touching endpoints (edge case)

Input:

```text
[(1, 3), (3, 6), (6, 8)]
```

After sorting, same order.

Scan:

* Compare (1,3) vs (3,6):

  * `3 < 3` → false → OK
* Compare (3,6) vs (6,8):

  * `6 < 6` → false → OK

No overlap → return `true`.

This matches the standard interpretation that `[1,3]` and `[3,6]` do not overlap.

---

## Why sorting by start works (intuition)

Once sorted, you never need to compare a meeting with *all* previous meetings—only the previous one—because:

* the previous meeting is the one that starts latest among all earlier meetings,
* and if even that one ends before the current meeting starts, then every earlier meeting (which starts no later) must also have ended by then (or you would have found an overlap earlier in the scan).

---

If you want, I can also show the alternative approach (sort starts and ends separately, or use a min-heap) and explain when that becomes useful for Meeting Rooms II (LeetCode 253).
*/
class Solution {
    public boolean canAttendMeetings(List<Interval> intervals) {
        if(intervals.size() <= 1){
            return true;
        }

        intervals.sort(Comparator.comparingInt(a -> a.start));

        for(int i=1; i<intervals.size(); i++){
            if(intervals.get(i).start < intervals.get(i-1).end){
                return false;
            }
        }

        return true;
    }
}
