// Method 1: Left = MaxHeap + Right = MinHeap method
/*
### Hint 1: Split the numbers into two halves

Maintain:

* A **max-heap** for the smaller half
* A **min-heap** for the larger half

In C++:

```cpp
priority_queue<int> left; // max-heap

priority_queue<int, vector<int>, greater<int>> right; // min-heap
```

The largest value in `left` and the smallest value in `right` will be near the median.

---

### Hint 2: Maintain two rules

After every insertion:

```text
Every value in left <= every value in right
```

And their sizes should differ by at most `1`.

A convenient size rule is:

```text
left.size() == right.size()
```

or:

```text
left.size() == right.size() + 1
```

This means the extra element, when the count is odd, stays in `left`.

---

### Hint 3: Where should a new number go?

Compare it with the largest number in the lower half:

```cpp
if (left.empty() || num <= left.top()) {
    // Add to left
} else {
    // Add to right
}
```

After inserting, the heaps might become unbalanced, so move the top element from one heap to the other.

---

### Hint 4: Rebalancing

Consider these two cases:

```cpp
if (left.size() > right.size() + 1) {
    // Move left.top() into right
}

if (right.size() > left.size()) {
    // Move right.top() into left
}
```

Remember to remove the element from its original heap after moving it.

---

### Hint 5: Finding the median

If there is an odd number of elements, `left` has one extra element:

```cpp
return left.top();
```

If there is an even number of elements, average the two middle values:

```cpp
return (left.top() + right.top()) / 2.0;
```

Be careful about integer overflow. A safer expression is:

```cpp
return ((double) left.top() + right.top()) / 2.0;
```

---

### Class skeleton

```cpp
class MedianFinder {
private:
    priority_queue<int> left;

    priority_queue<int, vector<int>, greater<int>> right;

public:
    MedianFinder() {
    }

    void addNum(int num) {
        // 1. Insert into the correct heap

        // 2. Rebalance the heaps
    }

    double findMedian() {
        // Check whether the total count is odd or even
    }
};
```

Each insertion takes `O(log n)`, while finding the median takes `O(1)`.
*/
class MedianFinder {
private:
    priority_queue<int> left;
    priority_queue<int, vector<int>, greater<int>> right;

public:
    MedianFinder() {
        
    }
    
    void addNum(int num) {
        if(left.empty() || num <= left.top()){
            left.push(num);
        }else{
            right.push(num);
        }

        if(left.size() > right.size() + 1){
            int leftMax = left.top();
            left.pop();

            right.push(leftMax);
        }

        if(right.size() > left.size()){
            int rightMin = right.top();
            right.pop();

            left.push(rightMin);
        }
    }
    
    double findMedian() {
        if(left.size() > right.size()){
            return left.top();
        }else if(left.size() == right.size()){
            return ((double) left.top() + right.top()) / 2.0;
        }

        return -1.0;
    }
};

/**
 * Your MedianFinder object will be instantiated and called as such:
 * MedianFinder* obj = new MedianFinder();
 * obj->addNum(num);
 * double param_2 = obj->findMedian();
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna