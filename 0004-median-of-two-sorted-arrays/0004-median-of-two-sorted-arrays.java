// Method 1: Binary Search Method O(log(min(m, n)))
/*
Quick idea:
Intuition for the moves
If A[i-1] > B[j], you took too many from A (its left max is “too big”) → move i left.
If B[j-1] > A[i], you took too few from A → move i right.


# Big Picture: What are we doing?

We want to split both arrays at some indices `i` and `j` so that:

* The **left side** has exactly `L = (m + n + 1) / 2` elements,
* All elements on the **left side** are ≤ all elements on the **right side**.

If we can enforce that, the median is:

* **Odd length:** the **max** on the left,
* **Even length:** the average of (max on the left, min on the right).

Crucially, we **binary search `i`** (the cut in the *shorter* array). Then `j = L - i` is forced, which keeps the left side size fixed. We check if the cut is “good” using two inequalities:

```
A[i-1] ≤ B[j]   and   B[j-1] ≤ A[i]
```

If either is violated, we move `i` left/right appropriately.

---

# Visual Intuition: Partition Diagram

Suppose we cut `nums1` after `i` elements and `nums2` after `j` elements.

```
nums1: [ A0  A1  ...  A(i-2) | A(i-1) || A(i) | A(i+1) ... ]
                          ^ left end          ^ right start

nums2: [ B0  B1  ...  B(j-2) | B(j-1) || B(j) | B(j+1) ... ]
                          ^ left end          ^ right start
```

The combined left half has exactly `L` elements: `i` from nums1 and `j` from nums2 (`i + j = L`).
Let:

* `Aleft  = A[i-1]` (or `-∞` if `i=0`)
* `Aright = A[i]`   (or `+∞` if `i=m`)
* `Bleft  = B[j-1]` (or `-∞` if `j=0`)
* `Bright = B[j]`   (or `+∞` if `j=n`)

The **correct partition** requires:

```
Aleft ≤ Bright   and   Bleft ≤ Aright
```

If these hold, **the left side is globally ≤ the right side**.

---

# Why the Two Inequalities?

They guarantee no “crossing”:

* `Aleft ≤ Bright`: The largest value we left behind in A isn’t greater than the smallest value we put on the right in B.
* `Bleft ≤ Aright`: The largest value we left behind in B isn’t greater than the smallest value we put on the right in A.

Both must be true so the **entire** left is ≤ the **entire** right.

---

# How to Move the Binary Search (Monotone Fix)

When the inequalities fail:

* If `Aleft > Bright` → we took **too many** from A (the left’s max from A is pushing into B’s right).
  **Move `i` left** (decrease `i`): `hi = i - 1`.

* Else (so `Bleft > Aright`) → we took **too few** from A (B’s left max is still larger than A’s right min).
  **Move `i` right** (increase `i`): `lo = i + 1`.

This is monotone and converges in `O(log m)`.

---

# Thorough Example Walkthrough (with diagrams)

Let’s do a detailed dry run (even total length):

```
nums1 = [1, 3, 8, 9, 15]      (m = 5)
nums2 = [7, 11, 18, 19, 21, 25] (n = 6)
total = 11, L = (11 + 1)/2 = 6
```

We search `i` in `[0..5]`. Then `j = 6 - i`.

### Iteration 1

* `lo=0, hi=5` → `i = 2`
* `j = 6 - 2 = 4`
* `Aleft  = nums1[1] = 3`
* `Aright = nums1[2] = 8`
* `Bleft  = nums2[3] = 19`
* `Bright = nums2[4] = 21`

Check:

* `Aleft ≤ Bright`? `3 ≤ 21` ✓
* `Bleft ≤ Aright`? `19 ≤ 8` ✗  (violated)

Since `Bleft > Aright`, we took **too few** from A → move `i` right:

* `lo = i + 1 = 3`

### Iteration 2

* `lo=3, hi=5` → `i = 4`
* `j = 6 - 4 = 2`
* `Aleft  = nums1[3] = 9`
* `Aright = nums1[4] = 15`
* `Bleft  = nums2[1] = 11`
* `Bright = nums2[2] = 18`

Check:

* `Aleft ≤ Bright`? `9 ≤ 18` ✓
* `Bleft ≤ Aright`? `11 ≤ 15` ✓

Both true → **correct partition found**.

Now compute median:

* `total=11` is odd → median is **max(Aleft, Bleft) = max(9, 11) = 11**.

**Diagram at the correct cut**

```
nums1: [ 1, 3, 8, | 9 ] || [ 15 ]
                   ^Aleft   ^Aright

nums2: [ 7, 11 | ] || [ 18, 19, 21, 25 ]
            ^Bleft    ^Bright

Left side (6 elems): [1,3,8,9] + [7,11]  (max = 11)
Right side          : [15] + [18,19,21,25] (min = 15)
```

All left ≤ all right. For odd total, median is left’s max = 11.

---

## Another Walkthrough (even total)

```
nums1 = [1, 2]
nums2 = [3, 4]
m=2, n=2, total=4, L=(4+1)/2=2
i ∈ [0..2]; j = 2 - i
```

Try `i=1`:

* `j=1`
* `Aleft=nums1[0]=1`, `Aright=nums1[1]=2`
* `Bleft=nums2[0]=3`, `Bright=nums2[1]=4`
  Check:
* `Aleft ≤ Bright`? `1 ≤ 4` ✓
* `Bleft ≤ Aright`? `3 ≤ 2` ✗

`Bleft > Aright` → move `i` right: `[lo,hi]` → `[2,2]`

Now `i=2`:

* `j=0`
* `Aleft=nums1[1]=2`, `Aright=+∞` (i==m)
* `Bleft=-∞` (j==0), `Bright=nums2[0]=3`
  Check:
* `Aleft ≤ Bright`? `2 ≤ 3` ✓
* `Bleft ≤ Aright`? `-∞ ≤ +∞` ✓ → valid

Even total ⇒

* `leftMax = max(Aleft, Bleft) = max(2, -∞) = 2`
* `rightMin = min(Aright, Bright) = min(+∞, 3) = 3`
* median = (2 + 3) / 2 = **2.5**

Diagram:

```
nums1: [ 1, 2 ] || [ ]
             ^Aleft ^Aright=+∞

nums2: [ ] || [ 3, 4 ]
      ^Bleft=-∞ ^Bright=3
```

---

# Why we force `nums1` to be the shorter array

Only to make the search range `[0..m]` as small as possible and to guarantee `j = L - i` always lands within `[0..n]`. It says **nothing** about values (they can be interleaved in any way—your earlier note on this was exactly right).

---

# Edge Cases & Sentinels

* `i=0` → there’s nothing on the left from `nums1`: set `Aleft = -∞`
* `i=m` → there’s nothing on the right from `nums1`: set `Aright = +∞`
* Similarly for `j=0` and `j=n` with `Bleft`/`Bright`.

Using sentinels means **no special “if” blocks** during checks—inequalities still work.

---

# Correctness Invariants (what keeps this safe)

1. **Fixed left size:** `i + j = L`. Each iteration preserves this.
2. **Monotone decision:**

   * If `Aleft > Bright` → decrease `i` (move left)
   * Else if `Bleft > Aright` → increase `i` (move right)
3. **Termination:** bounds `lo..hi` shrink each step, so we must hit a correct partition.

---

# Complexity

* Each step halves the `i` search interval → **O(log m)** iterations (with `m = min(lengths)`).
* Constant extra space → **O(1)**.

---

# Common Pitfalls (and how the pattern avoids them)

* **Merging everything** → O(m+n), too slow for the classic ask.
* **Searching the longer array** → still correct, but more iterations.
* **Forgetting sentinels** → edge indices cause index errors or lots of special casing.
* **Mixing which inequality moves which way** → memorize the rules:

  * `Aleft > Bright` → too many from A → move `i` left.
  * `Bleft > Aright` → too few from A → move `i` right.


# Why is the left side (total + 1) / 2 and not (total) / 2
### Short answer

We set the **left half size** to
[
L=\frac{T+1}{2}=\left\lceil\frac{T}{2}\right\rceil
]
so that **when the total length (T) is odd, the single middle element belongs to the left side**. That lets us return the median as simply **max(left side)** for odd (T), and as the average of **max(left)** and **min(right)** for even (T)—with the *same* partition logic.

### Why not (T/2)?

* If (T) is **even**, (T/2 = (T+1)/2). No difference.
* If (T) is **odd**, (T/2) (integer division) is (\lfloor T/2 \rfloor) and **does not include** the middle element in the left; you'd then have to treat the median as the **first element of the right**. That complicates the “read-off” step and makes the two parity cases asymmetric.

Using (L=\lceil T/2\rceil=(T+1)/2) makes both cases uniform:

* **Odd (T):**
  Left has one more element than right. The **median is (\max(\text{left}))**.
* **Even (T):**
  Left and right are the same size. The **median is (\frac{\max(\text{left})+\min(\text{right})}{2})**.

### Tiny visuals

* **Odd (T=5)** → (L=(5+1)/2=3)

  ```
  Left (3 elems):  _ _ [ _ ]   Right (2 elems):  _ _
                         ^
                       median
  ```

  Middle element sits in the left; answer = max(left).

* **Even (T=6)** → (L=(6+1)/2=3) (same as 6/2)

  ```
  Left (3 elems):  _ _ _   Right (3 elems):  _ _ _
                           ^ ^
                     two middles split across the cut
  ```

  Answer = average of max(left) and min(right).

### How it plugs into the inequalities

With cut positions (i) in (A) and (j=L-i) in (B):

* We enforce:
  (A[i-1] \le B[j]) **and** (B[j-1] \le A[i]).
* Then:

  * (\max(\text{left}) = \max(A[i-1], B[j-1]))
  * (\min(\text{right}) = \min(A[i], B[j]))

Choosing (L=\lceil T/2\rceil) guarantees that:

* For **odd** (T), the median is exactly (\max(\text{left})).
* For **even** (T), the two central elements straddle the cut, so the average of (\max(\text{left})) and (\min(\text{right})) is correct.

So ( (T+1)/2 ) isn’t claiming the left is “bigger” in general; it’s just the **cleanest way** to make the median calculation uniform across both parities with the same partition checks.


# Quick sanity scenarios

1. **All `A` elements bigger than all `B` elements**

   * Example: `A = [100, 101]`, `B = [1, 2, 3, 4, 5]`, total `T=7`, `L=(7+1)/2=4`.
   * The correct partition has **`i = 0`**, **`j = 4`** → left = `B[0..3]`, right = `B[4..]` + `A`.
   * Check: `A[i-1] = -∞ ≤ B[j]` (true), `B[j-1] = 4 ≤ A[i] = 100` (true).
   * Median is `maxLeft = 4` (odd `T`), which is correct.

2. **All `A` elements smaller than all `B` elements**

   * Then the correct cut is **`i = m`**, **`j = L − m`**.
   * Check inequalities still pass; median comes from the boundary between the tail of `A` and the head of `B`.

3. **Interleaved values**

   * The search will move `i` left/right until both inequalities are satisfied—no assumption about global ordering between arrays is needed.
*/
class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        // Ensure nums1 is the shorter array so we search the smaller index range
        if (nums1.length > nums2.length) {
            int[] tmp = nums1; nums1 = nums2; nums2 = tmp;
        }
        int m = nums1.length, n = nums2.length;
        int total = m + n;
        int L = (total + 1) / 2; // size of the combined LEFT half

        int lo = 0, hi = m;      // i ∈ [0..m], j = L - i ∈ [0..n]
        while (lo <= hi) {
            int i = lo + (hi - lo) / 2; // cut position into nums1's left part
            int j = L - i;              // rest comes from nums2

            // Sentinels for edges (treat out-of-bounds as ±∞)
            int Aleft  = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
            int Aright = (i == m) ? Integer.MAX_VALUE : nums1[i];
            int Bleft  = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
            int Bright = (j == n) ? Integer.MAX_VALUE : nums2[j];

            // Correct partition if left max ≤ right min on BOTH sides
            if (Aleft <= Bright && Bleft <= Aright) {
                // Found the correct cut
                if (total % 2 == 1) {
                    // Odd: median is max of the left side
                    return Math.max(Aleft, Bleft);
                } else {
                    // Even: average of middle two
                    int leftMax  = Math.max(Aleft, Bleft);
                    int rightMin = Math.min(Aright, Bright);
                    return (leftMax + rightMin) / 2.0;
                }
            } else if (Aleft > Bright) {
                // Took too many from nums1; move cut i left
                hi = i - 1;
            } else {
                // Bleft > Aright: took too few from nums1; move cut i right
                lo = i + 1;
            }
        }

        // Should never reach here for valid input
        throw new IllegalArgumentException("Input arrays are not valid.");
    }
}




// Method 2: K-th element selection (divide-and-conquer / binary search on rank) O(log(m + n))
/*
1. **K-th element selection (divide-and-conquer / binary search on rank)** — optimal `O(log(m+n))`

## How it works (intuition)

* We want the **k-th smallest** element in the union of two sorted arrays.
* At each step, we **compare the k/2-th candidate** from each array’s remaining portion:

  * Let `A[i + half - 1]` and `B[j + half - 1]` be those candidates (clamping at array ends).
  * Whichever is **smaller** can’t be the k-th (nor can any element before it in that array), because at least `half` elements in that array are ≤ that candidate, and at least `half` elements on the other side remain ≥ the other candidate.
  * So we **discard** that many (`half`) elements from the array with the smaller candidate and **decrease k** accordingly.
* Repeat until:

  * One array empties (answer is in the other),
  * Or `k == 1` (answer is the min of the two heads).

For the **median**:

* If `T = m+n` is **odd**, median is the (T+1)/2-th element.
* If **even**, median is the average of the T/2-th and (T/2+1)-th.

### Sentinels you don’t need to write

We avoid explicit ±∞ by **clamping** `iNew` / `jNew` to array ends. If an array is short, we take fewer than `half` from it (still correct).

## Detailed walkthrough (odd total)

Let:

```
A = [1, 3, 8, 9, 15]
B = [7, 11, 18, 19, 21, 25]
m=5, n=6, total=11 (odd), median is k=(11+1)/2=6-th smallest
```

We seek `k=6`.

* Start: `i=0 (A head=1)`, `j=0 (B head=7)`, `k=6`.
* `half = 3`.

  * `iNew = min(0+3,5)-1 = 2` → `A[iNew]=A[2]=8`
  * `jNew = min(0+3,6)-1 = 2` → `B[jNew]=B[2]=18`
* Compare 8 vs 18 → 8 is smaller ⇒ **discard A[0..2]** (3 elems).
  Now `i=3`, `k=6-3=3`.

Next step:

* `i=3 (A head=9)`, `j=0 (B head=7)`, `k=3`.
* `half = 1` (integer div).

  * `iNew = min(3+1,5)-1 = 3` → `A[iNew]=9`
  * `jNew = min(0+1,6)-1 = 0` → `B[jNew]=7`
* Compare 9 vs 7 → 7 is smaller ⇒ **discard B[0..0]** (1 elem).
  Now `j=1`, `k=3-1=2`.

Next:

* `i=3 (9)`, `j=1 (11)`, `k=2`.
* `half = 1`.

  * `iNew = min(3+1,5)-1 = 3` → 9
  * `jNew = min(1+1,6)-1 = 1` → 11
* Compare 9 vs 11 → 9 smaller ⇒ **discard A[3..3]** (1 elem).
  Now `i=4 (15)`, `k=2-1=1`.

Since `k==1`, answer is `min(A[i], B[j]) = min(15, 11) = 11`.
**Median = 11** (matches the partition method).

### Why it’s correct

Each discard eliminates elements that **cannot** be the k-th because there are already enough smaller elements before them to push them left of the rank‐k boundary. The process is logarithmic because we drop ~k/2 elements per iteration.


# Quick visual mnemonics

## K-th selection (rank picture)

```
A:  [ a a a a | a a ... ]   ^iNew (~k/2 ahead)
B:  [ b b b | b b b ... ]   ^jNew (~k/2 ahead)

Compare A[iNew] vs B[jNew]:
  smaller block (+ its left) can’t contain the k-th → drop it
reduce k accordingly; repeat.
```

*/
// class Solution {
//     public double findMedianSortedArrays(int[] A, int[] B) {
//         int m = A.length, n = B.length;
//         int total = m + n;

//         if (total % 2 == 1) {
//             // Odd: k = (total+1)/2
//             int k = (total + 1) / 2;
//             return kth(A, B, k);
//         } else {
//             // Even: average of k1=(total/2) and k2=(total/2+1)
//             int k1 = total / 2;
//             int k2 = k1 + 1;
//             return (kth(A, B, k1) + kth(A, B, k2)) / 2.0;
//         }
//     }

//     // Returns the k-th smallest (1-indexed) from the sorted union of A and B.
//     private int kth(int[] A, int[] B, int k) {
//         int i = 0, j = 0; // current heads in A and B

//         while (true) {
//             // Edge cases: one array exhausted
//             if (i == A.length) return B[j + k - 1];
//             if (j == B.length) return A[i + k - 1];
//             if (k == 1) return Math.min(A[i], B[j]);

//             // Probe step: try to discard k/2 elements from one side
//             int half = k / 2;

//             // Proposed new indices (be careful not to go past ends)
//             int iNew = Math.min(i + half, A.length) - 1; // index to compare in A
//             int jNew = Math.min(j + half, B.length) - 1; // index to compare in B

//             int Aval = A[iNew];
//             int Bval = B[jNew];

//             if (Aval <= Bval) {
//                 // Discard A[i..iNew], length = iNew - i + 1
//                 k -= (iNew - i + 1);
//                 i = iNew + 1;
//             } else {
//                 // Discard B[j..jNew]
//                 k -= (jNew - j + 1);
//                 j = jNew + 1;
//             }
//         }
//     }
// }








// Method 3: Two-Pointer “Brute-Merge (Without Storing)”
/*
2. **Two-pointer “brute-merge (without storing)”** — linear `O(m+n)` but simple and great for validation

## How it works (intuition)

* This mirrors the **merge step of merge sort**, but we **don’t store** the merged array.
* We walk two pointers (`i` into A, `j` into B), always taking the smaller head.
* We stop when we’ve “emitted” elements up to the median positions:

  * For odd: position `mid2 = total/2`
  * For even: positions `mid1 = total/2 - 1` and `mid2 = total/2`
* Track only the values at those positions.

**Pros:** dead simple, bullet-proof, great for sanity checks.
**Cons:** linear time.

## Detailed walkthrough (even total)

Let:

```
A = [1, 2]
B = [3, 4]
total=4 (even), median = average of positions 1 and 2 (0-based)
→ mid1=1, mid2=2
```

* `i=0 (1)`, `j=0 (3)`, `count=-1`

  * take `1` (smaller), `count=0` (not 1/2 yet)
* `i=1 (2)`, `j=0 (3)`

  * take `2`, `count=1` → `val1=2`
* `i=2 (exhausted)`, `j=0 (3)`

  * take `3`, `count=2` → `val2=3` and **break**
    Median = `(2 + 3)/2 = 2.5`.

---

# When to use which

* **K-th selection** — use when you want **optimal** asymptotics without the partition-cut derivation; also widely reusable for “k-th smallest in two sorted arrays.”
* **Two-pointer** — use when simplicity and quick correctness matter more than `O(log(m+n))`. Also great for **unit tests** to validate your optimal solution.

---

# Edge cases both methods handle

* One array empty (they immediately fall back to the other).
* Very skewed sizes (no special treatment needed).
* Duplicate values across arrays (ordering remains stable).
* All elements of one array ≤ all elements of the other (k-th selection naturally cuts at ends; two-pointer walks straight).


## Two-pointer (merge picture)

```
A:  [1,   3,   8,  9, ...]
B:  [2,   4,   7, 11, ...]
     ^i               ^j
take min head → advance that pointer → count++
stop when count hits the median index(es)
```
*/
// class Solution {
//     public double findMedianSortedArrays(int[] A, int[] B) {
//         int m = A.length, n = B.length;
//         int total = m + n;

//         // Indices of median(s) in 0-based indexing
//         int mid2 = total / 2;
//         int mid1 = (total % 2 == 0) ? (mid2 - 1) : mid2; // if even, we need both mid1 and mid2; if odd, both equal mid2

//         int i = 0, j = 0, count = -1;
//         int val1 = 0, val2 = 0; // holders for mid1 and mid2 values

//         while (i < m || j < n) {
//             int pick;
//             if (i < m && (j == n || A[i] <= B[j])) {
//                 pick = A[i++];
//             } else {
//                 pick = B[j++];
//             }
//             ++count;

//             if (count == mid1) val1 = pick;
//             if (count == mid2) { val2 = pick; break; }
//         }

//         if (total % 2 == 1) return val2;      // mid2 == mid1 for odd; val2 is the middle
//         return (val1 + val2) / 2.0;           // average for even
//     }
// }

