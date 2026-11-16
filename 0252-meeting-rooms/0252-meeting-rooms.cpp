/**
 * Definition of Interval:
 * class Interval {
 * public:
 *     int start, end;
 *     Interval(int start, int end) {
 *         this->start = start;
 *         this->end = end;
 *     }
 * }
 */

// Method 1: Greedy + Sorting
/*
## **NeetCode: Meeting Rooms (C++)**

**Approach: Sorting + Greedy | Difficulty: Easy**

### **Hint 1: Sort by Start Time**

Sort meetings in ascending order of their starting times.

```
sort(intervals.begin(), intervals.end(),
     [](const Interval& a, const Interval& b) {
         return a.start < b.start;
     });
```

### **Hint 2: Check Adjacent Meetings**

After sorting, you only need to compare consecutive meetings.

Two meetings overlap if:

```
intervals[i].start < intervals[i - 1].end
```

**Important:** If one meeting ends exactly when another starts, there's no conflict.

### **Hint 3: Detect Conflicts**

Return `false` immediately if two meetings overlap.

```
for (int i = 1; i < intervals.size(); i++) {
    if (intervals[i].start < intervals[i - 1].end)
        return false;
}
```

Otherwise, return `true`.

### **Complete C++ Solution**

```
class Solution {
public:
    bool canAttendMeetings(vector<Interval>& intervals) {
        sort(intervals.begin(), intervals.end(),
             [](const Interval& a, const Interval& b) {
                 return a.start < b.start;
             });

        for (int i = 1; i < intervals.size(); i++) {
            if (intervals[i].start < intervals[i - 1].end)
                return false;
        }

        return true;
    }
};
```

### **Complexity**

- **Time:** O(n log n) — Sorting dominates.
- **Space:** O(log n) — Typical auxiliary sorting stack.

### **Example**

```
intervals = [(0, 30), (5, 10), (15, 20)]
```

After sorting:

- `(0,30)` and `(5,10)` overlap because `5 < 30`.
- Return `false`.

**Answer:&#x20;**`false`

### **Key Intuition**

**Sorting + Adjacent Comparison = O(n log n)**

Once intervals are sorted by start time, any conflict can be detected by comparing neighboring intervals.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://neetcode.io\&sz=32)

NeetCode



**Interview tip:** Remember the difference:

- **Meeting Rooms I:** Determine whether any meetings overlap.
- **Meeting Rooms II:** Find the minimum number of rooms required.
- **Non-overlapping Intervals (435):** Find the minimum number of intervals to remove.
*/
class Solution {
public:
    bool canAttendMeetings(vector<Interval>& intervals) {
        sort(intervals.begin(), intervals.end(),
             [](const Interval& a, const Interval& b) {
                 return a.start < b.start;
             });

        for (int i = 1; i < intervals.size(); i++) {
            if (intervals[i].start < intervals[i - 1].end)
                return false;
        }

        return true;
    }
};
