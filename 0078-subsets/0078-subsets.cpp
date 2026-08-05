// Method 1: Rescursion include/exclude approach
/*
### Hint 1: Reuse the same include/exclude recursion

At each `index`, make two choices:

* Do not include `nums[index]`
* Include `nums[index]`

This creates every possible subset.

### Hint 2: Track the current subset

Instead of tracking `currentXor`, keep a temporary vector:

```cpp
vector<int> current;
```

When you include a number:

```cpp
current.push_back(nums[index]);
```

After finishing that recursive branch, undo the choice:

```cpp
current.pop_back();
```

This is called **backtracking**.

### Hint 3: Base case

Once `index == nums.size()`, you have completed one valid subset. Add a copy of `current` to the answer:

```cpp
result.push_back(current);
```

### Hint 4: Skeleton

```cpp
void generate(vector<int>& nums, int index,
              vector<int>& current,
              vector<vector<int>>& result) {
    if (index == nums.size()) {
        // Save the completed subset
        return;
    }

    // Exclude nums[index]

    // Include nums[index]
}
```

For `[1, 2]`, your recursion should generate:

```text
[]
[2]
[1]
[1, 2]
```

The ordering may differ, which is fine. Time complexity is `O(n × 2^n)` because there are `2^n` subsets and copying each subset can take up to `O(n)`.
*/
class Solution {
private:
    void generate(vector<int>& nums, int index, vector<int>& current, vector<vector<int>>& result){
        if(index == nums.size()){
            result.push_back(current);
            return;
        }

        generate(nums, index + 1, current, result);

        current.push_back(nums[index]);
        generate(nums, index + 1, current, result);

        current.pop_back();

        return;
    }

public:
    vector<vector<int>> subsets(vector<int>& nums) {
        vector<vector<int>> result;
        vector<int> current;

        generate(nums, 0, current, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna