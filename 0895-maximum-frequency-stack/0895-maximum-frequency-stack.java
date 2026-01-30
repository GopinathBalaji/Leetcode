// Method 1:  2 layer bookkeeping A) map of freq of each entry, B) freq to stack map
/*
######## HINTS ############

### Key idea

You need to support:

* `push(x)` → add element
* `pop()` → remove/return the element with **highest frequency**
* If tie in frequency → return the **most recently pushed among those tied**

So you’re optimizing by two criteria:

1. **frequency (descending)**
2. **recency within that frequency (descending)**

### Hint 1: Track frequencies

Use a map:

* `freq[x] = how many times x is currently in the stack`

On `push(x)`, do `freq[x]++`.

### Hint 2: Group values by frequency

Instead of one stack, maintain a structure:

* `group[f]` is a **stack** (LIFO) of values that **reached frequency f** in push order.

When you push `x` and its frequency becomes `f`, do:

* `group[f].push(x)`

Why a stack? Because ties require **most recent** among max-frequency elements.

### Hint 3: Track current maximum frequency

Keep:

* `maxFreq` = maximum frequency of any element currently present

On `push`, update:

* `maxFreq = max(maxFreq, freq[x])`

### Hint 4: How to pop efficiently

To pop the “most frequent, most recent”:

* Look at `group[maxFreq]` (a stack)
* Pop top value `x` from it (this is the most recent among those with `maxFreq`)
* Decrement `freq[x]--`
* If `group[maxFreq]` becomes empty, then `maxFreq--`

That’s the whole trick: **never search**; always index by `maxFreq`.

### Hint 5: Why this works (tie-breaking)

If two values have the same frequency `f`, whichever was pushed later **entered `group[f]` later**, so it sits on top → popped first.

### Hint 6: Complexity target

This design gives:

* `push`: **O(1)** average
* `pop`: **O(1)** average
* Space: **O(n)**

### Common pitfall to avoid

Don’t try to keep a priority queue with timestamps; it can work but gets messy with stale entries. The “frequency → stack of values” grouping is cleaner and is the standard intended solution.

##########################################

## Why this works

### Goal of `pop()`

Return the element with:

1. **highest frequency**
2. if tie, **most recently pushed**

### Key insight

Instead of storing everything in one place, we maintain **one stack per frequency**:

* `group[1]` = values in the order they *first reached* frequency 1
* `group[2]` = values in the order they *reached* frequency 2
* `group[3]` = values in the order they *reached* frequency 3
  …and so on.

When an element becomes frequency `f` during `push`, we do:

* `group[f].push(val)` (in your code: `addLast(val)`)

So, **within a fixed frequency bucket**, the newest element is at the top of that bucket’s stack — exactly what tie-breaking needs.

### Why we don’t move elements on `pop()`

This is the subtle part.

Suppose `val` currently has frequency `f`. That means there are `f` pushes of `val` that haven’t been popped yet.

When we `pop()` it once:

* Its frequency becomes `f-1`
* The “most recent remaining push” of `val` is now its **(f−1)-th push**
* And **the (f−1)-th push is exactly when it previously reached frequency (f−1)**

That push already placed `val` into `group[f-1]` at the correct position long ago.
So we don’t need to push it again or “move it” on pop — it’s already represented correctly.

This is why the design stays **O(1)**.

---

## Example walkthrough (classic test)

Operations:

```
push(5)
push(7)
push(5)
push(7)
push(4)
push(5)
pop()
pop()
pop()
pop()
```

I’ll show:

* `freq` map
* `maxFreq`
* `group[f]` stacks (bottom → top)

### 1) push(5)

* freq: {5=1}
* maxFreq = 1
* group[1]: [5]

### 2) push(7)

* freq: {5=1, 7=1}
* maxFreq = 1
* group[1]: [5, 7]   (7 is most recent at freq 1)

### 3) push(5)

* freq: {5=2, 7=1}
* maxFreq = 2
* group[1]: [5, 7]
* group[2]: [5]      (5 just reached freq 2)

### 4) push(7)

* freq: {5=2, 7=2}
* maxFreq = 2
* group[2]: [5, 7]   (7 most recent among freq 2)

### 5) push(4)

* freq: {5=2, 7=2, 4=1}
* maxFreq = 2
* group[1]: [5, 7, 4]
* group[2]: [5, 7]

### 6) push(5)

* freq: {5=3, 7=2, 4=1}
* maxFreq = 3
* group[3]: [5]      (5 reached freq 3)

---

## Now pops

### 7) pop()

We pop from `group[maxFreq] = group[3]`:

* group[3] top is 5 → return 5

After pop:

* freq becomes {5=2, 7=2, 4=1}
* group[3] becomes empty → maxFreq drops to 2

✅ pop() = **5**

---

### 8) pop()

Now maxFreq=2 → pop from group[2] = [5, 7]

* top is 7 → return 7

After pop:

* freq becomes {5=2, 7=1, 4=1}
* group[2] now [5] (not empty) → maxFreq stays 2

✅ pop() = **7**

---

### 9) pop()

maxFreq=2 → group[2] is [5]

* top is 5 → return 5

After pop:

* freq becomes {5=1, 7=1, 4=1}
* group[2] becomes empty → maxFreq drops to 1

✅ pop() = **5**

---

### 10) pop()

maxFreq=1 → group[1] is [5, 7, 4]

* top is 4 → return 4 (most recent among all freq=1 elements)

After pop:

* freq becomes {5=1, 7=1} (4 removed)
* group[1] now [5, 7] (maxFreq remains 1)

✅ pop() = **4**

---

## Final pop sequence

**[5, 7, 5, 4]** — which matches the expected behavior.

---

## Time and space complexity

* `push`: **O(1)** average
* `pop`: **O(1)** average
* Space: **O(n)** for storing pushes in the frequency stacks
*/

class FreqStack {

    private int maxFreq;
    private final Map<Integer, Integer> freq;
    private final Map<Integer, Deque<Integer>> group;

    public FreqStack() {
        maxFreq = 0;
        freq = new HashMap<>();
        group = new HashMap<>();
    }

    public void push(int val) {
        // 1) increment frequency of val
        int newFreq = freq.getOrDefault(val, 0) + 1;
        freq.put(val, newFreq);

        // 2) update maxFreq if needed
        if (newFreq > maxFreq) {
            maxFreq = newFreq;
        }

        // 3) push val into the stack for this frequency
        group.computeIfAbsent(newFreq, f -> new ArrayDeque<>()).addLast(val);
    }

    public int pop() {
        // We always pop from the stack of maxFreq.
        Deque<Integer> stack = group.get(maxFreq);

        // removeLast() matches "stack pop" behavior; assumes valid pop calls.
        int val = stack.removeLast();

        // decrement frequency
        int f = freq.get(val) - 1;
        if (f == 0) {
            freq.remove(val);          // optional cleanup
        } else {
            freq.put(val, f);
        }

        // if no more elements at maxFreq, decrease maxFreq
        if (stack.isEmpty()) {
            group.remove(maxFreq);      // optional cleanup
            maxFreq--;
        }

        return val;
    }
}


/**
 * Your FreqStack object will be instantiated and called as such:
 * FreqStack obj = new FreqStack();
 * obj.push(val);
 * int param_2 = obj.pop();
 */