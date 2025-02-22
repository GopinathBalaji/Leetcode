class Solution {
    public int maxArea(int[] height) {
        int start = 0;
        int end = height.length - 1;
        int area = 0;

        while(start < end){
            area = Math.max(area, (end - start) * Math.min(height[start],height[end]));

            if(height[start] < height[end]){
                start++;
            }
            else{
                end--;
            }
        }

        return area;
    }
}