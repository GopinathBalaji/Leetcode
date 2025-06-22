// Top Down DP
/*
How it works (top-down memoization):

State:
canBreak(s, dict, i, memo) asks “can the substring s[i..] be segmented into dictionary words?”

Choices:
At index i, try every word w in wordDict. If s starts with w at i, you “consume” it and recurse on i + w.length().

Base case:
When i == s.length(), you’ve successfully segmented the entire string.

Memoization:
The first time you compute canBreak(..., i, ...), you store the Boolean result in memo[i]. Any subsequent call for the same i returns in O(1), avoiding exponential re‐exploration.
*/
class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
           Boolean[] memo = new Boolean[s.length()];
           return dp(s, wordDict, memo, 0);
    }

    public boolean dp(String s, List<String> wordDict, Boolean[] memo, int start){

        // Base case: reached the end successfully
        if(start == s.length()){
            return true;
        }

        // If we’ve already solved dp(start), return it
        if(memo[start] != null){
            return memo[start];
        }

        // Try every word in the dictionary
        for(String w : wordDict){
            int len = w.length();

            if(start + len <= s.length() && s.startsWith(w, start)){
                // …recurse on the suffix after w
                if(dp(s, wordDict, memo, start + len)){
                    memo[start] = true;
                    return true;
                }
            }
        }

        // None of the choices worked
        memo[start] = false;
        return false;
    }
}


// /////////////////
// Bottom Up DP
/*
DP State
Define

dp[i]=[the substring s[0..i) can be segmented into words in the dictionary].
Initialization

dp[0] = true: the empty prefix (length 0) is trivially segmented.

Transition
For each prefix length i from 1 to n, we ask:

“Is there a split point j (0 ≤ j < i) such that:

dp[j] is true (the first j characters can be segmented), and

the substring s[j..i) is exactly a dictionary word?”

If so, we set dp[i] = true. Otherwise it stays false.

Answer
After filling in all dp[1]…dp[n], dp[n] tells us if the entire string s[0..n) can be segmented.
*/

// class Solution {
//     public boolean wordBreak(String s, List<String> wordDict) {
//         int n = s.length();

//         // Convert list to a HashSet for O(1) lookups
//         Set<String> dict = new HashSet<>(wordDict);

//         // dp[i] = true if s[0..i) can be segmented into dict words
//         boolean[] dp = new boolean[n+1];
//         dp[0] = true; // empty string is “segmented” by definition

//         // Build up from shorter prefixes to the full string
//         for(int i=0;i<=n;i++){
//             // Try every possible split point j: [0..j) and [j..i)
//             for(int j=0;j<i;j++){
//                 // If prefix s[0..j) is valid and the suffix s[j..i) is in the dict
//                 if(dp[j] && dict.contains(s.substring(j, i))){
//                     di[i] = true;
//                     break;  // no need to try other j’s for this i
//                 }
//             }
//         }

//         return dp[n];
//     }
// }