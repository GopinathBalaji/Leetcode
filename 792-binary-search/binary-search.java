// Method 1: Binary Search
/*
Below are the **accepted binary search boundary variations**:

## Mental model (applies to every variation)

Binary search always maintains an interval that still *might* contain `target`.
You repeatedly pick `mid`, compare, and throw away the half that cannot contain `target`, while keeping the interval definition consistent.

Two standard interval types:

1. **Closed interval**: `[left, right]` (both inclusive)
2. **Half-open interval**: `[left, right)` (left inclusive, right exclusive)

A third “inclusive but `left < right`” style exists, but it’s essentially a different way to write one of the above.

---

# Variation A: Closed interval `[left, right]` (your code)

### Invariant

If `target` exists, it is inside indices `[left, right]`.

### Initialization

* `left = 0`
* `right = n - 1`

### Loop condition

* `while (left <= right)`
  Because `[left, right]` is valid as long as `left` hasn’t crossed `right`.

### Mid

* `mid = left + (right - left) / 2`

### Updates

* If `nums[mid] < target`, discard left half including `mid`:
  `left = mid + 1`
* If `nums[mid] > target`, discard right half including `mid`:
  `right = mid - 1`
* If equal, return `mid`.

### Return

If loop ends, interval is empty → not found: return `-1`.

✅ This is the most common “exact match” version.

---

# Variation B: Half-open interval `[left, right)` (left < right) — “lower bound” style

This is what I gave you earlier. It’s super common because it cleanly generalizes to lower_bound / upper_bound.

### Invariant

If `target` exists, it is in indices `[left, right)`.

### Initialization

* `left = 0`
* `right = n` (exclusive!)

### Loop condition

* `while (left < right)`

### Key decision

Most people write this as a **lower bound** search:

> Find the first index `pos` such that `nums[pos] >= target`.

### Updates

* If `nums[mid] < target`, target must be to the right of `mid`:
  `left = mid + 1`
* Else (`nums[mid] >= target`), keep `mid` in the candidate range:
  `right = mid`

### After loop

* `left == right` is the insertion point for `target`
* Check if it’s actually present.

### Code

```java
class Solution {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length; // [left, right)

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return (left < nums.length && nums[left] == target) ? left : -1;
    }
}
```

✅ This is “`left < right`” and is accepted.

---

# Variation C: Closed interval but `left < right` by forcing convergence (upper mid / lower mid choices)

This style keeps a **closed interval** but avoids `<=` by ensuring the interval shrinks until one element remains.

There are two symmetric forms:

## C1) “Converge to potential answer” (bias mid to the left)

### Invariant

`target` (if present) is in `[left, right]`.

### Loop condition

`while (left < right)`

### Mid

Use left-biased mid:
`mid = left + (right - left) / 2`

### Updates

* If `nums[mid] < target`, target must be right of mid:
  `left = mid + 1`
* Else (`nums[mid] >= target`), keep mid:
  `right = mid`

This is basically the same logic as lower bound, but written with a closed interval end state.

### Finish

After loop, `left == right`:

* if `nums[left] == target` return left else -1

### Code

```java
class Solution {
    public int search(int[] nums, int target) {
        int n = nums.length;
        if (n == 0) return -1;

        int left = 0, right = n - 1; // [left, right]
        while (left < right) {
            int mid = left + (right - left) / 2; // left-biased
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return nums[left] == target ? left : -1;
    }
}
```

✅ Accepted. This finds the first position where `nums[pos] >= target` within `[0..n-1]`.

---

## C2) “Converge using right-biased mid” (often used for upper bound / last true)

If you instead want to keep the right side, you use an **upper mid** to avoid infinite loops:

`mid = left + (right - left + 1) / 2`  (note the `+1`)

Then update as:

* If `nums[mid] > target`, move right leftward:
  `right = mid - 1`
* Else (`nums[mid] <= target`), move left to mid:
  `left = mid`

This converges to the last position where `nums[pos] <= target`. Then check equality.

### Code

```java
class Solution {
    public int search(int[] nums, int target) {
        int n = nums.length;
        if (n == 0) return -1;

        int left = 0, right = n - 1; // [left, right]
        while (left < right) {
            int mid = left + (right - left + 1) / 2; // right-biased
            if (nums[mid] > target) {
                right = mid - 1;
            } else {
                left = mid;
            }
        }

        return nums[left] == target ? left : -1;
    }
}
```

✅ Also accepted.

Why the `+1` matters: without it, when `left+1==right`, mid could equal left repeatedly and you’d stall.

---

# Variation D: “Two pointers with post-check” (classic `left + 1 < right`)

This is common in interviews: shrink until 2 elements remain, then check both.

### Invariant

Target is in `[left, right]` if present.

### Loop condition

`while (left + 1 < right)`

### Updates

* If `nums[mid] == target` return mid
* If `nums[mid] < target` set `left = mid`
* else set `right = mid`

### After loop

Check `left` and `right` explicitly.

### Code

```java
class Solution {
    public int search(int[] nums, int target) {
        int n = nums.length;
        if (n == 0) return -1;

        int left = 0, right = n - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) left = mid;
            else right = mid;
        }

        if (nums[left] == target) return left;
        if (nums[right] == target) return right;
        return -1;
    }
}
```

✅ Accepted, but more verbose.

---

## How to choose which one to use

* If you want the simplest exact search: **Variation A** (`<=`, closed interval).
* If you want something that generalizes cleanly to bounds: **Variation B** (`[left, right)`, lower bound).
* If you want `left < right` but still exact: **C1/C2** converge-to-one-element variants.
* If you like explicit final checks: **D**.

---

## Common pitfalls to avoid (regardless of variant)

* Mixing interval types: e.g. using `right = n` but treating it like inclusive.
* Wrong width/termination: using `left < right` but setting `right = mid - 1` without correct mid bias can skip candidates or infinite loop.
* Not doing the final equality check in lower/upper bound forms.
*/
class Solution {
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while(left <= right){
            int mid = left + (right - left) / 2;

            if(nums[mid] == target){
                return mid;
            }else if(nums[mid] < target){
                left = mid + 1;
            }else{
                right = mid - 1;
            }
        }


        return -1;
    }
}