// Method 1: Two Pointer Approach
/*
# WHAT I WAS DOING WRONG:

### 1) It can get stuck forever

You do:

```java
int mid = left + (right - left) / 2;
...
else if(sum < target) left = mid;
else right = mid;
```

But when `right = left + 1`, `mid == left`.
If `sum < target`, you set `left = mid` → `left` stays the same → `left < right` still true → infinite loop.

Concrete example:

* `numbers = [2, 3]`, `target = 10`
* `left=0, right=1, mid=0`
* sum=5 < 10 → `left = mid = 0` (no change) → stuck.

Same issue on the other side when `mid == right` (though with this mid formula it’s usually `mid != right` unless you bias it).

### 2) The “binary search” move is logically wrong here

In Two Sum II, the correct monotonic rule is about **moving one pointer by 1**:

* If `numbers[left] + numbers[right] < target`, you must increase the sum by moving `left++`.
* If it’s `> target`, decrease the sum by moving `right--`.

Jumping to `mid` doesn’t preserve correctness because the sum depends on **both** pointers, and you haven’t established a monotonic predicate that allows halving the search space like binary search.

Even if you fix progress with `left = mid + 1` / `right = mid - 1`, it can still skip the correct pair.

### 3) Returning at the end is unsafe

You return `{left+1, right+1}` even if you never found a solution. LeetCode guarantees one exists, but with your logic you might never land on it, then return a wrong pair.

---

## What the correct approach should look like (conceptually)

Two pointers:

* start `left=0`, `right=n-1`
* while `left<right`:

  * `sum = numbers[left] + numbers[right]`
  * if `sum == target` return
  * if `sum < target` `left++`
  * else `right--`

If you want a *real* binary-search-based solution, the standard version is:

* for each `i`, binary search for `target - numbers[i]` in the range `(i+1..n-1)`.

But the “mid jump” inside a two-pointer loop is the part that breaks it.
*/
class Solution {
    public int[] twoSum(int[] numbers, int target) {
        int n = numbers.length;

        int left = 0;
        int right = n - 1;

        while(left < right){

            if(numbers[left] + numbers[right] == target){
                return new int[]{left + 1, right + 1};
            }else if(numbers[left] + numbers[right] < target){
                left++;
            }else{
                right--;
            }
        }

        return new int[] {-1, -1};
    }
}







// Method 2: Binary Search Approach
/*
### Complexity

* Outer loop runs `n` times
* Each binary search is `O(log n)`
* Total: **O(n log n)** time, **O(1)** extra space

---

## Why this is the “correct” binary-search pattern

Binary search requires a **monotonic condition** over a single sorted interval.

If you fix `i`, you’re looking for one value (`complement`) in the sorted subarray:

* `numbers[i+1 .. n-1]` is sorted
* “Is mid too small or too large?” is monotonic
  So classic binary search works perfectly.

This is different from trying to “binary search the two pointers” at the same time (which doesn’t form a monotonic predicate).

---

## Thorough walkthrough with an example

### Example

`numbers = [2, 7, 11, 15]`, `target = 9`

`n = 4`

---

### i = 0

* `numbers[i] = 2`
* `complement = 9 - 2 = 7`
* Search in subarray indices `[1..3]` → values `[7, 11, 15]`

Binary search:

* `left = 1`, `right = 3`
* `mid = 1 + (3-1)/2 = 2`
* `numbers[mid] = 11`
* Compare: `11 > 7` → move `right = mid - 1 = 1`

Next step:

* `left = 1`, `right = 1`
* `mid = 1`
* `numbers[mid] = 7`
* Match found!

Return:

* `{i+1, mid+1} = {1, 2}`

That matches the expected output.

---

## Another walkthrough (more iterations)

`numbers = [1, 2, 3, 4, 6, 10]`, `target = 16`

### i = 0

* `numbers[i] = 1`
* complement = 15
  Search `[2, 3, 4, 6, 10]`
* mid around 3/4, values too small → eventually not found

### i = 1

* `numbers[i] = 2`
* complement = 14
  Search `[3, 4, 6, 10]`
* not found

### i = 2

* `numbers[i] = 3`
* complement = 13
  Search `[4, 6, 10]`
* not found

### i = 3

* `numbers[i] = 4`
* complement = 12
  Search `[6, 10]`
* not found

### i = 4

* `numbers[i] = 6`
* complement = 10
  Search `[10]`
* mid = that element → found
  Return `{5, 6}` (1-based)

---

## Common pitfalls to avoid

* Search range must start at `i + 1` (can’t reuse the same element).
* Use `left <= right` in binary search.
* Return 1-based indices.
*/

// class Solution {
//     public int[] twoSum(int[] numbers, int target) {
//         int n = numbers.length;

//         for (int i = 0; i < n - 1; i++) {
//             int complement = target - numbers[i];

//             int left = i + 1;
//             int right = n - 1;

//             while (left <= right) {
//                 int mid = left + (right - left) / 2;

//                 if (numbers[mid] == complement) {
//                     // +1 because LeetCode wants 1-based indices
//                     return new int[] { i + 1, mid + 1 };
//                 } else if (numbers[mid] < complement) {
//                     left = mid + 1;
//                 } else {
//                     right = mid - 1;
//                 }
//             }
//         }

//         // Problem guarantees exactly one solution; this is just a fallback.
//         return new int[] { -1, -1 };
//     }
// }
