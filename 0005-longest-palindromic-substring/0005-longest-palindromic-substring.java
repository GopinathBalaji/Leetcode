class Solution {

    int start = 0;
    int maxLength = 1;
    Boolean[][] memo;

    public String longestPalindrome(String s) {
        int n = s.length();
        memo = new Boolean[n][n];

        for (int i = 0; i < n; ++i) {
            for (int j = i; j < n; ++j) {
                dp(s, i, j);
            }
        }

        return s.substring(start, start + maxLength);
    }

    public boolean dp(String s, int i, int j){
        if(i > j){
            return true;
        }
        if(memo[i][j] != null){
            return memo[i][j];
        }

        if(s.charAt(i) == s.charAt(j)){
            boolean innerPalindrome = dp(s, i+1, j-1);
            memo[i][j] = innerPalindrome;

            if(innerPalindrome && (j-i+1) > maxLength){
                start = i;
                maxLength = j-i+1; 
            }
        } else{
            memo[i][j] = false;
        }

        return memo[i][j];
    }
}