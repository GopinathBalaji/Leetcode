// Method 1: Greedy
/*
## LeetCode 135: Candy (C++)

Approach: Greedy + Two Passes | Difficulty: Hard

### Hint 1: Think Greedy

Each child must receive:

- At least 1 candy.
- More candies than an adjacent child if their rating is higher.

Key idea: Handle the left and right neighbors separately using two passes.

### Hint 2: Initialize Candies

Give every child 1 candy initially.

```
int n = ratings.size();
vector<int> candy(n, 1);
```

### Hint 3: Left-to-Right Pass

If the current child's rating is higher than the left neighbor's, give one more candy.

```
for (int i = 1; i < n; i++) {
    if (ratings[i] > ratings[i - 1])
        candy[i] = candy[i - 1] + 1;
}
```

### Hint 4: Right-to-Left Pass

Now ensure children with higher ratings than their right neighbors also receive more candies.

```
for (int i = n - 2; i >= 0; i--) {
    if (ratings[i] > ratings[i + 1])
        candy[i] = max(candy[i], candy[i + 1] + 1);
}
```

Why `max()`? To preserve the requirements already satisfied during the first pass.

### Hint 5: Calculate Total Candies

```
return accumulate(candy.begin(), candy.end(), 0);
```

### Complete C++ Solution

```
class Solution {
public:
    int candy(vector<int>& ratings) {
        int n = ratings.size();
        vector<int> candy(n, 1);

        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1])
                candy[i] = candy[i - 1] + 1;
        }

        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1])
                candy[i] = max(candy[i], candy[i + 1] + 1);
        }

        return accumulate(candy.begin(), candy.end(), 0);
    }
};
```

### Complexity

- Time: O(n) — Two passes and one summation.
- Space: O(n) — Candy array.

### Example

```
ratings = [1, 0, 2]
```

CANDY DISTRIBUTION

Child 0

## 2 🍬

Rating: 1

Child 1

## 1 🍬

Rating: 0

Child 2

## 2 🍬

Rating: 2

Minimum total candies

# 5

- Initial candies: `[1, 1, 1]`
- Left-to-right: `[1, 1, 2]`
- Right-to-left: `[2, 1, 2]`

Answer: `5`

### Key Intuition

Greedy + Two Passes = O(n)

- First pass satisfies higher ratings relative to left neighbors.
- Second pass satisfies higher ratings relative to right neighbors.
- Using `max()` ensures neither condition is violated.

Interview tip: When a problem has constraints involving both left and right neighbors, consider solving each direction independently using two greedy passes.
*/
class Solution {
public:
    int candy(vector<int>& ratings) {
        int n = ratings.size();
        
        vector<int> candy(n, 1);

        for(int i=1; i<n; i++){
            if(ratings[i] > ratings[i-1]){
                candy[i] = candy[i-1] + 1;
            }
        }

        for(int i = n-2; i >= 0; i--){
            if(ratings[i] > ratings[i + 1]){
                candy[i] = std::max(candy[i], candy[i + 1] + 1);
            }
        }

        return std::accumulate(candy.begin(), candy.end(), 0);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna