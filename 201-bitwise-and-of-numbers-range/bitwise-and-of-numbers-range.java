// Right shift to find common prefix
/*
## \U0001f9e0 Problem Statement

> Given two integers `left` and `right`, return the bitwise AND of all numbers in the inclusive range `[left, right]`.

---

## ❓ Key Observation

For any bit position:

* If that bit **flips even once** in the range `[left, right]`, it will become **0** in the final result of the AND operation.

So, we want to keep **only the common prefix bits** between `left` and `right`.

---

## \U0001f50d Example

Let’s say:

```java
left  = 26  → 11010
right = 30  → 11110
```

Now let's list all numbers in between:

```
11010 → 26
11011 → 27
11100 → 28
11101 → 29
11110 → 30
```

If you do `&` across all of these:

```
11010
& 11011 → 11010
& 11100 → 11000
& 11101 → 11000
& 11110 → 11000
```

✅ Final result: `11000` = `24`

Notice:

* The **leftmost 3 bits** (`110`) remain unchanged throughout the range.
* The **last 2 bits** (positions 1 and 0) flip → they become 0 in the final result.

So, we need to:

> Find the common prefix between `left` and `right`, and zero out everything after.

---

## ✅ Algorithm (Common Prefix Approach)

### Steps:

1. While `left != right`:

   * Right-shift both `left` and `right` by 1.
   * Count how many times you shifted.

2. When they become equal, it means we've found the common prefix.

3. Left-shift the result back by the shift count to restore the bits to their original positions.


## \U0001f50e Detailed Walkthrough of Example (`left = 26`, `right = 30`)

Binary:

```
left  = 11010
right = 11110
```

### Step 1: Shifting to find common prefix

| Shift # | left (>>=1) | right (>>=1)                |
| ------- | ----------- | --------------------------- |
| 0       | 11010 (26)  | 11110 (30)                  |
| 1       | 1101  (13)  | 1111  (15)                  |
| 2       | 110   (6)   | 111   (7)                   |
| 3       | 11    (3)   | 11    (3)     ✅ match found |

### Step 2: Shift result back 3 times

```
left = 11 (3)
3 << 3 = 24 = 11000
```

✅ Final answer: **24**

---

## \U0001f9ee Time & Space Complexity

| Metric           | Value    |
| ---------------- | -------- |
| Time Complexity  | O(log N) |
| Space Complexity | O(1)     |

---

## ✅ Summary

| Goal                        | Method                                      |
| --------------------------- | ------------------------------------------- |
| Remove bits that vary       | Use right-shift until `left == right`       |
| Preserve common prefix      | Restore it using left-shift                 |
| Guarantees shortest runtime | Because it avoids simulating the full range |

*/
class Solution {
    public int rangeBitwiseAnd(int left, int right) {
        int shift = 0;

        // Find the common prefix
        while (left < right) {
            left >>= 1;
            right >>= 1;
            shift++;
        }

        // Shift back to get the final result
        return left << shift;
    }
}