// Method 1: My O(n²) Greedy approach 
/*
### Hint 1 — Understand the merge

Merging is **component-wise max**:
`merge([a1,b1,c1], [a2,b2,c2]) = [max(a1,a2), max(b1,b2), max(c1,c2)]`.
So any sequence of merges is just taking the **max of each position** over some subset of triplets.

---

### Hint 2 — Immediate filter

If a triplet has any coordinate **greater** than the target’s corresponding coordinate, you can **never** use it: merging it would overshoot that target coordinate and you can’t bring it back down.
So first mentally discard all triplets where
`triplet[i] > target[i]` for any `i ∈ {0,1,2}`.

---

### Hint 3 — What do you really need?

After filtering, ask: can you get each coordinate of the target from **some** triplet?
You want:

* some triplet with `x == target[0]` (and all coords ≤ target),
* some triplet with `y == target[1]`,
* some triplet with `z == target[2]`.

If all three exist (not necessarily the same triplet), then by merging those few triplets you’ll hit the target exactly.

---

### Hint 4 — Why this is sufficient

Because you already filtered out overshoots, taking maxes across any combination can only move coordinates **up to** the target, never beyond. If you can supply each target coordinate from at least one triplet, the component-wise max across those will be exactly the target.

---

### Hint 5 — Tiny mental checklist

As you scan the triplets:

1. Skip if it overshoots any coordinate.
2. Otherwise, set flags:

   * `hitA |= (a == target[0])`
   * `hitB |= (b == target[1])`
   * `hitC |= (c == target[2])`
3. If `hitA && hitB && hitC` → you can return true.

---

### Hint 6 — Walkthroughs

**Example A (true):**
`triplets = [[2,5,3],[1,8,4],[1,7,5]]`, `target = [2,7,5]`

* Filter:

  * `[2,5,3]` OK (all ≤ target)
  * `[1,8,4]` ❌ (8 > 7)
  * `[1,7,5]` OK
* Flags from remaining:

  * `[2,5,3]` gives `hitA` (2==2)
  * `[1,7,5]` gives `hitB` (7==7) and `hitC` (5==5)
* All three true → merge of those is `[2,7,5]`.

**Example B (false):**
`triplets = [[3,4,5],[4,5,6]]`, `target = [3,2,5]`

* Both triplets overshoot the middle coord (4,5 > 2) → everything is discarded.
* No way to form target → false.

**Example C (edge):**
`triplets = [[2,1,3],[1,2,3],[2,2,2]]`, `target=[2,2,3]`

* All are ≤ target → keep all.
* Hits: `[2,1,3]` → `hitA` & `hitC`; `[1,2,3]` → `hitB` & `hitC`; `[2,2,2]` → `hitA` & `hitB`.
* Eventually `hitA && hitB && hitC` → true.
*/

class Solution {
    public boolean mergeTriplets(int[][] triplets, int[] target) {
        int val1 = 0;
        int val2 = 0;
        int val3 = 0;

        for(int i=0; i<triplets.length; i++){
            if(triplets[i][0] != target[0] && triplets[i][1] != target[1] && triplets[i][2] != target[2]){
                continue;
            }
            if(triplets[i][0] > target[0] || triplets[i][1] > target[1] || triplets[i][2] > target[2]){
                continue;
            }

            val1 = Math.max(val1, triplets[i][0]);
            val2 = Math.max(val2, triplets[i][1]);
            val3 = Math.max(val3, triplets[i][2]);

            for(int j=i+1; j<triplets.length; j++){
                if(triplets[j][0] != target[0] && triplets[j][1] != target[1] && triplets[j][2] != target[2]){
                    continue;
                }
                if(triplets[j][0] > target[0] || triplets[j][1] > target[1] || triplets[j][2] > target[2]){
                    continue;
                }

                val1 = Math.max(val1, triplets[j][0]);
                val2 = Math.max(val2, triplets[j][1]);
                val3 = Math.max(val3, triplets[j][2]);
            }
        }

        return val1 == target[0] && val2 == target[1] && val3 == target[2];
    }
}





// Method 2: Better O(n) Greedy solution
/*
## Clean O(n) way (what the greedy reduces to)

* First, discard any triplet with any coordinate `> target[coord]` (can’t use it; merge is max-only).
* Among the remaining triplets, check if you can “hit” each target coordinate exactly at least once.


### Why this works

After removing overshoots, merging (componentwise max) can only move coordinates **up to** the target, never beyond. If you can supply each coordinate from *some* acceptable triplet, then merging those (any order) yields exactly the target.

---

## Tiny walkthrough

`triplets = [[2,5,3],[1,8,4],[1,7,5]]`, `target = [2,7,5]`

* Discard overshoots: `[1,8,4]` goes away (8>7).
* Remaining:

  * `[2,5,3]` → hits A (2)
  * `[1,7,5]` → hits B (7) and C (5)
* All three flags true ⇒ possible.
*/

// class Solution {
//     public boolean mergeTriplets(int[][] triplets, int[] target) {
//         boolean hitA = false, hitB = false, hitC = false;

//         for (int[] t : triplets) {
//             // skip overshoots
//             if (t[0] > target[0] || t[1] > target[1] || t[2] > target[2]) continue;

//             if (t[0] == target[0]) hitA = true;
//             if (t[1] == target[1]) hitB = true;
//             if (t[2] == target[2]) hitC = true;

//             if (hitA && hitB && hitC) return true;
//         }
//         return false;
//     }
// }
