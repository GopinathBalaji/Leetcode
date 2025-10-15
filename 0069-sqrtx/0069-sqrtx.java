// Binary search
/*
You run a closed-interval binary search with these moves:

* If `mid*mid ≤ x` → move right: `l = mid + 1`
* Else (`mid*mid > x`) → move left: `r = mid - 1`

This search is for the **maximum `mid` that satisfies** `mid*mid ≤ x` (a “max-true” boundary).
With those updates, when the loop ends you have `r < l`, and:

* `r` is the **largest value** that still satisfied `mid*mid ≤ x`
* `l` is the first value that **fails** the predicate

## 1) Frame it as a monotone search

* Define `f(m) = m*m ≤ x`.
* `f(m)` is **true** for small `m` and turns **false** after the real √x.
* You want the **largest m** such that `m*m ≤ x` (a **max-true boundary**).

## 2) Pick tight initial bounds

* Handle tiny cases first: `x ∈ {0,1}` → answer is `x`.
* For `x ≥ 2`, you can safely search on `m ∈ [1, x/2 + 1]` (since √x ≤ x/2 for x≥4, and the +1 covers small x).

## 3) Avoid overflow on `m*m`

* Don’t compute `m*m` directly if you’re using 32-bit ints.
* Compare as `m ≤ x / m` instead of `m*m ≤ x`. (Same logic, no overflow.)

## 4) Choose a consistent binary-search “style”

Two robust patterns (both fine):

* **Closed interval** `[l, r]` (store candidate):

  * If `m ≤ x/m`, move `l = m + 1` and remember `m` as the best-so-far.
  * Else `r = m - 1`.
  * Return the remembered candidate.

* **Lower-bound (max-true) with bias**:

  * Invariant: answer in `[l, r]`. Use `mid = (l + r + 1) / 2` (upper mid).
  * If `mid ≤ x/mid`, keep right side: `l = mid`.
  * Else shrink right: `r = mid - 1`.
  * Return `l`.

> The “upper mid” trick prevents infinite loops when searching for a **maximum** satisfying index.

## 5) Edge cases to think about

* `x = 0` or `1`.
* Perfect squares (e.g., 4, 9, 16) should return exactly the root.
* Non-squares just below a perfect square (e.g., 8 near 9).
* Very large `x` near `Integer.MAX_VALUE` (overflow-safe compare matters).

## 6) Complexity targets

* **Binary search:** `O(log x)` time, `O(1)` space.

## 7) Alternative (optional) approach: Newton’s method

* Iterate `y_{k+1} = (y_k + x / y_k) / 2` starting from a positive guess (`y0 = x` or a smaller heuristic).
* Stop when `y_k*y_k ≤ x` and `(y_k+1)*(y_k+1) > x`, or when successive values stop changing in integers.
* Be careful with integer division and overflow; keep it in 64-bit during updates and floor at the end.

## 8) Common pitfalls

* Mixing binary-search styles (e.g., `while (l < r)` with `r = mid - 1`) causing off-by-one errors.
* Using `m*m` and overflowing.
* Not biasing the midpoint when searching for a **max true** boundary (can loop forever when `l + 1 == r`).

## 9) Quick mental checks

* `x=8` → floor √8 = 2 (since 2²=4 ≤ 8 < 9=3²).
* `x=2147395599` (near √=46340) → should return 46339 or 46340 depending on exact value—your overflow-safe compare must get this right.
*/
class Solution {
    public int mySqrt(int x) {
        if(x <= 1){
            return x;
        }

        int half = x / 2 + 1;

        int l = 1;
        int r = half;

        int mid = 0;
        while(l <= r){
            mid = l + (r - l) / 2;

            if(mid <= x / mid){
                l = mid + 1;
            }else{
                r = mid - 1;
            }
        }


        return r;
    }
}