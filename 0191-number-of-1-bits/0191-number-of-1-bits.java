// Method 1 : Looping through all 32 bits
// Using unsigned right shift since it is safe for negative numbers too
class Solution {
    public int hammingWeight(int n) {
        int count = 0;

        for(int i=0; i<32; i++){
            count += (n >>> i) & 1;
        }

        return count;   
    }
}


// Method 2: Brian Kernighan's Algorithm
/*
The logic behind `n &= (n - 1)` is a beautiful and efficient bit manipulation trick, known as **Brian Kernighan's Algorithm**. It's often used to count the number of `1` bits (also called the **Hamming weight**) in a binary number.

---

## \U0001f50d Statement:

```java
n &= (n - 1);
```

This **clears (removes) the lowest set bit (rightmost 1)** in `n`.

---

## ✅ Why does this work?

### Binary properties:

* Subtracting 1 from a number **flips all bits after the rightmost `1`**, including that `1` itself.
* Doing a bitwise **AND** with the original number then clears that rightmost `1`.

---

### \U0001f527 Example:

Let’s say:

```
n =  110100   (binary for 52)
n-1 = 110011  (binary for 51)

n & (n - 1) = 110000  (binary for 48)
```

Only the **rightmost `1` bit** in `n` was removed.

---

### \U0001f9e0 Visual Step-by-Step (for `n = 13` = `0b1101`):

#### Step 0:

```
n      = 1101
n - 1  = 1100
n & n-1= 1100   ← 1st 1 removed (LSB)
```

#### Step 1:

```
n      = 1100
n - 1  = 1011
n & n-1= 1000   ← 2nd 1 removed
```

#### Step 2:

```
n      = 1000
n - 1  = 0111
n & n-1= 0000   ← 3rd 1 removed, done
```

Now `n` is 0 — loop stops after **3 iterations**, which is the number of `1`s in original `n`.

---

## ✅ Summary

| Operation        | Effect                                  |
| ---------------- | --------------------------------------- |
| `n & (n - 1)`    | Clears the **lowest (rightmost) 1 bit** |
| Why useful?      | Lets us count the number of 1s in O(k)  |
| k = number of 1s | Fastest possible way to count 1 bits    |

---


*/

// public class Solution {
//     public int hammingWeight(int n) {
//         int count = 0;

//         while(n != 0){
//             //remove lowest set bit (rightmost 1) in n. 
//             n &= (n - 1);

//             count ++;
//         }

//         return count;
//     }
// }