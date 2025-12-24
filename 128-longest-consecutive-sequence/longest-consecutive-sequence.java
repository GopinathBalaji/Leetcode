// Method 1: Using Set
/*
### Big hint (the key insight for **O(n)**)

Use a **HashSet** so you can check “does x exist?” in **O(1)** average time. Then, **only start counting a consecutive run at numbers that are the beginning of a streak**.

A number `x` is the **start** of a streak if **`x - 1` is not in the set**.
That single rule prevents you from recounting the same streak over and over.

---

## Step-by-step hints (HashSet approach)

1. Put every number into a `HashSet<Integer> set`.
2. Keep an `int best = 0`.
3. For each number `x` in the set (or in the original array—either works):

   * If `set` **contains `x - 1`**, then `x` is **not** the start of a streak → skip it.
   * Otherwise `x` is the start:

     * Set `len = 1`
     * While `set` contains `x + len`, keep incrementing `len`
     * Update `best = max(best, len)`
4. Return `best`.

Why this is O(n) overall:

* Each number is only “walked forward” **once** as part of a streak starting point.
* Non-start numbers are skipped fast.

---

## Mini walkthrough (what your loop should “feel” like)

Example: `[100, 4, 200, 1, 3, 2]`
Set = `{100, 4, 200, 1, 3, 2}`

* Check `100`: `99` not in set → start streak: `100` length = 1
* Check `4`: `3` *is* in set → not a start → skip
* Check `200`: `199` not in set → start streak: `200` length = 1
* Check `1`: `0` not in set → start streak:

  * `1` yes, `2` yes, `3` yes, `4` yes → length = 4
    So answer is 4.

---

## Common pitfalls (things to watch)

* **Duplicates**: the set automatically removes them (good). But be careful if you iterate the original array: duplicates cause repeated checks (still okay, just a bit extra).
* **Negative numbers**: no special handling needed; the logic works the same.
* **Empty input**: return `0`.
* Don’t sort for the optimal solution: sorting gives **O(n log n)**, while the set trick gives **O(n)** average.
*/
class Solution {
    public int longestConsecutive(int[] nums) {
        if(nums.length == 0){
            return 0;
        }

        Set<Integer> set = new HashSet<>();
        int seqLen = 0;
        int maxLen = 0;

        for(int num: nums){
            set.add(num);
        }

        for(int num: set){
            if(set.contains(num - 1)){
                continue;
            }

            seqLen = 1;
            while(set.contains(num + seqLen)){
                seqLen++;
            }

            maxLen = Math.max(maxLen, seqLen);
        }

        return maxLen;
    }
}






// Method 2: Union-Find
/*
## Key idea

Treat each **distinct value** as a node.
If two values are consecutive (e.g., `x` and `x+1`), connect them (union them).
Then the answer is the **size of the largest connected component**.

Why this works:

* A consecutive sequence like `1,2,3,4` forms a chain of neighbor links.
* DSU groups connected nodes and tracks component sizes efficiently.

---

## Steps

1. **Deduplicate** values (duplicates don’t change sequences).
2. Assign each unique value an **id**: `value -> index`.
3. For each value `x`:

   * if `x-1` exists, `union(x, x-1)`
   * if `x+1` exists, `union(x, x+1)`
     (Doing only one direction like `x+1` is enough; both is fine but redundant.)
4. Track the maximum DSU component size.

Time: ~**O(n α(n))** (basically linear)
Space: **O(n)**

### Why we only union `x` with `x+1`

If `1,2,3,4` are all present:

* union(1,2), union(2,3), union(3,4) connects the whole chain.
  Unioning both directions would do extra work without changing the result.

---

## Thorough example walkthrough

Take:

```
nums = [100, 4, 200, 1, 3, 2, 2]
```

### Step 1: Deduplicate + assign ids

Unique values are `{100, 4, 200, 1, 3, 2}`.

Suppose the map assigns:

* 100 → 0
* 4   → 1
* 200 → 2
* 1   → 3
* 3   → 4
* 2   → 5

DSU starts with each node alone:

* Components: {100}, {4}, {200}, {1}, {3}, {2}
* Sizes: all 1

### Step 2: Union consecutive neighbors (x with x+1)

We scan each `x` in the set:

#### x = 100

* check 101: not present → no union

#### x = 4

* check 5: not present → no union

#### x = 200

* check 201: not present → no union

#### x = 1

* check 2: present
  union(id(1), id(2)) = union(3, 5)

  * now component {1,2} has size 2
  * best = 2

#### x = 3

* check 4: present
  union(id(3), id(4)) = union(4, 1)

  * now component {3,4} has size 2
  * best still 2

#### x = 2

* check 3: present
  union(id(2), id(3)) = union(5, 4)

But note:

* id(2)=5 is already connected to id(1)=3 in component {1,2}
* id(3)=4 is connected to id(4)=1 in component {3,4}

Union merges these two components:

* {1,2} ∪ {3,4} → {1,2,3,4}
* new size = 4
* best = 4

Final largest component size is **4**, matching the longest consecutive sequence `[1,2,3,4]`.

### What about the duplicate `2` in the original array?

It’s ignored because we only store each unique value once in the map. Duplicates don’t create longer consecutive sequences.

---

## When Union-Find is nice vs HashSet “start-of-streak”

* HashSet start-of-streak is simpler and usually preferred.
* Union-Find is useful if you like modeling the problem as connectivity / components, or if you’re extending the problem (e.g., dynamic insertions).
*/

// class Solution {
//     static class DSU {
//         int[] parent;
//         int[] size;

//         DSU(int n) {
//             parent = new int[n];
//             size = new int[n];
//             for (int i = 0; i < n; i++) {
//                 parent[i] = i;
//                 size[i] = 1;
//             }
//         }

//         int find(int x) {
//             if (parent[x] != x) parent[x] = find(parent[x]); // path compression
//             return parent[x];
//         }

//         int union(int a, int b) {
//             int ra = find(a), rb = find(b);
//             if (ra == rb) return size[ra];

//             // union by size
//             if (size[ra] < size[rb]) {
//                 int tmp = ra; ra = rb; rb = tmp;
//             }
//             parent[rb] = ra;
//             size[ra] += size[rb];
//             return size[ra];
//         }

//         int compSize(int x) {
//             return size[find(x)];
//         }
//     }

//     public int longestConsecutive(int[] nums) {
//         if (nums.length == 0) return 0;

//         // 1) Map each unique value -> DSU id
//         Map<Integer, Integer> id = new HashMap<>();
//         int nextId = 0;
//         for (int x : nums) {
//             if (!id.containsKey(x)) {
//                 id.put(x, nextId++);
//             }
//         }

//         // 2) DSU over unique values
//         DSU dsu = new DSU(nextId);
//         int best = 1;

//         // 3) Union neighbors
//         // Only union x with x+1 (one direction is sufficient)
//         for (int x : id.keySet()) {
//             Integer idx = id.get(x);
//             Integer jdx = id.get(x + 1); // neighbor
//             if (jdx != null) {
//                 best = Math.max(best, dsu.union(idx, jdx));
//             }
//         }

//         return best;
//     }
// }
