// Method 1: MaxHeap Approach
/*
Use a **max-heap of remaining task frequencies**. In each cycle, execute up to `n + 1` different tasks so the same task cannot repeat within the cooldown window.

### Why use `n + 1`?

Suppose:

```text
n = 2
```

After executing `A`, there must be two intervals before another `A`:

```text
A _ _ A
```

So each scheduling cycle has up to:

```text
n + 1 = 3
```

positions.

### Example

```text
tasks = [A, A, A, B, B, B]
n = 2
```

Initial heap contains:

```text
[3, 3]
```

First cycle:

```text
A B idle
```

Remaining frequencies:

```text
A: 2
B: 2
```

Second cycle:

```text
A B idle
```

Remaining:

```text
A: 1
B: 1
```

Final cycle:

```text
A B
```

There is no need to add trailing idle time because all tasks are finished.

Total:

```text
3 + 3 + 2 = 8
```

### Why temporarily remove tasks?

A task executed during the current cycle cannot immediately be pushed back into the heap. Otherwise, the same task might be selected again before its cooldown finishes.

So the algorithm:

1. Removes up to `n + 1` different tasks.
2. Decreases their frequencies.
3. Stores unfinished tasks temporarily.
4. Pushes them back only after the cycle ends.

### Complexity

Since there are at most 26 task types:

* Time: `O(tasks.size() log 26)`, effectively `O(tasks.size())`
* Space: `O(26)`, effectively `O(1)`
*/

class Solution {
public:
    int leastInterval(vector<char>& tasks, int n) {
        vector<int> frequency(26, 0);

        for (char task : tasks) {
            frequency[task - 'A']++;
        }

        priority_queue<int> maxHeap;

        for (int count : frequency) {
            if (count > 0) {
                maxHeap.push(count);
            }
        }

        int time = 0;
        int cycleLength = n + 1;

        while (!maxHeap.empty()) {
            vector<int> remaining;
            int tasksExecuted = 0;

            // Execute at most n + 1 different task types.
            for (int i = 0; i < cycleLength && !maxHeap.empty(); i++) {
                int count = maxHeap.top();
                maxHeap.pop();

                count--;
                tasksExecuted++;

                if (count > 0) {
                    remaining.push_back(count);
                }
            }

            // Tasks used in this cycle can now be added back.
            for (int count : remaining) {
                maxHeap.push(count);
            }

            if (maxHeap.empty()) {
                // No more work, so no trailing idle time is needed.
                time += tasksExecuted;
            } else {
                // The cycle must last n + 1 intervals.
                time += cycleLength;
            }
        }

        return time;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna