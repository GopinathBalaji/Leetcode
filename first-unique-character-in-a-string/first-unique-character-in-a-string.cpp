class Solution {
public:
    int firstUniqChar(string s) {
        if(s.length()==1){
            return 0;
        }
       unordered_map<char,int> m1;
       for(int i=0;i<s.length();i++){
         m1[s[i]]++;  
       }
        for(int j=0;j<s.length();j++){
            if(m1[s[j]]==1){
                return j;
            }
        }
        return -1;
    }
};

// Method 2: Similar but using arrays

   /*
     int firstUniqChar(string s) {
        int n=s.size();
        int arr[26]={0};
        for(int i=0;i<n;i++){
            arr[(int)(s[i]-'a')]++;
        }
        for(int i=0;i<n;i++){
            if(arr[(int)(s[i]-'a')]==1){
                return i;
            }
        }
        return -1;
    }
   */
  