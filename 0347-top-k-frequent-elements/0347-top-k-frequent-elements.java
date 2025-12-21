// Method 1: My Inefficient approach using HashMap and Max-Heap
/*
4) Minor: record is generic but always used as Pair<Integer,Integer>
Not wrong, just extra complexity; you can store int[] or Map.Entry<Integer,Integer> directly.

5) Complexity is not optimal
You build a heap of size U (#unique) and pop k:
Time: O(n + U log U + k log U) → typically summarized as O(n log U)
Space: O(U)
This passes, but there are faster approaches.
*/
class Solution {
    public record Pair<F, S> (F first, S second) {}

    public int[] topKFrequent(int[] nums, int k) {
        int[] ans = new int[k];

        HashMap<Integer, Integer> map = new HashMap<>();

        for(int num: nums){
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(
            (a, b) -> Integer.compare(b.second(), a.second())
        );

        for(Map.Entry<Integer, Integer> entry : map.entrySet()){
            pq.offer(new Pair<>(entry.getKey(), entry.getValue()));
        }

        while(k >= 1){
            Pair<Integer, Integer> pair = pq.poll();
            ans[k-1] = pair.first();
            k--;
        }

        return ans;
    }
}




// Method 1.5: Similar approach but better using HashMap and Min-Heap 
// Min-heap of size k (more efficient when k is small)
/*
Instead of pushing all elements then popping k, keep only the top k in a min-heap:
Heap stores (num, freq)
If heap size > k, pop the smallest freq
End result: heap contains k most frequent

Time: O(n + U log k) (better than log U if k ≪ U)
Space: O(U + k) (or just O(U) counting map dominates)

Why this works:

* The heap always keeps the “best k so far”.
* The top is the *worst among the kept* (smallest frequency), so it’s the first to be removed when we exceed k.

---

## Example walkthrough

### Example input

```text
nums = [1, 1, 1, 2, 2, 3]
k = 2
```

### Step 1: Build frequency map

Scan `nums`:

* count(1) = 3
* count(2) = 2
* count(3) = 1

So the map is:

```text
1 -> 3
2 -> 2
3 -> 1
```

### Step 2: Define min-heap

Heap stores pairs `[num, freq]`, ordered by `freq` ascending.

So the heap “top” (peek) is the pair with **smallest freq**.

---

## Step 3: Iterate through the frequency map and maintain heap size ≤ k

Assume we iterate over entries in some order. (HashMap order is arbitrary, but the logic always works.)
Let’s walk through one possible iteration order: `(1,3)`, `(2,2)`, `(3,1)`.

Heap shown as a set-like view (remember: internal heap array isn’t sorted; only min at top is guaranteed).

---

### Process entry (1, 3)

Operation:

* Offer `[1,3]` into heap.

Heap now:

```text
[ [1,3] ]         size = 1 (<= k)
```

No pop (size not > k).

---

### Process entry (2, 2)

Operation:

* Offer `[2,2]`.

Heap contains:

```text
[ [2,2], [1,3] ]  size = 2 (== k)
```

Min is `[2,2]` because freq 2 < freq 3.

No pop (size not > k).

Interpretation: “best 2 so far” are `{1 (freq3), 2 (freq2)}`.

---

### Process entry (3, 1)

Operation:

* Offer `[3,1]`.

Heap size becomes 3, which is > k, so we must pop once.

After offer, heap contains:

```text
[ [3,1], [1,3], [2,2] ]  size = 3
```

Now pop (remove smallest frequency):

* `poll()` removes `[3,1]`.

Heap after poll:

```text
[ [2,2], [1,3] ]  size = 2
```

Interpretation:

* We temporarily considered 3 candidates `{1,2,3}`
* Since k=2, we remove the least frequent one (3 with freq 1)
* Remaining are the top 2 frequent `{1,2}`

---

## Step 4: Build the output array

Heap now contains:

```text
[ [2,2], [1,3] ]
```

Pop them out. Your code fills from the end:

```java
for (int i = k - 1; i >= 0; i--) {
    ans[i] = pq.poll()[0];
}
```

### Pop #1

* `poll()` returns `[2,2]` (smallest freq among the top-k set)
* Put into `ans[1] = 2`

`ans = [?, 2]`

### Pop #2

* `poll()` returns `[1,3]`
* Put into `ans[0] = 1`

`ans = [1, 2]`

Return `[1,2]` (or could return `[2,1]` depending on how you fill; LC accepts any order).

✅ Correct: the 2 most frequent elements are 1 and 2.

---

# Bigger example (shows the “keep best k” behavior more clearly)

### Example

```text
nums = [4,4,4,4,  1,1,1,  2,2,  3]
k = 2
```

Frequencies:

```text
4 -> 4
1 -> 3
2 -> 2
3 -> 1
```

Walkthrough (order: 4,1,2,3):

* Add (4,4): heap = [(4,4)]
* Add (1,3): heap = [(1,3),(4,4)]  size=2
* Add (2,2): heap = [(2,2),(4,4),(1,3)] size=3 → pop (2,2)
  heap becomes [(1,3),(4,4)]
* Add (3,1): heap = [(3,1),(4,4),(1,3)] size=3 → pop (3,1)
  heap becomes [(1,3),(4,4)]

Final heap contains 4 and 1 — correct top 2.

---

## Why this is more efficient than a max-heap of all uniques

Let:

* `n` = total numbers
* `U` = unique numbers

Max-heap of all uniques: `O(U log U)`
Min-heap of size k: `O(U log k)`

If `k` is small (e.g., 10) and `U` is huge (e.g., 100,000), then `log k` is much smaller than `log U`.
*/

// class Solution {
//     public int[] topKFrequent(int[] nums, int k) {
//         Map<Integer, Integer> freq = new HashMap<>();
//         for (int x : nums) freq.put(x, freq.getOrDefault(x, 0) + 1);

//         PriorityQueue<int[]> pq = new PriorityQueue<>(
//             (a, b) -> Integer.compare(a[1], b[1]) // min-heap by freq
//         );

//         for (var e : freq.entrySet()) {
//             pq.offer(new int[]{e.getKey(), e.getValue()});
//             if (pq.size() > k) pq.poll();
//         }

//         int[] ans = new int[k];
//         for (int i = k - 1; i >= 0; i--) {
//             ans[i] = pq.poll()[0];
//         }
//         return ans;
//     }
// }





// Method 2: Bucket sort (best asymptotic: linear)
/*
## Why “bucket sort” works here

Key fact:
If `nums.length = n`, then the **maximum frequency** any number can have is `n`.

So frequencies are in the range:

```text
1, 2, 3, ..., n
```

That means we can create an array of buckets:

> `buckets[f]` holds **all numbers that appear exactly `f` times**.

Then the “most frequent” elements will be sitting in buckets with large `f`.
So we scan buckets from `n` down to `1` and collect numbers until we have `k` of them.

This avoids heaps and gives linear-ish performance.

---

## Algorithm steps

### Step 1: Count frequencies with a HashMap

Make a map `freq`:

* key: number
* value: frequency (count of occurrences)

### Step 2: Build buckets

Create:

```java
List<Integer>[] buckets = new ArrayList[n + 1];
```

Why `n+1`?

* We want bucket indices from `0..n`
* We won’t use `0`, but it’s convenient.

For each `(num, f)` in the map:

* if `buckets[f]` is null, create a new list
* add `num` to `buckets[f]`

### Step 3: Collect answer from high frequency down

Start from `f = n` down to `1`:

* if bucket not null, add elements to result until you have `k`.


LeetCode accepts answers in any order, so we don’t need to sort within a bucket.

---

## Thorough example walkthrough

Let’s use a good example where multiple frequencies exist:

### Example

```text
nums = [1,1,1,2,2,3,3,3,3,4]
k = 2
```

`n = 10`

### Step 1: Build frequency map

Scan `nums` and count:

* 1 appears 3 times
* 2 appears 2 times
* 3 appears 4 times
* 4 appears 1 time

So:

```text
freq = {
  1 -> 3,
  2 -> 2,
  3 -> 4,
  4 -> 1
}
```

### Step 2: Create buckets array

We create:

```text
buckets[0..10]  (size = 11)
```

Initially all are null:

```text
buckets[0] = null
buckets[1] = null
buckets[2] = null
...
buckets[10] = null
```

Now place each number into the bucket equal to its frequency:

* num=1, f=3 → buckets[3] = [1]
* num=2, f=2 → buckets[2] = [2]
* num=3, f=4 → buckets[4] = [3]
* num=4, f=1 → buckets[1] = [4]

So buckets look like:

```text
buckets[1] = [4]
buckets[2] = [2]
buckets[3] = [1]
buckets[4] = [3]
(all others null)
```

Interpretation:

* frequency 4: number 3
* frequency 3: number 1
* frequency 2: number 2
* frequency 1: number 4

### Step 3: Collect top k from highest frequency down

We need `k = 2` numbers.

Initialize:

```text
ans = [?, ?]
idx = 0
```

Now iterate f from 10 down to 1:

* f=10..5: buckets[f] is null → skip
* f=4: buckets[4] = [3] (not null)

Take numbers from buckets[4]:

* take 3 → ans[0] = 3, idx=1

Now:

```text
ans = [3, ?]
idx = 1
```

Continue scanning:

* f=3: buckets[3] = [1]

Take from buckets[3]:

* take 1 → ans[1] = 1, idx=2

Now:

```text
ans = [3, 1]
idx = 2 == k
```

Stop and return `[3, 1]`.

✅ Correct: the 2 most frequent elements are 3 (freq 4) and 1 (freq 3).

---

## Another example showing ties

### Example

```text
nums = [5,5,6,6,7,7,8]
k = 2
```

Frequencies:

* 5 -> 2
* 6 -> 2
* 7 -> 2
* 8 -> 1

Buckets:

* buckets[2] = [5,6,7]
* buckets[1] = [8]

Scan from high freq:

* at f=2, bucket has [5,6,7]
  Pick first two encountered: [5,6] (or [6,7], etc. depends on map iteration order)

LeetCode allows any valid top-k when there are ties.

---

## Complexity and why it’s “linear”

* Building freq map: `O(n)`
* Distributing into buckets: `O(U)` where `U` is #unique, and `U ≤ n`
* Scanning buckets from `n..1`: `O(n)` worst-case
* Total: `O(n)` time, `O(n)` space for buckets.
*/

// class Solution {
//     public int[] topKFrequent(int[] nums, int k) {
//         Map<Integer, Integer> freq = new HashMap<>();
//         for (int x : nums) freq.put(x, freq.getOrDefault(x, 0) + 1);

//         // buckets[f] = list of nums with frequency f
//         List<Integer>[] buckets = new ArrayList[nums.length + 1];
//         for (var e : freq.entrySet()) {
//             int f = e.getValue();
//             if (buckets[f] == null) buckets[f] = new ArrayList<>();
//             buckets[f].add(e.getKey());
//         }

//         int[] ans = new int[k];
//         int idx = 0;

//         // traverse from high freq to low
//         for (int f = nums.length; f >= 1 && idx < k; f--) {
//             if (buckets[f] != null) {
//                 for (int val : buckets[f]) {
//                     ans[idx++] = val;
//                     if (idx == k) break;
//                 }
//             }
//         }
//         return ans;
//     }
// }







// Method 3: Quickselect
/*
This is the “average O(n)” approach: instead of fully sorting by frequency, we **partition** so that the **top k frequent** elements end up on one side.

---

## 1) Key idea

1. Count frequencies: `num -> freq`
2. Put all unique numbers into an array `unique[]`
3. Use **quickselect** to rearrange `unique[]` so that:

   * the **k most frequent** numbers are in the last `k` positions (or first `k`, depending on how you partition)
4. Return those k numbers.

This is analogous to finding the k-th largest element, but the “value” is `freq[num]`.

---

## 2) Why Quickselect works here

Quickselect repeatedly:

* chooses a pivot,
* partitions the array into elements “less than pivot” and “greater than pivot” based on frequency,
* and recurses only into the side that contains the boundary you care about.

So you avoid sorting everything (`O(U log U)`), and instead do **expected O(U)** work.

> Worst-case can degrade to `O(U^2)` if pivots are consistently bad, so we randomize pivot selection.

---

## 3) Choose a partition direction

I’ll implement partition so that:

* **smaller frequencies go left**
* **larger frequencies go right**

Then the top k frequent elements will end up at the **right end**.

We want the element at index `target = U - k` to be the boundary:

* indices `[target ... U-1]` = k most frequent (not necessarily sorted)

Notes:

* We used `< pivotFreq` on the left. Equal frequencies go to the right side with the pivot group; that’s fine because any ordering among ties is acceptable.
* Output can be in any order.

---

## 5) Thorough example walkthrough

Let’s walk through:

```text
nums = [1,1,1,2,2,3]
k = 2
```

### Step A: Build frequencies

Count:

* 1 -> 3
* 2 -> 2
* 3 -> 1

Unique array might be:

```text
unique = [1, 2, 3]   (order depends on HashMap, but we’ll use this)
U = 3
k = 2
target = U - k = 1
```

We want `unique[1..2]` to be the top 2 frequent elements.

So after quickselect, we want index 1 to be the “boundary” such that:

* `unique[0]` has the smallest freq among all
* `unique[1]` and `unique[2]` are the top 2 (freq 2 and 3)

### Step B: First partition

Suppose we pick pivotIdx = 0 → pivotVal = 1 → pivotFreq = 3.

We partition by “freq < 3” to the left.

Current:

```text
unique = [1, 2, 3]
freqs  = [3, 2, 1]
left=0 right=2 pivot=1(freq3)
```

**Move pivot to end**:
swap pivotIdx 0 with right 2:

```text
unique = [3, 2, 1]
freqs  = [1, 2, 3]
pivot (1,freq3) is at end
```

Now scan i from left..right-1 (0..1), store = left = 0:

* i=0: unique[i]=3 freq=1 < 3 → swap(store=0, i=0) (no change), store=1
* i=1: unique[i]=2 freq=2 < 3 → swap(store=1, i=1) (no change), store=2

Finally swap pivot into place: swap(store=2, right=2) (no change)

Result:

```text
unique = [3, 2, 1]
pivot final index = 2
```

Interpretation:

* Left side [3,2] are all < pivotFreq (freq 1 and 2)
* Pivot [1] has freq 3 at index 2

Now compare pivotIndex=2 with target=1:

* 2 > 1, so the target is in the **left half**
* Set right = 1

### Step C: Second partition on subarray [0..1]

Now we quickselect within:

```text
unique subarray = [3, 2]
freqs           = [1, 2]
left=0 right=1 target=1
```

Pick pivotIdx randomly; suppose pivotIdx = 0 → pivotVal=3 → pivotFreq=1.

Move pivot to end:
swap(0,1):

```text
unique = [2, 3, 1]
freqs  = [2, 1, 3]
```

Partition i from 0..0, store=0:

* i=0: unique[i]=2 freq=2 < pivotFreq(1)? no → do nothing

Swap pivot into place: swap(store=0, right=1)

```text
unique = [3, 2, 1]
pivot final index = 0
```

Compare pivotIndex=0 with target=1:

* 0 < 1, so target is in the **right side** of this subarray
* Set left = 1

### Step D: Done (left == right == target)

Now left=1 right=1, quickselect ends. The array currently is:

```text
unique = [3, 2, 1]
freqs  = [1, 2, 3]
target = 1
```

Check what we need: top k = 2 elements should be in indices [1..2]:

* unique[1] = 2 (freq 2)
* unique[2] = 1 (freq 3)

Return `[2, 1]` (order doesn’t matter)

✅ Correct.

---

## 6) When to use Quickselect vs Heap/Bucket

* **Bucket sort**: simplest “optimal” and very popular on LC; O(n) time, O(n) space.
* **Min-heap size k**: great when k is small; O(n log k).
* **Quickselect**: good when you want **average linear** time without extra O(n) buckets, and you’re comfortable implementing partition safely.
*/

// class Solution {
//     private final Random rand = new Random();

//     public int[] topKFrequent(int[] nums, int k) {
//         // 1) Frequency map
//         Map<Integer, Integer> freq = new HashMap<>();
//         for (int x : nums) {
//             freq.put(x, freq.getOrDefault(x, 0) + 1);
//         }

//         int U = freq.size();                 // number of unique elements
//         int[] unique = new int[U];
//         int idx = 0;
//         for (int key : freq.keySet()) {
//             unique[idx++] = key;
//         }

//         // 2) Quickselect so that top k frequent are in unique[U-k ... U-1]
//         int target = U - k;
//         quickselect(unique, 0, U - 1, target, freq);

//         // 3) Collect answer
//         int[] ans = new int[k];
//         for (int i = 0; i < k; i++) {
//             ans[i] = unique[target + i];
//         }
//         return ans;
//     }

//     private void quickselect(int[] arr, int left, int right, int targetIdx,
//                              Map<Integer, Integer> freq) {
//         while (left <= right) {
//             int pivotIdx = left + rand.nextInt(right - left + 1);
//             int newPivotIdx = partition(arr, left, right, pivotIdx, freq);

//             if (newPivotIdx == targetIdx) {
//                 return;
//             } else if (newPivotIdx < targetIdx) {
//                 left = newPivotIdx + 1;
//             } else {
//                 right = newPivotIdx - 1;
//             }
//         }
//     }

//     // Partition by frequency (ascending):
//     // After partition:
//     //   arr[left .. store-1] have freq < pivotFreq
//     //   arr[store] is pivot
//     //   arr[store+1 .. right] have freq >= pivotFreq
//     private int partition(int[] arr, int left, int right, int pivotIdx,
//                           Map<Integer, Integer> freq) {
//         int pivotVal = arr[pivotIdx];
//         int pivotFreq = freq.get(pivotVal);

//         // Move pivot to end
//         swap(arr, pivotIdx, right);

//         int store = left;
//         for (int i = left; i < right; i++) {
//             if (freq.get(arr[i]) < pivotFreq) {
//                 swap(arr, store, i);
//                 store++;
//             }
//         }

//         // Move pivot to its final place
//         swap(arr, store, right);
//         return store;
//     }

//     private void swap(int[] arr, int i, int j) {
//         int tmp = arr[i];
//         arr[i] = arr[j];
//         arr[j] = tmp;
//     }
// }
