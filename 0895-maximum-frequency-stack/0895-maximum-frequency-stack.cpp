// Method 1: 2 layer bookkeeping A) map of freq of each entry, B) freq to stack map
/*
## Hint 1: Understand the rule

You need a stack-like data structure with:

```cpp
push(val)
pop()
```

But `pop()` should remove and return the element with the **highest frequency**.

If there is a tie, return the one that was pushed **most recently**.

Example:

```text
push(5)
push(7)
push(5)
push(7)
push(4)
push(5)
```

Frequencies:

```text
5 -> 3
7 -> 2
4 -> 1
```

So `pop()` returns:

```text
5
```

because `5` has the highest frequency.

---

## Hint 2: You need frequency counts

Maintain a hashmap:

```cpp
unordered_map<int, int> freq;
```

This stores how many times each value currently exists in the stack.

When pushing:

```cpp
freq[val]++;
```

When popping:

```cpp
freq[val]--;
```

---

## Hint 3: Track the current maximum frequency

You also need to know the highest frequency at any moment.

Use:

```cpp
int maxFreq = 0;
```

Whenever you push a value:

```cpp
maxFreq = max(maxFreq, freq[val]);
```

Then `pop()` should remove from the group with frequency `maxFreq`.

---

## Hint 4: Group values by frequency

The key trick is to store stacks of values by frequency.

Use:

```cpp
unordered_map<int, stack<int>> group;
```

Meaning:

```text
group[f] = stack of values that reached frequency f
```

For example, if `5` becomes frequency `3`, push it into:

```cpp
group[3].push(5);
```

If `7` becomes frequency `2`, push it into:

```cpp
group[2].push(7);
```

---

## Hint 5: Why stack per frequency solves recency tie

Suppose both `5` and `7` have frequency `2`.

The one pushed later should be popped first.

If every frequency group is a stack, then the most recently pushed value at that frequency is on top.

So for ties, just do:

```cpp
group[maxFreq].top()
```

---

## Hint 6: Push operation

When pushing `val`:

```cpp
freq[val]++;
int f = freq[val];

group[f].push(val);
maxFreq = max(maxFreq, f);
```

This records that `val` has now reached frequency `f`.

---

## Hint 7: Pop operation

To pop:

```cpp
int val = group[maxFreq].top();
group[maxFreq].pop();

freq[val]--;
```

Then, if the stack for `maxFreq` becomes empty, reduce `maxFreq`:

```cpp
if (group[maxFreq].empty()) {
    maxFreq--;
}
```

Return `val`.

---

## Hint 8: Why this works

Imagine:

```text
push(5) -> freq 1 -> group[1]: [5]
push(7) -> freq 1 -> group[1]: [5, 7]
push(5) -> freq 2 -> group[2]: [5]
push(7) -> freq 2 -> group[2]: [5, 7]
```

Now both `5` and `7` have frequency `2`.

Since `7` reached frequency `2` more recently, `group[2].top()` is `7`.

So `pop()` returns `7`.

---

## Core idea

Use:

```cpp
freq[value] = current frequency of value
group[frequency] = stack of values that reached this frequency
maxFreq = current maximum frequency
```

Then both operations can be efficient:

```text
push() -> O(1)
pop()  -> O(1)
```
*/
class FreqStack {
private:
    int maxFreq = 0;
    unordered_map<int, int> freq;
    unordered_map<int, stack<int>> group;

public:
    FreqStack() {
        
    }
    
    void push(int val) {
        freq[val]++;
        int f = freq[val];

        maxFreq = std::max(maxFreq, f);
        group[f].push(val);
    }
    
    int pop() {
        int val = group[maxFreq].top();
        group[maxFreq].pop();

        freq[val]--;

        if(group[maxFreq].empty()){
            maxFreq--;
        }

        return val;
    }
};

/**
 * Your FreqStack object will be instantiated and called as such:
 * FreqStack* obj = new FreqStack();
 * obj->push(val);
 * int param_2 = obj->pop();
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna