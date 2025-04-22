class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int[][] memo = new int[triangle.size()][triangle.size()];
        for(int[] row:memo){
            Arrays.fill(row, Integer.MIN_VALUE);
        }
        return dp(0, 0, triangle, memo);
    }

    public int dp(int row, int i, List<List<Integer>> triangle, int[][] memo){
        if(row == triangle.size()){
            return 0;
        }

        if(memo[row][i] != Integer.MIN_VALUE){
            return memo[row][i];
        }

        int current = triangle.get(row).get(i);
        int firsti = dp(row + 1, i, triangle, memo);
        int secondi = dp(row + 1, i + 1, triangle, memo);

        memo[row][i] = current + Math.min(firsti, secondi);

        return memo[row][i];
    }
}