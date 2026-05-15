// Method 1: Using Prefix Sum
/*
Use these hints in order.

## Hint 1: Start with brute force

You need to count the number of continuous subarrays whose sum equals `k`.

A brute force way is:

```cpp
for each start index i:
    sum = 0
    for each end index j from i to n - 1:
        sum += nums[j]
        if sum == k:
            count++
```

This works, but it is:

```text
O(n^2)
```

We want better.

---

## Hint 2: Think prefix sum

Define:

```cpp
prefixSum[i] = sum of nums[0] to nums[i]
```

If a subarray from index `l` to `r` has sum `k`, then:

```text
nums[l] + nums[l+1] + ... + nums[r] = k
```

Using prefix sums:

```text
prefixSum[r] - prefixSum[l - 1] = k
```

Rearrange:

```text
prefixSum[l - 1] = prefixSum[r] - k
```

This is the key idea.

---

## Hint 3: What are we looking for at each index?

While scanning the array, suppose your current prefix sum is:

```cpp
currSum
```

This means:

```text
currSum = sum from nums[0] to current index
```

You want to know:

```text
How many previous prefix sums equal currSum - k?
```

Because if a previous prefix sum was `currSum - k`, then the subarray after that previous point up to current index sums to `k`.

---

## Hint 4: Use a hash map

Use:

```cpp
unordered_map<int, int> freq;
```

where:

```text
freq[prefixSum] = how many times this prefix sum has appeared so far
```

Then at each number:

```cpp
currSum += num;
```

Check:

```cpp
currSum - k
```

If it exists in the map, add its frequency to the answer:

```cpp
count += freq[currSum - k];
```

Then record the current prefix sum:

```cpp
freq[currSum]++;
```

---

## Hint 5: Why store frequency, not just existence?

Because the same prefix sum can appear multiple times.

Example:

```cpp
nums = [1, -1, 1, -1]
```

Prefix sums can repeat:

```text
1, 0, 1, 0
```

Each occurrence can create a different valid subarray.

So the map value must be a count.

---

## Hint 6: Important initialization

Before scanning, set:

```cpp
freq[0] = 1;
```

Why?

This handles subarrays that start at index `0`.

Example:

```cpp
nums = [3, 4], k = 7
```

At index `1`:

```cpp
currSum = 7
currSum - k = 0
```

We need to count the empty prefix before the array starts, so `freq[0] = 1`.

---

## Hint 7: Example

For:

```cpp
nums = [1, 2, 3], k = 3
```

Initialize:

```text
freq[0] = 1
currSum = 0
count = 0
```

Process `1`:

```text
currSum = 1
currSum - k = -2
not found
freq[1]++
```

Process `2`:

```text
currSum = 3
currSum - k = 0
freq[0] = 1, so count += 1
```

This counts subarray:

```text
[1, 2]
```

Process `3`:

```text
currSum = 6
currSum - k = 3
freq[3] = 1, so count += 1
```

This counts subarray:

```text
[3]
```

Answer:

```text
2
```


## Hint 9: Why sliding window does not work here

A common mistake is trying sliding window.

Sliding window works well when all numbers are positive.

But this problem allows negative numbers.

Example:

```cpp
nums = [1, -1, 1], k = 1
```

Because sums can increase or decrease unpredictably, you cannot safely move the left pointer based only on whether the sum is too large or too small.

That is why prefix sum + hash map is the correct approach.

---

## Complexity

```text
Time:  O(n)
Space: O(n)
```

The main idea is:

```text
current prefix sum - previous prefix sum = k
```

So at each index, count how many previous prefix sums equal:

```cpp
currSum - k
```
*/
class Solution {
public:
    int subarraySum(vector<int>& nums, int k) {
        unordered_map<int, int> freq;
        freq[0] = 1;

        int currSum = 0;
        int count = 0;

        for(int i=0; i<nums.size(); i++){
            currSum += nums[i];
            
            if(freq.find(currSum - k) != freq.end()){
                count += freq[currSum - k];
            }

            freq[currSum]++;
        }

        return count;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna