// Check each point with all further points. Time: O(n²), Spce: O(n)
/*
We want the **maximum number of collinear points**. A reliable way is:

1. For each point `A` (the **anchor**), count how many other points share the **same slope** with `A`.
2. The largest count (plus duplicates + the anchor itself) is the best line through `A`.
3. The global maximum over all anchors is the answer.

Everything below supports making step (1) correct and efficient.

---

# Detailed steps — what & why

## 1) Loop over anchors `i`

**What:** For each index `i`, treat `points[i]` as anchor `A`.

**Why:** Any line that’s part of the final answer passes through *some* point; we’ll see its full multiplicity when that point is the anchor. This yields an **O(n²)** algorithm, which is the accepted optimal time for this problem.

---

## 2) Create a map `slope → count` for the anchor

**What:** New hash map per anchor: counts how many times each **slope** occurs relative to `A`.

**Why:** Points `B, C, D` are collinear with `A` iff their slopes w.r.t. `A` are the same. Counting slopes groups collinear points.

---

## 3) Track `duplicates`

**What:** If `B` exactly equals `A` (same `x` and `y`), increment `duplicates` instead of inserting a slope.

**Why:** Duplicates have **no defined slope** (0/0) and must be added to *every* line through `A`. At the end for anchor `A`, total points on the best line are:

```
bestSlopeCount + duplicates + 1       // +1 for A itself
```

---

## 4) Compute raw deltas

**What:** For each other point `B`, compute `dy = yB − yA`, `dx = xB − xA` as **64-bit** (long) if coordinates can be large.

**Why:** Slope = `dy/dx`. Deltas may briefly overflow 32-bit if coordinates are big (e.g., ±1e9). Using wider ints avoids UB before reduction.

---

## 5) **Reduce** the slope using `g = gcd(|dy|, |dx|)`

**What:** Replace `(dy, dx)` with `(dy/g, dx/g)`.

**Why:** Many different raw pairs represent the **same slope**: `(2,4)`, `(3,6)`, `(1,2)`. If you don’t reduce, counts split across keys → **undercounting**. Reduction collapses proportional pairs into **one canonical key**.

> Edge cases:
>
> * If `dx == 0` (vertical), you **don’t** need gcd for correctness (you’ll special-case), but computing it is harmless if you do.

---

## 6) **Normalize** the sign and special cases

**What:** Force one consistent representation:

* **Vertical lines**: `dx == 0` → canonical key `(1, 0)`.
  (All verticals share this single key.)
* **Horizontal lines**: `dy == 0` → canonical key `(0, 1)`.
  (All horizontals share this single key.)
* **General case**:

  * Reduce by gcd.
  * Enforce **`dx > 0`** (if `dx < 0`, multiply both by −1).
  * (If you prefer, you can enforce `dx ≥ 0` and for `dx == 0` force `dy > 0`. Consistency is what matters.)

**Why:**

* `(1,2)`, `(-1,-2)` are the **same direction**; without a sign rule they hash differently.
* Vertical lines would otherwise appear as `(k, 0)` for many `k`’s (and both signs), splitting counts.
* Horizontal lines would appear as `(0, k)` likewise. Canonical forms collapse each family to one bucket.

---

## 7) Update counts & track `best`

**What:** `count[slope]++`, and keep `best = max(best, count[slope])`.

**Why:** We want the largest group of points sharing a slope with `A`.

---

## 8) Finalize the anchor’s best line

**What:** The total through anchor `A` is:

```
best + duplicates + 1
```

**Why:**

* `best` counts **other** points with the most common slope.
* `duplicates` are exact copies of `A` and belong to every line through `A`.
* `+1` includes the anchor `A` itself.

Update `globalBest = max(globalBest, best + duplicates + 1)`.

---

## 9) Complexity & correctness notes

* **Time:** `O(n²)` comparisons, `O(1)` amortized map ops → overall `O(n²)`.
* **Space:** `O(n)` per anchor for the map (reused each outer loop).
* **Correctness:** Reduction + normalization ensure each geometric line direction maps to **exactly one** key per anchor. Duplicates are handled once per anchor.

---

# Thorough example walkthrough

Let’s use this set (mixing diagonals, vertical/horizontal lines, and duplicates):

```
P0 = (0, 0)
P1 = (1, 1)
P2 = (2, 2)
P3 = (3, 3)
P4 = (2, 0)   // horizontal with P0
P5 = (2, 5)   // vertical with P4
P6 = (1, 1)   // duplicate of P1
```

We expect two strong lines:

* Diagonal y = x through P0..P3 (4 points), plus duplicate P6 when anchoring at P1 → we should get 5 with anchor at (1,1).
* Vertical x = 2 through P2,P4,P5 (3 points).
  The maximum should be **5**.

We’ll show two anchors in detail: `A = P1 (1,1)` and `A = P4 (2,0)`.

---

## Anchor A = P1 = (1,1)

Initialize: `map = {}`, `duplicates = 0`, `best = 0`.

Process each `B` with `j > i` (or all `j != i`—either is fine):

1. `B = P0 (0,0)`

   * `dy = 0−1 = −1`, `dx = 0−1 = −1`
   * Not horizontal/vertical. `g = gcd(1,1) = 1` → `(−1, −1)`
   * Enforce `dx > 0`: `dx = −1 < 0` → flip to `(1, 1)`
   * Key `(1,1)` → `count=1`, `best=1`

2. `B = P2 (2,2)`

   * `dy = 1`, `dx = 1`, `g=1` → `(1,1)` (already normalized)
   * `count=2`, `best=2`

3. `B = P3 (3,3)`

   * `dy = 2`, `dx = 2`, `g=2` → `(1,1)`
   * `count=3`, `best=3`

4. `B = P4 (2,0)`

   * `dy = −1`, `dx = 1`, `g=1` → `(−1,1)`
   * `dx > 0` already; key `(−1,1)`
   * `count=1`, `best` stays `3`

5. `B = P5 (2,5)`

   * `dy = 4`, `dx = 1`, `g=1` → `(4,1)`
   * `dx > 0`; key `(4,1)`
   * `count=1`, `best=3`

6. `B = P6 (1,1)` **duplicate**

   * Same coords as A → `duplicates++` → `duplicates=1`

Finish anchor:

* `best = 3` (slope `(1,1)`), `duplicates = 1`, add the anchor:
  `total = 3 + 1 + 1 = 5`
* Update `globalBest = max(globalBest, 5) = 5`.

**Interpretation:** The line y = x through A has 3 other distinct points (P0,P2,P3), plus 1 duplicate of A (P6), plus A itself → 5.

---

## Anchor A = P4 = (2,0)

Reset: `map = {}`, `duplicates = 0`, `best = 0`.

1. `B = P0 (0,0)`

   * `dy = 0`, `dx = −2` → **horizontal**
   * Canonical horizontal key `(0,1)`
   * `count=1`, `best=1`

2. `B = P1 (1,1)`

   * `dy = 1`, `dx = −1`, `g=1` → `(1, −1)`
   * Enforce `dx > 0`: flip → `(−1, 1)`
   * `count=1`, `best=1`

3. `B = P2 (2,2)`

   * `dy = 2`, `dx = 0` → **vertical**
   * Canonical vertical key `(1,0)`
   * `count=1`, `best=1`

4. `B = P3 (3,3)`

   * `dy = 3`, `dx = 1`, `g=1` → `(3,1)`
   * `count=1`, `best=1`

5. `B = P5 (2,5)`

   * `dy = 5`, `dx = 0` → **vertical** `(1,0)`
   * `count=2`, `best=2`  // (P2,P5)+A vertical line

6. `B = P6 (1,1)`

   * `dy = 1`, `dx = −1` → normalize to `(−1,1)`
   * For slope `(−1,1)`: `count=2`, `best` stays `2`

Finish anchor:

* `best = 2`, `duplicates = 0`, `total = 2 + 0 + 1 = 3` (vertical line x=2: P4,A + two others P2,P5)

`globalBest` remains `5`.

---

# Why each rule matters (recap)

* **Reduce** with gcd: merges proportional deltas into one slope bucket → no undercounting.
* **Normalize** (sign + special cases): prevents equal slopes from splintering across many signed/parameterized variants → consistent keys.
* **Duplicates**: add to *every* line through anchor → handled once via a counter.
* **Per-anchor map**: lets us consider all lines through `A` simultaneously in `O(n)` time per anchor.
* **Final aggregation**: `best + duplicates + 1` captures “others on the line” + “all duplicates” + “the anchor.”

---

# Pitfalls to avoid

* Using `double` slope keys → precision & `-0.0` vs `0.0` issues.
* Forgetting to canonicalize vertical/horizontal → many distinct keys for the same line.
* Not normalizing sign → `(1,2)` vs `(-1,-2)` counted separately.
* Missing duplicates → lines through the anchor won’t include coincident points.
* Overflow in `dy, dx` before gcd on big coordinates → compute deltas in `long`.

---

# WHY DO WE NEED EQUALS AND HASCODE IN THE PAIR CLASS

it **is** being used, just not by you directly.

When you put a `Pair` into a `HashMap<Pair, Integer>`, Java’s `HashMap` uses **`hashCode()`** to pick a bucket and **`equals()`** to check if a key already exists in that bucket. So every time you do:

```java
map.getOrDefault(new Pair(dy, dx), 0);
map.put(new Pair(dy, dx), cnt);
```

the map calls `hashCode()` and (when needed) `equals()` on your `Pair`. If you don’t override them, the default implementations from `Object` use **identity** (memory address), meaning two `Pair(dy, dx)` with the same numbers are treated as **different keys**. That would split counts across duplicates and break the algorithm.

### TL;DR

* **Why have them?** To make `Pair(dy,dx)` behave as a **value object** (keys equal by content).
* **Why it looks unused?** Your code never calls them explicitly; the **map** does.
* **What happens without them?** `map.get(new Pair(1,2))` won’t find an earlier `put(new Pair(1,2), …)` because keys aren’t equal by content.

### Tips

* Make the fields **final** (immutable key): `final int dy, dx;`
* Keep `equals` and `hashCode` consistent with your normalization.
* If you’re on Java 16+, a `record Pair(int dy, int dx) {}` auto-generates correct `equals/hashCode`.


# Complexity

* **Time:** `O(n²)` (outer `n` anchors × inner ~`n` pairings; each operation O(1) amortized).
* **Space:** `O(n)` for the slope map per anchor (discarded each iteration).
*/
class Solution {
        static class Pair {
        final int dy, dx; // already reduced + normalized

        Pair(int dy, int dx) {
            this.dy = dy;
            this.dx = dx;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false;
            Pair p = (Pair) o;
            return dy == p.dy && dx == p.dx;
        }

        @Override public int hashCode() { // simple, fast
            return 31 * dy + dx;
        }
    }


    // Euclid's gcd on non-negative ints
    private static int gcd(int a, int b) {
        if (a < 0) a = -a;
        if (b < 0) b = -b;
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    /**
    * Returns a canonical slope key for vector (dy, dx):
    *  - Vertical => (1, 0)
    *  - Horizontal => (0, 1)
    *  - Otherwise: reduce by gcd; force dx > 0 (flip both signs if dx < 0).
    */
    private static Pair normalizeSlope(int dy, int dx) {
        if (dx == 0) return new Pair(1, 0);   // vertical
        if (dy == 0) return new Pair(0, 1);   // horizontal

        int g = gcd(dy, dx);
        dy /= g;
        dx /= g;

        // Normalize sign so each slope has a unique direction.
        // Force dx > 0. If dx < 0, flip both.
        if (dx < 0) { dy = -dy; dx = -dx; }

        return new Pair(dy, dx);
    }



    public int maxPoints(int[][] points) {
        int n = points.length;
        if (n <= 2) return n;

        int globalBest = 0;

        for (int i = 0; i < n; i++) {
            Map<Pair, Integer> map = new HashMap<>();
            int dup = 0;      // duplicates of points[i]
            int best = 0;     // best slope count through i

            int x1 = points[i][0], y1 = points[i][1];

            // You can start j = i + 1, but j = 0..n-1 with i==j skip works too.
            for (int j = i + 1; j < n; j++) {
                int x2 = points[j][0], y2 = points[j][1];

                if (x1 == x2 && y1 == y2) { // duplicate point
                    dup++;
                    continue;
                }

                int dy = y2 - y1;
                int dx = x2 - x1;

                Pair key = normalizeSlope(dy, dx);
                int cnt = map.getOrDefault(key, 0) + 1;
                map.put(key, cnt);

                if (cnt > best) best = cnt;
            }

            // best counts how many OTHER points share the max slope with i
            // add duplicates and the anchor itself
            globalBest = Math.max(globalBest, best + dup + 1);

            // Optional pruning: if remaining points can’t beat globalBest, break.
            // if (globalBest >= n - i) break;
        }

        return globalBest;
    }

}