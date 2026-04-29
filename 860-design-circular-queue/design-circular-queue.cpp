// Method 1: Using a fixed-sized vector and indices that wrap around using modulo
/*
Hints:

1. Use a fixed-size `vector<int>` of size `k`.

2. Maintain:

```cpp
int front;
int rear;
int count;
int capacity;
```

3. `front` points to the current front element.

4. `rear` points to the next empty position where a new element will be inserted.

5. To move circularly, use modulo:

```cpp
rear = (rear + 1) % capacity;
front = (front + 1) % capacity;
```

6. `enQueue(value)`:

   * if full, return `false`
   * put value at `rear`
   * move `rear`
   * increment `count`

7. `deQueue()`:

   * if empty, return `false`
   * move `front`
   * decrement `count`

8. `Front()`:

   * if empty, return `-1`
   * return `data[front]`

9. `Rear()` is slightly tricky because `rear` points to the **next empty slot**, not the last element.

So last element index is:

```cpp
(rear - 1 + capacity) % capacity
```

10. `isEmpty()`:

```cpp
count == 0
```

11. `isFull()`:

```cpp
count == capacity
```

Main idea: the array never moves. Only `front`, `rear`, and `count` move around circularly.
*/
class MyCircularQueue {
private:
    std::vector<int> deque;
    int front = 0;
    int rear = 0;
    int count = 0;
    int capacity;

public:
    MyCircularQueue(int k) : deque(k), capacity(k) {}
    
    bool enQueue(int value) {
        if(isFull()){
            return false;
        }

        deque[rear] = value;
        rear = (rear + 1) % capacity;
        count++;

        return true;
    }
    
    bool deQueue() {
        if(isEmpty()){
            return false;
        }

        front = (front + 1) % capacity;
        count--;

        return true;
    }
    
    int Front() {
        if(isEmpty()){
            return -1;
        }

        return deque[front];
    }
    
    int Rear() {
        if(isEmpty()){
            return -1;
        }

        int circularRearIdx = (rear - 1 + capacity) % capacity;

        return deque[circularRearIdx];
    }
    
    bool isEmpty() {
        return count == 0;
    }
    
    bool isFull() {
        return count == capacity;
    }
};

/**
 * Your MyCircularQueue object will be instantiated and called as such:
 * MyCircularQueue* obj = new MyCircularQueue(k);
 * bool param_1 = obj->enQueue(value);
 * bool param_2 = obj->deQueue();
 * int param_3 = obj->Front();
 * int param_4 = obj->Rear();
 * bool param_5 = obj->isEmpty();
 * bool param_6 = obj->isFull();
 */