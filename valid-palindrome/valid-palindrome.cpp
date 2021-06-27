class Solution {
public:
    bool isPalindrome(string s) {
        string ans;
        for(int i=0;i<s.length();i++){
            if((s[i]>='A' && s[i]<='Z') || (s[i]>='a' && s[i]<='z') || (s[i]>='0' && s[i]<='9')){
                if(s[i]>='A' && s[i]<='Z'){
                    s[i] = tolower(s[i]);
                }
          ans.push_back(s[i]);     
            } 
        }
       int a = 0;
       int b = ans.length()-1;
        while(a<b){
            if(ans[a]!=ans[b]){
                return false;
            }
            a++;
            b--;
        }
        return true;
    }
};

// Method 2: Without extra loop
   /*
   bool isPalindrome(string s) {
        int lg = s.size();
        int l = 0, r = lg - 1;
        while (l <= r) {
            if (!isAlphanum(s[l])) {
                l++;
                continue;
            }
            if (!isAlphanum(s[r])) {
                r--;
                continue;
            }
            if (toLower(s[l]) != toLower(s[r])) {
                return false;
            }
            l++;
            r--;
        }
        return true;
    }
    
    bool isAlphanum(char ch) {
        return (ch >= 'a' && ch <= 'z') ||
               (ch >= 'A' && ch <= 'Z') ||
               (ch >= '0' && ch <= '9');
    }
    
    char toLower(char ch) {
        if (ch >= 'A' && ch <= 'Z') {
            return (ch - 'A') + 'a';
        }
        return ch;
    }
   */