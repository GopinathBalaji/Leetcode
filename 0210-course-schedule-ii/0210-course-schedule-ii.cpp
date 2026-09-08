// Method 1: BFS / Indegree / Khan's Algorithm
/*
For **210. Course Schedule II**, Kahn’s algorithm is a very natural fit because it directly builds a valid course order.

The main idea is:

> Always take a course that currently has **0 prerequisites remaining**.

### Hint 1: Build the graph and indegree array

For:

```cpp
prerequisites = [[1, 0]]
```

meaning:

```text
to take 1, you need 0 first
```

create:

```text
0 -> 1
```

So:

```cpp
graph[0].push_back(1);
indegree[1]++;
```

Use:

```cpp
vector<vector<int>> graph(numCourses);
vector<int> indegree(numCourses, 0);
```

### Hint 2: Start with all courses having indegree 0

These courses have no prerequisites left.

Push all of them into a queue:

```cpp
queue<int> q;

for (int course = 0; course < numCourses; course++) {
    if (indegree[course] == 0) {
        q.push(course);
    }
}
```

### Hint 3: Process courses from the queue

When you pop a course:

```cpp
int course = q.front();
q.pop();
```

that course is safe to take, so add it to your answer:

```cpp
order.push_back(course);
```

Then look at every course that depends on it:

```cpp
for (int next : graph[course]) {
    indegree[next]--;
}
```

Why decrement?

Because you've now completed one of `next`'s prerequisites.

### Hint 4: When should a new course enter the queue?

After decrementing:

```cpp
if (indegree[next] == 0) {
    q.push(next);
}
```

That means all prerequisites for `next` have now been satisfied.

### Hint 5: How do you detect a cycle?

After BFS finishes, check:

```cpp
order.size()
```

If:

```cpp
order.size() == numCourses
```

you successfully processed every course.

Otherwise, some courses were trapped in a cycle, so return:

```cpp
{}
```

### Skeleton

```cpp
vector<int> findOrder(int numCourses,
                      vector<vector<int>>& prerequisites) {

    vector<vector<int>> graph(numCourses);
    vector<int> indegree(numCourses, 0);

    // build graph + indegree

    queue<int> q;

    // push all indegree-0 courses

    vector<int> order;

    while (!q.empty()) {
        int course = q.front();
        q.pop();

        // add course to result

        for (int next : graph[course]) {
            // decrement indegree

            // if it becomes 0, push it
        }
    }

    // if order has all courses -> return it
    // otherwise -> cycle -> return {}
}
```

The mental model is:

```text
indegree = number of prerequisites still blocking this course

indegree 0
→ course is available
→ take it
→ remove its outgoing edges
→ maybe unlock more courses
```

The main difference from **207** is:

```text
207: "Can I finish?"       → return bool
210: "What order?"         → save popped courses
```

With Kahn’s algorithm, the queue order itself becomes the topological ordering.
*/
class Solution {
public:
    vector<int> findOrder(int numCourses, vector<vector<int>>& prerequisites) {
        vector<vector<int>> graph(numCourses);
        vector<int> indegree(numCourses, 0);

        vector<int> order;

        for(auto& prereq : prerequisites){
            graph[prereq[1]].push_back(prereq[0]);
            indegree[prereq[0]]++;
        }

        queue<int> q;

        for(int course=0; course < numCourses; course++){
            if(indegree[course] == 0){
                q.push(course);
            }
        }

        while(!q.empty()){
            int course = q.front();
            q.pop();

            order.push_back(course);

            for(int next : graph[course]){
                indegree[next]--;

                if(indegree[next] == 0){
                    q.push(next);
                }
            }
        }

        return order.size() == numCourses ? order : vector<int>{};
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna