// Method 1: Include, Exclude Backtracking with duplicate skipping using sorting
/*
### Backtracking pattern

Instead of generating duplicates and then using a `set` to clean them up, you can avoid generating duplicates in the first place.

The important observation is:

```text
At the same recursion level,
don't start a combination with the same value twice.
```

So after sorting, you can use:

```cpp
for (int i = index; i < candidates.size(); i++) {
    if (i > index && candidates[i] == candidates[i - 1]) {
        continue;
    }

    // choose candidates[i]
}
```

The condition:

```cpp
i > index && candidates[i] == candidates[i - 1]
```

is the key idea behind **Combination Sum II**.
*/
class Solution {
private:
    void backtrack(vector<int>& candidates, int remaining, int index, vector<int>& current, vector<vector<int>>& result){
        if(remaining == 0){
            result.push_back(current);
            return;
        }

        for(int i=index; i<candidates.size(); i++){
            if(i > index && candidates[i] == candidates[i - 1]){
                continue;
            }

            if(candidates[i] > remaining){
                break;
            }

            current.push_back(candidates[i]);
            backtrack(candidates, remaining - candidates[i], i + 1, current, result);
            current.pop_back();
        }
    }

public:
    vector<vector<int>> combinationSum2(vector<int>& candidates, int target) {
        vector<vector<int>> result;
        vector<int> current;

        std::sort(candidates.begin(), candidates.end());

        backtrack(candidates, target, 0, current, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna