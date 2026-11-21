// Method 1: Bitwise AND
/*
## **LeetCode 191: Number of 1 Bits (C++)**

**Approach: Bit Manipulation | Difficulty: Easy**

### **Hint 1: Think Bitwise AND**

You need to count the number of `1` bits in the binary representation of an integer.

**Key idea:** Use `n & 1` to check whether the rightmost bit is `1`.

```
if (n & 1)
    count++;
```

### **Hint 2: Shift Bits Right**

After checking the rightmost bit, shift the number right by one position.

```
n >>= 1;
```

This removes the rightmost bit so you can check the next one.

### **Hint 3: Repeat Until Zero**

```
while (n != 0) {
    count += (n & 1);
    n >>= 1;
}
```

### **Hint 4: Optimize Using Brian Kernighan's Algorithm**

Instead of checking every bit, **remove the rightmost set bit** each iteration.

```
n = n & (n - 1);
```

Each operation removes exactly one `1` bit.

### **Complete C++ Solution**

```
class Solution {
public:
    int hammingWeight(int n) {
        int count = 0;

        while (n != 0) {
            n &= (n - 1);
            count++;
        }

        return count;
    }
};
```

### **Complexity**

- **Time:** O(k), where `k` is the number of set bits. At most 32 iterations for a 32-bit integer.
- **Space:** O(1).

### **Example**

```
n = 11;
```

Binary representation: `1011`

| Iteration | Binary | Operation                     |
| --------- | ------ | ----------------------------- |
| 1         | `1011` | Remove rightmost `1` → `1010` |
| 2         | `1010` | Remove rightmost `1` → `1000` |
| 3         | `1000` | Remove rightmost `1` → `0000` |

**Answer:&#x20;**`3`

### **Key Intuition**

**Bitwise AND + Removing Set Bits = O(k)**

The expression `n & (n - 1)` clears the lowest set bit without affecting the others.

**Interview tip:** Remember these bit manipulation patterns:

- `n & 1` → Check the rightmost bit.
- `n >> 1` → Shift right by one bit.
- `n & (n - 1)` → Remove the rightmost set bit.
- `n & -n` → Isolate the rightmost set bit.

For this problem, **Brian Kernighan's algorithm** is the most efficient and elegant approach.
*/
class Solution {
public:
    int hammingWeight(int n) {
        int count = 0;

        while(n != 0){
            n = n & (n-1);
            count++;
        }

        return count;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna