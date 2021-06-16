class Solution {
public:
    bool isAnagram(string s, string t) {
        if(s.length() != t.length()){
            return false;
        }
       map<char,int> m1;
       map<char,int> m2;
        for(char i: s){
            m1[i]++;
        }
        for(char j: t){
            m2[j]++;
        }  
//          map<char,int>::iterator it1;
//        map<char,int>::iterator it2;
//         for(int k=0;k<s.length();k++){
//            it1 = m1.find(s[k]);
//             it2 = m2.find(t[k]);
            
//             if(it1->second != it2->second){
//                 return false;
//             }
//         }
        for(auto x : m1){
          if(x.second != m2[x.first]){
           return false;
          }
        }
        return true;
    }
};