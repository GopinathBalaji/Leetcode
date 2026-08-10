// Method 1: Backtracking using loop instead of include, exclude to explore all possibilities without including duplicates
/*
### Hint 1: This is another backtracking problem

You need to generate all combinations of `k` numbers chosen from:

```text
1, 2, 3, ..., n
```

A useful recursive state is:

```cpp
(start, currentCombination)
```

`start` tells you the smallest number you're allowed to choose next.

### Hint 2: Use a loop at each recursion level

Instead of explicit include/exclude branches, try:

```cpp
for (int i = start; i <= n; i++) {
    // choose i
    // recurse
    // undo choice
}
```

This is very similar to the cleaner approach you used for Combination Sum II.

### Hint 3: When have you found a valid answer?

You don't need a `remaining` target here.

Your base case can simply check:

```cpp
if (current.size() == k) {
    // save current
    return;
}
```

### Hint 4: After choosing `i`

Do:

```cpp
current.push_back(i);
```

Then recurse starting from:

```cpp
i + 1
```

Why `i + 1`?

Because each number can only be chosen once, and combinations like:

```text
[1, 2]
[2, 1]
```

should not both appear.

Then backtrack:

```cpp
current.pop_back();
```

### Hint 5: Skeleton

```cpp
void backtrack(int n,
               int k,
               int start,
               vector<int>& current,
               vector<vector<int>>& result) {

    if (current.size() == k) {
        // save current
        return;
    }

    for (int i = start; i <= n; i++) {

        // choose i

        // recurse with i + 1

        // undo choice
    }
}
```

For:

```text
n = 4, k = 2
```

you should generate:

```text
[1,2]
[1,3]
[1,4]
[2,3]
[2,4]
[3,4]
```

### Bonus hint: pruning

Once the basic solution works, you can avoid exploring branches where there aren't enough numbers left to reach size `k`.

Ask yourself:

> If I still need `k - current.size()` numbers, how far does `i` actually need to loop?

But get the straightforward backtracking solution working first.
*/
class Solution {
private:
    void backtrack(int n, int k, int index, vector<int>& current, vector<vector<int>>& result){
        if(current.size() == k){
            result.push_back(current);
            return;
        }

        for(int i = index; i <= n; ++i){
            current.push_back(i);

            backtrack(n, k, i + 1, current, result);

            current.pop_back();
        }        
    }

public:
    vector<vector<int>> combine(int n, int k) {
        vector<int> current;
        vector<vector<int>> result;

        backtrack(n, k, 1, current, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna