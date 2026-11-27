// Method 1: Math
/*
## **LeetCode 7: Reverse Integer (C++)**

**Approach: Math + Overflow Handling | Difficulty: Medium**

### **Hint 1: Think Digit Manipulation**

Given a signed 32-bit integer `x`, reverse its digits.

**Key idea:** Extract the last digit using modulo and build the reversed integer.

### **Hint 2: Extract the Last Digit**

Use `% 10` to get the last digit and `/ 10` to remove it.

```
int digit = x % 10;
x /= 10;
```

**Important:** C++ preserves the sign of the dividend when using `%`, so this also works for negative integers.

### **Hint 3: Build the Reversed Integer**

```
result = result * 10 + digit;
```

For example, `123` becomes:

`0 → 3 → 32 → 321`

### **Hint 4: Handle Integer Overflow**

Before multiplying `result` by 10, ensure it stays within the signed 32-bit range.

```
if (result > INT_MAX / 10 ||
    (result == INT_MAX / 10 && digit > 7))
    return 0;

if (result < INT_MIN / 10 ||
    (result == INT_MIN / 10 && digit < -8))
    return 0;
```

**Why 7 and -8?** The signed 32-bit range is:

- `INT_MAX = 2147483647`
- `INT_MIN = -2147483648`

### **Hint 5: Repeat Until x Is Zero**

```
while (x != 0) {
    int digit = x % 10;
    x /= 10;
}
```

### **Complete C++ Solution**

```
class Solution {
public:
    int reverse(int x) {
        int result = 0;

        while (x != 0) {
            int digit = x % 10;
            x /= 10;

            if (result > INT_MAX / 10 ||
                (result == INT_MAX / 10 && digit > 7))
                return 0;

            if (result < INT_MIN / 10 ||
                (result == INT_MIN / 10 && digit < -8))
                return 0;

            result = result * 10 + digit;
        }

        return result;
    }
};
```

### **Complexity**

- **Time:** O(log |x|) — Process each digit once.
- **Space:** O(1) — Only two variables.

For a 32-bit integer, at most 10 digits are processed.

### **Example**

```
x = -123;
```

| Iteration | Digit | Result |
| --------- | ----- | ------ |
| 1         | -3    | -3     |
| 2         | -2    | -32    |
| 3         | -1    | -321   |

**Answer:&#x20;**`-321`

### **Key Intuition**

**Modulo + Division + Overflow Check = O(log |x|)**

- `x % 10` → Extract last digit.
- `x / 10` → Remove last digit.
- `result * 10 + digit` → Append digit.
- Check overflow **before** multiplying.

**Interview tip:** The biggest trap is integer overflow. Don't reverse the entire number first and check afterward, because overflow may already have occurred.

Also, avoid `abs(x)` because `abs(INT_MIN)` cannot be represented by a signed 32-bit integer.
*/
class Solution {
public:
    int reverse(int x) {
        int result = 0;

        while(x != 0){
            int digit = x % 10;
            x /= 10;

            if(result > INT_MAX / 10 || (result == INT_MAX / 10 && digit > 7)){
                return 0;
            }

            if(result < INT_MIN / 10 || (result == INT_MIN / 10 && digit < -8)){
                return 0;
            }

            result = result * 10 + digit;
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna