// Greedy (Bottom-Up DP with path compression)
/*
## What the variables mean (DP view)

* Think of indices grouped in **layers**:

  * **Layer 0**: index `0` (0 jumps).
  * **Layer 1**: all indices reachable with **1** jump from anywhere in Layer 0.
  * **Layer 2**: all indices reachable with **2** jumps from anywhere in Layer 1.
  * …and so on.

We scan left→right across the current layer (`0 … currEnd`) and compute how far the **next** layer can reach (`farthest`).
When we finish the current layer (`i == currEnd`), we **increment `jumps`** and shift the window to the next layer: `currEnd = farthest`.

**Invariants**

* At any time:

  * `currEnd` = rightmost index reachable with exactly `jumps` jumps.
  * `farthest` = rightmost index reachable with `jumps + 1` jumps using any index we’ve seen in the current layer.
* When `i == currEnd`, we must spend one jump to move to the next layer (otherwise we can’t progress further).

This is dynamic programming over **minimum jumps** because each layer represents indices reachable with the minimal number of jumps so far; we’re computing the next layer’s boundary in O(1) per index.

---

## Walkthrough on `[2,3,1,1,4]` (answer = 2)

`n=5`
Init: `jumps=0, currEnd=0, farthest=0`

* `i=0`:

  * `farthest = max(0, 0 + nums[0]=2) = 2`
  * `i == currEnd` (0): finish layer 0 → `jumps=1`, `currEnd=2`
* `i=1` (now inside layer 1: indices 1..2):

  * `farthest = max(2, 1 + nums[1]=3+1=4) = 4`
* `i=2`:

  * `farthest = max(4, 2 + nums[2]=1) = 4`
  * `i == currEnd` (2): finish layer 1 → `jumps=2`, `currEnd=4`
  * `currEnd >= n-1` → break

Return `jumps = 2` (e.g., 0→1→4 or 0→2→4).

---

## Walkthrough on `[1,1,1,1]` (answer = 3)

Init: `jumps=0, currEnd=0, farthest=0`

* `i=0`: `farthest=max(0,0+1)=1`; `i==currEnd` → `jumps=1`, `currEnd=1`
* `i=1`: `farthest=max(1,1+1)=2`; `i==currEnd` → `jumps=2`, `currEnd=2`
* `i=2`: `farthest=max(2,2+1)=3`; `i==currEnd` → `jumps=3`, `currEnd=3` (reaches end)

Return **3**.

---

## Why this is optimal

Each time we finish a layer we’ve explored **all** positions reachable with the current number of jumps and computed the **best possible** reach for one more jump. That’s exactly the shortest-path (fewest edges) idea on a DAG of indices → a dynamic programming over layers, done in linear time.

**Complexity:** O(n) time, O(1) extra space.

*/ 
class Solution {
    public int jump(int[] nums) {
        int n = nums.length;
        if (n <= 1) return 0; // already at the end

        int jumps = 0;      // number of layers we’ve completed (answer)
        int currEnd = 0;    // rightmost index reachable with exactly 'jumps' jumps (current layer boundary)
        int farthest = 0;   // rightmost index we can reach with 'jumps + 1' (next layer)

        // We only need to iterate to n-2: once we can reach n-1, we’re done.
        for (int i = 0; i < n - 1; i++) {
            // Expand the next-layer boundary using the jump power at i
            farthest = Math.max(farthest, i + nums[i]);

            // If we’ve reached the end of the current layer,
            // we must "take a jump" to move to the next layer.
            if (i == currEnd) {
                jumps++;
                currEnd = farthest;

                // Early exit: if the next layer already covers the end, stop
                if (currEnd >= n - 1) break;
            }
        }
        return jumps;
    }
}




// DP from the end while maintaining states and transitions
/*
* Fill from right → left and finally return `dp[0]`.

## Why it’s correct (states & transition)

* **State:** `dp[i]` = min jumps to reach `n-1` starting from `i`.
* **Base:** `dp[n-1] = 0`.
* **Transition:** from `i` you can jump to `j ∈ [i+1, min(n-1, i+nums[i])]`, so
  `dp[i] = 1 + min(dp[j])` over that window (or INF if none reachable).
* **Order:** right → left so dependencies are already computed.

## Walkthrough on `[2,3,1,1,4]`

`n=5`, `INF=10`
Initialize: `dp = [INF, INF, INF, INF, 0]`

* `i=3`, `far=min(4,3+1)=4`: window `{4}` → `best=min(dp[4])=0` → `dp[3]=1+0=1`
  `dp = [INF, INF, INF, 1, 0]`
* `i=2`, `far=min(4,2+1)=3`: window `{3}` → `best=dp[3]=1` → `dp[2]=1+1=2`
  `dp = [INF, INF, 2, 1, 0]`
* `i=1`, `far=min(4,1+3)=4`: window `{2,3,4}` → `best=min(2,1,0)=0` → `dp[1]=1+0=1`
  `dp = [INF, 1, 2, 1, 0]`
* `i=0`, `far=min(4,0+2)=2`: window `{1,2}` → `best=min(1,2)=1` → `dp[0]=1+1=2`
  `dp = [2, 1, 2, 1, 0]`

Return `dp[0] = 2` ✅

---

### Notes

* This is the clear “DP with states & transitions” version (O(n²)).
* If they ask to optimize, convert to the **layered DP (BFS-style)** which runs in **O(n)** time and O(1) space.
*/
// class Solution {
//     public int jump(int[] nums) {
//         int n = nums.length;
//         if (n <= 1) return 0;          // already at the end

//         int INF = n + 5;               // sentinel for "unreachable"
//         int[] dp = new int[n];
//         // init to INF
//         for (int i = 0; i < n; i++) dp[i] = INF;
//         dp[n - 1] = 0;                 // base: last index needs 0 jumps

//         // fill right -> left
//         for (int i = n - 2; i >= 0; i--) {
//             int far = Math.min(n - 1, i + nums[i]);
//             if (far == i) continue;    // can't move from i (nums[i] == 0)

//             int best = INF;
//             // dp[i] = 1 + min{ dp[j] | j in [i+1, far] }
//             for (int j = i + 1; j <= far; j++) {
//                 if (dp[j] < best) {
//                     best = dp[j];
//                     if (best == 0) break; // early stop: can't beat 0
//                 }
//             }
//             if (best != INF) dp[i] = 1 + best;
//         }

//         // If unreachable, dp[0] would be INF
//         return dp[0];
//     }
// }





// DP from the start
/*
DP formulation (from the start)

State: dp[i] = minimum number of jumps needed to reach index i from index 0.
Base: dp[0] = 0; for i > 0, initialize dp[i] = +∞ (unreached).
Transition (relaxation):
    From index i, you can reach any j in [i+1, min(n-1, i + nums[i])].
    Update dp[j] = min(dp[j], dp[i] + 1).
Order: left → right (0 to n−1). Since all edges go forward, one pass suffices.
Answer: dp[n-1].


**Why `INF = n + 5` (or `Integer.MAX_VALUE / 4`)?**
So `dp[i] + 1` never overflows and any real number of jumps is smaller than `INF`.

---

#  Intuition (shortest path on a DAG)

Think of indices as nodes. From `i`, there’s a **unit-weight edge** to every `j` in `[i+1, i+nums[i]]`.
We’re computing the **fewest edges** from node `0` to node `n−1`.
Processing nodes left→right is a **topological order** for this DAG, so each node is finalized in one pass.

---

# \U0001f50e Thorough walkthrough on `[2, 3, 1, 1, 4]`

`n = 5`
Initialize: `dp = [0, ∞, ∞, ∞, ∞]`

* **i = 0** (`nums[0] = 2`) → can reach `j = 1..2`

  * `dp[1] = min(∞, 0 + 1) = 1`
  * `dp[2] = min(∞, 0 + 1) = 1`
  * `dp = [0, 1, 1, ∞, ∞]`

* **i = 1** (`nums[1] = 3`) → can reach `j = 2..4`

  * `dp[2] = min(1, 1 + 1 = 2) = 1` (unchanged)
  * `dp[3] = min(∞, 1 + 1 = 2) = 2`
  * `dp[4] = min(∞, 1 + 1 = 2) = 2`
  * `dp = [0, 1, 1, 2, 2]`

* **i = 2** (`nums[2] = 1`) → can reach `j = 3`

  * `dp[3] = min(2, 1 + 1 = 2) = 2` (unchanged)

* **i = 3** (`nums[3] = 1`) → can reach `j = 4`

  * `dp[4] = min(2, 2 + 1 = 3) = 2` (unchanged)

* **i = 4** (last index) → no relax

Return `dp[4] = 2` ✅ (e.g., 0→1→4 or 0→2→4)

---

# \U0001f4dd Notes & edges

* If the problem didn’t guarantee reachability, `dp[n-1]` staying `INF` means “unreachable”.
* This is the clearest “states & transitions” DP. In interviews, you can then mention the **O(n)** layer/BFS optimization (same idea, compressed).
*/

// class Solution {
//     public int jump(int[] nums) {
//         int n = nums.length;
//         if (n <= 1) return 0;                 // already at the end

//         final int INF = n + 5;                // safe sentinel for "unreached"
//         int[] dp = new int[n];
//         Arrays.fill(dp, INF);
//         dp[0] = 0;

//         for (int i = 0; i < n; i++) {
//             if (dp[i] == INF) continue;       // can't relax from an unreached index
//             int far = Math.min(n - 1, i + nums[i]);
//             int next = dp[i] + 1;             // one more jump to reach any j in the window
//             for (int j = i + 1; j <= far; j++) {
//                 if (dp[j] > next) dp[j] = next;
//             }
//         }
//         return dp[n - 1];                     // minimal jumps to reach the last index
//     }
// }





// Top-Down DP
/*
### States & transitions

* **State:** `dp[i]` = minimum jumps to reach the last index starting from index `i`.
* **Base:** `dp[n-1] = 0` (if you’re at the last index, no more jumps).
* **Transition:** from `i`, you may jump to any `j ∈ [i+1, min(n-1, i + nums[i])]`, so
  `dp[i] = 1 + min(dp[j])` over that range.
* We compute `dp[i]` with recursion and **memoize** results so each `i` is solved once.

**Complexity:** Each index `i` is computed once, but the transition scans up to `O(n)` next positions → **O(n²)** worst case; **O(n)** space for the memo + recursion stack.

---

## Thorough walkthrough on `[2,3,1,1,4]`

Indices: 0  1  2  3  4
Values:  2  3  1  1  4

Goal: min jumps from 0 to 4 (answer should be 2).

We call `dfs(0)`:

1. `dfs(0)`

   * `far = min(4, 0+2) = 2` → candidates: `j = 2, 1` (we try far→near)
   * Try `j=2` → `dfs(2)`

2. `dfs(2)`

   * `far = min(4, 2+1) = 3` → candidates: `j=3`
   * Try `j=3` → `dfs(3)`

3. `dfs(3)`

   * `far = min(4, 3+1) = 4` → candidates: `j=4`
   * Try `j=4` → `dfs(4)`

4. `dfs(4)`

   * `i >= n-1` → return `0`.
   * So `dfs(3) = 1 + 0 = 1` (one jump from 3 to end)
   * Memoize: `memo[3] = 1`

Back to `dfs(2)`:

* Best from 2 is `1 + dfs(3) = 2`.
* Memoize: `memo[2] = 2`.

Back to `dfs(0)` (we already tried `j=2` → cost 3 overall? wait):

* From 0 to 2 costs `1 + dfs(2) = 3`.
* Now try `j=1` → `dfs(1)`.

5. `dfs(1)`

   * `far = min(4, 1+3) = 4` → candidates: `j = 4, 3, 2`
   * Try `j=4` → `dfs(4) = 0` → immediate success:

     * `1 + 0 = 1` jump from 1 to end → prune (best == 1).

Return to `dfs(0)`:

* From 0 to 1 to end = `1 + 1 = 2` jumps.
* `min(3 via j=2, 2 via j=1) = 2` → `dfs(0) = 2`.

Memo ends as: `memo[4]=0, memo[3]=1, memo[2]=2, memo[1]=1, memo[0]=2`.
Final answer: **2**.

---

### Tips

* Trying **farther targets first** often finds a quick path to the end → fewer recursive calls.
* If recursion depth worries you in Java for very large `n`, switch to the **O(n)** layered DP (BFS-style) you already know; it’s the optimized form of this DP.
*/

// class Solution {
//     public int jump(int[] nums) {
//         int n = nums.length;
//         if (n <= 1) return 0;                 // already at the end

//         int[] memo = new int[n];              // memo[i] = min jumps from i to last
//         Arrays.fill(memo, -1);
//         return dfs(0, nums, memo);
//     }

//     // Returns the minimum number of jumps needed to reach the last index from i.
//     private int dfs(int i, int[] a, int[] memo) {
//         int n = a.length;

//         // Base: at or beyond the last index -> 0 more jumps needed
//         if (i >= n - 1) return 0;

//         if (memo[i] != -1) return memo[i];

//         int far = Math.min(n - 1, i + a[i]);
//         // If we can't move from here (a[i] == 0) and we're not at the end,
//         // treat as unreachable (LC45 guarantees overall reachability, but this is safe).
//         if (far == i) return memo[i] = Integer.MAX_VALUE / 4;

//         int best = Integer.MAX_VALUE / 4;

//         // Try farther targets first; if we can jump straight to the end, we’ll prune quickly.
//         for (int j = far; j > i; j--) {
//             int sub = dfs(j, a, memo);
//             best = Math.min(best, 1 + sub);
//             if (best == 1) break; // already found a direct jump to end
//         }

//         return memo[i] = best;
//     }
// }
