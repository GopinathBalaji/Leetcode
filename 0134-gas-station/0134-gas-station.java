// Greedy Approach
/*
Key Idea: 
If tank ever drops below 0 at index i, then starting anywhere in the block [start … i] cannot work (you’d hit a deficit at or before i).
⇒ Skip this whole block by setting start = i + 1, and reset tank = 0.


# Core idea (in words)

* Let `gain[i] = gas[i] - cost[i]` be what you *net* at station `i`.
* Two running sums:

  * `total`: sum of all `gain[i]` (over the whole circle).
  * `tank`: sum of `gain` from your **current candidate start** up to the current index.

Greedy rule:

1. Scan indices left→right once.
   Update `tank += gain[i]` and `total += gain[i]`.

2. If `tank` ever drops **below 0** at index `i`, then starting anywhere in the block `[start … i]` cannot work (you’d hit a deficit at or before `i`).
   ⇒ **Skip this whole block** by setting `start = i + 1`, and reset `tank = 0`.

3. After the single pass:

   * If `total < 0` → impossible from any start → return `-1`.
   * Else the found `start` is the **unique** valid starting index.

No wrapping or extra loops needed.

---

# Why the “reset” is safe

Suppose we tried starting at `start`. If `tank` becomes negative at `i`, then the sum of gains from `start` to `i` is negative.
Any index `k` with `start ≤ k ≤ i` would start later, i.e., with **less** accumulated buffer before hitting that same negative segment, so it must also fail. Hence we can safely jump the start to `i + 1`.

This “skip a whole failing block at once” is why the algorithm is O(n).

---

# Walkthrough 1 (typical successful case)

`gas =  [1, 2, 3, 4, 5]`
`cost = [3, 4, 5, 1, 2]`
`gain = [-2, -2, -2, +3, +3]`

Initialize: `start=0`, `tank=0`, `total=0`

* i=0: gain=-2 → `tank=-2`, `total=-2` → `tank<0` ⇒ **reset**: `start=1`, `tank=0`
* i=1: gain=-2 → `tank=-2`, `total=-4` → **reset**: `start=2`, `tank=0`
* i=2: gain=-2 → `tank=-2`, `total=-6` → **reset**: `start=3`, `tank=0`
* i=3: gain=+3 → `tank=3`,  `total=-3`
* i=4: gain=+3 → `tank=6`,  `total=0`

End: `total = 0 ≥ 0` ⇒ possible. The candidate `start=3` is the answer (0-based).
Check quickly: start at 3 → tank sequence: +3, +3, then wrap: +1−3−2 = it never dips below 0 across the full loop.

---

# Walkthrough 2 (multiple resets; total ≥ 0)

`gas =  [5, 1, 2, 3, 4]`
`cost = [4, 4, 1, 5, 1]`
`gain = [+1, -3, +1, -2, +3]` (sums to 0)

Start `start=0`, `tank=0`, `total=0`

* i=0: +1 → `tank=1`, `total=1`
* i=1: -3 → `tank=-2`, `total=-2` → **reset**: `start=2`, `tank=0`
* i=2: +1 → `tank=1`, `total=-1`
* i=3: -2 → `tank=-1`, `total=-3` → **reset**: `start=4`, `tank=0`
* i=4: +3 → `tank=3`, `total=0`

End: `total=0 ≥ 0` ⇒ possible, answer `start=4`.
Indeed, starting at 4, you get +3, then wrapping: +1−3+1−2 = never negative.

---

# Equivalent “prefix valley” view (same result)

If you build the cumulative sum `S[k] = gain[0]+…+gain[k]`, the valid start is the index **right after the minimum prefix sum** (the deepest “valley”). In Walkthrough 1, the minimum is at `k=2`, so start at `3`. In Walkthrough 2 the deepest valley is at `k=3`, so start at `4`. This is mathematically the same as the greedy reset.

---

# Pitfalls to avoid

* Forgetting the **total check**. If `sum(gain) < 0`, no solution exists — return `-1`.
* Trying to “wrap the loop” manually. Not needed; the single pass with resets already accounts for the circle.
* Mixing 0-based vs 1-based indices when reporting the start.
* Overflow is rarely an issue here, but if inputs are huge, consider using `long` for `total`/`tank`.

---

# Complexity

* **Time:** O(n) — one pass, each index considered once.
* **Space:** O(1).
*/
class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int n = gas.length;
        int start = 0;     // current candidate starting index
        int tank = 0;      // running fuel since 'start'
        int total = 0;     // net fuel over the whole circle

        for (int i = 0; i < n; i++) {
            int gain = gas[i] - cost[i];
            tank  += gain;
            total += gain;

            // If we went negative here, 'start' can't be valid.
            if (tank < 0) {
                start = i + 1; // skip the whole failed block [start..i]
                tank = 0;      // reset tank for the next candidate
            }
        }
        return (total >= 0) ? start : -1; // impossible if total net < 0
    }
}




// Method 2: Prefix Sum
/*
Key idea: Shifting where you “cut” the circle subtracts a constant from all prefix sums. 
Starting after the global minimum shift makes every running sum from that start never negative, so your tank never goes below 0. That’s exactly what we need.


## Big idea (why it works)

Let `gain[i] = gas[i] - cost[i]`. Walk once around the circle and build a **cumulative sum**:

```
S[-1] = 0
S[i]  = gain[0] + gain[1] + ... + gain[i]     for i = 0..n-1
```

* If `total = S[n-1] < 0`, you can’t finish the loop from **any** start → return `-1`.
* Otherwise, choose the start to be **right after the deepest dip** of the prefix sums:

  * Find the **minimum** value among `S[-1], S[0], ..., S[n-1]`.
  * Let `minIndex` be the index where this minimum occurs (`-1` means “before the first element”).
  * The answer is `start = (minIndex + 1) % n`.

### Intuition

Shifting where you “cut” the circle subtracts a constant from all prefix sums. Starting **after** the global minimum shift makes every running sum from that start **never negative**, so your tank never goes below 0. That’s exactly what we need.


**Why `long`?** Safe against overflow if inputs are large.
**Time/Space:** O(n) time, O(1) extra space.

---

## Walkthrough 1 (classic “works” case)

```
gas  = [1, 2, 3, 4, 5]
cost = [3, 4, 5, 1, 2]
gain = [-2, -2, -2, +3, +3]
```

Build S with `S[-1]=0`:

* i=0: S0 = -2   (min so far: -2 at i=0)
* i=1: S1 = -4   (min: -4 at i=1)
* i=2: S2 = -6   (min: -6 at i=2)
* i=3: S3 = -3
* i=4: S4 =  0   (total = 0)

Minimum is at `i=2` (value −6). Start at `(2+1) % 5 = 3`.

Check from start=3:

* Station 3: +3 → tank 3
* Station 4: +3 → tank 6
* Station 0: −2 → tank 4
* Station 1: −2 → tank 2
* Station 2: −2 → tank 0  ✅ never negative.

---

## Walkthrough 2 (works; multiple dips)

```
gas  = [5, 1, 2, 3, 4]
cost = [4, 4, 1, 5, 1]
gain = [+1, -3, +1, -2, +3]
```

Sums:

* S0 =  1     (min still 0 at i=-1)
* S1 = -2   → min = -2 at i=1
* S2 = -1
* S3 = -3   → min = -3 at i=3
* S4 =  0   (total = 0)

Minimum at `i=3` → start `(3+1)%5 = 4`.

From start=4: +3, then wrap: +1, −3, +1, −2 → never negative overall.

---

## Why “start after the minimum” is correct (one-liner proof sketch)

For any start `s`, the tank after visiting up to index `i` (wrapping if needed) equals `S[i] − S[s−1]`.
Choosing `s` as **one after the global minimum** makes `S[s−1]` the smallest prefix; therefore, for all `i` the differences are ≥ 0, so the tank never dips below zero. If `total < 0`, you can’t make it regardless of `s`.

---

### Relation to the classic greedy reset

Tracking the lowest prefix is mathematically the same as the forward greedy that **resets the start** whenever the running tank goes negative. Both return the same unique start (when a solution exists).
*/
// class Solution {
//     public int canCompleteCircuit(int[] gas, int[] cost) {
//         int n = gas.length;
//         long total = 0;         // sum of all gains
//         long prefix = 0;        // running prefix sum S[i]
//         long minPrefix = 0;     // minimum over S[-1..i]
//         int minIndex = -1;      // index i where S[i] hits the global minimum; -1 for S[-1]=0

//         for (int i = 0; i < n; i++) {
//             long gain = (long)gas[i] - cost[i];
//             total  += gain;
//             prefix += gain;
//             if (prefix < minPrefix) {
//                 minPrefix = prefix;
//                 minIndex = i;
//             }
//         }

//         if (total < 0) return -1;          // impossible overall
//         return (minIndex + 1) % n;         // start right after the lowest valley
//     }
// }
