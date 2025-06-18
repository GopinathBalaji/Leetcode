class Solution {
    public int countCharacters(String[] words, String chars) {
        HashMap<Character, Integer> map = new HashMap<>();

        for(char c : chars.toCharArray()){
            map.put(c, map.getOrDefault(c, 0) + 1);
        }

        int ans = 0;
        

        for(String s : words){
            HashMap<Character, Integer> map2 = new HashMap<>();
            for(char c : s.toCharArray()){
                map2.put(c, map2.getOrDefault(c, 0) + 1);
            }

            int flag = 1;
            for(Character key : map2.keySet()){
                if(!map.containsKey(key) || map.get(key) < map2.get(key)){
                    flag = 0;
                    break;
                }
            }

            if(flag == 1){
                ans += s.length();
            }
        }

        return ans;
    }
}


// Similar approach but more efficient because of using Arrays
// class Solution {
//     public int countCharacters(String[] words, String chars) {
//         // 1) build the “available” counts array
//         int[] avail = new int[26];
//         for (char c : chars.toCharArray()) {
//             avail[c - 'a']++;
//         }

//         int ans = 0;

//         // 2) for each word, check if it can be formed
//         for (String w : words) {
//             // a) make a fresh copy of avail
//             int[] need = Arrays.copyOf(avail, 26);

//             boolean canForm = true;
//             for (char c : w.toCharArray()) {
//                 int idx = c - 'a';
//                 if (--need[idx] < 0) {    // used up more than available
//                     canForm = false;
//                     break;
//                 }
//             }

//             if (canForm) {
//                 ans += w.length();
//             }
//         }

//         return ans;
//     }
// }