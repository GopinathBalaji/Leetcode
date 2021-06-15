class Solution {
public:
    bool isIsomorphic(string s, string t) {
        unordered_map<char,char> m1;
        unordered_map<char,char> m2;
        
      for(int i=0;i<s.size();i++){
          char a = s[i];
          char b = t[i];
          if(m1.find(s[i])==m1.end() && m2.find(t[i])==m2.end()){
              m1.insert(make_pair(a,b));
              m2.insert(make_pair(b,a));
          }
          
          else if(!(m1[a]==b && m2[b]==a)){
              return false;
          }
      }
        return true;
    }
};