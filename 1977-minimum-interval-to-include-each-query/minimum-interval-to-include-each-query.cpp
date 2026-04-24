// Method 1: Line Sweep, Sorting, Heap/PriorityQueue
/*
# WHAT WAS WRONG WITH MY APPROACH:
Your logic is **correct** in the sense that it will return the right answer, but it has two big practical problems (and one small nuance):

## 1) Time complexity is too slow (will TLE)

You rebuild a heap **from scratch for every query**:

* For each query `q` you loop through **all intervals** and add those with `start <= q` → `O(n log n)` per query.
* Then you pop some expired ones → up to `O(n log n)` per query.
* Total worst case: **O(q * n log n)**, which will time out for LC 1851 constraints (both can be up to 10^5).

The intended solution is ~**O((n + q) log n)** by:

* sorting intervals by `start`
* sorting queries (keeping original indices)
* pushing intervals into ONE heap incrementally as queries increase
* popping intervals whose `end < query`
* answering from heap top

So the “wrong” thing is not correctness, it’s efficiency.

---

## 2) You’re missing an important filter: intervals that can’t ever cover the query

You only check:

```java
if (intervals[i][0] <= query) minHeap.add(intervals[i]);
```

That includes intervals with `end < query` too (you later pop those) — which is fine but expensive.

More importantly, you **never avoid adding** intervals where `end < query` even though they’re guaranteed useless for that query. Again: not incorrect, but unnecessary work.

A tighter per-query build (still too slow overall) would be:

```java
if (intervals[i][0] <= query && intervals[i][1] >= query) add
```

But the real fix is the global sweep approach.

---

## 3) Subtle but important: heap ordering alone can’t “clean” invalid intervals

You do clean-up with:

```java
while (!minHeap.isEmpty() && minHeap.peek()[1] < query) minHeap.poll();
```

This is correct only because you keep polling until the top is valid. Even if some invalid intervals are buried deeper in the heap, they don’t matter as long as the minimum-length valid interval rises to the top eventually.

So this part is **fine**.

---

# What the accepted approach looks like (high level)

1. Sort `intervals` by `start`.
2. Make `queriesWithIndex = [(queryValue, originalIndex)]` and sort by queryValue.
3. Maintain one min-heap ordered by interval length.
4. For each query in increasing order:

   * Push all intervals with `start <= query` into heap.
   * Pop while heap top has `end < query`.
   * Top gives answer, else -1.

That gets you the needed performance.

---

## Bottom line

* ✅ Your solution is **correct** logically.
* ❌ It will **time out** because it’s **O(Q * N log N)**.
* The fix is to **sort queries + sweep intervals once** with a single heap: **O((N+Q) log N)**.

############# CORRECT APPROACH ##############

Here’s the **accepted O((N+Q) log N)** approach for **LeetCode 1851: Minimum Interval to Include Each Query**, with a full walkthrough.

---

## Big picture

For each query `q`, we want:

* among all intervals `[l, r]` such that `l <= q <= r`,
* return the **minimum length** `(r - l + 1)`,
* else `-1`.

Brute force per query is too slow. The trick is to process queries in **increasing order** and add intervals only when they become relevant.

---

## Correct approach

### Step 1: Sort intervals by start

Sort `intervals` by `l` ascending.

This lets us “activate” intervals as queries increase: when we’re at query `q`, all intervals with `l <= q` are candidates (they *might* cover `q`).

### Step 2: Sort queries but keep original indices

Create an array `qArr = [(value, originalIndex)]` and sort by `value`.

We’ll compute answers in sorted-query order, but store them back into `ans[originalIndex]`.

### Step 3: Use a min-heap ordered by interval length

Maintain a min-heap of intervals that have **started** (`l <= q`). Heap ordering is:

1. interval length `(r - l + 1)` ascending
2. tie-break by `r` ascending (optional)

Why this works:

* Once we remove intervals that ended before `q`, then everything left in heap satisfies `l <= q` (by construction) and `r >= q` (by cleaning).
* So the heap top is the **shortest interval that covers q**.

### Step 4: Sweep through queries

For each query `q` in ascending order:

1. **Push** into heap all intervals with `start <= q`.
2. **Pop** from heap while `end < q` (these can’t cover this query or any later query).
3. If heap non-empty, answer is heap-top length; else -1.

---

## Why popping by `end < q` works even though heap is ordered by length

Some expired intervals might be buried deeper, but they only matter if they become the top.
By repeatedly removing expired intervals at the top, we ensure the top is always valid before answering.

Complexity:

* Sorting: `O(N log N + Q log Q)`
* Each interval inserted once and popped at most once: total heap ops `O((N+Q) log N)`

---

## Thorough example walkthrough

Use the classic example:

```text
intervals = [[1,4], [2,4], [3,6], [4,4]]
queries   = [2, 3, 4, 5]
```

Expected answers:

* q=2 → intervals covering 2: [1,4] len4, [2,4] len3 → answer 3
* q=3 → covers: [1,4] len4, [2,4] len3, [3,6] len4 → answer 3
* q=4 → covers: [1,4] len4, [2,4] len3, [3,6] len4, [4,4] len1 → answer 1
* q=5 → covers: [3,6] len4 → answer 4

### Step 1: Sort intervals by start

Already sorted by start:

1. [1,4]
2. [2,4]
3. [3,6]
4. [4,4]

### Step 2: Queries with indices, sorted

Queries already sorted:

* (2, idx0)
* (3, idx1)
* (4, idx2)
* (5, idx3)

We maintain:

* `i` pointer into intervals (starts at 0)
* min-heap `pq` (by length, then end)

---

### Process query = 2

**Add intervals with start <= 2**

* intervals[0] = [1,4] start 1 <= 2 → push
* intervals[1] = [2,4] start 2 <= 2 → push
* intervals[2] = [3,6] start 3 > 2 → stop

Heap contents (conceptually ordered by length):

* [2,4] len 3
* [1,4] len 4

**Remove expired (end < 2)**
Top is [2,4], end 4 < 2? no → nothing removed.

**Answer** = top length = 3
So ans[idx0] = 3

---

### Process query = 3

**Add intervals with start <= 3**

* intervals[2] = [3,6] start 3 <= 3 → push
* intervals[3] = [4,4] start 4 > 3 → stop

Heap now has:

* [2,4] len 3
* [1,4] len 4
* [3,6] len 4 (end 6)

**Remove expired (end < 3)**
Top [2,4] end 4 < 3? no → none removed.

**Answer** = 3
ans[idx1] = 3

---

### Process query = 4

**Add intervals with start <= 4**

* intervals[3] = [4,4] start 4 <= 4 → push
  Now i reaches end.

Heap contains:

* [4,4] len 1
* [2,4] len 3
* [1,4] len 4
* [3,6] len 4

**Remove expired (end < 4)**
Top is [4,4] end 4 < 4? no (equal is fine) → nothing removed.

**Answer** = top length = 1
ans[idx2] = 1

---

### Process query = 5

No more intervals to add (i already done).

Heap currently still has many intervals, but some don’t cover 5.

**Remove expired (end < 5)**
We keep popping while top’s end < 5:

* Top [4,4] has end 4 < 5 → pop it
* Now top likely [2,4] end 4 < 5 → pop it
* Next top [1,4] end 4 < 5 → pop it
* Next top [3,6] end 6 < 5? no → stop

Now heap top is [3,6] which *does* cover 5.

**Answer** = len([3,6]) = 4
ans[idx3] = 4

Final answer: `[3, 3, 1, 4]`

---

## Key takeaways

* Sort queries so you can sweep from small to large.
* Push intervals when they become eligible (`start <= q`).
* Pop intervals when they become impossible (`end < q`).
* Heap ordered by length gives the minimum covering interval.
*/
class Solution {
public:
    vector<int> minInterval(vector<vector<int>>& intervals, vector<int>& queries) {
        std::sort(intervals.begin(), intervals.end(), [](const vector<int>& a, const vector<int>& b){
            return a[0] < b[0];
        });

        vector<pair<int, int>> q;

        for(int i=0; i<queries.size(); i++){
            q.push_back({queries[i], i});
        }

        std::sort(q.begin(), q.end());

        priority_queue<pair<int, int>, vector<pair<int, int>>, greater<pair<int, int>>> minHeap;
        int i=0;
        vector<int> ans(queries.size(), -1);

        for(auto [query, idx] : q){

            while(i < intervals.size() && intervals[i][0] <= query){
                int start = intervals[i][0];
                int end = intervals[i][1];

                int length = end - start + 1;
                minHeap.push({length, end});

                i++;
            }

            while(!minHeap.empty() && minHeap.top().second < query){
                minHeap.pop();
            }

            int answer;
            if(!minHeap.empty()){
                answer = minHeap.top().first;
            }else{
                answer = -1;
            }

            ans[idx] = answer;
        }

        return ans;
    }
};







// Method 2: Binary Search + Ordered Set approach
/*
For LeetCode **1851**, the “binary search approach” usually means:

* Sort intervals by **length**
* Sort queries by **query value**
* For each interval, use binary search / ordered lookup to find unanswered queries inside that interval
* Since intervals are processed from smallest to largest, the first interval that covers a query is its answer

We can implement this cleanly using `set<pair<int,int>>`, where each query is stored as:

```cpp
{queryValue, originalIndex}
```

Then `lower_bound({left, -1})` gives the first unanswered query with value `>= left`.


## Core idea

For a query `q`, we need the smallest interval `[l, r]` such that:

```cpp
l <= q <= r
```

Instead of checking every interval for every query, we sort intervals by size.

So when we process this interval:

```cpp
[l, r]
```

if it covers some unanswered query, that interval must be the **smallest interval** for that query, because all smaller intervals were already processed before it.

That is why once we assign an answer to a query, we erase it from the set.

---

## Why `set` helps

The set keeps all unanswered queries sorted by query value.

This line:

```cpp
auto it = unanswered.lower_bound({left, -1});
```

finds the first query whose value is at least `left`.

Then we keep moving forward while:

```cpp
it->first <= right
```

So we find all unanswered queries inside:

```cpp
[left, right]
```

---

## Example walkthrough

Suppose:

```cpp
intervals = [[1,4], [2,4], [3,6], [4,4]]
queries   = [2, 3, 4, 5]
```

Interval lengths:

```cpp
[1,4] length 4
[2,4] length 3
[3,6] length 4
[4,4] length 1
```

After sorting intervals by length:

```cpp
[4,4] length 1
[2,4] length 3
[1,4] length 4
[3,6] length 4
```

Initial unanswered queries:

```cpp
{2, index 0}
{3, index 1}
{4, index 2}
{5, index 3}
```

Answer array:

```cpp
[-1, -1, -1, -1]
```

---

### Process interval `[4,4]`

Length = `1`

Find first query `>= 4`.

That is:

```cpp
{4, index 2}
```

It is also `<= 4`, so it is inside `[4,4]`.

So:

```cpp
ans[2] = 1
```

Erase query `4`.

Now:

```cpp
ans = [-1, -1, 1, -1]
```

Remaining unanswered:

```cpp
2, 3, 5
```

---

### Process interval `[2,4]`

Length = `3`

Find first query `>= 2`.

That is query `2`.

Query `2` is inside `[2,4]`.

```cpp
ans[0] = 3
```

Erase it.

Next query is `3`, also inside `[2,4]`.

```cpp
ans[1] = 3
```

Erase it.

Next query is `5`, but `5 > 4`, so stop.

Now:

```cpp
ans = [3, 3, 1, -1]
```

Remaining unanswered:

```cpp
5
```

---

### Process interval `[1,4]`

Length = `4`

First unanswered query `>= 1` is `5`.

But:

```cpp
5 > 4
```

So no query is answered.

---

### Process interval `[3,6]`

Length = `4`

First unanswered query `>= 3` is `5`.

And:

```cpp
5 <= 6
```

So query `5` is inside `[3,6]`.

```cpp
ans[3] = 4
```

Final answer:

```cpp
[3, 3, 1, 4]
```

---

## Complexity

Sorting intervals:

```cpp
O(m log m)
```

Inserting queries into set:

```cpp
O(n log n)
```

Each query is erased once:

```cpp
O(n log n)
```

Total:

```cpp
O((m + n) log n + m log m)
```

Space:

```cpp
O(n)
```

This is a nice alternative to the min-heap approach. The key trick is: **process intervals from shortest to longest, and assign answers only to still-unanswered queries.**
*/

// class Solution {
// public:
//     vector<int> minInterval(vector<vector<int>>& intervals, vector<int>& queries) {
//         int n = queries.size();

//         // Sort intervals by length: smaller intervals first
//         sort(intervals.begin(), intervals.end(), [](const vector<int>& a, const vector<int>& b) {
//             int lenA = a[1] - a[0] + 1;
//             int lenB = b[1] - b[0] + 1;
//             return lenA < lenB;
//         });

//         // Store unanswered queries as {queryValue, originalIndex}
//         set<pair<int, int>> unanswered;

//         for (int i = 0; i < n; i++) {
//             unanswered.insert({queries[i], i});
//         }

//         vector<int> ans(n, -1);

//         // Process intervals from smallest length to largest length
//         for (auto& interval : intervals) {
//             int left = interval[0];
//             int right = interval[1];
//             int length = right - left + 1;

//             // Find first unanswered query with queryValue >= left
//             auto it = unanswered.lower_bound({left, -1});

//             // Answer all unanswered queries inside [left, right]
//             while (it != unanswered.end() && it->first <= right) {
//                 int originalIndex = it->second;

//                 ans[originalIndex] = length;

//                 // Erase current query and move to next one
//                 it = unanswered.erase(it);
//             }
//         }

//         return ans;
//     }
// };