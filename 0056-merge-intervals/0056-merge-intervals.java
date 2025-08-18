// Only necessary check for each interval is if the first value of current
// smaller than the last value of prev.
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