class Solution {
public:
    int addDigits(int num) {
        if(num==0){
            return 0;
        }
        if(num>0 && num<=9){
            return num;
        }
        int remainder=0;
         
        while(num>9){
        int number = 0;
        while(num!=0){
            remainder = num % 10;
            number += remainder;
            num /= 10;
        }
            num = number;
        }
        return num;
    }
};
