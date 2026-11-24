// Method 1: Bitwise Operations
/*
## **LeetCode 190: Reverse Bits (C++)**

**Approach: Bit Manipulation | Difficulty: Easy**

### **Hint 1: Think Bitwise Operations**

Reverse the bits of a **32-bit unsigned integer**.

**Key idea:** Extract bits from right to left and build the reversed number from left to right.

### **Hint 2: Initialize Result**

```
uint32_t result = 0;
```

### **Hint 3: Extract the Rightmost Bit**

Use bitwise AND to get the last bit.

```
int bit = n & 1;
```

### **Hint 4: Build the Reversed Number**

Shift `result` left and append the extracted bit.

```
result = (result << 1) | (n & 1);
n >>= 1;
```

- `result << 1`: Makes room for the next bit.
- `n & 1`: Extracts the rightmost bit.
- `|`: Appends that bit.
- `n >>= 1`: Moves to the next bit.

### **Hint 5: Process Exactly 32 Bits**

**Important:** Don't stop when `n == 0`. Leading zeros in the original number become trailing zeros in the reversed number.

```
for (int i = 0; i < 32; i++) {
    result = (result << 1) | (n & 1);
    n >>= 1;
}
```

### **Complete C++ Solution**

```
class Solution {
public:
    uint32_t reverseBits(uint32_t n) {
        uint32_t result = 0;

        for (int i = 0; i < 32; i++) {
            result = (result << 1) | (n & 1);
            n >>= 1;
        }

        return result;
    }
};
```

### **Complexity**

- **Time:** O(1) — Exactly 32 iterations.
- **Space:** O(1) — Only one extra variable.

### **Example**

Input:

```
00000010100101000001111010011100
```

After reversing:

```
00111001011110000010100101000000
```

REVERSED INTEGER

# **964,176,192**

### **Key Intuition**

**Bitwise AND + Shift + OR = O(1)**

Extract the last bit of `n`, shift the result left, and append the extracted bit. Repeat 32 times.

**Interview tip:** Remember these bit manipulation patterns:

- `n & 1` → Extract rightmost bit.
- `n >> 1` → Remove rightmost bit.
- `result << 1` → Make space for a new bit.
- `result | bit` → Append a bit.

The most common mistake is using `while (n > 0)` instead of processing **all 32 bits**.
*/
class Solution {
public:
    int reverseBits(int n) {
        uint32_t result = 0;

        for(int i=0; i<32; i++){
            result = (result << 1) | (n & 1);
            n >>= 1;
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna