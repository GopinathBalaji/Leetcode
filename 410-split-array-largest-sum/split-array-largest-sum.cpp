// Method 1: Binary Search with open interval on the possible sum range
/*
This is another **binary search on the answer** problem, very similar to “Capacity To Ship Packages Within D Days.”

You need to split `nums` into exactly `k` non-empty subarrays while minimizing the **largest subarray sum**.

For example:

```text
nums = [7,2,5,10,8], k = 2
```

One split:

```text
[7,2,5] | [10,8]
```

Subarray sums are `14` and `18`, so the largest sum is `18`.

Your goal is to make that maximum as small as possible.

## Key observation

Instead of directly deciding where to split, ask:

> If the largest allowed subarray sum is `mid`, can I split the array into at most `k` subarrays?

This is a yes/no question, so binary search works.

## Search range

The answer cannot be smaller than the largest single number:

```cpp
left = max(nums);
```

The answer cannot be larger than the sum of all elements:

```cpp
right = sum(nums);
```

So search:

```cpp
[left, right]
```

## Feasibility check

For a candidate maximum sum `mid`:

* Keep adding numbers to the current subarray.
* If adding the next number would make the sum exceed `mid`:

  * Start a new subarray.
  * Increment the number of pieces used.

Pseudo-code:

```cpp
pieces = 1;
currentSum = 0;

for each num:
    if currentSum + num > mid:
        pieces++;
        currentSum = num;
    else:
        currentSum += num;
```

Then:

```cpp
return pieces <= k;
```

Why `<= k`, not `== k`?

Because if you can form fewer than `k` subarrays under this maximum sum, you can always split some existing subarrays further into non-empty pieces without increasing the largest sum.

## Binary-search direction

* If `pieces <= k`:

  * `mid` is feasible.
  * Try a smaller maximum sum.

```cpp
right = mid;
```

* If `pieces > k`:

  * `mid` is too small.
  * You need more than `k` subarrays.

```cpp
left = mid + 1;
```

## Skeleton

```cpp
int left = *max_element(nums.begin(), nums.end());
int right = accumulate(nums.begin(), nums.end(), 0);

while (left < right) {
    int mid = left + (right - left) / 2;

    if (canSplit(nums, k, mid)) {
        right = mid;
    } else {
        left = mid + 1;
    }
}

return left;
```

## Walk through one candidate

For:

```text
nums = [7,2,5,10,8], k = 2
mid = 17
```

Greedily split:

```text
[7,2,5] = 14
[10] = 10
[8] would make 18, so start another group
[8] = 8
```

That uses `3` subarrays:

```text
[7,2,5] | [10] | [8]
```

Since `3 > 2`, maximum sum `17` is too small.

Try a larger value.

## Important details

* Use `long long` for `left`, `right`, `mid`, and running sums if constraints may cause integer overflow.
* The greedy feasibility check works because all numbers are non-negative.
* The answer is the **minimum feasible maximum subarray sum**.
*/
class Solution {
private:
    bool isFeasible(vector<int>& nums, int k, int maxSum){
        int pieces = 1;
        int currentSum = 0;

        for(int num: nums){
            if(currentSum + num > maxSum){
                pieces++;
                currentSum = num;
            }else{
                currentSum += num;
            }
        }

        return pieces <= k;
    }


public:
    int splitArray(vector<int>& nums, int k) {
        int left = 0;
        int right = 0;

        for(int num: nums){
            left = std::max(left, num);
            right += num;
        }
        // Better way to find max of array and sum of array:
        // left = *max_element(nums.begin(), nums.end());
        // right = accumulate(nums.begin(), nums.end());

        while(left < right){
            int mid = left + (right - left) / 2;

            if(isFeasible(nums, k, mid)){
                right = mid;
            }else{
                left = mid + 1;
            }
        }

        return left;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna