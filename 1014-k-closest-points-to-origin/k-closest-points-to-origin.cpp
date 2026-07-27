// Method 1: Max-heap of size k approach
/*
### Hint 1

For a point `(x, y)`, its distance from the origin is:

```cpp
sqrt(x * x + y * y)
```

You do not need the square root. Compare using:

```cpp
x * x + y * y
```

because square root preserves ordering.

### Hint 2

You need the `k` points with the smallest distances.

One approach is to maintain a **max-heap of size `k`**.

Why a max-heap? Its top will be the farthest point among the current `k` closest points.

### Hint 3

For every point:

1. Calculate its squared distance.
2. Push it into the heap.
3. If the heap size becomes greater than `k`, remove the heap’s top.

```cpp
heap.push(distance and point);

if (heap.size() > k) {
    heap.pop();
}
```

This removes the farthest point and keeps only the `k` closest.

### Hint 4

You can store the squared distance together with the point:

```cpp
priority_queue<pair<int, vector<int>>> maxHeap;
```

A `pair` is compared using its first value first, so the largest distance will be on top.

Push using:

```cpp
int distance = point[0] * point[0] +
               point[1] * point[1];

maxHeap.push({distance, point});
```

### Hint 5

After processing every point, the heap contains exactly the answer.

Pop all points from the heap into a result vector:

```cpp
while (!maxHeap.empty()) {
    result.push_back(maxHeap.top().second);
    maxHeap.pop();
}
```

The output can be in any order.

### Complexity

With a heap of size `k`:

* Time: `O(n log k)`
* Space: `O(k)`

An alternative is Quickselect, which has average `O(n)` time, but the heap solution is usually easier to implement correctly.
*/
class Solution {
public:
    vector<vector<int>> kClosest(vector<vector<int>>& points, int k) {
        priority_queue<pair<int, vector<int>>> maxHeap;
        vector<vector<int>> result;

        for(int i=0; i<points.size(); i++){
            int distance = points[i][0] * points[i][0] + points[i][1] * points[i][1];

            maxHeap.push({distance, points[i]});

            if(maxHeap.size() > k){
                maxHeap.pop();
            }
        }

        while(!maxHeap.empty()){
            result.push_back(maxHeap.top().second);
            maxHeap.pop();
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna