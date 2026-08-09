// Method 1: Include, Exclude backtracking approach
/*
### Hint 1: This is backtracking, like Subsets — with one twist

At each candidate, you have two broad choices:

* skip it
* take it

But unlike LeetCode 78, you’re allowed to take the **same number multiple times**.

So if you choose `candidates[index]`, think carefully about whether `index` should increase.

### Hint 2: Track a running combination and remaining target

A useful recursive state is:

```cpp
(index, remainingTarget, currentCombination)
```

If you take a candidate:

```cpp
remainingTarget -= candidates[index];
```

If you skip it, move to the next index.

### Hint 3: Base cases matter

You’ve found a valid combination when:

```cpp
remainingTarget == 0
```

At that point, save `currentCombination`.

You should stop exploring a branch when:

```cpp
remainingTarget < 0
```

or when:

```cpp
index == candidates.size()
```

### Hint 4: Reusing the same candidate

Suppose you choose:

```cpp
candidates[index]
```

Then recurse with the **same `index`**:

```cpp
backtrack(index, remainingTarget - candidates[index])
```

Why? Because that candidate can be used again.

If instead you skip it:

```cpp
backtrack(index + 1, remainingTarget)
```

### Hint 5: Don’t forget to undo your choice

Typical backtracking pattern:

```cpp
current.push_back(candidates[index]);

// recurse

current.pop_back();
```

That `pop_back()` restores the state before exploring the next branch.

### Skeleton

```cpp
void backtrack(vector<int>& candidates,
               int index,
               int remaining,
               vector<int>& current,
               vector<vector<int>>& result) {

    if (remaining == 0) {
        // save current
        return;
    }

    if (remaining < 0 || index == candidates.size()) {
        return;
    }

    // Take candidates[index]
    // Important: stay at same index

    // Skip candidates[index]
    // Move to index + 1
}
```

The biggest conceptual difference from **Subsets** is:

```text
Subsets:
take → index + 1

Combination Sum:
take → same index
```

because reuse is allowed.
*/
class Solution {
private:
    void backtrack(vector<int>& candidates, int index, int remaining, vector<int>& current, vector<vector<int>>& result){
        if(remaining == 0){
            result.push_back(current);
            return;
        }
        if(remaining < 0 || index == candidates.size()){
            return;
        }

        current.push_back(candidates[index]);
        backtrack(candidates, index, remaining - candidates[index], current, result);
        current.pop_back();

        backtrack(candidates, index + 1, remaining, current, result);
    }

public:
    vector<vector<int>> combinationSum(vector<int>& candidates, int target) {
        vector<int> current;
        vector<vector<int>> result;
        
        backtrack(candidates, 0, target, current, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna