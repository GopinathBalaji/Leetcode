// 3 pointer approach
/*
## \U0001f31f Key Idea of the Code

* You are given two sorted arrays:

  * `nums1` has size `m + n`. Its first `m` elements are valid, and its last `n` elements are empty slots (`0`s) where you must place elements from `nums2`.
  * `nums2` has `n` valid sorted elements.
* We want to merge `nums2` into `nums1`, keeping everything sorted, **in place**.

**Strategy:**
Fill `nums1` from the **end** using three pointers:

* `i = m - 1`: last valid element in `nums1`’s initial part
* `j = n - 1`: last element in `nums2`
* `k = m + n - 1`: last slot in `nums1`

At each step:

* Compare `nums1[i]` and `nums2[j]`.
* Place the larger one at `nums1[k]`.
* Move the corresponding pointer (`i` or `j`) backward, and move `k` backward.
* When one array is exhausted, copy the remaining elements of the other.

---

## \U0001f4dd Example Walkthrough

Let’s take:

```
nums1 = [1, 3, 5, 0, 0, 0], m = 3
nums2 = [2, 4, 6],           n = 3
```

### Initial Pointers

```
i = 2  (pointing at nums1[2] = 5)
j = 2  (pointing at nums2[2] = 6)
k = 5  (last index in nums1)
```

---

### Iteration 1

Compare `nums1[i] = 5` and `nums2[j] = 6`.

* Larger = 6 → put it at `nums1[k]`.

```
nums1 = [1, 3, 5, 0, 0, 6]
```

Update pointers: `j=1`, `k=4`.

---

### Iteration 2

Compare `nums1[i] = 5` and `nums2[j] = 4`.

* Larger = 5 → put it at `nums1[k]`.

```
nums1 = [1, 3, 5, 0, 5, 6]
```

Update pointers: `i=1`, `k=3`.

---

### Iteration 3

Compare `nums1[i] = 3` and `nums2[j] = 4`.

* Larger = 4 → put it at `nums1[k]`.

```
nums1 = [1, 3, 5, 4, 5, 6]
```

Update pointers: `j=0`, `k=2`.

---

### Iteration 4

Compare `nums1[i] = 3` and `nums2[j] = 2`.

* Larger = 3 → put it at `nums1[k]`.

```
nums1 = [1, 3, 3, 4, 5, 6]
```

Update pointers: `i=0`, `k=1`.

---

### Iteration 5

Compare `nums1[i] = 1` and `nums2[j] = 2`.

* Larger = 2 → put it at `nums1[k]`.

```
nums1 = [1, 2, 3, 4, 5, 6]
```

Update pointers: `j=-1`, `k=0`.

---

### End Condition

`j < 0` → no elements left in `nums2`. We’re done.

**Final result:**

```
nums1 = [1, 2, 3, 4, 5, 6]
```

---

## \U0001f50d Why It Works

* We **merge from the back** to avoid overwriting useful elements in `nums1`.
* When `nums2` is exhausted, we’re done (any leftover `nums1` elements are already in place).
* When `nums1`’s valid part is exhausted, we just copy the remaining `nums2` elements (handled by the final `while (j >= 0)` loop).

---

## ⏱ Complexity

* **Time:** O(m+n) (every element processed once).
* **Space:** O(1) (all done in place).
*/

class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {

        if(n == 0){   // nothing to merge
            return;
        }

        int pointerA = m-1;   // last valid index in nums1's initial part
        int pointerB = n-1;   // last index in nums2
        int pointerC = m + n - 1;  // write index from the end of nums1

        // Merge from the back
        while(pointerA >= 0 && pointerB >= 0){
            if(nums1[pointerA] > nums2[pointerB]){
                nums1[pointerC--] = nums1[pointerA--];
            }else{
                nums1[pointerC--] = nums2[pointerB--];
            }
        }

        // Copy any remaining nums2 elements (if any)
        while(pointerB >= 0){
            nums1[pointerC--] = nums2[pointerB--];
        }

        // No need to copy remaining nums1 elements; they're already in place
    }
}