// Method 1: Backtracking
/*
# WHAT I WAS DOING WRONG:

## 1. You never explore the “skip this candidate” branch

Your `backtrack` only *includes* `candidates[i]` and then moves to `i + 1`:

```java
combination.add(candidates[i]);
sum += candidates[i];

backtrack(candidates, target, ans, combination, sum, i + 1);

combination.remove(combination.size() - 1);
sum -= candidates[i];
```

There is **no call** that explores the branch “do NOT take `candidates[i]` and move on to `i + 1`”.

Instead, you try to simulate that by starting a fresh recursion from every `i` in the outer loop:

```java
for (int i = 0; i < candidates.length; i++) {
    if (candidates[i] < target) {
        int sum = 0;
        List<Integer> combination = new ArrayList<>();
        backtrack(candidates, target, ans, combination, sum, i);
    }
}
```

This is not equivalent. Starting from each `i` with a fresh combination means:

* You only ever build combinations that **start** at some index `i` and then **take all further numbers in some pattern**.
* You never build combinations that require **skipping earlier indices** in the same path.

### Example where this fails

```text
candidates = [1, 2, 3], target = 3
Expected: [ [1,2], [3] ]
```

Let’s see what your code does:

* Outer loop `i = 0` → start with empty `combination`, `sum = 0`

  * In `backtrack` at `i = 0`:

    * Add `1` → `combination = [1]`, `sum = 1`
    * Recurse with `i = 1`

      * Add `2` → `combination = [1,2]`, `sum = 3`
      * `sum == target` → record `[1,2]`
      * Recurse with `i = 2`, add `3` → `sum = 6` > target → return
    * Done, no branch “skip 2 and go to 3” → you never see `[1,3]` (which is invalid anyway for 3, but pattern holds)

* Outer loop `i = 1`:

  * Start fresh, only include `2` and then `3`, never hitting sum = 3 exactly.

* Outer loop `i = 2`:

  * `candidates[2] < target`? `3 < 3` is false, so you **never even call** backtrack for the value `3`.

So you only output `[[1,2]]` and completely miss `[3]`.

This is because you never have an `(exclude)` branch inside your recursion.

---

## 2. You ignore the case where `candidates[i] == target` in the outer loop

In `combinationSum2`, a candidate equal to `target` is a valid single-element combination.

But you only call `backtrack` when:

```java
if (candidates[i] < target) {
    ...
    backtrack(..., i);
}
```

So if `candidates[i] == target`, you skip it entirely.

### Example

```text
candidates = [3,4], target = 3
Expected: [ [3] ]
```

Your code:

* `i = 0`, `candidates[0] = 3`

  * `3 < 3`? No → skip
* `i = 1`, `4 < 3`? No → skip

No recursive calls at all, result is `[]` → **incorrect**.

---

## 3. You don’t handle **duplicates** at all

For **Combination Sum II**, candidates may contain duplicates, and the output must not contain duplicate combinations.

Standard requirement:

* Use each index at most once (`i+1` in recursion is good for that),
* But if `candidates` has equal values, e.g. `[1,1,2]`, you must **avoid generating the same combination more than once**.

The accepted pattern is:

1. **Sort** the array.
2. In your recursive `for` loop, skip duplicate values at the *same recursion level*:

   ```java
   if (i > start && candidates[i] == candidates[i - 1]) continue;
   ```

Your solution:

* Does not sort.
* Does not skip duplicates.
* Also has the outer loop, which can call `backtrack` multiple times starting from different positions but using the same values → even more chance of duplicate combinations.

So for something like:

```text
candidates = [1,1,2], target = 3
Expected: [ [1,2] ]  (only once)
```

You’d risk either missing combinations (due to no exclude branch) or creating duplicates in more complex cases.

---

## 4. Structure is non-standard and redundant

Even ignoring correctness issues, the pattern

```java
for (int i = 0; i < candidates.length; i++) {
    if (candidates[i] < target) {
        int sum = 0;
        List<Integer> combination = new ArrayList<>();
        backtrack(..., sum, i);
    }
}
```

is unusual and not necessary. The standard backtracking is:

* One call from `(start = 0, target, empty path)`.
* Inside the helper, loop `i` from `start` to `n - 1`, choose or skip within that single recursion tree.
* No outer loop needed—the recursion *already* explores all starting positions.

This matters in interviews: they’re used to a clean pattern and will be skeptical of nested “starting loops” plus recursion.

---

## 5. Minor: continuing recursion after `sum == target`

In `backtrack`:

```java
if (sum == target && i < candidates.length) {
    ans.add(new ArrayList<>(combination));
}

combination.add(candidates[i]);
sum += candidates[i];

backtrack(..., sum, i + 1);
```

Once `sum == target`, you’ve already recorded the combination. But you still do:

* `combination.add(candidates[i])`
* `sum += candidates[i]`
* `backtrack(..., sum, i + 1)`

Those calls will always lead to `sum > target` and immediately return due to the first `if`:

```java
if (sum > target || i >= candidates.length) {
    return;
}
```

So it doesn’t break correctness, but it’s wasted work. A cleaner version would `return` immediately after adding the valid combination.

---

## What the correct backtracking should look like (high-level)

For **Combination Sum II**, the typical interview-friendly solution is:

1. **Sort** the candidates.
2. Call a helper once: `dfs(start = 0, remainingTarget, currentList)`.
3. In the helper:

   * If `remainingTarget == 0`: record currentList and return.
   * For `i` from `start` to `n-1`:

     * If `i > start && candidates[i] == candidates[i-1]` → `continue` (skip duplicates).
     * If `candidates[i] > remainingTarget` → `break` (since sorted, no need to go further).
     * Choose `candidates[i]`: add to path, recurse with `i+1` (can’t reuse the same index), and `remainingTarget - candidates[i]`.

This fixes:

* Missing “exclude” branch (handled by the `for` loop at each level),
* Duplicate combinations (skip when `i > start` and value repeats),
* Use-each-element-once (recurse with `i+1`),
* Starting-point logic (no outer loop needed),
* Handling candidates equal to target.

---

### TL;DR — Main issues with your code

1. No “skip” branch in recursion → misses valid combinations.
2. Outer loop starting recursion from each `i` is not a correct replacement for branching.
3. Skips candidates exactly equal to target in the outer `if` (`< target`).
4. No handling of duplicate candidate values (no sorting, no skip-duplicate logic).
5. Slight inefficiencies after `sum == target`.
*/

class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> ans = new ArrayList<>();
        List<Integer> combination = new ArrayList<>();
        Arrays.sort(candidates);

        backtrack(candidates, target, ans, combination, 0);
        
        return ans;
    }


    private void backtrack(int[] candidates, int target, List<List<Integer>> ans, List<Integer> combination, int start){
        
        if(target == 0){
            ans.add(new ArrayList<>(combination));
            return;
        }

        for(int i=start; i<candidates.length; i++){
            if(i > start && candidates[i] == candidates[i-1]){  // skip duplicates
                continue;
            }
            if(candidates[i] > target){  // pruning
                break;
            }

            combination.add(candidates[i]);
            
            backtrack(candidates, target - candidates[i], ans, combination, i + 1);

            combination.remove(combination.size() - 1);
        }

        return;
    }
}