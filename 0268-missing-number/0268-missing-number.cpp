// Method 1: XOR 
/*
## **LeetCode 268: Missing Number (C++)**

**Approach: Bit Manipulation (XOR) | Difficulty: Easy**

### **Hint 1: Think XOR**

You're given an array containing `n` distinct numbers from the range `[0, n]`, with exactly one number missing.

**Key idea:** XOR all expected numbers with all actual numbers. Matching values cancel out, leaving the missing number.

### **Hint 2: Understand XOR Properties**

Remember:

- `a ^ a = 0` — Identical numbers cancel.
- `a ^ 0 = a` — XOR with zero changes nothing.
- XOR is commutative and associative, so order doesn't matter.

### **Hint 3: Initialize with n**

```
int result = nums.size();
```

**Why&#x20;**`n`**?** The array has `n` elements, but the expected range contains `n + 1` numbers: `0` through `n`.

### **Hint 4: XOR Indices and Values**

For every index, XOR the index and the corresponding array value.

```
for (int i = 0; i < nums.size(); i++) {
    result ^= i ^ nums[i];
}
```

All matching values cancel, leaving only the missing number.

### **Complete C++ Solution**

```
class Solution {
public:
    int missingNumber(vector<int>& nums) {
        int result = nums.size();

        for (int i = 0; i < nums.size(); i++) {
            result ^= i ^ nums[i];
        }

        return result;
    }
};
```

### **Complexity**

- **Time:** O(n) — Single traversal.
- **Space:** O(1) — Only one extra variable.

### **Example**

```
nums = {3, 0, 1};
```

Expected numbers: `{0, 1, 2, 3}`

XOR calculation:

```
result = 3

i = 0: 3 ^ 0 ^ 3 = 0
i = 1: 0 ^ 1 ^ 0 = 1
i = 2: 1 ^ 2 ^ 1 = 2
```

**Answer:&#x20;**`2`

### **Key Intuition**

**XOR + Cancellation = O(n)**

Every number appearing in both the expected range and the input cancels out. The missing number is the only value remaining.

### **Alternative: Mathematical Formula**

You can also use the sum of the first `n` natural numbers:

```
int n = nums.size();
int expected = n * (n + 1) / 2;
```

Then subtract the sum of the array. Use `long long` for the intermediate calculation to avoid integer overflow.

**Interview tip:** Remember the difference:

- **136. Single Number:** XOR duplicate elements to find the unique number.
- **268. Missing Number:** XOR expected and actual elements to find the missing number.

Both use **XOR cancellation** and achieve O(n) time with O(1) extra space.
*/
class Solution {
public:
    int missingNumber(vector<int>& nums) {
        int result = nums.size();

        for(int i=0; i<nums.size(); i++){
            result = result ^ i ^ nums[i];
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna