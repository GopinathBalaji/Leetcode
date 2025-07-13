// Note: The input is the decimal number, not binary as shown
// Return is expected in Binary, not decimal as shown

// Converting to string first
public class Solution {
    // you need treat n as an unsigned value
    public int reverseBits(int n) {
        // Convert given integer value to binary
        String binary = Integer.toBinaryString(n);

        // Converted binary string may be shorter than 32 bits (because leading 0s may be dropped) so pad with 0s
        while(binary.length() < 32){
            binary = "0" + binary;
        }

        String reversed = new StringBuilder(binary).reverse().toString();

        // Parse back to int
        return (int) Long.parseLong(reversed, 2);
    }
}


// Bitwise solution without strings
/*
Example:

```
Input:  n = 43261596
Binary: 00000010100101000001111010011100
Output: 964176192
Binary: 00111001011110000010100101000000
```

## \U0001f50d Detailed Explanation

### Step-by-step with an example:

Let’s say `n = 5`, which in binary is:

```
00000000000000000000000000000101
```

We want to reverse this to:

```
10100000000000000000000000000000
```

We initialize:

```java
int result = 0;
```

Then, we loop 32 times (because an int in Java is 32 bits):

---

### \U0001f501 Loop: 32 Iterations

Let’s go through the core steps inside the loop:

```java
result <<= 1;
```

* Left shift `result` by 1 bit (multiplies by 2).
* Creates space for the next incoming bit from `n`.

```java
result |= (n & 1);
```

* `n & 1` extracts the **least significant bit** (LSB) of `n`.
* `|=` sets that bit in `result`.

```java
n >>>= 1;
```

* Unsigned right shift `n` by 1 bit (shifts everything right and fills in with **0s**, even for negative values).

---

### \U0001f9e0 Let's go through a few iterations with `n = 5`:

Initial:

```
n     = 00000000000000000000000000000101 (5)
result= 00000000000000000000000000000000
```

#### Iteration 1:

* `result <<= 1` → still `000...000`
* `n & 1 = 1`, so `result |= 1` → result becomes `...0001`
* `n >>>= 1` → n becomes `...00000010` (2)

#### Iteration 2:

* `result <<= 1` → `...0010`
* `n & 1 = 0`, `result |= 0` → still `...0010`
* `n >>>= 1` → n becomes `...00000001` (1)

#### Iteration 3:

* `result <<= 1` → `...0100`
* `n & 1 = 1`, `result |= 1` → result = `...0101`
* `n >>>= 1` → n = `0`

After 32 iterations, the bits of `n` have been **reversed into `result`**.

---

## \U0001f9ee Bit Behavior Summary

| Operation      | Effect                                                   |                                      |
| -------------- | -------------------------------------------------------- | ------------------------------------ |
| `result <<= 1` | Moves result bits left to make space for next bit        |                                      |
| `n & 1`        | Gets the current least significant bit of `n` (0 or 1)   |                                      |
| \`result       | = ...\`                                                  | Inserts that bit into the new result |
| `n >>>= 1`     | Drops the LSB of `n` and brings next bit to LSB position |                                      |

---

## ✅ Why Use `>>>` Instead of `>>`?

* `>>>` is **unsigned right shift**. It fills in **0s** on the left.
* `>>` (signed shift) would fill in `1` if `n` is negative, which would corrupt the result.
* Since we are reversing bits of a **32-bit unsigned integer**, we need `>>>`.

---

## \U0001f9e0 Time & Space Complexity

| Metric          | Value                            |
| --------------- | -------------------------------- |
| Time Complexity | `O(32)` = `O(1)` (constant time) |
| Space           | `O(1)`                           |

No arrays or strings used, so this is the **most optimal solution**.

---

## ✅ Output Example

For `n = 43261596`
Binary:

```
00000010100101000001111010011100
```

Reversed:

```
00111001011110000010100101000000
```

Which is: `964176192`

*/

// public class Solution {
//     public int reverseBits(int n) {
//         int result = 0;

//         for (int i = 0; i < 32; i++) {
//             result <<= 1;         // Step 1: Left shift result by 1 bit
//             result |= (n & 1);    // Step 2: Copy the least significant bit of n into result
//             n >>>= 1;             // Step 3: Unsigned right shift n by 1
//         }

//         return result;
//     }
// }