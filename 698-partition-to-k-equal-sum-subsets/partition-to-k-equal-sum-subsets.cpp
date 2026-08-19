// Method 1: Backtracking + Pruning
/*
This is very similar to **473. Matchsticks to Square**, except instead of filling 4 sides, you’re filling **`k` buckets**, each with the same target sum.

### Hint 1: Compute the required subset sum

Let:

```cpp
int total = accumulate(nums.begin(), nums.end(), 0);
```

If:

```cpp
total % k != 0
```

then partitioning is impossible.

Otherwise every subset must sum to:

```cpp
int target = total / k;
```

Also, if any number is larger than `target`, return `false`.

### Hint 2: Track the current sum of each bucket

You can use:

```cpp
vector<int> buckets(k, 0);
```

Then process one number at a time and try putting it into each bucket.

Your recursive state could be:

```cpp
backtrack(nums, index, buckets, target)
```

### Hint 3: Try placing `nums[index]` into every bucket

Inside your recursion:

```cpp
for (int i = 0; i < k; i++) {
    // try putting nums[index] into bucket i
}
```

But don't allow a bucket to exceed the target:

```cpp
if (buckets[i] + nums[index] > target) {
    continue;
}
```

Then follow the normal backtracking pattern:

```cpp
buckets[i] += nums[index];

// recurse

buckets[i] -= nums[index];
```

### Hint 4: Sort descending first

This matters a lot for performance:

```cpp
sort(nums.rbegin(), nums.rend());
```

Large numbers are harder to place, so handling them first causes impossible branches to fail earlier.

### Hint 5: The base case can be surprisingly simple

If:

```cpp
index == nums.size()
```

then every number has been placed.

If you never allowed any bucket to exceed `target`, and the total sum is exactly `k * target`, think about whether you even need to check every bucket at the end.

### Hint 6: Avoid symmetric work

Suppose:

```text
buckets = [0, 0, 0, 0]
```

Trying `nums[index]` in bucket 0, bucket 1, bucket 2, and bucket 3 produces equivalent states.

So after trying an empty bucket and failing:

```cpp
if (buckets[i] == 0) {
    break;
}
```

Be careful: check this **after undoing** the choice.

### Skeleton

```cpp
bool backtrack(vector<int>& nums,
               int index,
               vector<int>& buckets,
               int target) {

    if (index == nums.size()) {
        return true;
    }

    for (int i = 0; i < buckets.size(); i++) {

        if (buckets[i] + nums[index] > target) {
            continue;
        }

        buckets[i] += nums[index];

        // recurse

        buckets[i] -= nums[index];

        // symmetry pruning
    }

    return false;
}
```

The mental model is almost exactly:

```text
473 Matchsticks to Square:
each stick → one of 4 sides

698 Partition to K Equal Sum Subsets:
each number → one of k buckets
```

The biggest challenge in 698 is not the basic backtracking—it’s **pruning enough duplicate/symmetric states** so the search is fast.
*/
class Solution {
private:
    bool backtrack(vector<int>& nums, int k, int index, vector<int>& buckets, int target){
        if(index == nums.size()){
            return true;
        }

        for(int i=0; i<k; i++){
            if(buckets[i] + nums[index] > target){
                continue;
            }

            buckets[i] += nums[index];

            if(backtrack(nums, k, index + 1, buckets, target)){
                return true;
            }

            buckets[i] -= nums[index];

            if(buckets[i] == 0){
                break;
            }
        }

        return false;
    }


public:
    bool canPartitionKSubsets(vector<int>& nums, int k) {
        int total = std::accumulate(nums.begin(), nums.end(), 0);

        if(total % k != 0){
            return false;
        }

        int target = total / k;

        vector<int> buckets(k, 0);

        std::sort(nums.rbegin(), nums.rend());

        return backtrack(nums, k, 0, buckets, target);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna