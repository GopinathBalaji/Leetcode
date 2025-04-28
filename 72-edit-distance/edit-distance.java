class Solution {
    public int minDistance(String word1, String word2) {
        int[][] memo = new int[word1.length()+1][word2.length()+1];
        for(int[] row: memo){
            Arrays.fill(row, -1);
        }

        return dp(word1, word2, memo, word1.length(), word2.length());
    }

    public int minimum(int a, int b, int c){
        return Math.min(a, Math.min(b, c));
    }

    public int dp(String word1, String word2, int[][] memo, int m, int n){
        if(m == 0){
            return n;
        }
        if(n == 0){
            return m;
        }

        if(memo[m][n] != -1){
            return memo[m][n];
        }

        int cost = (word1.charAt(m-1) == word2.charAt(n-1)) ? 0 : 1;

        memo[m][n] = minimum(dp(word1, word2, memo, m-1, n) + 1,
                            dp(word1, word2, memo, m, n-1) + 1,
                            dp(word1, word2, memo, m-1, n-1) + cost);

        return memo[m][n];
    }
}