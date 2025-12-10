// Method 1: Top-Down 2D DP (DP over interval)
/*
### Problem 1: The neighbors of a balloon change as you burst others

In Burst Balloons, when you burst balloon `i`, the “neighbors” are the **closest remaining balloons** on the left and right *at that time*, not the original `nums[i-1]` and `nums[i+1]`.

Example: `nums = [3,1,5]`.

* If you burst index 1 (`1`) first:

  * neighbors are 3 and 5 → coins = `3 * 1 * 5 = 15`.
  * now the array is effectively `[3,5]` (balloon 1 is gone).
* If you instead burst index 1 **second**, after bursting some other balloon, its neighbors might be different.

Your code always does:

```java
before = (idx - 1 < 0) ? 1 : nums[idx - 1];
after  = (idx + 1 >= nums.length) ? 1 : nums[idx + 1];
```

This assumes the neighbors are just the original, static elements at `idx-1` and `idx+1`.
But after you burst some balloons, those neighbors might themselves be gone. You’re ignoring that.

So the computed `pick` value is simply **wrong** for almost all sequences except trivial arrays.

---

### Problem 2: The state `idx` doesn’t describe “which balloons are left”

Your DP state is:

```java
dp(idx) = best answer considering balloons from idx to end, deciding to pick or skip idx
```

This implicitly assumes:

* You’re processing balloons **left to right**, and
* The only question at position `idx` is “burst this now or later (skip)?”

But in the actual problem, you can burst balloons in **any order**. The optimal order is not necessarily increasing in index.

Example: `nums = [3,1,5,8]` (the classic example):

Optimal order is: burst `1`, then `5`, then `3`, then `8`. That order is not “just iterate idx from 0 to n-1 and pick/skip”.

Your `dp(idx)` can only express sequences where you consider each balloon once in order and never go back. That excludes many valid and optimal bursting orders.

In reality, the **state** must capture which segment/interval of balloons is still alive. That’s why the standard solution uses **interval DP** with two indices `(left, right)`.

---

### Problem 3: “Skip” vs “pick” logic doesn’t match the problem

You do:

```java
pick = before * nums[idx] * after + dp(nums, memo, idx + 1);
skip = dp(nums, memo, idx + 1);
memo[idx] = Math.max(pick, skip);
```

Interpreting this:

* If you burst `idx`, you get `before * nums[idx] * after`, then you move to `idx+1`.
* If you skip `idx`, you just move to `idx+1`.

Two big issues:

1. **Bursting idx changes the problem for all other balloons**, not just `idx+1`. It affects neighbors on both sides.
2. After bursting `idx`, you shouldn't just “start DP from idx+1”; you have two subproblems:

   * The balloons to the **left** of `idx`,
   * The balloons to the **right** of `idx`,

   which become independent. Your recurrence never splits into these two subproblems.

For Burst Balloons, the key property is:

> If you decide which balloon is the **last** to burst in some interval, the subproblems on the left and right become independent.

Your recurrence never models “last balloon in an interval”. It’s trying to model “current balloon now or later” in a linear scan, which doesn’t respect how coins are earned.

---

### Problem 4: Memoization dimension is too small

You use:

```java
Integer[] memo = new Integer[n];
```

But the true subproblem is defined by an **interval**:

* “What’s the max coins we can get from bursting all balloons between `left` and `right`?”

That’s a pair `(left, right)`, so you need a 2D memo: `memo[left][right]`.

A single index `idx` just isn’t enough information to know which balloons to the left/right are still unburst.

---

## 2. Correct top-down DP idea (interval DP)

Here’s the standard “aha” for this problem:

1. First, **pad** the array with 1s on both ends:

   ```text
   nums:  [3, 1, 5, 8]
   val:   [1, 3, 1, 5, 8, 1]
           0  1  2  3  4  5
   ```

   So if you burst balloon at index `k`, the neighbors are `val[left]` and `val[right]`.

2. Define:

   > `dp(left, right)` = maximum coins we can get by bursting **all balloons strictly between** `left` and `right` (i.e., in the open interval `(left, right)`).

   So:

   * We never burst `left` or `right`; they are just boundaries.
   * Example:

     * `dp(0,5)` means “max coins bursting balloons at indices 1..4 in `val`” → the entire original array.

3. Key insight:

   If we decide that **balloon `k`** (where `left < k < right`) is the **last** balloon we burst in the interval `(left, right)`, then:

   * By the time we burst `k`, everything between `left` and `right` except `k` is already gone.
   * So its neighbors at that moment are exactly `left` and `right`.
   * It yields `val[left] * val[k] * val[right]` coins at that final burst.
   * The balloons in `(left, k)` and `(k, right)` have been burst earlier, and their contributions are:

     ```text
     dp(left, k) + dp(k, right)
     ```

   So if `k` is last, total coins for interval `(left, right)` are:

   ```text
   val[left] * val[k] * val[right] + dp(left, k) + dp(k, right)
   ```

4. Since we don’t know which `k` is best, we try all:

   ```text
   dp(left, right) = max over k in (left, right):
                       val[left] * val[k] * val[right]
                     + dp(left, k)
                     + dp(k, right)
   ```

5. Base case:

   If `left + 1 == right`, there are no balloons between them:

   ```text
   dp(left, right) = 0
   ```

   (No balloons to burst.)

That’s the whole recurrence.

# About the code:
(In the code, if you worry about `0` as a valid memo value, you can initialize with `-1` and check for `-1` as “uncomputed”.)


## 4. Example walkthrough: `[3,1,5,8]`

Input:

```text
nums = [3,1,5,8]
val  = [1,3,1,5,8,1]  // padded
index   0 1 2 3 4 5
```

We want `burst(0, 5)`.

### Base: small intervals

For any `left, right` with `left + 1 >= right`, `burst(left, right) = 0`.

So:

* `burst(0,1) = 0`
* `burst(1,2) = 0`
* `burst(2,3) = 0`
* `burst(3,4) = 0`
* `burst(4,5) = 0`
* etc.

### Step 1: intervals of length 2 inside (one balloon in between)

Consider `burst(1,3)` (balloons between indices 1 and 3):

* Interval `(1,3)` has only `k = 2` to burst last.
* Coins if last = `val[1] * val[2] * val[3] + burst(1,2) + burst(2,3)`
  = `3 * 1 * 5 + 0 + 0`
  = `15`.

So:

```text
burst(1,3) = 15
```

Similarly:

* `burst(0,2)`: only `k = 1`:

  ```text
  coins = 1 * 3 * 1 + 0 + 0 = 3
  burst(0,2) = 3
  ```

* `burst(2,4)`: only `k = 3`:

  ```text
  coins = 1 * 5 * 8 + 0 + 0 = 40
  burst(2,4) = 40
  ```

* `burst(3,5)`: only `k = 4`:

  ```text
  coins = 5 * 8 * 1 + 0 + 0 = 40
  burst(3,5) = 40
  ```

These are intervals with exactly one balloon inside.

---

### Step 2: intervals of length 3 (two balloons inside)

Example: `burst(1,4)` (balloons 2 and 3 inside: values 1 and 5).

Candidates for `k`:

* `k = 2`, `k = 3`.

#### k = 2 (burst balloon with value 1 last in (1,4))

At the moment we burst k=2 last:

* Its neighbors are indices `1` and `4` → values `3` and `8`.
* Coins from bursting `k` last: `3 * 1 * 8 = 24`.
* Left side interval: `(1,2)` → `burst(1,2) = 0`.
* Right side interval: `(2,4)` → `burst(2,4) = 40`.

Total:

```text
coins_k2 = 24 + 0 + 40 = 64
```

#### k = 3 (burst balloon with value 5 last in (1,4))

At the moment we burst k=3 last:

* Its neighbors are indices `1` and `4` → values `3` and `8`.
* Burst `k` last: `3 * 5 * 8 = 120`.
* Left: `(1,3)` → `burst(1,3) = 15`.
* Right: `(3,4)` → `burst(3,4) = 0`.

Total:

```text
coins_k3 = 120 + 15 + 0 = 135
```

So:

```text
burst(1,4) = max(64, 135) = 135
```

Similarly we can compute `burst(0,3)`, `burst(2,5)` using already known smaller intervals.

---

### Step 3: final interval `burst(0,5)` (full array)

Now we consider all `k` in `(0,5)`: k = 1,2,3,4.

#### k = 1 (last balloon is value 3 at index 1)

* Neighbors at final burst: `0` and `5` → values 1 and 1.
* Coins: `1 * 3 * 1 = 3`.
* Left interval: `(0,1)` → `burst(0,1) = 0`.
* Right interval: `(1,5)` → computed from previous steps (we’d compute that similarly; result is 167).

Total:

```text
coins_k1 = 3 + 0 + burst(1,5)
```

#### k = 2 (last balloon is 1 at index 2)

* Neighbors final: `0` and `5` → 1 and 1.
* Coins: `1 * 1 * 1 = 1`.
* Left: `(0,2)` → `burst(0,2) = 3`.
* Right: `(2,5)` → some value (we compute via the same DP; result is 167 - 3? not needed to memorize now).

and similarly for k = 3, 4.

When you compute all of them correctly, the maximum is:

```text
burst(0,5) = 167
```

which is the known correct answer for `[3,1,5,8]`.

The key pattern:

* Every time you choose a `k` as the “last balloon in the interval”, you **split** into left and right independent subproblems and add `val[left] * val[k] * val[right]`.

Your linear pick/skip `dp(idx)` is missing precisely that structure.

---

## TL;DR

Your solution is incorrect because:

1. Neighbors are dynamic, but you treat them as static `nums[idx-1]` and `nums[idx+1]`.
2. The state `idx` doesn’t capture which balloons remain; the correct state is an interval `(left, right)`.
3. Bursting one balloon splits the array into two independent subproblems (left & right), but your recurrence never splits.
4. A 1D `memo[idx]` is insufficient; you need a 2D interval DP `memo[left][right]`.

The correct top-down solution is an **interval DP** that chooses the **last balloon to burst in each interval**, with recurrence:

```text
dp(left, right) = max over k in (left, right):
    val[left] * val[k] * val[right]
  + dp(left, k)
  + dp(k, right)
```

and base `dp(left, right) = 0` when `left + 1 >= right`.



## Example walkthrough:

## 1. Core idea recap (why we use intervals)

We **pad** the array with `1` at both ends:

```text
nums = [3, 1, 5, 8]

val  = [1, 3, 1, 5, 8, 1]
index   0  1  2  3  4  5
```

Now:

> `dp(left, right)` = **maximum coins** we can get by bursting **all balloons strictly between** indices `left` and `right`
> (i.e., balloons in `(left, right)`).

Important:

* We never remove `val[left]` or `val[right]`; they’re just boundaries.
* If we pick some `k` with `left < k < right` as the **last balloon** to burst in that open interval, then at that moment:

  * All balloons between `left` and `right` except `k` have already been burst.

  * So the neighbors of `k` are exactly `left` and `right`.

  * Coins from bursting `k` last:

    ```text
    val[left] * val[k] * val[right]
    ```

  * We also must have already burst everything in `(left, k)` and `(k, right)`, contributing:

    ```text
    dp(left, k) + dp(k, right)
    ```

So for each `k`:

```text
coins_if_k_is_last = val[left] * val[k] * val[right]
                   + dp(left, k)
                   + dp(k, right)
```

Take the maximum over all `k`:

```text
dp(left, right) = max_{left < k < right} (
    val[left] * val[k] * val[right] + dp(left, k) + dp(k, right)
)
```

Base case:

```text
if left + 1 >= right:
    dp(left, right) = 0   // no balloons between, nothing to burst
```

We want:

```text
answer = dp(0, n+1) = dp(0, 5)   // burst all balloons between the two 1’s
```

---

## 2. The DP table we’ll build

Indices: `0..5` for `val = [1,3,1,5,8,1]`.

We’ll fill a 2D table `dp[left][right]`.
Here is the **final** table (I’ll show how each key entry is computed):

```text
dp[left][right]:

        0    1    2    3    4    5
      --------------------------------
0   |   0    0    3    30   159  167
1   |   0    0    0    15   135  159
2   |   0    0    0    0    40   48
3   |   0    0    0    0    0    40
4   |   0    0    0    0    0    0
5   |   0    0    0    0    0    0
```

* `dp[0][5] = 167` is the final answer.
* I’ll explain how we reach these numbers.

We’ll think in terms of **interval lengths**:

* Length 2: intervals like `(0,2)`,`(1,3)`,`(2,4)`,`(3,5)` (one balloon inside).
* Length 3: intervals like `(0,3)`,`(1,4)`,`(2,5)` (two balloons inside).
* Length 4: `(0,4)`,`(1,5)` (three balloons inside).
* Length 5: `(0,5)` (all four balloons inside).

(Remember: an interval `(left, right)` corresponds to indices `left+1..right-1` in `val`.)

---

## 3. Step-by-step: intervals with 1 balloon inside (length = 2)

These have the form `dp(left, right)` where `right = left + 2`:

* `(0,2)` → inside is just index 1 (value 3)
* `(1,3)` → inside is 2 (value 1)
* `(2,4)` → inside is 3 (value 5)
* `(3,5)` → inside is 4 (value 8)

In these cases, there is only **one** choice for `k` (the single inside balloon), and both sub-intervals `(left, k)` and `(k, right)` contain no balloons, so `dp = 0` there.

### 3.1. `dp(0,2)` (inside: index 1 → value 3)

Neighbors when 3 is last: `val[0] = 1`, `val[2] = 1`:

```text
dp(0,2) = 1 * 3 * 1 + dp(0,1) + dp(1,2)
        = 3        + 0       + 0
        = 3
```

So `dp[0][2] = 3`.

### 3.2. `dp(1,3)` (inside: index 2 → value 1)

Neighbors: `val[1] = 3`, `val[3] = 5`:

```text
dp(1,3) = 3 * 1 * 5 + dp(1,2) + dp(2,3)
        = 15        + 0       + 0
        = 15
```

So `dp[1][3] = 15`.

### 3.3. `dp(2,4)` (inside: index 3 → value 5)

Neighbors: `val[2] = 1`, `val[4] = 8`:

```text
dp(2,4) = 1 * 5 * 8 + dp(2,3) + dp(3,4)
        = 40        + 0       + 0
        = 40
```

So `dp[2][4] = 40`.

### 3.4. `dp(3,5)` (inside: index 4 → value 8)

Neighbors: `val[3] = 5`, `val[5] = 1`:

```text
dp(3,5) = 5 * 8 * 1 + dp(3,4) + dp(4,5)
        = 40        + 0       + 0
        = 40
```

So `dp[3][5] = 40`.

So far (non-zero entries):

```text
dp[0][2] = 3
dp[1][3] = 15
dp[2][4] = 40
dp[3][5] = 40
```

---

## 4. Intervals with 2 balloons inside (length = 3)

Now we consider `dp(left, right)` with `right = left + 3`:

* `dp(0,3)` → inside: indices 1 & 2 (values 3,1)
* `dp(1,4)` → inside: 2 & 3 (1,5)
* `dp(2,5)` → inside: 3 & 4 (5,8)

For each, we try **both** choices for `k` in that interval.

### 4.1. `dp(0,3)` → inside balloons 1 (3) and 2 (1)

#### Option 1: `k = 1` (burst 3 last in interval (0,3))

Neighbors when we finally burst `k=1`: `val[0]=1`, `val[3]=5`.

Coins from this last burst:

```text
1 * 3 * 5 = 15
```

Subproblems:

* Left side: `(0,1)` → no balloons → `dp(0,1) = 0`
* Right side: `(1,3)` → we’ve already computed `dp(1,3) = 15`

Total:

```text
coins_k1 = 15 + 0 + 15 = 30
```

#### Option 2: `k = 2` (burst 1 last in (0,3))

Neighbors: `val[0]=1`, `val[3]=5`.

Last burst coins:

```text
1 * 1 * 5 = 5
```

Subproblems:

* Left `(0,2)` → `dp(0,2) = 3`
* Right `(2,3)` → `dp(2,3) = 0`

Total:

```text
coins_k2 = 5 + 3 + 0 = 8
```

Take max:

```text
dp(0,3) = max(30, 8) = 30
```

---

### 4.2. `dp(1,4)` → inside balloons 2 (1) and 3 (5)

We already did a version of this before, but let’s re-do it cleanly.

#### Option 1: `k = 2` (burst 1 last in (1,4))

Neighbors: `val[1] = 3`, `val[4] = 8`.

Last-burst coins:

```text
3 * 1 * 8 = 24
```

Subproblems:

* Left `(1,2)` → `dp(1,2) = 0`
* Right `(2,4)` → `dp(2,4) = 40` (from step 3)

Total:

```text
coins_k2 = 24 + 0 + 40 = 64
```

#### Option 2: `k = 3` (burst 5 last in (1,4))

Neighbors: still `val[1] = 3` and `val[4] = 8`.

Last-burst coins:

```text
3 * 5 * 8 = 120
```

Subproblems:

* Left `(1,3)` → `dp(1,3) = 15`
* Right `(3,4)` → `dp(3,4) = 0`

Total:

```text
coins_k3 = 120 + 15 + 0 = 135
```

Max:

```text
dp(1,4) = max(64, 135) = 135
```

---

### 4.3. `dp(2,5)` → inside balloons 3 (5) and 4 (8)

#### Option 1: `k = 3` (burst 5 last in (2,5))

Neighbors: `val[2] = 1`, `val[5] = 1`.

Last-burst coins:

```text
1 * 5 * 1 = 5
```

Subproblems:

* Left `(2,3)` → 0
* Right `(3,5)` → 40

Total:

```text
coins_k3 = 5 + 0 + 40 = 45
```

#### Option 2: `k = 4` (burst 8 last in (2,5))

Neighbors: `val[2] = 1`, `val[5] = 1`.

Last-burst coins:

```text
1 * 8 * 1 = 8
```

Subproblems:

* Left `(2,4)` → 40
* Right `(4,5)` → 0

Total:

```text
coins_k4 = 8 + 40 + 0 = 48
```

So:

```text
dp(2,5) = max(45, 48) = 48
```

Now our table has:

```text
dp[0][3] = 30
dp[1][4] = 135
dp[2][5] = 48
```

---

## 5. Intervals with 3 balloons inside (length = 4)

Now:

* `dp(0,4)` → inside 1,2,3
* `dp(1,5)` → inside 2,3,4

### 5.1. `dp(0,4)` → inside k ∈ {1,2,3}

#### k = 1 (last is balloon 3 at index 1)

Neighbors: `val[0] = 1`, `val[4] = 8`.

Last burst coins:

```text
1 * 3 * 8 = 24
```

Subproblems:

* Left `(0,1)` → 0
* Right `(1,4)` → 135

Total:

```text
coins_k1 = 24 + 0 + 135 = 159
```

#### k = 2 (last is balloon 1 at index 2)

Neighbors: `1` and `8`.

Last burst coins:

```text
1 * 1 * 8 = 8
```

Subproblems:

* `(0,2)` → 3
* `(2,4)` → 40

Total:

```text
coins_k2 = 8 + 3 + 40 = 51
```

#### k = 3 (last is balloon 5 at index 3)

Neighbors: `1` and `8`.

Last burst coins:

```text
1 * 5 * 8 = 40
```

Subproblems:

* `(0,3)` → 30
* `(3,4)` → 0

Total:

```text
coins_k3 = 40 + 30 + 0 = 70
```

Take max:

```text
dp(0,4) = max(159, 51, 70) = 159
```

---

### 5.2. `dp(1,5)` → inside k ∈ {2,3,4}

#### k = 2 (last is 1 at index 2)

Neighbors: `val[1] = 3`, `val[5] = 1`.

Last burst coins:

```text
3 * 1 * 1 = 3
```

Subproblems:

* `(1,2)` → 0
* `(2,5)` → 48

Total:

```text
coins_k2 = 3 + 0 + 48 = 51
```

#### k = 3 (last is 5 at index 3)

Neighbors: `3` and `1`.

Last burst coins:

```text
3 * 5 * 1 = 15
```

Subproblems:

* `(1,3)` → 15
* `(3,5)` → 40

Total:

```text
coins_k3 = 15 + 15 + 40 = 70
```

#### k = 4 (last is 8 at index 4)

Neighbors: `3` and `1`.

Last burst coins:

```text
3 * 8 * 1 = 24
```

Subproblems:

* `(1,4)` → 135
* `(4,5)` → 0

Total:

```text
coins_k4 = 24 + 135 + 0 = 159
```

Max:

```text
dp(1,5) = max(51, 70, 159) = 159
```

---

## 6. Final interval: `dp(0,5)` (whole array)

Now we use all balloons 1–4 as candidates for the **last burst** in the full range (0,5).

k ∈ {1,2,3,4}:

### 6.1. k = 1 (last is 3 at index 1)

Neighbors at final moment: `val[0]=1`, `val[5]=1`.

Last burst coins:

```text
1 * 3 * 1 = 3
```

Subproblems:

* `(0,1)` → 0
* `(1,5)` → 159

Total:

```text
coins_k1 = 3 + 0 + 159 = 162
```

### 6.2. k = 2 (last is 1 at index 2)

Neighbors: 1 and 1.

Last burst coins:

```text
1 * 1 * 1 = 1
```

Subproblems:

* `(0,2)` → 3
* `(2,5)` → 48

Total:

```text
coins_k2 = 1 + 3 + 48 = 52
```

### 6.3. k = 3 (last is 5 at index 3)

Neighbors: 1 and 1.

Last burst coins:

```text
1 * 5 * 1 = 5
```

Subproblems:

* `(0,3)` → 30
* `(3,5)` → 40

Total:

```text
coins_k3 = 5 + 30 + 40 = 75
```

### 6.4. k = 4 (last is 8 at index 4)

Neighbors: `val[0]=1`, `val[5]=1`.

Last burst coins:

```text
1 * 8 * 1 = 8
```

Subproblems:

* `(0,4)` → 159
* `(4,5)` → 0

Total:

```text
coins_k4 = 8 + 159 + 0 = 167
```

Take maximum:

```text
dp(0,5) = max(162, 52, 75, 167) = 167
```

So final answer: **167** coins.

---

## 7. How this corresponds to an actual bursting order

DP is choosing the **last balloon** per interval. If you read the “last choices” from **outside in**, you can reconstruct an optimal **bursting order** backwards.

From what we computed:

* For `(0,5)`, the best last balloon is **k = 4** (value `8`).
* For `(0,4)`, best last is `k = 1` (value `3`).
* For `(1,4)`, best last is `k = 3` (value `5`).
* For `(1,3)`, only option (and thus last) is `k = 2` (value `1`).

So the **last bursts** in each nested interval are:

1. In `(1,3)`: last is `1` (index 2)
2. In `(1,4)`: last is `5` (index 3)
3. In `(0,4)`: last is `3` (index 1)
4. In `(0,5)`: last is `8` (index 4)

If you reverse that order, you get the actual bursting sequence:

```text
Burst order: 1, 5, 3, 8   (values)
            (idx2, idx3, idx1, idx4 in original nums)
```

Simulate:

* Start: [3,1,5,8]
* Burst 1 (neighbors 3 and 5): 3*1*5 = 15 → [3,5,8]
* Burst 5 (neighbors 3 and 8): 3*5*8 = 120 → [3,8]
* Burst 3 (neighbors 1 and 8): 1*3*8 = 24 → [8]
* Burst 8 (neighbors 1 and 1): 1*8*1 = 8

Total:

```text
15 + 120 + 24 + 8 = 167
```

Exactly what `dp(0,5)` found.

---

So the interval top-down DP you’re using:

* Correctly models the **dynamic neighbors** (via `val[left]*val[k]*val[right]`).
* Encodes “which balloons are still there” as the interval `(left, right)`.
* Splits the problem into independent left/right subproblems around the last burst `k`.
* Produces the optimal answer 167 for `[3,1,5,8]`, and the bursting order you’d expect from editorial solutions.

*/
class Solution {
    public int maxCoins(int[] nums) {
        int n = nums.length;
        // Build padded array
        int[] val = new int[n + 2];
        val[0] = 1;
        val[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            val[i + 1] = nums[i];
        }

        int[][] memo = new int[n + 2][n + 2];
        // memo[left][right] = 0 means uncomputed, also valid result can be 0,
        // but we only compute when left+1 < right, so it's fine with a small tweak
        // or we can use -1 as sentinel.

        return burst(val, memo, 0, n + 1);
    }

    // burst all balloons in (left, right), val[left] and val[right] remain
    private int burst(int[] val, int[][] memo, int left, int right) {
        // no balloon to burst
        if (left + 1 >= right) {
            return 0;
        }

        if (memo[left][right] != 0) {
            return memo[left][right];
        }

        int best = 0;
        // try every k as the last balloon to burst in (left, right)
        for (int k = left + 1; k < right; k++) {
            int coins = val[left] * val[k] * val[right]
                      + burst(val, memo, left, k)
                      + burst(val, memo, k, right);
            if (coins > best) {
                best = coins;
            }
        }

        memo[left][right] = best;
        return best;
    }
}





// Method 2: Bottom-Up 2D DP
/*
## 1. Same idea as top-down: intervals and “last balloon”

We keep exactly the *same DP definition* as the top-down interval version:

1. **Pad** the array with 1s at both ends:

   ```text
   nums = [3, 1, 5, 8]
   val  = [1, 3, 1, 5, 8, 1]
           0  1  2  3  4  5
   ```

2. Define:

   > `dp[left][right]` = maximum coins from bursting **all balloons strictly between** `left` and `right` (i.e., indices `(left+1)..(right-1)`).

   * We **never** burst `left` or `right`; they’re just boundary 1’s / remaining balloons.

3. If we choose some `k` with `left < k < right` as the **last balloon** to burst in `(left, right)`:

   * At that moment its neighbors are exactly `left` and `right`.
   * Coins from that final burst: `val[left] * val[k] * val[right]`.
   * Before that, all balloons in `(left, k)` and `(k, right)` have been burst optimally:

     ```text
     dp[left][k] + dp[k][right]
     ```

   So:

   ```text
   dp[left][right] = max over k in (left, right):
       val[left] * val[k] * val[right] + dp[left][k] + dp[k][right]
   ```

4. Base case:

   If there are **no balloons** strictly between `left` and `right`, then:

   ```text
   left + 1 >= right  ⇒ dp[left][right] = 0
   ```

5. Answer we want:

   ```text
   // Burst all original balloons (indices 1..n in val)
   result = dp[0][n+1]
   ```

---

## 2. How to do this bottom-up

Top-down uses recursion + memo; bottom-up just fills `dp` in the **correct order**, so that when we compute `dp[left][right]`, all needed subintervals are already known.

Notice the recurrence:

```text
dp[left][right] depends on:
  - dp[left][k]   where left < k < right
  - dp[k][right]
```

Those are **smaller intervals inside (left,right)**. So we should:

* Fill `dp` by **increasing interval length**.

### Interval length

Define `len = right - left`.

* We need intervals with **at least 2 distance** to have something inside:

  * `len = 1` → no room inside `(left,right)` → 0
  * `len = 2` → **1 balloon inside** (right = left+2)
  * `len = 3` → **2 balloons inside**
  * ...
  * For `val` length `n+2`, the full interval `(0, n+1)` has `len = (n+1) - 0 = n+1`.

So we do:

```text
for len from 2 to n+1:
    for left from 0 to n+1-len:
        right = left + len
        compute dp[left][right]
```

Inside that, we try all `k` between `left` and `right`:

```text
dp[left][right] = 0
for k from left+1 to right-1:
    dp[left][right] = max(
        dp[left][right],
        val[left]*val[k]*val[right] + dp[left][k] + dp[k][right]
    )
```

* Time: `O(n^3)` (three nested loops: len, left, k).
* Space: `O(n^2)`.

---

## 4. Detailed example walkthrough: `nums = [3,1,5,8]`

We did this logic top-down earlier; now we’ll see how **exactly the same numbers** appear when filling the `dp` table bottom-up.

### 4.1. Setup

```text
nums = [3, 1, 5, 8]
n    = 4

val  = [1, 3, 1, 5, 8, 1]
index   0  1  2  3  4  5

dp is 6 x 6 (indices 0..5).
Initially all zero.
```

We run:

```text
for len = 2..5:
    for left:
        right = left + len
        compute dp[left][right]
```

---

### 4.2. len = 2 (intervals with exactly 1 balloon inside)

When `len = 2`, we get intervals `(left, right)` with `right = left+2`. These contain exactly one balloon `k = left+1`.

#### Interval [0,2]

* left=0, right=2, inside = {1}:

  Only `k = 1`:

  ```text
  coins = val[0]*val[1]*val[2] + dp[0][1] + dp[1][2]
        = 1*3*1 + 0        + 0
        = 3
  dp[0][2] = 3
  ```

#### Interval [1,3]

* left=1, right=3, inside = {2}:

  ```text
  k = 2
  coins = val[1]*val[2]*val[3] + dp[1][2] + dp[2][3]
        = 3*1*5 + 0        + 0
        = 15
  dp[1][3] = 15
  ```

#### Interval [2,4]

* left=2, right=4, inside = {3}:

  ```text
  k = 3
  coins = val[2]*val[3]*val[4] + dp[2][3] + dp[3][4]
        = 1*5*8 + 0        + 0
        = 40
  dp[2][4] = 40
  ```

#### Interval [3,5]

* left=3, right=5, inside = {4}:

  ```text
  k = 4
  coins = val[3]*val[4]*val[5] + dp[3][4] + dp[4][5]
        = 5*8*1 + 0        + 0
        = 40
  dp[3][5] = 40
  ```

Now `dp` (only showing interesting entries):

```text
dp[0][2] = 3
dp[1][3] = 15
dp[2][4] = 40
dp[3][5] = 40
```

---

### 4.3. len = 3 (intervals with exactly 2 balloons inside)

Now `len = 3`, so `right = left + 3`. These contain `k ∈ {left+1, left+2}`.

Intervals:

* [0,3] → inside {1,2}
* [1,4] → inside {2,3}
* [2,5] → inside {3,4}

We use the `dp` values we already computed for smaller intervals (`len=2` and base zeros).

#### Interval [0,3] → inside balloons 1 (3), 2 (1)

Left=0, right=3.

Try all k:

* `k = 1`:

  ```text
  coins = val[0]*val[1]*val[3] + dp[0][1] + dp[1][3]
        = 1*3*5 + 0        + 15
        = 30
  ```

* `k = 2`:

  ```text
  coins = val[0]*val[2]*val[3] + dp[0][2] + dp[2][3]
        = 1*1*5 + 3        + 0
        = 8
  ```

Best:

```text
dp[0][3] = max(30, 8) = 30
```

#### Interval [1,4] → inside balloons 2 (1), 3 (5)

Left=1, right=4.

* `k = 2`:

  ```text
  coins = val[1]*val[2]*val[4] + dp[1][2] + dp[2][4]
        = 3*1*8 + 0        + 40
        = 64
  ```

* `k = 3`:

  ```text
  coins = val[1]*val[3]*val[4] + dp[1][3] + dp[3][4]
        = 3*5*8 + 15       + 0
        = 135
  ```

So:

```text
dp[1][4] = 135
```

#### Interval [2,5] → inside balloons 3 (5), 4 (8)

Left=2, right=5.

* `k = 3`:

  ```text
  coins = val[2]*val[3]*val[5] + dp[2][3] + dp[3][5]
        = 1*5*1 + 0        + 40
        = 45
  ```

* `k = 4`:

  ```text
  coins = val[2]*val[4]*val[5] + dp[2][4] + dp[4][5]
        = 1*8*1 + 40       + 0
        = 48
  ```

So:

```text
dp[2][5] = 48
```

Current interesting dp:

```text
dp[0][2] = 3
dp[0][3] = 30
dp[1][3] = 15
dp[1][4] = 135
dp[2][4] = 40
dp[2][5] = 48
dp[3][5] = 40
```

---

### 4.4. len = 4 (intervals with 3 balloons inside)

Now `len = 4`, so `right = left + 4`:

* [0,4] → inside 1,2,3
* [1,5] → inside 2,3,4

#### Interval [0,4] → balloons 1 (3),2 (1),3 (5)

Left=0, right=4.

Try `k ∈ {1,2,3}`:

* `k = 1`:

  ```text
  coins = val[0]*val[1]*val[4] + dp[0][1] + dp[1][4]
        = 1*3*8 + 0        + 135
        = 159
  ```

* `k = 2`:

  ```text
  coins = val[0]*val[2]*val[4] + dp[0][2] + dp[2][4]
        = 1*1*8 + 3        + 40
        = 51
  ```

* `k = 3`:

  ```text
  coins = val[0]*val[3]*val[4] + dp[0][3] + dp[3][4]
        = 1*5*8 + 30       + 0
        = 70
  ```

Best:

```text
dp[0][4] = 159
```

#### Interval [1,5] → balloons 2 (1),3 (5),4 (8)

Left=1, right=5.

Try `k ∈ {2,3,4}`:

* `k = 2`:

  ```text
  coins = val[1]*val[2]*val[5] + dp[1][2] + dp[2][5]
        = 3*1*1 + 0        + 48
        = 51
  ```

* `k = 3`:

  ```text
  coins = val[1]*val[3]*val[5] + dp[1][3] + dp[3][5]
        = 3*5*1 + 15       + 40
        = 70
  ```

* `k = 4`:

  ```text
  coins = val[1]*val[4]*val[5] + dp[1][4] + dp[4][5]
        = 3*8*1 + 135      + 0
        = 159
  ```

Best:

```text
dp[1][5] = 159
```

---

### 4.5. len = 5 (full interval with 4 balloons inside)

Now `len = 5`, so only one interval:

* [0,5] → inside 1,2,3,4 (the entire original array)

Left=0, right=5, k ∈ {1,2,3,4}.

#### k = 1 (last balloon value 3)

```text
coins = val[0]*val[1]*val[5] + dp[0][1] + dp[1][5]
      = 1*3*1 + 0        + 159
      = 162
```

#### k = 2 (last 1)

```text
coins = val[0]*val[2]*val[5] + dp[0][2] + dp[2][5]
      = 1*1*1 + 3        + 48
      = 52
```

#### k = 3 (last 5)

```text
coins = val[0]*val[3]*val[5] + dp[0][3] + dp[3][5]
      = 1*5*1 + 30       + 40
      = 75
```

#### k = 4 (last 8)

```text
coins = val[0]*val[4]*val[5] + dp[0][4] + dp[4][5]
      = 1*8*1 + 159      + 0
      = 167
```

Take the maximum:

```text
dp[0][5] = max(162, 52, 75, 167) = 167
```

That’s our final answer.

---

## 5. Connecting to an optimal bursting order

The bottom-up DP just fills numbers, but conceptually it still corresponds to a **last-burst** choice per interval:

* For interval [0,5] we picked `k = 4` (balloon 8 last).
* For [0,4] we picked `k = 1` (balloon 3 last in that sub-interval).
* For [1,4] we picked `k = 3` (balloon 5).
* For [1,3] we have only k=2 (balloon 1).

Reading these “last choices” inside-out gives a valid optimal burst order:

```text
Burst sequence: [1, 5, 3, 8]
                (indices 2,3,1,4 in original nums)
Total coins = 15 + 120 + 24 + 8 = 167
```

Exactly matching `dp[0][5]`.

---
*/

// class Solution {
//     public int maxCoins(int[] nums) {
//         int n = nums.length;
//         // Build padded array: val[0] = 1, val[n+1] = 1
//         int[] val = new int[n + 2];
//         val[0] = 1;
//         val[n + 1] = 1;
//         for (int i = 0; i < n; i++) {
//             val[i + 1] = nums[i];
//         }

//         // dp[left][right] = max coins from bursting all balloons in (left, right)
//         int[][] dp = new int[n + 2][n + 2];

//         // len is distance between left and right
//         // minimum len = 2 (one balloon inside), maximum len = n+1 (full interval 0..n+1)
//         for (int len = 2; len <= n + 1; len++) {
//             for (int left = 0; left + len <= n + 1; left++) {
//                 int right = left + len;

//                 // Try all possible balloons as the last in (left, right)
//                 int best = 0;
//                 for (int k = left + 1; k < right; k++) {
//                     int coins = val[left] * val[k] * val[right]
//                               + dp[left][k]
//                               + dp[k][right];
//                     if (coins > best) {
//                         best = coins;
//                     }
//                 }
//                 dp[left][right] = best;
//             }
//         }

//         // Answer: burst all balloons between 0 and n+1
//         return dp[0][n + 1];
//     }
// }
