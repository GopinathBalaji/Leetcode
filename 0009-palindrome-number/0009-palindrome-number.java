// Method 1: No strings for reversal (Reverse only half the number)
/*
## Quick observations (filter fast cases)

1. **Negative numbers** can’t be palindromes (a leading `-` has no trailing match).
2. **Numbers ending in 0** (e.g., 10, 100) are **not** palindromes unless the number **is 0** (palindrome).

These two checks let you return early for many inputs.

## Two main approaches

### A) String approach (simple, acceptable unless restricted)

* Convert to string.
* Two pointers: `l` at start, `r` at end; move toward center comparing chars.
* Stop when `l ≥ r` or a mismatch is found.

> Hints:
> • Don’t forget the negative check before converting.
> • You can skip allocation by comparing characters directly without building a reversed copy.

### B) Math approach (no string conversion) — preferred in interviews

* **Key trick:** reverse **only half** of the integer, not the whole number, to avoid overflow and extra work.
* Steps:

  1. Handle fast rejects (negatives, trailing zero unless 0).
  2. Build `rev` by repeatedly taking the last digit of `x` (`x % 10`) and appending it to `rev` (`rev = rev*10 + digit`).
  3. Stop when `rev ≥ x` (means you’ve reversed half the digits).
  4. For even digit counts: `x == rev` → palindrome.
     For odd digit counts: `x == rev/10` (middle digit can be ignored).

> Why it works:
> When you reverse half, you’re effectively comparing the left half of `x` with the reversed right half. For odd lengths, the middle digit doesn’t matter.

## Corner cases to test mentally

* `0` → true (single digit)
* `121` → true (odd length, compare `12` vs `12` after dropping middle)
* `1221` → true (even length)
* `10` → false (trailing zero)
* `-121` → false (negative)

## Pitfalls to avoid

* Reversing the **whole** number can overflow 32-bit int (unnecessary risk). Reversing **half** avoids this.
* Forgetting the **trailing zero** rule (e.g., 1001 is fine, 10 is not).
* Not handling **odd vs even digit counts** in the math approach.

## Complexity targets

* Time: `O(d)` where `d` is number of digits (≤ 10 for 32-bit ints).
* Space: `O(1)` for the math approach; `O(d)` if you build strings (but still small).

## If you’re stuck

* Start with the string two-pointer method to lock in the logic.
* Then implement the half-reverse math version using the stopping condition `rev ≥ x` and the two equality checks (`x == rev` or `x == rev/10`).

With these hints you should be able to implement both versions confidently and pass all edge cases.
*/

class Solution {
    public boolean isPalindrome(int x) {
        if(x < 0){
            return false;
        }
        if(x == 0){
            return true;
        }
        if(x % 10 == 0){
            return false;
        }

        int rev = 0;
        int last = 0;

        while(rev < x){
            last = x % 10;
            x = x / 10;
            rev = rev * 10 + last;
        }

        if((rev == x) || (rev/10 == x)){
            return true;
        }

        return false;
    }
}