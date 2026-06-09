// Method 1: Using two stacks with lazy transfer (answers the follow-up)
/*
## Hint 1: Queue vs Stack behavior

A queue is:

```text
FIFO = First In, First Out
```

A stack is:

```text
LIFO = Last In, First Out
```

So the main challenge is: how do we make the **oldest inserted element** come out first using stacks?

---

## Hint 2: Use two stacks

Use two stacks:

```cpp
stack<int> input;
stack<int> output;
```

Think of them as:

```text
input  -> used for push()
output -> used for pop() and peek()
```

---

## Hint 3: Push is easy

When someone calls:

```cpp
push(x)
```

just push into the `input` stack.

```cpp
input.push(x);
```

Example:

```text
push(1)
push(2)
push(3)
```

Then `input` looks like:

```text
top -> 3, 2, 1
```

But for a queue, the next element to remove should be `1`, not `3`.

---

## Hint 4: Reverse the order when needed

To get the oldest element on top, move everything from `input` to `output`.

```cpp
while (!input.empty()) {
    output.push(input.top());
    input.pop();
}
```

Before moving:

```text
input top -> 3, 2, 1
```

After moving:

```text
output top -> 1, 2, 3
```

Now `output.top()` is the queue front.

---

## Hint 5: Only transfer when `output` is empty

Do not move elements every time.

Only transfer from `input` to `output` when you need to `pop()` or `peek()` and `output` is empty.

Why?

Because if `output` already has elements, those are older than anything in `input`, so they should be removed first.

---

## Hint 6: `pop()` logic

For `pop()`:

```text
If output is empty:
    move everything from input to output

Then:
    answer = output.top()
    output.pop()
```

That removes the oldest element.

---

## Hint 7: `peek()` is almost the same

For `peek()`:

```text
If output is empty:
    move everything from input to output

Then:
    return output.top()
```

Same as `pop()`, except you do not remove it.

---

## Hint 8: `empty()`

The queue is empty only when both stacks are empty:

```cpp
return input.empty() && output.empty();
```

---

## Core idea

Use one stack to collect new elements, and another stack to reverse the order when you need to remove or inspect the queue front.

The important trick is **lazy transfer**: move elements from `input` to `output` only when `output` is empty.

*/
class MyQueue {
private:
    stack<int> input;
    stack<int> output;

public:
    MyQueue() {
        
    }
    
    void push(int x) {
        input.push(x);
    }
    
    int pop() {
        if(output.empty()){
            while(!input.empty()){
                output.push(input.top());
                input.pop();
            }
        }
        
        int ans = output.top();
        output.pop();

        return ans;
    }
    
    int peek() {
        if(output.empty()){
            while(!input.empty()){
                output.push(input.top());
                input.pop();
            }
        }

        return output.top();
    }
    
    bool empty() {
        return input.empty() && output.empty();
    }
};



/**
 * Your MyQueue object will be instantiated and called as such:
 * MyQueue* obj = new MyQueue();
 * obj->push(x);
 * int param_2 = obj->pop();
 * int param_3 = obj->peek();
 * bool param_4 = obj->empty();
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna