// Method 1: Using 2 Heaps
/*
Complexity:
Build min-heap: O(n) (amortized) or O(n log n) if you prefer multiple offers.
Each project moves once from minCap to maxProf: total O(n log n).
Up to k picks from maxProf: O(k log n).
Overall: O((n + k) log n) time, O(n) space.

Tiny walkthrough:
k=3, w=2
capital=[0,1,3,4], profits=[1,2,5,6]
Round 1: unlock cap≤2 → push {1,2}; pick 2 → w=4
Round 2: unlock cap≤4 → push {5,6}; heap now {6,5,1}; pick 6 → w=10
Round 3: unlock none; pick 5 → w=15 → done
*/
class Solution {
    public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        int n = profits.length;
        if (k == 0 || n == 0) return w;              // nothing to do

        // Min-heap by required capital: (capital, profit)
        PriorityQueue<Map.Entry<Integer, Integer>> minCap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getKey));

        // Max-heap of profits for currently affordable projects
        PriorityQueue<Integer> maxProf = new PriorityQueue<>(Collections.reverseOrder());

        // Load all projects into the min-heap
        for (int i = 0; i < n; i++) {
            minCap.offer(new java.util.AbstractMap.SimpleEntry<>(capital[i], profits[i]));
        }

        long currentW = w; // long is safe; LC constraints fit in int, but this avoids overflow anxiety

        // Up to k picks
        for (int round = 0; round < k; round++) {

            // UNLOCK: move every project with capital <= currentW into the max-profit heap
            while (!minCap.isEmpty() && minCap.peek().getKey() <= currentW) {
                maxProf.offer(minCap.poll().getValue());
            }

            // If nothing affordable, we're done
            if (maxProf.isEmpty()) break;

            // PICK: take the most profitable affordable project
            currentW += maxProf.poll();
        }

        return (int) currentW;
    }
}





// Method 2: Sort by capital + Max heap of profits (similar to 2 heap answer)
/*
Here’s a clean, **fully correct** solution for **LeetCode 502. IPO** in Java, plus a deep explanation, a step-by-step walkthrough, and alternative methods (with pros/cons).

## Why this works

* Greedy exchange: At any moment, among **affordable** projects (capital ≤ `currentW`), taking the **largest profit** is optimal—doing so never makes later choices worse.
* Sorting by capital lets us **stream** projects into a max-heap as they become affordable; no rescanning.
* Picking up to `k` times gives at most `k` heap pops.
* **Time:** `O(n log n + k log n)` (sort once, then heap ops).
  **Space:** `O(n)` (projects + heap).

---

## Walkthrough (thorough, small example)

**Input**
`k = 3, w = 2`
`capital = [0, 1, 3, 4]`
`profits = [1, 2, 5, 6]`
Interpretation: projects are
`(cap→profit) = (0→1), (1→2), (3→5), (4→6)`.

**Step 1 — sort by capital** (already sorted).

**State initially**
`currentW = 2`, `p = 0`, `maxProf = []`.

**Round 1**

* Unlock: while `cap ≤ 2` push profits: push 1 (cap 0), push 2 (cap 1).
  `maxProf = [2, 1]`, `p = 2`.
* Pick: pop top = 2 → `currentW = 2 + 2 = 4`.

**Round 2**

* Unlock newly affordable with `currentW = 4`: push 5 (cap 3), push 6 (cap 4).
  `maxProf = [6, 5, 1]` (2 was consumed), `p = 4`.
* Pick: pop top = 6 → `currentW = 10`.

**Round 3**

* Nothing new to unlock (`p == n`), heap still has `[5, 1]`.
* Pick: pop top = 5 → `currentW = 15`.

Stop (3 rounds done). **Answer = 15**.

**Visualization of the invariant**

* Before each pick, `maxProf` contains exactly the profits of **all** projects with `capital ≤ currentW`.
* We always take the maximum—this maximizes capital growth earliest, thus enabling the largest feasible future set.

---

## Common pitfalls (your earlier version hit some of these)

* Iterating a `PriorityQueue` while calling `poll()` on it (concurrent modification & removing the wrong item). Use a **peek→poll loop**, not a for-each.
* Pushing **(capital, profit)** into max-heap; you only need **profit** in the max-heap. Capital only gates affordability at unlock time.
* Forgetting early exit when heap is empty (no affordable project → stop).

---

# \U0001f9f0 Variants / Other methods

## A) Two-heap version (min-heap by capital + max-heap by profit)

Functionally similar to the preferred solution; instead of sorting once and using a pointer, keep a **min-heap** of `(capital, profit)` (by capital). Each round:

* While `minCap.peek().capital ≤ currentW`, `poll()` and `offer(profit)` into `maxProf`.
* Then pick from `maxProf`.

**Time:** `O(n log n + k log n)`; **Space:** `O(n)`.
**Notes:** Slightly more overhead than sort+pointer; both are acceptable.

## B) O(k·n) scan (simple but slower)

For each of the `k` rounds:

* Scan all projects, among those with `capital ≤ currentW` and **unused**, pick the max profit.
* Mark used, add profit to `currentW`.

**Time:** `O(k·n)`; **Space:** `O(n)` (used flags).
**When to use:** Only if constraints are small or you need a super quick prototype; otherwise TLE risk for n up to 5e4.

## C) Balanced tree / multiset of profits

* Keep projects sorted by capital (array or min-heap).
* Keep a `TreeMap<Integer, count>` or `PriorityQueue` for profits.
* The mechanics mirror the preferred solution; a PQ is simpler/faster in Java.

## D) Why DP/backtracking isn’t suitable

* Capital is unbounded (keeps increasing by added profit), not a small discrete state → DP explodes.
* Backtracking over subsets is exponential.

---

# Edge cases & correctness checklist

* `k == 0` → return `w`.
* No affordable projects at any time → return `w`.
* Multiple projects with the same capital or same profit → max-heap naturally handles ties.
* Profits can be zero → picking them is harmless, but you might skip if better options exist (the heap ensures that).
* Arrays length zero → return `w`.
* Using `long currentW` avoids overflow worries (not strictly needed for LC constraints, but safe).

---

# Tiny test set (mentally simulate)

1. `k=1, w=0, capital=[0,1], profits=[0,2]` → unlock cap 0 → pick 0 → result 0.
2. `k=2, w=0, capital=[0,0,1], profits=[1,2,3]` → round1: unlock two → pick 2 → w=2 → round2: unlock third → pick 3 → w=5.
3. `k=3, w=1, capital=[2,3], profits=[100,100]` → never unlock → w=1.
*/

// import java.util.*;

// class Solution {
//     // Each project: required capital and profit
//     static class Project {
//         int cap, prof;
//         Project(int c, int p) { cap = c; prof = p; }
//     }

//     public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
//         int n = profits.length;
//         if (n == 0 || k == 0) return w;

//         // 1) Pack projects and sort by required capital (ascending)
//         Project[] arr = new Project[n];
//         for (int i = 0; i < n; i++) arr[i] = new Project(capital[i], profits[i]);
//         Arrays.sort(arr, (a, b) -> Integer.compare(a.cap, b.cap));

//         // 2) Max-heap of profits among projects we can currently afford
//         PriorityQueue<Integer> maxProf = new PriorityQueue<>(Collections.reverseOrder());

//         long currentW = w;   // long is safe though int is OK for LC constraints
//         int p = 0;           // pointer to stream newly-affordable projects

//         // 3) Do at most k picks
//         for (int round = 0; round < k; round++) {
//             // Unlock step: push all projects whose capital <= currentW
//             while (p < n && arr[p].cap <= currentW) {
//                 maxProf.offer(arr[p].prof);
//                 p++;
//             }

//             // If nothing is affordable, we're stuck
//             if (maxProf.isEmpty()) break;

//             // Pick step: take the most profitable affordable project
//             currentW += maxProf.poll();
//         }

//         return (int) currentW;
//     }
// }

