// Use Backtracking
/*
##  Key Idea Behind the Solution

You're using **backtracking**, which is perfect here. The intuition you had was correct:

> To avoid duplicates, each number should only be combined with numbers that come **after it** in the sequence.

This ensures we don’t get both `[1,2]` and `[2,1]`, which are considered the same combination.

---

##  How the Backtracking Works

### Method Signature

```java
private void backtrack(int cur, int n, int k, List<Integer> path, List<List<Integer>> ans)
```

* `cur`: the current number we’re considering to add next.
* `path`: the current combination being built.
* `ans`: the list of all valid combinations.

---

###  Step-by-Step Walkthrough

Let’s walk through `combine(4, 2)`.

#### Initial call:

```java
backtrack(1, 4, 2, [], ans)
```

#### First level:

* `i = 1`: add `1` → `path = [1]`

  * recurse → `backtrack(2, 4, 2, [1], ans)`

#### Second level:

* `i = 2`: add `2` → `path = [1,2]`

  *  `path.size() == 2`, so add a **copy** of `[1,2]` to `ans`.
  * Backtrack: remove `2` → `path = [1]`
* `i = 3`: add `3` → `path = [1,3]`

  * add copy of `[1,3]` to `ans`
  * Backtrack
* `i = 4`: add `4` → `path = [1,4]`

  *  add copy of `[1,4]` to `ans`
  * Backtrack

Back to first level:

* `i = 2`: add `2` → `path = [2]`

  * recurse → `backtrack(3, 4, 2, [2], ans)`

    * `i = 3`: add `3` → `path = [2,3]` →  add
    * `i = 4`: add `4` → `path = [2,4]` →  add
* `i = 3`: add `3` → `path = [3]`

  * recurse → `backtrack(4, 4, 2, [3], ans)`

    * `i = 4`: add `4` → `path = [3,4]` →  add
* `i = 4`: `path = [4]`, can't go further (only 1 number)

Final output:

```
[[1,2], [1,3], [1,4], [2,3], [2,4], [3,4]]
```

---

## Why We Need `new ArrayList<>(path)`

This is **extremely important**.

In Java:

```java
ans.add(path);
```

just adds a **reference** to `path`. But `path` gets changed later during recursion and backtracking. That means all the entries in `ans` would end up being **the same list**, or at least incorrect lists.

By using:

```java
ans.add(new ArrayList<>(path));
```

You make a **copy** of the current `path` and add that to `ans`. So, even if `path` changes later, the already added copy is safe.

---

###  Real Example of What Could Go Wrong:

Suppose we add a reference like this:

```java
ans.add(path); // no copy
```

Then you backtrack and modify `path` (e.g., remove last element or add a new one). Now the `ans` list still contains the **same list**, so all entries in `ans` get affected and eventually hold the same values.

---

##  Summary

* You use backtracking to explore all combinations.
* At each step, you try numbers starting from `cur` to `n`, ensuring no duplicates.
* When `path.size() == k`, you add it to the result.
* You must add a **copy** of `path`, not the reference, to prevent corruption of the result list.
*/

class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> ans = new ArrayList<>();

        backtrack(1, n, k, new ArrayList<Integer>(), ans);

        return ans;
    }

    private void backtrack(int cur, int n, int k, List<Integer> path, List<List<Integer>> ans){
        if(path.size() == k){
            ans.add(new ArrayList<>(path));

            return;
        }

        for(int i=cur; i<=n; i++){
            path.add(i);
            backtrack(i + 1, n, k, path, ans);
            path.remove(path.size() - 1);
        }
    }
}