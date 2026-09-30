// Method 1: 1D DP
/*
This is a **Dynamic Programming** problem.

The key idea is that at each house, you have two choices:

```text
rob this house
```

or:

```text
skip this house
```

If you rob house `i`, you cannot rob house `i - 1`, so the best total comes from combining `nums[i]` with the best answer from two houses back.

### Hint 1: Define your DP state

Let:

```cpp
dp[i]
```

mean:

```text
the maximum money you can rob from houses 0...i
```

Your goal is:

```cpp
dp[n - 1]
```

---

### Hint 2: At each house, make a choice

For house `i`, you have two possibilities.

#### Skip house `i`

Then your answer is just:

```cpp
dp[i - 1]
```

because nothing changes.

#### Rob house `i`

Then you cannot rob house `i - 1`.

So you add:

```cpp
nums[i]
```

to the best result from:

```cpp
dp[i - 2]
```

giving:

```cpp
nums[i] + dp[i - 2]
```

---

### Hint 3: Build the recurrence

Take the better of those two choices:

```cpp
dp[i] = max(
    dp[i - 1],
    nums[i] + dp[i - 2]
);
```

Mental model:

```text
best at house i
=
max(
    skip current house,
    rob current house
)
```

---

### Hint 4: Figure out the base cases

For just one house:

```cpp
dp[0] = nums[0];
```

For two houses, you cannot rob both, so:

```cpp
dp[1] = max(nums[0], nums[1]);
```

That gives you enough information to start computing from house `2`.

---

### Hint 5: Example

Suppose:

```cpp
nums = {1, 2, 3, 1};
```

Start with:

```text
dp[0] = 1

dp[1] = max(1, 2)
      = 2
```

For house `2`:

```text
skip:
dp[1] = 2

rob:
nums[2] + dp[0]
= 3 + 1
= 4
```

So:

```cpp
dp[2] = 4;
```

For house `3`:

```text
skip:
dp[2] = 4

rob:
nums[3] + dp[1]
= 1 + 2
= 3
```

So:

```cpp
dp[3] = 4;
```

---

### Hint 6: Why greedy doesn't work

You might be tempted to always rob the house with more money among adjacent houses.

But consider:

```cpp
nums = {2, 1, 1, 2};
```

The best choice is:

```text
house 0 + house 3
= 2 + 2
= 4
```

A purely local decision can miss the best overall combination.

That's why DP is useful: each state remembers the best result so far.

---

### Hint 7: You only need the previous two DP values

The recurrence only uses:

```cpp
dp[i - 1]
dp[i - 2]
```

So you don't actually need an entire array.

You can keep:

```cpp
int prev2 = nums[0];
int prev1 = max(nums[0], nums[1]);
```

Then calculate:

```cpp
int curr = max(
    prev1,
    nums[i] + prev2
);
```

and shift the variables forward.

---

### Hint 8: Handle the one-house case

If you use the constant-space approach, make sure you handle:

```cpp
nums.size() == 1
```

before accessing:

```cpp
nums[1]
```

So:

```cpp
if (nums.size() == 1) {
    return nums[0];
}
```

---

### Skeleton

```cpp
class Solution {
public:
    int rob(vector<int>& nums) {
        int n = nums.size();

        if (n == 1) {
            return nums[0];
        }

        int prev2 = nums[0];
        int prev1 = max(nums[0], nums[1]);

        for (int i = 2; i < n; i++) {
            int curr = max(
                skip current house ,
                 rob current house 
            );

            // shift:
            // prev2 = ?
            // prev1 = ?
        }

        return prev1;
    }
};
```

The core recurrence is:

```text
rob(i)
=
max(
    rob(i - 1),
    nums[i] + rob(i - 2)
)
```

The main idea is to think of every house as a **take-or-skip decision**: if you take the current house, you must jump back two positions; if you skip it, you keep the best answer from the previous house.
*/
class Solution {
public:
    int rob(vector<int>& nums) {
        int n = nums.size();

        if(n == 1){
            return nums[0];
        }

        vector<int> dp(n);


        dp[0] = nums[0];
        dp[1] = std::max(nums[0], nums[1]);

        for(int i=2; i<n; i++){
            dp[i] = std::max(dp[i-1], nums[i] + dp[i-2]);
        }       

        return dp[n-1];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna