// Method 1: 1D DP
/*
This is a **Dynamic Programming / Iterative Sequence** problem.

The key idea is almost identical to Fibonacci, except each value depends on the **previous three** values instead of the previous two.

### Hint 1: Understand the recurrence

The Tribonacci sequence is defined as:

```cpp
T0 = 0
T1 = 1
T2 = 1
```

And for every:

```text
n >= 3
```

you have:

```cpp
Tn = Tn-1 + Tn-2 + Tn-3;
```

So your job is just to compute this sequence until you reach `n`.

---

### Hint 2: Think of it like Climbing Stairs, but with 3 previous states

For Fibonacci-style problems, you often use:

```cpp
curr = prev1 + prev2;
```

Here, you need:

```cpp
curr = prev1 + prev2 + prev3;
```

So you only need to remember the previous three Tribonacci values.

---

### Hint 3: Handle the base cases first

The first three values are already given:

```cpp
T0 = 0;
T1 = 1;
T2 = 1;
```

So before doing any loop:

```cpp
if (n == 0) {
    return 0;
}

if (n == 1 || n == 2) {
    return 1;
}
```

This also prevents issues when initializing your three previous values.

---

### Hint 4: Initialize three rolling variables

You can represent:

```text
T0, T1, T2
```

with:

```cpp
int prev3 = 0;
int prev2 = 1;
int prev1 = 1;
```

Think of them as:

```text
prev3 = T(i - 3)
prev2 = T(i - 2)
prev1 = T(i - 1)
```

Then calculate the next value.

---

### Hint 5: Compute the next Tribonacci number

Inside your loop:

```cpp
int curr = prev1 + prev2 + prev3;
```

For example:

```text
T3 = T2 + T1 + T0
   = 1 + 1 + 0
   = 2
```

Then:

```text
T4 = T3 + T2 + T1
   = 2 + 1 + 1
   = 4
```

---

### Hint 6: Shift the variables carefully

After computing:

```cpp
curr
```

you need to move everything forward.

Before:

```text
prev3   prev2   prev1   curr
 T0      T1      T2      T3
```

After shifting:

```text
prev3   prev2   prev1
 T1      T2      T3
```

So think about the correct order for:

```cpp
prev3 = ?;
prev2 = ?;
prev1 = ?;
```

Be careful not to overwrite a value before you still need it.

---

### Hint 7: Start the loop at `3`

Since:

```cpp
T0
T1
T2
```

are already known, the first value you actually need to calculate is:

```cpp
T3
```

So:

```cpp
for (int i = 3; i <= n; i++) {
    // calculate curr
    // shift previous values
}
```

After the loop, one of your rolling variables will contain `Tn`.

---

### Hint 8: You don't need a DP array

You could use:

```cpp
vector<int> dp(n + 1);
```

with:

```cpp
dp[i] = dp[i - 1] + dp[i - 2] + dp[i - 3];
```

But that's unnecessary because each new value only depends on the previous three.

So you can get:

```text
Time:  O(n)
Space: O(1)
```

---

### Example

For:

```cpp
n = 4
```

start with:

```text
T0 = 0
T1 = 1
T2 = 1
```

Then:

```text
T3 = 1 + 1 + 0 = 2

T4 = 2 + 1 + 1 = 4
```

So:

```cpp
tribonacci(4) == 4
```

---

### Skeleton

```cpp
class Solution {
public:
    int tribonacci(int n) {
        if (n == 0) {
            return 0;
        }

        if (n == 1 || n == 2) {
            return 1;
        }

        int prev3 = 0;
        int prev2 = 1;
        int prev1 = 1;

        for (int i = 3; i <= n; i++) {
            int curr = sum previous three ;

            // shift:
            // prev3 = ?
            // prev2 = ?
            // prev1 = ?
        }

        return prev1;
    }
};
```

The core recurrence is:

```text
T(n)
=
T(n - 1)
+
T(n - 2)
+
T(n - 3)
```

The main implementation detail is keeping the **three rolling values in the correct order** when you shift them forward.
*/
class Solution {
public:
    int tribonacci(int n) {
        if(n == 0){
            return 0;
        }
        if(n == 1 || n == 2){
            return 1;
        }

        int prev3 = 0;
        int prev2 = 1;
        int prev1 = 1;

        for(int i=3; i<=n; i++){
            int curr = prev1 + prev2 + prev3;
            prev3 = prev2;
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna