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


// Method 1: Min-Heap of end times
/*
# WHAT WAS I DOING WRONG:

Yeah — this one is **not correct** for Meeting Rooms II.

### What’s fundamentally wrong

You’re tracking only **one** “current end” (`maxEnd`) (plus a `prevMaxEnd` hack). But Meeting Rooms II needs you to track **all ongoing meetings** (or at least the *minimum* end among them) because multiple meetings can overlap in complex ways.

In other words, the number of rooms required at time `t` equals the number of meetings active at `t`. With only one `maxEnd`, you can’t know how many are active — you only know something about one interval.

Also, `prevMaxEnd != maxEnd` is not a valid condition for anything meaningful here; it can accidentally prevent increments or cause increments at the wrong times.

---

## Small counterexample where your code fails

Consider:

```text
[0, 30], [5, 10], [10, 15]
```

Correct answer: **2** rooms

* Meeting A: [0,30]
* Meeting B: [5,10] overlaps with A → need second room
* Meeting C: [10,15] overlaps with A, but B ended exactly at 10 → still 2 rooms

Now trace your code:

Sorted by start: same.

* init: `maxEnd = 30`, `prevMaxEnd = -1`, `count = 1`

i=1 interval [5,10]:

* `start 5 < maxEnd 30` true and `prevMaxEnd != maxEnd` (-1 != 30) true
* `count = 2`, `prevMaxEnd = 30`
* (maxEnd stays 30)

i=2 interval [10,15]:

* `start 10 < maxEnd 30` true BUT `prevMaxEnd != maxEnd` is false (30 != 30 false)
* goes to else: `maxEnd = 15`

Return `count = 2` (looks okay here, but the internal state is now nonsense: it “forgot” about the long meeting ending at 30).

Now watch it break with a tiny tweak:

```text
[0, 30], [5, 10], [6, 15]
```

Correct answer: **3** rooms
At time 6–10, all three meetings are active.

Your code:

* init `maxEnd=30`, `count=1`
* [5,10] overlaps → `count=2`, `prevMaxEnd=30`
* [6,15] overlaps with maxEnd=30, but `prevMaxEnd == maxEnd` so you **don’t increment**
  and instead set `maxEnd=15`
* returns **2**, but correct is **3**

So it undercounts because it can’t represent “two meetings already active when a third arrives”.

---

## Another failure mode: resetting `maxEnd` incorrectly

This line:

```java
else { maxEnd = intervals.get(i).end; }
```

Even when there was overlap earlier, this can replace `maxEnd` with a smaller end time and effectively “pretend” the longest-running meeting ended earlier than it did, reducing future overlaps incorrectly.


### 1) Min-heap of end times (most common)

* Sort by start
* Keep a min-heap of end times of active meetings
* If the next meeting starts after (or at) the smallest end, you can reuse that room (pop)
* Always push this meeting’s end
* Max heap size = rooms needed


Goal: minimum number of rooms so that no meetings in the **same** room overlap.

Core observation:

* At any moment, the number of rooms you need equals the number of meetings that are currently **ongoing**.
* So as we scan meetings in chronological order of **start time**, we want to know: “how many meetings haven’t ended yet?”

A **min-heap (priority queue)** of end times lets us efficiently track the meeting that ends the earliest among those currently active.

---

## Algorithm idea

1. **Sort meetings by start time**.
2. Maintain a min-heap `pq` that stores the **end times** of meetings currently using rooms.

   * The smallest end time is at the top (`pq.peek()`): that’s the room that frees up first.
3. For each meeting `(start, end)` in sorted order:

   * If the earliest ending meeting ends **on or before** this meeting starts (`pq.peek() <= start`), we can **reuse** that room:

     * `pq.poll()` (remove that end time)
   * Whether reused or not, we now occupy a room until `end`:

     * `pq.add(end)`
4. The maximum size the heap ever reaches is the minimum number of rooms needed.

   * In practice, if we process all meetings, the answer is simply `pq.size()` **after processing all meetings** if we always reuse whenever possible (because the heap size evolves correctly). Many implementations also track `maxSize` explicitly; both are fine.


Key detail: use `<=` for reuse, because `[1,3]` and `[3,5]` do not overlap.

---

## Thorough example walkthrough

### Example A

Meetings:

```text
[(0,30), (5,10), (15,20)]
```

#### Step 1: Sort by start

Already sorted:

* (0,30)
* (5,10)
* (15,20)

Initialize:

* `pq = []` (stores end times)

---

### Process (0,30)

* `pq` is empty → no room to reuse
* Add end time 30

`pq = [30]`
Rooms in use: 1 (one meeting ends at 30)

---

### Process (5,10)

Check if we can reuse a room:

* `pq.peek() = 30`
* Is `30 <= 5`? No → earliest room is still busy at time 5

So we need a new room:

* Add end time 10

`pq = [10, 30]` (min-heap; top is 10)
Rooms in use: 2
Interpretation: one meeting ends at 10, one ends at 30.

---

### Process (15,20)

Try to reuse:

* `pq.peek() = 10`
* Is `10 <= 15`? Yes → that room frees up before time 15

Reuse:

* `poll()` removes 10

Now add this meeting’s end 20:

* `pq.add(20)`

`pq = [20, 30]`
Rooms in use: 2

Done. Answer is `pq.size() = 2`.

✅ Minimum rooms = 2

---

## Why this works (intuition)

At each meeting start time, the only room you can reuse is one whose meeting already ended. Among all ongoing meetings, the one ending earliest is the first candidate to free up—so if **even that** hasn’t ended, no room has ended. That’s exactly what `pq.peek()` tells you.

---

## Example B (shows why you need a heap)

```text
[(0,30), (5,10), (6,15)]
```

Sorted by start: same.

* Start: `pq=[]`

Process (0,30):

* add 30 → `pq=[30]`

Process (5,10):

* peek 30 <= 5? no → add 10 → `pq=[10,30]` (2 rooms)

Process (6,15):

* peek 10 <= 6? no → add 15 → `pq=[10,30,15]` (3 rooms)

Answer = 3.

This is exactly the kind of case your previous attempt can’t handle: you need to know there are already *two* ongoing meetings when the third starts.
*/
class Solution {
    public int minMeetingRooms(List<Interval> intervals) {
        int n = intervals.size();
        if (n == 0) return 0;

        // 1) Sort by start time
        intervals.sort(Comparator.comparingInt(a -> a.start));

        // 2) Min-heap of end times (rooms currently in use)
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        // 3) Process meetings in start-time order
        for (Interval meeting : intervals) {
            int start = meeting.start;
            int end = meeting.end;

            // If the earliest room frees up by the time this meeting starts, reuse it
            if (!pq.isEmpty() && pq.peek() <= start) {
                pq.poll();
            }

            // Allocate (or re-allocate) a room for this meeting
            pq.add(end);
        }

        // Heap size == number of rooms needed
        return pq.size();
    }
}







// Method 2: Two-pointer sweep line approach
/*
This method avoids a heap by tracking **when meetings start** and **when meetings end** separately.

---

## Core idea

At any time, the number of rooms you need equals:

> **#meetings started so far − #meetings ended so far**

If we process events in time order:

* A **start** event needs a room (unless a meeting already ended and freed one).
* An **end** event frees a room.

Instead of building a combined event list, we do it with **two sorted arrays**:

* `starts[]` = all start times, sorted
* `ends[]` = all end times, sorted

Then we walk through `starts` using pointer `s` and keep a pointer `e` for the earliest ending meeting we haven’t “freed” yet.

---

## Algorithm steps

1. Extract all start times into `starts[]`, all end times into `ends[]`.
2. Sort both arrays.
3. Initialize:

   * `rooms = 0`
   * `maxRooms = 0`
   * `e = 0` (points to earliest end time not yet used to free a room)
4. For each start time `starts[s]` (s goes from 0..n-1):

   * If `starts[s] >= ends[e]`:

     * A meeting ended before (or exactly when) this one starts → **reuse** a room:

       * `e++` (consume one ended meeting)
       * (rooms stays the same, because one ended and one started)
   * Else:

     * No meeting has ended yet → need a **new** room:

       * `rooms++`
       * `maxRooms = max(maxRooms, rooms)`

Return `maxRooms`.

Important detail: use `>=` for reuse, because `[1,3]` and `[3,5]` do not overlap.


Why don’t we decrement `rooms` when we do `e++`?

* Because we’re processing a **start** right now.
* If `starts[s] >= ends[e]`, that means **one meeting ended**, freeing a room, and this meeting uses it immediately.
* Net rooms in use doesn’t increase, so `rooms` doesn’t change.

(If you prefer, you can track `roomsInUse` explicitly with `roomsInUse--` then `roomsInUse++`, but it cancels out.)

---

## Thorough example walkthrough

### Example 1

Intervals:

```text
[(0,30), (5,10), (15,20)]
```

#### Step 1: Build and sort arrays

Starts: `[0, 5, 15]`
Ends:   `[10, 20, 30]`

Pointers/vars:

* `rooms = 0`
* `maxRooms = 0`
* `e = 0` (ends[e] = 10)

---

### Process each start

#### s = 0 → start = 0

Compare with earliest end = `ends[e] = 10`

* Is `0 >= 10`? **No**
* No meeting has ended before 0 → need a new room

Update:

* `rooms = 1`
* `maxRooms = 1`
* `e = 0` unchanged

Interpretation: One meeting (0–30) is active.

---

#### s = 1 → start = 5

Earliest end still `ends[0] = 10`

* Is `5 >= 10`? **No**
* Still no meeting has ended by time 5 → need another room

Update:

* `rooms = 2`
* `maxRooms = 2`

Interpretation: At time 5, meetings (0–30) and (5–10) overlap → 2 rooms.

---

#### s = 2 → start = 15

Earliest end is still `ends[0] = 10`

* Is `15 >= 10`? **Yes**
* That means the meeting ending at 10 has finished before 15 → we can reuse a room.

So:

* `e = 1` (now earliest end is `ends[1] = 20`)
* `rooms` stays 2 (one ended, one started)

Interpretation: We reused the room freed by (5–10) for (15–20).
We still need 2 rooms overall.

Done → answer `maxRooms = 2`.

✅ Minimum rooms = 2

---

## Example 2 (the “needs 3 rooms” case)

Intervals:

```text
[(0,30), (5,10), (6,15)]
```

Starts: `[0, 5, 6]`
Ends:   `[10, 15, 30]`

Initialize:

* `rooms = 0`, `maxRooms = 0`, `e = 0` (ends[e] = 10)

### s = 0, start = 0

* `0 >= 10`? no → new room
* `rooms = 1`, `maxRooms = 1`

### s = 1, start = 5

* `5 >= 10`? no → new room
* `rooms = 2`, `maxRooms = 2`

### s = 2, start = 6

* `6 >= 10`? no → new room
* `rooms = 3`, `maxRooms = 3`

Done → answer `maxRooms = 3`.

✅ Minimum rooms = 3

At time 6–10, all three meetings are active.

---

## Example 3 (touching endpoints)

Intervals:

```text
[(1,3), (3,5), (5,6)]
```

Starts: `[1,3,5]`
Ends:   `[3,5,6]`

* start 1 vs end 3 → need room (rooms=1)
* start 3 vs end 3 → `3 >= 3` reuse (e=1), rooms stays 1
* start 5 vs end 5 → reuse (e=2), rooms stays 1

Answer = 1.

✅ Correct: they don’t overlap.

---

## Why this works (intuition)

* `ends[e]` always represents the **earliest finishing** ongoing meeting.
* If the next meeting starts after that, at least one room is free → reuse.
* If it starts before that, then **no** meetings have finished (since this is the earliest end) → must allocate a new room.
* `maxRooms` is the peak number of simultaneous meetings.
*/

// class Solution {
//     public int minMeetingRooms(List<Interval> intervals) {
//         int n = intervals.size();
//         if (n == 0) return 0;

//         int[] starts = new int[n];
//         int[] ends = new int[n];

//         for (int i = 0; i < n; i++) {
//             starts[i] = intervals.get(i).start;
//             ends[i] = intervals.get(i).end;
//         }

//         Arrays.sort(starts);
//         Arrays.sort(ends);

//         int rooms = 0;
//         int maxRooms = 0;
//         int e = 0; // pointer in ends[]

//         for (int s = 0; s < n; s++) {
//             if (starts[s] >= ends[e]) {
//                 // reuse a room: one meeting ended
//                 e++;
//             } else {
//                 // need a new room
//                 rooms++;
//                 maxRooms = Math.max(maxRooms, rooms);
//             }
//         }

//         return maxRooms;
//     }
// }
