// Method 1: Modified Half-Open Interval Binary Search
/*
## Key idea

We still do binary search around the **pivot** (the minimum), but duplicates can make it impossible to decide which half is sorted.

The cleanest strategy compares `nums[mid]` with `nums[right]`.

### Why compare with `nums[right]`?

Think of `right` as a reference in the “tail” of the array. In a rotated sorted array, the **minimum is always in the same sorted block as the right end** (unless the array is fully sorted, but that’s fine too). Comparing with `nums[right]` helps determine whether `mid` lies in the left block (before the pivot) or right block (after the pivot).

---

## Cases (the heart of the solution)

Let `mid = left + (right-left)/2`.

### Case 1: `nums[mid] > nums[right]`

`mid` is in the left (larger) sorted block, and the pivot/minimum must be to the **right of mid**.

So:

```java
left = mid + 1;
```

### Case 2: `nums[mid] < nums[right]`

`mid` is in the right (smaller) sorted block that contains the minimum, so the minimum is at `mid` or to the **left of mid**.

So:

```java
right = mid;
```

### Case 3: `nums[mid] == nums[right]`  (the duplicates problem)

You can’t tell which side contains the minimum because `nums[mid]` equals `nums[right]`. Example: `[2,2,2,0,1,2]`—mid could be on either side of pivot.

But you can **safely shrink** the search space:

```java
right--;
```

#### Why is `right--` safe?

If `nums[right]` is the minimum, then since `nums[mid] == nums[right]`, there exists another element equal to it (at `mid`), so removing `right` won’t remove the only minimum.
If `nums[right]` is not the minimum, removing it obviously doesn’t hurt.

This is the standard way duplicates are handled, and it’s why the worst case degrades to O(n) (e.g., all elements equal: you’ll keep doing `right--`).

### Complexity

* Average-ish: close to **O(log n)**
* Worst-case (many duplicates like `[1,1,1,1,1]`): **O(n)**
* Space: **O(1)**

---

# Thorough example walkthroughs

## Walkthrough 1: `nums = [2, 2, 2, 0, 1, 2]`

Minimum is `0`.

Start:

* `left=0`, `right=5` (`nums[right]=2`)

### Iteration 1

* `mid = 0 + (5-0)/2 = 2`
* `nums[mid]=2`, `nums[right]=2` → equal
* Case 3 → `right--`
  Now: `left=0`, `right=4` (`nums[right]=1`)

### Iteration 2

* `mid = 0 + (4-0)/2 = 2`
* `nums[mid]=2`, `nums[right]=1`
* `nums[mid] > nums[right]` → Case 1
* `left = mid + 1 = 3`
  Now: `left=3`, `right=4`

### Iteration 3

* `mid = 3 + (4-3)/2 = 3`
* `nums[mid]=0`, `nums[right]=1`
* `nums[mid] < nums[right]` → Case 2
* `right = mid = 3`
  Now: `left=3`, `right=3` stop

Return `nums[3] = 0` ✅

---

## Walkthrough 2: `nums = [1, 1, 1, 1, 1]`

Minimum is `1`.

Start:

* `left=0`, `right=4`

Each iteration:

* `mid` will have value 1, `nums[right]=1`
* Case 3: `right--`

Progress:

* right=4 → 3 → 2 → 1 → 0
  Stop when `left=0`, `right=0`
  Return 1 ✅

This shows the **O(n) worst case**.

---

## Walkthrough 3: `nums = [3, 4, 5, 1, 2, 2]`

Minimum is `1`.

Start:

* `left=0`, `right=5` (`nums[right]=2`)

### Iteration 1

* `mid=2`, `nums[mid]=5`, `nums[right]=2`
* `5 > 2` → Case 1 → `left=3`

Now `left=3`, `right=5`

### Iteration 2

* `mid=4`, `nums[mid]=2`, `nums[right]=2`
* equal → Case 3 → `right=4`

Now `left=3`, `right=4`

### Iteration 3

* `mid=3`, `nums[mid]=1`, `nums[right]=2`
* `1 < 2` → Case 2 → `right=3`

Stop: `left=3`, `right=3`
Return `nums[3]=1` ✅

---

## Common pitfalls

* Using the 153 logic without handling `==` properly → infinite loop or wrong half chosen.
* When equal, doing `left++` is also possible, but `right--` is standard and easier to reason about.
* Using `<=` early exit incorrectly; safe early exit is `if (nums[left] < nums[right]) return nums[left]` (strict `<`).
*/
class Solution {
    public int findMin(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        if(nums[left] < nums[right]){
            return nums[left];
        }

        while(left < right){
            int mid = left + (right - left) / 2;

            if(nums[mid] > nums[right]){
                left = mid + 1;
            }else if(nums[mid] < nums[right]){
                right = mid;
            }else{
                right--;    
            }
        }

        return nums[left];
    }
}