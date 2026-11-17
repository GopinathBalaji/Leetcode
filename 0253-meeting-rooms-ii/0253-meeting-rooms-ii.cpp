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

// Method 1: Sorting + Two Pointer
/*
## **NeetCode: Meeting Rooms II (C++)**

**Approach: Sorting + Two Pointers | Difficulty: Medium**

### **Hint 1: Think Greedy**

Find the **minimum number of meeting rooms** required so that no meetings overlap.

**Key idea:** The answer equals the maximum number of meetings happening simultaneously.

### **Hint 2: Sort Start and End Times**

Create two separate sorted arrays.

```
vector<int> start, end;

for (auto& interval : intervals) {
    start.push_back(interval.start);
    end.push_back(interval.end);
}

sort(start.begin(), start.end());
sort(end.begin(), end.end());
```

### **Hint 3: Use Two Pointers**

Track:

- `s`: Next meeting starting.
- `e`: Next meeting ending.
- `rooms`: Currently occupied rooms.
- `maxRooms`: Maximum rooms needed.

### **Hint 4: Compare Start and End Times**

```
if (start[s] < end[e]) {
    rooms++;
    s++;
} else {
    rooms--;
    e++;
}

maxRooms = max(maxRooms, rooms);
```

**Important:** If `start[s] == end[e]`, reuse the room because the previous meeting has ended.

### **Complete C++ Solution**

```
class Solution {
public:
    int minMeetingRooms(vector<Interval>& intervals) {
        int n = intervals.size();

        vector<int> start, end;

        for (auto& interval : intervals) {
            start.push_back(interval.start);
            end.push_back(interval.end);
        }

        sort(start.begin(), start.end());
        sort(end.begin(), end.end());

        int s = 0, e = 0;
        int rooms = 0, maxRooms = 0;

        while (s < n) {
            if (start[s] < end[e]) {
                rooms++;
                s++;
            } else {
                rooms--;
                e++;
            }

            maxRooms = max(maxRooms, rooms);
        }

        return maxRooms;
    }
};
```

### **Complexity**

- **Time:** O(n log n) — Sorting dominates.
- **Space:** O(n) — Two arrays.

### **Example**

```
intervals = [(0, 40), (5, 10), (15, 20)]
```

Sorted arrays:

```
start = [0, 5, 15]
end   = [10, 20, 40]
```

| Event       | Active rooms | Maximum |
| ----------- | ------------ | ------- |
| Start at 0  | 1            | 1       |
| Start at 5  | 2            | 2       |
| End at 10   | 1            | 2       |
| Start at 15 | 2            | 2       |

**Answer:&#x20;**`2`

### **Key Intuition**

**Sorting + Two Pointers = O(n log n)**

Treat meeting starts as occupying rooms and meeting ends as freeing rooms. Track the maximum number of occupied rooms.

**Interview tip:** Remember the difference:

- **Meeting Rooms I:** Check whether any meetings overlap.
- **Meeting Rooms II:** Find the maximum number of simultaneous meetings.
- **435. Non-overlapping Intervals:** Remove the fewest intervals to eliminate overlaps.

An alternative solution uses a **min-heap of meeting end times**, also achieving O(n log n).
*/
class Solution {
public:
    int minMeetingRooms(vector<Interval>& intervals) {
      int n = intervals.size();

      vector<int> start, end;

      for(auto& interval : intervals){
        start.push_back(interval.start);
        end.push_back(interval.end);
      }

      std::sort(start.begin(), start.end());
      std::sort(end.begin(), end.end());

      int nextStart = 0;
      int nextEnd = 0;
      int occupiedRooms = 0;
      int maxRooms = 0;

      while(nextStart < n){
        if(start[nextStart] < end[nextEnd]){
          occupiedRooms++;
          nextStart++;
        }else{
          occupiedRooms--;
          nextEnd++;
        }

        maxRooms = std::max(maxRooms, occupiedRooms);
      }

      return maxRooms;
    }
};
