// Bottom-Up DP

class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        if(s1.length() + s2.length() != s3.length()){
            return false;
        }

        boolean[][] memo = new boolean[s1.length() + 1][s2.length() + 1];
        memo[0][0] = true;

        for(int j=1;j<=s2.length();j++){
            memo[0][j] = memo[0][j-1]  && (s2.charAt(j-1) == s3.charAt(j-1));
        }

        for(int i=1;i<=s1.length();i++){
            memo[i][0] = memo[i-1][0] && (s1.charAt(i-1) == s3.charAt(i-1));
        }

        for(int i=1;i<=s1.length();i++){
            for(int j=1;j<=s2.length();j++){
                if(s1.charAt(i-1) == s3.charAt(i+j-1) && memo[i-1][j] == true){
                    memo[i][j] = true;
                }
                if(s2.charAt(j-1) == s3.charAt(i+j-1) && memo[i][j-1] == true){
                    memo[i][j] = true;
                }
            }
        }

        return memo[s1.length()][s2.length()];
    }
}

