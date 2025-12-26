// Method 1: Two Pointer approach with sorting
/*
# WHAT I WAS DOING WRONG:


## 2) When you find a valid triplet, you never move pointers

You do:

```java
if(nums[left] + nums[right] == target){
   ans.add(...)
}
```

…but you don’t do `left++` / `right--` afterwards.
So if `left` and `right` stay the same, the condition will still be true next iteration → you’ll add the same triplet repeatedly → **infinite loop**.

After adding a triplet, you must move both pointers:

* `left++`
* `right--`
  (and usually also skip duplicates, below)

## 3) No duplicate handling → repeated triplets

Even with pointer movement fixed, you’ll output duplicates because:

* You don’t skip duplicate `i` values (`a`)
* You don’t skip duplicate `left/right` values after finding a match

Classic fixes:

* At the top of the `for` loop: `if (i > 0 && nums[i] == nums[i-1]) continue;`
* After finding a match and moving pointers, skip duplicates while `nums[left] == nums[left-1]`, and similarly for `right`.

## 4) Minor loop bound

You used `for (i=0; i<n-1; i++)`, but you really need room for **two** numbers after `i`, so the usual bound is:

* `i < n - 2`
  Not a big deal for correctness if the while loop handles it, but it’s the standard.

---

### Summary of what to change (high level)

* After recording a triplet, move pointers (and skip duplicates)
* Skip duplicate anchors (`i`) to avoid duplicate triplets

#############################

## High-level idea

We want all **unique triplets** `(a, b, c)` such that:

[
a + b + c = 0
]

If we **sort** the array, we can:

1. Fix one number `a = nums[i]` (the “anchor”).
2. Reduce the problem to **Two Sum on a sorted array**: find pairs `(b, c)` in the range `i+1..n-1` such that:
   [
   b + c = -a
   ]
3. Use **two pointers** (`left`, `right`) to find those pairs in linear time for each anchor.

Total time:

* Sorting: `O(n log n)`
* For each `i`, two-pointer scan: `O(n)`
* Overall: `O(n^2)`

---

## Why two pointers work (after sorting)

After sorting, if you look at the current sum:

`sum = nums[left] + nums[right]`

* If `sum` is **too small** (less than target), you need a **bigger sum**, so move `left++` (increase the smaller number).
* If `sum` is **too large** (greater than target), you need a **smaller sum**, so move `right--` (decrease the larger number).
* If it matches, record the triplet and move both pointers to look for the next pair.

This is exactly the same monotonic logic as Two Sum II.

---

## Duplicate handling (the subtle part)

We must return **unique triplets**, meaning we can’t produce the same `(a,b,c)` multiple times.

There are two duplicate sources:

### A) Duplicate anchors (`a`)

If `nums[i] == nums[i-1]`, then using it as an anchor will generate the same triplets you already found with the previous `i`.

So we skip:

```java
if (i > 0 && nums[i] == nums[i-1]) continue;
```

### B) Duplicate pairs (`b` and `c`) for the same anchor

Suppose `nums[left]` repeats (e.g., multiple `0`s). Even after finding a valid triplet, if we don’t skip duplicates, we might record the same triplet again.

After we find a valid triplet:

1. do `left++` and `right--`
2. skip duplicates:

   * while `nums[left] == nums[left-1]`, move `left++`
   * while `nums[right] == nums[right+1]`, move `right--`

That ensures every distinct `(b,c)` pair is used once per anchor.

# Thorough example walkthrough

Use the classic example:

```
nums = [-1, 0, 1, 2, -1, -4]
```

### Step 1: Sort

Sorted nums:

```
[-4, -1, -1, 0, 1, 2]
 index: 0   1   2  3  4  5
```

We will iterate `i` from `0` to `n-3`.

---

## i = 0

* `a = nums[0] = -4`
* `target = -a = 4`
* `left = 1`, `right = 5`

Now we need `nums[left] + nums[right] = 4`.

### left=1 (-1), right=5 (2)

sum = -1 + 2 = 1 < 4 → too small → `left++`

### left=2 (-1), right=5 (2)

sum = -1 + 2 = 1 < 4 → `left++`

### left=3 (0), right=5 (2)

sum = 0 + 2 = 2 < 4 → `left++`

### left=4 (1), right=5 (2)

sum = 1 + 2 = 3 < 4 → `left++`

Now left=5, not `< right`, stop.
No triplets with anchor -4.

---

## i = 1

* `a = nums[1] = -1`
* Check duplicate anchor: `nums[1] != nums[0]` → ok
* `target = 1`
* `left = 2`, `right = 5`

Need `nums[left] + nums[right] = 1`.

### left=2 (-1), right=5 (2)

sum = -1 + 2 = 1 ✅ match!

Triplet: `[-1, -1, 2]` add it.

Move pointers:

* `left++` → 3
* `right--` → 4

Skip duplicates:

* check left duplicate: `nums[3]=0` vs `nums[2]=-1` not same → no skip
* check right duplicate: `nums[4]=1` vs `nums[5]=2` not same → no skip

### left=3 (0), right=4 (1)

sum = 0 + 1 = 1 ✅ match!

Triplet: `[-1, 0, 1]` add it.

Move pointers:

* left=4
* right=3
  Now left >= right → stop.

So far answer contains:

* `[-1, -1, 2]`
* `[-1, 0, 1]`

---

## i = 2

* `a = nums[2] = -1`
* Duplicate anchor? `nums[2] == nums[1]` → YES → `continue;`

This prevents generating the same two triplets again.

---

## i = 3

* `a = nums[3] = 0`
* `target = 0`
* `left = 4`, `right = 5`

Need `nums[left] + nums[right] = 0`.

### left=4 (1), right=5 (2)

sum = 3 > 0 → too large → `right--` → 4

Now left == right stop. No triplet with anchor 0.

Done.

Final answer:

```
[[-1, -1, 2], [-1, 0, 1]]
```

---

# Duplicate-skipping example (why those inner while loops matter)

Consider:

```
nums = [-2, 0, 0, 2, 2]
```

Sorted is the same.

### i=0, a=-2, target=2

left=1 (0), right=4 (2) → sum=2 ✅ triplet [-2,0,2]
Move left=2, right=3

Now if you *don’t* skip duplicates:
left=2 (0), right=3 (2) → sum=2 ✅ you’d add [-2,0,2] AGAIN (duplicate)

With skipping:

* after left++ you check `nums[left] == nums[left-1]` → 0 == 0 → left++ (skip repeated 0)
* after right-- you check `nums[right] == nums[right+1]` → 2 == 2 → right-- (skip repeated 2)

So you only record it once.
*/
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {

        List<List<Integer>> ans = new ArrayList<>();
        int n = nums.length;
        Arrays.sort(nums);

        for(int i=0; i<n-2; i++){
            if(i > 0 && nums[i] == nums[i-1]){
                continue;
            }

            int a = nums[i];
            int target = 0 - a;

            int left = i + 1;
            int right = n - 1;


            while(left < right){
                if(nums[left] + nums[right] == target){
                    ans.add(Arrays.asList(a, nums[left], nums[right]));

                    left++;
                    right--;

                    while(left < right && nums[left] == nums[left-1]){
                        left++;
                    }
                    
                    while(left < right && nums[left] == nums[right+1]){
                        right--;
                    }

                }else if(nums[left] + nums[right] < target){
                    left++;
                }else{
                    right--;
                }
            }

        }

        return ans;
    }
}





// Method 2: Similar approach without sorting
/*
## Core idea (3Sum → repeated 2Sum with a HashSet)

We want `a + b + c = 0`.

If we fix `a = nums[i]`, then we need to find pairs `(b, c)` in the rest of the array such that:

[
b + c = -a
]

That’s a classic **Two Sum** problem. Without sorting, Two Sum is naturally done using a **HashSet**.

### Inner-loop logic (Two Sum with a set)

For fixed `a`:

* walk `j` from `i+1` to end
* let `b = nums[j]`
* you want `c = -a - b`
* if you’ve already seen `c` earlier in this scan, then you found a triplet `(a, b, c)`

---

## The hard part: avoiding duplicate triplets (without sorting)

When the array isn’t sorted, the same triplet can appear in many different orders and from different indices.

Example: `[-1, 0, 1]` could be discovered as:

* `(-1, 0, 1)`
* `(-1, 1, 0)`
* `(0, -1, 1)` etc.

So we need a **canonical representation** of a triplet.

### Trick: Canonicalize each found triplet by ordering just the 3 values

Even though we don’t sort the whole array, we can **sort the three numbers** (constant time) to get `(min, mid, max)` and use it as a unique key.

Then store that key in a global `HashSet` so we only add each unique triplet once.

### Why `seen.add(b)` happens after the check

This ensures the `c` we match was from an earlier index in this scan, i.e., we’re using **two distinct elements** for `b` and `c`.

---

## Thorough example walkthrough

Use the classic input:

```
nums = [-1, 0, 1, 2, -1, -4]
```

We do NOT sort it.

### Global sets initially

* `triplets = {}` (stores canonical keys)
* `usedA = {}`

---

## i = 0 → a = -1

`usedA.add(-1)` succeeds → proceed.

`seen = {}`

Now iterate `j`:

### j = 1 → b = 0

* need `c = -a - b = 1 - 0 = 1`
* `seen.contains(1)`? No
* add `b` → `seen = {0}`

### j = 2 → b = 1

* need `c = 1 - 1 = 0`
* `seen.contains(0)`? Yes ✅ (0 is in seen)
* found triplet: `(-1, 1, 0)` which sums to 0

Canonicalize:

* values: -1, 1, 0 → `(min=-1, mid=0, max=1)`
* key = `"-1#0#1"`
* `triplets.add(key)` succeeds → add `[-1,0,1]` to answer

Then add `b`:

* `seen = {0, 1}`

### j = 3 → b = 2

* need `c = 1 - 2 = -1`
* `seen.contains(-1)`? No
* add `2` → `seen = {0,1,2}`

### j = 4 → b = -1

* need `c = 1 - (-1) = 2`
* `seen.contains(2)`? Yes ✅
* found triplet: `(-1, -1, 2)`

Canonicalize:

* (-1, -1, 2) → `(min=-1, mid=-1, max=2)`
* key = `"-1#-1#2"`
* add succeeds → answer adds `[-1,-1,2]`

add `b`:

* `seen = {0,1,2,-1}`

### j = 5 → b = -4

* need `c = 1 - (-4) = 5`
* `seen.contains(5)`? No
* add `-4`

Done with i=0.
Answer currently:

* `[-1,0,1]`
* `[-1,-1,2]`

---

## i = 1 → a = 0

`usedA.add(0)` succeeds.

`seen = {}`

* j=2 b=1 → c = -0 - 1 = -1, not in seen; add 1
* j=3 b=2 → c=-2, not; add 2
* j=4 b=-1 → c=1, seen contains 1 ✅ → triplet (0, -1, 1)
  Canonicalize → (-1,0,1) → key `-1#0#1`
  But that key is already in `triplets`, so we do **not** add duplicate.
* rest won’t create new ones.

---

## i = 4 → a = -1 (again)

`usedA.add(-1)` fails (already processed anchor -1) → skip whole i.
This helps avoid extra work and duplicate discoveries.

Final answer remains:

* `[-1,0,1]`
* `[-1,-1,2]`

---

## Complexity

* Time: **O(n²)** average (each anchor does an O(n) scan with O(1) set ops)
* Space: **O(n)** for the `seen` set + result dedup set

---

## Notes: Why sorting is still “standard”

The sorted + two-pointer method is also O(n²), but:

* less hashing overhead
* simpler duplicate logic
* often faster in practice

But if you specifically want **no array sorting**, the hashing approach above is the clean go-to.
*/

// class Solution {
//     public List<List<Integer>> threeSum(int[] nums) {
//         List<List<Integer>> ans = new ArrayList<>();
//         Set<String> triplets = new HashSet<>(); // global dedup
//         Set<Integer> usedA = new HashSet<>();   // optional: avoid repeating same anchor value

//         int n = nums.length;

//         for (int i = 0; i < n; i++) {
//             int a = nums[i];

//             // If we've already processed this anchor value, skip (helps reduce duplicates/work).
//             if (!usedA.add(a)) continue;

//             Set<Integer> seen = new HashSet<>(); // values we've seen for this fixed 'a'

//             for (int j = i + 1; j < n; j++) {
//                 int b = nums[j];
//                 int c = -a - b;

//                 // If c was seen earlier in this i-loop, we found a + b + c = 0
//                 if (seen.contains(c)) {
//                     int x = a, y = b, z = c;

//                     // Canonicalize (x,y,z) -> (min, mid, max) in O(1)
//                     int min = Math.min(x, Math.min(y, z));
//                     int max = Math.max(x, Math.max(y, z));
//                     int mid = x + y + z - min - max;

//                     String key = min + "#" + mid + "#" + max;
//                     if (triplets.add(key)) {
//                         ans.add(Arrays.asList(min, mid, max));
//                     }
//                 }

//                 // Add b to seen after checking, so we don't reuse the same index j as c
//                 seen.add(b);
//             }
//         }

//         return ans;
//     }
// }
