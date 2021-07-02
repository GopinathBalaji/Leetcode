class Solution {
public:
    char findTheDifference(string s, string t) {
//         Method 1: Single HashMap (implemented using vector)
        if(s.length()==0){
            return t[0];
        }
        char a;
       vector<int> dict(26,0);
        for(int i=0;i<s.length();i++){
            dict[s[i] - 'a']++;
        }
        for(int j=0;j<t.length();j++){
            --dict[t[j]-'a'];
            if(dict[t[j]-'a']<0){
                a = t[j];
                break;
            }
        }
        return a;
    } 
};

// Method 2: Bit Manipulation usig XOR
   /*
     char findTheDifference(string s, string t) {
        int i,c,c1,x=0;
        char c3;
        if(s.empty())return t[0];
        for(i=0;i<t.size();i++)
        {
            if(i<s.size())c=s[i];
            else c=0;
            c1=t[i];
            x^=(c^c1);
        }return x;
    }
          
          OR
          
      char findTheDifference(string s, string t) {
		char res = t[s.size()];
		for(int i = 0; i < s.size(); i++){
			res ^= s[i] ^ t[i];
		}
		return res;
	}
     
          OR
      
       char findTheDifference(string &s, string &t) {
        char a = 0;
        for (char &c : s) a ^= c;
        for (char &c : t) a ^= c;
        
        return a;
    }
   */

// Method 3: Sum Difference 
   /*
     char findTheDifference(string s, string t) {
		int sum1 = 0;
		int sum2 = t[s.size()];
		for(int i = 0; i < s.length(); i++){
			sum1 += s[i];
			sum2 += t[i];
		}
		return char(abs(sum2 - sum1));
	}
   */

// Method 4: Sorting solution
   /*
    char findTheDifference(string s, string t) {
		sort(s.begin(), s.end());
		sort(t.begin(), t.end());
		int i = 0;
		while(i < s.size()){
			if(s[i] != t[i]){
				return t[i];
			}
			i++;
		}
		return t[s.size()];
	 }
   */