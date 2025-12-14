// Method 1: Sorting
/*
What my attempt got wrong:

Wrong merging condition.
You’re checking if(prev[1] <= intervals[i][1] && prev[1] >= intervals[i][0]), which only works for partial overlaps. It misses cases where:

The new interval is fully inside prev.

Or when prev fully covers intervals[i].
The correct overlap condition is:

if (intervals[i][0] <= prev[1])


Using List<List<Integer>> for intervals.
Overcomplicates things. It’s simpler to use a List<int[]> and store intervals directly as arrays.

Not adding the last interval properly.
Your code sometimes forgets to add the last merged interval into the result.

Returning wrong type.
LeetCode expects int[][], but you’re returning List<List<Integer>> converted incorrectly.
*/
class Solution {
    public int[][] merge(int[][] intervals) {
        if (intervals.length <= 1) return intervals;

        // Step 1: Sort intervals by starting point
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // Step 2: Use a list to store merged intervals
        List<int[]> merged = new ArrayList<>();
        int[] prev = intervals[0]; // start with the first interval

        // Step 3: Iterate through intervals
        for (int i = 1; i < intervals.length; i++) {
            int[] curr = intervals[i];

            if (curr[0] <= prev[1]) {
                // Overlap → merge
                prev[1] = Math.max(prev[1], curr[1]);
            } else {
                // No overlap → add previous to result
                merged.add(prev);
                prev = curr;
            }
        }

        // Add the last interval
        merged.add(prev);

        // Step 4: Convert list to int[][]
        return merged.toArray(new int[merged.size()][]);
    }
}




// Method 1.5: My approach similar to above
/*
# WHAT I GOT WRONG:

There *is* a real bug here, and it comes from this line:

```java
Arrays.sort(intervals, Comparator.comparingInt(a -> a[1]));
```

You’re sorting by **end time** (`a[1]`), but the classic merge-intervals algorithm assumes the intervals are sorted by **start time** (`a[0]`). That difference can absolutely break correctness.

---

## Why sorting by end is wrong

Your merging logic is:

```java
int[] arr = intervals[0];

for (int i = 1; i < n; i++) {
    if (intervals[i][0] <= arr[1]) {      // overlap?
        arr[0] = Math.min(arr[0], intervals[i][0]);
        arr[1] = Math.max(arr[1], intervals[i][1]);
    } else {
        ans.add(arr);
        arr = intervals[i];
    }

    if (i == n - 1) {
        ans.add(arr);
    }
}
```

This logic assumes that when you encounter a new interval, its **start** is ≥ the start of all previously seen intervals. That only holds if you sorted by start.

If you sort by end, you can see a **long interval that starts earlier** *after* several short intervals. Then your current `arr` (built from short intervals) gets treated as a separate merged block, even though the long interval overlaps everything and should absorb them.

---

## Concrete failing example

Take:

```java
int[][] intervals = {
    {1, 10},
    {2, 3},
    {4, 5},
    {6, 7}
};
```

Correct merged result should be:

```text
[1, 10]
```

Because `[1,10]` overlaps all the others.

### Step 1: Sort by end (`a[1]`)

Ends: 10, 3, 5, 7 → sorted by end:

```text
[2, 3], [4, 5], [6, 7], [1, 10]
```

### Step 2: Run your loop

* Start: `arr = [2, 3]`, `ans = []`

**i = 1**, `intervals[1] = [4, 5]`

* Check overlap: `4 <= arr[1] (3)`? → **no**
* So: `ans.add([2, 3])`, `arr = [4,5]`
* Not last yet, nothing else.

**i = 2**, `intervals[2] = [6, 7]`

* Check overlap: `6 <= arr[1] (5)`? → **no**
* So: `ans.add([4, 5])`, `arr = [6, 7]`

**i = 3**, `intervals[3] = [1, 10]`

* Check overlap: `1 <= arr[1] (7)`? → **yes**
* Merge:
  `arr[0] = min(6,1) = 1`,
  `arr[1] = max(7,10) = 10` → `arr = [1,10]`
* This is last index, so `ans.add([1,10])`.

Final `ans`:

```text
[2, 3], [4, 5], [1, 10]
```

That’s wrong; all those small intervals should have been merged into `[1,10]`.

---

## The fix

Just change the sort to use the **start**:

```java
Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
```

Everything else in your merge logic is fine (aside from tiny style things like the special `n == 1` early return, which is unnecessary but not incorrect).

So the **only real logical bug** is: you sorted by end instead of start, which breaks certain cases where a long interval that begins earlier appears later in the end-sorted order.
*/
// class Solution {
//     public int[][] merge(int[][] intervals) {
//         int n = intervals.length;

//         if(n == 1){
//             return intervals;
//         }
        

//         Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
//         List<int[]> ans = new ArrayList<>();

//         int[] arr = intervals[0];

//         for(int i=1; i<n; i++){
//             if(intervals[i][0] <= arr[1]){
//                 arr[0] = Math.min(arr[0], intervals[i][0]);
//                 arr[1] = Math.max(arr[1], intervals[i][1]);
//             }else{
//                 ans.add(arr);
//                 arr = intervals[i];
//             }

//             if(i == n-1){
//                 ans.add(arr);
//             }
//         }

//         return ans.toArray(new int[ans.size()][]);
//     }
// }