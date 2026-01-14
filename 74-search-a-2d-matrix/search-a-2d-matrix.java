// Method 1: Linear Search to find row + Binary Search Once (Using closed loop intervals) (O(m * log(n)))
/*
What my attempt was doing wrong:

Main issue: your inner binary search can miss the target at the last remaining index.
You use while (start < end) with the closed update rules end = mid - 1 / start = mid + 1.
That pattern needs while (start <= end).
With <, when start == end (one index left), the loop stops without checking it, so you’ll return false even if matrix[row][start] == target.

Concrete fail:
Row [10, 11, 16, 20], target = 20
start=0,end=3 → mid=1 (11<20) → start=2
mid=2 (16<20) → start=3
Now start==end==3; loop exits → returns false although 20 is present.

Other problems (smaller but worth fixing):
If no row satisfies matrix[i][0] <= target <= matrix[i][cols-1], you still binary-search row=0. Safer to detect “no row found” and return false immediately (or binary-search the rows too).
Time-wise, the first for is linear over rows; the intended solution is binary search on rows (log m) and then on columns (log n).
*/

class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int row = 0;

        for(int i=0; i<rows; i++){
            if(matrix[i][0] <= target && matrix[i][cols-1] >= target){
                row = i;
                break;
            }
        }

        int start = 0;
        int end = cols - 1;

        while(start <= end){
            int mid = start + (end - start) / 2;
            if(matrix[row][mid] == target){
                return true;
            }

            if(matrix[row][mid] > target){
                end = mid - 1;
            }else{
                start = mid + 1;
            }
        }

        return false;
    }
}


// Method 1.5: Linear Search to find row + Binary Search Once (Half Open Interval)
/*
Notes:
Uses r = m / l = m+1 (open rules).
After the loop, l is the lower_bound (can be out of bounds because interval is 
open at the end); we check equality.
*/
// class Solution {
//     public boolean searchMatrix(int[][] matrix, int target) {
//         int rows = matrix.length, cols = matrix[0].length;

//         // pick candidate row by scan (unchanged)
//         int row = -1;
//         for (int i = 0; i < rows; i++) {
//             if (matrix[i][0] <= target && target <= matrix[i][cols - 1]) {
//                 row = i;
//                 break;
//             }
//         }
//         if (row == -1) return false; // no possible row

//         // half-open binary search [l, r) to find lower_bound(target)
//         int l = 0, r = cols;
//         while (l < r) {
//             int m = l + (r - l) / 2;
//             if (matrix[row][m] < target) l = m + 1;
//             else r = m;                 // keep m
//         }
//         return l < cols && matrix[row][l] == target;
//     }
// }





// Method 2: Single binary search on a flattened array (using half open interval) best: O(log(m·n))
/*
Idea: Treat the matrix as one sorted array of length m*n.
Map a 1D index mid to (row, col) via:
    row = mid / cols
    col = mid % cols
Then do standard binary search on [0, m*n).

Why it works:
Because matrix[i][0] > matrix[i-1][n-1], the elements read in row-major order are globally sorted. Binary search on that order is valid.
*/
// class Solution {
//     public boolean searchMatrix(int[][] matrix, int target) {
//         int m = matrix.length, n = matrix[0].length;
//         int l = 0, r = m * n;                  // half-open [l, r)

//         while (l < r) {
//             int mid = l + (r - l) / 2;
//             int row = mid / n, col = mid % n;
//             int x = matrix[row][col];

//             if (x < target) l = mid + 1;
//             else r = mid;                      // keep mid
//         }
//         if (l == m * n) return false;
//         int row = l / n, col = l % n;
//         return matrix[row][col] == target;
//     }
// }




// Method 3: Two-phase binary search (row, then column) — O(log m + log n)
/*
Idea: First find the candidate row with a binary search on the first column (or on row ranges). Then binary search inside that row.

Walkthrough (target=16):
Phase 1 on first column [1,10,23]:
    Find first row with first > 16 → that’s row 2 (23). So l=2, row=l-1=1.
    Row 1’s range is [10..20] so 16 might be there.
Phase 2 on row 1: [10,11,16,20]
    lower_bound(16) → index 2, equals → true.

Complexity: O(log m + log n), O(1) space.
When to use: When you want to preserve matrix indexing and keep logic close to “binary search twice.”
*/
// class Solution {
//     public boolean searchMatrix(int[][] a, int target) {
//         int m = a.length, n = a[0].length;

//         // Phase 1: find the greatest r with a[r][0] <= target (lower_bound on first > target)
//         int l = 0, r = m;
//         while (l < r) {
//             int mid = l + (r - l) / 2;
//             if (a[mid][0] > target) r = mid;   // first row whose first > target
//             else l = mid + 1;
//         }
//         int row = l - 1;                       // row with first <= target
//         if (row < 0) return false;            // all firsts > target
//         if (target > a[row][n - 1]) return false; // target beyond row’s max

//         // Phase 2: binary search within that row
//         l = 0; r = n;
//         while (l < r) {
//             int mid = l + (r - l) / 2;
//             if (a[row][mid] < target) l = mid + 1;
//             else r = mid;
//         }
//         return l < n && a[row][l] == target;
//     }
// }




// Method 4: Staircase search from top-right (O(m+n))
/*
Idea: Start at the top-right corner.
    If current x == target, done.
    If x > target, move left (smaller).
    If x < target, move down (larger).

This relies on both: rows are sorted ascending and columns are (implicitly) ascending because each next row starts bigger than the previous row’s end ⇒ column-wise it’s also non-decreasing. (Even if you only knew rows sorted and first-of-next-row > last-of-previous, the top-right walk still works—it’s just not as optimal as pure binary search.)

Walkthrough (target=16):
(0,3)=7 < 16 → down to (1,3)=20
20 > 16 → left to (1,2)=16 → found.

Complexity: O(m + n) time, O(1) space.
When to use: Extremely clean, good when m and n are small-ish or when you’re solving LC 240-like problems (where each row & column is sorted but without the “global” order).
*/
// class Solution {
//     public boolean searchMatrix(int[][] a, int target) {
//         int m = a.length, n = a[0].length;
//         int i = 0, j = n - 1; // top-right
//         while (i < m && j >= 0) {
//             int x = a[i][j];
//             if (x == target) return true;
//             if (x > target) j--;  // move left
//             else i++;             // move down
//         }
//         return false;
//     }
// }
