// Method 1: DP + Sliding Window
/*
## LeetCode 1871: Jump Game VII (C++)

**Approach: Dynamic Programming + Sliding Window | Difficulty: Medium**

### Hint 1: Think DP
You need to determine whether you can reach the last index.

Define:

```cpp
dp[i] = true;
```

If index `i` is reachable.

You can reach index `i` only if:
- `s[i] == '0'`
- At least one index `j` in `[i - maxJump, i - minJump]` is reachable.

### Hint 2: Avoid O(n²)

Checking every possible previous index is too slow.

Instead, maintain a **count of reachable indices** within the valid jumping window.

```cpp
int reachable = 0;
```

### Hint 3: Sliding Window

As you move to index `i`:

Add the new index entering the window:

```cpp
if (i >= minJump)
    reachable += dp[i - minJump];
```

Remove the index leaving the window:

```cpp
if (i > maxJump)
    reachable -= dp[i - maxJump - 1];
```

### Hint 4: DP Transition

If the current position contains `'0'` and at least one previous position is reachable:

```cpp
dp[i] = (s[i] == '0' && reachable > 0);
```

### Complete C++ Solution

```cpp
class Solution {
public:
    bool canReach(string s, int minJump, int maxJump) {
        int n = s.size();
        vector<bool> dp(n, false);

        dp[0] = true;
        int reachable = 0;

        for (int i = 1; i < n; i++) {
            if (i >= minJump)
                reachable += dp[i - minJump];

            if (i > maxJump)
                reachable -= dp[i - maxJump - 1];

            dp[i] = (s[i] == '0' && reachable > 0);
        }

        return dp[n - 1];
    }
};
```

### Complexity

- **Time:** O(n) — Each index is processed once.
- **Space:** O(n) — DP array.

### Example

```cpp
s = "011010"
minJump = 2
maxJump = 3
```

- Start at index `0`.
- Jump to index `3` (valid because `s[3] == '0'`).
- Jump to index `5` (valid because `s[5] == '0'`).
- Answer: `true`.

### Key Intuition

**DP + Sliding Window = O(n)**

Instead of checking all previous reachable positions, maintain the number of reachable positions in the valid jump range.

**Interview tip:** When a DP transition requires checking whether *any element within a moving range* satisfies a condition, consider a **sliding window or prefix sum** optimization.
*/
class Solution {
public:
    bool canReach(string s, int minJump, int maxJump) {
        int n = s.size();

        vector<bool> dp(n, false);
        dp[0] = true;

        int reachable = 0;

        for(int i=1; i<n; i++){
            if(i >= minJump){
                reachable += dp[i - minJump];
            }

            if(i > maxJump){
                reachable -= dp[i - maxJump - 1];
            }

            dp[i] = (s[i] == '0' && reachable > 0);
        }

        return dp[n - 1];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna