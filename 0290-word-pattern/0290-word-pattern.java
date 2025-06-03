class Solution {
    public boolean wordPattern(String pattern, String s) {
        String[] words = s.split(" ");
        char[] letters = pattern.toCharArray();

        if(letters.length != words.length){
            return false;
        }

        Map<Character, String> mapLW = new HashMap<>();
        Map<String, Character> mapWL = new HashMap<>();

        for(int i=0;i<letters.length;i++){
            if(mapLW.containsKey(letters[i])){
                if(!mapLW.get(letters[i]).equals(words[i])){
                    return false;
                }
            }else{
                mapLW.put(letters[i], words[i]);
            }


            if(mapWL.containsKey(words[i])){
                if(mapWL.get(words[i]) != letters[i]){
                    return false;
                }
            }else{
                mapWL.put(words[i], letters[i]);
            }           
        }

        return true;
    }
}