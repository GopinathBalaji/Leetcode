// Reversing only half of the number and checking if they are equal
/*
Certainly! Let’s walk through the **logic behind the code** for checking whether an integer is a palindrome — **without converting it to a string**, and **by reversing only half of the number**.

---

### \U0001f50d Problem:

Check if an integer reads the same forward and backward (e.g., 121, 1221), without converting it to a string.

---

### ✅ Key Idea:

Instead of reversing the **entire number** (which can cause overflow), we **reverse only half** of the digits and compare the two halves.

---

### \U0001f9e0 Logic Breakdown:

```java
public boolean isPalindrome(int x) {
```

* We define a function that returns `true` if `x` is a palindrome, `false` otherwise.

---

#### \U0001f6d1 Step 1: Early Exit for Invalid Cases

```java
if (x < 0 || (x % 10 == 0 && x != 0)) return false;
```

* If `x < 0`: it's negative — never a palindrome (e.g., -121).
* If `x % 10 == 0 && x != 0`: it's divisible by 10 but not 0 — can't be a palindrome.

  * e.g., `10` → reversed is `01`, not valid.

---

#### \U0001f504 Step 2: Reverse Half the Number

```java
int reversed = 0;
while (x > reversed) {
    reversed = reversed * 10 + x % 10;
    x /= 10;
}
```

Let’s take an example: `x = 1221`

1. `reversed = 0`
2. First iteration:

   * `x % 10 = 1`
   * `reversed = 0 * 10 + 1 = 1`
   * `x = 122`
3. Second iteration:

   * `x % 10 = 2`
   * `reversed = 1 * 10 + 2 = 12`
   * `x = 12`

Now: `x == reversed` → both are `12` → we stop.

This works because:

* For **even-length palindromes**, the two halves become equal.
* For **odd-length palindromes**, the middle digit doesn't matter, so we divide `reversed` by 10.

---

#### ✅ Step 3: Check Equality

```java
return x == reversed || x == reversed / 10;
```

* If even digits (like 1221), `x == reversed` (both become 12).
* If odd digits (like 12321):

  * At end, `x = 12`, `reversed = 123`
  * Ignore the middle digit by `reversed / 10 = 12`
  * So again: `x == reversed / 10`

---

### \U0001f501 Why It Works:

| Original `x` | Reversed half | Reason             |
| ------------ | ------------- | ------------------ |
| 1221         | 12            | x == reversed      |
| 12321        | 123           | x == reversed / 10 |
| 10           | 1             | not equal → false  |

---

### \U0001f680 Time and Space Complexity:

* **Time**: O(log₁₀(n)) — We process half the digits.
* **Space**: O(1) — Constant space, no array/string used.

---
*/
class Solution {
    public boolean isPalindrome(int x) {
    if (x < 0 || (x % 10 == 0 && x != 0)) return false;

    int reversed = 0;
    while (x > reversed) {
        reversed = reversed * 10 + x % 10;
        x /= 10;
    }

    // For even-length numbers: x == reversed
    // For odd-length numbers: x == reversed / 10
    return x == reversed || x == reversed / 10;
    }
}