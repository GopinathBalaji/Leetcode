// Method 1: Without Sorting using HashMap
/*
*/
class Solution {
    public boolean isAnagram(String s, String t) {

        if (s.length() != t.length()) return false;

        Map<Character, Integer> mapS = new HashMap<>();
        Map<Character, Integer> mapT = new HashMap<>();

        for(char c: s.toCharArray()){
            mapS.put(c, mapS.getOrDefault(c, 0) + 1);
        }

        for(char c: t.toCharArray()){
            mapT.put(c, mapT.getOrDefault(c, 0) + 1);
        }

        for(char c: s.toCharArray()){
            int valS = mapS.get(c);

            if(!mapT.containsKey(c) || mapT.get(c) != valS){
                return false;
            }
        }

        return true;
    }
}





// Method 1.5: Similar using inbuilt function .equals() in HashMap
/*
*/
// class Solution {
//     public boolean isAnagram(String s, String t) {

//         if(s.length() != t.length()){
//             return false;
//         }

//         Map<Character, Integer> mapS = new HashMap<>();
//         Map<Character, Integer> mapT = new HashMap<>();

//         for(char c : s.toCharArray()){
//             mapS.put(c, mapS.getOrDefault(c , 0) + 1);
//         }

//         for(char c : t.toCharArray()){
//             mapT.put(c, mapT.getOrDefault(c , 0) + 1);
//         }        

//         return mapS.equals(mapT);
//     }
// }