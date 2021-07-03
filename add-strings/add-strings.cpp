class Solution {
public:
    string addStrings(string num1, string num2) {
//         Method 1: Digit by Digit additon
     
        int len1 = num1.length()-1;
        int len2 = num2.length()-1;
        int carry = 0;
        string ans;
        
        while(len1>=0 || len2>=0){
            int d1 = 0;
            int d2 = 0;
            if(len1>=0){
            d1 = num1[len1--] - '0';
            }
            if(len2>=0){
            d2 = num2[len2--] - '0';
            }
            int sum = d1 + d2 + carry;
            if(sum>=10){
                carry = sum / 10;
                sum = sum % 10;
            }else{
                carry = 0;
            }
            ans  = to_string(sum) + ans;
        }
        if(carry!=0){
            ans = to_string(carry) + ans;
        }
        return ans;
    }
};

// Method 2: Bit Manipulation
   /*
     def addStrings(self, num1: str, num2: str) -> str:
        a, b = int(num1), int(num2)
        while b: a, b = a^b, (a & b) << 1
        return str(a) 
   */