/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */


//  Middle element pivot for recursion
/*

## 1 · Why the “middle element” idea works

A BST’s **in‑order traversal** visits nodes in ascending order.
If we want the tree to be *balanced*, we should keep left and right sub‑trees roughly the same size.
The most natural way is:

1. **Pick the middle element** of the current sub‑array → make it the root.
2. Recursively build the **left subtree** from the left half.
3. Recursively build the **right subtree** from the right half.

Because we always split near the middle, the height becomes `O(log n)` (perfectly balanced if the array length is `(2^k – 1)`).

---

## 2 · Choosing the correct middle index

For the slice `[start … end]` (inclusive):

```text
mid = start + (end - start) / 2
```

*Why this formula?*

* `(end – start)/2` gives the **offset** to the midpoint.
* Adding `start` shifts that offset into the original array’s coordinates.
* Using integer division automatically floors the value (if the slice length is even, either middle works; this one biases to the left).

> **Never subtract 1** after dividing—that pushes `mid` outside the slice for small ranges.

---

## 3 · Recursive algorithm (divide‑and‑conquer)

```text
build(start, end):
    if start > end → return null      (empty slice)
    mid  = start + (end - start) // 2
    node = new TreeNode(nums[mid])
    node.left  = build(start, mid - 1)
    node.right = build(mid + 1, end)
    return node
```


### Why this works

* **BST property:** elements in left slice `< nums[mid] <` elements in right slice.
* **Balance:** each call cuts the slice roughly in half → height ≤ `⌈log₂ n⌉`.
* **Correctness by induction:**

  * Base: empty slice returns `null`.
  * Inductive step: if recursive calls produce valid BSTs for sub‑slices, attaching them under `nums[mid]` keeps the BST ordering intact.

### Complexity

| Metric             | Value                                                                                                             |
| ------------------ | ----------------------------------------------------------------------------------------------------------------- |
| Time               | `O(n)` — each element becomes exactly one node.                                                                   |
| Space (call stack) | `O(log n)` on average (`O(n)` worst if the array were badly skewed, but input is sorted so halves stay balanced). |

---

## 5 · Common pitfalls & fixes

| Pitfall                                   | Consequence                                        | Fix                            |
| ----------------------------------------- | -------------------------------------------------- | ------------------------------ |
| `mid = (start + end) / 2` on large `int`s | Potential overflow                                 | Use `start + (end - start)/2`  |
| `mid = (end - start) / 2 - 1`             | Off-by-one → negative index when slice length is 1 | Don’t subtract 1; add `start`. |
| Forgetting base case `start > end`        | Infinite recursion                                 | Always include it.             |

---

### Take‑away intuition

A sorted array’s *middle element* is the perfect pivot: it splits the numbers so that everything left is smaller and everything right is larger **while also keeping sub‑arrays similar in size**, guaranteeing a balanced BST. Recursively repeating this partitioning builds the tree in linear time with logarithmic depth.

*/
class Solution {
    public TreeNode sortedArrayToBST(int[] nums) {
        int mid = 0 + (nums.length - 1  - 0) / 2;
        TreeNode root = new TreeNode(nums[mid]);

        root.left = divideConquer(0, mid - 1, nums);
        root.right = divideConquer(mid + 1, nums.length - 1, nums);

        return root;
    }

    public TreeNode divideConquer(int start, int end, int[] nums){
        if(start > end || end < start){
            return null;
        }

        int mid = start + (end - start) / 2;
        TreeNode root = new TreeNode(nums[mid]);

        root.left = divideConquer(start, mid - 1, nums);
        root.right = divideConquer(mid + 1, end , nums);

        return root;
    }
}