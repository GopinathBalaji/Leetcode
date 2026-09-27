// Method 1: 1D DP
/*
This is a **Dynamic Programming / Fibonacci** problem.

The key idea is that to reach stair `n`, your last move must have come from either:

```text
n - 1
```

or:

```text
n - 2
```

So the number of ways to reach stair `n` depends on the answers to those two smaller subproblems.

### Hint 1: Define what your DP state means

Let:

```cpp
dp[i]
```

mean:

```text
the number of distinct ways to reach stair i
```

You want:

```cpp
dp[n]
```

---

### Hint 2: Think about the last move

You can climb either:

```text
1 step
```

or:

```text
2 steps
```

So if you're standing on stair `i`, your previous position must have been:

```text
i - 1
```

or:

```text
i - 2
```

Therefore:

```cpp
dp[i] = dp[i - 1] + dp[i - 2];
```

---

### Hint 3: Figure out the base cases

For:

```text
n = 1
```

there is only:

```text
1
```

way:

```text
1
```

So:

```cpp
dp[1] = 1;
```

For:

```text
n = 2
```

the possibilities are:

```text
1 + 1
2
```

So:

```cpp
dp[2] = 2;
```

---

### Hint 4: Build the answer bottom-up

Once you know:

```cpp
dp[1] = 1;
dp[2] = 2;
```

you can compute:

```cpp
dp[3] = dp[2] + dp[1];
dp[4] = dp[3] + dp[2];
dp[5] = dp[4] + dp[3];
```

and so on.

Conceptually:

```cpp
for (int i = 3; i <= n; i++) {
    dp[i] = dp[i - 1] + dp[i - 2];
}
```

---

### Hint 5: Notice the Fibonacci pattern

The sequence becomes:

```text
n = 1 → 1
n = 2 → 2
n = 3 → 3
n = 4 → 5
n = 5 → 8
```

Each answer is the sum of the previous two.

So this is essentially Fibonacci with slightly different starting values.

---

### Hint 6: You don't actually need the entire DP array

To compute:

```cpp
dp[i]
```

you only need:

```cpp
dp[i - 1]
dp[i - 2]
```

So instead of:

```cpp
vector<int> dp(n + 1);
```

you can keep only two variables:

```cpp
int prev2 = 1;
int prev1 = 2;
```

Then calculate:

```cpp
int curr = prev1 + prev2;
```

and shift them forward.

---

### Hint 7: Be careful with small `n`

If you use the two-variable approach, handle:

```cpp
if (n == 1) {
    return 1;
}
```

before initializing values for stairs `1` and `2`.

---

### Skeleton

```cpp
class Solution {
public:
    int climbStairs(int n) {
        if (n == 1) {
            return 1;
        }

        int prev2 = 1; // ways to reach stair 1
        int prev1 = 2; // ways to reach stair 2

        for (int i = 3; i <= n; i++) {
            int curr =  previous two answers;

            // shift values forward

            // prev2 = ?
            // prev1 = ?
        }

        return prev1;
    }
};
```

The core recurrence is:

```text
ways(n)
=
ways(n - 1)
+
ways(n - 2)
```

because the final move into stair `n` must be either a **1-step jump** or a **2-step jump**.
*/
class Solution {
public:
    int climbStairs(int n) {
        if(n <= 2){
            return n;
        }

        vector<int> dp(n + 1);

        dp[1] = 1;
        dp[2] = 2;

        for(int i=3; i<=n; i++){
            dp[i] = dp[i-1] + dp[i-2];
        }

        return dp[n];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna