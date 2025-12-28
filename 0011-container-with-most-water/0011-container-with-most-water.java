// Method 1: Two-pointer Greedy approach
/*
# Key idea:

* Area = `(right-left) * min(height[left], height[right])`
* The width always shrinks as pointers move, so to possibly get a bigger area you must try to increase the limiting height.
* The limiting height is the **smaller** side; moving the taller side cannot increase `min(...)`, so it can’t beat the current best for any smaller width.
* Therefore you move the pointer on the smaller height (`left++` if left is smaller, else `right--`).

Minor notes (not correctness issues):

* `minSide` and `len` don’t need to be declared outside the loop; you can compute inside.
* Potential integer overflow is not an issue here for LeetCode constraints (area fits in `int`).
*/
class Solution {
    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;

        int maxWater = 0;
        int minSide = 0;
        int len = 0;

        while(left < right){
            minSide = Math.min(height[left], height[right]);
            len = right - left;
            maxWater = Math.max(maxWater, minSide * len);

            if(height[left] < height[right]){
                left++;
            }else{
                right--;
            }
        }

        return maxWater;
    }
}