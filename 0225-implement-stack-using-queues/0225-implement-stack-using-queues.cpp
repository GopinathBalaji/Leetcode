// Method 1: Using 2 Queues
/*
## Hint 1: Stack vs Queue behavior

A stack is:

```text
LIFO = Last In, First Out
```

A queue is:

```text
FIFO = First In, First Out
```

So the main problem is: how do we make the **last pushed element** come out first using queues?

---

## Hint 2: You can use two queues

Use two queues:

```cpp
queue<int> q1;
queue<int> q2;
```

One queue stores the actual stack elements.

The other queue is used temporarily during `push()` or `pop()`.

---

## Hint 3: Make either `push()` expensive or `pop()` expensive

There are two common approaches:

### Approach 1: Expensive `push()`

Make sure the newest element is always at the **front** of the main queue.

Then:

```cpp
top()
pop()
```

become easy.

### Approach 2: Expensive `pop()`

Push normally into the queue.

Then during `pop()`, move all elements except the last one into another queue.

The last remaining element is the stack top.

---

## Hint 4: The cleaner approach is expensive `push()`

Suppose your current stack is:

```text
top -> 3, 2, 1
```

You want the queue to look like:

```text
front -> 3, 2, 1
```

Now if you push `4`, you want:

```text
front -> 4, 3, 2, 1
```

How can you do that using queues?

---

## Hint 5: Push into temporary queue first

When pushing `x`:

```cpp
q2.push(x);
```

Then move everything from `q1` into `q2`.

```cpp
while (!q1.empty()) {
    q2.push(q1.front());
    q1.pop();
}
```

Now `q2` has the new element at the front, followed by old elements.

Then swap:

```cpp
swap(q1, q2);
```

Now `q1` is again the main queue.

---

## Hint 6: Then `pop()` and `top()` are simple

Because the stack top is always at the front of `q1`:

```cpp
pop()  -> q1.front(), then q1.pop()
top()  -> q1.front()
empty() -> q1.empty()
```

---

## Example

Push `1`:

```text
q1: [1]
```

Push `2`:

```text
q2: [2]
move q1 to q2: [2, 1]
swap

q1: [2, 1]
```

Push `3`:

```text
q2: [3]
move q1 to q2: [3, 2, 1]
swap

q1: [3, 2, 1]
```

Now:

```cpp
top()
```

returns `3`.

And:

```cpp
pop()
```

removes `3`.

---

## Core idea

Maintain the main queue so that its **front always behaves like the top of the stack**. Then `pop()` and `top()` are easy.
*/
class MyStack {
private:
    queue<int> q1;
    queue<int> q2;

public:
    MyStack() {
        
    }
    
    void push(int x) {
        q2.push(x);

        while(!q1.empty()){
            q2.push(q1.front());
            q1.pop();
        }

        std::swap(q1, q2);
    }
    
    int pop() {
        int ans = q1.front();
        q1.pop();

        return ans;
    }
    
    int top() {
        return q1.front();
    }
    
    bool empty() {
        return q1.empty();
    }
};




// Method 2: Using one Queue
/*
## Core idea

A queue is FIFO:

```text
front -> oldest element
```

But a stack needs LIFO:

```text
top -> newest element
```

So after pushing a new element into the queue, rotate the older elements behind it.

Example:

```cpp
push(1)
q = [1]

push(2)
q initially = [1, 2]
rotate old elements once:
q = [2, 1]

push(3)
q initially = [2, 1, 3]
rotate old elements twice:
q = [3, 2, 1]
```

Now the front of the queue always behaves like the top of the stack.


## Explanation

Suppose the stack currently looks like this:

```text
top -> 3, 2, 1
```

We maintain the queue in the same order:

```text
front -> 3, 2, 1
```

Now if we call:

```cpp
push(4);
```

The queue first becomes:

```text
front -> 3, 2, 1, 4
```

But we want `4` to be at the front, because `4` is the newest element.

So we rotate the previous `n` elements:

```text
front -> 4, 3, 2, 1
```

Now:

```cpp
top()
```

returns `4`, and:

```cpp
pop()
```

removes `4`.

---

## Complexity

For `push(x)`:

```text
O(n)
```

because we rotate all previous elements.

For `pop()`:

```text
O(1)
```

For `top()`:

```text
O(1)
```

For `empty()`:

```text
O(1)
```

This is the standard one-queue solution: make `push()` expensive so that `pop()` and `top()` are easy.
*/

// class MyStack {
// private:
//     queue<int> q;

// public:
//     MyStack() {
        
//     }
    
//     void push(int x) {
//         int n = q.size();

//         q.push(x);

//         // Move the old elements behind the newly pushed element
//         for (int i = 0; i < n; i++) {
//             q.push(q.front());
//             q.pop();
//         }
//     }
    
//     int pop() {
//         int topElement = q.front();
//         q.pop();
//         return topElement;
//     }
    
//     int top() {
//         return q.front();
//     }
    
//     bool empty() {
//         return q.empty();
//     }
// };


/**
 * Your MyStack object will be instantiated and called as such:
 * MyStack* obj = new MyStack();
 * obj->push(x);
 * int param_2 = obj->pop();
 * int param_3 = obj->top();
 * bool param_4 = obj->empty();
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna