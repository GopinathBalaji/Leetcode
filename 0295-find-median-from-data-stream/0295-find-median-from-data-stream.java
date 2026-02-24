// Method 1: My complicated approach using 2 heaps
/*
Your logic for balancing the two heaps is **basically correct** (it maintains size difference ≤ 1 and keeps lower half in `maxHeap`, upper half in `minHeap`). It should pass most test cases.

The main real issue is this:


## 2) It’s overly complicated (not “wrong”, but easy to bug)

Your `addNum` has many branches and special cases. It’s correct, but it’s much more error-prone than the standard approach.

The standard pattern is:

1. push into `maxHeap`
2. move top of `maxHeap` into `minHeap`
3. if `minHeap` is bigger, move top back to `maxHeap`

That guarantees:

* `maxHeap.size() == minHeap.size()` or `maxHeap.size() == minHeap.size() + 1`
* all `maxHeap` elements ≤ all `minHeap` elements

Example standard `addNum`:

```java
public void addNum(int num) {
    maxHeap.offer(num);
    minHeap.offer(maxHeap.poll());
    if (minHeap.size() > maxHeap.size()) {
        maxHeap.offer(minHeap.poll());
    }
}
```

Then median:

```java
public double findMedian() {
    if (maxHeap.size() == minHeap.size()) {
        return ((long)maxHeap.peek() + (long)minHeap.peek()) / 2.0;
    }
    return (double) maxHeap.peek();
}
```

## 3) Minor redundancy

When sizes are equal, checking both heaps empty is unnecessary (if sizes equal and >0, `maxHeap` cannot be empty in your flow). Not wrong, just extra.

---

### Bottom line

* **Correct idea / mostly correct implementation**
* **Recommendation:** use the standard 3-step rebalance; it’s shorter and harder to get wrong.
*/
class MedianFinder {

    PriorityQueue<Integer> maxHeap;
    PriorityQueue<Integer> minHeap;

    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        if(maxHeap.size() == minHeap.size()){
            if(maxHeap.isEmpty() && minHeap.isEmpty()){
                maxHeap.add(num);
                return;
            }

            if(maxHeap.peek() < num){
                minHeap.add(num);
            }else{
                maxHeap.add(num);
            }
            
        }else if(maxHeap.size() > minHeap.size()){
            if(maxHeap.peek() > num){
                int maxMinVal = maxHeap.poll();
                maxHeap.add(num);
                minHeap.add(maxMinVal);
            }else{
                minHeap.add(num);
            }

        }else if(maxHeap.size() < minHeap.size()){
            if(minHeap.peek() < num){
                int minMaxVal = minHeap.poll();
                minHeap.add(num);
                maxHeap.add(minMaxVal);
            }else{
                maxHeap.add(num);
            }
        }
        
    }
    
    public double findMedian() {
        if(maxHeap.size() == minHeap.size()){
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        }else if(maxHeap.size() > minHeap.size()){
            return (double) maxHeap.peek();
        }else{
            return (double) minHeap.peek();
        }
    }
}

/**
 * Your MedianFinder object will be instantiated and called as such:
 * MedianFinder obj = new MedianFinder();
 * obj.addNum(num);
 * double param_2 = obj.findMedian();
 */