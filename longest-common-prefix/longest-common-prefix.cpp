class Solution {
public:
    string longestCommonPrefix(vector<string>& strs) {
//         Method 1: Vertical Method
        if(strs.size()==1){
            return strs[0];
        }
        string res="";
        int count = 0;
        for(int i=0;i<strs[0].length();i++){
           char ch = strs[0][i];
            for(int j=0;j<strs.size();j++){
                if(ch == strs[j][i]){
                    count++;
                }
            }
            if(count==strs.size()){
                res+=ch;
            }else{
                return res;
            }
          count = 0;
        }
        return res;
    }
};

// Method 2: Divide and Conquer

   /*
    string longestCommonPrefix(vector<string>& strs) {
    int size = strs.size();
    if (size==0)
        return "";
    if (size == 1)
        return strs[0];
    vector<string> v1(strs.begin(), strs.begin()+size/2);
    vector<string> v2(strs.begin()+size/2, strs.end());
    string s1 = longestCommonPrefix(v1);
    string s2 = longestCommonPrefix(v2);
    return longestCommon(s1, s2);
}

string longestCommon(string& s1, string& s2) {
    int i = 0;
    while (i<min(s1.size(), s2.size()) && s1[i] == s2[i])
        i++;
    return s1.substr(0, i);
}
   */

// Method 3: Binary Search

   /*
   public String longestCommonPrefix(String[] strs) {
    if (strs == null || strs.length == 0)
        return "";
    int minLen = Integer.MAX_VALUE;
    for (String str : strs)
        minLen = Math.min(minLen, str.length());
    int low = 1;
    int high = minLen;
    while (low <= high) {
        int middle = (low + high) / 2;
        if (isCommonPrefix(strs, middle))
            low = middle + 1;
        else
            high = middle - 1;
    }
    return strs[0].substring(0, (low + high) / 2);
}

private boolean isCommonPrefix(String[] strs, int len){
    String str1 = strs[0].substring(0,len);
    for (int i = 1; i < strs.length; i++)
        if (!strs[i].startsWith(str1))
            return false;
    return true;
}
   */