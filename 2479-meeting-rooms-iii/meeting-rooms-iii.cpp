// Method 1: Greedy + 2 Min-Heaps
/*
## **LeetCode 2402: Meeting Rooms III (C++)**

**Approach: Greedy + Two Min-Heaps | Difficulty: Hard**

### **Hint 1: Sort Meetings by Start Time**

Process meetings in chronological order.

```
sort(meetings.begin(), meetings.end());
```

**Key idea:** Always assign the smallest available room number. If no room is available, delay the meeting.

### **Hint 2: Use Two Min-Heaps**

Maintain two priority queues:

- `available`: Smallest available room number.
- `busy`: Pairs of `{endTime, roomNumber}`, ordered by earliest ending time, then smallest room number.

```
priority_queue<int, vector<int>, greater<int>> available;

using P = pair<long long, int>;
priority_queue<P, vector<P>, greater<P>> busy;
```

### **Hint 3: Free Rooms Before Each Meeting**

Before scheduling a meeting, release all rooms whose meetings have ended.

```
while (!busy.empty() && busy.top().first <= start) {
    available.push(busy.top().second);
    busy.pop();
}
```

### **Hint 4: Assign or Delay the Meeting**

If a room is available, select the smallest room number.

Otherwise, delay the meeting until the earliest occupied room becomes available.

```
if (!available.empty()) {
    room = available.top();
    available.pop();
    endTime = end;
} else {
    auto [freeTime, r] = busy.top();
    busy.pop();

    room = r;
    endTime = freeTime + (end - start);
}
```

**Important:** Delayed meetings must preserve their original duration.

### **Hint 5: Count Meetings per Room**

```
count[room]++;
busy.push({endTime, room});
```

Return the room with the highest meeting count. If tied, choose the smallest room number.

### **Complete C++ Solution**

```
class Solution {
public:
    int mostBooked(int n, vector<vector<int>>& meetings) {
        sort(meetings.begin(), meetings.end());

        priority_queue<int, vector<int>, greater<int>> available;

        using P = pair<long long, int>;
        priority_queue<P, vector<P>, greater<P>> busy;

        vector<int> count(n, 0);

        for (int i = 0; i < n; i++) {
            available.push(i);
        }

        for (auto& meeting : meetings) {
            long long start = meeting[0];
            long long end = meeting[1];

            while (!busy.empty() && busy.top().first <= start) {
                available.push(busy.top().second);
                busy.pop();
            }

            int room;
            long long endTime;

            if (!available.empty()) {
                room = available.top();
                available.pop();
                endTime = end;
            } else {
                auto [freeTime, r] = busy.top();
                busy.pop();

                room = r;
                endTime = freeTime + (end - start);
            }

            count[room]++;
            busy.push({endTime, room});
        }

        int result = 0;

        for (int i = 1; i < n; i++) {
            if (count[i] > count[result]) {
                result = i;
            }
        }

        return result;
    }
};
```

### **Complexity**

Let `m` be the number of meetings and `n` the number of rooms.

- **Time:** O(m log m + (m + n) log n) — Sorting and heap operations.
- **Space:** O(m + n) — Sorting overhead and two heaps.

### **Example**

```
n = 2
meetings = {{0,10}, {1,5}, {2,7}, {3,4}}
```

| Meeting  | Assigned room | Actual time |
| -------- | ------------- | ----------- |
| `[0,10]` | Room 0        | `[0,10]`    |
| `[1,5]`  | Room 1        | `[1,5]`     |
| `[2,7]`  | Room 1        | `[5,10]`    |
| `[3,4]`  | Room 0        | `[10,11]`   |

Both rooms host 2 meetings.

**Answer:&#x20;**`0` (smallest room number wins the tie).

### **Key Intuition**

**Greedy + Two Min-Heaps = Efficient Scheduling**

- Free rooms as soon as their meetings end.
- Always choose the smallest available room.
- If all rooms are busy, delay the meeting until the earliest room becomes free.
- Use `long long` for meeting end times to avoid integer overflow.

**Interview tip:** Remember the difference:

- **Meeting Rooms I:** Determine whether meetings overlap.
- **Meeting Rooms II:** Find the minimum rooms required.
- **Meeting Rooms III:** Simulate room assignments and delays using two min-heaps.
*/
class Solution {
public:
    int mostBooked(int n, vector<vector<int>>& meetings) {
        std::sort(meetings.begin(), meetings.end());

        priority_queue<int, vector<int>, greater<int>> available;
        priority_queue<pair<long long, int>, vector<pair<long long, int>>, greater<pair<long long, int>>> busy;

        vector<int> count(n, 0);

        for(int i=0; i<n; i++){
            available.push(i);
        }

        for(auto& meeting : meetings){
            long long start = meeting[0];
            long long end = meeting[1];

            while(!busy.empty() && busy.top().first <= start){
                available.push(busy.top().second);
                busy.pop();
            }

            int room;
            long long endTime;

            if(!available.empty()){
                room = available.top();
                available.pop();
                endTime = end;
            }else{
                auto[freeTime, r] = busy.top();
                busy.pop();

                room = r;
                endTime = freeTime + (end - start);
            }

            count[room]++;
            busy.push({endTime, room});
        }

        int result = 0;

        for(int i=1; i<n; i++){
            if(count[i] > count[result]){
                result = i;
            }
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna