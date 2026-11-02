// Method 1: Greedy 
/*
# LeetCode 978. Longest Turbulent Subarray — Hints (C++)

This is a **Dynamic Programming + Greedy + Array Traversal + State Tracking** problem.

The key idea is that instead of checking every possible subarray, you can track the **longest turbulent subarray ending at each index** based on whether the current element is greater than or less than the previous element.

This is closely related to:

- **376. Wiggle Subsequence:** Find a sequence where consecutive differences alternate between positive and negative.
- **674. Longest Continuous Increasing Subsequence:** Track the longest valid contiguous sequence ending at each index.
- **53. Maximum Subarray:** Maintain the best result ending at the current index.
- **198. House Robber:** Use previous DP states to determine the current state.

The major difference from LeetCode 376 is that **the selected elements must be contiguous, so you cannot skip elements to maintain the alternating pattern**.

---

## Hint 1: Understand what makes a subarray turbulent

A subarray is turbulent when consecutive comparisons alternate between:

```text
< > < > < ...
```

or:

```text
> < > < > ...
```

For example:

```cpp
arr = {4, 2, 10, 7, 8};
```

The comparisons are:

```text
4 > 2 < 10 > 7 < 8
```

They alternate perfectly.

Therefore, the entire subarray is turbulent.

```cpp
answer = 5;
```

However:

```cpp
arr = {4, 6, 8};
```

has comparisons:

```text
4 < 6 < 8
```

Two consecutive comparisons have the same direction.

Therefore, the longest turbulent subarray has length:

```cpp
answer = 2;
```

**Important:** Equal adjacent elements also break turbulence.

For example:

```cpp
arr = {4, 4, 4};
```

The answer is:

```cpp
1
```

because no pair of adjacent elements satisfies a strict inequality.

---

## Hint 2: Think about subarrays ending at index `i`

Instead of asking:

> What is the longest turbulent subarray anywhere in the array?

Ask:

> What is the longest turbulent subarray that must end at index `i`?

Consider:

```cpp
arr = {9, 4, 2, 10};
```

At index `2`:

```text
9 > 4 > 2
```

The comparisons do not alternate.

Therefore, the longest turbulent subarray ending at index `2` is:

```text
{4, 2}
```

Its length is:

```cpp
2
```

Now consider index `3`:

```text
4 > 2 < 10
```

The comparisons alternate.

Therefore, the longest turbulent subarray ending at index `3` is:

```text
{4, 2, 10}
```

Its length is:

```cpp
3
```

**Key observation:** Whether we can extend a turbulent subarray depends on the direction of the previous comparison.

This suggests tracking two states.

---

## Hint 3: Define two DP states

Let:

```cpp
up
```

represent:

```text
Length of the longest turbulent
subarray ending at the current index,
where the last comparison is increasing.
```

That means:

```cpp
arr[i] > arr[i - 1]
```

Similarly:

```cpp
down
```

represents:

```text
Length of the longest turbulent
subarray ending at the current index,
where the last comparison is decreasing.
```

That means:

```cpp
arr[i] < arr[i - 1]
```

For initialization, both states can have length `1`, representing a single-element subarray.

```cpp
int up = 1;
int down = 1;
```

Why two states?

Because an increasing comparison can extend a turbulent subarray whose previous comparison was decreasing.

Similarly, a decreasing comparison can extend one whose previous comparison was increasing.

---

## Hint 4: Case 1 — Current element is greater than previous

Suppose:

```cpp
arr[i] > arr[i - 1]
```

The current comparison is:

```text
<
```

For example:

```text
2 < 10
```

To maintain turbulence, the previous comparison must have been:

```text
>
```

For example:

```text
4 > 2 < 10
```

Therefore, the current increasing comparison can extend the previous decreasing state.

### Transition

```cpp
up = down + 1;
```

But what happens to `down`?

Since the current comparison is increasing, a turbulent subarray ending here cannot have a decreasing final comparison.

Reset:

```cpp
down = 1;
```

### Complete transition

```cpp
if (arr[i] > arr[i - 1]) {

    up = down + 1;

    down = 1;
}
```

**Important:** We extend the opposite state because turbulence requires alternating comparison directions.

---

## Hint 5: Case 2 — Current element is smaller than previous

Now suppose:

```cpp
arr[i] < arr[i - 1]
```

The current comparison is:

```text
>
```

For example:

```text
10 > 7
```

To maintain turbulence, the previous comparison must have been increasing:

```text
2 < 10 > 7
```

Therefore, extend the previous `up` state.

### Transition

```cpp
down = up + 1;
```

Reset the increasing state:

```cpp
up = 1;
```

### Complete transition

```cpp
else if (arr[i] < arr[i - 1]) {

    down = up + 1;

    up = 1;
}
```

Notice the symmetry:

```text
Current comparison increasing:
    up = down + 1

Current comparison decreasing:
    down = up + 1
```

This is the core DP idea.

---

## Hint 6: Case 3 — Adjacent elements are equal

Suppose:

```cpp
arr[i] == arr[i - 1]
```

For example:

```cpp
arr = {4, 2, 2, 10};
```

The comparisons are:

```text
4 > 2 = 2 < 10
```

The equality breaks the turbulent pattern.

No turbulent subarray of length greater than `1` can cross this equality.

Therefore, reset both states:

```cpp
up = 1;
down = 1;
```

### Complete transition

```cpp
else {

    up = 1;

    down = 1;
}
```

**Important:** Equality breaks both possible alternating patterns.

---

## Hint 7: Track the global maximum

Remember:

```cpp
up
down
```

represent the longest turbulent subarrays ending at the current index.

But the optimal turbulent subarray may end anywhere.

Therefore, maintain:

```cpp
int maxLen = 1;
```

After processing each index:

```cpp
maxLen = max(maxLen, max(up, down));
```

### Why not return `max(up, down)` at the end?

Consider:

```cpp
arr = {4, 2, 10, 7, 8, 8};
```

The longest turbulent subarray is:

```text
{4, 2, 10, 7, 8}
```

Its length is:

```cpp
5
```

But the final two elements are equal:

```text
8 = 8
```

Therefore, both states reset to `1` at the last index.

If you return only the final state, you lose the earlier maximum.

**Always track the global maximum separately.**

---

## Hint 8: Walk through an example

Consider:

```cpp
arr = {9, 4, 2, 10, 7, 8, 8, 1, 9};
```

Initialize:

```cpp
up = 1;
down = 1;
maxLen = 1;
```

Now apply the transitions.

| Index | `arr[i]` | Comparison | `up` | `down` | `maxLen` |
|---|---:|---|---:|---:|---:|
| 0 | 9 | — | 1 | 1 | 1 |
| 1 | 4 | `9 > 4` | 1 | 2 | 2 |
| 2 | 2 | `4 > 2` | 1 | 2 | 2 |
| 3 | 10 | `2 < 10` | 3 | 1 | 3 |
| 4 | 7 | `10 > 7` | 1 | 4 | 4 |
| 5 | 8 | `7 < 8` | 5 | 1 | 5 |
| 6 | 8 | `8 == 8` | 1 | 1 | 5 |
| 7 | 1 | `8 > 1` | 1 | 2 | 5 |
| 8 | 9 | `1 < 9` | 3 | 1 | 5 |

The longest turbulent subarray is:

```text
{4, 2, 10, 7, 8}
```

Its comparisons are:

```text
4 > 2 < 10 > 7 < 8
```

Therefore:

```cpp
answer = 5;
```

---

## Hint 9: Analyze the complexity

Let:

```cpp
int n = arr.size();
```

### Time complexity

We traverse the array once.

At each index, we perform constant-time comparisons and updates.

Therefore:

\[
\boxed{\text{Time: }O(n)}
\]

### Space complexity

We only maintain three variables:

```cpp
int up;
int down;
int maxLen;
```

No DP array is necessary.

Therefore:

\[
\boxed{\text{Space: }O(1)}
\]

---

# Skeleton — Dynamic Programming + Greedy (C++)

Try filling in the missing expressions.

```cpp
class Solution {
public:
    int maxTurbulenceSize(vector<int>& arr) {

        int n = arr.size();

        // Longest turbulent subarray
        // ending with an increasing comparison.

        int up = 1;

        // Longest turbulent subarray
        // ending with a decreasing comparison.

        int down = 1;

        // Global maximum length.

        int maxLen = 1;

        for (int i = 1; i < n; i++) {

            // Case 1:
            // Current comparison is increasing.

            if (arr[i] > arr[i - 1]) {

                // Extend previous decreasing state.

                up = _____;

                // Reset decreasing state.

                down = _____;
            }

            // Case 2:
            // Current comparison is decreasing.

            else if (arr[i] < arr[i - 1]) {

                // Extend previous increasing state.

                down = _____;

                // Reset increasing state.

                up = _____;
            }

            // Case 3:
            // Equal adjacent elements.

            else {

                // Equality breaks turbulence.

                up = _____;

                down = _____;
            }

            // Track the longest subarray.

            maxLen = _____;
        }

        return maxLen;
    }
};
```

The most important expressions to derive are:

```cpp
// Increasing comparison.

up = _____;
```

and:

```cpp
// Decreasing comparison.

down = _____;
```

Think about why an increasing comparison must extend the previous decreasing state, and vice versa.

---

# Core Recurrence

Define:

\[
up[i]
\]

as the longest turbulent subarray ending at index `i` whose final comparison is increasing.

Define:

\[
down[i]
\]

as the longest turbulent subarray ending at index `i` whose final comparison is decreasing.

For convenience, a single-element subarray has length `1` in both states.

### Base cases

\[
\boxed{up[0]=down[0]=1}
\]

### Case 1: Increasing comparison

If:

\[
arr[i]>arr[i-1]
\]

then:

\[
\boxed{up[i]=down[i-1]+1}
\]

\[
\boxed{down[i]=1}
\]

### Case 2: Decreasing comparison

If:

\[
arr[i]<arr[i-1]
\]

then:

\[
\boxed{down[i]=up[i-1]+1}
\]

\[
\boxed{up[i]=1}
\]

### Case 3: Equal elements

If:

\[
arr[i]=arr[i-1]
\]

then:

\[
\boxed{up[i]=down[i]=1}
\]

### Final answer

\[
\boxed{\max_{0\leq i<n}\left(\max(up[i],down[i])\right)}
\]

Since each state depends only on the previous index, we can optimize the space to:

\[
\boxed{O(1)}
\]

---

# Connection to LeetCode 376. Wiggle Subsequence

The strongest connection is with **376. Wiggle Subsequence**.

Both problems use alternating comparisons.

For example:

```cpp
nums = {1, 7, 4, 9, 2};
```

The comparisons are:

```text
1 < 7 > 4 < 9 > 2
```

This sequence is both turbulent and a wiggle subsequence.

However, consider:

```cpp
nums = {1, 3, 5, 2};
```

In LeetCode 376, you can skip `3` and select:

```text
{1, 5, 2}
```

This is a valid wiggle subsequence of length `3`.

In LeetCode 978, you cannot skip `3`.

The original comparisons are:

```text
1 < 3 < 5 > 2
```

The longest turbulent subarray has length `3`:

```text
{3, 5, 2}
```

### Comparison

| Feature | 376. Wiggle Subsequence | 978. Longest Turbulent Subarray |
|---|---|---|
| Alternating comparisons | Yes | Yes |
| Must be contiguous? | No | Yes |
| Can skip elements? | Yes | No |
| Two DP states | `up`, `down` | `up`, `down` |
| Equal elements | Cannot extend the wiggle | Break the current turbulent subarray |
| Time complexity | O(n) | O(n) |
| Space complexity | O(1) | O(1) |

**The important distinction is that turbulence requires consecutive elements to alternate without skipping any indices.**

---

# Common Mistakes to Avoid

### Mistake 1: Allowing consecutive comparisons in the same direction

Consider:

```cpp
arr = {1, 3, 5};
```

The comparisons are:

```text
1 < 3 < 5
```

They do not alternate.

Therefore:

```cpp
answer = 2;
```

not `3`.

### Mistake 2: Forgetting to reset on equality

Consider:

```cpp
arr = {4, 2, 2, 5};
```

The equality:

```text
2 == 2
```

breaks turbulence.

Both states must reset to `1`.

### Mistake 3: Extending the same state

This is incorrect:

```cpp
if (arr[i] > arr[i - 1]) {

    up = up + 1;
}
```

Why?

Because it would allow consecutive increasing comparisons.

Instead, an increasing comparison must extend the previous decreasing state.

### Mistake 4: Returning only the final state

The longest turbulent subarray may occur before the end of the array.

Maintain a separate `maxLen`.

---

# Edge Cases to Test

| Input | Expected output | Reason |
|---|---:|---|
| `{5}` | 1 | Single element |
| `{4, 4, 4}` | 1 | All equal |
| `{1, 2}` | 2 | Two unequal elements |
| `{1, 2, 3}` | 2 | Consecutive increases |
| `{3, 2, 1}` | 2 | Consecutive decreases |
| `{1, 3, 2, 4}` | 4 | Perfect alternation |
| `{4, 2, 2, 5}` | 2 | Equality breaks turbulence |
| `{9, 4, 2, 10, 7, 8, 8, 1, 9}` | 5 | Optimal subarray ends early |

---

# One Final Mental Trick

At every index, ask:

> Did the comparison direction change from the previous step?

```text
            Compare arr[i]
            with arr[i-1]
                  |
        ┌─────────┼─────────┐
        |         |         |
        v         v         v
       >          <         ==
        |         |         |
        v         v         v
   Extend      Extend     Reset
    down         up       both
        |         |         |
        v         v         v
  up=down+1  down=up+1   up=1
  down=1     up=1        down=1
        |         |         |
        └─────────┼─────────┘
                  |
                  v
           Update maxLen
                  |
                  v
             Next index
```

---

## Final Takeaway

**LeetCode 978. Longest Turbulent Subarray is a classic example of two-state Dynamic Programming optimized to constant space.**

The three most important ideas are:

1. **Track two states:** `up` for an increasing final comparison and `down` for a decreasing final comparison.
2. **Extend the opposite state:** Increasing comparisons extend `down`, while decreasing comparisons extend `up`.
3. **Reset on equality:** Equal adjacent elements break turbulence completely.

The essential transitions are:

\[
\boxed{
\begin{aligned}
arr[i]>arr[i-1]&:\quad up=down+1,\quad down=1\\
arr[i]<arr[i-1]&:\quad down=up+1,\quad up=1\\
arr[i]=arr[i-1]&:\quad up=down=1
\end{aligned}
}
\]

**Time:** \(O(n)\)  
**Space:** \(O(1)\)

**The biggest conceptual skill is recognizing that maintaining an alternating pattern requires extending the opposite comparison state, while any equality or repeated direction forces the current pattern to restart.**
*/
class Solution {
public:
    int maxTurbulenceSize(vector<int>& arr) {
        int n = arr.size();

        int up = 1;
        int down = 1;
        int maxLen = 1;

        for(int i=1; i<n; i++){
            if(arr[i] > arr[i-1]){
                up = down + 1;
                down = 1;
            }else if(arr[i] < arr[i - 1]){
                down = up + 1;
                up = 1;
            }else{
                up = 1;
                down = 1;
            }

            maxLen = std::max({maxLen, up, down});
        }

        return maxLen;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna