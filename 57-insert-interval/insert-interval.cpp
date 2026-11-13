// Method 1: Greedy
/*
## LeetCode 57: Insert Interval (C++)

Approach: Greedy + Interval Merging | Difficulty: Medium

### Hint 1: Think Greedy

You're given sorted, non-overlapping intervals and a new interval to insert.

Key idea: Divide the existing intervals into three groups:

1. Intervals completely before `newInterval`.
2. Intervals overlapping `newInterval`.
3. Intervals completely after `newInterval`.

### Hint 2: Add Intervals Before

If an interval ends before the new interval starts, add it directly.

```
while (i < n && intervals[i][1] < newInterval[0]) {
    result.push_back(intervals[i]);
    i++;
}
```

### Hint 3: Merge Overlapping Intervals

An interval overlaps if its start is less than or equal to the current merged interval's end.

```
while (i < n && intervals[i][0] <= newInterval[1]) {
    newInterval[0] = min(newInterval[0], intervals[i][0]);
    newInterval[1] = max(newInterval[1], intervals[i][1]);
    i++;
}
```

### Hint 4: Insert the Merged Interval

```
result.push_back(newInterval);
```

### Hint 5: Add Remaining Intervals

All remaining intervals occur after the merged interval.

```
while (i < n) {
    result.push_back(intervals[i]);
    i++;
}
```

### Complete C++ Solution

```
class Solution {
public:
    vector<vector<int>> insert(vector<vector<int>>& intervals,
                               vector<int>& newInterval) {
        vector<vector<int>> result;
        int n = intervals.size();
        int i = 0;

        while (i < n && intervals[i][1] < newInterval[0]) {
            result.push_back(intervals[i]);
            i++;
        }

        while (i < n && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = min(newInterval[0], intervals[i][0]);
            newInterval[1] = max(newInterval[1], intervals[i][1]);
            i++;
        }

        result.push_back(newInterval);

        while (i < n) {
            result.push_back(intervals[i]);
            i++;
        }

        return result;
    }
};
```

### Complexity

- Time: O(n) — Each interval is processed once.
- Space: O(n) — Output array; O(1) auxiliary space excluding output.

### Example

```
intervals = {{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}};
newInterval = {4, 8};
```

INTERVAL MERGING VISUALIZATION

Unchanged

New interval

Merged

- Keep `[1,2]`.
- Merge `[3,5]`, `[6,7]`, and `[8,10]` with `[4,8]` → `[3,10]`.
- Keep `[12,16]`.

Answer: `[[1,2],[3,10],[12,16]]`

### Key Intuition

Greedy + Three Phases = O(n)

Since intervals are already sorted, you don't need to sort again.

Interview tip: For interval problems, remember the overlap condition:

```
a[0] <= b[1] && b[0] <= a[1]
```

If two intervals overlap, merge them using `min(start)` and `max(end)`.
*/
class Solution {
public:
    vector<vector<int>> insert(vector<vector<int>>& intervals, vector<int>& newInterval) {
        vector<vector<int>> result;
        int n = intervals.size();

        int i=0;

        while(i < n && intervals[i][1] < newInterval[0]){
            result.push_back(intervals[i]);
            i++;
        }

        while(i < n && intervals[i][0] <= newInterval[1]){
            newInterval[0] = std::min(newInterval[0], intervals[i][0]);
            newInterval[1] = std::max(newInterval[1], intervals[i][1]);
            i++;
        }

        result.push_back(newInterval);

        while(i < n){
            result.push_back(intervals[i]);
            i++;
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna