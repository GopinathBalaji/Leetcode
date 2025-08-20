// Greedy approach
/*
Key greedy insight:
Always shoot at the earliest finishing balloon’s end.
Why: this arrow sits as far left as possible while still bursting that balloon, 
maximizing the chance it also hits later balloons. (Classic interval-stabbing / “minimum points to hit intervals”.)

Sorting choice (critical):
Sort balloons by end ascending (not by start).

Why sorting by start fails:
Counterexample: [[1,100], [2,3], [4,5]]. Sorting by start can tempt you to 
place arrows too late; sorting by end ensures minimal arrows.

Algorithm:
After sorting, place your first arrow at end of the first balloon.
Sweep rule:
Keep a variable arrowPos = currentEnd.
Walk the rest:
If next.start ≤ arrowPos, this balloon is already burst → keep going.
Else (no overlap with current arrow), you need a new arrow: increment count and set arrowPos = next.end.
*/
class Solution {
    public int findMinArrowShots(int[][] points) {
        if(points.length == 1){
            return 1;
        }

        // Sort by the end
        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
        int arrows = 1;
        int arrowPos = points[0][1];

        for(int i=1; i<points.length; i++){
            // Use start for the overlap check (closed intervals)
            if(points[i][0] <= arrowPos){
                continue; // current arrow bursts this balloon too
            }else{
                arrowPos = points[i][1];  // place a new arrow at this balloon's end
                arrows++;
            }
        }

        return arrows;
    }
}