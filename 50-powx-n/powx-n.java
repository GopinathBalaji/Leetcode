// Method 1: Iterative fast power (binary exponentiation)
/*
## Why this works

Binary exponentiation rewrites ( x^n ) using the binary bits of ( n ):

* If the least-significant bit of ( n ) is 1, multiply `result` by the current `base`.
* Square the base each iteration to move from ( x^{2^k} ) to ( x^{2^{k+1}} ).
* Shift ( n ) right to process the next bit.

This takes **~number of bits in n** steps → ( O(\log |n|) ), no recursion, no stack risk.

## Walkthrough (example: `x=2.0, n=10`)

* `e=10 (1010b)`, `result=1`, `base=2`
* Bit0=0 → skip; square base: `base=4`; `e=5`
* Bit0=1 → `result*=4` ⇒ `result=4`; square base: `base=16`; `e=2`
* Bit0=0 → skip; square base: `base=256`; `e=1`
* Bit0=1 → `result*=256` ⇒ `result=1024`; square base: `base=65536`; `e=0`
* Done → **1024**

## Negative exponent (example: `x=2.0, n=-3`)

* `e=-3` → flip base: `x=1/2=0.5`, `e=3`
* Bits of 3: `11b`

  * bit1: result = 1 * 0.5 = 0.5; base = 0.25; e=1
  * bit1: result = 0.5 * 0.25 = 0.125; base = 0.0625; e=0
* **0.125**

## Why we promote `n` to `long`

If `n == Integer.MIN_VALUE` (`-2_147_483_648`), `-n` overflows an `int`.
Promoting to `long` first makes `-e` valid.


# DETAILED LOGIC EXPLANATION
# Big picture: why fast power is (O(\log |n|))

Any non-negative integer exponent (n) can be written in **binary**:
[
n = b_0 \cdot 2^0 + b_1 \cdot 2^1 + \cdots + b_k \cdot 2^k \quad (b_i \in {0,1})
]
Therefore,
[
x^n = \prod_{i=0}^{k} x^{b_i \cdot 2^i} = \prod_{i: b_i=1} \left(x^{2^i}\right).
]
So if you can quickly compute (x^{2^0}, x^{2^1}, x^{2^2}, \ldots) (by repeated **squaring**), you only multiply in those terms where the corresponding **bit** (b_i) is 1. There are only (\lfloor \log_2 n \rfloor + 1) bits, so you do (O(\log n)) squarings, and at most that many multiplications into the result.

This is the heart of both methods:

* **Even exponent**: (x^{2k} = (x^k)^2) → one recursive step or one squaring.
* **Odd exponent**: (x^{2k+1} = x \cdot (x^k)^2) → same as even plus one extra multiply by (x).

---

# Negative exponents (and the `Integer.MIN_VALUE` trap)

For (n<0), use:
[
x^n = \frac{1}{x^{-n}}
]
So flip the base (x \leftarrow 1/x) and work with (|n|). But **do not** do `n = -n` in 32-bit `int` when `n == Integer.MIN_VALUE` (−2,147,483,648), because `-n` overflows. The standard, safe move:

1. Promote to 64-bit: `long e = n;`
2. If `e < 0`: set `x = 1/x; e = -e;`
3. Now compute (x^e) where (e \ge 0).

---

# Method A: Iterative fast power (binary, bit-by-bit)

## The invariant and bit-shifts

Keep two variables:

* `base` = the current power (x^{2^i}) you’re “sitting on”
* `result` = the product of all needed powers you’ve chosen so far

Process the binary digits of (e) from least significant to most significant:

1. **If the current bit is 1** (`(e & 1) == 1`), multiply it in: `result *= base`.
   This corresponds to including the term (x^{2^i}) in the product.
2. **Square the base**: `base *= base` → move from (x^{2^i}) to (x^{2^{i+1}}).
3. **Shift the exponent** right by 1: `e >>= 1` → drop the bit you just handled and move to the next bit.

Stop when `e == 0`. The `result` now equals (x^n).

### Why bit shifting works

* `e & 1` checks **odd/even**: if odd, the lowest bit is 1 (include `base`).
* `e >>= 1` is **integer division by 2**, discarding the lowest bit.
* Repeating this consumes all bits of (e) from LSB to MSB, exactly implementing the “multiply the (x^{2^i}) terms where bit (b_i=1)” plan.

## Example (detailed): (x=3, n=13)

* (13) in binary is `1101` → (13 = 8 + 4 + 1).
  So (3^{13} = 3^8 \cdot 3^4 \cdot 3^1).

Initialize: `result=1`, `base=3`, `e=13`.

| Step | e (bin) | e&1 | Action on result   | Square base           | Shift e | result  | base |
| ---- | ------- | --- | ------------------ | --------------------- | ------- | ------- | ---- |
| 0    | 1101    | 1   | result *= 3 → 3    | base = 3² = 9         | e=110   | 3       | 9    |
| 1    | 0110    | 0   | (skip)             | base = 9² = 81        | e=11    | 3       | 81   |
| 2    | 0011    | 1   | result *= 81 → 243 | base = 81² = 6561     | e=1     | 243     | 6561 |
| 3    | 0001    | 1   | result *= 6561 → … | base = 6561² (unused) | e=0     | 1594323 | …    |

End: `result = 1,594,323 = 3^13`. Only **3** multiplies into result, **3** squarings.

---

# Method B: Recursive fast power (divide exponent by 2)

This is the same math, expressed top-down.

Define:
[
\text{fastPow}(x, e) =
\begin{cases}
1, & e = 0\
\left(\text{fastPow}(x, \lfloor e/2 \rfloor)\right)^2, & e \text{ even}\
\left(\text{fastPow}(x, \lfloor e/2 \rfloor)\right)^2 \cdot x, & e \text{ odd}
\end{cases}
]

Each recursive call halves the exponent, so recursion depth is (\lfloor \log_2 e \rfloor + 1).

## What’s “actually happening”

* You compute (x^{\lfloor e/2 \rfloor}) just once (store it as `half`).
* If `e` is **even**, return `half * half`.
* If `e` is **odd**, return `half * half * x`.
  This exactly mirrors the iterative method’s “square the base and optionally multiply by x” but organizes it as a recursion tree with depth (O(\log e)).

## Example (detailed): (x=5, n=11)

(11 = 2\cdot5 + 1) (odd)

* `fastPow(5,11)`

  * compute `half = fastPow(5,5)` (since 11//2 = 5)

    * `fastPow(5,5)` (odd)

      * `half = fastPow(5,2)`

        * `fastPow(5,2)` (even)

          * `half = fastPow(5,1)`

            * `fastPow(5,1)` (odd)

              * `half = fastPow(5,0) = 1`
              * return `1*1*5 = 5`
          * return `5*5 = 25`
      * return `25*25*5 = 3125`
  * return `3125*3125*5 = 48,828,125`

Number of multiplications is proportional to the number of nodes in this recursion tree, i.e., **(O(\log n))**.

---

# Why both methods are equivalent

* **Iterative**: consumes bits LSB→MSB; keeps a running product `result` and a power `base = x^{2^i}` that it squares each step.
* **Recursive**: splits the exponent by halving (MSB-side reasoning); reuses the computed (x^{\lfloor e/2 \rfloor}) (`half`) to build back up.

They’re just two traversals of the same binary decomposition of (n).

---

# Correctness sketch (invariants)

* **Iterative invariant:** After processing the first (t) bits of (e), `result` equals the product of (x^{2^i}) for all 1-bits among those (t) bits, and `base` equals (x^{2^t}). Proof by induction on (t).
* **Recursive invariant:** For each call `fastPow(x, e)`, it returns exactly (x^e) by the identities (x^{2k}=(x^k)^2) and (x^{2k+1}=(x^k)^2 \cdot x).

---

# Complexity and numerics

* **Time:** (O(\log |n|)) multiplications; far fewer rounding errors than naïve (O(n)) multiplication.
* **Space:**

  * Iterative: (O(1)) extra space.
  * Recursive: (O(\log |n|)) stack depth.
* **Floating point:** Doubles inevitably accumulate rounding error; fast power minimizes operations so it’s better than multiplying (n) times.

---

# Common pitfalls (and why the above fixes them)

1. **`n = Integer.MIN_VALUE` overflow:** Negating `int` `-2^31` is still `-2^31`. Promoting to `long` before negation avoids it.
2. **Linear recursion `x * recurse(x, n-1)`:** That’s (O(n)) time and stack depth. Fast power shrinks the exponent by half each step.
3. **Odd/even mistakes:**

   * Iterative: forgetting to multiply in `base` when `(e & 1) == 1`.
   * Recursive: forgetting the extra `* x` when `e` is odd.

---

# Mental model to remember

* You’re **reading the exponent in binary**.
* Each time you **square**, you move to the **next power of two**.
* Each time a **bit is 1**, you **include** the current power in the answer.
* Negative exponents just mean “invert the base once, then do the same process.”

That’s all bit-shifting is: a fast way to peel off binary digits of the exponent while you hop through powers of two via squaring.

*/
class Solution {
    public double myPow(double x, int n) {
        // 1) Promote to long before negation to avoid overflow on Integer.MIN_VALUE
        long e = n;
        if (e < 0) {
            x = 1.0 / x;
            e = -e;
        }

        // 2) Binary exponentiation: build the result using bits of e
        double result = 1.0;
        double base = x;

        while (e > 0) {
            // If current bit is 1, multiply result by the current base
            if ((e & 1L) == 1L) {
                result *= base;
            }
            // Square the base and shift exponent right by one bit
            base *= base;
            e >>= 1;
        }

        return result;
    }
}



// Method 2: Recursive fast power (shallow recursion)
/*
## Why this works

Use the identities:

* ( x^{2k} = (x^k)^2 )
* ( x^{2k+1} = (x^k)^2 \cdot x )

Each call halves the exponent → recursion depth **O(log |n|)** (shallow, no risk of deep stack). Same time complexity as the iterative method.

## Walkthrough (example: `x=3.0, n=5`)

* `fastPow(3,5)`:

  * half = `fastPow(3,2)`

    * half = `fastPow(3,1)`

      * half = `fastPow(3,0)` → 1
      * e=1 odd → return `1*1*3 = 3`
    * e=2 even → return `3*3 = 9`
  * e=5 odd → return `9*9*3 = 243`

---

## Which should you choose?

* **Iterative**: bulletproof and stack-free; great default.
* **Recursive**: neat and short; still safe because depth is only ( O(\log |n|) ).

Both must:

* **Promote `n` to `long`** before negation.
* **Flip base** for negative `n` (`x = 1/x`) and use `|n|` for the exponent.

---

## Edge behavior notes (doubles)

* `x == 0` and `n < 0` → Java yields `Infinity` (`1/0.0`), which matches typical judge expectations.
* Rounding is inherent with `double`; both methods minimize multiplications and thus error accumulation.
*/
// class Solution {
//     public double myPow(double x, int n) {
//         long e = n;                // promote to long
//         if (e < 0) {
//             x = 1.0 / x;
//             e = -e;
//         }
//         return fastPow(x, e);
//     }

//     private double fastPow(double x, long e) {
//         if (e == 0) return 1.0;
//         double half = fastPow(x, e / 2);
//         if ((e & 1L) == 0L) {
//             return half * half;           // even exponent
//         } else {
//             return half * half * x;       // odd exponent
//         }
//     }
// }





// Method 2.5: My answer using naive recursion
// class Solution {
//     public double myPow(double x, int n) {
//         if(n == 0){
//             return 1;
//         }

//         if(n < 0){
//             x = 1 / x;
//             n = -n;
//         }

//         return recurse(x , n);
//     }

//     private double recurse(double x, int n){
//         if(n == 0){
//             return 1;
//         }

//         return x * recurse(x, n-1);
//     }
// }