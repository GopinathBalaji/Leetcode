// Method 1: 1D DP
/*
This is a **Dynamic Programming / Unbounded Knapsack** problem.

The key idea is to build the answer for every smaller amount from `0` up to `amount`.

For each amount, ask:

```text
If I use coin c as my last coin,
what was the minimum number of coins needed
for amount - c?
```

Then take the minimum across all possible coins.

### Hint 1: Define your DP state

Let:

```cpp
dp[x]
```

mean:

```text
minimum number of coins needed to make amount x
```

You want:

```cpp
dp[amount]
```

---

### Hint 2: What should the base case be?

To make amount:

```text
0
```

you need:

```text
0 coins
```

So:

```cpp
dp[0] = 0;
```

This is what allows larger amounts to build from a valid starting point.

For example, if you have coin `5`:

```text
dp[5]
can come from
dp[0] + 1
```

---

### Hint 3: Think about the last coin you use

Suppose you're trying to make amount:

```text
x
```

and you decide your last coin is:

```text
coin
```

Then before using that coin, you must have already made:

```cpp
x - coin
```

So if that smaller amount is possible:

```cpp
dp[x] = min(
    dp[x],
    dp[x - coin] + 1
);
```

The `+1` represents the current coin you're adding.

---

### Hint 4: Try every coin for every amount

For each amount:

```cpp
for (int x = 1; x <= amount; x++)
```

try every coin:

```cpp
for (int coin : coins)
```

But you can only use the coin when:

```cpp
coin <= x
```

because otherwise:

```cpp
x - coin
```

would be negative.

Conceptually:

```cpp
if (coin <= x) {
    dp[x] = min(
        dp[x],
        dp[x - coin] + 1
    );
}
```

There's one important issue with this code, though: you need a good initial value for `dp[x]`.

---

### Hint 5: Initialize impossible states to something large

Initially, you don't know how to make amounts:

```text
1, 2, 3, ..., amount
```

So initialize them to some value meaning:

```text
impossible / not computed yet
```

For example:

```cpp
vector<int> dp(amount + 1, amount + 1);
```

Why is:

```cpp
amount + 1
```

safe?

Because if a solution exists, you could never need more than `amount` coins when the smallest usable coin is `1`.

So `amount + 1` works like infinity.

Then:

```cpp
dp[0] = 0;
```

---

### Hint 6: Example with `coins = {1, 2, 5}`, `amount = 5`

Start with:

```text
dp[0] = 0
```

For amount `1`:

```text
use coin 1:
dp[1] = dp[0] + 1 = 1
```

For amount `2`:

```text
use coin 1:
dp[1] + 1 = 2

use coin 2:
dp[0] + 1 = 1
```

So:

```text
dp[2] = 1
```

For amount `3`:

```text
use coin 1:
dp[2] + 1 = 2

use coin 2:
dp[1] + 1 = 2
```

So:

```text
dp[3] = 2
```

For amount `5`:

```text
use coin 1:
dp[4] + 1

use coin 2:
dp[3] + 1

use coin 5:
dp[0] + 1 = 1
```

Therefore:

```text
dp[5] = 1
```

---

### Hint 7: This is not greedy

You might think:

```text
always take the largest coin possible
```

But that doesn't always work.

For example:

```cpp
coins = {1, 3, 4};
amount = 6;
```

Greedy would choose:

```text
4 + 1 + 1
= 3 coins
```

But the optimal answer is:

```text
3 + 3
= 2 coins
```

That's why you need DP.

---

### Hint 8: Reusing coins is allowed

This is why the problem is related to **unbounded knapsack**.

You can use the same denomination as many times as you want.

For example:

```cpp
coins = {2};
amount = 6;
```

you can do:

```text
2 + 2 + 2
```

Your recurrence naturally allows this because:

```cpp
dp[6]
```

can depend on:

```cpp
dp[4]
```

which may itself have used coin `2`.

---

### Hint 9: What if an amount is impossible?

Consider:

```cpp
coins = {2};
amount = 3;
```

You'll never find a valid transition that makes:

```cpp
dp[3]
```

smaller than your initial sentinel:

```cpp
amount + 1
```

So after filling the DP array:

```cpp
if (dp[amount] == amount + 1) {
    return -1;
}
```

Otherwise:

```cpp
return dp[amount];
```

---

### Hint 10: Why does the recurrence work?

Suppose the optimal way to make `11` ends with coin `5`.

Then before that final coin, you must have optimally made:

```text
11 - 5 = 6
```

So that candidate answer is:

```cpp
dp[6] + 1
```

Maybe the optimal solution ends with coin `2` instead:

```cpp
dp[9] + 1
```

Or coin `1`:

```cpp
dp[10] + 1
```

You simply take the best:

```text
dp[11]
=
min(
    dp[10] + 1,
    dp[9] + 1,
    dp[6] + 1
)
```

for coins `{1, 2, 5}`.

---

### Skeleton

```cpp
class Solution {
public:
    int coinChange(vector<int>& coins, int amount) {

        vector<int> dp(
            amount + 1,
            amount + 1
        );

        dp[0] = 0;

        for (int x = 1; x <= amount; x++) {

            for (int coin : coins) {

                if (coin <= x) {

                    dp[x] = min(
                        dp[x],
                        // previous answer + this coin
                    );
                }
            }
        }

        if (dp[amount] == amount + 1) {
            return -1;
        }

        return dp[amount];
    }
};
```

The core recurrence is:

```text
dp[x]
=
min over every usable coin c:

dp[x - c] + 1
```

Or mentally:

```text
current amount x
        ↓
try each possible last coin
        ↓
look up best answer for x - coin
        ↓
add 1 for current coin
        ↓
take the minimum
```

The trickiest part is usually realizing that `dp[x]` means the **minimum number of coins**, not the number of different combinations.
*/
class Solution {
public:
    int coinChange(vector<int>& coins, int amount) {
        vector<int> dp(amount + 1, amount + 1);
        dp[0] = 0;

        for(int x=1; x<=amount; ++x){
            for(int coin : coins){
                if(coin <= x){
                    dp[x] = std::min(dp[x], dp[x - coin] + 1);
                }
            }
        }

        if(dp[amount] == amount + 1){
            return -1;
        }

        return dp[amount];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna