// Method 1: Sorting and then merging
/*
1. **First sort the intervals by start time.**
   Once sorted, any interval that can overlap with another relevant one will appear next to it.

2. After sorting, keep a result list `ans`.

3. Put the **first interval** into `ans`.

4. For every next interval, compare it with the **last interval already in `ans`**.

5. Let:

   * last merged interval be `[a, b]`
   * current interval be `[c, d]`

   Then there are only 2 cases:

   **No overlap**

   * if `c > b`
   * push current interval into `ans`

   **Overlap**

   * if `c <= b`
   * merge them by updating the end:
     `b = max(b, d)`

6. Important idea:
   When intervals overlap, you do **not** add a new interval.
   You just extend the end of the last merged one.

7. Why sorting helps:

   * after sorting by start, if current interval overlaps with the last merged interval, it belongs to the same merged block
   * you never need to look far back, only at the last merged interval

8. Edge case:

   * if `intervals.size() <= 1`, just return it
*/
class Solution {
public:
    vector<vector<int>> merge(vector<vector<int>>& intervals) {
        std::sort(intervals.begin(), intervals.end(), [](const vector<int>& a, const vector<int>& b){
            return a[0] < b[0];
        });

        vector<vector<int>> ans;
        ans.push_back(intervals[0]);

        for(int i=1; i<intervals.size(); i++){
            if(intervals[i][0] > ans.back()[1]){
                ans.push_back(intervals[i]);
            }else{
                ans.back()[1] = std::max(ans.back()[1], intervals[i][1]);
            }
        }

        return ans;
    }
};
