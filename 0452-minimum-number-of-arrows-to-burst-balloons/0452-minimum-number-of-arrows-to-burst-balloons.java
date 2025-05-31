class Solution {
    public int findMinArrowShots(int[][] points) {
        Arrays.sort(points, (a,b)-> Integer.compare(a[0],b[0]));

        int[] current = points[0];
        List<int[]> ans = new ArrayList<>();

        for(int i=1;i<points.length;i++){
            if(current[1] >= points[i][0]){
                current[1] = Math.min(current[1], points[i][1]);
            }else{
                ans.add(current);
                current = points[i];
            }
        }

        ans.add(current);

        return ans.size();
    }
}