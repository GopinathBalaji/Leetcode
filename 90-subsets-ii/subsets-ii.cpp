// Method 1: Bracktracking while adding the subset every recursion
/*
This is basically **LeetCode 78: Subsets**, plus one extra challenge: **duplicates in the input**.

### Hint 1: Sort first

If:

```cpp
nums = [1, 2, 2]
```

sorting keeps equal values adjacent, which makes it easier to avoid generating duplicate subsets.

```cpp
sort(nums.begin(), nums.end());
```

### Hint 2: Use the same backtracking pattern as Combinations

Track:

```cpp
start
current
result
```

At every recursive call, the current subset itself is already a valid answer:

```cpp
result.push_back(current);
```

Then try adding more elements starting from `start`.

### Hint 3: The duplicate rule is the key

Inside the loop, if the current number is the same as the previous number **at the same recursion level**, skip it:

```cpp
if (i > start && nums[i] == nums[i - 1]) {
    continue;
}
```

This is the same idea you used in **Combination Sum II**.

### Hint 4: Why `i > start`?

You want to skip this duplicate branch:

```text
[]
├── choose first 2
└── choose second 2   <- duplicate starting choice
```

But you still want to allow:

```text
[2]
└── choose second 2
    -> [2,2]
```

So duplicate values are allowed deeper in the recursion, just not as duplicate choices at the same level.

### Skeleton

```cpp
void backtrack(vector<int>& nums,
               int start,
               vector<int>& current,
               vector<vector<int>>& result) {

    result.push_back(current);

    for (int i = start; i < nums.size(); i++) {

        if (i > start && nums[i] == nums[i - 1]) {
            continue;
        }

        current.push_back(nums[i]);

        // recurse

        current.pop_back();
    }
}
```

Your recursive call should start from `i + 1`, since each individual element can only be used once.

For `[1,2,2]`, you should end up with:

```text
[]
[1]
[1,2]
[1,2,2]
[2]
[2,2]
```

The core rule to remember is: **sort first, then skip equal values at the same recursion depth.**
*/
class Solution {
private:
    void backtrack(vector<int>& nums, int start, vector<int>& current, vector<vector<int>>& result){
        result.push_back(current);

        for(int i = start; i<nums.size(); i++){
            if(i > start && nums[i] == nums[i-1]){
                continue;
            }

            current.push_back(nums[i]);

            backtrack(nums, i + 1, current, result);

            current.pop_back();
        }
    }

public:
    vector<vector<int>> subsetsWithDup(vector<int>& nums) {
        std::sort(nums.begin(), nums.end());

        vector<int> current;
        vector<vector<int>> result;

        backtrack(nums, 0, current, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna