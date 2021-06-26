class Solution {
public:
    string addBinary(string a, string b) {
        int x = a.length();
        int z = b.length();
        
        if(x>z){
            int e = x-z;
            string c (e,'0');
            b = c + b;
        }else if(z>x){
            int e = z-x;
            string c(e,'0');
            a = c + a;
        }
            
            int d = a.length();
            string res = "";
            char carry = '0';
        
            while(d){
                if(a[d-1]=='0' && b[d-1]=='0'){
                  res += (carry=='0'?"0":"1");
                    carry = '0';
                }
                else if(a[d-1]=='1' && b[d-1]=='1'){
                    res += (carry=='0'?"0":"1");
                    carry = '1';
                }
                else{
                    if(carry=='0'){
                        res += "1";
                        carry = '0';
                    }else{
                        res += "0";
                        carry = '1';
                    }
                    
                }
              d--;
            }
        if(carry=='1'){
            res += "1";
        }
        reverse(res.begin(),res.end());
        return res;
    }
};

// Method 2: Without adding extra zeros or reversing
   /*
   string addBinary(string a, string b) {
        string res;
        int bit, carry = 0;
        int i=a.size()-1, j=b.size()-1; 

        while(i >= 0 || j >= 0) {
            bit = carry;
            if(i >= 0) {
                bit += a[i] - '0';
                i--;
            }
            if(j >= 0) {
                bit += b[j] - '0';
                j--; 
            }
            carry = bit / 2;
            bit = bit % 2;
            res = to_string(bit) + res;
        }
        if(carry) res = to_string(carry) + res;
        return res;
    }
   */

// Method 3: Recursion
   /*
   string addBinary(string a, string b) {
        if(a.size() < b.size()) std::swap(a, b);
        
        for(int i = b.size() ; i >= 0; i--) {
            if(b[i] == '1') {
                recursiveadd(a, a.size()-b.size() + i);
            }
        }
        return a;
    }
    
    void recursiveadd(string &num, int index) {
        if(index < 0) return;
        
        if(num[index] == '0') {
            num[index] = '1';
            return;
        }
        
        num[index] = '0';
        if(index == 0) num.insert(num.begin(), '1');
        
        recursiveadd(num, index-1);
    }
   */