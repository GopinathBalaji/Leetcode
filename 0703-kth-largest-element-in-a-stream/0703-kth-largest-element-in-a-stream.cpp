// Method 1: MinHeap approach
/*
### Hint 1

You do **not** need to store the entire stream in sorted order.

You only need enough information to identify the `k` largest elements seen so far.

### Hint 2

Use a **min-heap** of size at most `k`.

Why a min-heap?

Because among the current `k` largest elements, the smallest one is exactly the kth largest overall.

### Hint 3

Whenever a new value arrives:

```cpp
heap.push(val);
```

If the heap grows larger than `k`:

```cpp
heap.pop();
```

Since it is a min-heap, this removes the smallest value and keeps only the `k` largest values.

### Hint 4

After processing a value, the answer is:

```cpp
heap.top();
```

The heap contains the `k` largest values, and its smallest element is the kth largest.

### Hint 5

In the constructor, process every value from `nums` using the same logic as `add`.

This avoids duplicating logic:

```cpp
for (int num : nums) {
    add(num);
}
```

### Hint 6

In C++, create a min-heap using:

```cpp
priority_queue<int, vector<int>, greater<int>> minHeap;
```

Your class will need to store:

```cpp
int k;
priority_queue<int, vector<int>, greater<int>> minHeap;
```

### Structure

```cpp
class KthLargest {
private:
    int k;
    priority_queue<int, vector<int>, greater<int>> minHeap;

public:
    KthLargest(int k, vector<int>& nums) {
        // save k
        // insert all initial values
    }

    int add(int val) {
        // push val
        // if size > k, pop
        // return top
    }
};
```

Each `add` operation takes `O(log k)` time, and the heap uses `O(k)` space.
*/
class KthLargest {
private:
    int k;
    priority_queue<int, vector<int>, greater<int>> minHeap;

public:
    KthLargest(int k, vector<int>& nums) {
        this->k = k;

        for(int num: nums){
            minHeap.push(num);

            if(minHeap.size() > k){
                minHeap.pop();
            }
        }
    }
    
    int add(int val) {
        minHeap.push(val);

        if(minHeap.size() > k){
            minHeap.pop();
        }

        return minHeap.top();
    }
};

/**
 * Your KthLargest object will be instantiated and called as such:
 * KthLargest* obj = new KthLargest(k, nums);
 * int param_1 = obj->add(val);
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna