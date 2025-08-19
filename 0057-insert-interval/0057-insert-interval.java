// Method 1
// First add all the intervals with no overlap. The find the new modified overlapped interval.
// Finally add all the intervals with no overlap again.
// The condition for the new modified overlapped interval should be all intervals that have
// beginnings lesser than the new interval.
class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> ans = new ArrayList<>();
        int n = intervals.length;
        int i = 0;

        while(i<n && intervals[i][1] < newInterval[0]){
            ans.add(intervals[i++]);
        }

        // Merge all overlaps into newInterval
        // based on start of current vs end of new
        while(i<n && intervals[i][0] <= newInterval[1]){
            newInterval[0] = Math.min(intervals[i][0], newInterval[0]);
            newInterval[1] = Math.max(intervals[i][1], newInterval[1]);
            i++;
        }

        ans.add(newInterval);

        while(i < n){
            ans.add(intervals[i++]);
        }

        return ans.toArray(new int[ans.size()][]);
    }
}

// Method 2: Using binary search (same asymptotics, fewer comparisons)
/*
If intervals are already sorted and non-overlapping, you don’t need to linearly scan the whole array 
to find where the new interval fits. You can binary search the first possible overlap and the last 
possible overlap, then splice.

Idea:
Find l = first index with intervals[i][1] >= newStart (first that could overlap).
Find r = last index with intervals[i][0] <= newEnd (last that could overlap).
If l > r → no overlap: insert newInterval between the left and right slices.
Else merge to [min(newStart, intervals[l][0]), max(newEnd, intervals[r][1])], then append the right slice.

Time: O(log n) to locate + O(n) to build output (unavoidable copying)
Space: O(n) for the returned array
*/

// class Solution {
//     public int[][] insert(int[][] intervals, int[] newInterval) {
//         int n = intervals.length;
//         if (n == 0) return new int[][] { newInterval };

//         int s = newInterval[0], e = newInterval[1];

//         int l = lowerBoundEndGE(intervals, s);   // first i with end >= s
//         int r = upperBoundStartLE(intervals, e); // last  i with start <= e

//         int[][] res = new int[n + 1][2];
//         int idx = 0;

//         // left slice (strictly before overlap)
//         for (int i = 0; i < Math.min(l, n); i++) res[idx++] = intervals[i];

//         if (l > r) {
//             // no overlap -> just insert newInterval
//             res[idx++] = new int[] { s, e };
//             for (int i = l; i < n; i++) res[idx++] = intervals[i];
//         } else {
//             // merge overlapped window
//             int mergedStart = Math.min(s, intervals[l][0]);
//             int mergedEnd   = Math.max(e, intervals[r][1]);
//             res[idx++] = new int[] { mergedStart, mergedEnd };
//             for (int i = r + 1; i < n; i++) res[idx++] = intervals[i];
//         }

//         return Arrays.copyOf(res, idx);
//     }

//     // first index i with intervals[i][1] >= x
//     private int lowerBoundEndGE(int[][] a, int x) {
//         int lo = 0, hi = a.length;
//         while (lo < hi) {
//             int mid = (lo + hi) >>> 1;
//             if (a[mid][1] < x) lo = mid + 1;
//             else hi = mid;
//         }
//         return lo;
//     }

//     // last index i with intervals[i][0] <= x
//     private int upperBoundStartLE(int[][] a, int x) {
//         int lo = 0, hi = a.length;
//         while (lo < hi) {
//             int mid = (lo + hi) >>> 1;
//             if (a[mid][0] <= x) lo = mid + 1;
//             else hi = mid;
//         }
//         return lo - 1;
//     }
// }



// Method 3: In-place style (minimize allocations)
/*
If you want to sound systems-y: preallocate a result array of size n+1 and fill it in three 
writes (left slice, merged/new, right slice). The code above already does this (no ArrayList growth). 
It’s not asymptotically faster, but it reduces overhead and GC pressure.
*/



// Method 4: If there are many inserts over time: use a data structure
/*
If the interviewer asks “what if we need to insert intervals repeatedly,” switch to a balanced tree 
keyed by start (e.g., TreeMap<Integer,int[]>). Each insert:

1) find the interval with the greatest start ≤ newStart (floorEntry) and check overlap,

2) merge forward with higherEntry while overlapping,

3) update the map.

Amortized: O(k log n) where k is number of overlapped intervals (often small). This avoids 
rebuilding arrays on every insert.
*/

// class IntervalSet {
//     private final TreeMap<Integer, int[]> map = new TreeMap<>();

//     public void add(int[] x) {
//         int s = x[0], e = x[1];

//         // merge with predecessor if overlaps
//         Map.Entry<Integer,int[]> prev = map.floorEntry(s);
//         if (prev != null && prev.getValue()[1] >= s) {
//             s = Math.min(s, prev.getKey());
//             e = Math.max(e, prev.getValue()[1]);
//             map.remove(prev.getKey());
//         }

//         // merge with following intervals while overlapping
//         Map.Entry<Integer,int[]> cur = map.ceilingEntry(s);
//         while (cur != null && cur.getKey() <= e) {
//             e = Math.max(e, cur.getValue()[1]);
//             map.remove(cur.getKey());
//             cur = map.ceilingEntry(s);
//         }

//         map.put(s, new int[]{s, e});
//     }

//     public int[][] toArray() {
//         int[][] out = new int[map.size()][2];
//         int i = 0;
//         for (int[] v : map.values()) out[i++] = v;
//         return out;
//     }
// }