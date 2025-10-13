// Method 1.5: Min-heap of indices (atmost k values in heap at all times), O(k log m)
/*
# Why this works

Think of each fixed `i` in `nums1` as producing a sorted “row” of sums with `nums2`:

```
Row i: (nums1[i] + nums2[0]) ≤ (nums1[i] + nums2[1]) ≤ ... ≤ (nums1[i] + nums2[n-1])
```

All rows are individually sorted (because `nums2` is sorted). We want the global k smallest across all rows. That’s the classic **k-way merge** pattern:

* Seed the heap with the first element of each row: `(i, 0)` for `i = 0..min(m-1, k-1)`.
* Repeatedly pop the smallest sum `(i, j)` and append `(nums1[i], nums2[j])` to the answer.
* From the same row `i`, the **next** candidate is `(i, j+1)` (since rows are sorted). Push it if it exists.
* Stop after `k` pops.

This explores only **O(k)** pairs (plus the initial seeding), instead of generating all `m*n` pairs.

**Time:** heap holds at most `min(m, k)` nodes ⇒ each pop/push is `O(log min(m, k))`.
We do ≤ `k` pops ⇒ **`O(k log min(m, k))`**.
**Space:** heap + result ⇒ **`O(min(m, k))`** (result is `O(k)` as required).

---

# Thorough example walkthrough

**Input**
`nums1 = [1, 7, 11]`
`nums2 = [2, 4, 6]`
`k = 5`

We want 5 pairs with smallest sums.

## Seed

Push `(i,0)` for `i = 0..min(3,5)-1 = 0..2`:

* Push `(0,0)` sum = 1+2=3
* Push `(1,0)` sum = 7+2=9
* Push `(2,0)` sum = 11+2=13

Heap (by sum): `[ (0,0:3), (1,0:9), (2,0:13) ]`

## Pop 1 (k=5 → 4)

* Pop `(0,0)` → pair is `[1,2]`
* Push next from same row `i=0`: `(0,1)` sum=1+4=5
* Heap: `[ (0,1:5), (2,0:13), (1,0:9) ]`  (ordering shown by sums)

**Result so far:** `[[1,2]]`

## Pop 2 (k=4 → 3)

* Pop `(0,1)` → pair `[1,4]`
* Push `(0,2)` sum=1+6=7
* Heap: `[ (0,2:7), (2,0:13), (1,0:9) ]`

**Result:** `[[1,2],[1,4]]`

## Pop 3 (k=3 → 2)

* Pop `(0,2)` → pair `[1,6]`
* Next `(0,3)` doesn’t exist (j+1==3 == n), so push nothing.
* Heap: `[ (1,0:9), (2,0:13) ]`

**Result:** `[[1,2],[1,4],[1,6]]`

## Pop 4 (k=2 → 1)

* Pop `(1,0)` → pair `[7,2]`
* Push `(1,1)` sum=7+4=11
* Heap: `[ (1,1:11), (2,0:13) ]`

**Result:** `[[1,2],[1,4],[1,6],[7,2]]`

## Pop 5 (k=1 → 0)

* Pop `(1,1)` → pair `[7,4]`
* Push `(1,2)` sum=7+6=13
* Heap: `[ (2,0:13), (1,2:13) ]` (two 13s remain, but we already have k=0)

**Final 5 pairs:**
`[[1,2], [1,4], [1,6], [7,2], [7,4]]`
(these are indeed the 5 smallest sums)

---

# Subtleties & tips

* **Why indices, not values?**
  Using `(i, j)` lets us generate only the next needed neighbor `(i, j+1)` from a row. If you store values only, you lose the ability to advance in `nums2` without recomputing or duplicating.
* **No visited set needed:**
  We only ever push `(i, 0)` once per row, and from a popped `(i, j)` we push only `(i, j+1)`. That never creates duplicates.
* **Bounds & early exits:**

  * If either array is empty or `k == 0`, return `[]`.
  * If `k > m*n`, the loop stops when the heap empties; you’ll simply return all `m*n` pairs.
* **Comparator overflow:**
  The code stores `sum` as `long` so the comparator doesn’t overflow if inputs are large.

---

# (Optional) Alternative approaches

1. **Brute force + heap:** Push all `m*n` pairs, pop `k`.

   * **Time/Space:** `O(mn log (mn))` / `O(mn)` → not viable for large inputs.

2. **Binary search on the sum threshold:**

   * Binary search a value `S` such that the number of pairs with sum `≤ S` is ≥ `k`, then gather the first `k` pairs.
   * **Time:** `O((m + n) log (maxSum − minSum))` to count, plus gathering.
   * More complex to implement correctly; the min-heap method above is preferred for interviews.
*/

class Solution {

    // Heap node holds indices (i, j) into nums1 and nums2, plus their sum
    static class Node {
        int i, j;
        long sum; // use long to avoid overflow in comparator
        Node(int i, int j, long sum) { this.i = i; this.j = j; this.sum = sum; }
    }

    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> res = new ArrayList<>();
        int m = nums1.length, n = nums2.length;
        if (k == 0 || m == 0 || n == 0) return res;

        // Min-heap by pair sum
        PriorityQueue<Node> pq = new PriorityQueue<>(
                Comparator.comparingLong(a -> a.sum)
        );

        // Seed the heap with (i, 0) (i.e. the first column of the 2D grid) for i = 0..min(m-1, k-1)
        int limit = Math.min(m, k);
        for (int i = 0; i < limit; i++) {
            pq.offer(new Node(i, 0, (long) nums1[i] + nums2[0]));
        }

        // Extract the next smallest pair up to k times
        while (k > 0 && !pq.isEmpty()) {
            Node cur = pq.poll();
            int i = cur.i, j = cur.j;
            res.add(Arrays.asList(nums1[i], nums2[j]));
            k--;

            // Push the next pair from the same i: (i, j+1)
            if (j + 1 < n) {
                pq.offer(new Node(i, j + 1, (long) nums1[i] + nums2[j + 1]));
            }
        }

        return res;
    }
}






// Method 1: My ineffieicient answer by adding all pairs to the Min-heap
// class Solution {

//     static class Pair{
//         private int first;
//         private int second;
        
//         Pair() {}

//         Pair(int first, int second){
//             this.first = first;
//             this.second = second;
//         }

//         public int getFirst(){
//             return first;
//         }

//         public int getSecond(){
//             return second;
//         }
//     }

//     /*
//     record Pair(int first, int second) {}
//     PriorityQueue<Pair> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.first() + p.second()));
//     */

//     public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
//         List<List<Integer>> res = new ArrayList<>();
//         PriorityQueue<Pair> pq = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.getFirst() + p1.getSecond(), p2.getFirst() + p2.getSecond()));

//         for(int i=0; i<nums1.length; i++){
//             for(int j=0; j<nums2.length; j++){
//                 pq.add(new Pair(nums1[i], nums2[j]));
//             }   
//         }

//         for(int i=0; i<k; i++){
//             Pair p = pq.poll();
//             res.add(List.of(p.getFirst(), p.getSecond()));
//         }

//         return res;
//     }
// }
