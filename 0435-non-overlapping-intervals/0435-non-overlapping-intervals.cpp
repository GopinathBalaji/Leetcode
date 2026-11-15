// Method 1: Greedy + Sorting
/*
## **LeetCode 435: Non-overlapping Intervals (C++)**

**Approach: Greedy + Sorting | Difficulty: Medium**

### **Hint 1: Think Greedy**

Find the **minimum number of intervals to remove** so the remaining intervals don't overlap.

**Key idea:** Keep intervals that end earliest, leaving maximum room for future intervals.

### **Hint 2: Sort by Ending Time**

Unlike Merge Intervals (56), sort by the **end** of each interval.

```
sort(intervals.begin(), intervals.end(),
     [](const vector<int>& a, const vector<int>& b) {
         return a[1] < b[1];
     });
```

### **Hint 3: Track the Last Ending Position**

Initialize the end of the first interval.

```
int prevEnd = intervals[0][1];
int removed = 0;
```

### **Hint 4: Detect Overlapping Intervals**

If the current interval starts before `prevEnd`, it overlaps.

```
if (intervals[i][0] < prevEnd) {
    removed++;
}
```

**Why not&#x20;**`<=`**?** Intervals like `[1,2]` and `[2,3]` are considered non-overlapping.

### **Hint 5: Update When No Overlap**

If the current interval doesn't overlap, keep it and update `prevEnd`.

```
else {
    prevEnd = intervals[i][1];
}
```

### **Complete C++ Solution**

```
class Solution {
public:
    int eraseOverlapIntervals(vector<vector<int>>& intervals) {
        sort(intervals.begin(), intervals.end(),
             [](const vector<int>& a, const vector<int>& b) {
                 return a[1] < b[1];
             });

        int removed = 0;
        int prevEnd = intervals[0][1];

        for (int i = 1; i < intervals.size(); i++) {
            if (intervals[i][0] < prevEnd) {
                removed++;
            } else {
                prevEnd = intervals[i][1];
            }
        }

        return removed;
    }
};
```

### **Complexity**

- **Time:** O(n log n) — Sorting dominates.
- **Space:** O(log n) — Typical auxiliary space for sorting.

### **Example**

```
intervals = {{1, 2}, {2, 3}, {3, 4}, {1, 3}};
```

After sorting by ending time:

`[1,2], [2,3], [1,3], [3,4]`

- Keep `[1,2]`.
- Keep `[2,3]` — no overlap.
- Remove `[1,3]` — overlaps.
- Keep `[3,4]` — no overlap.

**Answer:&#x20;**`1`

### **Key Intuition**

**Greedy + Earliest Finish Time = O(n log n)**

By keeping the interval that finishes earliest, you maximize the number of non-overlapping intervals.

Therefore:

\\[ \text{Minimum Removals} = n - \text{Maximum Intervals Kept} \\]

**Interview tip:** Remember the difference:

- **56. Merge Intervals:** Sort by start time and merge overlaps.
- **435. Non-overlapping Intervals:** Sort by end time and discard overlaps.
- **452. Minimum Arrows:** Similar greedy strategy using ending times.
*/
class Solution {
public:
    int eraseOverlapIntervals(vector<vector<int>>& intervals) {
        std::sort(intervals.begin(), intervals.end(), [](const vector<int>& a, const vector<int>& b){
            return a[1] < b[1];
        });

        int prevEnd = intervals[0][1];
        int removed = 0;

        for(int i=1; i<intervals.size(); i++){
            if(intervals[i][0] < prevEnd){
                removed++;
            }else{
                prevEnd = intervals[i][1];
            }
        }

        return removed;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna