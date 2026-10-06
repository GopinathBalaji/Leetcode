// Method 1: 1D DP / Kadane-style algorithm
/*
This is a **Dynamic Programming / Kadane-style** problem.

The key twist is that unlike Maximum Subarray, you cannot track only the best positive product so far.

A **negative number can turn the smallest negative product into the largest positive product**.

### Hint 1: Track both the maximum and minimum product ending at each index

Let:

```cpp
maxProd
```

mean:

```text
maximum product of a subarray ending at the current index
```

And let:

```cpp
minProd
```

mean:

```text
minimum product of a subarray ending at the current index
```

Why track the minimum?

Because:

```text
negative × negative = positive
```

So today's worst negative product might become tomorrow's best positive product.

---

### Hint 2: At each number, there are three possibilities

Suppose the current number is:

```cpp
x = nums[i];
```

A subarray ending at `i` can be:

```text
1. start fresh at x

2. extend the previous max product

3. extend the previous min product
```

So the new maximum is based on:

```cpp
x
x * previousMax
x * previousMin
```

Similarly, the new minimum comes from those same three values.

---

### Hint 3: The recurrence

Conceptually:

```cpp
newMax = max({
    x,
    x * maxProd,
    x * minProd
});
```

and:

```cpp
newMin = min({
    x,
    x * maxProd,
    x * minProd
});
```

Then update:

```cpp
maxProd = newMax;
minProd = newMin;
```

---

### Hint 4: Be careful not to overwrite too early

This is important.

If you do:

```cpp
maxProd = ...
minProd = ...
```

and the second calculation uses the already-updated `maxProd`, your recurrence is wrong.

So store:

```cpp
int oldMax = maxProd;
int oldMin = minProd;
```

or compute:

```cpp
int newMax = ...
int newMin = ...
```

before assigning them back.

---

### Hint 5: Example with a negative number

Consider:

```cpp
nums = {-2, 3, -4};
```

After processing:

```text
-2
```

you have:

```text
maxProd = -2
minProd = -2
```

After `3`:

```text
maxProd = 3
minProd = -6
```

Now process `-4`.

Candidates:

```text
-4

3 * -4 = -12

-6 * -4 = 24
```

So:

```text
newMax = 24
newMin = -12
```

The minimum product was essential.

---

### Hint 6: Zero naturally resets things

Suppose:

```cpp
nums = {-2, 0, -1};
```

When you reach:

```cpp
x = 0;
```

your candidates include:

```text
0
```

so both running products can reset to `0`.

Then the next number can effectively start a new subarray.

You do not need special segmentation logic for zero if your recurrence is written correctly.

---

### Hint 7: Track the global answer separately

`maxProd` only means:

```text
best product ending at the current index
```

But the answer may have occurred earlier.

So keep:

```cpp
int ans = nums[0];
```

and after processing each number:

```cpp
ans = max(ans, maxProd);
```

---

### Hint 8: Initialize from the first element

A clean initialization is:

```cpp
int maxProd = nums[0];
int minProd = nums[0];
int ans = nums[0];
```

Then start your loop at:

```cpp
i = 1
```

This handles cases where every number is negative.

---

### Hint 9: Alternative shortcut when `x < 0`

Another common implementation trick is:

```cpp
if (x < 0) {
    swap(maxProd, minProd);
}
```

Why?

Multiplying by a negative flips roles:

```text
largest positive → becomes very negative
smallest negative → may become very positive
```

Then you can update:

```cpp
maxProd = max(x, x * maxProd);
minProd = min(x, x * minProd);
```

This is a slightly more compact version of the same idea.

---

### Skeleton

```cpp
class Solution {
public:
    int maxProduct(vector<int>& nums) {
        int maxProd = nums[0];
        int minProd = nums[0];
        int ans = nums[0];

        for (int i = 1; i < nums.size(); i++) {
            int x = nums[i];

            int oldMax = maxProd;
            int oldMin = minProd;

            maxProd = max({
                // start fresh
                // extend old max
                // extend old min
            });

            minProd = min({
                // same three candidates
            });

            ans = max(ans, maxProd);
        }

        return ans;
    }
};
```

The core idea is:

```text
for each number x:

newMax =
max(
    x,
    x * oldMax,
    x * oldMin
)

newMin =
min(
    x,
    x * oldMax,
    x * oldMin
)
```

The trickiest part is realizing that you must track the **minimum product too**, because a later negative number can flip it into the maximum product.
*/
class Solution {
public:
    int maxProduct(vector<int>& nums) {
        int n = nums.size();

        int maxProd = nums[0];
        int minProd = nums[0];
        int ans = nums[0];

        for(int i=1; i<n; i++){
            int x = nums[i];

            int oldMax = maxProd;
            int oldMin = minProd;

            maxProd = std::max({x, x * oldMax, x * oldMin});
            minProd = std::min({x, x * oldMax, x * oldMin});

            ans = std::max(ans, maxProd);
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna