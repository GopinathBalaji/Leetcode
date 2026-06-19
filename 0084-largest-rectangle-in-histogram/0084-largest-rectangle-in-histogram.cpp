// Method 1: Monotonic Increasing Stack (storing indices)
/*
## Hint 1: Understand the rectangle

For each bar, ask:

```text
If this bar is the shortest bar in a rectangle, how wide can that rectangle extend?
```

Example:

```text
heights = [2, 1, 5, 6, 2, 3]
```

For bar `5`, it can extend through `[5, 6]`, but it stops when it sees smaller height `2`.

So area is:

```text
height = 5
width = 2
area = 10
```

---

## Hint 2: Brute force idea

For every index `i`, you could expand left and right while bars are at least `heights[i]`.

```text
area = heights[i] * width
```

But doing this for every bar is `O(n^2)`.

You need to find the left and right smaller bars efficiently.

---

## Hint 3: Think monotonic stack

Use a stack that stores indices of bars in **increasing height order**.

```cpp
stack<int> st;
```

The stack helps you find the first smaller bar to the left and the first smaller bar to the right.

---

## Hint 4: When do you calculate area?

When the current bar is **smaller** than the bar at the top of the stack, that means the top bar cannot extend further right.

So you pop it and compute its maximum rectangle.

```cpp
while (!st.empty() && heights[i] < heights[st.top()]) {
    int h = heights[st.top()];
    st.pop();

    // compute width
}
```

The current index `i` is the first smaller bar on the right.

---

## Hint 5: How to compute width after popping

Suppose you popped index `mid`.

```cpp
int h = heights[mid];
```

Now:

```text
right smaller index = i
left smaller index = st.top() after popping
```

So the rectangle using height `h` can span between those smaller bars.

If the stack is not empty:

```cpp
width = i - st.top() - 1;
```

If the stack is empty:

```cpp
width = i;
```

Because it can extend all the way to index `0`.

---

## Hint 6: Add a sentinel `0`

At the end, some bars may still be in the stack.

To force all remaining bars to be processed, append a height `0`.

```cpp
heights.push_back(0);
```

Since `0` is smaller than every real bar, it will trigger popping all remaining bars.

---

## Hint 7: Core logic

```cpp
for i from 0 to heights.size():
    while stack not empty and heights[i] < heights[stack.top()]:
        h = heights[stack.top()]
        stack.pop()

        if stack empty:
            width = i
        else:
            width = i - stack.top() - 1

        ans = max(ans, h * width)

    stack.push(i)
```

---

## Example intuition

For:

```text
heights = [2, 1, 5, 6, 2, 3]
```

When you reach height `2` at index `4`, it is smaller than `6` and `5`.

So you pop `6`:

```text
height = 6
width = 1
area = 6
```

Then pop `5`:

```text
height = 5
width = 2
area = 10
```

That gives the best rectangle.

---

## Core idea

Use a **monotonic increasing stack of indices**. A bar’s largest rectangle is calculated when you finally see a smaller bar to its right.

```text
Time: O(n)
Space: O(n)
```

Each index is pushed once and popped once.
*/
class Solution {
public:
    int largestRectangleArea(vector<int>& heights) {
        heights.push_back(0); // sentinel value

        int n = heights.size();

        stack<int> stack;
        int maxArea = 0;

        for(int i=0; i<n; i++){
            while(!stack.empty() && heights[i] < heights[stack.top()]){
                int h = heights[stack.top()];
                stack.pop();
                
                int width;
                if(!stack.empty()){
                    width = i - stack.top() - 1;
                }else{
                    width = i;
                }

                maxArea = std::max(maxArea, h * width);
            }

            stack.push(i);
        }


        return maxArea;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna