// Method 1: Top-Down Recursive 1-D DP
/*
## 1. Problem restatement

You’re given an array `cost` where:

* `cost[i]` = cost paid when you **step on** stair `i`
* You start **before** step `0` (ground)
* You can move either:

  * 1 step up
  * or 2 steps up
* Your goal: **reach the “top”**, which is just *beyond* the last index (`n`) with **minimum total cost**.

You’re allowed to start either:

* from step `0`, or
* from step `1`.

That’s why we take `min(dp(0), dp(1))`.

---

## 2. What does `dp(idx)` mean?

Very important: define the DP state clearly.

```java
private int dp(int idx, int[] cost, int[] memo)
```

We interpret this as:

> **dp(idx)** = *the minimum total cost to reach the top (past the last stair) **starting from stair index `idx`**, assuming you will step on `idx` and pay `cost[idx]`.*

From stair `idx`, you have two choices:

* Step to `idx + 1`
* Step to `idx + 2`

Each time you step on a stair, you pay that stair’s cost.
You **never** pay a cost for the “top” because it’s not a stair index in the array.

---

## 3. Why is the recurrence correct?

Inside `dp`:

```java
int one = cost[idx] + dp(idx + 1, cost, memo);
int two = cost[idx] + dp(idx + 2, cost, memo);

memo[idx] = Math.min(one, two);
```

Interpretation:

* You are currently at stair `idx`.
* By stepping on stair `idx`, you pay `cost[idx]` now.
* After that, you have two choices:

  * Take **one step**: go to stair `idx + 1`, and from there you will incur `dp(idx + 1)` further cost.
  * Take **two steps**: go to stair `idx + 2`, and from there you will incur `dp(idx + 2)` further cost.

So total cost from `idx` if you go:

* One step: `cost[idx] + dp(idx + 1)`
* Two steps: `cost[idx] + dp(idx + 2)`

You want the **minimum** of these two choices:

```java
dp(idx) = min(
    cost[idx] + dp(idx + 1),
    cost[idx] + dp(idx + 2)
)
```

That’s exactly what you coded.

---

## 4. Base case: why `idx >= cost.length` → return 0?

```java
if (idx >= cost.length) {
    // Reached or passed the top
    return 0;
}
```

* `cost.length` = `n` = number of stairs.
* Valid stair indices: `0` to `n-1`.
* The “top” is **position `n`**, which is **just after** the last stair.

If `idx >= n`, this means:

* You have gone past the last stair (you’re at or above the top).
* There are no more stairs to step on.
* So you don’t pay anything more.

Hence, cost from here = `0`.

This base case “caps” the recursion.

---

## 5. Why call both `dp(0, ...)` and `dp(1, ...)`?

Problem statement: you can start from step `0` or step `1`.

* If you start on step `0`, your total cost is: `dp(0)`
* If you start on step `1`, your total cost is: `dp(1)`

You want the cheaper of those two:

```java
return Math.min(zeroIdx, oneIdx);
```

The separate `memo1` and `memo2` just mean:

* We’re treating “start at 0” and “start at 1” as two independent scenarios.

(You *could* reuse one memo with some care, but this is perfectly fine and simple for interviews.)

---

## 6. Walkthrough example: `cost = [10, 15, 20]`

This is a classic LeetCode example.

* Index:   `0    1    2`
* cost[] = `10, 15, 20`

We expect answer = **15** by:

* Start at step 1 (cost 15), then jump 2 steps to the top.

Let’s trace your code step-by-step.

### Step 0: `minCostClimbingStairs`

```java
int n = 3;

memo1 = [-1, -1, -1]
zeroIdx = dp(0, cost, memo1);

memo2 = [-1, -1, -1]
oneIdx = dp(1, cost, memo2);

return min(zeroIdx, oneIdx);
```

---

### Compute `dp(0, cost, memo1)`

Call: `dp(0)`

1. `idx = 0 < 3`, base case not hit.
2. `memo1[0] == -1`, so not memoized.
3. Compute:

   ```java
   one = cost[0] + dp(1)
   two = cost[0] + dp(2)
   ```

We need `dp(1)` and `dp(2)`.

---

#### Compute `dp(1, cost, memo1)` (from inside dp(0))

Call: `dp(1)`

1. `idx = 1 < 3`, continue.
2. `memo1[1] == -1`.
3. Compute:

   ```java
   one = cost[1] + dp(2)
   two = cost[1] + dp(3)
   ```

Need `dp(2)` and `dp(3)`.

---

##### Compute `dp(2, cost, memo1)` (from inside dp(1))

Call: `dp(2)`

1. `idx = 2 < 3`, continue.
2. `memo1[2] == -1`.
3. Compute:

   ```java
   one = cost[2] + dp(3)
   two = cost[2] + dp(4)
   ```

Need `dp(3)` and `dp(4)`.

---

###### Compute `dp(3, cost, memo1)`

Call: `dp(3)`

1. `idx = 3 >= 3`, base case hit → return `0`.

So `dp(3) = 0`.

###### Compute `dp(4, cost, memo1)`

Call: `dp(4)`

1. `idx = 4 >= 3`, base case hit → return `0`.

So `dp(4) = 0`.

---

Back to `dp(2)`:

* `cost[2] = 20`
* `one = 20 + dp(3) = 20 + 0 = 20`
* `two = 20 + dp(4) = 20 + 0 = 20`
* `memo1[2] = min(20, 20) = 20`

So:

```java
dp(2) = 20
memo1 = [-1, -1, 20]
```

Return `20` to caller (`dp(1)`).

---

##### Compute `dp(3, cost, memo1)` again (from inside dp(1))

We already know: `dp(3)` base case = `0`.

So:

* `cost[1] = 15`
* `one = 15 + dp(2) = 15 + 20 = 35`
* `two = 15 + dp(3) = 15 + 0 = 15`

Then:

```java
memo1[1] = min(35, 15) = 15
dp(1) = 15
memo1 = [-1, 15, 20]
```

Return `15` to caller (`dp(0)`).

---

#### Compute `dp(2, cost, memo1)` (from inside dp(0))

Now we call `dp(2)` again, but:

```java
if (memo[2] != -1) return memo[2];
```

* `memo1[2]` is already `20`
* So we immediately return `20` without recomputing.

So for `dp(0)`:

* `cost[0] = 10`
* `one = 10 + dp(1) = 10 + 15 = 25`
* `two = 10 + dp(2) = 10 + 20 = 30`

Then:

```java
memo1[0] = min(25, 30) = 25
dp(0) = 25
memo1 = [25, 15, 20]
```

So `zeroIdx = 25`.

---

### Compute `dp(1, cost, memo2)` (independent run)

Now we do the same logic for `memo2`, starting from index 1.

`memo2 = [-1, -1, -1]`

Call: `dp(1, cost, memo2)`

We already saw the math:

* From `idx = 1`:

  * `one = 15 + dp(2)`
  * `two = 15 + dp(3)`

We’ll end up with:

* `dp(2) = 20`
* `dp(3) = 0`
* so `dp(1) = min(15+20, 15+0) = min(35, 15) = 15`

So:

```java
oneIdx = 15
```

No need to trace every single call again; it’s structurally identical to the earlier path, just starting from index 1.

---

### Final answer

```java
zeroIdx = 25
oneIdx  = 15

return Math.min(25, 15) = 15
```

Which matches the expected result.

**Interpretation**:

* Starting from step 0: you pay at least 25 total cost.
* Starting from step 1: you can pay as little as 15 total cost.
* So we start from step 1.

---

## 7. Slightly larger example to see memoization in action

Take a more interesting example:

```java
int[] cost = {1, 100, 1, 1, 1, 100, 1, 1, 100, 1};
```

You won’t want to expand the entire tree by hand, but conceptually:

* `dp(0)` will call `dp(1)` and `dp(2)`
* `dp(1)` will call `dp(2)` and `dp(3)`
* `dp(2)` will call `dp(3)` and `dp(4)`
* ...

Without memoization, `dp(3)`, `dp(4)`, etc. would be recomputed many times.

With memoization:

* First time you compute `dp(k)`, you store it in `memo[k]`.
* Next time any path calls `dp(k)`, you just read and return `memo[k]` immediately.

So even though the recursion tree *looks* exponential, the actual number of unique states is just `n` (indices `0..n-1`).

That’s what drops the complexity from exponential to linear.

---

## 8. Time and space complexity

* **Time**:
  Each index `idx` is computed **once**.
  For each `idx`, we do O(1) work (a couple of additions and a min).
  So total time = **O(n)**.

* **Space**:

  * `memo` uses O(n) space.
  * Recursion depth in the worst case is also O(n) (if we always take +1 steps).
  * So total space = **O(n)**.

(There is also a bottom-up version that can get O(1) extra space, but your top-down version is completely valid and very clear.)
*/
class Solution {
    public int minCostClimbingStairs(int[] cost) {
        int n = cost.length;
        int[] memo1 = new int[n];
        Arrays.fill(memo1, -1);

        int zeroIdx = dp(0, cost, memo1);

        int[] memo2 = new int[n];
        Arrays.fill(memo2, -1);

        int oneIdx = dp(1, cost, memo2); 

        return Math.min(zeroIdx, oneIdx);      
    }

    public int dp(int idx, int[] cost, int[] memo){
        if(idx >= cost.length){
            // Reached or passed the top
            return 0;
        }

        if(memo[idx] != -1){
            // Already computed
            return memo[idx];
        }

        int one = cost[idx] + dp(idx + 1, cost, memo);
        int two = cost[idx] + dp(idx + 2, cost, memo);

        memo[idx] = Math.min(one, two);

        return memo[idx];
    }
}




// Method 2: Bottom-Up 1-D DP
/*
## 1. Rethinking the DP state (bottom-up)

We’ll use a slightly different—but very clean—DP definition from your top-down one:

> Let **dp[i]** be the *minimum cost to reach step `i`*.

Here, “step `i`” means *a position*, not an index in `cost`:

* The array `cost` has indices `0 .. n-1`.
* We define positions `0 .. n`:

  * Positions `0 .. n-1` correspond to the actual stairs.
  * Position `n` is the **top** (just beyond the last stair).

Crucial detail from the problem:

* You can **start** from step `0` or step `1` *without paying* (you pay only when you step *on* a stair).
* So before making any move, you’re effectively “standing” at both 0 and 1 with cost 0.

That leads to:

```text
dp[0] = 0   // cost to be at position 0
dp[1] = 0   // cost to be at position 1
```

Now we need a recurrence for `i >= 2`.

### How do we get to step i?

To reach position `i` (2 ≤ i ≤ n), you could have come from:

* step `i - 1` with a 1-step move
* step `i - 2` with a 2-step move

If you come from `i - 1`:

* You must have already paid `dp[i-1]` to stand on `i-1`
* Then you step onto stair `i-1` (the one *you just left*), and pay `cost[i-1]` when you stood on it.

Actually, an easier way to see it:

* To end up at position `i`, the **last stair you step on and pay for** is either:

  * stair `i-1` (then you jump 1 step to `i`), or
  * stair `i-2` (then you jump 2 steps to `i`).

So:

* If last paid stair is `i-1`:
  total cost is `dp[i-1] + cost[i-1]`
* If last paid stair is `i-2`:
  total cost is `dp[i-2] + cost[i-2]`

We want the cheaper of these two ways:

```text
dp[i] = min( dp[i-1] + cost[i-1],
             dp[i-2] + cost[i-2] )
```

And our answer is:

```text
answer = dp[n]   // min cost to reach the top
```

---

## 2. From dp-array to O(1) space

We only ever need the **last two** values:

* `dp[i-1]`
* `dp[i-2]`

So instead of an array, we keep two variables:

* `prev1` for `dp[i-1]`
* `prev2` for `dp[i-2]`

At each `i`, we compute:

```java
cur = Math.min(prev1 + cost[i-1], prev2 + cost[i-2]);
```

Then “shift” the window forward:

```java
prev2 = prev1;  // dp[i-1] becomes dp[i-2] for next iteration
prev1 = cur;    // current dp[i] becomes dp[i-1] for next iteration
```

At the end, `prev1` will hold `dp[n]`.


No arrays for dp, just two integers.

---

## 4. Walkthrough on `[10, 15, 20]`

This is the standard example.

```text
cost = [10, 15, 20]
index:   0   1   2
n = 3
positions: 0, 1, 2, 3 (3 is top)
```

We conceptually want dp[0..3]:

* `dp[0] = 0`
* `dp[1] = 0`
* For i = 2..3: `dp[i] = min(dp[i-1] + cost[i-1], dp[i-2] + cost[i-2])`

We’ll track `i`, `prev2`, `prev1`, and `cur`:

Initial:

```text
prev2 = 0 (dp[0])
prev1 = 0 (dp[1])
```

### Iteration i = 2

Goal: compute `dp[2]`.

Formula:

```java
takeOneStep = prev1 + cost[1]; // dp[1] + cost[1]
takeTwoSteps = prev2 + cost[0]; // dp[0] + cost[0]
cur = min(takeOneStep, takeTwoSteps);
```

Fill in numbers:

* `prev1 = dp[1] = 0`
* `prev2 = dp[0] = 0`
* `cost[1] = 15`
* `cost[0] = 10`

So:

```text
takeOneStep = 0 + 15 = 15
takeTwoSteps = 0 + 10 = 10

cur = min(15, 10) = 10
```

So `dp[2] = 10`.

Now shift:

```text
prev2 = prev1 = 0  (prev2 now holds dp[1])
prev1 = cur  = 10 (prev1 now holds dp[2])
```

State now:

```text
dp[0] = 0
dp[1] = 0
dp[2] = 10

prev2 = 0   // dp[1]
prev1 = 10  // dp[2]
```

Intuition:
To reach position 2 (just after stair 1 / on stair 1?), the cheapest way is actually:

* Start at step 0 (free), pay `10` on stair 0, then jump 2 steps to 2
  → total cost = 10

or

* Start at step 1 (free), pay `15` on stair 1, then jump to 2
  → cost 15 (worse)

So the minimal cost to reach position 2 is 10.

---

### Iteration i = 3 (the top)

Now we want `dp[3]`, which is the cost to reach the **top**.

```java
takeOneStep = prev1 + cost[2];   // dp[2] + cost[2]
takeTwoSteps = prev2 + cost[1];  // dp[1] + cost[1]
cur = min(takeOneStep, takeTwoSteps);
```

Values:

* `prev1 = dp[2] = 10`
* `prev2 = dp[1] = 0`
* `cost[2] = 20`
* `cost[1] = 15`

Compute:

```text
takeOneStep = 10 + 20 = 30
takeTwoSteps =  0 + 15 = 15

cur = min(30, 15) = 15
```

So `dp[3] = 15`.

Shift again:

```text
prev2 = prev1 = 10   // now dp[2]
prev1 = cur  = 15    // now dp[3]
```

We’ve finished the loop (`i` ran up to `n = 3`).
By design, `prev1` now holds `dp[3]`, which is our answer.

So:

```text
answer = prev1 = 15
```

Which matches the known optimal path:

* Start at step 1 (no cost yet)
* Pay 15 on stair 1
* Jump 2 steps to the top
  → total cost 15.

---

## 5. Walkthrough on a bigger example

Example from LeetCode discussions:

```java
int[] cost = {1,100,1,1,1,100,1,1,100,1};
```

Index → cost:

```text
i:      0   1    2  3  4   5   6  7   8  9
cost:   1 100   1  1  1  100  1  1  100  1
n = 10
positions: 0..10 (10 is top)
```

We’ll track dp[i] conceptually (though code only keeps prev1/prev2):

* dp[0] = 0
* dp[1] = 0

For i = 2..10:

```text
dp[i] = min(dp[i-1] + cost[i-1], dp[i-2] + cost[i-2])
```

Let me list them step by step:

### i = 2

```text
dp[2] = min(dp[1] + cost[1], dp[0] + cost[0])
      = min(0 + 100,       0 + 1)
      = 1
```

### i = 3

```text
dp[3] = min(dp[2] + cost[2], dp[1] + cost[1])
      = min(1 + 1,          0 + 100)
      = min(2, 100)
      = 2
```

### i = 4

```text
dp[4] = min(dp[3] + cost[3], dp[2] + cost[2])
      = min(2 + 1,          1 + 1)
      = min(3, 2)
      = 2
```

### i = 5

```text
dp[5] = min(dp[4] + cost[4], dp[3] + cost[3])
      = min(2 + 1,          2 + 1)
      = 3
```

### i = 6

```text
dp[6] = min(dp[5] + cost[5], dp[4] + cost[4])
      = min(3 + 100,        2 + 1)
      = min(103, 3)
      = 3
```

### i = 7

```text
dp[7] = min(dp[6] + cost[6], dp[5] + cost[5])
      = min(3 + 1,          3 + 100)
      = min(4, 103)
      = 4
```

### i = 8

```text
dp[8] = min(dp[7] + cost[7], dp[6] + cost[6])
      = min(4 + 1,          3 + 1)
      = min(5, 4)
      = 4
```

### i = 9

```text
dp[9] = min(dp[8] + cost[8], dp[7] + cost[7])
      = min(4 + 100,        4 + 1)
      = min(104, 5)
      = 5
```

### i = 10 (top)

```text
dp[10] = min(dp[9] + cost[9], dp[8] + cost[8])
       = min(5 + 1,          4 + 100)
       = min(6, 104)
       = 6
```

So the minimum cost to reach the top is `dp[10] = 6`.

Your O(1) code is doing exactly these updates in a compressed form using `prev2`, `prev1`, `cur`.

---

## 6. Complexity

* **Time:**
  We do one pass from `i = 2` to `i = n`.
  Each iteration is O(1).
  → Overall **O(n)**.

* **Space:**
  We use only `prev2`, `prev1`, `cur` (three ints).
  No dp array.
  → **O(1)** extra space.
*/

// class Solution {
//     public int minCostClimbingStairs(int[] cost) {
//         int n = cost.length;
        
//         // dp[0] = 0, dp[1] = 0
//         int prev2 = 0; // dp[i-2] initially dp[0]
//         int prev1 = 0; // dp[i-1] initially dp[1]
        
//         // i goes from 2..n (inclusive)
//         for (int i = 2; i <= n; i++) {
//             int takeOneStep = prev1 + cost[i - 1]; // last stair is i-1
//             int takeTwoSteps = prev2 + cost[i - 2]; // last stair is i-2
//             int cur = Math.min(takeOneStep, takeTwoSteps); // dp[i]
            
//             // shift window
//             prev2 = prev1;
//             prev1 = cur;
//         }
        
//         // prev1 now holds dp[n], min cost to reach the top
//         return prev1;
//     }
// }
