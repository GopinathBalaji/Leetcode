class Solution {
public:
    int reverse(int x) {
       int reversedno = 0;
        int remainder = 0;
        
        
        while(x != 0){
            remainder = x%10;
            if( (reversedno > INT_MAX/10) || (reversedno == INT_MAX/10 && remainder>7)){return 0;}
             if( (reversedno < INT_MIN/10) || (reversedno == INT_MIN/10 && remainder<-8)){
                 return 0;}
            reversedno = reversedno*10 + remainder;
            x /= 10;
        }
      
        return reversedno;
    }
};