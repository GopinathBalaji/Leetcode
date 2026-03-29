// Method 1: Top-Down DP
/*
// State: f(i, prev) = LIS length starting at index i when the last picked index is prev (prev = -1 means “nothing picked yet”).
// Choices at i:
//     Skip i → f(i+1, prev)
//     Take i if prev == -1 || nums[i] > nums[prev] → 1 + f(i+1, i)
// Return the max of the two, memoized by (i, prev+1).

// Why this fixes your issues:
// The previous choice is encoded via prev, so we only extend when valid.
// Memoization is accurate: (i, prev) uniquely identifies the subproblem.
// No mutable running len; each call returns the optimal length from its state.

// ---

// ## 1) Why a **2D memo**?

// Our state is **two-dimensional**:

// * `i`: the current index we’re deciding on (`i` ranges `0..n-1`)
// * `prev`: the **index of the last chosen element** in the subsequence so far
//   (or **`-1`** if we haven’t picked anything yet)

// The answer from a given point depends on **both** where we are (`i`) **and** what we last picked (`prev`).
// Two calls with the same `i` but different `prev` can have different results—so we must memoize on both → a 2D memo.

// **State:** `f(i, prev)` = LIS length in `nums[i..]` if the last chosen index is `prev` (or `-1` for none).

// ---

// ## 2) Why size **`[n][n+1]`** and not `[n][n]`?

// Because `prev` ranges over **`-1, 0, 1, ..., n-1`** → that’s **`n + 1`** distinct values.

// Arrays can’t be indexed at `-1`, so we **shift** by 1:

// * We store state `(i, prev)` at `memo[i][prev + 1]`.

//   * `prev = -1` → column `0`
//   * `prev = 0`  → column `1`
//   * …
//   * `prev = n-1` → column `n`

// Hence columns = `n + 1`. Rows are `i = 0..n-1` → `n` rows.
// So the memo is `n × (n+1)`.

// If you used `[n][n]`, there’d be **no column** to represent `prev = -1` (the “nothing chosen yet” state). You’d lose a valid state.

// ---

// ## 3) Why not just start `prev` at `0` and index `memo[i][prev]`?

// If you forced `prev` to start at `0`, you’d be saying

// > “the last chosen element is `nums[0]` already”

// —which is **wrong** when we haven’t chosen anything yet. That would:

// * **Block** valid choices: you would compare `nums[i] > nums[0]` on the very first decision even if `nums[0]` was **not** picked.
// * **Break correctness**: the initial freedom to pick **any** first element requires a **sentinel** previous index that doesn’t impose a constraint. That’s exactly what `prev = -1` encodes (“no constraint yet”).

// The `prev = -1` state is essential to:

// * allow the first chosen element to be **any** element, and
// * write the check concisely:
//   `if (prev == -1 || nums[i] > nums[prev]) { can take nums[i] }

// Without a distinct “no previous” value, you’d need messy special cases—or you’d accidentally treat some real index as the previous pick.

// ---

// ## Alternatives (just for completeness)

// * Use a **hash map** keyed by `(i, prev)` tuples; then you don’t need the `+1` shift, but it’s slower and more verbose.
// * Encode the “no previous” state by an **extra column** in another way (e.g., `[n][n]` plus a separate array for the `prev=-1` column), but `prev+1` is cleaner.
// * There’s also an **O(n log n)** method (patience sorting) that doesn’t need `prev` at all, but that’s a different algorithm.

// ---

// ### TL;DR

// * 2D memo because LIS state depends on **(i, prev)**.
// * `[n][n+1]` because `prev` has **n+1** possible values (`-1..n-1`), and we store them at `prev+1`.
// * We **must** have a distinct “no previous” sentinel (`prev = -1`); starting `prev` at `0` would incorrectly assume `nums[0]` is already chosen.


// ```
// nums = [10, 9, 2, 5, 3, 7, 101, 18]
// ```

// ---

// ## 🧩 Reminder of what each state means

// `dfs(i, prev)` = **length of the longest increasing subsequence** in `nums[i .. end]`,
// given that the **last chosen element** has index `prev` (or `-1` if none chosen yet).

// * `i` ranges `0..n-1`
// * `prev` ranges `-1..n-1` (we store it in `memo[i][prev+1]`)

// ---

// ## 🪜 Step-by-step recursion

// ### Start:

// ```
// dfs(0, -1)
// ```

// We haven’t chosen anything yet (`prev = -1`).

// ### i = 0, prev = -1  → nums[i] = 10

// Two choices:

// 1. **Skip 10:**  dfs(1, -1)
// 2. **Take 10:**  1 + dfs(1, 0)

// We’ll explore both.

// ---

// ### Branch 1: Skip 10 → dfs(1, -1)

// Now at index 1 (`nums[1] = 9`), still nothing chosen.

// Choices:

// * Skip → dfs(2, -1)
// * Take → 1 + dfs(2, 1)

// ---

// ### Branch 1.1: Skip 9 → dfs(2, -1)

// `nums[2] = 2`

// * Skip → dfs(3, -1)
// * Take → 1 + dfs(3, 2)

// ---

// ### Branch 1.1.1: Skip 2 → dfs(3, -1)

// `nums[3] = 5`

// * Skip → dfs(4, -1)
// * Take → 1 + dfs(4, 3)

// ---

// ### Branch 1.1.1.1: Skip 5 → dfs(4, -1)

// `nums[4] = 3`

// * Skip → dfs(5, -1)
// * Take → 1 + dfs(5, 4)

// ---

// ### …and so on, until i == nums.length → base case = 0.

// Eventually, the recursion will explore every possible subsequence **but** store results in `memo` so it doesn’t recompute them.

// ---

// ## 🧮 Let’s see how memoization works

// Take one repeated subproblem:
// `dfs(5, -1)` (start at index 5, no previous chosen).

// It will be called multiple times:

// * Once from the “skip 3” path (index 4 skipped)
// * Once from the “skip 5” path (index 3 skipped)
// * etc.

// **Without memoization:** each call would re-explore the tail `[7, 101, 18]`.

// **With memoization:**

// * The first time we compute `dfs(5, -1)`, we explore it fully and store the answer (say it finds LIS length = 3 → `[7, 101]` or `[7, 18]`).
// * The second time, the line

//   ```java
//   if (memo[i][prev+1] != null) return memo[i][prev+1];
//   ```

//   immediately returns that stored value, skipping all deeper recursion.

// This collapsing of repeated tails reduces exponential work to **O(n²)** distinct `(i, prev)` states.

// ---

// ## 🧠 How the result builds up (condensed trace)

// Let’s follow the “taking” logic to see actual LIS lengths.

// | Call          | Meaning              | Best result                                                |
// | ------------- | -------------------- | ---------------------------------------------------------- |
// | `dfs(7, any)` | at last element `18` | can take → LIS = 1                                         |
// | `dfs(6, any)` | `nums[6]=101`        | can take → LIS = 1                                         |
// | `dfs(5, -1)`  | starting from `7`    | take 7 → `1 + dfs(6,5)` → `1 + 1 = 2`                      |
// | `dfs(4, -1)`  | starting from `3`    | can build `[3,7,101]` length 3                             |
// | `dfs(3, -1)`  | starting from `5`    | `[5,7,101]` length 3                                       |
// | `dfs(2, -1)`  | starting from `2`    | `[2,3,7,101]` length 4                                     |
// | `dfs(1, -1)`  | starting from `9`    | `[9,101]` length 2                                         |
// | `dfs(0, -1)`  | starting from `10`   | `[10,101]` length 2, but skipping 10 gives 4 → overall = 4 |

// So the recursion finds **length = 4**, which matches the LIS `[2,3,7,101]`.

// ---

// ## 🔁 How memo saves time

// There are only `n × (n+1)` unique subproblems:
// For `n=8`, that’s 8×9=72 states.

// Without memoization, recursion could easily branch into **thousands** of calls. With memoization, each state is computed once.

// ---

// ## 🔍 Visualization (subset)

// ```
// dfs(0,-1)
//  ├── skip -> dfs(1,-1)
//  │    ├── skip -> dfs(2,-1)
//  │    │    ├── skip -> dfs(3,-1)
//  │    │    │    ├── skip -> dfs(4,-1)
//  │    │    │    └── take  -> 1 + dfs(4,3)
//  │    │    └── take  -> 1 + dfs(3,2)
//  │    └── take  -> 1 + dfs(2,1)
//  └── take  -> 1 + dfs(1,0)
// ```

// As soon as, say, `dfs(4,-1)` is computed once, all future references return immediately from memo.

// ---

// ## ✅ Summary of how it works

// | Concept              | Explanation                                                                                          |
// | -------------------- | ---------------------------------------------------------------------------------------------------- |
// | **State**            | `(i, prev)` where `i` is current index and `prev` is last chosen element index (or `-1`)             |
// | **Transition**       | Skip `nums[i]` → `dfs(i+1, prev)`  <br> Take `nums[i]` if `nums[i] > nums[prev]` → `1 + dfs(i+1, i)` |
// | **Base Case**        | `i == nums.length` → 0                                                                               |
// | **Memo key**         | `memo[i][prev+1]`  (shifted so `-1` becomes column 0)                                                |
// | **Time Complexity**  | `O(n²)`  (each pair `(i, prev)` computed once)                                                       |
// | **Space Complexity** | `O(n²)` for memo + recursion stack                                                                   |

// ---

// ### Final Answer for `[10, 9, 2, 5, 3, 7, 101, 18]`

// **LIS length = 4**
// One valid LIS is `[2, 3, 7, 101]`.

// This top-down approach mirrors the standard iterative DP but expresses it recursively with clean subproblem reuse.
*/

// class Solution {
//     public int lengthOfLIS(int[] nums) {
//         int n = nums.length;
//         // memo[i][prev+1] where prev in [-1..n-1] → shift by +1 to index 0..n
//         Integer[][] memo = new Integer[n][n + 1];
//         return dfs(0, -1, nums, memo);
//     }

//     // returns LIS length from i..end, given last chosen index = prev (or -1 if none)
//     private int dfs(int i, int prev, int[] nums, Integer[][] memo) {
//         if (i == nums.length) return 0;

//         if (memo[i][prev + 1] != null) return memo[i][prev + 1];

//         // Option 1: skip nums[i]
//         int best = dfs(i + 1, prev, nums, memo);

//         // Option 2: take nums[i] if it extends
//         if (prev == -1 || nums[i] > nums[prev]) {
//             best = Math.max(best, 1 + dfs(i + 1, i, nums, memo));
//         }

//         return memo[i][prev + 1] = best;
//     }
// }






// Method 1.5: My Top-Down approach
/*
#################### WHAT WAS I DOING WRONG ######################
The main problem is this line:

```java
dp(nums, memo, 0);
```

You are only computing the LIS that **starts at index 0** and whatever states get reached from there.

But the LIS can start at **any** index, not necessarily `0`.

## Why this is wrong

Your `dp(i)` means:

```text
length of LIS starting at index i
```

So the final answer should be:

```text
max(dp(i)) for all i
```

But in your code, you only call:

```java
dp(..., 0)
```

That means some `memo[i]` values may never get computed.

Then later you do:

```java
for (int val : memo) {
    ans = Math.max(ans, val);
}
```

But many entries may still be `-1`, because you never called `dp` on them.

---

## Example where it fails

Take:

```java
nums = [5, 1, 2, 3]
```

The actual LIS is:

```text
1, 2, 3
```

length = `3`

But your recursion starts only from index `0`:

```text
dp(0) = LIS starting from 5
```

There are no later elements greater than `5`, so:

```java
memo[0] = 1
```

And no other states get explored, because from `5` you cannot go anywhere.

So memo becomes something like:

```java
[1, -1, -1, -1]
```

Then your max loop gives:

```java
ans = 1
```

which is wrong.

---

# How to fix it

You must call `dp` for **every** starting index:

```java
int ans = 0;
for (int i = 0; i < n; i++) {
    ans = Math.max(ans, dp(nums, memo, i));
}
return ans;
```

Then every needed state gets computed, and you directly build the answer correctly.


# Is the recursive logic itself correct?

Yes. This part is fine:

```java
for(int j=i+1; j<nums.length; j++){
    if(nums[j] > nums[i]){
        best = Math.max(best, 1 + dp(nums, memo, j));
    }
}
```

It correctly says:

* from index `i`
* try every later bigger element `j`
* take the best continuation

So the recurrence is correct.

---

# Small conceptual summary

Your state is:

```text
dp(i) = LIS length starting at i
```

That does **not** mean the overall answer is `dp(0)`.

It means:

```text
answer = max(dp(0), dp(1), dp(2), ..., dp(n-1))
```

because the best subsequence may begin anywhere.

---

# One more edge note

If the input were empty, this code would need handling, but for LeetCode 300 the array length is usually at least 1. Still, for robustness, many people add:

```java
if (nums.length == 0) return 0;
```

---

The only real bug is that you started recursion from just index `0` instead of all indices.

##################################################################

Here are progressive **top-down DP / memoization hints** for **LeetCode 300. Longest Increasing Subsequence**.

## Hint 1

A very natural top-down question is:

> What is the length of the LIS that **starts at index `i`**?

So define:

```text
dfs(i) = length of the longest increasing subsequence starting at i
```

Then the final answer is:

```text
max(dfs(i)) for all i
```

because the overall LIS can start anywhere.

---

## Hint 2

If you are at index `i`, the next element in the subsequence must come from some later index `j > i` such that:

```text
nums[j] > nums[i]
```

So from `i`, you try all valid next choices.

---

## Hint 3

That gives the recurrence:

```text
dfs(i) = 1 + max(dfs(j)) for all j > i where nums[j] > nums[i]
```

If there is no such `j`, then:

```text
dfs(i) = 1
```

because the subsequence can just be the element at `i` itself.

---

## Hint 4

Why does this work?

Because once you choose `nums[i]` as the current element, the rest of the problem is:

> find the longest increasing subsequence among later elements that are bigger than `nums[i]`

That is exactly what the recursive calls represent.

---

## Hint 5

Memoization is straightforward:

```text
memo[i] = LIS length starting at index i
```

If `memo[i]` is already computed, return it directly.

That avoids recomputing the same suffix problem many times.

---

## Hint 6

Pseudocode shape:

```text
dfs(i):
    if memo[i] already exists:
        return memo[i]

    best = 1

    for j from i + 1 to n - 1:
        if nums[j] > nums[i]:
            best = max(best, 1 + dfs(j))

    memo[i] = best
    return best
```

Then:

```text
answer = 0
for i from 0 to n - 1:
    answer = max(answer, dfs(i))
```

---

## Hint 7

Example:

```text
nums = [1, 2, 4, 3]
```

* `dfs(0)` looks for bigger elements after `1`
* it can go to `2`, `4`, or `3`
* the best path is `1 -> 2 -> 4` or `1 -> 2 -> 3`
* so `dfs(0) = 3`

---

## Hint 8

Notice this top-down version uses only **one index**, not two.

That is because the current element is fixed by the starting index `i`, and that already tells you the constraint for what can come next.

---

## Hint 9

There is also another top-down formulation with two parameters:

```text
dfs(index, prevIndex)
```

meaning:

* you are currently at `index`
* the previous chosen element was at `prevIndex`

This is more general, but also heavier.

For learning DP, it is useful, but for this problem the **one-index memoized version** is cleaner.

---

## Hint 10

The one-index version works well because:

* `dfs(i)` assumes `nums[i]` is already chosen
* then it only needs to figure out the best continuation

So it is similar to the bottom-up state:

```text
dp[i] = LIS length starting from i
```

just computed recursively instead of iteratively.

---

## Hint 11

Base case is kind of implicit here:

If no later `j` satisfies:

```text
nums[j] > nums[i]
```

then the loop does nothing and `best` stays `1`.

So the subsequence is just `[nums[i]]`.

---

## Hint 12

Common mistake:
do not assume the answer is `dfs(0)`.

The LIS does **not** have to start at index `0`.

So you must compute:

```text
max over all starting indices
```

---

## Hint 13

Small dry run for:

```text
nums = [10, 9, 2, 5, 3, 7]
```

* `dfs(5)` for `7` = 1
* `dfs(4)` for `3` can go to `7`, so = 2
* `dfs(3)` for `5` can go to `7`, so = 2
* `dfs(2)` for `2` can go to `5`, `3`, `7`, best = 3

So the LIS starting at `2` has length 3: `[2, 5, 7]` or `[2, 3, 7]`

---

## Hint 14

Time complexity with memoization:

* there are `n` states
* each state loops over later indices

So total is:

```text
O(n^2)
```

with:

```text
O(n)
```

memo space, ignoring recursion stack.

---

## Hint 15

If you want a very compact recurrence:

```text
dfs(i) = 1 + max(dfs(j)) over all j > i with nums[j] > nums[i]
```

and if no such `j` exists, return `1`.

---

## Hint 16

Mental model:

At each position, ask:

> If I force myself to take this number, what is the longest increasing subsequence I can build after it?

That is exactly the recursive state.
*/
class Solution {
    public int lengthOfLIS(int[] nums) {
        int n = nums.length;
        
        int[] memo = new int[n];
        Arrays.fill(memo, -1);

        int ans = 0;
        for(int i=0; i<n; i++){
            ans = Math.max(ans, dp(nums, memo, i));
        }

        return ans;
    }

    private int dp(int[] nums, int[] memo, int i){
        if(memo[i] != -1){
            return memo[i];
        }

        int best = 1;

        for(int j=i+1; j<nums.length; j++){
            if(nums[j] > nums[i]){
                best = Math.max(best, 1 + dp(nums, memo, j));
            }
        }

        memo[i] = best;
        return best;
    }
}







// Method 2: Bottom-Up DP
/*
## Idea

Let `dp[i]` be the **length of the LIS that ends at index `i`** (i.e., the subsequence must include `nums[i]` as its last element).

Transition:

* Initialize `dp[i] = 1` for all `i` (a single element is an LIS of length 1).
* For each `i` from left to right, look back at all `j < i`:

  * If `nums[j] < nums[i]`, we can extend the LIS ending at `j` to `i`:

    ```
    dp[i] = max(dp[i], dp[j] + 1)
    ```
* Answer is `max(dp[i])` over all `i`.

### Complexity

* Time: `O(n²)`
* Space: `O(n)`

### Walkthrough (nums = `[10, 9, 2, 5, 3, 7, 101, 18]`)

Start: `dp = [1,1,1,1,1,1,1,1]`, best = 1

* `i=1 (9)`: compare with `10` → `10 < 9`? no → `dp[1]=1`
* `i=2 (2)`: compare with `10, 9` → no smaller → `dp[2]=1`
* `i=3 (5)`: compare with `10, 9` (no), `2` (yes) → `dp[3]=max(1, dp[2]+1=2)=2`
* `i=4 (3)`: compare with `10,9` (no), `2` (yes) → `dp[4]=2`; compare with `5` (no) → `dp[4]=2`
* `i=5 (7)`: smaller predecessors: `2( dp=1 )`, `5(2)`, `3(2)` → best extend is from `5` or `3`: `dp[5]=3`
* `i=6 (101)`: all previous are smaller; best previous dp is `3` (from `7`) → `dp[6]=4`
* `i=7 (18)`: smaller predecessors include `2(1)`, `5(2)`, `3(2)`, `7(3)` → best extend from `7` → `dp[7]=4`

Final `dp = [1,1,1,2,2,3,4,4]` → answer `4` (e.g., `[2,3,7,101]`).

*/

// class Solution {
//     public int lengthOfLIS(int[] nums) {
//         int n = nums.length;
//         if (n == 0) return 0;

//         int[] dp = new int[n];
//         Arrays.fill(dp, 1); // each element alone

//         int best = 1;
//         for (int i = 1; i < n; i++) {
//             for (int j = 0; j < i; j++) {
//                 if (nums[j] < nums[i]) {
//                     dp[i] = Math.max(dp[i], dp[j] + 1);
//                 }
//             }
//             best = Math.max(best, dp[i]);
//         }
//         return best;
//     }
// }







// Method 3: Patience Sorting using Binary Search Method 
/*
## Idea

Maintain an array `tails`, where:

* `tails[len-1]` is the **smallest possible tail value** of an increasing subsequence of length `len`.
* For each `x` in `nums`:

  * Find the **first** index `pos` in `tails` with `tails[pos] >= x` (lower_bound).
  * If such `pos` exists, set `tails[pos] = x` (we found a better/smaller tail for length `pos+1`).
  * Else append `x` to `tails` (we extended the longest length so far).
* The **length** of `tails` at the end is the LIS length.
  (We don’t necessarily reconstruct the sequence here; `tails` tracks only best tails.)


### Complexity

* Time: `O(n log n)` due to binary search per element
* Space: `O(n)` for `tails`

### Walkthrough (nums = `[10, 9, 2, 5, 3, 7, 101, 18]`)

`tails` evolves (always non-decreasing):

1. `10` → `tails = [10]`
2. `9`  → lower_bound(9) = 0 → replace → `[9]`
3. `2`  → lb(2)=0 → `[2]`
4. `5`  → lb(5)=1 (past end) → append → `[2, 5]`
5. `3`  → lb(3)=1 → replace → `[2, 3]`
6. `7`  → lb(7)=2 → append → `[2, 3, 7]`
7. `101`→ lb(101)=3 → append → `[2, 3, 7, 101]`
8. `18` → lb(18)=3 → replace → `[2, 3, 7, 18]`

`tails.size() = 4` → LIS length is `4`.
Note: `[2,3,7,18]` is not necessarily the actual LIS found earlier (`[2,3,7,101]`), but the length is correct; `tails` keeps minimal possible tails to allow future extensions.


## Common pitfalls

* In O(n log n): using `>` instead of `>=` in lower_bound changes semantics (you want the **first** `≥ x`).
* LIS is **strictly increasing**; change to `>`/`>=` consistently if a problem asks for non-decreasing.

*/

// class SolutionFast {
//     public int lengthOfLIS(int[] nums) {
//         List<Integer> tails = new ArrayList<>();
//         for (int x : nums) {
//             int pos = lowerBound(tails, x); // first index with tails[pos] >= x
//             if (pos == tails.size()) {
//                 tails.add(x);
//             } else {
//                 tails.set(pos, x);
//             }
//         }
//         return tails.size();
//     }

//     // Standard lower_bound for non-decreasing sequence
//     private int lowerBound(List<Integer> a, int target) {
//         int lo = 0, hi = a.size();
//         while (lo < hi) {
//             int mid = (lo + hi) >>> 1;
//             if (a.get(mid) < target) lo = mid + 1;
//             else hi = mid;
//         }
//         return lo;
//     }
// }

