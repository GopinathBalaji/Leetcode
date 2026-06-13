// Metho 1: Monotonic Decreasing Stack
/*
## Hint 1: Understand what the answer stores

For each day `i`, you need to find how many days later a warmer temperature appears.

Example:

```text
temperatures = [73,74,75,71,69,72,76,73]
```

Answer:

```text
[1,1,4,2,1,1,0,0]
```

For day `0`, temperature is `73`.

Next warmer day is day `1`, temperature `74`.

So answer is:

```text
1 - 0 = 1
```

---

## Hint 2: Brute force is simple but slow

For every day, you could scan forward until you find a warmer day.

```cpp
for each i:
    for each j > i:
        if temperatures[j] > temperatures[i]:
            answer[i] = j - i
            break
```

But this is:

```text
O(n^2)
```

You need a better way.

---

## Hint 3: Think monotonic stack

This is a **next greater element** problem.

You want the next day with a greater temperature.

Use a stack to store indices of days whose answer has not been found yet.

---

## Hint 4: What should the stack store?

Store indices, not temperatures.

```cpp
stack<int> st;
```

Why indices?

Because the answer needs the distance:

```cpp
currentIndex - previousIndex
```

---

## Hint 5: Maintain a decreasing stack

The stack should store indices of temperatures in decreasing order.

Example:

```text
temperatures = [75, 71, 69]
stack stores indices for temperatures:
75, 71, 69
```

When a warmer temperature appears, it can resolve previous colder days.

---

## Hint 6: When current temperature is warmer

Suppose current day is `i`.

While the stack is not empty and:

```cpp
temperatures[i] > temperatures[st.top()]
```

that means day `i` is the next warmer day for `st.top()`.

So:

```cpp
int prev = st.top();
st.pop();

answer[prev] = i - prev;
```

Keep popping while the current temperature is warmer than the top of the stack.

---

## Hint 7: Push current day afterward

After resolving all colder previous days, push the current index:

```cpp
st.push(i);
```

Because this current day is now waiting for a warmer future day.

---

## Example intuition

For:

```text
[73, 74, 75, 71, 69, 72, 76, 73]
```

When you reach `72`, it resolves:

```text
69 -> waits 1 day
71 -> waits 2 days
```

because `72` is warmer than both `69` and `71`.

But it does not resolve `75`, because `72` is not warmer than `75`.

---

## Core logic

```cpp
for i from 0 to n - 1:
    while stack is not empty and temperatures[i] > temperatures[stack.top()]:
        prev = stack.top()
        stack.pop()
        answer[prev] = i - prev

    stack.push(i)
```

Days left in the stack at the end have no warmer future day, so their answer stays `0`.

## Complexity

```text
Time: O(n)
Space: O(n)
```

Each index is pushed once and popped at most once.
*/
class Solution {
public:
    vector<int> dailyTemperatures(vector<int>& temperatures) {
        int n = temperatures.size();

        stack<int> stack;
        vector<int> ans(n);

        for(int i=0; i<n; i++){

            while(!stack.empty() && temperatures[i] > temperatures[stack.top()]){
                int prev = stack.top();
                stack.pop();

                ans[prev] = i - prev;
            }

            stack.push(i);
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna