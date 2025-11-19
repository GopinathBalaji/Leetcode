// Method 1: Backtracking and maintaining already used values in a permutation
/*
### Hint 1 – What changes compared to “Subsets”?

In **Subsets**, each number is either:

* included or
* excluded.

In **Permutations**, each number is:

* used **exactly once**, and
* the **order matters**.

So your recursion needs to keep track of:

* which numbers are already used, and
* the current ordering.

---

### Hint 2 – Backtracking state

Classic backtracking state:

* `path` (a list) → the current permutation being built.
* `used[]` (boolean array) → whether `nums[i]` is already in `path`.

At each step:

* If `path.size() == n`, you’ve built one full permutation → add a copy to answer.
* Otherwise, try all indices `i`:

  * If `used[i] == false`, choose `nums[i]` next.

---

### Hint 3 – The recursion structure

Your DFS function might look conceptually like:

* Parameters: `path`, `used[]`, `ans`.
* For each index `i`:

  * skip if already used.
  * mark used, add to path, recurse.
  * then backtrack: unmark and remove from path.

Notice how this is similar to **Combination Sum / Subsets**, but now you loop from `0..n-1` every time (no `start` index), and use `used[]` instead.

---

### Hint 4 – Base case

When do you stop?

* Once you’ve picked exactly `n` numbers:

```text
if path.size() == nums.length:
    add a copy of path to the result
    return
```

You **must** copy the path (e.g., `new ArrayList<>(path)`), not store the same mutable list.

---

### Hint 5 – Example mental trace: nums = [1, 2, 3]

Think in a tree:

* Start: `[]`

  * Choose 1 → `[1]`, used = {1}

    * Choose 2 → `[1,2]`, used = {1,2}

      * Choose 3 → `[1,2,3]` → record
      * Backtrack to `[1,2]`
    * Choose 3 → `[1,3,2]` → record
    * Backtrack to `[1]`
  * Backtrack to `[]`
  * Choose 2 → `[2]`

    * Choose 1 → `[2,1,3]` → record
    * Choose 3 → `[2,3,1]` → record
  * Choose 3 → `[3]`

    * Choose 1 → `[3,1,2]` → record
    * Choose 2 → `[3,2,1]` → record

You’ll get all 6 permutations.

---

### Hint 6 – Time & space

* There are `n!` permutations.
* Time: `O(n · n!)` (each permutation costs up to `O(n)` to copy).
* Recursion depth: `n`.

That’s expected; there’s no way around factorial growth since you must output them all.

---

### Hint 7 – Alternative idea (swap in-place)

Another very interview-friendly pattern:

* Work directly on the `nums` array.
* At recursion level `pos`:

  * For every `i` from `pos` to `n-1`, swap `nums[pos]` with `nums[i]`, recurse with `pos+1`, then swap back.
* When `pos == n`, record current array as a permutation.

This avoids a `used[]` and extra `path` list; you permute in-place.

*/
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        List<Integer> permutations = new ArrayList<>();
        HashSet<Integer> visited = new HashSet<>();

        backtrack(nums, ans, permutations, visited);

        return ans;
    }

    private void backtrack(int[] nums, List<List<Integer>> ans, List<Integer> permutations, HashSet<Integer> visited){
        if(permutations.size() == nums.length){
            ans.add(new ArrayList<>(permutations));
            return;
        }

        for(int i=0; i<nums.length; i++){
            if(visited.contains(i)){
                continue;
            }
            visited.add(i);
            permutations.add(nums[i]);

            backtrack(nums, ans, permutations, visited);

            visited.remove(i);
            permutations.remove(permutations.size() - 1);
        }

        return;
    }
}