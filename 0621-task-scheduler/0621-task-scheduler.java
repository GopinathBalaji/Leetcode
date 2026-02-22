// Method 1: Greedy approach using Counting (does not need hashmap, array of length 26 is enough)
/*
Here are **progressive hints** for LeetCode **621. Task Scheduler**. (I’ll give you enough to derive the solution yourself, without dumping a full implementation.)

---

## Hint 1: You only care about frequencies

The order of tasks in the input doesn’t matter. What matters is **how many times each letter appears**.

Build an array `freq[26]`.

---

## Hint 2: Identify the “bottleneck” task(s)

Let:

* `maxFreq` = maximum frequency among tasks
* `numMax` = how many tasks have frequency == `maxFreq`

These drive the minimum schedule length.

---

## Hint 3: Think in “frames” around the most frequent task

Imagine the most frequent task is `A` with `maxFreq`.

Place them like this (there are `maxFreq - 1` gaps between them):

`A _ _ A _ _ A _ _ ... A`

There are **`maxFreq - 1` gaps** that must each be at least length `n` (cooldown).

---

## Hint 4: Turn the gaps into blocks of size `(n + 1)`

A common mental model:

Make `maxFreq - 1` blocks, each of length `(n + 1)`:

* one slot for the “dominant” task
* `n` slots that must be filled by other tasks or idles

So you get a base shape of:

* `(maxFreq - 1) * (n + 1)` slots

Then you still need to place the last occurrences of the most frequent tasks.

---

## Hint 5: Handle ties for the max frequency

If multiple tasks tie for maximum frequency (say A and B both appear `maxFreq` times), the last “row” contains `numMax` tasks, not just 1.

This is where `numMax` comes in.

---

## Hint 6: The key formula to aim for

The length forced by cooldown structure is:

[
\text{frameLen} = (maxFreq - 1) * (n + 1) + numMax
]

But you can’t do better than the total number of tasks, so the answer is:

[
\max(\text{tasks.length},\ \text{frameLen})
]

Derive *why* that `+ numMax` is needed by drawing the final “row” of tied max tasks.

---

## Hint 7: Quick sanity checks on edge cases

* If `n == 0`, there’s no cooldown → answer is just `tasks.length`.
* If you have enough other tasks to fill all idle slots, the result collapses to `tasks.length`.
* If one task dominates heavily, the formula will be larger than `tasks.length` (meaning you need idles).

---

## Hint 8: Walkthrough example to confirm your reasoning

Example: `tasks = [A,A,A,B,B,B], n = 2`

* `maxFreq = 3` (A and B)
* `numMax = 2` (A and B)

Compute:

* `frameLen = (3 - 1) * (2 + 1) + 2 = 2 * 3 + 2 = 8`
* `tasks.length = 6`
  Answer: `max(6, 8) = 8`

A valid schedule: `A B _ A B _ A B` (two idles)

---

## Alternate approach hint (if you prefer simulation)

You can also solve with:

* a **max-heap** by remaining count
* a **queue** that stores tasks in cooldown with “ready time”
  Each time unit: release ready tasks → pick best available task → decrement → push into cooldown.

This is more mechanical, but easy to reason about.

---

Below is a **very detailed walkthrough** of the greedy counting / formula approach for **621. Task Scheduler**, including *why the formula is shaped the way it is* and multiple examples (including ties).

---

# The greedy counting approach (what it computes)

1. Count frequency of each task.
2. Let:

* `maxFreq` = highest frequency
* `numMax` = number of task types that appear `maxFreq` times

3. Compute:
   [
   \text{frameLen} = (maxFreq - 1)(n + 1) + numMax
   ]
4. Answer:
   [
   \max(\text{tasks.length}, \text{frameLen})
   ]

---


# Walkthrough Example 1 (classic tie case)

### Input

`tasks = [A,A,A, B,B,B], n = 2`

### Step 1: Frequency count

* A: 3
* B: 3
  All others: 0

So:

* `maxFreq = 3`
* `numMax = 2` (A and B)

### Step 2: Compute frame length

[
(maxFreq-1)(n+1)+numMax = (3-1)(2+1)+2 = 2\cdot 3 + 2 = 8
]

### Step 3: Compare with total tasks

* tasks.length = 6
* frameLen = 8

Answer:
[
\max(6, 8) = 8
]

### What the “frame” looks like

We have `maxFreq - 1 = 2` full blocks of length `n+1 = 3`:

Blocks:

* Block 1: `___`
* Block 2: `___`
  Then the “tail” has `numMax = 2` tasks.

Now place the max-frequency tasks A and B in a grid-like view:

Row 1: `A _ _`
Row 2: `A _ _`
Tail:  `A`

and also for B:

Row 1: `B _ _`
Row 2: `B _ _`
Tail:  `B`

Combine by columns (one valid schedule):
`A B _  A B _  A B`

Length = 8, idles = 2. ✅

---

# Walkthrough Example 2 (no ties, heavy domination → lots of idles)

### Input

`tasks = [A,A,A,A, B,B, C,C], n = 2`

Count:

* A: 4
* B: 2
* C: 2

So:

* `maxFreq = 4`
* `numMax = 1` (only A)

Compute:
[
frameLen = (4-1)(2+1)+1 = 3\cdot 3 + 1 = 10
]
Total tasks = 8

Answer = max(8, 10) = **10**

### Construct the frame

We need `maxFreq-1 = 3` blocks of size 3:

`A _ _ | A _ _ | A _ _ | A`

That is 10 slots already:
`A _ _ A _ _ A _ _ A`

Now fill blanks with other tasks: we have B,B,C,C (4 tasks) and 6 blanks.
So 2 blanks become idle:

One valid fill:
`A B C  A B C  A _ _  A`
(underscores are idles)

Length 10 ✅

---

# Walkthrough Example 3 (enough tasks to fill cooldown → answer becomes tasks.length)

### Input

`tasks = [A,A,A, B,B,B, C,C,C, D,D,E], n = 2`

Count:

* A:3, B:3, C:3
* D:2
* E:1

So:

* `maxFreq = 3`
* `numMax = 3` (A,B,C)

Compute frame:
[
frameLen = (3-1)(2+1)+3 = 2\cdot 3 + 3 = 9
]
Total tasks = 11

Answer = max(11, 9) = **11**

### Why no idles?

The frame says “at least 9 slots are needed because of cooldown pressure.”
But we already have 11 tasks, which can fill all required gaps and more, so no idle is needed. The schedule length can’t be shorter than 11 anyway.

A valid schedule of length 11 exists (not unique):
`A B C  A B C  A B C  D D E`
Cooldown `n=2` is respected because the same letter is spaced by 2 other tasks.

---

# Walkthrough Example 4 (edge case n = 0)

### Input

`tasks = [A,A,A,B,B,C], n = 0`

Cooldown is 0, meaning tasks can repeat immediately.
So answer is simply `tasks.length = 6`.

Check formula:

* maxFreq = 3 (A)
* numMax = 1
  [
  frameLen = (3-1)(0+1)+1 = 2\cdot 1 + 1 = 3
  ]
  Answer = max(6,3) = 6 ✅

*/
class Solution {
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        int maxFreq = 0;
        int numMax = 0;

        for(char task: tasks){
            int f = ++freq[task - 'A'];
            
            if(f > maxFreq){
                maxFreq = f;
                numMax = 1;
            }else if(f == maxFreq){
                numMax++;
            }
        }

        return Math.max(tasks.length, (maxFreq - 1) * (n + 1) + numMax);
    }
}








// Method 2: Greedy Simulation approach using Heap (maxHeap to store remaining count) and queue (to store cooldown time for a task)
/*
Here’s the **heap simulation** solution for LeetCode 621 (**Task Scheduler**) — this is the “construct the schedule step-by-step” greedy approach using:

* a **max-heap** (pick the task with highest remaining count),
* a **cooldown queue** (tasks you *can’t* run yet, with the time they become available again).

---

## Core idea

At each time unit:

1. **Release** any tasks whose cooldown has finished back into the heap.
2. If the heap is not empty:

   * **Run** the task with the highest remaining count.
   * Decrease its count.
   * If it still has remaining occurrences, put it into cooldown with a “ready time”.
3. If the heap is empty but cooldown queue isn’t:

   * You **idle** for this time unit.

Repeat until **both** structures are empty.

### Why the “ready time” is `time + n + 1`

If you run task `A` at time `t`, and cooldown is `n`, you need at least `n` intervals *between* two `A`s.
So the earliest next time you can run `A` is:

* `t + (n + 1)`

Example: `n = 2`, run `A` at `t=0` → next `A` at `t=3`.

---

## Data structures

### 1) Max-heap by remaining count

Store remaining counts of tasks available to run.
(You can store just integers counts, or (task,count). The schedule length doesn’t require knowing the letter, but it helps for debugging.)

### 2) Cooldown queue

A FIFO queue of items:

* which task (or just its remaining count),
* how many remaining,
* **readyTime** = when it can be put back into the heap.

Because time moves forward and ready times are non-decreasing, a normal queue works.


## Thorough example walkthrough (classic)

### Example

`tasks = [A,A,A,B,B,B], n = 2`

Counts:

* A: 3
* B: 3

Initial:

* maxHeap = [3(A), 3(B)]  (conceptually)
* cooldown = []
* time = 0

I’ll write each time slot as:
**time t:** available heap / cooldown → action → updates

---

### time = 0

* release: none
* heap: A3, B3 → pick one max (say A3)
* run **A**, remaining A becomes 2
* put A2 into cooldown with readyTime = 0 + 2 + 1 = **3**
* time++

State after:

* heap: B3
* cooldown: (A2 @ t=3)

Schedule so far: `A`

---

### time = 1

* release: none (A ready at 3)
* heap: B3 → run **B**, remaining B becomes 2
* cooldown add (B2 @ t=4)
* time++

State:

* heap: empty
* cooldown: (A2@3), (B2@4)

Schedule: `A B`

---

### time = 2

* release: none (next ready is 3)
* heap empty → **idle**
* time++

State unchanged except time:

* cooldown: (A2@3), (B2@4)

Schedule: `A B _`

---

### time = 3

* release: A2@3 → move A2 back to heap
* heap: A2 → run **A**, remaining A becomes 1
* cooldown add (A1 @ 3+3 = 6)
* time++

State:

* heap: empty
* cooldown: (B2@4), (A1@6)

Schedule: `A B _ A`

---

### time = 4

* release: B2@4 → heap gets B2
* heap: B2 → run **B**, remaining B becomes 1
* cooldown add (B1 @ 7)
* time++

State:

* heap empty
* cooldown: (A1@6), (B1@7)

Schedule: `A B _ A B`

---

### time = 5

* release: none (next ready 6)
* heap empty → **idle**
* time++

Schedule: `A B _ A B _`

---

### time = 6

* release: A1@6 → heap gets A1
* run **A**, remaining becomes 0 → do NOT cooldown
* time++

Schedule: `A B _ A B _ A`

---

### time = 7

* release: B1@7 → heap gets B1
* run **B**, remaining becomes 0
* time++

Schedule: `A B _ A B _ A B`

Now heap empty, cooldown empty, stop.
**Total time = 8** ✅

This matches the known answer: 8.

---

## Second walkthrough (shows “no idle” case)

`tasks = [A,A,A,B,B,B,C,C], n=2`

Counts:

* A:3, B:3, C:2

Because there are enough “other tasks” to fill gaps, the simulation will keep finding work almost every time slot, and it will end up returning **tasks.length = 8** (no idles needed). A valid schedule is:
`A B C A B C A B`

The heap+cooldown algorithm naturally produces something like this.

---

## Why this approach is greedy

At every time slot, among tasks that are currently allowed, you choose the one with the **largest remaining count**.
That’s greedy because the “most frequent” remaining tasks are the hardest to place later without causing idles.
*/

// class Solution {
//     private static class CooldownItem {
//         int remaining;
//         int readyTime;
//         CooldownItem(int remaining, int readyTime) {
//             this.remaining = remaining;
//             this.readyTime = readyTime;
//         }
//     }

//     public int leastInterval(char[] tasks, int n) {
//         int[] freq = new int[26];
//         for (char t : tasks) freq[t - 'A']++;

//         // Max-heap of remaining counts
//         PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
//         for (int f : freq) {
//             if (f > 0) maxHeap.offer(f);
//         }

//         // Cooldown queue: tasks waiting until they can be scheduled again
//         Deque<CooldownItem> cooldown = new ArrayDeque<>();

//         int time = 0;

//         while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {
//             // 1) Release tasks whose cooldown has finished
//             while (!cooldown.isEmpty() && cooldown.peekFirst().readyTime <= time) {
//                 maxHeap.offer(cooldown.pollFirst().remaining);
//             }

//             // 2) Run a task if possible; otherwise idle
//             if (!maxHeap.isEmpty()) {
//                 int remaining = maxHeap.poll();
//                 remaining--; // we execute it once at this "time"

//                 if (remaining > 0) {
//                     // next time we can run this task again
//                     cooldown.offerLast(new CooldownItem(remaining, time + n + 1));
//                 }
//             }
//             // else: idle (do nothing besides passing time)

//             time++;
//         }

//         return time;
//     }
// }