// Method 1: Sliding window
/*
Add the right end first (sum += nums[j]).
While the window meets/exceeds target, update answer and pop from the left (i++), so you get the shortest window ending at j.
Works because all numbers are positive (LeetCode 209 constraint), so expanding only increases sum, and shrinking only decreases it.

---- 

* Add the right end first (`sum += nums[j]`).
* While the window meets/exceeds `target`, update answer and pop from the left (`i++`), so you get the **shortest** window ending at `j`.
* Works because all numbers are **positive** (LeetCode 209 constraint), so expanding only increases `sum`, and shrinking only decreases it.


## Step-by-step walkthrough

**target = 7**, **nums = \[2, 3, 1, 2, 4, 3]** → expected answer = `2` (subarray `[4,3]`)

* Start: `i=0, sum=0, min=∞`
* `j=0`: add 2 → `sum=2` (<7) → no shrink
* `j=1`: add 3 → `sum=5` (<7) → no shrink
* `j=2`: add 1 → `sum=6` (<7) → no shrink
* `j=3`: add 2 → `sum=8` (≥7) → shrink:

  * update `min = min(∞, j-i+1 = 4) = 4`
  * pop `nums[i]=2`: `sum=6`, `i=1` → stop shrinking (sum<7)
* `j=4`: add 4 → `sum=10` (≥7) → shrink:

  * `min = min(4, 4) = 4` (window `[1..4]` len 4)
  * pop `3`: `sum=7`, `i=2` (still ≥7) → shrink again:

    * `min = min(4, 3) = 3` (window `[2..4]`)
    * pop `1`: `sum=6`, `i=3` → stop
* `j=5`: add 3 → `sum=9` (≥7) → shrink:

  * `min = min(3, 3) = 3` (window `[3..5]`)
  * pop `2`: `sum=7`, `i=4` (still ≥7) → shrink again:

    * `min = min(3, 2) = 2` (window `[4..5]` = `[4,3]`)
    * pop `4`: `sum=3`, `i=5` → stop

Done → `min = 2`.
*/
class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        int n = nums.length;
        int minLen = Integer.MAX_VALUE;
        int i = 0, sum = 0;

        for (int j = 0; j < n; j++) {
            sum += nums[j];                       // 1) expand window

            while (sum >= target) {               // 2) shrink as much as possible
                minLen = Math.min(minLen, j - i + 1);
                sum -= nums[i++];
            }
        }

        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }
}




// Method 2: Prefix sums + Binary Search (O(n log n), O(n))
/*
Awesome—let’s go deep on the **Prefix Sums + Binary Search** method for LeetCode 209 (Minimum Size Subarray Sum). I’ll explain the idea carefully, give clean Java code, and do a meticulous step-by-step walkthrough (including edge cases and common pitfalls).

---

# Intuition

You’re given a positive-integer array `nums` and a `target`. Define 1-indexed **prefix sums**:

* `pre[0] = 0`
* `pre[r] = nums[0] + nums[1] + ... + nums[r-1]` for `r = 1..n`

Because all `nums[i] > 0`, the prefix sums `pre[0], pre[1], …, pre[n]` are **strictly increasing**.

For each right boundary `r` (meaning the subarray ends at index `r-1`), we want the *shortest* left boundary `l` with:

```
pre[r] - pre[l] >= target
⇔ pre[l] <= pre[r] - target
```

Among all `l` that satisfy `pre[l] <= pre[r] - target`, the **largest** such `l` gives the **shortest** length `r - l`. So for each `r`, we can:

1. Compute `need = pre[r] - target`.
2. Find the **largest** index `l` such that `pre[l] <= need`.

Because `pre` is strictly increasing, we can find that `l` by:

* `upper_bound(need)` → returns the **first** index with `pre[idx] > need`.
* Then `l = upper_bound(need) - 1` is the last index with `pre[l] <= need`.

Update the answer with `min(ans, r - l)`.

---

# Correctness sketch

* Fix `r`. Feasible starts `l` satisfy `pre[l] <= pre[r] - target`.
  Among them, the **maximal** `l` minimizes `r - l` (shortest window ending at `r-1`).
* `pre` strictly increases ⇒ the set `{ l | pre[l] <= need }` is a prefix of indices.
  `upper_bound(need) - 1` jumps exactly to the **rightmost** valid `l`.
* Taking the minimum over all `r` yields the global minimum length.

---

# Complexity

* Build `pre`: `O(n)`.
* For each `r` (there are `n`), do a binary search on `pre`: `O(log n)`.
* Total: **O(n log n)** time, **O(n)** space.

### Notes & Pitfalls

* **Use `long`** for prefix sums if `nums` values can push sums past `int` (safer habit).
* `upperBound` is on the *whole* `pre` array `[0..n]`.
  It returns an index in `[0..n+1)`; subtracting 1 is safe, but check `l >= 0`.
* If `upperBound(need) == 0`, then no `pre[l] <= need` exists → skip.
* If *any* single element `>= target`, the loop will naturally find length `1`:

  * At the `r` right after that element, `need = pre[r] - target >= pre[r-1]`.
  * `upperBound(need)` will be `>= r`, making `l = r - 1`, so `r - l = 1`.

---

# Thorough example walkthrough

## Example 1 (LeetCode classic)

```
target = 7
nums   = [2, 3, 1, 2, 4, 3]
```

Build prefix sums (`pre[0]=0`):

```
i:    -  0  1  2  3   4   5
nums:    2  3  1  2   4   3
pre:  0  2  5  6  8  12  15
idx:  0  1  2  3  4   5   6   (these are r)
```

We iterate `r = 1..6`, compute `need = pre[r] - 7`, then `l = upperBound(pre, need) - 1`, and if valid, update `ans = min(ans, r - l)`.

* **r=1**: `pre[1]=2`, `need=-5`.
  `upperBound(-5) = 0` (first pre > -5 is pre\[0]=0). `l = -1` → no valid `l`.
  (No subarray ending at 0 reaches target—correct.)
* **r=2**: `pre[2]=5`, `need=-2`.
  `upperBound(-2) = 0` → `l=-1` → skip.
* **r=3**: `pre[3]=6`, `need=-1`.
  `upperBound(-1) = 0` → `l=-1` → skip.
* **r=4**: `pre[4]=8`, `need=1`.
  `upperBound(1) = 1` (first pre > 1 is pre\[1]=2). → `l=0`.
  Check: `pre[4]-pre[0] = 8 - 0 = 8 ≥ 7` → length `r - l = 4 - 0 = 4`.
  (This is subarray `[0..3] = [2,3,1,2]` sum 8.)
* **r=5**: `pre[5]=12`, `need=5`.
  `upperBound(5) = 3` (first pre > 5 is pre\[3]=6). → `l=2`.
  Check: `12 - pre[2]=12 - 5 = 7 ≥ 7` → length `5 - 2 = 3`.
  (This is subarray `[2..4] = [1,2,4]` sum 7.)
* **r=6**: `pre[6]=15`, `need=8`.
  `upperBound(8) = 5` (first pre > 8 is pre\[5]=12). → `l=4`.
  Check: `15 - pre[4]=15 - 8 = 7 ≥ 7` → length `6 - 4 = 2`.
  (This is subarray `[4..5] = [4,3]` sum 7.)

Minimum across all `r` is **2** — correct.

---

## Example 2 (only the whole array qualifies)

```
target = 7
nums   = [2, 3, 1, 2]   // only [2,3,1,2] = 8 qualifies
```

`pre = [0, 2, 5, 6, 8]`

* r=1: need=-5 → ub=0 → l=-1 → skip
* r=2: need=-2 → ub=0 → l=-1 → skip
* r=3: need=-1 → ub=0 → l=-1 → skip
* r=4: need=1  → ub=1 → l=0 → length = 4

Answer = **4**, as expected.

---

## Example 3 (single element ≥ target)

```
target = 7
nums   = [1, 8, 1]
```

`pre = [0, 1, 9, 10]`

* r=1: need=-6 → ub=0 → l=-1 → skip
* r=2: need=2  → ub=2 (first pre > 2 is pre\[2]=9) → l=1
  Check: `pre[2] - pre[1] = 9 - 1 = 8 ≥ 7` → length `2 - 1 = 1`.
  Found single-element window `[1..1]=[8]`, length **1**.

Answer = **1**.

---

# Why this approach is nice to know

* It leverages **monotonicity** (strictly increasing prefix sums).
* It’s a classic “**two loops + binary search**” pattern that generalizes to many problems.
* It complements the **O(n) sliding window**: if you forget the window invariant under pressure, this method is still efficient and easy to reason about.
*/

// class SolutionPrefixBinarySearch {
//     public int minSubArrayLen(int target, int[] nums) {
//         int n = nums.length;
//         // Use long for safety if sums can exceed int range
//         long[] pre = new long[n + 1];
//         for (int i = 0; i < n; i++) pre[i + 1] = pre[i] + nums[i];

//         int ans = Integer.MAX_VALUE;

//         for (int r = 1; r <= n; r++) {
//             long need = pre[r] - target;     // we want pre[l] <= need
//             int idx = upperBound(pre, need); // first index with pre[idx] > need
//             int l = idx - 1;                 // so l is last index with pre[l] <= need
//             if (l >= 0 && pre[r] - pre[l] >= target) {
//                 ans = Math.min(ans, r - l);  // window [l, r) → nums[l .. r-1]
//             }
//         }

//         return ans == Integer.MAX_VALUE ? 0 : ans;
//     }

//     // Upper bound: first index 'idx' with a[idx] > x  (array strictly increasing here)
//     private int upperBound(long[] a, long x) {
//         int lo = 0, hi = a.length;           // search in [lo, hi)
//         while (lo < hi) {
//             int mid = (lo + hi) >>> 1;
//             if (a[mid] > x) hi = mid;        // mid might be the first > x
//             else lo = mid + 1;               // still <= x, go right
//         }
//         return lo;                            // first > x
//     }
// }



// Method 3: Binary Search on Length (O(n log n), O(1))
/*
# Why binary search on length works

Let’s define:

* For any fixed length $L$, let
  $S_L$ = **maximum** sum among all contiguous subarrays of **exactly** length $L$.

Because all `nums[i]` are **positive**, $S_L$ is **non-decreasing** in $L$:

* Extending any window by one element **increases** its sum (or keeps it ≥ since numbers are positive).
* Therefore the **max** over all windows of length $L+1$ is at least the max for length $L$.

This gives a monotone predicate for binary search:

* Define $P(L) \equiv (S_L \ge \text{target})$.
* Since $S_L$ is non-decreasing, $P(L)$ is **monotone**: if $P(L)$ is false, then $P(k)$ is false for all $k \le L$; if $P(L)$ is true, the minimal feasible length is $\le L$.

So we can binary search the **smallest** $L \in [1, n]$ such that $S_L \ge \text{target}$.

How do we compute $S_L$ (or at least check $S_L \ge \text{target}$) fast?
Use a **fixed-size sliding window** of width $L$ in $O(n)$: keep the window sum, slide one step at a time, and track whether any window sum hits `target`.

---

# Algorithm outline

1. Set `lo = 1`, `hi = n`.
2. While `lo <= hi`:

   * `mid = (lo + hi) / 2`.
   * If **any** length-`mid` window has sum ≥ `target`, set `ans = mid`, and try shorter (`hi = mid - 1`).
   * Else, try longer (`lo = mid + 1`).
3. If never found, return `0`; otherwise return `ans`.

**Time:** $O(n \log n)$ — $\log n$ steps, each check in $O(n)$.
**Extra space:** $O(1)$.

### Notes & gotchas

* **Use `long`** for `sum`: with `n` up to $10^5$ and `nums[i]` up to $10^5$, sums can exceed `int`.
* The check is for **exactly** length $L$. That’s sufficient because our monotone predicate is $P(L): S_L \ge \text{target}$.
* If no window works for any $L$, `ans` remains 0 and we return 0 — per problem statement.

NOTE: In the code we have if (i >= L - 1 && sum >= target) return true; because
We only start checking once the window length is at least L.
The earliest i where a length-L window exists is i = L-1.

---
# Detailed walkthrough

## Example A (classic)

```
target = 7
nums   = [2, 3, 1, 2, 4, 3]
n = 6
```

**Binary search over L in \[1..6]:**

1. `lo=1, hi=6` → `mid=3`.
   Check length-3 windows (fixed window sum):

   * `[2,3,1] = 6`  (<7)
   * slide → subtract 2, add 2: `[3,1,2] = 6`  (<7)
   * slide → subtract 3, add 4: `[1,2,4] = 7`  (≥7) → **exists** → `ans=3`, `hi=2`
2. `lo=1, hi=2` → `mid=1`.
   Length-1 windows: `[2],[3],[1],[2],[4],[3]` → max is 4 (<7) → **no** → `lo=2`
3. `lo=2, hi=2` → `mid=2`.
   Length-2 windows:

   * `[2,3]=5`, slide→ `[3,1]=4`, slide→ `[1,2]=3`, slide→ `[2,4]=6`, slide→ `[4,3]=7` → **exists** → `ans=2`, `hi=1`

Loop ends → **answer = 2** (the subarray `[4,3]`).

Why correct here?

* `S_1 = 4` (<7), `S_2 = 7` (≥7), `S_3 = 7` (≥7), … — monotone in $L$.

---

## Example B (only the whole array qualifies)

```
target = 7
nums   = [2, 3, 1, 2]   // only [2,3,1,2] = 8 works
n = 4
```

* Try `L=2`: windows sums 5, 4, 3 → none
* Try `L=3`: sums 6, 6 → none
* Try `L=4`: sum 8 → yes → minimal L is **4**

Binary search finds 4 accordingly.

---

## Example C (single element is enough)

```
target = 7
nums   = [1, 8, 1]
n = 3
```

* `L=1` → windows: `[1], [8], [1]` → yes (8) → minimal L is **1**.
  Binary search converges to 1.

---

# Why this is a great interview answer

* Shows you can **identify a monotone predicate** and apply binary search beyond “sorted arrays.”
* The feasibility check is a clean **fixed-size sliding window**.
* Complexity is crisp: $O(n \log n)$ time, $O(1)$ extra space.
* You explicitly handle overflow and edge cases.

---

## Quick sanity checklist

* If `nums` is empty → return 0.
* If **no** subarray reaches `target` → return 0.
* If **any** single `nums[i] >= target` → answer is 1 (your check will catch it).
* Use `long` sums; keep the window **exactly** length `L` during the check.
*/

// class SolutionBinarySearchLength {
//     public int minSubArrayLen(int target, int[] nums) {
//         int n = nums.length;
//         if (n == 0) return 0;

//         int lo = 1, hi = n, ans = 0;

//         while (lo <= hi) {
//             int mid = lo + ((hi - lo) >>> 1);   // candidate length
//             if (existsWindowAtLeast(nums, target, mid)) {
//                 ans = mid;                      // mid works; try to shrink
//                 hi = mid - 1;
//             } else {
//                 lo = mid + 1;                   // mid doesn't work; need longer
//             }
//         }
//         return ans;
//     }

//     // Returns true iff there exists a contiguous window of EXACT length L
//     // whose sum >= target. Uses O(n) sliding window.
//     private boolean existsWindowAtLeast(int[] a, int target, int L) {
//         long sum = 0;                           // use long: sums can exceed int
//         for (int i = 0; i < a.length; i++) {
//             sum += a[i];                        // expand right
//             if (i >= L) sum -= a[i - L];       // shrink left to keep size L
//             if (i >= L - 1 && sum >= target) return true;
//         }
//         return false;
//     }
// }
