// Method 1: Using Boyer-Moore Voting algorithm
/*
## Hint 1: Understand the difference from LeetCode 169

In LeetCode 169, you needed the element appearing more than:

```text
n / 2
```

There can only be **one** such element.

In LeetCode 229, you need all elements appearing more than:

```text
n / 3
```

There can be at most **two** such elements.

Why? Because if there were three elements each appearing more than `n / 3`, the total count would exceed `n`.

---

## Hint 2: Hash map solution is easy

The straightforward solution is:

```cpp
unordered_map<int, int> count;
```

Count every number, then return numbers whose count is greater than `n / 3`.

This is:

```text
Time:  O(n)
Space: O(n)
```

But the follow-up usually expects **O(1) extra space**.

---

## Hint 3: Extend Boyer-Moore Voting

For majority greater than `n / 2`, you kept:

```cpp
candidate1
count1
```

For majority greater than `n / 3`, you need two candidates:

```cpp
candidate1, count1
candidate2, count2
```

Because there can be at most two valid answers.

---

## Hint 4: First pass finds possible candidates

You scan through the array and maintain two candidates.

For each `num`:

```text
If num == candidate1:
    count1++

Else if num == candidate2:
    count2++

Else if count1 == 0:
    candidate1 = num
    count1 = 1

Else if count2 == 0:
    candidate2 = num
    count2 = 1

Else:
    count1--
    count2--
```

The last case means:

```text
num is different from both candidates,
so this num cancels one vote from each candidate.
```

---

## Hint 5: Why decrement both counts?

Since we are looking for elements appearing more than `n / 3`, we can cancel groups of three different elements.

Example:

```text
a, b, c
```

If all three are different, none of them can dominate this group.

So when you see a number different from both candidates, you reduce both candidate counts.

---

## Hint 6: First pass does not guarantee correctness

Important: after the first pass, `candidate1` and `candidate2` are only **potential answers**.

You still need a second pass to count their real frequencies.

Example:

```cpp
nums = [1, 2, 3, 4]
```

The algorithm may end with some candidates, but no element appears more than `n / 3`.

So you must verify.

---

## Hint 7: Second pass verification

Reset counts:

```cpp
count1 = 0;
count2 = 0;
```

Then scan again:

```cpp
if (num == candidate1) count1++;
else if (num == candidate2) count2++;
```

Then add candidates to answer if:

```cpp
count > nums.size() / 3
```

---

## Hint 8: Be careful not to add duplicates

If `candidate1` and `candidate2` are the same, do not add the same number twice.

A safe check:

```cpp
if (count1 > n / 3) result.push_back(candidate1);

if (candidate2 != candidate1 && count2 > n / 3) {
    result.push_back(candidate2);
}
```

---

## Hint 10: Complexity

The final optimized approach is:

```text
Time:  O(n)
Space: O(1)
```

The key idea is:

```text
For majority > n/3, keep at most two candidates.
Cancel out groups of three different values.
Then verify the remaining candidates.
```
*/
class Solution {
public:
    vector<int> majorityElement(vector<int>& nums) {
        int n = nums.size();
        vector<int> ans;

        int candidate1 = 0;
        int candidate2 = 1;
        int count1 = 0;
        int count2 = 0;

        for(int num : nums){
            if(num == candidate1){
                count1++;
            }else if(num == candidate2){
                count2++;
            }else if(count1 == 0){
                candidate1 = num;
                count1 = 1;
            }else if(count2 == 0){
                candidate2 = num;
                count2 = 1;
            }else{
                count1--;
                count2--;
            }
        }

        count1 = 0;
        count2 = 0;

        for(int num : nums){
            if(num == candidate1){
                count1++;
            }else if(num == candidate2){
                count2++;
            }
        }

        if(count1 > n / 3){
            ans.push_back(candidate1);
        }

        if(candidate2 != candidate1 && count2 > n / 3){
            ans.push_back(candidate2);
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna