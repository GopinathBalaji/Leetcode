// Two Pass Greedy Approach
/*
# Idea (why two passes?)

You have two neighbor constraints:

* If `rating[i] > rating[i-1]` then `candies[i] > candies[i-1]` (pressure from the **left**).
* If `rating[i] > rating[i+1]` then `candies[i] > candies[i+1]` (pressure from the **right**).

Minimal candies at each child is simply the **stronger of the two pressures**. So:

1. Left→Right pass: give each child the least candies to satisfy the **left** constraint.
2. Right→Left pass: adjust using the **right** constraint by taking `max(current, neededFromRight)`.

Everyone starts at 1 candy.

## Why this is minimal

* The left pass gives the **least** candies that keep increases valid from the left.
* The right pass only raises a child’s candies **if needed** to meet the right constraint.
* Taking the **max** at each index satisfies both sides with no extra giveaways.

---

# Walkthroughs

## 1) Peak in the middle: `[1, 2, 3, 2, 1]`

* Init `candies = [1,1,1,1,1]`

Left→Right (enforce rising from the left):

* i=1: 2>1 → `[1,2,1,1,1]`
* i=2: 3>2 → `[1,2,3,1,1]`
* i=3: 2>3 no
* i=4: 1>2 no
  Result after pass1: `[1,2,3,1,1]`

Right→Left (enforce rising from the right on descents):

* i=3: 2>1 → need ≥ `candies[4]+1 = 2` → `[1,2,3,2,1]`
* i=2: 3>2 → need ≥ `candies[3]+1 = 3` → already 3
* i=1: 2>3 no
* i=0: 1>2 no
  Final: `[1,2,3,2,1]`, sum = **9**.

## 2) Valley/plateau mix: `[1, 0, 2]`

* Init: `[1,1,1]`

Left→Right:

* i=1: 0>1 no
* i=2: 2>0 → `[1,1,2]`

Right→Left:

* i=1: 0>2 no
* i=0: 1>0 → need ≥ `candies[1]+1 = 2` → `[2,1,2]`
  Sum = **5**.

## 3) Equal neighbors: `[1, 2, 2]`

* Init: `[1,1,1]`

Left→Right:

* i=1: 2>1 → `[1,2,1]`
* i=2: 2>2 no

Right→Left:

* i=1: 2>2 no
* i=0: 1>2 no
  Final: `[1,2,1]`, sum = **4**.

**Note on equals:** if `ratings[i] == ratings[i-1]` or `ratings[i] == ratings[i+1]`, there’s **no** constraint between those two kids, so we don’t force an increase across equals. This is why equals act as “breaks” between slopes.

---

# Complexity

* **Time:** O(n) — one linear pass each way.
* **Space:** O(n) extra for the `candies` array (you can also do an O(1)-extra “slope” method, but the two-pass approach is simpler and commonly accepted).
*/
class Solution {
    public int candy(int[] ratings) {
        int n = ratings.length;
        if (n == 0) return 0;

        int[] candies = new int[n];
        Arrays.fill(candies, 1);        // each kid gets at least 1

        // Pass 1: left -> right (handle strictly increasing runs)
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                candies[i] = candies[i - 1] + 1;
            }
        }

        // Pass 2: right -> left (handle strictly decreasing runs)
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                candies[i] = Math.max(candies[i], candies[i + 1] + 1);
            }
        }

        int total = 0;
        for (int c : candies) total += c;
        return total;
    }
}



// Greedy One pass solution
/*
NOTE: In the following logic, we are tracking the total number of candies and not the actual
number of candies each person receives. (Look at video on YouTube for visualizations)



# \U0001f9e0 Intuition (one pass, no arrays)

Think of the ratings as a sequence of **slopes** between neighbors:

* **Up step** at `i` if `ratings[i] > ratings[i-1]`
* **Down step** if `ratings[i] < ratings[i-1]`
* **Flat** if `ratings[i] == ratings[i-1]`

We only need to track **runs** of consecutive up steps and down steps:

* Let `up` = length of the current **increasing** run (number of `↑` steps so far).
* Let `down` = length of the current **decreasing** run (number of `↓` steps so far).
* Let `peak` = the length (`up`) at the **last local peak** (where an up-run ended before a down-run started).

Candies along:

* an **up run** of length `u` must look like `1, 2, ..., u+1`.
* a **down run** of length `d` must look like `d+1, d, ..., 1` (when read from the peak downward).
* The **peak** must be at least `max(u, d) + 1` to be strictly larger than both sides.

We’ll walk the array once and keep a running `sum` of candies without storing a candies array.

Key idea for the **down run**:

* Each time we extend the down run by 1 (so `down++`), we add `down` to the total (this builds the triangular number `1+2+…+down`).
* If the down run ever becomes **longer** than the previous up run (`down > peak`), we need to **bump the peak by +1** (so we add 1 to `sum` once per “excess” step).

We also always count **at least 1 candy per child**:

* First child starts `sum = 1`.
* On an **up** step, the current child gets `1 + up` candies → add `1 + up` to `sum`.
* On a **flat** step, reset everything and add `1`.
* On a **down** step, the current child’s minimal candies are handled by the down-run accumulation (we add `down`, and possibly `+1` if `down > peak`).

### Why this works

* Each **up step** adds exactly what the current child needs above 1: `+up`.
* Each **down step** adds the next number in the descending triangular sequence: `+down`.
* If the **down** run exceeds the previous **up** run, the **peak** (which was set by that up run) must be raised to keep the whole descent strictly decreasing while staying ≥ 1. We account for that with `if (down > peak) sum += 1;`.
* **Flat** breaks constraints; neighbors can both be 1, so we reset.

**Complexity:** O(n) time, O(1) extra space.

---

# \U0001f50e Walkthroughs

## 1) Perfect mountain: `[1, 2, 3, 2, 1]`

Ratings steps: `↑, ↑, ↓, ↓`

* Start: `sum=1, up=0, down=0, peak=0`
* i=1 (2>1, ↑): `up=1, peak=1, down=0, sum += 1+1=2` → `sum=3`
* i=2 (3>2, ↑): `up=2, peak=2, down=0, sum += 1+2=3` → `sum=6`
* i=3 (2<3, ↓): `up=0, down=1, sum += 1` (down) → `sum=7`, `down(1) > peak(2)?` no
* i=4 (1<2, ↓): `down=2, sum += 2` → `sum=9`, `down(2) > peak(2)?` no
  Result `sum=9` → minimal candies `[1,2,3,2,1]`.

---

## 2) Flat included: `[1, 2, 2]`

Ratings steps: `↑, =`

* Start: `sum=1`
* i=1 (2>1, ↑): `up=1, peak=1, down=0, sum += 2` → `sum=3`
* i=2 (2=2, =): reset `up=down=peak=0`, `sum += 1` → `sum=4`
  Result `4` → minimal candies `[1,2,1]`.

---

## 3) Valley then peak: `[1, 0, 2]`

Ratings steps: `↓, ↑`

* Start: `sum=1`
* i=1 (0<1, ↓): `down=1, sum += 1` → `sum=2`; `down(1) > peak(0)` → `sum += 1` → `sum=3`
* i=2 (2>0, ↑): `up=1, peak=1, down=0, sum += 2` → `sum=5`
  Result `5` → minimal candies `[2,1,2]`.

---

## 4) Strictly decreasing: `[4, 3, 2, 1]`

Ratings steps: `↓, ↓, ↓`

* Start: `sum=1, peak=0`
* i=1: `down=1, sum+=1→2`, `down>peak` → `+1` → `sum=3`
* i=2: `down=2, sum+=2→5`, `down>peak` → `+1` → `sum=6`
* i=3: `down=3, sum+=3→9`, `down>peak` → `+1` → `sum=10`
  Result `10` → minimal candies `[4,3,2,1]`.

---

# \U0001f9e9 Why the peak adjustment is exactly `+1` per excess

If the last up-run length was `peak`, the peak originally had `peak+1` candies.
A down-run of length `down` requires the peak to be at least `down+1`.

* If `down ≤ peak`, we’re fine; no change.
* Each time `down` increments **past** `peak` (i.e., `down = peak+1, peak+2, ...`), the peak must increase by **exactly 1** to remain strictly above its right neighbor chain. We account for that by adding `+1` each time `down > peak`.
*/
// class Solution {
//     public int candy(int[] ratings) {
//         int n = ratings.length;
//         if (n == 0) return 0;

//         int sum = 1;      // candies total; first child gets 1
//         int up = 0;       // length of current strictly increasing run (in steps)
//         int down = 0;     // length of current strictly decreasing run (in steps)
//         int peak = 0;     // up-run length at the last peak

//         for (int i = 1; i < n; i++) {
//             if (ratings[i] > ratings[i - 1]) {
//                 // we are climbing
//                 up++;
//                 peak = up;       // update peak length at this new top
//                 down = 0;        // any down-run is broken
//                 sum += 1 + up;   // current kid gets 1 + up candies
//             } else if (ratings[i] == ratings[i - 1]) {
//                 // flat resets everything
//                 up = down = peak = 0;
//                 sum += 1;        // just 1 candy for this kid
//             } else {
//                 // we are descending
//                 up = 0;          // any up-run is broken
//                 down++;
//                 sum += down;     // add triangular contribution for the down-run
//                 if (down > peak) {
//                     // peak must be higher to strictly decrease all the way
//                     sum += 1;    // bump peak by +1 (once per excess step)
//                 }
//             }
//         }
//         return sum;
//     }
// }