// Method 1: Sorting + Greedy
/*
## LeetCode 56: Merge Intervals (C++)

Approach: Sorting + Greedy | Difficulty: Medium

### Hint 1: Sort Intervals

Unlike Insert Interval (57), the intervals are not necessarily sorted.

Sort them by their starting positions.

```
sort(intervals.begin(), intervals.end());
```

### Hint 2: Initialize the Result

Start with the first interval.

```
vector<vector<int>> result;
result.push_back(intervals[0]);
```

### Hint 3: Check for Overlapping Intervals

Two sorted intervals overlap when:

```
intervals[i][0] <= result.back()[1]
```

Why? The current interval starts before or exactly when the previous merged interval ends.

### Hint 4: Merge Overlapping Intervals

If intervals overlap, extend the ending position.

```
result.back()[1] = max(result.back()[1], intervals[i][1]);
```

### Hint 5: Handle Non-Overlapping Intervals

If they don't overlap, add the current interval as a new group.

```
else {
    result.push_back(intervals[i]);
}
```

### Complete C++ Solution

```
class Solution {
public:
    vector<vector<int>> merge(vector<vector<int>>& intervals) {
        sort(intervals.begin(), intervals.end());

        vector<vector<int>> result;

        for (auto& interval : intervals) {
            if (result.empty() || interval[0] > result.back()[1]) {
                result.push_back(interval);
            } else {
                result.back()[1] = max(result.back()[1], interval[1]);
            }
        }

        return result;
    }
};
```

### Complexity

- Time: O(n log n) — Sorting dominates.
- Space: O(n) — Result array, excluding sorting overhead.

### Example

```
intervals = {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
```

INTERVAL MERGING VISUALIZATION

Overlapping

Merged

Unchanged

- `[1,3]` and `[2,6]` overlap → `[1,6]`
- `[8,10]` doesn't overlap → Keep.
- `[15,18]` doesn't overlap → Keep.

Answer: `[[1,6],[8,10],[15,18]]`

### Key Intuition

Sorting + Greedy = O(n log n)

Once intervals are sorted by start time, you only need to compare each interval with the last merged interval.

Interview tip: Remember the difference:

- 56\. Merge Intervals: Sort first, then merge.
- 57\. Insert Interval: Already sorted; insert and merge in O(n).
*/
class Solution {
public:
    vector<vector<int>> merge(vector<vector<int>>& intervals) {
        std::sort(intervals.begin(), intervals.end());

        vector<vector<int>> result;
        result.push_back(intervals[0]);

        for(auto& interval : intervals){
            if(result.empty() || interval[0] > result.back()[1]){
                result.push_back(interval);
            }else{
                result.back()[1] = std::max(result.back()[1], interval[1]);
            }
        }

        return result;   
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna