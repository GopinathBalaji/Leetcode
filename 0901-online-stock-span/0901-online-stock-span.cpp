// Method 1: Monotonic Decreasing Stack approach
/*
## Hint 1: Understand what span means

For each price, return the number of consecutive days up to today where the stock price was **less than or equal to today’s price**.

Example:

```text
prices = [100, 80, 60, 70, 60, 75, 85]
```

Output:

```text
[1, 1, 1, 2, 1, 4, 6]
```

For price `75`, the previous consecutive prices `60, 70, 60` are all `<= 75`, so span is `4`, including today.

---

## Hint 2: Brute force is too slow

For every new price, you could scan backward until you find a greater price.

```cpp
span = 1;
while previous prices <= current price:
    span++;
```

But this can become `O(n)` per call, causing `O(n^2)` overall.

You need a way to skip over blocks of smaller prices.

---

## Hint 3: This is a monotonic stack problem

Use a stack to store prices in **decreasing order**.

The stack should help you quickly find the previous price that is **greater** than the current price.

Once you find a greater price, the span stops.

---

## Hint 4: Store more than just the price

Instead of only storing:

```cpp
price
```

store:

```cpp
{price, span}
```

Why?

Because when the current price is greater than or equal to a previous price, you can absorb that previous price’s entire span.

---

## Hint 5: Pop smaller or equal prices

For each new price:

```cpp
span = 1
```

Then while the stack is not empty and the top price is `<= current price`, pop it and add its span.

```cpp
while (!st.empty() && st.top().price <= price) {
    span += st.top().span;
    st.pop();
}
```

Then push the current pair:

```cpp
st.push({price, span});
```

Return `span`.

---

## Hint 6: Walk through one example

For:

```text
[100, 80, 60, 70]
```

After `100`:

```text
stack = [(100, 1)]
span = 1
```

After `80`:

```text
stack = [(100, 1), (80, 1)]
span = 1
```

After `60`:

```text
stack = [(100, 1), (80, 1), (60, 1)]
span = 1
```

Now price `70` comes.

`60 <= 70`, so pop `(60, 1)` and add its span.

```text
span = 2
stack = [(100, 1), (80, 1)]
```

`80 > 70`, so stop.

Push `(70, 2)`.

```text
stack = [(100, 1), (80, 1), (70, 2)]
```

So answer for `70` is `2`.

---

## Core idea

Use a monotonic decreasing stack of:

```cpp
{price, span}
```

Each time a new price comes in, pop all previous prices `<= current price`, add their spans, then push the current price with its computed span.

The reason this is efficient is that each price is pushed once and popped once.

```text
Time per next(): amortized O(1)
Space: O(n)
```
*/
class StockSpanner {
private:
    // {price, span}
    stack<pair<int, int>> stack;

public:
    StockSpanner() {
        
    }
    
    int next(int price) {
        int span = 1;

        while(!stack.empty() && stack.top().first <= price){
            span += stack.top().second;
            stack.pop();
        }

        stack.push({price, span});

        return span;
    }
};

/**
 * Your StockSpanner object will be instantiated and called as such:
 * StockSpanner* obj = new StockSpanner();
 * int param_1 = obj->next(price);
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna