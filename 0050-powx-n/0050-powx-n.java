// Recursive halfing the power strategy
/*
## ✅ Problem Summary:

You're given a **base** `x` (a `double`) and an **exponent** `n` (an `int`). Return `x` raised to the power `n` — i.e., compute `xⁿ`.

---

## ✅ Constraints to keep in mind:

* `n` can be **negative**, zero, or positive.
* `n` can be as small as `Integer.MIN_VALUE` = `-2,147,483,648`, which cannot be negated using `Math.abs(n)` directly.
* We must aim for **logarithmic time complexity (O(log n))** to handle large values of `n`.

## \U0001f50d Step-by-Step Explanation:

### 1. **`myPow` function (public API)**

```java
if (x == 0) return 0;
```

* If the base is `0`, then the result is `0` (except when exponent is `0`, which is undefined but often treated as 1 mathematically; here, the next check handles it).

```java
if (n == 0) return 1;
```

* Any number to the power of `0` is `1`.

```java
long newN = Math.abs((long)n);
```

* We convert `n` to a `long` to avoid overflow when `n == Integer.MIN_VALUE`.
* Then we take the absolute value to simplify the logic (we'll handle negative exponents by inverting later).

```java
double ans = fastPow(x, newN);
```

* We call `fastPow` to compute `xⁿ` recursively in **logarithmic time**.

```java
return n < 0 ? 1 / ans : ans;
```

* If the original `n` was negative, return the **reciprocal** of the result.

---

### 2. **`fastPow` function (helper function using recursion)**

```java
if (n == 0) return 1;
```

* Base case: anything raised to power 0 is 1.

```java
double half = fastPow(x, n / 2);
```

* We divide the problem in half recursively — this is the core of **exponentiation by squaring**.
* This drastically reduces the number of multiplications (O(log n) time).

---

### \U0001f9e9 Rewritten return logic for clarity:

Instead of:

```java
return n % 2 == 0 ? half * half : half * half * x;
```

Use:

```java
if (n % 2 == 0) {
    // If exponent is even: x^n = (x^(n/2))^2
    return half * half;
} else {
    // If exponent is odd: x^n = (x^(n/2))^2 * x
    return half * half * x;
}
```

---

### ✅ Example:

Let's walk through an example: `x = 2.0`, `n = 10`

* `myPow(2.0, 10)`

  * `newN = 10`
  * `fastPow(2.0, 10)`

    * `fastPow(2.0, 5)`

      * `fastPow(2.0, 2)`

        * `fastPow(2.0, 1)`

          * `fastPow(2.0, 0)` → returns 1
          * back to `n=1` → `half = 1`, odd → return `1 * 1 * 2 = 2`
        * back to `n=2` → `half = 2`, even → return `2 * 2 = 4`
      * back to `n=5` → `half = 4`, odd → return `4 * 4 * 2 = 32`
    * back to `n=10` → `half = 32`, even → return `32 * 32 = 1024`

Final result = `2^10 = 1024.0`
*/

class Solution {
    public double myPow(double x, int n) {
        // Handle edge case where base is 0
        if (x == 0) return 0;

        // Exponent of 0 means x^0 = 1
        if (n == 0) return 1;

        // Use long to safely take absolute value of n
        long newN = Math.abs((long)n);

        // Compute the result using fast exponentiation
        double ans = fastPow(x, newN);

        // If n is negative, return reciprocal
        return n < 0 ? 1 / ans : ans;
    }

    public double fastPow(double x, long n) {
        // Base case: x^0 = 1
        if (n == 0) return 1;

        // Recursively compute x^(n/2)
        double half = fastPow(x, n / 2);

        // If n is even: x^n = (x^(n/2)) * (x^(n/2))
        if (n % 2 == 0) {
            return half * half;
        } 
        // If n is odd: x^n = (x^(n/2)) * (x^(n/2)) * x
        else {
            return half * half * x;
        }
    }
}