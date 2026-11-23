// Method 1: Two Pointer
/*
## **LeetCode 67: Add Binary (C++)**

**Approach: Two Pointers + Carry | Difficulty: Easy**

### **Hint 1: Think Binary Addition**

You're given two binary strings `a` and `b`. Return their sum as a binary string.

**Key idea:** Add digits from right to left, just like regular addition.

### **Hint 2: Initialize Two Pointers**

Start from the last character of each string.

```
int i = a.size() - 1;
int j = b.size() - 1;
int carry = 0;
string result = "";
```

### **Hint 3: Calculate the Sum**

Add the current digits and any existing carry.

```
int sum = carry;

if (i >= 0)
    sum += a[i--] - '0';

if (j >= 0)
    sum += b[j--] - '0';
```

**Why subtract&#x20;**`'0'`**?** It converts a character digit into its integer value.

### **Hint 4: Calculate the Binary Digit and Carry**

```
result += (sum % 2) + '0';
carry = sum / 2;
```

- `sum % 2` gives the current binary digit.
- `sum / 2` gives the carry for the next position.

### **Hint 5: Handle Remaining Digits**

Continue while either string has digits remaining or a carry exists.

```
while (i >= 0 || j >= 0 || carry)
```

Since digits are appended in reverse order, reverse the result before returning.

### **Complete C++ Solution**

```
class Solution {
public:
    string addBinary(string a, string b) {
        int i = a.size() - 1;
        int j = b.size() - 1;
        int carry = 0;
        string result = "";

        while (i >= 0 || j >= 0 || carry) {
            int sum = carry;

            if (i >= 0)
                sum += a[i--] - '0';

            if (j >= 0)
                sum += b[j--] - '0';

            result += (sum % 2) + '0';
            carry = sum / 2;
        }

        reverse(result.begin(), result.end());

        return result;
    }
};
```

### **Complexity**

Let `n = a.size()` and `m = b.size()`.

- **Time:** O(max(n, m)) — Process each binary digit once.
- **Space:** O(max(n, m)) — Result string; O(1) auxiliary space excluding output.

### **Example**

```
a = "1010"
b = "1011"
```

Binary addition:

```
    1010
  + 1011
  ------
   10101
```

**Answer:&#x20;**`"10101"`

### **Key Intuition**

**Two Pointers + Carry = O(max(n, m))**

The binary addition rules are:

| Sum | Result Bit | Carry |
| --- | ---------- | ----- |
| 0   | 0          | 0     |
| 1   | 1          | 0     |
| 2   | 0          | 1     |
| 3   | 1          | 1     |

**Interview tip:** Don't convert the entire binary strings into integers. They can be too large for standard integer types.

Instead, perform digit-by-digit addition, just like **LeetCode 415: Add Strings**.
*/
class Solution {
public:
    string addBinary(string a, string b) {
        int i = a.size() - 1;
        int j = b.size() - 1;

        int carry = 0;
        string result = "";

        while(i >=0 || j >= 0 || carry){
            int sum = carry;

            if(i >= 0){
                sum += a[i--] - '0';
            }

            if(j >= 0){
                sum += b[j--] - '0';
            }

            result += (sum % 2) + '0';
            carry = sum / 2;
        }

        std::reverse(result.begin(), result.end());

        return result;       
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna