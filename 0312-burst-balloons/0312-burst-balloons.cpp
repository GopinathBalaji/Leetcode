// Method 1: 2D DP
/*
# LeetCode 312. Burst Balloons — Hints

This is a **Dynamic Programming + Interval DP + Partitioning (Maximum Score)** problem.

The key idea is that instead of trying to determine which balloon to burst **first**, you should think about which balloon to burst **last** within a particular interval.

This small change in perspective makes the problem much easier.

This is closely related to:

- **516. Longest Palindromic Subsequence:** Solve problems involving intervals of an array or string.
- **1039. Minimum Score Triangulation of Polygon:** Partition an interval and choose the optimal score.
- **1547. Minimum Cost to Cut a Stick:** Choose an operation within an interval and combine independent subproblems.
- **664. Strange Printer:** Use interval DP to optimize a sequence of operations.

The major difference is that **bursting a balloon changes its neighbors**, so the order in which you burst balloons affects the coins you receive.

We'll derive the interval DP solution step by step, then explore both bottom-up DP and top-down memoization, following the progressive format of your Edit Distance example. Pasted text

---

## Hint 1: Understand what happens when you burst a balloon

Suppose:

```cpp
nums = {3, 1, 5, 8};
```

You have four balloons:

```text
3   1   5   8
```

When you burst balloon `i`, you earn:

```cpp
nums[i - 1] * nums[i] * nums[i + 1]
```

Here:

- `nums[i - 1]` is the closest remaining balloon on the left.
- `nums[i]` is the balloon being burst.
- `nums[i + 1]` is the closest remaining balloon on the right.

**Important:** These are the neighbors at the moment you burst the balloon, not necessarily its original neighbors.

For example, burst balloon `1` first:

```text
Original:

3   1   5   8
    ↑
  Burst
```

Coins earned:

```cpp
3 * 1 * 5 = 15;
```

After bursting `1`, the remaining balloons are:

```text
3   5   8
```

Notice that `3` and `5` are now neighbors.

Next, burst `5`:

```text
3   5   8
    ↑
  Burst
```

Coins earned:

```cpp
3 * 5 * 8 = 120;
```

Remaining balloons:

```text
3   8
```

Next, burst `3`:

```text
3   8
↑
Burst
```

Since there is no balloon on the left, treat its value as `1`.

Coins earned:

```cpp
1 * 3 * 8 = 24;
```

Finally, burst `8`:

```cpp
1 * 8 * 1 = 8;
```

Total:

```cpp
15 + 120 + 24 + 8 = 167;
```

Therefore:

```cpp
answer = 167;
```

This is the maximum number of coins for this example.

**Important observation:** The coins earned from bursting a balloon depend on which other balloons are still present.

---

## Hint 2: Why is choosing the first balloon difficult?

Suppose:

```cpp
nums = {3, 1, 5, 8};
```

You might try choosing the balloon that gives the most coins immediately.

Initially:

```text
3   1   5   8
```

Consider bursting `1`:

```cpp
3 * 1 * 5 = 15;
```

Or bursting `5`:

```cpp
1 * 5 * 8 = 40;
```

A greedy approach might choose `5` because:

```cpp
40 > 15
```

But the balloon that gives the most coins immediately does not necessarily lead to the maximum total coins.

Why?

Because bursting a balloon changes the neighbors of the remaining balloons.

Consider:

```text
3   1   5   8
```

If you burst `5` first:

```text
3   1   8
```

Now `1` has different neighbors.

Its value when burst becomes:

```cpp
3 * 1 * 8 = 24;
```

Instead of:

```cpp
3 * 1 * 5 = 15;
```

The problem is that each decision affects future decisions.

### Why is this difficult for DP?

Suppose you choose balloon `k` to burst first.

The balloons on its left and right may become neighbors after `k` disappears.

Therefore, the left and right parts are not independent.

```text
Before bursting k:

LEFT   k   RIGHT

After bursting k:

LEFT       RIGHT
     ↑
New interaction
```

This makes it difficult to divide the problem into independent subproblems.

**Key question:**

What if we reverse our thinking?

Instead of asking:

> Which balloon should I burst first?

Ask:

> Which balloon should I burst last?

---

## Hint 3: The most important trick — Think about the LAST balloon

This is the central insight of the problem.

Suppose you are considering an interval of balloons.

```text
LEFT_BOUNDARY   ... balloons ...   RIGHT_BOUNDARY
```

Imagine choosing balloon `k` to be the **last balloon burst inside this interval**.

What happens?

By the time you burst `k`, every other balloon strictly inside the interval has already disappeared.

Therefore, the only remaining balloons around `k` are the two boundaries.

```text
Initially:

LEFT   a   b   k   c   d   RIGHT

After bursting all other interior balloons:

LEFT           k           RIGHT

Finally burst k.
```

The coins earned from bursting `k` last are:

```cpp
LEFT * k * RIGHT
```

More precisely:

```cpp
nums[left] * nums[k] * nums[right]
```

Notice something important.

**The coins earned from bursting `k` last are completely predictable.**

You don't need to know the precise order in which all the other balloons were burst.

You only need to know:

- The left boundary.
- The right boundary.
- Which balloon is burst last.

This is what makes dynamic programming possible.

---

## Hint 4: Why does choosing the LAST balloon create independent subproblems?

Suppose:

```text
LEFT   a   b   k   c   d   RIGHT
```

You decide that `k` will be the last balloon burst inside the interval.

Therefore, before bursting `k`, you must burst:

```text
a   b
```

and:

```text
c   d
```

But `k` remains present while these balloons are being burst.

This means that the balloons to the left of `k` can be handled independently of the balloons to its right.

```text
LEFT   a   b   k   c   d   RIGHT
       └───┘       └───┘
        LEFT       RIGHT
      SUBPROBLEM  SUBPROBLEM
```

The left subproblem has boundaries:

```text
LEFT ... k
```

The right subproblem has boundaries:

```text
k ... RIGHT
```

After solving both:

```text
LEFT   k   RIGHT
```

Finally, burst `k`.

Total coins:

```text
Coins from left subproblem

+

Coins from right subproblem

+

Coins from bursting k last
```

This gives us the structure:

```cpp
totalCoins =
    leftCoins +
    rightCoins +
    lastBurstCoins;
```

Compare this with problems such as Edit Distance.

In Edit Distance, you solve smaller **prefixes**.

Here, you solve smaller **intervals**.

This suggests **Interval Dynamic Programming**.

---

## Hint 5: Add virtual balloons at both ends

Before defining the DP state, simplify the boundary cases.

Suppose:

```cpp
nums = {3, 1, 5, 8};
```

The problem says that when a balloon has no neighbor on one side, the missing neighbor is treated as `1`.

Instead of repeatedly checking whether a balloon is at the boundary, add two virtual balloons.

```text
Original:

3   1   5   8

After padding:

1   3   1   5   8   1
```

These two extra balloons:

- Always have value `1`.
- Are never burst.
- Act as permanent boundaries.

Your new array becomes:

```cpp
vector<int> balloons = {1, 3, 1, 5, 8, 1};
```

Let's label the indices:

```text
Index:      0   1   2   3   4   5

Balloon:    1   3   1   5   8   1
```

Now every real balloon has a left and right boundary.

Even if all the other real balloons have been burst, its neighbors are well-defined.

For example, if balloon `8` is the last balloon remaining:

```text
1   8   1
```

Coins:

```cpp
1 * 8 * 1 = 8;
```

No special boundary condition is needed.

### C++ preparation

```cpp
int n = nums.size();

vector<int> balloons(n + 2, 1);

for (int i = 0; i < n; i++) {
    balloons[i + 1] = nums[i];
}
```

Why `n + 2`?

Because:

```text
n original balloons

+

1 virtual left boundary

+

1 virtual right boundary
```

So the total array size is:

```cpp
n + 2
```

**Important:** We never burst the virtual balloons.

---

## Hint 6: Define your DP state

Now comes the most important part.

Let:

```cpp
dp[left][right]
```

represent:

```text
The MAXIMUM coins obtainable by bursting
all balloons STRICTLY BETWEEN

left and right,

while keeping the balloons at
left and right unburst.
```

Pay attention to:

**STRICTLY BETWEEN.**

The boundaries themselves are not burst within this subproblem.

For example:

```cpp
balloons = {1, 3, 1, 5, 8, 1};
```

Consider:

```cpp
dp[1][4]
```

The indices are:

```text
Index:       0   1   2   3   4   5

Balloon:     1   3   1   5   8   1
                 ↑       ↑   ↑
                LEFT   inside RIGHT
```

The boundaries are:

```cpp
left = 1;   // Balloon 3

right = 4;  // Balloon 8
```

The balloons strictly inside are:

```text
1   5
```

Therefore:

```cpp
dp[1][4]
```

asks:

> What is the maximum number of coins obtainable by bursting balloons `1` and `5`, assuming balloons `3` and `8` remain present?

The key detail is that `3` and `8` are not burst while solving this subproblem.

Another example:

```cpp
dp[0][5]
```

The boundaries are the two virtual balloons.

```text
1   3   1   5   8   1
↑                   ↑
LEFT               RIGHT
```

Everything between these boundaries must be burst.

Therefore:

```cpp
dp[0][5]
```

represents the answer for the entire problem.

In general:

```cpp
dp[0][n + 1]
```

will contain our final answer.

---

## Hint 7: Understand the base cases

Suppose:

```cpp
dp[left][right]
```

represents the maximum coins obtained by bursting all balloons strictly between `left` and `right`.

Now consider:

```cpp
right = left + 1;
```

For example:

```cpp
dp[1][2]
```

Look at the array:

```text
Index:      0   1   2   3   4   5

Balloon:    1   3   1   5   8   1
                ↑   ↑
               LEFT RIGHT
```

There are no balloons between these two indices.

Therefore, there is nothing to burst.

Coins earned:

```cpp
0
```

So:

```cpp
dp[left][left + 1] = 0;
```

Similarly:

```cpp
dp[0][1] = 0;

dp[1][2] = 0;

dp[2][3] = 0;

dp[3][4] = 0;

dp[4][5] = 0;
```

This is our base case.

### DP table initialization

```cpp
int N = balloons.size();

vector<vector<int>> dp(
    N,
    vector<int>(N, 0)
);
```

Why can we initialize everything to `0`?

Because:

- An empty interval produces zero coins.
- Balloon values are nonnegative.
- We want to maximize the number of coins.

Every nonempty interval will be calculated using the recurrence.

---

## Hint 8: Choose the LAST balloon inside an interval

Suppose you want to calculate:

```cpp
dp[left][right]
```

There may be several balloons between `left` and `right`.

You don't know which one should be burst last.

So try every possible choice.

Let:

```cpp
k
```

represent the index of the last balloon burst inside the interval.

Since the boundaries must remain unburst:

```cpp
left < k < right
```

Therefore, the possible values of `k` are:

```cpp
left + 1
left + 2
...
right - 1
```

In C++:

```cpp
for (int k = left + 1; k < right; k++) {

    // Assume balloon k is burst last.
}
```

For every choice of `k`, calculate the maximum coins possible.

Then choose the best option.

Why maximum?

Because the problem asks for the **maximum total number of coins**.

---

## Hint 9: Calculate the coins earned by bursting `k` last

Suppose you're calculating:

```cpp
dp[left][right]
```

and you choose balloon `k` to be the last balloon burst.

At that moment, all other balloons strictly inside the interval have disappeared.

The remaining arrangement is:

```text
balloons[left]   balloons[k]   balloons[right]
```

Therefore, coins earned from bursting `k` last:

```cpp
balloons[left] *
balloons[k] *
balloons[right]
```

Let's use an example.

```cpp
balloons = {1, 3, 1, 5, 8, 1};
```

Suppose:

```cpp
left = 1;
right = 4;
```

You are considering:

```text
Index:       1   2   3   4

Balloon:     3   1   5   8
             ↑           ↑
            LEFT        RIGHT
```

The interior balloons are:

```text
1   5
```

There are two possibilities.

### Option 1: Burst balloon `1` last

Here:

```cpp
k = 2;
```

By the time `1` is burst, balloon `5` has disappeared.

```text
3   1   8
```

Coins earned from bursting `1` last:

```cpp
3 * 1 * 8 = 24;
```

### Option 2: Burst balloon `5` last

Here:

```cpp
k = 3;
```

By the time `5` is burst, balloon `1` has disappeared.

```text
3   5   8
```

Coins earned from bursting `5` last:

```cpp
3 * 5 * 8 = 120;
```

Notice:

```cpp
120 > 24
```

However, **you cannot simply choose the largest final burst value**.

You must also account for the coins earned while bursting the other balloons.

That leads to the next hint.

---

## Hint 10: Divide the interval into two smaller intervals

Suppose:

```cpp
dp[left][right]
```

and you choose:

```cpp
k
```

as the last balloon to burst.

Visualize:

```text
LEFT   a   b   k   c   d   RIGHT
```

Because `k` is burst last, everything to the left and right of `k` must be burst before it.

This creates two smaller problems.

### Subproblem 1: Burst everything between `left` and `k`

```text
LEFT   a   b   k
```

The maximum coins obtainable are:

```cpp
dp[left][k]
```

Notice that `k` is the right boundary, so it is not burst inside this subproblem.

### Subproblem 2: Burst everything between `k` and `right`

```text
k   c   d   RIGHT
```

The maximum coins obtainable are:

```cpp
dp[k][right]
```

Again, `k` is not burst inside this subproblem.

### Final operation: Burst `k`

After both subproblems are complete:

```text
LEFT   k   RIGHT
```

Burst `k`:

```cpp
balloons[left] *
balloons[k] *
balloons[right]
```

Combine everything:

```cpp
totalCoins =
    dp[left][k]
    +
    dp[k][right]
    +
    balloons[left] * balloons[k] * balloons[right];
```

This is the most important calculation in the problem.

### Visualizing the transition

```text
                   dp[left][right]
                          |
               Choose k to burst LAST
                          |
              ┌───────────┴───────────┐
              |                       |
       LEFT SUBPROBLEM          RIGHT SUBPROBLEM
              |                       |
       dp[left][k]               dp[k][right]
              |                       |
              └───────────┬───────────┘
                          |
                   Burst k last
                          |
               balloons[left]
                      *
               balloons[k]
                      *
               balloons[right]
                          |
                          ▼
                Add all three values
```

---

## Hint 11: Derive the complete DP recurrence

For every interval:

```cpp
[left, right]
```

try every possible balloon:

```cpp
k
```

as the last balloon to burst.

For a particular choice of `k`:

```cpp
int coins =
    dp[left][k]
    +
    dp[k][right]
    +
    balloons[left] * balloons[k] * balloons[right];
```

Since the goal is to maximize the coins:

```cpp
dp[left][right] = max(
    dp[left][right],
    coins
);
```

Therefore, the complete transition is:

```cpp
for (int k = left + 1; k < right; k++) {

    int coins =
        dp[left][k]
        +
        dp[k][right]
        +
        balloons[left] * balloons[k] * balloons[right];

    dp[left][right] = max(
        dp[left][right],
        coins
    );
}
```

Mathematically:

\[
\boxed{
dp[l][r] =
\max_{l<k<r}
\left(
dp[l][k]
+
dp[k][r]
+
a[l]\cdot a[k]\cdot a[r]
\right)
}
\]

where `a` is the padded balloon array.

### Why addition?

Because you're combining three contributions:

```text
LEFT INTERVAL COINS
        +
RIGHT INTERVAL COINS
        +
LAST BURST COINS
```

### Why maximum?

Because you're choosing the best possible last balloon.

### Why does this recurrence work?

Every valid bursting order has exactly one balloon that is burst last within the interval.

If that last balloon is `k`, then:

- Every balloon to its left must have been burst already.
- Every balloon to its right must have been burst already.
- The two sides can be optimized independently because `k` remains present until the end.
- Finally, bursting `k` gives the predictable boundary product.

Since we try every possible `k`, we consider every possible choice of the last balloon.

This guarantees that the recurrence can find the optimal answer.

---

## Hint 12: Walk through a small interval

Consider:

```cpp
nums = {3, 1, 5, 8};
```

After padding:

```cpp
balloons = {1, 3, 1, 5, 8, 1};
```

Indices:

```text
       0   1   2   3   4   5

       1   3   1   5   8   1
```

Let's calculate:

```cpp
dp[1][4]
```

The boundaries are:

```cpp
balloons[1] = 3;
balloons[4] = 8;
```

The interior balloons are:

```text
1   5
```

We must burst both.

There are two choices for the last balloon.

### Case 1: `k = 2`

Balloon `1` is burst last.

The recurrence gives:

```cpp
dp[1][4] =
    dp[1][2]
    +
    dp[2][4]
    +
    balloons[1] * balloons[2] * balloons[4];
```

Calculate each part.

Left interval:

```cpp
dp[1][2] = 0;
```

There are no balloons between indices `1` and `2`.

Right interval:

```cpp
dp[2][4]
```

There is one balloon inside this interval:

```text
1   5   8
```

Burst `5`:

```cpp
1 * 5 * 8 = 40;
```

Therefore:

```cpp
dp[2][4] = 40;
```

The last burst earns:

```cpp
3 * 1 * 8 = 24;
```

Total:

```cpp
0 + 40 + 24 = 64;
```

So:

```cpp
coins = 64;
```

### Case 2: `k = 3`

Balloon `5` is burst last.

```cpp
dp[1][4] =
    dp[1][3]
    +
    dp[3][4]
    +
    balloons[1] * balloons[3] * balloons[4];
```

Left interval:

```cpp
dp[1][3]
```

The only interior balloon is `1`.

```text
3   1   5
```

Burst it:

```cpp
3 * 1 * 5 = 15;
```

Therefore:

```cpp
dp[1][3] = 15;
```

Right interval:

```cpp
dp[3][4] = 0;
```

The last burst earns:

```cpp
3 * 5 * 8 = 120;
```

Total:

```cpp
15 + 0 + 120 = 135;
```

So:

```cpp
coins = 135;
```

### Compare both choices

| Last balloon | Left coins | Right coins | Last burst | Total |
|---|---:|---:|---:|---:|
| `1` | 0 | 40 | 24 | 64 |
| `5` | 15 | 0 | 120 | **135** |

Choose the maximum:

```cpp
dp[1][4] = max(64, 135);
```

Therefore:

```cpp
dp[1][4] = 135;
```

**Important observation:** You're not determining the first balloon to burst. You're calculating the best total assuming a particular balloon is burst last.

---

## Hint 13: Why must smaller intervals be calculated first?

Look at the recurrence:

```cpp
dp[left][right] = max(
    dp[left][right],

    dp[left][k]
    +
    dp[k][right]
    +
    balloons[left] * balloons[k] * balloons[right]
);
```

To calculate:

```cpp
dp[left][right]
```

you need:

```cpp
dp[left][k]
```

and:

```cpp
dp[k][right]
```

Both correspond to smaller intervals.

For example:

```cpp
dp[1][4]
```

depends on:

```cpp
dp[1][2]
dp[2][4]
dp[1][3]
dp[3][4]
```

These intervals must already be calculated.

This tells us something about the order in which we fill the DP table.

**We should process intervals from shortest to longest.**

Unlike Edit Distance, where you can fill the table row by row, this problem requires an interval-length-based traversal.

---

## Hint 14: Introduce an interval length (or gap)

Let's define:

```cpp
gap = right - left;
```

This represents the distance between the boundary indices.

Be careful: the number of balloons strictly inside the interval is:

```cpp
gap - 1
```

For example:

```text
Index:    0   1   2   3   4   5
```

Consider:

```cpp
left = 1;
right = 4;
```

Then:

```cpp
gap = 4 - 1 = 3;
```

The interior indices are:

```text
2   3
```

So there are:

```cpp
gap - 1 = 2
```

balloons to burst.

### Gap = 1

```cpp
right = left + 1;
```

There are no balloons inside the interval.

```cpp
dp[left][right] = 0;
```

These are the base cases.

### Gap = 2

```cpp
right = left + 2;
```

There is exactly one balloon inside.

```text
LEFT   k   RIGHT
```

Only one choice exists:

```cpp
k = left + 1;
```

Therefore:

```cpp
dp[left][right] =
    balloons[left] *
    balloons[left + 1] *
    balloons[right];
```

### Gap = 3

Now there are two interior balloons.

```text
LEFT   a   b   RIGHT
```

Try:

```cpp
k = left + 1;
```

and:

```cpp
k = left + 2;
```

For each choice, combine the smaller intervals.

### Gap = 4

Now there are three interior balloons.

Again, try every possible `k`.

Continue until you calculate the interval containing every original balloon.

### Loop structure

```cpp
int N = balloons.size();

for (int gap = 2; gap < N; gap++) {

    for (int left = 0; left + gap < N; left++) {

        int right = left + gap;

        // Calculate dp[left][right]
    }
}
```

Why start at `gap = 2`?

Because intervals with `gap = 1` contain no balloons and already have value `0`.

Why increase the gap?

Because every interval depends on strictly smaller intervals.

---

## Hint 15: Put the three loops together

We now know that we need three loops.

### Loop 1: Choose the interval size

```cpp
for (int gap = 2; gap < N; gap++)
```

This ensures smaller intervals are processed first.

### Loop 2: Choose the interval boundaries

```cpp
for (int left = 0; left + gap < N; left++) {

    int right = left + gap;
}
```

This visits every interval of the current size.

### Loop 3: Choose the last balloon to burst

```cpp
for (int k = left + 1; k < right; k++)
```

This tries every possible last balloon.

### Complete traversal structure

```cpp
for (int gap = 2; gap < N; gap++) {

    for (int left = 0; left + gap < N; left++) {

        int right = left + gap;

        for (int k = left + 1; k < right; k++) {

            // Calculate coins if k is burst last.

            // Update dp[left][right].
        }
    }
}
```

The order of these loops is important.

```text
Choose interval size
        ↓
Choose left boundary
        ↓
Determine right boundary
        ↓
Try every possible last balloon
        ↓
Choose maximum coins
        ↓
Store dp[left][right]
```

**Important:** The outermost loop should process intervals in increasing size.

Otherwise, you might try to use DP values that have not been calculated yet.

---

## Hint 16: Walk through the complete example

Consider:

```cpp
nums = {3, 1, 5, 8};
```

After padding:

```cpp
balloons = {1, 3, 1, 5, 8, 1};
```

We have:

```cpp
N = 6;
```

Our goal:

```cpp
dp[0][5]
```

Let's fill the DP table by increasing gap.

### Step 1: Gap = 1 (Base cases)

There are no interior balloons.

```cpp
dp[0][1] = 0;
dp[1][2] = 0;
dp[2][3] = 0;
dp[3][4] = 0;
dp[4][5] = 0;
```

### Step 2: Gap = 2

Every interval contains exactly one balloon.

**Calculate `dp[0][2]`:**

```text
1   3   1
```

Burst `3`:

```cpp
dp[0][2] = 1 * 3 * 1;
```

Therefore:

```cpp
dp[0][2] = 3;
```

**Calculate `dp[1][3]`:**

```text
3   1   5
```

Burst `1`:

```cpp
dp[1][3] = 3 * 1 * 5;
```

Therefore:

```cpp
dp[1][3] = 15;
```

**Calculate `dp[2][4]`:**

```text
1   5   8
```

Burst `5`:

```cpp
dp[2][4] = 1 * 5 * 8;
```

Therefore:

```cpp
dp[2][4] = 40;
```

**Calculate `dp[3][5]`:**

```text
5   8   1
```

Burst `8`:

```cpp
dp[3][5] = 5 * 8 * 1;
```

Therefore:

```cpp
dp[3][5] = 40;
```

At this point:

| Interval | Maximum coins |
|---|---:|
| `dp[0][2]` | 3 |
| `dp[1][3]` | 15 |
| `dp[2][4]` | 40 |
| `dp[3][5]` | 40 |

These values will help us calculate larger intervals.

### Step 3: Gap = 3

Now each interval contains two balloons.

We already calculated:

```cpp
dp[1][4] = 135;
```

Let's calculate the other two intervals.

**Calculate `dp[0][3]`:**

The boundaries are:

```text
1   3   1   5
```

Possible last balloons:

```cpp
k = 1; // Balloon 3
k = 2; // Balloon 1
```

If `k = 1`:

```cpp
coins =
    dp[0][1]
    +
    dp[1][3]
    +
    balloons[0] * balloons[1] * balloons[3];
```

Substitute:

```cpp
coins = 0 + 15 + (1 * 3 * 5);
```

Therefore:

```cpp
coins = 30;
```

If `k = 2`:

```cpp
coins =
    dp[0][2]
    +
    dp[2][3]
    +
    balloons[0] * balloons[2] * balloons[3];
```

Substitute:

```cpp
coins = 3 + 0 + (1 * 1 * 5);
```

Therefore:

```cpp
coins = 8;
```

Choose:

```cpp
dp[0][3] = max(30, 8);
```

So:

```cpp
dp[0][3] = 30;
```

Similarly:

```cpp
dp[2][5] = 48;
```

At the end of gap `3`:

| Interval | Maximum coins |
|---|---:|
| `dp[0][3]` | 30 |
| `dp[1][4]` | 135 |
| `dp[2][5]` | 48 |

### Step 4: Gap = 4

There are now three interior balloons per interval.

For:

```cpp
dp[0][4]
```

we consider:

```text
1   3   1   5   8
```

Possible last balloons:

```cpp
k = 1;
k = 2;
k = 3;
```

Evaluate all three choices.

You obtain:

```cpp
dp[0][4] = 159;
```

Similarly:

```cpp
dp[1][5] = 159;
```

### Step 5: Gap = 5

Finally, calculate:

```cpp
dp[0][5]
```

This interval contains all four original balloons.

```text
1   3   1   5   8   1
↑                   ↑
LEFT               RIGHT
```

We can choose any of the four balloons to burst last.

| Last balloon | Left subproblem | Right subproblem | Last burst | Total |
|---|---:|---:|---:|---:|
| `3` | 0 | 159 | 3 | 162 |
| `1` | 3 | 48 | 1 | 52 |
| `5` | 30 | 40 | 5 | 75 |
| `8` | 159 | 0 | 8 | **167** |

Therefore:

```cpp
dp[0][5] = max({162, 52, 75, 167});
```

So:

```cpp
dp[0][5] = 167;
```

### Final DP table

Here is the complete upper triangular DP table:

| `dp[l][r]` | 0 | 1 | 2 | 3 | 4 | 5 |
|---|---:|---:|---:|---:|---:|---:|
| **0** | — | 0 | 3 | 30 | 159 | **167** |
| **1** | — | — | 0 | 15 | 135 | 159 |
| **2** | — | — | — | 0 | 40 | 48 |
| **3** | — | — | — | — | 0 | 40 |
| **4** | — | — | — | — | — | 0 |
| **5** | — | — | — | — | — | — |

Notice:

```cpp
dp[0][5] = 167;
```

This represents the maximum coins obtainable by bursting all original balloons.

---

## Hint 17: What is the final answer?

Recall:

```cpp
dp[left][right]
```

represents:

```text
Maximum coins obtained by bursting
all balloons strictly between
left and right.
```

We added virtual balloons at:

```cpp
0
```

and:

```cpp
n + 1
```

These boundaries surround the entire original array.

Therefore, the answer is:

```cpp
dp[0][n + 1]
```

Or, if:

```cpp
int N = balloons.size();
```

the answer is:

```cpp
dp[0][N - 1]
```

You don't need to search through the DP table for the maximum.

The interval containing the entire original array already represents the complete problem.

---

## Hint 18: Can you use top-down DP instead?

Yes.

There are two common approaches to this problem.

### Approach 1: Bottom-up interval DP

You explicitly calculate smaller intervals first.

```text
Intervals containing 0 balloons
             ↓
Intervals containing 1 balloon
             ↓
Intervals containing 2 balloons
             ↓
Intervals containing 3 balloons
             ↓
...
             ↓
Entire array
```

You use:

```cpp
dp[left][right]
```

to store already-computed results.

### Approach 2: Top-down recursion + memoization

Instead of manually choosing the calculation order, define a recursive function:

```cpp
solve(left, right)
```

that returns:

```text
Maximum coins obtainable by bursting
all balloons strictly between
left and right.
```

For each call:

1. If the interval contains no balloons, return `0`.
2. If the answer is already memoized, return it.
3. Try every possible last balloon `k`.
4. Recursively solve the left and right intervals.
5. Take the maximum total.
6. Save and return the result.

The recursive structure is:

```text
solve(left, right)
        |
        | Choose k
        |
        ├── solve(left, k)
        |
        ├── solve(k, right)
        |
        └── balloons[left]
                  *
            balloons[k]
                  *
            balloons[right]
```

This is often easier to understand initially because the recursion directly follows the mathematical recurrence.

Both approaches compute the same DP states.

---

## Hint 19: Analyze the complexity

Let:

```cpp
n = nums.size();
```

After padding:

```cpp
N = n + 2;
```

### How many DP states exist?

Each DP state is identified by:

```cpp
left
right
```

There are at most:

```cpp
N * N
```

possible pairs.

Therefore, the number of states is:

\[
O(n^2)
\]

### How much work is performed per state?

For every interval:

```cpp
dp[left][right]
```

you try every possible last balloon:

```cpp
for (int k = left + 1; k < right; k++)
```

In the worst case, you try:

```cpp
O(n)
```

different balloons.

Each choice takes constant time once the smaller DP values have been computed.

Therefore:

\[
\text{Time} =
O(n^2)\times O(n)
\]

So:

\[
\boxed{\text{Time: }O(n^3)}
\]

### Space complexity

The DP table contains:

```cpp
N * N
```

entries.

Therefore:

\[
\boxed{\text{Space: }O(n^2)}
\]

For top-down memoization, the recursion stack can use another `O(n)` space, but the overall space complexity remains `O(n²)`.

### Can you optimize the space to O(n)?

Unlike Edit Distance, this interval DP does not have a straightforward `O(n)` space optimization.

Why?

Because:

```cpp
dp[left][right]
```

can depend on many different smaller intervals:

```cpp
dp[left][k]
dp[k][right]
```

for every possible `k`.

These dependencies are not limited to just the previous row or column.

Therefore, the standard solution uses:

\[
O(n^2)
\]

space.

---

# Skeleton — Bottom-Up Interval DP (C++)

Try filling in the missing expressions yourself.

```cpp
class Solution {
public:
    int maxCoins(vector<int>& nums) {

        int n = nums.size();

        // Step 1:
        // Add virtual balloons with value 1
        // at both ends.

        vector<int> balloons(n + 2, 1);

        for (int i = 0; i < n; i++) {

            balloons[i + 1] =  ? ;
        }

        int N = balloons.size();

        // Step 2:
        // dp[left][right] = Maximum coins
        // obtainable by bursting all balloons
        // STRICTLY BETWEEN left and right.

        vector<vector<int>> dp(
            N,
            vector<int>(N, 0)
        );

        // Base case:
        // dp[left][left + 1] = 0
        //
        // No balloons exist between
        // adjacent boundaries.

        // Step 3:
        // Process intervals from
        // shortest to longest.

        for (int gap = 2; gap < N; gap++) {

            for (int left = 0;
                 left + gap < N;
                 left++) {

                int right =  ? ;

                // Step 4:
                // Try every possible balloon
                // to burst LAST.

                for (int k = left + 1;
                     k < right;
                     k++) {

                    // Maximum coins obtained
                    // from the left interval.

                    int leftCoins =  ? ;

                    // Maximum coins obtained
                    // from the right interval.

                    int rightCoins =  ? ;

                    // Coins earned from
                    // bursting k last.

                    int lastBurstCoins =  ? ;

                    // Combine all contributions.

                    int totalCoins =
                        leftCoins +
                        rightCoins +
                        lastBurstCoins;

                    // Choose maximum coins.

                    dp[left][right] =  ? ;
                }
            }
        }

        // Step 5:
        // Return the maximum coins
        // for the entire original array.

        return  ? ;
    }
};
```

Try implementing this version before looking at the next skeleton.

---

# Skeleton — Top-Down DP + Memoization (C++)

Once you understand the bottom-up solution, try the recursive version.

```cpp
class Solution {
private:

    vector<int> balloons;

    vector<vector<int>> memo;

    // Maximum coins obtainable by bursting
    // all balloons strictly between
    // left and right.

    int solve(int left, int right) {

        // Base case:
        // No balloons exist inside interval.

        if (right == left + 1) {
            return  ? ;
        }

        // Already calculated?

        if (memo[left][right] != -1) {
            return  ? ;
        }

        int best = 0;

        // Try every possible LAST balloon.

        for (int k = left + 1;
             k < right;
             k++) {

            // Solve left interval.

            int leftCoins =  ? ;

            // Solve right interval.

            int rightCoins =  ? ;

            // Burst k last.

            int lastBurstCoins =  ? ;

            // Total coins.

            int total =
                leftCoins +
                rightCoins +
                lastBurstCoins;

            // Choose maximum.

            best =  ? ;
        }

        // Save result.

        memo[left][right] =  ? ;

        return  ? ;
    }

public:

    int maxCoins(vector<int>& nums) {

        int n = nums.size();

        // Add virtual balloons.

        balloons.assign(n + 2, 1);

        for (int i = 0; i < n; i++) {
            balloons[i + 1] = nums[i];
        }

        int N = balloons.size();

        // -1 means not calculated.

        memo.assign(
            N,
            vector<int>(N, -1)
        );

        // Solve entire interval.

        return solve( ? ,  ? );
    }
};
```

The trickiest part of the recursive solution is understanding why:

```cpp
solve(left, k)
```

and:

```cpp
solve(k, right)
```

are independent when `k` is chosen as the last balloon.

---

# Core Recurrence

Define:

```cpp
dp[left][right]
```

as:

```text
Maximum coins obtainable by bursting
all balloons strictly between
left and right,

while keeping left and right unburst.
```

### Base case

When:

```cpp
right == left + 1
```

there are no balloons to burst.

Therefore:

```cpp
dp[left][right] = 0;
```

### Choose the last balloon

For every:

```cpp
left < k < right
```

calculate:

```cpp
leftCoins = dp[left][k];

rightCoins = dp[k][right];

lastBurstCoins =
    balloons[left] *
    balloons[k] *
    balloons[right];
```

### Transition

```cpp
dp[left][right] = max(
    dp[left][right],

    dp[left][k]
    +
    dp[k][right]
    +
    balloons[left] * balloons[k] * balloons[right]
);
```

### Final answer

```cpp
return dp[0][n + 1];
```

### Mental model

```text
        Given nums
             |
             v
    Add virtual balloons
       1 ... nums ... 1
             |
             v
    Define dp[left][right]
             |
             v
    Maximum coins obtained
    by bursting all balloons
    strictly between boundaries
             |
             v
    Process smaller intervals
             |
             v
    Choose balloon k
    to burst LAST
             |
             v
       ┌─────┴─────┐
       |           |
       v           v
   Left DP      Right DP
       |           |
       v           v
dp[left][k]   dp[k][right]
       |           |
       └─────┬─────┘
             |
             v
    Add coins from
    bursting k last
             |
             v
      Take MAXIMUM
    over all choices k
             |
             v
    Store dp[left][right]
             |
             v
     Return dp[0][n+1]
```

---

# Connection to Previous DP Problems

The most useful connection is with other interval DP problems.

| Problem | DP Meaning | Goal |
|---|---|---|
| 72. Edit Distance | Minimum operations for transforming prefixes | Minimize operations |
| 516. Longest Palindromic Subsequence | Longest palindromic subsequence inside an interval | Maximize length |
| 1039. Minimum Score Triangulation of Polygon | Minimum triangulation score within an interval | Minimize score |
| 1547. Minimum Cost to Cut a Stick | Minimum cost of cutting an interval | Minimize cost |
| **312. Burst Balloons** | Maximum coins obtainable by bursting an interval | Maximize coins |

Let's compare two particularly related problems.

## 1039. Minimum Score Triangulation of Polygon

This problem has a remarkably similar recurrence:

\[
dp[i][j] =
\min_{i<k<j}
\left(
dp[i][k]
+
dp[k][j]
+
values[i]\cdot values[k]\cdot values[j]
\right)
\]

You choose a point `k` to form a triangle with the interval boundaries.

Then you solve the left and right subproblems.

Compare that with Burst Balloons:

\[
dp[i][j] =
\max_{i<k<j}
\left(
dp[i][k]
+
dp[k][j]
+
balloons[i]\cdot balloons[k]\cdot balloons[j]
\right)
\]

The interval partitioning is nearly identical.

The main difference is:

```text
1039. Minimum Score Triangulation

MINIMIZE total score.
```

```text
312. Burst Balloons

MAXIMIZE total coins.
```

Recognizing this recurrence makes many interval DP problems easier.

---

## 1547. Minimum Cost to Cut a Stick

In this problem, you're given a stick and several positions at which you must make cuts.

Every cut has a cost.

The key idea is to choose a cut inside an interval and split the problem into:

```text
LEFT INTERVAL

+

RIGHT INTERVAL

+

COST OF CURRENT CUT
```

This is closely related to Burst Balloons.

However, there is an interesting difference.

### Burst Balloons

You think about:

```text
Which balloon should I burst LAST?
```

Because bursting a balloon last makes its neighbors predictable.

### Minimum Cost to Cut a Stick

You think about:

```text
Which cut should I make FIRST?
```

Because making the first cut divides the stick into two independent smaller segments.

The general lesson is:

**Choose the operation that creates independent subproblems.**

Sometimes that is the first operation.

Sometimes it is the last.

---

# Common Mistakes to Avoid

### Mistake 1: Choosing the first balloon instead of the last

A common attempt is:

```cpp
Burst k first.

Solve left interval.

Solve right interval.
```

The problem is that bursting `k` first changes the boundaries of neighboring balloons.

The two subproblems are not independent.

Instead:

```cpp
Burst k LAST.
```

This keeps `k` present while the left and right intervals are solved.

---

### Mistake 2: Bursting the interval boundaries

Recall:

```cpp
dp[left][right]
```

represents balloons:

```text
STRICTLY BETWEEN left and right.
```

Therefore, your loop must be:

```cpp
for (int k = left + 1; k < right; k++)
```

Not:

```cpp
for (int k = left; k <= right; k++)
```

The boundaries are never burst inside that DP state.

---

### Mistake 3: Using the original neighbors of `k`

A common incorrect calculation is:

```cpp
balloons[k - 1] *
balloons[k] *
balloons[k + 1]
```

Why is this incorrect?

Because `k - 1` and `k + 1` may already have been burst.

When `k` is the last balloon inside `[left, right]`, its remaining neighbors are:

```cpp
balloons[left]
```

and:

```cpp
balloons[right]
```

Therefore:

```cpp
balloons[left] *
balloons[k] *
balloons[right]
```

is the correct expression.

---

### Mistake 4: Filling the table in the wrong order

Because:

```cpp
dp[left][right]
```

depends on smaller intervals:

```cpp
dp[left][k]
dp[k][right]
```

you must calculate smaller intervals first.

Use:

```cpp
for (int gap = 2; gap < N; gap++)
```

as the outermost loop.

---

### Mistake 5: Forgetting the virtual balloons

Without virtual balloons, you need special logic for:

- The leftmost balloon.
- The rightmost balloon.
- The last remaining balloon.

Padding simplifies all of these cases.

```cpp
1   original balloons   1
```

The virtual balloons should never be burst.

---

# Three Important Rules to Remember

### Rule 1: When operations change neighboring elements, consider reversing the order

```text
Bursting FIRST
      ↓
Changes neighbors
      ↓
Creates dependent subproblems
```

Instead:

```text
Bursting LAST
      ↓
Neighbors are fixed
      ↓
Creates independent subproblems
```

### Rule 2: Define DP using open intervals

```cpp
dp[left][right]
```

means:

```text
Maximum coins obtained from
balloons strictly between
left and right.
```

The boundaries remain unburst.

### Rule 3: Try every possible last balloon

```cpp
for (int k = left + 1; k < right; k++)
```

For each choice:

```cpp
coins =
    dp[left][k]
    +
    dp[k][right]
    +
    balloons[left] * balloons[k] * balloons[right];
```

Take the maximum.

---

## One Final Mental Trick

When you see a problem involving:

- Removing elements from an array.
- Changing neighboring relationships.
- Maximizing or minimizing the total score.
- Choosing an order of operations.

Ask yourself:

> Can I make the problem easier by determining which element is removed last?

If choosing the last element makes the boundaries predictable, there's a good chance the problem can be solved using interval DP.

### Remember this diagram

```text
          LEFT                   RIGHT
            |                       |
            v                       v
            L   a   b   K   c   d   R
                └───┘     └───┘
                 LEFT     RIGHT
                  DP       DP
                   \       /
                    \     /
                     \   /
                       K
                       |
                       v
                 BURST K LAST
                       |
                       v
                L * K * R
                       |
                       v
                ADD EVERYTHING
                       |
                       v
                   TAKE MAX
```

**The biggest takeaway:** LeetCode 312 is a classic example of *interval DP where choosing the last operation makes the subproblems independent*.

Once you understand why choosing `k` as the last balloon gives you the two independent intervals `dp[left][k]` and `dp[k][right]`, the recurrence becomes intuitive.

\[
\boxed{
dp[l][r] =
\max_{l<k<r}
\left(
dp[l][k]+dp[k][r]+a[l]a[k]a[r]
\right)
}
\]

**Time:** \(O(n^3)\)  
**Space:** \(O(n^2)\)

**The most important conceptual skill is recognizing that sometimes the easiest way to solve a problem involving a sequence of operations is to reason backward from the final operation rather than forward from the first.**
*/
class Solution {
public:
    int maxCoins(vector<int>& nums) {
        int n = nums.size();

        vector<int> balloons(n + 2, 1);

        for(int i=0; i<n; i++){
            balloons[i + 1] = nums[i];
        }

        int N = balloons.size();

        vector<vector<int>> dp(N, vector<int>(N, 0));

        for(int gap=2; gap < N; gap++){
            for(int left = 0; left + gap < N; left++){
                int right = left + gap;

                for(int k = left + 1; k < right; k++){
                    int leftCoins = dp[left][k];

                    int rightCoins = dp[k][right];

                    int lastBurstCoins = balloons[left] + balloons[k] + balloons[right];

                    int totalCoins = leftCoins + rightCoins + lastBurstCoins;

                    dp[left][right] = std::max(dp[left][right], dp[left][k] + dp[k][right] + balloons[left] * balloons[k] * balloons[right]);
                }
            }
        }

        return dp[0][n + 1];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna