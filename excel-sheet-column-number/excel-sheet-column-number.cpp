class Solution {
public:
    int titleToNumber(string columnTitle) {
        unordered_map<char,int> m1;
        for(int i=0;i<26;i++){
            m1['A'+i] = i+1;
        }
        
        int ans = 0;
        int exp = columnTitle.length()-1;
        for(int j=0;j<columnTitle.length();j++){
            ans += m1[columnTitle[j]] * pow(26,exp);
            exp--;
        }
        return ans;
    }
};

// Method 2: Without using HashMap (character arithematic)
   /*
   int titleToNumber(char * columnTitle){
    long int result = 0;
    int i = 0;
    while (columnTitle[i] != '\0'){
        result = result * 26 + columnTitle[i] - 'A' + 1;
        i++;
    }
    return result;
}
   */