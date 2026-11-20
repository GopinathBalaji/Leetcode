// Method 1: XOR
/*
## **LeetCode 136: Single Number (C++)**

**Approach: Bit Manipulation (XOR) | Difficulty: Easy**

### **Hint 1: Think XOR**

Every number appears exactly twice except one.

**Key idea:** Use the XOR (`^`) operator to cancel duplicate numbers.

### **Hint 2: Understand XOR Properties**

Three important properties:

```
a ^ a = 0
a ^ 0 = a
a ^ b ^ a = b
```

- Identical numbers cancel each other.
- XOR with zero leaves the number unchanged.
- XOR is commutative and associative, so order doesn't matter.

### **Hint 3: Initialize a Variable**

```
int result = 0;
```

### **Hint 4: XOR Every Element**

```
for (int num : nums) {
    result ^= num;
}
```

All duplicate numbers cancel, leaving only the unique number.

### **Complete C++ Solution**

```
class Solution {
public:
    int singleNumber(vector<int>& nums) {
        int result = 0;

        for (int num : nums) {
            result ^= num;
        }

        return result;
    }
};
```

### **Complexity**

- **Time:** O(n) — Single traversal.
- **Space:** O(1) — Only one variable.

### **Example**

```
nums = {4, 1, 2, 1, 2};
```

XOR operations:

```
0 ^ 4 = 4
4 ^ 1 = 5
5 ^ 2 = 7
7 ^ 1 = 6
6 ^ 2 = 4
```

**Answer:&#x20;**`4`

### **Key Intuition**

**XOR + Duplicate Cancellation = O(n)**

Because each duplicate appears exactly twice, all pairs cancel out.

**Interview tip:** When a problem asks you to find a unique element while all others appear exactly twice, think **XOR** before using a hash map.

It's especially useful when the problem requires **O(n) time and O(1) extra space**.
*/
class Solution {
public:
    int singleNumber(vector<int>& nums) {
        int result = 0;

        for(int num : nums){
            result ^= num;
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna