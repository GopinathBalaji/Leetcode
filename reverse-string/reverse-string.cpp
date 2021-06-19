class Solution {
public:
    void reverseString(vector<char>& s) {
//         Method 1 (TLE) : Get first value and push then push back to end of vector.
        // char a;
        // vector<char>::iterator it;
        // it = s.begin();
        // int b  = s.size();
        // int c = 0;
        // while(c<=b){
        // a = s[0];
        // s.erase(it);
        // s.push_back(a);
        // }
        
//         Method 2: Using in-built reverse fuction in STL.
        // reverse(s.begin(),s.end());
        
//      Method 3:  Two pointer
        int left = 0;
        int right = s.size()-1;
        
        while(left<right){
            char tmp = s[left];
            s[left++] = s[right];
            s[right--] = tmp;
        }
        
    }
};