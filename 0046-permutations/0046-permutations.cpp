// Method 1: Backtracking while also considering order
/*
### Hint 1: This is backtracking, but order matters

For `nums = [1,2,3]`, these are different answers:

```text
[1,2,3]
[1,3,2]
[2,1,3]
```

So unlike **Combinations**, you should not use a `start` index that only moves forward.

At every position, you can choose **any number that has not already been used**.

### Hint 2: Track which numbers are already used

A useful recursive state is:

```cpp
current
used
```

For example:

```cpp
vector<int> current;
vector<bool> used(nums.size(), false);
```

When considering `nums[i]`:

```cpp
if (used[i]) {
    continue;
}
```

### Hint 3: Choose → recurse → undo

The backtracking pattern becomes:

```cpp
current.push_back(nums[i]);
used[i] = true;

// recurse

used[i] = false;
current.pop_back();
```

The undoing is important because the number must become available again for other permutations.

### Hint 4: Base case

A permutation is complete when:

```cpp
current.size() == nums.size()
```

At that point:

```cpp
result.push_back(current);
```

### Hint 5: Skeleton

```cpp
void backtrack(vector<int>& nums,
               vector<int>& current,
               vector<bool>& used,
               vector<vector<int>>& result) {

    if (current.size() == nums.size()) {
        // save permutation
        return;
    }

    for (int i = 0; i < nums.size(); i++) {

        if (used[i]) {
            continue;
        }

        // choose nums[i]

        // recurse

        // undo
    }
}
```

The key difference from LeetCode 77 is:

```text
Combinations:
after choosing i, only consider numbers after i

Permutations:
after choosing something, consider ALL numbers again,
except the ones already used
```

For `[1,2,3]`, the recursion begins like:

```text
[]
├── 1
│   ├── 2
│   │   └── 3  → [1,2,3]
│   └── 3
│       └── 2  → [1,3,2]
├── 2
│   ...
└── 3
    ...
```

That mental model is the main idea for this problem.
*/
class Solution {
private:
    void backtrack(vector<int>& nums, vector<int>& current, vector<bool>& used, vector<vector<int>>& result){
        if(current.size() == nums.size()){
            result.push_back(current);
            return;
        }

        for(int i=0; i<nums.size(); i++){
            if(used[i]){
                continue;
            }

            current.push_back(nums[i]);
            used[i] = true;

            backtrack(nums, current, used, result);

            current.pop_back();
            used[i] = false;
        }
    }

public:
    vector<vector<int>> permute(vector<int>& nums) {
        vector<int> current;
        vector<bool> used(nums.size(), false);
        vector<vector<int>> result;

        backtrack(nums, current, used, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna