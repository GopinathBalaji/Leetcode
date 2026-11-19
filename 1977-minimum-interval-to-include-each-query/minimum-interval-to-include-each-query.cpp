// Method 1: Min-Heap + Sorting
/*
## **LeetCode 1851: Minimum Interval to Include Each Query (C++)**

**Approach: Sorting + Min-Heap | Difficulty: Hard**

### **Hint 1: Think Greedy + Heap**

For each query `q`, find the **smallest interval** satisfying:

```
start <= q && q <= end
```

The interval size is `end - start + 1`.

**Key idea:** Process queries in sorted order while maintaining eligible intervals in a min-heap.

### **Hint 2: Sort Intervals and Queries**

Sort intervals by starting position.

Preserve original query indices because answers must be returned in their original order.

```
sort(intervals.begin(), intervals.end());

vector<pair<int, int>> sortedQueries;

for (int i = 0; i < queries.size(); i++) {
    sortedQueries.push_back({queries[i], i});
}

sort(sortedQueries.begin(), sortedQueries.end());
```

### **Hint 3: Use a Min-Heap**

Store `{intervalSize, end}`.

```
using P = pair<int, int>;
priority_queue<P, vector<P>, greater<P>> minHeap;
```

The smallest interval will always be at the top.

### **Hint 4: Add Eligible Intervals**

For each query `q`, add all intervals starting at or before `q`.

```
while (i < intervals.size() && intervals[i][0] <= q) {
    int start = intervals[i][0];
    int end = intervals[i][1];

    minHeap.push({end - start + 1, end});
    i++;
}
```

### **Hint 5: Remove Expired Intervals**

Remove intervals that end before the current query.

```
while (!minHeap.empty() && minHeap.top().second < q) {
    minHeap.pop();
}
```

If the heap isn't empty, its top contains the smallest valid interval.

### **Complete C++ Solution**

```
class Solution {
public:
    vector<int> minInterval(vector<vector<int>>& intervals,
                            vector<int>& queries) {
        sort(intervals.begin(), intervals.end());

        vector<pair<int, int>> sortedQueries;

        for (int i = 0; i < queries.size(); i++) {
            sortedQueries.push_back({queries[i], i});
        }

        sort(sortedQueries.begin(), sortedQueries.end());

        using P = pair<int, int>;
        priority_queue<P, vector<P>, greater<P>> minHeap;

        vector<int> result(queries.size(), -1);
        int i = 0;

        for (auto& [q, index] : sortedQueries) {
            while (i < intervals.size() &&
                   intervals[i][0] <= q) {
                int start = intervals[i][0];
                int end = intervals[i][1];

                minHeap.push({end - start + 1, end});
                i++;
            }

            while (!minHeap.empty() &&
                   minHeap.top().second < q) {
                minHeap.pop();
            }

            if (!minHeap.empty()) {
                result[index] = minHeap.top().first;
            }
        }

        return result;
    }
};
```

### **Complexity**

Let `n` = number of intervals and `m` = number of queries.

- **Time:** O(n log n + m log m + (n + m) log n)
- **Space:** O(n + m) — Heap, sorted queries, and output.

### **Example**

```
intervals = {{1,4}, {2,4}, {3,6}, {4,4}};
queries = {2,3,4,5};
```

| Query | Smallest valid interval | Size |
| ----- | ----------------------- | ---- |
| 2     | `[2,4]`                 | 3    |
| 3     | `[2,4]`                 | 3    |
| 4     | `[4,4]`                 | 1    |
| 5     | `[3,6]`                 | 4    |

**Answer:** `[3,3,1,4]`

### **Key Intuition**

**Sorting + Min-Heap = Efficient Interval Queries**

- Sorting queries lets you process intervals progressively.
- The min-heap prioritizes the shortest intervals.
- Remove intervals that can no longer contain the current query.
- Preserve original query indices to construct the correct output.

**Interview tip:** This is an example of an **offline query algorithm**. When queries can be reordered, sorting them and maintaining a heap of eligible candidates can turn an O(n × m) solution into an efficient one.
*/
class Solution {
public:
    vector<int> minInterval(vector<vector<int>>& intervals, vector<int>& queries) {
        std::sort(intervals.begin(), intervals.end());

        vector<pair<int, int>> sortedQueries;

        for(int i=0; i<queries.size(); ++i){
            sortedQueries.push_back({queries[i], i});
        }

        std::sort(sortedQueries.begin(), sortedQueries.end());

        priority_queue<pair<int, int>, vector<pair<int, int>>, greater<pair<int, int>>> minHeap;

        vector<int> result(queries.size(), -1);
        int i = 0;

        for(auto& [q, index] : sortedQueries){
            while(i < intervals.size() && intervals[i][0] <= q){
                int start = intervals[i][0];
                int end = intervals[i][1];

                minHeap.push({end - start + 1, end});
                i++;
            }

            while(!minHeap.empty() && minHeap.top().second < q){
                minHeap.pop();
            }

            if(!minHeap.empty()){
                result[index] = minHeap.top().first;
            }
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna