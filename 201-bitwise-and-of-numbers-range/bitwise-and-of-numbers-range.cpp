// Method 1: Bit Manipulation
/*
## **LeetCode 201: Bitwise AND of Numbers Range (C++)**

**Approach: Bit Manipulation | Difficulty: Medium**

### **Hint 1: Think Common Binary Prefix**

Given two integers `left` and `right`, compute the bitwise AND of every number in `[left, right]`.

**Key idea:** Only the common leading binary bits of `left` and `right` survive.

### **Hint 2: Understand Why Bits Become Zero**

Consider:

```
left  = 5 → 101
             110 → 6
right = 7 → 111
```

Bitwise AND:

```
101
110
111
---
100
```

**Answer:&#x20;**`4`

The lower bits become zero because they change somewhere within the range.

### **Hint 3: Find the Common Prefix**

Keep shifting both numbers right until they become equal.

```
while (left != right) {
    left >>= 1;
    right >>= 1;
    shifts++;
}
```

This removes the differing bits.

### **Hint 4: Restore the Common Bits**

Once both numbers are equal, shift the common prefix back to its original position.

```
return left << shifts;
```

### **Complete C++ Solution**

```
class Solution {
public:
    int rangeBitwiseAnd(int left, int right) {
        int shifts = 0;

        while (left != right) {
            left >>= 1;
            right >>= 1;
            shifts++;
        }

        return left << shifts;
    }
};
```

### **Complexity**

- **Time:** O(log M), where `M` is the larger endpoint.
- **Space:** O(1) — Constant extra space.

### **Example**

```
left = 5;
right = 7;
```

| Iteration | left  | right | shifts |
| --------- | ----- | ----- | ------ |
| Initial   | `101` | `111` | 0      |
| 1         | `10`  | `11`  | 1      |
| 2         | `1`   | `1`   | 2      |

Restore the prefix:

```
1 << 2 = 4;
```

**Answer:&#x20;**`4`

### **Alternative: Brian Kernighan's Algorithm**

Instead of shifting both endpoints, repeatedly clear the rightmost set bit of `right`.

```
class Solution {
public:
    int rangeBitwiseAnd(int left, int right) {
        while (left < right) {
            right &= (right - 1);
        }

        return right;
    }
};
```

### **Key Intuition**

**Common Binary Prefix = Range Bitwise AND**

Any bit that changes within the range becomes `0` in the final answer.

**Interview tip:** Remember these related problems:

- **191. Number of 1 Bits:** Count set bits using `n & (n - 1)`.
- **338. Counting Bits:** Use DP to count set bits.
- **201. Bitwise AND of Numbers Range:** Find the common binary prefix.

The main trick is recognizing that you **don't need to iterate through every number in the range**.
*/
class Solution {
public:
    int rangeBitwiseAnd(int left, int right) {
        int shifts = 0;

        while(left != right){
            left >>= 1;
            right >>= 1;

            shifts++;
        }

        return left << shifts;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna