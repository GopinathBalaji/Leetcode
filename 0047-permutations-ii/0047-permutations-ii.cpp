// Method 1: Backtracking with duplicate handling
/*
This is essentially **LeetCode 46: Permutations**, plus the duplicate-handling idea from **Subsets II / Combination Sum II**.

### Hint 1: Start with the same structure as Permutations

You still want:

```cpp
vector<int> current;
vector<bool> used(nums.size(), false);
```

At each recursion level, try every index:

```cpp
for (int i = 0; i < nums.size(); i++) {
    if (used[i]) {
        continue;
    }

    // choose nums[i]
}
```

And when:

```cpp
current.size() == nums.size()
```

you've built one complete permutation.

### Hint 2: Sort the array first

If:

```text
nums = [1, 1, 2]
```

sorting puts equal values next to each other:

```cpp
sort(nums.begin(), nums.end());
```

Now you can detect when you're about to create an identical branch.

### Hint 3: Don't just use this rule

You might think:

```cpp
if (i > 0 && nums[i] == nums[i - 1]) {
    continue;
}
```

But that's **too aggressive**.

It would prevent you from ever using both `1`s in:

```text
[1,1,2]
```

You need to care about whether the **previous duplicate has already been used in the current permutation**.

### Hint 4: The key condition

Inside your loop, think about:

```cpp
if (i > 0 &&
    nums[i] == nums[i - 1] &&
    !used[i - 1]) {
    continue;
}
```

This is the core of the problem.

Interpret it as:

> If this number is the same as the previous number, don't choose it before the previous identical copy has been chosen.

That forces duplicate values to be chosen in a consistent order and prevents duplicate permutation branches.

### Hint 5: Why does that work?

For:

```text
[1a, 1b, 2]
```

at the top level, you allow:

```text
choose 1a
```

but prevent:

```text
choose 1b
```

because `1a` hasn't been used yet.

Otherwise these two branches:

```text
1a → ...
1b → ...
```

would produce identical permutations since `1a` and `1b` have the same value.

But after choosing `1a`, choosing `1b` deeper is perfectly valid:

```text
1a → 1b → 2
```

### Hint 6: Skeleton

```cpp
void backtrack(vector<int>& nums,
               vector<int>& current,
               vector<bool>& used,
               vector<vector<int>>& result) {

    if (current.size() == nums.size()) {
        // save current
        return;
    }

    for (int i = 0; i < nums.size(); i++) {

        if (used[i]) {
            continue;
        }

        // Skip duplicate branch
        if (i > 0 &&
            nums[i] == nums[i - 1] &&
            !used[i - 1]) {
            continue;
        }

        // choose
        used[i] = true;
        current.push_back(nums[i]);

        // recurse

        // undo
        current.pop_back();
        used[i] = false;
    }
}
```

The most important difference to remember is:

```text
LC 46:
skip only if used[i]

LC 47:
skip if used[i]
OR
it's a duplicate whose previous copy hasn't been used
```

That duplicate condition is the whole trick.
*/
class Solution {
private:
    void backtrack(vector<int>& nums, vector<int>& current, vector<vector<int>>& result, vector<bool>& used){
        if(current.size() == nums.size()){
            result.push_back(current);
            return;
        }
        
        for(int i=0; i<nums.size(); i++){
            if(used[i]){
                continue;
            }

            if(i > 0 && nums[i] == nums[i - 1] && !used[i - 1]){
                continue;
            }

            used[i] = true;
            current.push_back(nums[i]);

            backtrack(nums, current, result, used);

            current.pop_back();
            used[i] = false;
        }
    }

public:
    vector<vector<int>> permuteUnique(vector<int>& nums) {
        std::sort(nums.begin(), nums.end());

        vector<int> current;
        vector<vector<int>> result;
        vector<bool> used(nums.size(), false);

        backtrack(nums, current, result, used);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna