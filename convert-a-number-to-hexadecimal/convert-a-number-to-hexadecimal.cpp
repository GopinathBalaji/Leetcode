class Solution {
public:
    string toHex(int num) {
        if(num==0){
            return "0";
        }
       unordered_map<int,char> m1={{10,'a'},{11,'b'},{12,'c'},{13,'d'},{14,'e'},{15,'f'}};
        string ans = "";
        if(num>0){
            while(num>0){
                int tail = num%16;
                if(tail>9){
                    ans = m1[tail] + ans;
                }else{
                    ans = to_string(tail) + ans;
                }
                num = num-tail;
                num = num/16;
            }
        }else{
            u_int n = num;
             while(n>0){
                int tail = n%16;
                if(tail>9){
                    ans = m1[tail] + ans;
                }else{
                    ans = to_string(tail) + ans;
                }
                n = n-tail;
                n = n/16;
            }
        }
        return ans;
    }
};

// Method 2: Bit Manipulation
// The idea is to take the advantage of the fact that the number is stored as binary in
// machine. We can just convert the binary 4-bit (most-right 4-bit) at a time to hex 
// value, and then join the results into a single string. An example with number 26:
// num = 26

// 0001'1010

// &----1111

// =----1010

// 0b1010 = decimal 10

// myhex[10] = 'a'

// mystr = 'a'

   /*
      def toHex(self, num: int) -> str:
        # Pre-define a string mapping the index value with the hex value
		myhex = "0123456789abcdef"
        # The given number is guaranteed to fit within the range of a 32-bit signed integer.
        # So 32/4 = 8 segments
        seg = 8
        mask = 15 # 15's binary is '1111'. It can be used to select the right-most 4 bits.
        mystr = ""
        if num == 0:
            return '0'
        while (seg>0) and (num!=0): # Keep doing until all 32 bits are iterated or the input number is zero.
            mystr = ''.join((myhex[num&mask], mystr))
            num = num >> 4
            seg -= 1
        return mystr
        
        OR
        
        public String toHex(int num) {
        if (num == 0)
            return "0";
        char [] possibility = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            sb.append(possibility[num & 15]);
            num = (num >>> 4);
            System.out.println(num);
        }
        return sb.reverse().toString();
    }
   */