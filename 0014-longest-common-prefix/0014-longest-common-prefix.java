class Solution {
    public String longestCommonPrefix(String[] strs) {
        if(strs.length == 0 || strs == null){
            return "";
        }

        int shortest = Integer.MAX_VALUE;
        int indexOfShortest = 0;
        for(int i=0;i<strs.length;i++){
            if(strs[i].length() < shortest){
                shortest = strs[i].length();
                indexOfShortest = i;
            }
        } 

        String ans = "";

        for(int i=0;i<shortest;i++){
            char currentChar = strs[indexOfShortest].charAt(i);
            for(String s : strs){
                if(s.charAt(i) != currentChar){
                    return ans;
                }
            }
            
            ans = ans + currentChar;
        }

        return ans;
    }
}

// More efficient GPT solution: Binary Searching the Prefix
/*
Binary‐search on prefix length:

You can search the answer length L in [0…m] by checking “is there a common prefix of length mid?” in O(n) time each (just comparing substr(0,mid) across all strings).

That gives O(n × log m) character‐comparisons instead of O(n × m).
*/

// class Solution {
//     public String longestCommonPrefix(String[] strs) {
//         if (strs == null || strs.length == 0) return "";
//         // 1) Find the minimum length among all strings
//         int minLen = Integer.MAX_VALUE;
//         for (String s : strs) {
//             minLen = Math.min(minLen, s.length());
//         }
        
//         // 2) Binary search over [0..minLen]
//         int low = 0, high = minLen;
//         while (low <= high) {
//             int mid = (low + high) / 2;
//             if (isCommonPrefix(strs, mid)) {
//                 low = mid + 1;   // try a longer prefix
//             } else {
//                 high = mid - 1;  // try a shorter prefix
//             }
//         }
        
//         // high is now the maximum length that worked
//         return strs[0].substring(0, high);
//     }
    
//     // Check if all strings share the same prefix of length len
//     private boolean isCommonPrefix(String[] strs, int len) {
//         String prefix = strs[0].substring(0, len);
//         for (int i = 1; i < strs.length; i++) {
//             if (!strs[i].startsWith(prefix)) {
//                 return false;
//             }
//         }
//         return true;
//     }
// }
