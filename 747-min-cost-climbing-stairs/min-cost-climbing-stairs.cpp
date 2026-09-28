// Method 1: 1D DP
/*
This is a **Dynamic Programming** problem.

The key idea is to define the minimum cost to **reach each step**, and notice that every step can only be reached from one of the previous two steps.

### Hint 1: Understand when you pay the cost

If you step on stair `i`, you pay:

```cpp
cost[i]
```

From there, you can move:

```text
1 step
or
2 steps
```

The top is just beyond the last index, and you do **not** pay a cost for reaching the top.

---

### Hint 2: Define your DP state

Let:

```cpp
dp[i]
```

mean:

```text
minimum cost required to reach step i
```

To reach step `i`, you must come from either:

```text
i - 1
```

or:

```text
i - 2
```

So ask:

```text
Which previous step was cheaper to reach?
```

---

### Hint 3: Build the recurrence

If you want to reach stair `i`, you must pay the cost of stepping onto it.

So:

```cpp
dp[i] = cost[i] + min(dp[i - 1], dp[i - 2]);
```

This is the main recurrence.

Mental model:

```text
cheapest way to reach i
=
cost of stair i
+
cheapest of the two places you could come from
```

---

### Hint 4: What are the base cases?

The problem allows you to start at either:

```text
step 0
or
step 1
```

So:

```cpp
dp[0] = cost[0];
dp[1] = cost[1];
```

You don't pay anything before choosing your starting stair.

---

### Hint 5: Be careful — the answer is not necessarily `dp[n - 1]`

The top is one step beyond the array.

If:

```cpp
n = cost.size();
```

you can reach the top from either:

```text
step n - 1
```

or:

```text
step n - 2
```

because you can climb 1 or 2 steps.

Therefore your final answer should depend on:

```cpp
dp[n - 1]
dp[n - 2]
```

Specifically, take the cheaper one.

---

### Hint 6: Example

Suppose:

```cpp
cost = {10, 15, 20};
```

Start with:

```text
dp[0] = 10
dp[1] = 15
```

Then:

```text
dp[2]
=
20 + min(10, 15)
=
30
```

Now the top can be reached from stair `1` or stair `2`.

So compare:

```text
15
30
```

and choose the cheaper path.

---

### Hint 7: You can solve it with an array first

A straightforward version looks like:

```cpp
vector<int> dp(n);

dp[0] = cost[0];
dp[1] = cost[1];

for (int i = 2; i < n; i++) {
    dp[i] = cost[i] + min(dp[i - 1], dp[i - 2]);
}
```

Then return the minimum of the last two states.

---

### Hint 8: You only need two previous values

Just like Climbing Stairs, the recurrence only depends on:

```cpp
dp[i - 1]
dp[i - 2]
```

So you can optimize from:

```text
O(n) space
```

to:

```text
O(1) space
```

using:

```cpp
int prev2 = cost[0];
int prev1 = cost[1];
```

Then for every new stair:

```cpp
int curr = cost[i] + min(prev1, prev2);
```

and shift the variables forward.

---

### Skeleton

```cpp
class Solution {
public:
    int minCostClimbingStairs(vector<int>& cost) {
        int n = cost.size();

        int prev2 = cost[0];
        int prev1 = cost[1];

        for (int i = 2; i < n; i++) {
            int curr = cost[i] + min(prev1, prev2);

            // shift:
            // prev2 = ?
            // prev1 = ?
        }

        // You can reach the top
        // from either of the last two stairs

        return  minimum of the last two ;
    }
};
```

The core recurrence is:

```text
minCost(i)
=
cost[i]
+
min(
    minCost(i - 1),
    minCost(i - 2)
)
```

The trickiest detail is that the **top is not an actual stair with a cost**, so the final answer is the minimum cost of reaching either of the last two stairs.
*/
class Solution {
public:
    int minCostClimbingStairs(vector<int>& cost) {
        int n = cost.size();

        vector<int> dp(n);
        dp[0] = cost[0];
        dp[1] = cost[1];

        for(int i=2; i<n; ++i){
            dp[i] = cost[i] + std::min(dp[i - 1], dp[i - 2]);
        }

        return std::min(dp[n - 1], dp[n - 2]);
    }
};