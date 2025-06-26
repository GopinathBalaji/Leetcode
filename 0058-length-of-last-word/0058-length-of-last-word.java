// My two pass solution
class Solution {
    public int lengthOfLastWord(String s) {
        int n = s.length()-1;
        for(int i=s.length()-1;i>=0;i--){
            if(s.charAt(i) == ' '){
                n--;
            }else{
                break;
            }
        }

        int count = 0;
        for(int i=n;i>=0;i--){
            if(s.charAt(i) != ' '){
                count++;
            }else{
                break;
            }
        }

        return count;
    }
}

// GPT Single pass solution
// class Solution {
//     public int lengthOfLastWord(String s) {
//         int count = 0;
//         boolean inWord = false;
        
//         // One pass from the end:
//         for (int i = s.length() - 1; i >= 0; i--) {
//             if (s.charAt(i) != ' ') {
//                 // once we hit a letter, we’re in the last word
//                 inWord = true;
//                 count++;
//             } else if (inWord) {
//                 // we’ve counted the last word and now hit a space → done
//                 break;
//             }
//             // if it's a space and inWord==false, we’re still skipping trailing spaces
//         }
        
//         return count;
//     }
// }
