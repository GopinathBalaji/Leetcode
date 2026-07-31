// Method 1: Using MinHeap
/*
### Hint 1

Each task has:

```text
[enqueueTime, processingTime]
```

The CPU can only choose tasks whose `enqueueTime` is less than or equal to the current time.

---

### Hint 2

Sort the tasks by `enqueueTime` first.

Before sorting, attach each task’s original index:

```cpp
{enqueueTime, processingTime, index}
```

You need the original index for the final answer.

---

### Hint 3

Use a min-heap containing all tasks that are currently available.

The CPU chooses based on:

1. Smallest processing time
2. Smallest original index when processing times are equal

So the heap should order tasks using:

```text
(processingTime, index)
```

---

### Hint 4

Maintain:

```cpp
long long currentTime = 0;
int i = 0;
```

Here, `i` points to the next task in the sorted list that has not yet been added to the heap.

Use `long long` because the total processing time can become large.

---

### Hint 5

Before choosing a task, add every task that has already arrived:

```cpp
while (i < tasks.size() &&
       tasks[i].enqueueTime <= currentTime) {
    // push into heap
    i++;
}
```

---

### Hint 6

What if the heap is empty but there are still unprocessed tasks?

The CPU is idle, so jump time directly to the next task’s enqueue time:

```cpp
currentTime = tasks[i].enqueueTime;
```

Do not advance time one unit at a time.

---

### Hint 7

When the heap is not empty:

1. Pop the task with the smallest processing time.
2. Add its original index to the result.
3. Increase `currentTime` by its processing time.

```cpp
currentTime += processingTime;
```

Then add any new tasks that arrived while this task was executing.

---

### Hint 8

A C++ min-heap can store:

```cpp
priority_queue<
    pair<int, int>,
    vector<pair<int, int>>,
    greater<pair<int, int>>
> minHeap;
```

Store:

```cpp
{processingTime, originalIndex}
```

`pair` comparison naturally handles the tie-breaking rule.

---

### Overall loop

```cpp
while (i < taskCount || !minHeap.empty()) {
    if (minHeap.empty() && currentTime < nextEnqueueTime) {
        // jump currentTime
    }

    // add all available tasks

    // pop best task
    // record its index
    // advance currentTime
}
```

### Complexity

* Sorting: `O(n log n)`
* Heap operations: `O(n log n)`
* Space: `O(n)`
*/

class Solution {
public:
    vector<int> getOrder(vector<vector<int>>& tasks) {
        int n = tasks.size();

        // Add original index to each task.
        for (int i = 0; i < n; i++) {
            tasks[i].push_back(i);
        }

        // Sort by enqueue time.
        sort(tasks.begin(), tasks.end(),
             [](const vector<int>& a, const vector<int>& b) {
                 return a[0] < b[0];
             });

        // {processingTime, originalIndex}
        priority_queue<
            pair<int, int>,
            vector<pair<int, int>>,
            greater<pair<int, int>>
        > minHeap;

        vector<int> result;

        long long currentTime = 0;
        int i = 0;

        while (i < n || !minHeap.empty()) {
            // No available tasks: jump to the next enqueue time.
            if (minHeap.empty() && currentTime < tasks[i][0]) {
                currentTime = tasks[i][0];
            }

            // Add every task that is currently available.
            while (i < n && tasks[i][0] <= currentTime) {
                minHeap.push({
                    tasks[i][1], // processing time
                    tasks[i][2]  // original index
                });

                i++;
            }

            // Execute the best available task.
            auto [processingTime, originalIndex] = minHeap.top();
            minHeap.pop();

            result.push_back(originalIndex);
            currentTime += processingTime;
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna