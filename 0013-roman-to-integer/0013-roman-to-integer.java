// My answer with extra space allocation for HashMap
class Solution {
    public int romanToInt(String s) {
        Map<Character, Integer> map = Map.of(
            'I', 1,
            'V', 5,
            'X', 10,
            'L', 50,
            'C', 100,
            'D', 500,
            'M', 1000
        );

        int ans = 0;
        int prev = 0;

        for(int i=s.length()-1; i>=0; i--){
            int val = map.get(s.charAt(i));
            if(val < prev){
                ans -= val;
                prev = val;
            }else{
                ans += val;
                prev = val;
            }
        }
        
        return ans;
    }
}




// Same logic but using "switch" to avoid any allocation
// class Solution {
//     public int romanToInt(String s) {
//         int ans = 0, prev = 0; // prev = value to the right
//         for (int i = s.length() - 1; i >= 0; i--) {
//             int v = val(s.charAt(i));
//             ans += (v < prev) ? -v : v;
//             prev = v;
//         }
//         return ans;
//     }

//     private int val(char c) {
//         switch (c) {
//             case 'I': return 1;
//             case 'V': return 5;
//             case 'X': return 10;
//             case 'L': return 50;
//             case 'C': return 100;
//             case 'D': return 500;
//             case 'M': return 1000;
//             default:  return 0; 
//         }
//     }
// }