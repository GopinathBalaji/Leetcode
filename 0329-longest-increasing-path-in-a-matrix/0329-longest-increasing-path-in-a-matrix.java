// Method 1: Top-Down DP = DFS + memoization
/*
## 1. You’re summing paths instead of taking the max

What the problem asks:

> For each cell `(i,j)`, `dp(i,j)` = **length of the longest increasing path starting from `(i,j)`**.

So the recurrence should be:

```text
dp(i,j) = 1 + max(
    dp(neighbor1),
    dp(neighbor2),
    dp(neighbor3),
    dp(neighbor4)
)
```

(where neighbors must have a strictly *greater* value to be valid moves).

But your code does:

```java
int right = ... // 0 or dp(...)
int down  = ...
int left  = ...
int up    = ...

matrix[i][j] = up + down + left + right;
return matrix[i][j];
```

This is **not** the longest path; it’s the **sum** of all possible path lengths in each direction.

### Why this is wrong

Imagine:

```text
3 4
2 1
```

At `(0,0) = 3`, suppose:

* Right path length is 1,
* Down is 5,
* Left/up don’t exist (0).

The correct answer for that cell is `max(1,5) + 1` (including the current cell), but your code uses `1+5+0+0 = 6`.

✅ Correct recurrence should be:

```java
int best = 1;  // at least the cell itself
if (can_go_right) best = Math.max(best, 1 + dp(...right...));
if (can_go_down)  best = Math.max(best, 1 + dp(...down...));
if (can_go_left)  best = Math.max(best, 1 + dp(...left...));
if (can_go_up)    best = Math.max(best, 1 + dp(...up...));

memo[i][j] = best;
return best;
```

---

## 2. You’re overwriting the `matrix` and never writing to `memo`

You created:

```java
Integer[][] memo = new Integer[m][n];
```

and you *read* from it:

```java
if (memo[i][j] != null) {
    return memo[i][j];
}
```

But you **never set** `memo[i][j]` anywhere.

Instead, at the end of `dp`, you do:

```java
matrix[i][j] = up + down + left + right;
return matrix[i][j];
```

This causes **two problems**:

### (a) Memoization doesn’t actually happen

Because you never do `memo[i][j] = ...`, the memo remains all `null`, so the `if (memo[i][j] != null)` shortcut never triggers. That means:

* You recompute `dp(i,j)` over and over from scratch.
* Complexity can blow up to exponential.

### (b) You corrupt the input matrix

By writing `matrix[i][j] = ...`, you are changing the original values that are used for comparisons like:

```java
if (j+1 < n && matrix[i][j] < matrix[i][j+1])
```

Once you modify `matrix[i][j]`, the comparison no longer reflects the original problem’s grid.

So after the first round of DFS, your `matrix` no longer contains the original heights; it contains random DP values, and all the future comparisons become meaningless.

✅ You should **never mutate** `matrix` here. Instead store the result into `memo`:

```java
int best = 1 + max(...); // or just best as described above
memo[i][j] = best;
return best;
```

Leave `matrix` read-only.

---

## 3. Off-by-one bugs in the left / up boundary checks

You have:

```java
int left = 0;
if (j-1 > 0 && matrix[i][j] < matrix[i][j-1]) {
    left = dp(matrix, memo, m, n, i, j-1);
}

int up = 0;
if (i-1 > 0 && matrix[i][j] < matrix[i-1][j]) {
    up = dp(matrix, memo, m, n, i-1, j);
}
```

### What’s wrong?

* For `left`, you’re checking `j-1 > 0`, i.e. `j > 1`.
* For `up`, you’re checking `i-1 > 0`, i.e. `i > 1`.

That means:

* When `j == 1`, `j-1` is `0`, which is a valid index, but `j-1 > 0` is `false`. So you **never go left** from column 1 to column 0.
* Similarly, when `i == 1`, you never go up to row 0.

You’re unintentionally skipping valid neighbors `(i,0)` and `(0,j)`.

✅ Correct boundary checks should be:

```java
if (j - 1 >= 0 && matrix[i][j] < matrix[i][j-1]) {
    left = dp(matrix, memo, m, n, i, j-1);
}

if (i - 1 >= 0 && matrix[i][j] < matrix[i-1][j]) {
    up = dp(matrix, memo, m, n, i-1, j);
}
```

or equivalently:

```java
if (j > 0 && matrix[i][j] < matrix[i][j-1]) { ... }
if (i > 0 && matrix[i][j] < matrix[i-1][j]) { ... }
```

---

## 4. Your base case doesn’t match how you call `dp`

You wrote:

```java
if (i < 0 || i >= m || j < 0 || j >= n) {
    return 0;
}
```

But you never actually call `dp` with out-of-bounds indices, because you guard *before* calling:

```java
if (j+1 < n && matrix[i][j] < matrix[i][j+1]) {
    right = dp(... i, j+1);
}
```

So the base case is basically unused / redundant. That’s not *wrong* logically, but it’s unnecessary and can be confusing.

In a clean solution, you usually do one of:

* Either guard before calling (like you do) and drop the out-of-bounds check inside, or
* Allow calls with any (i,j) and keep the out-of-bounds check, but then don’t guard outside.

---

## 5. You’re not counting the current cell in the path length

Even if everything else were fixed, notice you never add `1` for the current cell:

```java
matrix[i][j] = up + down + left + right;
return matrix[i][j];
```

Even if we pretend `up`, `down`, etc. were correct and we took max instead of sum, you’d still be missing the current cell in the path length.

The length of the path **starting at (i,j)** should always be at least **1** (the cell itself).

✅ So the correct structure is:

```java
int best = 1; // path of length 1: just this cell

if (can_go_right) best = Math.max(best, 1 + dp(...right...));
if (can_go_down)  best = Math.max(best, 1 + dp(...down...));
if (can_go_left)  best = Math.max(best, 1 + dp(...left...));
if (can_go_up)    best = Math.max(best, 1 + dp(...up...));

memo[i][j] = best;
return best;
```

Key differences from your code:

1. Uses `max` of neighbors, not sum.
2. Adds `1` for the current cell.
3. Uses `memo[i][j]` to store results; matrix is never modified.
4. Fixes boundary checks for left/up.
5. Drops unused out-of-bounds base case (it’s not needed since we guard before recursing).

Key ideas:

* `dp(i, j)` = length of **longest increasing path starting at (i, j)**.
* “Increasing” means you can only move to neighbors with **strictly greater** value.
* `memo[i][j]` caches the answer for `(i, j)` so we compute it once.

---

## 2. Example matrix

Let’s use the classic LeetCode example:

```text
matrix = [
  [9, 9, 4],
  [6, 6, 8],
  [2, 1, 1]
]
```

Index it as:

```text
(0,0)=9  (0,1)=9  (0,2)=4
(1,0)=6  (1,1)=6  (1,2)=8
(2,0)=2  (2,1)=1  (2,2)=1
```

We’ll build `memo` (same size) where `memo[i][j]` = result of `dp(i,j)`.

Initially:

```text
memo = [
  [0, 0, 0],
  [0, 0, 0],
  [0, 0, 0]
]
```

`0` means “not computed yet”.

---

## 3. Outer loops in `longestIncreasingPath`

We do:

```java
for i from 0 to 2:
  for j from 0 to 2:
    maxLen = max(maxLen, dp(i, j))
```

We’ll go in order and see what happens.

---

## 4. Compute dp(0,0)

Cell `(0,0)` value = `9`.

Call `dp(0, 0)`:

* `memo[0][0]` is `0` → not computed.
* `current = matrix[0][0] = 9`
* `best = 1` (path with just this cell)

Neighbors with greater value?

* Right `(0,1)` = 9 → **not greater** (must be `>`).
* Down `(1,0)` = 6 → **not greater**.
* Left `(0,-1)` → out of bounds.
* Up `(-1,0)` → out of bounds.

So no valid moves.

* `memo[0][0] = best = 1`
* Return `1`.

Now:

```text
memo =
[
  [1, 0, 0],
  [0, 0, 0],
  [0, 0, 0]
]

maxLen = max(0, 1) = 1
```

---

## 5. Compute dp(0,1)

Cell `(0,1)` value = `9`.

Call `dp(0, 1)`:

* `memo[0][1]` is `0`.
* `current = 9`, `best = 1`.

Neighbors:

* Right `(0,2)` = 4 → 4 <= 9 → no move.
* Down `(1,1)` = 6 → 6 <= 9 → no move.
* Left `(0,0)` = 9 → 9 <= 9 → not strictly greater.
* Up `(-1,1)` → out of bounds.

No moves, so:

* `memo[0][1] = 1`.
* Return `1`.

Update:

```text
memo =
[
  [1, 1, 0],
  [0, 0, 0],
  [0, 0, 0]
]

maxLen = max(1, 1) = 1
```

---

## 6. Compute dp(0,2)

Cell `(0,2)` value = `4`.

Call `dp(0, 2)`:

* `memo[0][2] == 0`.
* `current = 4`, `best = 1`.

Check neighbors:

### Right

* `(0,3)` out of bounds → ignore.

### Down

* `(1,2)` = 8 which is > 4 → valid move.
* We need `dp(1,2)`.

#### Compute dp(1,2)

Cell `(1,2)` value = `8`.

* `memo[1][2] == 0`.
* `current = 8`, `best = 1`.

Neighbors:

* Right `(1,3)` → out of bounds.
* Down `(2,2)` = 1 → 1 <= 8 → ignore.
* Left `(1,1)` = 6 → 6 <= 8 → ignore.
* Up `(0,2)` = 4 → 4 <= 8 → ignore.

No valid moves, so:

* `memo[1][2] = 1`
* Return `1`.

Back to `dp(0,2)`:

* From down, path length = `1 + dp(1,2) = 1 + 1 = 2`.
* `best = max(1, 2) = 2`.

### Left

* `(0,1)` = 9 (>4) → valid.
* Need `dp(0,1)` but we already computed: `memo[0][1] = 1`.
* So from left, path length = `1 + 1 = 2`.
* `best` stays `2`.

### Up

* `(-1,2)` out of bounds.

So final:

* `memo[0][2] = 2`.
* Return `2`.

Now state:

```text
memo =
[
  [1, 1, 2],
  [0, 0, 1],
  [0, 0, 0]
]

maxLen = max(1, 2) = 2
```

Up to now, the best path we’ve seen has length 2.

---

## 7. Compute dp(1,0)

Cell `(1,0)` value = `6`.

Call `dp(1, 0)`:

* `memo[1][0] == 0`.
* `current = 6`, `best = 1`.

Neighbors:

### Right

* `(1,1)` = 6 → not strictly greater.

### Down

* `(2,0)` = 2 → 2 <= 6 → ignore.

### Left

* `(1,-1)` out of bounds.

### Up

* `(0,0)` = 9 (>6) → valid.
* Need `dp(0,0)` → already computed = 1.
* Path length = `1 + 1 = 2`.
* `best = max(1, 2) = 2`.

So:

* `memo[1][0] = 2`.
* Return `2`.

Update:

```text
memo =
[
  [1, 1, 2],
  [2, 0, 1],
  [0, 0, 0]
]

maxLen = max(2, 2) = 2
```

---

## 8. Compute dp(1,1)

Cell `(1,1)` value = `6`.

Call `dp(1, 1)`:

* `memo[1][1] == 0`.
* `current = 6`, `best = 1`.

Neighbors:

### Right

* `(1,2)` = 8 (>6) → valid.
* `dp(1,2)` = 1 (memo).
* Path via right = `1 + 1 = 2` → `best = 2`.

### Down

* `(2,1)` = 1 → not greater.

### Left

* `(1,0)` = 6 → not strictly greater.

### Up

* `(0,1)` = 9 (>6) → valid.
* `dp(0,1)` = 1 (memo).
* Path via up = `1 + 1 = 2` → `best` stays 2.

So:

* `memo[1][1] = 2`.
* Return `2`.

Now:

```text
memo =
[
  [1, 1, 2],
  [2, 2, 1],
  [0, 0, 0]
]

maxLen = max(2, 2) = 2
```

---

## 9. We already did (1,2) when computing dp(0,2)

At this point, when the outer loop hits `(1,2)`, `memo[1][2]` is already `1`, so `dp(1,2)` returns immediately.

* `maxLen` stays `2`.

---

## 10. Compute dp(2,0)

Now the fun part: lower-left corner.

Cell `(2,0)` value = `2`.

Call `dp(2, 0)`:

* `memo[2][0] == 0`.
* `current = 2`, `best = 1`.

Neighbors:

### Right

* `(2,1)` = 1 → not greater.

### Down

* `(3,0)` out of bounds.

### Left

* `(2,-1)` out of bounds.

### Up

* `(1,0)` = 6 (>2) → valid.
* Need `dp(1,0)`; memo says `dp(1,0) = 2`.

Path via up = `1 + dp(1,0) = 1 + 2 = 3`.

* `best = max(1, 3) = 3`.

So:

* `memo[2][0] = 3`.
* Return `3`.

Update:

```text
memo =
[
  [1, 1, 2],
  [2, 2, 1],
  [3, 0, 0]
]

maxLen = max(2, 3) = 3
```

Interpretation: longest increasing path starting from value 2 at `(2,0)` has length 3.
One such path: `2 → 6 → 9`.

---

## 11. Compute dp(2,1) (this will give the global answer)

Cell `(2,1)` value = `1`.

Call `dp(2, 1)`:

* `memo[2][1] == 0`.
* `current = 1`, `best = 1`.

Neighbors:

### Right

* `(2,2)` = 1 → not **greater** (equal), so no.

### Down

* `(3,1)` out of bounds.

### Left

* `(2,0)` = 2 (>1) → valid.

  * `dp(2,0)` is already `3` in memo.
  * Path length via left = `1 + 3 = 4`.
  * `best = max(1, 4) = 4`.

### Up

* `(1,1)` = 6 (>1) → also valid.

  * `dp(1,1)` = 2.
  * Path length via up = `1 + 2 = 3`.
  * `best` remains `4` (max of 4 and 3).

So:

* `memo[2][1] = 4`.
* Return `4`.

Now:

```text
memo =
[
  [1, 1, 2],
  [2, 2, 1],
  [3, 4, 0]
]

maxLen = max(3, 4) = 4
```

Meaning: **the longest increasing path starting at the `1` at `(2,1)` has length 4**.

A concrete path is:

* `(2,1)` = `1`
* → `(2,0)` = `2`
* → `(1,0)` = `6`
* → `(0,0)` = `9`

So: `1 → 2 → 6 → 9`, length 4.

---

## 12. Compute dp(2,2)

Finally:

Cell `(2,2)` value = `1`.

Call `dp(2, 2)`:

* `memo[2][2] == 0`.
* `current = 1`, `best = 1`.

Neighbors:

### Right / Down

* Out of bounds.

### Left

* `(2,1)` = 1 → not greater.

### Up

* `(1,2)` = 8 (>1) → valid.
* `dp(1,2)` = 1 (memo).
* Path via up = `1 + 1 = 2`.
* `best = 2`.

So:

* `memo[2][2] = 2`.

Final `memo`:

```text
memo =
[
  [1, 1, 2],
  [2, 2, 1],
  [3, 4, 2]
]
```

Outer loop finishes; `maxLen` is `4`.

So the algorithm returns `4`, which is the correct answer.

---

## 13. What this walkthrough shows

1. **Each cell is solved once**
   Every time `dp(i,j)` is called the first time, it recursively solves its neighbors, then **caches** the result in `memo[i][j]`.

   Any further calls to `dp(i,j)` just read from `memo[i][j]` and return instantly.

2. **We explore outward from each cell**
   From a cell, we “expand” only to neighbors with a strictly higher value. That prevents cycles and enforces an increasing path.

3. **Path length includes the current cell**
   That’s why we start with `best = 1` and add `1 + dp(neighbor)` when moving.

4. **Longest path anywhere = max over all dp(i,j)**
   Some cells (like the `1` at `(2,1)`) have very long paths ahead; others (like the `9`s) have no neighbors bigger than them, so their path length is just 1.
*/

class Solution {
    public int longestIncreasingPath(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        int[][] memo = new int[m][n]; // 0 means "not computed yet"
        int maxLen = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                maxLen = Math.max(maxLen, dp(matrix, memo, m, n, i, j));
            }
        }

        return maxLen;
    }

    private int dp(int[][] matrix, int[][] memo, int m, int n, int i, int j) {
        // If we already computed this cell's best path length, return it
        if (memo[i][j] != 0) {
            return memo[i][j];
        }

        int current = matrix[i][j];
        int best = 1; // at least the cell itself

        // Right
        if (j + 1 < n && current < matrix[i][j + 1]) {
            best = Math.max(best, 1 + dp(matrix, memo, m, n, i, j + 1));
        }

        // Down
        if (i + 1 < m && current < matrix[i + 1][j]) {
            best = Math.max(best, 1 + dp(matrix, memo, m, n, i + 1, j));
        }

        // Left
        if (j - 1 >= 0 && current < matrix[i][j - 1]) {
            best = Math.max(best, 1 + dp(matrix, memo, m, n, i, j - 1));
        }

        // Up
        if (i - 1 >= 0 && current < matrix[i - 1][j]) {
            best = Math.max(best, 1 + dp(matrix, memo, m, n, i - 1, j));
        }

        memo[i][j] = best;
        return best;
    }
}






// Method 2: Bottom-Up DP = BFS version
/*
## 1. Core idea: reinterpret the DP meaning

In the DFS/memo version, we defined:

> `dp(i, j)` = **longest increasing path *starting* at cell (i, j)**.

That’s very natural for DFS.

For a **bottom-up** solution, it’s easier to flip the direction:

> Let `dp(i, j)` = **length of the longest increasing path that *ends* at cell (i, j)**.

So:

* The last cell of the path is `(i, j)`.
* The path moves from smaller values to larger values.
* So every predecessor of `(i, j)` in a valid path must:

  * Be a neighbor (up/down/left/right), and
  * Have a value **strictly less** than `matrix[i][j]`.

Then the recurrence becomes:

```text
dp(i, j) = 1 + max(dp(ni, nj)) over all neighbors (ni, nj)
           such that matrix[ni][nj] < matrix[i][j]

If there is no neighbor with smaller value, dp(i, j) = 1.
```

Finally:

```text
answer = max(dp(i, j)) over all cells (i, j)
```

So we’re building chains that **end** at each cell.

---

## 2. How to do it bottom-up

We need to ensure that whenever we compute `dp(i, j)`, we already know `dp` values for all potential predecessors (neighbors with smaller values).

Key observation:

* A path is strictly increasing.
* So values along the path are strictly increasing.
* That means the “graph” is acyclic if we draw edges from smaller values to larger values.

That suggests a natural topological order: **sort all cells by their value**.

If we process cells in **increasing order of value**, then:

* When we process cell `(i, j)`, all neighbors with value `< matrix[i][j]` are already processed.
* So their `dp` values are already known → perfect for bottom-up.

Algorithm sketch:

1. Create a list of all cells as `(value, row, col)`.
2. Sort this list by `value` ascending.
3. Initialize `dp[i][j] = 1` for all cells (a path of length 1: just itself).
4. Iterate over cells in sorted order:

   * For each cell `(i, j)`:

     * Look at its 4 neighbors `(ni, nj)`.
     * If `matrix[ni][nj] < matrix[i][j]`:

       * It can be the second-last node in a path that ends at `(i,j)`.
       * So: `dp[i][j] = max(dp[i][j], dp[ni][nj] + 1)`.
   * Keep track of a global `maxLen`.

That’s the entire bottom-up DP.

Time complexity: `O(mn log(mn))` due to the sort; DP itself is `O(mn)`.

Key points to notice:

* `dp[i][j]` is initialized to 1.
* We only ever look at neighbors with **smaller** value (since path must be increasing).
* Sorted order guarantees we’ve already set those neighbors’ `dp` before we touch `(i, j)`.

---

## 4. Full walkthrough on an example

Let’s use the standard LeetCode example:

```text
matrix = [
  [9, 9, 4],
  [6, 6, 8],
  [2, 1, 1]
]
```

Coordinates:

```text
(0,0)=9  (0,1)=9  (0,2)=4
(1,0)=6  (1,1)=6  (1,2)=8
(2,0)=2  (2,1)=1  (2,2)=1
```

### Step 1: Build and sort the cell list

We flatten the matrix into `(value, row, col)`:

* `(9, 0, 0)`
* `(9, 0, 1)`
* `(4, 0, 2)`
* `(6, 1, 0)`
* `(6, 1, 1)`
* `(8, 1, 2)`
* `(2, 2, 0)`
* `(1, 2, 1)`
* `(1, 2, 2)`

Sort ascending by value:

```text
Value  Row Col  Cell
-----  --- ---  ----
1      2   1    (2,1)
1      2   2    (2,2)
2      2   0    (2,0)
4      0   2    (0,2)
6      1   0    (1,0)
6      1   1    (1,1)
8      1   2    (1,2)
9      0   0    (0,0)
9      0   1    (0,1)
```

Initial `dp` (all 1’s):

```text
dp =
[
  [1, 1, 1],
  [1, 1, 1],
  [1, 1, 1]
]
```

`maxLen = 1`.

---

### Step 2: Process cells in sorted order

We’ll go through each cell in the sorted list, update its `dp`, and track `maxLen`.

We use 4 neighbor directions: down, up, right, left.

---

#### 2.1 Cell (2,1), value 1

Neighbors:

* Down: (3,1) → out of bounds.
* Up:   (1,1) = 6 → **6 < 1?** no.
* Right:(2,2) = 1 → **1 < 1?** no (needs strictly less).
* Left: (2,0) = 2 → **2 < 1?** no.

So no neighbor with smaller value.

* `dp[2][1]` stays `1`.
* `maxLen = max(1, 1) = 1`.

State:

```text
dp =
[
  [1, 1, 1],
  [1, 1, 1],
  [1, 1, 1]
]
```

---

#### 2.2 Cell (2,2), value 1

Neighbors:

* Down: (3,2) → out of bounds.
* Up:   (1,2) = 8 → 8 < 1? no.
* Right:(2,3) → out of bounds.
* Left: (2,1) = 1 → 1 < 1? no.

No smaller neighbors.

* `dp[2][2]` stays `1`.
* `maxLen` stays `1`.

State unchanged.

---

#### 2.3 Cell (2,0), value 2

Neighbors:

* Down: (3,0) → out of bounds.
* Up:   (1,0) = 6 → 6 < 2? no.
* Right:(2,1) = 1 → **1 < 2? yes**.

  * Candidate = `dp[2][1] + 1 = 1 + 1 = 2`.
  * `dp[2][0] = max(1, 2) = 2`.
* Left: (2,-1) → out of bounds.

So:

* `dp[2][0] = 2`.
* `maxLen = max(1, 2) = 2`.

Interpretation: longest increasing path **ending at 2** at `(2,0)` has length 2.
One such path: `1 → 2` from `(2,1)` to `(2,0)`.

State:

```text
dp =
[
  [1, 1, 1],
  [1, 1, 1],
  [2, 1, 1]
]
```

---

#### 2.4 Cell (0,2), value 4

Neighbors:

* Down: (1,2) = 8 → 8 < 4? no.
* Up:   (-1,2) → out of bounds.
* Right:(0,3) → out of bounds.
* Left: (0,1) = 9 → 9 < 4? no.

No smaller neighbors.

* `dp[0][2]` stays `1`.
* `maxLen` stays `2`.

State:

```text
dp =
[
  [1, 1, 1],
  [1, 1, 1],
  [2, 1, 1]
]
```

---

#### 2.5 Cell (1,0), value 6

Neighbors:

* Down: (2,0) = 2 → **2 < 6? yes**.

  * `candidate = dp[2][0] + 1 = 2 + 1 = 3`.
  * `dp[1][0] = max(1, 3) = 3`.
* Up:   (0,0) = 9 → 9 < 6? no.
* Right:(1,1) = 6 → 6 < 6? no.
* Left: (1,-1) → out of bounds.

So:

* `dp[1][0] = 3`.
* `maxLen = max(2, 3) = 3`.

Interpretation: longest increasing path **ending at 6 at (1,0)** has length 3.
Example: `1 → 2 → 6` from `(2,1)` → `(2,0)` → `(1,0)`.

State:

```text
dp =
[
  [1, 1, 1],
  [3, 1, 1],
  [2, 1, 1]
]
```

---

#### 2.6 Cell (1,1), value 6

Neighbors:

* Down: (2,1) = 1 → 1 < 6? yes.

  * `cand = dp[2][1] + 1 = 1 + 1 = 2`
  * `dp[1][1] = max(1, 2) = 2`.
* Up:   (0,1) = 9 → 9 < 6? no.
* Right:(1,2) = 8 → 8 < 6? no.
* Left: (1,0) = 6 → 6 < 6? no.

No other smaller neighbors that improve it.

So:

* `dp[1][1] = 2`.
* `maxLen` stays `3` (since `max(3,2) = 3`).

State:

```text
dp =
[
  [1, 1, 1],
  [3, 2, 1],
  [2, 1, 1]
]
```

Interpretation: longest inc path ending at this 6 `(1,1)` is length 2, e.g., `1 → 6`.

---

#### 2.7 Cell (1,2), value 8

Neighbors:

* Down: (2,2) = 1 → 1 < 8? yes.

  * cand = `dp[2][2] + 1 = 1 + 1 = 2`
  * `dp[1][2] = max(1, 2) = 2`.
* Up:   (0,2) = 4 → 4 < 8? yes.

  * cand = `dp[0][2] + 1 = 1 + 1 = 2`
  * `dp[1][2]` remains `2`.
* Right:(1,3) → out of bounds.
* Left: (1,1) = 6 → 6 < 8? yes.

  * cand = `dp[1][1] + 1 = 2 + 1 = 3`
  * `dp[1][2] = max(2, 3) = 3`.

So:

* `dp[1][2] = 3`.
* `maxLen = max(3, 3) = 3`.

Interpretation: longest inc path ending at 8 `(1,2)` has length 3.
Example: `1 → 6 → 8` or `1 → 2 → 8` depending on path.

State:

```text
dp =
[
  [1, 1, 1],
  [3, 2, 3],
  [2, 1, 1]
]
```

---

#### 2.8 Cell (0,0), value 9

Neighbors:

* Down: (1,0) = 6 → 6 < 9? yes.

  * cand = `dp[1][0] + 1 = 3 + 1 = 4`
  * `dp[0][0] = max(1, 4) = 4`.
* Up:   (-1,0) → out of bounds.
* Right:(0,1) = 9 → 9 < 9? no.
* Left: (0,-1) → out of bounds.

So:

* `dp[0][0] = 4`.
* `maxLen = max(3, 4) = 4`.

Interpretation: longest inc path ending at 9 `(0,0)` has length 4.

Concretely, one such path:

* `(2,1)` = 1
* `(2,0)` = 2
* `(1,0)` = 6
* `(0,0)` = 9

→ `1 → 2 → 6 → 9`, length 4.

State:

```text
dp =
[
  [4, 1, 1],
  [3, 2, 3],
  [2, 1, 1]
]
```

---

#### 2.9 Cell (0,1), value 9

Neighbors:

* Down: (1,1) = 6 → 6 < 9? yes.

  * cand = `dp[1][1] + 1 = 2 + 1 = 3`
  * `dp[0][1] = max(1, 3) = 3`.
* Up:   (-1,1) → out of bounds.
* Right:(0,2) = 4 → 4 < 9? yes.

  * cand = `dp[0][2] + 1 = 1 + 1 = 2`
  * `dp[0][1]` remains `3`.
* Left: (0,0) = 9 → 9 < 9? no.

So:

* `dp[0][1] = 3`.
* `maxLen = max(4, 3) = 4`.

Final `dp`:

```text
dp =
[
  [4, 3, 1],
  [3, 2, 3],
  [2, 1, 1]
]
```

Final answer: **`maxLen = 4`**.

---

## 5. Summary: DFS vs bottom-up DP

* DFS + memo (your top-down version) defines `dp(i,j)` as:

  > longest increasing path **starting** at (i,j)
  > and uses recursion.

* Bottom-up DP here defines `dp(i,j)` as:

  > longest increasing path **ending** at (i,j)
  > and uses sorting + iterative relaxation.

They’re solving the **same problem** from opposite directions:

* Top-down: start from each cell and go to bigger neighbors.
* Bottom-up: sort all cells by value and grow paths into each cell from smaller neighbors.

Both give the same final answer; bottom-up just avoids recursion by using a topological order (the sorted cell values).
*/
// class Solution {

//     static class Cell {
//         int r, c, val;
//         Cell(int r, int c, int val) {
//             this.r = r;
//             this.c = c;
//             this.val = val;
//         }
//     }

//     public int longestIncreasingPath(int[][] matrix) {
//         int m = matrix.length;
//         if (m == 0) return 0;
//         int n = matrix[0].length;
//         if (n == 0) return 0;

//         int[][] dp = new int[m][n];      // dp[i][j] = longest inc path ending at (i, j)
//         List<Cell> cells = new ArrayList<>();

//         // 1) Initialize dp and collect all cells
//         for (int i = 0; i < m; i++) {
//             for (int j = 0; j < n; j++) {
//                 dp[i][j] = 1;  // path of length 1 (just the cell itself)
//                 cells.add(new Cell(i, j, matrix[i][j]));
//             }
//         }

//         // 2) Sort cells by value in ascending order
//         cells.sort((a, b) -> Integer.compare(a.val, b.val));

//         // Directions for neighbors: down, up, right, left
//         int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

//         int maxLen = 1;

//         // 3) Process cells in sorted order
//         for (Cell cell : cells) {
//             int i = cell.r;
//             int j = cell.c;

//             // Look at all neighbors with smaller value
//             for (int[] d : dirs) {
//                 int ni = i + d[0];
//                 int nj = j + d[1];

//                 if (ni >= 0 && ni < m && nj >= 0 && nj < n
//                         && matrix[ni][nj] < matrix[i][j]) {

//                     dp[i][j] = Math.max(dp[i][j], dp[ni][nj] + 1);
//                 }
//             }

//             maxLen = Math.max(maxLen, dp[i][j]);
//         }

//         return maxLen;
//     }
// }





// Method 3: Graph + Topological Sort
/*
## 1. Turn the matrix into a graph (DAG)

You’re given an `m x n` matrix. Think of it as a graph:

* **Each cell** `(i, j)` is a **node**.
* You can move **up/down/left/right** to neighbors with a **strictly greater** value.

So if:

```text
matrix[ni][nj] > matrix[i][j]
```

we add a **directed edge**:

> `(i, j) → (ni, nj)`

That means: “there is a valid move from this cell to that larger neighbor.”

Because we always go to a **strictly larger** value, there can’t be cycles:

* You can’t keep going up in value forever and come back to the same cell.
* So this graph is a **DAG** (Directed Acyclic Graph).

In a DAG, the **longest path problem** can be solved using **topological order**.

---

## 2. Graph DP idea (in words)

We want the length of the **longest increasing path**. In graph terms:

> Longest directed path in our DAG.

One classic way to do this on a DAG is:

1. Compute the **indegree** of every node.

   * `indegree(node)` = number of edges coming *into* this node.
2. Use **Kahn’s algorithm** (BFS for topological sorting):

   * Put all nodes with `indegree == 0` into a queue.
   * Repeatedly pop nodes, reduce indegrees of their neighbors, and when a neighbor’s indegree becomes 0, push it into the queue.
3. If you count **how many BFS layers** you process, that number is exactly the length of the **longest path**.

Why?

* Each “layer” in this BFS corresponds to **one step along the path**.
* Nodes in layer 1: no incoming edges (no smaller neighbor) → path length at least 1.
* Nodes in layer 2: reachable from layer 1 by 1 edge → path length at least 2.
* Nodes in layer 3: reachable from layer 2 → path length 3.
* … and so on.

So:

> **Answer = number of BFS rounds (layers)**.

---

## 3. Concrete algorithm for this problem

We’ll define edges as **small → big**:

* Node `(i,j)` has directed edges to neighbors `(ni,nj)` where `matrix[ni][nj] > matrix[i][j]`.

### Step 1: Compute indegree of each cell

For every cell `(i, j)`:

* Look at its 4 neighbors.
* If neighbor is bigger (`matrix[ni][nj] > matrix[i][j]`), we add edge `(i,j) → (ni,nj)`.
* So we do:

```java
indegree[ni][nj]++
```

### Step 2: Initialize queue with all nodes having indegree 0

Those nodes have **no smaller neighbor**; they are like “local minima” in this graph.

### Step 3: BFS by layers

We’ll do:

```java
int length = 0;
while (!queue.isEmpty()) {
    int size = queue.size();
    length++;  // we're moving one step longer in path

    for (int s = 0; s < size; s++) {
        int[] cell = queue.poll();
        (i, j) = cell;

        // go to all neighbors (ni,nj) where matrix[ni][nj] > matrix[i][j]
        // reduce indegree and push when indegree becomes 0
    }
}
return length;
```

Because each outer `while` iteration processes all nodes that are at the **same distance** from some starting minima, `length` becomes the maximum number of steps you can take — i.e., the longest path length.


## 5. Detailed example walkthrough

Use the same example matrix:

```text
[
  [9, 9, 4],
  [6, 6, 8],
  [2, 1, 1]
]
```

Index by `(row, col)`:

```text
(0,0)=9  (0,1)=9  (0,2)=4
(1,0)=6  (1,1)=6  (1,2)=8
(2,0)=2  (2,1)=1  (2,2)=1
```

### 5.1 Compute indegree for every cell

We add an edge `(i,j) -> (ni,nj)` if `matrix[ni][nj] > matrix[i][j]`, and increment `indegree[ni][nj]`.

Let’s go cell by cell.

---

#### Cell (0,0) = 9

Neighbors:

* Down: (1,0) = 6 → 6 > 9? **no**
* Up:   (-1,0) → out
* Right:(0,1) = 9 → 9 > 9? **no** (must be strictly greater)
* Left: (0,-1) → out

No edges from (0,0).

---

#### Cell (0,1) = 9

Neighbors:

* Down: (1,1) = 6 → 6 > 9? no
* Up:   (-1,1) → out
* Right:(0,2) = 4 → 4 > 9? no
* Left: (0,0) = 9 → 9 > 9? no

No edges from (0,1).

---

#### Cell (0,2) = 4

Neighbors:

* Down: (1,2) = 8 → 8 > 4? **yes**

  * Edge: (0,2) → (1,2), so `indegree[1][2]++`.
* Up:   (-1,2) → out
* Right:(0,3) → out
* Left: (0,1) = 9 → 9 > 4? **yes**

  * Edge: (0,2) → (0,1), so `indegree[0][1]++`.

So far:

* indegree(1,2) = 1
* indegree(0,1) = 1

---

#### Cell (1,0) = 6

Neighbors:

* Down: (2,0) = 2 → 2 > 6? no
* Up:   (0,0) = 9 → 9 > 6? **yes**

  * Edge: (1,0) → (0,0), so `indegree[0][0]++`.
* Right:(1,1) = 6 → 6 > 6? no
* Left: (1,-1) → out

Now:

* indegree(0,0) = 1

---

#### Cell (1,1) = 6

Neighbors:

* Down: (2,1) = 1 → 1 > 6? no
* Up:   (0,1) = 9 → 9 > 6? **yes**

  * Edge: (1,1) → (0,1), so `indegree[0][1]++` again.
* Right:(1,2) = 8 → 8 > 6? **yes**

  * Edge: (1,1) → (1,2), so `indegree[1][2]++`.
* Left: (1,0) = 6 → 6 > 6? no

Update:

* indegree(0,1) = 2 (from (0,2) and (1,1))
* indegree(1,2) = 2 (from (0,2) and (1,1))

---

#### Cell (1,2) = 8

Neighbors:

* Down: (2,2) = 1 → 1 > 8? no
* Up:   (0,2) = 4 → 4 > 8? no
* Right:(1,3) → out
* Left: (1,1) = 6 → 6 > 8? no

No edges from (1,2).

---

#### Cell (2,0) = 2

Neighbors:

* Down: (3,0) → out
* Up:   (1,0) = 6 → 6 > 2? **yes**

  * Edge: (2,0) → (1,0), so `indegree[1][0]++`.
* Right:(2,1) = 1 → 1 > 2? no
* Left: (2,-1) → out

Now:

* indegree(1,0) = 1

---

#### Cell (2,1) = 1

Neighbors:

* Down: (3,1) → out
* Up:   (1,1) = 6 → 6 > 1? **yes**

  * Edge: (2,1) → (1,1), so `indegree[1][1]++`.
* Right:(2,2) = 1 → 1 > 1? no
* Left: (2,0) = 2 → 2 > 1? **yes**

  * Edge: (2,1) → (2,0), so `indegree[2][0]++`.

So:

* indegree(1,1) = 1
* indegree(2,0) = 1

---

#### Cell (2,2) = 1

Neighbors:

* Down: (3,2) → out
* Up:   (1,2) = 8 → 8 > 1? **yes**

  * Edge: (2,2) → (1,2), so `indegree[1][2]++`.
* Right:(2,3) → out
* Left: (2,1) = 1 → 1 > 1? no

Update:

* indegree(1,2) = 3 now (from (0,2), (1,1), and (2,2))

---

### 5.2 Final indegree table

Let’s list indegree for each cell:

* (0,0): 1
* (0,1): 2
* (0,2): 0
* (1,0): 1
* (1,1): 1
* (1,2): 3
* (2,0): 1
* (2,1): 0
* (2,2): 0

As a grid:

```text
indegree =
[
  [1, 2, 0],
  [1, 1, 3],
  [1, 0, 0]
]
```

Cells with indegree 0: `(0,2)`, `(2,1)`, `(2,2)`.

These have **no smaller neighbor** that can go into them, so they are **starting points** of increasing paths.

---

### 5.3 BFS layer 1

Initialize queue `q` with all indegree-zero nodes:

```text
q = [(0,2), (2,1), (2,2)]
longest = 0
```

Start BFS:

* `longest++` → `longest = 1` (we’re at path length 1 now).
* Process all nodes in this layer (`size = 3`):

#### Pop (0,2), value 4

Neighbors with bigger value:

* Down: (1,2) = 8 > 4 → indegree[1][2]--
* Left: (0,1) = 9 > 4 → indegree[0][1]--

Update them:

* indegree(1,2): 3 → 2
* indegree(0,1): 2 → 1

No one hits 0 yet, so nothing new enqueued from (0,2).

---

#### Pop (2,1), value 1

Neighbors with bigger value:

* Up:   (1,1) = 6 → indegree[1][1]--
* Left: (2,0) = 2 → indegree[2][0]--

Update:

* indegree(1,1): 1 → 0
* indegree(2,0): 1 → 0

Both reached 0, so we enqueue:

* `(1,1)`, `(2,0)`

---

#### Pop (2,2), value 1

Neighbors with bigger value:

* Up: (1,2) = 8 → indegree[1][2]--

indegree(1,2): 2 → 1

Still not 0.

At end of layer 1:

* New indegrees:

```text
indegree =
[
  [1, 1, 0],
  [1, 0, 1],
  [0, 0, 0]
]
```

* Queue now:

```text
q = [(1,1), (2,0)]
```

---

### 5.4 BFS layer 2

`longest++` → `longest = 2`.

Processing 2 nodes in this layer:

#### Pop (1,1), value 6

Neighbors with bigger value:

* Up:   (0,1) = 9 → indegree[0][1]--
* Right:(1,2) = 8 → indegree[1][2]--

Update:

* indegree(0,1): 1 → 0
* indegree(1,2): 1 → 0

Both now 0 → enqueue `(0,1)` and `(1,2)`.

---

#### Pop (2,0), value 2

Neighbors with bigger value:

* Up: (1,0) = 6 → indegree[1][0]--

Update:

* indegree(1,0): 1 → 0 → enqueue `(1,0)`.

End of layer 2:

* indegree becomes:

```text
indegree =
[
  [1, 0, 0],
  [0, 0, 0],
  [0, 0, 0]
]
```

* Queue now:

```text
q = [(0,1), (1,2), (1,0)]
```

---

### 5.5 BFS layer 3

`longest++` → `longest = 3`.

Process 3 nodes (0,1), (1,2), (1,0):

#### Pop (0,1), value 9

Neighbors with bigger value? None (all ≤ 9 around it). So nothing happens.

#### Pop (1,2), value 8

Neighbors with bigger value? None (all neighbors are 1,4,6).

#### Pop (1,0), value 6

Neighbors with bigger value:

* Up: (0,0) = 9 → indegree[0][0]--

Update:

* indegree(0,0): 1 → 0 → enqueue `(0,0)`.

End of layer 3:

* indegree:

```text
indegree =
[
  [0, 0, 0],
  [0, 0, 0],
  [0, 0, 0]
]
```

* Queue:

```text
q = [(0,0)]
```

---

### 5.6 BFS layer 4

`longest++` → `longest = 4`.

Process only `(0,0)`:

#### Pop (0,0), value 9

Neighbors with bigger value? None.

Queue becomes empty.

---

### 5.7 Final result

BFS is done. We processed **4 layers**, so:

```text
longest = 4
```

That is exactly the length of the **longest increasing path**.

One such path (as we know) is:

```text
(2,1) = 1
(2,0) = 2
(1,0) = 6
(0,0) = 9
```

So:

```text
1 → 2 → 6 → 9  (length 4)
```

---

## 6. How this relates to the other versions

* **DFS + memo (top-down)**:

  * Node = cell, edges from smaller to larger neighbors.
  * `dp(i,j)` = longest path starting at `(i,j)` found by DFS.
  * Uses recursion + memo to avoid recomputation.

* **Bottom-up DP (sorted by value)**:

  * Same DAG, but:
  * Sort cells by value ascending and fill `dp` from small to large.
  * `dp(i,j)` = longest path ending at `(i,j)`.

* **Graph / BFS version (this one)**:

  * Still the same DAG.
  * Instead of recursion or sorting explicitly, we:

    * Build indegrees for edges (small → large).
    * Use Kahn’s BFS to peel the DAG layer by layer.
    * Number of layers processed = longest path length.

All three are just different ways of exploiting the **same DAG structure** of “moving only to bigger neighbors”.
*/

// class Solution {
//     public int longestIncreasingPath(int[][] matrix) {
//         int m = matrix.length;
//         if (m == 0) return 0;
//         int n = matrix[0].length;
//         if (n == 0) return 0;

//         int[][] indegree = new int[m][n];
//         int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

//         // 1) Build indegree for each cell
//         // Edge: (i, j) -> (ni, nj) if matrix[ni][nj] > matrix[i][j]
//         // So indegree[ni][nj]++ when neighbor is bigger.
//         for (int i = 0; i < m; i++) {
//             for (int j = 0; j < n; j++) {
//                 for (int[] d : dirs) {
//                     int ni = i + d[0];
//                     int nj = j + d[1];
//                     if (ni >= 0 && ni < m && nj >= 0 && nj < n
//                             && matrix[ni][nj] > matrix[i][j]) {
//                         indegree[ni][nj]++;
//                     }
//                 }
//             }
//         }

//         // 2) Initialize queue with all nodes having indegree 0
//         java.util.Queue<int[]> q = new java.util.ArrayDeque<>();
//         for (int i = 0; i < m; i++) {
//             for (int j = 0; j < n; j++) {
//                 if (indegree[i][j] == 0) {
//                     q.offer(new int[]{i, j});
//                 }
//             }
//         }

//         // 3) BFS by layers
//         int longest = 0;
//         while (!q.isEmpty()) {
//             int size = q.size();
//             longest++;  // we are moving to the next "length" of path

//             for (int s = 0; s < size; s++) {
//                 int[] cell = q.poll();
//                 int i = cell[0], j = cell[1];

//                 // For each neighbor that is bigger, remove this edge
//                 for (int[] d : dirs) {
//                     int ni = i + d[0];
//                     int nj = j + d[1];
//                     if (ni >= 0 && ni < m && nj >= 0 && nj < n
//                             && matrix[ni][nj] > matrix[i][j]) {
//                         indegree[ni][nj]--;
//                         if (indegree[ni][nj] == 0) {
//                             q.offer(new int[]{ni, nj});
//                         }
//                     }
//                 }
//             }
//         }

//         return longest;
//     }
// }
