class Solution {
public:
    int lengthOfLastWord(string s) {
        
        int a = s.length()-1;
        while(a>=0 && s[a]==' '){
            a--;
        }
        int count=0;
        while(a>=0 && s[a]!=' '){
            a--;
            count++;
        }
        return count;
    }
    
};
