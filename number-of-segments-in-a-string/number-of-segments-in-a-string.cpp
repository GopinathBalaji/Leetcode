class Solution {
public:
    int countSegments(string s) {
//         Method 1: In Place
        if(s.length()==0){
            return 0;
        }
        int count=0;
       for(int i=0;i<s.length();i++){
          if((i==0 || s[i-1]==' ') && s[i]!=' '){
              count++;
          } 
       } 
        return count;
    }
};

// Method 2: C++ Built-in Stringstream
   /*
      int countSegments(string s) {
        stringstream ss(s);
        int word = 0;
        
		//stringstream>> operator will output strings separated by white spaces until exhausting all strings.
        while(ss >> s)
            word++;
        
        return word;
    }
   */