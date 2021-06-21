class Solution {
public:
    bool canConstruct(string ransomNote, string magazine) {
     unordered_map<char,int> m1;
        for(int i=0;i<magazine.size();i++){
            m1[magazine[i]]++;
        }
        for(char i:ransomNote){
           if(--m1[i]>=0){
               continue;
           } else{
               return false;
           }
        }
        return true;
    }
};