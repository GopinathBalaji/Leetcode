class Solution {
    public int lengthOfLongestSubstring(String s) {
        
        int start = 0;
        int length = 0;
        HashSet<Character> set = new HashSet<>();

        for(int i=0; i<s.length(); i++){
            while(set.contains(s.charAt(i))){
                set.remove(s.charAt(start));
                start++;
            }

            set.add(s.charAt(i));
            length = Math.max(length, i-start+1);
        }
        return length;
    }
}