// Method 1: Kadane's Algorithm
/*
Conceptually you want two variables:

curr = best sum of subarray ending at i
bestSoFar = best sum of subarray seen so far
*/
class Solution {
    public int maxSubArray(int[] nums) {
        int n = nums.length;

        int[] memo = new int[n];
        memo[0] = nums[0];

        int globalMax = memo[0];

        for(int i=1; i<n; i++){
            // Start a new subarray at i OR Extend best subarray ending at i - 1
            memo[i] = Math.max(nums[i], nums[i] + memo[i-1]);

            // Global maximum
            globalMax = Math.max(globalMax, memo[i]);
        }

        return globalMax;
    }
}






// Method 2: Divide and Conquer Approach
/*
## Core idea

For any range `nums[left...right]`, the maximum subarray must be in **one of three places**:

1. completely in the **left half**
2. completely in the **right half**
3. **crossing the middle**

So for every recursive call, we do this:

* split the array into two halves
* solve left half
* solve right half
* compute the best crossing sum
* return the maximum of those three

That is the divide and conquer idea.

---

## Why the crossing case is needed

Suppose the best subarray is something like:

```text
[4, -1, 2, 1]
```

This subarray may start in the left half and continue into the right half.

So if we only checked:

* best in left half
* best in right half

we would miss such a case.

That is why we must also compute the **best subarray that crosses the midpoint**.

---

## How the crossing sum is computed

If a subarray crosses the midpoint, then:

* its left part must end at `mid`
* its right part must start at `mid + 1`

So we compute:

* the best possible sum going from `mid` toward `left`
* the best possible sum going from `mid + 1` toward `right`

Then add them.

That gives the best subarray that crosses the middle.

---

## Why the base case is one element

If `left == right`, the subarray has only one number.

Then the maximum subarray in that range is just that element itself.

Example:

```text
[5] -> 5
[-3] -> -3
```

---

## Full walkthrough on example

Take:

```text
nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
```

We want the answer for indices `0...8`.

---

### Step 1: Split at the middle

```text
left = 0, right = 8
mid = 4
```

So we split into:

* left half: `0...4`  -> `[-2, 1, -3, 4, -1]`
* right half: `5...8` -> `[2, 1, -5, 4]`

Now the answer for `0...8` must be one of:

* best in `0...4`
* best in `5...8`
* best crossing index `4`

---

## Left half: `0...4`

Subarray:

```text
[-2, 1, -3, 4, -1]
```

Split again:

```text
left = 0, right = 4
mid = 2
```

Now check:

* best in `0...2`
* best in `3...4`
* best crossing `2`

---

### Left-left half: `0...2`

Subarray:

```text
[-2, 1, -3]
```

Split at:

```text
mid = 1
```

Check:

* best in `0...1`
* best in `2...2`
* best crossing `1`

#### `0...1`

Subarray:

```text
[-2, 1]
```

Split at `mid = 0`

* best in `0...0` = `-2`
* best in `1...1` = `1`
* crossing:

Left part ending at `0`:

* `-2` → best = `-2`

Right part starting at `1`:

* `1` → best = `1`

Crossing sum = `-2 + 1 = -1`

So answer for `0...1` is:

```text
max(-2, 1, -1) = 1
```

#### `2...2`

This is just:

```text
[-3]
```

Answer = `-3`

#### Crossing `1` for range `0...2`

Need best ending at index `1` and best starting at index `2`.

Left side from `1` to `0`:

* start at `1`: sum = `1`, best = `1`
* include `-2`: sum = `-1`, best still = `1`

So left best = `1`

Right side from `2` to `2`:

* `-3`, best = `-3`

Crossing sum = `1 + (-3) = -2`

So answer for `0...2` is:

```text
max(leftBest=1, rightBest=-3, crossBest=-2) = 1
```

---

### Left-right half: `3...4`

Subarray:

```text
[4, -1]
```

Split at `mid = 3`

* best in `3...3` = `4`
* best in `4...4` = `-1`

Crossing:

Left ending at `3`:

* `4` → best = `4`

Right starting at `4`:

* `-1` → best = `-1`

Crossing sum = `4 + (-1) = 3`

So answer for `3...4` is:

```text
max(4, -1, 3) = 4
```

---

### Crossing `2` for range `0...4`

Now compute the best subarray that crosses index `2`.

Range is:

```text
[-2, 1, -3, 4, -1]
         ^
        mid
```

Need:

* best sum ending at index `2`
* best sum starting at index `3`

#### Left part ending at `2`

Walk from `2` down to `0`:

* index 2: sum = `-3`, best = `-3`
* index 1: sum = `-2`, best = `-2`
* index 0: sum = `-4`, best = `-2`

So left best = `-2`

#### Right part starting at `3`

Walk from `3` up to `4`:

* index 3: sum = `4`, best = `4`
* index 4: sum = `3`, best = `4`

So right best = `4`

Crossing sum = `-2 + 4 = 2`

So answer for `0...4` is:

```text
max(leftBest=1, rightBest=4, crossBest=2) = 4
```

So the best subarray in the left half is `[4]`.

---

## Right half: `5...8`

Subarray:

```text
[2, 1, -5, 4]
```

Split at:

```text
mid = 6
```

Check:

* best in `5...6`
* best in `7...8`
* best crossing `6`

---

### `5...6`

Subarray:

```text
[2, 1]
```

Split at `5`

* best in `5...5` = `2`
* best in `6...6` = `1`
* crossing = `2 + 1 = 3`

So answer for `5...6` = `3`

---

### `7...8`

Subarray:

```text
[-5, 4]
```

Split at `7`

* best in `7...7` = `-5`
* best in `8...8` = `4`
* crossing = `-5 + 4 = -1`

So answer for `7...8` = `4`

---

### Crossing `6` for range `5...8`

Range:

```text
[2, 1, -5, 4]
    ^
   mid
```

Need best ending at `6` and best starting at `7`.

#### Left part ending at `6`

Walk from `6` to `5`:

* index 6: sum = `1`, best = `1`
* index 5: sum = `3`, best = `3`

Left best = `3`

#### Right part starting at `7`

Walk from `7` to `8`:

* index 7: sum = `-5`, best = `-5`
* index 8: sum = `-1`, best = `-1`

Right best = `-1`

Crossing sum = `3 + (-1) = 2`

So answer for `5...8` is:

```text
max(3, 4, 2) = 4
```

So the best subarray in the right half is `[4]`.

---

## Final crossing for whole array `0...8`

Now compute the best subarray that crosses `mid = 4`.

Whole array:

```text
[-2, 1, -3, 4, -1, 2, 1, -5, 4]
                ^
               mid
```

Need:

* best sum ending at index `4`
* best sum starting at index `5`

---

### Left part ending at `4`

Walk backward from `4` to `0`:

* index 4: sum = `-1`, best = `-1`
* index 3: sum = `3`, best = `3`
* index 2: sum = `0`, best = `3`
* index 1: sum = `1`, best = `3`
* index 0: sum = `-1`, best = `3`

Left best = `3`

This corresponds to subarray:

```text
[4, -1]
```

---

### Right part starting at `5`

Walk forward from `5` to `8`:

* index 5: sum = `2`, best = `2`
* index 6: sum = `3`, best = `3`
* index 7: sum = `-2`, best = `3`
* index 8: sum = `2`, best = `3`

Right best = `3`

This corresponds to subarray:

```text
[2, 1]
```

---

### Crossing sum

So crossing sum is:

```text
3 + 3 = 6
```

This corresponds to:

```text
[4, -1, 2, 1]
```

---

## Final answer

For entire array `0...8`:

* best in left half = `4`
* best in right half = `4`
* best crossing middle = `6`

So:

```text
max(4, 4, 6) = 6
```

Final answer is:

```text
6
```

---

## Why this works

For every subarray range, the optimal subarray must fall into exactly one category:

* fully left
* fully right
* crossing the midpoint

These three cases cover all possibilities, and they do not miss anything.

That is why the recurrence is correct.

---

## Time complexity

At each recursive level:

* we split into two halves
* we spend `O(n)` total computing crossing sums across all calls at that level

So the recurrence is:

```text
T(n) = 2T(n/2) + O(n)
```

which gives:

```text
O(n log n)
```

### Space complexity

The recursion depth is:

```text
O(log n)
```

So auxiliary stack space is `O(log n)`.

---

## Comparison with Kadane’s algorithm

Kadane’s algorithm is better for this problem in practice:

* Kadane: `O(n)`
* divide and conquer: `O(n log n)`

But divide and conquer is still very important because it teaches:

* recursive splitting
* cross-boundary handling
* how to combine solutions from subproblems

---

## One subtle point

In `maxCrossingSum`, the crossing subarray must include:

* some suffix of the left half ending at `mid`
* some prefix of the right half starting at `mid + 1`

That is exactly why the loops are:

* from `mid` down to `left`
* from `mid + 1` up to `right`

Not any other range.

---

If you want, I can also give the **segment-tree style divide and conquer solution** for the same problem, which is a more advanced and elegant version.
*/

// class Solution {
//     public int maxSubArray(int[] nums) {
//         return solve(nums, 0, nums.length - 1);
//     }

//     // Returns the maximum subarray sum in nums[left...right]
//     private int solve(int[] nums, int left, int right) {
//         // Base case: only one element
//         if (left == right) {
//             return nums[left];
//         }

//         int mid = left + (right - left) / 2;

//         // Best subarray completely in the left half
//         int leftBest = solve(nums, left, mid);

//         // Best subarray completely in the right half
//         int rightBest = solve(nums, mid + 1, right);

//         // Best subarray that crosses the middle
//         int crossBest = maxCrossingSum(nums, left, mid, right);

//         // The answer for this range is the best of these 3
//         return Math.max(Math.max(leftBest, rightBest), crossBest);
//     }

//     // Returns the maximum subarray sum that crosses mid
//     // Such a subarray must include nums[mid] and nums[mid + 1] boundary crossing
//     private int maxCrossingSum(int[] nums, int left, int mid, int right) {
//         // Best sum ending at mid, going leftward
//         int leftSum = Integer.MIN_VALUE;
//         int sum = 0;
//         for (int i = mid; i >= left; i--) {
//             sum += nums[i];
//             leftSum = Math.max(leftSum, sum);
//         }

//         // Best sum starting at mid + 1, going rightward
//         int rightSum = Integer.MIN_VALUE;
//         sum = 0;
//         for (int i = mid + 1; i <= right; i++) {
//             sum += nums[i];
//             rightSum = Math.max(rightSum, sum);
//         }

//         return leftSum + rightSum;
//     }
// }