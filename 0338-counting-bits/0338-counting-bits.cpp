// Method 1: DP Approach
/*
## **LeetCode 338: Counting Bits (C++)**

**Approach: Dynamic Programming + Bit Manipulation | Difficulty: Easy**

### **Hint 1: Think DP**

Given an integer `n`, return an array where `ans[i]` contains the number of `1` bits in the binary representation of `i`.

**Key idea:** Reuse previously calculated bit counts instead of counting bits from scratch.

### **Hint 2: Understand the Pattern**

Observe the binary representations:

| Number | Binary | Set Bits |
| ------ | ------ | -------- |
| 0      | `000`  | 0        |
| 1      | `001`  | 1        |
| 2      | `010`  | 1        |
| 3      | `011`  | 2        |
| 4      | `100`  | 1        |
| 5      | `101`  | 2        |

Notice that shifting a number right by one removes its last bit.

### **Hint 3: DP Recurrence**

Use this formula:

```
ans[i] = ans[i >> 1] + (i & 1);
```

- `i >> 1`: Removes the rightmost bit.
- `i & 1`: Checks whether the rightmost bit is `1`.
- `ans[i >> 1]`: Reuses a previously computed result.

### **Hint 4: Initialize DP**

```
vector<int> ans(n + 1, 0);
```

Since `ans[0] = 0`, start calculating from `1`.

### **Hint 5: Fill the DP Array**

```
for (int i = 1; i <= n; i++) {
    ans[i] = ans[i >> 1] + (i & 1);
}
```

### **Complete C++ Solution**

```
class Solution {
public:
    vector<int> countBits(int n) {
        vector<int> ans(n + 1, 0);

        for (int i = 1; i <= n; i++) {
            ans[i] = ans[i >> 1] + (i & 1);
        }

        return ans;
    }
};
```

### **Complexity**

- **Time:** O(n) — Calculate each answer once.
- **Space:** O(n) — Output array; O(1) auxiliary space.

### **Example**

```
n = 5
```

| i | Binary | Calculation | ans[i] |
| - | ------ | ----------- | ------ |
| 0 | `000`  | Base case   | 0      |
| 1 | `001`  | ans[0] + 1  | 1      |
| 2 | `010`  | ans[1] + 0  | 1      |
| 3 | `011`  | ans[1] + 1  | 2      |
| 4 | `100`  | ans[2] + 0  | 1      |
| 5 | `101`  | ans[2] + 1  | 2      |

**Answer:** `[0, 1, 1, 2, 1, 2]`

### **Key Intuition**

**DP + Bit Manipulation = O(n)**

Every number's set-bit count equals the set-bit count of half that number, plus its last bit.

**Interview tip:** Another useful recurrence is:

```
ans[i] = ans[i & (i - 1)] + 1;
```

This uses Brian Kernighan's trick from **191. Number of 1 Bits** to remove the rightmost set bit.

Both approaches achieve O(n) time.
*/
class Solution {
public:
    vector<int> countBits(int n) {
        vector<int> ans(n + 1, 0);

        for(int i=1; i<=n; i++){
            ans[i] = ans[i >> 1] + (i & 1);
        }

        return ans;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna