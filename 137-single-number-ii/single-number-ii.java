// Modulo 3 tracking for each bit across the array

/*
## \U0001f9e0 Key Insight

We will **simulate a counter modulo 3 for each bit** using two bitmasks:

* `ones`: stores bits that have appeared once
* `twos`: stores bits that have appeared twice

Together, these variables keep track of each bit’s **state** using logic similar to a **finite state machine**.

---

## ✅ Algorithm Intuition (Base-3 Logic):

Let’s say we want to track how many times a particular bit has been seen.

* If a bit appears the 1st time → it goes into `ones`
* If it appears the 2nd time → move it from `ones` to `twos`
* If it appears the 3rd time → remove it from both `ones` and `twos`

This is how we implement modulo 3 tracking for **each bit across the entire array**.

---

## \U0001f50d Step-by-Step Example

Let’s walk through `[2, 2, 3, 2]`:

### Binary values:

```
2 →  0010
3 →  0011
```

### First iteration (num = 2):

```
ones = 0 ^ 2 = 2
twos = 0 | (0 & 2) = 0
common_mask = ~(2 & 0) = ~0 = -1
```

### Second iteration (num = 2 again):

```
twos = 0 | (2 & 2) = 2
ones = 2 ^ 2 = 0
common_mask = ~(0 & 2) = ~0 = -1
```

### Third iteration (num = 3):

```
twos = 2 | (0 & 3) = 2
ones = 0 ^ 3 = 3
common_mask = ~(3 & 2) = ~(0011 & 0010) = ~0010 = 1101
ones &= 1101 → 0011 & 1101 = 0001
twos &= 1101 → 0010 & 1101 = 0000
```

### Final:

* `ones = 0001` = 3 (correct answer)

---

## ✅ Why It Works

This method **emulates modulo-3 counters at the bit level** without using any extra memory like arrays or hashmaps.

It ensures:

* Bits that appear 3 times are removed.
* The bits that appear **exactly once** remain in `ones`.

---

## ✅ Time & Space Complexity

| Metric       | Value                |
| ------------ | -------------------- |
| Time         | `O(n)`               |
| Space        | `O(1)`               |
| Extra Memory | 2 Integers (bitmask) |

---

## \U0001f9e0 Summary

| Variable         | Meaning                         |
| ---------------- | ------------------------------- |
| `ones`           | Bits seen exactly once          |
| `twos`           | Bits seen exactly twice         |
| `~(ones & twos)` | Mask to clear bits seen 3 times |

This solution is **fast**, **elegant**, and **interview-friendly** — a must-know trick for bit manipulation.
*/
class Solution {
    public int singleNumber(int[] nums) {
        int ones = 0;  // to track bits seen once
        int twos = 0;  // to track bits seen twice

        for (int num : nums) {
            // Step 1: Add current bits to 'twos' if they are already in 'ones'
            twos |= ones & num;

            // Step 2: XOR current bits into 'ones' (toggle bits)
            ones ^= num;

            // Step 3: Identify common bits in 'ones' and 'twos' (i.e., bits seen 3 times)
            int common_mask = ~(ones & twos);

            // Step 4: Remove those bits (that occurred 3 times) from 'ones' and 'twos'
            ones &= common_mask;
            twos &= common_mask;
        }

        // Only the bits that appeared once remain in 'ones'
        return ones;
    }
}