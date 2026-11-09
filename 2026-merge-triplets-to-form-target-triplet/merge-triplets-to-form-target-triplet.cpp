// Method 1: Greedy
/*
## LeetCode 1899: Merge Triplets to Form Target Triplet (C++)

**Approach: Greedy | Difficulty: Medium**

### Hint 1: Understand the Merge Operation

You can merge two triplets by taking the **maximum of each corresponding element**.

Example:

```cpp
[1, 3, 2] + [2, 1, 4] = [2, 3, 4]
```

**Goal:** Determine whether you can form the exact `target` triplet.

### Hint 2: Ignore Invalid Triplets

If any element of a triplet exceeds the corresponding target element, that triplet cannot be used.

```cpp
if (t[0] > target[0] ||
    t[1] > target[1] ||
    t[2] > target[2])
    continue;
```

**Why?** Merging only increases or maintains values, never decreases them.

### Hint 3: Track Matching Elements

For each valid triplet, check which target positions it matches.

```cpp
vector<bool> found(3, false);

for (int i = 0; i < 3; i++) {
    if (t[i] == target[i])
        found[i] = true;
}
```

### Hint 4: Check All Three Positions

You can form the target if all three positions have been matched by valid triplets.

```cpp
return found[0] && found[1] && found[2];
```

---

### Complete C++ Solution

```cpp
class Solution {
public:
    bool mergeTriplets(vector<vector<int>>& triplets,
                       vector<int>& target) {
        vector<bool> found(3, false);

        for (auto& t : triplets) {
            if (t[0] > target[0] ||
                t[1] > target[1] ||
                t[2] > target[2])
                continue;

            for (int i = 0; i < 3; i++) {
                if (t[i] == target[i])
                    found[i] = true;
            }
        }

        return found[0] && found[1] && found[2];
    }
};
```

### Complexity

- **Time:** O(n) — Traverse each triplet once.
- **Space:** O(1) — Only three boolean values.

### Example

```cpp
triplets = [[2, 5, 3], [1, 8, 4], [2, 3, 4]]
target = [2, 5, 4]
```

**Step 1:** `[2, 5, 3]` is valid.
- Matches target positions 0 and 1.

**Step 2:** `[1, 8, 4]` is invalid.
- `8 > 5`, so skip it.

**Step 3:** `[2, 3, 4]` is valid.
- Matches target positions 0 and 2.

Merge the valid triplets:

```cpp
[2, 5, 3] + [2, 3, 4] = [2, 5, 4]
```

**Answer: `true`**

### Key Intuition

**Greedy + Element Matching = O(n)**

Two important observations:

1. Never use a triplet containing an element greater than the target.
2. Every target position must be matched by at least one valid triplet.

**Interview tip:** When an operation uses element-wise maximum, think about which elements could **overshoot the target** and eliminate those candidates first.
*/
class Solution {
public:
    bool mergeTriplets(vector<vector<int>>& triplets, vector<int>& target) {
        vector<bool> found(3, false);

        for(auto& t : triplets){
            if(t[0] > target[0] || t[1] > target[1] || t[2] > target[2]){
                continue;
            }

            for(int i=0; i<3; i++){
                if(t[i] == target[i]){
                    found[i] = true;
                }
            }
        }

        return found[0] && found[1] && found[2];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna