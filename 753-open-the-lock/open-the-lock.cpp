// Method 1: Shortest path of graph so use BFS
/*
This is a **shortest path on an implicit graph**, so BFS is the natural fit.

Each lock combination is a node, like:

```text
"0000"
"0001"
"0900"
```

From any state, you have up to **8 neighbors** because each of the 4 wheels can turn:

```text
+1
-1
```

### Hint 1: Start BFS from `"0000"`

Use:

```cpp
queue<string> q;
unordered_set<string> visited;
unordered_set<string> dead(deadends.begin(), deadends.end());
```

If `"0000"` itself is a deadend, you should return immediately.

### Hint 2: Generate neighbors

For each of the 4 positions:

```cpp
for (int i = 0; i < 4; i++) {
    // turn wheel i forward
    // turn wheel i backward
}
```

Be careful with wraparound:

```text
'9' + 1 → '0'
'0' - 1 → '9'
```

One convenient formula is:

```cpp
next[i] = (state[i] - '0' + 1) % 10 + '0';
```

and for backward:

```cpp
next[i] = (state[i] - '0' + 9) % 10 + '0';
```

### Hint 3: BFS levels = number of turns

Process BFS level by level:

```cpp
int turns = 0;

while (!q.empty()) {
    int size = q.size();

    for (int i = 0; i < size; i++) {
        // process one state
    }

    turns++;
}
```

Each level represents one additional lock turn.

### Hint 4: When do you return?

When you pop or encounter:

```cpp
state == target
```

return the current number of turns.

If BFS finishes without reaching `target`:

```cpp
return -1;
```

### Hint 5: Skip bad states

Before adding a neighbor:

```cpp
if (dead.count(next) || visited.count(next)) {
    continue;
}
```

Then:

```cpp
visited.insert(next);
q.push(next);
```

Mark it visited **when you enqueue it**, not when you dequeue it, so you don't add the same state many times.

### Skeleton

```cpp
int openLock(vector<string>& deadends, string target) {
    unordered_set<string> dead(deadends.begin(), deadends.end());
    unordered_set<string> visited;

    queue<string> q;

    if (dead.count("0000")) {
        return -1;
    }

    q.push("0000");
    visited.insert("0000");

    int turns = 0;

    while (!q.empty()) {
        int size = q.size();

        for (int i = 0; i < size; i++) {
            string state = q.front();
            q.pop();

            if (state == target) {
                return turns;
            }

            for (int wheel = 0; wheel < 4; wheel++) {
                // generate +1 neighbor
                // generate -1 neighbor
                // enqueue valid unseen states
            }
        }

        turns++;
    }

    return -1;
}
```

The key mental model is:

```text
state = node
one wheel turn = edge
minimum turns = shortest path
```

So this is basically BFS on a graph whose neighbors are generated on the fly.
*/
class Solution {
public:
    int openLock(vector<string>& deadends, string target) {
        queue<string> q;

        unordered_set<string> visited;
        unordered_set<string> dead(deadends.begin(), deadends.end());

        if (dead.count("0000")) {
            return -1;
        }

        q.push("0000");
        visited.insert("0000");

        int turns = 0;

        while (!q.empty()) {
            int size = q.size();

            for (int i = 0; i < size; i++) {
                string state = q.front();
                q.pop();

                if (state == target) {
                    return turns;
                }

                for (int wheel = 0; wheel < 4; wheel++) {
                    string next = state;

                    // Forward
                    next[wheel] =
                        (state[wheel] - '0' + 1) % 10 + '0';

                    if (!dead.count(next) && !visited.count(next)) {
                        visited.insert(next);
                        q.push(next);
                    }

                    // Backward
                    next = state;

                    next[wheel] =
                        (state[wheel] - '0' + 9) % 10 + '0';

                    if (!dead.count(next) && !visited.count(next)) {
                        visited.insert(next);
                        q.push(next);
                    }
                }
            }

            turns++;
        }

        return -1;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna