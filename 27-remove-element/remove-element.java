// Method 1: Write-forward while Preserving order (more writes, but more efficient when val is frequent)
// Idea: scan once; copy every non-val forward.
/*
## Example

```
nums  = [0, 1, 2, 2, 3, 0, 4, 2]
val   = 2
i     = 0  (write index; length of the “kept” prefix so far)
```

We scan with `j = 0..7`.
Rule: if `nums[j] != val`, copy it forward to `nums[i]` and increment `i`. Otherwise skip.

| j | nums\[j] | Action                                      | nums after the step        | i |
| - | -------- | ------------------------------------------- | -------------------------- | - |
| 0 | 0        | keep → `nums[i]=nums[j]` (write to index 0) | `[0, 1, 2, 2, 3, 0, 4, 2]` | 1 |
| 1 | 1        | keep → write to index 1                     | `[0, 1, 2, 2, 3, 0, 4, 2]` | 2 |
| 2 | 2        | equals `val` → **skip**                     | (unchanged)                | 2 |
| 3 | 2        | equals `val` → **skip**                     | (unchanged)                | 2 |
| 4 | 3        | keep → write to index 2                     | `[0, 1, 3, 2, 3, 0, 4, 2]` | 3 |
| 5 | 0        | keep → write to index 3                     | `[0, 1, 3, 0, 3, 0, 4, 2]` | 4 |
| 6 | 4        | keep → write to index 4                     | `[0, 1, 3, 0, 4, 0, 4, 2]` | 5 |
| 7 | 2        | equals `val` → **skip**                     | (unchanged)                | 5 |

**Result:** `i = 5` → the new length is **5**.
LeetCode only checks the first `i` elements; they are the kept ones in original order:

```
nums[0..i-1] = [0, 1, 3, 0, 4]
```

Everything beyond index `i-1` can be ignored.

---

## Why this works (and why it’s stable)

* Each non-`val` element is written exactly **once** to the next free slot; relative order is preserved because we encounter them in order and fill `i=0,1,2,...`.
* We never need swaps or an inner search; it’s one clean pass.
* If `j == i` (common early on), writing `nums[i]=nums[j]` is effectively a no-op.

---

## Quick edge cases (to sanity-check yourself)

* **No occurrences:** `nums=[1,3,5], val=2` → `i` ends as 3; array unchanged; return 3.
* **All occurrences:** `nums=[2,2,2], val=2` → `i` stays 0; return 0 (content beyond 0 ignored).
* **Empty array:** return 0.

**Complexity:** O(n) time, O(1) extra space.
*/
class Solution {
    public int removeElement(int[] nums, int val) {
        
        int i = 0;

        for(int j=0; j<nums.length; j++){
            if(nums[j] != val){
                nums[i] = nums[j];
                i++;
            }
        }

        return i;
    }
}



// Method 2: Swap with end (does not preserve order but fewer writes when val is rare)
// Idea: whenever you see val at the front, replace it with the last unchecked 
//       element and shrink the effective array.
/*
## Why this works (quick intuition)

* Every time you hit `val` at the front, you **replace it** with the last unchecked element and **decrease** the effective array length.
* You **don’t** advance `i` after a replacement so you can **re-check** the swapped-in value (it might also be `val`).
* When the loop ends, the first `n` positions contain all non-`val` elements (in any order), and `n` is the new length.

---

## Detailed walkthrough (example)

**Input**

```
nums = [0, 1, 2, 2, 3, 0, 4, 2],  val = 2
```

Start: `i = 0`, `n = 8`

We’ll show (`i`, `n`, action) and the array after each step.
Remember: if `nums[i] == val`, we write `nums[i] = nums[n-1]` and `n--` (no `i++`); otherwise `i++`.

1. `i=0`, `n=8`, nums\[0]=0 ≠ 2 → **i++**
   → `i=1`, `n=8`, array unchanged: `[0,1,2,2,3,0,4,2]`

2. `i=1`, nums\[1]=1 ≠ 2 → **i++**
   → `i=2`, `n=8`, `[0,1,2,2,3,0,4,2]`

3. `i=2`, nums\[2]=2 == 2 → **replace with last unchecked (idx 7)**

   * `nums[2] = nums[7] = 2`, `n=7` (no `i++`)
     → `i=2`, `n=7`, array: `[0,1,2,2,3,0,4,2]` (looks same because we swapped in another 2)

4. Re-check `i=2`, nums\[2]=2 == 2 → **replace with new last (idx 6)**

   * `nums[2] = nums[6] = 4`, `n=6` (no `i++`)
     → `i=2`, `n=6`, array: `[0,1,4,2,3,0,4,2]`

5. Re-check `i=2`, nums\[2]=4 ≠ 2 → **i++**
   → `i=3`, `n=6`, `[0,1,4,2,3,0,4,2]`

6. `i=3`, nums\[3]=2 == 2 → **replace with last unchecked (idx 5)**

   * `nums[3] = nums[5] = 0`, `n=5` (no `i++`)
     → `i=3`, `n=5`, array: `[0,1,4,0,3,0,4,2]`

7. Re-check `i=3`, nums\[3]=0 ≠ 2 → **i++**
   → `i=4`, `n=5`, `[0,1,4,0,3,0,4,2]`

8. `i=4`, nums\[4]=3 ≠ 2 → **i++**
   → `i=5`, `n=5` → loop stops (`i < n` is false)

**Result**

* New length `n = 5`
* First `n` elements (kept, order not preserved):

  ```
  nums[0..4] = [0, 1, 4, 0, 3]
  ```
* Elements beyond index `4` don’t matter.

### Key moments to notice

* Steps 3–4 show why we **re-check** after swapping: the first swap pulled in another `2`, so we had to swap again before moving on.
* We never touch positions `>= n` again; they’re considered “removed area”.

---

## Complexity

* **Time:** O(n) — each index is examined up to a constant number of times.
* **Space:** O(1) — in-place.
* **Order:** not preserved (which is acceptable for LeetCode 27).
*/
// class Solution {
//     public int removeElement(int[] nums, int val) {
//         int i = 0;              // scan index (front)
//         int n = nums.length;    // effective end (elements beyond n-1 are "removed")

//         while (i < n) {
//             if (nums[i] == val) {
//                 // Overwrite current 'val' with the last unchecked element
//                 nums[i] = nums[n - 1];
//                 // Shrink the effective length (we've "deleted" one)
//                 n--;
//                 // IMPORTANT: do NOT i++ here; re-check nums[i] next loop
//                 // because the swapped-in value might also equal 'val'
//             } else {
//                 i++; // keep this element; move forward
//             }
//         }
//         // 'n' is the new length; nums[0..n-1] are the kept elements (order may change)
//         return n;
//     }
// }