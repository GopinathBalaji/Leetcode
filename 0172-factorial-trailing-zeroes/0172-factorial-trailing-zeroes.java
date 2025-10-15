// Method 1: Keeping n constant and growing divisor
/*
## 1) What creates a trailing zero?

* A trailing zero comes from a factor **10 = 2 × 5**.
* In **n!**, there are **far more 2s than 5s**, so the number of trailing zeros equals the **count of factor 5s** in the prime factorization of **n!**.

## 2) How many 5s are in n!?

* Every multiple of **5** contributes **at least one** factor 5.
* Every multiple of **25 = 5²** contributes **an extra** factor 5 (so it contributes two in total).
* Every multiple of **125 = 5³** contributes yet another extra, and so on.
* Therefore, the total number of 5s is:

  * **⌊n/5⌋ + ⌊n/25⌋ + ⌊n/125⌋ + …** (stop when the term becomes zero).

## 3) Why this works

* You’re counting how many numbers ≤ n are divisible by 5 (one 5 each), plus how many are divisible by 25 (their **second** 5), plus those divisible by 125 (their **third** 5), etc.

## 4) How to compute efficiently

* Repeatedly divide n by 5 and **accumulate the quotient**.
* This takes **O(log₅ n)** steps—very fast even for large n.

## 5) Edge cases & pitfalls

* Don’t try to compute **n!** directly (overflow and time blow-up).
* The result fits in 32-bit **int** for typical constraints.
* If n = 0, answer is 0 (0! = 1 has no trailing zero).
* For very large n, use a loop that stops when n becomes 0 after dividing by 5.
* Be careful not to double-count: the summation formula already accounts for multiples of 25, 125, etc.

## 6) Sanity checks (do by hand)

* **n = 5** → one multiple of 5 → **1** zero.
* **n = 10** → multiples of 5: {5,10} → **2** zeros.
* **n = 25** → ⌊25/5⌋=5 plus ⌊25/25⌋=1 → **6** zeros.
* **n = 100** → 20 + 4 = **24** zeros.

## 7) Mental template (algorithm steps)

* Initialize a counter to 0.
* While n > 0:

  * Divide n by 5, add the quotient to the counter.
  * Replace n with that quotient and repeat.
* Return the counter.
*/
class Solution {
    public int trailingZeroes(int n) {
        int ans = 0;
        int divisor = 5;

        while(divisor <= n){
            ans += (n / divisor);
            divisor *= 5;
        }
        return ans;
    }
}



// Method 2: Keep divinding by 5 and reducing n
// class Solution {
//     public int trailingZeroes(int n) {
//         int ans = 0;

//         while(n > 0){
//             n /= 5;
//             ans += n;
//         }
//         return ans;
//     }
// }