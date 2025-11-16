// Method 1: Recursive Backtracking
/*
### Hint 1 — Think “choose / don’t choose”

For each element `nums[i]`, you have two options:

* **Include** it in the current subset, or
* **Exclude** it.
  A depth-first recursion that explores both choices at every index naturally enumerates all subsets.

---

### Hint 2 — What’s the base case?

When your index reaches `n` (past the last element), the current “path” you built is a complete subset. **Add a copy** of it to the answer, then return.

---

### Hint 3 — Order matters (for structure, not for values)

Always move the index forward (`i+1`). This ensures you never revisit earlier elements and each subset is produced once.

---

### Hint 4 — Where to “push” results

You can:

* Either add the current path to the answer **at every node** (preorder), or
* Only at the leaves (when `i == n`).
  Both work; the “at every node” pattern yields subsets in increasing sizes naturally.

---

### Hint 5 — Iterative build-up alternative

Start with `ans = [[]]`.
For each number `x` in `nums`, **clone** all existing subsets and append `x` to each clone, then add those to `ans`.
This doubles the number of subsets each step.

---

### Hint 6 — Bitmask trick (compact and fast)

There are `2^n` subsets. For `mask` from `0` to `(1<<n) - 1`, pick `nums[j]` whenever the `j`-th bit of `mask` is 1. Each mask uniquely represents a subset.

---

### Hint 7 — Tiny walkthrough (backtracking on `[1,2,3]`)

* Start `path=[]` at `i=0`

  * Take `1` → `path=[1]`, go to `i=1`

    * Take `2` → `path=[1,2]`, go to `i=2`

      * Take `3` → `[1,2,3]` → add; backtrack
      * Skip `3` → `[1,2]` → add; backtrack
    * Skip `2` → `path=[1]`, go to `i=2`

      * Take `3` → `[1,3]` → add; backtrack
      * Skip `3` → `[1]` → add; backtrack
  * Skip `1` → `path=[]`, go to `i=1`

    * Take `2` → `[2]` …
    * Skip `2` → `[]`

      * Take `3` → `[3]`
      * Skip `3` → `[]`
        Result (one possible order):
        `[], [3], [2], [2,3], [1], [1,3], [1,2], [1,2,3]`.

---

### Hint 8 — Complexity + gotchas

* There are **`2^n`** subsets; total output size is `O(2^n * n)`.
* Make sure to **copy** the current path before storing it (don’t store a mutable reference).
* This problem guarantees **distinct** numbers; if you see duplicates, that’s **Subsets II (90)** which needs sorting + skip-duplicates logic.
*/
class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        List<Integer> subset = new ArrayList<>();

        return backtrack(nums, ans, subset, 0);
    }

    private List<List<Integer>> backtrack(int[] nums, List<List<Integer>> ans, List<Integer> subset, int i){
        if(i == nums.length){
            ans.add(new ArrayList<>(subset));
            return ans;
        }

        subset.add(nums[i]);
        List<List<Integer>> temp = backtrack(nums, ans, subset, i+1);

        subset.remove(subset.size() - 1);
        temp = backtrack(nums, ans, subset, i+1);

        return temp;
    }
}



// Method 1.5: Cleaner Recursive Backtracking
/*
Returning lists from backtrack is unnecessary (and confusing):

You pass ans by reference and mutate it in-place. There’s no need to return it, and your temp variable doesn’t help anything. Make backtrack void.
*/
// class Solution {
//     public List<List<Integer>> subsets(int[] nums) {
//         List<List<Integer>> ans = new ArrayList<>();
//         backtrack(nums, ans, new ArrayList<>(), 0);
//         return ans;
//     }

//     private void backtrack(int[] nums, List<List<Integer>> ans,
//                            List<Integer> subset, int i) {
//         if (i == nums.length) {
//             ans.add(new ArrayList<>(subset)); // <-- copy!
//             return;
//         }

//         // include nums[i]
//         subset.add(nums[i]);
//         backtrack(nums, ans, subset, i + 1);

//         // exclude nums[i]
//         subset.remove(subset.size() - 1);
//         backtrack(nums, ans, subset, i + 1);
//     }
// }





// Method 2: Iterative Backtracking
/*
Idea: push “states” (i, path) on a stack.
At each state you branch to:
    exclude nums[i] → (i+1, path)
    include nums[i] → (i+1, path+[nums[i]])
When i == n, add path to answers.

### Walkthrough on `[1,2,3]`

Stack holds states (top first). Start: `[(0, [])]`.

1. Pop `(0,[])` → push exclude `(1,[])` and include `(1,[1])`.
2. Pop `(1,[1])` → push `(2,[1])`, `(2,[1,2])`.
3. Pop `(2,[1,2])` → push `(3,[1,2])`, `(3,[1,2,3])`.
4. Pop `(3,[1,2,3])` → `i==n` → add `[1,2,3]`.
5. Pop `(3,[1,2])` → add `[1,2]`.
6. Pop `(2,[1])` → push `(3,[1])`, `(3,[1,3])` → add `[1,3]`, `[1]`.
7. Pop `(1,[])` → push `(2,[])`, `(2,[2])` → eventually add `[2,3]`, `[2]`, `[3]`, `[]`.

(Exact order depends on push/pop order; all 8 subsets appear.)

**Pros:** dead simple.
**Cons:** many path copies on the stack (still fine for interviews).
*/

// class Solution {
//     public List<List<Integer>> subsets(int[] nums) {
//         List<List<Integer>> ans = new ArrayList<>();
//         Deque<State> stack = new ArrayDeque<>();
//         stack.push(new State(0, new ArrayList<>()));

//         while (!stack.isEmpty()) {
//             State cur = stack.pop();
//             int i = cur.i;
//             List<Integer> path = cur.path;

//             if (i == nums.length) {
//                 ans.add(path); // already a distinct list
//                 continue;
//             }

//             // Branch 1: exclude nums[i]
//             stack.push(new State(i + 1, new ArrayList<>(path)));

//             // Branch 2: include nums[i]
//             List<Integer> with = new ArrayList<>(path);
//             with.add(nums[i]);
//             stack.push(new State(i + 1, with));
//         }
//         return ans;
//     }

//     static class State {
//         int i;
//         List<Integer> path;
//         State(int i, List<Integer> path) { this.i = i; this.path = path; }
//     }
// }






// Method 3: Bit Manipulation
/*
# Big idea (in one line)

Every subset corresponds to a **bitmask** of length `n` (one bit per element):
bit `j` is `1` ⇔ include `nums[j]` in the subset.

So if `n = nums.length`, there are `2^n` masks from `0` to `(1<<n) - 1`, each mapping to exactly one subset.

---

# How to implement

1. Let `n = nums.length` and `total = 1 << n` (that’s `2^n`).
2. For each `mask` in `[0, total)`:

   * Start an empty `subset`.
   * For each index `j` in `[0, n)`:

     * If the `j`-th bit is on (`(mask & (1 << j)) != 0`), append `nums[j]`.
   * Push `subset` into `ans`.
3. Return `ans`.

**Time:** `O(n · 2^n)` (you inspect up to `n` bits for each mask).
**Space (extra):** `O(1)` beyond the output.


> Notes:
>
> * This enumerates subsets in the order of mask (0..2^n-1). That’s fine for the problem.
> * LeetCode 78 guarantees **distinct** numbers; if duplicates were possible (that’s 90. Subsets II), you’d need extra handling.

---

# Detailed walkthrough with `nums = [1, 2, 3]`

`n = 3`, `total = 1 << 3 = 8`. We iterate `mask` from `0` to `7`.

| mask | binary (3 bits) | bits set | subset    |
| ---: | --------------- | -------- | --------- |
|    0 | 000             | –        | `[]`      |
|    1 | 001             | {0}      | `[1]`     |
|    2 | 010             | {1}      | `[2]`     |
|    3 | 011             | {0,1}    | `[1,2]`   |
|    4 | 100             | {2}      | `[3]`     |
|    5 | 101             | {0,2}    | `[1,3]`   |
|    6 | 110             | {1,2}    | `[2,3]`   |
|    7 | 111             | {0,1,2}  | `[1,2,3]` |

Mechanically for `mask = 5 (101₂)`:

* `j=0`: `(5 & 1) != 0` → include `nums[0]=1`
* `j=1`: `(5 & 2) == 0` → skip `nums[1]`
* `j=2`: `(5 & 4) != 0` → include `nums[2]=3`
  → subset = `[1, 3]`.

---

# (Optional) Gray-code variant (fun fact)

You can enumerate masks in **Gray code** order (`g = mask ^ (mask >> 1)`) so only **one** bit changes each step. Then you can maintain a single list and add/remove one element per step. It’s neat to mention, but the standard mask loop above is perfectly interview-ready.
*/

// class Solution {
//     public List<List<Integer>> subsets(int[] nums) {
//         int n = nums.length;
//         int total = 1 << n;             // 2^n
//         List<List<Integer>> ans = new ArrayList<>(total);

//         for (int mask = 0; mask < total; mask++) {
//             List<Integer> subset = new ArrayList<>();
//             for (int j = 0; j < n; j++) {
//                 if ((mask & (1 << j)) != 0) {
//                     subset.add(nums[j]);
//                 }
//             }
//             ans.add(subset);
//         }
//         return ans;
//     }
// }
