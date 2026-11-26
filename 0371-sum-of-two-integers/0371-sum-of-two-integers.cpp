// Method 1: XOR and AND
/*
## **LeetCode 371: Sum of Two Integers (C++)**

**Approach: Bit Manipulation | Difficulty: Medium**

### **Hint 1: Think Binary Addition**

Calculate the sum of two integers **without using&#x20;**`+`**&#x20;or&#x20;**`-`.

**Key idea:** Use XOR for addition without carry and AND for finding carry bits.

### **Hint 2: XOR Gives Sum Without Carry**

```
int sum = a ^ b;
```

Example:

```
  0101 (5)
^ 0011 (3)
------
  0110 (6)
```

XOR adds bits while ignoring carries.

### **Hint 3: AND Finds Carry Bits**

```
int carry = a & b;
```

Shift the carry left by one position:

```
carry <<= 1;
```

**Why?** A carry generated at bit position `i` must be added at position `i + 1`.

### **Hint 4: Repeat Until No Carry**

```
while (y != 0) {
    uint32_t carry = (x & y) << 1;
    x = x ^ y;
    y = carry;
}
```

Use unsigned integers for the bitwise calculations to avoid undefined behavior when shifting negative signed integers.

### **Complete C++ Solution**

```
class Solution {
public:
    int getSum(int a, int b) {
        uint32_t x = a;
        uint32_t y = b;

        while (y != 0) {
            uint32_t carry = (x & y) << 1;
            x = x ^ y;
            y = carry;
        }

        return static_cast<int32_t>(x);
    }
};
```

The final conversion assumes the usual two's-complement integer representation used by LeetCode.

### **Complexity**

- **Time:** O(1) — At most 32 iterations for 32-bit integers.
- **Space:** O(1) — Constant extra space.

### **Example**

```
a = 2;
b = 3;
```

| Iteration | x (sum)   | y (carry) |
| --------- | --------- | --------- |
| Initial   | `010` (2) | `011` (3) |
| 1         | `001` (1) | `100` (4) |
| 2         | `101` (5) | `000` (0) |

**Answer:&#x20;**`5`

### **Key Intuition**

**XOR + AND + Left Shift = Binary Addition**

- `a ^ b` → Sum without carry.
- `a & b` → Find carry bits.
- `(a & b) << 1` → Move carries to the next position.
- Repeat until no carry remains.

**Interview tip:** Remember that XOR acts like addition without carrying, while AND identifies where carries are needed. Together, they simulate binary addition without arithmetic operators.
*/
class Solution {
public:
    int getSum(int a, int b) {
        uint32_t x = a;
        uint32_t y = b;

        while(y != 0){
            uint32_t carry = (x & y) << 1;
            x = x ^ y;
            y = carry;
        }

        return static_cast<int32_t>(x);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna