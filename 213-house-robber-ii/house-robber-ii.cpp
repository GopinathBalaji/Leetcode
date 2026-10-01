// Method 1: 1D DP
/*
This is a **Dynamic Programming + Case Splitting** problem.

The key idea is that this is almost exactly **House Robber I**, except the houses form a circle.

That creates one extra restriction:

```text
you cannot rob both the first house and the last house
```

So instead of solving one DP problem, solve **two linear House Robber problems**.

### Hint 1: Reuse the House Robber I idea

For a normal line of houses, the recurrence is:

```cpp
dp[i] = max(
    dp[i - 1],
    nums[i] + dp[i - 2]
);
```

Mental model:

```text
skip current house
vs
rob current house
```

House Robber II uses the same recurrence.

The only problem is the circular connection.

---

### Hint 2: Why does the circle matter?

Suppose:

```cpp
nums = {2, 3, 2};
```

In a normal line, you might rob:

```text
house 0 + house 2
```

But here:

```text
house 0 and house 2 are adjacent
```

because the houses form a circle.

So you cannot include both ends.

---

### Hint 3: Split the problem into two cases

Since you cannot rob both:

```text
first house
and
last house
```

consider these two possibilities:

```text
Case 1:
ignore the last house
→ consider houses [0 ... n-2]

Case 2:
ignore the first house
→ consider houses [1 ... n-1]
```

Then solve ordinary House Robber on both ranges.

Finally:

```cpp
answer = max(case1, case2);
```

---

### Hint 4: Why are those two cases enough?

Every valid solution must exclude at least one of:

```text
house 0
house n - 1
```

So every valid robbery plan belongs to at least one of these groups:

```text
doesn't use last house
```

or:

```text
doesn't use first house
```

That means taking the best of the two linear problems covers every valid possibility.

---

### Hint 5: Write a helper for a range

Instead of duplicating your House Robber I logic, write something like:

```cpp
int robRange(
    vector<int>& nums,
    int start,
    int end
)
```

This helper should calculate the maximum money you can rob from:

```text
start ... end
```

treating those houses as a normal straight line.

Then your main function becomes roughly:

```cpp
int option1 = robRange(nums, 0, n - 2);
int option2 = robRange(nums, 1, n - 1);

return max(option1, option2);
```

---

### Hint 6: Your helper can use O(1) space

Inside `robRange`, keep two DP states:

```cpp
int prev2;
int prev1;
```

For each house:

```cpp
int curr = max(
    prev1,
    nums[i] + prev2
);
```

Then shift:

```text
prev2 ← prev1
prev1 ← curr
```

This is the same optimization from House Robber I.

---

### Hint 7: There's a convenient initialization

Instead of handling the first two houses separately inside the helper, you can start with:

```cpp
int prev2 = 0;
int prev1 = 0;
```

Then for every house in the range:

```cpp
int curr = max(
    prev1,
    prev2 + nums[i]
);
```

After that:

```cpp
prev2 = prev1;
prev1 = curr;
```

This makes the helper work cleanly for ranges of any size.

---

### Hint 8: Handle the single-house case

This is important.

If:

```cpp
nums.size() == 1
```

then the answer is simply:

```cpp
nums[0]
```

You should handle this before splitting into:

```text
0 ... n-2
1 ... n-1
```

---

### Example

Suppose:

```cpp
nums = {1, 2, 3, 1};
```

Case 1: ignore the last house:

```text
{1, 2, 3}
```

Best result:

```text
1 + 3 = 4
```

Case 2: ignore the first house:

```text
{2, 3, 1}
```

Best result:

```text
3
```

So:

```cpp
max(4, 3) = 4
```

---

### Skeleton

```cpp
class Solution {
private:
    int robRange(
        vector<int>& nums,
        int start,
        int end
    ) {
        int prev2 = 0;
        int prev1 = 0;

        for (int i = start; i <= end; i++) {
            int curr = max(
                 skip current ,
                 rob current 
            );

            // shift
            // prev2 = ?
            // prev1 = ?
        }

        return prev1;
    }

public:
    int rob(vector<int>& nums) {
        int n = nums.size();

        if (n == 1) {
            return nums[0];
        }

        int excludeLast =
            robRange(nums, 0, n - 2);

        int excludeFirst =
            robRange(nums, 1, n - 1);

        return max(excludeLast, excludeFirst);
    }
};
```

The core idea is:

```text
circular houses
        ↓
first and last cannot both be robbed
        ↓
split into two linear problems

[0 ... n-2]
and
[1 ... n-1]
        ↓
solve House Robber I twice
        ↓
take the maximum
```

The trickiest part is realizing that you do **not** need a new DP recurrence for the circular case. You just break the circle by excluding one endpoint at a time.
*/
class Solution {
private:
    int dp(vector<int>& nums, int start, int end){

        int prev2 = 0;
        int prev1 = 0;

        for(int i=start; i<=end; i++){
            int curr = std::max(prev1, nums[i] + prev2);

            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }

public:
    int rob(vector<int>& nums) {
        int n = nums.size();

        if(n == 1){
            return nums[0];
        }

        return std::max(dp(nums, 0, n-2), dp(nums, 1, n-1));        
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna