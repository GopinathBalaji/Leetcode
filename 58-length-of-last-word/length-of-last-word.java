// Remove trailing spaces, then count characters
class Solution {
    public int lengthOfLastWord(String s) {

        int i;
        for(i=s.length()-1; i>=0; i--){
            if(s.charAt(i) == ' '){
                continue;
            }else{
                break;
            }
        }
        
        int ans = 0;
        for(int j=i; j>=0; j--){
            if(s.charAt(j) != ' '){
                ans++;
            }else{
                break;
            }
        }

        return ans;
    }
}




// More clean code but same approach
// class Solution {
//     public int lengthOfLastWord(String s) {
//         int i = s.length() - 1;

//         // skip trailing spaces
//         while (i >= 0 && s.charAt(i) == ' ') i--;

//         // count last word
//         int ans = 0;
//         while (i >= 0 && s.charAt(i) != ' ') {
//             ans++;
//             i--;
//         }
//         return ans; // returns 0 if there is no word
//     }
// }