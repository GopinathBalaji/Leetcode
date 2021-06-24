class Solution {
public:
    bool isValid(string s) {
        if(s.length()==1){
            return false;
        }
        int b = 0;
        stack<char> s1;
        
        while(b<s.length()){
            if(s[b]==')' && !s1.empty() && s1.top()=='('){
                s1.pop();
                b++;
            }
           else if(s[b]=='}' && !s1.empty() && s1.top()=='{'){
                s1.pop();
               b++;
            }
           else if(s[b]==']' && !s1.empty() && s1.top()=='['){
                s1.pop();
               b++;
            }else{
            s1.push(s[b]);
               b++;
           }
        }
        if(s1.empty()){
            return true;
        }
        return false;
    }
};