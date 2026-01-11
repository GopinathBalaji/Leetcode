// Method 1: Using Monotonic Stack
/*
## Intuition

For each day `i`, you want to know: **how far to the right is the next day with a higher temperature?**
That’s the classic **Next Greater Element to the right** pattern.

A brute force approach checks forward from each day → `O(n^2)` worst case.

We can do it in **O(n)** using a **monotonic stack**:

### Monotonic stack idea

Maintain a stack of **indices** of days whose next warmer day we haven’t found yet.

We keep the stack such that temperatures are **strictly decreasing** from bottom → top:

* `temps[stack[0]] > temps[stack[1]] > ... > temps[stack[top]]`

When we see a new temperature `temps[i]`:

* While it’s warmer than the day at the stack top, we’ve found the answer for that top day.

---

## Algorithm

Let `ans` be initialized to all zeros.

For `i` from `0` to `n-1`:

1. While stack not empty and `temps[i] > temps[stack.peek()]`:

   * `j = stack.pop()`
   * `ans[j] = i - j` (distance to next warmer day)
2. Push `i` onto the stack

Any indices left in the stack never get a warmer day → their answer remains 0.

### Complexity

* **Time:** `O(n)` (each index pushed once, popped once)
* **Space:** `O(n)` for the stack in worst case (strictly decreasing temps)

---

## Thorough example walkthrough

Example:

```
temperatures = [73, 74, 75, 71, 69, 72, 76, 73]
index            0   1   2   3   4   5   6   7
```

Initialize:

* `ans = [0,0,0,0,0,0,0,0]`
* `stack = []` (stores indices)

I’ll show stack as `[top ... bottom]` since we use `push/pop/peek`.

---

### i = 0, temp = 73

* stack empty → push 0
  stack = [0]  (temps: [73])

---

### i = 1, temp = 74

Check top: temp[1]=74 > temp[0]=73 ✅

* pop j=0 → ans[0] = 1-0 = 1
  ans = [1,0,0,0,0,0,0,0]
* push 1
  stack = [1] (temps: [74])

---

### i = 2, temp = 75

75 > 74 ✅

* pop j=1 → ans[1] = 2-1 = 1
  ans = [1,1,0,0,0,0,0,0]
  push 2
  stack = [2] (temps: [75])

---

### i = 3, temp = 71

71 > 75? ❌
push 3
stack = [3,2] (temps: [71,75])
(Decreasing from bottom to top: 75 > 71 ✅)

---

### i = 4, temp = 69

69 > 71? ❌
push 4
stack = [4,3,2] (temps: [69,71,75])

---

### i = 5, temp = 72

Compare with top index 4: 72 > 69 ✅

* pop j=4 → ans[4] = 5-4 = 1
  ans = [1,1,0,0,1,0,0,0]

Now top index 3: 72 > 71 ✅

* pop j=3 → ans[3] = 5-3 = 2
  ans = [1,1,0,2,1,0,0,0]

Now top index 2: 72 > 75? ❌ stop popping

Push 5
stack = [5,2] (temps: [72,75])

Meaning:

* Day 2 (75) still waiting for a warmer day
* Day 5 (72) also waiting

---

### i = 6, temp = 76

Top index 5: 76 > 72 ✅

* pop j=5 → ans[5] = 6-5 = 1
  ans = [1,1,0,2,1,1,0,0]

Top index 2: 76 > 75 ✅

* pop j=2 → ans[2] = 6-2 = 4
  ans = [1,1,4,2,1,1,0,0]

Stack empty now → push 6
stack = [6] (temps: [76])

---

### i = 7, temp = 73

73 > 76? ❌
push 7
stack = [7,6] (temps: [73,76])

End of array.

Indices left (7 and 6) never get a warmer day → their ans stay 0.

Final:

```
ans = [1, 1, 4, 2, 1, 1, 0, 0]
```

---

## Why the stack must store indices (not values)

You need the **distance** `i - j`, so you must remember the index `j`.

---

If you want, I can also show the reverse-direction approach (scan from right to left) or explain typical pitfalls like using `>=` vs `>` (duplicates).
*/
class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] ans = new int[n];

        Deque<Integer> stack = new ArrayDeque<>(); // stack of indices

        for(int i=0; i<n; i++){
            while(!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]){
                int j = stack.pop();
                ans[j] = i - j;
            }

            stack.push(i);         
        }

        return ans;
    }
}