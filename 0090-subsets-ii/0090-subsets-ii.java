// Method 1: Backtracking with better deduplication using sorting
/*
Key points:
Arrays.sort(nums) groups equal values.
At each recursion level, if (i > start && nums[i] == nums[i-1]) continue; skips using the same value as the first element of a choice twice.
No Set, no contains.

We’ll track:

* `start` = where this recursion level’s loop begins
* `i` = current index in the loop
* `path` = current subset being built
* `ans` = all subsets collected so far

And we’ll see exactly **when and why** the `if (i > start && nums[i] == nums[i-1]) continue;` line skips duplicates.

---

## Step 0: Sort input

Input: `nums = [1, 2, 2]`
It’s already sorted, so still `[1, 2, 2]`.

---

## Initial call

We call:

```java
backtrack(nums, start = 0, path = [], ans = [])
```

### At `start = 0, path = []`

First line inside `backtrack`:

```java
ans.add(new ArrayList<>(path));
```

* We add a copy of `[]` to `ans`

Now:
`ans = [ [] ]`

Now the loop:

```java
for (int i = start; i < nums.length; i++) {
    ...
}
```

So here: `start = 0`, `nums.length = 3`, so:

* `i = 0, 1, 2`

---

### Level 1: `start = 0`

#### 1) i = 0

* `nums[0] = 1`
* Check duplicate condition:

  ```java
  if (i > start && nums[i] == nums[i - 1]) continue;
  ```

  * `i = 0`, `start = 0` → `i > start` is `0 > 0` → false
  * So we **do not** skip.

We **include** `nums[0]`:

```java
path.add(1);     // path = [1]
backtrack(nums, start = 1, path = [1], ans);
```

---

## Level 2: `start = 1, path = [1]`

We’re now in:

```java
backtrack(nums, start = 1, path = [1], ans)
```

First line:

```java
ans.add(new ArrayList<>(path));
```

Now `ans` becomes:

* `[]`
* `[1]`

So: `ans = [ [], [1] ]`

Loop at this level:

```java
for (int i = 1; i < 3; i++) {
    ...
}
```

So `i = 1, 2`.

---

### Level 2: `start = 1, path = [1]`

#### 2) i = 1

* `nums[1] = 2`
* Duplicate check:

  ```java
  if (i > start && nums[i] == nums[i - 1]) continue;
  ```

  * `i = 1`, `start = 1`
  * `i > start` → `1 > 1` → false
  * So we don’t skip.

Include `nums[1]`:

```java
path.add(2);     // path = [1, 2]
backtrack(nums, start = 2, path = [1, 2], ans);
```

---

## Level 3: `start = 2, path = [1,2]`

We’re in:

```java
backtrack(nums, start = 2, path = [1,2], ans)
```

First line:

```java
ans.add(new ArrayList<>(path));
```

`ans` now:

* `[]`
* `[1]`
* `[1, 2]`

Loop:

```java
for (int i = 2; i < 3; i++) {
    ...
}
```

So only `i = 2`.

---

### Level 3: `start = 2, path = [1,2]`

#### 3) i = 2

* `nums[2] = 2`
* Duplicate check:

  ```java
  if (i > start && nums[i] == nums[i - 1]) continue;
  ```

  * `i = 2`, `start = 2`
  * `i > start` → `2 > 2` → false
  * So we do **not** skip.

Include `nums[2]`:

```java
path.add(2);     // path = [1, 2, 2]
backtrack(nums, start = 3, path = [1, 2, 2], ans);
```

---

## Level 4: `start = 3, path = [1,2,2]`

Now:

```java
backtrack(nums, start = 3, path = [1,2,2], ans)
```

`start == nums.length` (3 == 3), but the code handles that naturally:

First line:

```java
ans.add(new ArrayList<>(path));
```

Add `[1, 2, 2]`:

Now `ans` is:

* `[]`
* `[1]`
* `[1,2]`
* `[1,2,2]`

Loop:

```java
for (int i = 3; i < 3; i++) { ... }   // no iterations
```

Return to previous level.

Backtrack step:

```java
path.remove(path.size() - 1);  // remove last element
// path was [1,2,2] -> becomes [1,2]
```

Return to Level 3 (`start = 2, path = [1,2]`).

---

### Back at Level 3

Loop ended (i was only 2).
Return to Level 2 (`start = 1, path = [1]`), and backtrack:

```java
path.remove(path.size() - 1); // remove the 2
// path: [1,2] -> [1]
```

---

### Level 2: continue loop (`start = 1, path = [1]`)

We already handled `i = 1`. Next:

#### 4) i = 2

* `nums[2] = 2`
* Duplicate check:

  ```java
  if (i > start && nums[i] == nums[i - 1]) continue;
  ```

  * `i = 2`, `start = 1`
  * `i > start` → `2 > 1` → true
  * `nums[2] == nums[1]` → `2 == 2` → true

So condition is **true** → we `continue` (skip this value).

Why? Because at this recursion level (with `start = 1`), we already considered picking a `2` when `i = 1`. If we also consider picking `2` when `i = 2` as the *first 2* at this level, we’d get duplicate subsets like `[1,2]` twice.

So we **skip `i = 2`** here.

Loop ends at Level 2.
Return to Level 1 (`start = 0, path = [1]`), then backtrack:

```java
path.remove(path.size() - 1);     // remove 1
// path: [1] -> []
```

---

### Level 1: continue loop (`start = 0, path = []`)

We’ve done `i = 0`. Next:

#### 5) i = 1

* `nums[1] = 2`
* Duplicate check:

  ```java
  if (i > start && nums[i] == nums[i - 1]) continue;
  ```

  * `i = 1`, `start = 0`
  * `i > start` → `1 > 0` → true
  * `nums[1] == nums[0]` → `2 == 1` → false

Since `nums[1] != nums[0]`, we **do not skip**.

Include `nums[1]`:

```java
path.add(2);    // path = [2]
backtrack(nums, start = 2, path = [2], ans);
```

---

## Level 2 (again): `start = 2, path = [2]`

We’re in:

```java
backtrack(nums, start = 2, path = [2], ans)
```

First line:

```java
ans.add(new ArrayList<>(path));
```

`ans` now includes `[2]` as well:

* `[]`
* `[1]`
* `[1,2]`
* `[1,2,2]`
* `[2]`

Loop:

```java
for (int i = 2; i < 3; i++) { ... }
```

So `i = 2`.

---

### Level 2 (second time): `start = 2, path = [2]`

#### 6) i = 2

* `nums[2] = 2`
* Duplicate check:

  ```java
  if (i > start && nums[i] == nums[i - 1]) continue;
  ```

  * `i = 2`, `start = 2`
  * `i > start` → `2 > 2` → false
  * So we **do not** skip.

Important: **this is different** from the earlier case with `start = 1`. Here `start = 2`, so `i > start` is false.

Include `nums[2]`:

```java
path.add(2);    // path = [2,2]
backtrack(nums, start = 3, path = [2,2], ans);
```

---

## Level 3: `start = 3, path = [2,2]`

First line:

```java
ans.add(new ArrayList<>(path));
```

Add `[2,2]`:

Now `ans` has:

* `[]`
* `[1]`
* `[1,2]`
* `[1,2,2]`
* `[2]`
* `[2,2]`

Loop `for (int i = 3; i < 3; i++)` → none.

Backtrack:

```java
path.remove(path.size() - 1);     // [2,2] -> [2]
```

Return to Level 2 (`start = 2, path = [2]`).

Loop ends; return to Level 1.

Backtrack there:

```java
path.remove(path.size() - 1);     // [2] -> []
```

---

### Level 1: continue loop (`start = 0, path = []`)

Next `i`:

#### 7) i = 2

* `nums[2] = 2`
* Duplicate check:

  ```java
  if (i > start && nums[i] == nums[i - 1]) continue;
  ```

  * `i = 2`, `start = 0`
  * `i > start` → `2 > 0` → true
  * `nums[2] == nums[1]` → `2 == 2` → true

So condition is **true** → we `continue` (skip).

Why skip here?

At the top level (`start = 0`), we already considered picking `2` as the **first element** when `i = 1`. If we pick `2` again as the first element when `i = 2`, we’d create duplicate subsets like `[2]` and `[2,2]` starting from the second `2`.

So we skip that branch to avoid duplicates.

Loop ends. Recursion ends.

---

## Final result

Collected subsets in `ans`:

* `[]`
* `[1]`
* `[1,2]`
* `[1,2,2]`
* `[2]`
* `[2,2]`

Exact order may differ depending on traversal order, but the set of subsets is:

```text
[ [], [1], [1,2], [1,2,2], [2], [2,2] ]
```

No duplicates, and all unique subsets of `[1,2,2]`.

---

## Key intuition about the duplicate-skip line

```java
if (i > start && nums[i] == nums[i - 1]) continue;
```

* `i > start` means: “this is **not** the first element at this recursion level.”
* `nums[i] == nums[i-1]` means: “this value is the **same as the previous one**.”

Together, that means:

> “At this recursion level, I’ve already used this value as the first choice. Don’t use it again as the first choice here, or I’ll create duplicate subsets.”

That’s exactly how it avoids duplicates like `[1,2]` coming from the first `2` and from the second `2` at the same level.
*/

class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), ans);

        return ans;
    }

    private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> ans){
        ans.add(new ArrayList<>(path));

        for(int i=start; i<nums.length; i++){

            // skip duplicates at the same recursion level
            if(i > start && nums[i] == nums[i-1]){
                continue;
            }

            path.add(nums[i]);
            backtrack(nums, i+1, path, ans);
            path.remove(path.size() - 1);
        }

        return;
    }
}