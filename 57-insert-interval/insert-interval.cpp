// Method 1: My approach to Insertion using 3 seperate steps
/*
 First add all the intervals with no overlap. The find the new modified overlapped interval.
Finally add all the intervals with no overlap again.
The condition for the new modified overlapped interval should be all intervals that have
beginnings lesser than the new interval.
*/
class Solution {
public:
    vector<vector<int>> insert(vector<vector<int>>& intervals, vector<int>& newInterval) {
        vector<vector<int>> ans;
        int i = 0;

        for(; i<intervals.size(); i++){
            if(intervals[i][1] < newInterval[0]){
                ans.push_back(intervals[i]);
            }else{
                break;
            }
        }

        for(; i<intervals.size(); i++){
            if(newInterval[1] >= intervals[i][0]){
                newInterval[0] = std::min(intervals[i][0], newInterval[0]);
                newInterval[1] = std::max(intervals[i][1], newInterval[1]);
            }else{
                break;
            }
        }

        ans.push_back(newInterval);

        for(; i<intervals.size(); i++){
            ans.push_back(intervals[i]);
        }

        return ans;
    }
};






// Method 1.5: More cleaner approach to insertion using 3 seperate steps
/*
*/
// class Solution {
// public:
//     vector<vector<int>> insert(vector<vector<int>>& intervals, vector<int>& newInterval) {
//         vector<vector<int>> ans;
//         int i = 0;
//         int n = intervals.size();

//         // add all intervals before newInterval
//         while (i < n && intervals[i][1] < newInterval[0]) {
//             ans.push_back(intervals[i]);
//             i++;
//         }

//         // merge all overlapping intervals
//         while (i < n && intervals[i][0] <= newInterval[1]) {
//             newInterval[0] = min(newInterval[0], intervals[i][0]);
//             newInterval[1] = max(newInterval[1], intervals[i][1]);
//             i++;
//         }

//         // add merged interval
//         ans.push_back(newInterval);

//         // add remaining intervals
//         while (i < n) {
//             ans.push_back(intervals[i]);
//             i++;
//         }

//         return ans;
//     }
// };






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
// public:
//     vector<vector<int>> insert(vector<vector<int>>& intervals, vector<int>& newInterval) {
//         int n = intervals.size();
//         if (n == 0) return {newInterval};

//         int s = newInterval[0];
//         int e = newInterval[1];

//         int l = lowerBoundEndGE(intervals, s);   // first index with end >= s
//         int r = upperBoundStartLE(intervals, e); // last index with start <= e

//         vector<vector<int>> res;
//         res.reserve(n + 1);

//         // left part: intervals completely before overlap
//         for (int i = 0; i < min(l, n); i++) {
//             res.push_back(intervals[i]);
//         }

//         if (l > r) {
//             // no overlap, just insert new interval
//             res.push_back({s, e});

//             for (int i = l; i < n; i++) {
//                 res.push_back(intervals[i]);
//             }
//         } else {
//             // merge the overlapping window [l ... r]
//             int mergedStart = min(s, intervals[l][0]);
//             int mergedEnd = max(e, intervals[r][1]);

//             res.push_back({mergedStart, mergedEnd});

//             for (int i = r + 1; i < n; i++) {
//                 res.push_back(intervals[i]);
//             }
//         }

//         return res;
//     }

// private:
//     // first index i such that intervals[i][1] >= x
//     int lowerBoundEndGE(const vector<vector<int>>& intervals, int x) {
//         int lo = 0, hi = intervals.size();

//         while (lo < hi) {
//             int mid = lo + (hi - lo) / 2;

//             if (intervals[mid][1] < x) {
//                 lo = mid + 1;
//             } else {
//                 hi = mid;
//             }
//         }

//         return lo;
//     }

//     // last index i such that intervals[i][0] <= x
//     int upperBoundStartLE(const vector<vector<int>>& intervals, int x) {
//         int lo = 0, hi = intervals.size();

//         while (lo < hi) {
//             int mid = lo + (hi - lo) / 2;

//             if (intervals[mid][0] <= x) {
//                 lo = mid + 1;
//             } else {
//                 hi = mid;
//             }
//         }

//         return lo - 1;
//     }
// };
