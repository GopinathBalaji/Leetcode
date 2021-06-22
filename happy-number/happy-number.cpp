class Solution {
public:
//     Method 1: Check each number using two while loops
    bool isHappy(int n) {
       if (n == 1 || n == 7)
            return true;
        int sum = n, x = n;
 
        
        while(sum > 9) {
            sum = 0;
 
            
            while (x > 0) {
                int d = x%10;
                sum += d*d;
                x/=10;
            }
            if (sum == 1)
                return true;
            x = sum;
        }
        if(sum == 7)
            return true;
        return false;
    }
};

// Method 2: If number repeats that means it will not be a happy number
   /*
    bool isHappy(int n) {
        if(n ==1) {return true;}
        unordered_map <int,int> map;
        while (n >0) {
            int sum =0;
            while(n>0) {
                int x = n%10;
                sum+= x*x;
                n = n/10;
            }
            n = sum;
            map[n]++;
            if(map[n] > 1){return false;}
            if(n == 1) {return true;}
        }
        return false;
    }
   */

// Method 3: Recursion 
   /*
   int singleNumber(int n){
if(n<10){
return n*n;
}
return pow((n%10),2)+singleNumber(n/10);
}
bool isHappy(int n) {
if(n==1){return true;}
int res=singleNumber(n);
if(res<10 && res==1){
return true;
}else if(res<10 && res!=7){ //Here by little mathematics we can figure out that if res comes out to be 7 in (1 to 9) then it should return true.
return false;
}else{
return isHappy(res);
}
}
   */