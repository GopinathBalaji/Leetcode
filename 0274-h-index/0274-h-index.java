// NOTE: If there are n papers, the H-index can’t be more than n.

// Method 1: Sort, then check remaining count
/*
Why this works: once you find the first i where citations[i] >= n - i, all later positions also satisfy it, and the maximum valid h is exactly n - i.
*/
class Solution {
    public int hIndex(int[] citations) {
        Arrays.sort(citations);
        int n = citations.length; 

        for(int i=0; i<citations.length; i++){
            int papersWithAtLeastThis = n - i;
            if(citations[i] >= papersWithAtLeastThis){
                return papersWithAtLeastThis;
            }
        }

        return 0;
    }
}



// Method 2: No Sort (Counting Sort algorithm (uses the frequency of the array elements))
/*

## How the method works

### Key facts about H-index

* If there are `n` papers, the H-index can’t be more than `n`.
* We only care, for each `h ∈ [0..n]`, how many papers have **≥ h** citations.

### Steps

1. **Make buckets** of size `n+1`: `bucket[0..n]`.

   * For each citation count `c`:

     * If `c >= n`, increment `bucket[n]` (cap at `n`, since H can’t exceed `n`).
     * Else increment `bucket[c]`.
   * Now `bucket[x]` = number of papers with **exactly** `x` citations, except `bucket[n]` also includes “`≥ n`”.

2. **Scan from high to low**, accumulating how many papers have **≥ h** citations:

   * Let `papers = 0`.
   * For `h` from `n` down to `0`:

     * `papers += bucket[h]`  (now `papers` = #papers with ≥ `h`)
     * If `papers >= h`, return `h` (this is the **largest** such `h` because we’re going downward).

### Why it’s correct

The H-index is defined as the **maximum** `h` such that at least `h` papers have ≥ `h` citations.
Scanning `h` from `n` down to `0` ensures the first time `papers ≥ h`, that `h` is maximal.

### Complexity

* Time: **O(n)** to fill buckets + **O(n)** to scan ⇒ **O(n)** total.
* Space: **O(n)** for the buckets.

---

## Walkthrough 1: `[3, 0, 6, 1, 5]` (classic example)

`n = 5` → buckets `bucket[0..5] = [0,0,0,0,0,0]`

Fill:

* 3 → `bucket[3]++`
* 0 → `bucket[0]++`
* 6 → `c ≥ n` → `bucket[5]++`
* 1 → `bucket[1]++`
* 5 → `c ≥ n` (5==n) → `bucket[5]++`

Buckets now:

```
h:        0  1  2  3  4  5
bucket:  [1, 1, 0, 1, 0, 2]
```

Scan downward, tracking `papers` (≥ h):

* h=5: papers = 2  → 2 ≥ 5 ? no
* h=4: papers = 2  → 2 ≥ 4 ? no
* h=3: papers = 2 + bucket\[3]=3 → 3 ≥ 3 ? **yes** ⇒ return **3**.

---

## Walkthrough 2: `[0, 1, 4, 4]`

`n = 4` → buckets `bucket[0..4]`

Fill:

* 0 → bucket\[0]++
* 1 → bucket\[1]++
* 4 → c ≥ n → bucket\[4]++
* 4 → c ≥ n → bucket\[4]++

Buckets:

```
h:        0  1  2  3  4
bucket:  [1, 1, 0, 0, 2]
```

Scan:

* h=4: papers = 2  → 2 ≥ 4 ? no
* h=3: papers = 2  → 2 ≥ 3 ? no
* h=2: papers = 2 + bucket\[2]=2 → 2 ≥ 2 ? **yes** ⇒ return **2**.

---

## Why we cap at `n`

Even if a paper has 1000 citations, it contributes at most **1** to any “≥ h” count, and `h` can’t exceed the number of papers `n`. So we lump all `c ≥ n` into `bucket[n]` to keep the array size small and the scan linear.
*/
// class Solution {
//     public int hIndex(int[] citations) {
//         int n = citations.length;
//         int[] bucket = new int[n + 1];

//         // Count frequency, capping at n
//         for (int c : citations) {
//             if (c >= n) bucket[n]++;
//             else bucket[c]++;
//         }

//         // Accumulate from high h down to 0
//         int papers = 0; // #papers with ≥ current h
//         for (int h = n; h >= 0; h--) {
//             papers += bucket[h];
//             if (papers >= h) return h;
//         }
//         return 0; // fallback; not actually reached
//     }
// }